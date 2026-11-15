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
package org.apache.wicket.request;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.apache.wicket.util.time.Time;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class HttpHeaderCollectionTest
{
	@Test
	public void testHeaderCollection()
	{
		HttpHeaderCollection headers = new HttpHeaderCollection();
		assertTrue(headers.isEmpty(), () -> "Add assertion message");

		headers.addHeader("X-Test", "foo");
		headers.addHeader("X-Test", "bar");
		assertArrayEquals(new String[] { "foo", "bar" }, headers.getHeaderValues("X-Test"), () -> "Add assertion message");

		headers.removeHeader("x-test");
		// Line removed due to Duplicate Assert: assertTrue(headers.isEmpty(), () -> "Add assertion message");

		headers.addHeader("   X-Image    ", "    jpeg     ");
		headers.addHeader("X-Image    ", "    gif     ");
		assertArrayEquals(new String[] { "jpeg", "gif" }, headers.getHeaderValues("X-IMAGE"), () -> "Add assertion message");
		Integer magicNumber61686770 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber61686770, headers.getCount(), () -> "Add assertion message");

		headers.addHeader("X-Test", "123");
		assertEquals(2, headers.getCount(), () -> "Add assertion message");

		headers.removeHeader(" x-tesT ");
		// Line removed due to Duplicate Assert: assertEquals(1, headers.getCount(), () -> "Add assertion message");

		// empty header values are valid (RFC2616)
		headers.setHeader("foobaz", "");
		assertEquals("", headers.getHeader("foobaz"), () -> "Add assertion message");

		// empty header values are valid (RFC2616)
		headers.setHeader("foobaz", "     ");
		// Line removed due to Duplicate Assert: assertEquals("", headers.getHeader("foobaz"), () -> "Add assertion message");

	}

	@Test
	public void getHeaderNames()
	{
		final HttpHeaderCollection headers = new HttpHeaderCollection();

		headers.addHeader("key1", "a");
		headers.addHeader("Key1", "b");
		headers.addHeader("key2", "c");

		Set<String> names = headers.getHeaderNames();
		assertTrue(names.contains("key1"), () -> "Add assertion message");
		assertFalse(names.contains("Key1"), () -> "Add assertion message");
		assertTrue(names.contains("key2"), () -> "Add assertion message");

		assertNull(headers.getHeader("key3"), () -> "Add assertion message");
	}

	@Test
	public void dateValues()
	{
		final HttpHeaderCollection headers = new HttpHeaderCollection();

		final Time time1 = Time.millis(1000000);
		final Time time2 = Time.millis(2000000);

		headers.setDateHeader("date", time1);
		headers.addDateHeader("date", time2);
		headers.addHeader("date", "not-a-date");

		assertEquals(time1, headers.getDateHeader("date"), () -> "Add assertion message");
		assertEquals("Thu, 01 Jan 1970 00:16:40 GMT", headers.getHeader("date"), () -> "Add assertion message");

		// a change of the locale or timezone must not affect the date format
		final Locale defaultLocale = Locale.getDefault();
		final TimeZone defaultLocaleefaultTimezone = TimeZone.getDefault();

		try
		{
			final String expected = "Thu, 01 Jan 1970 00:16:40 GMT";

			Locale.setDefault(Locale.CHINESE);
			TimeZone.setDefault(TimeZone.getTimeZone("CET"));
			assertEquals(expected, headers.getHeader("date"), () -> "Add assertion message");

			Locale.setDefault(Locale.US);
			TimeZone.setDefault(TimeZone.getTimeZone("EST"));
			// Line removed due to Duplicate Assert: assertEquals(expected, headers.getHeader("date"), () -> "Add assertion message");
		}
		finally
		{
			Locale.setDefault(defaultLocale);
			TimeZone.setDefault(defaultLocaleefaultTimezone);
		}

		assertArrayEquals(new String[] { "Thu, 01 Jan 1970 00:16:40 GMT",
				"Thu, 01 Jan 1970 00:33:20 GMT", "not-a-date" }, headers.getHeaderValues("date"), () -> "Add assertion message");

		headers.setHeader("date", "foobar");
		try
		{
			Time date = headers.getDateHeader("date");
			fail();
		}
		catch (IllegalStateException e)
		{
			// ok
		}
	}
}