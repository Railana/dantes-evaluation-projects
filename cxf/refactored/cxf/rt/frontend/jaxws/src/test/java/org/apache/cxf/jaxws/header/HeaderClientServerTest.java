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

package org.apache.cxf.jaxws.header;



import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;

import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.AbstractJaxWsTest;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.header_test.SOAPHeaderService;
import org.apache.header_test.TestHeader;
import org.apache.header_test.TestHeaderImpl;
import org.apache.header_test.rpc.SOAPRPCHeaderService;
import org.apache.header_test.rpc.TestRPCHeader;
import org.apache.header_test.rpc.TestRPCHeaderImpl;
import org.apache.header_test.rpc.types.HeaderMessage;
import org.apache.header_test.types.TestHeader1;
import org.apache.header_test.types.TestHeader1Response;
import org.apache.header_test.types.TestHeader2;
import org.apache.header_test.types.TestHeader2Response;
import org.apache.header_test.types.TestHeader3;
import org.apache.header_test.types.TestHeader3Response;
import org.apache.header_test.types.TestHeader5;
import org.apache.header_test.types.TestHeader5ResponseBody;
import org.apache.header_test.types.TestHeader6;
import org.apache.header_test.types.TestHeader6Response;
import org.apache.tests.type_test.all.SimpleAll;
import org.apache.tests.type_test.choice.SimpleChoice;
import org.apache.tests.type_test.sequence.SimpleStruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class HeaderClientServerTest extends AbstractJaxWsTest {
    private final QName serviceName = new QName("http://apache.org/header_test",
                                                "SOAPHeaderService");
    private final QName portName = new QName("http://apache.org/header_test",
                                             "SoapHeaderPort");

    private EndpointImpl endpoint;
    private EndpointImpl rpcEndpoint;

    @Before
    public void setUp() throws Exception {
        BusFactory.setDefaultBus(getBus());

        Object implementor = new TestHeaderImpl();
        String address = "http://localhost:9104/SoapHeaderContext/SoapHeaderPort";
        // Removed line due to General Fixture smell: endpoint = (EndpointImpl) Endpoint.publish(address, implementor);

        implementor = new TestRPCHeaderImpl();
        address = "http://localhost:9104/SoapHeaderRPCContext/SoapHeaderRPCPort";
        // Removed line due to General Fixture smell: rpcEndpoint = (EndpointImpl)Endpoint.publish(address, implementor);
    }

    @After
    public void stopendpoints() {
        rpcEndpoint = (EndpointImpl)Endpoint.publish(address, implementor);
        endpoint = (EndpointImpl) Endpoint.publish(address, implementor);
        if (endpoint != null) {
            endpoint.stop();
        }
        if (rpcEndpoint != null) {
            rpcEndpoint.stop();
        }
        endpoint = null;
        rpcEndpoint = null;
    }
    @Test
    public void testInHeader() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);
        try {
            TestHeader1 val = new TestHeader1();
            for (int idx = 0; idx < 2; idx++) {
                TestHeader1Response returnVal = proxy.testHeader1(val, val);
                assertNotNull(returnVal, () -> "Add assertion message");
                assertEquals(TestHeader1.class.getSimpleName(), returnVal.getResponseType(), () -> "Add assertion message");
            }
        } catch (UndeclaredThrowableException ex) {
            throw (Exception)ex.getCause();
        }
    }

    @Test
    public void testOutHeader() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);
        try {
            TestHeader2 in = new TestHeader2();
            String val = new String(TestHeader2Response.class.getSimpleName());
            Holder<TestHeader2Response> out = new Holder<>();
            Holder<TestHeader2Response> outHeader = new Holder<>();
            for (int idx = 0; idx < 2; idx++) {
                val += idx;
                in.setRequestType(val);
                proxy.testHeader2(in, out, outHeader);

                assertEquals(val, out.value.getResponseType(), () -> "Add assertion message");
                assertEquals(val, outHeader.value.getResponseType(), () -> "Add assertion message");
            }
        } catch (UndeclaredThrowableException ex) {
            ex.printStackTrace();
            throw (Exception)ex.getCause();
        }
    }

    @Test
    public void testInOutHeader() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);

        try {
            TestHeader3 in = new TestHeader3();
            String val = new String(TestHeader3.class.getSimpleName());
            Holder<TestHeader3> inoutHeader = new Holder<>();
            for (int idx = 0; idx < 2; idx++) {
                val += idx;
                in.setRequestType(val);
                inoutHeader.value = new TestHeader3();
                TestHeader3Response returnVal = proxy.testHeader3(in, inoutHeader);
                //inoutHeader copied to return
                //in copied to inoutHeader
                assertNotNull(returnVal, () -> "Add assertion message");
                assertNull(returnVal.getResponseType(), () -> "Add assertion message");
                assertEquals(val, inoutHeader.value.getRequestType(), () -> "Add assertion message");

                in.setRequestType(null);
                inoutHeader.value.setRequestType(val);
                returnVal = proxy.testHeader3(in, inoutHeader);
                // Line removed due to Duplicate Assert: assertNotNull(returnVal, () -> "Add assertion message");
                assertEquals(val, returnVal.getResponseType(), () -> "Add assertion message");
                assertNull(inoutHeader.value.getRequestType(), () -> "Add assertion message");
            }
        } catch (UndeclaredThrowableException ex) {
            throw (Exception)ex.getCause();
        }
    }

    @Test
    public void testReturnHeader() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);
        try {
            Holder<TestHeader5ResponseBody> out = new Holder<>();
            Holder<TestHeader5> outHeader = new Holder<>();
            TestHeader5 in = new TestHeader5();
            String val = new String(TestHeader5.class.getSimpleName());
            for (int idx = 0; idx < 2; idx++) {
                val += idx;
                in.setRequestType(val);
                proxy.testHeader5(out, outHeader, in);
                Integer magicNumber56047673 = 1000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                assertEquals(magicNumber56047673, out.value.getResponseType(), () -> "Add assertion message");
                assertEquals(val, outHeader.value.getRequestType(), () -> "Add assertion message");
            }
        } catch (UndeclaredThrowableException ex) {
            throw (Exception)ex.getCause();
        }
    }

    @Test
    public void testHeaderPartBeforeBodyPart() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);

        TestHeader6 in = new TestHeader6();
        String val = new String(TestHeader6.class.getSimpleName());
        Holder<TestHeader3> inoutHeader = new Holder<>();
        for (int idx = 0; idx < 1; idx++) {
            val += idx;
            in.setRequestType(val);
            inoutHeader.value = new TestHeader3();
            TestHeader6Response returnVal = proxy.testHeaderPartBeforeBodyPart(inoutHeader, in);
            //inoutHeader copied to return
            //in copied to inoutHeader
            assertNotNull(returnVal, () -> "Add assertion message");
            assertNull(returnVal.getResponseType(), () -> "Add assertion message");
            assertEquals(val, inoutHeader.value.getRequestType(), () -> "Add assertion message");

            in.setRequestType(null);
            inoutHeader.value.setRequestType(val);
            returnVal = proxy.testHeaderPartBeforeBodyPart(inoutHeader, in);
            // Line removed due to Duplicate Assert: assertNotNull(returnVal, () -> "Add assertion message");
            assertEquals(val, returnVal.getResponseType(), () -> "Add assertion message");
            assertNull(inoutHeader.value.getRequestType(), () -> "Add assertion message");
        }
    }

    @Test
    public void testHeader4() {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);
        try {
            proxy.testHeader4("cxf");
        } catch (Exception e) {
            // REVISIT
            // fail("No exception should happen in testHeader4");
        }
    }
    @Test
    public void testHeader7() {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);
        assertEquals("Hello", proxy.testHeader7(), () -> "Add assertion message");
    }
    @Test
    public void testRPCInHeader() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader_rpc.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPRPCHeaderService service
            = new SOAPRPCHeaderService(wsdl,
                new QName("http://apache.org/header_test/rpc", "SOAPRPCHeaderService"));
        assertNotNull(service, () -> "Add assertion message");
        TestRPCHeader proxy = service.getSoapRPCHeaderPort();
        try {
            HeaderMessage header = new HeaderMessage();
            header.setHeaderVal("header");

            for (int idx = 0; idx < 2; idx++) {
                String returnVal = proxy.testHeader1(header, "part");
                assertNotNull(returnVal, () -> "Add assertion message");
                assertEquals("part/header", returnVal, () -> "Add assertion message");
            }
        } catch (UndeclaredThrowableException ex) {
            throw (Exception)ex.getCause();
        }
    }

    @Test
    public void testRPCInOutHeader() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader_rpc.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        SOAPRPCHeaderService service
            = new SOAPRPCHeaderService(wsdl,
                new QName("http://apache.org/header_test/rpc", "SOAPRPCHeaderService"));
        assertNotNull(service, () -> "Add assertion message");
        TestRPCHeader proxy = service.getSoapRPCHeaderPort();
        try {
            HeaderMessage header = new HeaderMessage();
            Holder<HeaderMessage> holder = new Holder<>(header);

            for (int idx = 0; idx < 2; idx++) {
                holder.value.setHeaderVal("header" + idx);
                String returnVal = proxy.testInOutHeader("part" + idx, holder);

                assertNotNull(returnVal, () -> "Add assertion message");
                assertEquals("header" + idx, returnVal, () -> "Add assertion message");
                assertEquals("part" + idx, holder.value.getHeaderVal(), () -> "Add assertion message");
            }
        } catch (UndeclaredThrowableException ex) {
            throw (Exception)ex.getCause();
        }
    }



    @Test
    public void testHolderOutIsTheFirstMessagePart() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/soapheader.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");
        SOAPHeaderService service = new SOAPHeaderService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");
        TestHeader proxy = service.getPort(portName, TestHeader.class);
        Holder<SimpleAll> simpleAll = new Holder<>();
        SimpleAll sa = new SimpleAll();
        sa.setVarAttrString("varAttrString");
        sa.setVarInt(100);
        sa.setVarString("varString");
        simpleAll.value = sa;
        SimpleChoice sc = new SimpleChoice();
        sc.setVarString("scVarString");
        SimpleStruct ss = proxy.sendReceiveAnyType(simpleAll, sc);
        assertEquals(simpleAll.value.getVarString(), "scVarString", () -> "Add assertion message");
        assertEquals(ss.getVarInt(), 200, () -> "Add assertion message");
        assertEquals(ss.getVarAttrString(), "varAttrStringRet", () -> "Add assertion message");
    }

}