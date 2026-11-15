/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.bookkeeper.clients.impl.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.apache.bookkeeper.clients.grpc.GrpcClientTestBase;
import org.apache.bookkeeper.clients.impl.internal.api.LocationClient;
import org.apache.bookkeeper.clients.impl.internal.api.MetaRangeClient;
import org.apache.bookkeeper.common.concurrent.FutureUtils;
import org.apache.bookkeeper.common.util.Revisioned;
import org.apache.bookkeeper.stream.proto.StreamConfiguration;
import org.apache.bookkeeper.stream.proto.StreamProperties;
import org.apache.bookkeeper.stream.proto.storage.GetStreamRequest;
import org.apache.bookkeeper.stream.proto.storage.GetStreamResponse;
import org.apache.bookkeeper.stream.proto.storage.OneStorageContainerEndpointResponse;
import org.apache.bookkeeper.stream.proto.storage.RootRangeServiceGrpc.RootRangeServiceImplBase;
import org.apache.bookkeeper.stream.proto.storage.StatusCode;
import org.junit.Test;

/**
 * Test Case for {@link StorageServerClientManagerImpl}.
 */
public class TestStorageServerClientManagerImpl extends GrpcClientTestBase {

    @Override
    protected void doSetup() throws Exception {
    }

    @Override
    protected void doTeardown() throws Exception {
    }

    @Test
    public void testGetMetaRangeClient() throws Exception {
        long streamId = 3456L;
        StreamProperties props = StreamProperties.newBuilder()
            .setStorageContainerId(1234L)
            .setStreamId(streamId)
            .setStreamName("metaclient-stream")
            .setStreamConf(StreamConfiguration.newBuilder().build())
            .build();

        MetaRangeClientImpl metaRangeClient = serverManager.openMetaRangeClient(props);
        assertEquals(1234L, metaRangeClient.getStorageContainerClient().getStorageContainerId(), () -> "Add assertion message");
        assertTrue(props == metaRangeClient.getStreamProps(), () -> "Add assertion message");

        // the stream properties will be cached here
        assertEquals(props, FutureUtils.result(serverManager.getStreamProperties(streamId)), () -> "Add assertion message");

        // the metadata range client is cached as well
        assertEquals(metaRangeClient, FutureUtils.result(serverManager.openMetaRangeClient(streamId)), () -> "Add assertion message");
    }

    @Test
    public void testGetMetaRangeClientByStreamId() throws Exception {
        long streamId = 3456L;
        StreamProperties props = StreamProperties.newBuilder()
            .setStorageContainerId(1234L)
            .setStreamId(streamId)
            .setStreamName("metaclient-stream")
            .setStreamConf(StreamConfiguration.newBuilder().build())
            .build();

        RootRangeServiceImplBase rootRangeService = new RootRangeServiceImplBase() {
            @Override
            public void getStream(GetStreamRequest request,
                                  StreamObserver<GetStreamResponse> responseObserver) {
                responseObserver.onNext(GetStreamResponse.newBuilder()
                    .setCode(StatusCode.SUCCESS)
                    .setStreamProps(props)
                    .build());
                responseObserver.onCompleted();
            }
        };
        serviceRegistry.addService(rootRangeService.bindService());

        // the stream properties will be cached here
        assertEquals(props, FutureUtils.result(serverManager.getStreamProperties(streamId)), () -> "Add assertion message");

        // the metadata range client is cached as well
        MetaRangeClient client = FutureUtils.result(serverManager.openMetaRangeClient(streamId));
        assertEquals(props, client.getStreamProps(), () -> "Add assertion message");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetLocationClient() throws Exception {
        LocationClient lc = serverManager.getLocationClient();
        assertNotNull(lc, () -> "Add assertion message");
        assertEquals(lc, serverManager.getLocationClient(), () -> "Add assertion message");
        List<OneStorageContainerEndpointResponse> responses =
            FutureUtils.result(lc.locateStorageContainers(Lists.newArrayList(Revisioned.of(123L, 456L))));
        Integer magicNumber58232964 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber58232964, responses.size(), () -> "Add assertion message");
        assertEquals(StatusCode.SUCCESS, responses.get(0).getStatusCode(), () -> "Add assertion message");
        assertEquals(ENDPOINT, responses.get(0).getEndpoint().getRwEndpoint(), () -> "Add assertion message");
        assertEquals(0, responses.get(0).getEndpoint().getRoEndpointCount(), () -> "Add assertion message");
    }


}