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

package org.apache.cxf.service.model;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BindingOperationInfoTest {
    private static final String TEST_NS = "urn:test:ns";
    private BindingOperationInfo bindingOperationInfo;

    @Before
    public void setUp() throws Exception {
        OperationInfo operationInfo = new OperationInfo(null, new QName(TEST_NS, "operationTest"));
        MessageInfo inputMessage = operationInfo.createMessage(new QName(
            "http://apache.org/hello_world_soap_http", "testInputMessage"),
            MessageInfo.Type.INPUT);
        operationInfo.setInput("input", inputMessage);

        MessageInfo outputMessage = operationInfo.createMessage(new QName(
            "http://apache.org/hello_world_soap_http", "testOutputMessage"),
            MessageInfo.Type.OUTPUT);
        operationInfo.setOutput("output", outputMessage);
        operationInfo.addFault(new QName(TEST_NS, "fault"), new QName(
            "http://apache.org/hello_world_soap_http", "faultMessage"));
        bindingOperationInfo = new BindingOperationInfo(null, operationInfo);
    }

    @Test
    public void testName() throws Exception {
        assertEquals(bindingOperationInfo.getName(), new QName(TEST_NS, "operationTest"), () -> "Add assertion message");
    }

    @Test
    public void testBinding() throws Exception {
        assertNull(bindingOperationInfo.getBinding(), () -> "Add assertion message");
    }

    @Test
    public void testOperation() throws Exception {
        assertEquals(bindingOperationInfo.getOperationInfo().getName(), new QName(TEST_NS, "operationTest"), () -> "Add assertion message");
        assertTrue(bindingOperationInfo.getOperationInfo().hasInput(), () -> "Add assertion message");
        assertTrue(bindingOperationInfo.getOperationInfo().hasOutput(), () -> "Add assertion message");
        assertEquals(bindingOperationInfo.getOperationInfo().getInputName(), "input", () -> "Add assertion message");
        assertEquals(bindingOperationInfo.getOperationInfo().getOutputName(), "output", () -> "Add assertion message");
        assertEquals(bindingOperationInfo.getFaults().iterator().next().getFaultInfo().getFaultName(),
                     new QName(TEST_NS, "fault"), () -> "Add assertion message");
        Integer magicNumber84009935 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber84009935, bindingOperationInfo.getFaults().size(), () -> "Add assertion message");
    }

    @Test
    public void testInputMessage() throws Exception {
        BindingMessageInfo inputMessage = bindingOperationInfo.getInput();
        assertNotNull(inputMessage, () -> "Add assertion message");
        assertEquals(inputMessage.getMessageInfo().getName().getLocalPart(), "testInputMessage", () -> "Add assertion message");
        assertEquals(inputMessage.getMessageInfo().getName().getNamespaceURI(),
                     "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
    }

    @Test
    public void testOutputMessage() throws Exception {
        BindingMessageInfo outputMessage = bindingOperationInfo.getOutput();
        assertNotNull(outputMessage, () -> "Add assertion message");
        assertEquals(outputMessage.getMessageInfo().getName().getLocalPart(), "testOutputMessage", () -> "Add assertion message");
        assertEquals(outputMessage.getMessageInfo().getName().getNamespaceURI(),
                     "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
    }

    @Test
    public void testFaultMessage() throws Exception {
        BindingFaultInfo faultMessage = bindingOperationInfo.getFaults().iterator().next();
        assertNotNull(faultMessage, () -> "Add assertion message");
        assertEquals(faultMessage.getFaultInfo().getName().getLocalPart(), "faultMessage", () -> "Add assertion message");
        assertEquals(faultMessage.getFaultInfo().getName().getNamespaceURI(),
                     "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
    }
}