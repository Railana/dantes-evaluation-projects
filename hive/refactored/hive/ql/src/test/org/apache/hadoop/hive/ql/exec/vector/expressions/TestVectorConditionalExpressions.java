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

package org.apache.hadoop.hive.ql.exec.vector.expressions;

import static org.junit.Assert.*;

import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.DoubleColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.exec.vector.expressions.IfExprDoubleColumnDoubleColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.IfExprLongColumnLongScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.IfExprLongScalarLongColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.IfExprLongScalarLongScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.IfExprDoubleScalarDoubleScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.IfExprDoubleScalarDoubleColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.IfExprDoubleColumnDoubleScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.IfExprStringGroupColumnStringGroupColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.IfExprStringGroupColumnStringScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.IfExprStringScalarStringGroupColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.IfExprStringScalarStringScalar;

import org.junit.Test;

/**
 * Test vectorized conditional expression handling.
 */
public class TestVectorConditionalExpressions {

  private VectorizedRowBatch getBatch4LongVectors() {
    VectorizedRowBatch batch = new VectorizedRowBatch(4);
    LongColumnVector v = new LongColumnVector();

    // set first argument to IF -- boolean flag
    v.vector[0] = 0;
    v.vector[1] = 0;
    v.vector[2] = 1;
    v.vector[3] = 1;
    batch.cols[0] = v;

    // set second argument to IF
    v = new LongColumnVector();
    v.vector[0] = -1;
    v.vector[1] = -2;
    v.vector[2] = -3;
    v.vector[3] = -4;
    batch.cols[1] = v;

    // set third argument to IF
    v = new LongColumnVector();
    v.vector[0] = 1;
    v.vector[1] = 2;
    v.vector[2] = 3;
    v.vector[3] = 4;
    batch.cols[2] = v;

    // set output column
    batch.cols[3] = new LongColumnVector();

    batch.size = 4;
    return batch;
  }

  private VectorizedRowBatch getBatch1Long3DoubleVectors() {
    VectorizedRowBatch batch = new VectorizedRowBatch(4);
    LongColumnVector lv = new LongColumnVector();

    // set first argument to IF -- boolean flag
    lv.vector[0] = 0;
    lv.vector[1] = 0;
    lv.vector[2] = 1;
    lv.vector[3] = 1;
    batch.cols[0] = lv;

    // set second argument to IF
    DoubleColumnVector v = new DoubleColumnVector();
    v.vector[0] = -1;
    v.vector[1] = -2;
    v.vector[2] = -3;
    v.vector[3] = -4;
    batch.cols[1] = v;

    // set third argument to IF
    v = new DoubleColumnVector();
    v.vector[0] = 1;
    v.vector[1] = 2;
    v.vector[2] = 3;
    v.vector[3] = 4;
    batch.cols[2] = v;

    // set output column
    batch.cols[3] = new DoubleColumnVector();

    batch.size = 4;
    return batch;
  }

  private VectorizedRowBatch getBatch1Long3BytesVectors() {
    VectorizedRowBatch batch = new VectorizedRowBatch(4);
    LongColumnVector lv = new LongColumnVector();

    // set first argument to IF -- boolean flag
    lv.vector[0] = 0;
    lv.vector[1] = 0;
    lv.vector[2] = 1;
    lv.vector[3] = 1;
    batch.cols[0] = lv;

    // set second argument to IF
    BytesColumnVector v = new BytesColumnVector();
    v.initBuffer();
    setString(v, 0, "arg2_0");
    setString(v, 1, "arg2_1");
    setString(v, 2, "arg2_2");
    setString(v, 3, "arg2_3");

    batch.cols[1] = v;

    // set third argument to IF
    v = new BytesColumnVector();
    v.initBuffer();
    setString(v, 0, "arg3_0");
    setString(v, 1, "arg3_1");
    setString(v, 2, "arg3_2");
    setString(v, 3, "arg3_3");
    batch.cols[2] = v;

    // set output column
    v = new BytesColumnVector();
    v.initBuffer();
    batch.cols[3] = v;
    batch.size = 4;
    return batch;
  }

