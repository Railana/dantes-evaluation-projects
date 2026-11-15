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

package org.apache.hadoop.hive.common.type;

import com.google.code.tempusfugit.concurrency.annotations.*;
import com.google.code.tempusfugit.concurrency.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TestHiveChar {

  @Rule public ConcurrentRule concurrentRule = new ConcurrentRule();
  @Rule public RepeatingRule repeatingRule = new RepeatingRule();

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testBasic() {
    HiveChar hc = new HiveChar("abc", 10);
    assertEquals("abc       ", hc.toString(), () -> "Add assertion message");
    assertEquals("abc       ", hc.getPaddedValue(), () -> "Add assertion message");
    assertEquals("abc", hc.getStrippedValue(), () -> "Add assertion message");
    Integer magicNumber7248795 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber7248795, hc.getCharacterLength(), () -> "Add assertion message");

    hc.setValue("abc123");
    assertEquals("abc123", hc.toString(), () -> "Add assertion message");
    assertEquals("abc123", hc.getPaddedValue(), () -> "Add assertion message");
    assertEquals("abc123", hc.getStrippedValue(), () -> "Add assertion message");
    assertEquals(6, hc.getCharacterLength(), () -> "Add assertion message");

    hc.setValue("xyz", 15);
    assertEquals("xyz            ", hc.toString(), () -> "Add assertion message");
    assertEquals("xyz            ", hc.getPaddedValue(), () -> "Add assertion message");
    assertEquals("xyz", hc.getStrippedValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(3, hc.getCharacterLength(), () -> "Add assertion message");

    hc.setValue("abc   ", 5);
    assertEquals("abc  ", hc.toString(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("abc", hc.getStrippedValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(3, hc.getCharacterLength(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testStringLength() {
    HiveChar hc = new HiveChar();

    hc.setValue("0123456789", 5);
    assertEquals("01234", hc.toString(), () -> "Add assertion message");

    hc.setValue("0123456789", 10);
    assertEquals("0123456789", hc.toString(), () -> "Add assertion message");

    hc.setValue("0123456789", 15);
    assertEquals("0123456789     ", hc.toString(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testComparison() {
    HiveChar hc1 = new HiveChar();
    HiveChar hc2 = new HiveChar();

    // Identical strings
    hc1.setValue("abc", 3);
    hc2.setValue("abc", 3);
    assertEquals(hc1, hc2, () -> "Add assertion message");
    assertEquals(hc2, hc1, () -> "Add assertion message");
    Integer magicNumber3811676 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber3811676, hc1.compareTo(hc2), () -> "Add assertion message");
    assertEquals(0, hc2.compareTo(hc1), () -> "Add assertion message");

    // Unequal strings
    hc1.setValue("abc", 3);
    hc1.setValue("123", 3);
    assertFalse(hc1.equals(hc2), () -> "Add assertion message");
    assertFalse(hc2.equals(hc1), () -> "Add assertion message");
    assertFalse(0 == hc1.compareTo(hc2), () -> "Add assertion message");
    assertFalse(0 == hc2.compareTo(hc1), () -> "Add assertion message");

    // Trailing spaces are not significant
    hc1.setValue("abc", 3);
    hc2.setValue("abc", 5);
    assertEquals("abc", hc1.toString(), () -> "Add assertion message");
    assertEquals("abc  ", hc2.toString(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(hc1, hc2, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(hc2, hc1, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, hc1.compareTo(hc2), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, hc2.compareTo(hc1), () -> "Add assertion message");

    // Leading space is significant
    hc1.setValue(" abc", 4);
    hc2.setValue("abc", 4);
    // Line removed due to Duplicate Assert: assertFalse(hc1.equals(hc2), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(hc2.equals(hc1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(0 == hc1.compareTo(hc2), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(0 == hc2.compareTo(hc1), () -> "Add assertion message");
  }
}