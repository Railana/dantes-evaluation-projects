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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import org.junit.Test;

public class PairTest {

  /**
   * Test method for {@link org.apache.accumulo.core.util.Pair#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject() {
    Pair<Integer,String> pair = new Pair<>(25, "twenty-five");
    Pair<Integer,String> pair2 = new Pair<>(25, "twenty-five");
    assertEquals(pair, pair2, () -> "Add assertion message");
  }

  /**
   * Test method for {@link org.apache.accumulo.core.util.Pair#getFirst()}.
   */
  @Test
  public void testGetFirst() {
    Pair<Integer,String> pair = new Pair<>(25, "twenty-five");
    assertEquals((Integer) 25, pair.getFirst(), () -> "Add assertion message");
  }

  /**
   * Test method for {@link org.apache.accumulo.core.util.Pair#getSecond()}.
   */
  @Test
  public void testGetSecond() {
    Pair<Integer,String> pair = new Pair<>(25, "twenty-five");
    assertEquals("twenty-five", pair.getSecond(), () -> "Add assertion message");
  }

  /**
   * Test method for {@link org.apache.accumulo.core.util.Pair#toString()}.
   */
  @Test
  public void testToString() {
    Pair<Integer,String> pair = new Pair<>(25, "twenty-five");
    assertEquals("(25,twenty-five)", pair.toString(), () -> "Add assertion message");
  }

  /**
   * Test method for
   * {@link org.apache.accumulo.core.util.Pair#toString(java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testToStringStringStringString() {
    Pair<Integer,String> pair = new Pair<>(25, "twenty-five");
    assertEquals("---25~~~twenty-five+++", pair.toString("---", "~~~", "+++"), () -> "Add assertion message");
  }

  /**
   * Test method for {@link org.apache.accumulo.core.util.Pair#toMapEntry()}.
   */
  @Test
  public void testToMapEntry() {
    Pair<Integer,String> pair = new Pair<>(10, "IO");

    Entry<Integer,String> entry = pair.toMapEntry();
    assertEquals(pair.getFirst(), entry.getKey(), () -> "Add assertion message");
    assertEquals(pair.getSecond(), entry.getValue(), () -> "Add assertion message");
  }

  /**
   * Test method for {@link org.apache.accumulo.core.util.Pair#swap()}.
   */
  @Test
  public void testSwap() {
    Pair<Integer,String> pair = new Pair<>(25, "twenty-five");
    assertEquals(pair, pair.swap().swap(), () -> "Add assertion message");
    Pair<String,Integer> pair2 = new Pair<>("twenty-five", 25);
    assertEquals(pair, pair2.swap(), () -> "Add assertion message");
    assertEquals(pair2, pair.swap(), () -> "Add assertion message");
  }

  /**
   * Test method for {@link org.apache.accumulo.core.util.Pair#fromEntry(java.util.Map.Entry)}.
   */
  @Test
  public void testFromEntry() {
    Entry<Integer,String> entry = new SimpleImmutableEntry<>(10, "IO");

    Pair<Integer,String> pair0 = Pair.fromEntry(entry);
    assertEquals(entry.getKey(), pair0.getFirst(), () -> "Add assertion message");
    assertEquals(entry.getValue(), pair0.getSecond(), () -> "Add assertion message");

    Pair<Object,Object> pair = Pair.<Object,Object,Integer,String>fromEntry(entry);
    assertEquals(entry.getKey(), pair.getFirst(), () -> "Add assertion message");
    assertEquals(entry.getValue(), pair.getSecond(), () -> "Add assertion message");

    Pair<Number,CharSequence> pair2 = Pair.<Number,CharSequence,Integer,String>fromEntry(entry);
    assertEquals(entry.getKey(), pair2.getFirst(), () -> "Add assertion message");
    assertEquals(entry.getValue(), pair2.getSecond(), () -> "Add assertion message");
  }

}