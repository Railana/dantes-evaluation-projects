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
package org.apache.cayenne.exp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.exp.parser.ASTAbs;
import org.apache.cayenne.exp.parser.ASTAvg;
import org.apache.cayenne.exp.parser.ASTConcat;
import org.apache.cayenne.exp.parser.ASTCount;
import org.apache.cayenne.exp.parser.ASTDistinct;
import org.apache.cayenne.exp.parser.ASTLength;
import org.apache.cayenne.exp.parser.ASTLocate;
import org.apache.cayenne.exp.parser.ASTLower;
import org.apache.cayenne.exp.parser.ASTMax;
import org.apache.cayenne.exp.parser.ASTMin;
import org.apache.cayenne.exp.parser.ASTMod;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.ASTSqrt;
import org.apache.cayenne.exp.parser.ASTSubstring;
import org.apache.cayenne.exp.parser.ASTSum;
import org.apache.cayenne.exp.parser.ASTTrim;
import org.apache.cayenne.exp.parser.ASTUpper;
import org.apache.cayenne.exp.parser.PatternMatchNode;
import org.apache.cayenne.exp.property.NumericProperty;
import org.apache.cayenne.reflect.TstJavaBean;
import org.junit.Test;

@Deprecated
public class PropertyTest {

    @Test
    public void testPath() {
        Property<String> p = Property.create("x.y", String.class);
        Expression pp = p.path();
        assertEquals(ExpressionFactory.exp("x.y"), pp, () -> "Add assertion message");
    }

    @Test
    public void testIn() {
        Property<String> p = Property.create("x.y", String.class);

        Expression e1 = p.in("a");
        assertEquals("x.y in (\"a\")", e1.toString(), () -> "Add assertion message");

        Expression e2 = p.in("a", "b");
        assertEquals("x.y in (\"a\", \"b\")", e2.toString(), () -> "Add assertion message");

        Expression e3 = p.in(Arrays.asList("a", "b"));
        assertEquals("x.y in (\"a\", \"b\")", e3.toString(), () -> "Add assertion message");
    }

    @Test
    public void testGetFrom() {
        TstJavaBean bean = new TstJavaBean();
        bean.setIntField(7);
        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        assertEquals(Integer.valueOf(7), INT_FIELD.getFrom(bean), () -> "Add assertion message");
    }

    @Test
    public void testGetFromNestedProperty() {
        TstJavaBean bean = new TstJavaBean();
        TstJavaBean nestedBean = new TstJavaBean();
        nestedBean.setIntField(7);
        bean.setObjectField(nestedBean);
        Property<Integer> OBJECT_FIELD_INT_FIELD = Property.create("objectField.intField", Integer.class);
        assertEquals(Integer.valueOf(7), OBJECT_FIELD_INT_FIELD.getFrom(bean), () -> "Add assertion message");
    }

    @Test
    public void testGetFromNestedNull() {
        TstJavaBean bean = new TstJavaBean();
        bean.setObjectField(null);
        Property<Integer> OBJECT_FIELD_INT_FIELD = Property.create("objectField.intField", Integer.class);
        assertNull(OBJECT_FIELD_INT_FIELD.getFrom(bean), () -> "Add assertion message");
    }

    @Test
    public void testGetFromAll() {
        TstJavaBean bean = new TstJavaBean();
        bean.setIntField(7);

        TstJavaBean bean2 = new TstJavaBean();
        bean2.setIntField(8);

        List<TstJavaBean> beans = Arrays.asList(bean, bean2);

        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        assertEquals(Arrays.asList(7, 8), INT_FIELD.getFromAll(beans), () -> "Add assertion message");
    }

