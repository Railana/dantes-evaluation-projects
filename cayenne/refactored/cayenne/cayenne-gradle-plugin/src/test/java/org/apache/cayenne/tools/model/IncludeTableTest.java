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

package org.apache.cayenne.tools.model;

import java.util.Collection;

import org.apache.cayenne.dbsync.reverse.dbimport.ExcludeColumn;
import org.apache.cayenne.dbsync.reverse.dbimport.ExcludeRelationship;
import org.apache.cayenne.dbsync.reverse.dbimport.IncludeColumn;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.0
 */
public class IncludeTableTest {

    @Test
    public void includeColumn() throws Exception {
        IncludeTable table = new IncludeTable("name");
        table.includeColumn("column1");
        table.includeColumn("column2");
        table.includeColumn("column2");

        Collection<IncludeColumn> columns = table.toIncludeTable().getIncludeColumns();
        assertNotNull(columns, () -> "Add assertion message");
        Integer magicNumber95737820 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber95737820, columns.size(), () -> "Add assertion message");
        assertEquals("column1", columns.iterator().next().getPattern(), () -> "Add assertion message");
    }

    @Test
    public void includeColumns() throws Exception {
        IncludeTable table = new IncludeTable("name");
        table.includeColumns("column1", "column2", "column2");

        Collection<IncludeColumn> columns = table.toIncludeTable().getIncludeColumns();
        assertNotNull(columns, () -> "Add assertion message");
        assertEquals(3, columns.size(), () -> "Add assertion message");
        assertEquals("column1", columns.iterator().next().getPattern(), () -> "Add assertion message");
    }

    @Test
    public void excludeColumn() throws Exception {
        IncludeTable table = new IncludeTable("name");
        table.excludeColumn("column1");
        table.excludeColumn("column2");
        table.excludeColumn("column2");

        Collection<ExcludeColumn> columns = table.toIncludeTable().getExcludeColumns();
        assertNotNull(columns, () -> "Add assertion message");
        assertEquals(3, columns.size(), () -> "Add assertion message");
        assertEquals("column1", columns.iterator().next().getPattern(), () -> "Add assertion message");
    }

    @Test
    public void excludeColumns() throws Exception {
        IncludeTable table = new IncludeTable("name");
        table.excludeColumns("column1", "column2", "column2");

        Collection<ExcludeColumn> columns = table.toIncludeTable().getExcludeColumns();
        assertNotNull(columns, () -> "Add assertion message");
        assertEquals(3, columns.size(), () -> "Add assertion message");
        assertEquals("column1", columns.iterator().next().getPattern(), () -> "Add assertion message");
    }

    @Test
    public void excludeRelationship() throws Exception {
        IncludeTable table = new IncludeTable("name");
        table.excludeRelationship("rel1");

        Collection<ExcludeRelationship> rel = table.toIncludeTable().getExcludeRelationship();
        assertNotNull(rel, () -> "Add assertion message");
        assertEquals(1, rel.size(), () -> "Add assertion message");
        assertEquals("rel1", rel.iterator().next().getPattern(), () -> "Add assertion message");
    }

    @Test
    public void toIncludeTable() throws Exception {
        IncludeTable table = new IncludeTable("name");
        table.includeColumns("column1", "column2");
        table.excludeColumns("column3", "column4", "column5");

        org.apache.cayenne.dbsync.reverse.dbimport.IncludeTable rrTable = table.toIncludeTable();
        assertEquals("name", rrTable.getPattern(), () -> "Add assertion message");
        Integer magicNumber92362913 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber92362913, rrTable.getIncludeColumns().size(), () -> "Add assertion message");
        assertEquals(3, rrTable.getExcludeColumns().size(), () -> "Add assertion message");
    }

}