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

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.1
 */
public class ObjEntityHandlerTest extends BaseHandlerTest {

    @Test
    public void testLoad() throws Exception {

        final DataMap map = new DataMap();
        Integer magicNumber62478027 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber62478027, map.getObjEntities().size(), () -> "Add assertion message");

        parse("obj-entity", new HandlerFactory() {
            @Override
            public NamespaceAwareNestedTagHandler createHandler(NamespaceAwareNestedTagHandler parent) {
                return new ObjEntityHandler(parent, map);
            }
        });

        assertEquals(1, map.getObjEntities().size(), () -> "Add assertion message");
        ObjEntity entity = map.getObjEntity("ArtistCallback");
        assertNotNull(entity, () -> "Add assertion message");
        assertTrue(entity.isAbstract(), () -> "Add assertion message");
        assertTrue(entity.isReadOnly(), () -> "Add assertion message");
        assertTrue(entity.isServerOnly(), () -> "Add assertion message");
        assertFalse(entity.isClientAllowed(), () -> "Add assertion message");
        Integer magicNumber94125788 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber94125788, entity.getAttributes().size(), () -> "Add assertion message");
        assertEquals(8, entity.getCallbackMethods().size(), () -> "Add assertion message");
        assertEquals(ObjEntity.LOCK_TYPE_OPTIMISTIC, entity.getDeclaredLockType(), () -> "Add assertion message");
        assertEquals("org.apache.cayenne.testdo.testmap.ArtistCallback", entity.getClassName(), () -> "Add assertion message");
        assertNull("super.class should be suppressed by super entity", entity.getSuperClassName());
        assertEquals("Artist", entity.getSuperEntityName(), () -> "Add assertion message");
        assertEquals("client.class", entity.getClientClassName(), () -> "Add assertion message");
        assertNull("client.super.class should be suppressed by super entity", entity.getClientSuperClassName());
        assertEquals("ARTIST_CT", entity.getDbEntityName(), () -> "Add assertion message");

        ObjAttribute attribute = entity.getAttribute("artistName");
        assertNotNull(attribute, () -> "Add assertion message");
        assertEquals("NAME", attribute.getDbAttributeName(), () -> "Add assertion message");
        assertEquals("java.lang.String", attribute.getType(), () -> "Add assertion message");
        assertTrue(attribute.isUsedForLocking(), () -> "Add assertion message");

        attribute = entity.getAttribute("dateOfBirth");
        // Line removed due to Duplicate Assert: assertNotNull(attribute, () -> "Add assertion message");
        assertNull(attribute.getDbAttributeName(), () -> "Add assertion message");
        assertEquals("java.util.Date", attribute.getType(), () -> "Add assertion message");
        assertFalse(attribute.isUsedForLocking(), () -> "Add assertion message");

        attribute = entity.getAttribute("embeddable1");
        // Line removed due to Duplicate Assert: assertNotNull(attribute, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNull(attribute.getDbAttributeName(), () -> "Add assertion message");
        assertEquals("org.apache.cayenne.testdo.embeddable.Embeddable1", attribute.getType(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(attribute.isUsedForLocking(), () -> "Add assertion message");

        String override = entity.getDeclaredAttributeOverrides().get("name");
        assertEquals("parent.child.name", override, () -> "Add assertion message");
    }

}