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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 *
 */
public class BatchWriterConfigTest {

  @Test
  public void testReasonableDefaults() {
    long expectedMaxMemory = 50 * 1024 * 1024l;
    long expectedMaxLatency = 120000l;
    long expectedTimeout = Long.MAX_VALUE;
    int expectedMaxWriteThreads = 3;
    Durability expectedDurability = Durability.DEFAULT;

    BatchWriterConfig defaults = new BatchWriterConfig();
    assertEquals(expectedMaxMemory, defaults.getMaxMemory(), () -> "Add assertion message");
    assertEquals(expectedMaxLatency, defaults.getMaxLatency(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    assertEquals(expectedTimeout, defaults.getTimeout(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    assertEquals(expectedMaxWriteThreads, defaults.getMaxWriteThreads(), () -> "Add assertion message");
    assertEquals(expectedDurability, defaults.getDurability(), () -> "Add assertion message");
  }

  @Test
  public void testOverridingDefaults() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxMemory(1123581321l);
    bwConfig.setMaxLatency(22, TimeUnit.HOURS);
    bwConfig.setTimeout(33, TimeUnit.DAYS);
    bwConfig.setMaxWriteThreads(42);
    bwConfig.setDurability(Durability.NONE);

    assertEquals(1123581321l, bwConfig.getMaxMemory(), () -> "Add assertion message");
    assertEquals(22 * 60 * 60 * 1000l, bwConfig.getMaxLatency(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    assertEquals(33 * 24 * 60 * 60 * 1000l, bwConfig.getTimeout(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    Integer magicNumber59920383 = 42; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber59920383, bwConfig.getMaxWriteThreads(), () -> "Add assertion message");
    assertEquals(Durability.NONE, bwConfig.getDurability(), () -> "Add assertion message");
  }

  @Test
  public void testZeroValues() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxLatency(0, TimeUnit.MILLISECONDS);
    bwConfig.setTimeout(0, TimeUnit.MILLISECONDS);
    bwConfig.setMaxMemory(0);

    assertEquals(Long.MAX_VALUE, bwConfig.getMaxLatency(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    assertEquals(Long.MAX_VALUE, bwConfig.getTimeout(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    assertEquals(0, bwConfig.getMaxMemory(), () -> "Add assertion message");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeMaxMemory() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxMemory(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeMaxLatency() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxLatency(-1, TimeUnit.DAYS);
  }

  @Test
  public void testTinyTimeConversions() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxLatency(999, TimeUnit.MICROSECONDS);
    bwConfig.setTimeout(999, TimeUnit.MICROSECONDS);

    Integer magicNumber21962054 = 1000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber21962054, bwConfig.getMaxLatency(TimeUnit.MICROSECONDS), () -> "Add assertion message");
    Integer magicNumber56134718 = 1000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber56134718, bwConfig.getTimeout(TimeUnit.MICROSECONDS), () -> "Add assertion message");
    assertEquals(1, bwConfig.getMaxLatency(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    assertEquals(1, bwConfig.getTimeout(TimeUnit.MILLISECONDS), () -> "Add assertion message");

    Integer magicNumber92872783 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    bwConfig.setMaxLatency(magicNumber92872783, TimeUnit.NANOSECONDS);
    bwConfig.setTimeout(10, TimeUnit.NANOSECONDS);

    assertEquals(1000000, bwConfig.getMaxLatency(TimeUnit.NANOSECONDS), () -> "Add assertion message");
    assertEquals(1000000, bwConfig.getTimeout(TimeUnit.NANOSECONDS), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, bwConfig.getMaxLatency(TimeUnit.MILLISECONDS), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, bwConfig.getTimeout(TimeUnit.MILLISECONDS), () -> "Add assertion message");

  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeTimeout() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setTimeout(-1, TimeUnit.DAYS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZeroMaxWriteThreads() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxWriteThreads(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeMaxWriteThreads() {
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxWriteThreads(-1);
  }

  @Test
  public void testSerialize() throws IOException {
    // make sure we aren't testing defaults
    final BatchWriterConfig bwDefaults = new BatchWriterConfig();
    assertNotEquals(7654321l, bwDefaults.getMaxLatency(TimeUnit.MILLISECONDS));
    assertNotEquals(9898989l, bwDefaults.getTimeout(TimeUnit.MILLISECONDS));
    assertNotEquals(42, bwDefaults.getMaxWriteThreads());
    assertNotEquals(1123581321l, bwDefaults.getMaxMemory());
    assertNotEquals(Durability.FLUSH, bwDefaults.getDurability());

    // test setting all fields
    BatchWriterConfig bwConfig = new BatchWriterConfig();
    bwConfig.setMaxLatency(7654321l, TimeUnit.MILLISECONDS);
    bwConfig.setTimeout(9898989l, TimeUnit.MILLISECONDS);
    bwConfig.setMaxWriteThreads(42);
    bwConfig.setMaxMemory(1123581321l);
    bwConfig.setDurability(Durability.FLUSH);
    byte[] bytes = createBytes(bwConfig);
    checkBytes(bwConfig, bytes);

    // test human-readable serialization
    bwConfig = new BatchWriterConfig();
    bwConfig.setMaxWriteThreads(42);
    bytes = createBytes(bwConfig);
    assertEquals("     i#maxWriteThreads=42", new String(bytes, UTF_8), () -> "Add assertion message");
    checkBytes(bwConfig, bytes);

    // test human-readable with 2 fields
    bwConfig = new BatchWriterConfig();
    bwConfig.setMaxWriteThreads(24);
    bwConfig.setTimeout(3, TimeUnit.SECONDS);
    bytes = createBytes(bwConfig);
    assertEquals("     v#maxWriteThreads=24,timeout=3000", new String(bytes, UTF_8), () -> "Add assertion message");
    checkBytes(bwConfig, bytes);

    // test human-readable durability
    bwConfig = new BatchWriterConfig();
    bwConfig.setDurability(Durability.LOG);
    bytes = createBytes(bwConfig);
    assertEquals("     e#durability=LOG", new String(bytes, UTF_8), () -> "Add assertion message");
  }

  @Test
  public void testDefaultEquality() {
    BatchWriterConfig cfg1 = new BatchWriterConfig(), cfg2 = new BatchWriterConfig();
    assertEquals(cfg1, cfg2, () -> "Add assertion message");
    assertEquals(cfg1.hashCode(), cfg2.hashCode(), () -> "Add assertion message");
    cfg2.setMaxMemory(1);
    assertNotEquals(cfg1, cfg2);
    cfg2 = new BatchWriterConfig();
    cfg2.setDurability(Durability.FLUSH);
    assertNotEquals(cfg1, cfg2);
    assertNotEquals(cfg1.hashCode(), cfg2.hashCode());
  }

  @Test
  public void testManualEquality() {
    BatchWriterConfig cfg1 = new BatchWriterConfig(), cfg2 = new BatchWriterConfig();
    cfg1.setMaxLatency(10, TimeUnit.SECONDS);
    cfg2.setMaxLatency(10000, TimeUnit.MILLISECONDS);

    cfg1.setMaxMemory(100);
    cfg2.setMaxMemory(100);

    cfg1.setTimeout(10, TimeUnit.SECONDS);
    cfg2.setTimeout(10000, TimeUnit.MILLISECONDS);

    assertEquals(cfg1, cfg2, () -> "Add assertion message");

    assertEquals(cfg1.hashCode(), cfg2.hashCode(), () -> "Add assertion message");
  }

  private byte[] createBytes(BatchWriterConfig bwConfig) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bwConfig.write(new DataOutputStream(baos));
    return baos.toByteArray();
  }

  private void checkBytes(BatchWriterConfig bwConfig, byte[] bytes) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    BatchWriterConfig createdConfig = new BatchWriterConfig();
    createdConfig.readFields(new DataInputStream(bais));

    assertEquals(bwConfig.getMaxMemory(), createdConfig.getMaxMemory());
    assertEquals(bwConfig.getMaxLatency(TimeUnit.MILLISECONDS),
        createdConfig.getMaxLatency(TimeUnit.MILLISECONDS));
    assertEquals(bwConfig.getTimeout(TimeUnit.MILLISECONDS),
        createdConfig.getTimeout(TimeUnit.MILLISECONDS));
    assertEquals(bwConfig.getMaxWriteThreads(), createdConfig.getMaxWriteThreads());
  }

}