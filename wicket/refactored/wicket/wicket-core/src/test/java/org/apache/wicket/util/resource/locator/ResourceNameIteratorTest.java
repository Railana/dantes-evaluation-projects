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
package org.apache.wicket.util.resource.locator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import org.apache.wicket.core.util.resource.locator.ExtensionResourceNameIterator;
import org.apache.wicket.core.util.resource.locator.LocaleResourceNameIterator;
import org.apache.wicket.core.util.resource.locator.ResourceNameIterator;
import org.apache.wicket.core.util.resource.locator.StyleAndVariationResourceNameIterator;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;


/**
 * @author Pedro Santos
 */
public class ResourceNameIteratorTest extends WicketTestCase
{
	/**
	 * Asserting no duplicated locale sufix get iterated
	 */
	@Test
	public void localeResourceNameIterator()
	{
		Locale locale = new Locale("a", "b", "c");
		LocaleResourceNameIterator iterator = new LocaleResourceNameIterator(locale, false);
		HashSet<String> variations = new HashSet<String>();
		while (iterator.hasNext())
		{
			assertTrue(variations.add(iterator.next()), () -> "Add assertion message");
		}
		Integer magicNumber40267548 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber40267548, variations.size(), () -> "Add assertion message");
		assertTrue(variations.contains("_a_B_c"), () -> "Add assertion message");
		assertTrue(variations.contains("_a_B"), () -> "Add assertion message");
		assertTrue(variations.contains("_a"), () -> "Add assertion message");
		assertTrue(variations.contains(""), () -> "Add assertion message");

		locale = new Locale("a", "b");
		iterator = new LocaleResourceNameIterator(locale, false);
		variations = new HashSet<String>();
		while (iterator.hasNext())
		{
			// Line removed due to Duplicate Assert: assertTrue(variations.add(iterator.next()), () -> "Add assertion message");
		}
		assertEquals(3, variations.size(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(variations.contains("_a_B"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(variations.contains("_a"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(variations.contains(""), () -> "Add assertion message");

		locale = new Locale("a");
		iterator = new LocaleResourceNameIterator(locale, false);
		variations = new HashSet<String>();
		while (iterator.hasNext())
		{
			// Line removed due to Duplicate Assert: assertTrue(variations.add(iterator.next()), () -> "Add assertion message");
		}
		assertEquals(2, variations.size(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(variations.contains("_a"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(variations.contains(""), () -> "Add assertion message");
	}

	/**
	 * 
	 */
	@Test
	public void styleAndVariationResourceNameIterator()
	{
		StyleAndVariationResourceNameIterator iterator = new StyleAndVariationResourceNameIterator(
			null, null);
		assertTrue(iterator.hasNext(), () -> "Add assertion message");
		iterator.next();
		assertFalse(iterator.hasNext(), () -> "Add assertion message");

		iterator = new StyleAndVariationResourceNameIterator("style", null);
		// Line removed due to Duplicate Assert: assertTrue(iterator.hasNext(), () -> "Add assertion message");
		iterator.next();
		assertEquals("style", iterator.getStyle(), () -> "Add assertion message");
		assertEquals(null, iterator.getVariation(), () -> "Add assertion message");
		iterator.next();
		assertEquals(null, iterator.getStyle(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(null, iterator.getVariation(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(iterator.hasNext(), () -> "Add assertion message");

		iterator = new StyleAndVariationResourceNameIterator("style", "variation");
		// Line removed due to Duplicate Assert: assertTrue(iterator.hasNext(), () -> "Add assertion message");
		iterator.next();
		// Line removed due to Duplicate Assert: assertEquals("style", iterator.getStyle(), () -> "Add assertion message");
		assertEquals("variation", iterator.getVariation(), () -> "Add assertion message");
		iterator.next();
		// Line removed due to Duplicate Assert: assertEquals("style", iterator.getStyle(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(null, iterator.getVariation(), () -> "Add assertion message");
		iterator.next();
		// Line removed due to Duplicate Assert: assertEquals(null, iterator.getStyle(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals("variation", iterator.getVariation(), () -> "Add assertion message");
		iterator.next();
		// Line removed due to Duplicate Assert: assertEquals(null, iterator.getStyle(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(null, iterator.getVariation(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(iterator.hasNext(), () -> "Add assertion message");
	}

	/**
	 * 
	 */
	@Test
	public void extensionResourceNameIterator()
	{
		ExtensionResourceNameIterator iterator = new ExtensionResourceNameIterator(null);
		assertTrue(iterator.hasNext(), () -> "Add assertion message");
		assertEquals(null, iterator.next(), () -> "Add assertion message");
		assertFalse(iterator.hasNext(), () -> "Add assertion message");

		iterator = new ExtensionResourceNameIterator(Arrays.asList("txt"));
		// Line removed due to Duplicate Assert: assertTrue(iterator.hasNext(), () -> "Add assertion message");
		assertEquals("txt", iterator.next(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(iterator.hasNext(), () -> "Add assertion message");

		iterator = new ExtensionResourceNameIterator(Arrays.asList("properties", "utf8.properties", "properties.xml"));
		// Line removed due to Duplicate Assert: assertTrue(iterator.hasNext(), () -> "Add assertion message");
		assertEquals("properties", iterator.next(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(iterator.hasNext(), () -> "Add assertion message");
		assertEquals("utf8.properties", iterator.next(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(iterator.hasNext(), () -> "Add assertion message");
		assertEquals("properties.xml", iterator.next(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(iterator.hasNext(), () -> "Add assertion message");
	}

	/**
	 * 
	 */
	@Test
	public void noDuplicateVariations()
	{
		String path = "patch.extension";
		String style = null;
		String var = "var";
		Locale locale = Locale.CANADA_FRENCH;
		Iterable<String> extensions = null;
		boolean strict = false;
		Iterator<String> iterator = new ResourceNameIterator(path, style, var, locale, extensions, strict);
		HashSet<String> variations = new HashSet<String>();
		while (iterator.hasNext())
		{
			assertTrue(variations.add(iterator.next()), () -> "Add assertion message");
		}
		assertEquals(6, variations.size(), () -> "Add assertion message");
	}

	/**
	 * 
	 */
	@Test
	public void noTrailingDotWhenNoExtension()
	{
		Iterator<String> iterator = new ResourceNameIterator("foo", null, null, null, null, false);

		assertTrue(iterator.hasNext(), () -> "Add assertion message");

		assertEquals("foo", iterator.next(), () -> "Add assertion message");

		assertFalse(iterator.hasNext(), () -> "Add assertion message");
	}
}