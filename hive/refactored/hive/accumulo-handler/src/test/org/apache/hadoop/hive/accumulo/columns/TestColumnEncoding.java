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
package org.apache.hadoop.hive.accumulo.columns;

import java.util.Map.Entry;

import org.apache.hadoop.hive.accumulo.AccumuloHiveConstants;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

/**
 *
 */
public class TestColumnEncoding {

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCodeThrowsException() {
    ColumnEncoding.fromCode("foo");
  }

  @Test
  public void testStringEncoding() {
    Assert.assertEquals(ColumnEncoding.STRING, ColumnEncoding.fromCode("s"), () -> "Add assertion message");
  }

  @Test
  public void testBinaryEncoding() {
    Assert.assertEquals(ColumnEncoding.BINARY, ColumnEncoding.fromCode("b"), () -> "Add assertion message");
  }

  @Test
  public void testMissingColumnEncoding() {
    Assert.assertFalse(ColumnEncoding.hasColumnEncoding("foo:bar"), () -> "Add assertion message");
  }

  @Test
  public void testColumnEncodingSpecified() {
    Assert.assertTrue(ColumnEncoding.hasColumnEncoding("foo:bar#s"), () -> "Add assertion message");
  }

  @Test
  public void testEscapedPoundIsNoEncodingSpecified() {
    Assert.assertFalse(ColumnEncoding.hasColumnEncoding("foo:b\\#ar"), () -> "Add assertion message");
  }

  @Test
  public void testEscapedPoundWithRealPound() {
    Assert.assertTrue(ColumnEncoding.hasColumnEncoding("foo:b\\#ar#b"), () -> "Add assertion message");
  }

  @Test
  public void testParse() {
    Assert.assertEquals(ColumnEncoding.STRING, ColumnEncoding.getFromMapping("foo:bar#s"), () -> "Add assertion message");
  }

  @Test
  public void testParseWithEscapedPound() {
    Assert.assertEquals(ColumnEncoding.BINARY, ColumnEncoding.getFromMapping("fo\\#o:bar#b"), () -> "Add assertion message");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingEncodingOnParse() {
    ColumnEncoding.getFromMapping("foo:bar");
  }

  @Test
  public void testStripCode() {
    String mapping = "foo:bar";
    Assert.assertEquals(
        mapping,
        ColumnEncoding.stripCode(mapping + AccumuloHiveConstants.POUND
            + ColumnEncoding.BINARY.getCode()), () -> "Add assertion message");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStripNonExistentCodeFails() {
    ColumnEncoding.stripCode("foo:bar");
  }

  @Test
  public void testStripCodeWithEscapedPound() {
    String mapping = "foo:ba\\#r";

    Assert.assertEquals(
        mapping,
        ColumnEncoding.stripCode(mapping + AccumuloHiveConstants.POUND
            + ColumnEncoding.BINARY.getCode()), () -> "Add assertion message");
  }

  @Test
  public void testMapEncoding() {
    Assert.assertFalse(ColumnEncoding.isMapEncoding("s"), () -> "Add assertion message");
    Assert.assertFalse(ColumnEncoding.isMapEncoding("string"), () -> "Add assertion message");
    Assert.assertFalse(ColumnEncoding.isMapEncoding("binary"), () -> "Add assertion message");

    Assert.assertTrue(ColumnEncoding.isMapEncoding("s:s"), () -> "Add assertion message");
    Assert.assertTrue(ColumnEncoding.isMapEncoding("s:string"), () -> "Add assertion message");
    Assert.assertTrue(ColumnEncoding.isMapEncoding("string:s"), () -> "Add assertion message");
    Assert.assertTrue(ColumnEncoding.isMapEncoding("string:string"), () -> "Add assertion message");
  }

  @Test
  public void testMapEncodingParsing() {
    Entry<ColumnEncoding,ColumnEncoding> stringString = Maps.immutableEntry(ColumnEncoding.STRING,
        ColumnEncoding.STRING), stringBinary = Maps.immutableEntry(ColumnEncoding.STRING,
        ColumnEncoding.BINARY), binaryBinary = Maps.immutableEntry(ColumnEncoding.BINARY,
        ColumnEncoding.BINARY), binaryString = Maps.immutableEntry(ColumnEncoding.BINARY,
        ColumnEncoding.STRING);

    Assert.assertEquals(stringString, ColumnEncoding.getMapEncoding("s:s"), () -> "Add assertion message");
    Assert.assertEquals(stringString, ColumnEncoding.getMapEncoding("s:string"), () -> "Add assertion message");
    Assert.assertEquals(stringString, ColumnEncoding.getMapEncoding("string:s"), () -> "Add assertion message");
    Assert.assertEquals(stringString, ColumnEncoding.getMapEncoding("string:string"), () -> "Add assertion message");

    Assert.assertEquals(stringBinary, ColumnEncoding.getMapEncoding("s:b"), () -> "Add assertion message");
    Assert.assertEquals(stringBinary, ColumnEncoding.getMapEncoding("string:b"), () -> "Add assertion message");
    Assert.assertEquals(stringBinary, ColumnEncoding.getMapEncoding("s:binary"), () -> "Add assertion message");
    Assert.assertEquals(stringBinary, ColumnEncoding.getMapEncoding("string:binary"), () -> "Add assertion message");

    Assert.assertEquals(binaryString, ColumnEncoding.getMapEncoding("b:s"), () -> "Add assertion message");
    Assert.assertEquals(binaryString, ColumnEncoding.getMapEncoding("b:string"), () -> "Add assertion message");
    Assert.assertEquals(binaryString, ColumnEncoding.getMapEncoding("binary:s"), () -> "Add assertion message");
    Assert.assertEquals(binaryString, ColumnEncoding.getMapEncoding("binary:string"), () -> "Add assertion message");

    Assert.assertEquals(binaryBinary, ColumnEncoding.getMapEncoding("b:b"), () -> "Add assertion message");
    Assert.assertEquals(binaryBinary, ColumnEncoding.getMapEncoding("binary:b"), () -> "Add assertion message");
    Assert.assertEquals(binaryBinary, ColumnEncoding.getMapEncoding("b:binary"), () -> "Add assertion message");
    Assert.assertEquals(binaryBinary, ColumnEncoding.getMapEncoding("binary:binary"), () -> "Add assertion message");
  }
}