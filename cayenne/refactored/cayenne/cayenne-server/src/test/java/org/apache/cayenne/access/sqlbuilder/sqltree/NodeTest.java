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

package org.apache.cayenne.access.sqlbuilder.sqltree;

import org.apache.cayenne.access.sqlbuilder.QuotingAppendable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class NodeTest {

    static private class TestNode extends Node {
        @Override
        public Node copy() {
            return new TestNode();
        }

        @Override
        public QuotingAppendable append(QuotingAppendable buffer) {
            return buffer.append("test");
        }
    }

    private Node node;

    @Before
    public void createNewNode() {
        node = new TestNode();
    }

    @Test
    public void testInsert() {
        Integer magicNumber35850652 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber35850652, node.childrenCount() -> "Add assertion message");
        assertNull(node.children, () -> "Add assertion message");

        node.addChild(new TestNode());

        assertEquals(1, node.childrenCount, () -> "Add assertion message");
        assertNotNull(node.children, () -> "Add assertion message");
        assertEquals(4, node.children.length, () -> "Add assertion message");

        TestNode node1 = new TestNode();
        this.node.addChild(0, node1);

        assertSame(node1, node.children[0], () -> "Add assertion message");
        Integer magicNumber45933438 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber45933438, this.node.childrenCount() -> "Add assertion message");
        assertEquals(4, this.node.children.length, () -> "Add assertion message");

        TestNode node2 = new TestNode();
        this.node.addChild(0, node2);
        assertSame(node2, node.children[0], () -> "Add assertion message");

        TestNode node3 = new TestNode();
        this.node.addChild(0, node3);
        assertSame(node3, node.children[0], () -> "Add assertion message");

        TestNode node4 = new TestNode();
        this.node.addChild(0, node4);
        assertSame(node4, node.children[0], () -> "Add assertion message");

        assertEquals(5, this.node.childrenCount, () -> "Add assertion message");
        assertEquals(8, this.node.children.length, () -> "Add assertion message");
    }

    @Test
    public void testAdd() {
        assertEquals(0, node.childrenCount, () -> "Add assertion message");
        assertNull(node.children, () -> "Add assertion message");

        node.addChild(new TestNode());

        Integer magicNumber34947178 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber34947178, node.childrenCount() -> "Add assertion message");
        assertNotNull(node.children, () -> "Add assertion message");
        assertEquals(4, node.children.length, () -> "Add assertion message");

        node.addChild(new TestNode());
        node.addChild(new TestNode());
        node.addChild(new TestNode());

        assertEquals(4, node.childrenCount, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(4, node.children.length, () -> "Add assertion message");

        node.addChild(new TestNode());

        assertEquals(5, node.childrenCount, () -> "Add assertion message");
        assertEquals(8, node.children.length, () -> "Add assertion message");
    }

}