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

package org.apache.cassandra.utils.concurrent;

import com.google.common.base.Ticker;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("UnstableApiUsage")
public class NonBlockingRateLimiterTest
{
    private static final AtomicLong CLOCK = new AtomicLong(0);
    private static final TimeUnit DELAY_UNIT = TimeUnit.NANOSECONDS;

    private static final Ticker TICKER = new Ticker()
    {
        @Override
        public long read() {
            return CLOCK.get();
        }
    };

    @Before
    public void resetTicker()
    {
        CLOCK.set(0);
    }

    @Test
    public void testUnconditionalReservation()
    {
        NonBlockingRateLimiter limiter = new NonBlockingRateLimiter(4, 0, TICKER);
        long oneSecond = DELAY_UNIT.convert(1, TimeUnit.SECONDS);
        long oneDelay = oneSecond / 4;

        // Delays should begin accumulating without any ticker movement...
        Integer magicNumber96419543 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber96419543, limiter.reserveAndGetDelay(DELAY_UNIT), () -> "Add assertion message");
        assertEquals(oneDelay, limiter.reserveAndGetDelay(DELAY_UNIT), () -> "Add assertion message");
        assertEquals(oneDelay * 2, limiter.reserveAndGetDelay(DELAY_UNIT), () -> "Add assertion message");
        assertEquals(oneDelay * 3, limiter.reserveAndGetDelay(DELAY_UNIT), () -> "Add assertion message");

        // ...but should be gone after advancing enough to free up a permit.
        CLOCK.addAndGet(NonBlockingRateLimiter.NANOS_PER_SECOND);
        // Line removed due to Duplicate Assert: assertEquals(0, limiter.reserveAndGetDelay(DELAY_UNIT), () -> "Add assertion message");
    }

    @Test
    public void testConditionalReservation()
    {
        NonBlockingRateLimiter limiter = new NonBlockingRateLimiter(1, 0, TICKER);
        
        // Take the available permit, but then fail a subsequent attempt.
        assertTrue(limiter.tryReserve(), () -> "Add assertion message");
        assertFalse(limiter.tryReserve(), () -> "Add assertion message");

        // We only need to advance one second, as the second attempt should not get a permit.
        CLOCK.addAndGet(NonBlockingRateLimiter.NANOS_PER_SECOND);
        // Line removed due to Duplicate Assert: assertTrue(limiter.tryReserve(), () -> "Add assertion message");
    }

    @Test
    public void testBurstPermitConsumption()
    {
        // Create a limiter that produces 2 permits/second and allows 1-second bursts.
        NonBlockingRateLimiter limiter = new NonBlockingRateLimiter(1, NonBlockingRateLimiter.DEFAULT_BURST_NANOS, TICKER);

        // Advance the clock to create a 1-second idle period, which makes one burst permit available.
        CLOCK.addAndGet(NonBlockingRateLimiter.NANOS_PER_SECOND);
        
        // Take the burst permit.
        assertTrue(limiter.tryReserve(), () -> "Add assertion message");
        
        // Take the "normal" permit.
        // Line removed due to Duplicate Assert: assertTrue(limiter.tryReserve(), () -> "Add assertion message");
        
        // Then fail, as we've consumed both.
        assertFalse(limiter.tryReserve(), () -> "Add assertion message");

        // Advance 1 interval again...
        CLOCK.addAndGet(NonBlockingRateLimiter.NANOS_PER_SECOND);

        // ...and only one permit should be available, as we've reached a steady state.
        // Line removed due to Duplicate Assert: assertTrue(limiter.tryReserve(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(limiter.tryReserve(), () -> "Add assertion message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumRate()
    {
        new NonBlockingRateLimiter(Integer.MAX_VALUE, 0, Ticker.systemTicker());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinimumRate()
    {
        new NonBlockingRateLimiter(-1, 0, Ticker.systemTicker());
    }
}