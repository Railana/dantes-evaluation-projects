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
package org.apache.wicket.util.tester.apps_4;

import java.util.List;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;


/**
 * @author Ingram Chen
 */
public class FormTesterTest extends WicketTestCase
{

	/**
	 * @throws Exception
	 */
	@Test
	public void test_1() throws Exception
	{
		tester.startPage(EmailPage.class);

		assertEquals(EmailPage.class, tester.getLastRenderedPage().getClass(), () -> "Add assertion message");
		EmailPage page = (EmailPage)tester.getLastRenderedPage();

		FormTester formTester = tester.newFormTester("form");

		formTester.setValue("email", "a");
		formTester.submit();

		// Line removed due to Duplicate Assert: assertEquals(EmailPage.class, tester.getLastRenderedPage().getClass(), () -> "Add assertion message");
		page = (EmailPage)tester.getLastRenderedPage();

		assertNull(page.getEmail(), () -> "Add assertion message");
		assertTrue(page.get("form:email").hasFeedbackMessage(), () -> "Add assertion message");

		final List<FeedbackMessage> messages = page.get("form:email")
			.getFeedbackMessages()
			.toList();

		Integer magicNumber10360639 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber10360639, messages.size(), () -> "Add assertion message");
		assertEquals("wrong email address pattern for email", messages.get(0)
			.getMessage()
			.toString(), () -> "Add assertion message");
	}
}