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
package org.apache.accumulo.server.replication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.IteratorSetting.Column;
import org.apache.accumulo.core.client.impl.BaseIteratorEnvironment;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.iterators.Combiner;
import org.apache.accumulo.core.iterators.DevNull;
import org.apache.accumulo.core.iterators.IteratorUtil.IteratorScope;
import org.apache.accumulo.core.replication.ReplicationSchema.StatusSection;
import org.apache.accumulo.server.replication.proto.Replication.Status;
import org.junit.Before;
import org.junit.Test;

public class StatusCombinerTest {

  private StatusCombiner combiner;
  private Key key;
  private Status.Builder builder;

  private static class TestIE extends BaseIteratorEnvironment {
    @Override
    public IteratorScope getIteratorScope() {
      return IteratorScope.scan;
    }
  }

  @Before
  public void initCombiner() throws IOException {
    // Removed line due to General Fixture smell: key = new Key();
    combiner = new StatusCombiner();
    // Removed line due to General Fixture smell: builder = Status.newBuilder();
    IteratorSetting cfg = new IteratorSetting(50, StatusCombiner.class);
    Combiner.setColumns(cfg, Collections.singletonList(new Column(StatusSection.NAME)));
    combiner.init(new DevNull(), cfg.getOptions(), new TestIE());
  }

  @Test
  public void returnsSameObject() {
    key = new Key();
    Status status = StatusUtil.ingestedUntil(10);
    // When combining only one message, we should get back the same instance
    Status ret = combiner.typedReduce(key, Collections.singleton(status).iterator());
    assertEquals(status, ret, () -> "Add assertion message");
    assertTrue(status == ret, () -> "Add assertion message");
  }

  @Test
  public void newStatusWithNewIngest() {
    key = new Key();
    Status orig = StatusUtil.fileCreated(100);
    Status status = StatusUtil.replicatedAndIngested(10, 20);
    Status ret = combiner.typedReduce(key, Arrays.asList(orig, status).iterator());
    assertEquals(10l, ret.getBegin(), () -> "Add assertion message");
    assertEquals(20l, ret.getEnd(), () -> "Add assertion message");
    assertEquals(100l, ret.getCreatedTime(), () -> "Add assertion message");
    assertEquals(false, ret.getClosed(), () -> "Add assertion message");
  }

  @Test
  public void newStatusWithNewIngestSingleBuilder() {
    key = new Key();
    builder = Status.newBuilder();
    Status orig = StatusUtil.fileCreated(100);
    Status status = StatusUtil.replicatedAndIngested(builder, 10, 20);
    Status ret = combiner.typedReduce(key, Arrays.asList(orig, status).iterator());
    assertEquals(10l, ret.getBegin(), () -> "Add assertion message");
    assertEquals(20l, ret.getEnd(), () -> "Add assertion message");
    assertEquals(100l, ret.getCreatedTime(), () -> "Add assertion message");
    assertEquals(false, ret.getClosed(), () -> "Add assertion message");
  }

  @Test
  public void commutativeNewFile() {
    key = new Key();
    Status newFile = StatusUtil.fileCreated(100), firstSync = StatusUtil.ingestedUntil(100),
        secondSync = StatusUtil.ingestedUntil(200);

    Status order1 =
        combiner.typedReduce(key, Arrays.asList(newFile, firstSync, secondSync).iterator()),
        order2 =
            combiner.typedReduce(key, Arrays.asList(secondSync, firstSync, newFile).iterator());

    assertEquals(order1, order2, () -> "Add assertion message");
  }

  @Test
  public void commutativeNewFileSingleBuilder() {
    key = new Key();
    builder = Status.newBuilder();
    Status newFile = StatusUtil.fileCreated(100),
        firstSync = StatusUtil.ingestedUntil(builder, 100),
        secondSync = StatusUtil.ingestedUntil(builder, 200);

    Status order1 =
        combiner.typedReduce(key, Arrays.asList(newFile, firstSync, secondSync).iterator()),
        order2 =
            combiner.typedReduce(key, Arrays.asList(secondSync, firstSync, newFile).iterator());

    assertEquals(order1, order2, () -> "Add assertion message");
  }

