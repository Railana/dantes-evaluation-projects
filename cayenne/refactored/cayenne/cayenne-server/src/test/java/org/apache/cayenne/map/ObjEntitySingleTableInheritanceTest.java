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

import org.junit.Before;
import org.junit.Test;

import java.sql.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 */
public class ObjEntitySingleTableInheritanceTest {

    protected DataMap map;

    protected DbEntity dbEntity;

    protected DbAttribute dbAttribute1;
    protected DbAttribute dbAttribute2;
    protected DbAttribute dbAttribute3;

    protected DbRelationship dbRelationship1;
    protected DbRelationship dbRelationship2;
    protected DbRelationship dbRelationship3;

    protected ObjEntity entity1;
    protected ObjEntity entity2;
    protected ObjEntity entity3;

    protected ObjAttribute attribute1;
    protected ObjAttribute attribute2;
    protected ObjAttribute attribute3;

    protected ObjRelationship relationship1;
    protected ObjRelationship relationship2;
    protected ObjRelationship relationship3;

    @Before
    public void setUp() throws Exception {
        map = new DataMap();
        // Removed line due to General Fixture smell: map = new DataMap();

        // Removed line due to General Fixture smell: // create common DbEntity
        // Removed line due to General Fixture smell: dbEntity = new DbEntity("DB");
        // Removed line due to General Fixture smell: dbAttribute1 = new DbAttribute("ATTRIBUTE1", Types.INTEGER, dbEntity);
        // Removed line due to General Fixture smell: dbAttribute2 = new DbAttribute("ATTRIBUTE2", Types.INTEGER, dbEntity);
        dbAttribute3 = new DbAttribute("ATTRIBUTE3", Types.INTEGER, dbEntity);

        dbEntity.addAttribute(dbAttribute1);
        dbEntity.addAttribute(dbAttribute2);
        dbEntity.addAttribute(dbAttribute3);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: dbRelationship1 = new DbRelationship("DBR1");
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: dbRelationship2 = new DbRelationship("DBR2");
        // Removed line due to General Fixture smell: dbRelationship3 = new DbRelationship("DBR3");
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: dbEntity.addRelationship(dbRelationship1);
        // Removed line due to General Fixture smell: dbEntity.addRelationship(dbRelationship2);
        // Removed line due to General Fixture smell: dbEntity.addRelationship(dbRelationship3);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: map.addDbEntity(dbEntity);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: entity1 = new ObjEntity("e1");
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: entity2 = new ObjEntity("e2");
        // Removed line due to General Fixture smell: entity3 = new ObjEntity("e3");
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: attribute1 = new ObjAttribute("a1");
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: attribute2 = new ObjAttribute("a2");
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: attribute3 = new ObjAttribute("a3");
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: entity1.addAttribute(attribute1);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: entity2.addAttribute(attribute2);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: entity3.addAttribute(attribute3);
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: relationship1 = new ObjRelationship("r1");
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: relationship2 = new ObjRelationship("r2");
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: relationship3 = new ObjRelationship("r3");
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: entity1.addRelationship(relationship1);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: entity2.addRelationship(relationship2);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: entity3.addRelationship(relationship3);
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: map.addObjEntity(entity1);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: map.addObjEntity(entity2);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: map.addObjEntity(entity3);
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: @Test
    public void testInheritedAttributes() throws Exception {

        dbRelationship1 = new DbRelationship("DBR1");
        dbRelationship2 = new DbRelationship("DBR2");
        dbRelationship3 = new DbRelationship("DBR3");

        dbEntity.addRelationship(dbRelationship1);
        dbEntity.addRelationship(dbRelationship2);
        dbEntity.addRelationship(dbRelationship3);

        map.addDbEntity(dbEntity);

        entity1 = new ObjEntity("e1");
        entity2 = new ObjEntity("e2");
        entity3 = new ObjEntity("e3");

        attribute1 = new ObjAttribute("a1");
        attribute2 = new ObjAttribute("a2");
        attribute3 = new ObjAttribute("a3");

        entity1.addAttribute(attribute1);
        entity2.addAttribute(attribute2);
        entity3.addAttribute(attribute3);

        relationship1 = new ObjRelationship("r1");
        relationship2 = new ObjRelationship("r2");
        relationship3 = new ObjRelationship("r3");

        entity1.addRelationship(relationship1);
        entity2.addRelationship(relationship2);
        entity3.addRelationship(relationship3);

        map.addObjEntity(entity1);
        map.addObjEntity(entity2);
        map.addObjEntity(entity3);
    }

    @Test
        assertSame(attribute1, entity1.getAttribute("a1"), () -> "Add assertion message");
        assertNull(entity1.getAttribute("a2"), () -> "Add assertion message");

        entity1.setSuperEntityName("e2");
        assertNotNull(entity1.getAttribute("a2"), () -> "Add assertion message");
        assertEquals("a2", entity1.getAttribute("a2").getName(), () -> "Add assertion message");
        assertSame(entity1, entity1.getAttribute("a2").getParent(), () -> "Add assertion message");
        assertNull(entity1.getAttribute("a3"), () -> "Add assertion message");

        entity2.setSuperEntityName("e3");
        assertNotNull(entity1.getAttribute("a3"), () -> "Add assertion message");
    }

