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



import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.jaxws.handler.logical.LogicalMessageContextImpl;
import org.apache.cxf.jaxws.handler.soap.SOAPMessageContextImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HandlerChainInvokerTest {

    private static final int HANDLER_COUNT = 4;

    HandlerChainInvoker invoker;
    Message message;
    LogicalMessageContext lmc;
    MessageContext pmc;

    TestLogicalHandler[] logicalHandlers = new TestLogicalHandler[HANDLER_COUNT];
    TestProtocolHandler[] protocolHandlers = new TestProtocolHandler[HANDLER_COUNT];

    @Before
    public void setUp() {
        AbstractHandlerBase.clear();

        @SuppressWarnings("rawtypes")
        List<Handler> handlers = new ArrayList<>();
        for (int i = 0; i < logicalHandlers.length; i++) {
            logicalHandlers[i] = new TestLogicalHandler();
            handlers.add(logicalHandlers[i]);
        }
        for (int i = 0; i < protocolHandlers.length; i++) {
            protocolHandlers[i] = new TestProtocolHandler();
            handlers.add(protocolHandlers[i]);
        }

        invoker = new HandlerChainInvoker(handlers);

        // Removed line due to General Fixture smell: message = new MessageImpl();
        Exchange e = new ExchangeImpl();
        message.setExchange(e);
        // Removed line due to General Fixture smell: lmc = new LogicalMessageContextImpl(message);
        // Removed line due to General Fixture smell: pmc = new WrappedMessageContext(message);
/*
        payload = new DOMSource();
        message.setContent(Source.class, payload);*/

    }

    @Test
    public void testInvokeEmptyHandlerChain() {
        pmc = new WrappedMessageContext(message);
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        invoker = new HandlerChainInvoker(new ArrayList<>());
        assertTrue(invoker.invokeLogicalHandlers(false, lmc), () -> "Add assertion message");
        assertTrue(invoker.invokeProtocolHandlers(false, pmc), () -> "Add assertion message");
    }

    @Test
    public void testHandlerPartitioning() {
        message = new MessageImpl();

        assertEquals(HANDLER_COUNT, invoker.getLogicalHandlers().size(), () -> "Add assertion message");
        for (Handler<?> h : invoker.getLogicalHandlers()) {
            assertTrue(h instanceof LogicalHandler, () -> "Add assertion message");
        }

        assertEquals(HANDLER_COUNT, invoker.getProtocolHandlers().size(), () -> "Add assertion message");
        for (Handler<?> h : invoker.getProtocolHandlers()) {
            assertFalse(h instanceof LogicalHandler, () -> "Add assertion message");
        }

    }

    @Test
    public void testInvokeHandlersInbound() {
        message = new MessageImpl();

        invoker.setInbound();
        assertTrue(invoker.isInbound(), () -> "Add assertion message");
        checkProtocolHandlersInvoked(false);

        Integer magicNumber95665668 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber95665668, invoker.getInvokedHandlers().size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(invoker.isInbound(), () -> "Add assertion message");

        checkLogicalHandlersInvoked(false, true);
        assertEquals(8, invoker.getInvokedHandlers().size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(invoker.isInbound(), () -> "Add assertion message");

        assertFalse(invoker.isClosed(), () -> "Add assertion message");
        assertTrue(logicalHandlers[0].getInvokeOrderOfHandleMessage() > logicalHandlers[1]
            .getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage() > protocolHandlers[0]
            .getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(protocolHandlers[0].getInvokeOrderOfHandleMessage() > protocolHandlers[1]
            .getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerReturnFalseOutboundResponseExpected() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();

        Integer magicNumber76850072 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber76850072, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");

        assertTrue(invoker.isOutbound(), () -> "Add assertion message");

        // invoke the handlers.  when a handler returns false, processing
        // of handlers is stopped and message direction is reversed.
        logicalHandlers[1].setHandleMessageRet(false);

        boolean ret = invoker.invokeLogicalHandlers(false, lmc);

        assertFalse(ret, () -> "Add assertion message");
        assertFalse(invoker.isClosed(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(invoker.isInbound(), () -> "Add assertion message");

        // the next time invokeHandler is invoked, the 'next' handler is invoked.
        // As message direction has been reversed this means the that the previous
        // one on the list is actually invoked.
        logicalHandlers[1].setHandleMessageRet(true);

        ret = invoker.invokeLogicalHandlers(false, lmc);
        assertTrue(ret, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(invoker.isClosed(), () -> "Add assertion message");
        assertEquals(2, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(invoker.isInbound(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerInboundProcessingStoppedResponseExpected() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();

        assertEquals(0, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");

        invoker.setInbound();

        logicalHandlers[1].setHandleMessageRet(false);
        boolean ret = invoker.invokeLogicalHandlers(false, lmc);
        assertFalse(invoker.isClosed(), () -> "Add assertion message");

        assertFalse(ret, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(invoker.isOutbound(), () -> "Add assertion message");
    }

    @Test
    public void testHandleMessageReturnsFalseOutbound() {
        pmc = new WrappedMessageContext(message);
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        protocolHandlers[2].setHandleMessageRet(false);

        assertTrue(invoker.isOutbound(), () -> "Add assertion message");

        invoker.setLogicalMessageContext(lmc);
        boolean continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
        assertTrue(continueProcessing, () -> "Add assertion message");
        invoker.setProtocolMessageContext(pmc);
        continueProcessing = invoker.invokeProtocolHandlers(false, pmc);

        assertFalse((Boolean)pmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");
        assertFalse((Boolean)lmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");
        assertTrue(invoker.isInbound(), () -> "Add assertion message");
        assertFalse(continueProcessing, () -> "Add assertion message");

        protocolHandlers[2].setHandleMessageRet(true);
        invoker.setProtocolMessageContext(pmc);
        continueProcessing = invoker.invokeProtocolHandlers(false, pmc);
        // Line removed due to Duplicate Assert: assertTrue(continueProcessing, () -> "Add assertion message");
        invoker.setLogicalMessageContext(lmc);
        continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
        // Line removed due to Duplicate Assert: assertTrue(continueProcessing, () -> "Add assertion message");


        Integer magicNumber24187501 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24187501, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber75348558 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber75348558, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber70458894 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber70458894, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(2, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(2, protocolHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(2, protocolHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[3].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[2].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[1].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[0].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(protocolHandlers[0].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[3].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(protocolHandlers[2].getInvokeOrderOfHandleMessage()
                   < protocolHandlers[1].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        Integer magicNumber84906350 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber84906350, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber66681340 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber66681340, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber46972908 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber46972908, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber35859381 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber35859381, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");

        assertEquals(0, protocolHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
    }

    @Test
    public void testHandleMessageThrowsProtocolExceptionOutbound() {
        pmc = new WrappedMessageContext(message);
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        message = new SoapMessage(message);
        lmc = new LogicalMessageContextImpl(message);
        pmc = new WrappedMessageContext(message);

        ProtocolException pe = new ProtocolException("banzai");
        protocolHandlers[2].setException(pe);

        invoker.setRequestor(true);
        assertTrue(invoker.isOutbound(), () -> "Add assertion message");

        invoker.setLogicalMessageContext(lmc);
        boolean continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
        assertTrue(continueProcessing, () -> "Add assertion message");

        //create an empty SOAP body for testing
        try {
            pmc = new SOAPMessageContextImpl(message);
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage soapMessage = factory.createMessage();
            ((SOAPMessageContext)pmc).setMessage(soapMessage);
        } catch (SOAPException e) {
            //do nothing
        }

        assertThrows(ProtocolException e,
        		() -> {
        			invoker.setProtocolMessageContext(pmc);
        			invoker.invokeProtocolHandlers(false, pmc);
        			fail("did not get expected exception");
        		});

        assertFalse((Boolean)pmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");
        assertFalse((Boolean)lmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");
        assertTrue(invoker.isInbound(), () -> "Add assertion message");

        //the message is replaced by fault message
        Source responseMessage = lmc.getMessage().getPayload();
        //System.out.println(getSourceAsString(responseMessage));
        assertTrue(getSourceAsString(responseMessage).indexOf("banzai") > -1, () -> "Add assertion message");

        //assertFalse(continueProcessing);

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber7105382 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber7105382, protocolHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber96129994 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber96129994, protocolHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber94282660 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber94282660, protocolHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[3].getInvokeOrderOfHandleMessage()
                   < protocolHandlers[0].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(protocolHandlers[1].getInvokeOrderOfHandleMessage()
                   < protocolHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber8045901 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber8045901, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");

        Integer magicNumber60008571 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber60008571, protocolHandlers[0].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber10888327 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber10888327, protocolHandlers[1].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber56561443 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber56561443, protocolHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[3].getCloseCount(), () -> "Add assertion message");

        assertTrue(protocolHandlers[2].getInvokeOrderOfClose()
                   < protocolHandlers[1].getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(protocolHandlers[0].getInvokeOrderOfClose()
                   < logicalHandlers[3].getInvokeOrderOfClose(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");

        assertEquals(1, protocolHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[3].getHandleFaultCount(), () -> "Add assertion message");

        assertTrue(protocolHandlers[0].getInvokeOrderOfHandleFault()
                   < logicalHandlers[3].getInvokeOrderOfHandleFault(), () -> "Add assertion message");
        assertTrue(protocolHandlers[2].getInvokeOrderOfHandleFault()
                   < protocolHandlers[1].getInvokeOrderOfHandleFault(), () -> "Add assertion message");
    }

    @Test
    public void testHandleFaultReturnsFalseOutbound() {
        pmc = new WrappedMessageContext(message);
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        ProtocolException pe = new ProtocolException("banzai");
        protocolHandlers[2].setException(pe);
        protocolHandlers[0].setHandleFaultRet(false);

        invoker.setRequestor(true);
        assertTrue(invoker.isOutbound(), () -> "Add assertion message");

        invoker.setLogicalMessageContext(lmc);
        boolean continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
        assertTrue(continueProcessing, () -> "Add assertion message");

        try {
            invoker.setProtocolMessageContext(pmc);
            invoker.invokeProtocolHandlers(false, pmc);
            fail("did not get expected exception");
        } catch (ProtocolException e) {
            assertEquals("banzai", e.getMessage(), () -> "Add assertion message");
        }

        assertFalse((Boolean)pmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");
        assertFalse((Boolean)lmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");
        assertTrue(invoker.isInbound(), () -> "Add assertion message");
        //assertFalse(continueProcessing);

        Integer magicNumber64481471 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber64481471, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber60625465 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber60625465, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber93419384 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber93419384, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[3].getInvokeOrderOfHandleMessage()
                   < protocolHandlers[0].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(protocolHandlers[1].getInvokeOrderOfHandleMessage()
                   < protocolHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber78773217 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber78773217, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        Integer magicNumber40077781 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber40077781, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");

        assertEquals(1, protocolHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, protocolHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[3].getCloseCount(), () -> "Add assertion message");

        assertTrue(protocolHandlers[2].getInvokeOrderOfClose()
                   < protocolHandlers[1].getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(protocolHandlers[0].getInvokeOrderOfClose()
                   < logicalHandlers[3].getInvokeOrderOfClose(), () -> "Add assertion message");

        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");

        assertEquals(1, protocolHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        Integer magicNumber81977351 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber81977351, protocolHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        Integer magicNumber66687233 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber66687233, protocolHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, protocolHandlers[3].getHandleFaultCount(), () -> "Add assertion message");

        assertTrue(protocolHandlers[2].getInvokeOrderOfHandleFault()
                   < protocolHandlers[1].getInvokeOrderOfHandleFault(), () -> "Add assertion message");
    }

    @Test
    public void testHandleMessageReturnsTrue() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        logicalHandlers[0].setHandleMessageRet(true);
        logicalHandlers[1].setHandleMessageRet(true);
        logicalHandlers[2].setHandleMessageRet(true);
        logicalHandlers[3].setHandleMessageRet(true);

        boolean continueProcessing = invoker.invokeLogicalHandlers(false, lmc);

        assertTrue(continueProcessing, () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[0].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[1].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[2].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[3].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");
    }

    //JAX-WS 9.3.2.1:
    //Return false This indicates that normal message processing should cease. Subsequent actions
    //depend on whether the message exchange pattern (MEP) in use requires a response to the
    //message currently being processed or not:
    //Response The message direction is reversed, the runtime invokes handleMessage on the next
    //handler or dispatches the message (see section 9.1.2.2) if there are no further handlers.
    @Test
    public void testHandleMessageReturnsFalseWithResponseExpected() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        logicalHandlers[0].setHandleMessageRet(true);
        logicalHandlers[1].setHandleMessageRet(true);
        logicalHandlers[2].setHandleMessageRet(false);
        logicalHandlers[3].setHandleMessageRet(true);
        invoker.setResponseExpected(true);

        invoker.setLogicalMessageContext(lmc);
        boolean continueProcessing = invoker.invokeLogicalHandlers(false, lmc);

        assertFalse(continueProcessing, () -> "Add assertion message");

        assertFalse((Boolean)lmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");

        Integer magicNumber34151700 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber34151700, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");

        logicalHandlers[2].setHandleMessageRet(true);
        invoker.invokeLogicalHandlers(false, lmc);

        assertEquals(2, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(2, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[3].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[2].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[1].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[0].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(0, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        Integer magicNumber18313310 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber18313310, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        Integer magicNumber2375296 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber2375296, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
    }

    //JAX-WS 9.3.2.1:
    //Return false This indicates that normal message processing should cease. Subsequent actions
    //depend on whether the message exchange pattern (MEP) in use requires a response to the
    //message currently being processed or not:
    //No response Normal message processing stops, close is called on each previously invoked handler
    //in the chain, the message is dispatched
    @Test
    public void testHandleMessageReturnsFalseWithNoResponseExpected() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        logicalHandlers[0].setHandleMessageRet(true);
        logicalHandlers[1].setHandleMessageRet(true);
        logicalHandlers[2].setHandleMessageRet(false);
        logicalHandlers[3].setHandleMessageRet(true);
        invoker.setResponseExpected(false);

        boolean continueProcessing = invoker.invokeLogicalHandlers(false, lmc);

        assertFalse(continueProcessing, () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[0].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[1].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");

        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");
    }

    //JAX-WS 9.3.2.1:
    //Throw ProtocolException or a subclass This indicates that normal message processing should cease.
    //Subsequent actions depend on whether the MEP in use requires a response to the message currently
    //being processed or not:
    //Response Normal message processing stops, fault message processing starts. The message direction
    //is reversed, if the message is not already a fault message then it is replaced with a fault message,
    //and the runtime invokes handleFault on the next handler or dispatches the message (see
    //section 9.1.2.2) if there are no further handlers.
    @Test
    public void testHandleMessageThrowsProtocolExceptionWithResponseExpected() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        ProtocolException pe = new ProtocolException("banzai");
        logicalHandlers[2].setException(pe);

        invoker.setRequestor(true);

        //boolean continueProcessing = true;
        try {
            invoker.setLogicalMessageContext(lmc);
            invoker.invokeLogicalHandlers(false, lmc);
            fail("did not get expected exception");
        } catch (ProtocolException e) {
            assertEquals("banzai", e.getMessage(), () -> "Add assertion message");
        }

        assertTrue(invoker.faultRaised(), () -> "Add assertion message");
        //assertFalse(continueProcessing);
        //assertTrue(invoker.isClosed());
        assertSame(pe, invoker.getFault(), () -> "Add assertion message");

        assertFalse((Boolean)lmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleFault()
                   < logicalHandlers[0].getInvokeOrderOfHandleFault(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");
/*

        continueProcessing = invoker.invokeLogicalHandlers(false, lmc);

        assertFalse(continueProcessing);
        assertTrue(invoker.faultRaised());
        assertTrue(invoker.isClosed());
        assertSame(pe, invoker.getFault());

        assertEquals(1, logicalHandlers[0].getHandleMessageCount());
        assertEquals(1, logicalHandlers[1].getHandleMessageCount());
        assertEquals(1, logicalHandlers[0].getHandleFaultCount());
        assertEquals(0, logicalHandlers[1].getHandleFaultCount());*/
    }

    //JAX-WS 9.3.2.1:
    //Throw ProtocolException or a subclass This indicates that normal message processing should cease.
    //Subsequent actions depend on whether the MEP in use requires a response to the message currently
    //being processed or not:
    //No response Normal message processing stops, close is called on each previously invoked handler
    //in the chain, the exception is dispatched
    @Test
    public void testHandleMessageThrowsProtocolExceptionWithNoResponseExpected() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        ProtocolException pe = new ProtocolException("banzai");
        logicalHandlers[2].setException(pe);
        invoker.setResponseExpected(false);
        invoker.setRequestor(true);

        //boolean continueProcessing = true;
        try {
            invoker.invokeLogicalHandlers(false, lmc);
            //don't fail.  TCK says this shouldn't be thrown.
            //fail("did not get expected exception");
        } catch (ProtocolException e) {
            assertEquals("banzai", e.getMessage(), () -> "Add assertion message");
        }
        assertTrue(invoker.faultRaised(), () -> "Add assertion message");
        //assertFalse(continueProcessing);
        //assertTrue(invoker.isClosed());
        assertSame(pe, invoker.getFault(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfClose()
                   < logicalHandlers[0].getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    //Throw any other runtime exception This indicates that normal message processing should cease.
    //Subsequent actions depend on whether the MEP in use includes a response to the message currently being
    //processed or not:
    //Response Normal message processing stops, close is called on each previously invoked handler in
    //the chain, the message direction is reversed, and the exception is dispatched
    @Test
    public void testHandleMessageThrowsRuntimeExceptionWithResponseExpected() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        RuntimeException re = new RuntimeException("banzai");
        logicalHandlers[1].setException(re);
        invoker.setRequestor(true);

        //boolean continueProcessing = true;
        try {
            invoker.invokeLogicalHandlers(false, lmc);
            fail("did not get expected exception");
        } catch (RuntimeException e) {
            assertEquals("banzai", e.getMessage(), () -> "Add assertion message");
        }

        //assertTrue(invoker.faultRaised());
        //assertFalse(continueProcessing);
        assertTrue(invoker.isClosed(), () -> "Add assertion message");
        //assertSame(re, invoker.getFault());

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[0].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[1].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");
    }

    @Test
    public void testFaultRaised() {
        message = new MessageImpl();

        assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        invoker.setFault(new ProtocolException("test exception"));
        assertTrue(invoker.faultRaised(), () -> "Add assertion message");

        // reset
        invoker.setFault(null);
        // Line removed due to Duplicate Assert: assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        invoker.setFault(true);
        // Line removed due to Duplicate Assert: assertTrue(invoker.faultRaised(), () -> "Add assertion message");

        // reset
        invoker.setFault(false);
        invoker.setFault(null);
        // Line removed due to Duplicate Assert: assertFalse(invoker.faultRaised(), () -> "Add assertion message");

        invoker.setFault(true);
        invoker.setFault(new ProtocolException("test exception"));
    }

    // JAXB spec 9.3.2.2: Throw ProtocolException or a subclass This indicates
    // that fault message processing should cease. Fault message processing
    // stops, close is called on each previously invoked handler in the chain, the
    // exception is dispatched
    @Test
    public void testHandleFaultThrowsProtocolException() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        ProtocolException pe = new ProtocolException("banzai");
        ProtocolException pe2 = new ProtocolException("banzai2");
        // throw exception during handleFault processing
        logicalHandlers[2].setException(pe);
        logicalHandlers[1].setFaultException(pe2);
        invoker.setRequestor(true);

        boolean continueProcessing = false;
        try {
            continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
            fail("did not get expected exception");
        } catch (ProtocolException e) {
            assertEquals("banzai2", e.getMessage(), () -> "Add assertion message");
        }

        assertFalse(continueProcessing, () -> "Add assertion message");
        assertTrue(invoker.isClosed(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleMessage()
                   < logicalHandlers[2].getInvokeOrderOfHandleMessage(), () -> "Add assertion message");

        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[2].getInvokeOrderOfClose()
                   < logicalHandlers[1].getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfClose()
                   < logicalHandlers[0].getInvokeOrderOfClose(), () -> "Add assertion message");

    }

    // JAXB spec 9.3.2.2: Throw any other runtime exception This indicates
    // that fault message processing should cease. Fault message processing stops,
    // close is called on each previously invoked handler in the chain, the exception is
    // dispatched
    @Test
    public void testHandleFaultThrowsRuntimeException() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        ProtocolException pe = new ProtocolException("banzai");
        RuntimeException re = new RuntimeException("banzai");
        // throw exception during handleFault processing
        logicalHandlers[2].setException(pe);
        logicalHandlers[1].setFaultException(re);
        invoker.setRequestor(true);


        boolean continueProcessing = false;
        try {
            continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
            fail("did not get expected exception");
        } catch (RuntimeException e) {
            assertEquals("banzai", e.getMessage(), () -> "Add assertion message");
        }

        assertFalse(continueProcessing, () -> "Add assertion message");
        assertTrue(invoker.isClosed(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");

        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[2].getInvokeOrderOfClose()
                   < logicalHandlers[1].getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfClose()
                   < logicalHandlers[0].getInvokeOrderOfClose(), () -> "Add assertion message");
    }


    //JAXB spec 9.3.2.2: Return true This indicates that fault message processing
    //should continue. The runtime invokes handle Fault on the next handler or dispatches
    //the fault message (see section 9.1.2.2) if there are no further handlers.
    @Test
    public void testHandleFaultReturnsTrue() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        ProtocolException pe = new ProtocolException("banzai");
        logicalHandlers[2].setException(pe);
        invoker.setRequestor(true);

        logicalHandlers[0].setHandleFaultRet(true);
        logicalHandlers[1].setHandleFaultRet(true);
        logicalHandlers[2].setHandleFaultRet(true);
        logicalHandlers[3].setHandleFaultRet(true);

        boolean continueProcessing = false;
        try {
            continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
            fail("did not get expected exception");
        } catch (RuntimeException e) {
            assertEquals("banzai", e.getMessage(), () -> "Add assertion message");
        }

        assertFalse(continueProcessing, () -> "Add assertion message");
        //assertTrue(invoker.isClosed());

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfHandleFault()
                   < logicalHandlers[0].getInvokeOrderOfHandleFault(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");
    }


    //JAXB spec 9.3.2.2: Return false This indicates that fault message processing
    //should cease. Fault message processing stops, close is called on each previously invoked
    //handler in the chain, the fault message is dispatched
    @Test
    public void testHandleFaultReturnsFalse() {
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();
        ProtocolException pe = new ProtocolException("banzai");
        logicalHandlers[3].setException(pe);
        invoker.setRequestor(true);

        logicalHandlers[0].setHandleFaultRet(true);
        logicalHandlers[1].setHandleFaultRet(true);
        logicalHandlers[2].setHandleFaultRet(false);
        logicalHandlers[3].setHandleFaultRet(true);

        boolean continueProcessing = false;
        try {
            continueProcessing = invoker.invokeLogicalHandlers(false, lmc);
            fail("did not get expected exception");
        } catch (RuntimeException e) {
            assertEquals("banzai", e.getMessage(), () -> "Add assertion message");
        }

        assertFalse(continueProcessing, () -> "Add assertion message");
        //assertTrue(invoker.isClosed());

        assertEquals(1, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleMessageCount(), () -> "Add assertion message");
        Integer magicNumber85623527 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber85623527, logicalHandlers[3].getHandleMessageCount(), () -> "Add assertion message");


        assertEquals(0, logicalHandlers[0].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[1].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getHandleFaultCount(), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[3].getHandleFaultCount(), () -> "Add assertion message");

        assertEquals(1, logicalHandlers[0].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[2].getCloseCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[3].getCloseCount(), () -> "Add assertion message");

        assertTrue(logicalHandlers[3].getInvokeOrderOfClose()
                   < logicalHandlers[2].getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(logicalHandlers[2].getInvokeOrderOfClose()
                   < logicalHandlers[1].getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(logicalHandlers[1].getInvokeOrderOfClose()
                   < logicalHandlers[0].getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testMEPComplete() {
        pmc = new WrappedMessageContext(message);
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();

        invoker.invokeLogicalHandlers(false, lmc);
        invoker.invokeProtocolHandlers(false, pmc);
        assertEquals(8, invoker.getInvokedHandlers().size(), () -> "Add assertion message");

        invoker.mepComplete(message);

        assertTrue("close not invoked on logicalHandlers", logicalHandlers[0].isCloseInvoked());
        assertTrue("close not invoked on logicalHandlers", logicalHandlers[1].isCloseInvoked());
        assertTrue("close not invoked on protocolHandlers", protocolHandlers[0].isCloseInvoked());
        assertTrue("close not invoked on protocolHandlers", protocolHandlers[1].isCloseInvoked());

        assertTrue("incorrect invocation order of close", protocolHandlers[1].getInvokeOrderOfClose()
                   < protocolHandlers[0].getInvokeOrderOfClose());
        assertTrue("incorrect invocation order of close", protocolHandlers[0].getInvokeOrderOfClose()
                   < logicalHandlers[1].getInvokeOrderOfClose());
        assertTrue("incorrect invocation order of close", logicalHandlers[1].getInvokeOrderOfClose()
                   < logicalHandlers[0].getInvokeOrderOfClose());
    }


    @Test
    public void testResponseExpectedDefault() {
        message = new MessageImpl();
        assertTrue(invoker.isResponseExpected(), () -> "Add assertion message");
    }

    /* test invoking logical handlers when processing has been aborted
     * with both protocol and logical handlers in invokedHandlers list.
     *
     */
    @Test
    public void testInvokedAlreadyInvokedMixed() {
        pmc = new WrappedMessageContext(message);
        lmc = new LogicalMessageContextImpl(message);
        message = new MessageImpl();

        // simulate an invocation being aborted by a logical handler
        //
        logicalHandlers[1].setHandleMessageRet(false);
        invoker.setInbound();
        invoker.invokeProtocolHandlers(true, pmc);
        invoker.invokeLogicalHandlers(true, lmc);

        assertEquals(7, invoker.getInvokedHandlers().size(), () -> "Add assertion message");
//        assertFalse(invoker.getInvokedHandlers().contains(logicalHandlers[1]));
        assertTrue(invoker.getInvokedHandlers().contains(protocolHandlers[0]), () -> "Add assertion message");
        assertTrue(invoker.getInvokedHandlers().contains(protocolHandlers[1]), () -> "Add assertion message");
        assertEquals(0, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");

        logicalHandlers[1].setHandleMessageRet(true);
        invoker.invokeLogicalHandlers(true, lmc);
        invoker.invokeProtocolHandlers(true, pmc);


        assertEquals(2, protocolHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        assertEquals(2, protocolHandlers[1].getHandleMessageCount(), () -> "Add assertion message");

        // Line removed due to Duplicate Assert: assertEquals(1, logicalHandlers[1].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, logicalHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2, protocolHandlers[0].getHandleMessageCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2, protocolHandlers[1].getHandleMessageCount(), () -> "Add assertion message");

    }

    /*public void testHandlerReturnFalse() {
        pmc = new WrappedMessageContext(message);
        lmc = new LogicalMessageContextImpl(message);
        logicalHandlers[1].setHandleMessageRet(false);
        invoker.setInbound();
        doInvokeProtocolHandlers(true);
        invoker.invokeLogicalHandlers(true, lmc);

    }*/

    protected void checkLogicalHandlersInvoked(boolean outboundProperty, boolean requestorProperty) {

        invoker.invokeLogicalHandlers(requestorProperty, lmc);

        assertTrue("handler not invoked", logicalHandlers[0].isHandleMessageInvoked());
        assertTrue("handler not invoked", logicalHandlers[1].isHandleMessageInvoked());
        assertTrue(invoker.getInvokedHandlers().contains(logicalHandlers[0]));
        assertTrue(invoker.getInvokedHandlers().contains(logicalHandlers[1]));
    }

    protected void checkProtocolHandlersInvoked(boolean outboundProperty) {

        invoker.invokeProtocolHandlers(false, pmc);

        assertTrue("handler not invoked", protocolHandlers[0].isHandleMessageInvoked());
        assertTrue("handler not invoked", protocolHandlers[1].isHandleMessageInvoked());

        assertTrue(invoker.getInvokedHandlers().contains(protocolHandlers[0]));
        assertTrue(invoker.getInvokedHandlers().contains(protocolHandlers[1]));
    }

    private String getSourceAsString(Source s) {
        String result = "";

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            OutputStream out = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult();
            streamResult.setOutputStream(out);
            transformer.transform(s, streamResult);
            return streamResult.getOutputStream().toString();
        } catch (Exception e) {
            //do nothing
        }
        return result;
    }

    static class TestProtocolHandler extends AbstractHandlerBase<SOAPMessageContext> {

    }

    static class TestLogicalHandler extends AbstractHandlerBase<LogicalMessageContextImpl>
        implements LogicalHandler<LogicalMessageContextImpl> {

    }

    static class AbstractHandlerBase<T extends MessageContext> implements Handler<T> {

        private static int sinvokedOrder;
        private static int sid;

        private int invokeOrderOfHandleMessage;
        private int invokeOrderOfHandleFault;
        private int invokeOrderOfClose;
        private final int id = ++sid;

        private int handleMessageInvoked;
        private int handleFaultInvoked;
        private boolean handleMessageRet = true;
        private boolean handleFaultRet = true;
        private RuntimeException exception;
        private RuntimeException faultException;

        private int closeInvoked;

        public void reset() {
            handleMessageInvoked = 0;
            handleFaultInvoked = 0;
            handleMessageRet = true;
        }

        public boolean handleMessage(T arg0) {
            invokeOrderOfHandleMessage = ++sinvokedOrder;
            handleMessageInvoked++;

            if (exception != null) {
                RuntimeException e = exception;
                exception = null;
                throw e;
            }

            return handleMessageRet;
        }

        public boolean handleFault(T arg0) {
            invokeOrderOfHandleFault = ++sinvokedOrder;
            handleFaultInvoked++;

            if (faultException != null) {
                throw faultException;
            }

            return handleFaultRet;
        }

        public void close(MessageContext arg0) {
            invokeOrderOfClose = ++sinvokedOrder;
            closeInvoked++;
        }


        public void init(Map<String, Object> arg0) {
        }


        public void destroy() {
        }

        public int getHandleMessageCount() {
            return handleMessageInvoked;
        }

        public int getHandleFaultCount() {
            return handleFaultInvoked;
        }

        public boolean isHandleMessageInvoked() {
            return handleMessageInvoked > 0;
        }

        public boolean isCloseInvoked() {
            return closeInvoked > 0;
        }

        public int getCloseCount() {
            return closeInvoked;
        }

        public int getInvokeOrderOfHandleMessage() {
            return invokeOrderOfHandleMessage;
        }

        public int getInvokeOrderOfHandleFault() {
            return invokeOrderOfHandleFault;
        }

        public int getInvokeOrderOfClose() {
            return invokeOrderOfClose;
        }

        public void setHandleMessageRet(boolean ret) {
            handleMessageRet = ret;
        }

        public void setHandleFaultRet(boolean ret) {
            handleFaultRet = ret;
        }

        public String toString() {
            return "[" + super.toString() + " id: " + id + " invoke order: " + invokeOrderOfHandleMessage
                   + "]";
        }

        public void setException(RuntimeException rte) {
            exception = rte;
        }

        public void setFaultException(RuntimeException rte) {
            faultException = rte;
        }

        public static void clear() {
            sinvokedOrder = 0;
            sid = 0;
        }
    }

}