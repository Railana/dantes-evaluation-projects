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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.xmlschema.SchemaCollection;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.service.model.BindingFaultInfo;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.FaultInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.SchemaInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.ws.addressing.EndpointReferenceUtils;
import org.apache.ws.commons.schema.XmlSchemaElement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WSDLServiceBuilderTest {
    // TODO: reuse the wsdl in testutils and add the parameter order into one of the wsdl
    private static final Logger LOG = LogUtils.getLogger(WSDLServiceBuilderTest.class);
    private static final String WSDL_PATH = "hello_world.wsdl";
    private static final String BARE_WSDL_PATH = "hello_world_bare.wsdl";
    private static final String IMPORT_WSDL_PATH = "hello_world_schema_import.wsdl";
    private static final String MULTIPORT_WSDL_PATH = "hello_world_multiporttype.wsdl";
    private static final String NO_BODY_PARTS_WSDL_PATH = "no_body_parts.wsdl";

    private static final String EXTENSION_NAMESPACE = "http://cxf.apache.org/extension/ns";
    private static final QName EXTENSION_ATTR_BOOLEAN = new QName(EXTENSION_NAMESPACE, "booleanAttr");
    private static final QName EXTENSION_ATTR_STRING = new QName(EXTENSION_NAMESPACE, "stringAttr");
    private static final QName EXTENSION_ELEM = new QName(EXTENSION_NAMESPACE, "stringElem");

    private Definition def;

    private Service service;

    private ServiceInfo serviceInfo;
    private List<ServiceInfo> serviceInfos;

    private Bus bus;

    private BindingFactoryManager bindingFactoryManager;

    private DestinationFactoryManager destinationFactoryManager;

    protected void setUpBasic() throws Exception {
        setUpWSDL(WSDL_PATH, 0);
    }

    private void setUpWSDL(String wsdl, int serviceSeq) throws Exception {
        setUpDefinition(wsdl, serviceSeq);
        buildService();
    }

    private void setUpDefinition(String wsdl, int serviceSeq) throws Exception {
        URL url = getClass().getResource(wsdl);
        assertNotNull("could not find wsdl " + wsdl, url);
        String wsdlUrl = url.toString();
        LOG.info("the path of wsdl file is " + wsdlUrl);
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose", false);

        def = wsdlReader.readWSDL(new CatalogWSDLLocator(wsdlUrl));

        int seq = 0;
        for (Service serv : CastUtils.cast(def.getServices().values(), Service.class)) {
            if (serv != null) {
                service = serv;
                if (seq == serviceSeq) {
                    break;
                }
                seq++;
            }
        }
    }

    private void buildService() throws Exception {
        buildService(null);
    }

    private void buildService(QName endpointName) throws Exception {
        bus = mock(Bus.class);
        bindingFactoryManager = mock(BindingFactoryManager.class);
        destinationFactoryManager = mock(DestinationFactoryManager.class);
        DestinationFactory destinationFactory = mock(DestinationFactory.class);

        WSDLServiceBuilder wsdlServiceBuilder = new WSDLServiceBuilder(bus);

        when(bus.getExtension(BindingFactoryManager.class))
            .thenReturn(bindingFactoryManager);

        when(bus.getExtension(DestinationFactoryManager.class))
            .thenReturn(destinationFactoryManager);

        when(destinationFactoryManager
                        .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/"))
            .thenReturn(destinationFactory);


        serviceInfos = wsdlServiceBuilder.buildServices(def, service, endpointName);
        if (!serviceInfos.isEmpty()) {
            serviceInfo = serviceInfos.get(0);
        } else {
            serviceInfo = null;
        }
    }

    @Test
    public void testBuildServiceWithWrongEndpointName() throws Exception {
        setUpWSDL(WSDL_PATH, 0);
        buildService(new QName("http://apache.org/hello_world_soap_http",
                "NoExitSoapPort"));
        Integer magicNumber27450063 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("Should not build any serviceInfo.",magicNumber27450063, serviceInfos.size());
        assertEquals("Should not build any serviceInfo.", null, serviceInfo);
    }

    @Test
    public void testMultiPorttype() throws Exception {
        Integer magicNumber43001625 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        setUpWSDL(MULTIPORT_WSDL_PATH,magicNumber43001625
        assertEquals(2, serviceInfos.size(), () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testServiceInfo() throws Exception {
        setUpBasic();
        assertEquals("SOAPService", serviceInfo.getName().getLocalPart(), () -> "Add assertion message");
        assertEquals("http://apache.org/hello_world_soap_http", serviceInfo.getName().getNamespaceURI(), () -> "Add assertion message");
        assertEquals("http://apache.org/hello_world_soap_http", serviceInfo.getTargetNamespace(), () -> "Add assertion message");
        assertTrue(serviceInfo.getProperty(WSDLServiceBuilder.WSDL_DEFINITION) == def, () -> "Add assertion message");
        assertTrue(serviceInfo.getProperty(WSDLServiceBuilder.WSDL_SERVICE) == service, () -> "Add assertion message");

        assertEquals("Incorrect number of endpoints", 1, serviceInfo.getEndpoints().size());
        EndpointInfo ei = serviceInfo.getEndpoint(new QName("http://apache.org/hello_world_soap_http",
                "SoapPort"));
        assertNotNull(ei, () -> "Add assertion message");
        assertEquals("http://schemas.xmlsoap.org/wsdl/soap/", ei.getTransportId(), () -> "Add assertion message");
        assertNotNull(ei.getBinding(), () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testInterfaceInfo() throws Exception {
        setUpBasic();
        assertEquals("Greeter", serviceInfo.getInterface().getName().getLocalPart(), () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testOperationInfo() throws Exception {
        setUpBasic();
        QName name = new QName(serviceInfo.getName().getNamespaceURI(), "sayHi");
        assertEquals(4, serviceInfo.getInterface().getOperations().size(), () -> "Add assertion message");
        OperationInfo sayHi = serviceInfo.getInterface().getOperation(
                new QName(serviceInfo.getName().getNamespaceURI(), "sayHi"));
        assertNotNull(sayHi, () -> "Add assertion message");
        assertEquals(sayHi.getName(), name, () -> "Add assertion message");
        assertFalse(sayHi.isOneWay(), () -> "Add assertion message");
        assertTrue(sayHi.hasInput(), () -> "Add assertion message");
        assertTrue(sayHi.hasOutput(), () -> "Add assertion message");

        assertNull(sayHi.getParameterOrdering(), () -> "Add assertion message");

        name = new QName(serviceInfo.getName().getNamespaceURI(), "greetMe");
        OperationInfo greetMe = serviceInfo.getInterface().getOperation(name);
        assertNotNull(greetMe, () -> "Add assertion message");
        assertEquals(greetMe.getName(), name, () -> "Add assertion message");
        assertFalse(greetMe.isOneWay(), () -> "Add assertion message");
        assertTrue(greetMe.hasInput(), () -> "Add assertion message");
        assertTrue(greetMe.hasOutput(), () -> "Add assertion message");

        List<MessagePartInfo> inParts = greetMe.getInput().getMessageParts();
        assertEquals(1, inParts.size(), () -> "Add assertion message");
        MessagePartInfo part = inParts.get(0);
        assertNotNull(part.getXmlSchema(), () -> "Add assertion message");
        assertTrue(part.getXmlSchema() instanceof XmlSchemaElement, () -> "Add assertion message");

        List<MessagePartInfo> outParts = greetMe.getOutput().getMessageParts();
        assertEquals(1, outParts.size(), () -> "Add assertion message");
        part = outParts.get(0);
        // Line removed due to Duplicate Assert: assertNotNull(part.getXmlSchema(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(part.getXmlSchema() instanceof XmlSchemaElement, () -> "Add assertion message");

        assertTrue("greatMe should be wrapped", greetMe.isUnwrappedCapable());
        OperationInfo greetMeUnwrapped = greetMe.getUnwrappedOperation();

        assertNotNull(greetMeUnwrapped.getInput(), () -> "Add assertion message");
        assertNotNull(greetMeUnwrapped.getOutput(), () -> "Add assertion message");
        assertEquals("wrapped part not set", 1, greetMeUnwrapped.getInput().size());
        assertEquals("wrapped part not set", 1, greetMeUnwrapped.getOutput().size());
        assertEquals("wrapper part name wrong", "requestType", greetMeUnwrapped.getInput()
                .getMessagePartByIndex(0).getName().getLocalPart());
        assertEquals("wrapper part type name wrong", "MyStringType", greetMeUnwrapped.getInput()
                .getMessagePartByIndex(0).getTypeQName().getLocalPart());

        assertEquals("wrapper part name wrong", "responseType", greetMeUnwrapped.getOutput()
                .getMessagePartByIndex(0).getName().getLocalPart());
        assertEquals("wrapper part type name wrong", "string", greetMeUnwrapped.getOutput()
                .getMessagePartByIndex(0).getTypeQName().getLocalPart());

        name = new QName(serviceInfo.getName().getNamespaceURI(), "greetMeOneWay");
        OperationInfo greetMeOneWay = serviceInfo.getInterface().getOperation(name);
        assertNotNull(greetMeOneWay, () -> "Add assertion message");
        assertEquals(greetMeOneWay.getName(), name, () -> "Add assertion message");
        assertTrue(greetMeOneWay.isOneWay(), () -> "Add assertion message");
        assertTrue(greetMeOneWay.hasInput(), () -> "Add assertion message");
        assertFalse(greetMeOneWay.hasOutput(), () -> "Add assertion message");

        OperationInfo greetMeOneWayUnwrapped = greetMeOneWay.getUnwrappedOperation();
        assertNotNull(greetMeOneWayUnwrapped, () -> "Add assertion message");
        assertNotNull(greetMeOneWayUnwrapped.getInput(), () -> "Add assertion message");
        assertNull(greetMeOneWayUnwrapped.getOutput(), () -> "Add assertion message");
        assertEquals("wrapped part not set", 1, greetMeOneWayUnwrapped.getInput().size());
        assertEquals(new QName("http://apache.org/hello_world_soap_http/types", "requestType"),
                     greetMeOneWayUnwrapped.getInput().getMessagePartByIndex(0).getConcreteName(), () -> "Add assertion message");

        name = new QName(serviceInfo.getName().getNamespaceURI(), "pingMe");
        OperationInfo pingMe = serviceInfo.getInterface().getOperation(name);
        assertNotNull(pingMe, () -> "Add assertion message");
        assertEquals(pingMe.getName(), name, () -> "Add assertion message");
        assertFalse(pingMe.isOneWay(), () -> "Add assertion message");
        assertTrue(pingMe.hasInput(), () -> "Add assertion message");
        assertTrue(pingMe.hasOutput(), () -> "Add assertion message");

        assertNull(serviceInfo.getInterface().getOperation(new QName("what ever")), () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testBindingInfo() throws Exception {
        setUpBasic();
        assertEquals(1, serviceInfo.getBindings().size(), () -> "Add assertion message");
        BindingInfo bindingInfo = serviceInfo.getBindings().iterator().next();
        assertNotNull(bindingInfo, () -> "Add assertion message");
        assertEquals(bindingInfo.getInterface().getName().getLocalPart(), "Greeter", () -> "Add assertion message");
        assertEquals(bindingInfo.getName().getLocalPart(), "Greeter_SOAPBinding", () -> "Add assertion message");
        assertEquals(bindingInfo.getName().getNamespaceURI(), "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testBindingOperationInfo() throws Exception {
        setUpBasic();
        BindingInfo bindingInfo = serviceInfo.getBindings().iterator().next();
        Collection<BindingOperationInfo> bindingOperationInfos = bindingInfo.getOperations();
        assertNotNull(bindingOperationInfos, () -> "Add assertion message");
        assertEquals(bindingOperationInfos.size(), 4, () -> "Add assertion message");
        LOG.info("the binding operation is " + bindingOperationInfos.iterator().next().getName());

        QName name = new QName(serviceInfo.getName().getNamespaceURI(), "sayHi");
        BindingOperationInfo sayHi = bindingInfo.getOperation(name);
        assertNotNull(sayHi, () -> "Add assertion message");
        assertEquals(sayHi.getName(), name, () -> "Add assertion message");

        name = new QName(serviceInfo.getName().getNamespaceURI(), "greetMe");
        BindingOperationInfo greetMe = bindingInfo.getOperation(name);
        assertNotNull(greetMe, () -> "Add assertion message");
        assertEquals(greetMe.getName(), name, () -> "Add assertion message");

        name = new QName(serviceInfo.getName().getNamespaceURI(), "greetMeOneWay");
        BindingOperationInfo greetMeOneWay = bindingInfo.getOperation(name);
        assertNotNull(greetMeOneWay, () -> "Add assertion message");
        assertEquals(greetMeOneWay.getName(), name, () -> "Add assertion message");

        name = new QName(serviceInfo.getName().getNamespaceURI(), "pingMe");
        BindingOperationInfo pingMe = bindingInfo.getOperation(name);
        assertNotNull(pingMe, () -> "Add assertion message");
        assertEquals(pingMe.getName(), name, () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testBindingMessageInfo() throws Exception {
        setUpBasic();
        BindingInfo bindingInfo = serviceInfo.getBindings().iterator().next();

        QName name = new QName(serviceInfo.getName().getNamespaceURI(), "sayHi");
        BindingOperationInfo sayHi = bindingInfo.getOperation(name);
        BindingMessageInfo input = sayHi.getInput();
        assertNotNull(input, () -> "Add assertion message");
        assertEquals(input.getMessageInfo().getName().getLocalPart(), "sayHiRequest", () -> "Add assertion message");
        assertEquals(input.getMessageInfo().getName().getNamespaceURI(),
                "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        assertEquals(input.getMessageInfo().getMessageParts().size(), 1, () -> "Add assertion message");
        assertEquals(input.getMessageInfo().getMessageParts().get(0).getName().getLocalPart(), "in", () -> "Add assertion message");
        assertEquals(input.getMessageInfo().getMessageParts().get(0).getName().getNamespaceURI(),
                "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        assertTrue(input.getMessageInfo().getMessageParts().get(0).isElement(), () -> "Add assertion message");
        QName elementName = input.getMessageInfo().getMessageParts().get(0).getElementQName();
        assertEquals(elementName.getLocalPart(), "sayHi", () -> "Add assertion message");
        assertEquals(elementName.getNamespaceURI(), "http://apache.org/hello_world_soap_http/types", () -> "Add assertion message");

        BindingMessageInfo output = sayHi.getOutput();
        assertNotNull(output, () -> "Add assertion message");
        assertEquals(output.getMessageInfo().getName().getLocalPart(), "sayHiResponse", () -> "Add assertion message");
        assertEquals(output.getMessageInfo().getName().getNamespaceURI(),
                "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        assertEquals(output.getMessageInfo().getMessageParts().size(), 1, () -> "Add assertion message");
        assertEquals(output.getMessageInfo().getMessageParts().get(0).getName().getLocalPart(), "out", () -> "Add assertion message");
        assertEquals(output.getMessageInfo().getMessageParts().get(0).getName().getNamespaceURI(),
                "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        assertTrue(output.getMessageInfo().getMessageParts().get(0).isElement(), () -> "Add assertion message");
        elementName = output.getMessageInfo().getMessageParts().get(0).getElementQName();
        assertEquals(elementName.getLocalPart(), "sayHiResponse", () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(elementName.getNamespaceURI(), "http://apache.org/hello_world_soap_http/types", () -> "Add assertion message");

        assertTrue(sayHi.getFaults().isEmpty(), () -> "Add assertion message");

        name = new QName(serviceInfo.getName().getNamespaceURI(), "pingMe");
        BindingOperationInfo pingMe = bindingInfo.getOperation(name);
        assertNotNull(pingMe, () -> "Add assertion message");
        assertEquals(1, pingMe.getFaults().size(), () -> "Add assertion message");
        BindingFaultInfo fault = pingMe.getFaults().iterator().next();

        assertNotNull(fault, () -> "Add assertion message");
        assertEquals(fault.getFaultInfo().getName().getLocalPart(), "pingMeFault", () -> "Add assertion message");
        assertEquals(fault.getFaultInfo().getName().getNamespaceURI(),
                "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        assertEquals(fault.getFaultInfo().getMessageParts().size(), 1, () -> "Add assertion message");
        assertEquals(fault.getFaultInfo().getMessageParts().get(0).getName().getLocalPart(), "faultDetail", () -> "Add assertion message");
        assertEquals(fault.getFaultInfo().getMessageParts().get(0).getName().getNamespaceURI(),
                "http://apache.org/hello_world_soap_http", () -> "Add assertion message");
        assertTrue(fault.getFaultInfo().getMessageParts().get(0).isElement(), () -> "Add assertion message");
        elementName = fault.getFaultInfo().getMessageParts().get(0).getElementQName();
        assertEquals(elementName.getLocalPart(), "faultDetail", () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(elementName.getNamespaceURI(), "http://apache.org/hello_world_soap_http/types", () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testSchema() throws Exception {
        setUpBasic();
        SchemaCollection schemas = serviceInfo.getXmlSchemaCollection();
        assertNotNull(schemas, () -> "Add assertion message");
        assertEquals(1, serviceInfo.getSchemas().size(), () -> "Add assertion message");
        SchemaInfo schemaInfo = serviceInfo.getSchemas().iterator().next();
        assertNotNull(schemaInfo, () -> "Add assertion message");
        assertEquals(schemaInfo.getNamespaceURI(), "http://apache.org/hello_world_soap_http/types", () -> "Add assertion message");
        assertEquals(schemas.read(schemaInfo.getElement()).getTargetNamespace(),
                "http://apache.org/hello_world_soap_http/types", () -> "Add assertion message");
        // add below code to test the creation of javax.xml.validation.Schema
        // with schema in serviceInfo
        Schema schema = EndpointReferenceUtils.getSchema(serviceInfo);
        assertNotNull(schema, () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testNoBodyParts() throws Exception {
        setUpWSDL(NO_BODY_PARTS_WSDL_PATH, 0);
        QName messageName = new QName("urn:org:apache:cxf:no_body_parts/wsdl",
                                      "operation1Request");
        MessageInfo mi = serviceInfo.getMessage(messageName);
        QName partName = new QName("urn:org:apache:cxf:no_body_parts/wsdl",
                                   "mimeAttachment");
        MessagePartInfo pi = mi.getMessagePart(partName);
        QName typeName =
            new QName("http://www.w3.org/2001/XMLSchema",
                      "base64Binary");
        assertEquals(typeName, pi.getTypeQName(), () -> "Add assertion message");
        assertNull(pi.getElementQName(), () -> "Add assertion message");
    }

    @Test
    public void testBare() throws Exception {
        setUpWSDL(BARE_WSDL_PATH, 0);
        BindingInfo bindingInfo = serviceInfo.getBindings().iterator().next();
        Collection<BindingOperationInfo> bindingOperationInfos = bindingInfo.getOperations();
        assertNotNull(bindingOperationInfos, () -> "Add assertion message");
        assertEquals(bindingOperationInfos.size(), 1, () -> "Add assertion message");
        LOG.info("the binding operation is " + bindingOperationInfos.iterator().next().getName());
        QName name = new QName(serviceInfo.getName().getNamespaceURI(), "greetMe");
        BindingOperationInfo greetMe = bindingInfo.getOperation(name);
        assertNotNull(greetMe, () -> "Add assertion message");
        assertEquals("greetMe OperationInfo name error", greetMe.getName(), name);
        assertFalse("greetMe should be a Unwrapped operation ", greetMe.isUnwrappedCapable());

        assertNotNull(serviceInfo.getXmlSchemaCollection(), () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testImport() throws Exception {
        // rewrite the schema1.xsd to import schema2.xsd with absolute path.
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(this.getClass().getResourceAsStream("./s1/s2/schema2.xsd"));
        Element schemaImport = null;

        Node node = doc.getFirstChild();
        while (node != null) {
            if (node instanceof Element) {
                schemaImport = DOMUtils.getFirstElement(node);
            }
            node = node.getNextSibling();
        }

        if (schemaImport == null) {
            fail("Can't find import element");
        }
        String filePath = this.getClass().getResource("./s1/s2/s4/schema4.xsd").toURI().getPath();
        String importPath = schemaImport.getAttributeNode("schemaLocation").getValue();
        if (!new URI(URLEncoder.encode(importPath, "utf-8")).isAbsolute()) {
            schemaImport.getAttributeNode("schemaLocation").setNodeValue("file:" + filePath);
            String fileStr = this.getClass().getResource("./s1/s2/schema2.xsd").toURI().getPath();
            fileStr = URLDecoder.decode(fileStr, "utf-8");
            File file = new File(fileStr);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fout = new FileOutputStream(file);
            StaxUtils.writeTo(doc, fout);
            fout.flush();
            fout.close();
        }
        setUpWSDL(IMPORT_WSDL_PATH, 0);
        assertNotNull(serviceInfo.getSchemas(), () -> "Add assertion message");
        Element ele = serviceInfo.getSchemas().iterator().next().getElement();
        assertNotNull(ele, () -> "Add assertion message");
        Schema schema = EndpointReferenceUtils.getSchema(serviceInfo, null);
        assertNotNull(schema, () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }


    @Test
    public void testDiffPortTypeNsImport() throws Exception {
        setUpWSDL("/DiffPortTypeNs.wsdl", 0);
        doDiffPortTypeNsImport();
        setUpWSDL("/DiffPortTypeNs.wsdl", 1);
        doDiffPortTypeNsImport();
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    private void doDiffPortTypeNsImport() {
        if (serviceInfo.getName().getLocalPart().endsWith("Rpc")) {
            String ns = serviceInfo.getInterface().getName().getNamespaceURI();
            OperationInfo oi = serviceInfo.getInterface().getOperation(new QName(ns, "NewOperationRpc"));
            assertNotNull(oi);
            ns = oi.getInput().getName().getNamespaceURI();
            MessagePartInfo mpi = oi.getInput().getMessagePart(new QName(ns, "NewOperationRequestRpc"));
            assertNotNull(mpi);
        } else {
            String ns = serviceInfo.getInterface().getName().getNamespaceURI();
            OperationInfo oi = serviceInfo.getInterface().getOperation(new QName(ns, "NewOperation"));
            assertNotNull(oi);
            ns = oi.getInput().getName().getNamespaceURI();
            MessagePartInfo mpi = oi.getInput().getMessagePart(new QName(ns, "NewOperationRequest"));
            assertNotNull(mpi);
        }
    }

    @Test
    public void testParameterOrder() throws Exception {
        String ns = "http://apache.org/hello_world_xml_http/bare";
        setUpWSDL("hello_world_xml_bare.wsdl", 0);

        OperationInfo operation = serviceInfo.getInterface().getOperation(new QName(ns,
                                                                                    "testTriPart"));
        assertNotNull(operation, () -> "Add assertion message");
        List<MessagePartInfo> parts = operation.getInput().getMessageParts();
        assertNotNull(parts, () -> "Add assertion message");
        assertEquals(3, parts.size(), () -> "Add assertion message");
        assertEquals("in3", parts.get(0).getName().getLocalPart(), () -> "Add assertion message");
        assertEquals("in1", parts.get(1).getName().getLocalPart(), () -> "Add assertion message");
        assertEquals("in2", parts.get(2).getName().getLocalPart(), () -> "Add assertion message");

        List<String> order = operation.getParameterOrdering();
        assertNotNull(order, () -> "Add assertion message");
        Integer magicNumber72443405 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber72443405, order.size(), () -> "Add assertion message");
        assertEquals("in1", order.get(0), () -> "Add assertion message");
        assertEquals("in3", order.get(1), () -> "Add assertion message");
        assertEquals("in2", order.get(2), () -> "Add assertion message");

        parts = operation.getInput().getOrderedParts(order);
        // Line removed due to Duplicate Assert: assertNotNull(parts, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(3, parts.size(), () -> "Add assertion message");
        assertEquals("in1", parts.get(0).getName().getLocalPart(), () -> "Add assertion message");
        assertEquals("in3", parts.get(1).getName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("in2", parts.get(2).getName().getLocalPart(), () -> "Add assertion message");

        operation = serviceInfo.getInterface().getOperation(new QName(ns,
                                                                      "testTriPartNoOrder"));
        // Line removed due to Duplicate Assert: assertNotNull(operation, () -> "Add assertion message");
        parts = operation.getInput().getMessageParts();
        // Line removed due to Duplicate Assert: assertNotNull(parts, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(3, parts.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("in3", parts.get(0).getName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("in1", parts.get(1).getName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("in2", parts.get(2).getName().getLocalPart(), () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/http/");
    }

    @Test
    public void testParameterOrder2() throws Exception {
        setUpWSDL("header2.wsdl", 0);
        String ns = "http://apache.org/header2";
        OperationInfo operation = serviceInfo.getInterface().getOperation(new QName(ns, "headerMethod"));
        assertNotNull(operation, () -> "Add assertion message");
        List<MessagePartInfo> parts = operation.getInput().getMessageParts();
        assertNotNull(parts, () -> "Add assertion message");
        assertEquals(2, parts.size(), () -> "Add assertion message");
        assertEquals("header_info", parts.get(0).getName().getLocalPart(), () -> "Add assertion message");
        assertEquals("the_request", parts.get(1).getName().getLocalPart(), () -> "Add assertion message");
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    @Test
    public void testExtensions() throws Exception {
        setUpWSDL("hello_world_ext.wsdl", 0);

        String ns = "http://apache.org/hello_world_soap_http";
        QName pingMeOpName = new QName(ns, "pingMe");
        QName greetMeOpName = new QName(ns, "greetMe");
        QName faultName = new QName(ns, "pingMeFault");

        // portType extensions

        InterfaceInfo ii = serviceInfo.getInterface();
        Integer magicNumber85505606 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber85505606, ii.getExtensionAttributes().size(), () -> "Add assertion message");
        assertNotNull(ii.getExtensionAttribute(EXTENSION_ATTR_BOOLEAN), () -> "Add assertion message");
        assertNotNull(ii.getExtensionAttribute(EXTENSION_ATTR_STRING), () -> "Add assertion message");
        assertEquals(1, ii.getExtensors(UnknownExtensibilityElement.class).size(), () -> "Add assertion message");
        assertEquals(EXTENSION_ELEM, ii.getExtensor(UnknownExtensibilityElement.class).getElementType(), () -> "Add assertion message");

        // portType/operation extensions

        OperationInfo oi = ii.getOperation(pingMeOpName);
        assertPortTypeOperationExtensions(oi, true);
        assertPortTypeOperationExtensions(ii.getOperation(greetMeOpName), false);

        // portType/operation/[input|output|fault] extensions

        assertPortTypeOperationMessageExtensions(oi, true, true, faultName);
        assertPortTypeOperationMessageExtensions(ii.getOperation(greetMeOpName), false, true, null);

        // service extensions

        assertEquals(1, serviceInfo.getExtensionAttributes().size(), () -> "Add assertion message");
        assertNotNull(serviceInfo.getExtensionAttribute(EXTENSION_ATTR_STRING), () -> "Add assertion message");
        assertEquals(1, serviceInfo.getExtensors(UnknownExtensibilityElement.class).size(), () -> "Add assertion message");
        assertEquals(EXTENSION_ELEM,
            serviceInfo.getExtensor(UnknownExtensibilityElement.class).getElementType(), () -> "Add assertion message");

        // service/port extensions

        EndpointInfo ei = serviceInfo.getEndpoints().iterator().next();
        assertEquals(1, ei.getExtensionAttributes().size(), () -> "Add assertion message");
        assertNotNull(ei.getExtensionAttribute(EXTENSION_ATTR_STRING), () -> "Add assertion message");
        assertEquals(1, ei.getExtensors(UnknownExtensibilityElement.class).size(), () -> "Add assertion message");
        assertEquals(EXTENSION_ELEM, ei.getExtensor(UnknownExtensibilityElement.class).getElementType(), () -> "Add assertion message");

        // binding extensions

        BindingInfo bi = ei.getBinding();
        // REVISIT: bug in wsdl4j?
        // getExtensionAttributes on binding element returns an empty map
        // assertEquals(1, bi.getExtensionAttributes().size());
        // assertNotNull(bi.getExtensionAttribute(EXTENSION_ATTR_STRING));
        assertEquals(1, bi.getExtensors(UnknownExtensibilityElement.class).size(), () -> "Add assertion message");
        assertEquals(EXTENSION_ELEM, bi.getExtensor(UnknownExtensibilityElement.class).getElementType(), () -> "Add assertion message");

        // binding/operation extensions

        BindingOperationInfo boi = bi.getOperation(pingMeOpName);
        assertBindingOperationExtensions(boi, true);
        assertBindingOperationExtensions(bi.getOperation(greetMeOpName), false);

        // binding/operation/[input|output|fault] extensions

        assertBindingOperationMessageExtensions(boi, true, true, faultName);
        assertBindingOperationMessageExtensions(bi.getOperation(greetMeOpName), false, true, null);
        verify(destinationFactoryManager, atLeastOnce())
            .getDestinationFactory("http://schemas.xmlsoap.org/wsdl/soap/");
    }

    private void assertPortTypeOperationExtensions(OperationInfo oi, boolean expectExtensions) {
        if (expectExtensions) {
            assertEquals(1, oi.getExtensionAttributes().size());
            assertNotNull(oi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertEquals(1, oi.getExtensors(UnknownExtensibilityElement.class).size());
            assertEquals(EXTENSION_ELEM, oi.getExtensor(UnknownExtensibilityElement.class).getElementType());
        } else {
            assertNull(oi.getExtensionAttributes());
            assertNull(oi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertNull(oi.getExtensors(UnknownExtensibilityElement.class));
            assertNull(oi.getExtensor(UnknownExtensibilityElement.class));
        }
    }

    private void assertBindingOperationExtensions(BindingOperationInfo boi, boolean expectExtensions) {
        if (expectExtensions) {
            assertEquals(1, boi.getExtensionAttributes().size());
            assertNotNull(boi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertEquals(1, boi.getExtensors(UnknownExtensibilityElement.class).size());
            assertEquals(EXTENSION_ELEM, boi.getExtensor(UnknownExtensibilityElement.class).getElementType());
        } else {
            assertNull(boi.getExtensionAttributes());
            assertNull(boi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertEquals(0, boi.getExtensors(UnknownExtensibilityElement.class).size());
            assertNull(boi.getExtensor(UnknownExtensibilityElement.class));
        }
    }

    private void assertPortTypeOperationMessageExtensions(OperationInfo oi, boolean expectExtensions,
        boolean hasOutput, QName fault) {

        MessageInfo mi = oi.getInput();
        if (expectExtensions) {
            assertEquals(1, mi.getExtensionAttributes().size());
            assertNotNull(mi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertEquals(1, mi.getExtensors(UnknownExtensibilityElement.class).size());
            assertEquals(EXTENSION_ELEM, mi.getExtensor(UnknownExtensibilityElement.class).getElementType());
        } else {
            assertNull(mi.getExtensionAttributes());
            assertNull(mi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertNull(mi.getExtensors(UnknownExtensibilityElement.class));
            assertNull(mi.getExtensor(UnknownExtensibilityElement.class));
        }

        if (hasOutput) {
            mi = oi.getOutput();
            if (expectExtensions) {
                assertEquals(1, mi.getExtensionAttributes().size());
                assertNotNull(mi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertEquals(1, mi.getExtensors(UnknownExtensibilityElement.class).size());
                assertEquals(EXTENSION_ELEM,
                    mi.getExtensor(UnknownExtensibilityElement.class).getElementType());
            } else {
                assertNull(mi.getExtensionAttributes());
                assertNull(mi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertNull(mi.getExtensors(UnknownExtensibilityElement.class));
                assertNull(mi.getExtensor(UnknownExtensibilityElement.class));
            }
        }

        if (null != fault) {
            FaultInfo fi = oi.getFault(fault);
            if (expectExtensions) {
                assertEquals(1, fi.getExtensionAttributes().size());
                assertNotNull(fi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertEquals(1, fi.getExtensors(UnknownExtensibilityElement.class).size());
                assertEquals(EXTENSION_ELEM,
                    fi.getExtensor(UnknownExtensibilityElement.class).getElementType());
            } else {
                assertNull(fi.getExtensionAttributes());
                assertNull(fi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertNull(fi.getExtensors(UnknownExtensibilityElement.class));
                assertNull(fi.getExtensor(UnknownExtensibilityElement.class));
            }
        }
    }

    private void assertBindingOperationMessageExtensions(BindingOperationInfo boi, boolean expectExtensions,
        boolean hasOutput, QName fault) {

        BindingMessageInfo bmi = boi.getInput();
        if (expectExtensions) {
            // REVISIT: bug in wsdl4j?
            // getExtensionAttributes on binding/operation/input element returns an empty map
            // assertEquals(1, bmi.getExtensionAttributes().size());
            // assertNotNull(bmi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertEquals(1, bmi.getExtensors(UnknownExtensibilityElement.class).size());
            assertEquals(EXTENSION_ELEM, bmi.getExtensor(UnknownExtensibilityElement.class).getElementType());
        } else {
            assertNull(bmi.getExtensionAttributes());
            assertNull(bmi.getExtensionAttribute(EXTENSION_ATTR_STRING));
            assertEquals(0, bmi.getExtensors(UnknownExtensibilityElement.class).size());
            assertNull(bmi.getExtensor(UnknownExtensibilityElement.class));
        }

        if (hasOutput) {
            bmi = boi.getOutput();
            if (expectExtensions) {
                // REVISIT: bug in wsdl4j?
                // getExtensionAttributes on binding/operation/output element returns an empty map
                // assertEquals(1, bmi.getExtensionAttributes().size());
                // assertNotNull(bmi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertEquals(1, bmi.getExtensors(UnknownExtensibilityElement.class).size());
                assertEquals(EXTENSION_ELEM,
                    bmi.getExtensor(UnknownExtensibilityElement.class).getElementType());
            } else {
                assertNull(bmi.getExtensionAttributes());
                assertNull(bmi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertEquals(0, bmi.getExtensors(UnknownExtensibilityElement.class).size());
                assertNull(bmi.getExtensor(UnknownExtensibilityElement.class));
            }
        }

        if (null != fault) {
            BindingFaultInfo bfi = boi.getFault(fault);
            if (expectExtensions) {
                assertEquals(1, bfi.getExtensionAttributes().size());
                assertNotNull(bfi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertEquals(1, bfi.getExtensors(UnknownExtensibilityElement.class).size());
                assertEquals(EXTENSION_ELEM,
                    bfi.getExtensor(UnknownExtensibilityElement.class).getElementType());
            } else {
                assertNull(bfi.getExtensionAttributes());
                assertNull(bfi.getExtensionAttribute(EXTENSION_ATTR_STRING));
                assertNull(bfi.getExtensors(UnknownExtensibilityElement.class));
                assertNull(bfi.getExtensor(UnknownExtensibilityElement.class));
            }
        }
    }


}