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

package org.apache.cxf.tools.java2wsdl.processor.internal.jaxws;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.cxf.jaxws.JaxwsServiceBuilder;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.tools.common.model.JavaClass;
import org.apache.cxf.tools.common.model.JavaField;
import org.apache.cxf.tools.common.model.JavaMethod;
import org.apache.cxf.tools.fortest.withannotation.doc.GreeterArray;
import org.apache.cxf.tools.fortest.xmllist.AddNumbersPortType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RequestWrapperTest {
    JaxwsServiceBuilder builder = new JaxwsServiceBuilder();

    private OperationInfo getOperation(Class<?> clz, String opName) {
        builder.setServiceClass(clz);
        ServiceInfo serviceInfo = builder.createService();

        for (OperationInfo op : serviceInfo.getInterface().getOperations()) {
            if (op.getUnwrappedOperation() != null
                && op.hasInput() && opName.equals(op.getName().getLocalPart())) {
                return op;
            }
        }
        return null;
    }

    @Test
    public void testBuildRequestFields() {
        // Test String[]
        Class<?> testingClass = GreeterArray.class;
        OperationInfo opInfo = getOperation(testingClass, "sayStringArray");
        assertNotNull(opInfo, () -> "Add assertion message");

        RequestWrapper requestWrapper = new RequestWrapper();

        MessageInfo message = opInfo.getUnwrappedOperation().getInput();
        Method method = (Method)opInfo.getProperty("operation.method");

        List<JavaField> fields = requestWrapper.buildFields(method, message);
        Integer magicNumber83192660 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83192660, fields.size(), () -> "Add assertion message");
        JavaField field = fields.get(0);
        assertEquals("arg0", field.getName(), () -> "Add assertion message");
        assertEquals("String[]", field.getType(), () -> "Add assertion message");

        // Test int[]

        opInfo = getOperation(testingClass, "sayIntArray");
        // Line removed due to Duplicate Assert: assertNotNull(opInfo, () -> "Add assertion message");

        message = opInfo.getUnwrappedOperation().getInput();
        method = (Method) opInfo.getProperty("operation.method");

        fields = requestWrapper.buildFields(method, message);
        // Line removed due to Duplicate Assert: assertEquals(1, fields.size(), () -> "Add assertion message");
        field = fields.get(0);
        // Line removed due to Duplicate Assert: assertEquals("arg0", field.getName(), () -> "Add assertion message");
        assertEquals("int[]", field.getType(), () -> "Add assertion message");

        // Test TestDataBean[]

        opInfo = getOperation(testingClass, "sayTestDataBeanArray");
        // Line removed due to Duplicate Assert: assertNotNull(opInfo, () -> "Add assertion message");

        message = opInfo.getUnwrappedOperation().getInput();
        method = (Method) opInfo.getProperty("operation.method");

        fields = requestWrapper.buildFields(method, message);
        // Line removed due to Duplicate Assert: assertEquals(1, fields.size(), () -> "Add assertion message");
        field = fields.get(0);
        // Line removed due to Duplicate Assert: assertEquals("arg0", field.getName(), () -> "Add assertion message");
        assertEquals("org.apache.cxf.tools.fortest.withannotation.doc.TestDataBean[]", field.getType(), () -> "Add assertion message");
    }

    @Test
    public void testNoAnnotationNoClass() throws Exception {
        String pkgName = "org.apache.cxf.tools.fortest.classnoanno.docwrapped";
        Class<?> testingClass = Class.forName(pkgName + ".Stock");

        OperationInfo opInfo = getOperation(testingClass, "getPrice");
        Wrapper wrapper = new RequestWrapper();
        wrapper.setOperationInfo(opInfo);

        assertTrue(wrapper.isWrapperAbsent(), () -> "Add assertion message");
        assertTrue(wrapper.isToDifferentPackage(), () -> "Add assertion message");
        assertFalse(wrapper.isWrapperBeanClassNotExist(), () -> "Add assertion message");
        assertEquals(pkgName + ".jaxws", wrapper.getJavaClass().getPackageName(), () -> "Add assertion message");
        assertEquals("GetPrice", wrapper.getJavaClass().getName(), () -> "Add assertion message");

        JavaClass jClass = wrapper.buildWrapperBeanClass();
        assertNotNull(jClass, () -> "Add assertion message");
        List<JavaField> jFields = jClass.getFields();

        assertEquals(1, jFields.size(), () -> "Add assertion message");
        assertEquals("arg0", jFields.get(0).getName(), () -> "Add assertion message");
        assertEquals("java.lang.String", jFields.get(0).getClassName(), () -> "Add assertion message");

        List<JavaMethod> jMethods = jClass.getMethods();
        assertEquals(2, jMethods.size(), () -> "Add assertion message");

        JavaMethod jMethod = jMethods.get(0);
        assertEquals("getArg0", jMethod.getName(), () -> "Add assertion message");
        assertTrue(jMethod.getParameterListWithoutAnnotation().isEmpty(), () -> "Add assertion message");

        jMethod = jMethods.get(1);
        assertEquals("setArg0", jMethod.getName(), () -> "Add assertion message");
        Integer magicNumber81563567 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber81563567, jMethod.getParameterListWithoutAnnotation().size(), () -> "Add assertion message");
        assertEquals("java.lang.String newArg0", jMethod.getParameterListWithoutAnnotation().get(0), () -> "Add assertion message");
    }

    @Test
    public void testWithAnnotationNoClass() throws Exception {
        String pkgName = "org.apache.cxf.tools.fortest.withannotation.doc";
        Class<?> testingClass = Class.forName(pkgName + ".Stock");

        OperationInfo opInfo = getOperation(testingClass, "getPrice");
        Wrapper wrapper = new RequestWrapper();
        wrapper.setOperationInfo(opInfo);

        assertFalse(wrapper.isWrapperAbsent(), () -> "Add assertion message");
        assertTrue(wrapper.isToDifferentPackage(), () -> "Add assertion message");
        assertFalse(wrapper.isWrapperBeanClassNotExist(), () -> "Add assertion message");
        assertEquals(pkgName + ".jaxws", wrapper.getJavaClass().getPackageName(), () -> "Add assertion message");
        assertEquals("GetPrice", wrapper.getJavaClass().getName(), () -> "Add assertion message");
    }

    @Test
    public void testWithAnnotationWithClass() throws Exception {
        String pkgName = "org.apache.cxf.tools.fortest.withannotation.doc";
        Class<?> testingClass = Class.forName(pkgName + ".Greeter");

        OperationInfo opInfo = getOperation(testingClass, "sayHi");

        Wrapper wrapper = new RequestWrapper();
        wrapper.setOperationInfo(opInfo);

        assertFalse(wrapper.isWrapperAbsent(), () -> "Add assertion message");
        assertTrue(wrapper.isToDifferentPackage(), () -> "Add assertion message");
        assertFalse(wrapper.isWrapperBeanClassNotExist(), () -> "Add assertion message");
        assertEquals(pkgName, wrapper.getJavaClass().getPackageName(), () -> "Add assertion message");
        assertEquals("SayHi", wrapper.getJavaClass().getName(), () -> "Add assertion message");
    }

    @Test
    public void testCXF1752() throws Exception {
        OperationInfo opInfo = getOperation(AddNumbersPortType.class, "testCXF1752");
        RequestWrapper wrapper = new RequestWrapper();
        wrapper.setOperationInfo(opInfo);

        wrapper.buildWrapperBeanClass();
        List<JavaField> fields = wrapper.getJavaClass().getFields();
        assertEquals(6, fields.size(), () -> "Add assertion message");
        assertEquals("java.util.List<java.lang.Long>", fields.get(0).getClassName(), () -> "Add assertion message");
        assertEquals("byte[]", fields.get(2).getClassName(), () -> "Add assertion message");
        assertEquals("org.apache.cxf.tools.fortest.xmllist.AddNumbersPortType.UserObject[]",
                     fields.get(3).getClassName(), () -> "Add assertion message");
        assertEquals("java.util.List<org.apache.cxf.tools.fortest.xmllist.AddNumbersPortType.UserObject>",
                     fields.get(4).getClassName(), () -> "Add assertion message");
    }
}