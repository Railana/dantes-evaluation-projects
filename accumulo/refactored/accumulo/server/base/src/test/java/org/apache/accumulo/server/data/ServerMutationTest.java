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
package org.apache.accumulo.server.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.accumulo.core.data.ColumnUpdate;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.CachedConfiguration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Test;

public class ServerMutationTest {

  @Test
  public void test() throws Exception {
    ServerMutation m = new ServerMutation(new Text("r1"));
    m.put(new Text("cf1"), new Text("cq1"), new Value("v1".getBytes()));
    m.put(new Text("cf2"), new Text("cq2"), 56, new Value("v2".getBytes()));
    m.setSystemTimestamp(42);

    List<ColumnUpdate> updates = m.getUpdates();

    Integer magicNumber96997800 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber96997800, updates.size(), () -> "Add assertion message");

    assertEquals("r1", new String(m.getRow()), () -> "Add assertion message");
    ColumnUpdate cu = updates.get(0);

    assertEquals("cf1", new String(cu.getColumnFamily()), () -> "Add assertion message");
    assertEquals("cq1", new String(cu.getColumnQualifier()), () -> "Add assertion message");
    assertEquals("", new String(cu.getColumnVisibility()), () -> "Add assertion message");
    assertFalse(cu.hasTimestamp(), () -> "Add assertion message");
    assertEquals(42l, cu.getTimestamp(), () -> "Add assertion message");

    ServerMutation m2 = new ServerMutation();
    ReflectionUtils.copy(CachedConfiguration.getInstance(), m, m2);

    updates = m2.getUpdates();

    // Line removed due to Duplicate Assert: assertEquals(2, updates.size(), () -> "Add assertion message");
    assertEquals("r1", new String(m2.getRow()), () -> "Add assertion message");

    cu = updates.get(0);
    // Line removed due to Duplicate Assert: assertEquals("cf1", new String(cu.getColumnFamily()), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("cq1", new String(cu.getColumnQualifier()), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(cu.hasTimestamp(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(42l, cu.getTimestamp(), () -> "Add assertion message");

    cu = updates.get(1);

    // Line removed due to Duplicate Assert: assertEquals("r1", new String(m2.getRow()), () -> "Add assertion message");
    assertEquals("cf2", new String(cu.getColumnFamily()), () -> "Add assertion message");
    assertEquals("cq2", new String(cu.getColumnQualifier()), () -> "Add assertion message");
    assertTrue(cu.hasTimestamp(), () -> "Add assertion message");
    assertEquals(56, cu.getTimestamp(), () -> "Add assertion message");

  }

}