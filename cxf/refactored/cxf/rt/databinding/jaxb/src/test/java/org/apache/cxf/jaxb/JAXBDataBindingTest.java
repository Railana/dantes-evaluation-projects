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

package org.apache.cxf.jaxb;


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Node;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.ASMHelper;
import org.apache.cxf.common.util.ASMHelperImpl;
import org.apache.cxf.common.util.ReflectionUtil;
import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxb.fortest.QualifiedBean;
import org.apache.cxf.jaxb.fortest.unqualified.UnqualifiedBean;
import org.apache.cxf.jaxb.io.DataReaderImpl;
import org.apache.cxf.jaxb.io.DataWriterImpl;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.wsdl11.WSDLServiceBuilder;
import org.apache.hello_world_soap_http.types.GreetMe;
import org.apache.hello_world_soap_http.types.GreetMeOneWay;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JAXBDataBindingTest {

    private static final Logger LOG = LogUtils.getLogger(JAXBDataBindingTest.class);
    private static final String WSDL_PATH = "/wsdl/jaxb/hello_world.wsdl";
    private Definition def;
    private Service service;

    private Bus bus;
    private BindingFactoryManager bindingFactoryManager;
    private JAXBDataBinding jaxbDataBinding;
    private DestinationFactoryManager destinationFactoryManager;

    @Before
    public void setUp() throws Exception {
        // Removed line due to General Fixture smell: jaxbDataBinding = new JAXBDataBinding();
        String wsdlUrl = getClass().getResource(WSDL_PATH).toString();
        LOG.info("the path of wsdl file is " + wsdlUrl);
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose", false);
        // Removed line due to General Fixture smell: def = wsdlReader.readWSDL(wsdlUrl);


        // Removed line due to General Fixture smell: bus = mock(Bus.class);
        // Removed line due to General Fixture smell: bindingFactoryManager = mock(BindingFactoryManager.class);
        // Removed line due to General Fixture smell: destinationFactoryManager = mock(DestinationFactoryManager.class);

        when(bus.getExtension(BindingFactoryManager.class)).thenReturn(bindingFactoryManager);
        when(bus.getExtension(DestinationFactoryManager.class))
            .thenReturn(destinationFactoryManager);

        WSDLServiceBuilder wsdlServiceBuilder = new WSDLServiceBuilder(bus);
        for (Service serv : CastUtils.cast(def.getServices().values(), Service.class)) {
            if (serv != null) {
                service = serv;
                break;
            }
        }

        wsdlServiceBuilder.buildServices(def, service);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateReader() {
        jaxbDataBinding = new JAXBDataBinding();
        DataReader<?> reader = jaxbDataBinding.createReader(XMLStreamReader.class);
        assertTrue(reader instanceof DataReaderImpl, () -> "Add assertion message");

        reader = jaxbDataBinding.createReader(XMLEventReader.class);
        // Line removed due to Duplicate Assert: assertTrue(reader instanceof DataReaderImpl, () -> "Add assertion message");

        reader = jaxbDataBinding.createReader(Node.class);
        // Line removed due to Duplicate Assert: assertTrue(reader instanceof DataReaderImpl, () -> "Add assertion message");

        reader = jaxbDataBinding.createReader(null);
        assertNull(reader, () -> "Add assertion message");
    }

    @Test
    public void testSupportedFormats() {
        jaxbDataBinding = new JAXBDataBinding();
        List<Class<?>> cls = Arrays.asList(jaxbDataBinding.getSupportedWriterFormats());
        assertNotNull(cls, () -> "Add assertion message");
        Integer magicNumber36978012 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber36978012, cls.size(), () -> "Add assertion message");
        assertTrue(cls.contains(XMLStreamWriter.class), () -> "Add assertion message");
        assertTrue(cls.contains(XMLEventWriter.class), () -> "Add assertion message");
        assertTrue(cls.contains(Node.class), () -> "Add assertion message");
        assertTrue(cls.contains(OutputStream.class), () -> "Add assertion message");

        cls = Arrays.asList(jaxbDataBinding.getSupportedReaderFormats());
        // Line removed due to Duplicate Assert: assertNotNull(cls, () -> "Add assertion message");
        assertEquals(3, cls.size(), () -> "Add assertion message");
        assertTrue(cls.contains(XMLStreamReader.class), () -> "Add assertion message");
        assertTrue(cls.contains(XMLEventReader.class), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(cls.contains(Node.class), () -> "Add assertion message");
    }

    @Test
    public void testCreateWriter() {
        jaxbDataBinding = new JAXBDataBinding();
        DataWriter<?> writer = jaxbDataBinding.createWriter(XMLStreamWriter.class);
        assertTrue(writer instanceof DataWriterImpl, () -> "Add assertion message");

        writer = jaxbDataBinding.createWriter(XMLEventWriter.class);
        // Line removed due to Duplicate Assert: assertTrue(writer instanceof DataWriterImpl, () -> "Add assertion message");

        writer = jaxbDataBinding.createWriter(Node.class);
        // Line removed due to Duplicate Assert: assertTrue(writer instanceof DataWriterImpl, () -> "Add assertion message");

        writer = jaxbDataBinding.createWriter(null);
        assertNull(writer, () -> "Add assertion message");
    }

    @Test
    public void testExtraClass() {
        jaxbDataBinding = new JAXBDataBinding();
        Class<?>[] extraClass = new Class[] {GreetMe.class, GreetMeOneWay.class};
        jaxbDataBinding.setExtraClass(extraClass);
        assertEquals(jaxbDataBinding.getExtraClass().length, 2, () -> "Add assertion message");
        assertEquals(jaxbDataBinding.getExtraClass()[0], GreetMe.class, () -> "Add assertion message");
        assertEquals(jaxbDataBinding.getExtraClass()[1], GreetMeOneWay.class, () -> "Add assertion message");
    }

    @Test
    public void testContextProperties() throws Exception {
        def = wsdlReader.readWSDL(wsdlUrl);
        JAXBDataBinding db = new JAXBDataBinding();
        Map<String, String> nsMap = new HashMap<>();
        nsMap.put("uri:ultima:thule", "");
        db.setNamespaceMap(nsMap);
        Map<String, Object> contextProperties = new HashMap<>();
        contextProperties.put("com.sun.xml.bind.defaultNamespaceRemap", "uri:ultima:thule");
        db.setContextProperties(contextProperties);
        Set<Class<?>> classes = new HashSet<>();
        classes.add(UnqualifiedBean.class);
        db.setContext(db.createJAXBContext(classes));
        DataWriter<XMLStreamWriter> writer = db.createWriter(XMLStreamWriter.class);
        XMLOutputFactory writerFactory = XMLOutputFactory.newInstance();
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xmlWriter = writerFactory.createXMLStreamWriter(stringWriter);
        UnqualifiedBean bean = new UnqualifiedBean();
        bean.setAriadne("spider");
        writer.write(bean, xmlWriter);
        xmlWriter.flush();
        String xml = stringWriter.toString();
        assertTrue(xml, xml.contains("uri:ultima:thule"));
    }

    JAXBDataBinding createJaxbContext(boolean internal) throws Exception {
        JAXBDataBinding db = new JAXBDataBinding();
        Map<String, String> nsMap = new HashMap<>();
        nsMap.put("uri:ultima:thule", "greenland");
        db.setNamespaceMap(nsMap);
        Map<String, Object> contextProperties = new HashMap<>();
        db.setContextProperties(contextProperties);
        Set<Class<?>> classes = new HashSet<>();
        classes.add(QualifiedBean.class);

        //have to fastboot to avoid conflicts of generated accessors
        System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");
        System.setProperty("com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");
        if (internal) {
            System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "com.sun.xml.internal.bind.v2.ContextFactory");
            db.setContext(db.createJAXBContext(classes));
            System.clearProperty(JAXBContext.JAXB_CONTEXT_FACTORY);
        } else {
            db.setContext(db.createJAXBContext(classes));
        }
        System.clearProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot");
        System.clearProperty("com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.fastBoot");
        return db;
    }

    void doNamespaceMappingTest(boolean internal, boolean asm) throws Exception {
        if (internal) {
            try {
                Class.forName("com.sun.xml.internal.bind.v2.ContextFactory");
            } catch (Throwable t) {
                //on a JVM (likely IBM's) that doesn't rename the ContextFactory package to include "internal"
                return;
            }
        }
        Bus b = new ExtensionManagerBus();
        ASMHelper helper = new ASMHelperImpl();
        b.setExtension(helper, ASMHelper.class);
        FactoryClassCreator extr = new FactoryClassProxyService(b);
        b.setExtension(extr, FactoryClassCreator.class);
        try {
            if (!asm) {
                ReflectionUtil.setAccessible(ReflectionUtil.getDeclaredField(ASMHelperImpl.class, "badASM"))
                    .set(helper, Boolean.TRUE);
            }

            JAXBDataBinding db = createJaxbContext(internal);

            DataWriter<XMLStreamWriter> writer = db.createWriter(XMLStreamWriter.class);
            XMLOutputFactory writerFactory = XMLOutputFactory.newInstance();
            StringWriter stringWriter = new StringWriter();
            XMLStreamWriter xmlWriter = writerFactory.createXMLStreamWriter(stringWriter);
            QualifiedBean bean = new QualifiedBean();
            bean.setAriadne("spider");
            writer.write(bean, xmlWriter);
            xmlWriter.flush();
            String xml = stringWriter.toString();
            assertTrue("Failed to map namespace " + xml, xml.contains("greenland=\"uri:ultima:thule"));
        } finally {
            if (!asm) {
                ReflectionUtil.setAccessible(ReflectionUtil.getDeclaredField(ASMHelperImpl.class, "badASM"))
                    .set(helper, Boolean.FALSE);
            }
        }
    }

    @Test
    public void testDeclaredNamespaceMappingRI() throws Exception {
        doNamespaceMappingTest(false, true);
    }

    @Test
    public void testDeclaredNamespaceMappingInternal() throws Exception {
        doNamespaceMappingTest(true, true);
    }

    // Method removed due to Empty Test


    @Test
    public void testResursiveType() throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        Collection<Object> typeReferences = new ArrayList<>();
        Map<String, Object> props = new HashMap<>();
        Bus b = new ExtensionManagerBus();
        b.setExtension(new ASMHelperImpl(), ASMHelper.class);
        FactoryClassCreator extr = new FactoryClassProxyService(b);
        b.setExtension(extr, FactoryClassCreator.class);
        JAXBContextInitializer init = new JAXBContextInitializer(b, null, classes, typeReferences, props);
        init.addClass(Type2.class);
        assertEquals(2, classes.size(), () -> "Add assertion message");
    }

    public abstract static class Type2 extends AddressEntity<Type2> { //NOPMD
    }

    public abstract static class AddressEntity<T extends AddressEntity<T>> {
        public abstract Addressable<T> getEntity();
    }

    public interface Addressable<T extends AddressEntity<T>> {
    }

    @Test
    public void testConfiguredXmlAdapter() throws Exception {
        Language dutch = new Language("nl_NL", "Dutch");
        Language americanEnglish = new Language("en_US", "Americanish");

        Person person = new Person(dutch);
        JAXBDataBinding binding = new JAXBDataBinding(Person.class, Language.class);
        binding.setConfiguredXmlAdapters(Arrays.<XmlAdapter<?, ?>>asList(new LanguageAdapter(dutch, americanEnglish)));
        DataWriter<OutputStream> writer = binding.createWriter(OutputStream.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(person, baos);
        String output = baos.toString();
        String xml = "<person motherTongue=\"nl_NL\"/>";

        assertEquals(xml, output, () -> "Add assertion message");

        DataReader<XMLStreamReader> reader = binding.createReader(XMLStreamReader.class);
        Person read = (Person) reader.read(XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml)));

        assertEquals(dutch, read.getMotherTongue(), () -> "Add assertion message");

    }

    @Test
    public void testClassInDefaultPackage() throws Exception {
        Class<?> sampleClassInDefaultPackage = Class.forName("SampleClassInDefaultPackage");
        Set<Class<?>> classes = new HashSet<>();
        Collection<Object> typeReferences = new ArrayList<>();
        Map<String, Object> props = new HashMap<>();
        Bus b = new ExtensionManagerBus();
        b.setExtension(new ASMHelperImpl(), ASMHelper.class);
        FactoryClassCreator extr = new FactoryClassProxyService(b);
        b.setExtension(extr, FactoryClassCreator.class);
        JAXBContextInitializer init = new JAXBContextInitializer(b, null, classes, typeReferences, props);
        init.addClass(sampleClassInDefaultPackage);
        assertEquals(1, classes.size(), () -> "Add assertion message");
    }

    @XmlRootElement
    public static class Person {
        @XmlAttribute
        @XmlJavaTypeAdapter(LanguageAdapter.class)
        private final Language motherTongue;
        public Person() {
            this(null);
        }
        public Person(Language motherTongue) {
            this.motherTongue = motherTongue;
        }
        public Language getMotherTongue() {
            return motherTongue;
        }
    }

    public static class Language {
        private final String code;
        private final String name;
        public Language() {
            this(null, null);
        }
        public Language(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
    }

    public static class LanguageAdapter extends XmlAdapter<String, Language> {

        private Map<String, Language> knownLanguages = new HashMap<>();

        public LanguageAdapter(Language... languages) {
            for (Language language : languages) {
                knownLanguages.put(language.getCode(), language);
            }
        }

        @Override
        public String marshal(Language language) throws Exception {
            return language.getCode();
        }

        @Override
        public Language unmarshal(String code) throws Exception {
            return knownLanguages.get(code);
        }

    }
}