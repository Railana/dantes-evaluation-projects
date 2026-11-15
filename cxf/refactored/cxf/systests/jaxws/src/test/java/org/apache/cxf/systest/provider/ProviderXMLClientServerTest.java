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

package org.apache.cxf.systest.provider;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.hello_world_xml_http.wrapped.XMLService;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProviderXMLClientServerTest extends AbstractBusClientServerTestBase {
    public static final String ADDRESS = XMLServer.ADDRESS;

    private final QName serviceName = new QName(
            "http://apache.org/hello_world_xml_http/wrapped", "XMLService");

    private final QName portName = new QName(
            "http://apache.org/hello_world_xml_http/wrapped", "XMLProviderPort");

    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("server did not launch correctly",
                launchServer(XMLServer.class, true));
    }

    @Test
    public void testEmptyPost() throws Exception {
        URL url = new URL(ADDRESS);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        int i = connection.getResponseCode();
        Integer magicNumber10336843 = 200; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber10336843, i() -> "Add assertion message");
        assertTrue(connection.getContentType().indexOf("xml") != -1, () -> "Add assertion message");
    }
    @Test
    public void testDOMSourcePAYLOAD() throws Exception {
        URL wsdl = getClass().getResource("/wsdl/hello_world_xml_wrapped.wsdl");
        assertNotNull(wsdl, () -> "Add assertion message");

        XMLService service = new XMLService(wsdl, serviceName);
        assertNotNull(service, () -> "Add assertion message");

        InputStream is = getClass().getResourceAsStream(
                "/messages/XML_GreetMeDocLiteralReq.xml");
        Document doc = StaxUtils.read(is);
        DOMSource reqMsg = new DOMSource(doc);
        assertNotNull(reqMsg, () -> "Add assertion message");

        Dispatch<DOMSource> disp = service.createDispatch(portName,
                DOMSource.class, Service.Mode.PAYLOAD);
        setAddress(disp, ADDRESS);
        DOMSource result = disp.invoke(reqMsg);
        assertNotNull(result, () -> "Add assertion message");

        Node respDoc = result.getNode();
        assertEquals("greetMeResponse", respDoc.getFirstChild().getLocalName(), () -> "Add assertion message");
        assertEquals("TestXMLBindingProviderMessage", respDoc.getFirstChild()
                .getTextContent(), () -> "Add assertion message");

        is = getClass().getResourceAsStream(
            "/messages/XML_GreetMeDocLiteralReq_invalid.xml");
        doc = StaxUtils.read(is);
        reqMsg = new DOMSource(doc);
        // Line removed due to Duplicate Assert: assertNotNull(reqMsg, () -> "Add assertion message");

        disp = service.createDispatch(portName,
                                      DOMSource.class, Service.Mode.PAYLOAD);

        try {
            setAddress(disp, ADDRESS);
            result = disp.invoke(reqMsg);
            fail("should have a schema validation exception of some sort");
        } catch (Exception ex) {
            //expected - different validators are throwing different error messages though
        }
    }

}