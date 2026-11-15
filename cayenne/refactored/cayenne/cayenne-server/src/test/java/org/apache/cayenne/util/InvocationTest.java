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


package org.apache.cayenne.util;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InvocationTest {
	
    private String _methodName = "myListenerMethod";

    @Test
	public void testEqualsReflexive() throws NoSuchMethodException {
		Invocation inv0 = new Invocation(this, _methodName);		

		assertEquals(inv0, inv0, () -> "Add assertion message");
	}

    @Test
	public void testEqualsSymmetric() throws NoSuchMethodException {
		Invocation inv01 = new Invocation(this, _methodName);
		Invocation inv02 = new Invocation(this, _methodName);
		
		assertEquals(inv01, inv02, () -> "Add assertion message");
		assertEquals(inv02, inv01, () -> "Add assertion message");
	}

    @Test
	public void testEqualsTransitive() throws NoSuchMethodException {
		Invocation inv01 = new Invocation(this, _methodName);
		Invocation inv02 = new Invocation(this, _methodName);
		Invocation inv03 = new Invocation(this, _methodName);
		
		assertEquals(inv01, inv02, () -> "Add assertion message");
		assertEquals(inv02, inv03, () -> "Add assertion message");
		assertEquals(inv01, inv03, () -> "Add assertion message");
	}

    @Test
	public void testEqualsNull() throws NoSuchMethodException {
		Invocation inv0 = new Invocation(this, _methodName);

		assertTrue(inv0.equals(null) == false, () -> "Add assertion message");
	}

    @Test
	public void testEqualsDifferentMethods() throws NoSuchMethodException  {
		Invocation inv0 = new Invocation(this, _methodName);
		Invocation inv1 = new Invocation(this, _methodName, new Class[]{Object.class});

		assertTrue(inv0.equals(inv1) == false, () -> "Add assertion message");
	}

    @Test
	public void testEqualsNoVsOneArg() throws NoSuchMethodException {
		Invocation inv0 = new Invocation(this, _methodName);
		Invocation inv1 = new Invocation(this, _methodName, new Class[]{Object.class});

		assertTrue(inv0.equals(inv1) == false, () -> "Add assertion message");
	}

    @Test
	public void testAddToSet() throws NoSuchMethodException {
		HashSet set = new HashSet();
		
		Invocation inv0 = new Invocation(this, _methodName);

		set.add(inv0);
		set.add(inv0);

		Integer magicNumber47651262 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber47651262, set.size(), () -> "Add assertion message");
	}

    @Test
	public void testAddTwo() throws NoSuchMethodException {
		Set set = new HashSet();
		
		Invocation inv01 = new Invocation(this, _methodName);
		Invocation inv02 = new Invocation(this, _methodName);
		
		set.add(inv01);
		set.add(inv02);

		assertEquals(1, set.size(), () -> "Add assertion message");
	}

    @Test
	public void testEmptyParamTypes() throws NoSuchMethodException {
		try {
			new Invocation(this, _methodName, new Class[]{});
			fail();
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

    @Test
	public void testNullParamTypes0() throws NoSuchMethodException {
		try {
			new Invocation(this, _methodName, new Class[]{null});
			fail();
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

    @Test
	public void testNullParamTypes1() throws NoSuchMethodException {
		try {
			new Invocation(this, _methodName, new Class[]{String.class, null});
			fail();
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

    @Test
	public void testFireNoArgument() throws NoSuchMethodException {
		Invocation inv0 = new Invocation(this, _methodName);

		assertTrue(inv0.fire(), () -> "Add assertion message");
	}

    @Test
	public void testFireOneArgument() throws NoSuchMethodException {
		Invocation inv1 = new Invocation(this, _methodName, new Class[]{Object.class});

		assertTrue(inv1.fire("foo"), () -> "Add assertion message");
	}

    @Test
	public void testFireWrongArgumentCount0() throws Exception {
		Invocation inv0 = new Invocation(this, _methodName);

		try {
			inv0.fire("foo");
			fail();
		}

		catch (IllegalArgumentException ex) {
			// expected
		}
	}

    @Test
	public void testFireWrongArgumentCount1() throws Exception {
		Invocation inv1 = new Invocation(this, _methodName, new Class[]{Object.class});

		try {
			inv1.fire();
			fail();
		}

		catch (IllegalArgumentException ex) {
			// expected
		}
	}

    @Test
	public void testFireWrongArgumentCount2() throws Exception {
		Invocation inv1 = new Invocation(this, _methodName, new Class[]{Object.class});

		try {
			inv1.fire(new Object[]{"foo", "bar"});
			fail();
		}

		catch (IllegalArgumentException ex) {
			// expected
		}
	}

    @Test
	public void testFireNullArgArray() throws Exception {
		Invocation inv1 = new Invocation(this, _methodName, new Class[]{Object.class});

		try {
			inv1.fire(null);
			fail();
		}

		catch (IllegalArgumentException ex) {
			// expected
		}
	}

    @Test
	public void testGarbageCollection() throws NoSuchMethodException {
		// create an invocation with an listener that will be garbage collected
		Invocation inv0 = new Invocation(new String(), "toString");

		// (hopefully) make the listener go away
		System.gc();
		System.gc();

		assertFalse(inv0.fire(), () -> "Add assertion message");
	}

	
	// these methods exist for the test of Invocation equality
	public void myListenerMethod() {
	}

	public void myListenerMethod(Object o) {
	}

}