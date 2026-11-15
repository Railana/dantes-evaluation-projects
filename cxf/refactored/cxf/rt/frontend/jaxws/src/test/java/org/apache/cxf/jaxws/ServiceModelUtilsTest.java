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

package org.apache.cxf.jaxws;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.calculator.CalculatorImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceModelUtil;
import org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean;
import org.apache.hello_world_soap_http.GreeterImpl;
import org.apache.hello_world_soap_http.RPCLitGreeterImpl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServiceModelUtilsTest extends AbstractJaxWsTest {

    Message message;
    Exchange exchange;
    ReflectionServiceFactoryBean bean;

    @Before
    public void setUp() throws Exception {
        super.setUpBus();

        message = new MessageImpl();
        // Removed line due to General Fixture smell: exchange = new ExchangeImpl();
        message.setExchange(exchange);

        // Removed line due to General Fixture smell: bean = new JaxWsServiceFactoryBean();
        bean.setBus(getBus());
    }

    private Service getService(URL wsdl, Class<?> implClz, QName port) throws EndpointException {
        bean = new JaxWsServiceFactoryBean();
        exchange = new ExchangeImpl();
        assertNotNull(wsdl);
        bean.setWsdlURL(wsdl.toString());
        bean.setServiceClass(implClz);
        Service service = bean.create();
        EndpointInfo endpointInfo = service.getServiceInfos().get(0).getEndpoint(port);
        Endpoint endpoint = new EndpointImpl(getBus(), service, endpointInfo);
        exchange.put(Service.class, service);
        exchange.put(Endpoint.class, endpoint);
        return service;
    }

    @Test
    public void testGetOperationInputPartNamesWrapped() throws Exception {
        getService(getClass().getResource("/wsdl/hello_world.wsdl"),
                   GreeterImpl.class,
                   new QName("http://apache.org/hello_world_soap_http", "SoapPort"));

        BindingOperationInfo operation = ServiceModelUtil.getOperation(message.getExchange(), "greetMe");
        assertNotNull(operation, () -> "Add assertion message");
        List<String> names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        assertNotNull(names, () -> "Add assertion message");
        Integer magicNumber87359300 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber87359300, names.size(), () -> "Add assertion message");
        assertEquals("requestType", names.get(0), () -> "Add assertion message");

        operation = ServiceModelUtil.getOperation(message.getExchange(), "sayHi");
        // Line removed due to Duplicate Assert: assertNotNull(operation, () -> "Add assertion message");
        names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        // Line removed due to Duplicate Assert: assertNotNull(names, () -> "Add assertion message");
        assertEquals(0, names.size(), () -> "Add assertion message");
    }

    @Test
    public void testGetOperationInputPartNamesWrapped2() throws Exception {
        getService(getClass().getResource("/wsdl/calculator.wsdl"),
                   CalculatorImpl.class,
                   new QName("http://apache.org/cxf/calculator", "CalculatorPort"));

        BindingOperationInfo operation = ServiceModelUtil.getOperation(message.getExchange(), "add");
        assertNotNull(operation, () -> "Add assertion message");
        List<String> names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        assertNotNull(names, () -> "Add assertion message");
        Integer magicNumber74417407 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber74417407, names.size(), () -> "Add assertion message");
        assertEquals("arg0", names.get(0), () -> "Add assertion message");
        assertEquals("arg1", names.get(1), () -> "Add assertion message");
    }

    @Test
    public void testGetOperationInputPartNamesBare() throws Exception {
        getService(getClass().getResource("/wsdl/hello_world_xml_bare.wsdl"),
                   org.apache.hello_world_xml_http.bare.GreeterImpl.class,
                   new QName("http://apache.org/hello_world_xml_http/bare", "XMLPort"));
        BindingOperationInfo operation = ServiceModelUtil.getOperation(message.getExchange(), "greetMe");
        assertNotNull(operation, () -> "Add assertion message");
        List<String> names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        assertNotNull(names, () -> "Add assertion message");
        assertEquals(1, names.size(), () -> "Add assertion message");
        assertEquals("requestType", names.get(0), () -> "Add assertion message");

        operation = ServiceModelUtil.getOperation(message.getExchange(), "sayHi");
        // Line removed due to Duplicate Assert: assertNotNull(operation, () -> "Add assertion message");
        names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        // Line removed due to Duplicate Assert: assertNotNull(names, () -> "Add assertion message");
        assertEquals(0, names.size(), () -> "Add assertion message");
    }


    @Test
    public void testGetOperationInputPartNamesRpc() throws Exception {
        getService(getClass().getResource("/wsdl/hello_world_rpc_lit.wsdl"),
                   RPCLitGreeterImpl.class,
                   new QName("http://apache.org/hello_world_rpclit", "SoapPortRPCLit"));
        BindingOperationInfo operation = ServiceModelUtil.getOperation(message.getExchange(), "greetMe");
        assertNotNull(operation, () -> "Add assertion message");
        List<String> names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        assertNotNull(names, () -> "Add assertion message");
        assertEquals(1, names.size(), () -> "Add assertion message");
        assertEquals("in", names.get(0), () -> "Add assertion message");

        operation = ServiceModelUtil.getOperation(message.getExchange(), "sayHi");
        // Line removed due to Duplicate Assert: assertNotNull(operation, () -> "Add assertion message");
        names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        // Line removed due to Duplicate Assert: assertNotNull(names, () -> "Add assertion message");
        assertEquals(0, names.size(), () -> "Add assertion message");

        operation = ServiceModelUtil.getOperation(message.getExchange(), "greetUs");
        // Line removed due to Duplicate Assert: assertNotNull(operation, () -> "Add assertion message");
        names = ServiceModelUtil.getOperationInputPartNames(operation.getOperationInfo());
        // Line removed due to Duplicate Assert: assertNotNull(names, () -> "Add assertion message");
        assertEquals(2, names.size(), () -> "Add assertion message");
        //System.err.println(names);
    }
}