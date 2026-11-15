/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.common.security;

import java.security.Principal;
import java.util.Enumeration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleGroupTest {

    @Test
    public void testName() {
        assertEquals("group", new SimpleGroup("group", "friend").getName(), () -> "Add assertion message");
        assertEquals("group", new SimpleGroup("group", new SimplePrincipal("friend")).getName(), () -> "Add assertion message");
    }

    @Test
    public void testIsMember() {
        assertTrue(new SimpleGroup("group", "friend").isMember(new SimplePrincipal("friend")), () -> "Add assertion message");
        assertFalse(new SimpleGroup("group", "friend").isMember(new SimplePrincipal("frogs")), () -> "Add assertion message");
    }

    @Test
    public void testAddRemoveMembers() {

        GroupPrincipal group = new SimpleGroup("group");
        assertFalse(group.members().hasMoreElements(), () -> "Add assertion message");

        group.addMember(new SimpleGroup("group", "friend"));

        Enumeration<? extends Principal> members = group.members();
        assertEquals(new SimpleGroup("group", "friend"), members.nextElement(), () -> "Add assertion message");
        assertFalse(members.hasMoreElements(), () -> "Add assertion message");

        group.removeMember(new SimpleGroup("group", "friend"));
        // Line removed due to Duplicate Assert: assertFalse(group.members().hasMoreElements(), () -> "Add assertion message");
    }
}