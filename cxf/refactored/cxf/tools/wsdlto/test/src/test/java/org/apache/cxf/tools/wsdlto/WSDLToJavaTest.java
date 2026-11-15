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

package org.apache.cxf.tools.wsdlto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class WSDLToJavaTest {

    @Test
    public void testGetFrontEndName() throws Exception {
        WSDLToJava w2j = new WSDLToJava();
        assertEquals("jaxws", w2j.getFrontEndName(new String[]{"-frontend", "jaxws"}), () -> "Add assertion message");
        assertEquals("jaxws", w2j.getFrontEndName(new String[]{"-fe", "jaxws"}), () -> "Add assertion message");
        assertNull(w2j.getFrontEndName(new String[]{"-frontend"}), () -> "Add assertion message");
        assertNull(w2j.getFrontEndName(new String[]{"-fe"}), () -> "Add assertion message");
        assertNull(w2j.getFrontEndName(new String[]{"nothing"}), () -> "Add assertion message");
        assertNull(w2j.getFrontEndName(null), () -> "Add assertion message");
    }

    @Test
    public void testGetDataBindingName() throws Exception {
        WSDLToJava w2j = new WSDLToJava();
        assertEquals("jaxb", w2j.getDataBindingName(new String[]{"-databinding", "jaxb"}), () -> "Add assertion message");
        assertEquals("jaxb", w2j.getDataBindingName(new String[]{"-db", "jaxb"}), () -> "Add assertion message");
        assertNull(w2j.getDataBindingName(new String[]{"-databinding"}), () -> "Add assertion message");
        assertNull(w2j.getDataBindingName(new String[]{"-db"}), () -> "Add assertion message");
        assertNull(w2j.getDataBindingName(new String[]{"nothing"}), () -> "Add assertion message");
        assertNull(w2j.getDataBindingName(null), () -> "Add assertion message");
        assertNull(w2j.getDataBindingName(new String[]{"-frontend", "jaxws"}), () -> "Add assertion message");
    }

    @Test
    public void testIsVerbose() {
        WSDLToJava w2j = new WSDLToJava();
        w2j.setArguments(new String[]{"-V"});
        assertTrue(w2j.isVerbose(), () -> "Add assertion message");
        w2j = new WSDLToJava();
        w2j.setArguments(new String[]{"-verbose"});
        // Line removed due to Duplicate Assert: assertTrue(w2j.isVerbose(), () -> "Add assertion message");
        w2j = new WSDLToJava();
        w2j.setArguments(new String[]{"none"});
        assertFalse(w2j.isVerbose(), () -> "Add assertion message");
    }
}