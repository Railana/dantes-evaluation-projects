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

import java.util.Date;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @since 4.0
 */
public class ASTCurrentTimestampTest {

    @Test
    public void testParse() {
        Expression exp2 = ExpressionFactory.exp("currentTimestamp()");
        assertTrue(exp2 instanceof ASTCurrentTimestamp, () -> "Add assertion message");

        Expression exp3 = ExpressionFactory.exp("now()");
        assertTrue(exp3 instanceof ASTCurrentTimestamp, () -> "Add assertion message");

        assertEquals("currentTimestamp()", exp2.toString(), () -> "Add assertion message");
        assertEquals("currentTimestamp()", exp3.toString(), () -> "Add assertion message");
    }

    @Test
    public void testEvaluate() {
        Expression exp = new ASTCurrentTimestamp();
        Object result = exp.evaluate(new Object());
        assertTrue(result instanceof Date, () -> "Add assertion message");
    }
}