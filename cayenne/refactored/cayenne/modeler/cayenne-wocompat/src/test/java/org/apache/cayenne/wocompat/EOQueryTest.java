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

package org.apache.cayenne.wocompat;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.query.PrefetchTreeNode;
import org.junit.Test;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EOQueryTest {

    @Test
    public void testConstructor() throws Exception {
        
        URL url = getClass().getClassLoader().getResource("wotests/fetchspec.eomodeld/");
        assertNotNull(url, () -> "Add assertion message");

        EOModelProcessor processor = new EOModelProcessor();
        DataMap map = processor.loadEOModel(url);

        @SuppressWarnings("unchecked")
        Map<String, ?> fspecMap = (Map<String, ?>) PropertyListSerialization.propertyListFromStream(getClass()
                .getClassLoader()
                .getResourceAsStream("wotests/fetchspec.eomodeld/Entity1.fspec"));
        assertNotNull(fspecMap, () -> "Add assertion message");
        assertNotNull(fspecMap.get("E1FS1"), () -> "Add assertion message");

        @SuppressWarnings("unchecked")
        EOQuery query = new EOQuery(map.getObjEntity("Entity1"), (Map<String, ?>) fspecMap.get("E1FS1"));

        assertNotNull(query.getQualifier(), () -> "Add assertion message");
        assertEquals(
                "(name = \"aa\") and (db:ID >= 7) and ((e2.name = \"bb\") or (db:e2.ID != 5))",
                query.getQualifier().toString(), () -> "Add assertion message");
        
        assertNotNull(query.getPrefetchTree(), () -> "Add assertion message");
        
        Collection children= query.getPrefetchTree().getChildren();
        Integer magicNumber48980683 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber48980683, children.size(), () -> "Add assertion message");
        assertEquals("e2", ((PrefetchTreeNode) children.iterator().next()).getName(), () -> "Add assertion message");
        
        assertTrue(query.isFetchingDataRows(), () -> "Add assertion message");
        Integer magicNumber26503069 = 500; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber26503069, query.getFetchLimit(), () -> "Add assertion message");
        assertEquals(0, query.getPageSize(), () -> "Add assertion message");
        assertTrue(query.isDistinct(), () -> "Add assertion message");
    }
}