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

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.testdo.testmap.Artist;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 4.2
 */
public class EntityPropertyTest {

    private EntityProperty<Artist> property;
    private EntityProperty<Artist> property1;

    @Before
    public void createProperty() {
        property = new EntityProperty<>("path", null, Artist.class);
        // Removed line due to General Fixture smell: property1 = new EntityProperty<>("path.artist", null, Artist.class);
    }

    @Test
    public void alias() {
        property1 = new EntityProperty<>("path.artist", null, Artist.class);
        assertEquals("path", property.getName(), () -> "Add assertion message");
        property = property.alias("alias");
        assertEquals("alias", property.getName(), () -> "Add assertion message");
        Integer magicNumber1387262 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber1387262, property.getExpression().getPathAliases().size(), () -> "Add assertion message");

        assertEquals("path.artist", property1.getName(), () -> "Add assertion message");
        property1 = property1.alias("a");
        assertEquals("path.a", property1.getName(), () -> "Add assertion message");
        assertEquals(1, property1.getExpression().getPathAliases().size(), () -> "Add assertion message");
        assertEquals("artist", property1.getExpression().getPathAliases().get("a"), () -> "Add assertion message");
    }

    @Test
    public void outer() {
        assertEquals("path", property.getName(), () -> "Add assertion message");
        assertEquals(ExpressionFactory.pathExp("path"), property.getExpression(), () -> "Add assertion message");

        property = property.outer();

        assertEquals("path+", property.getName(), () -> "Add assertion message");
        assertEquals(ExpressionFactory.pathExp("path+"), property.getExpression(), () -> "Add assertion message");

        property = property.outer();

        // Line removed due to Duplicate Assert: assertEquals("path+", property.getName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(ExpressionFactory.pathExp("path+"), property.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void dot() {
        BaseProperty<?> other = property.dot("other");
        assertEquals("path.other", other.getName(), () -> "Add assertion message");
        assertEquals(ExpressionFactory.pathExp("path.other"), other.getExpression(), () -> "Add assertion message");
    }

    @Test
    public void eqId() {
        Expression exp = property.eqId(1);
        assertEquals(ExpressionFactory.exp("path = 1"), exp, () -> "Add assertion message");
    }

    @Test
    public void inIdCollection() {
        Expression exp = property.inId(Arrays.asList(1, 2, 3));
        assertEquals(ExpressionFactory.exp("path in (1, 2, 3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void inIdVararg() {
        Expression exp = property.inId(1, 2, 3);
        assertEquals(ExpressionFactory.exp("path in (1, 2, 3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void neqId() {
        Expression exp = property.neqId(1);
        assertEquals(ExpressionFactory.exp("path <> 1"), exp, () -> "Add assertion message");
    }

    @Test
    public void ninIdCollection() {
        Expression exp = property.ninId(Arrays.asList(1, 2, 3));
        assertEquals(ExpressionFactory.exp("path not in (1, 2, 3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void ninIdVararg() {
        Expression exp = property.ninId(1, 2, 3);
        assertEquals(ExpressionFactory.exp("path not in (1, 2, 3)"), exp, () -> "Add assertion message");
    }
}