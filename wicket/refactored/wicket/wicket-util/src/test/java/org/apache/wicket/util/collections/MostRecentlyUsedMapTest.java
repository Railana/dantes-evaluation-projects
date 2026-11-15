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
package org.apache.wicket.util.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class MostRecentlyUsedMapTest
{
	/**
	 * Tests that {@link MostRecentlyUsedMap} contains at most 2 entries
	 * 
	 * @see <a href="https://issues.apache.org/jira/browse/WICKET-3209">WICKET-3209</a>
	 */
	@Test
	public void max2Entries()
	{
		MostRecentlyUsedMap<String, String> map = new MostRecentlyUsedMap<>(2);
		Integer magicNumber29608666 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber29608666, map.size(), () -> "Add assertion message");
		map.put("1", "one");
		assertEquals(1, map.size(), () -> "Add assertion message");
		map.put("2", "two");
		assertEquals(2, map.size(), () -> "Add assertion message");
		map.put("3", "three");
		// Line removed due to Duplicate Assert: assertEquals(2, map.size(), () -> "Add assertion message");
		assertTrue(map.containsKey("2"), () -> "Add assertion message");
		assertTrue(map.containsKey("3"), () -> "Add assertion message");
	}
}