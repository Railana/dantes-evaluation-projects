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

package org.apache.cxf.jaxrs.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.apache.cxf.jaxrs.resources.BookInterface;
import org.apache.cxf.jaxrs.resources.BookStore;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class WebClientTest {

    @Test
    public void testReplaceHeader() {
        WebClient wc = WebClient.create("http://foo").header("a", "b");
        Integer magicNumber20119207 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber20119207, wc.getHeaders().size(), () -> "Add assertion message");
        assertEquals("b", wc.getHeaders().getFirst("a"), () -> "Add assertion message");
        wc.replaceHeader("a", "c");
        // Line removed due to Duplicate Assert: assertEquals(1, wc.getHeaders().size(), () -> "Add assertion message");
        assertEquals("c", wc.getHeaders().getFirst("a"), () -> "Add assertion message");
    }

    @Test
    public void testRemoveHeader() {
        WebClient wc = WebClient.create("http://foo").header("a", "b");
        assertEquals(1, wc.getHeaders().size(), () -> "Add assertion message");
        assertEquals("b", wc.getHeaders().getFirst("a"), () -> "Add assertion message");
        wc.replaceHeader("a", null);
        assertEquals(0, wc.getHeaders().size(), () -> "Add assertion message");
    }

    @Test
    public void testEncoding() {
        URI u = WebClient.create("http://foo").path("bar+ %2B").matrix("a", "value+ ")
            .query("b", "bv+ %2B").getCurrentURI();
        assertEquals("http://foo/bar+%20%2B;a=value+%20?b=bv%2B+%2B", u.toString(), () -> "Add assertion message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPath() {
        WebClient.create("http://foo").path(null);
        fail("Exception expected");
    }

    @Test
    public void testExistingAsteriscs() {
        URI u = WebClient.create("http://foo/*").getCurrentURI();
        assertEquals("http://foo/*", u.toString(), () -> "Add assertion message");
    }

    @Test
    public void testAsteriscs() {
        URI u = WebClient.create("http://foo").path("*").getCurrentURI();
        assertEquals("http://foo/*", u.toString(), () -> "Add assertion message");
    }

    @Test
    public void testAddressNotEncoded() {
        URI u = WebClient.create("http://localhost/somepath ").getCurrentURI();
        assertEquals("http://localhost/somepath%20", u.toString(), () -> "Add assertion message");
    }

    @Test
    public void testDoubleAsteriscs() {
        URI u = WebClient.create("http://foo").path("**").getCurrentURI();
        assertEquals("http://foo/**", u.toString(), () -> "Add assertion message");
    }

    @Test
    public void testBaseCurrentPath() {
        assertEquals(URI.create("http://foo"), WebClient.create("http://foo").getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo"), WebClient.create("http://foo").getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testNewBaseCurrentPath() {
        WebClient wc = WebClient.create("http://foo");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.to("http://bar", false);
        assertEquals(URI.create("http://bar"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://bar"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testBaseCurrentWebSocketPath() {
        WebClient wc = WebClient.create("ws://foo");
        assertEquals(URI.create("ws://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("ws://foo"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.path("a");
        // Line removed due to Duplicate Assert: assertEquals(URI.create("ws://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("ws://foo/a"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testNewBaseCurrentWebSocketPath() {
        WebClient wc = WebClient.create("ws://foo");
        assertEquals(URI.create("ws://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("ws://foo"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.to("ws://bar", false);
        assertEquals(URI.create("ws://bar"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("ws://bar"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testEmptyQuery() {
        WebClient wc = WebClient.create("http://foo");
        wc.query("_wadl");
        assertEquals("http://foo?_wadl", wc.getCurrentURI().toString(), () -> "Add assertion message");
    }

    @Test
    public void testEmptyQueryKey() {
        WebClient wc = WebClient.create("http://foo");
        wc.query("");
        assertEquals("http://foo", wc.getCurrentURI().toString(), () -> "Add assertion message");
    }

    @Test
    public void testForward() {
        WebClient wc = WebClient.create("http://foo");
        wc.to("http://foo/bar", true);
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testCompositePath() {
        WebClient wc = WebClient.create("http://foo");
        wc.path("/bar/baz/");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz/"), wc.getCurrentURI(), () -> "Add assertion message");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testWrongForward() {
        WebClient wc = WebClient.create("http://foo");
        wc.to("http://bar", true);
    }

    @Test
    public void testBaseCurrentPathAfterChange() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz").matrix("m1", "m1value").query("q1", "q1value");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz;m1=m1value?q1=q1value"), wc.getCurrentURI(), () -> "Add assertion message");
    }


    @Test
    public void testBaseCurrentPathAfterCopy() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz").matrix("m1", "m1value").query("q1", "q1value");
        WebClient wc1 = WebClient.fromClient(wc);
        assertEquals(URI.create("http://foo/bar/baz;m1=m1value?q1=q1value"), wc1.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz;m1=m1value?q1=q1value"), wc1.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testHeaders() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.header("h1", "h1value").header("h2", "h2value");
        assertEquals(1, wc.getHeaders().get("h1").size(), () -> "Add assertion message");
        assertEquals("h1value", wc.getHeaders().getFirst("h1"), () -> "Add assertion message");
        assertEquals(1, wc.getHeaders().get("h2").size(), () -> "Add assertion message");
        assertEquals("h2value", wc.getHeaders().getFirst("h2"), () -> "Add assertion message");
        wc.getHeaders().clear();
        // Line removed due to Duplicate Assert: assertEquals(1, wc.getHeaders().get("h1").size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("h1value", wc.getHeaders().getFirst("h1"), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, wc.getHeaders().get("h2").size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("h2value", wc.getHeaders().getFirst("h2"), () -> "Add assertion message");
        wc.reset();
        assertTrue(wc.getHeaders().isEmpty(), () -> "Add assertion message");
    }

    @Test
    public void testBackFast() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz").matrix("m1", "m1value");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz;m1=m1value"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.back(true);
        assertEquals(URI.create("http://foo"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testBack() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.back(false);
        assertEquals(URI.create("http://foo/bar"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.back(false);
        assertEquals(URI.create("http://foo"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.back(false);
        // Line removed due to Duplicate Assert: assertEquals(URI.create("http://foo"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testResetQueryAndBack() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz").query("foo", "bar");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz?foo=bar"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.resetQuery().back(false);
        assertEquals(URI.create("http://foo/bar"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testReplaceQuery() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz").query("foo", "bar");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz?foo=bar"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.replaceQuery("foo1=bar1");
        assertEquals(URI.create("http://foo/bar/baz?foo1=bar1"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testReplaceQueryParam() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz").query("foo", "bar").query("foo1", "bar1");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz?foo=bar&foo1=bar1"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.replaceQueryParam("foo1", "baz");
        assertEquals(URI.create("http://foo/bar/baz?foo=bar&foo1=baz"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testReplacePathAll() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.replacePath("/new");
        assertEquals(URI.create("http://foo/new"), wc.getCurrentURI(), () -> "Add assertion message");
    }
    @Test
    public void testReplacePathLastSegment() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz"), wc.getCurrentURI(), () -> "Add assertion message");
        wc.replacePath("new");
        assertEquals(URI.create("http://foo/bar/new"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testFragment() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        wc.path("bar").path("baz").query("foo", "bar").fragment("1");
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/bar/baz?foo=bar#1"), wc.getCurrentURI(), () -> "Add assertion message");
    }


    @Test
    public void testPathWithTemplates() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo"), wc.getCurrentURI(), () -> "Add assertion message");

        wc.path("{bar}/{foo}", 1, 2);
        // Line removed due to Duplicate Assert: assertEquals(URI.create("http://foo"), wc.getBaseURI(), () -> "Add assertion message");
        assertEquals(URI.create("http://foo/1/2"), wc.getCurrentURI(), () -> "Add assertion message");
    }

    @Test
    public void testWebClientConfiguration() {
        WebClient wc = WebClient.create(URI.create("http://foo"));
        assertNotNull(WebClient.getConfig(wc) != null, () -> "Add assertion message");
    }

    @Test
    public void testWebClientParamConverter() {
        WebClient wc = WebClient.create("http://foo",
                                        Collections.singletonList(new ParamConverterProviderImpl()));
        wc.path(new ComplexObject());
        wc.query("param", new ComplexObject(), new ComplexObject());
        assertEquals("http://foo/complex?param=complex&param=complex", wc.getCurrentURI().toString(), () -> "Add assertion message");

    }

    @Test
    public void testProxyConfiguration() {
        // interface
        BookInterface proxy = JAXRSClientFactory.create("http://foo", BookInterface.class);
        assertNotNull(WebClient.getConfig(proxy) != null, () -> "Add assertion message");
        // cglib
        BookStore proxy2 = JAXRSClientFactory.create("http://foo", BookStore.class);
        assertNotNull(WebClient.getConfig(proxy2) != null, () -> "Add assertion message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyNull() {
        // interface
        BookInterface proxy = JAXRSClientFactory.create("http://foo", BookInterface.class);
        proxy.getBook(null);
    }

    @Test
    public void testWebClientAuthorization() {
        String auth = "auth";
        WebClient wc = WebClient.create(URI.create("http://foo")).authorization(auth);
        assertEquals(auth, wc.getHeaders().getFirst(HttpHeaders.AUTHORIZATION), () -> "Add assertion message");
    }

    @Test
    public void testLanguageHeader() {
        WebClient wc = WebClient.create("http://foo").language("en_CA");
        assertEquals("en_CA", wc.getHeaders().getFirst(HttpHeaders.CONTENT_LANGUAGE), () -> "Add assertion message");
    }

    private static final class ParamConverterProviderImpl implements ParamConverterProvider {

        @SuppressWarnings("unchecked")
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            return (ParamConverter<T>)new ParamConverterImpl();
        }

    }

    private static final class ParamConverterImpl implements ParamConverter<ComplexObject> {

        @Override
        public ComplexObject fromString(String value) throws IllegalArgumentException {
            return null;
        }

        @Override
        public String toString(ComplexObject value) throws IllegalArgumentException {
            return "complex";
        }
    }

    private static final class ComplexObject {

    }
}