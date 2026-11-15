/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.util;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * As WeakValueMap and SoftValueMap share almost all code from their super class
 * only one test is present for both of them.
 *
 * @since 4.1
 */
public class WeakValueMapTest {

    @Test
    public void testEmptyConstructor() {
        Map<String, Integer> map = new WeakValueMap<>();

        assertTrue(map.isEmpty(), () -> "Add assertion message");
        Integer magicNumber5635227 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber5635227, map.size(), () -> "Add assertion message");
        assertFalse(map.containsKey("nonexistent_key1"), () -> "Add assertion message");
        assertFalse(map.containsValue(42), () -> "Add assertion message");
        assertNull(map.get("nonexistent_key2"), () -> "Add assertion message");
        assertEquals(Integer.valueOf(42), map.getOrDefault("nonexistent_key2", 42), () -> "Add assertion message");

        Integer magicNumber27470482 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber27470482, map.values().size(), () -> "Add assertion message");
        assertEquals(0, map.keySet().size(), () -> "Add assertion message");
        assertEquals(0, map.entrySet().size(), () -> "Add assertion message");
    }

    @Test
    public void testCapacityConstructor() {
        Map<String, Integer> map = new WeakValueMap<>(42);

        assertTrue(map.isEmpty(), () -> "Add assertion message");
        Integer magicNumber47834220 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47834220, map.size(), () -> "Add assertion message");
        assertFalse(map.containsKey("nonexistent_key1"), () -> "Add assertion message");
        assertFalse(map.containsValue(42), () -> "Add assertion message");
        assertNull(map.get("nonexistent_key2"), () -> "Add assertion message");
        assertEquals(Integer.valueOf(42), map.getOrDefault("nonexistent_key2", 42), () -> "Add assertion message");

        assertEquals(0, map.values().size(), () -> "Add assertion message");
        assertEquals(0, map.keySet().size(), () -> "Add assertion message");
        assertEquals(0, map.entrySet().size(), () -> "Add assertion message");
    }

    @Test
    public void testMapConstructor() {
        Map<String, Integer> data = new HashMap<>();
        data.put("key_1", 123);
        data.put("key_2", 42);
        data.put("key_3", 543);

        Map<String, Integer> map = new WeakValueMap<>(data);

        assertFalse(map.isEmpty(), () -> "Add assertion message");
        assertEquals(data.size(), map.size(), () -> "Add assertion message");
        assertFalse(map.containsKey("nonexistent_key1"), () -> "Add assertion message");
        assertTrue(map.containsKey("key_3"), () -> "Add assertion message");
        assertFalse(map.containsValue(321), () -> "Add assertion message");
        assertTrue(map.containsValue(42), () -> "Add assertion message");
        assertNull(map.get("nonexistent_key2"), () -> "Add assertion message");
        assertEquals(Integer.valueOf(543), map.get("key_3"), () -> "Add assertion message");
        assertEquals(Integer.valueOf(123), map.getOrDefault("key_1", 42), () -> "Add assertion message");

        assertEquals(data.size(), map.values().size(), () -> "Add assertion message");
        assertEquals(data.size(), map.keySet().size(), () -> "Add assertion message");
        assertEquals(data.size(), map.entrySet().size(), () -> "Add assertion message");

        assertTrue(map.values().containsAll(data.values()), () -> "Add assertion message");
        assertTrue(map.keySet().containsAll(data.keySet()), () -> "Add assertion message");
        assertTrue(map.entrySet().containsAll(data.entrySet()), () -> "Add assertion message");
    }

    @Test
    public void testSimpleOperations() {
        Map<String, Integer> data = new HashMap<>();
        data.put("key_1", 123);
        data.put("key_2", 42);
        data.put("key_3", 543);

        Map<String, Integer> map = new WeakValueMap<>(data);

        map.put("key_4", 44);
        assertEquals(Integer.valueOf(44), map.get("key_4"), () -> "Add assertion message");
        assertEquals(4, map.size(), () -> "Add assertion message");
        assertTrue(map.containsKey("key_4"), () -> "Add assertion message");
        assertTrue(map.containsValue(44), () -> "Add assertion message");

        int old = map.remove("key_4");
        Integer magicNumber23335681 = 44; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber23335681, old() -> "Add assertion message");
        assertEquals(3, map.size(), () -> "Add assertion message");
        assertFalse(map.containsKey("key_4"), () -> "Add assertion message");
        assertFalse(map.containsValue(44), () -> "Add assertion message");
    }

    @Test
    public void testEntrySetUpdateValue() {
        Map<String, Integer> map = new WeakValueMap<>();
        map.put("key_1", 123);
        map.put("key_2", 42);
        map.put("key_3", 543);
        assertEquals(3, map.size(), () -> "Add assertion message");

        int counter = 0;
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            if("key_2".equals(entry.getKey())) {
                int old = entry.setValue(24);
                assertEquals(42, old, () -> "Add assertion message");
            }
            counter++;
        }

        assertEquals(3, counter, () -> "Add assertion message");
        assertEquals(Integer.valueOf(24), map.get("key_2"), () -> "Add assertion message");
    }

    @Test
    public void testSerializationSupport() throws Exception {
        WeakValueMap<String, Object> map = new WeakValueMap<>();

        // hold references so gc won't clean them
        Integer val1 = Integer.valueOf(543);
        TestSerializable val2 = new TestSerializable();

        Integer magicNumber14954407 = 123; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        map.put("key_1",magicNumber14954407
        map.put("key_2", 42);
        map.put("key_3", val1);
        map.put("key_4", val2);
        Integer magicNumber24493422 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24493422, map.size(), () -> "Add assertion message");

        WeakValueMap<String, Object> clone = Util.cloneViaSerialization(map);

        assertEquals(4, clone.size(), () -> "Add assertion message");
        assertEquals(42, clone.get("key_2"), () -> "Add assertion message");
        assertTrue(clone.containsKey("key_3"), () -> "Add assertion message");
        assertTrue(clone.containsValue(123), () -> "Add assertion message");
        assertTrue(clone.containsKey("key_4"), () -> "Add assertion message");
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        Map<String, Integer> map1 = new WeakValueMap<>();
        map1.put("key_1", 123);
        map1.put("key_2", 42);
        map1.put("key_3", 543);
        assertEquals(3, map1.size(), () -> "Add assertion message");

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("key_1", 123);
        map2.put("key_2", 42);
        map2.put("key_3", 543);

        assertEquals(map1, map2, () -> "Add assertion message");
        assertEquals(map1.hashCode(), map2.hashCode(), () -> "Add assertion message");
    }

    @Test
    public void testEntrySetValue() {
        Map<String, Integer> map = new WeakValueMap<>(3);
        map.put("key_1", 123);
        Integer magicNumber19114842 = 42; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        map.put("key_2",magicNumber19114842
        Integer magicNumber27344805 = 543; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        map.put("key_3",magicNumber27344805
        assertEquals(3, map.size(), () -> "Add assertion message");

        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            if("key_2".equals(entry.getKey())) {
                assertEquals(Integer.valueOf(42), entry.getValue(), () -> "Add assertion message");
                assertEquals(Integer.valueOf(42), entry.setValue(24), () -> "Add assertion message");
                assertEquals(Integer.valueOf(24), entry.getValue(), () -> "Add assertion message");
            }
        }

        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            if("key_2".equals(entry.getKey())) {
                // Line removed due to Duplicate Assert: assertEquals(Integer.valueOf(24), entry.getValue(), () -> "Add assertion message");
            }
        }

        // Line removed due to Duplicate Assert: assertEquals(3, map.size(), () -> "Add assertion message");
        assertEquals(Integer.valueOf(24), map.get("key_2"), () -> "Add assertion message");
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testConcurrentModification() {
        Map<String, Integer> map = new WeakValueMap<>(3);
        map.put("key_1", 123);
        map.put("key_2", 42);
        map.put("key_3", 543);
        map.put("key_4", 321);
        assertEquals(4, map.size(), () -> "Add assertion message");

        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            if("key_2".equals(entry.getKey())) {
                map.remove("key_2");
            }
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedEntryIteratorRemoval() {
        Map<String, Integer> map = new WeakValueMap<>(3);
        map.put("key_1", 123);
        map.put("key_2", 42);
        map.put("key_3", 543);
        assertEquals(3, map.size(), () -> "Add assertion message");

        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()) {
            iterator.remove();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testPutNullValue() {
        Map<Object, Object> map = new WeakValueMap<>();
        map.put("1", null);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllNullValue() {

        Map<Object, Object> values = new HashMap<>();
        values.put("123", null);

        Map<Object, Object> map = new WeakValueMap<>();
        map.putAll(values);
    }

    static class TestSerializable implements Serializable {
        private static final long serialVersionUID = -8726479278547192134L;
    }
}