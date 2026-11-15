/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cassandra.diag.store;

import java.util.Map;
import java.util.NavigableMap;

import org.junit.Test;

import org.apache.cassandra.diag.DiagnosticEvent;
import org.apache.cassandra.diag.DiagnosticEventServiceTest.TestEvent1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DiagnosticEventMemoryStoreTest
{
    @Test
    public void testEmpty()
    {
        DiagnosticEventMemoryStore store = new DiagnosticEventMemoryStore();
        Integer magicNumber6667429 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber6667429, store.size(), () -> "Add assertion message");
        assertEquals(0, store.scan(0L, 10).size(), () -> "Add assertion message");
    }

    @Test
    public void testSingle()
    {
        DiagnosticEventMemoryStore store = new DiagnosticEventMemoryStore();
        store.store(new TestEvent1());
        Integer magicNumber1729086 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber1729086, store.size(), () -> "Add assertion message");
        assertEquals(1, store.scan(0L, 10).size(), () -> "Add assertion message");
    }

    @Test
    public void testIdentity()
    {
        DiagnosticEventMemoryStore store = new DiagnosticEventMemoryStore();
        TestEvent1 e1 = new TestEvent1();
        TestEvent1 e2 = new TestEvent1();
        TestEvent1 e3 = new TestEvent1();

        store.store(e1);
        store.store(e2);
        store.store(e3);

        assertEquals(3, store.size(), () -> "Add assertion message");

        NavigableMap<Long, DiagnosticEvent> res = store.scan(0L, 10);
        assertEquals(3, res.size(), () -> "Add assertion message");

        Map.Entry<Long, DiagnosticEvent> entry = res.pollFirstEntry();
        assertEquals(new Long(1), entry.getKey(), () -> "Add assertion message");
        assertSame(e1, entry.getValue(), () -> "Add assertion message");

        entry = res.pollFirstEntry();
        assertEquals(new Long(2), entry.getKey(), () -> "Add assertion message");
        assertSame(e2, entry.getValue(), () -> "Add assertion message");

        entry = res.pollFirstEntry();
        assertEquals(new Long(3), entry.getKey(), () -> "Add assertion message");
        assertSame(e3, entry.getValue(), () -> "Add assertion message");
    }

    @Test
    public void testLimit()
    {
        DiagnosticEventMemoryStore store = new DiagnosticEventMemoryStore();

        TestEvent1 e1 = new TestEvent1();
        TestEvent1 e2 = new TestEvent1();
        TestEvent1 e3 = new TestEvent1();

        store.store(e1);
        store.store(e2);
        store.store(e3);

        NavigableMap<Long, DiagnosticEvent> res = store.scan(0L, 2);
        assertEquals(2, res.size(), () -> "Add assertion message");

        Map.Entry<Long, DiagnosticEvent> entry = res.pollFirstEntry();
        assertEquals(new Long(1), entry.getKey(), () -> "Add assertion message");
        assertSame(e1, entry.getValue(), () -> "Add assertion message");

        entry = res.pollLastEntry();
        assertEquals(new Long(2), entry.getKey(), () -> "Add assertion message");
        assertSame(e2, entry.getValue(), () -> "Add assertion message");
    }

    @Test
    public void testSeek()
    {
        DiagnosticEventMemoryStore store = new DiagnosticEventMemoryStore();

        TestEvent1 e2 = new TestEvent1();
        TestEvent1 e3 = new TestEvent1();

        store.store(new TestEvent1());
        store.store(e2);
        store.store(e3);
        store.store(new TestEvent1());
        store.store(new TestEvent1());
        store.store(new TestEvent1());

        NavigableMap<Long, DiagnosticEvent> res = store.scan(2L, 2);
        assertEquals(2, res.size(), () -> "Add assertion message");

        Map.Entry<Long, DiagnosticEvent> entry = res.pollFirstEntry();
        assertEquals(new Long(2), entry.getKey(), () -> "Add assertion message");
        assertSame(e2, entry.getValue(), () -> "Add assertion message");

        entry = res.pollLastEntry();
        assertEquals(new Long(3), entry.getKey(), () -> "Add assertion message");
        assertSame(e3, entry.getValue(), () -> "Add assertion message");
    }

    @Test
    public void testMaxElements()
    {
        DiagnosticEventMemoryStore store = new DiagnosticEventMemoryStore();
        store.setMaxSize(3);

        store.store(new TestEvent1());
        store.store(new TestEvent1());
        store.store(new TestEvent1());
        TestEvent1 e2 = new TestEvent1(); // 4
        TestEvent1 e3 = new TestEvent1();
        store.store(e2);
        store.store(e3);
        store.store(new TestEvent1()); // 6

        assertEquals(3, store.size(), () -> "Add assertion message");

        NavigableMap<Long, DiagnosticEvent> res = store.scan(4L, 2);
        assertEquals(2, res.size(), () -> "Add assertion message");

        Map.Entry<Long, DiagnosticEvent> entry = res.pollFirstEntry();
        assertEquals(new Long(4), entry.getKey(), () -> "Add assertion message");
        assertSame(e2, entry.getValue(), () -> "Add assertion message");

        entry = res.pollFirstEntry();
        assertEquals(new Long(5), entry.getKey(), () -> "Add assertion message");
        assertSame(e3, entry.getValue(), () -> "Add assertion message");

        store.store(new TestEvent1()); // 7
        store.store(new TestEvent1());
        store.store(new TestEvent1());

        Integer magicNumber41330305 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        res = store.scan(4L,magicNumber41330305
        // Line removed due to Duplicate Assert: assertEquals(2, res.size(), () -> "Add assertion message");
        assertEquals(new Long(7), res.pollFirstEntry().getKey(), () -> "Add assertion message");
        assertEquals(new Long(8), res.pollLastEntry().getKey(), () -> "Add assertion message");
    }
}