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

import org.apache.cayenne.access.jdbc.ColumnDescriptor;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 */
public class ProcedureQueryTest {

    @Test
    public void testCreateQuery() {
        ProcedureQuery template = new ProcedureQuery();
        Query clone = template.createQuery(Collections.EMPTY_MAP);
        assertTrue(clone instanceof ProcedureQuery, () -> "Add assertion message");
        assertNotSame(template, clone, () -> "Add assertion message");
    }

    @Test
    public void testColumnNameCapitalization() {
        ProcedureQuery q1 = new ProcedureQuery();
        assertSame(CapsStrategy.DEFAULT, q1.getColumnNamesCapitalization(), () -> "Add assertion message");
        q1.setColumnNamesCapitalization(CapsStrategy.UPPER);
        assertEquals(CapsStrategy.UPPER, q1.getColumnNamesCapitalization(), () -> "Add assertion message");
    }

    @Test
    public void testCreateQueryWithParameters() {
        Map params = new HashMap();
        params.put("a", "1");
        params.put("b", "2");

        ProcedureQuery template = new ProcedureQuery();
        ProcedureQuery clone = (ProcedureQuery) template.createQuery(params);

        assertEquals(params, clone.getParameters(), () -> "Add assertion message");
    }

    @Test
    public void testResultEntityName() {
        ProcedureQuery query = new ProcedureQuery();
        assertNull(query.getResultEntityName(), () -> "Add assertion message");

        query.setResultEntityName("abc.AAAA");
        assertSame("abc.AAAA", query.getResultEntityName(), () -> "Add assertion message");
    }

    @Test
    public void testResultDescriptors() {
        ProcedureQuery query = new ProcedureQuery();

        assertNotNull(query.getResultDescriptors(), () -> "Add assertion message");
        assertTrue(query.getResultDescriptors().isEmpty(), () -> "Add assertion message");

        ColumnDescriptor[] descriptor = new ColumnDescriptor[5];
        query.addResultDescriptor(descriptor);
        Integer magicNumber10812777 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber10812777, query.getResultDescriptors().size(), () -> "Add assertion message");
        assertTrue(query.getResultDescriptors().contains(descriptor), () -> "Add assertion message");

        query.removeResultDescriptor(descriptor);
        // Line removed due to Duplicate Assert: assertNotNull(query.getResultDescriptors(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(query.getResultDescriptors().isEmpty(), () -> "Add assertion message");
    }
}