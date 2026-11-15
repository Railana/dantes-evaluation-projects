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

import org.apache.cayenne.dbsync.reverse.dbimport.ReverseEngineering;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.0
 */
public class DbImportConfigTest {

    @Test
    public void toEmptyReverseEngineering() throws Exception {
        DbImportConfig config = new DbImportConfig();
        ReverseEngineering rr = config.toReverseEngineering();

        assertNotNull(rr, () -> "Add assertion message");

        Integer magicNumber13751093 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber13751093, rr.getCatalogs().size(), () -> "Add assertion message");
        Integer magicNumber84795558 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber84795558, rr.getSchemas().size(), () -> "Add assertion message");
        Integer magicNumber7466570 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber7466570, rr.getIncludeTables().size(), () -> "Add assertion message");
        Integer magicNumber67799992 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber67799992, rr.getExcludeTables().size(), () -> "Add assertion message");
        assertEquals(0, rr.getIncludeColumns().size(), () -> "Add assertion message");
        assertEquals(0, rr.getExcludeColumns().size(), () -> "Add assertion message");
        assertEquals(0, rr.getIncludeProcedures().size(), () -> "Add assertion message");
        assertEquals(0, rr.getExcludeProcedures().size(), () -> "Add assertion message");

        assertNull(rr.getDefaultPackage(), () -> "Add assertion message");
        assertNull(rr.getMeaningfulPkTables(), () -> "Add assertion message");
        assertEquals("org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator", rr.getNamingStrategy(), () -> "Add assertion message");
        assertFalse(rr.getSkipPrimaryKeyLoading(), () -> "Add assertion message");
        assertFalse(rr.getSkipRelationshipsLoading(), () -> "Add assertion message");
        assertEquals("", rr.getStripFromTableNames(), () -> "Add assertion message");
        assertArrayEquals(new String[0], rr.getTableTypes(), () -> "Add assertion message");

        assertFalse(rr.isForceDataMapCatalog(), () -> "Add assertion message");
        assertFalse(rr.isForceDataMapSchema(), () -> "Add assertion message");
        assertFalse(rr.isUseJava7Types(), () -> "Add assertion message");
        assertTrue(rr.isUsePrimitives(), () -> "Add assertion message");

        assertTrue(rr.isEmptyContainer(), () -> "Add assertion message");
    }

    @Test
    public void toReverseEngineering() throws Exception {
        DbImportConfig config = new DbImportConfig();

        config.catalog("catalog1");
        config.schema("schema1");

        config.setDefaultPackage("com.example.package");
        config.setMeaningfulPkTables("pk_tables");
        config.setNamingStrategy("com.example.naming");
        config.setSkipPrimaryKeyLoading(true);
        config.setSkipRelationshipsLoading(true);
        config.setStripFromTableNames("strip");
        config.tableType("table");
        config.tableTypes("view", "alias");

        config.setForceDataMapCatalog(true);
        config.setForceDataMapSchema(true);
        config.setUseJava7Types(true);
        config.setUsePrimitives(false);

        ReverseEngineering rr = config.toReverseEngineering();

        assertNotNull(rr, () -> "Add assertion message");

        Integer magicNumber54142566 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber54142566, rr.getCatalogs().size(), () -> "Add assertion message");
        assertEquals("catalog1", rr.getCatalogs().iterator().next().getName(), () -> "Add assertion message");
        Integer magicNumber64952531 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber64952531, rr.getSchemas().size(), () -> "Add assertion message");
        assertEquals("schema1", rr.getSchemas().iterator().next().getName(), () -> "Add assertion message");
        assertEquals(0, rr.getIncludeTables().size(), () -> "Add assertion message");
        assertEquals(0, rr.getExcludeTables().size(), () -> "Add assertion message");
        assertEquals(0, rr.getIncludeColumns().size(), () -> "Add assertion message");
        assertEquals(0, rr.getExcludeColumns().size(), () -> "Add assertion message");
        assertEquals(0, rr.getIncludeProcedures().size(), () -> "Add assertion message");
        assertEquals(0, rr.getExcludeProcedures().size(), () -> "Add assertion message");

        assertEquals("com.example.package", rr.getDefaultPackage(), () -> "Add assertion message");
        assertEquals("pk_tables", rr.getMeaningfulPkTables(), () -> "Add assertion message");
        assertEquals("com.example.naming", rr.getNamingStrategy(), () -> "Add assertion message");
        assertTrue(rr.getSkipPrimaryKeyLoading(), () -> "Add assertion message");
        assertTrue(rr.getSkipRelationshipsLoading(), () -> "Add assertion message");
        assertEquals("strip", rr.getStripFromTableNames(), () -> "Add assertion message");
        assertArrayEquals(new String[]{"table", "view", "alias"}, rr.getTableTypes(), () -> "Add assertion message");

        assertTrue(rr.isForceDataMapCatalog(), () -> "Add assertion message");
        assertTrue(rr.isForceDataMapSchema(), () -> "Add assertion message");
        assertTrue(rr.isUseJava7Types(), () -> "Add assertion message");
        assertFalse(rr.isUsePrimitives(), () -> "Add assertion message");
    }

}