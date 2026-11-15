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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testcases for {@link SignedInt128}
 *
 * This code was based on code from Microsoft PolyBase.
 */
public class TestSignedInt128 {
  private SignedInt128 zero;

  private SignedInt128 one;

  private SignedInt128 two;

  private SignedInt128 negativeOne;

  private SignedInt128 negativeTwo;

  @Before
  public void setUp() throws Exception {
    // Removed line due to General Fixture smell: zero = new SignedInt128(0);
    // Removed line due to General Fixture smell: one = new SignedInt128(1);
    // Removed line due to General Fixture smell: two = new SignedInt128(2);
    // Removed line due to General Fixture smell: negativeOne = new SignedInt128(-1);
    // Removed line due to General Fixture smell: negativeTwo = new SignedInt128(-2);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testHashCode() {
    negativeTwo = new SignedInt128(-2);
    negativeOne = new SignedInt128(-1);
    one = new SignedInt128(1);
    zero = new SignedInt128(0);
    two = new SignedInt128(2);
    assertTrue(one.hashCode() != two.hashCode(), () -> "Add assertion message");
    assertTrue(zero.hashCode() != one.hashCode(), () -> "Add assertion message");
    assertTrue(zero.hashCode() != two.hashCode(), () -> "Add assertion message");

    assertTrue(one.hashCode() != negativeOne.hashCode(), () -> "Add assertion message");
    assertTrue(two.hashCode() != negativeTwo.hashCode(), () -> "Add assertion message");
    assertEquals(zero.hashCode(), new SignedInt128(-0).hashCode(), () -> "Add assertion message");

    assertEquals(zero.hashCode(), new SignedInt128(0).hashCode(), () -> "Add assertion message");
    assertEquals(one.hashCode(), new SignedInt128(1).hashCode(), () -> "Add assertion message");
    assertEquals(two.hashCode(), new SignedInt128(2).hashCode(), () -> "Add assertion message");
  }

  @Test
  public void testEquals() {
    negativeTwo = new SignedInt128(-2);
    negativeOne = new SignedInt128(-1);
    one = new SignedInt128(1);
    zero = new SignedInt128(0);
    two = new SignedInt128(2);
    assertTrue(!one.equals(two), () -> "Add assertion message");
    assertTrue(!zero.equals(one), () -> "Add assertion message");
    assertTrue(!zero.equals(two), () -> "Add assertion message");

    assertEquals(zero, new SignedInt128(0), () -> "Add assertion message");
    assertEquals(one, new SignedInt128(1), () -> "Add assertion message");
    assertEquals(two, new SignedInt128(2), () -> "Add assertion message");

    assertTrue(!one.equals(negativeOne), () -> "Add assertion message");
    assertTrue(!two.equals(negativeTwo), () -> "Add assertion message");
    assertEquals(zero, new SignedInt128(-0), () -> "Add assertion message");
  }

  @Test
  public void testCompareTo() {
    negativeTwo = new SignedInt128(-2);
    negativeOne = new SignedInt128(-1);
    one = new SignedInt128(1);
    zero = new SignedInt128(0);
    two = new SignedInt128(2);
    assertTrue(one.compareTo(two) < 0, () -> "Add assertion message");
    assertTrue(two.compareTo(one) > 0, () -> "Add assertion message");
    assertTrue(one.compareTo(zero) > 0, () -> "Add assertion message");
    assertTrue(zero.compareTo(two) < 0, () -> "Add assertion message");

    assertTrue(zero.compareTo(negativeOne) > 0, () -> "Add assertion message");
    assertTrue(zero.compareTo(negativeTwo) > 0, () -> "Add assertion message");
    assertTrue(one.compareTo(negativeOne) > 0, () -> "Add assertion message");
    assertTrue(one.compareTo(negativeTwo) > 0, () -> "Add assertion message");
    assertTrue(two.compareTo(negativeOne) > 0, () -> "Add assertion message");
    assertTrue(two.compareTo(negativeTwo) > 0, () -> "Add assertion message");
    assertTrue(negativeOne.compareTo(negativeTwo) > 0, () -> "Add assertion message");
    assertTrue(negativeTwo.compareTo(negativeOne) < 0, () -> "Add assertion message");
  }

