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
package org.apache.accumulo.server.master.state;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.apache.accumulo.core.data.impl.KeyExtent;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TabletLocationStateTest {
  private static final Collection<String> innerWalogs = new java.util.HashSet<>();
  private static final Collection<Collection<String>> walogs = new java.util.HashSet<>();

  @BeforeClass
  public static void setUpClass() {
    walogs.add(innerWalogs);
    innerWalogs.add("somelog");
  }

  private KeyExtent keyExtent;
  private TServerInstance future;
  private TServerInstance current;
  private TServerInstance last;
  private TabletLocationState tls;

  @Before
  public void setUp() throws Exception {
    keyExtent = createMock(KeyExtent.class);
    // Removed line due to General Fixture smell: future = createMock(TServerInstance.class);
    // Removed line due to General Fixture smell: current = createMock(TServerInstance.class);
    // Removed line due to General Fixture smell: last = createMock(TServerInstance.class);
  }

  @Test
  public void testConstruction_NoFuture() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    future = createMock(TServerInstance.class);
    tls = new TabletLocationState(keyExtent, null, current, last, null, walogs, true);
    assertSame(keyExtent, tls.extent, () -> "Add assertion message");
    assertNull(tls.future, () -> "Add assertion message");
    assertSame(current, tls.current, () -> "Add assertion message");
    assertSame(last, tls.last, () -> "Add assertion message");
    assertSame(walogs, tls.walogs, () -> "Add assertion message");
    assertTrue(tls.chopped, () -> "Add assertion message");
  }

  @Test
  public void testConstruction_NoCurrent() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    future = createMock(TServerInstance.class);
    tls = new TabletLocationState(keyExtent, future, null, last, null, walogs, true);
    assertSame(keyExtent, tls.extent, () -> "Add assertion message");
    assertSame(future, tls.future, () -> "Add assertion message");
    assertNull(tls.current, () -> "Add assertion message");
    assertSame(last, tls.last, () -> "Add assertion message");
    assertSame(walogs, tls.walogs, () -> "Add assertion message");
    assertTrue(tls.chopped, () -> "Add assertion message");
  }

  @Test(expected = TabletLocationState.BadLocationStateException.class)
  public void testConstruction_FutureAndCurrent() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    future = createMock(TServerInstance.class);
    expect(keyExtent.getMetadataEntry()).andReturn(new Text("entry"));
    replay(keyExtent);
    try {
      new TabletLocationState(keyExtent, future, current, last, null, walogs, true);
    } catch (TabletLocationState.BadLocationStateException e) {
      assertEquals(new Text("entry"), e.getEncodedEndRow(), () -> "Add assertion message");
      throw (e);
    }
  }

  @Test
  public void testConstruction_NoFuture_NoWalogs() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    tls = new TabletLocationState(keyExtent, null, current, last, null, null, true);
    assertNotNull(tls.walogs, () -> "Add assertion message");
    Integer magicNumber26090437 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber26090437, tls.walogs.size(), () -> "Add assertion message");
  }

  @Test
  public void testGetServer_Current() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    tls = new TabletLocationState(keyExtent, null, current, last, null, walogs, true);
    assertSame(current, tls.getServer(), () -> "Add assertion message");
  }

  @Test
  public void testGetServer_Future() throws Exception {
    last = createMock(TServerInstance.class);
    future = createMock(TServerInstance.class);
    tls = new TabletLocationState(keyExtent, future, null, last, null, walogs, true);
    assertSame(future, tls.getServer(), () -> "Add assertion message");
  }

  @Test
  public void testGetServer_Last() throws Exception {
    last = createMock(TServerInstance.class);
    tls = new TabletLocationState(keyExtent, null, null, last, null, walogs, true);
    assertSame(last, tls.getServer(), () -> "Add assertion message");
  }

  @Test
  public void testGetServer_None() throws Exception {
    tls = new TabletLocationState(keyExtent, null, null, null, null, walogs, true);
    assertNull(tls.getServer(), () -> "Add assertion message");
  }

  @Test
  public void testGetState_Unassigned1() throws Exception {
    tls = new TabletLocationState(keyExtent, null, null, null, null, walogs, true);
    assertEquals(TabletState.UNASSIGNED, tls.getState(null), () -> "Add assertion message");
  }

  @Test
  public void testGetState_Unassigned2() throws Exception {
    last = createMock(TServerInstance.class);
    tls = new TabletLocationState(keyExtent, null, null, last, null, walogs, true);
    assertEquals(TabletState.UNASSIGNED, tls.getState(null), () -> "Add assertion message");
  }

  @Test
  public void testGetState_Assigned() throws Exception {
    last = createMock(TServerInstance.class);
    future = createMock(TServerInstance.class);
    Set<TServerInstance> liveServers = new java.util.HashSet<>();
    liveServers.add(future);
    tls = new TabletLocationState(keyExtent, future, null, last, null, walogs, true);
    assertEquals(TabletState.ASSIGNED, tls.getState(liveServers), () -> "Add assertion message");
  }

  @Test
  public void testGetState_Hosted() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    Set<TServerInstance> liveServers = new java.util.HashSet<>();
    liveServers.add(current);
    tls = new TabletLocationState(keyExtent, null, current, last, null, walogs, true);
    assertEquals(TabletState.HOSTED, tls.getState(liveServers), () -> "Add assertion message");
  }

  @Test
  public void testGetState_Dead1() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    future = createMock(TServerInstance.class);
    Set<TServerInstance> liveServers = new java.util.HashSet<>();
    liveServers.add(current);
    tls = new TabletLocationState(keyExtent, future, null, last, null, walogs, true);
    assertEquals(TabletState.ASSIGNED_TO_DEAD_SERVER, tls.getState(liveServers), () -> "Add assertion message");
  }

  @Test
  public void testGetState_Dead2() throws Exception {
    last = createMock(TServerInstance.class);
    current = createMock(TServerInstance.class);
    future = createMock(TServerInstance.class);
    Set<TServerInstance> liveServers = new java.util.HashSet<>();
    liveServers.add(future);
    tls = new TabletLocationState(keyExtent, null, current, last, null, walogs, true);
    assertEquals(TabletState.ASSIGNED_TO_DEAD_SERVER, tls.getState(liveServers), () -> "Add assertion message");
  }
}