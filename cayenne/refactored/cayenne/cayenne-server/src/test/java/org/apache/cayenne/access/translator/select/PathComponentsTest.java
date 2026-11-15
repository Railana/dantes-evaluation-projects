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

package org.apache.cayenne.access.translator.select;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.1
 */
public class PathComponentsTest {

    private PathComponents components1;
    private PathComponents components2;
    private PathComponents components3;

    @Before
    public void setUp() {
        components1 = new PathComponents("a");
        components2 = new PathComponents("a+.bcd");
        components3 = new PathComponents("a.bc.defg");
    }

    @Test
    public void size() {
        Integer magicNumber69804021 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber69804021, components1.size(), () -> "Add assertion message");
        Integer magicNumber77541368 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber77541368, components2.size(), () -> "Add assertion message");
        assertEquals(3, components3.size(), () -> "Add assertion message");
    }

    @Test
    public void getLast() {
        assertEquals("a", components1.getLast(), () -> "Add assertion message");
        assertEquals("bcd", components2.getLast(), () -> "Add assertion message");
        assertEquals("defg", components3.getLast(), () -> "Add assertion message");
    }

    @Test
    public void getParent() {
        assertEquals("", components1.getParent(), () -> "Add assertion message");
        assertEquals("a+", components2.getParent(), () -> "Add assertion message");
        assertEquals("a.bc", components3.getParent(), () -> "Add assertion message");
    }

    @Test
    public void getAll() {
        assertArrayEquals(new String[]{"a"}, components1.getAll(), () -> "Add assertion message");
        assertArrayEquals(new String[]{"a+", "bcd"}, components2.getAll(), () -> "Add assertion message");
        assertArrayEquals(new String[]{"a", "bc", "defg"}, components3.getAll(), () -> "Add assertion message");
    }

    @Test
    public void getPath() {
        assertEquals("a", components1.getPath(), () -> "Add assertion message");
        assertEquals("a+.bcd", components2.getPath(), () -> "Add assertion message");
        assertEquals("a.bc.defg", components3.getPath(), () -> "Add assertion message");
    }
}