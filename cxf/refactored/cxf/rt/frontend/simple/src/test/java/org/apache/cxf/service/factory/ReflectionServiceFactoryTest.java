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
package org.apache.cxf.service.factory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.binding.soap.model.SoapOperationInfo;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.no_body_parts.NoBodyPartsImpl;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.wsdl.service.factory.AbstractServiceConfiguration;
import org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean;
import org.apache.ws.commons.schema.XmlSchemaElement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReflectionServiceFactoryTest extends AbstractSimpleFrontendTest {
    private ReflectionServiceFactoryBean serviceFactory;

    @Test
    public void testUnwrappedBuild() throws Exception {
        Service service = createService(false);

        ServiceInfo si = service.getServiceInfos().get(0);
        InterfaceInfo intf = si.getInterface();

        Integer magicNumber72920195 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber72920195, intf.getOperations().size(), () -> "Add assertion message");

        String ns = si.getName().getNamespaceURI();
        OperationInfo sayHelloOp = intf.getOperation(new QName(ns, "sayHello"));
        assertNotNull(sayHelloOp, () -> "Add assertion message");

        assertEquals("sayHello", sayHelloOp.getInput().getName().getLocalPart(), () -> "Add assertion message");

        List<MessagePartInfo> messageParts = sayHelloOp.getInput().getMessageParts();
        assertEquals(0, messageParts.size(), () -> "Add assertion message");

        // test output
        messageParts = sayHelloOp.getOutput().getMessageParts();
        assertEquals(1, messageParts.size(), () -> "Add assertion message");
        assertEquals("sayHelloResponse", sayHelloOp.getOutput().getName().getLocalPart(), () -> "Add assertion message");

        MessagePartInfo mpi = messageParts.get(0);
        assertEquals("return", mpi.getName().getLocalPart(), () -> "Add assertion message");
        assertEquals(String.class, mpi.getTypeClass(), () -> "Add assertion message");


        OperationInfo op = si.getInterface().getOperation(new QName(ns, "echoWithExchange"));
        assertEquals(1, op.getInput().getMessageParts().size(), () -> "Add assertion message");
    }

    @Test
    public void testWrappedBuild() throws Exception {
        Service service = createService(true);

        ServiceInfo si = service.getServiceInfos().get(0);
        InterfaceInfo intf = si.getInterface();

        assertEquals(4, intf.getOperations().size(), () -> "Add assertion message");

        String ns = si.getName().getNamespaceURI();
        OperationInfo sayHelloOp = intf.getOperation(new QName(ns, "sayHello"));
        assertNotNull(sayHelloOp, () -> "Add assertion message");

        assertEquals("sayHello", sayHelloOp.getInput().getName().getLocalPart(), () -> "Add assertion message");

        List<MessagePartInfo> messageParts = sayHelloOp.getInput().getMessageParts();
        assertEquals(1, messageParts.size(), () -> "Add assertion message");
        assertNotNull(messageParts.get(0).getXmlSchema(), () -> "Add assertion message");

        // test unwrapping
        assertTrue(sayHelloOp.isUnwrappedCapable(), () -> "Add assertion message");

        OperationInfo unwrappedOp = sayHelloOp.getUnwrappedOperation();
        assertEquals("sayHello", unwrappedOp.getInput().getName().getLocalPart(), () -> "Add assertion message");

        messageParts = unwrappedOp.getInput().getMessageParts();
        assertEquals(0, messageParts.size(), () -> "Add assertion message");

        // test output
        messageParts = sayHelloOp.getOutput().getMessageParts();
        // Line removed due to Duplicate Assert: assertEquals(1, messageParts.size(), () -> "Add assertion message");
        assertEquals("sayHelloResponse", sayHelloOp.getOutput().getName().getLocalPart(), () -> "Add assertion message");

        messageParts = unwrappedOp.getOutput().getMessageParts();
        assertEquals("sayHelloResponse", unwrappedOp.getOutput().getName().getLocalPart(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, messageParts.size(), () -> "Add assertion message");
        MessagePartInfo mpi = messageParts.get(0);
        assertEquals("return", mpi.getName().getLocalPart(), () -> "Add assertion message");
        assertEquals(String.class, mpi.getTypeClass(), () -> "Add assertion message");
    }

    private Service createService(boolean wrapped) throws JAXBException {
        serviceFactory = new ReflectionServiceFactoryBean();
        serviceFactory.setBus(getBus());
        serviceFactory.setServiceClass(HelloService.class);
        serviceFactory.setWrapped(wrapped);

        Map<String, Object> props = new HashMap<>();
        props.put("test", "test");
        serviceFactory.setProperties(props);

        return serviceFactory.create();
    }
    @Test
    public void testWSDLRequired() throws Exception {

        getBus().setProperty("requireExplicitContractLocation", Boolean.TRUE);
        try {
            createService(true);
            fail("Should have failed");
        } catch (ServiceConstructionException ex) {
            getBus().setProperty("requireExplicitContractLocation", null);
        }


    }
    @Test
    public void testServerFactoryBean() throws Exception {
        Service service = createService(true);
        assertEquals("test", service.get("test"), () -> "Add assertion message");

        ServerFactoryBean svrBean = new ServerFactoryBean();
        svrBean.setAddress("http://localhost/Hello");
        svrBean.setServiceFactory(serviceFactory);
        svrBean.setServiceBean(new HelloServiceImpl());
        svrBean.setBus(getBus());

        Map<String, Object> props = new HashMap<>();
        props.put("test", "test");
        serviceFactory.setProperties(props);
        svrBean.setProperties(props);

        Server server = svrBean.create();
        assertNotNull(server, () -> "Add assertion message");
        Map<QName, Endpoint> eps = service.getEndpoints();
        assertEquals(1, eps.size(), () -> "Add assertion message");

        Endpoint ep = eps.values().iterator().next();
        EndpointInfo endpointInfo = ep.getEndpointInfo();

        assertEquals("test", ep.get("test"), () -> "Add assertion message");

        BindingInfo b = endpointInfo.getService().getBindings().iterator().next();

        assertTrue(b instanceof SoapBindingInfo, () -> "Add assertion message");

        SoapBindingInfo sb = (SoapBindingInfo) b;
        assertEquals("HelloServiceSoapBinding", b.getName().getLocalPart(), () -> "Add assertion message");
        assertEquals("document", sb.getStyle(), () -> "Add assertion message");

        assertEquals(4, b.getOperations().size(), () -> "Add assertion message");

        BindingOperationInfo bop = b.getOperations().iterator().next();
        SoapOperationInfo sop = bop.getExtensor(SoapOperationInfo.class);
        assertNotNull(sop, () -> "Add assertion message");
        assertEquals("", sop.getAction(), () -> "Add assertion message");
        assertEquals("document", sop.getStyle(), () -> "Add assertion message");
    }

    @Test(expected = ServiceConstructionException.class)
    public void testDocLiteralPartWithType() throws Exception {
        serviceFactory = new ReflectionServiceFactoryBean();
        serviceFactory.setBus(getBus());
        serviceFactory.setServiceClass(NoBodyPartsImpl.class);
        serviceFactory.getServiceConfigurations().add(0,
            new AbstractServiceConfiguration() {
                @Override
                public Boolean isWrapped() {
                    return Boolean.FALSE;
                }

                @Override
                public Boolean isWrapped(Method m) {
                    return Boolean.FALSE;
                }
            });
        Service service = serviceFactory.create();
        ServiceInfo serviceInfo =
            service.getServiceInfos().get(0);
        QName qname = new QName("urn:org:apache:cxf:no_body_parts/wsdl",
                                "operation1");
        MessageInfo mi = serviceInfo.getMessage(qname);
        qname = new QName("urn:org:apache:cxf:no_body_parts/wsdl",
            "mimeAttachment");
        MessagePartInfo mpi = mi.getMessagePart(qname);
        QName elementQName = mpi.getElementQName();
        XmlSchemaElement element =
            serviceInfo.getXmlSchemaCollection().getElementByQName(elementQName);
        assertNotNull(element, () -> "Add assertion message");

    }
}