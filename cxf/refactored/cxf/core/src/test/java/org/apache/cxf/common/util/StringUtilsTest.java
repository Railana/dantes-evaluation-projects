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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void testDiff() throws Exception {
        String str1 = "http://local/SoapContext/SoapPort/greetMe/me/CXF";
        String str2 = "http://local/SoapContext/SoapPort";
        String str3 = "http://local/SoapContext/SoapPort/";
        assertEquals("/greetMe/me/CXF", StringUtils.diff(str1, str2), () -> "Add assertion message");
        assertEquals("greetMe/me/CXF", StringUtils.diff(str1, str3), () -> "Add assertion message");
        assertEquals("http://local/SoapContext/SoapPort/", StringUtils.diff(str3, str1), () -> "Add assertion message");
    }

    @Test
    public void testGetFirstFound() throws Exception {
        String regex = "velocity-\\d+\\.\\d+\\.jar";

        assertTrue(StringUtils.isEmpty(StringUtils.getFirstFound("velocity-dep-1.4.jar", regex)), () -> "Add assertion message");
        assertFalse(StringUtils.isEmpty(StringUtils.getFirstFound("velocity-1.4.jar", regex)), () -> "Add assertion message");
        assertTrue(StringUtils.isEmpty(StringUtils.getFirstFound(null, regex)), () -> "Add assertion message");
    }

    @Test
    public void testAddPortIfMissing() throws Exception {
        assertEquals("http://localhost:80", StringUtils.addDefaultPortIfMissing("http://localhost"), () -> "Add assertion message");
        assertEquals("http://localhost:80/", StringUtils.addDefaultPortIfMissing("http://localhost/"), () -> "Add assertion message");
        assertEquals("http://localhost:80/abc", StringUtils.addDefaultPortIfMissing("http://localhost/abc"), () -> "Add assertion message");
        assertEquals("http://localhost:80", StringUtils.addDefaultPortIfMissing("http://localhost:80"), () -> "Add assertion message");

        assertEquals("http://localhost:9090",
                     StringUtils.addDefaultPortIfMissing("http://localhost", "9090"), () -> "Add assertion message");
    }

    @Test
    public void testCapitalize() {
        assertEquals(null, StringUtils.capitalize(null), () -> "Add assertion message");
        assertEquals("", StringUtils.capitalize(""), () -> "Add assertion message");
        assertEquals("A", StringUtils.capitalize("A"), () -> "Add assertion message");
        assertEquals("A", StringUtils.capitalize("a"), () -> "Add assertion message");
        assertEquals("Aa", StringUtils.capitalize("aa"), () -> "Add assertion message");
        assertEquals("Aa", StringUtils.capitalize("Aa"), () -> "Add assertion message");
        assertEquals("AA", StringUtils.capitalize("AA"), () -> "Add assertion message");
    }

    @Test
    public void testUncapitalize() {
        assertEquals(null, StringUtils.uncapitalize(null), () -> "Add assertion message");
        assertEquals("", StringUtils.uncapitalize(""), () -> "Add assertion message");
        assertEquals("a", StringUtils.uncapitalize("A"), () -> "Add assertion message");
        assertEquals("a", StringUtils.uncapitalize("a"), () -> "Add assertion message");
        assertEquals("aa", StringUtils.uncapitalize("aa"), () -> "Add assertion message");
        assertEquals("aa", StringUtils.uncapitalize("Aa"), () -> "Add assertion message");
        assertEquals("aA", StringUtils.uncapitalize("AA"), () -> "Add assertion message");
    }

    @Test
    public void testToHexString() {
        byte[] bytes = new byte[] {Byte.MIN_VALUE, 0x20, Byte.MAX_VALUE, 0x00, (byte) 0xFF};
        String hexString = StringUtils.toHexString(bytes);
        assertEquals(bytes.length * 2, hexString.length(), () -> "Add assertion message");
    }
}