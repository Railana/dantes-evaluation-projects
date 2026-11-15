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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests component events
 * 
 * @author igor
 */
public class ComponentEventsTest
{

	private WicketTester tester;
	private TestPage page;
	private TestContainer c1;
	private TestContainer c12;
	private TestContainer c13;
	private TestContainer c134;
	private TestComponent c135;
	private TestComponent c6;
	private TestApplication application;
	private TestSession session;
	private TestRequestCycle cycle;
	private Testable[] all;

	private Object stop;

	/** */
	@Before
	public void setup()
	{
		// Removed line due to General Fixture smell: tester = new WicketTester(new TestApplication());

		// Removed line due to General Fixture smell: application = (TestApplication)tester.getApplication();

		// Removed line due to General Fixture smell: session = (TestSession)tester.getSession();
		// Removed line due to General Fixture smell: cycle = (TestRequestCycle)tester.getRequestCycle();

		// Removed line due to General Fixture smell: page = new TestPage();
		// Removed line due to General Fixture smell: c1 = new TestContainer("c1");
		// Removed line due to General Fixture smell: c12 = new TestContainer("c12");
		// Removed line due to General Fixture smell: c13 = new TestContainer("c13");
		// Removed line due to General Fixture smell: c134 = new TestContainer("c134");
		// Removed line due to General Fixture smell: c135 = new TestComponent("c135");
		// Removed line due to General Fixture smell: c6 = new TestComponent("c6");

		page.add(c1);
		c1.add(c12);
		c1.add(c13);
		c13.add(c134);
		c13.add(c135);
		page.add(c6);

		// Removed line due to General Fixture smell: // Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };

		// Removed line due to General Fixture smell: stop = null;
	}

	/** */
	@After
	public void destroy()
		tester = new WicketTester(new TestApplication());
	{
		tester.destroy();
	}

