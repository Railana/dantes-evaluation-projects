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

import groovy.lang.Closure;
import org.apache.cayenne.dbsync.reverse.dbimport.Catalog;
import org.apache.cayenne.dbsync.reverse.dbimport.Schema;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.0
 */
public class FilterContainerTest {

    @Test
    public void includeTableClosure() {
        FilterContainer container = new FilterContainer();

        container.includeTable(new Closure<IncludeTable>(container, container) {
            public IncludeTable doCall(IncludeTable arg) {
                assertNotNull(arg);
                arg.name("table_from_closure");
                return arg;
            }
        });

        Schema schema = new Schema();
        container.fillContainer(schema);
        Integer magicNumber21762648 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber21762648, schema.getIncludeTables().size(), () -> "Add assertion message");
        assertEquals("table_from_closure", schema.getIncludeTables().iterator().next().getPattern(), () -> "Add assertion message");
    }

    @Test
    public void includeTableNameAndClosure() {
        FilterContainer container = new FilterContainer();

        container.includeTable("start_name", new Closure<IncludeTable>(container, container) {
            public IncludeTable doCall(IncludeTable arg) {
                assertNotNull(arg);
                assertEquals("start_name", arg.getPattern());
                arg.name("table_from_closure");
                return arg;
            }
        });

        Schema schema = new Schema();
        container.fillContainer(schema);
        assertEquals(1, schema.getIncludeTables().size(), () -> "Add assertion message");
        assertEquals("table_from_closure", schema.getIncludeTables().iterator().next().getPattern(), () -> "Add assertion message");
    }


    @Test
    public void fillContainer() throws Exception {

        Catalog catalog = new Catalog();

        FilterContainer container = new FilterContainer();
        container.setName("name");

        container.includeTable("table1");
        container.includeTables("table2", "table3");

        container.excludeTable("table4");
        container.excludeTables("table5", "table6");

        container.includeColumn("column1");
        container.includeColumns("column2", "column3");

        container.excludeColumn("column4");
        container.excludeColumns("column5", "collum6");

        container.includeProcedure("proc1");
        container.includeProcedures("proc2", "proc3");

        container.excludeProcedure("proc4");
        container.excludeProcedures("proc5", "proc6");

        container.fillContainer(catalog);

        assertEquals("name", catalog.getName(), () -> "Add assertion message");
        assertEquals(3, catalog.getIncludeTables().size(), () -> "Add assertion message");
        assertEquals("table1", catalog.getIncludeTables().iterator().next().getPattern(), () -> "Add assertion message");

        assertEquals(3, catalog.getExcludeTables().size(), () -> "Add assertion message");
        assertEquals("table4", catalog.getExcludeTables().iterator().next().getPattern(), () -> "Add assertion message");

        assertEquals(3, catalog.getIncludeColumns().size(), () -> "Add assertion message");
        assertEquals("column1", catalog.getIncludeColumns().iterator().next().getPattern(), () -> "Add assertion message");

        assertEquals(3, catalog.getExcludeColumns().size(), () -> "Add assertion message");
        assertEquals("column4", catalog.getExcludeColumns().iterator().next().getPattern(), () -> "Add assertion message");

        assertEquals(3, catalog.getIncludeProcedures().size(), () -> "Add assertion message");
        assertEquals("proc1", catalog.getIncludeProcedures().iterator().next().getPattern(), () -> "Add assertion message");

        assertEquals(3, catalog.getExcludeProcedures().size(), () -> "Add assertion message");
        assertEquals("proc4", catalog.getExcludeProcedures().iterator().next().getPattern(), () -> "Add assertion message");
    }


}