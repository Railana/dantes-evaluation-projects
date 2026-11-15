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
package org.apache.wicket.util.parse.metapattern.parsers;

import org.junit.Assert;
import org.junit.Test;

/**
 * @since 1.5.8
 */
public class VariableAssignmentParserTest extends Assert
{
	/**
	 * Tests parsing of attribute names with and without namespaces
	 *
	 * https://issues.apache.org/jira/browse/WICKET-4679
	 */
	@Test
	public void parseAttributeName()
	{
		String tagName = "tagName";

		VariableAssignmentParser parser = new VariableAssignmentParser(tagName + " name='value'");

		assertTrue(parser.matcher().find(tagName.length()), () -> "Add assertion message");
		assertEquals("name", parser.getKey(), () -> "Add assertion message");
		assertEquals("'value'", parser.getValue(), () -> "Add assertion message");

		parser = new VariableAssignmentParser(tagName + " namespace:name='value'");

		// Line removed due to Duplicate Assert: assertTrue(parser.matcher().find(tagName.length()), () -> "Add assertion message");
		assertEquals("namespace:name", parser.getKey(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("'value'", parser.getValue(), () -> "Add assertion message");

		parser = new VariableAssignmentParser(tagName + " namespace:name:subname='value'");

		// Line removed due to Duplicate Assert: assertTrue(parser.matcher().find(tagName.length()), () -> "Add assertion message");
		assertEquals("namespace:name:subname", parser.getKey(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("'value'", parser.getValue(), () -> "Add assertion message");
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-6815
	 */
	@Test
	public void testTagAttributeParsing()
	{
		String tag = "label wicket:id=\"myid\" bind:test=\"test\" v-bind:test2=\"test2\" :test3=\"test3\" @test4=\"test4\"";
		VariableAssignmentParser attributeParser = new VariableAssignmentParser(tag);
		int pos = 5;
		assertTrue(attributeParser.matcher().find(pos), () -> "Add assertion message");
		String key = attributeParser.getKey();
		String value = attributeParser.getValue();
		pos = attributeParser.matcher().end(0);
		assertEquals("wicket:id", key, () -> "Add assertion message");
		assertEquals("\"myid\"", value, () -> "Add assertion message");

		// Line removed due to Duplicate Assert: assertTrue(attributeParser.matcher().find(pos), () -> "Add assertion message");
		key = attributeParser.getKey();
		value = attributeParser.getValue();
		pos = attributeParser.matcher().end(0);
		assertEquals("bind:test", key, () -> "Add assertion message");
		assertEquals("\"test\"", value, () -> "Add assertion message");

		// Line removed due to Duplicate Assert: assertTrue(attributeParser.matcher().find(pos), () -> "Add assertion message");
		key = attributeParser.getKey();
		value = attributeParser.getValue();
		pos = attributeParser.matcher().end(0);
		assertEquals("v-bind:test2", key, () -> "Add assertion message");
		assertEquals("\"test2\"", value, () -> "Add assertion message");

		// Line removed due to Duplicate Assert: assertTrue(attributeParser.matcher().find(pos), () -> "Add assertion message");
		key = attributeParser.getKey();
		value = attributeParser.getValue();
		pos = attributeParser.matcher().end(0);
		assertEquals(":test3", key, () -> "Add assertion message");
		assertEquals("\"test3\"", value, () -> "Add assertion message");

		// Line removed due to Duplicate Assert: assertTrue(attributeParser.matcher().find(pos), () -> "Add assertion message");
		key = attributeParser.getKey();
		value = attributeParser.getValue();
		assertEquals("@test4", key, () -> "Add assertion message");
		assertEquals("\"test4\"", value, () -> "Add assertion message");
	}
}