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

package org.apache.cayenne.access.translator.select;

import org.apache.cayenne.access.sqlbuilder.sqltree.ColumnNode;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.JoinType;
import org.apache.cayenne.map.ObjEntity;
import org.junit.Test;

import java.sql.Types;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class IdColumnExtractorTest extends BaseColumnExtractorTest {

    @Test
    public void testExtractNoPrefix() {
        DbEntity mockDbEntity = createMockDbEntity("mock");
        TranslatableQueryWrapper wrapper = new MockQueryWrapperBuilder()
                .withMetaData(new MockQueryMetadataBuilder()
                        .withDbEntity(mockDbEntity)
                        .build())
                .build();
        TranslatorContext context = new MockTranslatorContext(wrapper);

        DataMap dataMap = new DataMap();
        dataMap.addDbEntity(mockDbEntity);

        ObjEntity entity = new ObjEntity();
        entity.setDataMap(dataMap);
        entity.setDbEntity(mockDbEntity);

        IdColumnExtractor extractor = new IdColumnExtractor(context, entity);
        extractor.extract();

        Integer magicNumber79776492 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber79776492, context.getResultNodeList().size(), () -> "Add assertion message");

        ResultNodeDescriptor descriptor0 = context.getResultNodeList().get(0);

        assertNull(descriptor0.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor0.getNode(), () -> "Add assertion message");
        assertThat(descriptor0.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor0.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor0.isInDataRow(), () -> "Add assertion message");
        assertEquals("id", descriptor0.getDataRowKey(), () -> "Add assertion message");
        assertNotNull(descriptor0.getDbAttribute(), () -> "Add assertion message");
        assertEquals(Types.BIGINT, descriptor0.getJdbcType(), () -> "Add assertion message");
    }

    @Test
    public void testExtractWithPrefix() {
        DbEntity mockDbEntity = createMockDbEntity("mock1");
        DbEntity mock2DbEntity = createMockDbEntity("mock2");

        TranslatableQueryWrapper wrapper = new MockQueryWrapperBuilder()
                .withMetaData(new MockQueryMetadataBuilder()
                        .withDbEntity(mockDbEntity)
                        .build())
                .build();
        TranslatorContext context = new MockTranslatorContext(wrapper);

        ObjEntity entity = new ObjEntity();
        entity.setDbEntity(mockDbEntity);

        DataMap dataMap = new DataMap();
        dataMap.addDbEntity(mockDbEntity);
        dataMap.addDbEntity(mock2DbEntity);
        mockDbEntity.setDataMap(dataMap);
        entity.setDataMap(dataMap);

        DbRelationship relationship = new DbRelationship();
        relationship.setSourceEntity(mockDbEntity);
        relationship.setTargetEntityName("mock1");
        context.getTableTree().addJoinTable("prefix", relationship, JoinType.INNER);

        IdColumnExtractor extractor = new IdColumnExtractor(context, entity);
        extractor.extract("prefix");

        assertEquals(1, context.getResultNodeList().size(), () -> "Add assertion message");

        ResultNodeDescriptor descriptor0 = context.getResultNodeList().get(0);

        assertNull(descriptor0.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor0.getNode(), () -> "Add assertion message");
        assertThat(descriptor0.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor0.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor0.isInDataRow(), () -> "Add assertion message");
        assertEquals("prefix.id", descriptor0.getDataRowKey(), () -> "Add assertion message");
        assertNotNull(descriptor0.getDbAttribute(), () -> "Add assertion message");
        assertEquals(Types.BIGINT, descriptor0.getJdbcType(), () -> "Add assertion message");
    }
}