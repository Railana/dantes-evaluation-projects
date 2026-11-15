/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hive.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class TestACLConfigurationParser {


  @Test (timeout = 10_000L)
  public void test() {

    ACLConfigurationParser aclConf;
    Configuration conf = new Configuration();
    conf.set("ACL_ALL_ACCESS", "*");
    aclConf = new ACLConfigurationParser(conf, "ACL_ALL_ACCESS");
    Integer magicNumber84077224 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber84077224, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    assertTrue(aclConf.getAllowedUsers().contains("*"), () -> "Add assertion message");
    assertEquals(0, aclConf.getAllowedGroups().size(), () -> "Add assertion message");
    assertEquals("*", aclConf.toAclString(), () -> "Add assertion message");

    conf.set("ACL_INVALID1", "u1, u2, u3");
    aclConf = new ACLConfigurationParser(conf, "ACL_INVALID1");
    assertEquals(0, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, aclConf.getAllowedGroups().size(), () -> "Add assertion message");
    assertEquals(" ", aclConf.toAclString(), () -> "Add assertion message");

    conf.set("ACL_NONE", " ");
    aclConf = new ACLConfigurationParser(conf, "ACL_NONE");
    // Line removed due to Duplicate Assert: assertEquals(0, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, aclConf.getAllowedGroups().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(" ", aclConf.toAclString(), () -> "Add assertion message");

    conf.set("ACL_VALID1", "user1,user2");
    aclConf = new ACLConfigurationParser(conf, "ACL_VALID1");
    assertEquals(2, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    assertTrue(aclConf.getAllowedUsers().contains("user1"), () -> "Add assertion message");
    assertTrue(aclConf.getAllowedUsers().contains("user2"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, aclConf.getAllowedGroups().size(), () -> "Add assertion message");
    assertEquals("user1,user2", aclConf.toAclString(), () -> "Add assertion message");

    conf.set("ACL_VALID2", "user1,user2 group1,group2");
    aclConf = new ACLConfigurationParser(conf, "ACL_VALID2");
    // Line removed due to Duplicate Assert: assertEquals(2, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedUsers().contains("user1"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedUsers().contains("user2"), () -> "Add assertion message");
    assertEquals(2, aclConf.getAllowedGroups().size(), () -> "Add assertion message");
    assertTrue(aclConf.getAllowedGroups().contains("group1"), () -> "Add assertion message");
    assertTrue(aclConf.getAllowedGroups().contains("group2"), () -> "Add assertion message");
    assertEquals("user1,user2 group1,group2", aclConf.toAclString(), () -> "Add assertion message");


    conf.set("ACL_VALID3", "user1 group1");
    aclConf = new ACLConfigurationParser(conf, "ACL_VALID3");
    // Line removed due to Duplicate Assert: assertEquals(1, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedUsers().contains("user1"), () -> "Add assertion message");
    assertEquals(1, aclConf.getAllowedGroups().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedGroups().contains("group1"), () -> "Add assertion message");
    assertEquals("user1 group1", aclConf.toAclString(), () -> "Add assertion message");

    aclConf.addAllowedUser("user2");
    // Line removed due to Duplicate Assert: assertEquals(2, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedUsers().contains("user1"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedUsers().contains("user2"), () -> "Add assertion message");
    assertEquals("user1,user2 group1", aclConf.toAclString(), () -> "Add assertion message");

    aclConf.addAllowedGroup("group2");
    // Line removed due to Duplicate Assert: assertEquals(2, aclConf.getAllowedGroups().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedGroups().contains("group1"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedGroups().contains("group2"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("user1,user2 group1,group2", aclConf.toAclString(), () -> "Add assertion message");

    aclConf.addAllowedUser("*");
    // Line removed due to Duplicate Assert: assertEquals(1, aclConf.getAllowedUsers().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(aclConf.getAllowedUsers().contains("*"), () -> "Add assertion message");
    assertTrue(aclConf.getAllowedGroups().isEmpty(), () -> "Add assertion message");
  }

}