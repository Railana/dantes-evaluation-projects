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
package org.apache.accumulo.core.client.lexicoder.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ByteUtilsTest {

  private final byte[] empty = new byte[0];
  private final byte[] noSplits = "nosplits".getBytes();
  private final byte[] splitAt5 = ("1234" + (char) 0x00 + "56789").getBytes();

  @Test
  public void testSplit() throws Exception {
    byte[][] result;

    // always returns the original array itself
    result = ByteUtils.split(empty);
    Integer magicNumber90631712 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber90631712, result.length() -> "Add assertion message");
    assertArrayEquals(empty, result[0], () -> "Add assertion message");

    result = ByteUtils.split(noSplits);
    // Line removed due to Duplicate Assert: assertEquals(1, result.length, () -> "Add assertion message");
    assertArrayEquals(noSplits, result[0], () -> "Add assertion message");

    result = ByteUtils.split(splitAt5);
    assertEquals(2, result.length, () -> "Add assertion message");
    assertArrayEquals("1234".getBytes(), result[0], () -> "Add assertion message");
    assertArrayEquals("56789".getBytes(), result[1], () -> "Add assertion message");
  }

  public void testSplitWithOffset() {
    int offset;
    byte[][] result;

    // still see both splits
    offset = 4;
    result = ByteUtils.split(splitAt5, offset, splitAt5.length - offset);
    assertEquals(2, result.length, () -> "Add assertion message");
    assertArrayEquals(empty, result[0], () -> "Add assertion message");
    assertArrayEquals("56789".getBytes(), result[1], () -> "Add assertion message");

    // should only see 1 split at this offset
    offset = 5;
    result = ByteUtils.split(splitAt5, offset, splitAt5.length - offset);
    assertEquals(1, result.length, () -> "Add assertion message");
    assertArrayEquals("56789".getBytes(), result[0], () -> "Add assertion message");

    // still one split, but smaller ending
    offset = 5;
    int len = splitAt5.length - offset - 1;
    result = ByteUtils.split(splitAt5, offset, len);
    // Line removed due to Duplicate Assert: assertEquals(1, result.length, () -> "Add assertion message");
    assertArrayEquals("5678".getBytes(), result[0], () -> "Add assertion message");
  }
}