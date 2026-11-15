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
package org.apache.cxf.systest.handlers;


import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Element;

import org.apache.cxf.common.util.PackageUtils;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.cxf.testutil.common.TestUtil;
import org.apache.handler_test.HandlerTest;
import org.apache.handler_test.HandlerTestService;
import org.apache.handler_test.PingException;
import org.apache.handler_test.types.PingOneWay;
import org.apache.handler_test.types.PingResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HandlerInvocationTest extends AbstractBusClientServerTestBase {
    private static String port = TestUtil.getPortNumber(Server.class);

    private final QName serviceName = new QName("http://apache.org/handler_test", "HandlerTestService");
    private final QName portName = new QName("http://apache.org/handler_test", "SoapPort");

    private URL wsdl;
    private HandlerTestService service;
    private HandlerTest handlerTest;

    @BeforeClass
    public static void startServers() throws Exception {

        //System.setProperty(SAAJFactoryResolver.MESSAGE_FACTORY_KEY,
        //                     "com.ibm.ws.webservices.engine.soap.MessageFactoryImpl");
        //System.setProperty(SAAJFactoryResolver.SOAP_FACTORY_KEY,
        //    "com.ibm.ws.webservices.engine.xmlsoap.SOAPFactory");
        createStaticBus();
        assertTrue("server did not launch correctly", launchServer(Server.class, true));
    }

    @Before
    public void setUp() throws Exception {
        super.createBus();

        // Removed line due to General Fixture smell: wsdl = HandlerInvocationTest.class.getResource("/wsdl/handler_test.wsdl");
        // Removed line due to General Fixture smell: service = new HandlerTestService(wsdl, serviceName);
        // Removed line due to General Fixture smell: handlerTest = service.getPort(portName, HandlerTest.class);
        setAddress(handlerTest, "http://localhost:" + port + "/HandlerTest/SoapPort");
    }

    @Test
    public void testAddHandlerThroughHandlerResolverClientSide() throws Exception {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        wsdl = HandlerInvocationTest.class.getResource("/wsdl/handler_test.wsdl");
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);

        MyHandlerResolver myHandlerResolver = new MyHandlerResolver(handler1, handler2);

        service.setHandlerResolver(myHandlerResolver);

        HandlerTest handlerTestNew = service.getPort(portName, HandlerTest.class);
        setAddress(handlerTestNew, "http://localhost:" + port + "/HandlerTest/SoapPort");

        handlerTestNew.pingOneWay();

        String bindingID = myHandlerResolver.bindingID;
        assertEquals("http://schemas.xmlsoap.org/wsdl/soap/http", bindingID, () -> "Add assertion message");
        Integer magicNumber40617609 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber40617609, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");

        //CXF-3956 - check to make sure a Dispatch can also get the handlers
        JAXBContext context = JAXBContext.newInstance(org.apache.handler_test.types.ObjectFactory.class);
        Dispatch<Object> disp = service.createDispatch(portName, context, Service.Mode.PAYLOAD);
        setAddress(disp, "http://localhost:" + port + "/HandlerTest/SoapPort");
        disp.invokeOneWay(new org.apache.handler_test.types.PingOneWay());

        Integer magicNumber99519315 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber99519315, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, handler2.getHandleMessageInvoked(), () -> "Add assertion message");

    }

    @Test
    public void testAddingUnusedHandlersThroughConfigFile() {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        wsdl = HandlerInvocationTest.class.getResource("/wsdl/handler_test.wsdl");
        HandlerTestServiceWithAnnotation service1 = new HandlerTestServiceWithAnnotation(wsdl, serviceName);
        HandlerTest handlerTest1 = service1.getPort(portName, HandlerTest.class);

        BindingProvider bp1 = (BindingProvider)handlerTest1;
        Binding binding1 = bp1.getBinding();
        @SuppressWarnings("rawtypes")
        List<Handler> port1HandlerChain = binding1.getHandlerChain();
        assertEquals(1, port1HandlerChain.size(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerOneWay() {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);
        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2);

        handlerTest.pingOneWay();

        assertEquals(1, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerTwoWay() throws Exception {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);
        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2);

        handlerTest.pingWithArgs("hello");

        assertEquals(2, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
    }

    @Test
    public void testSOAPHandlerHandleMessageReturnTrueClient() throws Exception {
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                try {
                    Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                    if (!outbound) {
                        LogicalMessage msg = ctx.getMessage();
                        Source source = msg.getPayload();
                        assertNotNull(source);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
                return true;
            }
        };

        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);

        List<String> resp = handlerTest.ping();
        assertNotNull(resp, () -> "Add assertion message");

        Integer magicNumber43722513 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("handle message was not invoked",magicNumber43722513, handler1.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 2, handler2.getHandleMessageInvoked());
        Integer magicNumber52731086 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("handle message was not invoked",magicNumber52731086, soapHandler1.getHandleMessageInvoked());
        Integer magicNumber53431439 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("handle message was not invoked",magicNumber53431439, soapHandler2.getHandleMessageInvoked());

        assertEquals("close must be called", 1, handler1.getCloseInvoked());
        assertEquals("close must be called", 1, handler2.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler1.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler2.getCloseInvoked());

        assertTrue(soapHandler2.getInvokeOrderOfClose()
                   < soapHandler1.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(soapHandler1.getInvokeOrderOfClose()
                   < handler2.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");

        // the server has encoded into the response the order in
        // which the handlers have been invoked, parse it and make
        // sure everything is ok expected order for inbound interceptors
        String[] handlerNames = {"soapHandler4", "soapHandler3", "handler2", "handler1", "servant",
                                 "handler1", "handler2", "soapHandler3", "soapHandler4"};

        assertEquals(handlerNames.length, resp.size(), () -> "Add assertion message");

        Iterator<String> iter = resp.iterator();
        for (String expected : handlerNames) {
            assertEquals(expected, iter.next(), () -> "Add assertion message");
        }
    }

    @Test
    public void testLogicalHandlerHandleMessageReturnFalseClientOutBound() throws Exception {
        final String clientHandlerMessage = "handler2 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                try {
                    Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                    if (outbound) {
                        LogicalMessage msg = ctx.getMessage();
                        assertNotNull("logical message is null", msg);
                        JAXBContext jaxbCtx = JAXBContext.newInstance(PackageUtils
                            .getPackageName(PingOneWay.class));
                        PingResponse resp = new PingResponse();
                        resp.getHandlersInfo().add(clientHandlerMessage);

                        msg.setPayload(resp, jaxbCtx);
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
                return true;
            }
        };
        TestHandler<LogicalMessageContext> handler3 = new TestHandler<>(false);
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, handler3, soapHandler1);

        List<String> resp = handlerTest.ping();
        assertEquals(clientHandlerMessage, resp.get(0), () -> "Add assertion message");

        assertEquals("the first handler must be invoked twice", 2, handler1.getHandleMessageInvoked());
        assertEquals("the second handler must be invoked once only on outbound", 1, handler2
            .getHandleMessageInvoked());
        assertEquals("the third handler must not be invoked", 0, handler3.getHandleMessageInvoked());
        Integer magicNumber69967359 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("the last handler must not be invoked",magicNumber69967359, soapHandler1.getHandleMessageInvoked());

        //outbound MEP processing ceased, the message direction was changed to inbound, essentially this is
        //only one MEP. So close is called only once at the end of inbound MEP, and the close order is
        //reversed to the outbound handler invoking order.
        assertEquals("close must be called", 1, handler1.getCloseInvoked());
        assertEquals("close must be called", 1, handler2.getCloseInvoked());
        assertEquals("close must be called", 0, handler3.getCloseInvoked());
        assertEquals("close must be called", 0, soapHandler1.getCloseInvoked());
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerHandleMessageReturnFalseClientInBound() throws Exception {
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (!outbound) {
                    return false;
                }

                return true;
            }
        };
        TestHandler<LogicalMessageContext> handler3 = new TestHandler<>(false);
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, handler3, soapHandler1);

        handlerTest.ping();

        Integer magicNumber86030421 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber86030421, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        Integer magicNumber68568949 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber68568949, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, handler3.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals("close must be called", 1, handler1.getCloseInvoked());
        assertEquals("close must be called", 1, handler2.getCloseInvoked());
        assertEquals("close must be called", 1, handler3.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler1.getCloseInvoked());
        assertTrue(soapHandler1.getInvokeOrderOfClose()
                   < handler3.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(handler3.getInvokeOrderOfClose()
                   < handler2.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testSOAPHandlerHandleMessageReturnFalseClientOutbound() throws Exception {
        final String clientHandlerMessage = "client side";
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
                super.handleMessage(ctx);
                try {
                    Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                    if (outbound) {
                        LogicalMessage msg = ctx.getMessage();
                        assertNotNull("logical message is null", msg);
                        JAXBContext jaxbCtx = JAXBContext.newInstance(PackageUtils
                            .getPackageName(PingOneWay.class));
                        PingResponse resp = new PingResponse();
                        resp.getHandlersInfo().add(clientHandlerMessage);

                        msg.setPayload(resp, jaxbCtx);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false) {
            public boolean handleMessage(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (outbound) {
                    return false;
                }
                return true;
            }
        };
        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);

        List<String> resp = handlerTest.ping();
        assertEquals(clientHandlerMessage, resp.get(0), () -> "Add assertion message");

        Integer magicNumber4948947 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber4948947, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler2.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals("close must be called", 1, handler1.getCloseInvoked());
        assertEquals("close must be called", 1, handler2.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler1.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler2.getCloseInvoked());
        assertTrue(soapHandler2.getInvokeOrderOfClose()
                   < soapHandler1.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(soapHandler1.getInvokeOrderOfClose()
                   < handler2.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testSOAPHandlerHandleMessageReturnFalseClientInbound() throws Exception {
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false) {
            public boolean handleMessage(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (!outbound) {
                    return false;
                }
                return true;
            }
        };
        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);

        handlerTest.ping();

        assertEquals(1, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, soapHandler2.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals("close must be called", 1, handler1.getCloseInvoked());
        assertEquals("close must be called", 1, handler2.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler1.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler2.getCloseInvoked());
        assertTrue(soapHandler2.getInvokeOrderOfClose()
                   < soapHandler1.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(soapHandler1.getInvokeOrderOfClose()
                   < handler2.getInvokeOrderOfClose(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerHandleMessageReturnsFalseServerInbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        String[] expectedHandlers = {"soapHandler4", "soapHandler3", "handler2",
                                     "soapHandler3", "soapHandler4"};

        List<String> resp = handlerTest.pingWithArgs("handler2 inbound stop");

        assertEquals(expectedHandlers.length, resp.size(), () -> "Add assertion message");

        int i = 0;
        for (String expected : expectedHandlers) {
            assertEquals(expected, resp.get(i++), () -> "Add assertion message");
        }
    }

    @Test
    public void testLogicHandlerHandleMessageReturnFalseServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        String[] expectedHandlers = {"server handler1 outbound stop", "soapHandler4",
                                     "soapHandler3", "handler2", "handler1", "handler1"};

        List<String> resp = handlerTest.pingWithArgs("server handler1 outbound stop");

        assertEquals(expectedHandlers.length, resp.size(), () -> "Add assertion message");

        int i = 0;
        for (String expected : expectedHandlers) {
            assertEquals(expected, resp.get(i++), () -> "Add assertion message");
        }
    }

    @Test
    public void testSOAPHandlerHandleMessageReturnsFalseServerInbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        String[] expectedHandlers = {"soapHandler4", "soapHandler3", "soapHandler4"};
        List<String> resp = handlerTest.pingWithArgs("soapHandler3 inbound stop");
        assertEquals(expectedHandlers.length, resp.size(), () -> "Add assertion message");
        int i = 0;
        for (String expected : expectedHandlers) {
            assertEquals(expected, resp.get(i++), () -> "Add assertion message");
        }
    }

    @Test
    public void testSOAPHandlerHandleMessageReturnsFalseServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        String[] expectedHandlers = {"soapHandler3 outbound stop", "soapHandler4", "soapHandler3", "handler2",
                                     "handler1", "handler1", "handler2", "soapHandler3"};
        List<String> resp = handlerTest.pingWithArgs("soapHandler3 outbound stop");

        assertEquals(expectedHandlers.length, resp.size(), () -> "Add assertion message");
        int i = 0;
        for (String expected : expectedHandlers) {
            assertEquals(expected, resp.get(i++), () -> "Add assertion message");
        }
    }

    @Test
    public void testLogicalHandlerHandleMessageThrowsProtocolExceptionClientOutbound() throws Exception {
        final String clientHandlerMessage = "handler1 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (outbound) {
                    throw new ProtocolException(clientHandlerMessage);
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1);

        assertThrows(ProtocolException e,
        		() -> {
        			handlerTest.ping();
        			fail("did not get expected exception");
        		});

        Integer magicNumber55283582 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber55283582, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(0, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(1, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerHandleMessageThrowsProtocolExceptionClientInbound() throws Exception {
        final String clientHandlerMessage = "handler1 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (!outbound) {
                    throw new ProtocolException(clientHandlerMessage);
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1);

        try {
            handlerTest.ping();
            //fail("did not get expected exception");
        } catch (ProtocolException e) {
            assertEquals(clientHandlerMessage, e.getMessage(), () -> "Add assertion message");
        }

        Integer magicNumber97870238 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber97870238, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(0, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerHandleMessageThrowsRuntimeExceptionClientOutbound() throws Exception {
        final String clientHandlerMessage = "handler1 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (outbound) {
                    throw new RuntimeException(clientHandlerMessage);
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1);

        try {
            handlerTest.ping();
            fail("did not get expected exception");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains(clientHandlerMessage), () -> "Add assertion message");
        }

        assertEquals(1, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(0, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testLogicalHandlerHandleMessageThrowsRuntimeExceptionClientInbound() throws Exception {
        final String clientHandlerMessage = "handler1 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (!outbound) {
                    throw new RuntimeException(clientHandlerMessage);
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1);

        try {
            handlerTest.ping();
            fail("did not get expected exception");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains(clientHandlerMessage), () -> "Add assertion message");
        }

        assertEquals(1, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(0, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testSOAPHandlerHandleMessageThrowsProtocolExceptionClientOutbound() throws Exception {
        final String clientHandlerMessage = "handler1 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false) {
            public boolean handleMessage(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (outbound) {
                    throw new ProtocolException(clientHandlerMessage);
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);

        try {
            handlerTest.ping();
            fail("did not get expected exception");
        } catch (ProtocolException e) {
            assertEquals(clientHandlerMessage, e.getMessage(), () -> "Add assertion message");
        }

        Integer magicNumber83940986 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83940986, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        Integer magicNumber4061372 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber4061372, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler2.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(1, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(1, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler2.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler2.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testSOAPHandlerHandleMessageThrowsProtocolExceptionClientInbound() throws Exception {
        final String clientHandlerMessage = "handler1 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false) {
            public boolean handleMessage(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (!outbound) {
                    throw new ProtocolException(clientHandlerMessage);
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);

        try {
            handlerTest.ping();
            //fail("did not get expected exception");
        } catch (ProtocolException e) {
            assertEquals(clientHandlerMessage, e.getMessage(), () -> "Add assertion message");
        }

        Integer magicNumber24955663 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24955663, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(2, soapHandler2.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(0, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler2.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler2.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testSOAPHandlerHandleMessageThrowsRuntimeExceptionClientOutbound() throws Exception {
        final String clientHandlerMessage = "handler1 client side";

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false) {
            public boolean handleMessage(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleMessage(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (outbound) {
                    throw new RuntimeException(clientHandlerMessage);
                }
                return true;
            }
        };
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false);

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);
        try {
            handlerTest.ping();
            fail("did not get expected exception");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains(clientHandlerMessage), () -> "Add assertion message");
        }

        assertEquals(1, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler2.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(0, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler2.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(0, soapHandler2.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    @Test
    public void testSOAPHandlerHandleMessageThrowsRuntimeExceptionServerInbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("soapHandler3 inbound throw RuntimeException");
            fail("did not get expected exception");
        } catch (SOAPFaultException e) {
            assertEquals("HandleMessage throws exception", e.getMessage(), () -> "Add assertion message");
        }
    }

    @Test
    public void testSOAPHandlerHandleMessageThrowsRuntimeExceptionServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("soapHandler3 outbound throw RuntimeException");
            fail("did not get expected exception");
        } catch (SOAPFaultException e) {
            assertEquals("HandleMessage throws exception", e.getMessage(), () -> "Add assertion message");
        }
    }

    @Test
    public void testSOAPHandlerHandleMessageThrowsProtocolExceptionServerInbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("soapHandler3 inbound throw ProtocolException");
            fail("did not get expected WebServiceException");
        } catch (WebServiceException e) {
            assertEquals("HandleMessage throws exception", e.getMessage(), () -> "Add assertion message");
        }
    }
    @Test
    public void testSOAPHandlerHandleMessageThrowsSOAPFaultExceptionServerInbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("soapHandler3 inbound throw SOAPFaultExceptionWDetail");
            fail("did not get expected SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("HandleMessage throws exception", e.getMessage(), () -> "Add assertion message");
            SOAPFault fault = e.getFault();
            assertNotNull(fault, () -> "Add assertion message");
            assertEquals(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server"),
                         fault.getFaultCodeAsQName(), () -> "Add assertion message");
            assertEquals("http://gizmos.com/orders", fault.getFaultActor(), () -> "Add assertion message");

            Detail detail = fault.getDetail();
            assertNotNull(detail, () -> "Add assertion message");

            QName nn = new QName("http://gizmos.com/orders/", "order");
            Element el = DOMUtils.getFirstChildWithName(detail, nn);
            assertNotNull(el, () -> "Add assertion message");
            el.normalize();
            assertEquals("Quantity element does not have a value", el.getFirstChild().getNodeValue(), () -> "Add assertion message");
            el = DOMUtils.getNextElement(el);
            el.normalize();
            assertEquals("Incomplete address: no zip code", el.getFirstChild().getNodeValue(), () -> "Add assertion message");
        }
    }

    /*-------------------------------------------------------
    * This is the expected order
    *-------------------------------------------------------
    * soapHandler3.handleMessage().doInbound()
    * soapHandler4.handleMessage().doInbound()
    * soapHandler4 Throwing an inbound ProtocolException
    * soapHandler3.handleFault()
    * soapHandler3 Throwing an outbound RuntimeException
    * soapHandler4.close()
    * soapHandler3.close()
    */
    @Test
    public void testSOAPHandlerHandleFaultThrowsRuntimeExceptionServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("soapHandler3 inbound throw ProtocolException "
                                     + "soapHandler4HandleFaultThrowsRunException");
            fail("did not get expected WebServiceException");
        } catch (WebServiceException e) {
            assertEquals("soapHandler4 HandleFault throws RuntimeException",
                         e.getMessage(), () -> "Add assertion message");
        }
    }

    @Test
    public void testSOAPHandlerHandleFaultThrowsSOAPFaultExceptionServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);

        try {
            handlerTest.pingWithArgs("soapHandler3 inbound throw ProtocolException "
                                     + "soapHandler4HandleFaultThrowsSOAPFaultException");
            fail("did not get expected SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("soapHandler4 HandleFault throws SOAPFaultException",
                         e.getMessage(), () -> "Add assertion message");
        }
    }

    @Test
    public void testSOAPHandlerHandleMessageThrowsProtocolExceptionServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("soapHandler3 outbound throw ProtocolException");
            fail("did not get expected WebServiceException");
        } catch (WebServiceException e) {
            assertEquals("HandleMessage throws exception", e.getMessage(), () -> "Add assertion message");
        }
    }

    @Test
    public void testServerSOAPInboundHandlerThrowsSOAPFaultToClientHandlers() throws Exception {
        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleFault(LogicalMessageContext ctx) {
                super.handleFault(ctx);
                try {
                    Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                    if (!outbound) {
                        LogicalMessage msg = ctx.getMessage();
                        Source source = msg.getPayload();
                        assertNotNull(source);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
                return true;
            }
        };


        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false) {
            public boolean handleFault(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleFault(ctx);
                Boolean outbound = (Boolean)ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                if (!outbound) {
                    try {
                        SOAPMessage msg = ctx.getMessage();
                        assertNotNull(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e.toString());
                    }
                }
                return true;
            }
        };

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);

        try {
            handlerTest.pingWithArgs("soapHandler3 inbound throw SOAPFaultException");
            fail("did not get expected SOAPFaultException");
        } catch (SOAPFaultException e) {
            //e.printStackTrace();
/*            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true);
            e.printStackTrace(ps);
            assertTrue("Did not get expected exception message",  baos.toString()
                .indexOf("HandleMessage throws exception") > -1);
            assertTrue("Did not get expected javax.xml.ws.soap.SOAPFaultException", baos.toString()
                .indexOf("javax.xml.ws.soap.SOAPFaultException") > -1);*/

        }

/*        assertEquals("handle message was not invoked", 1, handler1.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 1, handler2.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 1, soapHandler1.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 1, soapHandler2.getHandleMessageInvoked());

        assertEquals("handle message was not invoked", 1, handler1.getHandleFaultInvoked());
        assertEquals("handle message was not invoked", 1, handler2.getHandleFaultInvoked());
        assertEquals("handle message was not invoked", 1, soapHandler1.getHandleFaultInvoked());
        assertEquals("handle message was not invoked", 1, soapHandler2.getHandleFaultInvoked());

        assertEquals("close must be called", 1, handler1.getCloseInvoked());
        assertEquals("close must be called", 1, handler2.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler1.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler2.getCloseInvoked());

        assertTrue(soapHandler2.getInvokeOrderOfClose()
                   < soapHandler1.getInvokeOrderOfClose());
        assertTrue(soapHandler1.getInvokeOrderOfClose()
                   < handler2.getInvokeOrderOfClose());
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose());  */
    }

    /*-------------------------------------------------------
    * This is the expected order
    *-------------------------------------------------------
    * soapHandler4.handleMessage().doInbound()
    * soapHandler3.handleMessage().doInbound()
    * handler2.handleMessage().doInbound()
    * handler1.handleMessage().doInbound()
    * servant throws RuntimeException
    * handler1.handleFault()
    * handler2.handleFault()
    * soapHandler3.handleFault()
    * soapHandler4.handleFault()
    * handler1.close()
    * handler2.close()
    * soapHandler3.close()
    * soapHandler4.close()
    * */
    @Test
    public void testServerEndpointRemoteRuntimeException() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("servant throw WebServiceException");
            fail("did not get expected WebServiceException");
        } catch (WebServiceException e) {
/*            e.printStackTrace();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true);
            e.printStackTrace(ps);
            assertTrue("Did not get expected exception message",  baos.toString()
                .indexOf("RemoteException with nested RuntimeException") > -1);*/
        }
    }


    @Test
    public void testServerEndpointRemoteFault() throws PingException {

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleFault(LogicalMessageContext ctx) {
                super.handleFault(ctx);
                try {
                    Boolean outbound = (Boolean)
                        ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                    if (!outbound) {
                        LogicalMessage msg = ctx.getMessage();
                        String payload = convertDOMToString(msg.getPayload());
                        assertTrue(payload.indexOf(
                            "<faultstring>"
                            + "servant throws SOAPFaultException"
                            + "</faultstring>") > -1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
                return true;
            }

            private String convertDOMToString(Source s)
                throws TransformerException {
                StringWriter stringWriter = new StringWriter();
                StreamResult streamResult = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "no");
                transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount",
                    "2");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.transform(s, streamResult);
                return stringWriter.toString();
            }
        };

        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false) {
            public boolean handleFault(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
                super.handleFault(ctx);
                try {
                    Boolean outbound = (Boolean)
                        ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                    if (!outbound) {
                        SOAPEnvelope env =
                            ctx.getMessage().getSOAPPart().getEnvelope();
                        assertTrue("expected SOAPFault in SAAJ model",
                                   env.getBody().hasFault());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
                return true;
            }
        };

        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2, soapHandler1, soapHandler2);

        try {
            handlerTest.pingWithArgs("servant throw SOAPFaultException");
            fail("did not get expected Exception");
        } catch (SOAPFaultException sfe) {
            // expected
        }

        assertEquals(1, handler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getHandleMessageInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler2.getHandleMessageInvoked(), () -> "Add assertion message");

        assertEquals(1, handler2.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(1, handler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getHandleFaultInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler2.getHandleFaultInvoked(), () -> "Add assertion message");

        assertEquals(1, handler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, handler2.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler1.getCloseInvoked(), () -> "Add assertion message");
        assertEquals(1, soapHandler2.getCloseInvoked(), () -> "Add assertion message");
        assertTrue(handler2.getInvokeOrderOfClose()
                   < handler1.getInvokeOrderOfClose(), () -> "Add assertion message");
    }

    /*-------------------------------------------------------
     * This is the expected order
     *-------------------------------------------------------
     * soapHandler3.handleMessage().doInbound()
     * soapHandler4.handleMessage().doInbound()
     * handler2.handleMessage().doInbound()
     * handler1.handleMessage().doInbound()
     * handler1 Throwing an inbound ProtocolException
     * handler2.handleFault()
     * handler2 Throwing an outbound RuntimeException
     * handler1.close()
     * handler1.close()
     * soapHandler4.close()
     * soapHandler3.close()
     */
    @Test
    public void testLogicalHandlerHandleFaultThrowsRuntimeExceptionServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("handler1 inbound throw ProtocolException "
                                     + "handler2HandleFaultThrowsRunException");
            fail("did not get expected WebServiceException");
        } catch (WebServiceException e) {
/*            e.printStackTrace();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true);
            e.printStackTrace(ps);
            assertTrue("Did not get expected exception message", baos.toString()
                .indexOf("handler2 HandleFault throws RuntimeException") > -1);
            assertTrue("Did not get expected javax.xml.ws.soap.SOAPFaultException", baos.toString()
                .indexOf("javax.xml.ws.soap.SOAPFaultException") > -1);*/

        }
    }

    @Test
    public void testLogicalHandlerHandleFaultThrowsSOAPFaultExceptionServerOutbound() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        try {
            handlerTest.pingWithArgs("handler1 inbound throw ProtocolException "
                                     + "handler2HandleFaultThrowsSOAPFaultException");
            fail("did not get expected SOAPFaultException");
        } catch (SOAPFaultException e) {
/*            e.printStackTrace();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true);
            e.printStackTrace(ps);
            assertTrue("Did not get expected exception message", baos.toString()
                .indexOf("handler2 HandleFault throws SOAPFaultException") > -1);
            assertTrue("Did not get expected javax.xml.ws.soap.SOAPFaultException", baos.toString()
                .indexOf("javax.xml.ws.soap.SOAPFaultException") > -1);*/
        }
    }

    @Test
    public void testLogicalHandlerHandleMessageThrowsProtocolExceptionServerInbound()
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        throws PingException {
        try {
            handlerTest.pingWithArgs("handler2 inbound throw ProtocolException");
            fail("did not get expected exception");
        } catch (WebServiceException e) {
            assertTrue(e.getMessage().indexOf("HandleMessage throws exception") >= 0, () -> "Add assertion message");
        }
    }

    @Test
    public void testDescription() throws PingException {
        TestHandler<LogicalMessageContext> handler = new TestHandler<LogicalMessageContext>(false) {
            public boolean handleMessage(LogicalMessageContext ctx) {
        wsdl = HandlerInvocationTest.class.getResource("/wsdl/handler_test.wsdl");
                super.handleMessage(ctx);
                assertTrue("wsdl description not found or invalid", isValidWsdlDescription(ctx
                    .get(MessageContext.WSDL_DESCRIPTION)));
                return true;
            }
        };
        TestSOAPHandler soapHandler = new TestSOAPHandler(false) {
            public boolean handleMessage(SOAPMessageContext ctx) {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        wsdl = HandlerInvocationTest.class.getResource("/wsdl/handler_test.wsdl");
                super.handleMessage(ctx);
                assertTrue("wsdl description not found or invalid", isValidWsdlDescription(ctx
                    .get(MessageContext.WSDL_DESCRIPTION)));
                return true;
            }
        };

        addHandlersToChain((BindingProvider)handlerTest, handler, soapHandler);

        List<String> resp = handlerTest.ping();
        assertNotNull(resp, () -> "Add assertion message");

        assertEquals("handler was not invoked", 2, handler.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 2, soapHandler.getHandleMessageInvoked());
        assertTrue("close must be  called", handler.isCloseInvoked());
        assertTrue("close must be  called", soapHandler.isCloseInvoked());
    }

    @Test
    public void testHandlersInvokedForDispatch() throws Exception {
        service = new HandlerTestService(wsdl, serviceName);
        Dispatch<SOAPMessage> disp = service
            .createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
        setAddress(disp, "http://localhost:" + port + "/HandlerTest/SoapPort");

        TestHandler<LogicalMessageContext> handler1 = new TestHandler<>(false);
        TestHandler<LogicalMessageContext> handler2 = new TestHandler<>(false);
        TestSOAPHandler soapHandler1 = new TestSOAPHandler(false);
        TestSOAPHandler soapHandler2 = new TestSOAPHandler(false);

        addHandlersToChain(disp, handler1, handler2, soapHandler1, soapHandler2);

        InputStream is = getClass().getResourceAsStream("PingReq.xml");
        SOAPMessage outMsg = MessageFactory.newInstance().createMessage(null, is);

        SOAPMessage inMsg = disp.invoke(outMsg);
        assertNotNull(inMsg, () -> "Add assertion message");

        assertEquals("handle message was not invoked", 2, handler1.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 2, handler2.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 2, soapHandler1.getHandleMessageInvoked());
        assertEquals("handle message was not invoked", 2, soapHandler2.getHandleMessageInvoked());

        assertEquals("close must be called", 1, handler1.getCloseInvoked());
        assertEquals("close must be called", 1, handler2.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler1.getCloseInvoked());
        assertEquals("close must be called", 1, soapHandler2.getCloseInvoked());

        // the server has encoded into the response the order in
        // which the handlers have been invoked, parse it and make
        // sure everything is ok

        // expected order for inbound interceptors
        String[] handlerNames = {"soapHandler4", "soapHandler3", "handler2", "handler1", "servant",
                                 "handler1", "handler2", "soapHandler3", "soapHandler4"};

        List<String> resp = getHandlerNames(inMsg.getSOAPPart().getEnvelope().getBody());
        assertEquals(handlerNames.length, resp.size(), () -> "Add assertion message");

        Iterator<String> iter = resp.iterator();
        for (String expected : handlerNames) {
            assertEquals(expected, iter.next(), () -> "Add assertion message");
        }
    }

    @Test
    public void testMustUnderstandSoapFaultOneWay() {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        TestMustUnderstandHandler<SOAPMessageContext> handler =
            new TestMustUnderstandHandler<>();
        addHandlersToChain((BindingProvider)handlerTest, handler);
        try {
            handlerTest.pingOneWay();
        } catch (Exception e) {
            fail("Catch unexpected exception: soap faule message "
                 + "should not be returned for one way operation");
        }

    }

    @Test
    public void testHandlerMessgeContext() throws PingException {
        service = new HandlerTestService(wsdl, serviceName);
        handlerTest = service.getPort(portName, HandlerTest.class);
        MessageContextFirstHandler handler1 = new MessageContextFirstHandler();
        MessageContextSecondHandler handler2 = new MessageContextSecondHandler();
        addHandlersToChain((BindingProvider)handlerTest, handler1, handler2);

        List<String> resp = handlerTest.ping();
        assertNotNull(resp, () -> "Add assertion message");
        assertNotNull("handler2 can't retrieve header map from message context", handler2.getHeaderMap());
    }

    void addHandlersToChain(BindingProvider bp, Handler<?>... handlers) {
        @SuppressWarnings("rawtypes")
        List<Handler> handlerChain = bp.getBinding().getHandlerChain();
        assertNotNull(handlerChain);
        for (Handler<?> h : handlers) {
            handlerChain.add(h);
        }
        bp.getBinding().setHandlerChain(handlerChain);
    }

    List<String> getHandlerNames(SOAPBody soapBody) throws Exception {

        Element elNode = DOMUtils.getFirstElement(soapBody);
        List<String> stringList = null;
        JAXBContext jaxbCtx = JAXBContext.newInstance(PingResponse.class);
        Unmarshaller um = jaxbCtx.createUnmarshaller();
        Object obj = um.unmarshal(elNode);

        if (obj instanceof PingResponse) {
            PingResponse pr = PingResponse.class.cast(obj);
            stringList = pr.getHandlersInfo();
        }
        return stringList;
    }

    public class MyHandlerResolver implements HandlerResolver {
        @SuppressWarnings("rawtypes")
        List<Handler> chain = new ArrayList<>();
        String bindingID;

        public MyHandlerResolver(Handler<?>... handlers) {
            for (Handler<?> h : handlers) {
                chain.add(h);
            }
        }

        @SuppressWarnings("rawtypes")
        public List<Handler> getHandlerChain(PortInfo portInfo) {
            bindingID = portInfo.getBindingID();
            return chain;
        }

    }

    private boolean isValidWsdlDescription(Object wsdlDescription) {
        wsdl = HandlerInvocationTest.class.getResource("/wsdl/handler_test.wsdl");
        return (wsdlDescription != null)
               && (wsdlDescription instanceof java.net.URI || wsdlDescription instanceof java.net.URL);
    }
}