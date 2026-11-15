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


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.common.jaxb.JAXBUtils;
import org.apache.cxf.common.util.ReflectionUtil;
import org.apache.hello_world_soap_http.types.GreetMe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class JAXBUtilsTest {

    @Test
    public void testBuiltInTypeToJavaType() {
        assertEquals("boolean", JAXBUtils.builtInTypeToJavaType("boolean"), () -> "Add assertion message");
        assertEquals("javax.xml.datatype.XMLGregorianCalendar", JAXBUtils.builtInTypeToJavaType("gYear"), () -> "Add assertion message");
        assertNull(JAXBUtils.builtInTypeToJavaType("other"), () -> "Add assertion message");
    }

    @Test
    public void testPackageNames() {
        assertEquals("org.apache.cxf.configuration.types",
                     JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/configuration/types"), () -> "Add assertion message");

        // tests of JAXB 2.2 Appendix D.5.1, Rule #1: schemes to be removed are just http and urn
        assertEquals("auto.org.apache.cxf.configuration.types",
                JAXBUtils.namespaceURIToPackage("auto://cxf.apache.org/configuration/types"), () -> "Add assertion message");
        assertEquals("mouse.org.apache.cxf.configuration.types",
                JAXBUtils.namespaceURIToPackage("mouse://cxf.apache.org/configuration/types"), () -> "Add assertion message");
        assertEquals("h.org.apache.cxf.configuration.types",
                JAXBUtils.namespaceURIToPackage("h://cxf.apache.org/configuration/types"), () -> "Add assertion message");

        // tests of JAXB 2.2 Appendix D.5.1, Rule #2: file type is one of .?? or .??? or .html
        assertEquals("org.apache.cxf.configuration.types_h",
                JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/configuration/types.h"), () -> "Add assertion message");
        assertEquals("org.apache.cxf.configuration.types",
                JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/configuration/types.hi"), () -> "Add assertion message");
        assertEquals("org.apache.cxf.configuration.types",
                JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/configuration/types.xsd"), () -> "Add assertion message");
        assertEquals("org.apache.cxf.configuration.types",
                JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/configuration/types.html"), () -> "Add assertion message");
        assertEquals("org.apache.cxf.configuration.types_auto",
                JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/configuration/types.auto"), () -> "Add assertion message");
        assertEquals("org.apache.cxf.configuration.types_mouse",
                JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/configuration/types.mouse"), () -> "Add assertion message");

        // other tests
        assertEquals("https.com.mytech.rosette_analysis",
                JAXBUtils.namespaceURIToPackage("https://mytech.com/rosette.analysis"), () -> "Add assertion message");
        assertEquals("org.apache.cxf.config._4types_",
                JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/config/4types."), () -> "Add assertion message");
        assertEquals("org.apache.cxf.config_types",
                     JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/config-types"), () -> "Add assertion message");
        assertEquals("org.apache.cxf._default.types",
                     JAXBUtils.namespaceURIToPackage("http://cxf.apache.org/default/types"), () -> "Add assertion message");
        assertEquals("com.progress.configuration.types",
                     JAXBUtils.namespaceURIToPackage("http://www.progress.com/configuration/types"), () -> "Add assertion message");
        assertEquals("org.apache.cxf.config.types",
                JAXBUtils.namespaceURIToPackage("urn:cxf-apache-org:config:types"), () -> "Add assertion message");
        assertEquals("types", JAXBUtils.namespaceURIToPackage("types"), () -> "Add assertion message");
    }

    @Test
    public void testNameToIdentifier() {        
        assertEquals("_return",
                     JAXBUtils.nameToIdentifier("return", JAXBUtils.IdentifierType.VARIABLE), () -> "Add assertion message");
        assertEquals("getReturn",
                     JAXBUtils.nameToIdentifier("return", JAXBUtils.IdentifierType.GETTER), () -> "Add assertion message");
        assertEquals("setReturn",
                     JAXBUtils.nameToIdentifier("return", JAXBUtils.IdentifierType.SETTER), () -> "Add assertion message");


        assertEquals("_public",
                     JAXBUtils.nameToIdentifier("public", JAXBUtils.IdentifierType.VARIABLE), () -> "Add assertion message");
        assertEquals("getPublic",
                     JAXBUtils.nameToIdentifier("public", JAXBUtils.IdentifierType.GETTER), () -> "Add assertion message");
        assertEquals("setPublic",
                     JAXBUtils.nameToIdentifier("public", JAXBUtils.IdentifierType.SETTER), () -> "Add assertion message");

        assertEquals("arg0",
                     JAXBUtils.nameToIdentifier("arg0", JAXBUtils.IdentifierType.VARIABLE), () -> "Add assertion message");
        assertEquals("getArg0",
                     JAXBUtils.nameToIdentifier("arg0", JAXBUtils.IdentifierType.GETTER), () -> "Add assertion message");
        assertEquals("setArg0",
                     JAXBUtils.nameToIdentifier("arg0", JAXBUtils.IdentifierType.SETTER), () -> "Add assertion message");

        assertEquals("mixedCaseName",
                     JAXBUtils.nameToIdentifier("mixedCaseName", JAXBUtils.IdentifierType.VARIABLE), () -> "Add assertion message");
        assertEquals("MixedCaseName",
                     JAXBUtils.nameToIdentifier("mixedCaseName", JAXBUtils.IdentifierType.CLASS), () -> "Add assertion message");
        assertEquals("setMixedCaseName",
                     JAXBUtils.nameToIdentifier("mixedCaseName", JAXBUtils.IdentifierType.SETTER), () -> "Add assertion message");
        assertEquals("MIXED_CASE_NAME",
                     JAXBUtils.nameToIdentifier("mixedCaseName", JAXBUtils.IdentifierType.CONSTANT), () -> "Add assertion message");

        assertEquals("answer42",
                     JAXBUtils.nameToIdentifier("Answer42", JAXBUtils.IdentifierType.VARIABLE), () -> "Add assertion message");
        assertEquals("Answer42",
                     JAXBUtils.nameToIdentifier("Answer42", JAXBUtils.IdentifierType.CLASS), () -> "Add assertion message");
        assertEquals("getAnswer42",
                     JAXBUtils.nameToIdentifier("Answer42", JAXBUtils.IdentifierType.GETTER), () -> "Add assertion message");
        assertEquals("ANSWER_42",
                     JAXBUtils.nameToIdentifier("Answer42", JAXBUtils.IdentifierType.CONSTANT), () -> "Add assertion message");

        assertEquals("nameWithDashes",
                     JAXBUtils.nameToIdentifier("name-with-dashes", JAXBUtils.IdentifierType.VARIABLE), () -> "Add assertion message");
        assertEquals("NameWithDashes",
                     JAXBUtils.nameToIdentifier("name-with-dashes", JAXBUtils.IdentifierType.CLASS), () -> "Add assertion message");
        assertEquals("setNameWithDashes",
                     JAXBUtils.nameToIdentifier("name-with-dashes", JAXBUtils.IdentifierType.SETTER), () -> "Add assertion message");
        assertEquals("NAME_WITH_DASHES",
                     JAXBUtils.nameToIdentifier("name-with-dashes", JAXBUtils.IdentifierType.CONSTANT), () -> "Add assertion message");

        assertEquals("otherPunctChars",
                     JAXBUtils.nameToIdentifier("other_punct-chars", JAXBUtils.IdentifierType.VARIABLE), () -> "Add assertion message");
        assertEquals("OtherPunctChars",
                     JAXBUtils.nameToIdentifier("other_punct-chars", JAXBUtils.IdentifierType.CLASS), () -> "Add assertion message");
        assertEquals("getOtherPunctChars",
                     JAXBUtils.nameToIdentifier("other_punct-chars", JAXBUtils.IdentifierType.GETTER), () -> "Add assertion message");
        assertEquals("OTHER_PUNCT_CHARS",
                     JAXBUtils.nameToIdentifier("other_punct-chars", JAXBUtils.IdentifierType.CONSTANT), () -> "Add assertion message");
        
        assertEquals("XMLTransfer",
                     JAXBUtils.nameToIdentifier("XMLTransfer", JAXBUtils.IdentifierType.CLASS), () -> "Add assertion message");

    }

    @Test
    public void testNsToPkg() {
        String urn = "urn:cxf.apache.org";
        String pkg = JAXBUtils.namespaceURIToPackage(urn);
        assertEquals("org.apache.cxf", pkg, () -> "Add assertion message");

        urn = "urn:cxf.apache.org:test.v4.6.4";
        pkg = JAXBUtils.namespaceURIToPackage(urn);
        assertEquals("org.apache.cxf.test_v4_6_4", pkg, () -> "Add assertion message");
    }

    @Test
    public void testSetNamespaceMapper() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(GreetMe.class);
        Marshaller marshaller = ctx.createMarshaller();
        Map<String, String> nspref = new HashMap<>();
        nspref.put("http://cxf.apache.org/hello_world_soap_http/types", "x");
        Bus bus = new ExtensionManagerBus();
        JAXBUtils.setNamespaceMapper(bus, nspref, marshaller);
        String mapperkey = null;
        if (marshaller.getClass().getName().contains(".internal.")) {
            mapperkey = "com.sun.xml.internal.bind.namespacePrefixMapper";
        } else if (marshaller.getClass().getName().contains("com.sun")) {
            mapperkey = "com.sun.xml.bind.namespacePrefixMapper";
        } else if (marshaller.getClass().getName().contains("eclipse")) {
            mapperkey = "eclipselink.namespace-prefix-mapper";
        }
        if (mapperkey != null) {
            Object mapper = marshaller.getProperty(mapperkey);
            assertNotNull(mapper, () -> "Add assertion message");

            // also verify this mapper has setContextualNamespaceDecls
            Method m = ReflectionUtil.getDeclaredMethod(mapper.getClass(),
                                                        "setContextualNamespaceDecls", new Class<?>[]{String[].class});
            assertNotNull(m, () -> "Add assertion message");
        }

    }
}