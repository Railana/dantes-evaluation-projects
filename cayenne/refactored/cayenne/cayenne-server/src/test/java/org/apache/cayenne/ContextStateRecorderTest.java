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

package org.apache.cayenne;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.cayenne.graph.GraphManager;
import org.junit.Before;
import org.junit.Test;

public class ContextStateRecorderTest {

	private ObjectContextStateLog recorder;
	private GraphManager mockGraphManager;

	@Before
	public void before() {
		this.mockGraphManager = mock(GraphManager.class);
		this.recorder = new ObjectContextStateLog(mockGraphManager);
	}

	@Test
	public void testDirtyNodesInState() {

		// check for null collections
		assertNotNull(recorder.dirtyNodes(PersistenceState.MODIFIED), () -> "Add assertion message");
		assertNotNull(recorder.dirtyNodes(PersistenceState.COMMITTED), () -> "Add assertion message");
		assertNotNull(recorder.dirtyNodes(PersistenceState.DELETED), () -> "Add assertion message");
		assertNotNull(recorder.dirtyNodes(PersistenceState.NEW), () -> "Add assertion message");
		assertNotNull(recorder.dirtyNodes(PersistenceState.TRANSIENT), () -> "Add assertion message");
		assertNotNull(recorder.dirtyNodes(PersistenceState.HOLLOW), () -> "Add assertion message");

		assertTrue(recorder.dirtyNodes(PersistenceState.MODIFIED).isEmpty(), () -> "Add assertion message");
		assertTrue(recorder.dirtyNodes(PersistenceState.COMMITTED).isEmpty(), () -> "Add assertion message");
		assertTrue(recorder.dirtyNodes(PersistenceState.DELETED).isEmpty(), () -> "Add assertion message");
		assertTrue(recorder.dirtyNodes(PersistenceState.NEW).isEmpty(), () -> "Add assertion message");
		assertTrue(recorder.dirtyNodes(PersistenceState.TRANSIENT).isEmpty(), () -> "Add assertion message");
		assertTrue(recorder.dirtyNodes(PersistenceState.HOLLOW).isEmpty(), () -> "Add assertion message");

		MockPersistentObject modified = new MockPersistentObject();
		modified.setObjectId(ObjectId.of("MockPersistentObject", "key", "value1"));
		modified.setPersistenceState(PersistenceState.MODIFIED);
		
		when(mockGraphManager.getNode(modified.getObjectId())).thenReturn(modified);
		recorder.nodePropertyChanged(modified.getObjectId(), "a", "b", "c");

		assertTrue(recorder.dirtyNodes(PersistenceState.MODIFIED).contains(modified), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.COMMITTED).isEmpty(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.DELETED).isEmpty(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.NEW).isEmpty(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.TRANSIENT).isEmpty(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.HOLLOW).isEmpty(), () -> "Add assertion message");

		MockPersistentObject deleted = new MockPersistentObject();
		deleted.setObjectId(ObjectId.of("MockPersistentObject", "key", "value2"));
		deleted.setPersistenceState(PersistenceState.DELETED);
		when(mockGraphManager.getNode(deleted.getObjectId())).thenReturn(deleted);
		recorder.nodeRemoved(deleted.getObjectId());

		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.MODIFIED).contains(modified), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.COMMITTED).isEmpty(), () -> "Add assertion message");
		assertTrue(recorder.dirtyNodes(PersistenceState.DELETED).contains(deleted), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.NEW).isEmpty(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.TRANSIENT).isEmpty(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes(PersistenceState.HOLLOW).isEmpty(), () -> "Add assertion message");
	}

	@Test
	public void testDirtyNodes() {

		assertNotNull(recorder.dirtyNodes(), () -> "Add assertion message");
		assertTrue(recorder.dirtyNodes().isEmpty(), () -> "Add assertion message");

		// introduce a fake dirty object
		MockPersistentObject object = new MockPersistentObject();
		object.setObjectId(ObjectId.of("MockPersistentObject", "key", "value"));
		object.setPersistenceState(PersistenceState.MODIFIED);

		when(mockGraphManager.getNode(object.getObjectId())).thenReturn(object);

		recorder.nodePropertyChanged(object.getObjectId(), "a", "b", "c");

		assertTrue(recorder.dirtyNodes().contains(object), () -> "Add assertion message");

		// must go away on clear...
		recorder.clear();
		// Line removed due to Duplicate Assert: assertNotNull(recorder.dirtyNodes(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertTrue(recorder.dirtyNodes().isEmpty(), () -> "Add assertion message");
	}

	@Test
	public void testHasChanges() {

		assertFalse(recorder.hasChanges(), () -> "Add assertion message");

		// introduce a fake dirty object
		MockPersistentObject object = new MockPersistentObject();
		object.setObjectId(ObjectId.of("MockPersistentObject", "key", "value"));
		object.setPersistenceState(PersistenceState.MODIFIED);
		recorder.nodePropertyChanged(object.getObjectId(), "xyz", "a", "b");

		assertTrue(recorder.hasChanges(), () -> "Add assertion message");

		// must go away on clear...
		recorder.clear();
		// Line removed due to Duplicate Assert: assertFalse(recorder.hasChanges(), () -> "Add assertion message");
	}

}