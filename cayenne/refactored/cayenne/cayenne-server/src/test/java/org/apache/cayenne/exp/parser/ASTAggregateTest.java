/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.exp.parser;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionException;
import org.apache.cayenne.exp.ExpressionFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @since 4.0
 */
public class ASTAggregateTest {

    @Test
    public void testAvgConstruct() throws Exception {
        ASTAvg avg = new ASTAvg(null);
        assertEquals("AVG", avg.getFunctionName(), () -> "Add assertion message");
    }

    @Test(expected = ExpressionException.class)
    public void testAvgEvaluate() throws Exception {
        ASTAvg avg = new ASTAvg(null);
        avg.evaluate(new Object());
    }

    @Test
    public void testAvgParse() throws Exception {
        String expressionString = "avg(artistName)";
        Expression exp = ExpressionFactory.exp(expressionString);
        assertTrue(exp instanceof ASTAvg, () -> "Add assertion message");
        Integer magicNumber88938102 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber88938102, exp.getOperandCount(), () -> "Add assertion message");
        assertTrue(exp.getOperand(0) instanceof ASTObjPath, () -> "Add assertion message");

        assertEquals(expressionString, exp.toString(), () -> "Add assertion message");
    }

    @Test
    public void testCountConstruct() throws Exception {
        ASTCount count = new ASTCount();
        assertEquals("COUNT", count.getFunctionName(), () -> "Add assertion message");
    }

    @Test
    public void testCountExpParse() throws Exception {
        String expressionString = "count(artistName)";
        Expression exp = ExpressionFactory.exp(expressionString);
        assertTrue(exp instanceof ASTCount, () -> "Add assertion message");
        assertEquals(1, exp.getOperandCount(), () -> "Add assertion message");
        assertTrue(exp.getOperand(0) instanceof ASTObjPath, () -> "Add assertion message");

        assertEquals(expressionString, exp.toString(), () -> "Add assertion message");
    }

    @Test
    public void testCountAsteriskParse() throws Exception {
        String expressionString = "count(*)";
        Expression exp = ExpressionFactory.exp(expressionString);
        assertTrue(exp instanceof ASTCount, () -> "Add assertion message");
        assertEquals(1, exp.getOperandCount(), () -> "Add assertion message");
        assertTrue(exp.getOperand(0) instanceof ASTAsterisk, () -> "Add assertion message");

        assertEquals(expressionString, exp.toString(), () -> "Add assertion message");
    }

    @Test
    public void testCountDistinctParse() throws Exception {
        String expressionString = "count(distinct(artistName))";
        Expression exp = ExpressionFactory.exp(expressionString);
        assertTrue(exp instanceof ASTCount, () -> "Add assertion message");
        assertEquals(1, exp.getOperandCount(), () -> "Add assertion message");
        assertTrue(exp.getOperand(0) instanceof ASTDistinct, () -> "Add assertion message");

        ASTDistinct distinct = (ASTDistinct)exp.getOperand(0);
        assertTrue(distinct.getOperand(0) instanceof ASTObjPath, () -> "Add assertion message");

        assertEquals(expressionString, exp.toString(), () -> "Add assertion message");
    }

    @Test
    public void testMinConstruct() throws Exception {
        ASTMin min = new ASTMin(null);
        assertEquals("MIN", min.getFunctionName(), () -> "Add assertion message");
    }

    @Test
    public void testMinParse() throws Exception {
        String expressionString = "min(artistName)";
        Expression exp = ExpressionFactory.exp(expressionString);
        assertTrue(exp instanceof ASTMin, () -> "Add assertion message");
        assertEquals(1, exp.getOperandCount(), () -> "Add assertion message");
        assertTrue(exp.getOperand(0) instanceof ASTObjPath, () -> "Add assertion message");

        assertEquals(expressionString, exp.toString(), () -> "Add assertion message");
    }

    @Test
    public void testMaxConstruct() throws Exception {
        ASTMax max = new ASTMax(null);
        assertEquals("MAX", max.getFunctionName(), () -> "Add assertion message");
    }

    @Test
    public void testMaxParse() throws Exception {
        String expressionString = "max(artistName)";
        Expression exp = ExpressionFactory.exp(expressionString);
        assertTrue(exp instanceof ASTMax, () -> "Add assertion message");
        assertEquals(1, exp.getOperandCount(), () -> "Add assertion message");
        assertTrue(exp.getOperand(0) instanceof ASTObjPath, () -> "Add assertion message");

        assertEquals(expressionString, exp.toString(), () -> "Add assertion message");
    }

    @Test
    public void testSumConstruct() throws Exception {
        ASTSum sum = new ASTSum(null);
        assertEquals("SUM", sum.getFunctionName(), () -> "Add assertion message");
    }

    @Test
    public void testSumParse() throws Exception {
        String expressionString = "sum(artistName)";
        Expression exp = ExpressionFactory.exp(expressionString);
        assertTrue(exp instanceof ASTSum, () -> "Add assertion message");
        assertEquals(1, exp.getOperandCount(), () -> "Add assertion message");
        assertTrue(exp.getOperand(0) instanceof ASTObjPath, () -> "Add assertion message");

        assertEquals(expressionString, exp.toString(), () -> "Add assertion message");
    }

}