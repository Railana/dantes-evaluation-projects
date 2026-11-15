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

package org.apache.cayenne.exp.property;

import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.parser.ASTLike;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.reflect.TstJavaBean;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class BasePropertyTest {

    private BaseProperty<Integer> property;

    @Before
    public void createProperty() {
        // Removed line due to General Fixture smell: property = new BaseProperty<>("path", null, Integer.class);
    }


    @Test
    public void testPathExpConstructor() {
        property = new BaseProperty<>("path", null, Integer.class);
        assertEquals(new ASTObjPath("path"), property.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void testCustomExpConstructor() {
        property = new BaseProperty<>("path", null, Integer.class);
        property = new BaseProperty<>("path", new ASTLike(), Integer.class);
        assertEquals(new ASTLike(), property.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void testEq() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.eq(1);
        assertEquals(ExpressionFactory.exp("path = 1"), exp, () -> "Add assertion message");
    }

    @Test
    public void testNe() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.ne(1);
        assertEquals(ExpressionFactory.exp("path != 1"), exp, () -> "Add assertion message");
    }

    @Test
    public void testIn() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.in(1, 2, 3);
        assertEquals(ExpressionFactory.exp("path in (1, 2, 3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void testInCollection() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.in(Arrays.asList(1,2,3));
        assertEquals(ExpressionFactory.exp("path in (1, 2, 3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void testNin() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.nin(1, 2, 3);
        assertEquals(ExpressionFactory.exp("path not in (1, 2, 3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void testNinCollection() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.nin(Arrays.asList(1,2,3));
        assertEquals(ExpressionFactory.exp("path not in (1, 2, 3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void testIsNull() {
        property = new BaseProperty<>("path", null, Integer.class);
        BaseProperty<Integer> property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.isNull();
        assertEquals(ExpressionFactory.exp("path = null"), exp, () -> "Add assertion message");
    }

    @Test
    public void testNotIsNull() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.isNotNull();
        assertEquals(ExpressionFactory.exp("path != null"), exp, () -> "Add assertion message");
    }

    @Test
    public void testIsTrue() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.isTrue();
        assertEquals(ExpressionFactory.exp("path = true"), exp, () -> "Add assertion message");
    }

    @Test
    public void testIsFalse() {
        property = new BaseProperty<>("path", null, Integer.class);
        Expression exp = property.isFalse();
        assertEquals(ExpressionFactory.exp("path = false"), exp, () -> "Add assertion message");
    }

    @Test
    public void testAlias() {
        property = new BaseProperty<>("path", null, Integer.class);
        assertEquals("path", property.getName(), () -> "Add assertion message");
        assertNull(property.getAlias(), () -> "Add assertion message");

        property = property.alias("test");

        assertEquals("test", property.getName(), () -> "Add assertion message");
        assertEquals("test", property.getAlias(), () -> "Add assertion message");
        assertEquals(new ASTObjPath("path"), property.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void testGetFrom() {
        TstJavaBean bean = new TstJavaBean();
        bean.setIntField(7);
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        assertEquals(Integer.valueOf(7), INT_FIELD.getFrom(bean), () -> "Add assertion message");
    }

    @Test
    public void testGetFromNestedProperty() {
        TstJavaBean bean = new TstJavaBean();
        TstJavaBean nestedBean = new TstJavaBean();
        nestedBean.setIntField(7);
        bean.setObjectField(nestedBean);
        BaseProperty<Integer> OBJECT_FIELD_INT_FIELD = new BaseProperty<>("objectField.intField", null, Integer.class);
        assertEquals(Integer.valueOf(7), OBJECT_FIELD_INT_FIELD.getFrom(bean), () -> "Add assertion message");
    }

    @Test
    public void testGetFromNestedNull() {
        TstJavaBean bean = new TstJavaBean();
        bean.setObjectField(null);
        BaseProperty<Integer> OBJECT_FIELD_INT_FIELD = new BaseProperty<>("objectField.intField", null, Integer.class);
        assertNull(OBJECT_FIELD_INT_FIELD.getFrom(bean), () -> "Add assertion message");
    }

    @Test
    public void testGetFromAll() {
        TstJavaBean bean = new TstJavaBean();
        bean.setIntField(7);

        TstJavaBean bean2 = new TstJavaBean();
        bean2.setIntField(8);

        List<TstJavaBean> beans = Arrays.asList(bean, bean2);

        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        assertEquals(Arrays.asList(7, 8), INT_FIELD.getFromAll(beans), () -> "Add assertion message");
    }

    @Test
    public void testSetIn() {
        TstJavaBean bean = new TstJavaBean();
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        INT_FIELD.setIn(bean, 7);
        Integer magicNumber18016387 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber18016387, bean.getIntField(), () -> "Add assertion message");
    }

    @Test
    public void testSetInNestedProperty() {
        TstJavaBean bean = new TstJavaBean();
        bean.setObjectField(new TstJavaBean());

        BaseProperty<Integer> OBJECT_FIELD_INT_FIELD = new BaseProperty<>("objectField.intField", null, Integer.class);

        Integer magicNumber36890913 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        OBJECT_FIELD_INT_FIELD.setIn(bean,magicNumber36890913
        assertEquals(7, ((TstJavaBean) bean.getObjectField()).getIntField(), () -> "Add assertion message");
    }

    @Test
    public void testSetInNestedNull() {
        TstJavaBean bean = new TstJavaBean();
        bean.setObjectField(null);
        BaseProperty<Integer> OBJECT_FIELD_INT_FIELD = new BaseProperty<>("objectField.intField", null, Integer.class);
        OBJECT_FIELD_INT_FIELD.setIn(bean, 7);
    }

    @Test
    public void testSetInAll() {
        TstJavaBean bean = new TstJavaBean();
        TstJavaBean bean2 = new TstJavaBean();
        List<TstJavaBean> beans = Arrays.asList(bean, bean2);

        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        Integer magicNumber80482025 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        INT_FIELD.setInAll(beans,magicNumber80482025
        assertEquals(7, bean.getIntField(), () -> "Add assertion message");
        assertEquals(7, bean2.getIntField(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsWithName() {
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Integer> INT_FIELD2 = new BaseProperty<>("intField", null, Integer.class);

        assertNotSame(INT_FIELD, INT_FIELD2, () -> "Add assertion message");
        assertEquals(INT_FIELD, INT_FIELD2, () -> "Add assertion message");
    }

    @Test
    public void testHashCodeWithName() {
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Integer> INT_FIELD2 = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Long> LONG_FIELD = new BaseProperty<>("longField", null, Long.class);

        assertEquals(INT_FIELD.hashCode(), INT_FIELD2.hashCode(), () -> "Add assertion message");
        assertNotSame(INT_FIELD.hashCode(), LONG_FIELD.hashCode(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsWithNameAndType() {
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Integer> INT_FIELD2 = new BaseProperty<>("intField", null, Integer.class);

        assertNotSame(INT_FIELD, INT_FIELD2, () -> "Add assertion message");
        assertEquals(INT_FIELD, INT_FIELD2, () -> "Add assertion message");
    }

    @Test
    public void testHashCodeWithNameAndType() {
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Integer> INT_FIELD2 = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Long> LONG_FIELD = new BaseProperty<>("longField", null, Long.class);

        assertEquals(INT_FIELD.hashCode(), INT_FIELD2.hashCode(), () -> "Add assertion message");
        assertNotEquals(INT_FIELD.hashCode(), LONG_FIELD.hashCode());
    }

    @Test
    public void testEqualsWithExpAndType() {
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>(null, ExpressionFactory.exp("1"), Integer.class);
        BaseProperty<Integer> INT_FIELD2 = new BaseProperty<>(null, ExpressionFactory.exp("1"), Integer.class);

        assertNotSame(INT_FIELD, INT_FIELD2, () -> "Add assertion message");
        assertEquals(INT_FIELD, INT_FIELD2, () -> "Add assertion message");
    }

    @Test
    public void testHashCodeWithExpAndType() {
        BaseProperty<Integer> INT_FIELD = new BaseProperty<>(null, ExpressionFactory.exp("1"), Integer.class);
        BaseProperty<Integer> INT_FIELD2 = new BaseProperty<>(null, ExpressionFactory.exp("1"), Integer.class);
        BaseProperty<Integer> INT_FIELD3 = new BaseProperty<>(null, ExpressionFactory.exp("2"), Integer.class);

        assertEquals(INT_FIELD.hashCode(), INT_FIELD2.hashCode(), () -> "Add assertion message");
        assertNotEquals(INT_FIELD.hashCode(), INT_FIELD3.hashCode());
    }

    @Test
    public void testFunctionProperty() {
        property = new BaseProperty<>("path", null, Integer.class);
        BaseProperty<Integer> property = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Integer> arg = new BaseProperty<>("intField2", null, Integer.class);

        BaseProperty<Integer> operator = property.function("%", Integer.class, arg);
        assertEquals(ExpressionFactory.exp("fn('%', intField, intField2)"), operator.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void testFunctionScalar() {
        property = new BaseProperty<>("path", null, Integer.class);
        BaseProperty<Integer> property = new BaseProperty<>("intField", null, Integer.class);

        BaseProperty<Integer> operator = property.function("%", Integer.class, 10);
        assertEquals(ExpressionFactory.exp("fn('%', intField, 10)"), operator.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void testOperatorProperty() {
        property = new BaseProperty<>("path", null, Integer.class);
        BaseProperty<Integer> property = new BaseProperty<>("intField", null, Integer.class);
        BaseProperty<Integer> arg = new BaseProperty<>("intField2", null, Integer.class);

        BaseProperty<Integer> operator = property.operator("%", Integer.class, arg);
        assertEquals(ExpressionFactory.exp("op('%', intField, intField2)"), operator.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void testOperatorScalar() {
        property = new BaseProperty<>("path", null, Integer.class);
        BaseProperty<Integer> property = new BaseProperty<>("intField", null, Integer.class);

        BaseProperty<Integer> operator = property.operator("%", Integer.class, 10);
        assertEquals(ExpressionFactory.exp("op('%', intField, 10)"), operator.getExpression(), () -> "Add assertion message");
    }

}