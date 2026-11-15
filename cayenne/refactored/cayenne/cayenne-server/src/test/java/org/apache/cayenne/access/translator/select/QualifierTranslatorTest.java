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

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.sqlbuilder.SQLGenerationVisitor;
import org.apache.cayenne.access.sqlbuilder.StringBuilderAppendable;
import org.apache.cayenne.access.sqlbuilder.sqltree.*;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.parser.ASTAsterisk;
import org.apache.cayenne.exp.property.BaseProperty;
import org.apache.cayenne.map.*;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class QualifierTranslatorTest {

    private QualifierTranslator translator;

    @Before
    public void prepareTranslator() {
        DbEntity dbEntity = new DbEntity();
        dbEntity.setName("mock");

        DbAttribute dbAttributeA = new DbAttribute();
        dbAttributeA.setName("a");
        dbEntity.addAttribute(dbAttributeA);

        DbAttribute dbAttributeB = new DbAttribute();
        dbAttributeB.setName("b");
        dbAttributeB.setPrimaryKey(true);
        dbEntity.addAttribute(dbAttributeB);

        ObjEntity entity = new ObjEntity();
        entity.setName("mock");
        ObjAttribute attribute = new ObjAttribute();
        attribute.setName("a");
        attribute.setDbAttributePath("a");
        entity.addAttribute(attribute);

        ObjAttribute attribute2 = new ObjAttribute();
        attribute2.setName("b");
        attribute2.setDbAttributePath("b");
        entity.addAttribute(attribute2);

        entity.setDbEntity(dbEntity);

        DataMap dataMap = new DataMap();
        dataMap.addDbEntity(dbEntity);
        dataMap.addObjEntity(entity);

        EntityResolver resolver = new EntityResolver();
        resolver.addDataMap(dataMap);

        TranslatableQueryWrapper wrapper = new MockQueryWrapperBuilder()
                .withMetaData(new MockQueryMetadataBuilder()
                        .withDbEntity(dbEntity)
                        .withObjEntity(entity)
                        .build())
                .build();
        TranslatorContext context = new MockTranslatorContext(wrapper, resolver);
        // Removed line due to General Fixture smell: translator = new QualifierTranslator(context);
    }

    @Test
    public void translateNull() {
        translator = new QualifierTranslator(context);
        assertNull(translator.translate((Expression)null), () -> "Add assertion message");
        assertNull(translator.translate((BaseProperty<?>)null), () -> "Add assertion message");
    }

    @Test
    public void translateIn() {
        translator = new QualifierTranslator(context);
        {
            Node in = translate("db:a in (1,2)");
            assertThat(in, instanceOf(InNode.class), () -> "Add assertion message");
            Integer magicNumber88562638 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber88562638, in.getChildrenCount(), () -> "Add assertion message");
            assertFalse(((InNode) in).isNot(), () -> "Add assertion message");
            assertThat(in.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(in.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) in.getChild(0)).getColumn(), () -> "Add assertion message");
            assertArrayEquals(new Object[]{1, 2}, (Object[]) ((ValueNode) in.getChild(1)).getValue(), () -> "Add assertion message");
        }

        {
            Node in = translator.translate(ExpressionFactory.inExp("a", Arrays.asList(1, 2, 3)));
            // Line removed due to Duplicate Assert: assertThat(in, instanceOf(InNode.class), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, in.getChildrenCount(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertFalse(((InNode) in).isNot(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertThat(in.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertThat(in.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals("a", ((ColumnNode) in.getChild(0)).getColumn(), () -> "Add assertion message");
            assertArrayEquals(new Object[]{1, 2, 3}, (Object[]) ((ValueNode) in.getChild(1)).getValue(), () -> "Add assertion message");
        }

        {
            Node notIn = translate("db:a not in (1,2)");
            assertThat(notIn, instanceOf(InNode.class), () -> "Add assertion message");
            assertEquals(2, notIn.getChildrenCount(), () -> "Add assertion message");
            assertTrue(((InNode) notIn).isNot(), () -> "Add assertion message");
            assertThat(notIn.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(notIn.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) notIn.getChild(0)).getColumn(), () -> "Add assertion message");
            assertArrayEquals(new Object[]{1, 2}, (Object[]) ((ValueNode) notIn.getChild(1)).getValue(), () -> "Add assertion message");
        }

    }

    @Test
    public void translateBetween() {
        {
            Node between = translate("db:a between 1 and 5");
            assertThat(between, instanceOf(BetweenNode.class), () -> "Add assertion message");
            assertEquals(3, between.getChildrenCount(), () -> "Add assertion message");
            assertFalse(((BetweenNode) between).isNot(), () -> "Add assertion message");
            assertThat(between.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(between.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertThat(between.getChild(2), instanceOf(ValueNode.class), () -> "Add assertion message");
        }

        {
            Node notBetween = translate("db:b not between 2 and 6");
            assertThat(notBetween, instanceOf(BetweenNode.class), () -> "Add assertion message");
            assertEquals(3, notBetween.getChildrenCount(), () -> "Add assertion message");
            assertTrue(((BetweenNode) notBetween).isNot(), () -> "Add assertion message");
            assertThat(notBetween.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(notBetween.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertThat(notBetween.getChild(2), instanceOf(ValueNode.class), () -> "Add assertion message");
        }
    }

    @Test
    public void translateNot() {
        Node not = translate("not true");
        assertThat(not, instanceOf(NotNode.class), () -> "Add assertion message");
        Integer magicNumber24351753 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24351753, not.getChildrenCount(), () -> "Add assertion message");
        assertThat(not.getChild(0), instanceOf(TextNode.class), () -> "Add assertion message");
        assertEquals(" 1=1", ((TextNode)not.getChild(0)).getText(), () -> "Add assertion message");
    }

    @Test
    public void translateBitwiseNot() {
        Node not = translate("~123");
        assertThat(not, instanceOf(BitwiseNotNode.class), () -> "Add assertion message");
        Integer magicNumber56476907 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber56476907, not.getChildrenCount(), () -> "Add assertion message");
        assertThat(not.getChild(0), instanceOf(ValueNode.class), () -> "Add assertion message");
        assertEquals(123, ((ValueNode)not.getChild(0)).getValue(), () -> "Add assertion message");
    }

    @Test
    public void translateEqual() {
        {
            Node eq = translate("db:a = 123");
            assertThat(eq, instanceOf(EqualNode.class), () -> "Add assertion message");
            assertEquals(2, eq.getChildrenCount(), () -> "Add assertion message");
            assertThat(eq.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(eq.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) eq.getChild(0)).getColumn(), () -> "Add assertion message");
            assertEquals(123, ((ValueNode) eq.getChild(1)).getValue(), () -> "Add assertion message");
        }

        {
            Node neq = translate("db:b != 321");
            assertThat(neq, instanceOf(NotEqualNode.class), () -> "Add assertion message");
            assertEquals(2, neq.getChildrenCount(), () -> "Add assertion message");
            assertThat(neq.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(neq.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("b", ((ColumnNode) neq.getChild(0)).getColumn(), () -> "Add assertion message");
            assertEquals(321, ((ValueNode) neq.getChild(1)).getValue(), () -> "Add assertion message");
        }
    }

    @Test
    public void translateLike() {
        translator = new QualifierTranslator(context);
        {
            // no support for escape char in exp parser
            Node like = translator.translate(ExpressionFactory.likeExp("a", "abc", '~'));
            assertThat(like, instanceOf(LikeNode.class), () -> "Add assertion message");
            assertEquals('~', ((LikeNode) like).getEscape(), () -> "Add assertion message");
            assertFalse(((LikeNode) like).isIgnoreCase(), () -> "Add assertion message");
            assertFalse(((LikeNode) like).isNot(), () -> "Add assertion message");
            assertEquals(2, like.getChildrenCount(), () -> "Add assertion message");
            assertThat(like.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(like.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) like.getChild(0)).getColumn(), () -> "Add assertion message");
            assertEquals("abc", ((ValueNode) like.getChild(1)).getValue(), () -> "Add assertion message");
        }

        {
            Node notLike = translate("db:a not like 'abc'");
            assertThat(notLike, instanceOf(LikeNode.class), () -> "Add assertion message");
            Integer magicNumber22070737 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber22070737((LikeNode) notLike).getEscape(), () -> "Add assertion message");
            assertFalse(((LikeNode) notLike).isIgnoreCase(), () -> "Add assertion message");
            assertTrue(((LikeNode) notLike).isNot(), () -> "Add assertion message");
            assertEquals(2, notLike.getChildrenCount(), () -> "Add assertion message");
            assertThat(notLike.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(notLike.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) notLike.getChild(0)).getColumn(), () -> "Add assertion message");
            assertEquals("abc", ((ValueNode) notLike.getChild(1)).getValue(), () -> "Add assertion message");
        }

        {
            Node likeIgnoreCase = translate("db:a likeIgnoreCase 'abc'");
            assertThat(likeIgnoreCase, instanceOf(LikeNode.class), () -> "Add assertion message");
            Integer magicNumber91275478 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber91275478((LikeNode) likeIgnoreCase).getEscape(), () -> "Add assertion message");
            assertTrue(((LikeNode) likeIgnoreCase).isIgnoreCase(), () -> "Add assertion message");
            assertFalse(((LikeNode) likeIgnoreCase).isNot(), () -> "Add assertion message");
            assertEquals(2, likeIgnoreCase.getChildrenCount(), () -> "Add assertion message");
            assertThat(likeIgnoreCase.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(likeIgnoreCase.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) likeIgnoreCase.getChild(0)).getColumn(), () -> "Add assertion message");
            assertEquals("abc", ((ValueNode) likeIgnoreCase.getChild(1)).getValue(), () -> "Add assertion message");
        }

        {
            Node notLikeIgnoreCase = translate("db:a not likeIgnoreCase 'abc'");
            assertThat(notLikeIgnoreCase, instanceOf(LikeNode.class), () -> "Add assertion message");
            Integer magicNumber1378561 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber1378561((LikeNode) notLikeIgnoreCase).getEscape(), () -> "Add assertion message");
            assertTrue(((LikeNode) notLikeIgnoreCase).isIgnoreCase(), () -> "Add assertion message");
            assertTrue(((LikeNode) notLikeIgnoreCase).isNot(), () -> "Add assertion message");
            assertEquals(2, notLikeIgnoreCase.getChildrenCount(), () -> "Add assertion message");
            assertThat(notLikeIgnoreCase.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertThat(notLikeIgnoreCase.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) notLikeIgnoreCase.getChild(0)).getColumn(), () -> "Add assertion message");
            assertEquals("abc", ((ValueNode) notLikeIgnoreCase.getChild(1)).getValue(), () -> "Add assertion message");
        }
    }

    @Test
    public void translateFunctionCall() {
        {
            Node function = translate("trim(a)");
            assertThat(function, instanceOf(FunctionNode.class), () -> "Add assertion message");
            assertEquals("TRIM", ((FunctionNode) function).getFunctionName(), () -> "Add assertion message");
            assertNull(((FunctionNode) function).getAlias(), () -> "Add assertion message");
            assertEquals(1, function.getChildrenCount(), () -> "Add assertion message");
            assertThat(function.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode) function.getChild(0)).getColumn(), () -> "Add assertion message");
        }

        {
            Node function = translate("year(a)");
            // Line removed due to Duplicate Assert: assertThat(function, instanceOf(FunctionNode.class), () -> "Add assertion message");
            assertEquals("YEAR", ((FunctionNode) function).getFunctionName(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertNull(((FunctionNode) function).getAlias(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(1, function.getChildrenCount(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertThat(function.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals("a", ((ColumnNode) function.getChild(0)).getColumn(), () -> "Add assertion message");
        }
    }

    @Test
    public void translateMathExp() {
        {
            Node op = translate("1 + 2");
            assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("+", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 - 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("-", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 / 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("/", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 * 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("*", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("-2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals("-", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            assertEquals(1, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 & 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("&", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 | 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("|", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 ^ 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("^", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 << 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("<<", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("1 >> 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals(">>", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

    }

    @Test
    public void translateComparison() {
        {
            Node op = translate("a < 2");
            assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("<", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("a > 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals(">", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("a <= 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("<=", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("a >= 2");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals(">=", ((OpExpressionNode)op).getOp(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(2, op.getChildrenCount(), () -> "Add assertion message");
        }
    }

    @Test
    public void translateConst() {
        translator = new QualifierTranslator(context);
        {
            Node op = translate("true");
            assertThat(op, instanceOf(TextNode.class), () -> "Add assertion message");
            assertEquals(" 1=1", ((TextNode)op).getText(), () -> "Add assertion message");
            assertEquals(0, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translate("false");
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(TextNode.class), () -> "Add assertion message");
            assertEquals(" 1=0", ((TextNode)op).getText(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(0, op.getChildrenCount(), () -> "Add assertion message");
        }

        {
            Node op = translator.translate(new ASTAsterisk());
            // Line removed due to Duplicate Assert: assertThat(op, instanceOf(TextNode.class), () -> "Add assertion message");
            assertEquals(" *", ((TextNode)op).getText(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(0, op.getChildrenCount(), () -> "Add assertion message");
        }
    }

    @Test
    public void translateExists() {
        translator = new QualifierTranslator(context);
        Node exists = translator.translate(ExpressionFactory.exists(ObjectSelect.dbQuery("mock")));
        assertThat(exists, instanceOf(FunctionNode.class), () -> "Add assertion message");
        assertEquals("EXISTS", ((FunctionNode) exists).getFunctionName(), () -> "Add assertion message");
        assertEquals(1, exists.getChildrenCount(), () -> "Add assertion message");
        assertThat(exists.getChild(0), instanceOf(SelectNode.class), () -> "Add assertion message");
    }

    @Test
    public void translateFullObject() {
        translator = new QualifierTranslator(context);
        Node fullObj = translator.translate(ExpressionFactory.fullObjectExp());
        assertThat(fullObj, instanceOf(ColumnNode.class), () -> "Add assertion message");
        ColumnNode columnNode = (ColumnNode)fullObj;
        assertEquals("b", columnNode.getColumn(), () -> "Add assertion message");
    }

    @Test(expected = CayenneRuntimeException.class)
    public void translateEnclosingObject() {
        translator = new QualifierTranslator(context);
        // can't translate enclosing exp not in nested query
        translator.translate(ExpressionFactory.enclosingObjectExp(ExpressionFactory.dbPathExp("a")));
    }

    @Test
    public void translateAnd() {
        Node and = translate("true and false");
        assertNotNull(and, () -> "Add assertion message");
        assertThat(and, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
        assertEquals("AND", ((OpExpressionNode)and).getOp(), () -> "Add assertion message");
        assertEquals(2, and.getChildrenCount(), () -> "Add assertion message");
        assertThat(and.getChild(0), instanceOf(TextNode.class), () -> "Add assertion message");
        assertEquals(" 1=1", ((TextNode)and.getChild(0)).getText(), () -> "Add assertion message");
        assertThat(and.getChild(1), instanceOf(TextNode.class), () -> "Add assertion message");
        assertEquals(" 1=0", ((TextNode)and.getChild(1)).getText(), () -> "Add assertion message");
    }

    @Test
    public void translateComplexAnd() {
        Node and = translate("a < 2 and b in (5,6) and b = 7");
        assertNotNull(and, () -> "Add assertion message");

        assertThat(and, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
        assertEquals("AND", ((OpExpressionNode)and).getOp(), () -> "Add assertion message");
        assertEquals(3, and.getChildrenCount(), () -> "Add assertion message");

        SQLGenerationVisitor visitor = new SQLGenerationVisitor(new StringBuilderAppendable());
        and.visit(visitor);
        assertEquals(" ( t0.a < 2 ) AND t0.b IN ( 5, 6) AND ( t0.b = 7 )", visitor.getSQLString(), () -> "Add assertion message");
    }

    @Test
    public void translateOr() {
        Node or = translate("true or false");
        assertNotNull(or, () -> "Add assertion message");
        assertThat(or, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
        assertEquals("OR", ((OpExpressionNode)or).getOp(), () -> "Add assertion message");
        assertEquals(2, or.getChildrenCount(), () -> "Add assertion message");
        assertThat(or.getChild(0), instanceOf(TextNode.class), () -> "Add assertion message");
        assertThat(or.getChild(1), instanceOf(TextNode.class), () -> "Add assertion message");
    }

    @Test
    public void translateNullComparison() {
        Node or = translate("a > null");
        assertNotNull(or, () -> "Add assertion message");
        assertThat(or, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
        assertEquals(">", ((OpExpressionNode)or).getOp(), () -> "Add assertion message");
        assertEquals(2, or.getChildrenCount(), () -> "Add assertion message");
        assertThat(or.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
        assertThat(or.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
    }

    @Test
    public void translateComplexExp() {
        Node result = translate("(a >= 1 + 2 / 3 << 4) and (db:b != true)");

        {
            assertNotNull(result, () -> "Add assertion message");
            assertThat(result, instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertEquals("AND", ((OpExpressionNode) result).getOp(), () -> "Add assertion message");
            assertEquals(2, result.getChildrenCount(), () -> "Add assertion message");
            assertThat(result.getChild(0), instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            assertThat(result.getChild(1), instanceOf(NotEqualNode.class), () -> "Add assertion message");
        }

        {
            OpExpressionNode left = (OpExpressionNode) result.getChild(0);
            assertEquals(">=", left.getOp(), () -> "Add assertion message");
            assertEquals(2, left.getChildrenCount(), () -> "Add assertion message");
            assertThat(left.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertEquals("a", ((ColumnNode)left.getChild(0)).getColumn(), () -> "Add assertion message");
            assertThat(left.getChild(1), instanceOf(OpExpressionNode.class), () -> "Add assertion message");
            {
                OpExpressionNode shift = (OpExpressionNode)left.getChild(1);
                assertEquals("<<", shift.getOp(), () -> "Add assertion message");
                assertThat(shift.getChild(0), instanceOf(OpExpressionNode.class), () -> "Add assertion message");
                {
                    OpExpressionNode plus = (OpExpressionNode)shift.getChild(0);
                    assertEquals("+", plus.getOp(), () -> "Add assertion message");
                    assertEquals(2, plus.getChildrenCount(), () -> "Add assertion message");
                    assertThat(plus.getChild(0), instanceOf(ValueNode.class), () -> "Add assertion message");
                    Integer magicNumber17927271 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                    assertEquals(magicNumber17927271((ValueNode)plus.getChild(0)).getValue(), () -> "Add assertion message");
                    assertThat(plus.getChild(1), instanceOf(OpExpressionNode.class), () -> "Add assertion message");
                    {
                        OpExpressionNode div = (OpExpressionNode)plus.getChild(1);
                        assertEquals("/", div.getOp(), () -> "Add assertion message");
                        Integer magicNumber19304978 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                        assertEquals(magicNumber19304978, div.getChildrenCount(), () -> "Add assertion message");
                        assertThat(div.getChild(0), instanceOf(ValueNode.class), () -> "Add assertion message");
                        assertEquals(2, ((ValueNode)div.getChild(0)).getValue(), () -> "Add assertion message");
                        // Line removed due to Duplicate Assert: assertThat(div.getChild(0), instanceOf(ValueNode.class), () -> "Add assertion message");
                        assertEquals(3, ((ValueNode)div.getChild(1)).getValue(), () -> "Add assertion message");
                    }
                }
                assertThat(shift.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
                assertEquals(4, ((ValueNode)shift.getChild(1)).getValue(), () -> "Add assertion message");
            }
        }

        {
            NotEqualNode right = (NotEqualNode) result.getChild(1);
            assertEquals(2, right.getChildrenCount(), () -> "Add assertion message");
            assertThat(right.getChild(0), instanceOf(ColumnNode.class), () -> "Add assertion message");
            assertEquals("b", ((ColumnNode)right.getChild(0)).getColumn(), () -> "Add assertion message");
            assertThat(right.getChild(1), instanceOf(ValueNode.class), () -> "Add assertion message");
            assertEquals(Boolean.TRUE, ((ValueNode)right.getChild(1)).getValue(), () -> "Add assertion message");
        }
    }

    private Node translate(String s) {
        translator = new QualifierTranslator(context);
        return translator.translate(ExpressionFactory.exp(s));
    }

}