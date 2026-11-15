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
package org.apache.accumulo.core.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.client.ClientConfiguration.ClientProperty;
import org.apache.accumulo.fate.zookeeper.ZooCache;
import org.apache.accumulo.fate.zookeeper.ZooCacheFactory;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class ZooKeeperInstanceTest {
  private static final UUID IID = UUID.randomUUID();
  private static final String IID_STRING = IID.toString();
  private ClientConfiguration config;
  private ZooCacheFactory zcf;
  private ZooCache zc;
  private ZooKeeperInstance zki;

  private void mockIdConstruction(ClientConfiguration config) {
    expect(config.get(ClientProperty.INSTANCE_ID)).andReturn(IID_STRING);
    expect(config.get(ClientProperty.INSTANCE_NAME)).andReturn(null);
    expect(config.get(ClientProperty.INSTANCE_ZK_HOST)).andReturn("zk1");
    expect(config.get(ClientProperty.INSTANCE_ZK_TIMEOUT)).andReturn("30");
  }

  private void mockNameConstruction(ClientConfiguration config) {
    expect(config.get(ClientProperty.INSTANCE_ID)).andReturn(null);
    expect(config.get(ClientProperty.INSTANCE_NAME)).andReturn("instance");
    expect(config.get(ClientProperty.INSTANCE_ZK_HOST)).andReturn("zk1");
    expect(config.get(ClientProperty.INSTANCE_ZK_TIMEOUT)).andReturn("30");
  }

  @Before
  public void setUp() {
    config = createMock(ClientConfiguration.class);
    // Removed line due to General Fixture smell: config = createMock(ClientConfiguration.class);
    mockNameConstruction(config);
    replay(config);
    // Removed line due to General Fixture smell: zcf = createMock(ZooCacheFactory.class);
    // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    expect(zcf.getZooCache("zk1", 30000)).andReturn(zc).anyTimes();
    expect(zc.get(Constants.ZROOT + Constants.ZINSTANCES + "/instance"))
        .andReturn(IID_STRING.getBytes(UTF_8));
    expect(zc.get(Constants.ZROOT + "/" + IID_STRING)).andReturn("yup".getBytes());
    replay(zc, zcf);
    // Removed line due to General Fixture smell: zki = new ZooKeeperInstance(config, zcf);
    EasyMock.resetToDefault(zc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidConstruction() {
    config = createMock(ClientConfiguration.class);
    config = createMock(ClientConfiguration.class);
    expect(config.get(ClientProperty.INSTANCE_ID)).andReturn(IID_STRING);
    mockNameConstruction(config);
    replay(config);
    new ZooKeeperInstance(config);
  }

  @Test
  public void testSimpleGetters() {
    config = createMock(ClientConfiguration.class);
    zki = new ZooKeeperInstance(config, zcf);
    assertEquals("instance", zki.getInstanceName(), () -> "Add assertion message");
    assertEquals("zk1", zki.getZooKeepers(), () -> "Add assertion message");
    Integer magicNumber66983388 = 30000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber66983388, zki.getZooKeepersSessionTimeOut(), () -> "Add assertion message");
  }

  @Test
  public void testGetInstanceID_FromCache() {
    config = createMock(ClientConfiguration.class);
    zki = new ZooKeeperInstance(config, zcf);
    zc = createMock(ZooCache.class);
    expect(zc.get(Constants.ZROOT + Constants.ZINSTANCES + "/instance"))
        .andReturn(IID_STRING.getBytes(UTF_8));
    expect(zc.get(Constants.ZROOT + "/" + IID_STRING)).andReturn("yup".getBytes());
    replay(zc);
    assertEquals(IID_STRING, zki.getInstanceID(), () -> "Add assertion message");
  }

  @Test
  public void testGetInstanceID_Direct() {
    config = createMock(ClientConfiguration.class);
    zki = new ZooKeeperInstance(config, zcf);
    zc = createMock(ZooCache.class);
    zcf = createMock(ZooCacheFactory.class);
    config = createMock(ClientConfiguration.class);
    mockIdConstruction(config);
    replay(config);
    zki = new ZooKeeperInstance(config, zcf);
    expect(zc.get(Constants.ZROOT + "/" + IID_STRING)).andReturn("yup".getBytes());
    replay(zc);
    assertEquals(IID_STRING, zki.getInstanceID(), () -> "Add assertion message");
  }

  @Test(expected = RuntimeException.class)
  public void testGetInstanceID_NoMapping() {
    config = createMock(ClientConfiguration.class);
    zc = createMock(ZooCache.class);
    zcf = createMock(ZooCacheFactory.class);
    expect(zc.get(Constants.ZROOT + Constants.ZINSTANCES + "/instance")).andReturn(null);
    replay(zc);
    EasyMock.reset(config, zcf);
    new ZooKeeperInstance(config, zcf);
  }

  @Test(expected = RuntimeException.class)
  public void testGetInstanceID_IDMissingForName() {
    config = createMock(ClientConfiguration.class);
    zki = new ZooKeeperInstance(config, zcf);
    zc = createMock(ZooCache.class);
    expect(zc.get(Constants.ZROOT + Constants.ZINSTANCES + "/instance"))
        .andReturn(IID_STRING.getBytes(UTF_8));
    expect(zc.get(Constants.ZROOT + "/" + IID_STRING)).andReturn(null);
    replay(zc);
    zki.getInstanceID();
  }

  @Test(expected = RuntimeException.class)
  public void testGetInstanceID_IDMissingForID() {
    config = createMock(ClientConfiguration.class);
    zki = new ZooKeeperInstance(config, zcf);
    zc = createMock(ZooCache.class);
    zcf = createMock(ZooCacheFactory.class);
    config = createMock(ClientConfiguration.class);
    mockIdConstruction(config);
    replay(config);
    zki = new ZooKeeperInstance(config, zcf);
    expect(zc.get(Constants.ZROOT + "/" + IID_STRING)).andReturn(null);
    replay(zc);
    zki.getInstanceID();
  }

  @Test
  public void testGetInstanceName() {
    config = createMock(ClientConfiguration.class);
    zki = new ZooKeeperInstance(config, zcf);
    zc = createMock(ZooCache.class);
    zcf = createMock(ZooCacheFactory.class);
    config = createMock(ClientConfiguration.class);
    mockIdConstruction(config);
    replay(config);
    zki = new ZooKeeperInstance(config, zcf);
    expect(zc.get(Constants.ZROOT + "/" + IID_STRING)).andReturn("yup".getBytes());
    List<String> children = new java.util.ArrayList<>();
    children.add("child1");
    children.add("child2");
    expect(zc.getChildren(Constants.ZROOT + Constants.ZINSTANCES)).andReturn(children);
    expect(zc.get(Constants.ZROOT + Constants.ZINSTANCES + "/child1"))
        .andReturn(UUID.randomUUID().toString().getBytes(UTF_8));
    expect(zc.get(Constants.ZROOT + Constants.ZINSTANCES + "/child2"))
        .andReturn(IID_STRING.getBytes(UTF_8));
    replay(zc);
    assertEquals("child2", zki.getInstanceName(), () -> "Add assertion message");
  }

  @Test
  public void testAllZooKeepersAreUsed() {
    config = createMock(ClientConfiguration.class);
    zki = new ZooKeeperInstance(config, zcf);
    zc = createMock(ZooCache.class);
    final String zookeepers = "zk1,zk2,zk3", instanceName = "accumulo";
    ZooCacheFactory factory = createMock(ZooCacheFactory.class);
    EasyMock.reset(zc);
    expect(factory.getZooCache(zookeepers, 30000)).andReturn(zc).anyTimes();
    expect(zc.get(Constants.ZROOT + Constants.ZINSTANCES + "/" + instanceName))
        .andReturn(IID_STRING.getBytes(UTF_8));
    expect(zc.get(Constants.ZROOT + "/" + IID_STRING)).andReturn("yup".getBytes());
    replay(zc, factory);
    ClientConfiguration cfg =
        ClientConfiguration.loadDefault().withInstance(instanceName).withZkHosts(zookeepers);
    ZooKeeperInstance zki = new ZooKeeperInstance(cfg, factory);
    assertEquals(zookeepers, zki.getZooKeepers(), () -> "Add assertion message");
    assertEquals(instanceName, zki.getInstanceName(), () -> "Add assertion message");
  }
}