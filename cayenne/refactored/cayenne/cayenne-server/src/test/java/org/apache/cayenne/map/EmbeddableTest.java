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
package org.apache.cayenne.map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EmbeddableTest {

    @Test
    public void testClassName() {
        Embeddable e1 = new Embeddable();
        assertNull(e1.getClassName(), () -> "Add assertion message");

        e1.setClassName("XYZ");
        assertEquals("XYZ", e1.getClassName(), () -> "Add assertion message");

        Embeddable e2 = new Embeddable("ABC");
        assertEquals("ABC", e2.getClassName(), () -> "Add assertion message");
    }

    @Test
    public void testAddAttribute() {
        Embeddable e1 = new Embeddable();

        EmbeddableAttribute a1 = new EmbeddableAttribute("a1");
        EmbeddableAttribute a2 = new EmbeddableAttribute("a2");

        Integer magicNumber84875800 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber84875800, e1.getAttributeMap().size(), () -> "Add assertion message");
        e1.addAttribute(a1);
        assertEquals(1, e1.getAttributeMap().size(), () -> "Add assertion message");
        assertSame(e1, a1.getEmbeddable(), () -> "Add assertion message");
        e1.addAttribute(a2);
        assertEquals(2, e1.getAttributeMap().size(), () -> "Add assertion message");
        assertSame(e1, a2.getEmbeddable(), () -> "Add assertion message");

        assertTrue(e1.getAttributes().contains(a1), () -> "Add assertion message");
        assertTrue(e1.getAttributes().contains(a2), () -> "Add assertion message");
    }

    @Test
    public void testRemoveAttribute() {
        Embeddable e1 = new Embeddable();

        EmbeddableAttribute a1 = new EmbeddableAttribute("a1");
        EmbeddableAttribute a2 = new EmbeddableAttribute("a2");

        e1.addAttribute(a1);
        e1.addAttribute(a2);

        e1.removeAttribute("a1");
        e1.removeAttribute("a2");
        assertEquals(0, e1.getAttributeMap().size(), () -> "Add assertion message");

        assertFalse(e1.getAttributes().contains(a1), () -> "Add assertion message");
        assertFalse(e1.getAttributes().contains(a2), () -> "Add assertion message");
    }
}