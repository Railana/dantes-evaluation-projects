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
package org.apache.accumulo.core.data;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

public class ConditionalMutationTest {
  private static final byte[] ROW = "row".getBytes(UTF_8);
  private static final String FAMILY = "family";
  private static final String QUALIFIER = "qualifier";
  private static final String QUALIFIER2 = "qualifier2";
  private static final String QUALIFIER3 = "qualifier3";

  private Condition c1, c2;
  private ConditionalMutation cm;

  @Before
  public void setUp() throws Exception {
    c1 = new Condition(FAMILY, QUALIFIER);
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: c1 = new Condition(FAMILY, QUALIFIER);
    c2 = new Condition(FAMILY, QUALIFIER2);
    assertFalse(c1.equals(c2));
    cm = new ConditionalMutation(ROW, c1, c2);
  }

  @Test
  public void testConstruction_ByteArray() {
    c1 = new Condition(FAMILY, QUALIFIER);
    assertArrayEquals(ROW, cm.getRow(), () -> "Add assertion message");
    List<Condition> cs = cm.getConditions();
    Integer magicNumber95838096 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber95838096, cs.size(), () -> "Add assertion message");
    assertEquals(c1, cs.get(0), () -> "Add assertion message");
    assertEquals(c2, cs.get(1), () -> "Add assertion message");
  }

  @Test
  public void testConstruction_ByteArray_StartAndLength() {
    c1 = new Condition(FAMILY, QUALIFIER);
    cm = new ConditionalMutation(ROW, 1, 1, c1, c2);
    assertArrayEquals("o".getBytes(UTF_8), cm.getRow(), () -> "Add assertion message");
    List<Condition> cs = cm.getConditions();
    Integer magicNumber61457517 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber61457517, cs.size(), () -> "Add assertion message");
    assertEquals(c1, cs.get(0), () -> "Add assertion message");
    assertEquals(c2, cs.get(1), () -> "Add assertion message");
  }

  @Test
  public void testConstruction_Text() {
    c1 = new Condition(FAMILY, QUALIFIER);
    cm = new ConditionalMutation(new Text(ROW), c1, c2);
    assertArrayEquals(ROW, cm.getRow(), () -> "Add assertion message");
    List<Condition> cs = cm.getConditions();
    Integer magicNumber23796172 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber23796172, cs.size(), () -> "Add assertion message");
    assertEquals(c1, cs.get(0), () -> "Add assertion message");
    assertEquals(c2, cs.get(1), () -> "Add assertion message");
  }

  @Test
  public void testConstruction_CharSequence() {
    c1 = new Condition(FAMILY, QUALIFIER);
    cm = new ConditionalMutation(new String(ROW, UTF_8), c1, c2);
    assertArrayEquals(ROW, cm.getRow(), () -> "Add assertion message");
    List<Condition> cs = cm.getConditions();
    assertEquals(2, cs.size(), () -> "Add assertion message");
    assertEquals(c1, cs.get(0), () -> "Add assertion message");
    assertEquals(c2, cs.get(1), () -> "Add assertion message");
  }

  @Test
  public void testConstruction_ByteSequence() {
    c1 = new Condition(FAMILY, QUALIFIER);
    cm = new ConditionalMutation(new ArrayByteSequence(ROW), c1, c2);
    assertArrayEquals(ROW, cm.getRow(), () -> "Add assertion message");
    List<Condition> cs = cm.getConditions();
    assertEquals(2, cs.size(), () -> "Add assertion message");
    assertEquals(c1, cs.get(0), () -> "Add assertion message");
    assertEquals(c2, cs.get(1), () -> "Add assertion message");
  }

  @Test
  public void testCopyConstructor() {
    ConditionalMutation cm2 = new ConditionalMutation(cm);
    assertArrayEquals(cm.getRow(), cm2.getRow(), () -> "Add assertion message");
    assertEquals(cm.getConditions(), cm2.getConditions(), () -> "Add assertion message");
  }

  @Test
  public void testAddCondition() {
    c1 = new Condition(FAMILY, QUALIFIER);
    Condition c3 = new Condition(FAMILY, QUALIFIER3);
    cm.addCondition(c3);
    List<Condition> cs = cm.getConditions();
    Integer magicNumber38974260 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber38974260, cs.size(), () -> "Add assertion message");
    assertEquals(c1, cs.get(0), () -> "Add assertion message");
    assertEquals(c2, cs.get(1), () -> "Add assertion message");
    assertEquals(c3, cs.get(2), () -> "Add assertion message");
  }

  @Test
  public void testEquals() {
    c1 = new Condition(FAMILY, QUALIFIER);
    // reflexivity
    assertTrue(cm.equals(cm), () -> "Add assertion message");

    // non-nullity
    assertFalse(cm.equals((Object) null), () -> "Add assertion message");

    // symmetry
    ConditionalMutation cm2 = new ConditionalMutation(ROW, c1, c2);
    assertTrue(cm.equals(cm2), () -> "Add assertion message");
    assertTrue(cm2.equals(cm), () -> "Add assertion message");

    ConditionalMutation cm3 = new ConditionalMutation("row2".getBytes(UTF_8), c1, c2);
    assertFalse(cm.equals(cm3), () -> "Add assertion message");
    cm3 = new ConditionalMutation(ROW, c2, c1);
    assertFalse(cm.getConditions().equals(cm3.getConditions()), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertFalse(cm.equals(cm3), () -> "Add assertion message");
  }

  @Test
  public void testEquals_Mutation() {
    Mutation m = new Mutation(ROW);
    assertFalse(m.equals(cm), () -> "Add assertion message");
    assertFalse(cm.equals(m), () -> "Add assertion message");
  }

  @Test
  public void testHashcode() {
    c1 = new Condition(FAMILY, QUALIFIER);
    ConditionalMutation cm2 = new ConditionalMutation(ROW, c1, c2);
    assertTrue(cm.equals(cm2), () -> "Add assertion message");
    assertEquals(cm2.hashCode(), cm.hashCode(), () -> "Add assertion message");
  }
}