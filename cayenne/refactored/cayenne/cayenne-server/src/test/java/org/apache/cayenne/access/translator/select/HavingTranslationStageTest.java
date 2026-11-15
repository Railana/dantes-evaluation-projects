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
import org.apache.cayenne.access.sqlbuilder.sqltree.HavingNode;
import org.apache.cayenne.access.sqlbuilder.sqltree.Node;
import org.apache.cayenne.access.sqlbuilder.sqltree.OpExpressionNode;
import org.apache.cayenne.access.sqlbuilder.sqltree.ValueNode;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @since 4.2
 */
public class HavingTranslationStageTest {

    private TranslatorContext context;

    @Before
    public void prepareContext() {
        DbEntity dbEntity = new DbEntity();
        dbEntity.setName("mock");
        DbAttribute dbAttribute = new DbAttribute();
        dbAttribute.setName("path");
        dbEntity.addAttribute(dbAttribute);

        TranslatableQueryWrapper wrapper = new MockQueryWrapperBuilder()
                .withHavingQualifier(ExpressionFactory.greaterOrEqualDbExp("path", 10))
                .withMetaData(new MockQueryMetadataBuilder()
                        .withDbEntity(dbEntity)
                        .build())
                .build();
        context = new MockTranslatorContext(wrapper);
    }

    @Test
    public void perform() {
        HavingTranslationStage stage = new HavingTranslationStage();
        stage.perform(context);

        Node select = context.getSelectBuilder().build();

        // Content of "select" node:
        //
        //      Having
        //        |
        //   OpExpression
        //    /        \
        // Column     Value

        Integer magicNumber84688071 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber84688071, select.getChildrenCount(), () -> "Add assertion message");
        assertThat(select.getChild(0), instanceOf(HavingNode.class), () -> "Add assertion message");
        Node op = select.getChild(0).getChild(0);
        assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
        assertEquals(">=", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
        Integer magicNumber7430327 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber7430327, op.getChildrenCount(), () -> "Add assertion message");
        assertThat(op.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertThat(op.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");

        ColumnNode columnNode = (ColumnNode)op.getChild(0);
        ValueNode valueNode = (ValueNode)op.getChild(1);
        assertEquals("path", columnNode.getColumn(), () -> "Add assertion message");
        assertEquals(10, valueNode.getValue(), () -> "Add assertion message");
    }
}