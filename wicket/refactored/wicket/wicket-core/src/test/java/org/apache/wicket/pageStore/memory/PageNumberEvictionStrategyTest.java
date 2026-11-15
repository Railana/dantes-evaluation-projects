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
package org.apache.wicket.pageStore.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/***/
public class PageNumberEvictionStrategyTest
{

	final byte[] PAGE1 = new byte[] { 1 };
	final byte[] PAGE2 = new byte[] { 2, 3 };

	/***/
	@Test
	public void evict()
	{
		// evict to page table with one page only
		PageNumberEvictionStrategy strategy = new PageNumberEvictionStrategy(1);

		PageTable pageTable = new PageTable();

		pageTable.storePage(PAGE1.length, PAGE1);
		Integer magicNumber75884823 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber75884823, pageTable.size(), () -> "Add assertion message");
		strategy.evict(pageTable);
		// Line removed due to Duplicate Assert: assertEquals(1, pageTable.size(), () -> "Add assertion message");
		assertNotNull(pageTable.getPage(PAGE1.length), () -> "Add assertion message");

		pageTable.storePage(PAGE2.length, PAGE2);
		assertEquals(2, pageTable.size(), () -> "Add assertion message");
		strategy.evict(pageTable);
		// Line removed due to Duplicate Assert: assertEquals(1, pageTable.size(), () -> "Add assertion message");
		assertNotNull(pageTable.getPage(PAGE2.length), () -> "Add assertion message");
		assertNull(pageTable.getPage(PAGE1.length), () -> "Add assertion message");
	}

	/**
	 * The number of pages must be at least '1'
	 */
	@Test(expected = IllegalArgumentException.class)
	public void greaterThanZero()
	{
		new PageNumberEvictionStrategy(0);
	}
}