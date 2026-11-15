/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.bookkeeper.statelib.impl.kv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.bookkeeper.statelib.impl.rocksdb.checkpoint.CheckpointInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;




/**
 * Test cases for Rocksdb KV Store with checkpoints.
 */
@Slf4j
public class TestRocksdbKVStoreCheckpoint {

    @Rule
    public final TestName runtime = new TestName();
    @Rule
    public final TemporaryFolder testDir = new TemporaryFolder();

    private TestStateStore store;

    @Before
    public void setUp() throws Exception {
        store = new TestStateStore(runtime, testDir);
        store.enableCheckpoints(true);
        store.init();
    }

    @After
    public void tearDown() throws Exception {
        if (null != store) {
            store.close();
        }
    }

    @Test
    public void testRestoreCorruptCheckpoint() throws Exception {
        int numKvs = 100;

        store.addNumKVs("transaction-1", numKvs, 0);
        String checkpoint1 = store.checkpoint("checkpoint-1");
        assertEquals("transaction-1", store.get("transaction-id"), () -> "Add assertion message");

        store.addNumKVs("transaction-2", numKvs, 100);
        assertEquals("transaction-2", store.get("transaction-id"), () -> "Add assertion message");
        String checkpoint2 = store.checkpoint("checkpoint-2");

        store.addNumKVs("transaction-3", numKvs, 200);
        assertEquals("transaction-3", store.get("transaction-id"), () -> "Add assertion message");

        store.destroyLocal();
        store.restore();
        // Line removed due to Duplicate Assert: assertEquals("transaction-2", store.get("transaction-id"), () -> "Add assertion message");

        // Ensure we can write to new store
        store.addNumKVs("transaction-4", numKvs, 300);
        assertEquals("transaction-4", store.get("transaction-id"), () -> "Add assertion message");

        // corrupt the checkpoint-2 so restore fails

        CheckpointInfo cpi = store.getLatestCheckpoint();
        store.corruptCheckpoint(cpi);
        store.destroyLocal();

        // latest checkpoint is checkpoint-2, which has been corrupted.
        store.restore();
        // We should fallback to checkpoint-1
        // Line removed due to Duplicate Assert: assertEquals("transaction-1", store.get("transaction-id"), () -> "Add assertion message");
    }

    @Test
    public void testLocalStoreCleanup() throws Exception {
        File checkpointDir = new File(store.getLocalDir(), "checkpoints");

        store.setRemoveLocal(true);
        store.setRemoveRemote(true);
        store.setLocalStorageCleanup(true);

        String[] checkpoints = checkpointDir.list();
        // Initially there is only one checkpoint directory that is used by the statestore
        Integer magicNumber10258321 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber10258321, checkpoints.length() -> "Add assertion message");

        store.restore();

        checkpoints = checkpointDir.list();
        // We should only have one checkpoint in the local directory.
        // Line removed due to Duplicate Assert: assertEquals(1, checkpoints.length, () -> "Add assertion message");

        int numKvs = 100;
        for (int i = 0; i < 3; i++) {
            String txid = "txid-" + i;
            store.addNumKVs(txid, numKvs, i * numKvs);
            String checkpoint1 = store.checkpoint("checkpoint-1");

            checkpoints = checkpointDir.list();
            // Ensure the checkpoints are cleaned up
            // Line removed due to Duplicate Assert: assertEquals(1, checkpoints.length, () -> "Add assertion message");

            store.restore();
            assertEquals(txid, store.get("transaction-id"), () -> "Add assertion message");

            checkpoints = checkpointDir.list();
            // We should only have one checkpoint in the local directory.
            // Line removed due to Duplicate Assert: assertEquals(1, checkpoints.length, () -> "Add assertion message");
        }

        store.close();

        checkpoints = checkpointDir.list();
        // We should not have any checkpoints af the store is closed.
        assertNull(checkpoints, () -> "Add assertion message");
    }
}