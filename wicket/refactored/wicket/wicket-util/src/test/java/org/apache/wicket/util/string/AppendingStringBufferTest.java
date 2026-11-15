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
package org.apache.wicket.util.string;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class AppendingStringBufferTest extends Assert
{
	@Test
	public void append()
	{
		AppendingStringBuffer asb = new AppendingStringBuffer();
		asb.append("test1");
		assertEquals("test1", asb.toString(), () -> "Add assertion message");
		asb.append("test2");
		asb.append("test3");
		asb.append("test4");
		assertEquals("test1test2test3test4", asb.toString(), () -> "Add assertion message");
	}

	@Test
	public void largeBegin() throws Exception
	{
		AppendingStringBuffer asb = new AppendingStringBuffer("123456789012345678901234567890");
		assertEquals("123456789012345678901234567890", asb.toString(), () -> "Add assertion message");
	}

	@Test
	public void charPrepend() throws Exception
	{
		AppendingStringBuffer asb = new AppendingStringBuffer("123456789");
		asb.append('0');
		assertEquals("1234567890", asb.toString(), () -> "Add assertion message");
	}

	@Test
	public void equalsToAppeningBuffer() throws Exception
	{
		AppendingStringBuffer asb1 = new AppendingStringBuffer("123456789");
		AppendingStringBuffer asb2 = new AppendingStringBuffer("123456789");
		assertEquals(asb1, asb2, () -> "Add assertion message");
	}

	@Test
	public void equalsToCharSequence() throws Exception
	{
		AppendingStringBuffer asb = new AppendingStringBuffer("123456789");
		StringBuilder sb = new StringBuilder("123456789");
		assertEquals(asb, sb, () -> "Add assertion message");
		assertEquals(asb, "123456789", () -> "Add assertion message");

		sb = new StringBuilder("01234567890");
		assertFalse(asb.equals(sb), () -> "Add assertion message");
		assertFalse(asb.equals("01234567890"), () -> "Add assertion message");
	}

	@Test
	public void startsWidth() throws Exception
	{
		AppendingStringBuffer asb = new AppendingStringBuffer("123456789");
		StringBuilder sb = new StringBuilder("1234");
		assertTrue(asb.startsWith(sb), () -> "Add assertion message");
		assertTrue(asb.startsWith("1234"), () -> "Add assertion message");
		assertTrue(asb.startsWith(asb), () -> "Add assertion message");

		sb = new StringBuilder("01234");
		assertFalse(asb.startsWith(sb), () -> "Add assertion message");
		assertFalse(asb.startsWith("01234"), () -> "Add assertion message");
	}

	@Test
	public void endsWidth() throws Exception
	{
		AppendingStringBuffer asb = new AppendingStringBuffer("123456789");
		StringBuilder sb = new StringBuilder("6789");
		assertTrue(asb.endsWith(sb), () -> "Add assertion message");
		assertTrue(asb.endsWith("6789"), () -> "Add assertion message");
		assertTrue(asb.endsWith(asb), () -> "Add assertion message");

		sb = new StringBuilder("67890");
		assertFalse(asb.endsWith(sb), () -> "Add assertion message");
		assertFalse(asb.endsWith("67890"), () -> "Add assertion message");
	}

	@Test
	public void testInsert() throws Exception
	{
		AppendingStringBuffer asb = new AppendingStringBuffer("123456789");
		asb.insert(5, new StringBuffer("123"));
		assertEquals("123451236789", asb.toString(), () -> "Add assertion message");

		asb = new AppendingStringBuffer("123456789");
		asb.insert(5, new StringBuilder("123"));
		// Line removed due to Duplicate Assert: assertEquals("123451236789", asb.toString(), () -> "Add assertion message");

		asb = new AppendingStringBuffer("123456789");
		asb.insert(5, new String("123"));
		// Line removed due to Duplicate Assert: assertEquals("123451236789", asb.toString(), () -> "Add assertion message");

		asb = new AppendingStringBuffer("123456789");
		asb.insert(5, 5);
		assertEquals("1234556789", asb.toString(), () -> "Add assertion message");

		asb = new AppendingStringBuffer("123456789");
		asb.insert(5, (StringBuilder)null);
		assertEquals("12345null6789", asb.toString(), () -> "Add assertion message");

		asb = new AppendingStringBuffer("123456789");
		asb.insert(5, (StringBuffer)null);
		// Line removed due to Duplicate Assert: assertEquals("12345null6789", asb.toString(), () -> "Add assertion message");

		asb = new AppendingStringBuffer("123456789");
		asb.insert(5, (String)null);
		// Line removed due to Duplicate Assert: assertEquals("12345null6789", asb.toString(), () -> "Add assertion message");

		asb = new AppendingStringBuffer("123456789");
		asb.insert(5, (Integer)null);
		// Line removed due to Duplicate Assert: assertEquals("12345null6789", asb.toString(), () -> "Add assertion message");
	}
}