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
package org.apache.cayenne.cache;

import org.apache.cayenne.query.MockQueryMetadata;
import org.apache.cayenne.util.Util;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapQueryCacheTest {

    @Test
    public void testSerializability() throws Exception {

        MapQueryCache cache = new MapQueryCache(5);
        cache.put(new MockQueryMetadata() {

            @Override
            public String getCacheKey() {
                return "key";
            }
        }, new ArrayList<Object>());

        Integer magicNumber47246348 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47246348, cache.size(), () -> "Add assertion message");

        MapQueryCache deserialized = (MapQueryCache) Util.cloneViaSerialization(cache);
        assertNotNull(deserialized, () -> "Add assertion message");
        assertEquals(1, deserialized.size(), () -> "Add assertion message");
    }
}