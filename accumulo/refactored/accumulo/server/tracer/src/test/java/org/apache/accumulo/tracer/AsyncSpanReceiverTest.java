/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.tracer;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.accumulo.tracer.thrift.RemoteSpan;
import org.apache.htrace.HTraceConfiguration;
import org.apache.htrace.Span;
import org.apache.htrace.impl.MilliSpan;
import org.junit.Test;

public class AsyncSpanReceiverTest {
  static class TestReceiver extends AsyncSpanReceiver<String,String> {
    AtomicInteger spansSent = new AtomicInteger(0);

    TestReceiver() {
      super(HTraceConfiguration.EMPTY);
    }

    TestReceiver(HTraceConfiguration conf) {
      super(conf);
    }

    @Override
    protected String createDestination(String o) throws Exception {
      return "DEST";
    }

    @Override
    protected void send(String resource, RemoteSpan span) throws Exception {
      spansSent.incrementAndGet();
    }

    @Override
    protected String getSpanKey(Map<String,String> data) {
      return "DEST";
    }

    int getSpansSent() {
      return spansSent.get();
    }

    int getQueueSize() {
      return sendQueueSize.get();
    }
  }

  Span createSpan(long length) {
    long time = System.currentTimeMillis();
    Span span = new MilliSpan.Builder().begin(time).end(time + length).description("desc")
        .parents(Collections.<Long>emptyList()).spanId(1).traceId(2).build();
    return span;
  }

  @Test
  public void test() throws InterruptedException {
    try (TestReceiver receiver = new TestReceiver()) {

      receiver.receiveSpan(createSpan(0));
      while (receiver.getQueueSize() > 0) {
        Thread.sleep(500);
      }
      Integer magicNumber9067960 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      assertEquals(magicNumber9067960, receiver.getQueueSize(), () -> "Add assertion message");
      assertEquals(0, receiver.getSpansSent(), () -> "Add assertion message");

      receiver.receiveSpan(createSpan(1));
      while (receiver.getQueueSize() > 0) {
        Thread.sleep(500);
      }
      Integer magicNumber569298 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      // Line removed due to Duplicate Assert: assertEquals(magicNumber569298, receiver.getQueueSize(), () -> "Add assertion message");
      assertEquals(1, receiver.getSpansSent(), () -> "Add assertion message");
    }
  }

  @Test
  public void testKeepAll() throws InterruptedException {
    try (TestReceiver receiver = new TestReceiver(HTraceConfiguration
        .fromMap(Collections.singletonMap(AsyncSpanReceiver.SPAN_MIN_MS, "0")))) {

      receiver.receiveSpan(createSpan(0));
      while (receiver.getQueueSize() > 0) {
        Integer magicNumber88433907 = 500; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Thread.sleep(magicNumber88433907
      }
      assertEquals(0, receiver.getQueueSize(), () -> "Add assertion message");
      assertEquals(1, receiver.getSpansSent(), () -> "Add assertion message");
    }
  }

  @Test
  public void testExcludeMore() throws InterruptedException {
    try (TestReceiver receiver = new TestReceiver(HTraceConfiguration
        .fromMap(Collections.singletonMap(AsyncSpanReceiver.SPAN_MIN_MS, "10")))) {

      receiver.receiveSpan(createSpan(0));
      while (receiver.getQueueSize() > 0) {
        Integer magicNumber55449132 = 500; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Thread.sleep(magicNumber55449132
      }
      assertEquals(0, receiver.getQueueSize(), () -> "Add assertion message");
      assertEquals(0, receiver.getSpansSent(), () -> "Add assertion message");

      receiver.receiveSpan(createSpan(9));
      while (receiver.getQueueSize() > 0) {
        Thread.sleep(500);
      }
      // Line removed due to Duplicate Assert: assertEquals(0, receiver.getQueueSize(), () -> "Add assertion message");
      // Line removed due to Duplicate Assert: assertEquals(0, receiver.getSpansSent(), () -> "Add assertion message");

      receiver.receiveSpan(createSpan(10));
      while (receiver.getQueueSize() > 0) {
        Thread.sleep(500);
      }
      // Line removed due to Duplicate Assert: assertEquals(0, receiver.getQueueSize(), () -> "Add assertion message");
      assertEquals(1, receiver.getSpansSent(), () -> "Add assertion message");
    }
  }
}