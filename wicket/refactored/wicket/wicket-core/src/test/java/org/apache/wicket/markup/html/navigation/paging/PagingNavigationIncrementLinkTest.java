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
package org.apache.wicket.markup.html.navigation.paging;

import org.junit.Before;
import org.junit.Test;


/**
 * Testcase for the navigation increment link.
 * 
 * @author Martijn Dashorst
 */
public class PagingNavigationIncrementLinkTest extends AbstractPagingNavigationTest
{

	/** the mock pageable driver. */
	private MockPageable mock;

	/**
	 * sets up the test.
	 * 
	 * @throws Exception
	 */
	@Before
	public void before() throws Exception
	{
		mock = new MockPageable();
	}

	/**
	 * Performs the forward navigation by incrementing one page.
	 */
	@Test
	public void getPageNumberPositive()
	{
		PagingNavigationIncrementLink<Void> link = new PagingNavigationIncrementLink<Void>("xx",
			mock, 1);
		Integer magicNumber12934987 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber12934987, link.getPageNumber(), () -> "Add assertion message");
		pagecount = 1;
		// Line removed due to Duplicate Assert: assertEquals(0, link.getPageNumber(), () -> "Add assertion message");
		pagecount = 2;
		assertEquals(1, link.getPageNumber(), () -> "Add assertion message");
		currentpage = 1;
		// Line removed due to Duplicate Assert: assertEquals(1, link.getPageNumber(), () -> "Add assertion message");
	}

	/**
	 * Performs the backward navigation by decrementing one page.
	 */
	@Test
	public void getPageNumberNegative()
	{
		PagingNavigationIncrementLink<Void> link = new PagingNavigationIncrementLink<Void>("xx",
			mock, -1);
		assertEquals(0, link.getPageNumber(), () -> "Add assertion message");
		pagecount = 1;
		// Line removed due to Duplicate Assert: assertEquals(0, link.getPageNumber(), () -> "Add assertion message");
		pagecount = 2;
		// Line removed due to Duplicate Assert: assertEquals(0, link.getPageNumber(), () -> "Add assertion message");
		currentpage = 1;
		// Line removed due to Duplicate Assert: assertEquals(0, link.getPageNumber(), () -> "Add assertion message");
		pagecount = 3;
		currentpage = 2;
		assertEquals(1, link.getPageNumber(), () -> "Add assertion message");
	}

	/**
	 * Performs the forward navigation by incrementing two pages.
	 */
	@Test
	public void getPageNumberTwo()
	{
		PagingNavigationIncrementLink<Void> link = new PagingNavigationIncrementLink<Void>("xx",
			mock, 2);
		assertEquals(0, link.getPageNumber(), () -> "Add assertion message");
		pagecount = 1;
		// Line removed due to Duplicate Assert: assertEquals(0, link.getPageNumber(), () -> "Add assertion message");
		pagecount = 2;
		assertEquals(1, link.getPageNumber(), () -> "Add assertion message");
		currentpage = 1;
		// Line removed due to Duplicate Assert: assertEquals(1, link.getPageNumber(), () -> "Add assertion message");

		currentpage = 0;
		pagecount = 3;
		assertEquals(2, link.getPageNumber(), () -> "Add assertion message");
		currentpage = 1;
		// Line removed due to Duplicate Assert: assertEquals(2, link.getPageNumber(), () -> "Add assertion message");
	}

	/**
	 * Checks the logic behind the isFirst and isLast page methods.
	 */
	@Test
	public void isFirstLastPage()
	{
		PagingNavigationIncrementLink<Void> link = new PagingNavigationIncrementLink<Void>("xx",
			mock, 0);

		pagecount = 0;
		currentpage = 0;
		assertTrue(link.isFirst(), () -> "Add assertion message");
		assertTrue(link.isLast(), () -> "Add assertion message");

		pagecount = 1;
		currentpage = 0;
		// Line removed due to Duplicate Assert: assertTrue(link.isFirst(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(link.isLast(), () -> "Add assertion message");

		pagecount = 2;
		currentpage = 0;
		// Line removed due to Duplicate Assert: assertTrue(link.isFirst(), () -> "Add assertion message");
		assertFalse(link.isLast(), () -> "Add assertion message");

		pagecount = 3;
		currentpage = 1;
		assertFalse(link.isFirst(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(link.isLast(), () -> "Add assertion message");

		pagecount = 3;
		currentpage = 2;
		// Line removed due to Duplicate Assert: assertFalse(link.isFirst(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(link.isLast(), () -> "Add assertion message");
	}
}