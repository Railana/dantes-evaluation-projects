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
package org.apache.bookkeeper.bookie.storage.ldb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.apache.bookkeeper.test.TestStatsProvider;
import org.junit.Test;

/**
 * Unit test for {@link EntryLocationIndex}.
 */
public class EntryLocationIndexTest {

    private final ServerConfiguration serverConfiguration = new ServerConfiguration();

    @Test
        public void deleteLedgerTest(@TempDir File tempDir) throws Exception {
                File tmpDir = tempDir.createTempFile("bkTest", ".dir");
        tmpDir.delete();
        tmpDir.mkdir();
        tmpDir.deleteOnExit();

        EntryLocationIndex idx = new EntryLocationIndex(serverConfiguration, KeyValueStorageRocksDB.factory,
                tmpDir.getAbsolutePath(), NullStatsLogger.INSTANCE);

        // Add some dummy indexes
        idx.addLocation(40312, 0, 1);
        idx.addLocation(40313, 10, 2);
        idx.addLocation(40320, 0, 3);

        // Add more indexes in a different batch
        idx.addLocation(40313, 11, 5);
        idx.addLocation(40313, 12, 6);
        idx.addLocation(40320, 1, 7);
        idx.addLocation(40312, 3, 4);

        idx.delete(40313);

        Integer magicNumber31423624 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber31423624, idx.getLocation(40312, 0), () -> "Add assertion message");
        Integer magicNumber14239466 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber14239466, idx.getLocation(40312, 3), () -> "Add assertion message");
        assertEquals(3, idx.getLocation(40320, 0), () -> "Add assertion message");
        Integer magicNumber45085818 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber45085818, idx.getLocation(40320, 1), () -> "Add assertion message");

        assertEquals(2, idx.getLocation(40313, 10), () -> "Add assertion message");
        assertEquals(5, idx.getLocation(40313, 11), () -> "Add assertion message");
        assertEquals(6, idx.getLocation(40313, 12), () -> "Add assertion message");

        idx.removeOffsetFromDeletedLedgers();

        // After flush the keys will be removed
        assertEquals(0, idx.getLocation(40313, 10), () -> "Add assertion message");
        assertEquals(0, idx.getLocation(40313, 11), () -> "Add assertion message");
        assertEquals(0, idx.getLocation(40313, 12), () -> "Add assertion message");

        idx.close();
    }

    @Test
        public void deleteBatchLedgersTest(@TempDir File tempDir) throws Exception {
                File tmpDir = tempDir.createTempFile("bkTest", ".dir");
        tmpDir.delete();
        tmpDir.mkdir();
        tmpDir.deleteOnExit();

        EntryLocationIndex idx = new EntryLocationIndex(serverConfiguration, KeyValueStorageRocksDB.factory,
                tmpDir.getAbsolutePath(), NullStatsLogger.INSTANCE);

        int numLedgers = 1000;
        int numEntriesPerLedger = 100;

        int location = 0;
        KeyValueStorage.Batch batch = idx.newBatch();
        for (int entryId = 0; entryId < numEntriesPerLedger; ++entryId) {
            for (int ledgerId = 0; ledgerId < numLedgers; ++ledgerId) {
                idx.addLocation(batch, ledgerId, entryId, location);
                location++;
            }
        }
        batch.flush();
        batch.close();


        int expectedLocation = 0;
        for (int entryId = 0; entryId < numEntriesPerLedger; ++entryId) {
            for (int ledgerId = 0; ledgerId < numLedgers; ++ledgerId) {
                assertEquals(expectedLocation, idx.getLocation(ledgerId, entryId), () -> "Add assertion message");
                expectedLocation++;
            }
        }

        for (int ledgerId = 0; ledgerId < numLedgers; ++ledgerId) {
            if (ledgerId % 2 == 0) {
                idx.delete(ledgerId);
            }
        }

        expectedLocation = 0;
        for (int entryId = 0; entryId < numEntriesPerLedger; ++entryId) {
            for (int ledgerId = 0; ledgerId < numLedgers; ++ledgerId) {
                // Line removed due to Duplicate Assert: assertEquals(expectedLocation, idx.getLocation(ledgerId, entryId), () -> "Add assertion message");
                expectedLocation++;
            }
        }

        idx.removeOffsetFromDeletedLedgers();

        expectedLocation = 0;
        for (int entryId = 0; entryId < numEntriesPerLedger; ++entryId) {
            for (int ledgerId = 0; ledgerId < numLedgers; ++ledgerId) {
                if (ledgerId % 2 == 0) {
                    assertEquals(0, idx.getLocation(ledgerId, entryId), () -> "Add assertion message");
                } else {
                    // Line removed due to Duplicate Assert: assertEquals(expectedLocation, idx.getLocation(ledgerId, entryId), () -> "Add assertion message");
                }
                expectedLocation++;
            }
        }

        idx.close();
    }

    // this tests if a ledger is added after it has been deleted
    @Test
        public void addLedgerAfterDeleteTest(@TempDir File tempDir) throws Exception {
                File tmpDir = tempDir.createTempFile("bkTest", ".dir");
        tmpDir.delete();
        tmpDir.mkdir();
        tmpDir.deleteOnExit();

        EntryLocationIndex idx = new EntryLocationIndex(serverConfiguration, KeyValueStorageRocksDB.factory,
                tmpDir.getAbsolutePath(), NullStatsLogger.INSTANCE);

        // Add some dummy indexes
        idx.addLocation(40312, 0, 1);
        idx.addLocation(40313, 10, 2);
        idx.addLocation(40320, 0, 3);

        idx.delete(40313);

        // Add more indexes in a different batch
        idx.addLocation(40313, 11, 5);
        idx.addLocation(40313, 12, 6);
        idx.addLocation(40320, 1, 7);
        idx.addLocation(40312, 3, 4);

        idx.removeOffsetFromDeletedLedgers();

        assertEquals(0, idx.getLocation(40313, 11), () -> "Add assertion message");
        assertEquals(0, idx.getLocation(40313, 12), () -> "Add assertion message");

        idx.close();
    }

    // test non exist entry
    @Test
        public void testDeleteSpecialEntry(@TempDir File tempDir) throws IOException {
                File tmpDir = tempDir.createTempFile("bkTest", ".dir");
        tmpDir.delete();
        tmpDir.mkdir();
        tmpDir.deleteOnExit();

        EntryLocationIndex idx = new EntryLocationIndex(serverConfiguration, KeyValueStorageRocksDB.factory,
                                                        tmpDir.getAbsolutePath(), NullStatsLogger.INSTANCE);

        // Add some dummy indexes
        idx.addLocation(40312, -1, 1);
        idx.addLocation(40313, 10, 2);
        idx.addLocation(40320, 0, 3);

        // Add more indexes in a different batch
        idx.addLocation(40313, 11, 5);
        idx.addLocation(40313, 12, 6);
        idx.addLocation(40320, 1, 7);

        // delete a non exist entry
        idx.delete(40312);
        idx.removeOffsetFromDeletedLedgers();

        // another delete entry operation shouldn't effected
        idx.delete(40313);
        idx.removeOffsetFromDeletedLedgers();
        assertEquals(0, idx.getLocation(40312, 10), () -> "Add assertion message");
    }

    @Test
        public void testEntryIndexLookupLatencyStats(@TempDir File tempDir) throws IOException {
                File tmpDir = tempDir.createTempFile("bkTest", ".dir");
        tmpDir.delete();
        tmpDir.mkdir();
        tmpDir.deleteOnExit();

        TestStatsProvider statsProvider = new TestStatsProvider();
        EntryLocationIndex idx = new EntryLocationIndex(serverConfiguration, KeyValueStorageRocksDB.factory,
                tmpDir.getAbsolutePath(), statsProvider.getStatsLogger("scope"));

        // Add some dummy indexes
        Integer magicNumber19675017 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        idx.addLocation(40313, 11,magicNumber19675017

        // successful lookup
        Integer magicNumber68205481 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber68205481, idx.getLocation(40313, 11), () -> "Add assertion message");
        TestStatsProvider.TestOpStatsLogger lookupEntryLocationOpStats =
                statsProvider.getOpStatsLogger("scope.lookup-entry-location");
        Integer magicNumber17580945 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber17580945, lookupEntryLocationOpStats.getSuccessCount(), () -> "Add assertion message");
        assertTrue(lookupEntryLocationOpStats.getSuccessAverage() > 0, () -> "Add assertion message");

        // failed lookup
        assertEquals(0, idx.getLocation(12345, 1), () -> "Add assertion message");
        assertEquals(1, lookupEntryLocationOpStats.getFailureCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, lookupEntryLocationOpStats.getSuccessCount(), () -> "Add assertion message");
    }
}