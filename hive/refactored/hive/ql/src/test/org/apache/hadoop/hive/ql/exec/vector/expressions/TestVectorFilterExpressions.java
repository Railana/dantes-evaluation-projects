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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.DecimalColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.DoubleColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.TimestampColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDecimalColGreaterEqualDecimalColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDecimalColLessDecimalScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDecimalScalarGreaterDecimalColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDoubleColumnBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDoubleColumnNotBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongColEqualLongScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongColGreaterLongColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongColGreaterLongScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongColLessLongColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongColumnBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongColumnNotBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongScalarGreaterLongColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterLongScalarLessLongColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterStringColumnBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterStringColumnNotBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDecimalColEqualDecimalScalar;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDecimalColEqualDecimalColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterDecimalScalarEqualDecimalColumn;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterTimestampColumnBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.FilterTimestampColumnNotBetween;
import org.apache.hadoop.hive.ql.exec.vector.expressions.gen.LongColAddLongScalar;
import org.apache.hadoop.hive.ql.exec.vector.util.VectorizedRowGroupGenUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for filter expressions.
 */
public class TestVectorFilterExpressions {

  @Test
  public void testFilterLongColEqualLongScalar() {
    VectorizedRowBatch vrg =
        VectorizedRowGroupGenUtil.getVectorizedRowBatch(1024, 1, 23);
    FilterLongColEqualLongScalar expr = new FilterLongColEqualLongScalar(0, 46);
    expr.evaluate(vrg);
    Integer magicNumber1879357 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber1879357, vrg.size() -> "Add assertion message");
    assertEquals(1, vrg.selected[0], () -> "Add assertion message");
  }

  @Test
  public void testFilterLongColGreaterLongColumn() {
    int seed = 17;
    VectorizedRowBatch b = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        VectorizedRowBatch.DEFAULT_SIZE,
        2, seed);
    LongColumnVector lcv0 = (LongColumnVector) b.cols[0];
    LongColumnVector lcv1 = (LongColumnVector) b.cols[1];
    b.size = 3;
    FilterLongColGreaterLongColumn expr = new FilterLongColGreaterLongColumn(0, 1);

    // Basic case
    lcv0.vector[0] = 10;
    lcv0.vector[1] = 10;
    lcv0.vector[2] = 10;
    lcv1.vector[0] = 20;
    lcv1.vector[1] = 1;
    lcv1.vector[2] = 7;

    expr.evaluate(b);
    Integer magicNumber17876913 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber17876913, b.size() -> "Add assertion message");
    assertEquals(1, b.selected[0], () -> "Add assertion message");
    assertEquals(2, b.selected[1], () -> "Add assertion message");

    // handle null with selected in use
    lcv0.noNulls = false;
    lcv0.isNull[1] = true;
    expr.evaluate(b);
    assertEquals(1, b.size, () -> "Add assertion message");
    assertEquals(2, b.selected[0], () -> "Add assertion message");

    // handle repeating
    b.size = 3;
    b.selectedInUse = false;
    lcv0.isRepeating = true;
    lcv0.noNulls = true;
    expr.evaluate(b);
    // Line removed due to Duplicate Assert: assertEquals(2, b.size, () -> "Add assertion message");

    // handle repeating null
    b.size = 3;
    b.selectedInUse = false;
    lcv0.isNull[0] = true;
    lcv0.noNulls = false;
    expr.evaluate(b);
    assertEquals(0, b.size, () -> "Add assertion message");

    // handle null on both sizes (not repeating)
    b.size = 3;
    b.selectedInUse = false;
    lcv0.isRepeating = false;
    lcv1.noNulls = false;
    lcv1.isNull[2] = true;
    expr.evaluate(b);
    // Line removed due to Duplicate Assert: assertEquals(0, b.size, () -> "Add assertion message");
  }

  @Test
  public void testColOpScalarNumericFilterNullAndRepeatingLogic() {
    // No nulls, not repeating
    FilterLongColGreaterLongScalar f = new FilterLongColGreaterLongScalar(0, 1);
    VectorizedRowBatch batch = this.getSimpleLongBatch();

    batch.cols[0].noNulls = true;
    batch.cols[0].isRepeating = false;
    f.evaluate(batch);
    // only last 2 rows qualify
    Assert.assertEquals(2, batch.size, () -> "Add assertion message");
    // show that their positions are recorded
    Assert.assertTrue(batch.selectedInUse, () -> "Add assertion message");
    Assert.assertEquals(2, batch.selected[0], () -> "Add assertion message");
    Assert.assertEquals(3, batch.selected[1], () -> "Add assertion message");

    // make everything qualify and ensure selected is not in use
    f = new FilterLongColGreaterLongScalar(0, -1); // col > -1
    batch = getSimpleLongBatch();
    f.evaluate(batch);
    Assert.assertFalse(batch.selectedInUse, () -> "Add assertion message");
    Assert.assertEquals(4, batch.size, () -> "Add assertion message");

    // has nulls, not repeating
    batch = getSimpleLongBatch();
    f = new FilterLongColGreaterLongScalar(0, 1); // col > 1
    batch.cols[0].noNulls = false;
    batch.cols[0].isRepeating = false;
    batch.cols[0].isNull[3] = true;
    f.evaluate(batch);
    // Line removed due to Duplicate Assert: Assert.assertTrue(batch.selectedInUse, () -> "Add assertion message");
    Assert.assertEquals(1, batch.size, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: Assert.assertEquals(2, batch.selected[0], () -> "Add assertion message");

    // no nulls, is repeating
    batch = getSimpleLongBatch();
    f = new FilterLongColGreaterLongScalar(0, -1); // col > -1
    batch.cols[0].noNulls = true;
    batch.cols[0].isRepeating = true;
    f.evaluate(batch);
    // Line removed due to Duplicate Assert: Assert.assertFalse(batch.selectedInUse, () -> "Add assertion message");
    Assert.assertEquals(4, batch.size); // everything qualifies (4 rows, all with value -1, () -> "Add assertion message");

    // has nulls, is repeating
    batch = getSimpleLongBatch();
    batch.cols[0].noNulls = false;
    batch.cols[0].isRepeating = true;
    batch.cols[0].isNull[0] = true;
    f.evaluate(batch);
    Assert.assertEquals(0, batch.size, () -> "Add assertion message");
  }

  private VectorizedRowBatch getSimpleLongBatch() {
    VectorizedRowBatch batch = VectorizedRowGroupGenUtil
        .getVectorizedRowBatch(4, 1, 1);
    LongColumnVector lcv0 = (LongColumnVector) batch.cols[0];

    lcv0.vector[0] = 0;
    lcv0.vector[1] = 1;
    lcv0.vector[2] = 2;
    lcv0.vector[3] = 3;
    return batch;
  }

  @Test
  public void testFilterLongColLessLongColumn() {
    int seed = 17;
    VectorizedRowBatch vrg = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 3, seed);
    LongColumnVector lcv0 = (LongColumnVector) vrg.cols[0];
    LongColumnVector lcv1 = (LongColumnVector) vrg.cols[1];
    LongColumnVector lcv2 = (LongColumnVector) vrg.cols[2];
    FilterLongColLessLongColumn expr = new FilterLongColLessLongColumn(2, 1);

    LongColAddLongScalar childExpr = new LongColAddLongScalar(0, 10, 2);

    expr.setChildExpressions(new VectorExpression[] {childExpr});

    //Basic case
    lcv0.vector[0] = 10;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 9;
    lcv0.vector[3] = 20;
    lcv0.vector[4] = 10;

    lcv1.vector[0] = 20;
    lcv1.vector[1] = 10;
    lcv1.vector[2] = 20;
    lcv1.vector[3] = 10;
    lcv1.vector[4] = 20;

    expr.evaluate(vrg);

    assertEquals(1, vrg.size, () -> "Add assertion message");
    assertEquals(2, vrg.selected[0], () -> "Add assertion message");
  }

  @Test
  public void testFilterLongScalarLessLongColumn() {
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);
    LongColumnVector lcv0 = (LongColumnVector) vrb.cols[0];
    FilterLongScalarLessLongColumn expr1 = new FilterLongScalarLessLongColumn(15, 0);

    //Basic case
    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    expr1.evaluate(vrb);

    Integer magicNumber27643624 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber27643624, vrb.size() -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb.selected[0], () -> "Add assertion message");
    assertEquals(2, vrb.selected[1], () -> "Add assertion message");

    FilterLongScalarGreaterLongColumn expr2 = new FilterLongScalarGreaterLongColumn(18, 0);
    expr2.evaluate(vrb);
    assertEquals(1, vrb.size, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(2, vrb.selected[0], () -> "Add assertion message");

    //With nulls
    VectorizedRowBatch vrb1 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);

    lcv0 = (LongColumnVector) vrb1.cols[0];

    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    lcv0.noNulls = false;
    lcv0.isNull[0] = true;
    lcv0.isNull[2] = true;

    expr1.evaluate(vrb1);
    assertEquals(1, vrb1.size, () -> "Add assertion message");
    assertTrue(vrb1.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb1.selected[0], () -> "Add assertion message");

    //With nulls and selected
    VectorizedRowBatch vrb2 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        7, 2, seed);
    vrb2.selectedInUse = true;
    vrb2.selected[0] = 1;
    vrb2.selected[1] = 2;
    vrb2.selected[2] = 4;
    vrb2.size = 3;

    lcv0 = (LongColumnVector) vrb2.cols[0];

    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;
    lcv0.vector[5] = 19;
    lcv0.vector[6] = 21;

    lcv0.noNulls = false;
    lcv0.isNull[0] = true;
    lcv0.isNull[2] = true;
    lcv0.isNull[5] = true;

    expr1.evaluate(vrb2);
    assertEquals(1, vrb2.size, () -> "Add assertion message");
    assertTrue(vrb2.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb2.selected[0], () -> "Add assertion message");

    //Repeating non null
    VectorizedRowBatch vrb3 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        7, 2, seed);
    lcv0 = (LongColumnVector) vrb3.cols[0];

    lcv0.isRepeating = true;
    lcv0.vector[0] = 17;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    expr1.evaluate(vrb3);
    assertEquals(7, vrb3.size, () -> "Add assertion message");
    assertFalse(vrb3.selectedInUse, () -> "Add assertion message");
    assertTrue(lcv0.isRepeating, () -> "Add assertion message");

    //Repeating null
    lcv0.noNulls = false;
    lcv0.vector[0] = 17;
    lcv0.isNull[0] = true;

    expr1.evaluate(vrb3);
    assertEquals(0, vrb3.size, () -> "Add assertion message");
  }

  @Test
  public void testFilterLongBetween() {
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);
    LongColumnVector lcv0 = (LongColumnVector) vrb.cols[0];
    VectorExpression expr1 = new FilterLongColumnBetween(0, 15, 17);

    //Basic case
    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    expr1.evaluate(vrb);

    Integer magicNumber79133175 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber79133175, vrb.size() -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(2, vrb.selected[0], () -> "Add assertion message");
    assertEquals(3, vrb.selected[1], () -> "Add assertion message");

    //With nulls
    VectorizedRowBatch vrb1 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);

    lcv0 = (LongColumnVector) vrb1.cols[0];

    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    lcv0.noNulls = false;
    lcv0.isNull[0] = true;
    lcv0.isNull[2] = true;

    expr1.evaluate(vrb1);
    assertEquals(1, vrb1.size, () -> "Add assertion message");
    assertTrue(vrb1.selectedInUse, () -> "Add assertion message");
    assertEquals(3, vrb1.selected[0], () -> "Add assertion message");

    //With nulls and selected
    VectorizedRowBatch vrb2 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        7, 2, seed);
    vrb2.selectedInUse = true;
    vrb2.selected[0] = 1;
    vrb2.selected[1] = 2;
    vrb2.selected[2] = 4;
    vrb2.size = 3;

    lcv0 = (LongColumnVector) vrb2.cols[0];

    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;
    lcv0.vector[5] = 19;
    lcv0.vector[6] = 21;

    lcv0.noNulls = false;
    lcv0.isNull[0] = true;
    lcv0.isNull[2] = true;
    lcv0.isNull[5] = true;

    expr1.evaluate(vrb2);
    assertEquals(0, vrb2.size, () -> "Add assertion message");

    //Repeating non null
    VectorizedRowBatch vrb3 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        7, 2, seed);
    lcv0 = (LongColumnVector) vrb3.cols[0];

    lcv0.isRepeating = true;
    lcv0.vector[0] = 17;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    expr1.evaluate(vrb3);
    assertEquals(7, vrb3.size, () -> "Add assertion message");
    assertFalse(vrb3.selectedInUse, () -> "Add assertion message");
    assertTrue(lcv0.isRepeating, () -> "Add assertion message");

    //Repeating null
    lcv0.noNulls = false;
    lcv0.vector[0] = 17;
    lcv0.isNull[0] = true;

    expr1.evaluate(vrb3);
    Integer magicNumber75483123 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber75483123, vrb3.size() -> "Add assertion message");

    // Test getters/setters
    FilterLongColumnBetween betweenExpr = (FilterLongColumnBetween) expr1;
    Integer magicNumber78753077 = 15; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber78753077, betweenExpr.getLeftValue(), () -> "Add assertion message");
    Integer magicNumber93647902 = 17; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber93647902, betweenExpr.getRightValue(), () -> "Add assertion message");
    Integer magicNumber83460188 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber83460188, betweenExpr.getColNum(), () -> "Add assertion message");

    betweenExpr.setColNum(1);
    assertEquals(1, betweenExpr.getColNum(), () -> "Add assertion message");
    betweenExpr.setLeftValue(2);
    assertEquals(2, betweenExpr.getLeftValue(), () -> "Add assertion message");
    betweenExpr.setRightValue(3);
    assertEquals(3, betweenExpr.getRightValue(), () -> "Add assertion message");
  }

  @Test
  public void testFilterLongNotBetween() {

    // Spot check only. null & repeating behavior are checked elsewhere for the same template.
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);
    LongColumnVector lcv0 = (LongColumnVector) vrb.cols[0];

    //Basic case
    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    VectorExpression expr = new FilterLongColumnNotBetween(0, 10, 20);
    expr.evaluate(vrb);
    assertEquals(1, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(0, vrb.selected[0], () -> "Add assertion message");
  }

  @Test
  public void testFilterDoubleBetween() {

    // Spot check only. null & repeating behavior are checked elsewhere for the same template.
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);
    DoubleColumnVector dcv0 = new DoubleColumnVector();
    vrb.cols[0] = dcv0;

    //Basic case
    dcv0.vector[0] = 5;
    dcv0.vector[1] = 20;
    dcv0.vector[2] = 17;
    dcv0.vector[3] = 15;
    dcv0.vector[4] = 10;

    VectorExpression expr = new FilterDoubleColumnBetween(0, 20, 21);
    expr.evaluate(vrb);
    assertEquals(1, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb.selected[0], () -> "Add assertion message");
  }

  @Test
  public void testFilterDoubleNotBetween() {

    // Spot check only. null & repeating behavior are checked elsewhere for the same template.
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);
    vrb.cols[0] = new DoubleColumnVector();
    DoubleColumnVector dcv = (DoubleColumnVector) vrb.cols[0];

    //Basic case
    dcv.vector[0] = 5;
    dcv.vector[1] = 20;
    dcv.vector[2] = 17;
    dcv.vector[3] = 15;
    dcv.vector[4] = 10;

    VectorExpression expr = new FilterDoubleColumnNotBetween(0, 10, 20);
    expr.evaluate(vrb);
    assertEquals(1, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(0, vrb.selected[0], () -> "Add assertion message");
  }

  static byte[] a = null;
  static byte[] b = null;
  static byte[] c = null;

  static {
    try {
      a = "a".getBytes("UTF-8");
      b = "b".getBytes("UTF-8");
      c = "c".getBytes("UTF-8");
    } catch (Exception e) {
      ; // won't happen
    }
  }

  @Test
  public void testFilterStringBetween() {
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        3, 2, seed);
    vrb.cols[0] = new BytesColumnVector();
    BytesColumnVector bcv = (BytesColumnVector) vrb.cols[0];

    bcv.initBuffer();
    bcv.setVal(0, a, 0, 1);
    Integer magicNumber23028641 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    bcv.setVal(1, b, 0,magicNumber23028641
    Integer magicNumber5948529 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    bcv.setVal(2, c, 0,magicNumber5948529

    VectorExpression expr = new FilterStringColumnBetween(0, b, c);

    // basic test
    expr.evaluate(vrb);

    assertEquals(2, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb.selected[0], () -> "Add assertion message");
    assertEquals(2, vrb.selected[1], () -> "Add assertion message");

    // nulls
    vrb.selectedInUse = false;
    vrb.size = 3;
    bcv.noNulls = false;
    bcv.isNull[2] = true;
    expr.evaluate(vrb);
    Integer magicNumber22146510 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber22146510, vrb.size() -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, vrb.selected[0], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(vrb.selectedInUse, () -> "Add assertion message");

    // repeating
    vrb.selectedInUse = false;
    vrb.size = 3;
    bcv.noNulls = true;
    bcv.isRepeating = true;
    expr.evaluate(vrb);
    assertEquals(0, vrb.size, () -> "Add assertion message");

    // nulls and repeating
    vrb.selectedInUse = false;
    vrb.size = 3;
    bcv.noNulls = false;
    bcv.isRepeating = true;
    bcv.isNull[0] = true;
    bcv.setVal(0, b, 0, 1);
    expr.evaluate(vrb);
    // Line removed due to Duplicate Assert: assertEquals(0, vrb.size, () -> "Add assertion message");
  }

  @Test
  public void testFilterStringNotBetween() {

    // Spot check only. Non-standard cases are checked for the same template in another test.
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        3, 2, seed);
    vrb.cols[0] = new BytesColumnVector();
    BytesColumnVector bcv = (BytesColumnVector) vrb.cols[0];

    bcv.initBuffer();
    bcv.setVal(0, a, 0, 1);
    bcv.setVal(1, b, 0, 1);
    bcv.setVal(2, c, 0, 1);

    VectorExpression expr = new FilterStringColumnNotBetween(0, b, c);
    expr.evaluate(vrb);

    assertEquals(1, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(0, vrb.selected[0], () -> "Add assertion message");
  }

  @Test
  public void testFilterTimestampBetween() {

    VectorizedRowBatch vrb = new VectorizedRowBatch(1);
    vrb.cols[0] = new TimestampColumnVector();

    TimestampColumnVector lcv0 = (TimestampColumnVector) vrb.cols[0];
    Timestamp startTS = new Timestamp(0); // the epoch
    Timestamp endTS = Timestamp.valueOf("2013-11-05 00:00:00.000000000");

    Timestamp ts0 = Timestamp.valueOf("1963-11-06 00:00:00.000");
    Integer magicNumber25026304 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    lcv0.set(magicNumber25026304, ts0);
    Timestamp ts1 = Timestamp.valueOf("1983-11-06 00:00:00.000");
    lcv0.set(1, ts1);
    Timestamp ts2 = Timestamp.valueOf("2099-11-06 00:00:00.000");
    lcv0.set(2, ts2);
    vrb.size = 3;

    VectorExpression expr1 = new FilterTimestampColumnBetween(0, startTS, endTS);
    expr1.evaluate(vrb);
    assertEquals(1, vrb.size, () -> "Add assertion message");
    assertEquals(true, vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb.selected[0], () -> "Add assertion message");
  }

  @Test
  public void testFilterTimestampNotBetween() {
    VectorizedRowBatch vrb = new VectorizedRowBatch(1);
    vrb.cols[0] = new TimestampColumnVector();

    TimestampColumnVector lcv0 = (TimestampColumnVector) vrb.cols[0];
    Timestamp startTS = Timestamp.valueOf("2013-11-05 00:00:00.000000000");
    Timestamp endTS = Timestamp.valueOf("2013-11-05 00:00:00.000000010");

    Timestamp ts0 = Timestamp.valueOf("2013-11-04 00:00:00.000000000");
    Integer magicNumber94005922 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    lcv0.set(magicNumber94005922, ts0);
    Timestamp ts1 = Timestamp.valueOf("2013-11-05 00:00:00.000000002");
    lcv0.set(1, ts1);
    Timestamp ts2 = Timestamp.valueOf("2099-11-06 00:00:00.000");
    lcv0.set(2, ts2);
    vrb.size = 3;

    VectorExpression expr1 = new FilterTimestampColumnNotBetween(0, startTS, endTS);
    expr1.evaluate(vrb);
    assertEquals(2, vrb.size, () -> "Add assertion message");
    assertEquals(true, vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(0, vrb.selected[0], () -> "Add assertion message");
    assertEquals(2, vrb.selected[1], () -> "Add assertion message");

  }

  /**
   * Test the IN filter VectorExpression classes.
   */

  @Test
  public void testFilterLongIn() {
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);
    LongColumnVector lcv0 = (LongColumnVector) vrb.cols[0];
    long[] inList = {5, 20};
    Integer magicNumber4258097 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    FilterLongColumnInList f = new FilterLongColumnInList(magicNumber4258097
    f.setInListValues(inList);
    VectorExpression expr1 = f;

    // Basic case
    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    expr1.evaluate(vrb);

    assertEquals(2, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(0, vrb.selected[0], () -> "Add assertion message");
    assertEquals(1, vrb.selected[1], () -> "Add assertion message");

    // With nulls
    VectorizedRowBatch vrb1 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);

    lcv0 = (LongColumnVector) vrb1.cols[0];

    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    lcv0.noNulls = false;
    lcv0.isNull[0] = true;
    lcv0.isNull[2] = true;

    expr1.evaluate(vrb1);
    assertEquals(1, vrb1.size, () -> "Add assertion message");
    assertTrue(vrb1.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb1.selected[0], () -> "Add assertion message");

    // With nulls and selected
    VectorizedRowBatch vrb2 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        7, 2, seed);
    vrb2.selectedInUse = true;
    vrb2.selected[0] = 1;
    vrb2.selected[1] = 2;
    vrb2.selected[2] = 4;
    vrb2.size = 3;

    lcv0 = (LongColumnVector) vrb2.cols[0];

    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;
    lcv0.vector[5] = 19;
    lcv0.vector[6] = 21;

    lcv0.noNulls = false;
    lcv0.isNull[0] = true;
    lcv0.isNull[2] = true;
    lcv0.isNull[5] = true;

    expr1.evaluate(vrb2);
    assertEquals(1, vrb2.size, () -> "Add assertion message");
    assertEquals(1, vrb2.selected[0], () -> "Add assertion message");

    // Repeating non null
    VectorizedRowBatch vrb3 = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        7, 2, seed);
    lcv0 = (LongColumnVector) vrb3.cols[0];

    lcv0.isRepeating = true;
    lcv0.vector[0] = 5;
    lcv0.vector[1] = 20;
    lcv0.vector[2] = 17;
    lcv0.vector[3] = 15;
    lcv0.vector[4] = 10;

    expr1.evaluate(vrb3);
    assertEquals(7, vrb3.size, () -> "Add assertion message");
    assertFalse(vrb3.selectedInUse, () -> "Add assertion message");
    assertTrue(lcv0.isRepeating, () -> "Add assertion message");

    // Repeating null
    lcv0.noNulls = false;
    lcv0.vector[0] = 5;
    lcv0.isNull[0] = true;

    expr1.evaluate(vrb3);
    assertEquals(0, vrb3.size, () -> "Add assertion message");
  }

  @Test
  public void testFilterDoubleIn() {
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        5, 2, seed);
    DoubleColumnVector dcv0 = new DoubleColumnVector();
    vrb.cols[0] = dcv0;
    double[] inList = {5.0, 20.2};
    FilterDoubleColumnInList f = new FilterDoubleColumnInList(0);
    f.setInListValues(inList);
    VectorExpression expr1 = f;

    // Basic sanity check. Other cases are not skipped because it is similar to the case for Long.
    dcv0.vector[0] = 5.0;
    dcv0.vector[1] = 20.2;
    dcv0.vector[2] = 17.0;
    dcv0.vector[3] = 15.0;
    dcv0.vector[4] = 10.0;

    expr1.evaluate(vrb);

    assertEquals(2, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(0, vrb.selected[0], () -> "Add assertion message");
    assertEquals(1, vrb.selected[1], () -> "Add assertion message");
  }

  @Test
  public void testFilterStringIn() {
    int seed = 17;
    VectorizedRowBatch vrb = VectorizedRowGroupGenUtil.getVectorizedRowBatch(
        3, 2, seed);
    vrb.cols[0] = new BytesColumnVector();
    BytesColumnVector bcv = (BytesColumnVector) vrb.cols[0];

    bcv.initBuffer();
    Integer magicNumber64509670 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    bcv.setVal(0, a, 0,magicNumber64509670
    bcv.setVal(1, b, 0, 1);
    bcv.setVal(2, c, 0, 1);

    VectorExpression expr = new FilterStringColumnInList(0);
    byte[][] inList = {b, c};
    ((FilterStringColumnInList) expr).setInListValues(inList);

    // basic test
    expr.evaluate(vrb);

    assertEquals(2, vrb.size, () -> "Add assertion message");
    assertTrue(vrb.selectedInUse, () -> "Add assertion message");
    assertEquals(1, vrb.selected[0], () -> "Add assertion message");
    assertEquals(2, vrb.selected[1], () -> "Add assertion message");

    // nulls
    vrb.selectedInUse = false;
    vrb.size = 3;
    bcv.noNulls = false;
    bcv.isNull[2] = true;
    expr.evaluate(vrb);
    assertEquals(1, vrb.size, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, vrb.selected[0], () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(vrb.selectedInUse, () -> "Add assertion message");

    // repeating
    vrb.selectedInUse = false;
    vrb.size = 3;
    bcv.noNulls = true;
    bcv.isRepeating = true;
    expr.evaluate(vrb);
    assertEquals(0, vrb.size, () -> "Add assertion message");

    // nulls and repeating
    vrb.selectedInUse = false;
    vrb.size = 3;
    bcv.noNulls = false;
    bcv.isRepeating = true;
    bcv.isNull[0] = true;
    bcv.setVal(0, b, 0, 1);
    expr.evaluate(vrb);
    // Line removed due to Duplicate Assert: assertEquals(0, vrb.size, () -> "Add assertion message");
  }

  /**
   * This tests the template for Decimal Column-Scalar comparison filters,
   * called FilterDecimalColumnCompareScalar.txt. Only equal is tested for
   * multiple cases because the logic is the same for <, >, <=, >=, == and !=.
   */
  @Test
  public void testFilterDecimalColEqualDecimalScalar() {
    VectorizedRowBatch b = getVectorizedRowBatch1DecimalCol();
    HiveDecimal scalar = HiveDecimal.create("-3.30");
    Integer magicNumber30444391 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    VectorExpression expr = new FilterDecimalColEqualDecimalScalar(magicNumber30444391, scalar);
    expr.evaluate(b);

    // check that right row(s) are selected
    assertTrue(b.selectedInUse, () -> "Add assertion message");
    assertEquals(1, b.selected[0], () -> "Add assertion message");
    assertEquals(1, b.size, () -> "Add assertion message");

    // try again with a null value
    b = getVectorizedRowBatch1DecimalCol();
    b.cols[0].noNulls = false;
    b.cols[0].isNull[1] = true;
    expr.evaluate(b);

    // verify that no rows were selected
    assertEquals(0, b.size, () -> "Add assertion message");

    // try the repeating case
    b = getVectorizedRowBatch1DecimalCol();
    b.cols[0].isRepeating = true;
    expr.evaluate(b);

    // verify that no rows were selected
    // Line removed due to Duplicate Assert: assertEquals(0, b.size, () -> "Add assertion message");

    // try the repeating null case
    b = getVectorizedRowBatch1DecimalCol();
    b.cols[0].isRepeating = true;
    b.cols[0].noNulls = false;
    b.cols[0].isNull[0] = true;
    expr.evaluate(b);

    // verify that no rows were selected
    // Line removed due to Duplicate Assert: assertEquals(0, b.size, () -> "Add assertion message");
  }

  /**
   * This tests the template for Decimal Scalar-Column comparison filters,
   * called FilterDecimalScalarCompareColumn.txt. Only equal is tested for multiple
   * cases because the logic is the same for <, >, <=, >=, == and !=.
   */
  @Test
  public void testFilterDecimalScalarEqualDecimalColumn() {
    VectorizedRowBatch b = getVectorizedRowBatch1DecimalCol();
    HiveDecimal scalar = HiveDecimal.create("-3.30");
    VectorExpression expr = new FilterDecimalScalarEqualDecimalColumn(scalar, 0);
    expr.evaluate(b);

    // check that right row(s) are selected
    assertTrue(b.selectedInUse, () -> "Add assertion message");
    assertEquals(1, b.selected[0], () -> "Add assertion message");
    assertEquals(1, b.size, () -> "Add assertion message");

    // try again with a null value
    b = getVectorizedRowBatch1DecimalCol();
    b.cols[0].noNulls = false;
    b.cols[0].isNull[1] = true;
    expr.evaluate(b);

    // verify that no rows were selected
    assertEquals(0, b.size, () -> "Add assertion message");

    // try the repeating case
    b = getVectorizedRowBatch1DecimalCol();
    b.cols[0].isRepeating = true;
    expr.evaluate(b);

    // verify that no rows were selected
    // Line removed due to Duplicate Assert: assertEquals(0, b.size, () -> "Add assertion message");

    // try the repeating null case
    b = getVectorizedRowBatch1DecimalCol();
    b.cols[0].isRepeating = true;
    b.cols[0].noNulls = false;
    b.cols[0].isNull[0] = true;
    expr.evaluate(b);

    // verify that no rows were selected
    Integer magicNumber32649818 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    // Line removed due to Duplicate Assert: assertEquals(magicNumber32649818, b.size() -> "Add assertion message");
  }

  /**
   * This tests the template for Decimal Column-Column comparison filters,
   * called FilterDecimalColumnCompareColumn.txt. Only equal is tested for multiple
   * cases because the logic is the same for <, >, <=, >=, == and !=.
   */
  @Test
  public void testFilterDecimalColumnEqualDecimalColumn() {
    VectorizedRowBatch b = getVectorizedRowBatch2DecimalCol();
    VectorExpression expr = new FilterDecimalColEqualDecimalColumn(0, 1);
    expr.evaluate(b);

    // check that right row(s) are selected
    assertTrue(b.selectedInUse, () -> "Add assertion message");
    assertEquals(1, b.selected[0], () -> "Add assertion message");
    assertEquals(1, b.size, () -> "Add assertion message");

    // try again with a null value
    b = getVectorizedRowBatch2DecimalCol();
    b.cols[0].noNulls = false;
    b.cols[0].isNull[1] = true;
    expr.evaluate(b);

    // verify that no rows were selected
    assertEquals(0, b.size, () -> "Add assertion message");

    // try the repeating case
    b = getVectorizedRowBatch2DecimalCol();
    b.cols[0].isRepeating = true;
    expr.evaluate(b);

    // verify that no rows were selected
    // Line removed due to Duplicate Assert: assertEquals(0, b.size, () -> "Add assertion message");

    // try the repeating null case
    b = getVectorizedRowBatch2DecimalCol();
    b.cols[0].isRepeating = true;
    b.cols[0].noNulls = false;
    b.cols[0].isNull[0] = true;
    expr.evaluate(b);

    // verify that no rows were selected
    // Line removed due to Duplicate Assert: assertEquals(0, b.size, () -> "Add assertion message");

    // try nulls on both sides
    b = getVectorizedRowBatch2DecimalCol();
    b.cols[0].noNulls = false;
    b.cols[0].isNull[0] = true;
    b.cols[1].noNulls = false;
    b.cols[1].isNull[2] = true;
    expr.evaluate(b);
    // Line removed due to Duplicate Assert: assertEquals(1, b.size, () -> "Add assertion message");

    // try repeating on both sides
    b = getVectorizedRowBatch2DecimalCol();
    b.cols[0].isRepeating = true;
    b.cols[1].isRepeating = true;
    expr.evaluate(b);

    // verify that no rows were selected
    // Line removed due to Duplicate Assert: assertEquals(0, b.size, () -> "Add assertion message");
  }

  /**
   * Spot check col < scalar for decimal.
   */
  @Test
  public void testFilterDecimalColLessScalar() {
    VectorizedRowBatch b = getVectorizedRowBatch1DecimalCol();
    HiveDecimal scalar = HiveDecimal.create("0");
    VectorExpression expr = new FilterDecimalColLessDecimalScalar(0, scalar);
    expr.evaluate(b);

    // check that right row(s) are selected
    assertTrue(b.selectedInUse, () -> "Add assertion message");
    Integer magicNumber11117633 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber11117633, b.selected[0]() -> "Add assertion message");
    assertEquals(1, b.size, () -> "Add assertion message");
  }

  /**
   * Spot check scalar > col for decimal.
   */
  @Test
  public void testFilterDecimalScalarGreaterThanColumn() {
    VectorizedRowBatch b = getVectorizedRowBatch1DecimalCol();
    HiveDecimal scalar = HiveDecimal.create("0");
    VectorExpression expr = new FilterDecimalScalarGreaterDecimalColumn(scalar, 0);
    expr.evaluate(b);

    // check that right row(s) are selected
    assertTrue(b.selectedInUse, () -> "Add assertion message");
    assertEquals(1, b.selected[0], () -> "Add assertion message");
    assertEquals(1, b.size, () -> "Add assertion message");
  }

  /**
   * Spot check col >= col for decimal.
   */
  @Test
  public void testFilterDecimalColGreaterEqualCol() {
    VectorizedRowBatch b = getVectorizedRowBatch2DecimalCol();
    VectorExpression expr = new FilterDecimalColGreaterEqualDecimalColumn(0, 1);
    expr.evaluate(b);

    // check that right row(s) are selected
    assertTrue(b.selectedInUse, () -> "Add assertion message");
    assertEquals(0, b.selected[0], () -> "Add assertion message");
    assertEquals(1, b.selected[1], () -> "Add assertion message");
    assertEquals(2, b.size, () -> "Add assertion message");
  }

  private VectorizedRowBatch getVectorizedRowBatch1DecimalCol() {
    VectorizedRowBatch b = new VectorizedRowBatch(1);
    DecimalColumnVector v0;
    b.cols[0] = v0 = new DecimalColumnVector(18, 2);
    v0.vector[0].set(HiveDecimal.create("1.20"));
    v0.vector[1].set(HiveDecimal.create("-3.30"));
    v0.vector[2].set(HiveDecimal.create("0"));

    b.size = 3;
    return b;
  }

  private VectorizedRowBatch getVectorizedRowBatch2DecimalCol() {
    VectorizedRowBatch b = new VectorizedRowBatch(2);
    DecimalColumnVector v0, v1;
    b.cols[0] = v0 = new DecimalColumnVector(18, 2);
    v0.vector[0].set(HiveDecimal.create("1.20"));
    v0.vector[1].set(HiveDecimal.create("-3.30"));
    v0.vector[2].set(HiveDecimal.create("0"));

    b.cols[1] = v1 = new DecimalColumnVector(18, 2);
    v1.vector[0].set(HiveDecimal.create("-1.00"));
    v1.vector[1].set(HiveDecimal.create("-3.30"));
    v1.vector[2].set(HiveDecimal.create("10.00"));

    b.size = 3;
    return b;
  }
}