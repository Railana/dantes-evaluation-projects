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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.distributedlog.api.DistributedLogManager;
import org.apache.distributedlog.exceptions.BKTransmitException;
import org.apache.distributedlog.exceptions.EndOfStreamException;
import org.apache.distributedlog.exceptions.WriteException;
import org.apache.distributedlog.util.FailpointUtils;
import org.apache.distributedlog.util.Utils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Test Case for {@link AppendOnlyStreamWriter}.
 */
public class TestAppendOnlyStreamWriter extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestAppendOnlyStreamWriter.class);

    @Rule
    public TestName testNames = new TestName();

    @Test(timeout = 60000)
    public void testBasicReadAndWriteBehavior() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogManager dlmwrite = createNewDLM(conf, name);
        DistributedLogManager dlmreader = createNewDLM(conf, name);
        byte[] byteStream = DLMTestUtil.repeatString("abc", 51).getBytes();

        long txid = 1;
        AppendOnlyStreamWriter writer = dlmwrite.getAppendOnlyStreamWriter();
        writer.write(DLMTestUtil.repeatString("abc", 11).getBytes());
        writer.write(DLMTestUtil.repeatString("abc", 40).getBytes());
        writer.force(false);
        writer.close();
        AppendOnlyStreamReader reader = dlmreader.getAppendOnlyStreamReader();

        byte[] bytesIn = new byte[byteStream.length];
        int read = reader.read(bytesIn, 0, 23);
        Integer magicNumber12628159 = 23; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber12628159, read() -> "Add assertion message");
        Integer magicNumber65225477 = 31; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        read = reader.read(bytesIn, 23,magicNumber65225477
        assertEquals(read, 31, () -> "Add assertion message");
        byte[] bytesInTemp = new byte[byteStream.length];
        read = reader.read(bytesInTemp, 0, byteStream.length);
        assertEquals(read, byteStream.length - 23 - 31, () -> "Add assertion message");
        read = new ByteArrayInputStream(bytesInTemp).read(bytesIn, 23 + 31, byteStream.length - 23 - 31);
        // Line removed due to Duplicate Assert: assertEquals(read, byteStream.length - 23 - 31, () -> "Add assertion message");
        assertArrayEquals(bytesIn, byteStream, () -> "Add assertion message");
        reader.close();
        dlmreader.close();
        dlmwrite.close();
    }

    @Test(timeout = 60000)
    public void testWriteFutureDoesNotCompleteUntilWritePersisted() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.setPeriodicFlushFrequencyMilliSeconds(Integer.MAX_VALUE);
        conf.setImmediateFlushEnabled(false);

        DistributedLogManager dlmwriter = createNewDLM(conf, name);
        DistributedLogManager dlmreader = createNewDLM(conf, name);
        byte[] byteStream = DLMTestUtil.repeatString("abc", 51).getBytes();

        // Can't reliably test the future is not completed until fsync is called, since writer.force may just
        // happen very quickly. But we can test that the mechanics of the future write and api are basically
        // correct.
        AppendOnlyStreamWriter writer = dlmwriter.getAppendOnlyStreamWriter();
        CompletableFuture<DLSN> dlsnFuture = writer.write(DLMTestUtil.repeatString("abc", 11).getBytes());

        // The real problem is the fsync completes before writes are submitted, so it never takes effect.
        Thread.sleep(1000);
        assertFalse(dlsnFuture.isDone(), () -> "Add assertion message");
        writer.force(false);
        // Must not throw.
        Utils.ioResult(dlsnFuture, 5, TimeUnit.SECONDS);
        writer.close();
        dlmwriter.close();

        AppendOnlyStreamReader reader = dlmreader.getAppendOnlyStreamReader();
        byte[] bytesIn = new byte[byteStream.length];
        int read = reader.read(bytesIn, 0, 31);
        assertEquals(31, read, () -> "Add assertion message");
        reader.close();
        dlmreader.close();
    }

    @Test(timeout = 60000)
    public void testPositionUpdatesOnlyAfterWriteCompletion() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.setPeriodicFlushFrequencyMilliSeconds(10 * 1000);
        conf.setImmediateFlushEnabled(false);

        DistributedLogManager dlmwriter = createNewDLM(conf, name);
        DistributedLogManager dlmreader = createNewDLM(conf, name);
        byte[] byteStream = DLMTestUtil.repeatString("abc", 11).getBytes();

        // Can't reliably test the future is not completed until fsync is called, since writer.force may just
        // happen very quickly. But we can test that the mechanics of the future write and api are basically
        // correct.
        AppendOnlyStreamWriter writer = dlmwriter.getAppendOnlyStreamWriter();
        CompletableFuture<DLSN> dlsnFuture = writer.write(byteStream);
        Thread.sleep(100);

        // Write hasn't been persisted, position better not be updated.
        assertFalse(dlsnFuture.isDone(), () -> "Add assertion message");
        assertEquals(0, writer.position(), () -> "Add assertion message");
        writer.force(false);
        // Position guaranteed to be accurate after writer.force().
        assertEquals(byteStream.length, writer.position(), () -> "Add assertion message");

        // Close writer.
        writer.close();
        dlmwriter.close();

        // Make sure we can read it.
        AppendOnlyStreamReader reader = dlmreader.getAppendOnlyStreamReader();
        byte[] bytesIn = new byte[byteStream.length];
        int read = reader.read(bytesIn, 0, byteStream.length);
        assertEquals(byteStream.length, read, () -> "Add assertion message");
        assertEquals(byteStream.length, reader.position(), () -> "Add assertion message");
        reader.close();
        dlmreader.close();
    }

    @Test(timeout = 60000)
    public void testPositionDoesntUpdateBeforeWriteCompletion() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration();

        // Long flush time, but we don't wait for it.
        conf.setPeriodicFlushFrequencyMilliSeconds(100 * 1000);
        conf.setImmediateFlushEnabled(false);
        conf.setOutputBufferSize(1024 * 1024);

        DistributedLogManager dlmwriter = createNewDLM(conf, name);
        byte[] byteStream = DLMTestUtil.repeatString("abc", 11).getBytes();

        AppendOnlyStreamWriter writer = dlmwriter.getAppendOnlyStreamWriter();
        assertEquals(0, writer.position(), () -> "Add assertion message");

        // Much much less than the flush time, small enough not to slow down tests too much, just
        // gives a little more confidence.
        Thread.sleep(500);
        CompletableFuture<DLSN> dlsnFuture = writer.write(byteStream);
        // Line removed due to Duplicate Assert: assertEquals(0, writer.position(), () -> "Add assertion message");

        writer.close();
        dlmwriter.close();
    }

    @Test(timeout = 60000)
    public void testPositionUpdatesOnlyAfterWriteCompletionWithoutFsync() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.setPeriodicFlushFrequencyMilliSeconds(1 * 1000);
        conf.setImmediateFlushEnabled(false);
        conf.setOutputBufferSize(1024 * 1024);

        DistributedLogManager dlmwriter = createNewDLM(conf, name);
        byte[] byteStream = DLMTestUtil.repeatString("abc", 11).getBytes();

        AppendOnlyStreamWriter writer = dlmwriter.getAppendOnlyStreamWriter();
        assertEquals(0, writer.position(), () -> "Add assertion message");

        Utils.ioResult(writer.write(byteStream));
        Thread.sleep(100); // let WriteCompleteListener have time to run
        assertEquals(33, writer.position(), () -> "Add assertion message");

        writer.close();
        dlmwriter.close();
    }

    @Test(timeout = 60000)
    public void testWriterStartsAtTxidZeroForEmptyStream() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.setImmediateFlushEnabled(true);
        conf.setOutputBufferSize(1024);
        BKDistributedLogManager dlm = (BKDistributedLogManager) createNewDLM(conf, name);

        URI uri = createDLMURI("/" + name);
        Utils.ioResult(dlm.getWriterMetadataStore().getLog(uri, name, true, true));

        // Log exists but is empty, better not throw.
        AppendOnlyStreamWriter writer = dlm.getAppendOnlyStreamWriter();
        byte[] byteStream = DLMTestUtil.repeatString("a", 1025).getBytes();
        Utils.ioResult(writer.write(byteStream));

        writer.close();
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testOffsetGapAfterSegmentWriterFailure() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.setImmediateFlushEnabled(false);
        conf.setPeriodicFlushFrequencyMilliSeconds(60 * 1000);
        conf.setOutputBufferSize(1024 * 1024);
        conf.setLogSegmentSequenceNumberValidationEnabled(false);

        final int writeLen = 5;
        final int sectionWrites = 10;
        long read = writeRecordsAndReadThemBackAfterInjectingAFailedTransmit(conf, name, writeLen, sectionWrites);
        assertEquals((2 * sectionWrites + 1) * writeLen, read, () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testNoOffsetGapAfterSegmentWriterFailure() throws Exception {
        String name = testNames.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.setImmediateFlushEnabled(false);
        conf.setPeriodicFlushFrequencyMilliSeconds(60 * 1000);
        conf.setOutputBufferSize(1024 * 1024);
        conf.setDisableRollingOnLogSegmentError(true);

        final int writeLen = 5;
        final int sectionWrites = 10;

        try {
            writeRecordsAndReadThemBackAfterInjectingAFailedTransmit(conf, name, writeLen, sectionWrites);
            fail("should have thrown");
        } catch (BKTransmitException ex) {
        }

        BKDistributedLogManager dlm = (BKDistributedLogManager) createNewDLM(conf, name);
        long length = dlm.getLastTxId();
        long read = read(dlm, length);
        assertEquals(length, read, () -> "Add assertion message");
    }

    long writeRecordsAndReadThemBackAfterInjectingAFailedTransmit(
            DistributedLogConfiguration conf,
            String name,
            int writeLen,
            int sectionWrites)
            throws Exception {

        BKDistributedLogManager dlm = (BKDistributedLogManager) createNewDLM(conf, name);

        URI uri = createDLMURI("/" + name);
        Utils.ioResult(dlm.getWriterMetadataStore().getLog(uri, name, true, true));

        // Log exists but is empty, better not throw.
        AppendOnlyStreamWriter writer = dlm.getAppendOnlyStreamWriter();
        byte[] byteStream = DLMTestUtil.repeatString("A", writeLen).getBytes();

        // Log a hundred entries. Offset is advanced accordingly.
        for (int i = 0; i < sectionWrites; i++) {
            writer.write(byteStream);
        }
        writer.force(false);

        long read = read(dlm, 1 * sectionWrites * writeLen);
        assertEquals(1 * sectionWrites * writeLen, read);

        // Now write another 100, but trigger failure during transmit.
        for (int i = 0; i < sectionWrites; i++) {
            writer.write(byteStream);
        }

        try {
            FailpointUtils.setFailpoint(
                FailpointUtils.FailPointName.FP_TransmitFailGetBuffer,
                FailpointUtils.FailPointActions.FailPointAction_Throw);

            writer.force(false);
            fail("should have thown ⊙﹏⊙");
        } catch (WriteException we) {
        } finally {
            FailpointUtils.removeFailpoint(
                FailpointUtils.FailPointName.FP_TransmitFailGetBuffer);
        }

        // This actually fails because we try to close an errored out stream.
        writer.write(byteStream);

        // Writing another 100 triggers offset gap.
        for (int i = 0; i < sectionWrites; i++) {
            writer.write(byteStream);
        }

        writer.force(false);
        writer.markEndOfStream();
        writer.close();

        long length = dlm.getLastTxId();
        assertEquals(3 * sectionWrites * writeLen + 5, length);
        read = read(dlm, length);
        dlm.close();
        return read;
    }

    long read(DistributedLogManager dlm, long n) throws Exception {
        AppendOnlyStreamReader reader = dlm.getAppendOnlyStreamReader();
        byte[] bytesIn = new byte[1];
        long offset = 0;
        try {
            while (offset < n) {
                int read = reader.read(bytesIn, 0, 1);
                offset += read;
            }
        } catch (EndOfStreamException ex) {
            LOG.info("Caught ex", ex);
        } finally {
            reader.close();
        }
        return offset;
    }
}