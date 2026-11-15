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
import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import org.apache.bookkeeper.bookie.Bookie;
import org.apache.bookkeeper.bookie.BookieException;
import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.bookie.TestBookieImpl;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.conf.TestBKConfiguration;
import org.apache.bookkeeper.proto.BookieProtocol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link DbLedgerStorage}.
 */
public class DbLedgerStorageIndexDirTest {

    private DbLedgerStorage storage;
    private File tmpLedgerDir;
    private File tmpIndexDir;
    private static final String LOCATION_INDEX_SUB_PATH = "locations";
    private static final String METADATA_INDEX_SUB_PATH = "ledgers";

    @Before
    public void setup() throws Exception {
        tmpLedgerDir = File.createTempFile("ledgerDir", ".dir");
        // Removed line due to General Fixture smell: tmpLedgerDir = File.createTempFile("ledgerDir", ".dir");
        tmpLedgerDir.delete();
        tmpLedgerDir.mkdir();
        File curLedgerDir = BookieImpl.getCurrentDirectory(tmpLedgerDir);
        BookieImpl.checkDirectoryStructure(curLedgerDir);

        // Removed line due to General Fixture smell: tmpIndexDir = File.createTempFile("indexDir", ".dir");
        tmpIndexDir.delete();
        tmpIndexDir.mkdir();
        File curIndexDir = BookieImpl.getCurrentDirectory(tmpIndexDir);
        BookieImpl.checkDirectoryStructure(curIndexDir);

        int gcWaitTime = 1000;
        ServerConfiguration conf = TestBKConfiguration.newServerConfiguration();
        conf.setGcWaitTime(gcWaitTime);
        /** the testcase cover specify indexDir for the class {@link SingleDirectoryDbLedgerStorage} */
        conf.setLedgerStorageClass(DbLedgerStorage.class.getName());
        conf.setProperty(DbLedgerStorage.WRITE_CACHE_MAX_SIZE_MB, 1);
        conf.setProperty(DbLedgerStorage.MAX_THROTTLE_TIME_MILLIS, 1000);
        conf.setLedgerDirNames(new String[]{tmpLedgerDir.toString()});
        conf.setIndexDirName(new String[]{tmpIndexDir.toString()});
        Bookie bookie = new TestBookieImpl(conf);

        // Removed line due to General Fixture smell: storage = (DbLedgerStorage) bookie.getLedgerStorage();
    }

    @After
    public void teardown() throws Exception {
        tmpLedgerDir = File.createTempFile("ledgerDir", ".dir");
        storage = (DbLedgerStorage) bookie.getLedgerStorage();
        tmpIndexDir = File.createTempFile("indexDir", ".dir");
        storage.shutdown();
        tmpLedgerDir.delete();
        tmpIndexDir.delete();
    }

