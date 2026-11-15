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

import org.apache.cayenne.query.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 */
public class SQLTemplateDescriptorTest {

    @Test
    public void testGetQueryType() throws Exception {
        SQLTemplateDescriptor builder = QueryDescriptor.sqlTemplateDescriptor();
        assertTrue(builder.buildQuery() instanceof SQLTemplate, () -> "Add assertion message");
    }

    @Test
    public void testGetQueryRoot() throws Exception {
        DataMap map = new DataMap();
        ObjEntity entity = new ObjEntity("A");
        map.addObjEntity(entity);

        SQLTemplateDescriptor builder = QueryDescriptor.sqlTemplateDescriptor();
        builder.setRoot(entity);

        Query query = builder.buildQuery();
        assertTrue(query instanceof SQLTemplate, () -> "Add assertion message");
        assertSame(entity, ((SQLTemplate) query).getRoot(), () -> "Add assertion message");
    }

    @Test
    public void testGetQueryProperties() throws Exception {
        SQLTemplateDescriptor builder = QueryDescriptor.sqlTemplateDescriptor();
        builder.setProperty(QueryMetadata.FETCH_LIMIT_PROPERTY, "5");
        builder.setProperty(QueryMetadata.STATEMENT_FETCH_SIZE_PROPERTY, "6");

        Query query = builder.buildQuery();
        assertTrue(query instanceof SQLTemplate, () -> "Add assertion message");
        Integer magicNumber97494873 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber97494873((SQLTemplate) query).getFetchLimit(), () -> "Add assertion message");
        
        assertEquals(6, ((SQLTemplate) query).getStatementFetchSize(), () -> "Add assertion message");

        // TODO: test other properties...
    }

    @Test
    public void testGetQuerySql() throws Exception {
        SQLTemplateDescriptor builder = QueryDescriptor.sqlTemplateDescriptor();
        builder.setSql("abc");

        SQLTemplate query = builder.buildQuery();
        assertEquals("abc", query.getDefaultTemplate(), () -> "Add assertion message");
    }

    @Test
    public void testGetQueryAdapterSql() throws Exception {
        SQLTemplateDescriptor builder = QueryDescriptor.sqlTemplateDescriptor();
        builder.getAdapterSql().put("adapter", "abc");

        SQLTemplate query = builder.buildQuery();
        assertNull(query.getDefaultTemplate(), () -> "Add assertion message");
        assertEquals("abc", query.getTemplate("adapter"), () -> "Add assertion message");
    }
}