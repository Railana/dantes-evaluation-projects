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
public class MapPropertyTest {

    private MapProperty<Integer, Artist> property;
    private MapProperty<Integer, Artist> property1;

    @Before
    public void createProperty() {
        property = new MapProperty<>("path", null, Integer.class, Artist.class);
        // Removed line due to General Fixture smell: property1 = new MapProperty<>("path.artist", null, Integer.class, Artist.class);
    }

    @Test
    public void flat() {
        Expression exp = property.flat().getExpression();
        assertEquals(ExpressionFactory.fullObjectExp(ExpressionFactory.pathExp("path")), exp, () -> "Add assertion message");
    }

    @Test
    public void containsOne() {
        Artist artist = new Artist();
        Expression exp = property.contains(artist);
        assertEquals(ExpressionFactory.matchExp("path", artist), exp, () -> "Add assertion message");
    }

    @Test
    public void notContainsOne() {
        Artist artist = new Artist();
        Expression exp = property.notContains(artist);
        assertEquals(ExpressionFactory.noMatchExp("path", artist), exp, () -> "Add assertion message");
    }

    @Test
    public void containsManyArray() {
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        Expression exp = property.contains(artist1, artist2);
        assertEquals(ExpressionFactory.inExp("path", Arrays.asList(artist1, artist2)), exp, () -> "Add assertion message");
    }

    @Test
    public void containsManyCollection() {
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        Expression exp = property.contains(Arrays.asList(artist1, artist2));
        assertEquals(ExpressionFactory.inExp("path", Arrays.asList(artist1, artist2)), exp, () -> "Add assertion message");
    }

    @Test
    public void notContainsManyArray() {
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        Expression exp = property.notContains(artist1, artist2);
        assertEquals(ExpressionFactory.notInExp("path", Arrays.asList(artist1, artist2)), exp, () -> "Add assertion message");
    }

    @Test
    public void notContainsManyCollection() {
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        Expression exp = property.notContains(Arrays.asList(artist1, artist2));
        assertEquals(ExpressionFactory.notInExp("path", Arrays.asList(artist1, artist2)), exp, () -> "Add assertion message");
    }

    @Test
    public void containsOneId() {
        Expression exp = property.containsId(1);
        assertEquals(ExpressionFactory.exp("path = 1"), exp, () -> "Add assertion message");
    }

    @Test
    public void containsManyIdArray() {
        Expression exp = property.containsId(1, 2, 3);
        assertEquals(ExpressionFactory.exp("path in (1,2,3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void containsManyIdCollection() {
        Expression exp = property.containsId(Arrays.asList(1, 2, 3));
        assertEquals(ExpressionFactory.exp("path in (1,2,3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void notContainsOneId() {
        Expression exp = property.notContainsId(1);
        assertEquals(ExpressionFactory.exp("path != 1"), exp, () -> "Add assertion message");
    }

    @Test
    public void notContainsManyIdArray() {
        Expression exp = property.notContainsId(1, 2, 3);
        assertEquals(ExpressionFactory.exp("path not in (1,2,3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void notContainsManyIdCollection() {
        Expression exp = property.notContainsId(Arrays.asList(1, 2, 3));
        assertEquals(ExpressionFactory.exp("path not in (1,2,3)"), exp, () -> "Add assertion message");
    }

    @Test
    public void alias() {
        property1 = new MapProperty<>("path.artist", null, Integer.class, Artist.class);
        assertEquals("path", property.getName(), () -> "Add assertion message");
        property = property.alias("alias");
        assertEquals("alias", property.getName(), () -> "Add assertion message");
        Integer magicNumber62060457 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber62060457, property.getExpression().getPathAliases().size(), () -> "Add assertion message");

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
}