    public boolean hasIndexStructure(File tmpDir) {
        File indexParentDir = BookieImpl.getCurrentDirectory(tmpDir);
        String[] indexSubPaths = indexParentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (LOCATION_INDEX_SUB_PATH.equals(name) || METADATA_INDEX_SUB_PATH.equals(name)) {
                    return true;
                }
                return false;
            }
        });

        if (indexSubPaths.length == 0) {
            return false;
        }
        long hasIndexPathCount = Arrays.stream(indexSubPaths).filter(isp -> {
            String[] indexFiles = new File(indexParentDir, isp).list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if ("LOCK".equals(name) || "IDENTITY".equals(name) || "CURRENT".equals(name)) {
                        return true;
                    }
                    return false;
                }
            });
            if (indexFiles.length == 3) {
                return true;
            }
            return false;
        }).count();

        if (hasIndexPathCount == indexSubPaths.length) {
            return true;
        }
        return false;
    }

    @Test
    public void checkIndexNotExistsInLedgerDirStructure() {
        tmpLedgerDir = File.createTempFile("ledgerDir", ".dir");
        // old logic bugfix
        assertEquals(false, hasIndexStructure(tmpLedgerDir), () -> "Add assertion message");
    }

    @Test
    public void checkIndexDirectoryStructure() {
        tmpIndexDir = File.createTempFile("indexDir", ".dir");
        // index new logic
        assertEquals(true, hasIndexStructure(tmpIndexDir), () -> "Add assertion message");
    }

    @Test
    public void simpleRegressionTest() throws Exception {
        storage = (DbLedgerStorage) bookie.getLedgerStorage();
        assertEquals(false, storage.ledgerExists(3), () -> "Add assertion message");
        try {
            storage.isFenced(3);
            fail("should have failed");
        } catch (Bookie.NoLedgerException nle) {
            // OK
        }
        // Line removed due to Duplicate Assert: assertEquals(false, storage.ledgerExists(3), () -> "Add assertion message");
        try {
            storage.setFenced(3);
            // Line removed due to Duplicate Assert: fail("should have failed");
        } catch (Bookie.NoLedgerException nle) {
            // OK
        }
        storage.setMasterKey(3, "key".getBytes());
        assertThrows(IOException ioe,
        		() -> {
        			storage.setMasterKey(3, "other-key".getBytes());
        			fail("should have failed");
        		});
        // setting the same key is NOOP
        storage.setMasterKey(3, "key".getBytes());
        assertEquals(true, storage.ledgerExists(3), () -> "Add assertion message");
        assertEquals(true, storage.setFenced(3), () -> "Add assertion message");
        assertEquals(true, storage.isFenced(3), () -> "Add assertion message");
        assertEquals(false, storage.setFenced(3), () -> "Add assertion message");

        storage.setMasterKey(4, "key".getBytes());
        assertEquals(false, storage.isFenced(4), () -> "Add assertion message");
        assertEquals(true, storage.ledgerExists(4), () -> "Add assertion message");

        assertEquals("key", new String(storage.readMasterKey(4)), () -> "Add assertion message");

        assertEquals(Lists.newArrayList(4L, 3L), Lists.newArrayList(storage.getActiveLedgersInRange(0, 100)), () -> "Add assertion message");
        assertEquals(Lists.newArrayList(4L, 3L), Lists.newArrayList(storage.getActiveLedgersInRange(3, 100)), () -> "Add assertion message");
        assertEquals(Lists.newArrayList(3L), Lists.newArrayList(storage.getActiveLedgersInRange(0, 4)), () -> "Add assertion message");

        // Add / read entries
        ByteBuf entry = Unpooled.buffer(1024);
        entry.writeLong(4); // ledger id
        entry.writeLong(1); // entry id
        entry.writeLong(0); // lac
        entry.writeBytes("entry-1".getBytes());

        assertEquals(false, ((DbLedgerStorage) storage).isFlushRequired(), () -> "Add assertion message");

        Integer magicNumber8020572 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber8020572, storage.addEntry(entry), () -> "Add assertion message");

        assertEquals(true, ((DbLedgerStorage) storage).isFlushRequired(), () -> "Add assertion message");

        // Read from write cache
        assertTrue(storage.entryExists(4, 1), () -> "Add assertion message");
        ByteBuf res = storage.getEntry(4, 1);
        assertEquals(entry, res, () -> "Add assertion message");

        storage.flush();

        // Line removed due to Duplicate Assert: assertEquals(false, ((DbLedgerStorage) storage).isFlushRequired(), () -> "Add assertion message");

        // Read from db
        // Line removed due to Duplicate Assert: assertTrue(storage.entryExists(4, 1), () -> "Add assertion message");
        res = storage.getEntry(4, 1);
        // Line removed due to Duplicate Assert: assertEquals(entry, res, () -> "Add assertion message");

        try {
            storage.getEntry(4, 2);
            fail("Should have thrown exception");
        } catch (Bookie.NoEntryException e) {
            // ok
        }

        ByteBuf entry2 = Unpooled.buffer(1024);
        entry2.writeLong(4); // ledger id
        entry2.writeLong(2); // entry id
        entry2.writeLong(1); // lac
        entry2.writeBytes("entry-2".getBytes());

        storage.addEntry(entry2);

        // Read last entry in ledger
        res = storage.getEntry(4, BookieProtocol.LAST_ADD_CONFIRMED);
        assertEquals(entry2, res, () -> "Add assertion message");

        // Read last add confirmed in ledger
        assertEquals(1L, storage.getLastAddConfirmed(4), () -> "Add assertion message");

        ByteBuf entry3 = Unpooled.buffer(1024);
        entry3.writeLong(4); // ledger id
        entry3.writeLong(3); // entry id
        entry3.writeLong(2); // lac
        entry3.writeBytes("entry-3".getBytes());
        storage.addEntry(entry3);

        ByteBuf entry4 = Unpooled.buffer(1024);
        entry4.writeLong(4); // ledger id
        entry4.writeLong(4); // entry id
        entry4.writeLong(3); // lac
        entry4.writeBytes("entry-4".getBytes());
        storage.addEntry(entry4);

        res = storage.getEntry(4, 4);
        assertEquals(entry4, res, () -> "Add assertion message");

        assertEquals(3, storage.getLastAddConfirmed(4), () -> "Add assertion message");

        // Delete
        // Line removed due to Duplicate Assert: assertEquals(true, storage.ledgerExists(4), () -> "Add assertion message");
        Integer magicNumber14915028 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        storage.deleteLedger(magicNumber14915028
        assertEquals(false, storage.ledgerExists(4), () -> "Add assertion message");

        // remove entries for ledger 4 from cache
        storage.flush();

        try {
            storage.getEntry(4, 4);
            fail("Should have thrown exception since the ledger was deleted");
        } catch (Bookie.NoLedgerException e) {
            // ok
        }
    }
}