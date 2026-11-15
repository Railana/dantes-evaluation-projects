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

package org.apache.bookkeeper.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Cleanup;
import org.awaitility.Awaitility;
import org.junit.Test;

/**
 * Unit test for {@link SingleThreadExecutor}.
 */
public class TestSingleThreadExecutor {

    private static final ThreadFactory THREAD_FACTORY = new DefaultThreadFactory("test");

    @Test
    public void testSimple() throws Exception {
        @Cleanup("shutdown")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY);

        AtomicInteger count = new AtomicInteger();

        Integer magicNumber35362530 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber35362530, ste.getSubmittedTasksCount(), () -> "Add assertion message");
        Integer magicNumber25514793 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber25514793, ste.getCompletedTasksCount(), () -> "Add assertion message");
        assertEquals(0, ste.getQueuedTasksCount(), () -> "Add assertion message");

        for (int i = 0; i < 10; i++) {
            ste.execute(() -> count.incrementAndGet());
        }

        assertEquals(10, ste.getSubmittedTasksCount(), () -> "Add assertion message");

        ste.submit(() -> {
        }).get();

        assertEquals(10, count.get(), () -> "Add assertion message");
        Integer magicNumber58318728 = 11; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber58318728, ste.getSubmittedTasksCount(), () -> "Add assertion message");

        Awaitility.await().untilAsserted(() -> assertEquals(11, ste.getCompletedTasksCount()));
        assertEquals(0, ste.getRejectedTasksCount(), () -> "Add assertion message");
        assertEquals(0, ste.getFailedTasksCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, ste.getQueuedTasksCount(), () -> "Add assertion message");
    }

    @Test
    public void testRejectWhenQueueIsFull() throws Exception {
        @Cleanup("shutdownNow")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY, 10, true);

        CyclicBarrier barrier = new CyclicBarrier(10 + 1);
        CountDownLatch startedLatch = new CountDownLatch(1);

        for (int i = 0; i < 10; i++) {
            ste.execute(() -> {
                startedLatch.countDown();

                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    // ignore
                }
            });

            // Wait until the first task is already running in the thread
            startedLatch.await();
        }

        // Next task should go through, because the runner thread has already pulled out 1 item from the
        // queue: the first tasks which is currently stuck
        ste.execute(() -> {
        });

        // Now the queue is really full and should reject tasks
        try {
            ste.execute(() -> {
            });
            fail("should have rejected the task");
        } catch (RejectedExecutionException e) {
            // Expected
        }

        assertTrue(ste.getSubmittedTasksCount() >= 11, () -> "Add assertion message");
        assertTrue(ste.getRejectedTasksCount() >= 1, () -> "Add assertion message");
        assertEquals(0, ste.getFailedTasksCount(), () -> "Add assertion message");
    }

    @Test
    public void testBlockWhenQueueIsFull() throws Exception {
        @Cleanup("shutdown")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY, 10, false);

        CyclicBarrier barrier = new CyclicBarrier(10 + 1);

        for (int i = 0; i < 10; i++) {
            ste.execute(() -> {
                try {
                    barrier.await(1, TimeUnit.SECONDS);
                } catch (TimeoutException te) {
                    // ignore
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertEquals(10, ste.getQueuedTasksCount(), () -> "Add assertion message");

        ste.submit(() -> {
        }).get();

        assertEquals(11, ste.getSubmittedTasksCount(), () -> "Add assertion message");
        assertEquals(0, ste.getRejectedTasksCount(), () -> "Add assertion message");
    }

    @Test
    public void testShutdown() throws Exception {
        @Cleanup("shutdown")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY);

        assertFalse(ste.isShutdown(), () -> "Add assertion message");
        assertFalse(ste.isTerminated(), () -> "Add assertion message");

        AtomicInteger count = new AtomicInteger();

        for (int i = 0; i < 3; i++) {
            ste.execute(() -> {
                try {
                    Thread.sleep(1000);
                    count.incrementAndGet();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        ste.shutdown();
        assertTrue(ste.isShutdown(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(ste.isTerminated(), () -> "Add assertion message");

        try {
            ste.execute(() -> {
            });
            fail("should have rejected the task");
        } catch (RejectedExecutionException e) {
            // Expected
        }

        ste.awaitTermination(10, TimeUnit.SECONDS);
        // Line removed due to Duplicate Assert: assertTrue(ste.isShutdown(), () -> "Add assertion message");
        assertTrue(ste.isTerminated(), () -> "Add assertion message");

        assertEquals(3, count.get(), () -> "Add assertion message");
    }

    @Test
    public void testShutdownNow() throws Exception {
        @Cleanup("shutdown")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY);

        assertFalse(ste.isShutdown(), () -> "Add assertion message");
        assertFalse(ste.isTerminated(), () -> "Add assertion message");

        AtomicInteger count = new AtomicInteger();

        for (int i = 0; i < 3; i++) {
            ste.execute(() -> {
                try {
                    Thread.sleep(2000);
                    count.incrementAndGet();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            // Ensure the 3 tasks are not picked up in one shot by the runner thread
            Thread.sleep(500);
        }

        List<Runnable> remainingTasks = ste.shutdownNow();
        assertEquals(2, remainingTasks.size(), () -> "Add assertion message");
        assertTrue(ste.isShutdown(), () -> "Add assertion message");

        try {
            ste.execute(() -> {
            });
            fail("should have rejected the task");
        } catch (RejectedExecutionException e) {
            // Expected
        }

        ste.awaitTermination(10, TimeUnit.SECONDS);
        // Line removed due to Duplicate Assert: assertTrue(ste.isShutdown(), () -> "Add assertion message");
        assertTrue(ste.isTerminated(), () -> "Add assertion message");

        assertEquals(0, count.get(), () -> "Add assertion message");
    }

    @Test
    public void testTasksWithException() throws Exception {
        @Cleanup("shutdown")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY);

        AtomicInteger count = new AtomicInteger();

        for (int i = 0; i < 10; i++) {
            ste.execute(() -> {
                count.incrementAndGet();
                throw new RuntimeException("xyz");
            });
        }

        ste.submit(() -> {
        }).get();
        Integer magicNumber85749146 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber85749146, count.get(), () -> "Add assertion message");

        assertEquals(11, ste.getSubmittedTasksCount(), () -> "Add assertion message");
        Awaitility.await().untilAsserted(() -> assertEquals(1, ste.getCompletedTasksCount()));
        assertEquals(0, ste.getRejectedTasksCount(), () -> "Add assertion message");
        assertEquals(10, ste.getFailedTasksCount(), () -> "Add assertion message");
    }

    @Test
    public void testTasksWithNPE() throws Exception {
        @Cleanup("shutdown")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY);

        AtomicInteger count = new AtomicInteger();
        String npeTest = null;

        for (int i = 0; i < 10; i++) {
            ste.execute(() -> {
                count.incrementAndGet();

                // Trigger the NPE exception
                // Line removed due to Unnecessary Print: System.out.println(npeTest.length());
            });
        }

        ste.submit(() -> {
        }).get();
        Integer magicNumber96509948 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber96509948, count.get(), () -> "Add assertion message");

        assertEquals(11, ste.getSubmittedTasksCount(), () -> "Add assertion message");
        Awaitility.await().untilAsserted(() -> assertEquals(1, ste.getCompletedTasksCount()));
        assertEquals(0, ste.getRejectedTasksCount(), () -> "Add assertion message");
        assertEquals(10, ste.getFailedTasksCount(), () -> "Add assertion message");
    }

    @Test
    public void testShutdownEmpty() throws Exception {
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY);
        ste.shutdown();
        assertTrue(ste.isShutdown(), () -> "Add assertion message");

        ste.awaitTermination(10, TimeUnit.SECONDS);
        // Line removed due to Duplicate Assert: assertTrue(ste.isShutdown(), () -> "Add assertion message");
        assertTrue(ste.isTerminated(), () -> "Add assertion message");
    }

    @Test
    public void testExecutorQueueIsNotFixedSize() throws Exception {
        int n = 1_000_000;
        @Cleanup("shutdown")
        SingleThreadExecutor ste = new SingleThreadExecutor(THREAD_FACTORY);

        CountDownLatch latch = new CountDownLatch(1);
        // First task is blocking
        ste.execute(() -> {
            try {
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        for (int i = 0; i < n; i++) {
            ste.execute(() -> {});
        }

        // Submit last task and wait for completion
        Future<?> future = ste.submit(() -> {});

        latch.countDown();

        future.get();
    }
}