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

import java.sql.Types;

import org.apache.cayenne.access.sqlbuilder.sqltree.ColumnNode;
import org.apache.cayenne.access.types.ValueObjectTypeRegistry;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.reflect.generic.DefaultValueComparisonStrategyFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @since 4.2
 */
public class DescriptorColumnExtractorTest extends BaseColumnExtractorTest {

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
        entity.setName("mock");
        entity.setDataMap(dataMap);
        entity.setDbEntity(mockDbEntity);

        ObjAttribute attribute = new ObjAttribute();
        attribute.setName("not_name");
        attribute.setDbAttributePath("name");
        attribute.setType("java.lang.Integer");
        entity.addAttribute(attribute);

        dataMap.addObjEntity(entity);

        EntityResolver resolver = new EntityResolver();
        resolver.addDataMap(dataMap);
        resolver.setValueComparisionStrategyFactory(new DefaultValueComparisonStrategyFactory(mock(ValueObjectTypeRegistry.class)));

        DescriptorColumnExtractor extractor = new DescriptorColumnExtractor(context, resolver.getClassDescriptor("mock"));
        extractor.extract();

        Integer magicNumber32894060 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber32894060, context.getResultNodeList().size(), () -> "Add assertion message");

        ResultNodeDescriptor descriptor0 = context.getResultNodeList().get(0);
        ResultNodeDescriptor descriptor1 = context.getResultNodeList().get(1);

        assertNull(descriptor0.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor0.getNode(), () -> "Add assertion message");
        assertThat(descriptor0.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor0.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor0.isInDataRow(), () -> "Add assertion message");
        assertNotNull(descriptor0.getDbAttribute(), () -> "Add assertion message");
        assertEquals("name", descriptor0.getDataRowKey(), () -> "Add assertion message");
        assertEquals(Types.VARBINARY, descriptor0.getJdbcType(), () -> "Add assertion message");
        assertEquals("java.lang.Integer", descriptor0.getJavaType(), () -> "Add assertion message");

        assertNull(descriptor1.getProperty(), () -> "Add assertion message");
        assertNotNull(descriptor1.getNode(), () -> "Add assertion message");
        assertThat(descriptor1.getNode(), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertFalse(descriptor1.isAggregate(), () -> "Add assertion message");
        assertTrue(descriptor1.isInDataRow(), () -> "Add assertion message");
        assertEquals("id", descriptor1.getDataRowKey(), () -> "Add assertion message");
        assertNotNull(descriptor1.getDbAttribute(), () -> "Add assertion message");
        assertEquals(Types.BIGINT, descriptor1.getJdbcType(), () -> "Add assertion message");
    }
}