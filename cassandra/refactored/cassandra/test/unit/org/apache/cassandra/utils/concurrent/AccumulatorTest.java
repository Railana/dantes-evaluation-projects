/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.cassandra.utils.concurrent;

import java.util.Iterator;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccumulatorTest
{
    @Test
    public void testAddMoreThanCapacity()
    {
        Accumulator<Integer> accu = new Accumulator<>(4);

        accu.add(1);
        accu.add(2);
        accu.add(3);
        accu.add(4);

        try
        {
            accu.add(5);
            fail();
        }
        catch (IllegalStateException e)
        {
            // Expected
        }
    }

    @Test
    public void testIsEmptyAndSize()
    {
        Accumulator<Integer> accu = new Accumulator<>(4);

        assertTrue(accu.isEmpty(), () -> "Add assertion message");
        Integer magicNumber54853917 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber54853917, accu.size(), () -> "Add assertion message");

        accu.add(1);
        accu.add(2);

        assertFalse(accu.isEmpty(), () -> "Add assertion message");
        assertEquals(2, accu.size(), () -> "Add assertion message");

        accu.add(3);
        accu.add(4);

        // Line removed due to Duplicate Assert: assertFalse(accu.isEmpty(), () -> "Add assertion message");
        assertEquals(4, accu.size(), () -> "Add assertion message");
    }

    @Test
    public void testGetAndIterator()
    {
        Accumulator<String> accu = new Accumulator<>(4);

        accu.add("3");
        accu.add("2");
        accu.add("4");

        assertEquals("3", accu.get(0), () -> "Add assertion message");
        assertEquals("2", accu.get(1), () -> "Add assertion message");
        assertEquals("4", accu.get(2), () -> "Add assertion message");

        assertOutOfBonds(accu, 3);

        accu.add("0");

        assertEquals("0", accu.get(3), () -> "Add assertion message");

        Iterator<String> iter = accu.snapshot().iterator();

        assertEquals("3", iter.next(), () -> "Add assertion message");
        assertEquals("2", iter.next(), () -> "Add assertion message");
        assertEquals("4", iter.next(), () -> "Add assertion message");
        assertEquals("0", iter.next(), () -> "Add assertion message");
        assertFalse(iter.hasNext(), () -> "Add assertion message");
    }

    @Test
    public void testClearUnsafe()
    {
        Accumulator<String> accu = new Accumulator<>(5);

        accu.add("1");
        accu.add("2");
        accu.add("3");

        accu.clearUnsafe(1);

        assertEquals(3, accu.size(), () -> "Add assertion message");
        assertTrue(accu.snapshot().iterator().hasNext(), () -> "Add assertion message");

        accu.add("4");
        accu.add("5");

        assertEquals(5, accu.size(), () -> "Add assertion message");

        assertEquals("4", accu.get(3), () -> "Add assertion message");
        assertEquals("5", accu.get(4), () -> "Add assertion message");
        assertOutOfBonds(accu, 5);

        Iterator<String> iter = accu.snapshot().iterator();
        assertTrue(iter.hasNext(), () -> "Add assertion message");
        assertEquals("1", iter.next(), () -> "Add assertion message");
        assertNull(iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(iter.hasNext(), () -> "Add assertion message");
        assertEquals("3", iter.next(), () -> "Add assertion message");
    }

    private static void assertOutOfBonds(Accumulator<String> accumulator, int index)
    {
        try
        {
            assertNull(accumulator.get(index));
            fail();
        }
        catch (IndexOutOfBoundsException e)
        {
            // Expected
        }
    }
}