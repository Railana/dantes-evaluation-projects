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

package org.apache.cxf.jaxws.handler;

import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class JavaxAnnotationHandlerChainBuilderTest {

    @Before
    public void setUp() {
    }

    @Test
    public void testFindHandlerChainAnnotation() {
        JavaxHandlerTestImpl handlerTestImpl = new JavaxHandlerTestImpl();
        AnnotationHandlerChainBuilder chainBuilder = new AnnotationHandlerChainBuilder();
        @SuppressWarnings("rawtypes")
        List<Handler> handlers = chainBuilder
            .buildHandlerChainFromClass(handlerTestImpl.getClass(),
                                        null,
                                        null,
                                        null);
        assertNotNull(handlers, () -> "Add assertion message");
        Integer magicNumber28617807 = 9; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber28617807, handlers.size(), () -> "Add assertion message");
        assertEquals(TestLogicalHandler.class, handlers.get(0).getClass(), () -> "Add assertion message");
        assertEquals(TestLogicalHandler.class, handlers.get(1).getClass(), () -> "Add assertion message");
        assertEquals(TestLogicalHandler.class, handlers.get(2).getClass(), () -> "Add assertion message");
        assertEquals(TestLogicalHandler.class, handlers.get(3).getClass(), () -> "Add assertion message");
        assertEquals(TestLogicalHandler.class, handlers.get(4).getClass(), () -> "Add assertion message");
        assertEquals(TestLogicalHandler.class, handlers.get(5).getClass(), () -> "Add assertion message");
        assertEquals(TestLogicalHandler.class, handlers.get(6).getClass(), () -> "Add assertion message");
        assertEquals(TestProtocolHandler.class, handlers.get(7).getClass(), () -> "Add assertion message");
        assertEquals(TestProtocolHandler.class, handlers.get(8).getClass(), () -> "Add assertion message");
    }

    @Test
    public void testFindHandlerChainAnnotationPerPortServiceBinding() {
        JavaxHandlerTestImpl handlerTestImpl = new JavaxHandlerTestImpl();
        AnnotationHandlerChainBuilder chainBuilder = new AnnotationHandlerChainBuilder();
        QName portQName = new QName("namespacedoesntsupportyet", "SoapPort1");
        QName serviceQName = new QName("namespacedoesntsupportyet", "SoapService1");
        String bindingID = "http://schemas.xmlsoap.org/wsdl/soap/http";
        @SuppressWarnings("rawtypes")
        List<Handler> handlers = chainBuilder
            .buildHandlerChainFromClass(handlerTestImpl.getClass(), portQName, serviceQName, bindingID);
        assertNotNull(handlers, () -> "Add assertion message");
        assertEquals(5, handlers.size(), () -> "Add assertion message");
    }

    @Test
    public void testFindHandlerChainAnnotationPerPortServiceBindingNegative() {
        JavaxHandlerTestImpl handlerTestImpl = new JavaxHandlerTestImpl();
        AnnotationHandlerChainBuilder chainBuilder = new AnnotationHandlerChainBuilder();
        QName portQName = new QName("namespacedoesntsupportyet", "SoapPortUnknown");
        QName serviceQName = new QName("namespacedoesntsupportyet", "SoapServiceUnknown");
        String bindingID = "BindingUnknow";
        @SuppressWarnings("rawtypes")
        List<Handler> handlers = chainBuilder
            .buildHandlerChainFromClass(handlerTestImpl.getClass(), portQName, serviceQName, bindingID);
        assertNotNull(handlers, () -> "Add assertion message");
        assertEquals(3, handlers.size(), () -> "Add assertion message");
    }

    @Test
    public void testFindHandlerChainAnnotationPerPortServiceBindingWildcard() {
        JavaxHandlerTestImpl handlerTestImpl = new JavaxHandlerTestImpl();
        AnnotationHandlerChainBuilder chainBuilder = new AnnotationHandlerChainBuilder();
        QName portQName = new QName("http://apache.org/handler_test", "SoapPortWildcard");
        QName serviceQName = new QName("http://apache.org/handler_test", "SoapServiceWildcard");
        String bindingID = "BindingUnknow";
        @SuppressWarnings("rawtypes")
        List<Handler> handlers = chainBuilder
            .buildHandlerChainFromClass(handlerTestImpl.getClass(), portQName, serviceQName, bindingID);
        assertNotNull(handlers, () -> "Add assertion message");
        assertEquals(7, handlers.size(), () -> "Add assertion message");
    }

    public static class TestLogicalHandler implements LogicalHandler<LogicalMessageContext> {
        boolean initCalled;

        public void close(MessageContext arg0) {
        }

        public boolean handleFault(LogicalMessageContext arg0) {
            return false;
        }

        public boolean handleMessage(LogicalMessageContext arg0) {
            return false;
        }
    }

    public static class TestProtocolHandler implements Handler<MessageContext> {

        public void close(MessageContext arg0) {
        }

        public boolean handleFault(MessageContext arg0) {
            return false;
        }

        public boolean handleMessage(MessageContext arg0) {
            return false;
        }
    }

    @WebService()
    @HandlerChain(file = "./javax-handlers.xml", name = "TestHandlerChain")
    public class JavaxHandlerTestImpl {

    }
}