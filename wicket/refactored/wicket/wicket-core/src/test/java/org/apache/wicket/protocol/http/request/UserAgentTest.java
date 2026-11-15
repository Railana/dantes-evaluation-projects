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
package org.apache.wicket.protocol.http.request;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for {@link UserAgent}
 */
public class UserAgentTest
{

	/**
	 * Tests for {@link UserAgent#matches(String)} based on the blacklisted entries
	 */
	@Test
	public void notAllowedList()
	{
		// check single blacklisted entry - Opera
		assertFalse(UserAgent.INTERNET_EXPLORER.matches("Something that contains Opera"), () -> "Add assertion message");

		// check comma separated list of entries - Konqueror,Opera
		assertFalse(UserAgent.MOZILLA.matches("Something that contains Konqueror"), () -> "Add assertion message");
		assertFalse(UserAgent.MOZILLA.matches("Something that contains Opera"), () -> "Add assertion message");

		// check blacklisted for EDGE
		assertFalse(UserAgent.EDGE.matches("Something that contains Opera"), () -> "Add assertion message");
		assertFalse(UserAgent.EDGE.matches("Something that contains Konqueror"), () -> "Add assertion message");
		assertFalse(UserAgent.EDGE.matches("Something that contains Trident"), () -> "Add assertion message");
	}

	/**
	 * Tests for {@link UserAgent#matches(String)} based on the detection groups
	 */
	@Test
	public void detectionGroups()
	{
		// IE always send the agent header, if don't:
		assertFalse(UserAgent.INTERNET_EXPLORER.matches(null), () -> "Add assertion message");

		// no blacklisted entries, but not full match in a detection group
		assertFalse(UserAgent.INTERNET_EXPLORER.matches("Mozilla MSIE"), () -> "Add assertion message");

		// full match in detection group 1
		assertTrue(UserAgent.INTERNET_EXPLORER.matches("Mozilla MSIE Trident"), () -> "Add assertion message");

		// full match in detection group 2
		assertTrue(UserAgent.INTERNET_EXPLORER.matches("Mozilla MSIE Mac_PowerPC"), () -> "Add assertion message");

		// check Edge
		final String userAgentEdge
				= "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063";
		assertTrue(UserAgent.EDGE.matches(userAgentEdge), () -> "Add assertion message");
		assertFalse(UserAgent.FIREFOX.matches(userAgentEdge), () -> "Add assertion message");
		assertFalse(UserAgent.INTERNET_EXPLORER.matches(userAgentEdge), () -> "Add assertion message");
		assertFalse(UserAgent.SAFARI.matches(userAgentEdge), () -> "Add assertion message");
		assertFalse(UserAgent.CHROME.matches(userAgentEdge), () -> "Add assertion message");
		assertFalse(UserAgent.OPERA.matches(userAgentEdge), () -> "Add assertion message");
		assertFalse(UserAgent.KONQUEROR.matches(userAgentEdge), () -> "Add assertion message");
	}
}