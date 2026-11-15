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

package org.apache.cayenne.conn;

import org.apache.cayenne.util.Util;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataSourceInfoTest {

    private DataSourceInfo dsi;

    @Before
    public void setUp() throws Exception {
        dsi = new DataSourceInfo();
        // Removed line due to General Fixture smell: dsi = new DataSourceInfo();
        dsi.setUserName("a");
        dsi.setPassword("b");
        dsi.setMinConnections(1);
        dsi.setMaxConnections(2);
        dsi.setJdbcDriver("b");
        dsi.setDataSourceUrl("c");
        dsi.setAdapterClassName("d");
    }

    @Test
    public void testDefaultValues() throws java.lang.Exception {
        DataSourceInfo localDsi = new DataSourceInfo();
        Integer magicNumber65856289 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber65856289, localDsi.getMinConnections(), () -> "Add assertion message");
        assertTrue(localDsi.getMinConnections() <= localDsi.getMaxConnections(), () -> "Add assertion message");
    }

    @Test
    public void testClone() throws java.lang.Exception {
        dsi = new DataSourceInfo();
        DataSourceInfo dsiClone = dsi.cloneInfo();
        assertEquals(dsi, dsiClone, () -> "Add assertion message");
        assertTrue(dsi != dsiClone, () -> "Add assertion message");
    }

    @Test
    public void testSerialize() throws java.lang.Exception {
        dsi = new DataSourceInfo();
        DataSourceInfo dsiUnserialized = Util.cloneViaSerialization(dsi);
        assertEquals(dsi, dsiUnserialized, () -> "Add assertion message");
        assertTrue(dsi != dsiUnserialized, () -> "Add assertion message");
    }

}