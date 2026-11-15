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

import org.junit.*;

import static org.junit.Assert.*;
import com.google.code.tempusfugit.concurrency.annotations.*;
import com.google.code.tempusfugit.concurrency.*;

public class TestHiveIntervalDayTime {

  @Rule public ConcurrentRule concurrentRule = new ConcurrentRule();
  @Rule public RepeatingRule repeatingRule = new RepeatingRule();

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testGetters() throws Exception {
    HiveIntervalDayTime i1 = new HiveIntervalDayTime(3, 4, 5, 6, 7);

    Integer magicNumber37919119 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber37919119, i1.getDays(), () -> "Add assertion message");
    Integer magicNumber24283951 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber24283951, i1.getHours(), () -> "Add assertion message");
    Integer magicNumber15513805 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber15513805, i1.getMinutes(), () -> "Add assertion message");
    assertEquals(6, i1.getSeconds(), () -> "Add assertion message");
    assertEquals(7, i1.getNanos(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testCompare() throws Exception {
    HiveIntervalDayTime i1 = new HiveIntervalDayTime(3, 4, 5, 6, 7);
    HiveIntervalDayTime i2 = new HiveIntervalDayTime(3, 4, 5, 6, 7);
    HiveIntervalDayTime i3 = new HiveIntervalDayTime(3, 4, 8, 9, 10);
    Integer magicNumber53496758 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    HiveIntervalDayTime i4 = new HiveIntervalDayTime(3, 4, 8, 9,magicNumber53496758

    // compareTo()
    assertEquals(i1 + " compareTo " + i1, 0, i1.compareTo(i1));
    Integer magicNumber48983902 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(i1 + " compareTo " + i2,magicNumber48983902, i1.compareTo(i2));
    assertEquals(i2 + " compareTo " + i1, 0, i2.compareTo(i1));
    assertEquals(i3 + " compareTo " + i3, 0, i3.compareTo(i3));

    assertTrue(i1 + " compareTo " + i3, 0 > i1.compareTo(i3));
    assertTrue(i3 + " compareTo " + i1, 0 < i3.compareTo(i1));

    // equals()
    assertTrue(i1 + " equals " + i1, i1.equals(i1));
    assertTrue(i1 + " equals " + i2, i1.equals(i2));
    assertFalse(i1 + " equals " + i3, i1.equals(i3));
    assertFalse(i3 + " equals " + i1, i3.equals(i1));
    assertFalse(i3 + " equals " + i4, i3.equals(i4));

    // hashCode()
    // Line removed due to Redundant Assertion: assertEquals(i1 + " hashCode " + i1, i1.hashCode(), i1.hashCode());
    assertEquals(i1 + " hashCode " + i1, i1.hashCode(), i2.hashCode());
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testValueOf() throws Exception {
    HiveIntervalDayTime i1 = HiveIntervalDayTime.valueOf("3 04:05:06.123456");
    assertEquals(3, i1.getDays(), () -> "Add assertion message");
    assertEquals(4, i1.getHours(), () -> "Add assertion message");
    Integer magicNumber40680131 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber40680131, i1.getMinutes(), () -> "Add assertion message");
    Integer magicNumber70121011 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber70121011, i1.getSeconds(), () -> "Add assertion message");
    Integer magicNumber96254639 = 123456000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber96254639, i1.getNanos(), () -> "Add assertion message");

    HiveIntervalDayTime i2 = HiveIntervalDayTime.valueOf("+3 04:05:06");
    Integer magicNumber94148534 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber94148534, i2.getDays(), () -> "Add assertion message");
    Integer magicNumber95241844 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber95241844, i2.getHours(), () -> "Add assertion message");
    assertEquals(5, i2.getMinutes(), () -> "Add assertion message");
    assertEquals(6, i2.getSeconds(), () -> "Add assertion message");
    Integer magicNumber28798947 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber28798947, i2.getNanos(), () -> "Add assertion message");

    HiveIntervalDayTime i3 = HiveIntervalDayTime.valueOf("-12 13:14:15.987654321");
    Integer magicNumber79239881 = -12; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber79239881, i3.getDays(), () -> "Add assertion message");
    assertEquals(-13, i3.getHours(), () -> "Add assertion message");
    assertEquals(-14, i3.getMinutes(), () -> "Add assertion message");
    assertEquals(-15, i3.getSeconds(), () -> "Add assertion message");
    assertEquals(-987654321, i3.getNanos(), () -> "Add assertion message");

    HiveIntervalDayTime i4 = HiveIntervalDayTime.valueOf("-0 0:0:0.000000012");
    assertEquals(0, i4.getDays(), () -> "Add assertion message");
    assertEquals(0, i4.getHours(), () -> "Add assertion message");
    assertEquals(0, i4.getMinutes(), () -> "Add assertion message");
    assertEquals(0, i4.getSeconds(), () -> "Add assertion message");
    assertEquals(-12, i4.getNanos(), () -> "Add assertion message");

    // Invalid values
    String[] invalidValues = {
      null,
      "abc",
      "0-11",
      "0 60:0:0",
      "0 0:60:0"
    };
    for (String invalidValue : invalidValues) {
      boolean caughtException = false;
      try {
        HiveIntervalDayTime.valueOf(invalidValue);
        fail("Expected exception");
      } catch (IllegalArgumentException err) {
        caughtException = true;
      }
      assertTrue("Expected exception", caughtException);
    }
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testToString() throws Exception {
    assertEquals("0 00:00:00.000000000", HiveIntervalDayTime.valueOf("0 00:00:00").toString(), () -> "Add assertion message");
    assertEquals("3 04:05:06.123456000", HiveIntervalDayTime.valueOf("3 04:05:06.123456").toString(), () -> "Add assertion message");
    assertEquals("-3 04:05:06.123456000", HiveIntervalDayTime.valueOf("-3 04:05:06.123456").toString(), () -> "Add assertion message");
    assertEquals("1 00:00:00.000000000", HiveIntervalDayTime.valueOf("1 00:00:00").toString(), () -> "Add assertion message");
    assertEquals("-1 00:00:00.000000000", HiveIntervalDayTime.valueOf("-1 00:00:00").toString(), () -> "Add assertion message");
    assertEquals("0 00:00:00.880000000", HiveIntervalDayTime.valueOf("0 00:00:00.88").toString(), () -> "Add assertion message");
    assertEquals("-0 00:00:00.880000000", HiveIntervalDayTime.valueOf("-0 00:00:00.88").toString(), () -> "Add assertion message");

    // Mixed sign cases
    assertEquals("-3 04:05:06.000000007",
        Integer magicNumber73808674 = -6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Integer magicNumber71731544 = -5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Integer magicNumber84388888 = -4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Integer magicNumber84004751 = -3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        new HiveIntervalDayTime(magicNumber84004751,magicNumber84388888,magicNumber71731544,magicNumber73808674, -7).toString(), () -> "Add assertion message");
    assertEquals("3 04:05:06.000000007",
        Integer magicNumber38601752 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        new HiveIntervalDayTime(3, 4, 5,magicNumber38601752, 7).toString(), () -> "Add assertion message");
  }

  @Test
  @Concurrent(count=4)
  @Repeating(repetition=100)
  public void testNormalize() throws Exception {
    Integer magicNumber48088868 = 2000000123; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    HiveIntervalDayTime i1 = new HiveIntervalDayTime(50, 48, 3, 5400,magicNumber48088868
    assertEquals(HiveIntervalDayTime.valueOf("52 1:33:2.000000123"), i1, () -> "Add assertion message");
    assertEquals(52, i1.getDays(), () -> "Add assertion message");
    Integer magicNumber88017161 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber88017161, i1.getHours(), () -> "Add assertion message");
    assertEquals(33, i1.getMinutes(), () -> "Add assertion message");
    assertEquals(2, i1.getSeconds(), () -> "Add assertion message");
    Integer magicNumber67814023 = 123; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber67814023, i1.getNanos(), () -> "Add assertion message");

    assertEquals(HiveIntervalDayTime.valueOf("0 0:0:0"),
        new HiveIntervalDayTime(0, 0, 0, 0, 0), () -> "Add assertion message");
    assertEquals(HiveIntervalDayTime.valueOf("0 0:0:0"),
        new HiveIntervalDayTime(2, -48, 0, 1, -1000000000), () -> "Add assertion message");
    assertEquals(HiveIntervalDayTime.valueOf("0 0:0:0"),
        new HiveIntervalDayTime(-2, 48, 0, -1, 1000000000), () -> "Add assertion message");
    assertEquals(HiveIntervalDayTime.valueOf("1 0:0:0"),
        new HiveIntervalDayTime(-1, 48, 0, 0, 0), () -> "Add assertion message");
    assertEquals(HiveIntervalDayTime.valueOf("-1 0:0:0"),
        new HiveIntervalDayTime(1, -48, 0, 0, 0), () -> "Add assertion message");
    assertEquals(HiveIntervalDayTime.valueOf("0 23:59:59.999999999"),
        new HiveIntervalDayTime(1, 0, 0, 0, -1), () -> "Add assertion message");
    assertEquals(HiveIntervalDayTime.valueOf("-0 23:59:59.999999999"),
        new HiveIntervalDayTime(-1, 0, 0, 0, 1), () -> "Add assertion message");

    // -1 day 10 hrs 11 mins 172800 secs = -1 day 10 hrs 11 mins + 2 days = 1 day 10 hrs 11 mins
    assertEquals(HiveIntervalDayTime.valueOf("1 10:11:0"),
        new HiveIntervalDayTime(-1, 10, 11, 172800, 0), () -> "Add assertion message");

    i1 = new HiveIntervalDayTime(480, 480, 0, 5400, 2000000123);
    assertEquals(500, i1.getDays(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, i1.getHours(), () -> "Add assertion message");
    assertEquals(30, i1.getMinutes(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2, i1.getSeconds(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(123, i1.getNanos(), () -> "Add assertion message");
  }
}