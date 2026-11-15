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

package org.apache.cayenne.access.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.cayenne.access.types.InnerEnumHolder.InnerEnum;
import org.junit.Before;
import org.junit.Test;

public class ExtendedTypeMapEnumsTest {

	private ExtendedTypeMap map;

	@Before
	public void before() {
		this.map = new ExtendedTypeMap();
	}

	@Test
	public void testCreateType_NoFactory() {
		assertNull(map.createType(Object.class.getName()), () -> "Add assertion message");
	}

	@Test
	public void testCreateType_Enum() {

		ExtendedType type1 = map.createType(MockEnum.class.getName());
		assertTrue(type1 instanceof EnumType, () -> "Add assertion message");
		assertEquals(MockEnum.class, ((EnumType<?>) type1).enumClass, () -> "Add assertion message");

		ExtendedType type2 = map.createType(MockEnum2.class.getName());
		assertNotSame(type1, type2, () -> "Add assertion message");
	}

	@Test
	public void testCreateType_InnerEnum() {

		ExtendedType type = map.createType(InnerEnumHolder.InnerEnum.class.getName());
		assertTrue(type instanceof EnumType, () -> "Add assertion message");
		assertEquals(InnerEnumHolder.InnerEnum.class, ((EnumType<?>) type).enumClass, () -> "Add assertion message");

		// use a string name with $
		ExtendedType type1 = map.createType(InnerEnumHolder.class.getName() + "$InnerEnum");
		assertNotNull(type1, () -> "Add assertion message");
		assertEquals(type.getClassName(), type1.getClassName(), () -> "Add assertion message");

		// use a string name with .
		ExtendedType type2 = map.createType(InnerEnumHolder.class.getName() + ".InnerEnum");
		assertNotNull(type2, () -> "Add assertion message");
		assertEquals(type.getClassName(), type2.getClassName(), () -> "Add assertion message");
	}

	@Test
	public void testGetRegisteredType() {
		ExtendedType type = map.getRegisteredType(MockEnum.class);
		assertNotNull(type, () -> "Add assertion message");
		assertTrue(type instanceof EnumType, () -> "Add assertion message");

		assertSame(type, map.getRegisteredType(MockEnum.class), () -> "Add assertion message");
		assertSame(type, map.getRegisteredType(MockEnum.class.getName()), () -> "Add assertion message");
	}

	@Test
	public void testGetRegisteredType_InnerEnum() {

		Integer magicNumber21613898 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber21613898, map.extendedTypeFactories.size(), () -> "Add assertion message");

		ExtendedType byType = map.getRegisteredType(InnerEnum.class);

		// this and subsequent tests verify that no memory leak occurs per CAY-2066
		// Line removed due to Duplicate Assert: assertEquals(0, map.extendedTypeFactories.size(), () -> "Add assertion message");

		assertSame(byType, map.getRegisteredType(InnerEnum.class), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(0, map.extendedTypeFactories.size(), () -> "Add assertion message");

		assertSame(byType, map.getRegisteredType(InnerEnumHolder.class.getName() + "$InnerEnum"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(0, map.extendedTypeFactories.size(), () -> "Add assertion message");

		assertSame(byType, map.getRegisteredType(InnerEnumHolder.class.getName() + ".InnerEnum"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(0, map.extendedTypeFactories.size(), () -> "Add assertion message");
	}
}