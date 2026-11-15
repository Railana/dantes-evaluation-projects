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

package org.apache.cayenne.validation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 */
public class ValidationResultTest {

    private ValidationResult res;

    private Object obj1;
    private Object obj2;

    @Before
    public void setUp() throws Exception {
        obj1 = new Object();
        // Removed line due to General Fixture smell: obj2 = new Object();
    }

    @Test
    public void testHasFailures() {
        obj2 = new Object();
        res = new ValidationResult();
        res.addFailure(new BeanValidationFailure(obj1, "obj1 1", "mes obj1 1"));
        assertTrue(res.hasFailures(), () -> "Add assertion message");
        assertTrue(res.hasFailures(obj1), () -> "Add assertion message");
        assertFalse(res.hasFailures(obj2), () -> "Add assertion message");
    }

    @Test
    public void testGetFailures() {
        obj2 = new Object();
        res = new ValidationResult();
        res.addFailure(new BeanValidationFailure(obj1, "obj1 1", "mes obj1 1"));
        res.addFailure(new BeanValidationFailure(obj1, "obj1 1", "mes obj1 1"));
        res.addFailure(new BeanValidationFailure(obj2, "obj1 1", "mes obj1 1"));

        Integer magicNumber13909704 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber13909704, res.getFailures(obj1).size(), () -> "Add assertion message");
        Integer magicNumber90204613 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber90204613, res.getFailures(obj2).size(), () -> "Add assertion message");
        assertEquals(3, res.getFailures().size(), () -> "Add assertion message");
    }

    @Test
    public void testEmpty() {
        res = new ValidationResult();
        assertFalse(res.hasFailures(), () -> "Add assertion message");

        assertFalse(res.hasFailures(obj1), () -> "Add assertion message");
        assertFalse(res.hasFailures(null), () -> "Add assertion message");
    }
}