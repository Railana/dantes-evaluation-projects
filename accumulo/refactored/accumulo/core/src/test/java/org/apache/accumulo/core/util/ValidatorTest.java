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
package org.apache.accumulo.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ValidatorTest {
  private static class TestValidator extends Validator<String> {
    private final String s;

    TestValidator(String s) {
      this.s = s;
    }

    @Override
    public boolean apply(String argument) {
      return s.equals(argument);
    }
  }

  private static class Test2Validator extends Validator<String> {
    private final String ps;

    Test2Validator(String s) {
      ps = s;
    }

    @Override
    public boolean apply(String argument) {
      return (argument != null && argument.matches(ps));
    }
  }

  private Validator<String> v, v2, v3;

  @Before
  public void setUp() {
    v = new TestValidator("correct");
    // Removed line due to General Fixture smell: v = new TestValidator("correct");
    // Removed line due to General Fixture smell: v2 = new TestValidator("righto");
    // Removed line due to General Fixture smell: v3 = new Test2Validator("c.*");
  }

  @Test
  public void testValidate_Success() {
    v = new TestValidator("correct");
    assertEquals("correct", v.validate("correct"), () -> "Add assertion message");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidate_Failure() {
    v = new TestValidator("correct");
    v.validate("incorrect");
  }

  @Test
  public void testInvalidMessage() {
    v = new TestValidator("correct");
    assertEquals("Invalid argument incorrect", v.invalidMessage("incorrect"), () -> "Add assertion message");
  }

  @Test
  public void testAnd() {
    v = new TestValidator("correct");
    v3 = new Test2Validator("c.*");
    Validator<String> vand = v3.and(v);
    assertTrue(vand.apply("correct"), () -> "Add assertion message");
    assertFalse(vand.apply("righto"), () -> "Add assertion message");
    assertFalse(vand.apply("coriander"), () -> "Add assertion message");
  }

  @Test
  public void testOr() {
    v = new TestValidator("correct");
    v2 = new TestValidator("righto");
    Validator<String> vor = v.or(v2);
    assertTrue(vor.apply("correct"), () -> "Add assertion message");
    assertTrue(vor.apply("righto"), () -> "Add assertion message");
    assertFalse(vor.apply("coriander"), () -> "Add assertion message");
  }

  @Test
  public void testNot() {
    v = new TestValidator("correct");
    v3 = new Test2Validator("c.*");
    Validator<String> vnot = v3.not();
    assertFalse(vnot.apply("correct"), () -> "Add assertion message");
    assertFalse(vnot.apply("coriander"), () -> "Add assertion message");
    assertTrue(vnot.apply("righto"), () -> "Add assertion message");
  }
}