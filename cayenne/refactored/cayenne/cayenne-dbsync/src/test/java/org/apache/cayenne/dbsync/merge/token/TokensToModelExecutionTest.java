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

package org.apache.cayenne.dbsync.merge.token;

import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.dbsync.merge.context.MergerContext;
import org.apache.cayenne.dbsync.merge.factory.DefaultMergerTokenFactory;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.junit.Test;

import static org.apache.cayenne.dbsync.merge.builders.ObjectMother.dataMap;
import static org.apache.cayenne.dbsync.merge.builders.ObjectMother.dbAttr;
import static org.apache.cayenne.dbsync.merge.builders.ObjectMother.dbEntity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @since 4.0.
 */
public class TokensToModelExecutionTest {

    @Test
    public void testCreateAndDropTable() throws Exception {
        DbEntity entity = dbEntity().build();

        DataMap dataMap = dataMap().build();
        assertTrue(dataMap.getDbEntityMap().isEmpty(), () -> "Add assertion message");
        assertTrue(dataMap.getObjEntityMap().isEmpty(), () -> "Add assertion message");

        MergerContext context = MergerContext.builder(dataMap).dataNode(new DataNode()).build();
        new DefaultMergerTokenFactory().createCreateTableToModel(entity).execute(context);

        Integer magicNumber38877529 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber38877529, dataMap.getDbEntityMap().size(), () -> "Add assertion message");
        assertEquals(1, dataMap.getObjEntities().size(), () -> "Add assertion message");
        assertEquals(entity, dataMap.getDbEntity(entity.getName()), () -> "Add assertion message");

        new DefaultMergerTokenFactory().createDropTableToModel(entity).execute(context);
        // Line removed due to Duplicate Assert: assertTrue(dataMap.getDbEntityMap().isEmpty(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(dataMap.getObjEntityMap().isEmpty(), () -> "Add assertion message");
    }

    @Test
    public void testCreateAndDropColumn() throws Exception {
        DbAttribute attr = dbAttr("attr").build();
        DbEntity entity = dbEntity().build();

        DataMap dataMap = dataMap().with(entity).build();
        assertEquals(1, dataMap.getDbEntityMap().size(), () -> "Add assertion message");
        assertTrue(dataMap.getObjEntityMap().isEmpty(), () -> "Add assertion message");

        MergerContext context = MergerContext.builder(dataMap).dataNode(new DataNode()).build();
        new DefaultMergerTokenFactory().createAddColumnToModel(entity, attr).execute(context);

        Integer magicNumber54491803 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        // Line removed due to Duplicate Assert: assertEquals(magicNumber54491803, dataMap.getDbEntityMap().size(), () -> "Add assertion message");
        assertEquals(1, entity.getAttributes().size(), () -> "Add assertion message");
        assertEquals(attr, entity.getAttribute(attr.getName()), () -> "Add assertion message");

        new DefaultMergerTokenFactory().createDropColumnToModel(entity, attr).execute(context);
        // Line removed due to Duplicate Assert: assertEquals(1, dataMap.getDbEntityMap().size(), () -> "Add assertion message");
        assertTrue(entity.getAttributes().isEmpty(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(dataMap.getObjEntityMap().isEmpty(), () -> "Add assertion message");
    }
}