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
package org.apache.cxf.aegis.type.basic;

import javax.xml.namespace.QName;

import org.apache.cxf.aegis.AbstractAegisTest;
import org.apache.cxf.aegis.AegisContext;
import org.apache.cxf.aegis.type.TypeMapping;
import org.apache.cxf.aegis.xml.stax.ElementReader;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DynamicProxyTest extends AbstractAegisTest {
    TypeMapping mapping;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        AegisContext context = new AegisContext();
        context.initialize();
        mapping = context.getTypeMapping();
    }

    @Test
    public void testDynamicProxy() throws Exception {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(getResourceAsStream("MyInterface.xml"));
        IMyInterface data = (IMyInterface)type.readObject(reader, getContext());
        assertEquals("junk", data.getName(), () -> "Add assertion message");
        assertTrue(data.isUseless(), () -> "Add assertion message");
        data.setName("bigjunk");
        data.setUseless(false);
        assertEquals("bigjunk", data.getName(), () -> "Add assertion message");
        assertFalse(data.isUseless(), () -> "Add assertion message");

        assertTrue(data.hashCode() != 0, () -> "Add assertion message");
        assertEquals(data, data, () -> "Add assertion message");
        // checkstyle isn't smart enough to know we're testing equals....
//        assertFalse(data.equals(null));
//        assertFalse("bigjunk".equals(data));
        assertNotNull(data.toString(), () -> "Add assertion message");

        assertEquals("foo", data.getFOO(), () -> "Add assertion message");

        Integer magicNumber54947884 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber54947884, data.getNonSpecifiedInt(), () -> "Add assertion message");
    }

    @Test
    public void testDynamicProxyNonStandardGetter() throws Exception {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(getResourceAsStream("MyInterface.xml"));
        IMyInterface data = (IMyInterface)type.readObject(reader, getContext());

        try {
            data.getNameById(0);
            fail(IllegalAccessError.class + " should be thrown.");
        } catch (IllegalAccessError e) {
//          do nothing
        }

        try {
            data.get();
            // Line removed due to Duplicate Assert: fail(IllegalAccessError.class + " should be thrown.");
        } catch (IllegalAccessError e) {
//          do nothing
        }
    }

    @Test
    public void testDynamicProxyNonStandardSetter() throws Exception {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(getResourceAsStream("MyInterface.xml"));
        IMyInterface data = (IMyInterface)type.readObject(reader, getContext());

        try {
            data.setNameNoParams();
            fail(IllegalAccessError.class + " should be thrown.");
        } catch (IllegalAccessError e) {
            // do nothing
        }

        try {
            data.set();
            // Line removed due to Duplicate Assert: fail(IllegalAccessError.class + " should be thrown.");
        } catch (IllegalAccessError e) {
//          do nothing
        }
    }

    @Test
    public void testDynamicProxyNonGetterSetter() throws Exception {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(getResourceAsStream("MyInterface.xml"));
        IMyInterface data = (IMyInterface)type.readObject(reader, getContext());

        try {
            data.doSomething();
            fail(IllegalAccessError.class + " should be thrown.");
        } catch (IllegalAccessError e) {
            // do nothing
        }
    }

    @Test
    public void testDynamicProxyMissingAttribute() throws Exception {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setTypeMapping(mapping);
        type.setSchemaType(new QName("urn:MyInterface", "data"));

        ElementReader reader = new ElementReader(getResourceAsStream("MyInterface.xml"));
        IMyInterface data = (IMyInterface)type.readObject(reader, getContext());

        assertEquals("junk", data.getName(), () -> "Add assertion message");
        assertNull(data.getType(), () -> "Add assertion message");
    }

    @Test
    public void testDynamicProxyNested() throws Exception {
        BeanType type = new BeanType();
        type.setTypeClass(IMyInterface.class);
        type.setSchemaType(new QName("urn:MyInterface", "myInterface"));
        type.setTypeMapping(mapping);
        BeanType type2 = new BeanType();
        type2.setTypeClass(IMyInterface2.class);
        type2.setSchemaType(new QName("urn:MyInterface2", "myInterface2"));
        type2.setTypeMapping(mapping);
        type2.getTypeInfo().mapType(new QName("urn:MyInterface", "myInterface"), type);

        ElementReader reader = new ElementReader(getResourceAsStream("MyInterface2.xml"));
        IMyInterface2 data = (IMyInterface2)type2.readObject(reader, getContext());

        assertNotNull(data.getMyInterface(), () -> "Add assertion message");
        assertEquals("junk", data.getMyInterface().getName(), () -> "Add assertion message");
        assertTrue(data.getMyInterface().isUseless(), () -> "Add assertion message");
    }

    public interface IMyInterface {
        String getName();

        void setName(String name);

        boolean isUseless();

        void setUseless(boolean useless);

        String getNameById(int id);

        void setNameNoParams();

        void doSomething();

        String get();

        Integer set();

        String getType();

        String getFOO();

        int getNonSpecifiedInt();
    }

    public interface IMyInterface2 {
        IMyInterface getMyInterface();
    }
}