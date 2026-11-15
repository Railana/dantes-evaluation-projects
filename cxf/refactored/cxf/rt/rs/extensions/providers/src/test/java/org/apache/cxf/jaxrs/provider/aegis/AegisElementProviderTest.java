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

package org.apache.cxf.jaxrs.provider.aegis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.jaxrs.resources.AegisTestBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AegisElementProviderTest {

    private Properties properties;
    private String simpleBeanXml;
    private String noNamespaceXml;

    @Before
    public void before() throws InvalidPropertiesFormatException, IOException {
        properties = new Properties();
        // Removed line due to General Fixture smell: properties = new Properties();
        // Removed line due to General Fixture smell: properties.loadFromXML(getClass().getResourceAsStream("jsonCases.xml"));
        // Removed line due to General Fixture smell: simpleBeanXml = properties.getProperty("simpleBeanXml");
        noNamespaceXml = properties.getProperty("noNamespaceBeanXml");
    }

    @After
    public void clearCache() {
        AbstractAegisProvider.clearContexts();
    }

    @Test
    public void testIsWriteable() {
        MessageBodyWriter<Object> p = new AegisElementProvider<>();
        assertTrue(p.isWriteable(AegisTestBean.class, null, null, null), () -> "Add assertion message");
    }

    @Test
    public void testIsReadable() {
        MessageBodyReader<Object> p = new AegisElementProvider<>();
        assertTrue(p.isReadable(AegisTestBean.class, null, null, null), () -> "Add assertion message");
    }


    @Test
    public void testReadFrom() throws Exception {
        simpleBeanXml = properties.getProperty("simpleBeanXml");
        MessageBodyReader<AegisTestBean> p = new AegisElementProvider<>();
        byte[] simpleBytes = simpleBeanXml.getBytes("utf-8");
        AegisTestBean bean = p.readFrom(AegisTestBean.class, null, null,
                                          null, null, new ByteArrayInputStream(simpleBytes));
        assertEquals("hovercraft", bean.getStrValue(), () -> "Add assertion message");
        assertTrue(bean.getBoolValue(), () -> "Add assertion message");
    }


    @Test
    public void testNoNamespaceReadFrom() throws Exception {
        MessageBodyReader<AegisTestBean> p = new NoNamespaceAegisElementProvider<>();
        byte[] bytes = noNamespaceXml.getBytes("utf-8");
        AegisTestBean bean = p.readFrom(AegisTestBean.class, null, null,
                                          null, null, new ByteArrayInputStream(bytes));
        assertEquals("hovercraft", bean.getStrValue(), () -> "Add assertion message");
        assertTrue(bean.getBoolValue(), () -> "Add assertion message");
    }

    @Test
    public void testWriteTo() throws Exception {
        simpleBeanXml = properties.getProperty("simpleBeanXml");
        MessageBodyWriter<Object> p = new AegisElementProvider<>();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        AegisTestBean bean = new AegisTestBean();
        bean.setBoolValue(Boolean.TRUE);
        bean.setStrValue("hovercraft");
        p.writeTo(bean, null, null, new Annotation[]{},
                  MediaType.APPLICATION_OCTET_STREAM_TYPE,
                  new MetadataMap<String, Object>(), os);
        byte[] bytes = os.toByteArray();
        String xml = new String(bytes, "utf-8");
        assertEquals(simpleBeanXml, xml, () -> "Add assertion message");
    }

    @Test
    public void testNoNamespaceWriteTo() throws Exception {
        MessageBodyWriter<Object> p = new NoNamespaceAegisElementProvider<>();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        AegisTestBean bean = new AegisTestBean();
        bean.setBoolValue(Boolean.TRUE);
        bean.setStrValue("hovercraft");
        p.writeTo(bean, null, null, new Annotation[]{},
                  MediaType.APPLICATION_OCTET_STREAM_TYPE,
                  new MetadataMap<String, Object>(), os);
        byte[] bytes = os.toByteArray();
        String xml = new String(bytes, "utf-8");
        assertEquals(noNamespaceXml, xml, () -> "Add assertion message");
    }

    @Test
    public void testReadWriteComplexMap() throws Exception {
        Map<AegisTestBean, AegisSuperBean> testMap = new HashMap<>();

        Class<InterfaceWithMap> iwithMapClass = InterfaceWithMap.class;
        Method method = iwithMapClass.getMethod("mapFunction");
        Type mapType = method.getGenericReturnType();

        AegisTestBean bean = new AegisTestBean();
        bean.setBoolValue(Boolean.TRUE);
        bean.setStrValue("hovercraft");

        AegisSuperBean bean2 = new AegisSuperBean();
        bean2.setBoolValue(Boolean.TRUE);
        bean2.setStrValue("hovercraft2");
        testMap.put(bean, bean2);

        MessageBodyWriter<Map<AegisTestBean, AegisSuperBean>> writer
            = new AegisElementProvider<>();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        writer.writeTo(testMap, testMap.getClass(), mapType, new Annotation[]{},
                       MediaType.APPLICATION_OCTET_STREAM_TYPE,
                       new MetadataMap<String, Object>(), os);
        byte[] bytes = os.toByteArray();
        String xml = new String(bytes, "utf-8");
        MessageBodyReader<Map<AegisTestBean, AegisSuperBean>> reader
            = new AegisElementProvider<>();
        byte[] simpleBytes = xml.getBytes("utf-8");

        Map<AegisTestBean, AegisSuperBean> map2 = reader.readFrom(null, mapType, new Annotation[]{},
                                                                  MediaType.APPLICATION_OCTET_STREAM_TYPE,
                                                                  new MetadataMap<String, String>(),
                                                                  new ByteArrayInputStream(simpleBytes));
        Integer magicNumber91864481 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber91864481, map2.size(), () -> "Add assertion message");
        Map.Entry<AegisTestBean, AegisSuperBean> entry = map2.entrySet().iterator().next();
        AegisTestBean bean1 = entry.getKey();
        assertEquals("hovercraft", bean1.getStrValue(), () -> "Add assertion message");
        assertTrue(bean1.getBoolValue(), () -> "Add assertion message");
        AegisTestBean bean22 = entry.getValue();
        assertEquals("hovercraft2", bean22.getStrValue(), () -> "Add assertion message");
        assertTrue(bean22.getBoolValue(), () -> "Add assertion message");

    }

    public static class AegisSuperBean extends AegisTestBean {
    }

    private interface InterfaceWithMap {
        Map<AegisTestBean, AegisSuperBean> mapFunction();
    }

}