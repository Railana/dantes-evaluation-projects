/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.cxf.message.MessageImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class ClientCallbackTest {
    private Map<String, Object> ctx;
    private ClientCallback callback;
    private ScheduledExecutorService executor;
    
    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: executor = Executors.newSingleThreadScheduledExecutor();
        callback = new ClientCallback();
        // Removed line due to General Fixture smell: ctx = new HashMap<String, Object>();
    }
    
    @After
    public void tearDown() throws Exception {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    @Test
    public void testHandleResponseCallback() throws Exception {
        ctx = new HashMap<String, Object>();
        final CyclicBarrier barrier = new CyclicBarrier(2);

        Object[] result = new Object[0];
        schedule(barrier, () -> callback.handleResponse(ctx, result));

        synchronized (callback) {
            barrier.await(5, TimeUnit.SECONDS);
            callback.wait();
        }

        assertThat(callback.get(), equalTo(result), () -> "Add assertion message");
        assertThat(callback.get(10, TimeUnit.MILLISECONDS), equalTo(result), () -> "Add assertion message");
        assertThat(callback.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(true), () -> "Add assertion message");
    }
    
    @Test
    public void testGetResponseContextOnSuccessCallback() throws Exception {
        ctx = new HashMap<String, Object>();
        final CyclicBarrier barrier = new CyclicBarrier(1);

        Object[] result = new Object[0];
        schedule(barrier, () -> callback.handleResponse(ctx, result));

        assertThat(callback.getResponseContext(), equalTo(ctx), () -> "Add assertion message");
        assertThat(callback.get(), equalTo(result), () -> "Add assertion message");
        assertThat(callback.get(10, TimeUnit.MILLISECONDS), equalTo(result), () -> "Add assertion message");
        assertThat(callback.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(true), () -> "Add assertion message");
    }
    
    @Test
    public void testHandleExceptionCallback() throws Exception {
        ctx = new HashMap<String, Object>();
        final CyclicBarrier barrier = new CyclicBarrier(2);
        schedule(barrier, () -> callback.handleException(ctx, new RuntimeException()));

        synchronized (callback) {
            barrier.await(5, TimeUnit.SECONDS);
            callback.wait();
        }

        assertThrows(ExecutionException.class, () -> callback.get());
        assertThrows(ExecutionException.class, () -> callback.get(10, TimeUnit.MILLISECONDS));
        assertThat(callback.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test
    public void testGetResponseContextOnExceptionCallback() throws Exception {
        ctx = new HashMap<String, Object>();
        final CyclicBarrier barrier = new CyclicBarrier(1);
        schedule(barrier, () -> callback.handleException(ctx, new RuntimeException()));

        assertThrows(ExecutionException.class, () -> callback.getResponseContext());
        assertThat(callback.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test
    public void testHandleCancellationCallback() throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        schedule(barrier, () -> callback.cancel(true));

        synchronized (callback) {
            barrier.await(5, TimeUnit.SECONDS);
            callback.wait();
        }

        assertThrows(InterruptedException.class, () -> callback.get());
        assertThrows(InterruptedException.class, () -> callback.get(10, TimeUnit.MILLISECONDS));
        assertThat(callback.isCancelled(), equalTo(true), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(true), () -> "Add assertion message");
    }
    

    @Test
    public void testHandleCancellationCallbackWhenStarted() throws Exception {
        callback.start(new MessageImpl());
        assertThat(callback.cancel(true), equalTo(false), () -> "Add assertion message");
        assertThat(callback.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(false), () -> "Add assertion message");
    }

    @Test
    public void testGetResponseContextOnCancellationCallback() throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(1);
        schedule(barrier, () -> callback.cancel(true));

        assertThrows(InterruptedException.class, () -> callback.getResponseContext());
        assertThat(callback.isCancelled(), equalTo(true), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test(expected = TimeoutException.class)
    public void testTimeout() throws Exception {
        callback.get(10, TimeUnit.MILLISECONDS);
    }

    private void schedule(CyclicBarrier barrier, Runnable runnable) {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            barrier.await(5, TimeUnit.SECONDS);
            runnable.run();
            return null;
        }, 100, TimeUnit.MILLISECONDS);
    }
}