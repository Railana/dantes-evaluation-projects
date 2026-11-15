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

import org.apache.cayenne.remote.hessian.service.HessianUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EntityTest {

    @Test
    public void testSerializabilityWithHessian() throws Exception {
        Entity entity = new MockEntity("entity");

        Entity d1 = (Entity) HessianUtil.cloneViaClientServerSerialization(
                entity,
                new EntityResolver());
        assertEquals(entity.getName(), d1.getName(), () -> "Add assertion message");

        entity.addAttribute(new MockAttribute("abc"));
        entity.addRelationship(new MockRelationship("xyz"));
        Entity d2 = (Entity) HessianUtil.cloneViaClientServerSerialization(
                entity,
                new EntityResolver());
        assertNotNull(d2.getAttribute("abc"), () -> "Add assertion message");
        assertNotNull(d2.getRelationship("xyz"), () -> "Add assertion message");

        // test that ref collection wrappers are still working
        assertNotNull(d2.getAttributes(), () -> "Add assertion message");
        assertEquals(entity.getAttributes().size(), d2.getAttributes().size(), () -> "Add assertion message");
        assertTrue(d2.getAttributes().contains(d2.getAttribute("abc")), () -> "Add assertion message");

        assertNotNull(d2.getRelationships(), () -> "Add assertion message");
        assertEquals(entity.getRelationships().size(), d2.getRelationships().size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(d2.getAttributes().contains(d2.getAttribute("abc")), () -> "Add assertion message");

        assertNotNull(d2.getAttributeMap(), () -> "Add assertion message");
        assertEquals(entity.getAttributes().size(), d2.getAttributeMap().size(), () -> "Add assertion message");
        assertSame(d2.getAttribute("abc"), d2.getAttributeMap().get("abc"), () -> "Add assertion message");

        assertNotNull(d2.getRelationshipMap(), () -> "Add assertion message");
        assertEquals(entity.getRelationships().size(), d2.getRelationshipMap().size(), () -> "Add assertion message");
        assertSame(d2.getRelationship("xyz"), d2.getRelationshipMap().get("xyz"), () -> "Add assertion message");
    }
}