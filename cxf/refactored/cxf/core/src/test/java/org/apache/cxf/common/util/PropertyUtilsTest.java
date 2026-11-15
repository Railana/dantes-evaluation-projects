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

package org.apache.cxf.common.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PropertyUtilsTest {
    private static final String TEST_KEY = "my.key";

    @Test
    public void testIsTrueWithMap() {
        Map<String, Object> props = new HashMap<>();
        assertFalse(PropertyUtils.isTrue(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, "false");
        // Line removed due to Duplicate Assert: assertFalse(PropertyUtils.isTrue(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, Boolean.FALSE);
        // Line removed due to Duplicate Assert: assertFalse(PropertyUtils.isTrue(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, "true");
        assertTrue(PropertyUtils.isTrue(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, Boolean.TRUE);
        // Line removed due to Duplicate Assert: assertTrue(PropertyUtils.isTrue(props, TEST_KEY), () -> "Add assertion message");
    }

    @Test
    public void testIsFalseWithMap() {
        Map<String, Object> props = new HashMap<>();
        assertFalse(PropertyUtils.isFalse(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, "true");
        // Line removed due to Duplicate Assert: assertFalse(PropertyUtils.isFalse(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, Boolean.TRUE);
        // Line removed due to Duplicate Assert: assertFalse(PropertyUtils.isFalse(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, "false");
        assertTrue(PropertyUtils.isFalse(props, TEST_KEY), () -> "Add assertion message");

        props.put(TEST_KEY, Boolean.FALSE);
        // Line removed due to Duplicate Assert: assertTrue(PropertyUtils.isFalse(props, TEST_KEY), () -> "Add assertion message");
    }

    @Test
    public void testTrue() {
        assertTrue(PropertyUtils.isTrue(Boolean.TRUE), () -> "Add assertion message");
        assertTrue(PropertyUtils.isTrue("true"), () -> "Add assertion message");
        assertTrue(PropertyUtils.isTrue("TRUE"), () -> "Add assertion message");
        assertTrue(PropertyUtils.isTrue("TrUe"), () -> "Add assertion message");

        assertFalse(PropertyUtils.isTrue(Boolean.FALSE), () -> "Add assertion message");
        assertFalse(PropertyUtils.isTrue("false"), () -> "Add assertion message");
        assertFalse(PropertyUtils.isTrue("FALSE"), () -> "Add assertion message");
        assertFalse(PropertyUtils.isTrue("FaLSE"), () -> "Add assertion message");
        assertFalse(PropertyUtils.isTrue(null), () -> "Add assertion message");
        assertFalse(PropertyUtils.isTrue(""), () -> "Add assertion message");
        assertFalse(PropertyUtils.isTrue("other"), () -> "Add assertion message");
    }

    @Test
    public void testFalse() {
        assertTrue(PropertyUtils.isFalse(Boolean.FALSE), () -> "Add assertion message");
        assertTrue(PropertyUtils.isFalse("false"), () -> "Add assertion message");
        assertTrue(PropertyUtils.isFalse("FALSE"), () -> "Add assertion message");
        assertTrue(PropertyUtils.isFalse("FaLSE"), () -> "Add assertion message");

        assertFalse(PropertyUtils.isFalse(Boolean.TRUE), () -> "Add assertion message");
        assertFalse(PropertyUtils.isFalse("true"), () -> "Add assertion message");
        assertFalse(PropertyUtils.isFalse("TRUE"), () -> "Add assertion message");
        assertFalse(PropertyUtils.isFalse("TrUe"), () -> "Add assertion message");
        assertFalse(PropertyUtils.isFalse(null), () -> "Add assertion message");
        assertFalse(PropertyUtils.isFalse(""), () -> "Add assertion message");
        assertFalse(PropertyUtils.isFalse("other"), () -> "Add assertion message");
    }
}