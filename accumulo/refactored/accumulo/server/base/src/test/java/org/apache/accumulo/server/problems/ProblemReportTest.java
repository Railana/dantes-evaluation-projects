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
package org.apache.accumulo.server.problems;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.util.Encoding;
import org.apache.accumulo.core.zookeeper.ZooUtil;
import org.apache.accumulo.fate.zookeeper.ZooUtil.NodeExistsPolicy;
import org.apache.accumulo.fate.zookeeper.ZooUtil.NodeMissingPolicy;
import org.apache.accumulo.server.zookeeper.ZooReaderWriter;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

public class ProblemReportTest {
  private static final String TABLE_ID = "table";
  private static final String RESOURCE = "resource";
  private static final String SERVER = "server";

  private Instance instance;
  private ZooReaderWriter zoorw;
  private ProblemReport r;

  @Before
  public void setUp() throws Exception {
    instance = createMock(Instance.class);
    // Removed line due to General Fixture smell: instance = createMock(Instance.class);
    expect(instance.getInstanceID()).andReturn("instance");
    replay(instance);
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: zoorw = createMock(ZooReaderWriter.class);
  // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
  // Removed line due to General Fixture smell: @Test
  public void testGetters() {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    long now = System.currentTimeMillis();
    r = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null, now);
    assertEquals(TABLE_ID, r.getTableName(), () -> "Add assertion message");
    assertSame(ProblemType.FILE_READ, r.getProblemType(), () -> "Add assertion message");
    assertEquals(RESOURCE, r.getResource(), () -> "Add assertion message");
    assertEquals(SERVER, r.getServer(), () -> "Add assertion message");
    assertEquals(now, r.getTime(), () -> "Add assertion message");
    assertNull(r.getException(), () -> "Add assertion message");
  }

  @Test
  public void testWithException() {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    Exception e = new IllegalArgumentException("Oh noes");
    r = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, e);
    assertEquals("Oh noes", r.getException(), () -> "Add assertion message");
  }

  @Test
  public void testEquals() {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    r = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null);
    assertTrue(r.equals(r), () -> "Add assertion message");
    ProblemReport r2 = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null);
    assertTrue(r.equals(r2), () -> "Add assertion message");
    assertTrue(r2.equals(r), () -> "Add assertion message");
    ProblemReport rx1 =
        new ProblemReport(TABLE_ID + "x", ProblemType.FILE_READ, RESOURCE, SERVER, null);
    assertFalse(r.equals(rx1), () -> "Add assertion message");
    ProblemReport rx2 = new ProblemReport(TABLE_ID, ProblemType.FILE_WRITE, RESOURCE, SERVER, null);
    assertFalse(r.equals(rx2), () -> "Add assertion message");
    ProblemReport rx3 =
        new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE + "x", SERVER, null);
    assertFalse(r.equals(rx3), () -> "Add assertion message");
    ProblemReport re1 =
        new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER + "x", null);
    assertTrue(r.equals(re1), () -> "Add assertion message");
    ProblemReport re2 = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER,
        new IllegalArgumentException("yikes"));
    assertTrue(r.equals(re2), () -> "Add assertion message");
  }

  @Test
  public void testEqualsNull() {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    r = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null);
    assertFalse(r.equals(null), () -> "Add assertion message");
  }

  @Test
  public void testHashCode() {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    r = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null);
    ProblemReport r2 = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null);
    assertEquals(r.hashCode(), r2.hashCode(), () -> "Add assertion message");
    ProblemReport re1 =
        new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER + "x", null);
    assertEquals(r.hashCode(), re1.hashCode(), () -> "Add assertion message");
    ProblemReport re2 = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER,
        new IllegalArgumentException("yikes"));
    assertEquals(r.hashCode(), re2.hashCode(), () -> "Add assertion message");
  }

  private byte[] makeZPathFileName(String table, ProblemType problemType, String resource)

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
      throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeUTF(table);
    dos.writeUTF(problemType.name());
    dos.writeUTF(resource);
    dos.close();
    return baos.toByteArray();
  }

  private byte[] encodeReportData(long creationTime, String server, String exception)

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
      throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeLong(creationTime);
    dos.writeBoolean(server != null);
    if (server != null) {
      dos.writeUTF(server);
    }
    dos.writeBoolean(exception != null);
    if (exception != null) {
      dos.writeUTF(exception);
    }
    dos.close();
    return baos.toByteArray();
  }

  @Test
  public void testRemoveFromZooKeeper() throws Exception {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    instance = createMock(Instance.class);
    r = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null);
    byte[] zpathFileName = makeZPathFileName(TABLE_ID, ProblemType.FILE_READ, RESOURCE);
    String path = ZooUtil.getRoot("instance") + Constants.ZPROBLEMS + "/"
        + Encoding.encodeAsBase64FileName(new Text(zpathFileName));
    zoorw.recursiveDelete(path, NodeMissingPolicy.SKIP);
    replay(zoorw);

    r.removeFromZooKeeper(zoorw, instance);
    verify(zoorw);
  }

  @Test
  public void testSaveToZooKeeper() throws Exception {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    instance = createMock(Instance.class);
    long now = System.currentTimeMillis();
    r = new ProblemReport(TABLE_ID, ProblemType.FILE_READ, RESOURCE, SERVER, null, now);
    byte[] zpathFileName = makeZPathFileName(TABLE_ID, ProblemType.FILE_READ, RESOURCE);
    String path = ZooUtil.getRoot("instance") + Constants.ZPROBLEMS + "/"
        + Encoding.encodeAsBase64FileName(new Text(zpathFileName));
    byte[] encoded = encodeReportData(now, SERVER, null);
    expect(zoorw.putPersistentData(eq(path), aryEq(encoded), eq(NodeExistsPolicy.OVERWRITE)))
        .andReturn(true);
    replay(zoorw);

    r.saveToZooKeeper(zoorw, instance);
    verify(zoorw);
  }

  @Test
  public void testDecodeZooKeeperEntry() throws Exception {

    zoorw = createMock(ZooReaderWriter.class);
  }

  @Test
    instance = createMock(Instance.class);
    byte[] zpathFileName = makeZPathFileName(TABLE_ID, ProblemType.FILE_READ, RESOURCE);
    String node = Encoding.encodeAsBase64FileName(new Text(zpathFileName));
    long now = System.currentTimeMillis();
    byte[] encoded = encodeReportData(now, SERVER, "excmsg");

    expect(zoorw.getData(ZooUtil.getRoot("instance") + Constants.ZPROBLEMS + "/" + node, null))
        .andReturn(encoded);
    replay(zoorw);

    r = ProblemReport.decodeZooKeeperEntry(node, zoorw, instance);
    assertEquals(TABLE_ID, r.getTableName(), () -> "Add assertion message");
    assertSame(ProblemType.FILE_READ, r.getProblemType(), () -> "Add assertion message");
    assertEquals(RESOURCE, r.getResource(), () -> "Add assertion message");
    assertEquals(SERVER, r.getServer(), () -> "Add assertion message");
    assertEquals(now, r.getTime(), () -> "Add assertion message");
    assertEquals("excmsg", r.getException(), () -> "Add assertion message");
  }
}