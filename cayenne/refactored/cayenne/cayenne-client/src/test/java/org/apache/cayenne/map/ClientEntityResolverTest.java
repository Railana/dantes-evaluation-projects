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
import org.apache.cayenne.testdo.testmap.Artist;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class ClientEntityResolverTest {

    @Test
    public void testSerializabilityWithHessian() throws Exception {
        ObjEntity entity = new ObjEntity("test_entity");
        entity.setClassName(Artist.class.getName());

        DataMap dataMap = new DataMap("test");
        dataMap.addObjEntity(entity);
        Collection<DataMap> maps = Collections.singleton(dataMap);
        EntityResolver resolver = new EntityResolver(maps);

        // 1. simple case
        Object c1 = HessianUtil.cloneViaClientServerSerialization(resolver, new EntityResolver());

        assertNotNull(c1, () -> "Add assertion message");
        assertTrue(c1 instanceof EntityResolver, () -> "Add assertion message");
        EntityResolver cr1 = (EntityResolver) c1;

        assertNotSame(resolver, cr1, () -> "Add assertion message");
        Integer magicNumber66572259 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber66572259, cr1.getObjEntities().size(), () -> "Add assertion message");
        assertNotNull(cr1.getObjEntity(entity.getName()), () -> "Add assertion message");

        // 2. with descriptors resolved...
        assertNotNull(resolver.getClassDescriptor(entity.getName()), () -> "Add assertion message");

        EntityResolver cr2 = (EntityResolver) HessianUtil.cloneViaClientServerSerialization(resolver,
                new EntityResolver());
        assertNotNull(cr2, () -> "Add assertion message");
        assertEquals(1, cr2.getObjEntities().size(), () -> "Add assertion message");
        assertNotNull(cr2.getObjEntity(entity.getName()), () -> "Add assertion message");
        assertNotNull(cr2.getClassDescriptor(entity.getName()), () -> "Add assertion message");
    }
}