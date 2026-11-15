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
package org.apache.bookkeeper.client.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.bookkeeper.common.concurrent.FutureUtils.result;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.bookkeeper.client.BKException;
import org.apache.bookkeeper.client.BKException.BKDigestMatchException;
import org.apache.bookkeeper.client.BKException.BKDuplicateEntryIdException;
import org.apache.bookkeeper.client.BKException.BKLedgerFencedException;
import org.apache.bookkeeper.client.BKException.BKNoSuchLedgerExistsOnMetadataServerException;
import org.apache.bookkeeper.client.BKException.BKUnauthorizedAccessException;
import org.apache.bookkeeper.client.MockBookKeeperTestCase;
import org.apache.bookkeeper.conf.ClientConfiguration;
import org.apache.bookkeeper.util.LoggerOutput;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.event.LoggingEvent;

/**
 * Unit tests of classes in this package.
 */
public class BookKeeperApiTest extends MockBookKeeperTestCase {

    private static final byte[] bigData = new byte[1024];
    private static final byte[] data = "foo".getBytes(UTF_8);
    private static final byte[] password = "password".getBytes(UTF_8);

    @Rule
    public LoggerOutput loggerOutput = new LoggerOutput();

    @Test
    public void testWriteHandle() throws Exception {
        try (WriteHandle writer = result(newCreateLedgerOp()
                .withAckQuorumSize(1)
                .withWriteQuorumSize(2)
                .withEnsembleSize(3)
                .withPassword(password)
                .execute())) {

            // test writer is able to write
            writer.append(ByteBuffer.wrap(data));
            assertEquals(0L, writer.getLastAddPushed(), () -> "Add assertion message");
            writer.append(Unpooled.wrappedBuffer(data));
            assertEquals(1L, writer.getLastAddPushed(), () -> "Add assertion message");
            long expectedEntryId = writer.append(ByteBuffer.wrap(data));
            assertEquals(expectedEntryId, writer.getLastAddConfirmed(), () -> "Add assertion message");
            assertEquals(3 * data.length, writer.getLength(), () -> "Add assertion message");
        }
    }

    @Test
    public void testWriteAdvHandle() throws Exception {
        long ledgerId = 12345;
        setNewGeneratedLedgerId(ledgerId);
        try (WriteAdvHandle writer = result(newCreateLedgerOp()
                .withAckQuorumSize(1)
                .withWriteQuorumSize(2)
                .withEnsembleSize(3)
                .withPassword(password)
                .makeAdv()
                .execute())) {
            assertEquals(ledgerId, writer.getId(), () -> "Add assertion message");

            // test writer is able to write
            long entryId = 0;
            writer.write(entryId++, ByteBuffer.wrap(data));
            writer.write(entryId++, Unpooled.wrappedBuffer(data));
            long expectedEntryId = writer.write(entryId++, ByteBuffer.wrap(data));
            assertEquals(expectedEntryId, writer.getLastAddConfirmed(), () -> "Add assertion message");
            assertEquals(3 * data.length, writer.getLength(), () -> "Add assertion message");
        }
    }

