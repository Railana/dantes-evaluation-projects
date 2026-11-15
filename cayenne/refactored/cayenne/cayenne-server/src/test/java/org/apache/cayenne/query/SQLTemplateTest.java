/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.util.Util;
import org.junit.Test;

public class SQLTemplateTest {

	@Test
	public void testSetParams() throws Exception {
		SQLTemplate query = new SQLTemplate();

		assertTrue(query.getParams().isEmpty(), () -> "Add assertion message");

		Map<String, Object> params = new HashMap<>();
		params.put("a", "b");

		query.setParams(params);
		assertEquals(params, query.getParams(), () -> "Add assertion message");

		query.setParams(null);
		// Line removed due to Duplicate Assert: assertTrue(query.getParams().isEmpty(), () -> "Add assertion message");
	}

	@Test
	public void testSetParamsArray() throws Exception {
		SQLTemplate query = new SQLTemplate();

		assertTrue(query.getPositionalParams().isEmpty(), () -> "Add assertion message");

		query.setParamsArray("N", "m");
		assertEquals(Arrays.asList("N", "m"), query.getPositionalParams(), () -> "Add assertion message");

		query.setParamsArray();
		// Line removed due to Duplicate Assert: assertTrue(query.getPositionalParams().isEmpty(), () -> "Add assertion message");
	}

	@Test
	public void testSetParams_MixingStyles() throws Exception {

		SQLTemplate query = new SQLTemplate();

		assertTrue(query.getParams().isEmpty(), () -> "Add assertion message");
		assertTrue(query.getPositionalParams().isEmpty(), () -> "Add assertion message");

		Map<String, Object> params = Collections.<String, Object> singletonMap("a", "b");
		query.setParams(params);
		assertEquals(params, query.getParams(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(query.getPositionalParams().isEmpty(), () -> "Add assertion message");

		query.setParamsArray("D", "G");
		assertEquals(Arrays.asList("D", "G"), query.getPositionalParams(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(query.getParams().isEmpty(), () -> "Add assertion message");

		// even resetting named to null should result in resetting positional
		query.setParams(null);
		// Line removed due to Duplicate Assert: assertTrue(query.getParams().isEmpty(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(query.getPositionalParams().isEmpty(), () -> "Add assertion message");
	}

	@Test
	public void testGetDefaultTemplate() {
		SQLTemplate query = new SQLTemplate();
		query.setDefaultTemplate("AAA # BBB");
		assertEquals("AAA # BBB", query.getDefaultTemplate(), () -> "Add assertion message");
	}

	@Test
	public void testGetTemplate() {
		SQLTemplate query = new SQLTemplate();

		// no template for key, no default template... must be null
		assertNull(query.getTemplate("key1"), () -> "Add assertion message");

		// no template for key, must return default
		query.setDefaultTemplate("AAA # BBB");
		assertEquals("AAA # BBB", query.getTemplate("key1"), () -> "Add assertion message");

		// must find template
		query.setTemplate("key1", "XYZ");
		assertEquals("XYZ", query.getTemplate("key1"), () -> "Add assertion message");

		// add another template.. still must find
		query.setTemplate("key2", "123");
		// Line removed due to Duplicate Assert: assertEquals("XYZ", query.getTemplate("key1"), () -> "Add assertion message");
		assertEquals("123", query.getTemplate("key2"), () -> "Add assertion message");
	}

	@Test
	public void testColumnNameCapitalization() {
		SQLTemplate q1 = new SQLTemplate("E1", "SELECT");
		assertSame(CapsStrategy.DEFAULT, q1.getColumnNamesCapitalization(), () -> "Add assertion message");
		q1.setColumnNamesCapitalization(CapsStrategy.UPPER);
		assertEquals(CapsStrategy.UPPER, q1.getColumnNamesCapitalization(), () -> "Add assertion message");
	}

	@Test
	public void testSerializability() throws Exception {
		SQLTemplate o = new SQLTemplate("Test", "DO SQL");
		Object clone = Util.cloneViaSerialization(o);

		assertTrue(clone instanceof SQLTemplate, () -> "Add assertion message");
		SQLTemplate c1 = (SQLTemplate) clone;

		assertNotSame(o, c1, () -> "Add assertion message");
		assertEquals(o.getRoot(), c1.getRoot(), () -> "Add assertion message");
		assertEquals(o.getDefaultTemplate(), c1.getDefaultTemplate(), () -> "Add assertion message");
	}
}