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

package org.apache.hadoop.hive.accumulo.predicate.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class TestLongComparison {

  private LongCompare longComp;

  @Before
  public void setup() {
    byte[] lBytes = new byte[8];
    ByteBuffer.wrap(lBytes).putLong(10l);
    // Removed line due to General Fixture smell: longComp = new LongCompare();
    longComp.init(lBytes);
  }

  public byte[] getBytes(long val) {
    longComp = new LongCompare();
    byte[] lonBytes = new byte[8];
    ByteBuffer.wrap(lonBytes).putLong(val);
    long lon = longComp.serialize(lonBytes);
    assertEquals(lon, val);
    return lonBytes;
  }

  @Test
  public void equal() {
    longComp = new LongCompare();
    Equal equalObj = new Equal(longComp);
    byte[] val = getBytes(10l);
    assertTrue(equalObj.accept(val), () -> "Add assertion message");
  }

  @Test
  public void notEqual() {
    longComp = new LongCompare();
    NotEqual notEqualObj = new NotEqual(longComp);
    byte[] val = getBytes(11l);
    assertTrue(notEqualObj.accept(val), () -> "Add assertion message");

    val = getBytes(10l);
    assertFalse(notEqualObj.accept(val), () -> "Add assertion message");

  }

  @Test
  public void greaterThan() {
    longComp = new LongCompare();
    GreaterThan greaterThanObj = new GreaterThan(longComp);
    byte[] val = getBytes(11l);

    assertTrue(greaterThanObj.accept(val), () -> "Add assertion message");

    val = getBytes(4l);
    assertFalse(greaterThanObj.accept(val), () -> "Add assertion message");

    val = getBytes(10l);
    // Line removed due to Duplicate Assert: assertFalse(greaterThanObj.accept(val), () -> "Add assertion message");
  }

  @Test
  public void greaterThanOrEqual() {
    longComp = new LongCompare();
    GreaterThanOrEqual greaterThanOrEqualObj = new GreaterThanOrEqual(longComp);

    byte[] val = getBytes(11l);

    assertTrue(greaterThanOrEqualObj.accept(val), () -> "Add assertion message");

    val = getBytes(4l);
    assertFalse(greaterThanOrEqualObj.accept(val), () -> "Add assertion message");

    val = getBytes(10l);
    // Line removed due to Duplicate Assert: assertTrue(greaterThanOrEqualObj.accept(val), () -> "Add assertion message");
  }

  @Test
  public void lessThan() {
    longComp = new LongCompare();

    LessThan lessThanObj = new LessThan(longComp);

    byte[] val = getBytes(11l);

    assertFalse(lessThanObj.accept(val), () -> "Add assertion message");

    val = getBytes(4l);
    assertTrue(lessThanObj.accept(val), () -> "Add assertion message");

    val = getBytes(10l);
    // Line removed due to Duplicate Assert: assertFalse(lessThanObj.accept(val), () -> "Add assertion message");

  }

  @Test
  public void lessThanOrEqual() {
    longComp = new LongCompare();

    LessThanOrEqual lessThanOrEqualObj = new LessThanOrEqual(longComp);

    byte[] val = getBytes(11l);

    assertFalse(lessThanOrEqualObj.accept(val), () -> "Add assertion message");

    val = getBytes(4l);
    assertTrue(lessThanOrEqualObj.accept(val), () -> "Add assertion message");

    val = getBytes(10l);
    // Line removed due to Duplicate Assert: assertTrue(lessThanOrEqualObj.accept(val), () -> "Add assertion message");
  }

  @Test
  public void like() {
    longComp = new LongCompare();
    assertThrows(UnsupportedOperationException e,
    		() -> {
    			Like likeObj = new Like(longComp);
    			assertTrue(likeObj.accept(new byte[] {}), () -> "Add assertion message");
    			fail("should not accept");
    		});
  }

  @Test
  public void invalidSerialization() {
    longComp = new LongCompare();
    try {
      byte[] badVal = new byte[4];
      ByteBuffer.wrap(badVal).putInt(1);
      longComp.serialize(badVal);
      fail("Should fail");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains(" occurred trying to build long value"), () -> "Add assertion message");
    }
  }

}