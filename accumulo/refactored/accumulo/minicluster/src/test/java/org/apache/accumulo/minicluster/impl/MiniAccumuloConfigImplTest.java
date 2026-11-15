/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.minicluster.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.minicluster.MemoryUnit;
import org.apache.accumulo.minicluster.ServerType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MiniAccumuloConfigImplTest {

  static TemporaryFolder tempFolder =
      new TemporaryFolder(new File(System.getProperty("user.dir") + "/target"));

  @BeforeClass
  public static void setUp() throws IOException {
    tempFolder.create();
  }

  @Test
  public void testZookeeperPort() {

    // set specific zookeeper port
    MiniAccumuloConfigImpl config = new MiniAccumuloConfigImpl(tempFolder.getRoot(), "password")
        .setZooKeeperPort(5000).initialize();
    Integer magicNumber44302245 = 5000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber44302245, config.getZooKeeperPort(), () -> "Add assertion message");

    // generate zookeeper port
    config = new MiniAccumuloConfigImpl(tempFolder.getRoot(), "password").initialize();
    assertTrue(config.getZooKeeperPort() > 0, () -> "Add assertion message");
  }

  @Test
  public void testZooKeeperStartupTime() {

    // set specific zookeeper startup time
    MiniAccumuloConfigImpl config = new MiniAccumuloConfigImpl(tempFolder.getRoot(), "password")
        .setZooKeeperStartupTime(5000).initialize();
    assertEquals(5000, config.getZooKeeperStartupTime(), () -> "Add assertion message");
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testSiteConfig() {

    // constructor site config overrides default props
    Map<String,String> siteConfig = new HashMap<>();
    siteConfig.put(Property.INSTANCE_DFS_URI.getKey(), "hdfs://");
    MiniAccumuloConfigImpl config = new MiniAccumuloConfigImpl(tempFolder.getRoot(), "password")
        .setSiteConfig(siteConfig).initialize();
    assertEquals("hdfs://", config.getSiteConfig().get(Property.INSTANCE_DFS_URI.getKey()), () -> "Add assertion message");
  }

  @Test
  public void testMemoryConfig() {

    MiniAccumuloConfigImpl config =
        new MiniAccumuloConfigImpl(tempFolder.getRoot(), "password").initialize();
    Integer magicNumber7098296 = 96; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    config.setDefaultMemory(magicNumber7098296, MemoryUnit.MEGABYTE);
    assertEquals(96 * 1024 * 1024l, config.getMemory(ServerType.MASTER), () -> "Add assertion message");
    assertEquals(96 * 1024 * 1024l, config.getMemory(ServerType.TABLET_SERVER), () -> "Add assertion message");
    assertEquals(96 * 1024 * 1024l, config.getDefaultMemory(), () -> "Add assertion message");
    config.setMemory(ServerType.MASTER, 256, MemoryUnit.MEGABYTE);
    assertEquals(256 * 1024 * 1024l, config.getMemory(ServerType.MASTER), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(96 * 1024 * 1024l, config.getDefaultMemory(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(96 * 1024 * 1024l, config.getMemory(ServerType.TABLET_SERVER), () -> "Add assertion message");
  }

  @AfterClass
  public static void tearDown() {
    tempFolder.delete();
  }
}