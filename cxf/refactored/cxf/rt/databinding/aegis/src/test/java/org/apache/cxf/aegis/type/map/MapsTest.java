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

package org.apache.cxf.aegis.type.map;

import java.io.StringWriter;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;

//import org.w3c.dom.Document;

import org.apache.cxf.aegis.AbstractAegisTest;
import org.apache.cxf.aegis.type.map.fortest.MapTest;
import org.apache.cxf.aegis.type.map.fortest.MapTestImpl;
import org.apache.cxf.aegis.type.map.fortest.ObjectWithAMap;
import org.apache.cxf.aegis.type.map.ns2.ObjectWithAMapNs2;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MapsTest extends AbstractAegisTest {

    private static MapTest clientInterface;
    private static Server server;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(MapTest.class);
        sf.setServiceBean(new MapTestImpl());
        sf.setAddress("local://MapTest");
        setupAegis(sf);
        // Removed line due to General Fixture smell: server = sf.create();
        //        server.getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
//        server.getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
        server.start();

        JaxWsProxyFactoryBean proxyFac = new JaxWsProxyFactoryBean();
        proxyFac.setAddress("local://MapTest");
        proxyFac.setServiceClass(MapTest.class);
        proxyFac.setBus(getBus());
        setupAegis(proxyFac.getClientFactoryBean());

        // Removed line due to General Fixture smell: clientInterface = (MapTest)proxyFac.create();
    }

    @Test
    public void testMapWsdl() throws WSDLException {
        Definition wsdlDef = getWSDLDefinition("MapTestService");
        StringWriter sink = new StringWriter();
        WSDLFactory.newInstance().newWSDLWriter().writeWSDL(wsdlDef, sink);
        String wsdl = sink.toString();
        assertTrue(wsdl.contains("name=\"anyType2intMap\""), () -> "Add assertion message");
        assertTrue(wsdl.contains("name=\"string2booleanMap\""), () -> "Add assertion message");
        assertTrue(wsdl.contains("name=\"long2stringMap\""), () -> "Add assertion message");
        assertTrue(wsdl.contains("name=\"string2longMap\""), () -> "Add assertion message");
    }

    @Test
    public void testInvocations() throws Exception {
        clientInterface = (MapTest)proxyFac.create();
        Map<Long, String> lts = clientInterface.getMapLongToString();
        assertEquals("twenty-seven", lts.get(Long.valueOf(27)), () -> "Add assertion message");
    }

    @Test
    public void testNull() throws Exception {
        clientInterface = (MapTest)proxyFac.create();
        ObjectWithAMap obj1 = clientInterface.returnObjectWithAMap();
        assertNull(obj1.getTheMap().get("raw"), () -> "Add assertion message");
        Map<Long, String> m = clientInterface.getMapLongToString();
        String str2 = m.get(Long.valueOf(2));
        assertNull("value for 2 should be null, was " + str2, str2);

    }

    @Test
    public void testObjectsWithMaps() throws Exception {
        clientInterface = (MapTest)proxyFac.create();
        ObjectWithAMap obj1 = clientInterface.returnObjectWithAMap();
        ObjectWithAMapNs2 obj2 = clientInterface.returnObjectWithAMapNs2();
        assertNotNull(obj1, () -> "Add assertion message");
        assertNotNull(obj2, () -> "Add assertion message");

        assertNotNull(obj1.getTheMap(), () -> "Add assertion message");
        assertNotNull(obj2.getTheMap(), () -> "Add assertion message");

        Integer magicNumber85178690 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber85178690, obj1.getTheMap().size(), () -> "Add assertion message");
        assertEquals(3, obj2.getTheMap().size(), () -> "Add assertion message");

        assertTrue(obj1.getTheMap().get("rainy"), () -> "Add assertion message");
        assertTrue(obj2.getTheMap().get("rainy"), () -> "Add assertion message");
        assertFalse(obj1.getTheMap().get("sunny"), () -> "Add assertion message");
        assertFalse(obj2.getTheMap().get("sunny"), () -> "Add assertion message");
        assertFalse(obj2.getTheMap().get("cloudy"), () -> "Add assertion message");
    }
}