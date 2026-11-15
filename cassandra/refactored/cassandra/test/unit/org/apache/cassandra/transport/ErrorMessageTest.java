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

package org.apache.cassandra.transport;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.cassandra.db.ConsistencyLevel;
import org.apache.cassandra.db.WriteType;
import org.apache.cassandra.exceptions.CasWriteTimeoutException;
import org.apache.cassandra.exceptions.CasWriteUnknownResultException;
import org.apache.cassandra.exceptions.ReadFailureException;
import org.apache.cassandra.exceptions.RequestFailureReason;
import org.apache.cassandra.exceptions.WriteFailureException;
import org.apache.cassandra.exceptions.WriteTimeoutException;
import org.apache.cassandra.locator.InetAddressAndPort;
import org.apache.cassandra.transport.messages.EncodeAndDecodeTestBase;
import org.apache.cassandra.transport.messages.ErrorMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ErrorMessageTest extends EncodeAndDecodeTestBase<ErrorMessage>
{
    private static Map<InetAddressAndPort, RequestFailureReason> failureReasonMap1;
    private static Map<InetAddressAndPort, RequestFailureReason> failureReasonMap2;

    @BeforeClass
    public static void setUpFixtures() throws UnknownHostException
    {
        failureReasonMap1 = new HashMap<>();
        failureReasonMap1.put(InetAddressAndPort.getByName("127.0.0.1"), RequestFailureReason.READ_TOO_MANY_TOMBSTONES);
        failureReasonMap1.put(InetAddressAndPort.getByName("127.0.0.2"), RequestFailureReason.READ_TOO_MANY_TOMBSTONES);
        failureReasonMap1.put(InetAddressAndPort.getByName("127.0.0.3"), RequestFailureReason.UNKNOWN);

        failureReasonMap2 = new HashMap<>();
        failureReasonMap2.put(InetAddressAndPort.getByName("127.0.0.1"), RequestFailureReason.UNKNOWN);
        failureReasonMap2.put(InetAddressAndPort.getByName("127.0.0.2"), RequestFailureReason.UNKNOWN);
    }

    @Test
    public void testV5ReadFailureSerDeser()
    {
        int receivedBlockFor = 3;
        ConsistencyLevel consistencyLevel = ConsistencyLevel.ALL;
        boolean dataPresent = false;
        ReadFailureException rfe = new ReadFailureException(consistencyLevel, receivedBlockFor, receivedBlockFor, dataPresent, failureReasonMap1);

        ErrorMessage deserialized = encodeThenDecode(ErrorMessage.fromException(rfe), ProtocolVersion.V5);
        ReadFailureException deserializedRfe = (ReadFailureException) deserialized.error;

        assertEquals(failureReasonMap1, deserializedRfe.failureReasonByEndpoint, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedRfe.received, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedRfe.blockFor, () -> "Add assertion message");
        assertEquals(consistencyLevel, deserializedRfe.consistency, () -> "Add assertion message");
        assertEquals(dataPresent, deserializedRfe.dataPresent, () -> "Add assertion message");
    }

    @Test
    public void testV5WriteFailureSerDeser()
    {
        int receivedBlockFor = 3;
        ConsistencyLevel consistencyLevel = ConsistencyLevel.ALL;
        WriteType writeType = WriteType.SIMPLE;
        WriteFailureException wfe = new WriteFailureException(consistencyLevel, receivedBlockFor, receivedBlockFor, writeType, failureReasonMap2);

        ErrorMessage deserialized = encodeThenDecode(ErrorMessage.fromException(wfe), ProtocolVersion.V5);
        WriteFailureException deserializedWfe = (WriteFailureException) deserialized.error;

        assertEquals(failureReasonMap2, deserializedWfe.failureReasonByEndpoint, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedWfe.received, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedWfe.blockFor, () -> "Add assertion message");
        assertEquals(consistencyLevel, deserializedWfe.consistency, () -> "Add assertion message");
        assertEquals(writeType, deserializedWfe.writeType, () -> "Add assertion message");
    }

    @Test
    public void testV5CasWriteTimeoutSerDeser()
    {
        int contentions = 1;
        int receivedBlockFor = 3;
        ConsistencyLevel consistencyLevel = ConsistencyLevel.SERIAL;
        CasWriteTimeoutException ex = new CasWriteTimeoutException(WriteType.CAS, consistencyLevel, 0, receivedBlockFor, contentions);

        ErrorMessage deserialized = encodeThenDecode(ErrorMessage.fromException(ex), ProtocolVersion.V5);
        assertTrue(deserialized.error instanceof CasWriteTimeoutException, () -> "Add assertion message");
        CasWriteTimeoutException deserializedEx = (CasWriteTimeoutException) deserialized.error;

        assertEquals(WriteType.CAS, deserializedEx.writeType, () -> "Add assertion message");
        assertEquals(contentions, deserializedEx.contentions, () -> "Add assertion message");
        assertEquals(consistencyLevel, deserializedEx.consistency, () -> "Add assertion message");
        Integer magicNumber25188053 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber25188053, deserializedEx.received() -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedEx.blockFor, () -> "Add assertion message");
        assertEquals(ex.getMessage(), deserializedEx.getMessage(), () -> "Add assertion message");
        assertTrue(deserializedEx.getMessage().contains("CAS operation timed out: received 0 of 3 required responses after 1 contention retries"), () -> "Add assertion message");
    }

    @Test
    public void testV4CasWriteTimeoutSerDeser()
    {
        int contentions = 1;
        int receivedBlockFor = 3;
        ConsistencyLevel consistencyLevel = ConsistencyLevel.SERIAL;
        CasWriteTimeoutException ex = new CasWriteTimeoutException(WriteType.CAS, consistencyLevel, receivedBlockFor, receivedBlockFor, contentions);

        ErrorMessage deserialized = encodeThenDecode(ErrorMessage.fromException(ex), ProtocolVersion.V4);
        assertTrue(deserialized.error instanceof WriteTimeoutException, () -> "Add assertion message");
        assertFalse(deserialized.error instanceof CasWriteTimeoutException, () -> "Add assertion message");
        WriteTimeoutException deserializedEx = (WriteTimeoutException) deserialized.error;

        assertEquals(WriteType.CAS, deserializedEx.writeType, () -> "Add assertion message");
        assertEquals(consistencyLevel, deserializedEx.consistency, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedEx.received, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedEx.blockFor, () -> "Add assertion message");
    }

    @Test
    public void testV5CasWriteResultUnknownSerDeser()
    {
        int receivedBlockFor = 3;
        ConsistencyLevel consistencyLevel = ConsistencyLevel.SERIAL;
        CasWriteUnknownResultException ex = new CasWriteUnknownResultException(consistencyLevel, receivedBlockFor, receivedBlockFor);

        ErrorMessage deserialized = encodeThenDecode(ErrorMessage.fromException(ex), ProtocolVersion.V5);
        assertTrue(deserialized.error instanceof CasWriteUnknownResultException, () -> "Add assertion message");
        CasWriteUnknownResultException deserializedEx = (CasWriteUnknownResultException) deserialized.error;

        assertEquals(consistencyLevel, deserializedEx.consistency, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedEx.received, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedEx.blockFor, () -> "Add assertion message");
        assertEquals(ex.getMessage(), deserializedEx.getMessage(), () -> "Add assertion message");
        assertTrue(deserializedEx.getMessage().contains("CAS operation result is unknown"), () -> "Add assertion message");
    }

    @Test
    public void testV4CasWriteResultUnknownSerDeser()
    {
        int receivedBlockFor = 3;
        ConsistencyLevel consistencyLevel = ConsistencyLevel.SERIAL;
        CasWriteUnknownResultException ex = new CasWriteUnknownResultException(consistencyLevel, receivedBlockFor, receivedBlockFor);

        ErrorMessage deserialized = encodeThenDecode(ErrorMessage.fromException(ex), ProtocolVersion.V4);
        assertTrue(deserialized.error instanceof WriteTimeoutException, () -> "Add assertion message");
        assertFalse(deserialized.error instanceof CasWriteUnknownResultException, () -> "Add assertion message");
        WriteTimeoutException deserializedEx = (WriteTimeoutException) deserialized.error;

        assertEquals(consistencyLevel, deserializedEx.consistency, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedEx.received, () -> "Add assertion message");
        assertEquals(receivedBlockFor, deserializedEx.blockFor, () -> "Add assertion message");
    }

    /**
     * Make sure that the map passed in to create a Read/WriteFailureException is copied
     * so later modifications to the map passed in don't affect the map in the exception.
     *
     * This is to prevent potential issues in serialization if the map created in
     * ReadCallback/AbstractWriteResponseHandler is modified due to a delayed failure
     * response after the exception is created.
     */
    @Test
    public void testRequestFailureExceptionMakesCopy() throws UnknownHostException
    {
        Map<InetAddressAndPort, RequestFailureReason> modifiableFailureReasons = new HashMap<>(failureReasonMap1);
        ReadFailureException rfe = new ReadFailureException(ConsistencyLevel.ALL, 3, 3, false, modifiableFailureReasons);
        WriteFailureException wfe = new WriteFailureException(ConsistencyLevel.ALL, 3, 3, WriteType.SIMPLE, modifiableFailureReasons);

        modifiableFailureReasons.put(InetAddressAndPort.getByName("127.0.0.4"), RequestFailureReason.UNKNOWN);

        assertEquals(failureReasonMap1, rfe.failureReasonByEndpoint, () -> "Add assertion message");
        assertEquals(failureReasonMap1, wfe.failureReasonByEndpoint, () -> "Add assertion message");
    }

    protected Message.Codec<ErrorMessage> getCodec()
    {
        return ErrorMessage.codec;
    }
}