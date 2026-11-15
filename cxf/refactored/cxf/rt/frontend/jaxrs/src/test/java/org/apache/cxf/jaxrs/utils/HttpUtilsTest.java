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

package org.apache.cxf.jaxrs.utils;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.model.ParameterType;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpUtilsTest {

    @Test
    public void testEncodePartiallyEncoded() {
        assertEquals("/address", HttpUtils.encodePartiallyEncoded("/address", false), () -> "Add assertion message");
    }
    @Test
    public void testEncodePartiallyEncoded2() {
        assertEquals("/add%20ress", HttpUtils.encodePartiallyEncoded("/add ress", false), () -> "Add assertion message");
    }
    @Test
    public void testEncodePartiallyEncoded3() {
        assertEquals("/add%20ress", HttpUtils.encodePartiallyEncoded("/add%20ress", false), () -> "Add assertion message");
    }
    @Test
    public void testEncodePartiallyEncoded4() {
        assertEquals("http://localhost:8080/", 
                     HttpUtils.encodePartiallyEncoded("http://localhost:8080/", false), () -> "Add assertion message");
    }
    @Test
    public void testEncodePartiallyEncoded5() {
        assertEquals("http://localhost:8080/1/%202", 
                     HttpUtils.encodePartiallyEncoded("http://localhost:8080/1/ 2", false), () -> "Add assertion message");
    }
    
    @Test
    public void testUrlDecode() {
        assertEquals("+ ", HttpUtils.urlDecode("%2B+"), () -> "Add assertion message");
    }

    @Test
    public void testCommaInQuery() {
        assertEquals("a+,b", HttpUtils.queryEncode("a ,b"), () -> "Add assertion message");
    }

    @Test
    public void testRelativize() throws Exception {
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6226081
        URI a = new URI("file:/c:/abc/def/myDocument/doc.xml");
        URI b = new URI("file:/c:/abc/def/images/subdir/image.png");

        URI c = HttpUtils.relativize(a, b);

        assertEquals("../images/subdir/image.png", c.toString(), () -> "Add assertion message");
    }

    @Test
    public void testMediaTypeWithUTF8() {
        assertEquals("UTF-8",
                     HttpUtils.getEncoding(MediaType.valueOf("application/json;charset=UTF-8"), "UTF-16"), () -> "Add assertion message");
    }
    @Test
    public void testMediaTypeWithUTF8WithQuotes() {
        assertEquals("UTF-8",
                     HttpUtils.getEncoding(MediaType.valueOf("application/json;charset=\"UTF-8\""), "UTF-16"), () -> "Add assertion message");
    }
    @Test
    public void testMediaTypeWithNoCharset() {
        assertEquals("UTF-16",
                     HttpUtils.getEncoding(MediaType.valueOf("application/json"), "UTF-16"), () -> "Add assertion message");
    }
    
    @Test
    public void testIsDateHeader() {
        assertFalse(HttpUtils.isDateRelatedHeader(HttpHeaders.ETAG), () -> "Add assertion message");
        assertTrue(HttpUtils.isDateRelatedHeader(HttpHeaders.EXPIRES), () -> "Add assertion message");
        assertTrue(HttpUtils.isDateRelatedHeader(HttpHeaders.IF_MODIFIED_SINCE), () -> "Add assertion message");
        assertTrue(HttpUtils.isDateRelatedHeader(HttpHeaders.IF_UNMODIFIED_SINCE), () -> "Add assertion message");
        assertTrue(HttpUtils.isDateRelatedHeader(HttpHeaders.DATE), () -> "Add assertion message");
        assertTrue(HttpUtils.isDateRelatedHeader(HttpHeaders.LAST_MODIFIED), () -> "Add assertion message");
    }

    @Test
    public void testUrlEncode() {
        assertEquals("%2B+", HttpUtils.urlEncode("+ "), () -> "Add assertion message");
    }


    @Test
    public void testPathEncode() {
        // rfc3986.txt 3.3
        //segment-nz = 1*pchar
        //pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
        // sub-delims = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
        // unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"

        // '&' has to be represented as &amp; in WADL

        String pathChars = ":@!$&'()*+,;=-._~";
        String str = HttpUtils.pathEncode(pathChars);
        assertEquals(str, pathChars, () -> "Add assertion message");
    }

    @Test
    public void testPathEncodeWithPlusAndSpace() {
        assertEquals("+%20", HttpUtils.pathEncode("+ "), () -> "Add assertion message");
    }

    @Test
    public void testURLEncode() {
        assertEquals("%2B+", HttpUtils.urlEncode("+ "), () -> "Add assertion message");
    }

    @Test
    public void testUrlDecodeReserved() {
        assertEquals("!$&'()*,;=", HttpUtils.urlDecode("!$&'()*,;="), () -> "Add assertion message");
    }

    @Test
    public void testPathDecode() {
        assertEquals("+++", HttpUtils.pathDecode("+%2B+"), () -> "Add assertion message");
    }

    @Test
    public void testPathToMatch() {
        assertEquals("/", HttpUtils.getPathToMatch("/", "/", true), () -> "Add assertion message");
        assertEquals("/", HttpUtils.getPathToMatch("/", "/bar", true), () -> "Add assertion message");
        assertEquals("/", HttpUtils.getPathToMatch("/bar", "/bar/", true), () -> "Add assertion message");
        assertEquals("/bar", HttpUtils.getPathToMatch("/bar", "/", true), () -> "Add assertion message");

        assertEquals("/", HttpUtils.getPathToMatch("/bar", "/bar", true), () -> "Add assertion message");
        assertEquals("/bar", HttpUtils.getPathToMatch("/baz/bar", "/baz", true), () -> "Add assertion message");
        assertEquals("/baz/bar/foo/", HttpUtils.getPathToMatch("/baz/bar/foo/", "/bar", true), () -> "Add assertion message");

    }

    @Test
    public void testUpdatePath() {

        Message m = new MessageImpl();
        m.setExchange(new ExchangeImpl());
        m.put(Message.ENDPOINT_ADDRESS, "http://localhost/");
        HttpUtils.updatePath(m, "/bar");
        assertEquals("/bar", m.get(Message.REQUEST_URI), () -> "Add assertion message");
        HttpUtils.updatePath(m, "bar");
        // Line removed due to Duplicate Assert: assertEquals("/bar", m.get(Message.REQUEST_URI), () -> "Add assertion message");
        HttpUtils.updatePath(m, "bar/");
        assertEquals("/bar/", m.get(Message.REQUEST_URI), () -> "Add assertion message");
        m.put(Message.ENDPOINT_ADDRESS, "http://localhost");
        HttpUtils.updatePath(m, "bar/");
        // Line removed due to Duplicate Assert: assertEquals("/bar/", m.get(Message.REQUEST_URI), () -> "Add assertion message");
    }


    @Test
    public void testParameterErrorStatus() {
        assertEquals(Response.Status.NOT_FOUND,
                     HttpUtils.getParameterFailureStatus(ParameterType.PATH), () -> "Add assertion message");
        assertEquals(Response.Status.NOT_FOUND,
                     HttpUtils.getParameterFailureStatus(ParameterType.QUERY), () -> "Add assertion message");
        assertEquals(Response.Status.NOT_FOUND,
                     HttpUtils.getParameterFailureStatus(ParameterType.MATRIX), () -> "Add assertion message");
        assertEquals(Response.Status.BAD_REQUEST,
                     HttpUtils.getParameterFailureStatus(ParameterType.HEADER), () -> "Add assertion message");
        assertEquals(Response.Status.BAD_REQUEST,
                     HttpUtils.getParameterFailureStatus(ParameterType.FORM), () -> "Add assertion message");
        assertEquals(Response.Status.BAD_REQUEST,
                     HttpUtils.getParameterFailureStatus(ParameterType.COOKIE), () -> "Add assertion message");
    }

    @Test
    public void testGetBaseAddressHttpUri() {
        doTestGetBaseAddress("http://localhost:8080/store?query", "/store");
    }

    @Test
    public void testGetBaseAddressHttpEncodedUri() {
        doTestGetBaseAddress("http://localhost:8080/store%20?query", "/store%20");
    }

    @Test
    public void testGetBaseAddressJmsUri() {
        doTestGetBaseAddress("jms://topic", "/");
    }

    @Test
    public void testGetBaseAddressWithoutScheme() {
        doTestGetBaseAddress("/s", "/s");
    }

    @Test
    public void testGetBaseAddressHttpUriMixedCase() {
        final String baseURI = "HTTP://LoCALHoST:8080/STORE";
        
        Message m = new MessageImpl();
        HttpServletRequest req = mock(HttpServletRequest.class);
        m.put(AbstractHTTPDestination.HTTP_REQUEST, req);
        Exchange exchange = new ExchangeImpl();
        
        when(req.getRequestURL()).thenReturn(new StringBuffer(baseURI));
        when(req.getPathInfo()).thenReturn("/STORE");
        when(req.getContextPath()).thenReturn("/");
        when(req.getServletPath()).thenReturn("/");

        m.setExchange(exchange);
        Destination dest = mock(Destination.class);
        exchange.setDestination(dest);
        m.put(Message.BASE_PATH, baseURI);
        String address = HttpUtils.getBaseAddress(m);
        assertEquals("/STORE", address, () -> "Add assertion message");
    }

    @Test
    public void testReplaceAnyIPAddress() {
        Message m = new MessageImpl();
        HttpServletRequest req = mock(HttpServletRequest.class);
        m.put(AbstractHTTPDestination.HTTP_REQUEST, req);
        when(req.getScheme()).thenReturn("http");
        when(req.getServerName()).thenReturn("localhost");
        when(req.getServerPort()).thenReturn(8080);
        URI u = HttpUtils.toAbsoluteUri(URI.create("http://0.0.0.0/bar/foo"), m);
        assertEquals("http://localhost:8080/bar/foo", u.toString(), () -> "Add assertion message");
    }

    @Test
    public void testReplaceAnyIPAddressWithPort() {
        doTestReplaceAnyIPAddressWithPort(true);
    }
    @Test
    public void testReplaceLHostIPAddressWithPort() {
        doTestReplaceAnyIPAddressWithPort(false);
    }

    private void doTestReplaceAnyIPAddressWithPort(boolean anyIp) {
        Message m = new MessageImpl();
        HttpServletRequest req = mock(HttpServletRequest.class);
        m.put(AbstractHTTPDestination.HTTP_REQUEST, req);
        when(req.getScheme()).thenReturn("http");
        when(req.getServerName()).thenReturn("localhost");
        when(req.getServerPort()).thenReturn(8080);
        String host = anyIp ? "0.0.0.0" : "127.0.0.1";
        URI u = HttpUtils.toAbsoluteUri(URI.create("http://" + host + ":8080/bar/foo"), m);
        assertEquals("http://localhost:8080/bar/foo", u.toString());
    }

    @Test
    public void testReplaceLocalHostWithPort() {
        Message m = new MessageImpl();
        URI u = HttpUtils.toAbsoluteUri(URI.create("http://localhost:8080/bar/foo"), m);
        assertEquals("http://localhost:8080/bar/foo", u.toString(), () -> "Add assertion message");
    }

    private void doTestGetBaseAddress(String baseURI, String expected) {
        Message m = new MessageImpl();
        Exchange exchange = new ExchangeImpl();
        m.setExchange(exchange);
        Destination dest = mock(Destination.class);
        exchange.setDestination(dest);
        m.put(Message.BASE_PATH, baseURI);
        String address = HttpUtils.getBaseAddress(m);
        assertEquals(expected, address);
    }
}