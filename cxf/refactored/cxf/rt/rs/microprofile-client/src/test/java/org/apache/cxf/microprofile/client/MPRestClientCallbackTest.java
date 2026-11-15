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

package org.apache.cxf.microprofile.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.client.InvocationCallback;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class MPRestClientCallbackTest {
    private Map<String, Object> ctx;
    private MPRestClientCallback<String> callback;
    private InvocationCallback<String> handler;
    private ScheduledExecutorService executor;
    private CompletableFuture<String> future;
    private Message message;
    
    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: executor = Executors.newSingleThreadScheduledExecutor();
        // Removed line due to General Fixture smell: handler = new InvocationCallback<String>() {
            @Override
            public void failed(Throwable throwable) {
            }
            
            @Override
            public void completed(String response) {
        handler = new InvocationCallback<String>() {
            }
        };
        // Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: message = new MessageImpl();
        // Removed line due to General Fixture smell: callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: future = (CompletableFuture<String>)callback.createFuture();
        // Removed line due to General Fixture smell: ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    // Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: @After
    public void tearDown() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    @Test
    public void testHandleResponseCallback() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        final CyclicBarrier barrier = new CyclicBarrier(2);

        Object[] result = new String[] {"results"};
        schedule(barrier, () -> callback.handleResponse(ctx, result));
        barrier.await(5, TimeUnit.SECONDS);
        
        assertThat(future.get(), equalTo("results"), () -> "Add assertion message");
        assertThat(future.get(10, TimeUnit.MILLISECONDS), equalTo("results"), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }
    
    @Test
    public void testGetResponseContextOnSuccessCallback() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        final CyclicBarrier barrier = new CyclicBarrier(2);

        Object[] result = new String[] {"results"};
        schedule(barrier, () -> callback.handleResponse(ctx, result));
        barrier.await(5, TimeUnit.SECONDS);
        
        assertThat(callback.getResponseContext(), equalTo(ctx), () -> "Add assertion message");
        assertThat(future.get(), equalTo("results"), () -> "Add assertion message");
        assertThat(future.get(10, TimeUnit.MILLISECONDS), equalTo("results"), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }
    
    @Test
    public void testHandleExceptionCallback() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        final CyclicBarrier barrier = new CyclicBarrier(1);
        schedule(barrier, () -> callback.handleException(ctx, new RuntimeException()));

        assertThrows(ExecutionException.class, () -> future.get());
        assertThrows(ExecutionException.class, () -> future.get(10, TimeUnit.MILLISECONDS));
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test
    public void testGetResponseContextOnExceptionCallback() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        final CyclicBarrier barrier = new CyclicBarrier(1);
        schedule(barrier, () -> callback.handleException(ctx, new RuntimeException()));

        assertThrows(ExecutionException.class, () -> callback.getResponseContext());
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test
    public void testHandleCancellationCallback() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        final CyclicBarrier barrier = new CyclicBarrier(2);
        schedule(barrier, () -> future.cancel(true));
        barrier.await(5, TimeUnit.SECONDS);

        assertThrows(CancellationException.class, () -> future.get());
        assertThrows(CancellationException.class, () -> future.get(10, TimeUnit.MILLISECONDS));
        assertThat(future.isCancelled(), equalTo(true), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test
    public void testHandleCancellationCallbackWhenStarted() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        callback.start(message);
        assertThat(future.cancel(true), equalTo(true), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(true), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test(expected = TimeoutException.class)
    public void testTimeout() throws Exception {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        future.get(10, TimeUnit.MILLISECONDS);
    }

    private void schedule(CyclicBarrier barrier, Runnable runnable) {
        
        message = new MessageImpl();
        callback = new MPRestClientCallback<String>(handler, message, String.class, null);
        future = (CompletableFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    }
    
    @After
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            barrier.await(5, TimeUnit.SECONDS);
            runnable.run();
            return null;
        }, 100, TimeUnit.MILLISECONDS);
    }
}