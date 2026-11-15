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
package org.apache.accumulo.core.file.rfile;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.accumulo.core.data.ArrayByteSequence;
import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.LocalityGroupUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.util.concurrent.AtomicLongMap;

/**
 *
 */
public class RFileMetricsTest {

  @Rule
  public TemporaryFolder tempFolder =
      new TemporaryFolder(new File(System.getProperty("user.dir") + "/target"));

  static {
    Logger.getLogger(org.apache.hadoop.io.compress.CodecPool.class).setLevel(Level.OFF);
    Logger.getLogger(org.apache.hadoop.util.NativeCodeLoader.class).setLevel(Level.OFF);
  }

  private TestRFile trf = null;

  @Before
  public void makeTestRFile() {
    trf = new TestRFile();
  }

  @After
  public void cleanUpTestRFile() {
    // do our best to clean up first
    if (trf != null) {
      if (trf.writer != null) {
        try {
          trf.closeWriter();
        } catch (IOException e) {
          // ignore
        }
      }
      if (trf.reader != null) {
        try {
          trf.closeReader();
        } catch (IOException e) {
          // ignore
        }
      }
    }
    trf = null;
  }

  public static class TestRFile extends RFileTest.TestRFile {

    public TestRFile() {
      super(null);
    }

    public VisMetricsGatherer gatherMetrics() throws IOException {
      VisMetricsGatherer vmg = new VisMetricsGatherer();
      reader.registerMetrics(vmg);
      Map<String,ArrayList<ByteSequence>> localityGroupCF = reader.getLocalityGroupCF();

      for (String lgName : localityGroupCF.keySet()) {
        LocalityGroupUtil.seek(reader, new Range(), lgName, localityGroupCF);
        while (reader.hasTop()) {
          reader.next();
        }
      }
      return vmg;
    }
  }

  @Test
  public void emptyFile() throws IOException {
    // test an empty file

    trf.openWriter();
    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    Map<String,AtomicLongMap<String>> metrics = vmg.metric;
    Map<String,AtomicLongMap<String>> blocks = vmg.blocks;
    Integer magicNumber22012290 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber22012290, metrics.size(), () -> "Add assertion message");

    assertEquals(0, blocks.size(), () -> "Add assertion message");

