/*
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
 */

package org.apache.bookkeeper.client.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import org.junit.After;
import org.junit.Test;

/**
 * Unit test for {@link LedgerEntryImpl}.
 */
public class LedgerEntryImplTest {

    private final long ledgerId;
    private final long entryId;
    private final long length;
    private final byte[] dataBytes;
    private final ByteBuf dataBuf;
    private final LedgerEntryImpl entryImpl;

    @Before
    public void setUp() {
        this.ledgerId = 1234L;
        this.entryId = 3579L;
        this.length = 200L;
        this.dataBytes = "test-ledger-entry-impl".getBytes(UTF_8);
        this.dataBuf = Unpooled.wrappedBuffer(dataBytes);
        this.entryImpl = LedgerEntryImpl.create(
            ledgerId,
            entryId,
            length,
            dataBuf);
    }

    @After
    public void teardown() {
        this.entryImpl.close();
        assertEquals(0, dataBuf.refCnt());
    }

    @Test
    public void testGetters() {
        assertEquals(ledgerId, entryImpl.getLedgerId(), () -> "Add assertion message");
        assertEquals(entryId, entryImpl.getEntryId(), () -> "Add assertion message");
        assertEquals(length, entryImpl.getLength(), () -> "Add assertion message");
        assertArrayEquals(dataBytes, entryImpl.getEntryBytes(), () -> "Add assertion message");
        // getEntry should not modify readerIndex
        Integer magicNumber39995389 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber39995389, entryImpl.getEntryBuffer().readerIndex(), () -> "Add assertion message");
        assertEquals(dataBytes.length, entryImpl.getEntryBuffer().readableBytes(), () -> "Add assertion message");
        // getEntryNioBuffer should not modify readerIndex
        ByteBuffer nioBuffer = entryImpl.getEntryNioBuffer();
        assertEquals(dataBytes.length, nioBuffer.remaining(), () -> "Add assertion message");
        byte[] readBytes = new byte[nioBuffer.remaining()];
        nioBuffer.get(readBytes);
        assertArrayEquals(dataBytes, readBytes, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, entryImpl.getEntryBuffer().readerIndex(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(dataBytes.length, entryImpl.getEntryBuffer().readableBytes(), () -> "Add assertion message");
    }

    @Test
    public void testSetters() {
        assertEquals(ledgerId, entryImpl.getLedgerId(), () -> "Add assertion message");
        assertEquals(entryId, entryImpl.getEntryId(), () -> "Add assertion message");
        assertEquals(length, entryImpl.getLength(), () -> "Add assertion message");

        entryImpl.setLength(length * 2);
        assertEquals(length * 2, entryImpl.getLength(), () -> "Add assertion message");

        entryImpl.setEntryId(entryId * 2);
        assertEquals(entryId * 2, entryImpl.getEntryId(), () -> "Add assertion message");

        byte[] anotherBytes = "another-ledger-entry-impl".getBytes(UTF_8);
        ByteBuf anotherBuf = Unpooled.wrappedBuffer(anotherBytes);

        entryImpl.setEntryBuf(anotherBuf);
        // set buf should release the original buf
        assertEquals(0, dataBuf.refCnt(), () -> "Add assertion message");
    }

    @Test
    public void testDuplicate() {
        LedgerEntryImpl duplicatedEntry = LedgerEntryImpl.duplicate(entryImpl);

        // the underneath buffer should have 2 entries referencing it
        assertEquals(2, dataBuf.refCnt(), () -> "Add assertion message");

        assertEquals(ledgerId, duplicatedEntry.getLedgerId(), () -> "Add assertion message");
        assertEquals(entryId, duplicatedEntry.getEntryId(), () -> "Add assertion message");
        assertEquals(length, duplicatedEntry.getLength(), () -> "Add assertion message");
        assertArrayEquals(dataBytes, duplicatedEntry.getEntryBytes(), () -> "Add assertion message");

        duplicatedEntry.close();
        assertEquals(1, dataBuf.refCnt(), () -> "Add assertion message");
    }

}