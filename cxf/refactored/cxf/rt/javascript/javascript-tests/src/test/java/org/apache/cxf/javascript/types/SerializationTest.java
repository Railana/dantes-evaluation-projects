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

package org.apache.cxf.javascript.types;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.Bus;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.javascript.BasicNameManager;
import org.apache.cxf.javascript.JavascriptTestUtilities;
import org.apache.cxf.javascript.NameManager;
import org.apache.cxf.javascript.NamespacePrefixAccumulator;
import org.apache.cxf.javascript.fortest.TestBean1;
import org.apache.cxf.javascript.fortest.TestBean2;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.service.model.SchemaInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.test.AbstractCXFSpringTest;
import org.apache.cxf.ws.addressing.EndpointReferenceUtils;
import org.springframework.context.support.GenericApplicationContext;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SerializationTest extends AbstractCXFSpringTest {
    private JavascriptTestUtilities testUtilities;
    private XMLInputFactory xmlInputFactory;
    private XMLOutputFactory xmlOutputFactory;
    private Client client;
    private List<ServiceInfo> serviceInfos;
    private Collection<SchemaInfo> schemata;
    private NameManager nameManager;
    private JaxWsProxyFactoryBean clientProxyFactory;

    @Before
    public void setUp() {
        testUtilities = new JavascriptTestUtilities(getClass());
        // Removed line due to General Fixture smell: testUtilities = new JavascriptTestUtilities(getClass());
        // Removed line due to General Fixture smell: testUtilities.addDefaultNamespaces();
        // Removed line due to General Fixture smell: xmlInputFactory = XMLInputFactory.newInstance();
        xmlOutputFactory = XMLOutputFactory.newInstance();
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"classpath:serializationTestBeans.xml"};
    }

    @Test
    public void testEndpointGetSchema() throws IOException {
        setupClientAndRhino("simple-dlwu-proxy-factory");
        ServiceInfo serviceInfo = serviceInfos.get(0);
        EndpointReferenceUtils.getSchema(serviceInfo);
    }

    @Test
    public void testDeserialization() throws Exception {
        testUtilities = new JavascriptTestUtilities(getClass());
        setupClientAndRhino("simple-dlwu-proxy-factory");
        testUtilities.readResourceIntoRhino("/deserializationTests.js");
        DataBinding dataBinding = new JAXBDataBinding(TestBean1.class, TestBean2.class);
        assertNotNull(dataBinding, () -> "Add assertion message");
        TestBean1 bean = new TestBean1();
        bean.stringItem = "bean1>stringItem";
        bean.doubleItem = -1.0;
        String serialized = serializeObject(dataBinding, bean);
        testUtilities.rhinoCallInContext("deserializeTestBean3_1", serialized);

        bean = new TestBean1();
        bean.stringItem = null;
        bean.intItem = 21;
        bean.longItem = 200000001;
        bean.optionalIntItem = 456123;
        bean.optionalIntArrayItem = new int[4];
        bean.optionalIntArrayItem[0] = 3;
        bean.optionalIntArrayItem[1] = 1;
        bean.optionalIntArrayItem[2] = 4;
        bean.optionalIntArrayItem[3] = 1;
        bean.doubleItem = -1.0;
        serialized = serializeObject(dataBinding, bean);
        testUtilities.rhinoCallInContext("deserializeTestBean3_2", serialized);
    }

    private String serializeObject(DataBinding dataBinding, TestBean1 bean) throws XMLStreamException {
        DataWriter<XMLStreamWriter> writer = dataBinding.createWriter(XMLStreamWriter.class);
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
        writer.write(bean, xmlStreamWriter);
        xmlStreamWriter.flush();
        xmlStreamWriter.close();
        return stringWriter.toString();
    }

    @Test
    public void testSerialization() throws Exception {
        xmlInputFactory = XMLInputFactory.newInstance();
        testUtilities = new JavascriptTestUtilities(getClass());
        setupClientAndRhino("simple-dlwu-proxy-factory");

        testUtilities.readResourceIntoRhino("/serializationTests.js");
        DataBinding dataBinding = clientProxyFactory.getServiceFactory().getDataBinding();
        assertNotNull(dataBinding, () -> "Add assertion message");

        Object serialized = testUtilities.rhinoCallInContext("serializeTestBean1_1");
        assertTrue(serialized instanceof String, () -> "Add assertion message");
        String xml = (String)serialized;
        DataReader<XMLStreamReader> reader = dataBinding.createReader(XMLStreamReader.class);
        StringReader stringReader = new StringReader(xml);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(stringReader);
        QName testBeanQName = new QName("uri:org.apache.cxf.javascript.testns", "TestBean1");
        Object bean = reader.read(testBeanQName, xmlStreamReader, TestBean1.class);
        assertNotNull(bean, () -> "Add assertion message");
        assertTrue(bean instanceof TestBean1, () -> "Add assertion message");
        TestBean1 testBean = (TestBean1)bean;
        assertEquals("bean1<stringItem", testBean.stringItem, () -> "Add assertion message");
        Integer magicNumber34286137 = 64; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber34286137, testBean.intItem() -> "Add assertion message");
        Integer magicNumber61114814 = 64000000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber61114814, testBean.longItem() -> "Add assertion message");
        Integer magicNumber40175295 = 101; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber40175295, testBean.optionalIntItem() -> "Add assertion message");
        assertNotNull(testBean.optionalIntArrayItem, () -> "Add assertion message");
        assertEquals(1, testBean.optionalIntArrayItem.length, () -> "Add assertion message");
        assertEquals(543, testBean.optionalIntArrayItem[0], () -> "Add assertion message");

        serialized = testUtilities.rhinoCallInContext("serializeTestBean1_2");
        // Line removed due to Duplicate Assert: assertTrue(serialized instanceof String, () -> "Add assertion message");
        xml = (String)serialized;
        reader = dataBinding.createReader(XMLStreamReader.class);
        stringReader = new StringReader(xml);
        xmlStreamReader = xmlInputFactory.createXMLStreamReader(stringReader);
        bean = reader.read(testBeanQName, xmlStreamReader, TestBean1.class);
        // Line removed due to Duplicate Assert: assertNotNull(bean, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(bean instanceof TestBean1, () -> "Add assertion message");
        testBean = (TestBean1)bean;
        // Line removed due to Duplicate Assert: assertEquals("bean1<stringItem", testBean.stringItem, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(64, testBean.intItem, () -> "Add assertion message");
        Integer magicNumber8713710 = 64000000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        // Line removed due to Duplicate Assert: assertEquals(magicNumber8713710, testBean.longItem() -> "Add assertion message");
        Integer magicNumber69484142 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber69484142, testBean.optionalIntItem() -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNotNull(testBean.optionalIntArrayItem, () -> "Add assertion message");
        assertEquals(3, testBean.optionalIntArrayItem.length, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(543, testBean.optionalIntArrayItem[0], () -> "Add assertion message");
        assertEquals(0, testBean.optionalIntArrayItem[1], () -> "Add assertion message");
        assertEquals(345, testBean.optionalIntArrayItem[2], () -> "Add assertion message");

        serialized = testUtilities.rhinoCallInContext("serializeTestBean1_3");
        // Line removed due to Duplicate Assert: assertTrue(serialized instanceof String, () -> "Add assertion message");
        xml = (String)serialized;
        reader = dataBinding.createReader(XMLStreamReader.class);
        stringReader = new StringReader(xml);
        xmlStreamReader = xmlInputFactory.createXMLStreamReader(stringReader);
        bean = reader.read(testBeanQName, xmlStreamReader, TestBean1.class);
        // Line removed due to Duplicate Assert: assertNotNull(bean, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(bean instanceof TestBean1, () -> "Add assertion message");
        testBean = (TestBean1)bean;
        // Line removed due to Duplicate Assert: assertEquals("bean1<stringItem", testBean.stringItem, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(64, testBean.intItem, () -> "Add assertion message");
        assertEquals(43, testBean.longItem, () -> "Add assertion message");
        assertEquals(33, testBean.optionalIntItem, () -> "Add assertion message");
        assertNull(testBean.optionalIntArrayItem, () -> "Add assertion message");
    }

    private void setupClientAndRhino(String clientProxyFactoryBeanId) throws IOException {
        testUtilities = new JavascriptTestUtilities(getClass());
        testUtilities.setBus(getBean(Bus.class, "cxf"));

        testUtilities.initializeRhino();
        testUtilities.readResourceIntoRhino("/org/apache/cxf/javascript/cxf-utils.js");

        clientProxyFactory = getBean(JaxWsProxyFactoryBean.class, clientProxyFactoryBeanId);
        client = clientProxyFactory.getClientFactoryBean().create();
        serviceInfos = client.getEndpoint().getService().getServiceInfos();
        // there can only be one.
        assertEquals(1, serviceInfos.size());
        ServiceInfo serviceInfo = serviceInfos.get(0);
        schemata = serviceInfo.getSchemas();
        nameManager = BasicNameManager.newNameManager(serviceInfo);
        NamespacePrefixAccumulator prefixAccumulator =
            new NamespacePrefixAccumulator(serviceInfo.getXmlSchemaCollection());
        for (SchemaInfo schema : schemata) {
            SchemaJavascriptBuilder builder = new SchemaJavascriptBuilder(serviceInfo
                .getXmlSchemaCollection(), prefixAccumulator, nameManager);
            String allThatJavascript = builder.generateCodeForSchema(schema.getSchema());
            assertNotNull(allThatJavascript);
            testUtilities.readStringIntoRhino(allThatJavascript, schema.toString() + ".js");
        }
    }

    @Override
    protected void additionalSpringConfiguration(GenericApplicationContext context) throws Exception {
    }
}