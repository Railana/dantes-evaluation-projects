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

package org.apache.cayenne.exp;

import org.apache.cayenne.exp.parser.ASTAbs;
import org.apache.cayenne.exp.parser.ASTAsterisk;
import org.apache.cayenne.exp.parser.ASTAvg;
import org.apache.cayenne.exp.parser.ASTConcat;
import org.apache.cayenne.exp.parser.ASTCount;
import org.apache.cayenne.exp.parser.ASTCurrentDate;
import org.apache.cayenne.exp.parser.ASTCurrentTime;
import org.apache.cayenne.exp.parser.ASTCurrentTimestamp;
import org.apache.cayenne.exp.parser.ASTCustomOperator;
import org.apache.cayenne.exp.parser.ASTLength;
import org.apache.cayenne.exp.parser.ASTLocate;
import org.apache.cayenne.exp.parser.ASTLower;
import org.apache.cayenne.exp.parser.ASTMax;
import org.apache.cayenne.exp.parser.ASTMin;
import org.apache.cayenne.exp.parser.ASTMod;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.ASTSqrt;
import org.apache.cayenne.exp.parser.ASTSubstring;
import org.apache.cayenne.exp.parser.ASTSum;
import org.apache.cayenne.exp.parser.ASTTrim;
import org.apache.cayenne.exp.parser.ASTUpper;
import org.apache.cayenne.testdo.testmap.Artist;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @since 4.0
 */
public class FunctionExpressionFactoryTest {

    @Test
    public void substringExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.substringExp(Artist.ARTIST_NAME.getExpression(), 10, 15);
        Expression exp2 = FunctionExpressionFactory.substringExp(Artist.ARTIST_NAME.getName(), 10, 15);
        Expression exp3 = FunctionExpressionFactory.substringExp(Artist.ARTIST_NAME.getExpression(), new ASTScalar(10), new ASTScalar(15));

        assertTrue(exp1 instanceof ASTSubstring, () -> "Add assertion message");

