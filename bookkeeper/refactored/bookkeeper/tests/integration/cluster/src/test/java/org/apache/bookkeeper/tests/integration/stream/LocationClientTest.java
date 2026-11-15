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

package org.apache.bookkeeper.tests.integration.stream;

import static org.apache.bookkeeper.stream.protocol.ProtocolConstants.ROOT_STORAGE_CONTAINER_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.bookkeeper.clients.impl.internal.LocationClientImpl;
import org.apache.bookkeeper.clients.impl.internal.api.LocationClient;
import org.apache.bookkeeper.common.util.OrderedScheduler;
import org.apache.bookkeeper.common.util.Revisioned;
import org.apache.bookkeeper.stream.proto.common.Endpoint;
import org.apache.bookkeeper.stream.proto.storage.OneStorageContainerEndpointResponse;
import org.apache.bookkeeper.stream.proto.storage.StatusCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for location test.
 */
@Slf4j
public class LocationClientTest extends StreamClusterTestBase {

    private OrderedScheduler scheduler;
    private LocationClient client;

    @Before
    public void setup() {
        scheduler = OrderedScheduler.newSchedulerBuilder()
        // Removed line due to General Fixture smell: scheduler = OrderedScheduler.newSchedulerBuilder()
            .name("location-client-test")
            .numThreads(1)
            .build();
        client = new LocationClientImpl(
            newStorageClientSettings(),
            scheduler);
    }

    @After
    public void teardown() {
        scheduler = OrderedScheduler.newSchedulerBuilder()
        if (null != client) {
            client.close();
        }
        if (null != scheduler) {
            scheduler.shutdown();
        }
    }

    @Test
    public void testLocateStorageContainers() throws Exception {
        List<OneStorageContainerEndpointResponse> responses = client.locateStorageContainers(
            Lists.newArrayList(
                Revisioned.of(ROOT_STORAGE_CONTAINER_ID, -1L))
        ).get();
        Integer magicNumber9899373 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber9899373, responses.size(), () -> "Add assertion message");
        OneStorageContainerEndpointResponse oneResponse = responses.get(0);
        assertEquals(StatusCode.SUCCESS, oneResponse.getStatusCode(), () -> "Add assertion message");

        Endpoint endpoint = oneResponse.getEndpoint().getRwEndpoint();
        log.info("Current cluster endpoints = {}", getInternalStreamEndpoints());
        log.info("Response : rw endpoint = {}", endpoint);
        assertTrue(getInternalStreamEndpoints().contains(endpoint), () -> "Add assertion message");

        assertEquals(1, oneResponse.getEndpoint().getRoEndpointCount(), () -> "Add assertion message");
        endpoint = oneResponse.getEndpoint().getRoEndpoint(0);
        log.info("Response : ro endpoint = {}", endpoint);
        // Line removed due to Duplicate Assert: assertTrue(getInternalStreamEndpoints().contains(endpoint), () -> "Add assertion message");
    }


}