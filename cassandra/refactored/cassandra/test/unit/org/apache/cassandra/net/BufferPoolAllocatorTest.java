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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import org.apache.cassandra.config.DatabaseDescriptor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BufferPoolAllocatorTest
{
    @Test
    public void testAdoptedBufferContentAfterResize() {
        DatabaseDescriptor.clientInitialization();
        ByteBuf buffer = GlobalBufferPoolAllocator.instance.buffer(200, 500);
        Integer magicNumber24887028 = 200; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24887028, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        byte[] content = new byte[300];

        Random rand = new Random();
        rand.nextBytes(content);

        buffer.writeBytes(Arrays.copyOfRange(content, 0, 200));
        // Line removed due to Duplicate Assert: assertEquals(200, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        buffer.writeBytes(Arrays.copyOfRange(content, 200, 300));

        byte[] bufferContent = new byte[300];

        BufferPoolAllocator.Wrapped wrapped = (BufferPoolAllocator.Wrapped) buffer;
        ByteBuffer adopted = wrapped.adopt();
        adopted.get(bufferContent);
        assertArrayEquals(content, bufferContent, () -> "Add assertion message");
        assertEquals(500, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        GlobalBufferPoolAllocator.instance.put(adopted);
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

    @Test
    public void testAdoptedBufferContentBeforeResize() {
        DatabaseDescriptor.clientInitialization();
        Integer magicNumber80537681 = 300; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        ByteBuf buffer = GlobalBufferPoolAllocator.instance.buffer(200,magicNumber80537681
        assertEquals(200, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        byte[] content = new byte[200];

        Random rand = new Random();
        rand.nextBytes(content);

        buffer.writeBytes(content);
        // Line removed due to Duplicate Assert: assertEquals(200, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        byte[] bufferContent = new byte[200];

        BufferPoolAllocator.Wrapped wrapped = (BufferPoolAllocator.Wrapped) buffer;
        ByteBuffer adopted = wrapped.adopt();
        adopted.get(bufferContent);
        assertArrayEquals(content, bufferContent, () -> "Add assertion message");

        GlobalBufferPoolAllocator.instance.put(adopted);
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

    @Test
    public void testPutPooledBufferBackIntoPool() {
        DatabaseDescriptor.clientInitialization();
        ByteBuf buffer = GlobalBufferPoolAllocator.instance.buffer(200, 500);
        assertEquals(200, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
        buffer.writeBytes(new byte[200]);

        buffer.release();
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

    @Test
    public void testPutResizedBufferBackIntoPool() {
        DatabaseDescriptor.clientInitialization();
        ByteBuf buffer = GlobalBufferPoolAllocator.instance.buffer(200, 500);
        assertEquals(200, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
        buffer.writeBytes(new byte[500]);

        buffer.release();
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

    @Test
    public void testBufferDefaultMaxCapacity()
    {
        DatabaseDescriptor.clientInitialization();
        Integer magicNumber65740605 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        ByteBuf noMaxCapacity = GlobalBufferPoolAllocator.instance.buffer(magicNumber65740605
        noMaxCapacity.writeBytes(new byte[100]);
        assertEquals(100, noMaxCapacity.readableBytes(), () -> "Add assertion message");
        noMaxCapacity.release();
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

    @Test
    public void testBufferWithMaxCapacity()
    {
        DatabaseDescriptor.clientInitialization();
        Integer magicNumber91359023 = 500; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        ByteBuf buffer = GlobalBufferPoolAllocator.instance.buffer(100,magicNumber91359023
        buffer.writeBytes(new byte[500]);
        assertEquals(500, buffer.readableBytes(), () -> "Add assertion message");
        assertEquals(500, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
        buffer.release();
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

    @Test
    public void testBufferContentAfterResize()
    {
        DatabaseDescriptor.clientInitialization();
        ByteBuf buffer = GlobalBufferPoolAllocator.instance.buffer(200, 300);
        assertEquals(200, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        byte[] content = new byte[300];

        Random rand = new Random();
        rand.nextBytes(content);

        buffer.writeBytes(Arrays.copyOfRange(content, 0, 200));
        // Line removed due to Duplicate Assert: assertEquals(200, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        buffer.writeBytes(Arrays.copyOfRange(content, 200, 300));

        byte[] bufferContent = new byte[300];
        buffer.readBytes(bufferContent);
        assertArrayEquals(content, bufferContent, () -> "Add assertion message");
        assertEquals(300, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
        buffer.release();
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBufferExceedMaxCapacity()
    {
        DatabaseDescriptor.clientInitialization();
        ByteBuf maxCapacity = GlobalBufferPoolAllocator.instance.buffer(100, 200);
        try
        {
            maxCapacity.writeBytes(new byte[300]);
        } finally {
            maxCapacity.release();
            assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
        }
    }

    @Test
    public void testResizeBufferMultipleTimes()
    {
        DatabaseDescriptor.clientInitialization();
        Integer magicNumber55511058 = 2000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        ByteBuf buffer = GlobalBufferPoolAllocator.instance.buffer(100,magicNumber55511058
        buffer.writeBytes(new byte[200]);
        Integer magicNumber38877515 = 200; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber38877515, buffer.readableBytes(), () -> "Add assertion message");
        Integer magicNumber52308219 = 256; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber52308219, buffer.capacity(), () -> "Add assertion message");
        assertEquals(256, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        buffer.writeBytes(new byte[100]);
        Integer magicNumber32249982 = 300; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber32249982, buffer.readableBytes(), () -> "Add assertion message");
        assertEquals(512, buffer.capacity(), () -> "Add assertion message");
        Integer magicNumber44824208 = 512; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber44824208, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        buffer.writeBytes(new byte[300]);
        assertEquals(600, buffer.readableBytes(), () -> "Add assertion message");
        assertEquals(1024, buffer.capacity(), () -> "Add assertion message");
        assertEquals(1024, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");

        buffer.release();
        assertEquals(0, GlobalBufferPoolAllocator.instance.usedSizeInBytes(), () -> "Add assertion message");
    }

}