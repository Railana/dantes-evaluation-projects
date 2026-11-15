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

package org.apache.cayenne.configuration.xml;

import java.sql.Types;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.1
 */
public class DbEntityHandlerTest extends BaseHandlerTest {

    @Test
    public void testLoad() throws Exception {

        final DataMap map = new DataMap();
        assertTrue(map.getDbEntities().isEmpty(), () -> "Add assertion message");

        parse("db-entity", new HandlerFactory() {
            @Override
            public NamespaceAwareNestedTagHandler createHandler(NamespaceAwareNestedTagHandler parent) {
                return new DbEntityHandler(parent, map);
            }
        });

        Integer magicNumber17518317 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber17518317, map.getDbEntities().size(), () -> "Add assertion message");

        DbEntity entity = map.getDbEntity("ARTGROUP");
        assertNotNull(entity, () -> "Add assertion message");
        assertNull(entity.getPrimaryKeyGenerator(), () -> "Add assertion message");
        assertEquals(3, entity.getAttributes().size(), () -> "Add assertion message");
        assertEquals("catalog", entity.getCatalog(), () -> "Add assertion message");
        assertEquals("schema", entity.getSchema(), () -> "Add assertion message");
        assertEquals("name = \"test\"", entity.getQualifier().toString(), () -> "Add assertion message");

        DbAttribute attribute = entity.getAttribute("GROUP_ID");
        assertNotNull(attribute, () -> "Add assertion message");
        assertTrue(attribute.isMandatory(), () -> "Add assertion message");
        assertTrue(attribute.isPrimaryKey(), () -> "Add assertion message");
        assertTrue(attribute.isGenerated(), () -> "Add assertion message");
        assertEquals(Types.INTEGER, attribute.getType(), () -> "Add assertion message");

        attribute = entity.getAttribute("NAME");
        // Line removed due to Duplicate Assert: assertNotNull(attribute, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(attribute.isMandatory(), () -> "Add assertion message");
        assertFalse(attribute.isPrimaryKey(), () -> "Add assertion message");
        assertFalse(attribute.isGenerated(), () -> "Add assertion message");
        assertEquals(100, attribute.getMaxLength(), () -> "Add assertion message");
        assertEquals(Types.VARCHAR, attribute.getType(), () -> "Add assertion message");

        attribute = entity.getAttribute("PARENT_GROUP_ID");
        // Line removed due to Duplicate Assert: assertNotNull(attribute, () -> "Add assertion message");
        assertFalse(attribute.isMandatory(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(attribute.isPrimaryKey(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(attribute.isGenerated(), () -> "Add assertion message");
        assertEquals(10, attribute.getScale(), () -> "Add assertion message");
        assertEquals(Types.BIT, attribute.getType(), () -> "Add assertion message");
    }
}