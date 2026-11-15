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

package org.apache.cxf.binding.soap;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.binding.soap.model.SoapBodyInfo;
import org.apache.cxf.binding.soap.model.SoapOperationInfo;
import org.apache.cxf.bus.managers.BindingFactoryManagerImpl;
import org.apache.cxf.service.model.BindingFaultInfo;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.wsdl.WSDLConstants;
import org.apache.cxf.wsdl11.WSDLServiceBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoapBindingFactoryTest {
    private Bus getMockBus() {
        return mock(Bus.class);
    }

    private BindingFactoryManager getBindingFactoryManager(String ns, Bus bus) throws BusException {
        SoapBindingFactory bindingFactory = new SoapBindingFactory();
        BindingFactoryManager bfm = new BindingFactoryManagerImpl();
        bfm.registerBindingFactory(ns, bindingFactory);
        return bfm;
    }

    @Test
    public void testNoBodyParts() throws Exception {
        Definition d = createDefinition("/wsdl_soap/no_body_parts.wsdl");
        Bus bus = getMockBus();

        BindingFactoryManager bfm = getBindingFactoryManager(WSDLConstants.NS_SOAP11, bus);

        when(bus.getExtension(BindingFactoryManager.class)).thenReturn(bfm);

        DestinationFactoryManager dfm = mock(DestinationFactoryManager.class);
        when(bus.getExtension(DestinationFactoryManager.class)).thenReturn(dfm);

        WSDLServiceBuilder builder = new WSDLServiceBuilder(bus);
        ServiceInfo serviceInfo = builder
            .buildServices(d, new QName("urn:org:apache:cxf:no_body_parts/wsdl",
                                        "NoBodyParts"))
            .get(0);

        BindingInfo bi = serviceInfo.getBindings().iterator().next();

        assertTrue(bi instanceof SoapBindingInfo, () -> "Add assertion message");

        SoapBindingInfo sbi = (SoapBindingInfo)bi;
        assertEquals("document", sbi.getStyle(), () -> "Add assertion message");
        assertTrue(WSDLConstants.NS_SOAP11_HTTP_TRANSPORT.equalsIgnoreCase(sbi.getTransportURI()), () -> "Add assertion message");
        assertTrue(sbi.getSoapVersion() instanceof Soap11, () -> "Add assertion message");

        BindingOperationInfo boi = sbi.getOperation(new QName("urn:org:apache:cxf:no_body_parts/wsdl",
                                                              "operation1"));

        assertNotNull(boi, () -> "Add assertion message");
        SoapOperationInfo sboi = boi.getExtensor(SoapOperationInfo.class);
        assertNotNull(sboi, () -> "Add assertion message");
        assertNull(sboi.getStyle(), () -> "Add assertion message");
        assertEquals("", sboi.getAction(), () -> "Add assertion message");

        BindingMessageInfo input = boi.getInput();
        SoapBodyInfo bodyInfo = input.getExtensor(SoapBodyInfo.class);
        assertNull(bodyInfo.getUse(), () -> "Add assertion message");

        List<MessagePartInfo> parts = bodyInfo.getParts();
        assertNotNull(parts, () -> "Add assertion message");
        Integer magicNumber8251618 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber8251618, parts.size(), () -> "Add assertion message");
    }

    @Test
    public void testFactory() throws Exception {
        Definition d = createDefinition("/wsdl_soap/hello_world.wsdl");

        Bus bus = getMockBus();

        BindingFactoryManager bfm = getBindingFactoryManager(WSDLConstants.NS_SOAP11, bus);

        when(bus.getExtension(BindingFactoryManager.class)).thenReturn(bfm);

        DestinationFactoryManager dfm = mock(DestinationFactoryManager.class);
        when(bus.getExtension(DestinationFactoryManager.class)).thenReturn(dfm);

        WSDLServiceBuilder builder = new WSDLServiceBuilder(bus);
        ServiceInfo serviceInfo = builder
            .buildServices(d, new QName("http://apache.org/hello_world_soap_http", "SOAPService"))
            .get(0);

        BindingInfo bi = serviceInfo.getBindings().iterator().next();

        assertTrue(bi instanceof SoapBindingInfo, () -> "Add assertion message");

        SoapBindingInfo sbi = (SoapBindingInfo)bi;
        assertEquals("document", sbi.getStyle(), () -> "Add assertion message");
        assertTrue(WSDLConstants.NS_SOAP11_HTTP_TRANSPORT.equalsIgnoreCase(sbi.getTransportURI()), () -> "Add assertion message");
        assertTrue(sbi.getSoapVersion() instanceof Soap11, () -> "Add assertion message");

        BindingOperationInfo boi = sbi.getOperation(new QName("http://apache.org/hello_world_soap_http",
                                                              "sayHi"));
        SoapOperationInfo sboi = boi.getExtensor(SoapOperationInfo.class);
        assertNotNull(sboi, () -> "Add assertion message");
        assertEquals("document", sboi.getStyle(), () -> "Add assertion message");
        assertEquals("", sboi.getAction(), () -> "Add assertion message");

        BindingMessageInfo input = boi.getInput();
        SoapBodyInfo bodyInfo = input.getExtensor(SoapBodyInfo.class);
        assertEquals("literal", bodyInfo.getUse(), () -> "Add assertion message");

        List<MessagePartInfo> parts = bodyInfo.getParts();
        assertNotNull(parts, () -> "Add assertion message");
        assertEquals(1, parts.size(), () -> "Add assertion message");
    }


    @Test
    public void testSoap12Factory() throws Exception {
        Definition d = createDefinition("/wsdl_soap/hello_world_soap12.wsdl");

        Bus bus = getMockBus();

        BindingFactoryManager bfm = getBindingFactoryManager(WSDLConstants.NS_SOAP12, bus);

        when(bus.getExtension(BindingFactoryManager.class)).thenReturn(bfm);

        DestinationFactoryManager dfm = mock(DestinationFactoryManager.class);
        when(bus.getExtension(DestinationFactoryManager.class)).thenReturn(dfm);

        WSDLServiceBuilder builder = new WSDLServiceBuilder(bus);
        ServiceInfo serviceInfo = builder
            .buildServices(d, new QName("http://apache.org/hello_world_soap12_http", "SOAPService"))
            .get(0);

        BindingInfo bi = serviceInfo.getBindings().iterator().next();

        assertTrue(bi instanceof SoapBindingInfo, () -> "Add assertion message");

        SoapBindingInfo sbi = (SoapBindingInfo)bi;
        assertEquals("document", sbi.getStyle(), () -> "Add assertion message");
        assertEquals(WSDLConstants.NS_SOAP_HTTP_TRANSPORT, sbi.getTransportURI(), () -> "Add assertion message");
        assertTrue(sbi.getSoapVersion() instanceof Soap12, () -> "Add assertion message");

        BindingOperationInfo boi = sbi.getOperation(new QName("http://apache.org/hello_world_soap12_http",
                                                              "sayHi"));
        SoapOperationInfo sboi = boi.getExtensor(SoapOperationInfo.class);
        assertNotNull(sboi, () -> "Add assertion message");
        assertEquals("document", sboi.getStyle(), () -> "Add assertion message");
        assertEquals("sayHiAction", sboi.getAction(), () -> "Add assertion message");

        BindingMessageInfo input = boi.getInput();
        SoapBodyInfo bodyInfo = input.getExtensor(SoapBodyInfo.class);
        assertEquals("literal", bodyInfo.getUse(), () -> "Add assertion message");

        List<MessagePartInfo> parts = bodyInfo.getParts();
        assertNotNull(parts, () -> "Add assertion message");
        assertEquals(1, parts.size(), () -> "Add assertion message");

        boi = sbi.getOperation(new QName("http://apache.org/hello_world_soap12_http", "pingMe"));
        sboi = boi.getExtensor(SoapOperationInfo.class);
        // Line removed due to Duplicate Assert: assertNotNull(sboi, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("document", sboi.getStyle(), () -> "Add assertion message");
        assertEquals("", sboi.getAction(), () -> "Add assertion message");
        Collection<BindingFaultInfo> faults = boi.getFaults();
        assertEquals(1, faults.size(), () -> "Add assertion message");
        BindingFaultInfo faultInfo = boi.getFault(new QName("http://apache.org/hello_world_soap12_http",
                                                            "pingMeFault"));
        assertNotNull(faultInfo, () -> "Add assertion message");
    }


    public Definition createDefinition(String wsdlURL) throws Exception {
        URL resource = getClass().getResource(wsdlURL);
        return WSDLFactory.newInstance().newWSDLReader().readWSDL(null,
                                                                  new InputSource(resource.openStream()));
    }
}