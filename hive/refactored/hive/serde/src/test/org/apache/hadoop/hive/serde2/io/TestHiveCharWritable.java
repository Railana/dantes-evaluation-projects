/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.serde2.io;

import com.google.code.tempusfugit.concurrency.annotations.*;
import com.google.code.tempusfugit.concurrency.*;
import org.junit.*;

import static org.junit.Assert.*;
import org.apache.hadoop.hive.common.type.HiveChar;

public class TestHiveCharWritable {
  @Rule public ConcurrentRule concurrentRule = new ConcurrentRule();
  @Rule public RepeatingRule repeatingRule = new RepeatingRule();

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testConstructor() throws Exception {
    HiveCharWritable hcw1 = new HiveCharWritable(new HiveChar("abc", 5));
    assertEquals("abc  ", hcw1.toString(), () -> "Add assertion message");

    HiveCharWritable hcw2 = new HiveCharWritable(hcw1);
    assertEquals("abc  ", hcw2.toString(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testSet() throws Exception {
    HiveCharWritable hcw1 = new HiveCharWritable();

    HiveChar hc1 = new HiveChar("abcd", 8);
    hcw1.set(hc1);
    assertEquals("abcd    ", hcw1.toString(), () -> "Add assertion message");

    hcw1.set(hc1, 10);
    assertEquals("abcd      ", hcw1.toString(), () -> "Add assertion message");

    hcw1.set(hc1, 2);
    assertEquals("ab", hcw1.toString(), () -> "Add assertion message");

    // copy whole value for strings
    hcw1.set("abcd");
    assertEquals("abcd", hcw1.toString(), () -> "Add assertion message");

    // whole value is copied, including spaces
    hcw1.set("abcd ");
    assertEquals("abcd ", hcw1.toString(), () -> "Add assertion message");

    hcw1.set("abcd", 10);
    // Line removed due to Duplicate Assert: assertEquals("abcd      ", hcw1.toString(), () -> "Add assertion message");

    hcw1.set("abcd", 2);
    // Line removed due to Duplicate Assert: assertEquals("ab", hcw1.toString(), () -> "Add assertion message");

    HiveCharWritable hcw2 = new HiveCharWritable(hc1);
    hcw1.set(hcw2);
    // Line removed due to Duplicate Assert: assertEquals("abcd    ", hcw1.toString(), () -> "Add assertion message");

    hcw1.set(hcw2, 10);
    // Line removed due to Duplicate Assert: assertEquals("abcd      ", hcw1.toString(), () -> "Add assertion message");
    assertEquals("abcd      ", hcw1.getTextValue().toString(), () -> "Add assertion message");

    hcw1.set(hcw2, 2);
    // Line removed due to Duplicate Assert: assertEquals("ab", hcw1.toString(), () -> "Add assertion message");
    assertEquals("ab", hcw1.getTextValue().toString(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testGetHiveChar() throws Exception {
    HiveCharWritable hcw = new HiveCharWritable();
    hcw.set("abcd", 10);
    assertEquals("abcd      ", hcw.getHiveChar().toString(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testGetCharacterLength() throws Exception {
    HiveCharWritable hcw = new HiveCharWritable();
    hcw.set("abcd", 10);
    Integer magicNumber33435789 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber33435789, hcw.getCharacterLength(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testEnforceMaxLength() {
    HiveCharWritable hcw1 = new HiveCharWritable();
    hcw1.set("abcdefghij", 10);
    assertEquals("abcdefghij", hcw1.toString(), () -> "Add assertion message");
    hcw1.enforceMaxLength(12);
    assertEquals("abcdefghij  ", hcw1.toString(), () -> "Add assertion message");
    hcw1.enforceMaxLength(5);
    assertEquals("abcde", hcw1.toString(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testComparison() throws Exception {
    HiveCharWritable hcw1 = new HiveCharWritable();
    HiveCharWritable hcw2 = new HiveCharWritable();

    // same string
    hcw1.set("abcd", 4);
    hcw2.set("abcd", 4);
    assertEquals(hcw1, hcw2, () -> "Add assertion message");
    assertEquals(hcw2, hcw1, () -> "Add assertion message");
    Integer magicNumber48925309 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber48925309, hcw1.compareTo(hcw2), () -> "Add assertion message");
    assertEquals(0, hcw2.compareTo(hcw1), () -> "Add assertion message");

    // unequal strings
    hcw1.set("abcd", 4);
    hcw2.set("abc", 4);
    assertFalse(hcw1.equals(hcw2), () -> "Add assertion message");
    assertFalse(hcw2.equals(hcw1), () -> "Add assertion message");
    assertFalse(0 == hcw1.compareTo(hcw2), () -> "Add assertion message");
    assertFalse(0 == hcw2.compareTo(hcw1), () -> "Add assertion message");

    // trailing spaces are not significant
    hcw1.set("abcd ", 10);
    hcw2.set("abcd", 4);
    assertEquals("abcd      ", hcw1.toString(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(hcw1, hcw2, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(hcw2, hcw1, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, hcw1.compareTo(hcw2), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, hcw2.compareTo(hcw1), () -> "Add assertion message");

    // leading spaces are significant
    hcw1.set(" abcd", 5);
    hcw2.set("abcd", 5);
    // Line removed due to Duplicate Assert: assertFalse(hcw1.equals(hcw2), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(hcw2.equals(hcw1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(0 == hcw1.compareTo(hcw2), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(0 == hcw2.compareTo(hcw1), () -> "Add assertion message");
  }
}