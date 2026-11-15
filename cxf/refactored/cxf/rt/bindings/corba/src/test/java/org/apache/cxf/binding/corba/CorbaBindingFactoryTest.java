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
package org.apache.cxf.binding.corba;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.binding.corba.wsdl.CorbaConstants;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl11.WSDLServiceFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CorbaBindingFactoryTest {

    protected Bus bus;
    protected EndpointInfo endpointInfo;
    protected EndpointReferenceType target;
    protected MessageObserver observer;
    protected Message inMessage;
    CorbaBindingFactory factory;

    @Before
    public void setUp() throws Exception {
        bus = BusFactory.getDefaultBus();
        // Removed line due to General Fixture smell: bus = BusFactory.getDefaultBus();
        BindingFactoryManager bfm = bus.getExtension(BindingFactoryManager.class);
        factory = (CorbaBindingFactory)bfm.getBindingFactory("http://cxf.apache.org/bindings/corba");
        bfm.registerBindingFactory(CorbaConstants.NU_WSDL_CORBA, factory);
    }

    @After
    public void tearDown() {
        bus = BusFactory.getDefaultBus();
        bus.shutdown(true);
    }

    protected void setupServiceInfo(String ns, String wsdl, String serviceName, String portName) {
        URL wsdlUrl = getClass().getResource(wsdl);
        assertNotNull(wsdlUrl);
        WSDLServiceFactory f = new WSDLServiceFactory(bus, wsdlUrl.toString(), new QName(ns, serviceName));

        Service service = f.create();
        endpointInfo = service.getEndpointInfo(new QName(ns, portName));

    }

    @Test
    public void testCreateBinding() throws Exception {
        BindingInfo bindingInfo = mock(BindingInfo.class);

        CorbaBinding binding = (CorbaBinding)factory.createBinding(bindingInfo);
        assertNotNull(binding, () -> "Add assertion message");
        assertTrue(CorbaBinding.class.isInstance(binding), () -> "Add assertion message");
        List<Interceptor<? extends Message>> inInterceptors = binding.getInInterceptors();
        assertNotNull(inInterceptors, () -> "Add assertion message");
        List<Interceptor<? extends Message>> outInterceptors = binding.getOutInterceptors();
        assertNotNull(outInterceptors, () -> "Add assertion message");
        Integer magicNumber41464073 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber41464073, inInterceptors.size(), () -> "Add assertion message");
        assertEquals(2, outInterceptors.size(), () -> "Add assertion message");
    }

    @Test
    public void testGetCorbaConduit() throws Exception {
        bus = BusFactory.getDefaultBus();
        setupServiceInfo("http://cxf.apache.org/bindings/corba/simple",
                         "/wsdl_corbabinding/simpleIdl.wsdl",
                         "SimpleCORBAService",
                         "SimpleCORBAPort");

        Conduit conduit = factory.getConduit(endpointInfo, bus);
        assertNotNull(conduit, () -> "Add assertion message");
        conduit = factory.getConduit(endpointInfo, null, bus);
        // Line removed due to Duplicate Assert: assertNotNull(conduit, () -> "Add assertion message");
        target = mock(EndpointReferenceType.class);
        conduit = factory.getConduit(endpointInfo, target, bus);
        // Line removed due to Duplicate Assert: assertNotNull(conduit, () -> "Add assertion message");
    }

    @Test
    public void testGetDestination() throws Exception {
        bus = BusFactory.getDefaultBus();
        setupServiceInfo("http://cxf.apache.org/bindings/corba/simple",
                         "/wsdl_corbabinding/simpleIdl.wsdl",
                         "SimpleCORBAService",
                         "SimpleCORBAPort");

        Destination destination = factory.getDestination(endpointInfo, bus);
        assertNotNull(destination, () -> "Add assertion message");
        target = destination.getAddress();
        assertNotNull(target, () -> "Add assertion message");
    }

    @Test
    public void testTransportIds() throws Exception {
        setupServiceInfo("http://cxf.apache.org/bindings/corba/simple",
                         "/wsdl_corbabinding/simpleIdl.wsdl",
                         "SimpleCORBAService",
                         "SimpleCORBAPort");

        List<String> strs = new ArrayList<>();
        strs.add("one");
        strs.add("two");
        factory.setTransportIds(strs);
        List<String> retStrs = factory.getTransportIds();
        assertNotNull(retStrs, () -> "Add assertion message");
        String str = retStrs.get(0);
        assertEquals("one", str, () -> "Add assertion message");
        str = retStrs.get(1);
        assertEquals("two", str, () -> "Add assertion message");
    }

    @Test
    public void testGetUriPrefixes() throws Exception {
        Set<String> prefixes = factory.getUriPrefixes();
        assertNotNull("Prefixes should not be null", prefixes != null);
    }

    // check this
    @Test
    public void testSetBus() throws Exception {
        bus = BusFactory.getDefaultBus();
        factory.setBus(bus);
    }

}