    trf.closeReader();
  }

  @Test
  public void oneEntryDefaultLocGroup() throws IOException {
    // test an rfile with one entry in the default locality group

    trf.openWriter();
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get(null);
    AtomicLongMap<String> blocks = vmg.blocks.get(null);
    assertEquals(1, metrics.get("L1"), () -> "Add assertion message");

    assertEquals(1, blocks.get("L1"), () -> "Add assertion message");

    Integer magicNumber21910209 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber21910209, vmg.numEntries.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");

    trf.closeReader();
  }

  @Test
  public void twoEntriesDefaultLocGroup() throws IOException {
    // test an rfile with two entries in the default locality group

    trf.openWriter();
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L2", 55), RFileTest.newValue("foo"));
    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get(null);
    AtomicLongMap<String> blocks = vmg.blocks.get(null);
    assertEquals(1, metrics.get("L1"), () -> "Add assertion message");
    Integer magicNumber99048523 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber99048523, metrics.get("L2"), () -> "Add assertion message");

    Integer magicNumber78417805 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber78417805, blocks.get("L1"), () -> "Add assertion message");
    assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(2, vmg.numEntries.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void oneEntryNonDefaultLocGroup() throws IOException {
    // test an rfile with two entries in a non-default locality group

    trf.openWriter(false);
    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence("cf1"));

    trf.writer.startNewLocalityGroup("lg1", lg1);
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get("lg1");
    AtomicLongMap<String> blocks = vmg.blocks.get("lg1");
    Integer magicNumber59219897 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber59219897, metrics.get("L1"), () -> "Add assertion message");

    assertEquals(1, blocks.get("L1"), () -> "Add assertion message");

    assertEquals(1, vmg.numEntries.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void twoEntryNonDefaultLocGroup() throws IOException {
    // test an rfile with two entries in a non-default locality group

    trf.openWriter(false);
    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence("cf1"));

    trf.writer.startNewLocalityGroup("lg1", lg1);
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L2", 55), RFileTest.newValue("foo"));
    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get("lg1");
    AtomicLongMap<String> blocks = vmg.blocks.get("lg1");
    assertEquals(1, metrics.get("L1"), () -> "Add assertion message");
    Integer magicNumber31551694 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber31551694, metrics.get("L2"), () -> "Add assertion message");

    assertEquals(1, blocks.get("L1"), () -> "Add assertion message");
    assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(2, vmg.numEntries.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void twoNonDefaultLocGroups() throws IOException {
    // test an rfile with two entries in 2 non-default locality groups

    trf.openWriter(false);
    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence("cf1"));

    trf.writer.startNewLocalityGroup("lg1", lg1);
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L2", 55), RFileTest.newValue("foo"));

    Set<ByteSequence> lg2 = new HashSet<>();
    lg2.add(new ArrayByteSequence("cf2"));

    trf.writer.startNewLocalityGroup("lg2", lg2);
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "L2", 55), RFileTest.newValue("foo"));

    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get("lg1");
    AtomicLongMap<String> blocks = vmg.blocks.get("lg1");
    Integer magicNumber72786380 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber72786380, metrics.get("L1"), () -> "Add assertion message");
    assertEquals(1, metrics.get("L2"), () -> "Add assertion message");

    assertEquals(1, blocks.get("L1"), () -> "Add assertion message");
    assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(2, vmg.numEntries.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");

    metrics = vmg.metric.get("lg2");
    blocks = vmg.blocks.get("lg2");
    Integer magicNumber32479439 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    // Line removed due to Duplicate Assert: assertEquals(magicNumber32479439, metrics.get("L1"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, metrics.get("L2"), () -> "Add assertion message");

    // Line removed due to Duplicate Assert: assertEquals(1, blocks.get("L1"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(2, vmg.numEntries.get(vmg.localityGroups.indexOf("lg2")).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg2")).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void nonDefaultAndDefaultLocGroup() throws IOException {
    // test an rfile with 3 entries in a non-default locality group and the default locality group

    trf.openWriter(false);
    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence("cf1"));

    trf.writer.startNewLocalityGroup("lg1", lg1);
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq2", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq2", "L2", 55), RFileTest.newValue("foo"));

    trf.writer.startDefaultLocalityGroup();
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "A", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "B", 55), RFileTest.newValue("foo"));

    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get("lg1");
    AtomicLongMap<String> blocks = vmg.blocks.get("lg1");
    assertEquals(2, metrics.get("L1"), () -> "Add assertion message");
    assertEquals(1, metrics.get("L2"), () -> "Add assertion message");

    Integer magicNumber40987924 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber40987924, blocks.get("L1"), () -> "Add assertion message");
    assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    Integer magicNumber76686515 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber76686515, vmg.numEntries.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");
    Integer magicNumber60086847 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber60086847, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");

    metrics = vmg.metric.get(null);
    blocks = vmg.blocks.get(null);
    assertEquals(1, metrics.get("A"), () -> "Add assertion message");
    assertEquals(1, metrics.get("B"), () -> "Add assertion message");

    assertEquals(1, blocks.get("A"), () -> "Add assertion message");
    assertEquals(1, blocks.get("B"), () -> "Add assertion message");

    assertEquals(2, vmg.numEntries.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void multiCFNonDefaultAndDefaultLocGroup() throws IOException {
    // test an rfile with multiple column families in a non-default locality group and the default
    // locality group

    trf.openWriter(false);
    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence("cf1"));
    lg1.add(new ArrayByteSequence("cf3"));

    trf.writer.startNewLocalityGroup("lg1", lg1);
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq2", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq2", "L2", 55), RFileTest.newValue("foo"));

    trf.writer.startDefaultLocalityGroup();
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "A", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "B", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf4", "cq1", "A", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf4", "cq1", "B", 55), RFileTest.newValue("foo"));

    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get("lg1");
    AtomicLongMap<String> blocks = vmg.blocks.get("lg1");
    Integer magicNumber22126838 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber22126838, metrics.get("L1"), () -> "Add assertion message");
    assertEquals(1, metrics.get("L2"), () -> "Add assertion message");

    Integer magicNumber99037225 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber99037225, blocks.get("L1"), () -> "Add assertion message");
    Integer magicNumber78034873 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber78034873, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(4, vmg.numEntries.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");

    metrics = vmg.metric.get(null);
    blocks = vmg.blocks.get(null);
    assertEquals(2, metrics.get("A"), () -> "Add assertion message");
    assertEquals(2, metrics.get("B"), () -> "Add assertion message");

    assertEquals(1, blocks.get("A"), () -> "Add assertion message");
    assertEquals(1, blocks.get("B"), () -> "Add assertion message");

    assertEquals(4, vmg.numEntries.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");
    assertEquals(1, vmg.numBlocks.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void multiBlockDefaultLocGroup() throws IOException {
    // test an rfile with four blocks in the default locality group

    Integer magicNumber57231067 = 20; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    trf.openWriter(magicNumber57231067
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq2", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq2", "L2", 55), RFileTest.newValue("foo"));
    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get(null);
    AtomicLongMap<String> blocks = vmg.blocks.get(null);
    assertEquals(3, metrics.get("L1"), () -> "Add assertion message");
    assertEquals(1, metrics.get("L2"), () -> "Add assertion message");

    assertEquals(3, blocks.get("L1"), () -> "Add assertion message");
    assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(4, vmg.numEntries.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");
    assertEquals(4, vmg.numBlocks.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void multiBlockNonDefaultLocGroup() throws IOException {
    // test an rfile with four blocks in a non-default locality group

    trf.openWriter(false, 20);// Each entry is a block
    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence("cf1"));
    lg1.add(new ArrayByteSequence("cf3"));

    trf.writer.startNewLocalityGroup("lg1", lg1);
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq2", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq2", "L2", 55), RFileTest.newValue("foo"));
    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get("lg1");
    AtomicLongMap<String> blocks = vmg.blocks.get("lg1");
    assertEquals(3, metrics.get("L1"), () -> "Add assertion message");
    assertEquals(1, metrics.get("L2"), () -> "Add assertion message");

    assertEquals(3, blocks.get("L1"), () -> "Add assertion message");
    assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(4, vmg.numEntries.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");
    assertEquals(4, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");

    trf.closeReader();

  }

  @Test
  public void multiBlockMultiCFNonDefaultAndDefaultLocGroup() throws IOException {
    // test an rfile with multiple column families and multiple blocks in a non-default locality
    // group and the default locality group

    trf.openWriter(false, 10);// Each entry is a block
    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence("cf1"));
    lg1.add(new ArrayByteSequence("cf3"));

    trf.writer.startNewLocalityGroup("lg1", lg1);
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf1", "cq2", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq1", "L1", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf3", "cq2", "L2", 55), RFileTest.newValue("foo"));

    trf.writer.startDefaultLocalityGroup();
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "A", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf2", "cq1", "B", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf4", "cq1", "A", 55), RFileTest.newValue("foo"));
    trf.writer.append(RFileTest.newKey("r1", "cf4", "cq1", "B", 55), RFileTest.newValue("foo"));

    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    AtomicLongMap<String> metrics = vmg.metric.get("lg1");
    AtomicLongMap<String> blocks = vmg.blocks.get("lg1");
    Integer magicNumber58064890 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber58064890, metrics.get("L1"), () -> "Add assertion message");
    Integer magicNumber49449041 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber49449041, metrics.get("L2"), () -> "Add assertion message");

    assertEquals(3, blocks.get("L1"), () -> "Add assertion message");
    assertEquals(1, blocks.get("L2"), () -> "Add assertion message");

    assertEquals(4, vmg.numEntries.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");
    assertEquals(4, vmg.numBlocks.get(vmg.localityGroups.indexOf("lg1")).longValue(), () -> "Add assertion message");

    metrics = vmg.metric.get(null);
    blocks = vmg.blocks.get(null);
    assertEquals(2, metrics.get("A"), () -> "Add assertion message");
    assertEquals(2, metrics.get("B"), () -> "Add assertion message");

    assertEquals(2, blocks.get("A"), () -> "Add assertion message");
    assertEquals(2, blocks.get("B"), () -> "Add assertion message");

    assertEquals(4, vmg.numEntries.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");
    assertEquals(4, vmg.numBlocks.get(vmg.localityGroups.indexOf(null)).longValue(), () -> "Add assertion message");

    trf.closeReader();
  }

  @Test
  public void testManyFamiliesInDefaultLocGroup() throws IOException {
    trf.openWriter(false, 1024);

    String fam1 = String.format("%06x", 9000);
    String fam2 = String.format("%06x", 9001);

    Set<ByteSequence> lg1 = new HashSet<>();
    lg1.add(new ArrayByteSequence(fam1));
    lg1.add(new ArrayByteSequence(fam2));

    trf.writer.startNewLocalityGroup("lg1", lg1);

    for (int row = 0; row < 1100; row++) {
      String rs = String.format("%06x", row);
      trf.writer.append(new Key(rs, fam1, "q4", "A", 42l), new Value("v".getBytes()));
      trf.writer.append(new Key(rs, fam2, "q4", "A|B", 42l), new Value("v".getBytes()));
    }

    trf.writer.startDefaultLocalityGroup();

    String vis[] = new String[] {"A", "A&B", "A|C", "B&C", "Boo"};

    int fam = 0;
    for (int row = 0; row < 1000; row++) {
      String rs = String.format("%06x", row);
      for (int v = 0; v < 5; v++) {
        String fs = String.format("%06x", fam++);
        trf.writer.append(new Key(rs, fs, "q4", vis[v], 42l), new Value("v".getBytes()));
      }
    }

    trf.closeWriter();

    trf.openReader(false);

    VisMetricsGatherer vmg = trf.gatherMetrics();

    Map<String,Long> expected = new HashMap<>();
    Map<String,Long> expectedBlocks = new HashMap<>();
    for (String v : vis) {
      expected.put(v, 1000l);
      expectedBlocks.put(v, 71l);
    }
    assertEquals(expected, vmg.metric.get(null).asMap(), () -> "Add assertion message");
    assertEquals(expectedBlocks, vmg.blocks.get(null).asMap(), () -> "Add assertion message");

    expected.clear();
    expectedBlocks.clear();
    expected.put("A", 1100l);
    expected.put("A|B", 1100l);
    expectedBlocks.put("A", 32l);
    expectedBlocks.put("A|B", 32l);
    assertEquals(expected, vmg.metric.get("lg1").asMap(), () -> "Add assertion message");
    assertEquals(expectedBlocks, vmg.blocks.get("lg1").asMap(), () -> "Add assertion message");

    assertEquals(2, vmg.metric.keySet().size(), () -> "Add assertion message");
    assertEquals(2, vmg.blocks.keySet().size(), () -> "Add assertion message");
  }
}