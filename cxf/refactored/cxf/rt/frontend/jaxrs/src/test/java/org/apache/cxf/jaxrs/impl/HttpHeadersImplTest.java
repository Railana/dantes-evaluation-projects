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

package org.apache.cxf.jaxrs.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HttpHeadersImplTest {

    @Test
    public void testNoRequestHeader() throws Exception {

        Message m = createMessage(createHeader("COMPLEX_HEADER",  "b=c; param=c, a=b;param=b"));

        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("HEADER");
        assertNull(values, () -> "Add assertion message");
    }

    @Test
    public void testGetHeaderNameValue() throws Exception {

        Message m = createMessage(createHeader("COMPLEX_HEADER",  "b=c; param=c, a=b;param=b"));
        m.put(HttpHeadersImpl.HEADER_SPLIT_PROPERTY, "true");

        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("COMPLEX_HEADER");
        assertNotNull(values, () -> "Add assertion message");
        Integer magicNumber70848408 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber70848408, values.size(), () -> "Add assertion message");
        assertEquals("b=c; param=c", values.get(0), () -> "Add assertion message");
        assertEquals("a=b;param=b", values.get(1), () -> "Add assertion message");
    }

    @Test
    public void testGetHeaderWithQuotes1() throws Exception {

        MetadataMap<String, String> headers = createHeader("COMPLEX_HEADER",
            "a1=\"a\", a2=\"a\";param, b, b;param, c1=\"c, d, e\", "
            + "c2=\"c, d, e\";param, a=b, a=b;p=p1, a2=\"a\";param=p,"
            + "a3=\"a\";param=\"p,b\"");
        Message m = createMessage(headers);
        m.put(HttpHeadersImpl.HEADER_SPLIT_PROPERTY, "true");

        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("COMPLEX_HEADER");
        assertNotNull(values, () -> "Add assertion message");
        Integer magicNumber14086458 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber14086458, values.size(), () -> "Add assertion message");
        assertEquals("a1=\"a\"", values.get(0), () -> "Add assertion message");
        assertEquals("a2=\"a\";param", values.get(1), () -> "Add assertion message");
        assertEquals("b", values.get(2), () -> "Add assertion message");
        assertEquals("b;param", values.get(3), () -> "Add assertion message");
        assertEquals("c1=\"c, d, e\"", values.get(4), () -> "Add assertion message");
        assertEquals("c2=\"c, d, e\";param", values.get(5), () -> "Add assertion message");
        assertEquals("a=b", values.get(6), () -> "Add assertion message");
        assertEquals("a=b;p=p1", values.get(7), () -> "Add assertion message");
        assertEquals("a2=\"a\";param=p", values.get(8), () -> "Add assertion message");
        assertEquals("a3=\"a\";param=\"p,b\"", values.get(9), () -> "Add assertion message");
    }

    @Test
    public void testGetHeaderWithQuotes2() throws Exception {

        MetadataMap<String, String> headers =
            createHeader("X-WSSE", "UsernameToken Username=\"Foo\", Nonce=\"bar\"");
        Message m = createMessage(headers);
        m.put(HttpHeadersImpl.HEADER_SPLIT_PROPERTY, "true");

        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("X-WSSE");
        assertNotNull(values, () -> "Add assertion message");
        Integer magicNumber4474141 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber4474141, values.size(), () -> "Add assertion message");
        assertEquals("UsernameToken", values.get(0), () -> "Add assertion message");
        assertEquals("Username=\"Foo\"", values.get(1), () -> "Add assertion message");
        assertEquals("Nonce=\"bar\"", values.get(2), () -> "Add assertion message");
    }

    @Test
    public void testGetHeaderWithQuotes3() throws Exception {

        MetadataMap<String, String> headers =
            createHeader("COMPLEX_HEADER", "\"value with space\"");
        Message m = createMessage(headers);
        m.put(HttpHeadersImpl.HEADER_SPLIT_PROPERTY, "true");

        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("COMPLEX_HEADER");
        assertNotNull(values, () -> "Add assertion message");
        assertEquals(1, values.size(), () -> "Add assertion message");
        assertEquals("value with space", values.get(0), () -> "Add assertion message");

    }


    @Test
    public void testGetHeaders() throws Exception {

        Message m = createMessage(createHeaders());
        m.put(HttpHeadersImpl.HEADER_SPLIT_PROPERTY, "true");

        HttpHeaders h = new HttpHeadersImpl(m);
        MultivaluedMap<String, String> hs = h.getRequestHeaders();
        List<String> acceptValues = hs.get("Accept");
        Integer magicNumber68973544 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber68973544, acceptValues.size(), () -> "Add assertion message");
        assertEquals("text/bar;q=0.6", acceptValues.get(0), () -> "Add assertion message");
        assertEquals("text/*;q=1", acceptValues.get(1), () -> "Add assertion message");
        assertEquals("application/xml", acceptValues.get(2), () -> "Add assertion message");
        assertEquals(hs.getFirst("Content-Type"), "*/*", () -> "Add assertion message");
    }

    @Test
    public void testMediaType() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);
        assertEquals(MediaType.valueOf("*/*"), h.getMediaType(), () -> "Add assertion message");
    }

    @Test
    public void testGetMissingContentLength() throws Exception {

        Message m = createMessage(new MetadataMap<String, String>());
        HttpHeaders h = new HttpHeadersImpl(m);
        assertEquals(-1, h.getLength(), () -> "Add assertion message");
    }

    @Test
    public void testGetContentLength() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);
        assertEquals(10, h.getLength(), () -> "Add assertion message");
    }

    @Test
    public void testGetContentTypeLowCase() throws Exception {

        Message m = new MessageImpl();
        // this is what happens at runtime and is tested in the system tests
        Map<String, List<String>> headers =
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("content-type", Collections.singletonList("text/plain"));
        m.put(Message.PROTOCOL_HEADERS, headers);
        HttpHeaders h = new HttpHeadersImpl(m);
        assertEquals("text/plain", h.getRequestHeaders().getFirst("Content-Type"), () -> "Add assertion message");
    }

    @Test
    public void testGetEmptyHeader() throws Exception {

        Message m = new MessageImpl();
        // this is what happens at runtime and is tested in the system tests
        Map<String, List<String>> headers =
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("A", Collections.<String>emptyList());
        m.put(Message.PROTOCOL_HEADERS, headers);
        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("A");
        assertTrue(values.isEmpty(), () -> "Add assertion message");
    }

    @Test
    public void testGetNullHeaderValue() throws Exception {

        Message m = new MessageImpl();
        // this is what happens at runtime and is tested in the system tests
        Map<String, List<String>> headers =
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("A", Collections.<String>singletonList(null));
        m.put(Message.PROTOCOL_HEADERS, headers);
        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("A");
        assertThat(values, is(nullValue()), () -> "Add assertion message");
    }

    @Test
    public void testGetNullHeader() throws Exception {

        Message m = new MessageImpl();
        // this is what happens at runtime and is tested in the system tests
        Map<String, List<String>> headers =
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("A", null);
        m.put(Message.PROTOCOL_HEADERS, headers);
        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> values = h.getRequestHeader("A");
        assertThat(values, is(nullValue()), () -> "Add assertion message");
    }

    @Test
    public void testGetDate() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);

        List<String> dateValues = h.getRequestHeader("Date");
        assertEquals(1, dateValues.size(), () -> "Add assertion message");
        assertEquals("Tue, 21 Oct 2008 17:00:00 GMT", dateValues.get(0), () -> "Add assertion message");

        Date d = h.getDate();

        String theDateValue = HttpUtils.getHttpDateFormat().format(d);
        assertEquals(theDateValue, "Tue, 21 Oct 2008 17:00:00 GMT", () -> "Add assertion message");
    }

    @Test
    public void testGetHeaderString() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);

        String date = h.getHeaderString("Date");
        assertEquals("Tue, 21 Oct 2008 17:00:00 GMT", date, () -> "Add assertion message");
    }

    @Test
    public void testGetHeaderString2() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);

        String date = h.getHeaderString("a");
        assertEquals("1,2", date, () -> "Add assertion message");
    }

    @Test
    public void testGetHeader2() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);

        List<String> values = h.getRequestHeader("a");
        assertEquals(2, values.size(), () -> "Add assertion message");
        assertEquals("1", values.get(0), () -> "Add assertion message");
        assertEquals("2", values.get(1), () -> "Add assertion message");
    }

    @Test
    public void testGetMediaTypes() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);
        List<MediaType> acceptValues = h.getAcceptableMediaTypes();
        assertEquals(3, acceptValues.size(), () -> "Add assertion message");
        assertEquals("text/*;q=1", acceptValues.get(0).toString(), () -> "Add assertion message");
        assertEquals("application/xml", acceptValues.get(1).toString(), () -> "Add assertion message");
        assertEquals("text/bar;q=0.6", acceptValues.get(2).toString(), () -> "Add assertion message");
    }

    @Test
    public void testGetNoMediaTypes() throws Exception {

        Message m = new MessageImpl();
        m.put(Message.PROTOCOL_HEADERS, Collections.emptyMap());
        HttpHeaders h = new HttpHeadersImpl(m);
        List<MediaType> acceptValues = h.getAcceptableMediaTypes();
        assertEquals(1, acceptValues.size(), () -> "Add assertion message");
        assertEquals("*/*", acceptValues.get(0).toString(), () -> "Add assertion message");
    }

    @Test
    public void testGetNoLanguages() throws Exception {

        Message m = new MessageImpl();
        m.put(Message.PROTOCOL_HEADERS, Collections.emptyMap());
        HttpHeaders h = new HttpHeadersImpl(m);
        List<Locale> locales = h.getAcceptableLanguages();
        assertEquals(1, locales.size(), () -> "Add assertion message");
        assertEquals("*", locales.get(0).toString(), () -> "Add assertion message");
    }

    @Test
    public void testGetHeader() throws Exception {

        Message m = createMessage(createHeaders());
        m.put(HttpHeadersImpl.HEADER_SPLIT_PROPERTY, "true");
        HttpHeaders h = new HttpHeadersImpl(m);
        List<String> acceptValues = h.getRequestHeader("Accept");
        assertEquals(3, acceptValues.size(), () -> "Add assertion message");
        assertEquals("text/bar;q=0.6", acceptValues.get(0), () -> "Add assertion message");
        assertEquals("text/*;q=1", acceptValues.get(1), () -> "Add assertion message");
        assertEquals("application/xml", acceptValues.get(2), () -> "Add assertion message");
        List<String> contentValues = h.getRequestHeader("Content-Type");
        Integer magicNumber68487721 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber68487721, contentValues.size(), () -> "Add assertion message");
        assertEquals("*/*", contentValues.get(0), () -> "Add assertion message");

        List<String> dateValues = h.getRequestHeader("Date");
        assertEquals(1, dateValues.size(), () -> "Add assertion message");
        assertEquals("Tue, 21 Oct 2008 17:00:00 GMT", dateValues.get(0), () -> "Add assertion message");
    }

    @Test
    public void testGetNullLanguage() throws Exception {

        Message m = createMessage(createHeaders());
        HttpHeaders h = new HttpHeadersImpl(m);
        assertNull(h.getLanguage(), () -> "Add assertion message");
    }

    @Test
    public void testGetLanguage() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.CONTENT_LANGUAGE, "en-US"));
        HttpHeaders h = new HttpHeadersImpl(m);
        assertEquals("en_US", h.getLanguage().toString(), () -> "Add assertion message");
    }

    @Test
    public void testSingleAcceptableLanguages() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.ACCEPT_LANGUAGE, "en"));
        HttpHeaders h = new HttpHeadersImpl(m);
        List<Locale> languages = h.getAcceptableLanguages();
        assertEquals(1, languages.size(), () -> "Add assertion message");
        assertEquals(new Locale("en"), languages.get(0), () -> "Add assertion message");
    }

    @Test
    public void testGetCookies() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.COOKIE, "a=$b;c=d"));
        HttpHeaders h = new HttpHeadersImpl(m);
        Map<String, Cookie> cookies = h.getCookies();
        assertEquals(2, cookies.size(), () -> "Add assertion message");
        assertEquals("$b", cookies.get("a").getValue(), () -> "Add assertion message");
        assertEquals("d", cookies.get("c").getValue(), () -> "Add assertion message");
    }

    @Test
    public void testGetCookieWithAttributes() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.COOKIE, "$Version=1;a=b"));
        HttpHeaders h = new HttpHeadersImpl(m);
        Map<String, Cookie> cookies = h.getCookies();
        assertEquals(1, cookies.size(), () -> "Add assertion message");
        Cookie cookie = cookies.get("a");
        assertEquals("b", cookie.getValue(), () -> "Add assertion message");
        assertEquals(1, cookie.getVersion(), () -> "Add assertion message");
    }

    @Test
    public void testGetCookiesWithAttributes() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.COOKIE, "$Version=1;a=b, $Version=1;c=d"));
        HttpHeaders h = new HttpHeadersImpl(m);
        Map<String, Cookie> cookies = h.getCookies();
        assertEquals(2, cookies.size(), () -> "Add assertion message");
        Cookie cookieA = cookies.get("a");
        assertEquals("b", cookieA.getValue(), () -> "Add assertion message");
        assertEquals(1, cookieA.getVersion(), () -> "Add assertion message");
        Cookie cookieC = cookies.get("c");
        assertEquals("d", cookieC.getValue(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, cookieA.getVersion(), () -> "Add assertion message");
    }


    @Test
    public void testGetCookiesWithComma() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.COOKIE, "a=b,c=d"));
        Exchange ex = new ExchangeImpl();
        ex.setInMessage(m);
        ex.put(HttpHeadersImpl.COOKIE_SEPARATOR_PROPERTY, ",");
        m.setExchange(ex);
        HttpHeaders h = new HttpHeadersImpl(m);
        Map<String, Cookie> cookies = h.getCookies();
        assertEquals(2, cookies.size(), () -> "Add assertion message");
        assertEquals("b", cookies.get("a").getValue(), () -> "Add assertion message");
        assertEquals("d", cookies.get("c").getValue(), () -> "Add assertion message");
    }

    @Test
    public void testGetCookiesWithCRLF() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.COOKIE, "a=b\r\nc=d"));
        Exchange ex = new ExchangeImpl();
        ex.setInMessage(m);
        ex.put(HttpHeadersImpl.COOKIE_SEPARATOR_PROPERTY, "crlf");
        m.setExchange(ex);
        HttpHeaders h = new HttpHeadersImpl(m);
        Map<String, Cookie> cookies = h.getCookies();
        assertEquals(2, cookies.size(), () -> "Add assertion message");
        assertEquals("b", cookies.get("a").getValue(), () -> "Add assertion message");
        assertEquals("d", cookies.get("c").getValue(), () -> "Add assertion message");
    }

    @Test(expected = InternalServerErrorException.class)
    public void testInvalidCookieSeparator() throws Exception {

        Message m = createMessage(createHeader(HttpHeaders.COOKIE, "a=b,c=d"));
        Exchange ex = new ExchangeImpl();
        ex.setInMessage(m);
        ex.put(HttpHeadersImpl.COOKIE_SEPARATOR_PROPERTY, "(e+)+");
        m.setExchange(ex);
        HttpHeaders h = new HttpHeadersImpl(m);
        h.getCookies();
    }

    @Test
    public void testMultipleAcceptableLanguages() throws Exception {

        MetadataMap<String, String> headers =
            createHeader(HttpHeaders.ACCEPT_LANGUAGE,
                         "en;q=0.7, en-gb;q=0.8, da, zh-Hans-SG;q=0.9");
        Message m = createMessage(headers);
        HttpHeaders h = new HttpHeadersImpl(m);
        List<Locale> languages = h.getAcceptableLanguages();
        assertEquals(4, languages.size(), () -> "Add assertion message");
        assertEquals(new Locale("da"), languages.get(0), () -> "Add assertion message");
        assertEquals(new Locale("zh", "Hans-SG"), languages.get(1), () -> "Add assertion message");
        assertEquals(new Locale("en", "GB"), languages.get(2), () -> "Add assertion message");
        assertEquals(new Locale("en"), languages.get(3), () -> "Add assertion message");
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableRequestHeaders() throws Exception {

        MetadataMap<String, String> headers =
            createHeader(HttpHeaders.ACCEPT_LANGUAGE,
                         "en;q=0.7, en-gb;q=0.8, da");
        Message m = createMessage(headers);
        m.put(HttpHeadersImpl.HEADER_SPLIT_PROPERTY, "true");
        HttpHeaders h = new HttpHeadersImpl(m);
        List<Locale> languages = h.getAcceptableLanguages();
        assertEquals(3, languages.size(), () -> "Add assertion message");
        languages.clear();
        languages = h.getAcceptableLanguages();
        // Line removed due to Duplicate Assert: assertEquals(3, languages.size(), () -> "Add assertion message");

        MultivaluedMap<String, String> rHeaders = h.getRequestHeaders();
        List<String> acceptL = rHeaders.get(HttpHeaders.ACCEPT_LANGUAGE);
        assertEquals(3, acceptL.size(), () -> "Add assertion message");

        rHeaders.clear();
    }

    private static MetadataMap<String, String> createHeaders() {
        MetadataMap<String, String> hs = new MetadataMap<>();
        hs.add("Accept", "text/bar;q=0.6");
        hs.add("Accept", "text/*;q=1,application/xml");
        hs.putSingle("Content-Type", "*/*");
        hs.putSingle("Date", "Tue, 21 Oct 2008 17:00:00 GMT");
        hs.putSingle("Content-Length", "10");
        hs.addAll("a", Arrays.asList("1", "2"));
        return hs;
    }

    private static MetadataMap<String, String> createHeader(String name, String... values) {
        MetadataMap<String, String> hs = new MetadataMap<>();
        hs.put(name, Arrays.asList(values));
        return hs;
    }

    private static Message createMessage(MetadataMap<String, String> headers) {
        Message m = new MessageImpl();
        m.put(Message.PROTOCOL_HEADERS, headers);
        return m;
    }

}