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
package org.apache.cayenne.query;

import org.apache.cayenne.MockPersistentObject;
import org.apache.cayenne.Persistent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RefreshQueryTest {

    @Test
    public void testRefreshAllConstructor() {

        RefreshQuery q = new RefreshQuery();
        assertNull(q.getObjects(), () -> "Add assertion message");
        assertNull(q.getQuery(), () -> "Add assertion message");
        assertNull(q.getGroupKeys(), () -> "Add assertion message");
        assertTrue(q.isRefreshAll(), () -> "Add assertion message");
    }

    @Test
    public void testCollectionConstructor() {
        Collection c = new ArrayList();
        c.add(new Object());
        c.add(new Object());

        RefreshQuery q = new RefreshQuery(c);
        assertSame(c, q.getObjects(), () -> "Add assertion message");
        assertNull(q.getQuery(), () -> "Add assertion message");
        assertNull(q.getGroupKeys(), () -> "Add assertion message");
        assertFalse(q.isRefreshAll(), () -> "Add assertion message");
    }

    @Test
    public void testObjectConstructor() {
        Persistent p = new MockPersistentObject();

        RefreshQuery q = new RefreshQuery(p);
        assertNotNull(q.getObjects(), () -> "Add assertion message");
        Integer magicNumber24467223 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24467223, q.getObjects().size(), () -> "Add assertion message");
        assertSame(p, q.getObjects().iterator().next(), () -> "Add assertion message");
        assertNull(q.getQuery(), () -> "Add assertion message");
        assertNull(q.getGroupKeys(), () -> "Add assertion message");
        assertFalse(q.isRefreshAll(), () -> "Add assertion message");
    }

    @Test
    public void testQueryConstructor() {
        Query query = new MockQuery();

        RefreshQuery q = new RefreshQuery(query);
        assertNull(q.getObjects(), () -> "Add assertion message");
        assertNotNull(q.getQuery(), () -> "Add assertion message");
        assertNotSame("query must be wrapped", query, q.getQuery());
        assertNull(q.getGroupKeys(), () -> "Add assertion message");
        assertFalse(q.isRefreshAll(), () -> "Add assertion message");
    }

    @Test
    public void testGroupKeysConstructor() {
        String[] groupKeys = new String[] { "a", "b" };

        RefreshQuery q = new RefreshQuery(groupKeys);
        assertNull(q.getObjects(), () -> "Add assertion message");
        assertNull(q.getQuery(), () -> "Add assertion message");
        assertSame(groupKeys, q.getGroupKeys(), () -> "Add assertion message");
    }

}