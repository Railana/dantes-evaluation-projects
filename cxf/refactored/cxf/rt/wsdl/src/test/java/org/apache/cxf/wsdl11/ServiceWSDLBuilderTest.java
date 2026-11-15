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

package org.apache.cxf.wsdl11;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.service.model.SchemaInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.wsdl.WSDLManager;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceWSDLBuilderTest {

    private static final Logger LOG = LogUtils.getLogger(ServiceWSDLBuilderTest.class);
    private static final String WSDL_PATH = "hello_world.wsdl";
    private static final String NO_BODY_PARTS_WSDL_PATH = "no_body_parts.wsdl";
    private static final String WSDL_XSD_IMPORT_PATH = "hello_world_schema_import_test.wsdl";

    private Definition def;
    private Definition newDef;
    private Service service;

    private WSDLServiceBuilder wsdlServiceBuilder;
    private ServiceInfo serviceInfo;

    private Bus bus;
    private BindingFactoryManager bindingFactoryManager;
    private DestinationFactoryManager destinationFactoryManager;
    private DestinationFactory destinationFactory;

    private void setupWSDL(String wsdlPath) throws Exception {
        setupWSDL(wsdlPath, false);
    }

    private void setupWSDL(String wsdlPath, boolean doXsdImports) throws Exception {
        String wsdlUrl = getClass().getResource(wsdlPath).toString();
        LOG.info("the path of wsdl file is " + wsdlUrl);
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose", false);
        def = wsdlReader.readWSDL(wsdlUrl);

        bus = mock(Bus.class);
        bindingFactoryManager = mock(BindingFactoryManager.class);
        destinationFactoryManager = mock(DestinationFactoryManager.class);
        destinationFactory = mock(DestinationFactory.class);
        wsdlServiceBuilder = new WSDLServiceBuilder(bus, false);

        for (Service serv : CastUtils.cast(def.getServices().values(), Service.class)) {
            if (serv != null) {
                service = serv;
                break;
            }
        }
        when(bus.getExtension(WSDLManager.class)).thenReturn(new WSDLManagerImpl());

        when(bus.getExtension(BindingFactoryManager.class)).thenReturn(bindingFactoryManager);
        when(bus.getExtension(DestinationFactoryManager.class))
            .thenReturn(destinationFactoryManager);

        when(destinationFactoryManager
                        .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/"))
            .thenReturn(destinationFactory);

        serviceInfo = wsdlServiceBuilder.buildServices(def, service).get(0);
        ServiceWSDLBuilder builder = new ServiceWSDLBuilder(bus, serviceInfo);
        builder.setUseSchemaImports(doXsdImports);
        builder.setBaseFileName("HelloWorld");
        newDef = builder.build(new HashMap<String, SchemaInfo>());
    }

    @After
    public void tearDown() throws Exception {
        newDef = null;
    }

    @Test
    public void testNoBodyParts() throws Exception {
        setupWSDL(NO_BODY_PARTS_WSDL_PATH);
        QName messageName = new QName("urn:org:apache:cxf:no_body_parts/wsdl",
            "operation1Request");
        Message message = newDef.getMessage(messageName);
        Part part = message.getPart("mimeAttachment");
        assertNotNull(part.getTypeName(), () -> "Add assertion message");
    }

    @Test
    public void testDefinition() throws Exception {
        setupWSDL(WSDL_PATH);
        assertEquals(newDef.getTargetNamespace(), "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        Service serv = newDef.getService(new QName("http://apache.org/hello_world_soap_http",
                                                   "SOAPService"));
        assertNotNull(serv, () -> "Add assertion message");
        assertNotNull(serv.getPort("SoapPort"), () -> "Add assertion message");
    }

    @Test
    public void testPortType() throws Exception {
        setupWSDL(WSDL_PATH);
        Integer magicNumber57483188 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber57483188, newDef.getPortTypes().size(), () -> "Add assertion message");
        PortType portType = (PortType)newDef.getPortTypes().values().iterator().next();
        assertNotNull(portType, () -> "Add assertion message");
        assertEquals(portType.getQName(), new QName(newDef.getTargetNamespace(), "Greeter"), () -> "Add assertion message");

    }

    @Test
    public void testSayHiOperation() throws Exception {
        setupWSDL(WSDL_PATH);
        PortType portType = newDef.getPortType(new QName(newDef.getTargetNamespace(),
            "Greeter"));
        Collection<Operation> operations =
            CastUtils.cast(
                portType.getOperations(), Operation.class);

        assertEquals(4, operations.size(), () -> "Add assertion message");
        Operation sayHi = portType.getOperation("sayHi", "sayHiRequest", "sayHiResponse");
        assertNotNull(sayHi, () -> "Add assertion message");
        assertEquals(sayHi.getName(), "sayHi", () -> "Add assertion message");
        Input input = sayHi.getInput();
        assertNotNull(input, () -> "Add assertion message");
        assertEquals("sayHiRequest", input.getName(), () -> "Add assertion message");
        Message message = input.getMessage();
        assertNotNull(message, () -> "Add assertion message");
        assertEquals("sayHiRequest", message.getQName().getLocalPart(), () -> "Add assertion message");
        assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        assertEquals(1, message.getParts().size(), () -> "Add assertion message");
        assertEquals("in", message.getPart("in").getName(), () -> "Add assertion message");
        Output output = sayHi.getOutput();
        assertNotNull(output, () -> "Add assertion message");
        assertEquals("sayHiResponse", output.getName(), () -> "Add assertion message");
        message = output.getMessage();
        // Line removed due to Duplicate Assert: assertNotNull(message, () -> "Add assertion message");
        assertEquals("sayHiResponse", message.getQName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, message.getParts().size(), () -> "Add assertion message");
        assertEquals("out", message.getPart("out").getName(), () -> "Add assertion message");
        assertEquals(0, sayHi.getFaults().size(), () -> "Add assertion message");

    }

    @Test
    public void testGreetMeOperation() throws Exception {
        setupWSDL(WSDL_PATH);
        PortType portType = newDef.getPortType(new QName(newDef.getTargetNamespace(),
            "Greeter"));
        Operation greetMe = portType.getOperation("greetMe", "greetMeRequest", "greetMeResponse");
        assertNotNull(greetMe, () -> "Add assertion message");
        assertEquals("greetMe", greetMe.getName(), () -> "Add assertion message");
        Input input = greetMe.getInput();
        assertNotNull(input, () -> "Add assertion message");
        assertEquals("greetMeRequest", input.getName(), () -> "Add assertion message");
        Message message = input.getMessage();
        assertNotNull(message, () -> "Add assertion message");
        assertEquals("greetMeRequest", message.getQName().getLocalPart(), () -> "Add assertion message");
        assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        assertEquals(1, message.getParts().size(), () -> "Add assertion message");
        assertEquals("in", message.getPart("in").getName(), () -> "Add assertion message");
        Output output = greetMe.getOutput();
        assertNotNull(output, () -> "Add assertion message");
        assertEquals("greetMeResponse", output.getName(), () -> "Add assertion message");
        message = output.getMessage();
        // Line removed due to Duplicate Assert: assertNotNull(message, () -> "Add assertion message");
        assertEquals("greetMeResponse", message.getQName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, message.getParts().size(), () -> "Add assertion message");
        assertEquals("out", message.getPart("out").getName(), () -> "Add assertion message");
        assertEquals(0, greetMe.getFaults().size(), () -> "Add assertion message");

    }

    @Test
    public void testGreetMeOneWayOperation() throws Exception {
        setupWSDL(WSDL_PATH);
        PortType portType = newDef.getPortType(new QName(newDef.getTargetNamespace(),
            "Greeter"));
        Operation greetMeOneWay = portType.getOperation("greetMeOneWay", "greetMeOneWayRequest", null);
        assertNotNull(greetMeOneWay, () -> "Add assertion message");
        assertEquals("greetMeOneWay", greetMeOneWay.getName(), () -> "Add assertion message");
        Input input = greetMeOneWay.getInput();
        assertNotNull(input, () -> "Add assertion message");
        assertEquals("greetMeOneWayRequest", input.getName(), () -> "Add assertion message");
        Message message = input.getMessage();
        assertNotNull(message, () -> "Add assertion message");
        assertEquals("greetMeOneWayRequest", message.getQName().getLocalPart(), () -> "Add assertion message");
        assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        assertEquals(1, message.getParts().size(), () -> "Add assertion message");
        assertEquals("in", message.getPart("in").getName(), () -> "Add assertion message");
        Output output = greetMeOneWay.getOutput();
        assertNull(output, () -> "Add assertion message");
        assertEquals(0, greetMeOneWay.getFaults().size(), () -> "Add assertion message");
    }

    @Test
    public void testPingMeOperation() throws Exception {
        setupWSDL(WSDL_PATH);
        PortType portType = newDef.getPortType(new QName(newDef.getTargetNamespace(),
            "Greeter"));
        Operation pingMe = portType.getOperation("pingMe", "pingMeRequest", "pingMeResponse");
        assertNotNull(pingMe, () -> "Add assertion message");
        assertEquals("pingMe", pingMe.getName(), () -> "Add assertion message");
        Input input = pingMe.getInput();
        assertNotNull(input, () -> "Add assertion message");
        assertEquals("pingMeRequest", input.getName(), () -> "Add assertion message");
        Message message = input.getMessage();
        assertNotNull(message, () -> "Add assertion message");
        assertEquals("pingMeRequest", message.getQName().getLocalPart(), () -> "Add assertion message");
        assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        assertEquals(1, message.getParts().size(), () -> "Add assertion message");
        assertEquals("in", message.getPart("in").getName(), () -> "Add assertion message");
        Output output = pingMe.getOutput();
        assertNotNull(output, () -> "Add assertion message");
        assertEquals("pingMeResponse", output.getName(), () -> "Add assertion message");
        message = output.getMessage();
        // Line removed due to Duplicate Assert: assertNotNull(message, () -> "Add assertion message");
        assertEquals("pingMeResponse", message.getQName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        assertEquals(message.getParts().size(), 1, () -> "Add assertion message");
        assertEquals("out", message.getPart("out").getName(), () -> "Add assertion message");
        assertEquals(1, pingMe.getFaults().size(), () -> "Add assertion message");
        Fault fault = pingMe.getFault("pingMeFault");
        assertNotNull(fault, () -> "Add assertion message");
        assertEquals("pingMeFault", fault.getName(), () -> "Add assertion message");
        message = fault.getMessage();
        // Line removed due to Duplicate Assert: assertNotNull(message, () -> "Add assertion message");
        assertEquals("pingMeFault", message.getQName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(newDef.getTargetNamespace(), message.getQName().getNamespaceURI(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, message.getParts().size(), () -> "Add assertion message");
        assertEquals("faultDetail", message.getPart("faultDetail").getName(), () -> "Add assertion message");
        assertNull(message.getPart("faultDetail").getTypeName(), () -> "Add assertion message");
    }

    @Test
    public void testBinding() throws Exception {
        setupWSDL(WSDL_PATH);
        assertEquals(newDef.getBindings().size(), 1, () -> "Add assertion message");
        Binding binding = newDef.getBinding(new QName(newDef.getTargetNamespace(), "Greeter_SOAPBinding"));
        assertNotNull(binding, () -> "Add assertion message");
        assertEquals(4, binding.getBindingOperations().size(), () -> "Add assertion message");
    }

    @Test
    public void testBindingWithDifferentNamespaceImport() throws Exception {
        setupWSDL("wsdl2/person.wsdl");
        assertEquals(newDef.getBindings().size(), 1, () -> "Add assertion message");
        assertEquals("http://cxf.apache.org/samples/wsdl-first", newDef.getNamespace("ns3"), () -> "Add assertion message");
    }

    @Test
    public void testSchemas() throws Exception {
        setupWSDL(WSDL_PATH);
        Types types = newDef.getTypes();
        assertNotNull(types, () -> "Add assertion message");
        Collection<ExtensibilityElement> schemas =
            CastUtils.cast(types.getExtensibilityElements(), ExtensibilityElement.class);
        assertEquals(1, schemas.size(), () -> "Add assertion message");
        XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
        Element schemaElem = ((Schema)schemas.iterator().next()).getElement();
        XmlSchema newSchema = schemaCollection.read(schemaElem);
        assertEquals("http://apache.org/hello_world_soap_http/types",
                     newSchema.getTargetNamespace()
                     , () -> "Add assertion message");
    }

    @Test
    public void testXsdImportMultipleSchemas() throws Exception {
        setupWSDL(WSDL_XSD_IMPORT_PATH, true);

        Types types = newDef.getTypes();
        assertNotNull(types, () -> "Add assertion message");

        Collection<ExtensibilityElement> schemas = CastUtils.cast(types.getExtensibilityElements(),
                                                                  ExtensibilityElement.class);
        assertEquals(1, schemas.size(), () -> "Add assertion message");

        Schema schema = (Schema)schemas.iterator().next();

        assertEquals(1, schema.getImports().values().size(), () -> "Add assertion message");

        SchemaImport serviceTypesSchemaImport = getImport(schema.getImports(),
                "http://apache.org/hello_world_soap_http/servicetypes");

        Schema serviceTypesSchema = serviceTypesSchemaImport.getReferencedSchema();

        assertEquals(1, serviceTypesSchema.getImports().values().size(), () -> "Add assertion message");
        SchemaImport typesSchemaImport = getImport(serviceTypesSchema.getImports(),
                "http://apache.org/hello_world_soap_http/types");

        Schema typesSchema = typesSchemaImport.getReferencedSchema();

        Document doc = typesSchema.getElement().getOwnerDocument();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XMLStreamWriter writer = StaxUtils.createXMLStreamWriter(outputStream, "utf-8");
        StaxUtils.writeNode(doc, writer, true);
        writer.close();

        // this is a test to make sure any embedded namespaces are properly included
        String savedSchema = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        assertTrue(savedSchema.contains("http://www.w3.org/2005/05/xmlmime"), () -> "Add assertion message");

        SchemaImport types2SchemaImport = getImport(typesSchema.getImports(),
                "http://apache.org/hello_world_soap_http/types2");

        Schema types2Schema = types2SchemaImport.getReferencedSchema();
        assertNotNull(types2Schema, () -> "Add assertion message");
    }

    private SchemaImport getImport(Map<?, ?> imps, String key) {
        List<SchemaImport> s1 = CastUtils.cast((List<?>)imps.get(key));
        return s1.get(0);
    }

}