	/** */
	@Test
	public void testBreadth_Application()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		page.send(tester.getApplication(), Broadcast.BREADTH, new Payload());
		assertPath(application, session, cycle, page, c1, c12, c13, c134, c135, c6);
	}

	/** */
	@Test
	public void testBreadth_Session()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
	{
		page.send(tester.getSession(), Broadcast.BREADTH, new Payload());
		assertPath(session, cycle, page, c1, c12, c13, c134, c135, c6);
	}

	/** */
	@Test
	public void testBreadth_Cycle()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
	{
		page.send(tester.getRequestCycle(), Broadcast.BREADTH, new Payload());
		assertPath(cycle, page, c1, c12, c13, c134, c135, c6);
	}

	/** */
	@Test
	public void testBreadth_Page()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
	{
		page.send(page, Broadcast.BREADTH, new Payload());
		assertPath(page, c1, c12, c13, c134, c135, c6);
	}

	/** */
	@Test
	public void testBreadth_Container()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c1 = new TestContainer("c1");
	{
		page.send(c13, Broadcast.BREADTH, new Payload());
		assertPath(c13, c134, c135);
	}

	/** */
	@Test
	public void testBreadth_Component()
		page = new TestPage();
		c6 = new TestComponent("c6");
	{
		page.send(c6, Broadcast.BREADTH, new Payload());
		assertPath(c6);
	}


	/** */
	@Test
	public void testBreadth_Application_Stop()
		page = new TestPage();
		tester = new WicketTester(new TestApplication());
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = application;
		page.send(application, Broadcast.BREADTH, new Payload());
		assertPath(application);
	}

	/** */
	@Test
	public void testBreadth_Session_Stop()
		page = new TestPage();
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = session;
		page.send(application, Broadcast.BREADTH, new Payload());
		assertPath(application, session);
	}

	/** */
	@Test
	public void testBreadth_Cycle_Stop()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = cycle;
		page.send(application, Broadcast.BREADTH, new Payload());
		assertPath(application, session, cycle);
	}

	/** */
	@Test
	public void testBreadth_Page_Stop()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = page;
		page.send(application, Broadcast.BREADTH, new Payload());
		assertPath(application, session, cycle, page);
	}

	/** */
	@Test
	public void testBreadth_Component_Stop()
		page = new TestPage();
		c13 = new TestContainer("c13");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = c13;
		page.send(application, Broadcast.BREADTH, new Payload());
		assertPath(application, session, cycle, page, c1, c12, c13);
	}


	/** */
	@Test
	public void testDepth_Application()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		page.send(application, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page, cycle, session, application);
	}

	/** */
	@Test
	public void testDepth_Session()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
	{
		page.send(session, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page, cycle, session);
	}

	/** */
	@Test
	public void testDepth_Cycle()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
	{
		page.send(cycle, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page, cycle);
	}

	/** */
	@Test
	public void testDepth_Page()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
	{
		page.send(page, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page);
	}

	/** */
	@Test
	public void testDepth_Container()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
	{
		page.send(c1, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1);
	}

	/** */
	@Test
	public void testDepth_Component()
		page = new TestPage();
		c6 = new TestComponent("c6");
	{
		page.send(c6, Broadcast.DEPTH, new Payload());
		assertPath(c6);
	}


	/** */
	@Test
	public void testDepth_Session_Stop()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = session;
		page.send(application, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page, cycle, session);
	}

	/** */
	@Test
	public void testDepth_Cycle_Stop()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = cycle;
		page.send(application, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page, cycle);
	}

	/** */
	@Test
	public void testDepth_Page_Stop()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		tester = new WicketTester(new TestApplication());
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = page;
		page.send(application, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page);
	}

	/** */
	@Test
	public void testDepth_Component_Stop()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		tester = new WicketTester(new TestApplication());
		application = (TestApplication)tester.getApplication();
		stop = null;
	{
		stop = c1;
		page.send(application, Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1);
	}


	/**
	 * 
	 */
	@Test
	public void testBubble_Component()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		c6.send(c135, Broadcast.BUBBLE, new Payload());
		assertPath(c135, c13, c1, page, cycle, session, application);
	}

	/** */
	@Test
	public void testBubble_Page()
		page = new TestPage();
		c6 = new TestComponent("c6");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		c6.send(page, Broadcast.BUBBLE, new Payload());
		assertPath(page, cycle, session, application);
	}

	/** */
	@Test
	public void testBubble_Cycle()
		c6 = new TestComponent("c6");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		c6.send(cycle, Broadcast.BUBBLE, new Payload());
		assertPath(cycle, session, application);
	}

	/** */
	@Test
	public void testBubble_Session()
		c6 = new TestComponent("c6");
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		c6.send(session, Broadcast.BUBBLE, new Payload());
		assertPath(session, application);
	}


	/** */
	@Test
	public void testBubble_Application()
		c6 = new TestComponent("c6");
		tester = new WicketTester(new TestApplication());
		application = (TestApplication)tester.getApplication();
	{
		c6.send(application, Broadcast.BUBBLE, new Payload());
		assertPath(application);
	}

	/** */
	@Test
	public void testBubble_Component_Stop()
		c135 = new TestComponent("c135");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c1 = new TestContainer("c1");
		stop = null;
	{
		stop = c1;
		c6.send(c135, Broadcast.BUBBLE, new Payload());
		assertPath(c135, c13, c1);
	}

	/** */
	@Test
	public void testBubble_Component_Page()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c1 = new TestContainer("c1");
		stop = null;
	{
		stop = page;
		c6.send(c135, Broadcast.BUBBLE, new Payload());
		assertPath(c135, c13, c1, page);
	}

	/** */
	@Test
	public void testBubble_Cycle_Stop()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		stop = null;
	{
		stop = cycle;
		c6.send(c135, Broadcast.BUBBLE, new Payload());
		assertPath(c135, c13, c1, page, cycle);
	}

	/** */
	@Test
	public void testBubble_Session_Stop()
		page = new TestPage();
		c135 = new TestComponent("c135");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		stop = null;
	{
		stop = session;
		c6.send(c135, Broadcast.BUBBLE, new Payload());
		assertPath(c135, c13, c1, page, cycle, session);
	}

	@Test
	public void testBehaviorBreadth()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		TestComponent c = new TestComponent("c");
		TestBehavior b1 = new TestBehavior();
		TestBehavior b2 = new TestBehavior();
		c.add(b1, b2);

		c.send(c, Broadcast.BREADTH, new Payload());
		Integer magicNumber95597694 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber95597694, c.sequence() -> "Add assertion message");
		Integer magicNumber79924508 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber79924508, b1.sequence() -> "Add assertion message");
		Integer magicNumber83894464 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber83894464, b2.sequence() -> "Add assertion message");
		Integer magicNumber82025839 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber82025839, application.sequence() -> "Add assertion message");
		assertEquals(-1, session.sequence, () -> "Add assertion message");
		assertEquals(-1, cycle.sequence, () -> "Add assertion message");
		assertEquals(-1, page.sequence, () -> "Add assertion message");
	}

	@Test
	public void testBehaviorExact()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		TestComponent c = new TestComponent("c");
		TestBehavior b1 = new TestBehavior();
		TestBehavior b2 = new TestBehavior();
		c.add(b1, b2);

		c.send(c, Broadcast.EXACT, new Payload());
		Integer magicNumber33982207 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber33982207, c.sequence() -> "Add assertion message");
		Integer magicNumber59911227 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber59911227, b1.sequence() -> "Add assertion message");
		assertEquals(2, b2.sequence, () -> "Add assertion message");
		assertEquals(-1, application.sequence, () -> "Add assertion message");
		assertEquals(-1, session.sequence, () -> "Add assertion message");
		assertEquals(-1, cycle.sequence, () -> "Add assertion message");
		assertEquals(-1, page.sequence, () -> "Add assertion message");
	}

	@Test
	public void testPageExact()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		TestComponent c = new TestComponent("c");
		TestBehavior b1 = new TestBehavior();
		c.add(b1);

		page.add(c);
		TestBehavior b2 = new TestBehavior();
		page.add(b2);

		c.send(page, Broadcast.EXACT, new Payload());
		Integer magicNumber13052513 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber13052513, c.sequence() -> "Add assertion message");
		assertEquals(-1, b1.sequence, () -> "Add assertion message");
		assertEquals(1, b2.sequence, () -> "Add assertion message");
		assertEquals(-1, application.sequence, () -> "Add assertion message");
		assertEquals(-1, session.sequence, () -> "Add assertion message");
		assertEquals(-1, cycle.sequence, () -> "Add assertion message");
		assertEquals(0, page.sequence, () -> "Add assertion message");
	}

	@Test
	public void testApplicationExact()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		TestComponent c = new TestComponent("c");
		TestBehavior b1 = new TestBehavior();
		TestBehavior b2 = new TestBehavior();
		c.add(b1, b2);

		c.send(c.getApplication(), Broadcast.EXACT, new Payload());
		assertEquals(-1, c.sequence, () -> "Add assertion message");
		assertEquals(-1, b1.sequence, () -> "Add assertion message");
		assertEquals(-1, b2.sequence, () -> "Add assertion message");
		assertEquals(0, application.sequence, () -> "Add assertion message");
		assertEquals(-1, session.sequence, () -> "Add assertion message");
		assertEquals(-1, cycle.sequence, () -> "Add assertion message");
		assertEquals(-1, page.sequence, () -> "Add assertion message");
	}

	@Test
	public void testSessionExact()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		TestComponent c = new TestComponent("c");
		TestBehavior b1 = new TestBehavior();
		TestBehavior b2 = new TestBehavior();
		c.add(b1, b2);

		c.send(c.getSession(), Broadcast.EXACT, new Payload());
		assertEquals(-1, c.sequence, () -> "Add assertion message");
		assertEquals(-1, b1.sequence, () -> "Add assertion message");
		assertEquals(-1, b2.sequence, () -> "Add assertion message");
		assertEquals(-1, application.sequence, () -> "Add assertion message");
		assertEquals(0, session.sequence, () -> "Add assertion message");
		assertEquals(-1, cycle.sequence, () -> "Add assertion message");
		assertEquals(-1, page.sequence, () -> "Add assertion message");
	}

	@Test
	public void testRequestCycleExact()
		page = new TestPage();
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
	{
		TestComponent c = new TestComponent("c");
		TestBehavior b1 = new TestBehavior();
		TestBehavior b2 = new TestBehavior();
		c.add(b1, b2);

		c.send(c.getRequestCycle(), Broadcast.EXACT, new Payload());
		assertEquals(-1, c.sequence, () -> "Add assertion message");
		assertEquals(-1, b1.sequence, () -> "Add assertion message");
		assertEquals(-1, b2.sequence, () -> "Add assertion message");
		assertEquals(-1, application.sequence, () -> "Add assertion message");
		assertEquals(-1, session.sequence, () -> "Add assertion message");
		assertEquals(0, cycle.sequence, () -> "Add assertion message");
		assertEquals(-1, page.sequence, () -> "Add assertion message");
	}

	@Test
	public void testBehavior_stop()
		stop = null;
	{
		TestComponent c = new TestComponent("c");
		TestBehavior b1 = new TestBehavior()
		{
			@Override
			public void onEvent(Component component, IEvent<?> event)
		stop = null;
			{
				super.onEvent(component, event);
				event.stop();
			}
		};
		TestBehavior b2 = new TestBehavior();
		c.add(b1, b2);

		c.send(c, Broadcast.BREADTH, new Payload());
		assertEquals(0, c.sequence, () -> "Add assertion message");
		assertEquals(1, b1.sequence, () -> "Add assertion message");
		assertEquals(-1, b2.sequence, () -> "Add assertion message");
	}


	private void assertPath(Testable... testables)
		page = new TestPage();
		c135 = new TestComponent("c135");
		c134 = new TestContainer("c134");
		c13 = new TestContainer("c13");
		c6 = new TestComponent("c6");
		c12 = new TestContainer("c12");
		c1 = new TestContainer("c1");
		cycle = (TestRequestCycle)tester.getRequestCycle();
		// Removed line due to General Fixture smell: all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
		tester = new WicketTester(new TestApplication());
		session = (TestSession)tester.getSession();
		application = (TestApplication)tester.getApplication();
		all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
	{
		List<Testable> remaining = new ArrayList<Testable>(Arrays.asList(all));

		for (int i = 0; i < testables.length; i++)
		{
			Assert.assertEquals("checking path element " + i, i, testables[i].getSequence());
			remaining.remove(testables[i]);
		}

		for (Testable testable : remaining)
		{
			String name = testable.getClass().getSimpleName();
			if (testable instanceof Component && !(testable instanceof Page))
			{
				name += "#" + ((Component)testable).getId();
			}
			Assert.assertEquals(name + " should not have been visited, but was.", -1,
				testable.getSequence());
		}
	}

	private static interface Testable
	{
		int getSequence();
	}


	private class TestApplication extends MockApplication implements Testable
	{
		int sequence = -1;

		@Override
		public void onEvent(IEvent<?> event)
		stop = null;
		{
			super.onEvent(event);
			Payload payload = (Payload)event.getPayload();
			sequence = payload.next();
			if (stop == this)
			{
				event.stop();
			}
		}

		@Override
		public Session newSession(Request request, Response response)
		{
			return new TestSession(request);
		}

		@Override
		protected void init()
		{
			super.init();
			setRequestCycleProvider(TestRequestCycle::new);
		}

		@Override
		public int getSequence()
		{
			return sequence;
		}
	}

	private class TestSession extends WebSession implements Testable
	{
		private static final long serialVersionUID = 1L;

		int sequence = -1;

		public TestSession(Request request)
		{
			super(request);
		}


		@Override
		public int getSequence()
		{
			return sequence;
		}

		@Override
		public void onEvent(IEvent<?> event)
		stop = null;
		{
			super.onEvent(event);
			Payload payload = (Payload)event.getPayload();
			sequence = payload.next();
			if (stop == this)
			{
				event.stop();
			}
		}
	}

	private class TestRequestCycle extends RequestCycle implements Testable
	{
		int sequence = -1;

		public TestRequestCycle(RequestCycleContext context)
		{
			super(context);
		}


		@Override
		public int getSequence()
		{
			return sequence;
		}

		@Override
		public void onEvent(IEvent<?> event)
		stop = null;
		{
			super.onEvent(event);
			Payload payload = (Payload)event.getPayload();
			sequence = payload.next();
			if (stop == this)
			{
				event.stop();
			}
		}
	}


	private class TestPage extends WebPage implements Testable
	{
		private static final long serialVersionUID = 1L;

		int sequence = -1;

		public TestPage()
		{
		}


		@Override
		public int getSequence()
		{
			return sequence;
		}

		@Override
		public void onEvent(IEvent<?> event)
		stop = null;
		{
			super.onEvent(event);
			Payload payload = (Payload)event.getPayload();
			sequence = payload.next();
			// System.out.println(getId());
			if (stop == this)
			{
				event.stop();
			}
		}

	}


	private class TestContainer extends WebMarkupContainer implements Testable
	{
		private static final long serialVersionUID = 1L;

		int sequence = -1;

		public TestContainer(String id)
		{
			super(id);
		}

		@Override
		public void onEvent(IEvent<?> event)
		stop = null;
		{
			super.onEvent(event);
			Payload payload = (Payload)event.getPayload();
			sequence = payload.next();
			// System.out.println(getId());
			if (stop == this)
			{
				event.stop();
			}
		}


		@Override
		public int getSequence()
		{
			return sequence;
		}
	}

	private class TestComponent extends WebComponent implements Testable
	{
		private static final long serialVersionUID = 1L;

		int sequence = -1;

		public TestComponent(String id)
		{
			super(id);
		}

		@Override
		public void onEvent(IEvent<?> event)
		stop = null;
		{
			super.onEvent(event);
			Payload payload = (Payload)event.getPayload();
			sequence = payload.next();
			// System.out.println(getId());
			if (stop == this)
			{
				event.stop();
			}
		}


		@Override
		public int getSequence()
		{
			return sequence;
		}
	}


	private class TestBehavior extends Behavior implements Testable
	{
		private static final long serialVersionUID = 1L;

		int sequence = -1;
		Component component;

		@Override
		public void onEvent(Component component, IEvent<?> event)
		stop = null;
		{
			super.onEvent(component, event);
			Payload payload = (Payload)event.getPayload();
			sequence = payload.next();
			this.component = component;
			// System.out.println(getId());
			if (stop == this)
			{
				event.stop();
			}
		}


		@Override
		public int getSequence()
		{
			return sequence;
		}
	}


	private static class Payload
	{
		private int counter;

		public int next()
		{
			return counter++;
		}
	}

}