  @Test
  public void commutativeNewUpdates() {
    key = new Key();
    Status newFile = StatusUtil.fileCreated(100), firstSync = StatusUtil.ingestedUntil(100),
        secondSync = StatusUtil.ingestedUntil(200);

    Status order1 =
        combiner.typedReduce(key, Arrays.asList(newFile, firstSync, secondSync).iterator()),
        order2 =
            combiner.typedReduce(key, Arrays.asList(newFile, secondSync, firstSync).iterator());

    assertEquals(order1, order2, () -> "Add assertion message");
  }

  @Test
  public void commutativeNewUpdatesSingleBuilder() {
    key = new Key();
    builder = Status.newBuilder();
    Status newFile = StatusUtil.fileCreated(100),
        firstSync = StatusUtil.ingestedUntil(builder, 100),
        secondSync = StatusUtil.ingestedUntil(builder, 200);

    Status order1 =
        combiner.typedReduce(key, Arrays.asList(newFile, firstSync, secondSync).iterator()),
        order2 =
            combiner.typedReduce(key, Arrays.asList(newFile, secondSync, firstSync).iterator());

    assertEquals(order1, order2, () -> "Add assertion message");
  }

  @Test
  public void commutativeWithClose() {
    key = new Key();
    Status newFile = StatusUtil.fileCreated(100), closed = StatusUtil.fileClosed(),
        secondSync = StatusUtil.ingestedUntil(200);

    Status order1 =
        combiner.typedReduce(key, Arrays.asList(newFile, closed, secondSync).iterator()),
        order2 = combiner.typedReduce(key, Arrays.asList(newFile, secondSync, closed).iterator());

    assertEquals(order1, order2, () -> "Add assertion message");
  }

  @Test
  public void commutativeWithCloseSingleBuilder() {
    key = new Key();
    builder = Status.newBuilder();
    Status newFile = StatusUtil.fileCreated(100), closed = StatusUtil.fileClosed(),
        secondSync = StatusUtil.ingestedUntil(builder, 200);

    Status order1 =
        combiner.typedReduce(key, Arrays.asList(newFile, closed, secondSync).iterator()),
        order2 = combiner.typedReduce(key, Arrays.asList(newFile, secondSync, closed).iterator());

    assertEquals(order1, order2, () -> "Add assertion message");
  }

  @Test
  public void commutativeWithMultipleUpdates() {
    key = new Key();
    Status newFile = StatusUtil.fileCreated(100), update1 = StatusUtil.ingestedUntil(100),
        update2 = StatusUtil.ingestedUntil(200), repl1 = StatusUtil.replicated(50),
        repl2 = StatusUtil.replicated(150);

    Status order1 = combiner.typedReduce(key,
        Arrays.asList(newFile, update1, repl1, update2, repl2).iterator());

    // Got all replication updates before ingest updates
    Status permutation = combiner.typedReduce(key,
        Arrays.asList(newFile, repl1, update1, repl2, update2).iterator());

    assertEquals(order1, permutation, () -> "Add assertion message");

    // All replications before updates
    permutation = combiner.typedReduce(key,
        Arrays.asList(newFile, repl1, repl2, update1, update2).iterator());

    // Line removed due to Duplicate Assert: assertEquals(order1, permutation, () -> "Add assertion message");

    // All updates before replications
    permutation = combiner.typedReduce(key,
        Arrays.asList(newFile, update1, update2, repl1, repl2, update1, update2).iterator());

    // Line removed due to Duplicate Assert: assertEquals(order1, permutation, () -> "Add assertion message");
  }

