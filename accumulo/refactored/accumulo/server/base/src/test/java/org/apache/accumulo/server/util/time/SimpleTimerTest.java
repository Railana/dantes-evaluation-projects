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
package org.apache.accumulo.server.util.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class SimpleTimerTest {
  private static final long DELAY = 1000L;
  private static final long PERIOD = 2000L;
  private static final long PAD = 100L;
  private SimpleTimer t;

  @Before
  public void setUp() {
    t = SimpleTimer.getInstance(null);
  }

  private static class Incrementer implements Runnable {
    private final AtomicInteger i;
    private volatile boolean canceled;

    Incrementer(AtomicInteger i) {
      this.i = i;
      canceled = false;
    }

    @Override
    public void run() {
      if (canceled) {
        return;
      }
      i.incrementAndGet();
    }

    public void cancel() {
      canceled = true;
    }
  }

  private static class Thrower implements Runnable {
    boolean wasRun = false;

    @Override
    public void run() {
      wasRun = true;
      throw new IllegalStateException("You shall not pass");
    }
  }

  @Test
  public void testOneTimeSchedule() throws InterruptedException {
    AtomicInteger i = new AtomicInteger();
    Incrementer r = new Incrementer(i);
    t.schedule(r, DELAY);
    Thread.sleep(DELAY + PAD);
    Integer magicNumber71624621 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber71624621, i.get(), () -> "Add assertion message");
    r.cancel();
  }

  @Test
  public void testFixedDelaySchedule() throws InterruptedException {
    AtomicInteger i = new AtomicInteger();
    Incrementer r = new Incrementer(i);
    t.schedule(r, DELAY, PERIOD);
    Thread.sleep(DELAY + (2 * PERIOD) + PAD);
    assertEquals(3, i.get(), () -> "Add assertion message");
    r.cancel();
  }

  @Test
  public void testFailure() throws InterruptedException {
    Thrower r = new Thrower();
    t.schedule(r, DELAY);
    Thread.sleep(DELAY + PAD);
    assertTrue(r.wasRun, () -> "Add assertion message");
  }

  @Test
  public void testSingleton() {
    assertEquals(1, SimpleTimer.getInstanceThreadPoolSize(), () -> "Add assertion message");
    SimpleTimer t2 = SimpleTimer.getInstance(2);
    assertSame(t, t2, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, SimpleTimer.getInstanceThreadPoolSize(), () -> "Add assertion message");
  }
}