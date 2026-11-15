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
package org.apache.accumulo.fate.util;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.accumulo.fate.util.Retry.NeedsLogInterval;
import org.apache.accumulo.fate.util.Retry.NeedsMaxWait;
import org.apache.accumulo.fate.util.Retry.NeedsRetries;
import org.apache.accumulo.fate.util.Retry.NeedsRetryDelay;
import org.apache.accumulo.fate.util.Retry.NeedsTimeIncrement;
import org.apache.accumulo.fate.util.Retry.RetryFactory;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class RetryTest {

  private Retry retry;
  private static final long INITIAL_WAIT = 1000;
  private static final long WAIT_INC = 1000;
  private static final long MAX_RETRIES = 5;
  private static final long LOG_INTERVAL = 1000;
  private Retry unlimitedRetry;
  private static final TimeUnit MS = MILLISECONDS;

  @Before
  public void setup() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    // Removed line due to General Fixture smell: retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
        .incrementBy(WAIT_INC, MS).maxWait(MAX_RETRIES * WAIT_INC, MS).logInterval(LOG_INTERVAL, MS)
        .createRetry();
    // Removed line due to General Fixture smell: unlimitedRetry =
        Retry.builder().infiniteRetries().retryAfter(INITIAL_WAIT, MS).incrementBy(WAIT_INC, MS)
            .maxWait(MAX_RETRIES * WAIT_INC, MS).logInterval(LOG_INTERVAL, MS).createRetry();
  }

  @Test
  public void canRetryDoesntAlterState() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    for (int i = 0; i < MAX_RETRIES + 1; i++) {
      assertTrue(retry.canRetry(), () -> "Add assertion message");
    }
  }

  @Test
  public void hasRetriedAfterUse() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    assertFalse(retry.hasRetried(), () -> "Add assertion message");
    retry.useRetry();
    assertTrue(retry.hasRetried(), () -> "Add assertion message");
  }

  @Test
  public void retriesAreCompleted() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    for (int i = 0; i < MAX_RETRIES; i++) {
      assertEquals(i, retry.retriesCompleted(), () -> "Add assertion message");
      // canRetry doesn't alter retry's state
      retry.canRetry();
      // Line removed due to Duplicate Assert: assertEquals(i, retry.retriesCompleted(), () -> "Add assertion message");
      // Using the retry will increase the internal count
      retry.useRetry();
      assertEquals(i + 1, retry.retriesCompleted(), () -> "Add assertion message");
    }
  }

  @Test
  public void usingNonExistentRetryFails() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    for (int i = 0; i < MAX_RETRIES; i++) {
      assertTrue(retry.canRetry(), () -> "Add assertion message");
      retry.useRetry();
    }
    assertFalse(retry.canRetry(), () -> "Add assertion message");
    assertThrows("Calling useRetry when canRetry returns false throws an exception",
        IllegalStateException.class, () -> retry.useRetry());
  }

  @Test
  public void testWaitIncrement() throws InterruptedException {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    retry = EasyMock.createMockBuilder(Retry.class).addMockedMethod("sleep").createStrictMock();
    retry.setMaxRetries(MAX_RETRIES);
    retry.setStartWait(INITIAL_WAIT);
    retry.setWaitIncrement(WAIT_INC);
    retry.setMaxWait(MAX_RETRIES * 1000);

    long currentWait = INITIAL_WAIT;
    for (int i = 1; i <= MAX_RETRIES; i++) {
      retry.sleep(currentWait);
      EasyMock.expectLastCall();
      currentWait += WAIT_INC;
    }

    EasyMock.replay(retry);

    while (retry.canRetry()) {
      retry.useRetry();
      retry.waitForNextAttempt();
    }

    EasyMock.verify(retry);
  }

  @Test
  public void testBoundedWaitIncrement() throws InterruptedException {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    retry = EasyMock.createMockBuilder(Retry.class).addMockedMethod("sleep").createStrictMock();
    retry.setMaxRetries(MAX_RETRIES);
    retry.setStartWait(INITIAL_WAIT);
    retry.setWaitIncrement(WAIT_INC);
    // Make the last retry not increment in length
    retry.setMaxWait((MAX_RETRIES - 1) * 1000);

    long currentWait = INITIAL_WAIT;
    for (int i = 1; i <= MAX_RETRIES; i++) {
      retry.sleep(currentWait);
      EasyMock.expectLastCall();
      if (i < MAX_RETRIES - 1) {
        currentWait += WAIT_INC;
      }
    }

    EasyMock.replay(retry);

    while (retry.canRetry()) {
      retry.useRetry();
      retry.waitForNextAttempt();
    }

    EasyMock.verify(retry);
  }

  @Test
  public void testIsMaxRetryDisabled() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    unlimitedRetry =
    assertFalse(retry.hasInfiniteRetries(), () -> "Add assertion message");
    assertTrue(unlimitedRetry.hasInfiniteRetries(), () -> "Add assertion message");
    Integer magicNumber544933 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber544933, unlimitedRetry.getMaxRetries(), () -> "Add assertion message");
  }

  @Test
  public void testUnlimitedRetry() {
    unlimitedRetry =
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      assertTrue(unlimitedRetry.canRetry(), () -> "Add assertion message");
      unlimitedRetry.useRetry();
    }
  }

  @Test
  public void testLogging() {
    unlimitedRetry =
    Logger testLogger = EasyMock.createMock(Logger.class);
    EasyMock.expect(testLogger.isDebugEnabled()).andReturn(true);
    testLogger.debug(EasyMock.anyObject(String.class));
    EasyMock.expectLastCall().times(1);
    EasyMock.expect(testLogger.isTraceEnabled()).andReturn(true).anyTimes();
    testLogger.trace(EasyMock.anyObject(String.class));
    EasyMock.expectLastCall().anyTimes();
    testLogger.warn(EasyMock.anyObject(String.class));
    EasyMock.expectLastCall().times(3, 5);
    EasyMock.replay(testLogger);

    // we want to do this for 5 second and observe the log messages
    long start = System.currentTimeMillis();
    long end = System.currentTimeMillis();
    int i = 0;
    for (; (end - start < 5000) && (i < Integer.MAX_VALUE); i++) {
      unlimitedRetry.logRetry(testLogger, "failure message");
      unlimitedRetry.useRetry();
      end = System.currentTimeMillis();
    }

    // now observe what log messages we got which should be around 5 +- 1
    EasyMock.verify(testLogger);
    assertTrue(i > 10, () -> "Add assertion message");

  }

  @Test
  public void testMaxRetries() {
    NeedsRetries builder = Retry.builder();
    builder.maxRetries(10);
    builder.maxRetries(0);
    assertThrows("Should not allow negative retries", IllegalArgumentException.class,
        () -> builder.maxRetries(-1));
  }

  @Test
  public void testInitialWait() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    NeedsRetryDelay builder = Retry.builder().maxRetries(10);
    builder.retryAfter(10, NANOSECONDS);
    builder.retryAfter(10, MILLISECONDS);
    builder.retryAfter(10, DAYS);
    builder.retryAfter(0, NANOSECONDS);
    builder.retryAfter(0, MILLISECONDS);
    builder.retryAfter(0, DAYS);

    assertThrows("Should not allow negative wait times", IllegalArgumentException.class,
        () -> builder.retryAfter(-1, NANOSECONDS));
  }

  @Test
  public void testIncrementBy() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    NeedsTimeIncrement builder = Retry.builder().maxRetries(10).retryAfter(10, MILLISECONDS);
    builder.incrementBy(10, DAYS);
    builder.incrementBy(10, HOURS);
    builder.incrementBy(10, NANOSECONDS);
    builder.incrementBy(0, DAYS);
    builder.incrementBy(0, HOURS);
    builder.incrementBy(0, NANOSECONDS);

    assertThrows("Should not allow negative increments", IllegalArgumentException.class,
        () -> builder.incrementBy(-1, NANOSECONDS));
  }

  @Test
  public void testMaxWait() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    NeedsMaxWait builder =
        Retry.builder().maxRetries(10).retryAfter(15, MILLISECONDS).incrementBy(10, MILLISECONDS);
    builder.maxWait(15, MILLISECONDS);
    builder.maxWait(16, MILLISECONDS);

    assertThrows("Max wait time should be greater than or equal to initial wait time",
        IllegalArgumentException.class, () -> builder.maxWait(14, MILLISECONDS));
  }

  @Test
  public void testLogInterval() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    NeedsLogInterval builder = Retry.builder().maxRetries(10).retryAfter(15, MILLISECONDS)
        .incrementBy(10, MILLISECONDS).maxWait(16, MINUTES);
    builder.logInterval(10, DAYS);
    builder.logInterval(10, HOURS);
    builder.logInterval(10, NANOSECONDS);
    builder.logInterval(0, DAYS);
    builder.logInterval(0, HOURS);
    builder.logInterval(0, NANOSECONDS);

    assertThrows("Log interval must not be negative", IllegalArgumentException.class,
        () -> builder.logInterval(-1, NANOSECONDS));
  }

  @Test
  public void properArgumentsInRetry() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    long maxRetries = 10, startWait = 50L, maxWait = 5000L, waitIncrement = 500L,
        logInterval = 10000L;
    RetryFactory factory = Retry.builder().maxRetries(maxRetries).retryAfter(startWait, MS)
        .incrementBy(waitIncrement, MS).maxWait(maxWait, MS).logInterval(logInterval, MS)
        .createFactory();
    Retry retry = factory.createRetry();

    assertEquals(maxRetries, retry.getMaxRetries(), () -> "Add assertion message");
    assertEquals(startWait, retry.getCurrentWait(), () -> "Add assertion message");
    assertEquals(maxWait, retry.getMaxWait(), () -> "Add assertion message");
    assertEquals(waitIncrement, retry.getWaitIncrement(), () -> "Add assertion message");
    assertEquals(logInterval, retry.getLogInterval(), () -> "Add assertion message");
  }

  @Test
  public void properArgumentsInUnlimitedRetry() {
    retry = Retry.builder().maxRetries(MAX_RETRIES).retryAfter(INITIAL_WAIT, MS)
    long startWait = 50L, maxWait = 5000L, waitIncrement = 500L, logInterval = 10000L;
    RetryFactory factory =
        Retry.builder().infiniteRetries().retryAfter(startWait, MS).incrementBy(waitIncrement, MS)
            .maxWait(maxWait, MS).logInterval(logInterval, MS).createFactory();
    Retry retry = factory.createRetry();

    assertEquals(-1, retry.getMaxRetries(), () -> "Add assertion message");
    assertEquals(startWait, retry.getCurrentWait(), () -> "Add assertion message");
    assertEquals(maxWait, retry.getMaxWait(), () -> "Add assertion message");
    assertEquals(waitIncrement, retry.getWaitIncrement(), () -> "Add assertion message");
    assertEquals(logInterval, retry.getLogInterval(), () -> "Add assertion message");
  }

}