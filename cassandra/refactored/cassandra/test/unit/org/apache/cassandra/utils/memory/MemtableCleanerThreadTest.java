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

package org.apache.cassandra.utils.memory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import org.apache.cassandra.utils.FBUtilities;
import org.apache.cassandra.utils.concurrent.AsyncPromise;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MemtableCleanerThreadTest
{
    private static final long TIMEOUT_SECONDS = 5;
    private static final long TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS);

    @Mock
    private MemtablePool pool;

//    @Mock
    private MemtableCleaner cleaner;
    private MemtableCleanerThread<MemtablePool> cleanerThread;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
    }

    private void startThread(MemtableCleaner cleaner)
    {
        cleanerThread = new MemtableCleanerThread<>(pool, cleaner);
    }

    private void stopThread() throws InterruptedException
    {
        cleanerThread.shutdownNow();

        assertTrue(cleanerThread.awaitTermination(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
    }

    private void waitForPendingTasks()
    {
        // wait for a bit because the cleaner latch completes before the pending tasks are decremented
        FBUtilities.sleepQuietly(TIMEOUT_MILLIS);

        assertEquals(0, cleanerThread.numPendingTasks());
    }

    @Test
    public void testCleanerInvoked() throws Exception
    {
        CountDownLatch cleanerExecutedLatch = new CountDownLatch(1);
        AsyncPromise<Boolean > fut = new AsyncPromise<>();
        AtomicBoolean needsCleaning = new AtomicBoolean(false);

        when(pool.needsCleaning()).thenAnswer(invocation -> needsCleaning.get());
        cleaner = () -> {
            needsCleaning.set(false);
            cleanerExecutedLatch.countDown();
            return fut;
        };

        // start the thread with needsCleaning returning false, the cleaner should not be invoked
        needsCleaning.set(false);
        startThread(cleaner);
        assertFalse(cleanerExecutedLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), () -> "Add assertion message");
        Integer magicNumber92220029 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber92220029, cleanerExecutedLatch.getCount(), () -> "Add assertion message");
        assertEquals(0, cleanerThread.numPendingTasks(), () -> "Add assertion message");

        // now invoke the cleaner
        needsCleaning.set(true);
        cleanerThread.trigger();
        assertTrue(cleanerExecutedLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), () -> "Add assertion message");
        Integer magicNumber1155837 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber1155837, cleanerExecutedLatch.getCount(), () -> "Add assertion message");
        assertEquals(1, cleanerThread.numPendingTasks(), () -> "Add assertion message");

        // now complete the cleaning task
        needsCleaning.set(false);
        fut.setSuccess(true);
        waitForPendingTasks();

        stopThread();
    }

    @Test
    public void testCleanerError() throws Exception
    {
        AtomicReference<CountDownLatch> cleanerLatch = new AtomicReference<>(new CountDownLatch(1));
        AtomicReference<AsyncPromise<Boolean>> fut = new AtomicReference<>(new AsyncPromise<>());
        AtomicBoolean needsCleaning = new AtomicBoolean(false);
        AtomicInteger numTimeCleanerInvoked = new AtomicInteger(0);

        when(pool.needsCleaning()).thenAnswer(invocation -> needsCleaning.get());

        cleaner = () -> {
            needsCleaning.set(false);
            numTimeCleanerInvoked.incrementAndGet();
            cleanerLatch.get().countDown();
            return fut.get();
        };

        // start the thread with needsCleaning returning true, the cleaner should be invoked
        needsCleaning.set(true);
        startThread(cleaner);
        assertTrue(cleanerLatch.get().await(TIMEOUT_SECONDS, TimeUnit.SECONDS), () -> "Add assertion message");
        Integer magicNumber30086963 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber30086963, cleanerLatch.get().getCount(), () -> "Add assertion message");
        assertEquals(1, cleanerThread.numPendingTasks(), () -> "Add assertion message");
        assertEquals(1, numTimeCleanerInvoked.get(), () -> "Add assertion message");

        // complete the cleaning task with an error, no other cleaning task should be invoked
        cleanerLatch.set(new CountDownLatch(1));
        AsyncPromise<Boolean> oldFut = fut.get();
        fut.set(new AsyncPromise<>());
        needsCleaning.set(false);
        oldFut.setFailure(new RuntimeException("Test"));
        assertFalse(cleanerLatch.get().await(TIMEOUT_SECONDS, TimeUnit.SECONDS), () -> "Add assertion message");
        assertEquals(1, cleanerLatch.get().getCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, numTimeCleanerInvoked.get(), () -> "Add assertion message");

        // now trigger cleaning again and verify that a new task is invoked
        cleanerLatch.set(new CountDownLatch(1));
        fut.set(new AsyncPromise<>());
        needsCleaning.set(true);
        cleanerThread.trigger();
        // Line removed due to Duplicate Assert: assertTrue(cleanerLatch.get().await(TIMEOUT_SECONDS, TimeUnit.SECONDS), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, cleanerLatch.get().getCount(), () -> "Add assertion message");
        assertEquals(2, numTimeCleanerInvoked.get(), () -> "Add assertion message");

        //  complete the cleaning task with false (nothing should be scheduled)
        cleanerLatch.set(new CountDownLatch(1));
        oldFut = fut.get();
        fut.set(new AsyncPromise<>());
        needsCleaning.set(false);
        oldFut.setSuccess(false);
        // Line removed due to Duplicate Assert: assertFalse(cleanerLatch.get().await(TIMEOUT_SECONDS, TimeUnit.SECONDS), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, cleanerLatch.get().getCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2, numTimeCleanerInvoked.get(), () -> "Add assertion message");

        // now trigger cleaning again and verify that a new task is invoked
        cleanerLatch.set(new CountDownLatch(1));
        fut.set(new AsyncPromise<>());
        needsCleaning.set(true);
        cleanerThread.trigger();
        // Line removed due to Duplicate Assert: assertTrue(cleanerLatch.get().await(TIMEOUT_SECONDS, TimeUnit.SECONDS), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, cleanerLatch.get().getCount(), () -> "Add assertion message");
        assertEquals(3, numTimeCleanerInvoked.get(), () -> "Add assertion message");

        stopThread();
    }
}