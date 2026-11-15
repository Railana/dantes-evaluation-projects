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

package org.apache.bookkeeper.stream.protocol.util;

import static org.apache.bookkeeper.stream.protocol.ProtocolConstants.DEFAULT_STREAM_CONF;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.createCreateNamespaceRequest;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.createDeleteNamespaceRequest;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.createGetNamespaceRequest;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.isStreamCreated;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.isStreamWritable;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.keyRangeOverlaps;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.validateNamespaceName;
import static org.apache.bookkeeper.stream.protocol.util.ProtoUtils.validateStreamName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.bookkeeper.stream.proto.NamespaceConfiguration;
import org.apache.bookkeeper.stream.proto.RangeProperties;
import org.apache.bookkeeper.stream.proto.StreamMetadata.LifecycleState;
import org.apache.bookkeeper.stream.proto.StreamMetadata.ServingState;
import org.apache.bookkeeper.stream.proto.storage.CreateNamespaceRequest;
import org.apache.bookkeeper.stream.proto.storage.DeleteNamespaceRequest;
import org.apache.bookkeeper.stream.proto.storage.GetNamespaceRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Unit test of {@link ProtoUtils}.
 */
public class TestProtoUtils {

    @Rule
    public final TestName name = new TestName();

    @Test
    public void testValidateNamespaceName() {
        assertTrue(validateNamespaceName("namespace_name"), () -> "Add assertion message");
        assertTrue(validateNamespaceName("NamespaceName"), () -> "Add assertion message");
        assertTrue(validateNamespaceName("9NamespaceName"), () -> "Add assertion message");
        assertTrue(validateNamespaceName("namespace-name"), () -> "Add assertion message");
        assertTrue(validateNamespaceName("!namespace_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName(" namespace_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName("<namespace_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName(">namespace_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName(""), () -> "Add assertion message");
        assertFalse(validateNamespaceName(null), () -> "Add assertion message");
    }

    @Test
    public void testValidateStreamName() {
        assertTrue(validateStreamName("stream_name"), () -> "Add assertion message");
        assertTrue(validateStreamName("StreamName"), () -> "Add assertion message");
        assertTrue(validateStreamName("9StreamName"), () -> "Add assertion message");
        assertTrue(validateStreamName("stream-name"), () -> "Add assertion message");
        assertTrue(validateStreamName("!stream_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName(" stream_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName("<stream_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName(">stream_name"), () -> "Add assertion message");
        assertFalse(validateNamespaceName(""), () -> "Add assertion message");
        assertFalse(validateNamespaceName(null), () -> "Add assertion message");
    }

    @Test
    public void testKeyRangeOverlaps1() {
        assertFalse(keyRangeOverlaps(1000L, 2000L, 3000L, 4000L), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(1000L, 2000L, 1500L, 2500L), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(1000L, 2000L, 1500L, 1800L), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(1000L, 3500L, 3000L, 4000L), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(3200L, 3500L, 3000L, 4000L), () -> "Add assertion message");
    }

    @Test
    public void testKeyRangeOverlaps2() {
        assertFalse(keyRangeOverlaps(
            Pair.of(1000L, 2000L),
            Pair.of(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(1000L, 2000L),
            Pair.of(1500L, 2500L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(1000L, 2000L),
            Pair.of(1500L, 1800L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(1000L, 3500L),
            Pair.of(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(3200L, 3500L),
            Pair.of(3000L, 4000L)), () -> "Add assertion message");
    }

    private static RangeProperties createRangeMeta(long startKey, long endKey) {
        return RangeProperties.newBuilder()
            .setStartHashKey(startKey)
            .setEndHashKey(endKey)
            .setStorageContainerId(1234L)
            .setRangeId(1234L)
            .build();
    }

    @Test
    public void testKeyRangeOverlaps3() {
        assertFalse(keyRangeOverlaps(
            createRangeMeta(1000L, 2000L),
            createRangeMeta(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(1000L, 2000L),
            createRangeMeta(1500L, 2500L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(1000L, 2000L),
            createRangeMeta(1500L, 1800L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(1000L, 3500L),
            createRangeMeta(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(3200L, 3500L),
            createRangeMeta(3000L, 4000L)), () -> "Add assertion message");
    }

    @Test
    public void testKeyRangeOverlaps4() {
        assertFalse(keyRangeOverlaps(
            createRangeMeta(1000L, 2000L),
            Pair.of(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(1000L, 2000L),
            Pair.of(1500L, 2500L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(1000L, 2000L),
            Pair.of(1500L, 1800L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(1000L, 3500L),
            Pair.of(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            createRangeMeta(3200L, 3500L),
            Pair.of(3000L, 4000L)), () -> "Add assertion message");
    }

    @Test
    public void testKeyRangeOverlaps5() {
        assertFalse(keyRangeOverlaps(
            Pair.of(1000L, 2000L),
            createRangeMeta(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(1000L, 2000L),
            createRangeMeta(1500L, 2500L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(1000L, 2000L),
            createRangeMeta(1500L, 1800L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(1000L, 3500L),
            createRangeMeta(3000L, 4000L)), () -> "Add assertion message");
        assertTrue(keyRangeOverlaps(
            Pair.of(3200L, 3500L),
            createRangeMeta(3000L, 4000L)), () -> "Add assertion message");
    }

    @Test
    public void testIsStreamCreated() {
        assertFalse(isStreamCreated(LifecycleState.UNINIT), () -> "Add assertion message");
        assertFalse(isStreamCreated(LifecycleState.CREATING), () -> "Add assertion message");
        assertTrue(isStreamCreated(LifecycleState.CREATED), () -> "Add assertion message");
        assertTrue(isStreamCreated(LifecycleState.FENCING), () -> "Add assertion message");
        assertTrue(isStreamCreated(LifecycleState.FENCED), () -> "Add assertion message");
    }

    @Test
    public void testIsStreamWritable() {
        assertTrue(isStreamWritable(ServingState.WRITABLE), () -> "Add assertion message");
        assertFalse(isStreamWritable(ServingState.READONLY), () -> "Add assertion message");
    }

    //
    // Namespace API
    //

    @Test
    public void testCreateCreateNamespaceRequest() {
        NamespaceConfiguration nsConf = NamespaceConfiguration.newBuilder()
            .setDefaultStreamConf(DEFAULT_STREAM_CONF)
            .build();
        CreateNamespaceRequest request = createCreateNamespaceRequest(
            name.getMethodName(),
            nsConf);
        assertEquals(name.getMethodName(), request.getName(), () -> "Add assertion message");
        assertEquals(nsConf, request.getNsConf(), () -> "Add assertion message");
    }

    @Test
    public void testCreateDeleteNamespaceRequest() {
        DeleteNamespaceRequest request = createDeleteNamespaceRequest(
            name.getMethodName());
        assertEquals(name.getMethodName(), request.getName(), () -> "Add assertion message");
    }

    @Test
    public void testCreateGetNamespaceRequest() {
        GetNamespaceRequest request = createGetNamespaceRequest(
            name.getMethodName());
        assertEquals(name.getMethodName(), request.getName(), () -> "Add assertion message");
    }

}