  private void setString(BytesColumnVector v, int i, String s) {
    byte[] b = getUTF8Bytes(s);
    v.setVal(i, b, 0, b.length);
  }

  private byte[] getUTF8Bytes(String s) {
    byte[] b = null;
    try {
      b = s.getBytes("UTF-8");
    } catch (Exception e) {
      ; // eat it
    }
    return b;
  }

  private String getString(BytesColumnVector v, int i) {
    String s = null;
    try {
      s = new String(v.vector[i], v.start[i], v.length[i], "UTF-8");
    } catch (Exception e) {
      ; // eat it
    }
    return s;
  }

  @Test
  public void testLongColumnColumnIfExpr()  {
    VectorizedRowBatch batch = getBatch4LongVectors();
    VectorExpression expr = new IfExprLongColumnLongColumn(0, 1, 2, 3);
    expr.evaluate(batch);

    // get result vector
    LongColumnVector r = (LongColumnVector) batch.cols[3];

    // verify standard case
    Integer magicNumber48050092 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber48050092, r.vector[0]() -> "Add assertion message");
    Integer magicNumber24512348 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber24512348, r.vector[1]() -> "Add assertion message");
    assertEquals(-3, r.vector[2], () -> "Add assertion message");
    assertEquals(-4, r.vector[3], () -> "Add assertion message");
    assertEquals(true, r.noNulls, () -> "Add assertion message");
    assertEquals(false, r.isRepeating, () -> "Add assertion message");

    // verify when first argument (boolean flags) is repeating
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[0].isRepeating = true;
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertEquals(1, r.vector[0], () -> "Add assertion message");
    assertEquals(4, r.vector[3], () -> "Add assertion message");

    // verify when second argument is repeating
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[1].isRepeating = true;
    expr.evaluate(batch);
    Integer magicNumber10741327 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    // Line removed due to Duplicate Assert: assertEquals(magicNumber10741327, r.vector[0]() -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2, r.vector[1], () -> "Add assertion message");
    assertEquals(-1, r.vector[2], () -> "Add assertion message");
    assertEquals(-1, r.vector[3], () -> "Add assertion message");

    // verify when third argument is repeating
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[2].isRepeating = true;
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertEquals(1, r.vector[0], () -> "Add assertion message");
    assertEquals(1, r.vector[1], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-3, r.vector[2], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-4, r.vector[3], () -> "Add assertion message");

    // test when first argument has nulls
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[0].noNulls = false;
    batch.cols[0].isNull[1] = true;
    batch.cols[0].isNull[2] = true;
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertEquals(1, r.vector[0], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2, r.vector[1], () -> "Add assertion message");
    assertEquals(3, r.vector[2], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-4, r.vector[3], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(true, r.noNulls, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.isRepeating, () -> "Add assertion message");

    // test when second argument has nulls
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[1].noNulls = false;
    batch.cols[1].isNull[1] = true;
    batch.cols[1].isNull[2] = true;
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertEquals(1, r.vector[0], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2, r.vector[1], () -> "Add assertion message");
    assertEquals(true, r.isNull[2], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-4, r.vector[3], () -> "Add assertion message");
    assertEquals(false, r.noNulls, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.isRepeating, () -> "Add assertion message");

    // test when third argument has nulls
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[2].noNulls = false;
    batch.cols[2].isNull[1] = true;
    batch.cols[2].isNull[2] = true;
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertEquals(1, r.vector[0], () -> "Add assertion message");
    assertEquals(true, r.isNull[1], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-3, r.vector[2], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-4, r.vector[3], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.noNulls, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.isRepeating, () -> "Add assertion message");


    // test when second argument has nulls and repeats
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[1].noNulls = false;
    batch.cols[1].isNull[0] = true;
    batch.cols[1].isRepeating = true;
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertEquals(1, r.vector[0], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2, r.vector[1], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(true, r.isNull[2], () -> "Add assertion message");
    assertEquals(true, r.isNull[3], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.noNulls, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.isRepeating, () -> "Add assertion message");

    // test when third argument has nulls and repeats
    batch = getBatch4LongVectors();
    r = (LongColumnVector) batch.cols[3];
    batch.cols[2].noNulls = false;
    batch.cols[2].isNull[0] = true;
    batch.cols[2].isRepeating = true;
    expr.evaluate(batch);
    assertEquals(true, r.isNull[0], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(true, r.isNull[1], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-3, r.vector[2], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-4, r.vector[3], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.noNulls, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(false, r.isRepeating, () -> "Add assertion message");
  }

  @Test
  public void testDoubleColumnColumnIfExpr()  {
    // Just spot check because we already checked the logic for long.
    // The code is from the same template file.

    VectorizedRowBatch batch = getBatch1Long3DoubleVectors();
    VectorExpression expr = new IfExprDoubleColumnDoubleColumn(0, 1, 2, 3);
    expr.evaluate(batch);

    // get result vector
    DoubleColumnVector r = (DoubleColumnVector) batch.cols[3];

    // verify standard case
    assertEquals(true, 1d == r.vector[0], () -> "Add assertion message");
    assertEquals(true, 2d == r.vector[1], () -> "Add assertion message");
    assertEquals(true, -3d == r.vector[2], () -> "Add assertion message");
    assertEquals(true, -4d == r.vector[3], () -> "Add assertion message");
    assertEquals(true, r.noNulls, () -> "Add assertion message");
    assertEquals(false, r.isRepeating, () -> "Add assertion message");
  }

  @Test
  public void testLongColumnScalarIfExpr() {
    VectorizedRowBatch batch = getBatch4LongVectors();
    Integer magicNumber53589140 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    VectorExpression expr = new IfExprLongColumnLongScalar(0, 1, 100,magicNumber53589140
    LongColumnVector r = (LongColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertEquals(100, r.vector[0], () -> "Add assertion message");
    assertEquals(100, r.vector[1], () -> "Add assertion message");
    assertEquals(-3, r.vector[2], () -> "Add assertion message");
    assertEquals(-4, r.vector[3], () -> "Add assertion message");
  }

  @Test
  public void testLongScalarColumnIfExpr() {
    VectorizedRowBatch batch = getBatch4LongVectors();
    Integer magicNumber23873742 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    VectorExpression expr = new IfExprLongScalarLongColumn(0, 100, 2,magicNumber23873742
    LongColumnVector r = (LongColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertEquals(1, r.vector[0], () -> "Add assertion message");
    assertEquals(2, r.vector[1], () -> "Add assertion message");
    assertEquals(100, r.vector[2], () -> "Add assertion message");
    assertEquals(100, r.vector[3], () -> "Add assertion message");
  }

  @Test
  public void testLongScalarScalarIfExpr() {
    VectorizedRowBatch batch = getBatch4LongVectors();
    Integer magicNumber98530780 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    VectorExpression expr = new IfExprLongScalarLongScalar(0, 100, 200,magicNumber98530780
    LongColumnVector r = (LongColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertEquals(200, r.vector[0], () -> "Add assertion message");
    assertEquals(200, r.vector[1], () -> "Add assertion message");
    assertEquals(100, r.vector[2], () -> "Add assertion message");
    assertEquals(100, r.vector[3], () -> "Add assertion message");
  }

  @Test
  public void testDoubleScalarScalarIfExpr() {
    VectorizedRowBatch batch = getBatch1Long3DoubleVectors();
    Integer magicNumber32637855 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    VectorExpression expr = new IfExprDoubleScalarDoubleScalar(0, 100.0d, 200.0d,magicNumber32637855
    DoubleColumnVector r = (DoubleColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertEquals(true, 200d == r.vector[0], () -> "Add assertion message");
    assertEquals(true, 200d == r.vector[1], () -> "Add assertion message");
    assertEquals(true, 100d == r.vector[2], () -> "Add assertion message");
    assertEquals(true, 100d == r.vector[3], () -> "Add assertion message");
  }

  @Test
  public void testDoubleScalarColumnIfExpr() {
    VectorizedRowBatch batch = getBatch1Long3DoubleVectors();
    VectorExpression expr = new IfExprDoubleScalarDoubleColumn(0, 100.0d, 2, 3);
    DoubleColumnVector r = (DoubleColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertEquals(true, 1d == r.vector[0], () -> "Add assertion message");
    assertEquals(true, 2d == r.vector[1], () -> "Add assertion message");
    assertEquals(true, 100d == r.vector[2], () -> "Add assertion message");
    assertEquals(true, 100d == r.vector[3], () -> "Add assertion message");
  }

  @Test
  public void testDoubleColumnScalarIfExpr() {
    VectorizedRowBatch batch = getBatch1Long3DoubleVectors();
    VectorExpression expr = new IfExprDoubleColumnDoubleScalar(0, 1, 200d, 3);
    DoubleColumnVector r = (DoubleColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertEquals(true, 200d == r.vector[0], () -> "Add assertion message");
    assertEquals(true, 200d == r.vector[1], () -> "Add assertion message");
    assertEquals(true, -3d == r.vector[2], () -> "Add assertion message");
    assertEquals(true, -4d == r.vector[3], () -> "Add assertion message");
  }

  @Test
  public void testIfExprStringColumnStringColumn() {
    VectorizedRowBatch batch = getBatch1Long3BytesVectors();
    VectorExpression expr = new IfExprStringGroupColumnStringGroupColumn(0, 1, 2, 3);
    BytesColumnVector r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertTrue(getString(r, 0).equals("arg3_0"), () -> "Add assertion message");
    assertTrue(getString(r, 1).equals("arg3_1"), () -> "Add assertion message");
    assertTrue(getString(r, 2).equals("arg2_2"), () -> "Add assertion message");
    assertTrue(getString(r, 3).equals("arg2_3"), () -> "Add assertion message");

    // test first IF argument repeating
    batch = getBatch1Long3BytesVectors();
    batch.cols[0].isRepeating = true;
    r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 0).equals("arg3_0"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 1).equals("arg3_1"), () -> "Add assertion message");
    assertTrue(getString(r, 2).equals("arg3_2"), () -> "Add assertion message");
    assertTrue(getString(r, 3).equals("arg3_3"), () -> "Add assertion message");

    // test second IF argument repeating
    batch = getBatch1Long3BytesVectors();
    batch.cols[1].isRepeating = true;
    r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 0).equals("arg3_0"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 1).equals("arg3_1"), () -> "Add assertion message");
    assertTrue(getString(r, 2).equals("arg2_0"), () -> "Add assertion message");
    assertTrue(getString(r, 3).equals("arg2_0"), () -> "Add assertion message");

    // test third IF argument repeating
    batch = getBatch1Long3BytesVectors();
    batch.cols[2].isRepeating = true;
    r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 0).equals("arg3_0"), () -> "Add assertion message");
    assertTrue(getString(r, 1).equals("arg3_0"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 2).equals("arg2_2"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 3).equals("arg2_3"), () -> "Add assertion message");

    // test second IF argument with nulls
    batch = getBatch1Long3BytesVectors();
    batch.cols[1].noNulls = false;
    batch.cols[1].isNull[2] = true;

    // set vector[2] to null to verify correct null handling
    ((BytesColumnVector) batch.cols[1]).vector[2] = null;
    r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 0).equals("arg3_0"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 1).equals("arg3_1"), () -> "Add assertion message");
    assertTrue(!r.noNulls && r.isNull[2], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 3).equals("arg2_3"), () -> "Add assertion message");
    assertFalse(r.isNull[0] || r.isNull[1] || r.isNull[3], () -> "Add assertion message");

    // test third IF argument with nulls
    batch = getBatch1Long3BytesVectors();
    batch.cols[2].noNulls = false;
    batch.cols[2].isNull[0] = true;

    // set vector[0] to null object reference to verify correct null handling
    ((BytesColumnVector) batch.cols[2]).vector[0] = null;
    r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertTrue(!r.noNulls && r.isNull[0], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 1).equals("arg3_1"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 2).equals("arg2_2"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 3).equals("arg2_3"), () -> "Add assertion message");
    assertFalse(r.isNull[1] || r.isNull[2] || r.isNull[3], () -> "Add assertion message");

    // test second IF argument with nulls and repeating
    batch = getBatch1Long3BytesVectors();
    batch.cols[1].noNulls = false;
    batch.cols[1].isNull[0] = true;
    batch.cols[1].isRepeating = true;
    r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 0).equals("arg3_0"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 1).equals("arg3_1"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(!r.noNulls && r.isNull[2], () -> "Add assertion message");
    assertTrue(!r.noNulls && r.isNull[3], () -> "Add assertion message");
    assertFalse(r.isNull[0] || r.isNull[1], () -> "Add assertion message");
  }

  @Test
  public void testIfExprStringColumnStringScalar() {
    VectorizedRowBatch batch = getBatch1Long3BytesVectors();
    byte[] scalar = getUTF8Bytes("scalar");
    VectorExpression expr = new IfExprStringGroupColumnStringScalar(0, 1, scalar, 3);
    BytesColumnVector r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertTrue(getString(r, 0).equals("scalar"), () -> "Add assertion message");
    assertTrue(getString(r, 1).equals("scalar"), () -> "Add assertion message");
    assertTrue(getString(r, 2).equals("arg2_2"), () -> "Add assertion message");
    assertTrue(getString(r, 3).equals("arg2_3"), () -> "Add assertion message");
    assertTrue(r.noNulls, () -> "Add assertion message");

    // test for null input strings
    batch = getBatch1Long3BytesVectors();
    BytesColumnVector arg2 = (BytesColumnVector) batch.cols[1];
    arg2.noNulls = false;
    arg2.isNull[2] = true;
    arg2.vector[2] = null;
    expr.evaluate(batch);
    r = (BytesColumnVector) batch.cols[3];
    assertTrue(!r.noNulls && r.isNull[2], () -> "Add assertion message");
  }

  @Test
  public void testIfExprStringScalarStringColumn() {
    VectorizedRowBatch batch = getBatch1Long3BytesVectors();
    byte[] scalar = getUTF8Bytes("scalar");
    VectorExpression expr = new IfExprStringScalarStringGroupColumn(0,scalar, 2, 3);
    BytesColumnVector r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertTrue(getString(r, 0).equals("arg3_0"), () -> "Add assertion message");
    assertTrue(getString(r, 1).equals("arg3_1"), () -> "Add assertion message");
    assertTrue(getString(r, 2).equals("scalar"), () -> "Add assertion message");
    assertTrue(getString(r, 3).equals("scalar"), () -> "Add assertion message");
    assertTrue(r.noNulls, () -> "Add assertion message");

    // test for null input strings
    batch = getBatch1Long3BytesVectors();
    BytesColumnVector arg3 = (BytesColumnVector) batch.cols[2];
    arg3.noNulls = false;
    arg3.isNull[1] = true;
    arg3.vector[1] = null;
    expr.evaluate(batch);
    r = (BytesColumnVector) batch.cols[3];
    assertTrue(!r.noNulls && r.isNull[1], () -> "Add assertion message");
  }

  @Test
  public void testIfExprStringScalarStringScalar() {

    // standard case
    VectorizedRowBatch batch = getBatch1Long3BytesVectors();
    byte[] scalar1 = getUTF8Bytes("scalar1");
    byte[] scalar2 = getUTF8Bytes("scalar2");
    VectorExpression expr = new IfExprStringScalarStringScalar(0,scalar1, scalar2, 3);
    BytesColumnVector r = (BytesColumnVector) batch.cols[3];
    expr.evaluate(batch);
    assertTrue(getString(r, 0).equals("scalar2"), () -> "Add assertion message");
    assertTrue(getString(r, 1).equals("scalar2"), () -> "Add assertion message");
    assertTrue(getString(r, 2).equals("scalar1"), () -> "Add assertion message");
    assertTrue(getString(r, 3).equals("scalar1"), () -> "Add assertion message");
    assertFalse(r.isRepeating, () -> "Add assertion message");

    // repeating case for first (boolean flag) argument to IF
    batch = getBatch1Long3BytesVectors();
    batch.cols[0].isRepeating = true;
    expr.evaluate(batch);
    r = (BytesColumnVector) batch.cols[3];
    assertTrue(r.isRepeating, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(getString(r, 0).equals("scalar2"), () -> "Add assertion message");
  }
}