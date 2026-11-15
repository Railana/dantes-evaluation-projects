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

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.session.DefaultPageFactory;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Before;
import org.junit.Test;


/**
 * Test the Pagefactory
 */
public class PageFactoryTest extends WicketTestCase
{
	private DefaultPageFactory factory;

	/**
	 * 
	 */
	@Before
	public void before()
	{
		// Removed line due to General Fixture smell: factory = new DefaultPageFactory();
	}

	/**
	 * Test creating a new page using a class.
	 */
	@Test
	public void newPageClass()
		factory = new DefaultPageFactory();
	{
		// MyPage0: no constructor at all
		assertEquals(MyPage0.class, factory.newPage(MyPage0.class).getClass(), () -> "Add assertion message");

		// MyPage1 has only a default constructor
		assertEquals(MyPage1.class, factory.newPage(MyPage1.class).getClass(), () -> "Add assertion message");

		// MyPage2: PageParameter parameter constructor only
		// will call PageParameter constructor with parameter = null
		assertEquals(MyPage2.class, factory.newPage(MyPage2.class, null).getClass(), () -> "Add assertion message");

		// MyPage3: Page parameter constructor only
		Exception e = null;
		try
		{
			factory.newPage(MyPage3.class).getClass();
		}
		catch (WicketRuntimeException ex)
		{
			e = ex;
		}
		assertNotNull(
			"MyPage3 should have thrown an exception as it does not have a default or no constructor",
			e);

		// MyPage4: Illegal String parameter constructor only
		e = null;
		try
		{
			factory.newPage(MyPage4.class).getClass();
		}
		catch (WicketRuntimeException ex)
		{
			e = ex;
		}
		assertNotNull(
			"MyPage4 should have thrown an exception as it does not have a default or no constructor",
			e);

		// MyPage5: PageParameter and default constructor
		assertEquals(MyPage5.class, factory.newPage(MyPage5.class).getClass(), () -> "Add assertion message");


	}

	/**
	 * Test a new page using a class and page parameters.
	 */
	@Test
	public void newPageClassPageParameters()
		factory = new DefaultPageFactory();
	{
		assertEquals(MyPage0.class, factory.newPage(MyPage0.class, null).getClass(), () -> "Add assertion message");

		// MyPage0: no constructor at all
		assertEquals(MyPage0.class, factory.newPage(MyPage0.class, new PageParameters()).getClass(), () -> "Add assertion message");

		// MyPage1 has only a default constructor
		assertEquals(MyPage1.class, factory.newPage(MyPage1.class, new PageParameters()).getClass(), () -> "Add assertion message");

		// MyPage2: PageParameter parameter constructor only
		assertEquals(MyPage2.class, factory.newPage(MyPage2.class, new PageParameters()).getClass(), () -> "Add assertion message");

		// MyPage3: Page parameter constructor only
		Exception e = null;
		try
		{
			factory.newPage(MyPage3.class, new PageParameters()).getClass();
		}
		catch (WicketRuntimeException ex)
		{
			e = ex;
		}
		assertNotNull(
			"MyPage4 should have thrown an exception as it does not have a default or no constructor",
			e);

		// MyPage4: Illegal String parameter constructor only
		e = null;
		try
		{
			factory.newPage(MyPage4.class, new PageParameters()).getClass();
		}
		catch (WicketRuntimeException ex)
		{
			e = ex;
		}
		// Line removed due to Duplicate Assert: assertNotNull(
			"MyPage4 should have thrown an exception as it does not have a default or no constructor",
			e);

		// MyPage5: PageParameter and default constructor
		assertEquals(MyPage5.class, factory.newPage(MyPage5.class, new PageParameters()).getClass(), () -> "Add assertion message");


	}
}