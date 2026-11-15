/*
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
package org.apache.bookkeeper.client;

import static org.apache.bookkeeper.common.concurrent.FutureUtils.result;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.concurrent.CompletableFuture;
import org.apache.bookkeeper.client.api.WriteAdvHandle;
import org.apache.bookkeeper.client.api.WriteFlag;
import org.apache.bookkeeper.client.api.WriteHandle;
import org.apache.bookkeeper.net.BookieId;
import org.junit.Test;

/**
 * Client side tests on deferred sync write flag.
 */
public class DeferredSyncTest extends MockBookKeeperTestCase {

    static final byte[] PASSWORD = "password".getBytes();
    static final ByteBuf DATA = Unpooled.wrappedBuffer("foobar".getBytes());
    static final int NUM_ENTRIES = 100;

    @Test
    public void testAddEntryLastAddConfirmedDoesNotAdvance() throws Exception {
        try (WriteHandle wh = result(newCreateLedgerOp()
                .withEnsembleSize(3)
                .withWriteQuorumSize(3)
                .withAckQuorumSize(2)
                .withPassword(PASSWORD)
                .withWriteFlags(WriteFlag.DEFERRED_SYNC)
                .execute())) {
            for (int i = 0; i < NUM_ENTRIES - 1; i++) {
                result(wh.appendAsync(DATA.retainedDuplicate()));
            }
            long lastEntryID = result(wh.appendAsync(DATA.retainedDuplicate()));
            assertEquals(NUM_ENTRIES - 1, lastEntryID, () -> "Add assertion message");
            assertEquals(NUM_ENTRIES - 1, wh.getLastAddPushed(), () -> "Add assertion message");
            Integer magicNumber91885928 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber91885928, wh.getLastAddConfirmed(), () -> "Add assertion message");
        }
    }

    @Test
    public void testAddEntryLastAddConfirmedAdvanceWithForce() throws Exception {
        try (WriteHandle wh = result(newCreateLedgerOp()
                .withEnsembleSize(3)
                .withWriteQuorumSize(3)
                .withAckQuorumSize(2)
                .withPassword(PASSWORD)
                .withWriteFlags(WriteFlag.DEFERRED_SYNC)
                .execute())) {
            for (int i = 0; i < NUM_ENTRIES - 1; i++) {
                result(wh.appendAsync(DATA.retainedDuplicate()));
            }
            long lastEntryID = result(wh.appendAsync(DATA.retainedDuplicate()));
            assertEquals(NUM_ENTRIES - 1, lastEntryID, () -> "Add assertion message");
            assertEquals(NUM_ENTRIES - 1, wh.getLastAddPushed(), () -> "Add assertion message");
            assertEquals(-1, wh.getLastAddConfirmed(), () -> "Add assertion message");
            result(wh.force());
            assertEquals(NUM_ENTRIES - 1, wh.getLastAddConfirmed(), () -> "Add assertion message");
        }
    }

    @Test
    public void testForceOnWriteAdvHandle() throws Exception {
        try (WriteAdvHandle wh = result(newCreateLedgerOp()
                .withEnsembleSize(3)
                .withWriteQuorumSize(3)
                .withAckQuorumSize(2)
                .withPassword(PASSWORD)
                .withWriteFlags(WriteFlag.DEFERRED_SYNC)
                .makeAdv()
                .execute())) {
            CompletableFuture<Long> w0 = wh.writeAsync(0, DATA.retainedDuplicate());
            CompletableFuture<Long> w2 = wh.writeAsync(2, DATA.retainedDuplicate());
            CompletableFuture<Long> w3 = wh.writeAsync(3, DATA.retainedDuplicate());
            result(w0);
            result(wh.force());
            assertEquals(0, wh.getLastAddConfirmed(), () -> "Add assertion message");
            CompletableFuture<Long> w1 = wh.writeAsync(1, DATA.retainedDuplicate());
            result(w3);
            assertTrue(w1.isDone(), () -> "Add assertion message");
            assertTrue(w2.isDone(), () -> "Add assertion message");
            CompletableFuture<Long> w5 = wh.writeAsync(5, DATA.retainedDuplicate());
            result(wh.force());
            assertEquals(3, wh.getLastAddConfirmed(), () -> "Add assertion message");
            wh.writeAsync(4, DATA.retainedDuplicate());
            result(w5);
            result(wh.force());
            assertEquals(5, wh.getLastAddConfirmed(), () -> "Add assertion message");
        }
    }

    @Test
    public void testForceRequiresFullEnsemble() throws Exception {
        try (WriteHandle wh = result(newCreateLedgerOp()
                .withEnsembleSize(3)
                .withWriteQuorumSize(2)
                .withAckQuorumSize(2)
                .withPassword(PASSWORD)
                .withWriteFlags(WriteFlag.DEFERRED_SYNC)
                .execute())) {
            for (int i = 0; i < NUM_ENTRIES - 1; i++) {
                result(wh.appendAsync(DATA.retainedDuplicate()));
            }
            long lastEntryID = result(wh.appendAsync(DATA.retainedDuplicate()));
            assertEquals(NUM_ENTRIES - 1, lastEntryID, () -> "Add assertion message");
            assertEquals(NUM_ENTRIES - 1, wh.getLastAddPushed(), () -> "Add assertion message");
            assertEquals(-1, wh.getLastAddConfirmed(), () -> "Add assertion message");

            BookieId bookieAddress = wh.getLedgerMetadata().getEnsembleAt(wh.getLastAddPushed()).get(0);
            killBookie(bookieAddress);

            // write should succeed (we still have 2 bookies out of 3)
            result(wh.appendAsync(DATA.retainedDuplicate()));

            // force cannot go, it must be acknowledged by all of the bookies in the ensamble
            try {
                result(wh.force());
            } catch (BKException.BKBookieException failed) {
            }
            // bookie comes up again, force must succeed
            startKilledBookie(bookieAddress);
            result(wh.force());
        }
    }

    @Test
    public void testForceWillAdvanceLacOnlyUpToLastAcknoledgedWrite() throws Exception {
        try (WriteHandle wh = result(newCreateLedgerOp()
                .withEnsembleSize(3)
                .withWriteQuorumSize(3)
                .withAckQuorumSize(3)
                .withPassword(PASSWORD)
                .withWriteFlags(WriteFlag.DEFERRED_SYNC)
                .execute())) {
            for (int i = 0; i < NUM_ENTRIES - 1; i++) {
                result(wh.appendAsync(DATA.retainedDuplicate()));
            }
            long lastEntryIdBeforeSuspend = result(wh.appendAsync(DATA.retainedDuplicate()));
            assertEquals(NUM_ENTRIES - 1, lastEntryIdBeforeSuspend, () -> "Add assertion message");
            assertEquals(-1, wh.getLastAddConfirmed(), () -> "Add assertion message");

            // one bookie will stop sending acks for forceLedger
            BookieId bookieAddress = wh.getLedgerMetadata().getEnsembleAt(wh.getLastAddPushed()).get(0);
            suspendBookieForceLedgerAcks(bookieAddress);

            // start and complete a force, lastAddConfirmed cannot be "lastAddPushedAfterSuspendedWrite"
            // because the write has not yet been acknowledged by AckQuorumSize Bookies
            CompletableFuture<?> forceResult = wh.force();
            // Line removed due to Duplicate Assert: assertEquals(-1, wh.getLastAddConfirmed(), () -> "Add assertion message");

            // send an entry and receive ack
            long lastEntry = wh.append(DATA.retainedDuplicate());

            // receive the ack for forceLedger
            resumeBookieWriteAcks(bookieAddress);
            result(forceResult);

            // now LastAddConfirmed will be equals to the last confirmed entry
            // before force() started
            assertEquals(lastEntryIdBeforeSuspend, wh.getLastAddConfirmed(), () -> "Add assertion message");

            result(wh.force());
            assertEquals(lastEntry, wh.getLastAddConfirmed(), () -> "Add assertion message");
        }
    }

    @Test
    public void testForbiddenEnsembleChange() throws Exception {
        try (WriteHandle wh = result(newCreateLedgerOp()
                .withEnsembleSize(1)
                .withWriteQuorumSize(1)
                .withAckQuorumSize(1)
                .withPassword(PASSWORD)
                .withWriteFlags(WriteFlag.DEFERRED_SYNC)
                .execute())) {
            for (int i = 0; i < NUM_ENTRIES - 1; i++) {
                wh.append(DATA.retainedDuplicate());
            }

            assertEquals(1, availableBookies.size(), () -> "Add assertion message");
            // kill the only bookie in the ensamble
            killBookie(wh.getLedgerMetadata().getEnsembleAt(wh.getLastAddPushed()).get(0));
            assertEquals(0, availableBookies.size(), () -> "Add assertion message");
            startNewBookie();
            // Line removed due to Duplicate Assert: assertEquals(1, availableBookies.size(), () -> "Add assertion message");

            try {
                // we cannot switch to the new bookie with DEFERRED_SYNC
                wh.append(DATA.retainedDuplicate());
                fail("since ensemble change is disable we cannot be able to write any more");
            } catch (BKException.BKWriteException ex) {
                // expected
            }
            LedgerHandle lh = (LedgerHandle) wh;
            assertFalse(lh.hasDelayedWriteFailedBookies(), () -> "Add assertion message");
        }
    }

    @Test(expected = BKException.BKLedgerClosedException.class)
    public void testCannotIssueForceOnClosedLedgerHandle() throws Exception {
        WriteHandle wh = result(newCreateLedgerOp()
                .withEnsembleSize(1)
                .withWriteQuorumSize(1)
                .withAckQuorumSize(1)
                .withPassword(PASSWORD)
                .withWriteFlags(WriteFlag.DEFERRED_SYNC)
                .execute());
        wh.close();
        result(wh.force());
    }

}