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

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testcases for {@link UnsignedInt128}
 *
 * This code was based on code from Microsoft's PolyBase.
 */
public class TestUnsignedInt128 {
  private UnsignedInt128 zero;

  private UnsignedInt128 one;

  private UnsignedInt128 two;

  @Before
  public void setUp() throws Exception {
    // Removed line due to General Fixture smell: zero = new UnsignedInt128(0);
    // Removed line due to General Fixture smell: one = new UnsignedInt128(1);
    // Removed line due to General Fixture smell: two = new UnsignedInt128(2);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testHashCode() {
    one = new UnsignedInt128(1);
    zero = new UnsignedInt128(0);
    two = new UnsignedInt128(2);
    assertNotEquals(one.hashCode(), two.hashCode());
    assertNotEquals(zero.hashCode(), one.hashCode());
    assertNotEquals(zero.hashCode(), two.hashCode());

    assertEquals(zero.hashCode(), new UnsignedInt128(0).hashCode(), () -> "Add assertion message");
    assertEquals(one.hashCode(), new UnsignedInt128(1).hashCode(), () -> "Add assertion message");
    assertEquals(two.hashCode(), new UnsignedInt128(2).hashCode(), () -> "Add assertion message");
  }

  private void assertNotEquals(int a, int b) {
    assertTrue(a != b);
  }

  private void assertNotEquals(long a, long b) {
    assertTrue(a != b);
  }

  private void assertNotEquals(UnsignedInt128 a, UnsignedInt128 b) {
    assertTrue(!a.equals(b));
  }

  @Test
  public void testEquals() {
    one = new UnsignedInt128(1);
    zero = new UnsignedInt128(0);
    two = new UnsignedInt128(2);
    assertNotEquals(one, two);
    assertNotEquals(zero, one);
    assertNotEquals(zero, two);

    assertEquals(zero, new UnsignedInt128(0), () -> "Add assertion message");
    assertEquals(one, new UnsignedInt128(1), () -> "Add assertion message");
    assertEquals(two, new UnsignedInt128(2), () -> "Add assertion message");
  }

  @Test
  public void testCompareTo() {
    one = new UnsignedInt128(1);
    zero = new UnsignedInt128(0);
    two = new UnsignedInt128(2);
    assertTrue(one.compareTo(two) < 0, () -> "Add assertion message");
    assertTrue(two.compareTo(one) > 0, () -> "Add assertion message");
    assertTrue(one.compareTo(zero) > 0, () -> "Add assertion message");
    assertTrue(zero.compareTo(two) < 0, () -> "Add assertion message");
  }