  @Test
  public void testToFormalString() {
    negativeTwo = new SignedInt128(-2);
    negativeOne = new SignedInt128(-1);
    one = new SignedInt128(1);
    zero = new SignedInt128(0);
    assertEquals("0", zero.toFormalString(), () -> "Add assertion message");
    assertEquals("1", one.toFormalString(), () -> "Add assertion message");
    assertEquals("-1", negativeOne.toFormalString(), () -> "Add assertion message");
    assertEquals("-2", negativeTwo.toFormalString(), () -> "Add assertion message");

    assertEquals("30", new SignedInt128(30).toFormalString(), () -> "Add assertion message");
    assertEquals("680000000000",
        new SignedInt128(680000000000L).toFormalString(), () -> "Add assertion message");
    assertEquals("6800000000000",
        new SignedInt128(6800000000000L).toFormalString(), () -> "Add assertion message");
    assertEquals("68", new SignedInt128(68).toFormalString(), () -> "Add assertion message");

    assertEquals("-30", new SignedInt128(-30).toFormalString(), () -> "Add assertion message");
    assertEquals("-680000000000",
        new SignedInt128(-680000000000L).toFormalString(), () -> "Add assertion message");
    assertEquals("-6800000000000",
        new SignedInt128(-6800000000000L).toFormalString(), () -> "Add assertion message");
    assertEquals("-68", new SignedInt128(-68).toFormalString(), () -> "Add assertion message");

    assertEquals(zero, new SignedInt128("0"), () -> "Add assertion message");
    assertEquals(one, new SignedInt128("1"), () -> "Add assertion message");

    assertEquals(zero, new SignedInt128("-0"), () -> "Add assertion message");
    assertEquals(negativeOne, new SignedInt128("-1"), () -> "Add assertion message");
    assertEquals(negativeTwo, new SignedInt128("-2"), () -> "Add assertion message");

    assertEquals(new SignedInt128(30), new SignedInt128("30"), () -> "Add assertion message");
    assertEquals(new SignedInt128(680000000000L), new SignedInt128(
        "680000000000"), () -> "Add assertion message");
    assertEquals(new SignedInt128(6800000000000L), new SignedInt128(
        "6800000000000"), () -> "Add assertion message");
    assertEquals(new SignedInt128(68), new SignedInt128("68"), () -> "Add assertion message");

    assertEquals(new SignedInt128(-30), new SignedInt128("-30"), () -> "Add assertion message");
    assertEquals(new SignedInt128(-680000000000L), new SignedInt128(
        "-680000000000"), () -> "Add assertion message");
    assertEquals(new SignedInt128(-6800000000000L), new SignedInt128(
        "-6800000000000"), () -> "Add assertion message");
    assertEquals(new SignedInt128(-68), new SignedInt128("-68"), () -> "Add assertion message");
  }

  @Test
  public void testSignedInt128() {
    assertEquals(0L, new SignedInt128().longValue(), () -> "Add assertion message");
  }

  @Test
  public void testSignedInt128SignedInt128() {
    one = new SignedInt128(1);
    two = new SignedInt128(2);
    assertEquals(1L, new SignedInt128(one).longValue(), () -> "Add assertion message");
    assertEquals(2L, new SignedInt128(two).longValue(), () -> "Add assertion message");
  }

  @Test
  public void testSignedInt128IntIntIntInt() {
    assertEquals(((long) 11) << 32L | 23L,
        new SignedInt128(23, 11, 0, 0).longValue(), () -> "Add assertion message");
  }

