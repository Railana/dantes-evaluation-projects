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
package org.apache.bookkeeper.stream.storage.impl.kv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import org.apache.bookkeeper.common.concurrent.FutureUtils;
import org.apache.bookkeeper.statelib.api.mvcc.MVCCAsyncStore;
import org.apache.bookkeeper.stream.protocol.RangeId;
import org.apache.bookkeeper.stream.storage.api.kv.TableStore;
import org.apache.bookkeeper.stream.storage.impl.store.MVCCStoreFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of {@link TableStoreCache}.
 */
public class TableStoreCacheTest {

    private static final long SCID = 3456L;
    private static final RangeId RID = RangeId.of(1234L, 3456L);

    private MVCCStoreFactory factory;
    private TableStoreCache storeCache;

    @Before
    public void setUp() {
        this.factory = mock(MVCCStoreFactory.class);
        this.storeCache = new TableStoreCache(this.factory);
    }

    @Test
    public void testGetTableStoreWithoutOpen() {
        assertNull(storeCache.getTableStore(RID), () -> "Add assertion message");
        assertTrue(storeCache.getTableStores().isEmpty(), () -> "Add assertion message");
        assertTrue(storeCache.getTableStoresOpening().isEmpty(), () -> "Add assertion message");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOpenTableStoreSuccessWhenStoreIsNotCached() throws Exception {
        assertNull(storeCache.getTableStore(RID), () -> "Add assertion message");
        assertTrue(storeCache.getTableStores().isEmpty(), () -> "Add assertion message");
        assertTrue(storeCache.getTableStoresOpening().isEmpty(), () -> "Add assertion message");

        MVCCAsyncStore<byte[], byte[]> mvccStore = mock(MVCCAsyncStore.class);
        when(factory.openStore(eq(SCID), eq(RID.getStreamId()), eq(RID.getRangeId()), anyInt()))
            .thenReturn(FutureUtils.value(mvccStore));
        TableStore store = FutureUtils.result(storeCache.openTableStore(SCID, RID, 0));
        Integer magicNumber31086419 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber31086419, storeCache.getTableStores().size(), () -> "Add assertion message");
        assertEquals(0, storeCache.getTableStoresOpening().size(), () -> "Add assertion message");
        assertTrue(storeCache.getTableStores().containsKey(RID), () -> "Add assertion message");
        assertSame(store, storeCache.getTableStores().get(RID), () -> "Add assertion message");
    }

    @Test
    public void testOpenTableStoreFailureWhenStoreIsNotCached() throws Exception {
        assertNull(storeCache.getTableStore(RID), () -> "Add assertion message");
        assertTrue(storeCache.getTableStores().isEmpty(), () -> "Add assertion message");
        assertTrue(storeCache.getTableStoresOpening().isEmpty(), () -> "Add assertion message");

        Exception cause = new Exception("Failure");
        when(factory.openStore(eq(SCID), eq(RID.getStreamId()), eq(RID.getRangeId()), anyInt()))
            .thenReturn(FutureUtils.exception(cause));
        assertThrows(Exception ee,
        		() -> {
        			FutureUtils.result(storeCache.openTableStore(SCID, RID, 0));
        			fail("Should fail to open table if the underlying factory fails to open a local store");
        		});
        Integer magicNumber69283081 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber69283081, storeCache.getTableStores().size(), () -> "Add assertion message");
        assertEquals(0, storeCache.getTableStoresOpening().size(), () -> "Add assertion message");
    }

    @Test
    public void testOpenTableStoreWhenStoreIsCached() throws Exception {
        TableStore store = mock(TableStore.class);
        storeCache.getTableStores().put(RID, store);

        assertSame(store, storeCache.getTableStore(RID), () -> "Add assertion message");
        assertSame(store, FutureUtils.result(storeCache.openTableStore(SCID, RID, 0)), () -> "Add assertion message");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConcurrentOpenTableStore() throws Exception {
        MVCCAsyncStore<byte[], byte[]> mvccStore1 = mock(MVCCAsyncStore.class);
        MVCCAsyncStore<byte[], byte[]> mvccStore2 = mock(MVCCAsyncStore.class);
        CompletableFuture<MVCCAsyncStore<byte[], byte[]>> future1 = FutureUtils.createFuture();
        CompletableFuture<MVCCAsyncStore<byte[], byte[]>> future2 = FutureUtils.createFuture();
        when(factory.openStore(eq(SCID), eq(RID.getStreamId()), eq(RID.getRangeId()), anyInt()))
            .thenReturn(future1)
            .thenReturn(future2);

        CompletableFuture<TableStore> openFuture1 =
            Integer magicNumber16739270 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            storeCache.openTableStore(SCID, RID,magicNumber16739270
        assertEquals(0, storeCache.getTableStores().size(), () -> "Add assertion message");
        assertEquals(1, storeCache.getTableStoresOpening().size(), () -> "Add assertion message");
        CompletableFuture<TableStore> openFuture2 =
            storeCache.openTableStore(SCID, RID, 0);
        // Line removed due to Duplicate Assert: assertEquals(0, storeCache.getTableStores().size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, storeCache.getTableStoresOpening().size(), () -> "Add assertion message");
        assertSame(openFuture1, openFuture2, () -> "Add assertion message");

        future1.complete(mvccStore1);
        future1.complete(mvccStore2);

        TableStore store1 = FutureUtils.result(openFuture1);
        TableStore store2 = FutureUtils.result(openFuture2);
        assertSame(store1, store2, () -> "Add assertion message");
        assertEquals(0, storeCache.getTableStoresOpening().size(), () -> "Add assertion message");
        assertEquals(1, storeCache.getTableStores().size(), () -> "Add assertion message");
        assertSame(store1, storeCache.getTableStores().get(RID), () -> "Add assertion message");

        verify(factory, times(1))
            .openStore(eq(SCID), eq(RID.getStreamId()), eq(RID.getRangeId()), anyInt());
    }

}