    @Test
    public void testSetIn() {
        TstJavaBean bean = new TstJavaBean();
        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        INT_FIELD.setIn(bean, 7);
        Integer magicNumber37894263 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber37894263, bean.getIntField(), () -> "Add assertion message");
    }

    @Test
    public void testSetInNestedProperty() {
        TstJavaBean bean = new TstJavaBean();
        bean.setObjectField(new TstJavaBean());

        Property<Integer> OBJECT_FIELD_INT_FIELD = Property.create("objectField.intField", Integer.class);

        Integer magicNumber47769580 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        OBJECT_FIELD_INT_FIELD.setIn(bean,magicNumber47769580
        assertEquals(7, ((TstJavaBean) bean.getObjectField()).getIntField(), () -> "Add assertion message");
    }

    @Test
    public void testSetInNestedNull() {
        TstJavaBean bean = new TstJavaBean();
        bean.setObjectField(null);
        Property<Integer> OBJECT_FIELD_INT_FIELD = Property.create("objectField.intField", Integer.class);
        OBJECT_FIELD_INT_FIELD.setIn(bean, 7);
    }

    @Test
    public void testSetInAll() {
        TstJavaBean bean = new TstJavaBean();
        TstJavaBean bean2 = new TstJavaBean();
        List<TstJavaBean> beans = Arrays.asList(bean, bean2);

        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        Integer magicNumber38939378 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        INT_FIELD.setInAll(beans,magicNumber38939378
        assertEquals(7, bean.getIntField(), () -> "Add assertion message");
        assertEquals(7, bean2.getIntField(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsWithName() {
        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        Property<Integer> INT_FIELD2 = Property.create("intField", Integer.class);

        assertTrue(INT_FIELD != INT_FIELD2, () -> "Add assertion message");
        assertTrue(INT_FIELD.equals(INT_FIELD2), () -> "Add assertion message");
    }

    @Test
    public void testHashCodeWithName() {
        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        Property<Integer> INT_FIELD2 = Property.create("intField", Integer.class);
        Property<Long> LONG_FIELD = Property.create("longField", Long.class);

        assertTrue(INT_FIELD.hashCode() == INT_FIELD2.hashCode(), () -> "Add assertion message");
        assertTrue(INT_FIELD.hashCode() != LONG_FIELD.hashCode(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsWithNameAndType() {
        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        Property<Integer> INT_FIELD2 = Property.create("intField", Integer.class);

        assertTrue(INT_FIELD != INT_FIELD2, () -> "Add assertion message");
        assertTrue(INT_FIELD.equals(INT_FIELD2), () -> "Add assertion message");
    }

    @Test
    public void testHashCodeWithNameAndType() {
        Property<Integer> INT_FIELD = Property.create("intField", Integer.class);
        Property<Integer> INT_FIELD2 = Property.create("intField", Integer.class);
        Property<Long> LONG_FIELD = Property.create("longField", Long.class);

        assertTrue(INT_FIELD.hashCode() == INT_FIELD2.hashCode(), () -> "Add assertion message");
        assertTrue(INT_FIELD.hashCode() != LONG_FIELD.hashCode(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsWithExpAndType() {
        Property<Integer> INT_FIELD = new Property<>(null, ExpressionFactory.exp("1"), Integer.class);
        Property<Integer> INT_FIELD2 = new Property<>(null, ExpressionFactory.exp("1"), Integer.class);

        assertTrue(INT_FIELD != INT_FIELD2, () -> "Add assertion message");
        assertTrue(INT_FIELD.equals(INT_FIELD2), () -> "Add assertion message");
    }

    @Test
    public void testHashCodeWithExpAndType() {
        Property<Integer> INT_FIELD = new Property<>(null, ExpressionFactory.exp("1"), Integer.class);
        Property<Integer> INT_FIELD2 = new Property<>(null, ExpressionFactory.exp("1"), Integer.class);
        Property<Integer> INT_FIELD3 = new Property<>(null, ExpressionFactory.exp("2"), Integer.class);

        assertEquals(INT_FIELD.hashCode(), INT_FIELD2.hashCode(), () -> "Add assertion message");
        assertNotEquals(INT_FIELD.hashCode(), INT_FIELD3.hashCode());
    }

    @Test
    public void testOuter() {
        Property<String> inner = Property.create("xyz", String.class);
        assertEquals("xyz+", inner.outer().getName(), () -> "Add assertion message");

        Property<String> inner1 = Property.create("xyz.xxx", String.class);
        assertEquals("xyz.xxx+", inner1.outer().getName(), () -> "Add assertion message");

        Property<String> outer = Property.create("xyz+", String.class);
        assertEquals("xyz+", outer.outer().getName(), () -> "Add assertion message");
    }

    @Test
    public void testLike() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.like("abc");
        assertEquals("prop like \"abc\"", e.toString(), () -> "Add assertion message");
    }

    @Test
    public void testLikeIgnoreCase() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.likeIgnoreCase("abc");
        assertEquals("prop likeIgnoreCase \"abc\"", e.toString(), () -> "Add assertion message");
    }

    @Test
    public void testLike_NoEscape() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.like("ab%c");
        assertEquals("prop like \"ab%c\"", e.toString(), () -> "Add assertion message");
        assertEquals(0, ((PatternMatchNode) e).getEscapeChar(), () -> "Add assertion message");
    }

    @Test
    public void testContains() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.contains("abc");
        assertEquals("prop like \"%abc%\"", e.toString(), () -> "Add assertion message");
        assertEquals(0, ((PatternMatchNode) e).getEscapeChar(), () -> "Add assertion message");
    }

    @Test
    public void testStartsWith() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.startsWith("abc");
        assertEquals("prop like \"abc%\"", e.toString(), () -> "Add assertion message");
        assertEquals(0, ((PatternMatchNode) e).getEscapeChar(), () -> "Add assertion message");
    }

    @Test
    public void testEndsWith() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.endsWith("abc");
        assertEquals("prop like \"%abc\"", e.toString(), () -> "Add assertion message");
        assertEquals(0, ((PatternMatchNode) e).getEscapeChar(), () -> "Add assertion message");
    }

    @Test
    public void testContains_Escape1() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.contains("a%bc");
        assertEquals("prop like \"%a!%bc%\"", e.toString(), () -> "Add assertion message");
        assertEquals('!', ((PatternMatchNode) e).getEscapeChar(), () -> "Add assertion message");
    }

    @Test
    public void testContains_Escape2() {
        Property<String> p = Property.create("prop", String.class);
        Expression e = p.contains("a_!bc");
        assertEquals("prop like \"%a#_!bc%\"", e.toString(), () -> "Add assertion message");
        assertEquals('#', ((PatternMatchNode) e).getEscapeChar(), () -> "Add assertion message");
    }

    @Test
    public void testExpressionConstructor() {
        Property<Integer> p = Property.create("testPath", new ASTObjPath("test.path"), Integer.class);
        assertEquals("testPath", p.getName(), () -> "Add assertion message");
        Expression ex = p.getExpression();
        assertEquals("test.path", ex.toString(), () -> "Add assertion message");
    }

    @Test
    public void testCreationWithName() {
        Property<String> p1 = new Property<>("p1", String.class);
        assertEquals(String.class, p1.getType(), () -> "Add assertion message");
        assertEquals("p1", p1.getName(), () -> "Add assertion message");
        assertEquals(new ASTObjPath("p1"), p1.getExpression(), () -> "Add assertion message");

        Property<String> p2 = Property.create("p1", String.class);
        assertEquals(p1, p2, () -> "Add assertion message");
    }

    @Test
    public void testCreationWithExp() {
        Expression exp = FunctionExpressionFactory.currentTime();

        Property<String> p1 = new Property<>(null, exp, String.class);
        assertEquals(String.class, p1.getType(), () -> "Add assertion message");
        assertEquals(null, p1.getName(), () -> "Add assertion message");
        assertEquals(exp, p1.getExpression(), () -> "Add assertion message");

        Property<String> p2 = Property.create(exp, String.class);
        assertEquals(p1, p2, () -> "Add assertion message");
    }

    @Test
    public void testCreationWithNameAndExp() {
        Expression exp = FunctionExpressionFactory.currentTime();

        Property<String> p1 = new Property<>("p1", exp, String.class);
        assertEquals(String.class, p1.getType(), () -> "Add assertion message");
        assertEquals("p1", p1.getName(), () -> "Add assertion message");
        assertEquals(exp, p1.getExpression(), () -> "Add assertion message");

        Property<String> p2 = Property.create("p1", exp, String.class);
        assertEquals(p1, p2, () -> "Add assertion message");
    }

    @Test
    public void testAlias() {
        Expression exp = FunctionExpressionFactory.currentTime();

        Property<String> p1 = new Property<>("p1", exp, String.class);
        assertEquals(String.class, p1.getType(), () -> "Add assertion message");
        assertEquals("p1", p1.getName(), () -> "Add assertion message");
        assertEquals(exp, p1.getExpression(), () -> "Add assertion message");

        Property<String> p2 = p1.alias("p2");
        assertEquals(String.class, p2.getType(), () -> "Add assertion message");
        assertEquals("p2", p2.getName(), () -> "Add assertion message");
        assertEquals(exp, p2.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void testCount() {
        Property<String> p = Property.create("test", String.class);
        NumericProperty<Long> newProp = p.count();
        assertTrue(newProp.getExpression() instanceof ASTCount, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }
    
    @Test
    public void testCountDistinct() {
        Property<String> p = Property.create("test", String.class);
        NumericProperty<Long> newProp = p.countDistinct();
        assertTrue(newProp.getExpression() instanceof ASTCount, () -> "Add assertion message");
        assertTrue(newProp.getExpression().getOperand(0) instanceof ASTDistinct, () -> "Add assertion message");
        assertEquals(p.getExpression(), ((ASTDistinct)newProp.getExpression().getOperand(0)).getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testMin() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.min();
        assertTrue(newProp.getExpression() instanceof ASTMin, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testMax() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.max();
        assertTrue(newProp.getExpression() instanceof ASTMax, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testSum() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.sum();
        assertTrue(newProp.getExpression() instanceof ASTSum, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testAvg() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.avg();
        assertTrue(newProp.getExpression() instanceof ASTAvg, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testAbs() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.abs();
        assertTrue(newProp.getExpression() instanceof ASTAbs, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testMod() {
        Property<String> p = Property.create("test", String.class);
        Double magicNumber94611725 = 3.0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Property<String> newProp = p.mod(magicNumber94611725
        assertTrue(newProp.getExpression() instanceof ASTMod, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
        assertEquals(3.0, newProp.getExpression().getOperand(1), () -> "Add assertion message");
    }

    @Test
    public void testSqrt() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.sqrt();
        assertTrue(newProp.getExpression() instanceof ASTSqrt, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testLength() {
        Property<String> p = Property.create("test", String.class);
        Property<Integer> newProp = p.length();
        assertTrue(newProp.getExpression() instanceof ASTLength, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testLocateString() {
        Property<String> p = Property.create("test", String.class);
        Property<Integer> newProp = p.locate("test");
        assertTrue(newProp.getExpression() instanceof ASTLocate, () -> "Add assertion message");
        assertEquals("test", newProp.getExpression().getOperand(0), () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(1), () -> "Add assertion message");
    }

    @Test
    public void testLocateProperty() {
        Property<String> p = Property.create("test", String.class);
        Property<String> p2 = Property.create("test2", String.class);
        Property<Integer> newProp = p.locate(p2);
        assertTrue(newProp.getExpression() instanceof ASTLocate, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(1), () -> "Add assertion message");
        assertEquals(p2.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testSustring() {
        Property<String> p = Property.create("test", String.class);
        Integer magicNumber12387182 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Property<String> newProp = p.substring(1,magicNumber12387182
        assertTrue(newProp.getExpression() instanceof ASTSubstring, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
        assertEquals(1, newProp.getExpression().getOperand(1), () -> "Add assertion message");
        assertEquals(2, newProp.getExpression().getOperand(2), () -> "Add assertion message");
    }

    @Test
    public void testTrim() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.trim();
        assertTrue(newProp.getExpression() instanceof ASTTrim, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testLower() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.lower();
        assertTrue(newProp.getExpression() instanceof ASTLower, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testUpper() {
        Property<String> p = Property.create("test", String.class);
        Property<String> newProp = p.upper();
        assertTrue(newProp.getExpression() instanceof ASTUpper, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void testConcat() {
        Property<String> p = Property.create("test", String.class);
        Property<String> p2 = Property.create("concat", String.class);
        Integer magicNumber65069258 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Expression exp = new ASTScalar(magicNumber65069258

        Property<String> newProp = p.concat("string", exp, p2);
        assertTrue(newProp.getExpression() instanceof ASTConcat, () -> "Add assertion message");
        assertEquals(p.getExpression(), newProp.getExpression().getOperand(0), () -> "Add assertion message");
        assertEquals("string", newProp.getExpression().getOperand(1), () -> "Add assertion message");
        assertEquals(3, newProp.getExpression().getOperand(2), () -> "Add assertion message");
        assertEquals(p2.getExpression(), newProp.getExpression().getOperand(3), () -> "Add assertion message");
    }
}