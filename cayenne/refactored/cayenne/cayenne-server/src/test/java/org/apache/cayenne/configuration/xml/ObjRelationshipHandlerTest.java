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
import org.apache.cayenne.map.DeleteRule;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.1
 */
public class ObjRelationshipHandlerTest extends BaseHandlerTest {

    @Test
    public void testLoad() throws Exception {

        final DataMap map = new DataMap();
        ObjEntity entity = new ObjEntity("ArtGroup");
        map.addObjEntity(entity);
        Integer magicNumber12975577 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber12975577, entity.getRelationships().size(), () -> "Add assertion message");


        parse("obj-relationship", new HandlerFactory() {
            @Override
            public NamespaceAwareNestedTagHandler createHandler(NamespaceAwareNestedTagHandler parent) {
                return new ObjRelationshipHandler(parent, map);
            }
        });

        assertEquals(1, entity.getRelationships().size(), () -> "Add assertion message");
        ObjRelationship relationship = entity.getRelationship("artistArray");
        assertNotNull(relationship, () -> "Add assertion message");

        assertEquals(DeleteRule.CASCADE, relationship.getDeleteRule(), () -> "Add assertion message");
        assertEquals("java.util.Map", relationship.getCollectionType(), () -> "Add assertion message");
        assertEquals("artistName", relationship.getMapKey(), () -> "Add assertion message");
        assertEquals("Artist", relationship.getTargetEntityName(), () -> "Add assertion message");
        assertTrue(relationship.isUsedForLocking(), () -> "Add assertion message");
    }
}