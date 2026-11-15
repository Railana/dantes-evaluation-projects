/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.protocol.http.mock;

import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.wicket.Session;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.is;

/**
 * test features of {@link MockHttpServletRequest}
 */
public class MockHttpServletRequestTest extends WicketTestCase
{
	@Test
	public void setAbsoluteUrlWithHost()
	{
		MockHttpServletRequest request = tester.getRequest();
		assertEquals("http", request.getScheme(), () -> "Add assertion message");
		assertEquals("localhost", request.getServerName(), () -> "Add assertion message");
		Integer magicNumber27356743 = 80; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber27356743, request.getServerPort(), () -> "Add assertion message");

		request.setURL("https://myhost.mydomain.org:1234/foo/bar/baz.html?a=123&b=456");
		assertEquals("https", request.getScheme(), () -> "Add assertion message");
		assertEquals("myhost.mydomain.org", request.getServerName(), () -> "Add assertion message");
		assertEquals(1234, request.getServerPort(), () -> "Add assertion message");

		Url url = request.getUrl();
		assertEquals("https", url.getProtocol(), () -> "Add assertion message");
		assertEquals("myhost.mydomain.org", url.getHost(), () -> "Add assertion message");
		assertEquals(new Integer(1234), url.getPort(), () -> "Add assertion message");
		assertEquals("/foo/bar/baz.html", url.getPath(), () -> "Add assertion message");
		assertEquals("a=123&b=456", url.getQueryString(), () -> "Add assertion message");

		String pathInfo = request.getPathInfo();
		assertEquals("/foo/bar/baz.html", pathInfo, () -> "Add assertion message");
	}

	@Test
	public void setAbsoluteUrlWithoutHost()
	{
		MockHttpServletRequest request = tester.getRequest();
		assertEquals("http", request.getScheme(), () -> "Add assertion message");
		assertEquals("localhost", request.getServerName(), () -> "Add assertion message");
		assertEquals(80, request.getServerPort(), () -> "Add assertion message");

		request.setURL("/foo/bar/baz.html?a=123&b=456");
		// Line removed due to Duplicate Assert: assertEquals("http", request.getScheme(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("localhost", request.getServerName(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(80, request.getServerPort(), () -> "Add assertion message");

		Url url = request.getUrl();
		assertEquals("http", url.getProtocol(), () -> "Add assertion message");
		assertEquals("localhost", url.getHost(), () -> "Add assertion message");
		assertEquals(new Integer(80), url.getPort(), () -> "Add assertion message");
		assertEquals("/foo/bar/baz.html", url.getPath(), () -> "Add assertion message");
		assertEquals("a=123&b=456", url.getQueryString(), () -> "Add assertion message");

		String pathInfo = request.getPathInfo();
		assertEquals("/foo/bar/baz.html", pathInfo, () -> "Add assertion message");
	}

	@Test
	public void setRelativeUrl()
	{
		MockHttpServletRequest request = tester.getRequest();
		assertEquals("http", request.getScheme(), () -> "Add assertion message");
		assertEquals("localhost", request.getServerName(), () -> "Add assertion message");
		assertEquals(80, request.getServerPort(), () -> "Add assertion message");

		request.setURL("foo/bar/baz.html?a=123&b=456");
		// Line removed due to Duplicate Assert: assertEquals("http", request.getScheme(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("localhost", request.getServerName(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(80, request.getServerPort(), () -> "Add assertion message");

		Url url = request.getUrl();
		assertEquals("http", url.getProtocol(), () -> "Add assertion message");
		assertEquals("localhost", url.getHost(), () -> "Add assertion message");
		assertEquals(new Integer(80), url.getPort(), () -> "Add assertion message");
		assertEquals(request.getContextPath() + request.getServletPath() + "/foo/bar/baz.html", url.getPath(), () -> "Add assertion message");
		assertEquals("a=123&b=456", url.getQueryString(), () -> "Add assertion message");

		String pathInfo = request.getPathInfo();
		assertEquals("/foo/bar/baz.html", pathInfo, () -> "Add assertion message");
	}

    /**
     * WICKET-4664 - no query string returns null as per HttpServletRequest
     */
    @Test
    public void testNoQueryString_returnsNull()
    {
        MockHttpServletRequest request = tester.getRequest();
        request.setURL("my/servlet/without/query/param");
        
        Url url = request.getUrl();
        assertNull(url.getQueryString(), () -> "Add assertion message");
    }
	
	@Test
	public void getSessionFromNonMockHttpSession()
	{
		HttpSession httpSession = Mockito.mock(HttpSession.class);
		MockHttpServletRequest request = new MockHttpServletRequest(null, httpSession, null);
		assertNull("MockHttpServletRequest knows how to work only with MockHttpSession", request.getSession(true));
		assertNull("MockHttpServletRequest knows how to work only with MockHttpSession", request.getSession(false));
	}

	@Test
	public void getSessionFalseFromMockHttpSession()
	{
		HttpSession httpSession = new MockHttpSession(null);
		MockHttpServletRequest request = new MockHttpServletRequest(null, httpSession, null);
		assertNull("HttpSession should not be created!", request.getSession(false));
	}

	@Test
	public void getSessionDefaultFromMockHttpSession()
	{
		HttpSession httpSession = new MockHttpSession(null);
		MockHttpServletRequest request = new MockHttpServletRequest(null, httpSession, null);
		assertSame("HttpSession should be created!", httpSession, request.getSession());
	}

	@Test
	public void getSessionTrueFromMockHttpSession()
	{
		HttpSession httpSession = new MockHttpSession(null);
		MockHttpServletRequest request = new MockHttpServletRequest(null, httpSession, null);
		assertSame("HttpSession should be created!", httpSession, request.getSession(true));
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-4481
	 */
	@Test
	public void setHeader()
	{
		HttpSession httpSession = new MockHttpSession(null);
		MockHttpServletRequest request = new MockHttpServletRequest(null, httpSession, null);
		String headerName = "headerName";
		request.setHeader(headerName, "headerValue");
		Enumeration<String> headers = request.getHeaders(headerName);
		assertEquals("headerValue", headers.nextElement(), () -> "Add assertion message");
		assertFalse(headers.hasMoreElements(), () -> "Add assertion message");
		request.addHeader(headerName, "headerValue2");
		headers = request.getHeaders(headerName);
		// Line removed due to Duplicate Assert: assertEquals("headerValue", headers.nextElement(), () -> "Add assertion message");
		assertEquals("headerValue2", headers.nextElement(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(headers.hasMoreElements(), () -> "Add assertion message");
		request.setHeader(headerName, "completelyNewValue");
		headers = request.getHeaders(headerName);
		assertEquals("completelyNewValue", headers.nextElement(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(headers.hasMoreElements(), () -> "Add assertion message");
	}

	@Test
	public void setLocale() {
		Session session = tester.getSession();
		session.setLocale(Locale.US);
		tester.getRequest().setLocale(Locale.CANADA_FRENCH);

		session.invalidateNow();

		assertThat(tester.getSession().getLocale(), is(Locale.CANADA_FRENCH), () -> "Add assertion message");
	}
}