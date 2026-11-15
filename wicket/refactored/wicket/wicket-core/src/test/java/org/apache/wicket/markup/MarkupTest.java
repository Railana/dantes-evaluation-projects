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
package org.apache.wicket.markup;

import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;

/**
 * Tests {@link Markup} class.
 */
public class MarkupTest extends WicketTestCase
{
	@Test
	public void testFind()
	{
		MarkupFactory markupFactory = tester.getApplication().getMarkupSettings().getMarkupFactory();
		Markup markup = markupFactory.getMarkup(new MarkupTest_Find_3(), false);

		IMarkupFragment childMarkup;

		/*
		 * Ensure we can find inside <head>
		 */
		childMarkup = markup.find("a1");
		assertNotNull(childMarkup, () -> "Add assertion message");
		assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("a1", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we can find in body
		 */
		childMarkup = markup.find("a2");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("a2", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we cannot find inside component tag
		 */
		assertNull(markup.find("a3"), () -> "Add assertion message");

		/*
		 * Ensure we can find after other component tag
		 */
		childMarkup = markup.find("a4");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("a4", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we can find after wicket:child
		 */
		childMarkup = markup.find("a5");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("a5", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we can find after fragment
		 */
		childMarkup = markup.find("a6");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		assertTrue(childMarkup.get(0) instanceof WicketTag, () -> "Add assertion message");
		assertEquals("a6", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");
		assertTrue(((WicketTag)childMarkup.get(0)).isFragmentTag(), () -> "Add assertion message");

		/*
		 * Ensure we cannot find inside fragment
		 */
		assertNull(markup.find("a7"), () -> "Add assertion message");

		/*
		 * Ensure we can find in subclass <wicket:head> section
		 */
		childMarkup = markup.find("b1");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("b1", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we can find fragment in subclass
		 */
		childMarkup = markup.find("b2");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof WicketTag, () -> "Add assertion message");
		assertEquals("b2", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(((WicketTag)childMarkup.get(0)).isFragmentTag(), () -> "Add assertion message");

		/*
		 * Ensure we cannot find inside fragment in subclass
		 */
		assertNull(markup.find("b3"), () -> "Add assertion message");

		/*
		 * Ensure we can find in subclass <wicket:extend> section
		 */
		childMarkup = markup.find("b4");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("b4", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we cannot find inside component tag in subclass
		 */
		assertNull(markup.find("b5"), () -> "Add assertion message");

		/*
		 * Ensure we cannot find inside component tag in subclass after wicket:child
		 */
		assertNull(markup.find("b6"), () -> "Add assertion message");

		/*
		 * Ensure we can find in subclass <wicket:head> section
		 */
		childMarkup = markup.find("c1");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("c1", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we can find fragment in subclass
		 */
		childMarkup = markup.find("c2");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof WicketTag, () -> "Add assertion message");
		assertEquals("c2", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(((WicketTag)childMarkup.get(0)).isFragmentTag(), () -> "Add assertion message");

		/*
		 * Ensure we cannot find inside fragment in subclass
		 */
		assertNull(markup.find("c3"), () -> "Add assertion message");

		/*
		 * Ensure we can find in subclass <wicket:extend> section
		 */
		childMarkup = markup.find("c4");
		// Line removed due to Duplicate Assert: assertNotNull(childMarkup, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(childMarkup.get(0) instanceof ComponentTag, () -> "Add assertion message");
		assertEquals("c4", ((ComponentTag)childMarkup.get(0)).getId(), () -> "Add assertion message");

		/*
		 * Ensure we cannot find inside component tag in subclass
		 */
		assertNull(markup.find("c5"), () -> "Add assertion message");
	}
}