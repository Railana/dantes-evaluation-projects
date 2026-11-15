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
package org.apache.cxf.aegis.type.java5;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import org.apache.cxf.aegis.AbstractAegisTest;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.aegis.type.AegisType;
import org.apache.cxf.aegis.type.TypeCreationOptions;
import org.apache.cxf.aegis.type.TypeMapping;
import org.apache.cxf.aegis.type.XMLTypeCreator;
import org.apache.cxf.aegis.type.basic.BeanType;
import org.apache.cxf.aegis.type.basic.StringType;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.Service;
import org.apache.ws.commons.schema.constants.Constants;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class XFireTypeTest extends AbstractAegisTest {
    private TypeMapping tm;
    private Service service;
    private AegisDatabinding databinding;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Server s = createService(XFireService.class);
        // Removed line due to General Fixture smell: service = s.getEndpoint().getService();
        // Removed line due to General Fixture smell: databinding = (AegisDatabinding) service.getDataBinding();

        // Removed line due to General Fixture smell: tm = databinding.getAegisContext().getTypeMapping();
    }

    @Test
    public void testTM() {
        tm = databinding.getAegisContext().getTypeMapping();
        assertTrue(tm.getTypeCreator() instanceof XMLTypeCreator, () -> "Add assertion message");
    }

    @Test
    public void testType() {
        tm = databinding.getAegisContext().getTypeMapping();
        AnnotatedTypeInfo info = new AnnotatedTypeInfo(tm, XFireBean1.class, "urn:foo",
                                                       new TypeCreationOptions());

        Iterator<QName> elements = info.getElements().iterator();
        assertTrue(elements.hasNext(), () -> "Add assertion message");
        QName element = elements.next();
        // Line removed due to Duplicate Assert: assertTrue(elements.hasNext(), () -> "Add assertion message");

        AegisType custom = info.getType(element);

        if ("bogusProperty".equals(element.getLocalPart())) {
            assertTrue(custom instanceof StringType, () -> "Add assertion message");
        } else if ("elementProperty".equals(element.getLocalPart())) {
            assertTrue(custom instanceof CustomStringType, () -> "Add assertion message");
        } else {
            fail("Unexpected element name: " + element.getLocalPart());
        }
        element = elements.next();
        assertFalse(elements.hasNext(), () -> "Add assertion message");

        custom = info.getType(element);

        if ("bogusProperty".equals(element.getLocalPart())) {
            // Line removed due to Duplicate Assert: assertTrue(custom instanceof StringType, () -> "Add assertion message");
        } else if ("elementProperty".equals(element.getLocalPart())) {
            // Line removed due to Duplicate Assert: assertTrue(custom instanceof CustomStringType, () -> "Add assertion message");
        } else {
            // Line removed due to Duplicate Assert: fail("Unexpected element name: " + element.getLocalPart());
        }

        Iterator<QName> atts = info.getAttributes().iterator();
        assertTrue(atts.hasNext(), () -> "Add assertion message");
        atts.next();
        assertFalse(atts.hasNext(), () -> "Add assertion message");

        assertTrue(info.isExtensibleElements(), () -> "Add assertion message");
        assertTrue(info.isExtensibleAttributes(), () -> "Add assertion message");
    }

    /**
     * Test if attributeProperty is correctly mapped to attProp by
     * applying the xml mapping file <className>.aegis.xml
     */
    @Test
    public void testAegisType() {
        tm = databinding.getAegisContext().getTypeMapping();
        BeanType type = (BeanType)tm.getTypeCreator().createType(XFireBean3.class);

        Integer magicNumber37258857 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber37258857, type.getTypeInfo().getAttributes().size(), () -> "Add assertion message");

        Iterator<QName> itr = type.getTypeInfo().getElements().iterator();
        assertTrue(itr.hasNext(), () -> "Add assertion message");
        QName q = itr.next();
        assertEquals("attProp", q.getLocalPart(), () -> "Add assertion message");
    }

    @Test
    public void testExtensibilityOff() {
        tm = databinding.getAegisContext().getTypeMapping();
        BeanType type = (BeanType)tm.getTypeCreator().createType(XFireBean4.class);

        assertFalse(type.getTypeInfo().isExtensibleElements(), () -> "Add assertion message");
        assertFalse(type.getTypeInfo().isExtensibleAttributes(), () -> "Add assertion message");
    }

    @Test
    public void testNillableAndMinOccurs() {
        tm = databinding.getAegisContext().getTypeMapping();
        BeanType type = (BeanType)tm.getTypeCreator().createType(XFireBean4.class);
        AnnotatedTypeInfo info = (AnnotatedTypeInfo)type.getTypeInfo();
        Iterator<QName> elements = info.getElements().iterator();
        assertTrue(elements.hasNext(), () -> "Add assertion message");
        // nillable first
        QName element = elements.next();
        if ("minOccursProperty".equals(element.getLocalPart())) {
            assertEquals(1, info.getMinOccurs(element), () -> "Add assertion message");
        } else {
            assertFalse(info.isNillable(element), () -> "Add assertion message");
        }

        // Line removed due to Duplicate Assert: assertTrue(elements.hasNext(), () -> "Add assertion message");
        // minOccurs = 1 second
        element = elements.next();
        if ("minOccursProperty".equals(element.getLocalPart())) {
            // Line removed due to Duplicate Assert: assertEquals(1, info.getMinOccurs(element), () -> "Add assertion message");
        } else {
            // Line removed due to Duplicate Assert: assertFalse(info.isNillable(element), () -> "Add assertion message");
        }
    }

    @Test
    public void testWSDL() throws Exception {
        Document wsdl = getWSDLDocument("XFireService");

        addNamespace("xsd", Constants.URI_2001_SCHEMA_XSD);
        assertValid(
                    "//xsd:complexType[@name='XFireBean1']/xsd:sequence/xsd:"
                    + "element[@name='elementProperty']",
                    wsdl);
        assertValid("//xsd:complexType[@name='XFireBean1']/xsd:attribute"
                    + "[@name='attributeProperty']",
                    wsdl);
        assertValid(
                    "//xsd:complexType[@name='XFireBean1']/xsd:sequence/xsd:element"
                    + "[@name='bogusProperty']",
                    wsdl);

        assertValid(
                    "//xsd:complexType[@name='XFireBean2']/xsd:sequence/xsd:element"
                    + "[@name='element'][@type='xsd:string']",
                    wsdl);
        assertValid(
                    "//xsd:complexType[@name='XFireBean2']/xsd:attribute"
                    + "[@name='attribute'][@type='xsd:string']",
                    wsdl);
    }

    @Test
    public void testGetSetRequired() throws Exception {
        tm = databinding.getAegisContext().getTypeMapping();
        BeanType type = new BeanType(new AnnotatedTypeInfo(tm, BadBean.class, "urn:foo",
                                                           new TypeCreationOptions()));
        type.setSchemaType(new QName("urn:foo", "BadBean"));

        assertEquals(0, type.getTypeInfo().getElements().size(), () -> "Add assertion message");
    }

    public static class BadBean {
        public void setString(String string) {
        }
    }
}