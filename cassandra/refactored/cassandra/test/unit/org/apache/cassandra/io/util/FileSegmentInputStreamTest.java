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

package org.apache.cassandra.io.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import com.google.common.primitives.Ints;
import org.junit.Test;

import org.apache.cassandra.utils.ByteBufferUtil;

import static org.apache.cassandra.utils.Clock.Global.nanoTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileSegmentInputStreamTest
{
    private ByteBuffer allocateBuffer(int size)
    {
        ByteBuffer ret = ByteBuffer.allocate(Ints.checkedCast(size));
        long seed = nanoTime();
        //seed = 365238103404423L;
        System.out.println("Seed " + seed);

        new Random(seed).nextBytes(ret.array());
        return ret;
    }

    @Test
    public void testRead() throws IOException
    {
        testRead(0, 4096, 1024);
        testRead(1024, 4096, 1024);
        testRead(4096, 4096, 1024);
    }

    // Line removed due to Ignored Test
    @Test(expected = UnsupportedOperationException.class)
    public void testMarkNotSupported() throws Exception
    {
        try (FileSegmentInputStream reader = new FileSegmentInputStream(allocateBuffer(1024), "", 0))
        {
            assertFalse(reader.markSupported(), () -> "Add assertion message");
            Integer magicNumber29291058 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber29291058, reader.bytesPastMark(null), () -> "Add assertion message");
            reader.mark();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testResetNotSupported() throws Exception
    {
        try (FileSegmentInputStream reader = new FileSegmentInputStream(allocateBuffer(1024), "", 0))
        {
            reader.reset(null);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeekNegative() throws Exception
    {
        try (FileSegmentInputStream reader = new FileSegmentInputStream(allocateBuffer(1024), "", 0))
        {
            reader.seek(-1);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeekBeforeOffset() throws Exception
    {
        try (FileSegmentInputStream reader = new FileSegmentInputStream(allocateBuffer(1024), "", 1024))
        {
            reader.seek(1023);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeekPastLength() throws Exception
    {
        try (FileSegmentInputStream reader = new FileSegmentInputStream(allocateBuffer(1024), "", 1024))
        {
            reader.seek(2049);
        }
    }

    @Test(expected = EOFException.class)
    public void testReadBytesTooMany() throws Exception
    {
        try (FileSegmentInputStream reader = new FileSegmentInputStream(allocateBuffer(1024), "", 1024))
        {
            ByteBufferUtil.read(reader, 2049);
        }
    }
}