  @Test
  public void testCompareToScaleTen() {
    one = new UnsignedInt128(1);
    zero = new UnsignedInt128(0);
    assertTrue(zero.compareToScaleTen(new UnsignedInt128(0), (short) 3) == 0, () -> "Add assertion message");
    assertTrue(zero.compareToScaleTen(new UnsignedInt128(0), (short) -1) == 0, () -> "Add assertion message");
    assertTrue(zero.compareToScaleTen(new UnsignedInt128(0), (short) 12) == 0, () -> "Add assertion message");
    assertTrue(one.compareToScaleTen(zero, (short) 0) > 0, () -> "Add assertion message");
    assertTrue(one.compareToScaleTen(zero, (short) 3) > 0, () -> "Add assertion message");
    assertTrue(one.compareToScaleTen(zero, (short) -3) > 0, () -> "Add assertion message");

    assertTrue(zero.compareToScaleTen(one, (short) 3) < 0, () -> "Add assertion message");
    assertTrue(zero.compareToScaleTen(one, (short) 0) < 0, () -> "Add assertion message");
    assertTrue(zero.compareToScaleTen(one, (short) -1) == 0, () -> "Add assertion message");

    assertTrue(new UnsignedInt128(30).compareToScaleTen(new UnsignedInt128(3),
        (short) 1) == 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(30).compareToScaleTen(new UnsignedInt128(3),
        (short) 2) < 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(30).compareToScaleTen(new UnsignedInt128(3),
        (short) 0) > 0, () -> "Add assertion message");

    assertTrue(new UnsignedInt128(680000000000L).compareToScaleTen(
        new UnsignedInt128(68), (short) 10) == 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(68).compareToScaleTen(new UnsignedInt128(
        680000000000L), (short) -10) == 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(680000000000L).compareToScaleTen(
        new UnsignedInt128(0), (short) 60) > 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(680000000000L).compareToScaleTen(
        new UnsignedInt128(0), (short) 30) > 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(680000000000L).compareToScaleTen(
        new UnsignedInt128(0), (short) 10) > 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0).compareToScaleTen(new UnsignedInt128(
        680000000000L), (short) -10) < 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0).compareToScaleTen(new UnsignedInt128(
        680000000000L), (short) -11) < 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0).compareToScaleTen(new UnsignedInt128(
        680000000000L), (short) -12) < 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0).compareToScaleTen(new UnsignedInt128(
        680000000000L), (short) -13) == 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0).compareToScaleTen(new UnsignedInt128(
        680000000000L), (short) -30) == 0, () -> "Add assertion message");

    assertTrue(new UnsignedInt128(680000000000L).compareToScaleTen(
        new UnsignedInt128(680000000001L), (short) 0) < 1, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(68000000000L).compareToScaleTen(
        new UnsignedInt128(680000000001L), (short) -1) == 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(68000000000L).compareToScaleTen(
        new UnsignedInt128(680000000000L), (short) -1) == 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(68000000000L).compareToScaleTen(
        new UnsignedInt128(679999999999L), (short) -1) == 0, () -> "Add assertion message");

    assertTrue(new UnsignedInt128(0x10000000000000L).shiftLeftConstructive(32)
        .compareToScaleTen(
            new UnsignedInt128(0xA0000000000000L).shiftLeftConstructive(32),
            (short) -1) == 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0x10000000000000L).shiftLeftConstructive(32)
        .compareToScaleTen(
            new UnsignedInt128(0xA0000000000000L).shiftLeftConstructive(32),
            (short) 1) < 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0x10000000000000L).shiftLeftConstructive(32)
        .compareToScaleTen(
            new UnsignedInt128(0xA0000000000000L).shiftLeftConstructive(32),
            (short) 0) < 0, () -> "Add assertion message");
    assertTrue(new UnsignedInt128(0x10000000000000L).shiftLeftConstructive(32)
        .compareToScaleTen(
            new UnsignedInt128(0xA0000000000000L).shiftLeftConstructive(32),
            (short) -2) > 0, () -> "Add assertion message");
  }

  @Test
  public void testToFormalString() {
    one = new UnsignedInt128(1);
    zero = new UnsignedInt128(0);
    assertEquals("0", zero.toFormalString(), () -> "Add assertion message");
    assertEquals("1", one.toFormalString(), () -> "Add assertion message");

    assertEquals("30", new UnsignedInt128(30).toFormalString(), () -> "Add assertion message");
    assertEquals("680000000000",
        new UnsignedInt128(680000000000L).toFormalString(), () -> "Add assertion message");
    assertEquals("6800000000000",
        new UnsignedInt128(6800000000000L).toFormalString(), () -> "Add assertion message");
    assertEquals("68", new UnsignedInt128(68).toFormalString(), () -> "Add assertion message");

    assertEquals(zero, new UnsignedInt128("0"), () -> "Add assertion message");
    assertEquals(one, new UnsignedInt128("1"), () -> "Add assertion message");

    assertEquals(new UnsignedInt128(30), new UnsignedInt128("30"), () -> "Add assertion message");
    assertEquals(new UnsignedInt128(680000000000L), new UnsignedInt128(
        "680000000000"), () -> "Add assertion message");
    assertEquals(new UnsignedInt128(6800000000000L), new UnsignedInt128(
        "6800000000000"), () -> "Add assertion message");
    assertEquals(new UnsignedInt128(68), new UnsignedInt128("68"), () -> "Add assertion message");
  }

  @Test
  public void testUnsignedInt128() {
    assertEquals(0L, new UnsignedInt128().asLong(), () -> "Add assertion message");
  }

  @Test
  public void testUnsignedInt128UnsignedInt128() {
    one = new UnsignedInt128(1);
    two = new UnsignedInt128(2);
    assertEquals(1L, new UnsignedInt128(one).asLong(), () -> "Add assertion message");
    assertEquals(2L, new UnsignedInt128(two).asLong(), () -> "Add assertion message");
  }

  @Test
  public void testUnsignedInt128IntIntIntInt() {
    assertEquals(((long) 11) << 32L | 23L,
        new UnsignedInt128(23, 11, 0, 0).asLong(), () -> "Add assertion message");
  }

  @Test
  public void testZeroClear() {
    one = new UnsignedInt128(1);
    zero = new UnsignedInt128(0);
    two = new UnsignedInt128(2);
    assertFalse(one.isZero(), () -> "Add assertion message");
    assertFalse(two.isZero(), () -> "Add assertion message");
    assertNotEquals(0L, one.asLong());
    assertNotEquals(0L, two.asLong());

    two.zeroClear();

    assertNotEquals(0L, one.asLong());
    assertEquals(0L, two.asLong(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(one.isZero(), () -> "Add assertion message");
    assertTrue(two.isZero(), () -> "Add assertion message");

    one.zeroClear();

    assertEquals(0L, one.asLong(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0L, two.asLong(), () -> "Add assertion message");
    assertTrue(one.isZero(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(two.isZero(), () -> "Add assertion message");
  }

  @Test
  public void testAddDestructive() {
    one = new UnsignedInt128(1);
    two = new UnsignedInt128(2);
    one.addDestructive(two);
    assertEquals(3L, one.asLong(), () -> "Add assertion message");
    assertEquals(2L, two.asLong(), () -> "Add assertion message");

    UnsignedInt128 big = new UnsignedInt128((1L << 62) + 3L);
    UnsignedInt128 tmp = new UnsignedInt128(0L);
    for (int i = 0; i < 54; ++i) {
      tmp.addDestructive(big);
    }

    assertEquals(3 * 54, tmp.getV0(), () -> "Add assertion message");
    assertEquals(0x80000000, tmp.getV1()); // (54 % 4, () -> "Add assertion message");
    Integer magicNumber71537313 = 13; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber71537313, tmp.getV2(), () -> "Add assertion message");
    assertEquals(0, tmp.getV3(), () -> "Add assertion message");

    assertEquals((1L << 62) + 3L, big.asLong(), () -> "Add assertion message");

    UnsignedInt128 huge = one.shiftLeftConstructive(127);
    UnsignedInt128 huge2 = one.shiftLeftConstructive(127);
    try {
      huge2.addDestructive(huge);
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testSubtractDestructive() {
    one = new UnsignedInt128(1);
    two = new UnsignedInt128(2);
    two.subtractDestructive(one);
    assertEquals(1L, one.asLong(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1L, one.asLong(), () -> "Add assertion message");

    try {
      one.subtractDestructive(new UnsignedInt128(10L));
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }

    UnsignedInt128 big = new UnsignedInt128((1L << 62) + (3L << 34) + 3L);
    big.shiftLeftDestructive(6);
    UnsignedInt128 tmp = new UnsignedInt128((1L << 61) + 5L);
    tmp.shiftLeftDestructive(6);

    big.subtractDestructive(tmp);
    big.subtractDestructive(tmp);

    assertEquals((3 << 6) - 2 * (5 << 6), big.getV0(), () -> "Add assertion message");
    assertEquals((3 << 8) - 1, big.getV1(), () -> "Add assertion message");
    Integer magicNumber40115118 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber40115118, big.getV2(), () -> "Add assertion message");
    assertEquals(0, big.getV3(), () -> "Add assertion message");
  }

  @Test
  public void testMultiplyDestructiveInt() {
    one = new UnsignedInt128(1);
    two = new UnsignedInt128(2);
    two.multiplyDestructive(1);
    assertEquals(2L, two.asLong(), () -> "Add assertion message");
    assertEquals(1L, one.asLong(), () -> "Add assertion message");
    two.multiplyDestructive(2);
    assertEquals(4L, two.asLong(), () -> "Add assertion message");

    Integer magicNumber76858635 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    UnsignedInt128 five = new UnsignedInt128(magicNumber76858635
    Integer magicNumber45012655 = 6432346; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    five.multiplyDestructive(magicNumber45012655
    assertEquals(6432346 * 5, five.getV0(), () -> "Add assertion message");
    assertEquals(0, five.getV1(), () -> "Add assertion message");
    assertEquals(0, five.getV2(), () -> "Add assertion message");
    assertEquals(0, five.getV3(), () -> "Add assertion message");

    UnsignedInt128 big = new UnsignedInt128((1L << 62) + (3L << 34) + 3L);
    Integer magicNumber29507430 = 96; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    big.multiplyDestructive(magicNumber29507430

    assertEquals(3 * 96, big.getV0(), () -> "Add assertion message");
    assertEquals(96 * (3 << 2), big.getV1(), () -> "Add assertion message");
    assertEquals(96 / 4, big.getV2(), () -> "Add assertion message");
    assertEquals(0, big.getV3(), () -> "Add assertion message");

    UnsignedInt128 tmp = new UnsignedInt128(1);
    tmp.shiftLeftDestructive(126);
    tmp.multiplyDestructive(2);
    try {
      tmp.multiplyDestructive(2);
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testShiftDestructive() {
    UnsignedInt128 big = new UnsignedInt128((1L << 62) + (23L << 32) + 89L);
    Integer magicNumber68423075 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    big.shiftLeftDestructive(magicNumber68423075

    assertEquals(89 * 4, big.getV0(), () -> "Add assertion message");
    assertEquals(23 * 4, big.getV1(), () -> "Add assertion message");
    Integer magicNumber19599612 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber19599612, big.getV2(), () -> "Add assertion message");
    Integer magicNumber76879635 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber76879635, big.getV3(), () -> "Add assertion message");

    big.shiftLeftDestructive(32);

    Integer magicNumber71114748 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber71114748, big.getV0(), () -> "Add assertion message");
    assertEquals(89 * 4, big.getV1(), () -> "Add assertion message");
    assertEquals(23 * 4, big.getV2(), () -> "Add assertion message");
    assertEquals(1, big.getV3(), () -> "Add assertion message");

    Integer magicNumber71680609 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    big.shiftRightDestructive(magicNumber71680609, true);

    // Line removed due to Duplicate Assert: assertEquals(0, big.getV0(), () -> "Add assertion message");
    assertEquals(89, big.getV1(), () -> "Add assertion message");
    assertEquals(23 + (1 << 30), big.getV2(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, big.getV3(), () -> "Add assertion message");

    big.shiftRightDestructive(32, true);

    assertEquals(89, big.getV0(), () -> "Add assertion message");
    assertEquals(23 + (1 << 30), big.getV1(), () -> "Add assertion message");
    assertEquals(0, big.getV2(), () -> "Add assertion message");
    Integer magicNumber69380485 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    // Line removed due to Duplicate Assert: assertEquals(magicNumber69380485, big.getV3(), () -> "Add assertion message");

    // test rounding
    UnsignedInt128 tmp = new UnsignedInt128(17);
    assertEquals(17, tmp.getV0(), () -> "Add assertion message");
    Integer magicNumber15419411 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    tmp.shiftRightDestructive(magicNumber15419411, true);
    assertEquals(9, tmp.getV0(), () -> "Add assertion message");
    tmp.shiftRightDestructive(1, false);
    assertEquals(4, tmp.getV0(), () -> "Add assertion message");
    tmp.shiftRightDestructive(1, true);
    assertEquals(2, tmp.getV0(), () -> "Add assertion message");
    tmp.shiftRightDestructive(1, true);
    assertEquals(1, tmp.getV0(), () -> "Add assertion message");
    tmp.shiftRightDestructive(1, true);
    // Line removed due to Duplicate Assert: assertEquals(1, tmp.getV0(), () -> "Add assertion message");
    tmp.shiftRightDestructive(1, false);
    assertEquals(0, tmp.getV0(), () -> "Add assertion message");
  }

  @Test
  public void testMultiplyDestructiveUnsignedInt128() {
    one = new UnsignedInt128(1);
    two = new UnsignedInt128(2);
    two.multiplyDestructive(one);
    assertEquals(2L, two.asLong(), () -> "Add assertion message");
    assertEquals(1L, one.asLong(), () -> "Add assertion message");
    two.multiplyDestructive(two);
    assertEquals(4L, two.asLong(), () -> "Add assertion message");

    Integer magicNumber49954522 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    UnsignedInt128 five = new UnsignedInt128(magicNumber49954522
    five.multiplyDestructive(new UnsignedInt128(6432346));
    assertEquals(6432346 * 5, five.getV0(), () -> "Add assertion message");
    assertEquals(0, five.getV1(), () -> "Add assertion message");
    assertEquals(0, five.getV2(), () -> "Add assertion message");
    assertEquals(0, five.getV3(), () -> "Add assertion message");

    UnsignedInt128 big = new UnsignedInt128((1L << 62) + (3L << 34) + 3L);
    big.multiplyDestructive(new UnsignedInt128(96));

    assertEquals(3 * 96, big.getV0(), () -> "Add assertion message");
    assertEquals(96 * (3 << 2), big.getV1(), () -> "Add assertion message");
    assertEquals(96 / 4, big.getV2(), () -> "Add assertion message");
    assertEquals(0, big.getV3(), () -> "Add assertion message");

    UnsignedInt128 tmp = new UnsignedInt128(1);
    tmp.shiftLeftDestructive(126);
    tmp.multiplyDestructive(new UnsignedInt128(2));
    try {
      tmp.multiplyDestructive(new UnsignedInt128(2));
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }

    UnsignedInt128 complicated1 = new UnsignedInt128(0xF9892FCA, 0x59D109AD,
        0x0534AB4C, 0);
    BigInteger bigInteger1 = complicated1.toBigIntegerSlow();
    UnsignedInt128 complicated2 = new UnsignedInt128(54234234, 9, 0, 0);
    BigInteger bigInteger2 = complicated2.toBigIntegerSlow();
    complicated1.multiplyDestructive(complicated2);
    BigInteger ans = bigInteger1.multiply(bigInteger2);
    assertEquals(ans, complicated1.toBigIntegerSlow(), () -> "Add assertion message");

    try {
      UnsignedInt128 complicated3 = new UnsignedInt128(0xF9892FCA, 0x59D109AD,
          0x0534AB4C, 0);
      complicated3
          .multiplyDestructive(new UnsignedInt128(54234234, 9845, 0, 0));
      // Line removed due to Duplicate Assert: fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testMultiplyScaleDownTenDestructiveScaleTen() {
    for (int scale = 0; scale < 38; ++scale) {
      UnsignedInt128 right = new UnsignedInt128(1);
      right.scaleUpTenDestructive((short) scale);
      {
        // 10000000....000
        UnsignedInt128 leftJust = new UnsignedInt128(1);
        leftJust.scaleUpTenDestructive((short) 15);
        UnsignedInt128 leftInc = leftJust.incrementConstructive();
        UnsignedInt128 leftDec = leftJust.decrementConstructive();

        if (scale + 10 <= 38) {
          leftJust.multiplyScaleDownTenDestructive(right, (short) (scale + 10));
          assertEquals("scale=" + scale, 100000L, leftJust.asLong());
          leftInc.multiplyScaleDownTenDestructive(right, (short) (scale + 10));
          assertEquals("scale=" + scale, 100000L, leftInc.asLong());
          leftDec.multiplyScaleDownTenDestructive(right, (short) (scale + 10));
          assertEquals("scale=" + scale, 100000L, leftDec.asLong());
        } else {
          leftJust.multiplyScaleDownTenDestructive(right, (short) (scale + 10));
          assertEquals("scale=" + scale, 0L, leftJust.asLong());
          leftInc.multiplyScaleDownTenDestructive(right, (short) (scale + 10));
          assertEquals("scale=" + scale, 0L, leftInc.asLong());
          leftDec.multiplyScaleDownTenDestructive(right, (short) (scale + 10));
          assertEquals("scale=" + scale, 0L, leftDec.asLong());
        }
      }

      {
        // 10000500....00
        UnsignedInt128 leftHalfJust = new UnsignedInt128(1);
        leftHalfJust.scaleUpTenDestructive((short) 6);
        leftHalfJust.addDestructive(new UnsignedInt128(5));
        leftHalfJust.scaleUpTenDestructive((short) 9);
        UnsignedInt128 leftHalfInc = leftHalfJust.incrementConstructive();
        UnsignedInt128 leftHalfDec = leftHalfJust.decrementConstructive();

        if (scale + 10 <= 38) {
          leftHalfJust.multiplyScaleDownTenDestructive(right,
              (short) (scale + 10));
          assertEquals("scale=" + scale, 100001L, leftHalfJust.asLong());
          leftHalfInc.multiplyScaleDownTenDestructive(right,
              (short) (scale + 10));
          assertEquals("scale=" + scale, 100001L, leftHalfInc.asLong());
          leftHalfDec.multiplyScaleDownTenDestructive(right,
              (short) (scale + 10));
          assertEquals("scale=" + scale, 100000L, leftHalfDec.asLong());
        } else {
          leftHalfJust.multiplyScaleDownTenDestructive(right,
              (short) (scale + 10));
          assertEquals("scale=" + scale, 0L, leftHalfJust.asLong());
          leftHalfInc.multiplyScaleDownTenDestructive(right,
              (short) (scale + 10));
          assertEquals("scale=" + scale, 0L, leftHalfInc.asLong());
          leftHalfDec.multiplyScaleDownTenDestructive(right,
              (short) (scale + 10));
          assertEquals("scale=" + scale, 0L, leftHalfDec.asLong());
        }
      }
    }
  }

  @Test
  public void testDivideDestructiveInt() {
    one = new UnsignedInt128(1);
    two = new UnsignedInt128(2);
    two.divideDestructive(1);
    assertEquals(1L, one.asLong(), () -> "Add assertion message");
    assertEquals(2L, two.asLong(), () -> "Add assertion message");
    one.divideDestructive(2);
    assertEquals(0L, one.asLong(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2L, two.asLong(), () -> "Add assertion message");

    UnsignedInt128 var1 = new UnsignedInt128(1234234662345L);
    var1.divideDestructive(642337);
    assertEquals(1234234662345L / 642337L, var1.asLong(), () -> "Add assertion message");

    UnsignedInt128 complicated1 = new UnsignedInt128(0xF9892FCA, 0x59D109AD,
        0x0534AB4C, 0);
    BigInteger bigInteger1 = complicated1.toBigIntegerSlow();
    complicated1.divideDestructive(1534223465);
    BigInteger bigInteger2 = BigInteger.valueOf(1534223465);
    BigInteger ans = bigInteger1.divide(bigInteger2);
    assertEquals(ans, complicated1.toBigIntegerSlow(), () -> "Add assertion message");

    try {
      complicated1.divideDestructive(0);
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testDivideDestructiveUnsignedInt128() {
    one = new UnsignedInt128(1);
    zero = new UnsignedInt128(0);
    two = new UnsignedInt128(2);
    UnsignedInt128 remainder = new UnsignedInt128();
    two.divideDestructive(one, remainder);
    assertEquals(1L, one.asLong(), () -> "Add assertion message");
    assertEquals(2L, two.asLong(), () -> "Add assertion message");
    assertEquals(zero, remainder, () -> "Add assertion message");
    one.divideDestructive(two, remainder);
    assertEquals(0L, one.asLong(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2L, two.asLong(), () -> "Add assertion message");
    assertEquals(new UnsignedInt128(1), remainder, () -> "Add assertion message");

    UnsignedInt128 var1 = new UnsignedInt128(1234234662345L);
    var1.divideDestructive(new UnsignedInt128(642337), remainder);
    assertEquals(1234234662345L / 642337L, var1.asLong(), () -> "Add assertion message");
    assertEquals(1234234662345L % 642337L, remainder.asLong(), () -> "Add assertion message");

    UnsignedInt128 complicated1 = new UnsignedInt128(0xF9892FCA, 0x59D109AD,
        0x0534AB4C, 0x42395ADC);
    UnsignedInt128 complicated2 = new UnsignedInt128(0xF09DC19A, 0x00001234, 0,
        0);
    BigInteger bigInteger1 = complicated1.toBigIntegerSlow();
    BigInteger bigInteger2 = complicated2.toBigIntegerSlow();
    complicated1.divideDestructive(complicated2, remainder);
    BigInteger ans = bigInteger1.divide(bigInteger2);
    assertEquals(ans, complicated1.toBigIntegerSlow(), () -> "Add assertion message");

    try {
      complicated1.divideDestructive(zero, remainder);
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testDivideDestructiveUnsignedInt128Again() {
    UnsignedInt128 complicated1 = new UnsignedInt128(0xF9892FCA, 0x59D109AD, 0,
        0);
    UnsignedInt128 complicated2 = new UnsignedInt128(0xF09DC19A, 3, 0, 0);
    BigInteger bigInteger1 = complicated1.toBigIntegerSlow();
    BigInteger bigInteger2 = complicated2.toBigIntegerSlow();
    complicated1.divideDestructive(complicated2, new UnsignedInt128());
    BigInteger ans = bigInteger1.divide(bigInteger2);
    assertEquals(ans, complicated1.toBigIntegerSlow(), () -> "Add assertion message");
  }

  @Test
  public void testBigIntConversion() {
    BigInteger bigInteger = BigInteger.valueOf(0x1ABCDEF0123456L);
    UnsignedInt128 uInt128 = new UnsignedInt128(bigInteger);
    assertEquals(bigInteger, uInt128.toBigIntegerSlow(), () -> "Add assertion message");
  }
}