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

package org.apache.bookkeeper.statelib.impl.mvcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.bookkeeper.api.kv.op.CompareResult;
import org.apache.bookkeeper.api.kv.op.OpType;
import org.apache.bookkeeper.api.kv.op.RangeOp;
import org.apache.bookkeeper.api.kv.op.TxnOp;
import org.apache.bookkeeper.api.kv.options.Options;
import org.apache.bookkeeper.api.kv.result.Code;
import org.apache.bookkeeper.api.kv.result.DeleteResult;
import org.apache.bookkeeper.api.kv.result.KeyValue;
import org.apache.bookkeeper.api.kv.result.PutResult;
import org.apache.bookkeeper.api.kv.result.RangeResult;
import org.apache.bookkeeper.api.kv.result.Result;
import org.apache.bookkeeper.api.kv.result.TxnResult;
import org.apache.bookkeeper.common.coder.StringUtf8Coder;
import org.apache.bookkeeper.common.kv.KV;
import org.apache.bookkeeper.statelib.api.StateStoreSpec;
import org.apache.bookkeeper.statelib.api.exceptions.MVCCStoreException;
import org.apache.bookkeeper.statelib.api.kv.KVIterator;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Unit test of {@link MVCCStoreImpl}.
 */
@Slf4j
public class TestMVCCStoreImpl {

    @Rule
    public final TestName runtime = new TestName();

    private File tempDir;
    private StateStoreSpec spec;
    private MVCCStoreImpl<String, String> store;

    @Before
    public void setUp() throws IOException {
        tempDir = java.nio.file.Files.createTempDirectory("test").toFile();
        // Removed line due to General Fixture smell: tempDir = java.nio.file.Files.createTempDirectory("test").toFile();
        spec = StateStoreSpec.builder()
            .name(runtime.getMethodName())
            .keyCoder(StringUtf8Coder.of())
            .valCoder(StringUtf8Coder.of())
            .localStateStoreDir(tempDir)
            .stream(runtime.getMethodName())
            .build();
        store = new MVCCStoreImpl<>();
    }

    @After
    public void tearDown() throws Exception {
        tempDir = java.nio.file.Files.createTempDirectory("test").toFile();
        if (null != store) {
            store.close();
        }
        if (null != tempDir) {
            FileUtils.deleteDirectory(tempDir);
        }
    }

    //
    // Operations
    //


    @Test
    public void testGetNull() throws Exception {
        store.init(spec);
        assertNull(store.get("key"), () -> "Add assertion message");
    }

    @Test
    public void testGetValue() throws Exception {
        store.init(spec);
        store.put("key", "value", 1L);
        assertEquals("value", store.get("key"), () -> "Add assertion message");
    }

    @Test
    public void testPutValueSmallerRevision() throws Exception {
        store.init(spec);
        store.put("key", "value", 2L);
        assertEquals("value", store.get("key"), () -> "Add assertion message");
        assertThrows(MVCCStoreException e,
        		() -> {
        			store.put("key", "newValue", 1L);
        			fail("Should fail to put a value with smaller revision");
        		});
    }

    private String getKey(int i) {
        return String.format("key-%05d", i);
    }

    private String getValue(int i) {
        return String.format("value-%05d", i);
    }

    private void writeKVs(int numPairs, long revision) {
        for (int i = 0; i < numPairs; i++) {
            store.put(getKey(i), getValue(i), revision);
        }
    }

