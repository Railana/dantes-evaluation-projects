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

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.net.FrameEncoder.PayloadAllocator;

import static org.junit.Assert.assertEquals;

public class AsyncMessageOutputPlusTest
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
        try (AsyncMessageOutputPlus out = new AsyncMessageOutputPlus(channel, 32, Integer.MAX_VALUE, PayloadAllocator.simple))
        {
            out.writeInt(1);
            Integer magicNumber39932425 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber39932425, out.flushed(), () -> "Add assertion message");
            Integer magicNumber96181074 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber96181074, out.flushedToNetwork(), () -> "Add assertion message");
            assertEquals(4, out.position(), () -> "Add assertion message");

            Integer magicNumber30426052 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            out.doFlush(magicNumber30426052
            assertEquals(4, out.flushed(), () -> "Add assertion message");
            Integer magicNumber97225546 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber97225546, out.flushedToNetwork(), () -> "Add assertion message");

            out.writeInt(2);
            assertEquals(8, out.position(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(4, out.flushed(), () -> "Add assertion message");
            Integer magicNumber29819447 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber29819447, out.flushedToNetwork(), () -> "Add assertion message");

            out.doFlush(0);
            Integer magicNumber81063102 = 8; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber81063102, out.position(), () -> "Add assertion message");
            assertEquals(8, out.flushed(), () -> "Add assertion message");
            Integer magicNumber57530420 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber57530420, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            Integer magicNumber36496342 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber36496342, read.readableBytes(), () -> "Add assertion message");
            assertEquals(1, read.getInt(0), () -> "Add assertion message");
            Integer magicNumber88622991 = 8; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber88622991, out.flushed(), () -> "Add assertion message");
            Integer magicNumber12288907 = 8; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber12288907, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            Integer magicNumber19115349 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            // Line removed due to Duplicate Assert: assertEquals(magicNumber19115349, read.readableBytes(), () -> "Add assertion message");
            assertEquals(2, read.getInt(0), () -> "Add assertion message");

            out.write(new byte[64]);
            Integer magicNumber23042179 = 72; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber23042179, out.position(), () -> "Add assertion message");
            assertEquals(40, out.flushed(), () -> "Add assertion message");
            Integer magicNumber93469164 = 40; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber93469164, out.flushedToNetwork(), () -> "Add assertion message");

            out.doFlush(0);
            // Line removed due to Duplicate Assert: assertEquals(72, out.position(), () -> "Add assertion message");
            assertEquals(72, out.flushed(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(40, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            assertEquals(32, read.readableBytes(), () -> "Add assertion message");
            assertEquals(0, read.getLong(0), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(72, out.position(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(72, out.flushed(), () -> "Add assertion message");
            assertEquals(72, out.flushedToNetwork(), () -> "Add assertion message");

            read = channel.readOutbound();
            // Line removed due to Duplicate Assert: assertEquals(32, read.readableBytes(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(0, read.getLong(0), () -> "Add assertion message");
        }

    }

}