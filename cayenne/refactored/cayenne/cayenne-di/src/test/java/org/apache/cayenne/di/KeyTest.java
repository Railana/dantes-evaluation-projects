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
package org.apache.cayenne.di;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class KeyTest {

    @Test
    public void testEquals() {
        Key<String> key1 = Key.get(String.class);
        Key<String> key2 = Key.get(String.class);
        Key<Integer> key3 = Key.get(Integer.class);
        Key<Integer> key31 = Key.get(Integer.class, "");
        Key<Integer> key4 = Key.get(Integer.class, "a");
        Key<Integer> key5 = Key.get(Integer.class, "a");
        Key<Integer> key6 = Key.get(Integer.class, "b");
        Key<String> key7 = Key.get(String.class, "a");

        assertTrue(key1.equals(key2), () -> "Add assertion message");

        assertFalse(key1.equals(key3), () -> "Add assertion message");

        assertTrue(key3.equals(key31), () -> "Add assertion message");
        assertTrue(key31.equals(key3), () -> "Add assertion message");

        assertFalse(key3.equals(key4), () -> "Add assertion message");
        assertFalse(key4.equals(key3), () -> "Add assertion message");

        assertTrue(key4.equals(key5), () -> "Add assertion message");
        assertTrue(key5.equals(key4), () -> "Add assertion message");

        assertFalse(key5.equals(key6), () -> "Add assertion message");
        assertFalse(key6.equals(key5), () -> "Add assertion message");

        assertFalse(key4.equals(key7), () -> "Add assertion message");
        assertFalse(key7.equals(key4), () -> "Add assertion message");
    }

    @Test
    public void testListKeysEquals() {
        Key<List<Integer>> key1 = Key.getListOf(Integer.class);
        Key<List<String>> key2 = Key.getListOf(String.class);
        Key<List<Integer>> key3 = Key.getListOf(Integer.class);
        Key<List<String>> key4 = Key.getListOf(String.class);

        assertNotEquals(key1, key2);
        assertNotEquals(key3, key4);

        assertEquals(key1, key3, () -> "Add assertion message");
        assertEquals(key1, key1, () -> "Add assertion message");
        assertEquals(key2, key4, () -> "Add assertion message");
        assertEquals(key4, key4, () -> "Add assertion message");

        // Name is suppressing generic type, to keep backward compatibility.
        Key key5 = Key.getListOf(Object.class, "xyz");
        Key key6 = Key.getListOf(Object.class, "abc");
        assertNotEquals(key5, key6);

        Key key7 = Key.getListOf(Integer.class, "xyz");
        Key key8 = Key.getListOf(Integer.class, "abc");
        assertNotEquals(key7, key8);
        assertNotEquals(key5, key7);

        Key key9 = Key.get(List.class, "xyz");
        assertNotEquals(key7, key9);
     }

    @Test
    public void testHashCode() {
        Key<String> key1 = Key.get(String.class);
        Key<String> key2 = Key.get(String.class);
        Key<Integer> key3 = Key.get(Integer.class);
        Key<Integer> key4 = Key.get(Integer.class, "a");
        Key<Integer> key5 = Key.get(Integer.class, "a");
        Key<Integer> key6 = Key.get(Integer.class, "b");
        Key<String> key7 = Key.get(String.class, "a");

        assertTrue(
                "generated different hashcode on second inocation",
                key1.hashCode() == key1.hashCode());
        assertTrue(key1.hashCode() == key2.hashCode(), () -> "Add assertion message");
        assertTrue(key4.hashCode() == key5.hashCode(), () -> "Add assertion message");

        // these are not technically required for hashCode() validity, but as things stand
        // now, these tests will all succeed.
        assertFalse(key1.hashCode() == key3.hashCode(), () -> "Add assertion message");
        assertFalse(key4.hashCode() == key3.hashCode(), () -> "Add assertion message");
        assertFalse(key5.hashCode() == key6.hashCode(), () -> "Add assertion message");
        assertFalse(key7.hashCode() == key4.hashCode(), () -> "Add assertion message");
    }

    @Test
    public void testToString() {
        assertEquals("<BindingKey: java.lang.String>",
                Key.get(String.class).toString(), () -> "Add assertion message");
        assertEquals("<BindingKey: java.lang.String, 'xyz'>",
                Key.get(String.class, "xyz").toString(), () -> "Add assertion message");
        assertEquals("<BindingKey: java.util.List[java.lang.String]>",
                Key.getListOf(String.class).toString(), () -> "Add assertion message");
        assertEquals("<BindingKey: java.util.List[java.lang.String], 'xyz'>",
                Key.getListOf(String.class, "xyz").toString(), () -> "Add assertion message");
    }
}