        Integer magicNumber74794816 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber74794816, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");
        Integer magicNumber49204130 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber49204130, exp1.getOperand(1), () -> "Add assertion message");
        assertEquals(15, exp1.getOperand(2), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
        assertEquals(exp2, exp3, () -> "Add assertion message");
    }

    @Test
    public void trimExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.trimExp(Artist.ARTIST_NAME.getExpression());
        Expression exp2 = FunctionExpressionFactory.trimExp(Artist.ARTIST_NAME.getName());

        assertTrue(exp1 instanceof ASTTrim, () -> "Add assertion message");

        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
    }

    @Test
    public void lowerExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.lowerExp(Artist.ARTIST_NAME.getExpression());
        Expression exp2 = FunctionExpressionFactory.lowerExp(Artist.ARTIST_NAME.getName());

        assertTrue(exp1 instanceof ASTLower, () -> "Add assertion message");

        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
    }

    @Test
    public void upperExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.upperExp(Artist.ARTIST_NAME.getExpression());
        Expression exp2 = FunctionExpressionFactory.upperExp(Artist.ARTIST_NAME.getName());

        assertTrue(exp1 instanceof ASTUpper, () -> "Add assertion message");

        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
    }

    @Test
    public void lengthExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.lengthExp(Artist.ARTIST_NAME.getExpression());
        Expression exp2 = FunctionExpressionFactory.lengthExp(Artist.ARTIST_NAME.getName());

        assertTrue(exp1 instanceof ASTLength, () -> "Add assertion message");

        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
    }


    @Test
    public void locateExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.locateExp("abc", Artist.ARTIST_NAME.getExpression());
        Expression exp2 = FunctionExpressionFactory.locateExp("abc", Artist.ARTIST_NAME.getName());
        Expression exp3 = FunctionExpressionFactory.locateExp(new ASTScalar("abc"), Artist.ARTIST_NAME.getExpression());

        assertTrue(exp1 instanceof ASTLocate, () -> "Add assertion message");

        Integer magicNumber66675639 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber66675639, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals("abc", exp1.getOperand(0), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(1), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
        assertEquals(exp2, exp3, () -> "Add assertion message");
    }


    @Test
    public void absExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.absExp(Artist.ARTIST_NAME.getExpression());
        Expression exp2 = FunctionExpressionFactory.absExp(Artist.ARTIST_NAME.getName());

        assertTrue(exp1 instanceof ASTAbs, () -> "Add assertion message");

        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
    }

    @Test
    public void sqrtExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.sqrtExp(Artist.ARTIST_NAME.getExpression());
        Expression exp2 = FunctionExpressionFactory.sqrtExp(Artist.ARTIST_NAME.getName());

        assertTrue(exp1 instanceof ASTSqrt, () -> "Add assertion message");

        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
    }


    @Test
    public void modExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.modExp(Artist.ARTIST_NAME.getExpression(), 10);
        Expression exp2 = FunctionExpressionFactory.modExp(Artist.ARTIST_NAME.getName(), 10);
        Expression exp3 = FunctionExpressionFactory.modExp(Artist.ARTIST_NAME.getExpression(), new ASTScalar(10));

        assertTrue(exp1 instanceof ASTMod, () -> "Add assertion message");

        assertEquals(2, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");
        assertEquals(10, exp1.getOperand(1), () -> "Add assertion message");

        assertEquals(exp1, exp2, () -> "Add assertion message");
        assertEquals(exp2, exp3, () -> "Add assertion message");
    }


    @Test
    public void concatExp() throws Exception {
        Expression exp1 = FunctionExpressionFactory.concatExp(Artist.ARTIST_NAME.getExpression(), new ASTScalar("abc"), Artist.DATE_OF_BIRTH.getExpression());
        assertTrue(exp1 instanceof ASTConcat, () -> "Add assertion message");
        Integer magicNumber23400592 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber23400592, exp1.getOperandCount(), () -> "Add assertion message");

        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");
        assertEquals("abc", exp1.getOperand(1), () -> "Add assertion message");
        assertEquals(Artist.DATE_OF_BIRTH.getExpression(), exp1.getOperand(2), () -> "Add assertion message");


        Expression exp2 = FunctionExpressionFactory.concatExp(Artist.ARTIST_NAME.getName(), Artist.DATE_OF_BIRTH.getName(), Artist.PAINTING_ARRAY.getName());
        assertTrue(exp2 instanceof ASTConcat, () -> "Add assertion message");
        Integer magicNumber78128137 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber78128137, exp2.getOperandCount(), () -> "Add assertion message");

        assertEquals(Artist.ARTIST_NAME.getExpression(), exp2.getOperand(0), () -> "Add assertion message");
        assertEquals(Artist.DATE_OF_BIRTH.getExpression(), exp2.getOperand(1), () -> "Add assertion message");
        assertEquals(Artist.PAINTING_ARRAY.getExpression(), exp2.getOperand(2), () -> "Add assertion message");
    }

    @Test
    public void countTest() throws Exception {
        Expression exp1 = FunctionExpressionFactory.countExp();
        assertTrue(exp1 instanceof ASTCount, () -> "Add assertion message");
        Integer magicNumber65759626 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber65759626, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(new ASTAsterisk(), exp1.getOperand(0), () -> "Add assertion message");

        Expression exp2 = FunctionExpressionFactory.countExp(Artist.ARTIST_NAME.getExpression());
        assertTrue(exp2 instanceof ASTCount, () -> "Add assertion message");
        assertEquals(1, exp2.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp2.getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void minTest() throws Exception {
        Expression exp1 = FunctionExpressionFactory.minExp(Artist.ARTIST_NAME.getExpression());
        assertTrue(exp1 instanceof ASTMin, () -> "Add assertion message");
        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void maxTest() throws Exception {
        Expression exp1 = FunctionExpressionFactory.maxExp(Artist.ARTIST_NAME.getExpression());
        assertTrue(exp1 instanceof ASTMax, () -> "Add assertion message");
        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void avgTest() throws Exception {
        Expression exp1 = FunctionExpressionFactory.avgExp(Artist.ARTIST_NAME.getExpression());
        assertTrue(exp1 instanceof ASTAvg, () -> "Add assertion message");
        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void sumTest() throws Exception {
        Expression exp1 = FunctionExpressionFactory.sumExp(Artist.ARTIST_NAME.getExpression());
        assertTrue(exp1 instanceof ASTSum, () -> "Add assertion message");
        assertEquals(1, exp1.getOperandCount(), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), exp1.getOperand(0), () -> "Add assertion message");
    }

    @Test
    public void currentDateTest() throws Exception {
        Expression exp = FunctionExpressionFactory.currentDate();
        assertTrue(exp instanceof ASTCurrentDate, () -> "Add assertion message");
    }

    @Test
    public void currentTimeTest() throws Exception {
        Expression exp = FunctionExpressionFactory.currentTime();
        assertTrue(exp instanceof ASTCurrentTime, () -> "Add assertion message");
    }

    @Test
    public void currentTimestampTest() throws Exception {
        Expression exp = FunctionExpressionFactory.currentTimestamp();
        assertTrue(exp instanceof ASTCurrentTimestamp, () -> "Add assertion message");
    }

    @Test
    public void customOpTest() {
        Integer magicNumber98304408 = 123; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Expression exp = FunctionExpressionFactory.operator("==>",magicNumber98304408, Artist.ARTIST_NAME.getExpression());
        assertTrue(exp instanceof ASTCustomOperator, () -> "Add assertion message");
        ASTCustomOperator operator = (ASTCustomOperator) exp;
        assertEquals("==>", operator.getOperator(), () -> "Add assertion message");
        assertEquals(2, operator.jjtGetNumChildren(), () -> "Add assertion message");

        assertEquals(123, operator.getOperand(0), () -> "Add assertion message");
        assertEquals(Artist.ARTIST_NAME.getExpression(), operator.getOperand(1), () -> "Add assertion message");
    }
}