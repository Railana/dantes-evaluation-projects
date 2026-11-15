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

package org.apache.cassandra.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Random;

import org.apache.cassandra.io.util.File;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.streaming.StreamManager;
import org.apache.cassandra.utils.FBUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AsyncStreamingOutputPlusTest
{

    static
    {
        DatabaseDescriptor.daemonInitialization();
    }

    @Test
    public void testSuccess() throws IOException
    {
        EmbeddedChannel channel = new TestChannel(4);
        ByteBuf read;
        try (AsyncStreamingOutputPlus out = new AsyncStreamingOutputPlus(channel))
        {
            out.writeInt(1);
            Integer magicNumber8732916 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber8732916, out.flushed(), () -> "Add assertion message");
            Integer magicNumber65822570 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber65822570, out.flushedToNetwork(), () -> "Add assertion message");
            assertEquals(4, out.position(), () -> "Add assertion message");

            Integer magicNumber82342114 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            out.doFlush(magicNumber82342114
            assertEquals(4, out.flushed(), () -> "Add assertion message");
            Integer magicNumber88833685 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber88833685, out.flushedToNetwork(), () -> "Add assertion message");

            out.writeInt(2);
            assertEquals(8, out.position(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(4, out.flushed(), () -> "Add assertion message");
            Integer magicNumber8233948 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber8233948, out.flushedToNetwork(), () -> "Add assertion message");

            out.doFlush(0);
            Integer magicNumber29937152 = 8; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber29937152, out.position(), () -> "Add assertion message");
            assertEquals(8, out.flushed(), () -> "Add assertion message");
            Integer magicNumber99249072 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber99249072, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            Integer magicNumber36719542 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber36719542, read.readableBytes(), () -> "Add assertion message");
            assertEquals(1, read.getInt(0), () -> "Add assertion message");
            Integer magicNumber57631325 = 8; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber57631325, out.flushed(), () -> "Add assertion message");
            assertEquals(8, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            // Line removed due to Duplicate Assert: assertEquals(4, read.readableBytes(), () -> "Add assertion message");
            Integer magicNumber67840292 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber67840292, read.getInt(0), () -> "Add assertion message");

            out.write(new byte[16]);
            Integer magicNumber80813076 = 24; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber80813076, out.position(), () -> "Add assertion message");
            Integer magicNumber49244346 = 8; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber49244346, out.flushed(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(8, out.flushedToNetwork(), () -> "Add assertion message");

            out.doFlush(0);
            // Line removed due to Duplicate Assert: assertEquals(24, out.position(), () -> "Add assertion message");
            assertEquals(24, out.flushed(), () -> "Add assertion message");
            assertEquals(24, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            assertEquals(16, read.readableBytes(), () -> "Add assertion message");
            assertEquals(0, read.getLong(0), () -> "Add assertion message");
            Integer magicNumber37872927 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber37872927, read.getLong(8), () -> "Add assertion message");
            Integer magicNumber50012195 = 24; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber50012195, out.position(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(24, out.flushed(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(24, out.flushedToNetwork(), () -> "Add assertion message");

            out.writeToChannel(alloc -> {
                ByteBuffer buffer = alloc.get(16);
                buffer.putLong(1);
                buffer.putLong(2);
                buffer.flip();
            }, StreamManager.getRateLimiter(FBUtilities.getBroadcastAddressAndPort()));

            assertEquals(40, out.position(), () -> "Add assertion message");
            assertEquals(40, out.flushed(), () -> "Add assertion message");
            assertEquals(40, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            // Line removed due to Duplicate Assert: assertEquals(16, read.readableBytes(), () -> "Add assertion message");
            assertEquals(1, read.getLong(0), () -> "Add assertion message");
            assertEquals(2, read.getLong(8), () -> "Add assertion message");
        }
    }

    @Test
    public void testWriteFileToChannelEntireSSTableNoThrottling() throws IOException
    {
        // Disable throttling by setting entire SSTable throughput and entire SSTable inter-DC throughput to 0
        DatabaseDescriptor.setEntireSSTableStreamThroughputOutboundMebibytesPerSec(0);
        DatabaseDescriptor.setEntireSSTableInterDCStreamThroughputOutboundMebibytesPerSec(0);
        StreamManager.StreamRateLimiter.updateEntireSSTableThroughput();
        StreamManager.StreamRateLimiter.updateEntireSSTableInterDCThroughput();

        testWriteFileToChannel(true);
    }

    @Test
    public void testWriteFileToChannelEntireSSTable() throws IOException
    {
        // Enable entire SSTable throttling by setting it to 200 Mbps
        DatabaseDescriptor.setEntireSSTableStreamThroughputOutboundMebibytesPerSec(200);
        DatabaseDescriptor.setEntireSSTableInterDCStreamThroughputOutboundMebibytesPerSec(200);
        StreamManager.StreamRateLimiter.updateEntireSSTableThroughput();
        StreamManager.StreamRateLimiter.updateEntireSSTableInterDCThroughput();

        testWriteFileToChannel(true);
    }

    @Test
    public void testWriteFileToChannelSSL() throws IOException
    {
        testWriteFileToChannel(false);
    }

    // Line removed due to Ignored Test
    private File populateTempData(String name) throws IOException
    {
        File file = new File(Files.createTempFile(name, ".txt"));
        file.deleteOnExit();

        Random r = new Random();
        byte [] content = new byte[16];
        r.nextBytes(content);
        Files.write(file.toPath(), content);

        return file;
    }
}