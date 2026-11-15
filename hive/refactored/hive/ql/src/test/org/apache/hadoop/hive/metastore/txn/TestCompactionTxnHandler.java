/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.txn;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.AbortTxnRequest;
import org.apache.hadoop.hive.metastore.api.AddDynamicPartitions;
import org.apache.hadoop.hive.metastore.api.CommitTxnRequest;
import org.apache.hadoop.hive.metastore.api.CompactionRequest;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.DataOperationType;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsResponse;
import org.apache.hadoop.hive.metastore.api.LockComponent;
import org.apache.hadoop.hive.metastore.api.LockLevel;
import org.apache.hadoop.hive.metastore.api.LockRequest;
import org.apache.hadoop.hive.metastore.api.LockResponse;
import org.apache.hadoop.hive.metastore.api.LockState;
import org.apache.hadoop.hive.metastore.api.LockType;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.OpenTxnRequest;
import org.apache.hadoop.hive.metastore.api.OpenTxnsResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactRequest;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponseElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Tests for TxnHandler.
 */
public class TestCompactionTxnHandler {

  private HiveConf conf = new HiveConf();
  private TxnStore txnHandler;

  public TestCompactionTxnHandler() throws Exception {
    TxnDbUtil.setConfValues(conf);
    tearDown();
  }

