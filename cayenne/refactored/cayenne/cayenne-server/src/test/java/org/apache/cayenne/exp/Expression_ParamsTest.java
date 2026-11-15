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

package org.apache.cayenne.exp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.exp.parser.ASTList;
import org.junit.Test;

public class Expression_ParamsTest {

	@Test
	public void testParams_Positional1() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x");
		Expression ep = e.paramsArray("A", 5);
		assertNotSame(e, ep, () -> "Add assertion message");
		assertEquals("(a = \"A\") or (x = 5)", ep.toString(), () -> "Add assertion message");
	}

	@Test
	public void testParams_Positional_Repeating() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x and y = $x");
		Expression ep = e.paramsArray("A", 5);
		assertNotSame(e, ep, () -> "Add assertion message");
		assertEquals("(a = \"A\") or ((x = 5) and (y = 5))", ep.toString(), () -> "Add assertion message");
	}

	@Test
	public void testParams_Positional_InAsValues() throws Exception {
		Expression inExp = ExpressionFactory.exp("k1 in ($ap, $bp)");
		Expression transformed = inExp.paramsArray("a", "b");
		assertEquals("k1 in (\"a\", \"b\")", transformed.toString(), () -> "Add assertion message");
	}

	@Test(expected = ExpressionException.class)
	public void testParams_Positional_MoreParams() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x");
		e.paramsArray("A", 5, "B");
	}

	@Test(expected = ExpressionException.class)
	public void testParams_Positional_LessParams() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x");
		e.paramsArray("B");
	}

	@Test(expected = ExpressionException.class)
	public void testParams_Positional_NoParams() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x");
		e.paramsArray();
	}

	@Test
	public void testParams_NullHandling_CAY847() {
		Expression e = ExpressionFactory.exp("X = $x");

		e = e.params(Collections.singletonMap("x", null));
		assertEquals("X = null", e.toString(), () -> "Add assertion message");
	}

	@Test
	public void testParams_Map_Full() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x");
		@SuppressWarnings("serial")
		Expression ep = e.params(new HashMap<String, Object>() {
			{
				put("a", "A");
				put("x", "X");
			}
		});
		assertNotSame(e, ep, () -> "Add assertion message");
		assertEquals("(a = \"A\") or (x = \"X\")", ep.toString(), () -> "Add assertion message");
	}

	@Test
	public void testParams_Map_Partial_Prune() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x");
		@SuppressWarnings("serial")
		Expression ep = e.params(new HashMap<String, Object>() {
			{
				put("a", "A");
			}
		});
		assertNotSame(e, ep, () -> "Add assertion message");
		assertEquals("(a = \"A\")", ep.toString(), () -> "Add assertion message");
	}

	@SuppressWarnings("serial")
	@Test(expected = ExpressionException.class)
	public void testParams_Map_Partial_NoPrune() {
		Expression e = ExpressionFactory.exp("a = $a or x = $x");
		e.params(new HashMap<String, Object>() {
			{
				put("a", "A");
			}
		}, false);
	}

	/**
	 * Tests how parameter substitution algorithm works on an expression with no
	 * parameters.
	 */
	@Test
	public void testCopy1() throws Exception {
		Expression e1 = ExpressionFactory.matchExp("k1", "v1");
		e1 = e1.orExp(ExpressionFactory.matchExp("k2", "v2"));
		e1 = e1.orExp(ExpressionFactory.matchExp("k3", "v3"));

		Expression e2 = e1.params(new HashMap());

		TstTraversalHandler.compareExps(e1, e2);
	}

	/**
	 * Tests how parameter substitution algorithm works on an expression with no
	 * parameters.
	 */
	@Test
	public void testCopy2() throws Exception {
		Expression andExp = ExpressionFactory.matchExp("k1", "v1");
		andExp = andExp.andExp(ExpressionFactory.matchExp("k2", "v2"));
		andExp = andExp.andExp(ExpressionFactory.matchExp("k3", "v3"));

		List exprs = new ArrayList();
		exprs.add(andExp);
		exprs.add(ExpressionFactory.matchExp("k1", "v1"));

		Expression e1 = ExpressionFactory.joinExp(Expression.OR, exprs);
		Expression e2 = e1.params(new HashMap());

		TstTraversalHandler.compareExps(e1, e2);
	}

	/**
	 * Tests how parameter substitution algorithm works on an expression with no
	 * parameters.
	 */
	@Test
	public void testInParameter() throws Exception {
		Expression inExp = ExpressionFactory.exp("k1 in $test");
		Expression e1 = ExpressionFactory.exp("k1 in ('a', 'b')");

		Expression transformed = inExp.params(Collections.singletonMap("test",
				new Object[] { "a", "b" }));
		TstTraversalHandler.compareExps(e1, transformed);

		// just in case manually check params
		DataObject o1 = new CayenneDataObject();
		o1.writePropertyDirectly("k1", "a");
		assertTrue(transformed.match(o1), () -> "Add assertion message");

		DataObject o2 = new CayenneDataObject();
		o2.writePropertyDirectly("k1", "x");
		assertFalse(transformed.match(o2), () -> "Add assertion message");
	}

	@Test
	public void testInParameter_AsValues() throws Exception {
		Expression inExp = ExpressionFactory.exp("k1 in ($ap, $bp)");

		String e1String = "k1 in (\"a\", \"b\")";
		Expression e1 = ExpressionFactory.exp(e1String);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ap", "a");
		params.put("bp", "b");
		Expression transformed = inExp.params(params);
		TstTraversalHandler.compareExps(e1, transformed);

		assertEquals(e1String, transformed.toString(), () -> "Add assertion message");

		// just in case manually check params
		DataObject o1 = new CayenneDataObject();
		o1.writePropertyDirectly("k1", "a");
		assertTrue(transformed.match(o1), () -> "Add assertion message");

		DataObject o2 = new CayenneDataObject();
		o2.writePropertyDirectly("k1", "x");
		assertFalse(transformed.match(o2), () -> "Add assertion message");
	}

	@Test(expected = ExpressionException.class)
	public void testFailOnMissingParams() throws Exception {
		Expression e1 = ExpressionFactory.matchExp("k1",
				new ExpressionParameter("test"));
		e1 = e1.orExp(ExpressionFactory.matchExp("k2", "v2"));
		e1 = e1.orExp(ExpressionFactory.matchExp("k3", "v3"));

		e1.params(new HashMap(), false);
	}

	@Test
	public void testParams1() throws Exception {
		Expression e1 = ExpressionFactory.matchExp("k1",
				new ExpressionParameter("test"));

		Map map = new HashMap();
		map.put("test", "xyz");
		Expression e2 = e1.params(map, false);
		assertNotNull(e2, () -> "Add assertion message");
		Integer magicNumber1992621 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber1992621, e2.getOperandCount(), () -> "Add assertion message");
		assertEquals(Expression.EQUAL_TO, e2.getType(), () -> "Add assertion message");
		assertEquals("xyz", e2.getOperand(1), () -> "Add assertion message");
	}

	@Test
	public void testParams2() throws Exception {
		Expression e1 = ExpressionFactory.likeExp("k1",
				new ExpressionParameter("test"));

		Map map = new HashMap();
		map.put("test", "xyz");
		Expression e2 = e1.params(map, false);
		assertNotNull(e2, () -> "Add assertion message");
		assertEquals(2, e2.getOperandCount(), () -> "Add assertion message");
		assertEquals(Expression.LIKE, e2.getType(), () -> "Add assertion message");
		assertEquals("xyz", e2.getOperand(1), () -> "Add assertion message");
	}

	@Test
	public void testNoParams1() throws Exception {
		Expression e1 = ExpressionFactory.matchExp("k1",
				new ExpressionParameter("test"));

		Expression e2 = e1.params(Collections.<String, Object> emptyMap());

		// all expression nodes must be pruned
		assertNull(e2, () -> "Add assertion message");
	}

	@Test
	public void testNoParams2() throws Exception {
		List list = new ArrayList();
		list.add(ExpressionFactory.matchExp("k1", new ExpressionParameter(
				"test1")));
		list.add(ExpressionFactory.matchExp("k2", new ExpressionParameter(
				"test2")));
		list.add(ExpressionFactory.matchExp("k3", new ExpressionParameter(
				"test3")));
		list.add(ExpressionFactory.matchExp("k4", new ExpressionParameter(
				"test4")));
		Expression e1 = ExpressionFactory.or(list);

		Map params = new HashMap();
		params.put("test2", "abc");
		params.put("test3", "xyz");
		Expression e2 = e1.params(params);

		// some expression nodes must be pruned
		assertNotNull(e2, () -> "Add assertion message");

		assertEquals(2, e2.getOperandCount(), () -> "Add assertion message");

		Expression k2 = (Expression) e2.getOperand(0);
		assertEquals("abc", k2.getOperand(1), () -> "Add assertion message");

		Expression k3 = (Expression) e2.getOperand(1);
		assertEquals("xyz", k3.getOperand(1), () -> "Add assertion message");
	}

	@Test
	public void testNoParams3() throws Exception {
		List list = new ArrayList();
		list.add(ExpressionFactory.matchExp("k1", new ExpressionParameter(
				"test1")));
		list.add(ExpressionFactory.matchExp("k2", new ExpressionParameter(
				"test2")));
		list.add(ExpressionFactory.matchExp("k3", new ExpressionParameter(
				"test3")));
		list.add(ExpressionFactory.matchExp("k4", new ExpressionParameter(
				"test4")));
		Expression e1 = ExpressionFactory.joinExp(Expression.OR, list);

		Map params = new HashMap();
		params.put("test4", "123");
		Expression e2 = e1.params(params, true);

		// some expression nodes must be pruned
		assertNotNull(e2, () -> "Add assertion message");
		assertTrue("List expression: " + e2, !(e2 instanceof ASTList));

		Integer magicNumber9631743 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber9631743, e2.getOperandCount(), () -> "Add assertion message");
		assertEquals("123", e2.getOperand(1), () -> "Add assertion message");
		assertEquals("k4", ((Expression) e2.getOperand(0)).getOperand(0), () -> "Add assertion message");
	}

	@Test
	public void testNullOptionalParameter() throws Exception {
		Expression e = ExpressionFactory.exp("abc = 3 and x = $a");

		Expression e1 = e.params(Collections.EMPTY_MAP, true);

		// $a must be pruned
		assertEquals(ExpressionFactory.exp("abc = 3"), e1, () -> "Add assertion message");

		Map params = new HashMap();
		params.put("a", null);
		Expression e2 = e.params(params, true);

		// null must be preserved
		assertEquals(ExpressionFactory.exp("abc = 3 and x = null"), e2, () -> "Add assertion message");
	}

	@Test(expected = ExpressionException.class)
	public void testNullRequiredParameter1() throws Exception {
		Expression e1 = ExpressionFactory.exp("abc = 3 and x = $a");

		e1.params(Collections.EMPTY_MAP, false);
	}

	@Test
	public void testNullRequiredParameter2() throws Exception {
		Expression e1 = ExpressionFactory.exp("abc = 3 and x = $a");

		Map params = new HashMap();
		params.put("a", null);
		Expression e2 = e1.params(params, false);

		// null must be preserved
		assertEquals(ExpressionFactory.exp("abc = 3 and x = null"), e2, () -> "Add assertion message");
	}

	@Test
	public void testNulls() {
		Expression e1 = ExpressionFactory.exp("x = null");
		Expression e2 = e1.params(Collections.EMPTY_MAP, true);
		assertNotNull(e2, () -> "Add assertion message");
		TstTraversalHandler.compareExps(e1, e2);
	}

	@Test
	public void testCopy1_FromString() {
		Expression e1 = ExpressionFactory
				.exp("k1 = 'v1' or k2 = 'v2' or k3 = 'v3'");
		Expression e2 = e1.params(Collections.EMPTY_MAP);
		TstTraversalHandler.compareExps(e1, e2);
	}

	/**
	 * Tests how parameter substitution algorithm works on an expression with no
	 * parameters.
	 */
	@Test
	public void testCopy2_FromString() {
		Expression e1 = ExpressionFactory
				.exp("(k1 = 'v1' and k2 = 'v2' and k3 = 'v3') or (k1 = 'v1')");
		Expression e2 = e1.params(Collections.EMPTY_MAP);
		TstTraversalHandler.compareExps(e1, e2);
	}

	@Test
	public void testCopy3_FromString() {
		Expression e1 = ExpressionFactory.exp("(k1 / 2) = (k2 * 2)");
		Expression e2 = e1.params(Collections.EMPTY_MAP);
		TstTraversalHandler.compareExps(e1, e2);
	}

	/**
	 * Tests how parameter substitution algorithm works on an expression with no
	 * parameters.
	 */
	@Test(expected = ExpressionException.class)
	public void testFailOnMissingParams_FromString() {
		Expression e1 = ExpressionFactory
				.exp("k1 = $test or k2 = 'v2' or k3 = 'v3'");

		e1.params(Collections.EMPTY_MAP, false);
	}

	@Test
	public void testParams1_FromString() {
		Expression e1 = ExpressionFactory.exp("k1 = $test");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "xyz");
		Expression e2 = e1.params(map, false);
		assertNotNull(e2, () -> "Add assertion message");
		Integer magicNumber17783556 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber17783556, e2.getOperandCount(), () -> "Add assertion message");
		assertEquals(Expression.EQUAL_TO, e2.getType(), () -> "Add assertion message");
		assertEquals("xyz", e2.getOperand(1), () -> "Add assertion message");
	}

	@Test
	public void testParams2_FromString() {
		Expression e1 = ExpressionFactory.exp("k1 like $test");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "xyz");
		Expression e2 = e1.params(map, false);
		assertNotNull(e2, () -> "Add assertion message");
		assertEquals(2, e2.getOperandCount(), () -> "Add assertion message");
		assertEquals(Expression.LIKE, e2.getType(), () -> "Add assertion message");
		assertEquals("xyz", e2.getOperand(1), () -> "Add assertion message");
	}

	@Test
	public void testNoParams1_FromString() {
		Expression e1 = ExpressionFactory.exp("k1 = $test");
		Expression e2 = e1.params(Collections.EMPTY_MAP);

		// all expression nodes must be pruned
		assertNull(e2, () -> "Add assertion message");
	}

	@Test
	public void testNoParams2_FromString() {
		Expression e1 = ExpressionFactory
				.exp("k1 = $test1 or k2 = $test2 or k3 = $test3 or k4 = $test4");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("test2", "abc");
		params.put("test3", "xyz");
		Expression e2 = e1.params(params);

		// some expression nodes must be pruned
		assertNotNull(e2, () -> "Add assertion message");
		Integer magicNumber5394195 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber5394195, e2.getOperandCount(), () -> "Add assertion message");

		Expression k2 = (Expression) e2.getOperand(0);
		assertEquals("abc", k2.getOperand(1), () -> "Add assertion message");

		Expression k3 = (Expression) e2.getOperand(1);
		assertEquals("xyz", k3.getOperand(1), () -> "Add assertion message");
	}

	@Test
	public void testNoParams3_FromString() {
		Expression e1 = ExpressionFactory
				.exp("k1 = $test1 or k2 = $test2 or k3 = $test3 or k4 = $test4");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("test4", "123");
		Expression e2 = e1.params(params);

		// some expression nodes must be pruned
		assertNotNull(e2, () -> "Add assertion message");
		assertEquals(2, e2.getOperandCount(), () -> "Add assertion message");
		assertEquals("123", e2.getOperand(1), () -> "Add assertion message");
		assertEquals("k4", ((Expression) e2.getOperand(0)).getOperand(0), () -> "Add assertion message");
	}
}