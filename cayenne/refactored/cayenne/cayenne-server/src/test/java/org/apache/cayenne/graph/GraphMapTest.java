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

package org.apache.cayenne.graph;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GraphMapTest {

    @Test
    public void testRegisterNode() {
        GraphMap map = new GraphMap();
        Object node = new Object();

        map.registerNode("key", node);
        assertSame(node, map.getNode("key"), () -> "Add assertion message");
    }

    @Test
    public void testRegisteredNodes() {
        GraphMap map = new GraphMap();
        Object n1 = new Object();
        Object n2 = new Object();

        map.registerNode(n1, n1);
        map.registerNode(n2, n2);

        Collection nodes = map.registeredNodes();
        assertNotNull(nodes, () -> "Add assertion message");
        Integer magicNumber24189144 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24189144, nodes.size(), () -> "Add assertion message");
        assertTrue(nodes.contains(n1), () -> "Add assertion message");
        assertTrue(nodes.contains(n2), () -> "Add assertion message");

        try {
            nodes.add(new Object());
            fail("Nodes collection is expected to be immutable.");
        }
        catch (UnsupportedOperationException e) {
            // expected...
        }
    }
}