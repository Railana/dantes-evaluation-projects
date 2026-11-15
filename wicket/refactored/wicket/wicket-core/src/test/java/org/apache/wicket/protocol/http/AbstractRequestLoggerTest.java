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
package org.apache.wicket.protocol.http;

import org.apache.wicket.protocol.http.IRequestLogger.RequestData;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;

/**
 * Test for {@link AbstractRequestLogger}.
 */
public class AbstractRequestLoggerTest extends WicketTestCase
{
	private int counter;
	
	@Test
	public void foo()
	{
		tester.getApplication().getRequestLoggerSettings().setRequestsWindowSize(4);
		
		AbstractRequestLogger logger = new AbstractRequestLogger()
		{
			@Override
			protected void log(RequestData rd, SessionData sd)
			{
			}
		};
		
		logger.addRequest(requestData());
		Integer magicNumber1600803 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber1600803, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("0", logger.getRequests().get(0).getRequestedUrl(), () -> "Add assertion message");
		assertEquals("0value", logger.getRequests().get(0).getUserData("someKey"), () -> "Add assertion message");
		
		logger.addRequest(requestData());
		assertEquals(2, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("1", logger.getRequests().get(1).getRequestedUrl(), () -> "Add assertion message");

		logger.addRequest(requestData());
		assertEquals(3, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("2", logger.getRequests().get(2).getRequestedUrl(), () -> "Add assertion message");

		logger.addRequest(requestData());
		assertEquals(4, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("3", logger.getRequests().get(3).getRequestedUrl(), () -> "Add assertion message");
		
		logger.addRequest(requestData());
		// Line removed due to Duplicate Assert: assertEquals(4, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("4", logger.getRequests().get(3).getRequestedUrl(), () -> "Add assertion message");
		
		logger.addRequest(requestData());
		// Line removed due to Duplicate Assert: assertEquals(4, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("5", logger.getRequests().get(3).getRequestedUrl(), () -> "Add assertion message");
		
		logger.addRequest(requestData());
		// Line removed due to Duplicate Assert: assertEquals(4, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("6", logger.getRequests().get(3).getRequestedUrl(), () -> "Add assertion message");
		
		logger.addRequest(requestData());
		// Line removed due to Duplicate Assert: assertEquals(4, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("7", logger.getRequests().get(3).getRequestedUrl(), () -> "Add assertion message");
		
		logger.addRequest(requestData());
		// Line removed due to Duplicate Assert: assertEquals(4, logger.getRequests().size(), () -> "Add assertion message");
		assertEquals("8", logger.getRequests().get(3).getRequestedUrl(), () -> "Add assertion message");
}
	
	private RequestData requestData() {
		RequestData data = new RequestData();

		data.addUserData("someKey", "" + counter + "value");
		data.setRequestedUrl("" + counter++);

		return data;
	}
}