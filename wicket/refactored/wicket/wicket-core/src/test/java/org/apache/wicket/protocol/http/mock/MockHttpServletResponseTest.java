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

import java.util.List;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MockHttpServletResponse}
 */
public class MockHttpServletResponseTest extends Assert
{
	private MockHttpServletResponse response;

	/**
	 * Prepare
	 */
	@Before
	public void before()
	{
		response = new MockHttpServletResponse(null);
	}

	/**
	 * Clean up
	 */
	@After
	public void after()
	{
		response = null;
	}

	/**
	 * Add a cookie
	 */
	@Test
	public void addCookie()
	{
		Cookie cookie = new Cookie("name", "value");
		response.addCookie(cookie);

		List<Cookie> cookies = response.getCookies();
		Integer magicNumber47695967 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber47695967, cookies.size(), () -> "Add assertion message");
		assertEquals("name", cookies.get(0).getName(), () -> "Add assertion message");
		assertEquals("value", cookies.get(0).getValue(), () -> "Add assertion message");
		assertNull(cookies.get(0).getComment(), () -> "Add assertion message");
		assertNull(cookies.get(0).getDomain(), () -> "Add assertion message");
		assertEquals(-1, cookies.get(0).getMaxAge(), () -> "Add assertion message");
		assertNull(cookies.get(0).getPath(), () -> "Add assertion message");
		assertEquals(false, cookies.get(0).getSecure(), () -> "Add assertion message");
		assertEquals(0, cookies.get(0).getVersion(), () -> "Add assertion message");
	}

	/**
	 * Add a duplicate cookie. <br/>
	 * https://issues.apache.org/jira/browse/WICKET-4292
	 */
	@Test
	public void addDuplicateCookie()
	{
		Cookie cookie1 = new Cookie("name", "value");
		response.addCookie(cookie1);
		assertEquals(1, response.getCookies().size(), () -> "Add assertion message");

		Cookie cookie2 = new Cookie("name", "value");
		response.addCookie(cookie2);
		// Line removed due to Duplicate Assert: assertEquals(1, response.getCookies().size(), () -> "Add assertion message");

		Cookie cookie3 = new Cookie("name", "value");
		cookie3.setPath("/");
		response.addCookie(cookie3);
		assertEquals(2, response.getCookies().size(), () -> "Add assertion message");

		Cookie cookie4 = new Cookie("name", "value");
		cookie4.setPath("/");
		response.addCookie(cookie4);
		// Line removed due to Duplicate Assert: assertEquals(2, response.getCookies().size(), () -> "Add assertion message");

		Cookie cookie5 = new Cookie("name", "value");
		cookie5.setPath("/");
		cookie5.setDomain("example.com");
		response.addCookie(cookie5);
		assertEquals(3, response.getCookies().size(), () -> "Add assertion message");

		Cookie cookie6 = new Cookie("name", "value");
		cookie6.setPath("/");
		cookie6.setDomain("example.com");
		response.addCookie(cookie6);
		// Line removed due to Duplicate Assert: assertEquals(3, response.getCookies().size(), () -> "Add assertion message");
	}
}