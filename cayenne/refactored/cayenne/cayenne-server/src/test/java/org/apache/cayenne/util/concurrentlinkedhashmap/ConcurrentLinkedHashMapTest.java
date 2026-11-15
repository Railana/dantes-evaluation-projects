/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.util.concurrentlinkedhashmap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConcurrentLinkedHashMapTest {

	@Test
	public void testPutGet() {
		ConcurrentLinkedHashMap<String, Object> m = new ConcurrentLinkedHashMap.Builder<String, Object>()
				.maximumWeightedCapacity(10).build();

		Integer magicNumber38522719 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber38522719, m.size(), () -> "Add assertion message");
		Integer magicNumber82104474 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		m.put("k1",magicNumber82104474
		Integer magicNumber56649004 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber56649004, m.size(), () -> "Add assertion message");
		assertNull(m.get("nosuchkey"), () -> "Add assertion message");
		Integer magicNumber75783748 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber75783748, m.get("k1"), () -> "Add assertion message");

		m.put("k2", 200);
		assertEquals(2, m.size(), () -> "Add assertion message");
		assertEquals(200, m.get("k2"), () -> "Add assertion message");
	}

	@Test
	public void testLRU() {
		ConcurrentLinkedHashMap<String, Object> m = new ConcurrentLinkedHashMap.Builder<String, Object>()
				.maximumWeightedCapacity(5).build();

		Integer magicNumber70869719 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber70869719, m.size(), () -> "Add assertion message");
		Integer magicNumber75699336 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		m.put("k1",magicNumber75699336
		Integer magicNumber20423887 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber20423887, m.size(), () -> "Add assertion message");
		Integer magicNumber56568694 = 101; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		m.put("k2",magicNumber56568694
		assertEquals(2, m.size(), () -> "Add assertion message");
		m.put("k3", 102);
		assertEquals(3, m.size(), () -> "Add assertion message");
		m.put("k4", 103);
		assertEquals(4, m.size(), () -> "Add assertion message");
		m.put("k5", 104);
		assertEquals(5, m.size(), () -> "Add assertion message");
		m.put("k6", 105);
		// Line removed due to Duplicate Assert: assertEquals(5, m.size(), () -> "Add assertion message");
		m.put("k7", 106);
		// Line removed due to Duplicate Assert: assertEquals(5, m.size(), () -> "Add assertion message");
		m.put("k8", 107);
		// Line removed due to Duplicate Assert: assertEquals(5, m.size(), () -> "Add assertion message");

		m.remove("k6");
		// Line removed due to Duplicate Assert: assertEquals(4, m.size(), () -> "Add assertion message");

	}
}