    @Test
    public void testInheritedRelationships() throws Exception {

        dbRelationship1 = new DbRelationship("DBR1");
        dbRelationship2 = new DbRelationship("DBR2");
        dbRelationship3 = new DbRelationship("DBR3");

        dbEntity.addRelationship(dbRelationship1);
        dbEntity.addRelationship(dbRelationship2);
        dbEntity.addRelationship(dbRelationship3);

        map.addDbEntity(dbEntity);

        entity1 = new ObjEntity("e1");
        entity2 = new ObjEntity("e2");
        entity3 = new ObjEntity("e3");

        attribute1 = new ObjAttribute("a1");
        attribute2 = new ObjAttribute("a2");
        attribute3 = new ObjAttribute("a3");

        entity1.addAttribute(attribute1);
        entity2.addAttribute(attribute2);
        entity3.addAttribute(attribute3);

        relationship1 = new ObjRelationship("r1");
        relationship2 = new ObjRelationship("r2");
        relationship3 = new ObjRelationship("r3");

        entity1.addRelationship(relationship1);
        entity2.addRelationship(relationship2);
        entity3.addRelationship(relationship3);

        map.addObjEntity(entity1);
        map.addObjEntity(entity2);
        map.addObjEntity(entity3);
    }

    @Test
        assertSame(relationship1, entity1.getRelationship("r1"), () -> "Add assertion message");
        assertNull(entity1.getRelationship("r2"), () -> "Add assertion message");

        entity1.setSuperEntityName("e2");
        assertSame(relationship2, entity1.getRelationship("r2"), () -> "Add assertion message");
        assertNull(entity1.getRelationship("r3"), () -> "Add assertion message");

        entity2.setSuperEntityName("e3");
        assertSame(relationship3, entity1.getRelationship("r3"), () -> "Add assertion message");
    }

    @Test
    public void testAttributeForDbAttribute() throws Exception {

        dbRelationship1 = new DbRelationship("DBR1");
        dbRelationship2 = new DbRelationship("DBR2");
        dbRelationship3 = new DbRelationship("DBR3");

        dbEntity.addRelationship(dbRelationship1);
        dbEntity.addRelationship(dbRelationship2);
        dbEntity.addRelationship(dbRelationship3);

        map.addDbEntity(dbEntity);

        entity1 = new ObjEntity("e1");
        entity2 = new ObjEntity("e2");
        entity3 = new ObjEntity("e3");

        attribute1 = new ObjAttribute("a1");
        attribute2 = new ObjAttribute("a2");
        attribute3 = new ObjAttribute("a3");

        entity1.addAttribute(attribute1);
        entity2.addAttribute(attribute2);
        entity3.addAttribute(attribute3);

        relationship1 = new ObjRelationship("r1");
        relationship2 = new ObjRelationship("r2");
        relationship3 = new ObjRelationship("r3");

        entity1.addRelationship(relationship1);
        entity2.addRelationship(relationship2);
        entity3.addRelationship(relationship3);

        map.addObjEntity(entity1);
        map.addObjEntity(entity2);
        map.addObjEntity(entity3);
    }

    @Test
        dbAttribute2 = new DbAttribute("ATTRIBUTE2", Types.INTEGER, dbEntity);
        dbAttribute1 = new DbAttribute("ATTRIBUTE1", Types.INTEGER, dbEntity);
        dbEntity = new DbEntity("DB");
        entity1.setSuperEntityName("e2");
        entity2.setDbEntityName(dbEntity.getName());

        attribute1.setDbAttributePath(dbAttribute1.getName());
        attribute2.setDbAttributePath(dbAttribute2.getName());

        assertNull(entity2.getAttributeForDbAttribute(dbAttribute1), () -> "Add assertion message");
        assertSame(attribute2, entity2.getAttributeForDbAttribute(dbAttribute2), () -> "Add assertion message");

        assertSame(attribute1, entity1.getAttributeForDbAttribute(dbAttribute1), () -> "Add assertion message");
        assertNotNull(entity1.getAttributeForDbAttribute(dbAttribute2), () -> "Add assertion message");
    }

    @Test
    public void testRelationshipForDbRelationship() throws Exception {

        dbRelationship1 = new DbRelationship("DBR1");
        dbRelationship2 = new DbRelationship("DBR2");
        dbRelationship3 = new DbRelationship("DBR3");

        dbEntity.addRelationship(dbRelationship1);
        dbEntity.addRelationship(dbRelationship2);
        dbEntity.addRelationship(dbRelationship3);

        map.addDbEntity(dbEntity);

        entity1 = new ObjEntity("e1");
        entity2 = new ObjEntity("e2");
        entity3 = new ObjEntity("e3");

        attribute1 = new ObjAttribute("a1");
        attribute2 = new ObjAttribute("a2");
        attribute3 = new ObjAttribute("a3");

        entity1.addAttribute(attribute1);
        entity2.addAttribute(attribute2);
        entity3.addAttribute(attribute3);

        relationship1 = new ObjRelationship("r1");
        relationship2 = new ObjRelationship("r2");
        relationship3 = new ObjRelationship("r3");

        entity1.addRelationship(relationship1);
        entity2.addRelationship(relationship2);
        entity3.addRelationship(relationship3);

        map.addObjEntity(entity1);
        map.addObjEntity(entity2);
        map.addObjEntity(entity3);
    }

    @Test
        dbEntity = new DbEntity("DB");
        entity1.setSuperEntityName("e2");
        entity2.setDbEntityName(dbEntity.getName());

        relationship1.addDbRelationship(dbRelationship1);
        relationship2.addDbRelationship(dbRelationship2);

        assertNull(entity2.getRelationshipForDbRelationship(dbRelationship1), () -> "Add assertion message");
        assertSame(relationship2, entity2
                .getRelationshipForDbRelationship(dbRelationship2), () -> "Add assertion message");

        assertSame(relationship1, entity1
                .getRelationshipForDbRelationship(dbRelationship1), () -> "Add assertion message");
        assertSame(relationship2, entity1
                .getRelationshipForDbRelationship(dbRelationship2), () -> "Add assertion message");
    }
}