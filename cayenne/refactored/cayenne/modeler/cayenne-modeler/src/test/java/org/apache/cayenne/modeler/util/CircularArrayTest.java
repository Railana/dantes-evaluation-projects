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

package org.apache.cayenne.modeler.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CircularArrayTest {

    @Test
    public void testArraySize5() {

        String a = "A", b = "B", c = "C", d = "D", e = "E", f = "F", g = "G", h = "H";
        CircularArray<String> q = new CircularArray<>(5);

        assertAdd(q, a, "[A, null, null, null, null]");
        assertRemove(q, a, "[null, null, null, null, null]");
        assertAdd(q, a, "[A, null, null, null, null]");

        assertAdd(q, b, "[A, B, null, null, null]");
        assertRemove(q, b, "[A, null, null, null, null]");
        assertAdd(q, b, "[A, B, null, null, null]");

        assertAdd(q, c, "[A, B, C, null, null]");
        assertRemove(q, c, "[A, B, null, null, null]");
        assertAdd(q, c, "[A, B, C, null, null]");

        assertAdd(q, d, "[A, B, C, D, null]");
        assertRemove(q, d, "[A, B, C, null, null]");
        assertAdd(q, d, "[A, B, C, D, null]");

        assertAdd(q, e, "[A, B, C, D, E]");
        assertRemove(q, e, "[A, B, C, D, null]");
        assertAdd(q, e, "[A, B, C, D, E]");

        assertAdd(q, f, "[B, C, D, E, F]");
        assertRemove(q, f, "[B, C, D, E, null]");
        assertAdd(q, f, "[B, C, D, E, F]");

        assertAdd(q, g, "[C, D, E, F, G]");
        assertRemove(q, e, "[C, D, F, G, null]");
        assertAdd(q, h, "[C, D, F, G, H]");

        assertRemove(q, c, "[D, F, G, H, null]");
        assertRemove(q, h, "[D, F, G, null, null]");
        assertRemove(q, f, "[D, G, null, null, null]");
        assertRemove(q, g, "[D, null, null, null, null]");
        assertRemove(q, d, "[null, null, null, null, null]");
    }

    @Test
    public void testArraySize3() {
        String a = "A", b = "B", c = "C", d = "D", e = "E";
        CircularArray<String> q = new CircularArray<>(3);

        Integer magicNumber75282781 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber75282781, q.size(), () -> "Add assertion message");
        assertEquals(3, q.capacity(), () -> "Add assertion message");

        q.add(a);

        Integer magicNumber59663986 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber59663986, q.indexOf(a), () -> "Add assertion message");
        assertEquals(a, q.get(0), () -> "Add assertion message");
        assertEquals(1, q.size(), () -> "Add assertion message");

        q.add(b);

        Integer magicNumber28118111 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber28118111, q.indexOf(b), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(a, q.get(0), () -> "Add assertion message");
        assertEquals(b, q.get(1), () -> "Add assertion message");
        assertEquals(2, q.size(), () -> "Add assertion message");

        q.add(c);

        Integer magicNumber83564777 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83564777, q.indexOf(c), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(a, q.get(0), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(b, q.get(1), () -> "Add assertion message");
        assertEquals(c, q.get(2), () -> "Add assertion message");
        assertEquals(3, q.size(), () -> "Add assertion message");

        q.add(d);

        // Line removed due to Duplicate Assert: assertEquals(3, q.size(), () -> "Add assertion message");

        q.add(e);

        Integer magicNumber13544611 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        // Line removed due to Duplicate Assert: assertEquals(magicNumber13544611, q.size(), () -> "Add assertion message");
        assertFalse("A should not be in the q", q.contains(a));
        Integer magicNumber92907464 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber92907464, q.indexOf(c), () -> "Add assertion message");
        assertEquals(c, q.get(0), () -> "Add assertion message");
        assertEquals(1, q.indexOf(d), () -> "Add assertion message");
        assertEquals(d, q.get(1), () -> "Add assertion message");
        Integer magicNumber68610412 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber68610412, q.indexOf(e), () -> "Add assertion message");
        assertEquals(e, q.get(2), () -> "Add assertion message");

        // should be the same after resizing
        q.resize(5);

        Integer magicNumber95266836 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber95266836, q.capacity(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(3, q.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2, q.indexOf(e), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(e, q.get(2), () -> "Add assertion message");

        q.resize(2);

        assertEquals(2, q.capacity(), () -> "Add assertion message");
    }

    @Test
    public void testToString() {
        CircularArray<String> a = new CircularArray<>(5);
        assertEquals("[null, null, null, null, null]", a.toString(), () -> "Add assertion message");
    }

    public void assertAdd(CircularArray<String> a, String obj, String expected) {
        a.add(obj);
        assertEquals(expected, a.toString());
    }

    public void assertRemove(CircularArray<String> a, String obj, String expected) {
        a.indexOf(obj);
        a.remove(obj);
        assertEquals(expected, a.toString());
    }
}