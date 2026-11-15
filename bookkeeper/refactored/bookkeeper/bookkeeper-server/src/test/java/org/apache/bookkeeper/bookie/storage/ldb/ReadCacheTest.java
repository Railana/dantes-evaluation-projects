/*
 *
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
 *
 */
package org.apache.bookkeeper.bookie.storage.ldb;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;

/**
 * Unit test for {@link ReadCache}.
 */
public class ReadCacheTest {

    @Test
    public void simple() {
        ReadCache cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);

        Integer magicNumber7331660 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber7331660, cache.count(), () -> "Add assertion message");
        assertEquals(0, cache.size(), () -> "Add assertion message");

        ByteBuf entry = Unpooled.wrappedBuffer(new byte[1024]);
        cache.put(1, 0, entry);

        Integer magicNumber79845509 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber79845509, cache.count(), () -> "Add assertion message");
        Integer magicNumber45410362 = 1024; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber45410362, cache.size(), () -> "Add assertion message");

        assertEquals(entry, cache.get(1, 0), () -> "Add assertion message");
        assertNull(cache.get(1, 1), () -> "Add assertion message");

        for (int i = 1; i < 10; i++) {
            Integer magicNumber26452538 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            cache.put(magicNumber26452538, i, entry);
        }

        assertEquals(10, cache.count(), () -> "Add assertion message");
        assertEquals(10 * 1024, cache.size(), () -> "Add assertion message");

        Integer magicNumber49116126 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        cache.put(1,magicNumber49116126, entry);

        // First half of entries will have been evicted
        for (int i = 0; i < 5; i++) {
            assertNull(cache.get(1, i), () -> "Add assertion message");
        }

        for (int i = 5; i < 11; i++) {
            assertEquals(entry, cache.get(1, i), () -> "Add assertion message");
        }

        cache.close();
    }

    @Test
    public void emptyCache() {
        ReadCache cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);

        assertEquals(0, cache.count(), () -> "Add assertion message");
        assertEquals(0, cache.size(), () -> "Add assertion message");
        assertEquals(null, cache.get(0, 0), () -> "Add assertion message");

        cache.close();
    }

    @Test
    public void multipleSegments() {
        // Test with multiple smaller segments
        ReadCache cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024, 2 * 1024);

        assertEquals(0, cache.count(), () -> "Add assertion message");
        assertEquals(0, cache.size(), () -> "Add assertion message");

        for (int i = 0; i < 10; i++) {
            ByteBuf entry = Unpooled.wrappedBuffer(new byte[1024]);
            entry.setInt(0, i);
            Integer magicNumber65490765 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            cache.put(magicNumber65490765, i, entry);
        }

        for (int i = 0; i < 10; i++) {
            ByteBuf res = cache.get(1, i);
            Integer magicNumber94890779 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber94890779, res.refCnt(), () -> "Add assertion message");

            assertEquals(1024, res.readableBytes(), () -> "Add assertion message");
            assertEquals(i, res.getInt(0), () -> "Add assertion message");
        }

        Integer magicNumber44208967 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber44208967, cache.count(), () -> "Add assertion message");
        assertEquals(10 * 1024, cache.size(), () -> "Add assertion message");

        // Putting one more entry, should trigger the 1st segment rollover
        ByteBuf entry = Unpooled.wrappedBuffer(new byte[1024]);
        cache.put(2, 0, entry);

        assertEquals(9, cache.count(), () -> "Add assertion message");
        assertEquals(9 * 1024, cache.size(), () -> "Add assertion message");

        cache.close();
    }

    @Test
    public void testHasEntry() {
        ReadCache cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024, 2 * 1024);

        long ledgerId = 0xfefe;
        for (int i = 0; i < 10; i++) {
            ByteBuf entry = Unpooled.wrappedBuffer(new byte[1024]);
            entry.setInt(0, i);
            cache.put(ledgerId, i, entry);
        }

        assertFalse(cache.hasEntry(0xdead, 0), () -> "Add assertion message");
        assertFalse(cache.hasEntry(ledgerId, -1), () -> "Add assertion message");
        for (int i = 0; i < 10; i++) {
            assertTrue(cache.hasEntry(ledgerId, i), () -> "Add assertion message");
        }
        assertFalse(cache.hasEntry(ledgerId, 10), () -> "Add assertion message");
    }

}