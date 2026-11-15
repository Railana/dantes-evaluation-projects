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

import org.junit.Before;
import org.junit.Test;

import static org.apache.cayenne.exp.ExpressionFactory.exp;
import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class StringPropertyTest {

    private StringProperty<String> property;
    private StringProperty<String> other;

    @Before
    public void createProperty() {
        property = new StringProperty<>("path", null, String.class);
        // Removed line due to General Fixture smell: other = new StringProperty<>("other", null, String.class);
    }

    @Test
    public void like() {
        assertEquals(exp("path like 'abc'"), property.like("abc"), () -> "Add assertion message");
    }

    @Test
    public void likeProp() {
        other = new StringProperty<>("other", null, String.class);
        assertEquals(exp("path like other"), property.like(other), () -> "Add assertion message");
    }

    @Test
    public void likeWithEscape() {
        assertEquals(exp("path like 'abc'"), property.like("abc", '|'), () -> "Add assertion message");
    }

    @Test
    public void likeIgnoreCase() {
        assertEquals(exp("path likeIgnoreCase 'abc'"), property.likeIgnoreCase("abc"), () -> "Add assertion message");
    }

    @Test
    public void likeIgnoreCaseProp() {
        other = new StringProperty<>("other", null, String.class);
        assertEquals(exp("path likeIgnoreCase other"), property.likeIgnoreCase(other), () -> "Add assertion message");
    }

    @Test
    public void nlike() {
        assertEquals(exp("path not like 'abc'"), property.nlike("abc"), () -> "Add assertion message");
    }

    @Test
    public void nlikeProp() {
        other = new StringProperty<>("other", null, String.class);
        assertEquals(exp("path not like other"), property.nlike(other), () -> "Add assertion message");
    }

    @Test
    public void nlikeIgnoreCase() {
        assertEquals(exp("path not likeIgnoreCase 'abc'"), property.nlikeIgnoreCase("abc"), () -> "Add assertion message");
    }

    @Test
    public void nlikeIgnoreCaseProp() {
        other = new StringProperty<>("other", null, String.class);
        assertEquals(exp("path not likeIgnoreCase other"), property.nlikeIgnoreCase(other), () -> "Add assertion message");
    }

    @Test
    public void contains() {
        assertEquals(exp("path like '%abc%'"), property.contains("abc"), () -> "Add assertion message");
    }

    @Test
    public void startsWith() {
        assertEquals(exp("path like 'abc%'"), property.startsWith("abc"), () -> "Add assertion message");
    }

    @Test
    public void endsWith() {
        assertEquals(exp("path like '%abc'"), property.endsWith("abc"), () -> "Add assertion message");
    }

    @Test
    public void containsIgnoreCase() {
        assertEquals(exp("path likeIgnoreCase '%abc%'"), property.containsIgnoreCase("abc"), () -> "Add assertion message");
    }

    @Test
    public void startsWithIgnoreCase() {
        assertEquals(exp("path likeIgnoreCase 'abc%'"), property.startsWithIgnoreCase("abc"), () -> "Add assertion message");
    }

    @Test
    public void endsWithIgnoreCase() {
        assertEquals(exp("path likeIgnoreCase '%abc'"), property.endsWithIgnoreCase("abc"), () -> "Add assertion message");
    }

    @Test
    public void length() {
        assertEquals(exp("length(path)"), property.length().getExpression(), () -> "Add assertion message");
    }

    @Test
    public void locate() {
        assertEquals(exp("locate('abc', path)"), property.locate("abc").getExpression(), () -> "Add assertion message");
    }

    @Test
    public void locateProp() {
        other = new StringProperty<>("other", null, String.class);
        assertEquals(exp("locate(other, path)"), property.locate(other).getExpression(), () -> "Add assertion message");
    }

    @Test
    public void trim() {
        assertEquals(exp("trim(path)"), property.trim().getExpression(), () -> "Add assertion message");
    }

    @Test
    public void upper() {
        assertEquals(exp("upper(path)"), property.upper().getExpression(), () -> "Add assertion message");
    }

    @Test
    public void lower() {
        assertEquals(exp("lower(path)"), property.lower().getExpression(), () -> "Add assertion message");
    }

    @Test
    public void concat() {
        other = new StringProperty<>("other", null, String.class);
        assertEquals(exp("concat(path, 'abc', ' ', 'def', other)"),
                property.concat("abc", ' ', exp("'def'"), other).getExpression(), () -> "Add assertion message");
    }

    @Test
    public void substring() {
        assertEquals(exp("substring(path, 10, 30)"), property.substring(10, 30).getExpression(), () -> "Add assertion message");
    }

    @Test
    public void substringProp() {
        NumericProperty<Integer> offset = PropertyFactory.createNumeric("offset", Integer.class);
        // length is a function name, so use len here
        NumericProperty<Integer> length = PropertyFactory.createNumeric("len", Integer.class);
        assertEquals(exp("substring(path, offset, len)"), property.substring(offset, length).getExpression(), () -> "Add assertion message");
    }

    @Test
    public void compare() {
        other = new StringProperty<>("other", null, String.class);
        assertEquals(exp("path > 'abc'"),  property.gt("abc"), () -> "Add assertion message");
        assertEquals(exp("path > other"),  property.gt(other), () -> "Add assertion message");
        assertEquals(exp("path >= 'abc'"), property.gte("abc"), () -> "Add assertion message");
        assertEquals(exp("path >= other"), property.gte(other), () -> "Add assertion message");
        assertEquals(exp("path < 'abc'"),  property.lt("abc"), () -> "Add assertion message");
        assertEquals(exp("path < other"),  property.lt(other), () -> "Add assertion message");
        assertEquals(exp("path <= 'abc'"), property.lte("abc"), () -> "Add assertion message");
        assertEquals(exp("path <= other"), property.lte(other), () -> "Add assertion message");
    }

    @Test
    public void alias() {
        assertEquals("path", property.getName(), () -> "Add assertion message");
        assertNull(property.getAlias(), () -> "Add assertion message");

        property = property.alias("alias");

        assertEquals("alias", property.getName(), () -> "Add assertion message");
        assertEquals("alias", property.getAlias(), () -> "Add assertion message");
    }
}