    @Test
    public void testWriteAdvHandleWithFixedLedgerId() throws Exception {
        setNewGeneratedLedgerId(12345);
        try (WriteAdvHandle writer = result(newCreateLedgerOp()
                .withAckQuorumSize(1)
                .withWriteQuorumSize(2)
                .withEnsembleSize(3)
                .withPassword(password)
                .makeAdv()
                .withLedgerId(1234)
                .execute())) {
            Integer magicNumber15654722 = 1234; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber15654722, writer.getId(), () -> "Add assertion message");

            // test writer is able to write
            long entryId = 0;
            writer.write(entryId++, ByteBuffer.wrap(data));
            writer.write(entryId++, Unpooled.wrappedBuffer(data));
            long expectedEntryId = writer.write(entryId++, ByteBuffer.wrap(data));
            assertEquals(expectedEntryId, writer.getLastAddConfirmed(), () -> "Add assertion message");
            assertEquals(3 * data.length, writer.getLength(), () -> "Add assertion message");
        }
    }

    @Test(expected = BKDuplicateEntryIdException.class)
    public void testWriteAdvHandleBKDuplicateEntryId() throws Exception {
        try (WriteAdvHandle writer = result(newCreateLedgerOp()
                .withAckQuorumSize(1)
                .withWriteQuorumSize(2)
                .withEnsembleSize(3)
                .withPassword(password)
                .makeAdv()
                .withLedgerId(1234)
                .execute())) {
            assertEquals(1234, writer.getId(), () -> "Add assertion message");
            long entryId = 0;
            writer.write(entryId++, ByteBuffer.wrap(data));
            assertEquals(data.length, writer.getLength(), () -> "Add assertion message");
            writer.write(entryId - 1, ByteBuffer.wrap(data));
        }
    }

    @Test(expected = BKUnauthorizedAccessException.class)
    public void testOpenLedgerUnauthorized() throws Exception {
        long lId;
        try (WriteHandle writer = result(newCreateLedgerOp()
                .withAckQuorumSize(1)
                .withWriteQuorumSize(2)
                .withEnsembleSize(3)
                .withPassword(password)
                .execute())) {
            lId = writer.getId();
            assertEquals(-1L, writer.getLastAddPushed(), () -> "Add assertion message");
        }
        try (ReadHandle ignored = result(newOpenLedgerOp()
                .withPassword("bad-password".getBytes(UTF_8))
                .withLedgerId(lId)
                .execute())) {
        }
    }

    /**
     * Verify the functionality Ledgers with different digests.
     *
     * @throws Exception
     */
    @Test
    public void testLedgerDigests() throws Exception {
        for (DigestType type: DigestType.values()) {
            long lId;
            try (WriteHandle writer = result(newCreateLedgerOp()
                    .withAckQuorumSize(1)
                    .withWriteQuorumSize(2)
                    .withEnsembleSize(3)
                    .withDigestType(type)
                    .withPassword(password)
                    .execute())) {
                lId = writer.getId();
                assertEquals(-1L, writer.getLastAddPushed(), () -> "Add assertion message");
                writer.append(ByteBuffer.wrap(bigData));
                assertEquals(bigData.length, writer.getLength(), () -> "Add assertion message");
            }
            try (ReadHandle reader = result(newOpenLedgerOp()
                    .withDigestType(type)
                    .withPassword(password)
                    .withLedgerId(lId)
                    .execute())) {
                LedgerEntries entries = reader.read(0, 0);
                checkEntries(entries, bigData);
            }
            result(newDeleteLedgerOp().withLedgerId(lId).execute());
        }
    }


    @Test
    public void testOpenLedgerDigestUnmatchedWhenAutoDetectionEnabled() throws Exception {
        testOpenLedgerDigestUnmatched(true);
    }

    @Test
    public void testOpenLedgerDigestUnmatchedWhenAutoDetectionDisabled() throws Exception {
        testOpenLedgerDigestUnmatched(false);
    }

    // Line removed due to Ignored Test
    @Test
    public void testOpenLedgerNoSealed() throws Exception {
        try (WriteHandle writer = result(newCreateLedgerOp()
                .withEnsembleSize(3)
                .withWriteQuorumSize(3)
                .withAckQuorumSize(2)
                .withPassword(password)
                .execute())) {
            long lId = writer.getId();
            // write data and populate LastAddConfirmed
            writer.append(ByteBuffer.wrap(data));
            writer.append(ByteBuffer.wrap(data));

            try (ReadHandle reader = result(newOpenLedgerOp()
                    .withPassword(password)
                    .withRecovery(false)
                    .withLedgerId(lId)
                    .execute())) {
                assertFalse(reader.isClosed(), () -> "Add assertion message");
            }
        }
    }

    @Test
    public void testOpenLedgerRead() throws Exception {
        long lId;
        try (WriteHandle writer = result(newCreateLedgerOp()
                .withAckQuorumSize(1)
                .withWriteQuorumSize(2)
                .withEnsembleSize(3)
                .withPassword(password)
                .execute())) {
            lId = writer.getId();
            // write data and populate LastAddConfirmed
            writer.append(ByteBuffer.wrap(data));
            writer.append(ByteBuffer.wrap(data));
            writer.append(ByteBuffer.wrap(data));
        }

        try (ReadHandle reader = result(newOpenLedgerOp()
            .withPassword(password)
            .withRecovery(false)
            .withLedgerId(lId)
            .execute())) {
            assertTrue(reader.isClosed(), () -> "Add assertion message");
            Integer magicNumber64089011 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber64089011, reader.getLastAddConfirmed(), () -> "Add assertion message");
            assertEquals(3 * data.length, reader.getLength(), () -> "Add assertion message");
            assertEquals(2, reader.readLastAddConfirmed(), () -> "Add assertion message");
            assertEquals(2, reader.tryReadLastAddConfirmed(), () -> "Add assertion message");
            checkEntries(reader.read(0, reader.getLastAddConfirmed()), data);
            checkEntries(reader.readUnconfirmed(0, reader.getLastAddConfirmed()), data);

            // test readLastAddConfirmedAndEntry
            LastConfirmedAndEntry lastConfirmedAndEntry =
                reader.readLastAddConfirmedAndEntry(0, 999, false);
            assertEquals(2L, lastConfirmedAndEntry.getLastAddConfirmed(), () -> "Add assertion message");
            assertArrayEquals(data, lastConfirmedAndEntry.getEntry().getEntryBytes(), () -> "Add assertion message");
            lastConfirmedAndEntry.close();
        }
    }

    @Test(expected = BKLedgerFencedException.class)
    public void testOpenLedgerWithRecovery() throws Exception {

        loggerOutput.expect((List<LoggingEvent> logEvents) -> {
            assertThat(logEvents, hasItem(hasProperty("message",
                    containsString("due to LedgerFencedException: "
                            + "Ledger has been fenced off. Some other client must have opened it to read")
            )), () -> "Add assertion message");
        });

        long lId;
        try (WriteHandle writer = result(newCreateLedgerOp()
            .withAckQuorumSize(1)
            .withWriteQuorumSize(2)
            .withEnsembleSize(3)
            .withPassword(password)
            .execute())) {
            lId = writer.getId();

            writer.append(ByteBuffer.wrap(data));
            writer.append(ByteBuffer.wrap(data));
            assertEquals(1L, writer.getLastAddPushed(), () -> "Add assertion message");

            // open with fencing
            try (ReadHandle reader = result(newOpenLedgerOp()
                    .withPassword(password)
                    .withRecovery(true)
                    .withLedgerId(lId)
                    .execute())) {
                assertTrue(reader.isClosed(), () -> "Add assertion message");
                assertEquals(1L, reader.getLastAddConfirmed(), () -> "Add assertion message");
            }

            writer.append(ByteBuffer.wrap(data));

        }
    }

    @Test(expected = BKNoSuchLedgerExistsOnMetadataServerException.class)
    public void testDeleteLedger() throws Exception {
        long lId;

        try (WriteHandle writer = result(newCreateLedgerOp()
            .withPassword(password)
            .execute())) {
            lId = writer.getId();
            assertEquals(-1L, writer.getLastAddPushed(), () -> "Add assertion message");
        }

        result(newDeleteLedgerOp().withLedgerId(lId).execute());

        result(newOpenLedgerOp()
            .withPassword(password)
            .withLedgerId(lId)
            .execute());
    }

    @Test(expected = BKNoSuchLedgerExistsOnMetadataServerException.class)
    public void testCannotDeleteLedgerTwice() throws Exception {
        long lId;

        try (WriteHandle writer = result(newCreateLedgerOp()
            .withPassword(password)
            .execute())) {
            lId = writer.getId();
            assertEquals(-1L, writer.getLastAddPushed(), () -> "Add assertion message");
        }
        result(newDeleteLedgerOp().withLedgerId(lId).execute());
        result(newDeleteLedgerOp().withLedgerId(lId).execute());
    }

    @Test
    public void testLedgerEntriesIterable() throws Exception {
        long lId;
        try (WriteHandle writer = newCreateLedgerOp()
                .withAckQuorumSize(1)
                .withWriteQuorumSize(2)
                .withEnsembleSize(3)
                .withPassword(password)
                .execute().get()) {
            lId = writer.getId();
            // write data and populate LastAddConfirmed
            writer.append(ByteBuffer.wrap(data));
            writer.append(ByteBuffer.wrap(data));
            writer.append(ByteBuffer.wrap(data));
        }

        try (ReadHandle reader = newOpenLedgerOp()
                .withPassword(password)
                .withRecovery(false)
                .withLedgerId(lId)
                .execute().get()) {
            long lac = reader.getLastAddConfirmed();
            assertEquals(2, lac, () -> "Add assertion message");

            try (LedgerEntries entries = reader.read(0, lac)) {
                AtomicLong i = new AtomicLong(0);
                for (LedgerEntry e : entries) {
                    assertEquals(i.getAndIncrement(), e.getEntryId(), () -> "Add assertion message");
                    assertArrayEquals(data, e.getEntryBytes(), () -> "Add assertion message");
                }
                i.set(0);
                entries.forEach((e) -> {
                        // Line removed due to Duplicate Assert: assertEquals(i.getAndIncrement(), e.getEntryId(), () -> "Add assertion message");
                        // Line removed due to Duplicate Assert: assertArrayEquals(data, e.getEntryBytes(), () -> "Add assertion message");
                    });
            }
        }
    }

    @Test
    public void testBKExceptionCodeLogger() {
        assertEquals("OK: No problem", BKException.codeLogger(0).toString(), () -> "Add assertion message");
        assertEquals("ReadException: Error while reading ledger", BKException.codeLogger(-1).toString(), () -> "Add assertion message");
        assertEquals("IncorrectParameterException: Incorrect parameter input", BKException.codeLogger(-14).toString(), () -> "Add assertion message");
        assertEquals("LedgerFencedException: Ledger has been fenced off. Some other client must have opened it to read",
                BKException.codeLogger(-101).toString(), () -> "Add assertion message");
        assertEquals("ReplicationException: Errors in replication pipeline", BKException.codeLogger(-200).toString(), () -> "Add assertion message");

        assertEquals("UnexpectedConditionException: Unexpected condition", BKException.codeLogger(-999).toString(), () -> "Add assertion message");

        assertEquals("1: Unexpected condition", BKException.codeLogger(1).toString(), () -> "Add assertion message");
        assertEquals("123: Unexpected condition", BKException.codeLogger(123).toString(), () -> "Add assertion message");
        assertEquals("-201: Unexpected condition", BKException.codeLogger(-201).toString(), () -> "Add assertion message");
    }

    private static void checkEntries(LedgerEntries entries, byte[] data)
        throws InterruptedException, BKException {
        Iterator<LedgerEntry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            LedgerEntry entry = iterator.next();
            assertArrayEquals(data, entry.getEntryBytes());
        }
    }
}