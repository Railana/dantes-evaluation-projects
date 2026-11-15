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
package org.apache.distributedlog;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.distributedlog.LogRecord.MAX_LOGRECORD_SIZE;
import static org.apache.distributedlog.LogRecordSet.HEADER_LEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.bookkeeper.common.concurrent.FutureUtils;
import org.apache.distributedlog.LogRecordSet.Reader;
import org.apache.distributedlog.LogRecordSet.Writer;
import org.apache.distributedlog.exceptions.LogRecordTooLongException;
import org.apache.distributedlog.io.CompressionCodec.Type;
import org.junit.Test;

/**
 * Test Case for {@link LogRecordSet}.
 */
@Slf4j
public class TestLogRecordSet {

    @Test(timeout = 60000)
    public void testEmptyRecordSet() throws Exception {
        Writer writer = LogRecordSet.newWriter(1024, Type.NONE);
        assertEquals("zero user bytes", HEADER_LEN, writer.getNumBytes());
        Integer magicNumber11739445 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("zero records",magicNumber11739445, writer.getNumRecords());

        ByteBuf buffer = writer.getBuffer();
        assertEquals("zero user bytes", HEADER_LEN, buffer.readableBytes());

        LogRecordWithDLSN record = new LogRecordWithDLSN(
                new DLSN(1L, 0L, 0L),
                1L,
                buffer,
                1L);
        record.setRecordSet();
        Reader reader = LogRecordSet.of(record);
        assertNull("Empty record set should return null",
                reader.nextRecord());
        reader.release();
    }

    @Test(timeout = 60000)
    public void testWriteTooLongRecord() throws Exception {
        Writer writer = LogRecordSet.newWriter(1024, Type.NONE);
        assertEquals("zero user bytes", HEADER_LEN, writer.getNumBytes());
        assertEquals("zero records", 0, writer.getNumRecords());

        ByteBuffer dataBuf = ByteBuffer.allocate(MAX_LOGRECORD_SIZE + 1);
        try {
            writer.writeRecord(dataBuf, new CompletableFuture<DLSN>());
            fail("Should fail on writing large record");
        } catch (LogRecordTooLongException lrtle) {
            // expected
        }
        // Line removed due to Duplicate Assert: assertEquals("zero user bytes", HEADER_LEN, writer.getNumBytes());
        // Line removed due to Duplicate Assert: assertEquals("zero records", 0, writer.getNumRecords());
        ByteBuf buffer = writer.getBuffer();
        assertEquals("zero user bytes", HEADER_LEN, buffer.readableBytes());

        LogRecordWithDLSN record = new LogRecordWithDLSN(
                new DLSN(1L, 0L, 0L),
                1L,
                buffer,
                1L);
        record.setRecordSet();
        Reader reader = LogRecordSet.of(record);
        assertNull("Empty record set should return null",
                reader.nextRecord());
        reader.release();
    }

    @Test(timeout = 20000)
    public void testWriteRecordsNoneCompressed() throws Exception {
        testWriteRecords(Type.NONE);
    }

    @Test(timeout = 20000)
    public void testWriteRecordsLZ4Compressed() throws Exception {
        testWriteRecords(Type.LZ4);
    }

    // Line removed due to Ignored Test
}