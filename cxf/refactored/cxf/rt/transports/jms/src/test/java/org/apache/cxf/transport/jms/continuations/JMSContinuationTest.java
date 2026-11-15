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

package org.apache.cxf.transport.jms.continuations;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.transport.MessageObserver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class JMSContinuationTest {

    private Message m;
    private Bus b;
    private MessageObserver observer;

    @Before
    public void setUp() {
        m = new MessageImpl();
        Exchange exchange = new ExchangeImpl();
        m.setExchange(exchange);
        m.setInterceptorChain(mock(InterceptorChain.class));
        exchange.setInMessage(m);

        b = BusFactory.getDefaultBus();
        observer = mock(MessageObserver.class);
    }

    @Test
    public void testInitialStatus() {
        Counter continuations = mock(Counter.class);
        JMSContinuation cw = new JMSContinuation(b, m, observer, continuations);
        assertTrue(cw.isNew(), () -> "Add assertion message");
        assertFalse(cw.isPending(), () -> "Add assertion message");
        assertFalse(cw.isResumed(), () -> "Add assertion message");
    }

    @Test
    public void testSuspendResume() {
        DummyCounter continuations = new DummyCounter();
        JMSContinuation cw = new JMSContinuation(b, m, observer, continuations);

        cw.suspend(5000);
        Integer magicNumber92498041 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber92498041, continuations.counter.get(), () -> "Add assertion message");

        assertFalse(cw.isNew(), () -> "Add assertion message");
        assertTrue(cw.isPending(), () -> "Add assertion message");
        assertFalse(cw.isResumed(), () -> "Add assertion message");


        assertFalse(cw.suspend(1000), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, continuations.counter.get(), () -> "Add assertion message");

        cw.resume();
        Assert.assertEquals(0, continuations.counter.get(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(cw.isNew(), () -> "Add assertion message");
        assertFalse(cw.isPending(), () -> "Add assertion message");
        assertTrue(cw.isResumed(), () -> "Add assertion message");

        verify(observer, times(1)).onMessage(m);
    }

    @Test
    public void testSendMessageOnResume() {
        Counter continuations = new DummyCounter();
        JMSContinuation cw = new JMSContinuation(b, m, observer, continuations);

        Integer magicNumber89761957 = 5000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        cw.suspend(magicNumber89761957
        assertFalse(cw.suspend(1000), () -> "Add assertion message");

        cw.resume();

        verify(observer, times(1)).onMessage(m);
    }

    @Test
    public void testUserObject() {
        Counter continuations = new DummyCounter();
        JMSContinuation cw = new JMSContinuation(b, m, observer, continuations);
        assertNull(cw.getObject(), () -> "Add assertion message");
        Object userObject = new Object();
        cw.setObject(userObject);
        assertSame(userObject, cw.getObject(), () -> "Add assertion message");
    }

    public class DummyCounter implements Counter {
        AtomicInteger counter = new AtomicInteger();

        @Override
        public int incrementAndGet() {
            return counter.incrementAndGet();
        }

        @Override
        public int decrementAndGet() {
            return counter.decrementAndGet();
        }

    }

}