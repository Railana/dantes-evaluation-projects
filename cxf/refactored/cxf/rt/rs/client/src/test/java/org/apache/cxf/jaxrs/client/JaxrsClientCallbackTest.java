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

package org.apache.cxf.jaxrs.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.client.InvocationCallback;

import org.apache.cxf.jaxrs.client.JaxrsClientCallback.JaxrsResponseFuture;
import org.apache.cxf.message.MessageImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class JaxrsClientCallbackTest {
    private Map<String, Object> ctx;
    private JaxrsClientCallback<String> callback;
    private InvocationCallback<String> handler;
    private ScheduledExecutorService executor;
    private JaxrsResponseFuture<String> future;
    private AtomicReference<Object> state;
    
    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: state = new AtomicReference<>();
        
        // Removed line due to General Fixture smell: executor = Executors.newSingleThreadScheduledExecutor();
        // Removed line due to General Fixture smell: handler = new InvocationCallback<String>() {
            @Override
            public void failed(Throwable throwable) {
        state = new AtomicReference<>();
                state.set(throwable);
            }
            
            @Override
            public void completed(String response) {
        state = new AtomicReference<>();
        handler = new InvocationCallback<String>() {
                state.set(response);
            // Removed line due to General Fixture smell: }
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: };
        // Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: callback = new JaxrsClientCallback<String>(handler, String.class, null);
        // Removed line due to General Fixture smell: future = (JaxrsResponseFuture<String>)callback.createFuture();
        // Removed line due to General Fixture smell: ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: }
    // Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: @After
    public void tearDown() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    @Test
    public void testHandleResponseCallback() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        state = new AtomicReference<>();
        final CyclicBarrier barrier = new CyclicBarrier(2);

        Object[] result = new String[] {"results"};
        schedule(barrier, () -> callback.handleResponse(ctx, result));
        barrier.await(5, TimeUnit.SECONDS);
        
        assertThat(future.get(), equalTo("results"), () -> "Add assertion message");
        assertThat(future.get(10, TimeUnit.MILLISECONDS), equalTo("results"), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
        assertThat(state.get(), equalTo("results"), () -> "Add assertion message");
    }

    @Test
    public void testGetResponseContextOnSuccessCallback() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        state = new AtomicReference<>();
        final CyclicBarrier barrier = new CyclicBarrier(2);

        Object[] result = new String[] {"results"};
        schedule(barrier, () -> callback.handleResponse(ctx, result));
        barrier.await(5, TimeUnit.SECONDS);
        
        assertThat(future.getContext(), equalTo(ctx), () -> "Add assertion message");
        assertThat(future.get(), equalTo("results"), () -> "Add assertion message");
        assertThat(future.get(10, TimeUnit.MILLISECONDS), equalTo("results"), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
        assertThat(state.get(), equalTo("results"), () -> "Add assertion message");
    }
    
    @Test
    public void testHandleExceptionCallback() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        state = new AtomicReference<>();
        final CyclicBarrier barrier = new CyclicBarrier(1);
        schedule(barrier, () -> callback.handleException(ctx, new RuntimeException()));

        assertThrows(ExecutionException.class, () -> future.get());
        assertThrows(ExecutionException.class, () -> future.get(10, TimeUnit.MILLISECONDS));
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
        assertThat(state.get(), instanceOf(RuntimeException.class), () -> "Add assertion message");
    }

    @Test
    public void testGetResponseContextOnExceptionCallback() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        final CyclicBarrier barrier = new CyclicBarrier(1);
        schedule(barrier, () -> callback.handleException(ctx, new RuntimeException()));

        assertThat(future.getContext(), nullValue(), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }

    @Test
    public void testHandleCancellationCallbackWithFuture() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        state = new AtomicReference<>();
        final CyclicBarrier barrier = new CyclicBarrier(2);
        schedule(barrier, () -> future.cancel(true));
        barrier.await(5, TimeUnit.SECONDS);

        assertThrows(InterruptedException.class, () -> future.get());
        assertThrows(InterruptedException.class, () -> future.get(10, TimeUnit.MILLISECONDS));
        assertThat(future.isCancelled(), equalTo(true), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
        assertThat(state.get(), instanceOf(InterruptedException.class), () -> "Add assertion message");
    }

    @Test
    public void testHandleCancellationCallback() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        state = new AtomicReference<>();
        final CyclicBarrier barrier = new CyclicBarrier(2);
        schedule(barrier, () -> callback.cancel(true));
        barrier.await(5, TimeUnit.SECONDS);

        assertThrows(InterruptedException.class, () -> callback.get());
        assertThrows(InterruptedException.class, () -> callback.get(10, TimeUnit.MILLISECONDS));
        assertThat(callback.isCancelled(), equalTo(true), () -> "Add assertion message");
        assertThat(callback.isDone(), equalTo(true), () -> "Add assertion message");
        assertThat(state.get(), instanceOf(CancellationException.class), () -> "Add assertion message");
    }

    @Test
    public void testHandleCancellationCallbackWhenStarted() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        callback.start(new MessageImpl());
        assertThat(future.cancel(true), equalTo(false), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(false), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(false), () -> "Add assertion message");
    }

    @Test
    public void testGetResponseContextOnCancellationCallback() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        final CyclicBarrier barrier = new CyclicBarrier(1);
        schedule(barrier, () -> future.cancel(true));

        assertThat(future.getContext(), nullValue(), () -> "Add assertion message");
        assertThat(future.isCancelled(), equalTo(true), () -> "Add assertion message");
        assertThat(future.isDone(), equalTo(true), () -> "Add assertion message");
    }
    
    @Test(expected = TimeoutException.class)
    public void testTimeout() throws Exception {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        future.get(10, TimeUnit.MILLISECONDS);
    }

    private void schedule(CyclicBarrier barrier, Runnable runnable) {
        
        callback = new JaxrsClientCallback<String>(handler, String.class, null);
        future = (JaxrsResponseFuture<String>)callback.createFuture();
        ctx = new HashMap<String, Object>();
    // Removed line due to General Fixture smell: }
    
    @After
            }
        };
    }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            barrier.await(5, TimeUnit.SECONDS);
            runnable.run();
            return null;
        }, 100, TimeUnit.MILLISECONDS);
    }
}