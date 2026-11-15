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

package org.apache.cxf.common.util;

import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class URIParserUtilTest {

    @Test
    public void testRelativize() throws URISyntaxException {
        assertNull(URIParserUtil.relativize(null, "foo"), () -> "Add assertion message");
        assertNull(URIParserUtil.relativize("foo", null), () -> "Add assertion message");
        assertEquals("", URIParserUtil.relativize("", ""), () -> "Add assertion message");
        assertEquals("", URIParserUtil.relativize("fds", ""), () -> "Add assertion message");
        assertEquals("../", URIParserUtil.relativize("fds/", ""), () -> "Add assertion message");
        assertEquals("fdsfs", URIParserUtil.relativize("", "fdsfs"), () -> "Add assertion message");
        assertEquals("fdsfs/a", URIParserUtil.relativize("", "fdsfs/a"), () -> "Add assertion message");
        assertEquals("../de", URIParserUtil.relativize("ab/cd", "de"), () -> "Add assertion message");
        assertEquals("../de/fe/gh", URIParserUtil.relativize("ab/cd", "de/fe/gh"), () -> "Add assertion message");
        assertEquals("../../../de/fe/gh", URIParserUtil.relativize("/abc/def/", "de/fe/gh"), () -> "Add assertion message");
        assertNull(URIParserUtil.relativize("file:/c:/abc/def/", "de/fe/gh"), () -> "Add assertion message");
                                                                               // the 2 strings are not both
                                                                               // absolute or not absolute
        assertEquals("pippo2.xsd", URIParserUtil.relativize("/abc/def/pippo1.xsd", "/abc/def/pippo2.xsd"), () -> "Add assertion message");
        assertEquals("../default/pippo2.xsd",
                     URIParserUtil.relativize("/abc/def/pippo1.xsd", "/abc/default/pippo2.xsd"), () -> "Add assertion message");
        assertEquals("def/pippo2.xsd", URIParserUtil.relativize("/abc/def", "/abc/def/pippo2.xsd"), () -> "Add assertion message");
        assertEquals("hello_world_schema2.xsd",
                     URIParserUtil.relativize("jar:file:/home/a.jar!/wsdl/others/",
                                              "jar:file:/home/a.jar!/wsdl/others/hello_world_schema2.xsd"), () -> "Add assertion message");
    }

    @Test
    public void testNormalize() throws Exception {
        String uri = "wsdl/hello_world.wsdl";
        assertEquals("file:wsdl/hello_world.wsdl", URIParserUtil.normalize(uri), () -> "Add assertion message");

        uri = "\\src\\wsdl/hello_world.wsdl";
        assertEquals("file:/src/wsdl/hello_world.wsdl", URIParserUtil.normalize(uri), () -> "Add assertion message");

        uri = "wsdl\\hello_world.wsdl";
        // Line removed due to Duplicate Assert: assertEquals("file:wsdl/hello_world.wsdl", URIParserUtil.normalize(uri), () -> "Add assertion message");

        uri = "http://hello.com";
        assertEquals("http://hello.com", URIParserUtil.normalize(uri), () -> "Add assertion message");

        uri = "file:///c:\\hello.wsdl";
        assertEquals("file:/c:/hello.wsdl", URIParserUtil.normalize(uri), () -> "Add assertion message");

        uri = "c:\\hello.wsdl";
        // Line removed due to Duplicate Assert: assertEquals("file:/c:/hello.wsdl", URIParserUtil.normalize(uri), () -> "Add assertion message");

        uri = "/c:\\hello.wsdl";
        // Line removed due to Duplicate Assert: assertEquals("file:/c:/hello.wsdl", URIParserUtil.normalize(uri), () -> "Add assertion message");

        uri = "file:/home/john/test/all/../../alltest";
        assertEquals("file:/home/john/alltest", URIParserUtil.normalize(uri), () -> "Add assertion message");
    }

    @Test
    public void testGetAbsoluteURI() throws Exception {
        String uri = "wsdl/hello_world.wsdl";
        String uri2 = URIParserUtil.getAbsoluteURI(uri);
        assertNotNull(uri2, () -> "Add assertion message");
        assertTrue(uri2.startsWith("file"), () -> "Add assertion message");
        assertTrue(uri2.contains(uri), () -> "Add assertion message");
        assertTrue(uri2.contains(new java.io.File("").toString()), () -> "Add assertion message");

        uri = getClass().getResource("/schemas/echoSchema.xsd").toString();
        uri2 = URIParserUtil.getAbsoluteURI(uri);
        // Line removed due to Duplicate Assert: assertNotNull(uri2, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(uri2.startsWith("file"), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(uri2.contains(uri), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(uri2.contains(new java.io.File("").toString()), () -> "Add assertion message");

        uri = "c:\\wsdl\\hello_world.wsdl";
        uri2 = URIParserUtil.getAbsoluteURI(uri);
        // Line removed due to Duplicate Assert: assertNotNull(uri2, () -> "Add assertion message");
        assertEquals("file:/c:/wsdl/hello_world.wsdl", uri2, () -> "Add assertion message");

        uri = "/c:\\wsdl\\hello_world.wsdl";
        uri2 = URIParserUtil.getAbsoluteURI(uri);
        // Line removed due to Duplicate Assert: assertNotNull(uri2, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("file:/c:/wsdl/hello_world.wsdl", uri2, () -> "Add assertion message");

        uri = "http://hello/world.wsdl";
        assertEquals(uri, URIParserUtil.getAbsoluteURI(uri), () -> "Add assertion message");

        uri = "file:/home/john/test/all/../../alltest";
        assertEquals("file:/home/john/alltest", URIParserUtil.getAbsoluteURI(uri), () -> "Add assertion message");
    }
    @Test
    public void testCXF3855() throws Exception {
        String orig = new String(new byte[] {-47, -122}, StandardCharsets.UTF_8);
        orig = "/foo" + orig + ".txt";
        String s = URIParserUtil.escapeChars(orig);
        assertEquals(orig, URLDecoder.decode(s, StandardCharsets.UTF_8.name()), () -> "Add assertion message");
    }

}