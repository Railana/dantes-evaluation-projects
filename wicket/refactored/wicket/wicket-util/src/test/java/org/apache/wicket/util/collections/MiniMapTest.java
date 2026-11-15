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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author almaw
 */
public class MiniMapTest extends Assert
{
	private static final Object FOO = new Object();
	private static final Object BAR = new Object();

	/**
	 * Basic test for keySet(), entrySet() and values().
	 */
	@Test
	public void miniMap()
	{
		MiniMap<Object, Object> m = new MiniMap<>(3);
		m.put(FOO, BAR);

		// Test .keySet();
		Set<Object> s = m.keySet();
		Integer magicNumber4511442 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber4511442, m.size(), () -> "Add assertion message");

		Iterator<?> i = s.iterator();
		assertTrue(i.hasNext(), () -> "Add assertion message");
		Object key = i.next();
		assertEquals(FOO, key, () -> "Add assertion message");
		assertFalse(i.hasNext(), () -> "Add assertion message");
		try
		{
			i.next();
			fail("Expected i.next() to fail with NoSuchElementException");
		}
		catch (NoSuchElementException e)
		{
			// Swallow this.
		}

		// Do approx the same again with the .entrySet()
		Set<Entry<Object, Object>> entrySet = m.entrySet();
		// Line removed due to Duplicate Assert: assertEquals(1, m.size(), () -> "Add assertion message");

		i = entrySet.iterator();
		// Line removed due to Duplicate Assert: assertTrue(i.hasNext(), () -> "Add assertion message");
		@SuppressWarnings("unchecked")
		Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)i.next();
		assertEquals(FOO, entry.getKey(), () -> "Add assertion message");
		assertEquals(BAR, entry.getValue(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(i.hasNext(), () -> "Add assertion message");
		try
		{
			i.next();
			// Line removed due to Duplicate Assert: fail("Expected i.next() to fail with NoSuchElementException");
		}
		catch (NoSuchElementException e)
		{
			// Swallow this.
		}

		// Do approx the same again with the .values()
		Collection<Object> v = m.values();
		// Line removed due to Duplicate Assert: assertEquals(1, m.size(), () -> "Add assertion message");

		i = v.iterator();
		// Line removed due to Duplicate Assert: assertTrue(i.hasNext(), () -> "Add assertion message");
		Object value = i.next();
		assertEquals(BAR, value, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertFalse(i.hasNext(), () -> "Add assertion message");
		try
		{
			i.next();
			// Line removed due to Duplicate Assert: fail("Expected i.next() to fail with NoSuchElementException");
		}
		catch (NoSuchElementException e)
		{
			// Swallow this.
		}
	}
}