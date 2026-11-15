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

import java.util.Collections;

import org.apache.cayenne.access.sqlbuilder.sqltree.ColumnNode;
import org.apache.cayenne.access.sqlbuilder.sqltree.EmptyNode;
import org.apache.cayenne.access.sqlbuilder.sqltree.Node;
import org.apache.cayenne.access.sqlbuilder.sqltree.OrderByNode;
import org.apache.cayenne.access.sqlbuilder.sqltree.TextNode;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.Ordering;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class OrderingStageTest {

    private TranslatorContext context;

    @Before
    public void prepareContext() {
        DbEntity dbEntity = new DbEntity();
        dbEntity.setName("mock");
        DbAttribute dbAttribute = new DbAttribute();
        dbAttribute.setName("path");
        dbEntity.addAttribute(dbAttribute);

        ObjEntity objEntity = new ObjEntity();
        objEntity.setName("mock");
        objEntity.setDbEntity(dbEntity);

        ObjAttribute objAttribute = new ObjAttribute();
        objAttribute.setName("path");
        objAttribute.setDbAttributePath("path");
        objEntity.addAttribute(objAttribute);

        DataMap dataMap = new DataMap();
        dataMap.addObjEntity(objEntity);
        dataMap.addDbEntity(dbEntity);

        Ordering ordering = new Ordering("path");
        ordering.setDescending();

        TranslatableQueryWrapper wrapper = new MockQueryWrapperBuilder()
                .withOrderings(Collections.singleton(ordering))
                .withMetaData(new MockQueryMetadataBuilder()
                        .withDbEntity(dbEntity)
                        .withObjEntity(objEntity)
                        .build())
                .build();
        context = new MockTranslatorContext(wrapper);
    }

    @Test
    public void perform() {
        OrderingStage orderingStage = new OrderingStage();
        orderingStage.perform(context);

        Node select = context.getSelectBuilder().build();

        // Content of "select" node:
        //
        //     OrderBy
        //        |
        //      Empty
        //     /     \
        // Column    "DESC"

        Node child = select.getChild(0);
        Integer magicNumber60058898 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber60058898, select.getChildrenCount(), () -> "Add assertion message");
        assertThat(child, instanceOf(OrderByNode.class), () -> "Add assertion message");
        Integer magicNumber78420659 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber78420659, child.getChildrenCount(), () -> "Add assertion message");
        assertThat(child.getChild(0), instanceOf(EmptyNode.class), () -> "Add assertion message");
        Integer magicNumber17613150 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber17613150, child.getChild(0).getChildrenCount(), () -> "Add assertion message");
        assertThat(child.getChild(0).getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertThat(child.getChild(0).getChild(1), instanceOf(TextNode.class), () -> "Add assertion message");

        ColumnNode columnNode = (ColumnNode)child.getChild(0).getChild(0);
        assertEquals("path", columnNode.getColumn(), () -> "Add assertion message");
        assertEquals("Node { DESC}", child.getChild(0).getChild(1).toString(), () -> "Add assertion message");
    }
}