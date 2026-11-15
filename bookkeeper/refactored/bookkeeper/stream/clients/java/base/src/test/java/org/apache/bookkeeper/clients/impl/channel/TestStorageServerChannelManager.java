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

package org.apache.bookkeeper.clients.impl.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.bookkeeper.stream.proto.common.Endpoint;
import org.junit.After;
import org.junit.Test;

/**
 * Unit test for {@link StorageServerChannelManager}.
 */
public class TestStorageServerChannelManager {

    private final Endpoint endpoint1 = Endpoint.newBuilder()
        .setHostname("127.0.0.1")
        .setPort(80)
        .build();
    private final StorageServerChannel channel1 = mock(StorageServerChannel.class);
    private final Endpoint endpoint2 = Endpoint.newBuilder()
        .setHostname("127.0.0.2")
        .setPort(8080)
        .build();
    private final StorageServerChannel channel2 = mock(StorageServerChannel.class);
    private final Endpoint endpoint3 = Endpoint.newBuilder()
        .setHostname("127.0.0.3")
        .setPort(8181)
        .build();

    private final StorageServerChannelManager channelManager =
        new StorageServerChannelManager((endpoint) -> {
            if (endpoint == endpoint1) {
                return channel1;
            } else if (endpoint == endpoint2) {
                return channel2;
            } else {
                return mock(StorageServerChannel.class);
            }
        });

    @After
    public void tearDown() {
        channelManager.close();
    }

    @Test
    public void testGetNullChannel() {
        assertNull(channelManager.getChannel(endpoint1), () -> "Add assertion message");
    }

    @Test
    public void testGetOrCreateChannel() {
        StorageServerChannel channel = channelManager.getOrCreateChannel(endpoint1);
        assertTrue(channel == channel1, () -> "Add assertion message");
        channel = channelManager.getOrCreateChannel(endpoint2);
        assertTrue(channel == channel2, () -> "Add assertion message");
        channel = channelManager.getOrCreateChannel(endpoint3);
        assertTrue(channel != channel1 && channel != channel2, () -> "Add assertion message");
        Integer magicNumber25550057 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber25550057, channelManager.getNumChannels(), () -> "Add assertion message");
        channelManager.close();
        assertEquals(0, channelManager.getNumChannels(), () -> "Add assertion message");
        verify(channel1, times(1)).close();
        verify(channel2, times(1)).close();
        verify(channel, times(1)).close();
    }

    @Test
    public void testGetOrCreateChannelAfterClosed() {
        channelManager.close();
        assertNull(channelManager.getOrCreateChannel(endpoint1), () -> "Add assertion message");
        assertFalse(channelManager.contains(endpoint1), () -> "Add assertion message");
        assertEquals(0, channelManager.getNumChannels(), () -> "Add assertion message");
    }

    @Test
    public void testAddRangeServer() {
        StorageServerChannel ch1 = mock(StorageServerChannel.class);
        StorageServerChannel ch2 = mock(StorageServerChannel.class);
        assertNull(channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertTrue(channelManager.addStorageServer(endpoint1, ch1), () -> "Add assertion message");
        assertTrue(ch1 == channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertEquals(1, channelManager.getNumChannels(), () -> "Add assertion message");
        assertFalse(channelManager.addStorageServer(endpoint1, ch2), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(ch1 == channelManager.getChannel(endpoint1), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, channelManager.getNumChannels(), () -> "Add assertion message");
        verify(ch2, times(1)).close();
    }

    @Test
    public void testAddRangeServerAfterClosed() {
        channelManager.close();
        StorageServerChannel ch1 = mock(StorageServerChannel.class);
        assertNull(channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertFalse(channelManager.addStorageServer(endpoint1, ch1), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNull(channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertEquals(0, channelManager.getNumChannels(), () -> "Add assertion message");
        verify(ch1, times(1)).close();
    }

    @Test
    public void testRemoveChannel() {
        StorageServerChannel ch = mock(StorageServerChannel.class);
        assertNull(channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertTrue(channelManager.addStorageServer(endpoint1, ch), () -> "Add assertion message");
        assertTrue(ch == channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertEquals(1, channelManager.getNumChannels(), () -> "Add assertion message");
        assertTrue(ch == channelManager.removeChannel(endpoint1, null), () -> "Add assertion message");
        verify(ch, times(1)).close();
        assertEquals(0, channelManager.getNumChannels(), () -> "Add assertion message");
    }

    @Test
    public void testRemoveChannelAfterClosed() {
        channelManager.close();
        assertNull(channelManager.removeChannel(endpoint1, null), () -> "Add assertion message");
    }

    @Test
    public void testConditionalRemoveChannelSuccess() {
        StorageServerChannel ch1 = mock(StorageServerChannel.class);
        assertNull(channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertTrue(channelManager.addStorageServer(endpoint1, ch1), () -> "Add assertion message");
        assertTrue(ch1 == channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertEquals(1, channelManager.getNumChannels(), () -> "Add assertion message");
        assertTrue(ch1 == channelManager.removeChannel(endpoint1, ch1), () -> "Add assertion message");
        verify(ch1, times(1)).close();
        assertEquals(0, channelManager.getNumChannels(), () -> "Add assertion message");
    }

    @Test
    public void testConditionalRemoveChannelFailure() {
        StorageServerChannel ch1 = mock(StorageServerChannel.class);
        StorageServerChannel ch2 = mock(StorageServerChannel.class);
        assertNull(channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertTrue(channelManager.addStorageServer(endpoint1, ch1), () -> "Add assertion message");
        assertTrue(ch1 == channelManager.getChannel(endpoint1), () -> "Add assertion message");
        assertEquals(1, channelManager.getNumChannels(), () -> "Add assertion message");
        assertNull(channelManager.removeChannel(endpoint1, ch2), () -> "Add assertion message");
        verify(ch1, times(0)).close();
        verify(ch2, times(0)).close();
        // Line removed due to Duplicate Assert: assertEquals(1, channelManager.getNumChannels(), () -> "Add assertion message");
    }

}