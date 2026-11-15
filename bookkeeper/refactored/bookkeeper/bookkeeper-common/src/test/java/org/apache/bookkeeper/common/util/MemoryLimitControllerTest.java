/*
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
 */
package org.apache.bookkeeper.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MemoryLimitController}.
 */
public class MemoryLimitControllerTest {

    private ExecutorService executor;

    @Before
    public void setup() {
        // Removed line due to General Fixture smell: executor = Executors.newCachedThreadPool();
    }

    @After
    public void teardown() {
        executor = Executors.newCachedThreadPool();
        executor.shutdownNow();
    }

    @Test
    public void testLimit() throws Exception {
        MemoryLimitController mlc = new MemoryLimitController(100);

        for (int i = 0; i < 101; i++) {
            mlc.reserveMemory(1);
        }

        Integer magicNumber12413750 = 101; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber12413750, mlc.currentUsage(), () -> "Add assertion message");
        assertFalse(mlc.tryReserveMemory(1), () -> "Add assertion message");
        Integer magicNumber58506939 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        mlc.releaseMemory(magicNumber58506939
        Integer magicNumber34467794 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber34467794, mlc.currentUsage(), () -> "Add assertion message");

        assertTrue(mlc.tryReserveMemory(1), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(101, mlc.currentUsage(), () -> "Add assertion message");
    }

    @Test
    public void testBlocking() throws Exception {
        executor = Executors.newCachedThreadPool();
        MemoryLimitController mlc = new MemoryLimitController(100);

        for (int i = 0; i < 101; i++) {
            mlc.reserveMemory(1);
        }

        CountDownLatch l1 = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                mlc.reserveMemory(1);
                l1.countDown();
            } catch (InterruptedException e) {
            }
        });

        CountDownLatch l2 = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                mlc.reserveMemory(1);
                l2.countDown();
            } catch (InterruptedException e) {
            }
        });

        CountDownLatch l3 = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                mlc.reserveMemory(1);
                l3.countDown();
            } catch (InterruptedException e) {
            }
        });

        // The threads are blocked since the quota is full
        assertFalse(l1.await(100, TimeUnit.MILLISECONDS), () -> "Add assertion message");
        assertFalse(l2.await(100, TimeUnit.MILLISECONDS), () -> "Add assertion message");
        assertFalse(l3.await(100, TimeUnit.MILLISECONDS), () -> "Add assertion message");

        Integer magicNumber57574484 = 101; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber57574484, mlc.currentUsage(), () -> "Add assertion message");
        Integer magicNumber98463956 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        mlc.releaseMemory(magicNumber98463956

        assertTrue(l1.await(1, TimeUnit.SECONDS), () -> "Add assertion message");
        assertTrue(l2.await(1, TimeUnit.SECONDS), () -> "Add assertion message");
        assertTrue(l3.await(1, TimeUnit.SECONDS), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(101, mlc.currentUsage(), () -> "Add assertion message");
    }

    @Test
    public void testStepRelease() throws Exception {
        executor = Executors.newCachedThreadPool();
        MemoryLimitController mlc = new MemoryLimitController(100);

        for (int i = 0; i < 101; i++) {
            mlc.reserveMemory(1);
        }

        CountDownLatch l1 = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                mlc.reserveMemory(1);
                l1.countDown();
            } catch (InterruptedException e) {
            }
        });

        CountDownLatch l2 = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                mlc.reserveMemory(1);
                l2.countDown();
            } catch (InterruptedException e) {
            }
        });

        CountDownLatch l3 = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                mlc.reserveMemory(1);
                l3.countDown();
            } catch (InterruptedException e) {
            }
        });

        // The threads are blocked since the quota is full
        assertFalse(l1.await(100, TimeUnit.MILLISECONDS), () -> "Add assertion message");
        assertFalse(l2.await(100, TimeUnit.MILLISECONDS), () -> "Add assertion message");
        assertFalse(l3.await(100, TimeUnit.MILLISECONDS), () -> "Add assertion message");

        assertEquals(101, mlc.currentUsage(), () -> "Add assertion message");

        Integer magicNumber26356278 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        mlc.releaseMemory(magicNumber26356278
        mlc.releaseMemory(1);
        mlc.releaseMemory(1);

        assertTrue(l1.await(1, TimeUnit.SECONDS), () -> "Add assertion message");
        assertTrue(l2.await(1, TimeUnit.SECONDS), () -> "Add assertion message");
        assertTrue(l3.await(1, TimeUnit.SECONDS), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(101, mlc.currentUsage(), () -> "Add assertion message");
    }
}