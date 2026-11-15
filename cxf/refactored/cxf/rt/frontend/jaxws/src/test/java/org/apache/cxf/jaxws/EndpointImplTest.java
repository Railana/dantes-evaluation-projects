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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.transform.Source;
import javax.xml.ws.Binding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxws.service.Hello;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.invoker.BeanInvoker;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.hello_world_soap_http.GreeterImpl;
import org.apache.hello_world_soap_http.HelloImpl;
import org.apache.hello_world_soap_http.HelloWrongAnnotation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EndpointImplTest extends AbstractJaxWsTest {


    @Override
    protected Bus createBus() throws BusException {
        return BusFactory.getDefaultBus();
    }


    @Test
    public void testEndpoint() throws Exception {
        String address = "http://localhost:8080/test";

        GreeterImpl greeter = new GreeterImpl();
        try (EndpointImpl endpoint = new EndpointImpl(getBus(), greeter, (String)null)) {

            WebServiceContext ctx = greeter.getContext();
            assertNull(ctx, () -> "Add assertion message");
            assertThrows(IllegalArgumentException ex,
            		() -> endpoint.publish(address));
            ctx = greeter.getContext();

            assertNotNull(ctx, () -> "Add assertion message");

            // Test that we can't change settings through the JAX-WS API after publishing

            try {
                endpoint.publish(address);
                fail("republished an already published endpoint.");
            } catch (IllegalStateException e) {
                // expected
            }

            try {
                endpoint.setMetadata(new ArrayList<Source>(0));
                fail("set metadata on an already published endpoint.");
            } catch (IllegalStateException e) {
                // expected
            }
        }
    }

    @Test
    public void testEndpointStop() throws Exception {
        String address = "http://localhost:8080/test";

        GreeterImpl greeter = new GreeterImpl();
        try (EndpointImpl endpoint = new EndpointImpl(getBus(), greeter, (String)null)) {

            WebServiceContext ctx = greeter.getContext();
            assertNull(ctx, () -> "Add assertion message");
            try {
                endpoint.publish(address);
            } catch (IllegalArgumentException ex) {
                assertTrue(ex.getCause() instanceof BusException, () -> "Add assertion message");
                assertEquals("BINDING_INCOMPATIBLE_ADDRESS_EXC", ((BusException)ex.getCause()).getCode(), () -> "Add assertion message");
            }
            ctx = greeter.getContext();

            assertNotNull(ctx, () -> "Add assertion message");

            // Test that calling stop on the Endpoint works
            assertTrue(endpoint.isPublished(), () -> "Add assertion message");
            endpoint.stop();
            assertFalse(endpoint.isPublished(), () -> "Add assertion message");

            // Test that the Endpoint cannot be restarted.
            try {
                endpoint.publish(address);
                fail("stopped endpoint restarted.");
            } catch (IllegalStateException e) {
                // expected.
            }
        }
    }


    @Test
    public void testEndpointServiceConstructor() throws Exception {
        GreeterImpl greeter = new GreeterImpl();
        JaxWsServiceFactoryBean serviceFactory = new JaxWsServiceFactoryBean();
        serviceFactory.setBus(getBus());
        serviceFactory.setInvoker(new BeanInvoker(greeter));
        serviceFactory.setServiceClass(GreeterImpl.class);

        try (EndpointImpl endpoint = new EndpointImpl(getBus(), greeter,
                                                 new JaxWsServerFactoryBean(serviceFactory))) {

            WebServiceContext ctx = greeter.getContext();
            assertNull(ctx, () -> "Add assertion message");
            try {
                String address = "http://localhost:8080/test";
                endpoint.publish(address);
            } catch (IllegalArgumentException ex) {
                assertTrue(ex.getCause() instanceof BusException, () -> "Add assertion message");
                assertEquals("BINDING_INCOMPATIBLE_ADDRESS_EXC", ((BusException)ex.getCause()).getCode(), () -> "Add assertion message");
            }
            ctx = greeter.getContext();

            assertNotNull(ctx, () -> "Add assertion message");
        }
    }

    @Test
    public void testWSAnnoWithoutWSDLLocationInSEI() throws Exception {
        HelloImpl hello = new HelloImpl();
        JaxWsServiceFactoryBean serviceFactory = new JaxWsServiceFactoryBean();
        serviceFactory.setBus(getBus());
        serviceFactory.setInvoker(new BeanInvoker(hello));
        serviceFactory.setServiceClass(HelloImpl.class);

        try (EndpointImpl endpoint = new EndpointImpl(getBus(), hello,
                                                 new JaxWsServerFactoryBean(serviceFactory))) {

            try {
                String address = "http://localhost:8080/test";
                endpoint.publish(address);
            } catch (IllegalArgumentException ex) {
                assertTrue(ex.getCause() instanceof BusException, () -> "Add assertion message");
                assertEquals("BINDING_INCOMPATIBLE_ADDRESS_EXC", ((BusException)ex.getCause()).getCode(), () -> "Add assertion message");
            }
        }
    }

    @Test
    public void testSOAPBindingOnMethodWithRPC() {
        HelloWrongAnnotation hello = new HelloWrongAnnotation();
        JaxWsServiceFactoryBean serviceFactory = new JaxWsServiceFactoryBean();
        serviceFactory.setBus(getBus());
        serviceFactory.setInvoker(new BeanInvoker(hello));
        serviceFactory.setServiceClass(HelloWrongAnnotation.class);

        try {
            new EndpointImpl(getBus(), hello, new JaxWsServerFactoryBean(serviceFactory)).close();
        } catch (Exception e) {
            String expeced = "Method [sayHi] processing error: SOAPBinding can not on method with RPC style";
            assertEquals(expeced, e.getMessage(), () -> "Add assertion message");
        }
    }

    @Test
    public void testPublishEndpointPermission() throws Exception {
        Hello service = new Hello();
        try (EndpointImpl ep = new EndpointImpl(getBus(), service, (String) null)) {

            System.setProperty(EndpointImpl.CHECK_PUBLISH_ENDPOINT_PERMISSON_PROPERTY, "true");

            try {
                ep.publish("local://localhost:9090/hello");
                fail("Did not throw exception as expected");
            } catch (SecurityException e) {
                // that's expected
            } finally {
                System.setProperty(EndpointImpl.CHECK_PUBLISH_ENDPOINT_PERMISSON_PROPERTY, "false");
            }

            ep.publish("local://localhost:9090/hello");
        }
    }

    @Test
    public void testAddWSAFeature() throws Exception {
        GreeterImpl greeter = new GreeterImpl();
        JaxWsServiceFactoryBean serviceFactory = new JaxWsServiceFactoryBean();
        serviceFactory.setBus(getBus());
        serviceFactory.setInvoker(new BeanInvoker(greeter));
        serviceFactory.setServiceClass(GreeterImpl.class);

        try (EndpointImpl endpoint = new EndpointImpl(getBus(), greeter,
                                                 new JaxWsServerFactoryBean(serviceFactory))) {

            endpoint.getFeatures().add(new WSAddressingFeature());
            try {
                String address = "http://localhost:8080/test";
                endpoint.publish(address);
            } catch (IllegalArgumentException ex) {
                assertTrue(ex.getCause() instanceof BusException, () -> "Add assertion message");
                assertEquals("BINDING_INCOMPATIBLE_ADDRESS_EXC", ((BusException)ex.getCause()).getCode(), () -> "Add assertion message");
            }

            assertTrue(serviceFactory.getFeatures().size() == 1, () -> "Add assertion message");
            assertTrue(serviceFactory.getFeatures().get(0) instanceof WSAddressingFeature, () -> "Add assertion message");
        }
    }

    @Test
    public void testJaxWsaFeature() throws Exception {
        HelloWsa greeter = new HelloWsa();
        JaxWsServiceFactoryBean serviceFactory = new JaxWsServiceFactoryBean();
        serviceFactory.setBus(getBus());
        serviceFactory.setInvoker(new BeanInvoker(greeter));
        serviceFactory.setServiceClass(HelloWsa.class);

        try (EndpointImpl endpoint = new EndpointImpl(getBus(), greeter,
                                                 new JaxWsServerFactoryBean(serviceFactory))) {
            try {
                String address = "http://localhost:8080/test";
                endpoint.publish(address);
            } catch (IllegalArgumentException ex) {
                assertTrue(ex.getCause() instanceof BusException, () -> "Add assertion message");
                assertEquals("BINDING_INCOMPATIBLE_ADDRESS_EXC", ((BusException)ex.getCause()).getCode(), () -> "Add assertion message");
            }

            Integer magicNumber9531332 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber9531332, serviceFactory.getFeatures().size(), () -> "Add assertion message");
            assertTrue(serviceFactory.getFeatures().get(0) instanceof WSAddressingFeature, () -> "Add assertion message");
        }
    }

    @Test
    public void testEndpointPublishAfterGetBinding() throws Exception {
        Endpoint endpoint = Endpoint.create(new Hello());

        Binding binding = endpoint.getBinding();
        assertNotNull(binding, () -> "Add assertion message");

        // CXF-6257
        endpoint.publish("http://localhost:8080/test");
        endpoint.stop();
    }

    static class EchoObserver implements MessageObserver {

        public void onMessage(Message message) {
            try {
                Conduit backChannel = message.getDestination().getBackChannel(message);

                backChannel.prepare(message);

                OutputStream out = message.getContent(OutputStream.class);
                assertNotNull(out);
                InputStream in = message.getContent(InputStream.class);
                assertNotNull(in);

                IOUtils.copy(in, out, 2045);

                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}