  @Test
  public void testZeroClear() {
    one = new SignedInt128(1);
    zero = new SignedInt128(0);
    two = new SignedInt128(2);
    assertFalse(one.isZero(), () -> "Add assertion message");
    assertFalse(two.isZero(), () -> "Add assertion message");
    assertTrue(0L != one.longValue(), () -> "Add assertion message");
    assertTrue(0L != two.longValue(), () -> "Add assertion message");

    two.zeroClear();

    // Line removed due to Duplicate Assert: assertTrue(0L != one.longValue(), () -> "Add assertion message");
    assertEquals(0L, two.longValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(one.isZero(), () -> "Add assertion message");
    assertTrue(two.isZero(), () -> "Add assertion message");

    one.zeroClear();

    assertEquals(0L, one.longValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0L, two.longValue(), () -> "Add assertion message");
    assertTrue(one.isZero(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(two.isZero(), () -> "Add assertion message");
  }

  @Test
  public void testAddDestructive() {
    one = new SignedInt128(1);
    two = new SignedInt128(2);
    one.addDestructive(two);
    assertEquals(3L, one.longValue(), () -> "Add assertion message");
    assertEquals(2L, two.longValue(), () -> "Add assertion message");

    SignedInt128 big = new SignedInt128((1L << 62) + 3L);
    SignedInt128 tmp = new SignedInt128(0L);
    for (int i = 0; i < 54; ++i) {
      tmp.addDestructive(big);
    }

    assertEquals(3 * 54, tmp.getV0(), () -> "Add assertion message");
    assertEquals(0x80000000, tmp.getV1()); // (54 % 4, () -> "Add assertion message");
    Integer magicNumber55926741 = 13; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber55926741, tmp.getV2(), () -> "Add assertion message");
    assertEquals(0, tmp.getV3(), () -> "Add assertion message");

    assertEquals((1L << 62) + 3L, big.longValue(), () -> "Add assertion message");

    SignedInt128 huge = new SignedInt128(one);
    huge.shiftLeftDestructive(125);
    SignedInt128 huge2 = new SignedInt128(one);
    huge2.shiftLeftDestructive(125);
    try {
      huge2.addDestructive(huge);
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testSubtractDestructive() {
    one = new SignedInt128(1);
    two = new SignedInt128(2);
    two.subtractDestructive(one);
    assertEquals(1L, one.longValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1L, one.longValue(), () -> "Add assertion message");

    one.subtractDestructive(new SignedInt128(10L));
    assertEquals(-9L, one.longValue(), () -> "Add assertion message");

    SignedInt128 big = new SignedInt128((1L << 62) + (3L << 34) + 3L);
    big.shiftLeftDestructive(6);
    SignedInt128 tmp = new SignedInt128((1L << 61) + 5L);
    tmp.shiftLeftDestructive(6);

    big.subtractDestructive(tmp);
    big.subtractDestructive(tmp);

    assertEquals((3 << 6) - 2 * (5 << 6), big.getV0(), () -> "Add assertion message");
    assertEquals((3 << 8) - 1, big.getV1(), () -> "Add assertion message");
    Integer magicNumber36591405 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber36591405, big.getV2(), () -> "Add assertion message");
    assertEquals(0, big.getV3(), () -> "Add assertion message");
  }

  @Test
  public void testMultiplyDestructiveInt() {
    one = new SignedInt128(1);
    two = new SignedInt128(2);
    two.multiplyDestructive(1);
    assertEquals(2L, two.longValue(), () -> "Add assertion message");
    assertEquals(1L, one.longValue(), () -> "Add assertion message");
    two.multiplyDestructive(2);
    assertEquals(4L, two.longValue(), () -> "Add assertion message");

    Integer magicNumber81131651 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    SignedInt128 five = new SignedInt128(magicNumber81131651
    Integer magicNumber95635793 = 6432346; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    five.multiplyDestructive(magicNumber95635793
    assertEquals(6432346 * 5, five.getV0(), () -> "Add assertion message");
    assertEquals(0, five.getV1(), () -> "Add assertion message");
    assertEquals(0, five.getV2(), () -> "Add assertion message");
    assertEquals(0, five.getV3(), () -> "Add assertion message");

    SignedInt128 big = new SignedInt128((1L << 62) + (3L << 34) + 3L);
    Integer magicNumber40498010 = 96; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    big.multiplyDestructive(magicNumber40498010

    assertEquals(3 * 96, big.getV0(), () -> "Add assertion message");
    assertEquals(96 * (3 << 2), big.getV1(), () -> "Add assertion message");
    assertEquals(96 / 4, big.getV2(), () -> "Add assertion message");
    assertEquals(0, big.getV3(), () -> "Add assertion message");

    SignedInt128 tmp = new SignedInt128(1);
    tmp.shiftLeftDestructive(126);
    try {
      tmp.multiplyDestructive(2);
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testShiftDestructive() {
    SignedInt128 big = new SignedInt128((1L << 62) + (23L << 32) + 89L);
    Integer magicNumber3215445 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    big.shiftLeftDestructive(magicNumber3215445

    assertEquals(89 * 4, big.getV0(), () -> "Add assertion message");
    assertEquals(23 * 4, big.getV1(), () -> "Add assertion message");
    Integer magicNumber54855446 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber54855446, big.getV2(), () -> "Add assertion message");
    Integer magicNumber60269857 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber60269857, big.getV3(), () -> "Add assertion message");

    big.shiftLeftDestructive(32);

    Integer magicNumber5868356 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber5868356, big.getV0(), () -> "Add assertion message");
    assertEquals(89 * 4, big.getV1(), () -> "Add assertion message");
    assertEquals(23 * 4, big.getV2(), () -> "Add assertion message");
    assertEquals(1, big.getV3(), () -> "Add assertion message");

    Integer magicNumber53245706 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    big.shiftRightDestructive(magicNumber53245706, true);

    // Line removed due to Duplicate Assert: assertEquals(0, big.getV0(), () -> "Add assertion message");
    assertEquals(89, big.getV1(), () -> "Add assertion message");
    assertEquals(23 + (1 << 30), big.getV2(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, big.getV3(), () -> "Add assertion message");

    big.shiftRightDestructive(32, true);

    assertEquals(89, big.getV0(), () -> "Add assertion message");
    assertEquals(23 + (1 << 30), big.getV1(), () -> "Add assertion message");
    assertEquals(0, big.getV2(), () -> "Add assertion message");
    Integer magicNumber25748853 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    // Line removed due to Duplicate Assert: assertEquals(magicNumber25748853, big.getV3(), () -> "Add assertion message");

    // test rounding
    SignedInt128 tmp = new SignedInt128(17);
    assertEquals(17, tmp.getV0(), () -> "Add assertion message");
    Integer magicNumber18938503 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    tmp.shiftRightDestructive(magicNumber18938503, true);
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
  public void testMultiplyDestructiveSignedInt128() {
    one = new SignedInt128(1);
    two = new SignedInt128(2);
    two.multiplyDestructive(one);
    assertEquals(2L, two.longValue(), () -> "Add assertion message");
    assertEquals(1L, one.longValue(), () -> "Add assertion message");
    two.multiplyDestructive(two);
    assertEquals(4L, two.longValue(), () -> "Add assertion message");

    Integer magicNumber92920726 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    SignedInt128 five = new SignedInt128(magicNumber92920726
    five.multiplyDestructive(new SignedInt128(6432346));
    assertEquals(6432346 * 5, five.getV0(), () -> "Add assertion message");
    assertEquals(0, five.getV1(), () -> "Add assertion message");
    assertEquals(0, five.getV2(), () -> "Add assertion message");
    assertEquals(0, five.getV3(), () -> "Add assertion message");

    SignedInt128 big = new SignedInt128((1L << 62) + (3L << 34) + 3L);
    big.multiplyDestructive(new SignedInt128(96));

    assertEquals(3 * 96, big.getV0(), () -> "Add assertion message");
    assertEquals(96 * (3 << 2), big.getV1(), () -> "Add assertion message");
    assertEquals(96 / 4, big.getV2(), () -> "Add assertion message");
    assertEquals(0, big.getV3(), () -> "Add assertion message");

    SignedInt128 tmp = new SignedInt128(1);
    tmp.shiftLeftDestructive(126);
    try {
      tmp.multiplyDestructive(new SignedInt128(2));
      fail();
    } catch (ArithmeticException ex) {
      // ok
    }

    SignedInt128 complicated1 = new SignedInt128(0xF9892FCA, 0x59D109AD,
        0x0534AB4C, 0);
    BigInteger bigInteger1 = complicated1.toBigIntegerSlow();
    SignedInt128 complicated2 = new SignedInt128(54234234, 9, 0, 0);
    BigInteger bigInteger2 = complicated2.toBigIntegerSlow();
    complicated1.multiplyDestructive(complicated2);
    BigInteger ans = bigInteger1.multiply(bigInteger2);
    assertEquals(ans, complicated1.toBigIntegerSlow(), () -> "Add assertion message");

    try {
      SignedInt128 complicated3 = new SignedInt128(0xF9892FCA, 0x59D109AD,
          0x0534AB4C, 0);
      complicated3.multiplyDestructive(new SignedInt128(54234234, 9845, 0, 0));
      // Line removed due to Duplicate Assert: fail();
    } catch (ArithmeticException ex) {
      // ok
    }
  }

  @Test
  public void testDivideDestructiveInt() {
    one = new SignedInt128(1);
    two = new SignedInt128(2);
    two.divideDestructive(1);
    assertEquals(1L, one.longValue(), () -> "Add assertion message");
    assertEquals(2L, two.longValue(), () -> "Add assertion message");
    one.divideDestructive(2);
    assertEquals(0L, one.longValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2L, two.longValue(), () -> "Add assertion message");

    SignedInt128 var1 = new SignedInt128(1234234662345L);
    var1.divideDestructive(642337);
    assertEquals(1234234662345L / 642337L, var1.longValue(), () -> "Add assertion message");

    SignedInt128 complicated1 = new SignedInt128(0xF9892FCA, 0x59D109AD,
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
  public void testDivideDestructiveSignedInt128() {
    one = new SignedInt128(1);
    zero = new SignedInt128(0);
    two = new SignedInt128(2);
    SignedInt128 remainder = new SignedInt128();
    two.divideDestructive(one, remainder);
    assertEquals(1L, one.longValue(), () -> "Add assertion message");
    assertEquals(2L, two.longValue(), () -> "Add assertion message");
    assertEquals(zero, remainder, () -> "Add assertion message");
    one.divideDestructive(two, remainder);
    assertEquals(0L, one.longValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2L, two.longValue(), () -> "Add assertion message");
    assertEquals(new SignedInt128(1), remainder, () -> "Add assertion message");

    SignedInt128 var1 = new SignedInt128(1234234662345L);
    var1.divideDestructive(new SignedInt128(642337), remainder);
    assertEquals(1234234662345L / 642337L, var1.longValue(), () -> "Add assertion message");
    assertEquals(1234234662345L % 642337L, remainder.longValue(), () -> "Add assertion message");

    SignedInt128 complicated1 = new SignedInt128(0xF9892FCA, 0x59D109AD,
        0x0534AB4C, 0x42395ADC);
    SignedInt128 complicated2 = new SignedInt128(0xF09DC19A, 0x00001234, 0, 0);
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
  public void testDivideDestructiveSignedInt128Again() {
    SignedInt128 complicated1 = new SignedInt128(0xF9892FCA, 0x59D109AD, 0, 0);
    SignedInt128 complicated2 = new SignedInt128(0xF09DC19A, 3, 0, 0);
    BigInteger bigInteger1 = complicated1.toBigIntegerSlow();
    BigInteger bigInteger2 = complicated2.toBigIntegerSlow();
    complicated1.divideDestructive(complicated2, new SignedInt128());
    BigInteger ans = bigInteger1.divide(bigInteger2);
    assertEquals(ans, complicated1.toBigIntegerSlow(), () -> "Add assertion message");
  }
}