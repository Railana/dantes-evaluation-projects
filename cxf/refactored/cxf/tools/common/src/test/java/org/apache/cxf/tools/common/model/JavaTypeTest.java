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

package org.apache.cxf.tools.common.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaTypeTest {
    @Test
    public void testGetPredefinedDefaultTypeValue() throws Exception {
        assertEquals("0", new JavaType("i", int.class.getName(), null).getDefaultTypeValue(), () -> "Add assertion message");
        assertEquals("false", new JavaType("i", boolean.class.getName(), null).getDefaultTypeValue(), () -> "Add assertion message");
        assertEquals("new javax.xml.namespace.QName(\"\", \"\")",
                     new JavaType("i",
                                  javax.xml.namespace.QName.class.getName(), null).getDefaultTypeValue(), () -> "Add assertion message");
    }

    @Test
    public void testGetArrayDefaultTypeValue() throws Exception {
        assertEquals("new int[0]", new JavaType("i", "int[]", null).getDefaultTypeValue(), () -> "Add assertion message");
        assertEquals("new String[0]", new JavaType("i", "String[]", null).getDefaultTypeValue(), () -> "Add assertion message");
    }

    @Test
    public void testGetClassDefaultTypeValue() throws Exception {
        assertEquals("new org.apache.cxf.tools.common.model.JavaType()",
                     new JavaType("i", "org.apache.cxf.tools.common.model.JavaType", null)
                         .getDefaultTypeValue(), () -> "Add assertion message");
    }

    @Test
    public void testSetClass() {
        JavaType type = new JavaType();
        type.setClassName("foo.bar.A");
        assertEquals("foo.bar", type.getPackageName(), () -> "Add assertion message");
        assertEquals("A", type.getSimpleName(), () -> "Add assertion message");
    }
}