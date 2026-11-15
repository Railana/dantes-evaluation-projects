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
import org.junit.Test;

import java.sql.Types;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class DbEntityColumnExtractorTest extends BaseColumnExtractorTest {

    @Test
    public void testExtractNoPrefix() {
        TranslatableQueryWrapper wrapper = new MockQueryWrapperBuilder()
                .withMetaData(new MockQueryMetadataBuilder()
                        .withDbEntity(createMockDbEntity("mock"))
                        .build())
                .build();
        TranslatorContext context = new MockTranslatorContext(wrapper);

        DbEntityColumnExtractor extractor = new DbEntityColumnExtractor(context);
        extractor.extract(null);

        Integer magicNumber87577654 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber87577654, context.getResultNodeList().size(), () -> "Add assertion message");

        ResultNodeDescriptor descriptor0 = context.getResultNodeList().get(0);
        ResultNodeDescriptor descriptor1 = context.getResultNodeList().get(1);

        assertNull(descriptor0.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor0.getNode(), () -> "Add assertion message");
        assertThat(descriptor0.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor0.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor0.isInDataRow(), () -> "Add assertion message");
        assertEquals("id", descriptor0.getDataRowKey(), () -> "Add assertion message");
        assertNotNull(descriptor0.getDbAttribute(), () -> "Add assertion message");
        assertEquals(Types.BIGINT, descriptor0.getJdbcType(), () -> "Add assertion message");

        assertNull(descriptor1.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor1.getNode(), () -> "Add assertion message");
        assertThat(descriptor1.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor1.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor1.isInDataRow(), () -> "Add assertion message");
        assertNotNull(descriptor1.getDbAttribute(), () -> "Add assertion message");
        assertEquals("name", descriptor1.getDataRowKey(), () -> "Add assertion message");
        assertEquals(Types.VARBINARY, descriptor1.getJdbcType(), () -> "Add assertion message");
        assertEquals("byte[]", descriptor1.getJavaType(), () -> "Add assertion message");
    }

    @Test
    public void testExtractWithPrefix() {
        DbEntity mockDbEntity = createMockDbEntity("mock1");
        DbEntity mock2DbEntity = createMockDbEntity("mock2");
        DataMap dataMap = new DataMap();
        dataMap.addDbEntity(mockDbEntity);
        dataMap.addDbEntity(mock2DbEntity);
        mockDbEntity.setDataMap(dataMap);

        TranslatableQueryWrapper wrapper = new MockQueryWrapperBuilder()
                .withMetaData(new MockQueryMetadataBuilder()
                        .withDbEntity(mockDbEntity)
                        .build())
                .build();
        TranslatorContext context = new MockTranslatorContext(wrapper);

        DbRelationship relationship = new DbRelationship();
        relationship.setSourceEntity(mockDbEntity);
        relationship.setTargetEntityName("mock1");
        context.getTableTree().addJoinTable("prefix", relationship, JoinType.INNER);

        DbEntityColumnExtractor extractor = new DbEntityColumnExtractor(context);
        extractor.extract("prefix");

        assertEquals(2, context.getResultNodeList().size(), () -> "Add assertion message");

        ResultNodeDescriptor descriptor0 = context.getResultNodeList().get(0);
        ResultNodeDescriptor descriptor1 = context.getResultNodeList().get(1);

        assertNull(descriptor0.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor0.getNode(), () -> "Add assertion message");
        assertThat(descriptor0.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor0.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor0.isInDataRow(), () -> "Add assertion message");
        assertEquals("prefix.id", descriptor0.getDataRowKey(), () -> "Add assertion message");
        assertNotNull(descriptor0.getDbAttribute(), () -> "Add assertion message");
        assertEquals(Types.BIGINT, descriptor0.getJdbcType(), () -> "Add assertion message");

        assertNull(descriptor1.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor1.getNode(), () -> "Add assertion message");
        assertThat(descriptor1.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor1.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor1.isInDataRow(), () -> "Add assertion message");
        assertNotNull(descriptor1.getDbAttribute(), () -> "Add assertion message");
        assertEquals("prefix.name", descriptor1.getDataRowKey(), () -> "Add assertion message");
        assertEquals(Types.VARBINARY, descriptor1.getJdbcType(), () -> "Add assertion message");
    }

}