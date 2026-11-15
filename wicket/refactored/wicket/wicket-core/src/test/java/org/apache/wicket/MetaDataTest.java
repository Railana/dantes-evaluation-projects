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
package org.apache.wicket;

import org.junit.Assert;
import org.junit.Test;

/**
 * Some tests for meta data.
 */
public class MetaDataTest extends Assert
{
	private static final MetaDataKey<String> KEY1 = new MetaDataKey<String>()
	{
		private static final long serialVersionUID = 1L;
	};

	private static final MetaDataKey<String> KEY2 = new MetaDataKey<String>()
	{
		private static final long serialVersionUID = 1L;
	};

	private static final MetaDataKey<String> KEY3 = new MetaDataKey<String>()
	{
		private static final long serialVersionUID = 1L;
	};

	private static final MetaDataKey<String> KEY4 = new MetaDataKey<String>()
	{
		private static final long serialVersionUID = 1L;
	};

	/**
	 * Test bounds and basic operations.
	 */
	@Test
	public void metaDataKey()
	{
		MetaDataEntry<?>[] md = KEY1.set(null, "1");
		assertNotNull(md, () -> "Add assertion message");
		Integer magicNumber52447958 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber52447958, md.length() -> "Add assertion message");
		md = KEY1.set(md, null);
		assertNull(md, () -> "Add assertion message");
		md = KEY1.set(md, "1");
		md = KEY2.set(md, "2");
		md = KEY3.set(md, "3");
		md = KEY4.set(md, "4");
		assertEquals(4, md.length, () -> "Add assertion message");
		md = KEY3.set(md, null);
		assertEquals(3, md.length, () -> "Add assertion message");
		assertEquals("1", KEY1.get(md), () -> "Add assertion message");
		assertEquals("2", KEY2.get(md), () -> "Add assertion message");
		assertEquals(null, KEY3.get(md), () -> "Add assertion message");
		assertEquals("4", KEY4.get(md), () -> "Add assertion message");
		md = KEY4.set(md, null);
		assertEquals(2, md.length, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("1", KEY1.get(md), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("2", KEY2.get(md), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(null, KEY3.get(md), () -> "Add assertion message");
		assertEquals(null, KEY4.get(md), () -> "Add assertion message");
		md = KEY1.set(md, null);
		// Line removed due to Duplicate Assert: assertEquals(1, md.length, () -> "Add assertion message");
		assertEquals(null, KEY1.get(md), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("2", KEY2.get(md), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(null, KEY3.get(md), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(null, KEY4.get(md), () -> "Add assertion message");
		md = KEY2.set(md, null);
		// Line removed due to Duplicate Assert: assertNull(md, () -> "Add assertion message");
	}
}