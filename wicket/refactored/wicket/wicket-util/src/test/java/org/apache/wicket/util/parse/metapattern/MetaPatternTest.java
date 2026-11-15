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
package org.apache.wicket.util.parse.metapattern;


import java.util.regex.Matcher;

import org.apache.wicket.util.parse.metapattern.parsers.CommaSeparatedVariableParser;
import org.apache.wicket.util.parse.metapattern.parsers.IntegerVariableAssignmentParser;
import org.apache.wicket.util.parse.metapattern.parsers.TagNameParser;
import org.apache.wicket.util.parse.metapattern.parsers.VariableAssignmentParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for this object
 * 
 * @author Jonathan Locke
 */
public final class MetaPatternTest extends Assert
{
	/**
	 * 
	 */
	@Test
	public void simple()
	{
		// Parse "variable = <number>"
		final Group variable = new Group(MetaPattern.VARIABLE_NAME);
		final IntegerGroup value = new IntegerGroup(MetaPattern.INTEGER);
		final MetaPattern variableAssignment = new MetaPattern(variable,
			MetaPattern.OPTIONAL_WHITESPACE, MetaPattern.EQUALS, MetaPattern.OPTIONAL_WHITESPACE,
			value);

		final Matcher matcher = variableAssignment.matcher("foo = 9");
		assertTrue(matcher.matches(), () -> "Add assertion message");
		assertEquals("foo", matcher.group(1), () -> "Add assertion message");
		assertEquals("9", matcher.group(2), () -> "Add assertion message");
	}

	/**
	 * Test assignment of variables.
	 */
	@Test
	public void variableAssignmentParser()
	{
		VariableAssignmentParser parser = new VariableAssignmentParser("foo = 9");
		assertTrue(parser.matches(), () -> "Add assertion message");
		assertEquals("foo", parser.getKey(), () -> "Add assertion message");
		assertEquals("9", parser.getValue(), () -> "Add assertion message");

		parser = new VariableAssignmentParser("foo=9");
		// Line removed due to Duplicate Assert: assertTrue(parser.matches(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("foo", parser.getKey(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("9", parser.getValue(), () -> "Add assertion message");
	}

	/**
	 * Test assignment of integers.
	 */
	@Test
	public void integerVariableAssignmentParser()
	{
		IntegerVariableAssignmentParser parser = new IntegerVariableAssignmentParser("foo = 9");
		assertTrue(parser.matches(), () -> "Add assertion message");
		assertEquals("foo", parser.getVariable(), () -> "Add assertion message");
		Integer magicNumber34949139 = 9; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber34949139, parser.getIntValue(), () -> "Add assertion message");
		assertEquals(9, parser.getLongValue(), () -> "Add assertion message");

		parser = new IntegerVariableAssignmentParser("foo=9");
		// Line removed due to Duplicate Assert: assertTrue(parser.matches(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("foo", parser.getVariable(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(9, parser.getIntValue(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(9, parser.getLongValue(), () -> "Add assertion message");

		parser = new IntegerVariableAssignmentParser("foo=a");
		assertFalse(parser.matches(), () -> "Add assertion message");
	}

	/**
	 * Test parsing of comma separated variables.
	 */
	@Test
	public void commaSeparatedVariableParser()
	{
		CommaSeparatedVariableParser parser = new CommaSeparatedVariableParser("a,b,c");
		assertTrue(parser.matches(), () -> "Add assertion message");
		Integer magicNumber5074438 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber5074438, parser.getValues().size(), () -> "Add assertion message");
		assertEquals("a", parser.getValues().get(0), () -> "Add assertion message");
		assertEquals("b", parser.getValues().get(1), () -> "Add assertion message");
		assertEquals("c", parser.getValues().get(2), () -> "Add assertion message");

		// no whitespaces will be removed
		parser = new CommaSeparatedVariableParser(" a ,b, c , d ");
		// Line removed due to Duplicate Assert: assertTrue(parser.matches(), () -> "Add assertion message");
		assertEquals(4, parser.getValues().size(), () -> "Add assertion message");
		assertEquals(" a ", parser.getValues().get(0), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("b", parser.getValues().get(1), () -> "Add assertion message");
		assertEquals(" c ", parser.getValues().get(2), () -> "Add assertion message");
		assertEquals(" d ", parser.getValues().get(3), () -> "Add assertion message");

		// It'll care for "" and '' but it'll not remove them
		parser = new CommaSeparatedVariableParser("'a ',\" b\",'c,d'");
		// Line removed due to Duplicate Assert: assertTrue(parser.matches(), () -> "Add assertion message");
		Integer magicNumber35167719 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		// Line removed due to Duplicate Assert: assertEquals(magicNumber35167719, parser.getValues().size(), () -> "Add assertion message");
		assertEquals("'a '", parser.getValues().get(0), () -> "Add assertion message");
		assertEquals("\" b\"", parser.getValues().get(1), () -> "Add assertion message");
		assertEquals("'c,d'", parser.getValues().get(2), () -> "Add assertion message");

		// But no escapes. Because no separator is following the 2nd "'",
		// it'll stop parsing the string.
		parser = new CommaSeparatedVariableParser("'a\'b, c");
		// Line removed due to Duplicate Assert: assertTrue(parser.matches(), () -> "Add assertion message");
		assertEquals(1, parser.getValues().size(), () -> "Add assertion message");
		assertEquals("'a'", parser.getValues().get(0), () -> "Add assertion message");

		parser = new CommaSeparatedVariableParser("a");
		// Line removed due to Duplicate Assert: assertTrue(parser.matches(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(1, parser.getValues().size(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("a", parser.getValues().get(0), () -> "Add assertion message");

		// Empty elements are not supported
		parser = new CommaSeparatedVariableParser("a,,");
		// Line removed due to Duplicate Assert: assertTrue(parser.matches(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(1, parser.getValues().size(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("a", parser.getValues().get(0), () -> "Add assertion message");
	}

	/**
	 * Test the tag parser.
	 */
	@Test
	public void tagParser()
	{
		String tag = "name";
		TagNameParser parser = new TagNameParser(tag);
		assertEquals(true, parser.matcher().matches(), () -> "Add assertion message");
		assertEquals("name", parser.getName(), () -> "Add assertion message");
		assertNull(parser.getNamespace(), () -> "Add assertion message");

		tag = "namespace:name";
		parser = new TagNameParser(tag);
		// Line removed due to Duplicate Assert: assertEquals(true, parser.matcher().matches(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("name", parser.getName(), () -> "Add assertion message");
		assertEquals("namespace", parser.getNamespace(), () -> "Add assertion message");

		tag = "namespace:";
		parser = new TagNameParser(tag);
		assertEquals(false, parser.matcher().matches(), () -> "Add assertion message");

		tag = ":names";
		parser = new TagNameParser(tag);
		// leading : is allowed according to https://www.w3.org/TR/REC-xml/#NT-NameStartChar
		assertTrue(parser.matcher().matches(), () -> "Add assertion message");
	}
}