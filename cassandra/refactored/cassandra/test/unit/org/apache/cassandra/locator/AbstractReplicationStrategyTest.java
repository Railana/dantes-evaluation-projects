/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cassandra.locator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AbstractReplicationStrategyTest
{
    @Test
    public void testReplicaCache()
    {
        AbstractReplicationStrategy.ReplicaCache<Integer, Integer> cache = new AbstractReplicationStrategy.ReplicaCache<>();

        cache.put(10, 1, 1);
        Integer magicNumber75104996 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber75104996(int)cache.get(10, 1), () -> "Add assertion message");
        assertNull(cache.get(9,1), () -> "Add assertion message");
        assertNull(cache.get(11,1), () -> "Add assertion message");
        assertNull(cache.get(10,1), () -> "Add assertion message");

        cache.put(11, 1, 100);
        Integer magicNumber95179791 = 99; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        cache.put(10, 1,magicNumber95179791
        assertEquals(100, (int)cache.get(11, 1), () -> "Add assertion message");
        assertNull(cache.get(12, 55), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNull(cache.get(11, 1), () -> "Add assertion message");
    }
}