/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.transport.http;

import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A {@code PatternBuilderTest} is ...
 */
public class PatternBuilderTest {

    @Test
    public void testPatternBuilder() {

        // Simple matching
        Pattern pattern = PatternBuilder.build("localhost");

        assertTrue(pattern.matcher("localhost").matches(), () -> "Add assertion message");
        assertFalse(pattern.matcher("localhost-after").matches(), () -> "Add assertion message");
        assertFalse(pattern.matcher("before-localhost").matches(), () -> "Add assertion message");

        // List matching
        pattern = PatternBuilder.build("localhost|othername");

        // Line removed due to Duplicate Assert: assertTrue(pattern.matcher("localhost").matches(), () -> "Add assertion message");
        assertTrue(pattern.matcher("othername").matches(), () -> "Add assertion message");
        assertFalse(pattern.matcher("somename").matches(), () -> "Add assertion message");

        // Name with dots matching
        pattern = PatternBuilder.build("www.apache.org");

        assertTrue(pattern.matcher("www.apache.org").matches(), () -> "Add assertion message");
        assertFalse(pattern.matcher("www1apache1org").matches(), () -> "Add assertion message");

        // Wildcards matching 1/2
        pattern = PatternBuilder.build("*.apache.org");

        // Line removed due to Duplicate Assert: assertTrue(pattern.matcher("www.apache.org").matches(), () -> "Add assertion message");
        assertTrue(pattern.matcher("svn.apache.org").matches(), () -> "Add assertion message");
        assertFalse(pattern.matcher("apache.org").matches(), () -> "Add assertion message");
        assertTrue(pattern.matcher(".apache.org").matches(), () -> "Add assertion message");

        // Wildcards matching 2/2
        pattern = PatternBuilder.build("www.apache.*");

        // Line removed due to Duplicate Assert: assertTrue(pattern.matcher("www.apache.org").matches(), () -> "Add assertion message");
        assertTrue(pattern.matcher("www.apache.net").matches(), () -> "Add assertion message");
        assertFalse(pattern.matcher("www.apache").matches(), () -> "Add assertion message");
        assertTrue(pattern.matcher("www.apache.").matches(), () -> "Add assertion message");

    }

}