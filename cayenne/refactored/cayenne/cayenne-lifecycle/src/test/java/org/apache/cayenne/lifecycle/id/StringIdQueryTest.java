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
package org.apache.cayenne.lifecycle.id;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.test.jdbc.DBHelper;
import org.apache.cayenne.test.jdbc.TableHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringIdQueryTest {

    private ServerRuntime runtime;
    private TableHelper e1Helper;
    private TableHelper e2Helper;

    @Before
    public void setUp() {
        runtime = ServerRuntime.builder().addConfig("cayenne-lifecycle.xml").build();
        // Removed line due to General Fixture smell: runtime = ServerRuntime.builder().addConfig("cayenne-lifecycle.xml").build();
        // Removed line due to General Fixture smell: DBHelper dbHelper = new DBHelper(runtime.getDataSource("lifecycle-db"));
        // Removed line due to General Fixture smell: e1Helper = new TableHelper(dbHelper, "E1", "ID");
        e2Helper = new TableHelper(dbHelper, "E2", "ID");
    }

    @After
    public void tearDown() {
        runtime = ServerRuntime.builder().addConfig("cayenne-lifecycle.xml").build();
        runtime.shutdown();
    }

    @Test
    public void testConstructor() {
        StringIdQuery q1 = new StringIdQuery();
        Integer magicNumber65966106 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber65966106, q1.getStringIds().size(), () -> "Add assertion message");

        StringIdQuery q2 = new StringIdQuery("a", "b", "c", "c");
        assertEquals(3, q2.getStringIds().size(), () -> "Add assertion message");
        assertTrue(q2.getStringIds().contains("a"), () -> "Add assertion message");
        assertTrue(q2.getStringIds().contains("b"), () -> "Add assertion message");
        assertTrue(q2.getStringIds().contains("c"), () -> "Add assertion message");

        StringIdQuery q3 = new StringIdQuery(Arrays.asList("a", "b", "b", "c"));
        assertEquals(3, q3.getStringIds().size(), () -> "Add assertion message");
        assertTrue(q3.getStringIds().contains("a"), () -> "Add assertion message");
        assertTrue(q3.getStringIds().contains("b"), () -> "Add assertion message");
        assertTrue(q3.getStringIds().contains("c"), () -> "Add assertion message");
    }

    @Test
    public void testPerformQuery_SingleEntity() throws Exception {
        e1Helper = new TableHelper(dbHelper, "E1", "ID");
        runtime = ServerRuntime.builder().addConfig("cayenne-lifecycle.xml").build();
        e1Helper.deleteAll();
        e1Helper.insert(3).insert(4);

        StringIdQuery query = new StringIdQuery("E1:3", "E1:4", "E1:5");
        QueryResponse response = runtime.newContext().performGenericQuery(query);
        Integer magicNumber52894882 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber52894882, response.size(), () -> "Add assertion message");
        assertEquals(2, response.firstList().size(), () -> "Add assertion message");

        Set<Number> ids = new HashSet<>();

        DataRow r1 = (DataRow) response.firstList().get(0);
        ids.add((Number) r1.get("ID"));

        DataRow r2 = (DataRow) response.firstList().get(1);
        ids.add((Number) r2.get("ID"));

        assertTrue(ids.contains(3L), () -> "Add assertion message");
        assertTrue(ids.contains(4L), () -> "Add assertion message");
    }

    @Test
    public void testPerformQuery_MultipleEntities() throws Exception {
        e1Helper = new TableHelper(dbHelper, "E1", "ID");
        runtime = ServerRuntime.builder().addConfig("cayenne-lifecycle.xml").build();
        e1Helper.deleteAll();
        e1Helper.insert(3).insert(4);

        e2Helper.deleteAll();
        e2Helper.insert(5).insert(6).insert(7);

        StringIdQuery query = new StringIdQuery("E1:3", "E1:4", "E2:6", "E1:5");
        QueryResponse response = runtime.newContext().performGenericQuery(query);
        assertEquals(2, response.size(), () -> "Add assertion message");

        Set<String> ids = new HashSet<>();

        while (response.next()) {
            @SuppressWarnings("unchecked")
            List<DataRow> list = (List<DataRow>) response.currentList();
            for (DataRow row : list) {
                ids.add(row.getEntityName() + ":" + row.get("ID"));
            }
        }

        assertEquals(3, ids.size(), () -> "Add assertion message");
        assertTrue(ids.contains("E1:3"), () -> "Add assertion message");
        assertTrue(ids.contains("E1:4"), () -> "Add assertion message");
        assertTrue(ids.contains("E2:6"), () -> "Add assertion message");
    }

}