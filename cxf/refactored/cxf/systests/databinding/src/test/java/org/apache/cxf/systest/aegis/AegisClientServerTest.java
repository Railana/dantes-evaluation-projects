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

package org.apache.cxf.systest.aegis;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.authservice.AuthService;
import org.apache.cxf.authservice.Authenticate;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.systest.aegis.SportsService.Pair;
import org.apache.cxf.test.TestUtilities;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AegisClientServerTest extends AbstractBusClientServerTestBase {
    static final String PORT = allocatePort(AegisServer.class);

    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("server did not launch correctly", launchServer(AegisServer.class, true));
    }

    @Test
    public void testAegisClient() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        ClientProxyFactoryBean proxyFactory = new ClientProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(AuthService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/service");
        AuthService service = (AuthService) proxyFactory.create();
        assertTrue(service.authenticate("Joe", "Joe", "123"), () -> "Add assertion message");
        assertFalse(service.authenticate("Joe1", "Joe", "fang"), () -> "Add assertion message");
        assertTrue(service.authenticate("Joe", null, "123"), () -> "Add assertion message");
        List<String> list = service.getRoles("Joe");
        Integer magicNumber39059137 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber39059137, list.size(), () -> "Add assertion message");
        assertEquals("Joe", list.get(0), () -> "Add assertion message");
        assertEquals("Joe-1", list.get(1), () -> "Add assertion message");
        assertEquals("Joe-2", list.get(2), () -> "Add assertion message");
        String[] roles = service.getRolesAsArray("Joe");
        assertEquals(2, roles.length, () -> "Add assertion message");
        assertEquals("Joe", roles[0], () -> "Add assertion message");
        assertEquals("Joe-1", roles[1], () -> "Add assertion message");

        assertEquals("get Joe", service.getAuthentication("Joe"), () -> "Add assertion message");
        Authenticate au = new Authenticate();
        au.setSid("ffang");
        au.setUid("ffang");
        assertTrue(service.authenticate(au), () -> "Add assertion message");
        au.setUid("ffang1");
        assertFalse(service.authenticate(au), () -> "Add assertion message");
    }

    @Test
    public void testJaxWsAegisClient() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(AuthService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/jaxwsAndAegis");
        AuthService service = (AuthService) proxyFactory.create();
        assertTrue(service.authenticate("Joe", "Joe", "123"), () -> "Add assertion message");
        assertFalse(service.authenticate("Joe1", "Joe", "fang"), () -> "Add assertion message");
        assertTrue(service.authenticate("Joe", null, "123"), () -> "Add assertion message");
        List<String> list = service.getRoles("Joe");
        Integer magicNumber25393705 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber25393705, list.size(), () -> "Add assertion message");
        assertEquals("Joe", list.get(0), () -> "Add assertion message");
        assertEquals("Joe-1", list.get(1), () -> "Add assertion message");
        assertEquals("Joe-2", list.get(2), () -> "Add assertion message");
        String[] roles = service.getRolesAsArray("Joe");
        assertEquals(2, roles.length, () -> "Add assertion message");
        assertEquals("Joe", roles[0], () -> "Add assertion message");
        assertEquals("Joe-1", roles[1], () -> "Add assertion message");

        roles = service.getRolesAsArray("null");
        assertNull(roles, () -> "Add assertion message");

        roles = service.getRolesAsArray("0");
        assertEquals(0, roles.length, () -> "Add assertion message");

        assertEquals("get Joe", service.getAuthentication("Joe"), () -> "Add assertion message");
        Authenticate au = new Authenticate();
        au.setSid("ffang");
        au.setUid("ffang");
        assertTrue(service.authenticate(au), () -> "Add assertion message");
        au.setUid("ffang1");
        assertFalse(service.authenticate(au), () -> "Add assertion message");
    }

    @Test
    public void testWSDL() throws Exception {
        URL url = new URL("http://localhost:" + PORT + "/jaxwsAndAegis?wsdl");
        Document dom = StaxUtils.read(url.openStream());
        TestUtilities util = new TestUtilities(this.getClass());
        util.addDefaultNamespaces();
        util.assertInvalid("//wsdl:definitions/wsdl:types/xsd:schema/"
                           + "xsd:complexType[@name='getRolesAsArrayResponse']/"
                           + "xsd:sequence/xsd:element[@maxOccurs]",
                           dom);
        util.assertValid("//wsdl:definitions/wsdl:types/xsd:schema/"
                           + "xsd:complexType[@name='getRolesAsArrayResponse']/"
                           + "xsd:sequence/xsd:element[@nillable='true']",
                           dom);

        url = new URL("http://localhost:" + PORT + "/serviceWithCustomNS?wsdl");
        dom = StaxUtils.read(url.openStream());
        util.assertValid("//wsdl:definitions[@targetNamespace='http://foo.bar.com']",
                         dom);
    }

    @Test
    public void testCollection() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(SportsService.class);
        proxyFactory.setWsdlLocation("http://localhost:" + PORT + "/jaxwsAndAegisSports?wsdl");
        proxyFactory.getInInterceptors().add(new LoggingInInterceptor());
        proxyFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        SportsService service = (SportsService) proxyFactory.create();

        Collection<Team> teams = service.getTeams();
        assertEquals(1, teams.size(), () -> "Add assertion message");
        assertEquals("Patriots", teams.iterator().next().getName(), () -> "Add assertion message");

        //CXF-1251
        String s = service.testForMinOccurs0("A", null, "b");
        assertEquals("Anullb", s, () -> "Add assertion message");
    }

    @Test
    public void testComplexMapResult() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(SportsService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/jaxwsAndAegisSports");
        proxyFactory.getInInterceptors().add(new LoggingInInterceptor());
        proxyFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        SportsService service = (SportsService) proxyFactory.create();
        Map<String, Map<Integer, Integer>> result = service.testComplexMapResult();
        assertEquals(result.size(), 1, () -> "Add assertion message");
        assertTrue(result.containsKey("key1"), () -> "Add assertion message");
        assertNotNull(result.get("key1"), () -> "Add assertion message");
        assertEquals(result.toString(), "{key1={1=3}}", () -> "Add assertion message");
    }

    @Test
    public void testGenericCollection() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(SportsService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/jaxwsAndAegisSports");
        proxyFactory.getInInterceptors().add(new LoggingInInterceptor());
        proxyFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        SportsService service = (SportsService) proxyFactory.create();
        List<String> list = new ArrayList<>();
        list.add("ffang");
        String ret = service.getGeneric(list);
        assertEquals(ret, "ffang", () -> "Add assertion message");
    }

    @Test
    public void testGenericPair() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(SportsService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/jaxwsAndAegisSports");
        proxyFactory.getInInterceptors().add(new LoggingInInterceptor());
        proxyFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        SportsService service = (SportsService) proxyFactory.create();
        Integer magicNumber75673586 = 111; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Pair<String, Integer> ret = service.getReturnGenericPair("ffang",magicNumber75673586
        assertEquals("ffang", ret.getFirst(), () -> "Add assertion message");
        assertEquals(Integer.valueOf(111), ret.getSecond(), () -> "Add assertion message");
    }

    @Test
    public void testReturnQualifiedPair() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(SportsService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/jaxwsAndAegisSports");
        proxyFactory.getInInterceptors().add(new LoggingInInterceptor());
        proxyFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        SportsService service = (SportsService) proxyFactory.create();
        Pair<Integer, String> ret = service.getReturnQualifiedPair(111, "ffang");
        assertEquals(Integer.valueOf(111), ret.getFirst(), () -> "Add assertion message");
        assertEquals("ffang", ret.getSecond(), () -> "Add assertion message");
    }


    @Test
    public void testReturnGenericPair() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(SportsService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/jaxwsAndAegisSports");
        proxyFactory.getInInterceptors().add(new LoggingInInterceptor());
        proxyFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        SportsService service = (SportsService) proxyFactory.create();
        int ret = service.getGenericPair(new Pair<Integer, String>(111, "String"));
        assertEquals(111, ret, () -> "Add assertion message");
    }

    @Test
    public void testQualifiedPair() throws Exception {
        AegisDatabinding aegisBinding = new AegisDatabinding();
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setDataBinding(aegisBinding);
        proxyFactory.setServiceClass(SportsService.class);
        proxyFactory.setAddress("http://localhost:" + PORT + "/jaxwsAndAegisSports");
        proxyFactory.getInInterceptors().add(new LoggingInInterceptor());
        proxyFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        SportsService service = (SportsService) proxyFactory.create();
        int ret = service.getQualifiedPair(new Pair<Integer, String>(111, "ffang"));
        assertEquals(111, ret, () -> "Add assertion message");
    }


    @Test
    public void testDynamicClient() throws Exception {
        DynamicClientFactory dcf = DynamicClientFactory.newInstance();
        Client client = dcf.createClient("http://localhost:" + PORT + "/jaxwsAndAegisSports?wsdl&dynamic");

        Object r = client.invoke("getAttributeBean")[0];
        Method getAddrPlainString = r.getClass().getMethod("getAttrPlainString");
        String s = (String)getAddrPlainString.invoke(r);

        assertEquals("attrPlain", s, () -> "Add assertion message");
    }
}