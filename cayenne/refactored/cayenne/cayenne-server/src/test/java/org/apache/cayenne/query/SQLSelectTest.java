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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.map.EntityResolver;
import org.junit.Test;

public class SQLSelectTest {


	@Test
	public void testCacheStrategy() {
		SQLSelect<DataRow> q = SQLSelect.dataRowQuery("bla");

		assertNull(q.getCacheStrategy(), () -> "Add assertion message");
		assertNull(q.getCacheGroup(), () -> "Add assertion message");

		q.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, "a");
		assertSame(QueryCacheStrategy.LOCAL_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertEquals("a", q.getCacheGroup(), () -> "Add assertion message");

		q.cacheStrategy(QueryCacheStrategy.SHARED_CACHE);
		assertSame(QueryCacheStrategy.SHARED_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertNull(q.getCacheGroup(), () -> "Add assertion message");
	}

	@Test
	public void testLocalCache() {
		SQLSelect<DataRow> q = SQLSelect.dataRowQuery("bla");

		assertNull(q.getCacheStrategy(), () -> "Add assertion message");
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
		SQLSelect<DataRow> q = SQLSelect.dataRowQuery("bla");

		assertNull(q.getCacheStrategy(), () -> "Add assertion message");
		assertNull(q.getCacheGroup(), () -> "Add assertion message");

		q.sharedCache("a");
		assertSame(QueryCacheStrategy.SHARED_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		assertEquals("a", q.getCacheGroup(), () -> "Add assertion message");

		q.sharedCache();
		// Line removed due to Duplicate Assert: assertSame(QueryCacheStrategy.SHARED_CACHE, q.getCacheStrategy(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertNull(q.getCacheGroup(), () -> "Add assertion message");
	}

	@Test
	public void testCreateReplacementQuery() {

		SQLSelect<DataRow> q = SQLSelect.dataRowQuery("bla");
		Query replacement = q.createReplacementQuery(mock(EntityResolver.class));
		assertThat(replacement, instanceOf(SQLTemplate.class), () -> "Add assertion message");
	}

	@Test
	public void testCreateReplacementQuery_ParamsArray_Single() {

		SQLSelect<DataRow> q = SQLSelect.dataRowQuery("bla").paramsArray("a");
		SQLTemplate replacement = (SQLTemplate) q.createReplacementQuery(mock(EntityResolver.class));
		assertArrayEquals(new Object[] { "a" }, replacement.getPositionalParams().toArray(), () -> "Add assertion message");
	}

	@Test
	public void testCreateReplacementQuery_ParamsArray_Multiple() {

		SQLSelect<DataRow> q = SQLSelect.dataRowQuery("bla").paramsArray("a", "b");
		SQLTemplate replacement = (SQLTemplate) q.createReplacementQuery(mock(EntityResolver.class));
		assertArrayEquals(new Object[] { "a", "b" }, replacement.getPositionalParams().toArray(), () -> "Add assertion message");
	}

	@Test
	public void testGetMetadata_ParamsArray_Multiple_Cache() {

		EntityResolver resolver = mock(EntityResolver.class);
		QueryMetadata md0 = SQLSelect.dataRowQuery("bla").localCache().getMetaData(resolver);
		QueryMetadata md1 = SQLSelect.dataRowQuery("bla").localCache().paramsArray("a").getMetaData(resolver);
		QueryMetadata md2 = SQLSelect.dataRowQuery("bla").localCache().paramsArray("a", "b").getMetaData(resolver);
		QueryMetadata md3 = SQLSelect.dataRowQuery("bla").localCache().paramsArray(null, "b").getMetaData(resolver);

		assertNotNull(md0.getCacheKey(), () -> "Add assertion message");
		assertNotNull(md1.getCacheKey(), () -> "Add assertion message");
		assertNotNull(md2.getCacheKey(), () -> "Add assertion message");
		assertNotNull(md3.getCacheKey(), () -> "Add assertion message");
		
		assertNotEquals(md0.getCacheKey(), md1.getCacheKey());
		assertNotEquals(md0.getCacheKey(), md2.getCacheKey());
		assertNotEquals(md1.getCacheKey(), md2.getCacheKey());
		assertNotEquals(md3.getCacheKey(), md2.getCacheKey());
	}
}