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
package org.apache.accumulo.server.conf;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.conf.ConfigurationObserver;
import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.core.zookeeper.ZooUtil;
import org.apache.accumulo.fate.zookeeper.ZooCache;
import org.apache.accumulo.fate.zookeeper.ZooCacheFactory;
import org.gaul.modernizer_maven_annotations.SuppressModernizer;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class TableConfigurationTest {
  private static final String TID = "table";
  private static final String ZOOKEEPERS = "localhost";
  private static final int ZK_SESSION_TIMEOUT = 120000;

  private String iid;
  private Instance instance;
  private NamespaceConfiguration parent;
  private ZooCacheFactory zcf;
  private ZooCache zc;
  private TableConfiguration c;

  @Before
  public void setUp() {
    iid = UUID.randomUUID().toString();
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: iid = UUID.randomUUID().toString();
    instance = createMock(Instance.class);
    expect(instance.getInstanceID()).andReturn(iid).anyTimes();
    expect(instance.getZooKeepers()).andReturn(ZOOKEEPERS);
    expect(instance.getZooKeepersSessionTimeOut()).andReturn(ZK_SESSION_TIMEOUT);
    replay(instance);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: parent = createMock(NamespaceConfiguration.class);
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: c = new TableConfiguration(instance, TID, parent);
    // Removed line due to General Fixture smell: zcf = createMock(ZooCacheFactory.class);
    // Removed line due to General Fixture smell: c.setZooCacheFactory(zcf);
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: expect(
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: .andReturn(zc);
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: replay(zcf);
  // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: // Removed line due to General Fixture smell:
  // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: @Test
  public void testGetters() {

    parent = createMock(NamespaceConfiguration.class);
    // Removed line due to General Fixture smell: c = new TableConfiguration(instance, TID, parent);
    zcf = createMock(ZooCacheFactory.class);
    c.setZooCacheFactory(zcf);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    // Removed line due to General Fixture smell: expect(
        // Removed line due to General Fixture smell: zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        // Removed line due to General Fixture smell: .andReturn(zc);
    // Removed line due to General Fixture smell: replay(zcf);
  // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
  // Removed line due to General Fixture smell: @Test

    // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    expect(
        zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        .andReturn(zc);
    replay(zcf);
  }

  @Test
    c = new TableConfiguration(instance, TID, parent);
    zc = createMock(ZooCache.class);
    assertEquals(TID, c.getTableId(), () -> "Add assertion message");
    assertEquals(parent, c.getParentConfiguration(), () -> "Add assertion message");
  }

  @Test
  public void testGet_InZK() {

    parent = createMock(NamespaceConfiguration.class);
    // Removed line due to General Fixture smell: c = new TableConfiguration(instance, TID, parent);
    zcf = createMock(ZooCacheFactory.class);
    c.setZooCacheFactory(zcf);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    // Removed line due to General Fixture smell: expect(
        // Removed line due to General Fixture smell: zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        // Removed line due to General Fixture smell: .andReturn(zc);
    // Removed line due to General Fixture smell: replay(zcf);
  // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
  // Removed line due to General Fixture smell: @Test

    // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    expect(
        zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        .andReturn(zc);
    replay(zcf);
  }

  @Test
    c = new TableConfiguration(instance, TID, parent);
    zc = createMock(ZooCache.class);
    iid = UUID.randomUUID().toString();
    Property p = Property.INSTANCE_SECRET;
    expect(zc.get(ZooUtil.getRoot(iid) + Constants.ZTABLES + "/" + TID + Constants.ZTABLE_CONF + "/"
        + p.getKey())).andReturn("sekrit".getBytes(UTF_8));
    replay(zc);
    assertEquals("sekrit", c.get(Property.INSTANCE_SECRET), () -> "Add assertion message");
  }

  @Test
  public void testGet_InParent() {

    parent = createMock(NamespaceConfiguration.class);
    // Removed line due to General Fixture smell: c = new TableConfiguration(instance, TID, parent);
    zcf = createMock(ZooCacheFactory.class);
    c.setZooCacheFactory(zcf);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    // Removed line due to General Fixture smell: expect(
        // Removed line due to General Fixture smell: zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        // Removed line due to General Fixture smell: .andReturn(zc);
    // Removed line due to General Fixture smell: replay(zcf);
  // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
  // Removed line due to General Fixture smell: @Test

    // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    expect(
        zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        .andReturn(zc);
    replay(zcf);
  }

  @Test
    c = new TableConfiguration(instance, TID, parent);
    zc = createMock(ZooCache.class);
    iid = UUID.randomUUID().toString();
    Property p = Property.INSTANCE_SECRET;
    expect(zc.get(ZooUtil.getRoot(iid) + Constants.ZTABLES + "/" + TID + Constants.ZTABLE_CONF + "/"
        + p.getKey())).andReturn(null);
    replay(zc);
    expect(parent.get(p)).andReturn("sekrit");
    replay(parent);
    assertEquals("sekrit", c.get(Property.INSTANCE_SECRET), () -> "Add assertion message");
  }

  @Test
  @SuppressModernizer
  public void testGetProperties() {

    parent = createMock(NamespaceConfiguration.class);
    // Removed line due to General Fixture smell: c = new TableConfiguration(instance, TID, parent);
    zcf = createMock(ZooCacheFactory.class);
    c.setZooCacheFactory(zcf);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    // Removed line due to General Fixture smell: expect(
        // Removed line due to General Fixture smell: zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        // Removed line due to General Fixture smell: .andReturn(zc);
    // Removed line due to General Fixture smell: replay(zcf);
  // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
  // Removed line due to General Fixture smell: @Test

    // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    expect(
        zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        .andReturn(zc);
    replay(zcf);
  }

  @Test
    c = new TableConfiguration(instance, TID, parent);
    zc = createMock(ZooCache.class);
    iid = UUID.randomUUID().toString();
    Predicate<String> all = Predicates.alwaysTrue();
    Map<String,String> props = new java.util.HashMap<>();
    parent.getProperties(props, all);
    replay(parent);
    List<String> children = new java.util.ArrayList<>();
    children.add("foo");
    children.add("ding");
    expect(zc
        .getChildren(ZooUtil.getRoot(iid) + Constants.ZTABLES + "/" + TID + Constants.ZTABLE_CONF))
        .andReturn(children);
    expect(zc.get(
        ZooUtil.getRoot(iid) + Constants.ZTABLES + "/" + TID + Constants.ZTABLE_CONF + "/" + "foo"))
        .andReturn("bar".getBytes(UTF_8));
    expect(zc.get(ZooUtil.getRoot(iid) + Constants.ZTABLES + "/" + TID + Constants.ZTABLE_CONF + "/"
        + "ding")).andReturn("dong".getBytes(UTF_8));
    replay(zc);
    c.getProperties(props, all);
    Integer magicNumber31823456 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber31823456, props.size(), () -> "Add assertion message");
    assertEquals("bar", props.get("foo"), () -> "Add assertion message");
    assertEquals("dong", props.get("ding"), () -> "Add assertion message");
  }

  @Test
  public void testObserver() {

    parent = createMock(NamespaceConfiguration.class);
    // Removed line due to General Fixture smell: c = new TableConfiguration(instance, TID, parent);
    zcf = createMock(ZooCacheFactory.class);
    c.setZooCacheFactory(zcf);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    // Removed line due to General Fixture smell: expect(
        // Removed line due to General Fixture smell: zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        // Removed line due to General Fixture smell: .andReturn(zc);
    // Removed line due to General Fixture smell: replay(zcf);
  // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
  // Removed line due to General Fixture smell: @Test

    // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    expect(
        zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        .andReturn(zc);
    replay(zcf);
  }

  @Test
    c = new TableConfiguration(instance, TID, parent);
    zc = createMock(ZooCache.class);
    ConfigurationObserver o = createMock(ConfigurationObserver.class);
    c.addObserver(o);
    Collection<ConfigurationObserver> os = c.getObservers();
    assertEquals(1, os.size(), () -> "Add assertion message");
    assertTrue(os.contains(o), () -> "Add assertion message");
    c.removeObserver(o);
    os = c.getObservers();
    assertEquals(0, os.size(), () -> "Add assertion message");
  }

  @Test
  public void testInvalidateCache() {

    parent = createMock(NamespaceConfiguration.class);
    // Removed line due to General Fixture smell: c = new TableConfiguration(instance, TID, parent);
    zcf = createMock(ZooCacheFactory.class);
    c.setZooCacheFactory(zcf);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    // Removed line due to General Fixture smell: expect(
        // Removed line due to General Fixture smell: zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        // Removed line due to General Fixture smell: .andReturn(zc);
    // Removed line due to General Fixture smell: replay(zcf);
  // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
  // Removed line due to General Fixture smell: @Test

    // Removed line due to General Fixture smell: zc = createMock(ZooCache.class);
    expect(
        zcf.getZooCache(eq(ZOOKEEPERS), eq(ZK_SESSION_TIMEOUT), anyObject(TableConfWatcher.class)))
        .andReturn(zc);
    replay(zcf);
  }

  @Test
    c = new TableConfiguration(instance, TID, parent);
    zc = createMock(ZooCache.class);
    iid = UUID.randomUUID().toString();
    // need to do a get so the accessor is created
    Property p = Property.INSTANCE_SECRET;
    expect(zc.get(ZooUtil.getRoot(iid) + Constants.ZTABLES + "/" + TID + Constants.ZTABLE_CONF + "/"
        + p.getKey())).andReturn("sekrit".getBytes(UTF_8));
    zc.clear();
    replay(zc);
    c.get(Property.INSTANCE_SECRET);
    c.invalidateCache();
    verify(zc);
  }
}