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
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.testdo.testmap.Artist;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @since 4.0
 */
public class ASTLocateTest {

    @Test
    public void testEvaluateLocate() throws Exception {
        ASTObjPath path = new ASTObjPath("artistName");
        ASTScalar substr = new ASTScalar("678");
        ASTScalar offset = new ASTScalar((Integer)5);
        ASTLocate exp = new ASTLocate(substr, path, offset);

        Artist a = new Artist();
        a.setArtistName("1267834567890abc");

        Object res = exp.evaluateNode(a);
        assertTrue(res instanceof Integer, () -> "Add assertion message");
        Integer magicNumber60817537 = 9; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber60817537, res() -> "Add assertion message");

        a.setArtistName("abcdefgh");
        res = exp.evaluateNode(a);
        // Line removed due to Duplicate Assert: assertTrue(res instanceof Integer, () -> "Add assertion message");
        assertEquals(0, res, () -> "Add assertion message");
    }

    @Test
    public void parseTest() throws Exception {
        String expString = "locate(\"xyz\" , abc , 4)";
        Expression exp = ExpressionFactory.exp(expString);

        assertTrue(exp instanceof ASTLocate, () -> "Add assertion message");
        String toString = exp.toString();
        assertEquals(expString, toString, () -> "Add assertion message");
    }

}