  @Test
  public void commutativeWithMultipleUpdatesSingleBuilder() {
    key = new Key();
    builder = Status.newBuilder();
    Status newFile = StatusUtil.fileCreated(100), update1 = StatusUtil.ingestedUntil(builder, 100),
        update2 = StatusUtil.ingestedUntil(builder, 200),
        repl1 = StatusUtil.replicated(builder, 50), repl2 = StatusUtil.replicated(builder, 150);

    Status order1 = combiner.typedReduce(key,
        Arrays.asList(newFile, update1, repl1, update2, repl2).iterator());

    // Got all replication updates before ingest updates
    Status permutation = combiner.typedReduce(key,
        Arrays.asList(newFile, repl1, update1, repl2, update2).iterator());

    assertEquals(order1, permutation, () -> "Add assertion message");

    // All replications before updates
    permutation = combiner.typedReduce(key,
        Arrays.asList(newFile, repl1, repl2, update1, update2).iterator());

    // Line removed due to Duplicate Assert: assertEquals(order1, permutation, () -> "Add assertion message");

    // All updates before replications
    permutation = combiner.typedReduce(key,
        Arrays.asList(newFile, update1, update2, repl1, repl2).iterator());

    // Line removed due to Duplicate Assert: assertEquals(order1, permutation, () -> "Add assertion message");
  }

  @Test
  public void duplicateStatuses() {
    key = new Key();
    builder = Status.newBuilder();
    Status newFile = StatusUtil.fileCreated(100), update1 = StatusUtil.ingestedUntil(builder, 100),
        update2 = StatusUtil.ingestedUntil(builder, 200),
        repl1 = StatusUtil.replicated(builder, 50), repl2 = StatusUtil.replicated(builder, 150);

    Status order1 = combiner.typedReduce(key,
        Arrays.asList(newFile, update1, repl1, update2, repl2).iterator());

    // Repeat the same thing more than once
    Status permutation = combiner.typedReduce(key,
        Arrays.asList(newFile, repl1, update1, update1, repl2, update2, update2).iterator());

    assertEquals(order1, permutation, () -> "Add assertion message");
  }

  @Test
  public void fileClosedTimePropagated() {
    key = new Key();
    Status stat1 = Status.newBuilder().setBegin(10).setEnd(20).setClosed(true).setInfiniteEnd(false)
        .setCreatedTime(50).build();
    Status stat2 =
        Status.newBuilder().setBegin(10).setEnd(20).setClosed(true).setInfiniteEnd(false).build();

    Status combined = combiner.typedReduce(key, Arrays.asList(stat1, stat2).iterator());

    assertEquals(stat1, combined, () -> "Add assertion message");
  }

  @Test
  public void fileClosedTimeChoosesEarliestIgnoringDefault() {
    key = new Key();
    Status stat1 = Status.newBuilder().setBegin(10).setEnd(20).setClosed(true).setInfiniteEnd(false)
        .setCreatedTime(50).build();
    Status stat2 = Status.newBuilder().setBegin(10).setEnd(20).setClosed(true).setInfiniteEnd(false)
        .setCreatedTime(100).build();

    Status combined = combiner.typedReduce(key, Arrays.asList(stat1, stat2).iterator());

    assertEquals(stat1, combined, () -> "Add assertion message");

    Status stat3 = Status.newBuilder().setBegin(10).setEnd(20).setClosed(true).setInfiniteEnd(false)
        .setCreatedTime(100).build();

    Status combined2 = combiner.typedReduce(key, Arrays.asList(combined, stat3).iterator());

    assertEquals(combined, combined2, () -> "Add assertion message");
  }

  @Test
  public void testCombination() {
    List<Status> status = new ArrayList<>();
    long time = System.currentTimeMillis();

    status.add(StatusUtil.fileCreated(time));
    status.add(StatusUtil.openWithUnknownLength());
    status.add(StatusUtil.fileClosed());

    Status combined = combiner.typedReduce(new Key("row"), status.iterator());

    assertEquals(time, combined.getCreatedTime(), () -> "Add assertion message");
    assertTrue(combined.getInfiniteEnd(), () -> "Add assertion message");
    assertTrue(combined.getClosed(), () -> "Add assertion message");
  }
}