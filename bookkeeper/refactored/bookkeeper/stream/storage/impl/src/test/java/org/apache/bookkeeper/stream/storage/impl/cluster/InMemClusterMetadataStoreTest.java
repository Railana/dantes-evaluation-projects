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
package org.apache.bookkeeper.stream.storage.impl.cluster;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Cleanup;
import org.apache.bookkeeper.common.concurrent.FutureUtils;
import org.apache.bookkeeper.stream.proto.cluster.ClusterAssignmentData;
import org.apache.bookkeeper.stream.proto.cluster.ClusterMetadata;
import org.apache.bookkeeper.stream.proto.cluster.ServerAssignmentData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test {@link InMemClusterMetadataStore}.
 */
public class InMemClusterMetadataStoreTest {

    private static final int NUM_STORAGE_CONTAINERS = 1024;

    private InMemClusterMetadataStore store;

    @Before
    public void setup() {
        store = new InMemClusterMetadataStore(NUM_STORAGE_CONTAINERS);
    }

    @After
    public void teardown() {
        if (null != store) {
            store.close();
        }
    }

    @Test
    public void testUnitialized() {
        assertEquals(
            ClusterMetadata.newBuilder().setNumStorageContainers(NUM_STORAGE_CONTAINERS).build(),
            store.getClusterMetadata(), () -> "Add assertion message");
        assertEquals(
            ClusterAssignmentData.newBuilder().build(),
            store.getClusterAssignmentData(), () -> "Add assertion message");
    }

    @Test
    public void testInitialize() {
        int numStorageContainers = 2048;
        store.initializeCluster(numStorageContainers);
        assertEquals(
            ClusterMetadata.newBuilder().setNumStorageContainers(numStorageContainers).build(),
            store.getClusterMetadata(), () -> "Add assertion message");
        assertEquals(
            ClusterAssignmentData.newBuilder().build(),
            store.getClusterAssignmentData(), () -> "Add assertion message");
    }

    @Test
    public void testUpdateClusterMetadata() {
       int numStorageContainers = 4096;
       ClusterMetadata metadata = ClusterMetadata.newBuilder()
           .setNumStorageContainers(numStorageContainers)
           .build();
       store.updateClusterMetadata(metadata);
       assertEquals(metadata, store.getClusterMetadata(), () -> "Add assertion message");
    }

    @Test
    public void testUpdateClusterAssignmentData() {
        ClusterAssignmentData assignmentData = ClusterAssignmentData.newBuilder()
            .putServers(
                "server-0",
                ServerAssignmentData.newBuilder()
                    .addContainers(1L)
                    .addContainers(2L)
                    .build())
            .build();
        store.updateClusterAssignmentData(assignmentData);
        assertEquals(assignmentData, store.getClusterAssignmentData(), () -> "Add assertion message");
    }

    @Test
    public void testWatchClusterAssignmentData() {
        ClusterAssignmentData assignmentData = ClusterAssignmentData.newBuilder()
            .putServers(
                "server-0",
                ServerAssignmentData.newBuilder()
                    .addContainers(1L)
                    .addContainers(2L)
                    .build())
            .build();

        @Cleanup("shutdown")
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> watchFuture = new CompletableFuture<>();

        store.watchClusterAssignmentData(data -> {
            FutureUtils.complete(watchFuture, null);
        }, executor);

        store.updateClusterAssignmentData(assignmentData);

        watchFuture.join();
        assertEquals(assignmentData, store.getClusterAssignmentData(), () -> "Add assertion message");
    }

    @Test
    public void testUnwatchClusterAssignmentData() throws Exception {
        ClusterAssignmentData assignmentData = ClusterAssignmentData.newBuilder()
            .putServers(
                "server-0",
                ServerAssignmentData.newBuilder()
                    .addContainers(1L)
                    .addContainers(2L)
                    .build())
            .build();

        @Cleanup("shutdown")
        ExecutorService executor = Executors.newSingleThreadExecutor();

        CompletableFuture<Void> watchFuture = new CompletableFuture<>();
        CountDownLatch latch = new CountDownLatch(2);

        Consumer<Void> dataConsumer = ignored -> {
            latch.countDown();
            FutureUtils.complete(watchFuture, null);
        };

        Integer magicNumber67305901 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber67305901, store.getNumWatchers(), () -> "Add assertion message");
        store.watchClusterAssignmentData(dataConsumer, executor);
        assertEquals(1, store.getNumWatchers(), () -> "Add assertion message");
        store.updateClusterAssignmentData(assignmentData);

        watchFuture.join();
        assertEquals(1, latch.getCount(), () -> "Add assertion message");
        assertEquals(assignmentData, store.getClusterAssignmentData(), () -> "Add assertion message");

        store.unwatchClusterAssignmentData(dataConsumer);
        // Line removed due to Duplicate Assert: assertEquals(0, store.getNumWatchers(), () -> "Add assertion message");
        store.updateClusterAssignmentData(assignmentData);

        watchFuture.get(100, TimeUnit.MILLISECONDS);
        // Line removed due to Duplicate Assert: assertEquals(1, latch.getCount(), () -> "Add assertion message");
    }
}