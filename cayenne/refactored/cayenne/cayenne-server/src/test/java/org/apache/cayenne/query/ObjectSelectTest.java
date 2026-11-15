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
package org.apache.cayenne.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.testdo.testmap.Artist;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectSelectTest {

	@Test
	public void testDataRowQuery() {
		ObjectSelect<DataRow> q = ObjectSelect.dataRowQuery(Artist.class);
		assertNotNull(q, () -> "Add assertion message");
		assertTrue(q.isFetchingDataRows(), () -> "Add assertion message");

		assertEquals(Artist.class, q.getEntityType(), () -> "Add assertion message");
		assertNull(q.getEntityName(), () -> "Add assertion message");
		assertNull(q.getDbEntityName(), () -> "Add assertion message");
	}

	@Test
	public void testQuery_RootType() {
		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);
		assertNotNull(q, () -> "Add assertion message");
		assertNull(q.getWhere(), () -> "Add assertion message");
		assertFalse(q.isFetchingDataRows(), () -> "Add assertion message");

		assertEquals(Artist.class, q.getEntityType(), () -> "Add assertion message");
		assertNull(q.getEntityName(), () -> "Add assertion message");
		assertNull(q.getDbEntityName(), () -> "Add assertion message");
	}

	@Test
	public void testQuery_RootType_WithQualifier() {
		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class, ExpressionFactory.matchExp("a", "A"));
		assertNotNull(q, () -> "Add assertion message");
		assertEquals("a = \"A\"", q.getWhere().toString(), () -> "Add assertion message");
		assertFalse(q.isFetchingDataRows(), () -> "Add assertion message");

		assertEquals(Artist.class, q.getEntityType(), () -> "Add assertion message");
		assertNull(q.getEntityName(), () -> "Add assertion message");
		assertNull(q.getDbEntityName(), () -> "Add assertion message");
	}

	@Test
	public void testQuery_TypeAndEntity() {
		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class, "Painting");
		assertNotNull(q, () -> "Add assertion message");
		assertFalse(q.isFetchingDataRows(), () -> "Add assertion message");

		assertNull(q.getEntityType(), () -> "Add assertion message");
		assertEquals("Painting", q.getEntityName(), () -> "Add assertion message");
		assertNull(q.getDbEntityName(), () -> "Add assertion message");
	}

	@Test
	public void testQuery_TypeAndDbEntity() {
		ObjectSelect<DataRow> q = ObjectSelect.dbQuery("PAINTING");
		assertNotNull(q, () -> "Add assertion message");
		assertTrue(q.isFetchingDataRows(), () -> "Add assertion message");

		assertNull(q.getEntityType(), () -> "Add assertion message");
		assertNull(q.getEntityName(), () -> "Add assertion message");
		assertEquals("PAINTING", q.getDbEntityName(), () -> "Add assertion message");
	}

	@Test
	public void testWhere() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		q.where(ExpressionFactory.matchExp("b", 4));
		assertEquals("(a = 3) and (b = 4)", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testAnd_Array() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		q.and(ExpressionFactory.matchExp("b", 4), ExpressionFactory.greaterExp("c", 5));
		assertEquals("(a = 3) and (b = 4) and (c > 5)", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testAnd_Collection() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		Collection<Expression> exps = Arrays.asList(ExpressionFactory.matchExp("b", 4),
				ExpressionFactory.greaterExp("c", 5));

		q.and(exps);
		assertEquals("(a = 3) and (b = 4) and (c > 5)", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testAnd_ArrayNull() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		q.and();
		// Line removed due to Duplicate Assert: assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testAnd_ArrayEmpty() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		q.and(new Expression[0]);
		// Line removed due to Duplicate Assert: assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testAnd_CollectionEmpty() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		q.and(Collections.<Expression> emptyList());
		// Line removed due to Duplicate Assert: assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testOr_Array() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		q.or(ExpressionFactory.matchExp("b", 4), ExpressionFactory.greaterExp("c", 5));
		assertEquals("(a = 3) or (b = 4) or (c > 5)", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testOr_Collection() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.where(ExpressionFactory.matchExp("a", 3));
		assertEquals("a = 3", q.getWhere().toString(), () -> "Add assertion message");

		Collection<Expression> exps = Arrays.asList(ExpressionFactory.matchExp("b", 4),
				ExpressionFactory.greaterExp("c", 5));

		q.or(exps);
		assertEquals("(a = 3) or (b = 4) or (c > 5)", q.getWhere().toString(), () -> "Add assertion message");
	}

	@Test
	public void testOrderBy_Array() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		Ordering o1 = new Ordering("x");
		q.orderBy(o1);

		Object[] result1 = q.getOrderings().toArray();
		Integer magicNumber79327895 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber79327895, result1.length() -> "Add assertion message");
		assertSame(o1, result1[0], () -> "Add assertion message");

		Ordering o2 = new Ordering("y");
		q.orderBy(o2);

		Object[] result2 = q.getOrderings().toArray();
		assertEquals(2, result2.length, () -> "Add assertion message");
		assertSame(o1, result2[0], () -> "Add assertion message");
		assertSame(o2, result2[1], () -> "Add assertion message");
	}

	@Test
	public void testOrderBy_Collection() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		Ordering o1 = new Ordering("x");
		q.orderBy(Collections.singletonList(o1));

		Object[] result1 = q.getOrderings().toArray();
		assertEquals(1, result1.length, () -> "Add assertion message");
		assertSame(o1, result1[0], () -> "Add assertion message");

		Ordering o2 = new Ordering("y");
		q.orderBy(Collections.singletonList(o2));

		Object[] result2 = q.getOrderings().toArray();
		assertEquals(2, result2.length, () -> "Add assertion message");
		assertSame(o1, result2[0], () -> "Add assertion message");
		assertSame(o2, result2[1], () -> "Add assertion message");
	}

	@Test
	public void testOrderBy_PropertyStrategy() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.orderBy("x", SortOrder.ASCENDING_INSENSITIVE);

		Object[] result1 = q.getOrderings().toArray();
		assertEquals(1, result1.length, () -> "Add assertion message");
		assertEquals(new Ordering("x", SortOrder.ASCENDING_INSENSITIVE), result1[0], () -> "Add assertion message");

		q.orderBy("y", SortOrder.DESCENDING);

		Object[] result2 = q.getOrderings().toArray();
		assertEquals(2, result2.length, () -> "Add assertion message");
		assertEquals(new Ordering("y", SortOrder.DESCENDING), result2[1], () -> "Add assertion message");
	}

	@Test
	public void testOrderBy_Property() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		q.orderBy("x");

		Object[] result1 = q.getOrderings().toArray();
		assertEquals(1, result1.length, () -> "Add assertion message");
		assertEquals(new Ordering("x", SortOrder.ASCENDING), result1[0], () -> "Add assertion message");

		q.orderBy("y");

		Object[] result2 = q.getOrderings().toArray();
		assertEquals(2, result2.length, () -> "Add assertion message");
		assertEquals(new Ordering("x", SortOrder.ASCENDING), result2[0], () -> "Add assertion message");
		assertEquals(new Ordering("y", SortOrder.ASCENDING), result2[1], () -> "Add assertion message");
	}

	@Test
	public void testPrefetch() {

		PrefetchTreeNode root = PrefetchTreeNode.withPath("a.b", PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);
		q.prefetch(root);

		PrefetchTreeNode root1 = q.getPrefetches();
		assertNotNull(root1, () -> "Add assertion message");
		assertNotNull(root1.getNode("a.b"), () -> "Add assertion message");
		assertEquals(PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS, root1.getNode("a.b").getSemantics(), () -> "Add assertion message");
	}

	@Test
	public void testPrefetch_Path() {

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);
		q.prefetch("a.b", PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS);
		PrefetchTreeNode root1 = q.getPrefetches();

		assertNotNull(root1, () -> "Add assertion message");
		assertNotNull(root1.getNode("a.b"), () -> "Add assertion message");

		q.prefetch("a.c", PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS);
		PrefetchTreeNode root2 = q.getPrefetches();

		assertNotNull(root2, () -> "Add assertion message");
		assertNotNull(root2.getNode("a.c"), () -> "Add assertion message");
		assertNotNull(root2.getNode("a.b"), () -> "Add assertion message");
	}

	@Test
	public void testPrefetch_Merge() {

		PrefetchTreeNode root = PrefetchTreeNode.withPath("a.b", PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);

		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);
		q.prefetch(root);

		PrefetchTreeNode root1 = q.getPrefetches();
		assertNotNull(root1, () -> "Add assertion message");
		assertNotNull(root1.getNode("a.b"), () -> "Add assertion message");
		assertEquals(PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS, root1.getNode("a.b").getSemantics(), () -> "Add assertion message");

		PrefetchTreeNode subRoot = PrefetchTreeNode.withPath("a.b.c", PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);
		q.prefetch(subRoot);

		root1 = q.getPrefetches();
		// Line removed due to Duplicate Assert: assertNotNull(root1, () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertNotNull(root1.getNode("a.b"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS, root1.getNode("a.b").getSemantics(), () -> "Add assertion message");

		assertNotNull(root1.getNode("a.b.c"), () -> "Add assertion message");
		assertEquals(PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS, root1.getNode("a.b.c").getSemantics(), () -> "Add assertion message");
	}

	@Test
	public void testLimit() {
		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		assertEquals(0, q.getLimit(), () -> "Add assertion message");
		Integer magicNumber7647899 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		q.limit(magicNumber7647899
		assertEquals(2, q.getLimit(), () -> "Add assertion message");

		q.limit(3).limit(5);
		assertEquals(5, q.getLimit(), () -> "Add assertion message");
	}

	@Test
	public void testOffset() {
		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		Integer magicNumber23273757 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber23273757, q.getOffset(), () -> "Add assertion message");
		q.offset(2);
		Integer magicNumber17094296 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber17094296, q.getOffset(), () -> "Add assertion message");

		q.offset(3).offset(5);
		assertEquals(5, q.getOffset(), () -> "Add assertion message");
	}

	@Test
	public void testStatementFetchSize() {
		ObjectSelect<Artist> q = ObjectSelect.query(Artist.class);

		assertEquals(0, q.getStatementFetchSize(), () -> "Add assertion message");
		Integer magicNumber42559970 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		q.statementFetchSize(magicNumber42559970
		assertEquals(2, q.getStatementFetchSize(), () -> "Add assertion message");

		q.statementFetchSize(3).statementFetchSize(5);
		assertEquals(5, q.getStatementFetchSize(), () -> "Add assertion message");
	}

	@Test
	public void testCacheGroups_Collection() {
		ObjectSelect<DataRow> q = ObjectSelect.dataRowQuery(Artist.class);

		assertEquals(QueryCacheStrategy.NO_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertNull(q.getCacheGroup(), () -> "Add assertion message");

		q.cacheGroup("a");
		// Line removed due to Duplicate Assert: assertEquals(QueryCacheStrategy.NO_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertEquals("a", q.getCacheGroup(), () -> "Add assertion message");
	}

	@Test
	public void testCacheStrategy() {
		ObjectSelect<DataRow> q = ObjectSelect.dataRowQuery(Artist.class);

		assertEquals(QueryCacheStrategy.NO_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertNull(q.getCacheGroup(), () -> "Add assertion message");

		q.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, "b");
		assertSame(QueryCacheStrategy.LOCAL_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertEquals("b", q.getCacheGroup(), () -> "Add assertion message");

		q.cacheStrategy(QueryCacheStrategy.SHARED_CACHE);
		assertSame(QueryCacheStrategy.SHARED_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertNull(q.getCacheGroup(), () -> "Add assertion message");
	}

	@Test
	public void testLocalCache() {
		ObjectSelect<DataRow> q = ObjectSelect.dataRowQuery(Artist.class);

		assertEquals(QueryCacheStrategy.NO_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertNull(q.getCacheGroup(), () -> "Add assertion message");

		q.localCache("a");
		assertSame(QueryCacheStrategy.LOCAL_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertEquals("a", q.getCacheGroup(), () -> "Add assertion message");

		q.localCache();
		// Line removed due to Duplicate Assert: assertSame(QueryCacheStrategy.LOCAL_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertNull(q.getCacheGroup(), () -> "Add assertion message");
	}

	@Test
	public void testSharedCache() {
		ObjectSelect<DataRow> q = ObjectSelect.dataRowQuery(Artist.class);

		assertEquals(QueryCacheStrategy.NO_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertNull(q.getCacheGroup(), () -> "Add assertion message");

		q.sharedCache("b");
		assertSame(QueryCacheStrategy.SHARED_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertEquals("b", q.getCacheGroup(), () -> "Add assertion message");

		q.sharedCache();
		// Line removed due to Duplicate Assert: assertSame(QueryCacheStrategy.SHARED_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertNull(q.getCacheGroup(), () -> "Add assertion message");
	}

	@Test
	public void testQueryTimeout() {
		ObjectSelect<Artist> query = ObjectSelect.query(Artist.class);
		assertEquals(-1, query.getQueryTimeout(), () -> "Add assertion message");

		query.queryTimeout(10);
		assertEquals(10, query.getQueryTimeout(), () -> "Add assertion message");

		query.queryTimeout(1).queryTimeout(2);
		assertEquals(2, query.getQueryTimeout(), () -> "Add assertion message");
	}

}