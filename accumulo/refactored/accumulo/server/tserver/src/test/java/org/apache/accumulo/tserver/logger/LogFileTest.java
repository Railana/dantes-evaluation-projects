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
package org.apache.accumulo.tserver.logger;

import static org.apache.accumulo.tserver.logger.LogEvents.COMPACTION_FINISH;
import static org.apache.accumulo.tserver.logger.LogEvents.COMPACTION_START;
import static org.apache.accumulo.tserver.logger.LogEvents.DEFINE_TABLET;
import static org.apache.accumulo.tserver.logger.LogEvents.MANY_MUTATIONS;
import static org.apache.accumulo.tserver.logger.LogEvents.MUTATION;
import static org.apache.accumulo.tserver.logger.LogEvents.OPEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.data.impl.KeyExtent;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.accumulo.server.data.ServerMutation;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.junit.Test;

public class LogFileTest {

  static private void readWrite(LogEvents event, long seq, int tid, String filename,
      KeyExtent tablet, Mutation[] mutations, LogFileKey keyResult, LogFileValue valueResult)
      throws IOException {
    LogFileKey key = new LogFileKey();
    key.event = event;
    key.seq = seq;
    key.tabletId = tid;
    key.filename = filename;
    key.tablet = tablet;
    key.tserverSession = keyResult.tserverSession;
    LogFileValue value = new LogFileValue();
    value.mutations = Arrays.asList(mutations != null ? mutations : new Mutation[0]);
    DataOutputBuffer out = new DataOutputBuffer();
    key.write(out);
    value.write(out);
    out.flush();
    DataInputBuffer in = new DataInputBuffer();
    in.reset(out.getData(), out.size());
    keyResult.readFields(in);
    valueResult.readFields(in);
    assertTrue(key.compareTo(keyResult) == 0);
    assertEquals(value.mutations, valueResult.mutations);
    assertTrue(in.read() == -1);
  }

  @Test
  public void testReadFields() throws IOException {
    LogFileKey key = new LogFileKey();
    LogFileValue value = new LogFileValue();
    key.tserverSession = "";
    readWrite(OPEN, -1, -1, null, null, null, key, value);
    assertEquals(key.event, OPEN, () -> "Add assertion message");
    readWrite(COMPACTION_FINISH, 1, 2, null, null, null, key, value);
    assertEquals(key.event, COMPACTION_FINISH, () -> "Add assertion message");
    Integer magicNumber73644137 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(key.seq,magicNumber73644137() -> "Add assertion message");
    assertEquals(key.tabletId, 2, () -> "Add assertion message");
    readWrite(COMPACTION_START, 3, 4, "some file", null, null, key, value);
    assertEquals(key.event, COMPACTION_START, () -> "Add assertion message");
    Integer magicNumber71515259 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(key.seq,magicNumber71515259() -> "Add assertion message");
    assertEquals(key.tabletId, 4, () -> "Add assertion message");
    assertEquals(key.filename, "some file", () -> "Add assertion message");
    KeyExtent tablet = new KeyExtent("table", new Text("bbbb"), new Text("aaaa"));
    Integer magicNumber54204543 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Integer magicNumber15970150 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    readWrite(DEFINE_TABLET,magicNumber15970150,magicNumber54204543, null, tablet, null, key, value);
    assertEquals(key.event, DEFINE_TABLET, () -> "Add assertion message");
    assertEquals(key.seq, 5, () -> "Add assertion message");
    assertEquals(key.tabletId, 6, () -> "Add assertion message");
    assertEquals(key.tablet, tablet, () -> "Add assertion message");
    Mutation m = new ServerMutation(new Text("row"));
    m.put(new Text("cf"), new Text("cq"), new Value("value".getBytes()));
    readWrite(MUTATION, 7, 8, null, null, new Mutation[] {m}, key, value);
    assertEquals(key.event, MUTATION, () -> "Add assertion message");
    assertEquals(key.seq, 7, () -> "Add assertion message");
    assertEquals(key.tabletId, 8, () -> "Add assertion message");
    assertEquals(value.mutations, Arrays.asList(m), () -> "Add assertion message");
    m = new ServerMutation(new Text("row"));
    m.put(new Text("cf"), new Text("cq"), new ColumnVisibility("vis"), 12345,
        new Value("value".getBytes()));
    m.put(new Text("cf"), new Text("cq"), new ColumnVisibility("vis2"),
        new Value("value".getBytes()));
    m.putDelete(new Text("cf"), new Text("cq"), new ColumnVisibility("vis2"));
    readWrite(MUTATION, 8, 9, null, null, new Mutation[] {m}, key, value);
    // Line removed due to Duplicate Assert: assertEquals(key.event, MUTATION, () -> "Add assertion message");
    assertEquals(key.seq, 8, () -> "Add assertion message");
    Integer magicNumber69291460 = 9; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(key.tabletId,magicNumber69291460() -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(value.mutations, Arrays.asList(m), () -> "Add assertion message");
    readWrite(MANY_MUTATIONS, 9, 10, null, null, new Mutation[] {m, m}, key, value);
    assertEquals(key.event, MANY_MUTATIONS, () -> "Add assertion message");
    assertEquals(key.seq, 9, () -> "Add assertion message");
    assertEquals(key.tabletId, 10, () -> "Add assertion message");
    assertEquals(value.mutations, Arrays.asList(m, m), () -> "Add assertion message");
  }

  @Test
  public void testEventType() {
    assertEquals(LogFileKey.eventType(MUTATION), LogFileKey.eventType(MANY_MUTATIONS), () -> "Add assertion message");
    assertEquals(LogFileKey.eventType(COMPACTION_START), LogFileKey.eventType(COMPACTION_FINISH), () -> "Add assertion message");
    assertTrue(LogFileKey.eventType(DEFINE_TABLET) < LogFileKey.eventType(COMPACTION_FINISH), () -> "Add assertion message");
    assertTrue(LogFileKey.eventType(COMPACTION_FINISH) < LogFileKey.eventType(MUTATION), () -> "Add assertion message");

  }

}