  @Test
  public void testFindNextToCompact() throws Exception {
    CompactionRequest rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    rqst.setPartitionname("ds=today");
    txnHandler.compact(rqst);
    long now = System.currentTimeMillis();
    CompactionInfo ci = txnHandler.findNextToCompact("fred");
    assertNotNull(ci, () -> "Add assertion message");
    assertEquals("foo", ci.dbname, () -> "Add assertion message");
    assertEquals("bar", ci.tableName, () -> "Add assertion message");
    assertEquals("ds=today", ci.partName, () -> "Add assertion message");
    assertEquals(CompactionType.MINOR, ci.type, () -> "Add assertion message");
    assertNull(ci.runAs, () -> "Add assertion message");
    assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");

    txnHandler.setRunAs(ci.id, "bob");

    ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
    List<ShowCompactResponseElement> compacts = rsp.getCompacts();
    Integer magicNumber74698217 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber74698217, compacts.size(), () -> "Add assertion message");
    ShowCompactResponseElement c = compacts.get(0);
    assertEquals("foo", c.getDbname(), () -> "Add assertion message");
    assertEquals("bar", c.getTablename(), () -> "Add assertion message");
    assertEquals("ds=today", c.getPartitionname(), () -> "Add assertion message");
    assertEquals(CompactionType.MINOR, c.getType(), () -> "Add assertion message");
    assertEquals("working", c.getState(), () -> "Add assertion message");
    assertTrue(c.getStart() - 5000 < now && c.getStart() + 5000 > now, () -> "Add assertion message");
    assertEquals("fred", c.getWorkerid(), () -> "Add assertion message");
    assertEquals("bob", c.getRunAs(), () -> "Add assertion message");
  }

  @Test
  public void testFindNextToCompact2() throws Exception {
    CompactionRequest rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    rqst.setPartitionname("ds=today");
    txnHandler.compact(rqst);

    rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    rqst.setPartitionname("ds=yesterday");
    txnHandler.compact(rqst);

    long now = System.currentTimeMillis();
    boolean expectToday = false;
    CompactionInfo ci = txnHandler.findNextToCompact("fred");
    assertNotNull(ci, () -> "Add assertion message");
    assertEquals("foo", ci.dbname, () -> "Add assertion message");
    assertEquals("bar", ci.tableName, () -> "Add assertion message");
    if ("ds=today".equals(ci.partName)) expectToday = false;
    else if ("ds=yesterday".equals(ci.partName)) expectToday = true;
    else fail("partition name should have been today or yesterday but was " + ci.partName);
    assertEquals(CompactionType.MINOR, ci.type, () -> "Add assertion message");

    ci = txnHandler.findNextToCompact("fred");
    // Line removed due to Duplicate Assert: assertNotNull(ci, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("foo", ci.dbname, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("bar", ci.tableName, () -> "Add assertion message");
    if (expectToday) assertEquals("ds=today", ci.partName, () -> "Add assertion message");
    else assertEquals("ds=yesterday", ci.partName, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(CompactionType.MINOR, ci.type, () -> "Add assertion message");

    assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");

    ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
    List<ShowCompactResponseElement> compacts = rsp.getCompacts();
    assertEquals(2, compacts.size(), () -> "Add assertion message");
    for (ShowCompactResponseElement e : compacts) {
      assertEquals("working", e.getState(), () -> "Add assertion message");
      assertTrue(e.getStart() - 5000 < now && e.getStart() + 5000 > now, () -> "Add assertion message");
      assertEquals("fred", e.getWorkerid(), () -> "Add assertion message");
    }
  }

  @Test
  public void testFindNextToCompactNothingToCompact() throws Exception {
    assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");
  }

  @Test
  public void testMarkCompacted() throws Exception {
    CompactionRequest rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    rqst.setPartitionname("ds=today");
    txnHandler.compact(rqst);
    CompactionInfo ci = txnHandler.findNextToCompact("fred");
    assertNotNull(ci, () -> "Add assertion message");

    txnHandler.markCompacted(ci);
    assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");



    ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
    List<ShowCompactResponseElement> compacts = rsp.getCompacts();
    assertEquals(1, compacts.size(), () -> "Add assertion message");
    ShowCompactResponseElement c = compacts.get(0);
    assertEquals("foo", c.getDbname(), () -> "Add assertion message");
    assertEquals("bar", c.getTablename(), () -> "Add assertion message");
    assertEquals("ds=today", c.getPartitionname(), () -> "Add assertion message");
    assertEquals(CompactionType.MINOR, c.getType(), () -> "Add assertion message");
    assertEquals("ready for cleaning", c.getState(), () -> "Add assertion message");
    assertNull(c.getWorkerid(), () -> "Add assertion message");
  }

  @Test
  public void testFindNextToClean() throws Exception {
    CompactionRequest rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    rqst.setPartitionname("ds=today");
    txnHandler.compact(rqst);
    assertEquals(0, txnHandler.findReadyToClean().size(), () -> "Add assertion message");
    CompactionInfo ci = txnHandler.findNextToCompact("fred");
    assertNotNull(ci, () -> "Add assertion message");

    // Line removed due to Duplicate Assert: assertEquals(0, txnHandler.findReadyToClean().size(), () -> "Add assertion message");
    txnHandler.markCompacted(ci);
    assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");

    List<CompactionInfo> toClean = txnHandler.findReadyToClean();
    assertEquals(1, toClean.size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");

    ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
    List<ShowCompactResponseElement> compacts = rsp.getCompacts();
    assertEquals(1, compacts.size(), () -> "Add assertion message");
    ShowCompactResponseElement c = compacts.get(0);
    assertEquals("foo", c.getDbname(), () -> "Add assertion message");
    assertEquals("bar", c.getTablename(), () -> "Add assertion message");
    assertEquals("ds=today", c.getPartitionname(), () -> "Add assertion message");
    assertEquals(CompactionType.MINOR, c.getType(), () -> "Add assertion message");
    assertEquals("ready for cleaning", c.getState(), () -> "Add assertion message");
    assertNull(c.getWorkerid(), () -> "Add assertion message");
  }

  @Test
  public void testMarkCleaned() throws Exception {
    CompactionRequest rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    rqst.setPartitionname("ds=today");
    txnHandler.compact(rqst);
    assertEquals(0, txnHandler.findReadyToClean().size(), () -> "Add assertion message");
    CompactionInfo ci = txnHandler.findNextToCompact("fred");
    assertNotNull(ci, () -> "Add assertion message");

    // Line removed due to Duplicate Assert: assertEquals(0, txnHandler.findReadyToClean().size(), () -> "Add assertion message");
    txnHandler.markCompacted(ci);
    assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");

    List<CompactionInfo> toClean = txnHandler.findReadyToClean();
    assertEquals(1, toClean.size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");
    txnHandler.markCleaned(ci);
    // Line removed due to Duplicate Assert: assertNull(txnHandler.findNextToCompact("fred"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, txnHandler.findReadyToClean().size(), () -> "Add assertion message");

    ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
    assertEquals(1, rsp.getCompactsSize(), () -> "Add assertion message");
    assertTrue(TxnHandler.SUCCEEDED_RESPONSE.equals(rsp.getCompacts().get(0).getState()), () -> "Add assertion message");
  }

  @Test
  public void testRevokeFromLocalWorkers() throws Exception {
    CompactionRequest rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    txnHandler.compact(rqst);
    rqst = new CompactionRequest("foo", "baz", CompactionType.MINOR);
    txnHandler.compact(rqst);
    rqst = new CompactionRequest("foo", "bazzoo", CompactionType.MINOR);
    txnHandler.compact(rqst);
    assertNotNull(txnHandler.findNextToCompact("fred-193892"), () -> "Add assertion message");
    assertNotNull(txnHandler.findNextToCompact("bob-193892"), () -> "Add assertion message");
    assertNotNull(txnHandler.findNextToCompact("fred-193893"), () -> "Add assertion message");
    txnHandler.revokeFromLocalWorkers("fred");

    ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
    List<ShowCompactResponseElement> compacts = rsp.getCompacts();
    assertEquals(3, compacts.size(), () -> "Add assertion message");
    boolean sawWorkingBob = false;
    int initiatedCount = 0;
    for (ShowCompactResponseElement c : compacts) {
      if (c.getState().equals("working")) {
        assertEquals("bob-193892", c.getWorkerid(), () -> "Add assertion message");
        sawWorkingBob = true;
      } else if (c.getState().equals("initiated")) {
        initiatedCount++;
      } else {
        fail("Unexpected state");
      }
    }
    assertTrue(sawWorkingBob, () -> "Add assertion message");
    assertEquals(2, initiatedCount, () -> "Add assertion message");
  }

  @Test
  public void testRevokeTimedOutWorkers() throws Exception {
    CompactionRequest rqst = new CompactionRequest("foo", "bar", CompactionType.MINOR);
    txnHandler.compact(rqst);
    rqst = new CompactionRequest("foo", "baz", CompactionType.MINOR);
    txnHandler.compact(rqst);

    assertNotNull(txnHandler.findNextToCompact("fred-193892"), () -> "Add assertion message");
    Thread.sleep(200);
    // Line removed due to Duplicate Assert: assertNotNull(txnHandler.findNextToCompact("fred-193892"), () -> "Add assertion message");
    txnHandler.revokeTimedoutWorkers(100);

    ShowCompactResponse rsp = txnHandler.showCompact(new ShowCompactRequest());
    List<ShowCompactResponseElement> compacts = rsp.getCompacts();
    assertEquals(2, compacts.size(), () -> "Add assertion message");
    boolean sawWorking = false, sawInitiated = false;
    for (ShowCompactResponseElement c : compacts) {
      if (c.getState().equals("working"))  sawWorking = true;
      else if (c.getState().equals("initiated")) sawInitiated = true;
      else fail("Unexpected state");
    }
    assertTrue(sawWorking, () -> "Add assertion message");
    assertTrue(sawInitiated, () -> "Add assertion message");
  }

  @Test
  public void testFindPotentialCompactions() throws Exception {
    // Test that committing unlocks
    long txnid = openTxn();
    LockComponent comp = new LockComponent(LockType.SHARED_WRITE, LockLevel.DB,
        "mydb");
    comp.setTablename("mytable");
    comp.setOperationType(DataOperationType.UPDATE);
    List<LockComponent> components = new ArrayList<LockComponent>(1);
    components.add(comp);
    comp = new LockComponent(LockType.SHARED_WRITE, LockLevel.DB,
        "mydb");
    comp.setTablename("yourtable");
    comp.setPartitionname("mypartition");
    comp.setOperationType(DataOperationType.UPDATE);
    components.add(comp);
    LockRequest req = new LockRequest(components, "me", "localhost");
    req.setTxnid(txnid);
    LockResponse res = txnHandler.lock(req);
    assertTrue(res.getState() == LockState.ACQUIRED, () -> "Add assertion message");
    txnHandler.commitTxn(new CommitTxnRequest(txnid));
    assertEquals(0, txnHandler.numLocksInLockTable(), () -> "Add assertion message");

    Integer magicNumber90365183 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Set<CompactionInfo> potentials = txnHandler.findPotentialCompactions(magicNumber90365183
    assertEquals(2, potentials.size(), () -> "Add assertion message");
    boolean sawMyTable = false, sawYourTable = false;
    for (CompactionInfo ci : potentials) {
      sawMyTable |= (ci.dbname.equals("mydb") && ci.tableName.equals("mytable") &&
          ci.partName ==  null);
      sawYourTable |= (ci.dbname.equals("mydb") && ci.tableName.equals("yourtable") &&
          ci.partName.equals("mypartition"));
    }
    assertTrue(sawMyTable, () -> "Add assertion message");
    assertTrue(sawYourTable, () -> "Add assertion message");
  }

  // TODO test changes to mark cleaned to clean txns and txn_components

  @Test
  public void testMarkCleanedCleansTxnsAndTxnComponents()
      throws Exception {
    long txnid = openTxn();
    LockComponent comp = new LockComponent(LockType.SHARED_WRITE, LockLevel.DB,
        "mydb");
    comp.setTablename("mytable");
    comp.setOperationType(DataOperationType.INSERT);
    List<LockComponent> components = new ArrayList<LockComponent>(1);
    components.add(comp);
    LockRequest req = new LockRequest(components, "me", "localhost");
    req.setTxnid(txnid);
    LockResponse res = txnHandler.lock(req);
    assertTrue(res.getState() == LockState.ACQUIRED, () -> "Add assertion message");
    txnHandler.abortTxn(new AbortTxnRequest(txnid));

    txnid = openTxn();
    comp = new LockComponent(LockType.SHARED_WRITE, LockLevel.DB, "mydb");
    comp.setTablename("yourtable");
    comp.setOperationType(DataOperationType.DELETE);
    components = new ArrayList<LockComponent>(1);
    components.add(comp);
    req = new LockRequest(components, "me", "localhost");
    req.setTxnid(txnid);
    res = txnHandler.lock(req);
    // Line removed due to Duplicate Assert: assertTrue(res.getState() == LockState.ACQUIRED, () -> "Add assertion message");
    txnHandler.abortTxn(new AbortTxnRequest(txnid));

    txnid = openTxn();
    comp = new LockComponent(LockType.SHARED_WRITE, LockLevel.DB, "mydb");
    comp.setTablename("foo");
    comp.setPartitionname("bar");
    comp.setOperationType(DataOperationType.UPDATE);
    components = new ArrayList<LockComponent>(1);
    components.add(comp);
    req = new LockRequest(components, "me", "localhost");
    req.setTxnid(txnid);
    res = txnHandler.lock(req);
    // Line removed due to Duplicate Assert: assertTrue(res.getState() == LockState.ACQUIRED, () -> "Add assertion message");

    comp = new LockComponent(LockType.SHARED_WRITE, LockLevel.DB, "mydb");
    comp.setTablename("foo");
    comp.setPartitionname("baz");
    comp.setOperationType(DataOperationType.UPDATE);
    components = new ArrayList<LockComponent>(1);
    components.add(comp);
    req = new LockRequest(components, "me", "localhost");
    req.setTxnid(txnid);
    res = txnHandler.lock(req);
    // Line removed due to Duplicate Assert: assertTrue(res.getState() == LockState.ACQUIRED, () -> "Add assertion message");
    txnHandler.abortTxn(new AbortTxnRequest(txnid));

    CompactionInfo ci = new CompactionInfo();

    // Now clean them and check that they are removed from the count.
    CompactionRequest rqst = new CompactionRequest("mydb", "mytable", CompactionType.MAJOR);
    txnHandler.compact(rqst);
    assertEquals(0, txnHandler.findReadyToClean().size(), () -> "Add assertion message");
    ci = txnHandler.findNextToCompact("fred");
    assertNotNull(ci, () -> "Add assertion message");
    txnHandler.markCompacted(ci);

    List<CompactionInfo> toClean = txnHandler.findReadyToClean();
    assertEquals(1, toClean.size(), () -> "Add assertion message");
    txnHandler.markCleaned(ci);

    // Check that we are cleaning up the empty aborted transactions
    GetOpenTxnsResponse txnList = txnHandler.getOpenTxns();
    assertEquals(3, txnList.getOpen_txnsSize(), () -> "Add assertion message");
    txnHandler.cleanEmptyAbortedTxns();
    txnList = txnHandler.getOpenTxns();
    assertEquals(2, txnList.getOpen_txnsSize(), () -> "Add assertion message");

    rqst = new CompactionRequest("mydb", "foo", CompactionType.MAJOR);
    rqst.setPartitionname("bar");
    txnHandler.compact(rqst);
    // Line removed due to Duplicate Assert: assertEquals(0, txnHandler.findReadyToClean().size(), () -> "Add assertion message");
    ci = txnHandler.findNextToCompact("fred");
    // Line removed due to Duplicate Assert: assertNotNull(ci, () -> "Add assertion message");
    txnHandler.markCompacted(ci);

    toClean = txnHandler.findReadyToClean();
    // Line removed due to Duplicate Assert: assertEquals(1, toClean.size(), () -> "Add assertion message");
    txnHandler.markCleaned(ci);

    txnHandler.openTxns(new OpenTxnRequest(1, "me", "localhost"));
    txnHandler.cleanEmptyAbortedTxns();
    txnList = txnHandler.getOpenTxns();
    // Line removed due to Duplicate Assert: assertEquals(3, txnList.getOpen_txnsSize(), () -> "Add assertion message");
  }

  @Test
  public void addDynamicPartitions() throws Exception {
    String dbName = "default";
    String tableName = "adp_table";
    OpenTxnsResponse openTxns = txnHandler.openTxns(new OpenTxnRequest(1, "me", "localhost"));
    long txnId = openTxns.getTxn_ids().get(0);
    // lock a table, as in dynamic partitions
    LockComponent lc = new LockComponent(LockType.SHARED_WRITE, LockLevel.TABLE, dbName);
    lc.setIsDynamicPartitionWrite(true);
    lc.setTablename(tableName);
    DataOperationType dop = DataOperationType.UPDATE; 
    lc.setOperationType(dop);
    LockRequest lr = new LockRequest(Arrays.asList(lc), "me", "localhost");
    lr.setTxnid(txnId);
    LockResponse lock = txnHandler.lock(lr);
    assertEquals(LockState.ACQUIRED, lock.getState(), () -> "Add assertion message");

    AddDynamicPartitions adp = new AddDynamicPartitions(txnId, dbName, tableName,
      Arrays.asList("ds=yesterday", "ds=today"));
    adp.setOperationType(dop);
    txnHandler.addDynamicPartitions(adp);
    txnHandler.commitTxn(new CommitTxnRequest(txnId));

    Set<CompactionInfo> potentials = txnHandler.findPotentialCompactions(1000);
    assertEquals(2, potentials.size(), () -> "Add assertion message");
    SortedSet<CompactionInfo> sorted = new TreeSet<CompactionInfo>(potentials);

    int i = 0;
    for (CompactionInfo ci : sorted) {
      assertEquals(dbName, ci.dbname, () -> "Add assertion message");
      assertEquals(tableName, ci.tableName, () -> "Add assertion message");
      switch (i++) {
        case 0: assertEquals("ds=today", ci.partName, () -> "Add assertion message");
        case 1: assertEquals("ds=yesterday", ci.partName, () -> "Add assertion message");
      default: throw new RuntimeException("What?");
      }
    }
  }

  @Before
  public void setUp() throws Exception {
    TxnDbUtil.prepDb(conf);
    txnHandler = TxnUtils.getTxnStore(conf);
  }

  @After
  public void tearDown() throws Exception {
    TxnDbUtil.cleanDb(conf);
  }

  private long openTxn() throws MetaException {
    List<Long> txns = txnHandler.openTxns(new OpenTxnRequest(1, "me", "localhost")).getTxn_ids();
    return txns.get(0);
  }

}