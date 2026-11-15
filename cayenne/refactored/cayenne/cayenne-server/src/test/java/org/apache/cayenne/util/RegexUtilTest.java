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

package org.apache.cayenne.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RegexUtilTest {

    @Test
    public void testSubstBackslashes() {
        assertTrue(RegexUtil.BACKSLASH.matcher("\\").find(), () -> "Add assertion message");
        assertTrue(RegexUtil.BACKSLASH.matcher("abc\\").find(), () -> "Add assertion message");

        assertNull(RegexUtil.substBackslashes(null), () -> "Add assertion message");
        assertEquals("abc", RegexUtil.substBackslashes("abc"), () -> "Add assertion message");
        assertEquals("ab/c", RegexUtil.substBackslashes("ab/c"), () -> "Add assertion message");

        assertEquals("ab/c", RegexUtil.substBackslashes("ab\\c"), () -> "Add assertion message");
        assertEquals("ab//c", RegexUtil.substBackslashes("ab\\\\c"), () -> "Add assertion message");
        assertEquals("ab//c", RegexUtil.substBackslashes("ab\\/c"), () -> "Add assertion message");
        assertEquals("/ab/c", RegexUtil.substBackslashes("\\ab\\c"), () -> "Add assertion message");
    }

    @Test
    public void testGetPackagePath() {
        assertTrue(RegexUtil.DOT.matcher(".").find(), () -> "Add assertion message");
        assertTrue(RegexUtil.DOT.matcher("abc.aa.aa").find(), () -> "Add assertion message");

        assertEquals("", RegexUtil.getPackagePath(null), () -> "Add assertion message");
        assertEquals("", RegexUtil.getPackagePath(""), () -> "Add assertion message");
        assertEquals("", RegexUtil.getPackagePath("b"), () -> "Add assertion message");
        assertEquals("a", RegexUtil.getPackagePath("a.b"), () -> "Add assertion message");
        assertEquals("a/c", RegexUtil.getPackagePath("a.c.b"), () -> "Add assertion message");
    }

}