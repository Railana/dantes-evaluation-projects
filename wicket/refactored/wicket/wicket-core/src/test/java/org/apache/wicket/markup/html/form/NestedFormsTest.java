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
package org.apache.wicket.markup.html.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;

/**
 * @author Pedro Santos
 */
public class NestedFormsTest extends WicketTestCase
{
	/**
	 * 
	 */
	@Test
	public void tagName() throws Exception
	{
		tester.executeTest(NestedFormsTest.class, new TestPage(), "NestedFormsTest_expected.html");

	}

	/**
	 * WICKET-2166, WICKET-6760
	 */
	@Test
	public void tagNamePlaceholder() throws Exception
	{
		tester.executeTest(NestedFormsTest.class, new TestPage().hideInner(), "NestedFormsTest_placeholder_expected.html");
	}

	/**
	 * 
	 */
	@Test
	public void postOrderSequenceSubmittingRootForm()
	{
		tester.startPage(TestPage.class);
		TestPage testPage = (TestPage)tester.getLastRenderedPage();
		FormTester formTester = tester.newFormTester("outerForm");
		formTester.submit("outerSubmit");
		Integer magicNumber34706538 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber34706538, testPage.submitSequence.size(), () -> "Add assertion message");
		Integer magicNumber50158429 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber50158429, testPage.submitSequence.indexOf(testPage.outerSubmit), () -> "Add assertion message");
		assertEquals(1, testPage.submitSequence.indexOf(testPage.innerForm), () -> "Add assertion message");
		assertEquals(2, testPage.submitSequence.indexOf(testPage.outerForm), () -> "Add assertion message");
	}

	/**
	 *
	 */
	@Test
	public void postOrderSequenceSubmittingInnerForm()
	{
		tester.startPage(TestPage.class);
		TestPage testPage = (TestPage)tester.getLastRenderedPage();
		FormTester formTester = tester.newFormTester("outerForm");
		formTester.submit("innerForm:innerSubmit");
		Integer magicNumber90054031 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber90054031, testPage.submitSequence.size(), () -> "Add assertion message");
		assertEquals(0, testPage.submitSequence.indexOf(testPage.innerSubmit), () -> "Add assertion message");
		assertEquals(1, testPage.submitSequence.indexOf(testPage.innerForm), () -> "Add assertion message");
	}

	/** */
	public static class TestPage extends WebPage implements IMarkupResourceStreamProvider
	{
		/** */
		private static final long serialVersionUID = 1L;
		private List<Component> submitSequence = new ArrayList<Component>();
		Form<Void> outerForm;
		Button outerSubmit;
		Form<Void> innerForm;
		Button innerSubmit;

		/**
		 * Construct.
		 */
		public TestPage()
		{
			outerForm = new Form<Void>("outerForm")
			{
				/** */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit()
				{
					submitSequence.add(this);
				}
			};
			add(outerForm);
			outerSubmit = new Button("outerSubmit")
			{
				/** */
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					submitSequence.add(this);
				}
			};
			outerForm.add(outerSubmit);
			innerForm = new Form<Void>("innerForm")
			{
				/** */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit()
				{
					submitSequence.add(this);
				}
			};
			outerForm.add(innerForm);
			innerSubmit = new Button("innerSubmit")
			{
				/** */
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					submitSequence.add(this);
				}
			};
			innerForm.add(innerSubmit);
		}

		public TestPage hideInner()
		{
			innerForm.setOutputMarkupPlaceholderTag(true);
			innerForm.setVisible(false);
			
			return this;
		}

		@Override
		public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass)
		{
			return new StringResourceStream(
				"<html><body>"
					+ "<form wicket:id=\"outerForm\">"//
					+ "  <input type=\"submit\" wicket:id=\"outerSubmit\"/>"//
					+ "  <form wicket:id=\"innerForm\"><input type=\"submit\" wicket:id=\"innerSubmit\"/></form>"//
					+ "</form>" + //
					"</body></html>");
		}
	}
}