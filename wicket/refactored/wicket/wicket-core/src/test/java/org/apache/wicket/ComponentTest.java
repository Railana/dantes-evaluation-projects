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

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;

/**
 * Test for ajax handler.
 * 
 * @author Juergen Donnerstag
 */
public class ComponentTest extends WicketTestCase
{

	/**
	 * Tests the number of detach calls on a Page, Component, Behavior and Model during a normal
	 * request.
	 * 
	 * @throws Exception
	 */
	@Test
	public void detachPage() throws Exception
	{
		executeTest(TestDetachPage.class, "TestDetachPageExpectedResult.html");
		TestDetachPage page = (TestDetachPage)tester.getLastRenderedPage();
		assertTrue(page.getNrComponentDetachModelCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrComponentDetachModelsCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrComponentOnDetachCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrPageDetachModelCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrPageDetachModelsCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrPageOnDetachCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrModelDetachCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrAjaxBehaviorDetachModelCalls() > 0, () -> "Add assertion message");
	}

	/**
	 * Tests the number of detach calls on a Page, Component, Behavior and Model during an Ajax
	 * request.
	 * 
	 * @throws Exception
	 */
	@Test
	public void detachPageAjaxRequest() throws Exception
	{
		executeTest(TestDetachPage.class, "TestDetachPageExpectedResult.html");
		TestDetachPage page = (TestDetachPage)tester.getLastRenderedPage();

		assertTrue(page.getNrComponentDetachModelCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrComponentDetachModelsCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrComponentOnDetachCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrPageDetachModelCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrPageDetachModelsCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrPageOnDetachCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrModelDetachCalls() > 0, () -> "Add assertion message");
		assertTrue(page.getNrAjaxBehaviorDetachModelCalls() > 0, () -> "Add assertion message");

		AjaxEventBehavior behavior = page.getAjaxBehavior();
		executeBehavior(behavior, "TestDetachPageAjaxResult.html");
		assertTrue(1 <= page.getNrComponentDetachModelCalls(), () -> "Add assertion message");
		assertTrue(1 <= page.getNrComponentDetachModelsCalls(), () -> "Add assertion message");
		assertTrue(1 <= page.getNrComponentOnDetachCalls(), () -> "Add assertion message");
		assertTrue(1 <= page.getNrPageDetachModelCalls(), () -> "Add assertion message");
		assertTrue(1 <= page.getNrPageDetachModelsCalls(), () -> "Add assertion message");
		assertTrue(1 <= page.getNrPageOnDetachCalls(), () -> "Add assertion message");
		assertTrue(1 <= page.getNrModelDetachCalls(), () -> "Add assertion message");
		assertTrue(1 <= page.getNrAjaxBehaviorDetachModelCalls(), () -> "Add assertion message");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void renderHomePage_1() throws Exception
	{
		executeTest(TestPage_1.class, "TestPageExpectedResult_1.html");
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-4468
	 *
	 * Verifies that a stateful component can pretend to be stateless if both conditions are
	 * fulfilled:
	 * <ol>
	 *     <li>it is either invisible or disabled (or both)</li>
	 *     <li>it cannot call any listener interface method while invisible/disabled</li>
	 * </ol>
	 */
	@Test
	public void isStateless()
	{
		Behavior statefulBehavior = new Behavior()
		{
			@Override
			public boolean getStatelessHint(Component component)
			{
				return false;
			}
		};

		WebComponent component = new WebComponent("someId");

		// by default every component is stateless
		assertTrue(component.isStateless(), () -> "Add assertion message");

		// make the component stateful
		component.add(statefulBehavior);
		assertFalse(component.isStateless(), () -> "Add assertion message");

		// invisible component cannot be requested by default so it
		// can pretend being stateless
		component.setVisible(false);
		// Line removed due to Duplicate Assert: assertTrue(component.isStateless(), () -> "Add assertion message");

		// same for disabled component
		component.setVisible(true).setEnabled(false);
		// Line removed due to Duplicate Assert: assertTrue(component.isStateless(), () -> "Add assertion message");

		// make the component such that it can call listener interface
		// methods no matter whether it is visible or enabled
		component = new WebComponent("someId") {
			@Override
			public boolean canCallListener()
			{
				return true;
			}
		};
		component.add(statefulBehavior);
		component.setVisible(false);
		// Line removed due to Duplicate Assert: assertFalse(component.isStateless(), () -> "Add assertion message");


		// do the same set of tests on a component that is stateful "by itself"
		// rather than from a behavior
		Link<Void> link = new Link<Void>("someId") {
			@Override
			public void onClick()
			{
			}
		};
        
		// Links are stateful by default
		assertFalse(link.isStateless(), () -> "Add assertion message");
        
		//make the link invisible
		link.setVisible(false);

		// invisible link cannot be requested by default so it
		// can pretend being stateless
		assertTrue(link.isStateless(), () -> "Add assertion message");

		// same for disabled link
		link.setVisible(true).setEnabled(false);
		// Line removed due to Duplicate Assert: assertTrue(link.isStateless(), () -> "Add assertion message");
        
		// make the link such that it can call listener interface
		// methods no matter whether it is visible or enabled
		link = new Link<Void>("someId") {

		    @Override
		    public boolean canCallListener() {
		    	return true;
		    }

		    @Override
		    public void onClick() {
		    }
		};

		link.setVisible(false);
		// Line removed due to Duplicate Assert: assertFalse(link.isStateless(), () -> "Add assertion message");
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-4483
	 *
	 * setDefaultModel() should call modelChanging/modelChanged only if the new model
	 * is different that the old one. The same as setDefaultModelObject().
	 */
	@Test
	public void modelChange()
	{
		final AtomicBoolean modelChanging = new AtomicBoolean(false);
		final AtomicBoolean modelChanged = new AtomicBoolean(false);

		WebComponent component = new WebComponent("someId")
		{
			@Override
			protected void onModelChanging()
			{
				super.onModelChanging();
				modelChanging.set(true);
			}

			@Override
			protected void onModelChanged()
			{
				super.onModelChanged();
				modelChanged.set(true);
			}
		};

		assertNull(component.getDefaultModel(), () -> "Add assertion message");
		IModel<Integer> model = Model.of(1);

		// set a model which is different that the old one (old = null, new = non-null)
		component.setDefaultModel(model);
		assertTrue(modelChanging.getAndSet(false), () -> "Add assertion message");
		assertTrue(modelChanged.getAndSet(false), () -> "Add assertion message");

		// set the same instance - no change notifications should happen
		component.setDefaultModel(model);
		assertFalse(modelChanging.get(), () -> "Add assertion message");
		assertFalse(modelChanged.get(), () -> "Add assertion message");
	}

	@Test
	public void pageIsInitiallyStateless()
	{
		FlagReserved5Component component = new FlagReserved5Component("test");
		assertTrue(component.getFlagReserved5(), () -> "Add assertion message");
	}

	@Test
	public void isEnabledInHierarchyCachesResult()
	{
		final SpyComponent c = new SpyComponent("test");

		c.isEnabledInHierarchy();
		c.isEnabledInHierarchy();
		Integer magicNumber9528225 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber9528225, c.isEnabledCallCount() -> "Add assertion message");

		c.setEnabled(false);

		c.isEnabledInHierarchy();
		c.isEnabledInHierarchy();
		assertEquals(2, c.isEnabledCallCount, () -> "Add assertion message");
	}

	@Test
	public void isVisibleInHierarchyCachesResult()
	{
		final SpyComponent c = new SpyComponent("test");
		
		c.isVisibleInHierarchy();
		c.isVisibleInHierarchy();
		assertEquals(1, c.isVisibleCallCount, () -> "Add assertion message");
		
		c.setVisible(false);
		
		c.isVisibleInHierarchy();
		c.isVisibleInHierarchy();
		assertEquals(2, c.isVisibleCallCount, () -> "Add assertion message");
		
		c.setVisibilityAllowed(false);
		
		c.isVisibleInHierarchy();
		c.isVisibleInHierarchy();
		assertEquals(3, c.isVisibleCallCount, () -> "Add assertion message");
	}

	/**
	 * Component#FLAG_RESERVED5 (Page's STATELESS_HINT) must be initially set to true
	 */
	private static class FlagReserved5Component extends Component
	{

		public FlagReserved5Component(final String id) {
			super(id);
		}

		private boolean getFlagReserved5()
		{
			return getFlag(FLAG_RESERVED5);
		}

		@Override
		protected void onRender() {
		}
	}

	private static class SpyComponent extends Component
	{

		int isEnabledCallCount;
		int isVisibleCallCount;

		public SpyComponent(final String id)
		{
			super(id);
		}

		@Override
		public boolean isEnabled()
		{
			isEnabledCallCount++;
			return super.isEnabled();
		}

		@Override
		public boolean isVisible()
		{
			isVisibleCallCount++;
			return super.isVisible();
		}

		@Override
		protected void onRender() {
		}
	}
}