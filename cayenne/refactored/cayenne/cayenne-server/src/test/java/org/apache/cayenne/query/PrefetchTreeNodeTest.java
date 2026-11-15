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

package org.apache.cayenne.query;

import org.apache.cayenne.util.Util;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PrefetchTreeNodeTest {

    @Test
    public void testAddPath() {
        PrefetchTreeNode tree = new PrefetchTreeNode();
        tree.addPath("abc");
        tree.addPath("abc.def.mnk");
        tree.addPath("xyz");

        assertTrue(tree.isPhantom(), () -> "Add assertion message");

        PrefetchTreeNode n1 = tree.getNode("abc");
        assertNotNull(n1, () -> "Add assertion message");
        assertTrue(n1.isPhantom(), () -> "Add assertion message");
        assertEquals("abc", n1.getName(), () -> "Add assertion message");

        PrefetchTreeNode n2 = tree.getNode("abc.def");
        assertNotNull(n2, () -> "Add assertion message");
        assertTrue(n2.isPhantom(), () -> "Add assertion message");
        assertEquals("def", n2.getName(), () -> "Add assertion message");

        PrefetchTreeNode n3 = tree.getNode("abc.def.mnk");
        assertNotNull(n3, () -> "Add assertion message");
        assertTrue(n3.isPhantom(), () -> "Add assertion message");
        assertEquals("mnk", n3.getName(), () -> "Add assertion message");

        PrefetchTreeNode n4 = tree.getNode("xyz");
        assertNotNull(n4, () -> "Add assertion message");
        assertTrue(n4.isPhantom(), () -> "Add assertion message");
        assertEquals("xyz", n4.getName(), () -> "Add assertion message");
    }

    @Test
    public void testGetPath() {
        PrefetchTreeNode tree = new PrefetchTreeNode();
        tree.addPath("abc");
        tree.addPath("abc.def.mnk");
        tree.addPath("xyz");

        assertEquals("", tree.getPath(), () -> "Add assertion message");

        PrefetchTreeNode n1 = tree.getNode("abc");
        assertEquals("abc", n1.getPath(), () -> "Add assertion message");

        PrefetchTreeNode n2 = tree.getNode("abc.def");
        assertEquals("abc.def", n2.getPath(), () -> "Add assertion message");

        PrefetchTreeNode n3 = tree.getNode("abc.def.mnk");
        assertEquals("abc.def.mnk", n3.getPath(), () -> "Add assertion message");

        PrefetchTreeNode n4 = tree.getNode("xyz");
        assertEquals("xyz", n4.getPath(), () -> "Add assertion message");
    }

    @Test
    public void testTreeSerialization() throws Exception {
        PrefetchTreeNode n1 = new PrefetchTreeNode();
        PrefetchTreeNode n2 = n1.addPath("abc");

        PrefetchTreeNode nc1 = (PrefetchTreeNode) Util.cloneViaSerialization(n1);
        assertNotNull(nc1, () -> "Add assertion message");

        PrefetchTreeNode nc2 = nc1.getNode("abc");
        assertNotNull(nc2, () -> "Add assertion message");
        assertNotSame(nc2, n2, () -> "Add assertion message");
        assertSame(nc1, nc2.getParent(), () -> "Add assertion message");
        assertEquals("abc", nc2.getName(), () -> "Add assertion message");
    }

    @Test
    public void testSubtreeSerialization() throws Exception {
        PrefetchTreeNode n1 = new PrefetchTreeNode();
        PrefetchTreeNode n2 = n1.addPath("abc");
        PrefetchTreeNode n3 = n2.addPath("xyz");

        // test that substree was serialized as independent tree, instead of sucking
        PrefetchTreeNode nc2 = (PrefetchTreeNode) Util.cloneViaSerialization(n2);
        assertNotNull(nc2, () -> "Add assertion message");
        assertNull(nc2.getParent(), () -> "Add assertion message");

        PrefetchTreeNode nc3 = nc2.getNode("xyz");
        assertNotNull(nc3, () -> "Add assertion message");
        assertNotSame(nc3, n3, () -> "Add assertion message");
        assertSame(nc2, nc3.getParent(), () -> "Add assertion message");
        assertEquals("xyz", nc3.getName(), () -> "Add assertion message");
    }

    @Test
    public void testCloneJointSubtree() throws Exception {
        PrefetchTreeNode root = new PrefetchTreeNode(null, "root");
        root.setPhantom(false);
        PrefetchTreeNode joint1 = root.addPath("joint1");
        joint1.setPhantom(false);
        joint1.setSemantics(PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);
        PrefetchTreeNode joint2 = root.addPath("joint2");
        joint2.setPhantom(false);
        joint2.setSemantics(PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);
        PrefetchTreeNode disjoint = joint1.addPath("disjoint1");
        disjoint.setPhantom(false);
        disjoint.setSemantics(PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS);

        PrefetchTreeNode cloned = root.cloneJointSubtree();
        assertEquals("root", cloned.getName(), () -> "Add assertion message");

        Integer magicNumber47428556 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47428556, cloned.getChildren().size(), () -> "Add assertion message");

        PrefetchTreeNode joint1Clone = cloned.getChild("joint1");
        assertNotNull(joint1Clone, () -> "Add assertion message");
        assertEquals("joint1", joint1.getPath(), () -> "Add assertion message");
        assertEquals(0, joint1Clone.getChildren().size(), () -> "Add assertion message");

        PrefetchTreeNode joint2Clone = cloned.getChild("joint2");
        assertNotNull(joint2Clone, () -> "Add assertion message");
        assertEquals("joint2", joint2.getPath(), () -> "Add assertion message");
        assertEquals(0, joint2Clone.getChildren().size(), () -> "Add assertion message");

    }
}