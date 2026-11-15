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

package org.apache.cxf.tools.wsdlto.core;

import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.cxf.BusFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WSDLDefinitionBuilderTest {
    @BeforeClass
    public static void ensureNewBus() {
        BusFactory.setDefaultBus(null);
    }


    @Test
    public void testBuildSimpleWSDL() throws Exception {
        String qname = "http://apache.org/hello_world_soap_http";
        String wsdlUrl = getClass().getResource("hello_world.wsdl").toString();

        WSDLDefinitionBuilder builder = new WSDLDefinitionBuilder(BusFactory.getDefaultBus());
        Definition def = builder.build(wsdlUrl);
        assertNotNull(def, () -> "Add assertion message");

        Map<?, ?> services = def.getServices();
        assertNotNull(services, () -> "Add assertion message");
        Integer magicNumber6784719 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber6784719, services.size(), () -> "Add assertion message");
        Service service = (Service)services.get(new QName(qname, "SOAPService"));
        assertNotNull(service, () -> "Add assertion message");

        Map<?, ?> ports = service.getPorts();
        assertNotNull(ports, () -> "Add assertion message");
        assertEquals(1, ports.size(), () -> "Add assertion message");
        Port port = service.getPort("SoapPort");
        assertNotNull(port, () -> "Add assertion message");
    }

    @Test
    public void testBuildImportedWSDL() throws Exception {
        String wsdlUrl = getClass().getResource("hello_world_services.wsdl").toString();

        WSDLDefinitionBuilder builder = new WSDLDefinitionBuilder(BusFactory.getDefaultBus());
        Definition def = builder.build(wsdlUrl);

        assertNotNull(def, () -> "Add assertion message");
        Map<?, ?> services = def.getServices();
        assertNotNull(services, () -> "Add assertion message");
        assertEquals(1, services.size(), () -> "Add assertion message");

        String serviceQName = "http://apache.org/hello_world/services";
        Service service = (Service)services.get(new QName(serviceQName, "SOAPService"));
        assertNotNull(service, () -> "Add assertion message");

        Map<?, ?> ports = service.getPorts();
        assertNotNull(ports, () -> "Add assertion message");
        assertEquals(1, ports.size(), () -> "Add assertion message");
        Port port = service.getPort("SoapPort");
        assertNotNull(port, () -> "Add assertion message");

        Binding binding = port.getBinding();
        assertNotNull(binding, () -> "Add assertion message");
        QName bindingQName = new QName("http://apache.org/hello_world/bindings", "SOAPBinding");
        assertEquals(bindingQName, binding.getQName(), () -> "Add assertion message");
        PortType portType = binding.getPortType();
        assertNotNull(portType, () -> "Add assertion message");
        QName portTypeQName = new QName("http://apache.org/hello_world", "Greeter");
        assertEquals(portTypeQName, portType.getQName(), () -> "Add assertion message");
        Operation op1 = portType.getOperation("sayHi", "sayHiRequest", "sayHiResponse");
        assertNotNull(op1, () -> "Add assertion message");
        QName messageQName = new QName("http://apache.org/hello_world/messages", "sayHiRequest");
        assertEquals(messageQName, op1.getInput().getMessage().getQName(), () -> "Add assertion message");

        Part part = op1.getInput().getMessage().getPart("in");
        assertNotNull(part, () -> "Add assertion message");
        assertEquals(new QName("http://apache.org/hello_world/types", "sayHi"), part.getElementName(), () -> "Add assertion message");
    }

    @Test
    public void testBuildImportedWSDLSpacesInPath() throws Exception {
        WSDLDefinitionBuilder builder = new WSDLDefinitionBuilder(BusFactory.getDefaultBus());
        String wsdlUrl = getClass().getResource("/folder with spaces/import_test.wsdl").toString();

        Definition def = builder.build(wsdlUrl);
        assertNotNull(def, () -> "Add assertion message");

        Map<?, ?> services = def.getServices();
        assertNotNull(services, () -> "Add assertion message");
        assertEquals(1, services.size(), () -> "Add assertion message");

        String serviceQName = "urn:S1importS2S3/resources/wsdl/S1importsS2S3Test1";
        Service service = (Service)services.get(new QName(serviceQName, "S1importsS2S3TestService"));
        assertNotNull(service, () -> "Add assertion message");

        Map<?, ?> ports = service.getPorts();
        assertNotNull(ports, () -> "Add assertion message");
        assertEquals(1, ports.size(), () -> "Add assertion message");
        Port port = service.getPort("S1importsS2S3TestPort");
        assertNotNull(port, () -> "Add assertion message");
    }

}