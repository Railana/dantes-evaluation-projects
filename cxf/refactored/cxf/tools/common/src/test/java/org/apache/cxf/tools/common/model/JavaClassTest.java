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

public class JavaClassTest {
    @Test
    public void testGetterSetter() throws Exception {
        JavaField field = new JavaField("arg0",
                                        "org.apache.cxf.tools.fortest.withannotation.doc.TestDataBean",
                                        "http://doc.withannotation.fortest.tools.cxf.apache.org/");
        JavaClass clz = new JavaClass();
        clz.setFullClassName("org.apache.cxf.tools.fortest.withannotation.doc.jaxws.EchoDataBean");
        JavaMethod getter = clz.appendGetter(field);
        assertEquals("getArg0", getter.getName(), () -> "Add assertion message");
        assertEquals("org.apache.cxf.tools.fortest.withannotation.doc.TestDataBean",
                     getter.getReturn().getClassName(), () -> "Add assertion message");
        assertEquals("arg0", getter.getReturn().getName(), () -> "Add assertion message");

        JavaMethod setter = clz.appendSetter(field);
        assertEquals("setArg0", setter.getName(), () -> "Add assertion message");
        assertEquals("void", setter.getReturn().getClassName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("arg0", getter.getReturn().getName(), () -> "Add assertion message");
        assertEquals("org.apache.cxf.tools.fortest.withannotation.doc.TestDataBean",
                     setter.getParameters().get(0).getClassName(), () -> "Add assertion message");
    }

    @Test
    public void testGetterSetterStringArray() {
        JavaField field = new JavaField("array",
                                        "String[]",
                                        "http://doc.withannotation.fortest.tools.cxf.apache.org/");

        JavaClass clz = new JavaClass();
        clz.setFullClassName("org.apache.cxf.tools.fortest.withannotation.doc.jaxws.SayHi");
        JavaMethod getter = clz.appendGetter(field);
        assertEquals("getArray", getter.getName(), () -> "Add assertion message");
        assertEquals("String[]",
                     getter.getReturn().getClassName(), () -> "Add assertion message");
        assertEquals("array", getter.getReturn().getName(), () -> "Add assertion message");
        assertEquals("return this.array;", getter.getJavaCodeBlock().getExpressions().get(0).toString(), () -> "Add assertion message");

        JavaMethod setter = clz.appendSetter(field);
        assertEquals("setArray", setter.getName(), () -> "Add assertion message");
        assertEquals("void", setter.getReturn().getClassName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("array", getter.getReturn().getName(), () -> "Add assertion message");
        assertEquals("String[]",
                     setter.getParameters().get(0).getClassName(), () -> "Add assertion message");
        assertEquals("this.array = newArray;", setter.getJavaCodeBlock().getExpressions().get(0).toString(), () -> "Add assertion message");


        field = new JavaField("return",
                              "String[]",
                              "http://doc.withannotation.fortest.tools.cxf.apache.org/");
        clz = new JavaClass();
        clz.setFullClassName("org.apache.cxf.tools.fortest.withannotation.doc.jaxws.SayHiResponse");
        getter = clz.appendGetter(field);
        assertEquals("getReturn", getter.getName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("String[]",
                     getter.getReturn().getClassName(), () -> "Add assertion message");
        assertEquals("_return", getter.getReturn().getName(), () -> "Add assertion message");

        setter = clz.appendSetter(field);
        assertEquals("setReturn", setter.getName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("void", setter.getReturn().getClassName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("_return", getter.getReturn().getName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("String[]",
                     setter.getParameters().get(0).getClassName(), () -> "Add assertion message");
    }
}