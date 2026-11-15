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

import static org.junit.Assert.assertNotNull;

import org.apache.cayenne.testdo.testmap.Artist;
import org.junit.Test;

public class SelectByIdTest {

	@Test
	public void testPrefetch() {

		PrefetchTreeNode root = PrefetchTreeNode.withPath("a.b", PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);

		SelectById<Artist> q = SelectById.query(Artist.class, 6);
		q.prefetch(root);

		PrefetchTreeNode prefetch = q.getPrefetches();

		assertNotNull(prefetch, () -> "Add assertion message");
		assertNotNull(prefetch.getNode("a.b"), () -> "Add assertion message");
	}

	@Test
	public void testPrefetch_Path() {

		SelectById<Artist> q = SelectById.query(Artist.class, 7);
		q.prefetch("a.b", PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS);
		PrefetchTreeNode prefetch = q.getPrefetches();

		assertNotNull(prefetch, () -> "Add assertion message");
		assertNotNull(prefetch.getNode("a.b"), () -> "Add assertion message");

		q.prefetch("a.c", PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS);
		prefetch = q.getPrefetches();

		// Line removed due to Duplicate Assert: assertNotNull(prefetch, () -> "Add assertion message");
		assertNotNull(prefetch.getNode("a.c"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertNotNull(prefetch.getNode("a.b"), () -> "Add assertion message");
	}

	@Test
	public void testPrefetch_Subroot() {

		PrefetchTreeNode root = PrefetchTreeNode.withPath("a.b", PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);

		SelectById<Artist> q = SelectById.query(Artist.class, 8);
		q.prefetch(root);

		PrefetchTreeNode prefetch = q.getPrefetches();

		assertNotNull(prefetch.getNode("a.b"), () -> "Add assertion message");

		PrefetchTreeNode subRoot = PrefetchTreeNode.withPath("a.b.c", PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS);
		q.prefetch(subRoot);

		prefetch = q.getPrefetches();

		// Line removed due to Duplicate Assert: assertNotNull(prefetch.getNode("a.b"), () -> "Add assertion message");
		assertNotNull(prefetch.getNode("a.b.c"), () -> "Add assertion message");
	}
}