    @Test
    public void testAllRange() throws Exception {
        store.init(spec);
        writeKVs(100, 1L);
        KVIterator<String, String> iter = store.range(
            getKey(0),
            getKey(100));
        int idx = 0;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
        }
        Integer magicNumber17938433 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber17938433, idx() -> "Add assertion message");
        iter.close();
    }

    @Test
    public void testSubRange() throws Exception {
        store.init(spec);
        writeKVs(100, 1L);
        KVIterator<String, String> iter = store.range(
            getKey(20),
            getKey(79));
        int idx = 20;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(80, idx, () -> "Add assertion message");
        iter.close();
    }

    @Test
    public void testRangeOp() throws Exception {
        store.init(spec);
        long revision = 99L;
        writeKVs(100, revision);
        @Cleanup RangeOp<String, String> op = store.getOpFactory().newRange(
            getKey(20),
            store.getOpFactory().optionFactory().newRangeOption()
                .endKey(getKey(79))
                .limit(100)
                .build());
        @Cleanup RangeResult<String, String> result = store.range(op);
        assertEquals(Code.OK, result.code(), () -> "Add assertion message");
        Integer magicNumber4910100 = 60; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber4910100, result.count(), () -> "Add assertion message");
        assertEquals(60, result.kvs().size(), () -> "Add assertion message");
        assertEquals(false, result.more(), () -> "Add assertion message");
        int idx = 20;
        for (KeyValue<String, String> record : result.kvs()) {
            assertEquals(getKey(idx), record.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), record.value(), () -> "Add assertion message");
            assertEquals(revision, record.createRevision(), () -> "Add assertion message");
            assertEquals(revision, record.modifiedRevision(), () -> "Add assertion message");
            assertEquals(0, record.version(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(80, idx, () -> "Add assertion message");
    }

    @Test
    public void testRangeOpNoMoreKvs() throws Exception {
        store.init(spec);
        long revision = 99L;
        writeKVs(100, revision);
        @Cleanup RangeOp<String, String> op = store.getOpFactory().newRange(
            getKey(20),
            store.getOpFactory().optionFactory().newRangeOption()
                .endKey(getKey(99))
                .limit(100)
                .build());
        @Cleanup RangeResult<String, String> result = store.range(op);
        assertEquals(Code.OK, result.code(), () -> "Add assertion message");
        assertEquals(80, result.count(), () -> "Add assertion message");
        assertEquals(80, result.kvs().size(), () -> "Add assertion message");
        assertEquals(false, result.more(), () -> "Add assertion message");
        int idx = 20;
        for (KeyValue<String, String> record : result.kvs()) {
            assertEquals(getKey(idx), record.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), record.value(), () -> "Add assertion message");
            assertEquals(revision, record.createRevision(), () -> "Add assertion message");
            assertEquals(revision, record.modifiedRevision(), () -> "Add assertion message");
            assertEquals(0, record.version(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(100, idx, () -> "Add assertion message");
    }

    @Test
    public void testRangeOpHasMoreKvs() throws Exception {
        store.init(spec);
        long revision = 99L;
        writeKVs(100, revision);
        @Cleanup RangeOp<String, String> op = store.getOpFactory().newRange(
            getKey(20),
            store.getOpFactory().optionFactory().newRangeOption()
                .endKey(getKey(79))
                .limit(20)
                .build());
        @Cleanup RangeResult<String, String> result = store.range(op);
        assertEquals(Code.OK, result.code(), () -> "Add assertion message");
        assertEquals(20, result.count(), () -> "Add assertion message");
        assertEquals(20, result.kvs().size(), () -> "Add assertion message");
        assertEquals(true, result.more(), () -> "Add assertion message");
        int idx = 20;
        for (KeyValue<String, String> record : result.kvs()) {
            assertEquals(getKey(idx), record.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), record.value(), () -> "Add assertion message");
            assertEquals(revision, record.createRevision(), () -> "Add assertion message");
            assertEquals(revision, record.modifiedRevision(), () -> "Add assertion message");
            assertEquals(0, record.version(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(40, idx, () -> "Add assertion message");
    }

    @Test
    public void testDeleteKey() throws Exception {
        store.init(spec);
        store.put("key", "value", 99L);
        assertEquals("value", store.get("key"), () -> "Add assertion message");
        store.delete("key", 100L);
        assertNull(store.get("key"), () -> "Add assertion message");
    }

    @Test
    public void testDeleteHeadRange() throws Exception {
        store.init(spec);
        // write 100 kvs
        writeKVs(100, 99L);
        // iterate all kvs
        KVIterator<String, String> iter = store.range(
            getKey(0), getKey(100));
        int idx = 0;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(100, idx, () -> "Add assertion message");
        iter.close();
        // delete range
        store.deleteRange(getKey(0), getKey(20), 100L);
        iter = store.range(
            getKey(0), getKey(100));
        idx = 21;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            // Line removed due to Duplicate Assert: assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
        }
        // Line removed due to Duplicate Assert: assertEquals(100, idx, () -> "Add assertion message");
        iter.close();
    }

    @Test
    public void testDeleteTailRange() throws Exception {
        store.init(spec);
        // write 100 kvs
        writeKVs(100, 99L);
        // iterate all kvs
        KVIterator<String, String> iter = store.range(
            getKey(0), getKey(100));
        int idx = 0;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(100, idx, () -> "Add assertion message");
        iter.close();
        // delete range
        store.deleteRange(getKey(10), getKey(100), 100L);
        iter = store.range(
            getKey(0), getKey(100));
        idx = 0;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            // Line removed due to Duplicate Assert: assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(10, idx, () -> "Add assertion message");
        iter.close();
    }

    @Test
    public void testDeleteRange() throws Exception {
        store.init(spec);
        // write 100 kvs
        writeKVs(100, 99L);
        // iterate all kvs
        KVIterator<String, String> iter = store.range(
            getKey(0),
            getKey(100));
        int idx = 0;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
        }
        assertEquals(100, idx, () -> "Add assertion message");
        iter.close();
        // delete range
        store.deleteRange(getKey(10), getKey(20), 100L);
        iter = store.range(
            getKey(0),
            getKey(100));
        idx = 0;
        while (iter.hasNext()) {
            KV<String, String> kv = iter.next();
            // Line removed due to Duplicate Assert: assertEquals(getKey(idx), kv.key(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(getValue(idx), kv.value(), () -> "Add assertion message");
            ++idx;
            if (10 == idx) {
                idx = 21;
            }
        }
        // Line removed due to Duplicate Assert: assertEquals(100, idx, () -> "Add assertion message");
        iter.close();
    }

    @Test
    public void testTxnCompareSuccess() throws Exception {
        store.init(spec);
        // write 10 kvs at revision 99L
        writeKVs(20, 99L);
        @Cleanup TxnOp<String, String> txnOp = store.getOpFactory().newTxn()
            .If(
                store.getOpFactory().compareCreateRevision(
                    CompareResult.EQUAL,
                    getKey(10),
                    99L))
            .Then(
                store.getOpFactory().newPut(
                    getKey(11),
                    "test-value",
                    Options.putAndGet()))
            .Else(
                store.getOpFactory().newDelete(
                    getKey(11),
                    store.getOpFactory().optionFactory().newDeleteOption()
                        .build()))
            .build();
        @Cleanup TxnResult<String, String> result = store.txn(100L, txnOp);
        assertEquals(Code.OK, result.code(), () -> "Add assertion message");
        assertEquals(100L, result.revision(), () -> "Add assertion message");
        assertEquals(1, result.results().size(), () -> "Add assertion message");
        assertTrue(result.isSuccess(), () -> "Add assertion message");
        Result<String, String> subResult = result.results().get(0);
        assertEquals(OpType.PUT, subResult.type(), () -> "Add assertion message");
        PutResult<String, String> putResult = (PutResult<String, String>) subResult;
        KeyValue<String, String> prevResult = putResult.prevKv();
        assertEquals(getKey(11), prevResult.key(), () -> "Add assertion message");
        assertEquals(getValue(11), prevResult.value(), () -> "Add assertion message");
        assertEquals(99L, prevResult.createRevision(), () -> "Add assertion message");
        assertEquals(99L, prevResult.modifiedRevision(), () -> "Add assertion message");
        assertEquals(0L, prevResult.version(), () -> "Add assertion message");

        assertEquals("test-value", store.get(getKey(11)), () -> "Add assertion message");
    }


    @Test
    public void testTxnCompareFailure() throws Exception {
        store.init(spec);
        // write 10 kvs at revision 99L
        writeKVs(20, 99L);
        @Cleanup TxnOp<String, String> txnOp = store.getOpFactory().newTxn()
            .If(
                store.getOpFactory().compareCreateRevision(
                    CompareResult.NOT_EQUAL,
                    getKey(10),
                    99L))
            .Then(
                store.getOpFactory().newPut(
                    getKey(11),
                    "test-value",
                    Options.putAndGet()))
            .Else(
                store.getOpFactory().newDelete(
                    getKey(11),
                    Options.deleteAndGet()))
            .build();
        @Cleanup TxnResult<String, String> result = store.txn(100L, txnOp);
        assertEquals(Code.OK, result.code(), () -> "Add assertion message");
        assertEquals(100L, result.revision(), () -> "Add assertion message");
        assertEquals(1, result.results().size(), () -> "Add assertion message");
        assertFalse(result.isSuccess(), () -> "Add assertion message");
        Result<String, String> subResult = result.results().get(0);
        assertEquals(OpType.DELETE, subResult.type(), () -> "Add assertion message");
        DeleteResult<String, String> deleteResult = (DeleteResult<String, String>) subResult;
        List<KeyValue<String, String>> prevResults = deleteResult.prevKvs();
        Integer magicNumber97272129 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber97272129, prevResults.size(), () -> "Add assertion message");
        KeyValue<String, String> prevResult = prevResults.get(0);
        assertEquals(getKey(11), prevResult.key(), () -> "Add assertion message");
        assertEquals(getValue(11), prevResult.value(), () -> "Add assertion message");
        assertEquals(99L, prevResult.createRevision(), () -> "Add assertion message");
        assertEquals(99L, prevResult.modifiedRevision(), () -> "Add assertion message");
        assertEquals(0L, prevResult.version(), () -> "Add assertion message");

        assertEquals(null, store.get(getKey(11)), () -> "Add assertion message");
    }

    @Test
    public void testIncrement() throws Exception {
        store.init(spec);

        for (int i = 0; i < 5; i++) {
            store.increment("key", 100L, 99L + i);
            Long number = store.getNumber("key");
            assertNotNull(number, () -> "Add assertion message");
            assertEquals(100L * (i + 1), number.longValue(), () -> "Add assertion message");
        }
    }

    @Test
    public void testIncrementFailure() throws Exception {
        store.init(spec);

        store.put("key", "value", 1L);
        try {
            store.increment("key", 100, 2L);
            fail("Should fail to increment a non-number key");
        } catch (MVCCStoreException e) {
            assertEquals(Code.ILLEGAL_OP, e.getCode(), () -> "Add assertion message");
        }
    }

}