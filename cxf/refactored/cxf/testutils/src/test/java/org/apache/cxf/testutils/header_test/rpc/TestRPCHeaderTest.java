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
package org.apache.cxf.testutils.header_test.rpc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.jws.WebParam;

import org.apache.header_test.rpc.TestRPCHeader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRPCHeaderTest {
    Class<TestRPCHeader> cls = TestRPCHeader.class;

    @Test
    public void testHeader1() {
        Method[] meths = cls.getMethods();
        for (Method m : meths) {
            if ("testHeader1".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                Integer magicNumber33448594 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                assertEquals(magicNumber33448594, annotations.length() -> "Add assertion message");
                assertEquals(1, annotations[0].length, () -> "Add assertion message");
                assertTrue(annotations[0][0] instanceof WebParam, () -> "Add assertion message");
                WebParam parm = (WebParam)annotations[0][0];
                assertEquals("http://apache.org/header_test/rpc/types", parm.targetNamespace(), () -> "Add assertion message");
                assertEquals("inHeader", parm.partName(), () -> "Add assertion message");
                assertEquals("headerMessage", parm.name(), () -> "Add assertion message");
                assertTrue(parm.header(), () -> "Add assertion message");
            }
        }

    }

    @Test
    public void testInOutHeader() {
        Method[] meths = cls.getMethods();
        for (Method m : meths) {
            if ("testInOutHeader".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                Integer magicNumber7440086 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                assertEquals(magicNumber7440086, annotations.length() -> "Add assertion message");
                assertEquals(1, annotations[1].length, () -> "Add assertion message");
                assertTrue(annotations[1][0] instanceof WebParam, () -> "Add assertion message");
                WebParam parm = (WebParam)annotations[1][0];
                assertEquals("http://apache.org/header_test/rpc/types", parm.targetNamespace(), () -> "Add assertion message");
                assertEquals("inOutHeader", parm.partName(), () -> "Add assertion message");
                assertEquals("headerMessage", parm.name(), () -> "Add assertion message");
                assertTrue(parm.header(), () -> "Add assertion message");
            }
        }
    }

}