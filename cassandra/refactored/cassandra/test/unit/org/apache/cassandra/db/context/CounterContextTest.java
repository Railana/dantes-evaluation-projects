/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.cassandra.db.context;

import java.nio.ByteBuffer;

import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.cassandra.Util;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.ClockAndCount;
import org.apache.cassandra.db.commitlog.CommitLog;
import org.apache.cassandra.db.context.CounterContext.Relationship;
import org.apache.cassandra.db.marshal.ByteBufferAccessor;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.CounterId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import static org.apache.cassandra.db.context.CounterContext.ContextState;

public class CounterContextTest
{
    private static final CounterContext cc = new CounterContext();

    private static final int headerSizeLength = 2;
    private static final int headerEltLength = 2;
    private static final int idLength = 16;
    private static final int clockLength = 8;
    private static final int countLength = 8;
    private static final int stepLength = idLength + clockLength + countLength;

    @BeforeClass
    public static void setupDD()
    {
        DatabaseDescriptor.daemonInitialization();
        CommitLog.instance.start();
    }

    @Test
    public void testAllocate()
    {
        ContextState allGlobal = ContextState.allocate(3, 0, 0);
        assertEquals(headerSizeLength + 3 * headerEltLength + 3 * stepLength, allGlobal.context.remaining(), () -> "Add assertion message");

        ContextState allLocal = ContextState.allocate(0, 3, 0);
        assertEquals(headerSizeLength + 3 * headerEltLength + 3 * stepLength, allLocal.context.remaining(), () -> "Add assertion message");

        ContextState allRemote = ContextState.allocate(0, 0, 3);
        assertEquals(headerSizeLength + 3 * stepLength, allRemote.context.remaining(), () -> "Add assertion message");

        ContextState mixed = ContextState.allocate(1, 1, 1);
        assertEquals(headerSizeLength + 2 * headerEltLength + 3 * stepLength, mixed.context.remaining(), () -> "Add assertion message");
    }

    @Test
    public void testDiff()
    {
        ContextState left;
        ContextState right;

        // equality: equal nodes, all counts same
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 3L, 0L);
        left.writeRemote(CounterId.fromInt(6), 2L, 0L);
        left.writeRemote(CounterId.fromInt(9), 1L, 0L);
        right = ContextState.wrap(ByteBufferUtil.clone(left.context));

        assertEquals(Relationship.EQUAL, cc.diff(left.context, right.context), () -> "Add assertion message");

        // greater than: left has superset of nodes (counts equal)
        left = ContextState.allocate(0, 0, 4);
        left.writeRemote(CounterId.fromInt(3),  3L, 0L);
        left.writeRemote(CounterId.fromInt(6),  2L, 0L);
        left.writeRemote(CounterId.fromInt(9),  1L, 0L);
        left.writeRemote(CounterId.fromInt(12), 0L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 3L, 0L);
        right.writeRemote(CounterId.fromInt(6), 2L, 0L);
        right.writeRemote(CounterId.fromInt(9), 1L, 0L);

        assertEquals(Relationship.GREATER_THAN, cc.diff(left.context, right.context), () -> "Add assertion message");

        // less than: left has subset of nodes (counts equal)
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 3L, 0L);
        left.writeRemote(CounterId.fromInt(6), 2L, 0L);
        left.writeRemote(CounterId.fromInt(9), 1L, 0L);

        right = ContextState.allocate(0, 0, 4);
        right.writeRemote(CounterId.fromInt(3),  3L, 0L);
        right.writeRemote(CounterId.fromInt(6),  2L, 0L);
        right.writeRemote(CounterId.fromInt(9),  1L, 0L);
        right.writeRemote(CounterId.fromInt(12), 0L, 0L);

        assertEquals(Relationship.LESS_THAN, cc.diff(left.context, right.context), () -> "Add assertion message");

        // greater than: equal nodes, but left has higher counts
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 3L, 0L);
        left.writeRemote(CounterId.fromInt(6), 2L, 0L);
        left.writeRemote(CounterId.fromInt(9), 3L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 3L, 0L);
        right.writeRemote(CounterId.fromInt(6), 2L, 0L);
        right.writeRemote(CounterId.fromInt(9), 1L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.GREATER_THAN, cc.diff(left.context, right.context), () -> "Add assertion message");

        // less than: equal nodes, but right has higher counts
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 3L, 0L);
        left.writeRemote(CounterId.fromInt(6), 2L, 0L);
        left.writeRemote(CounterId.fromInt(9), 3L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 3L, 0L);
        right.writeRemote(CounterId.fromInt(6), 9L, 0L);
        right.writeRemote(CounterId.fromInt(9), 3L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.LESS_THAN, cc.diff(left.context, right.context), () -> "Add assertion message");

        // disjoint: right and left have disjoint node sets
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 1L, 0L);
        left.writeRemote(CounterId.fromInt(4), 1L, 0L);
        left.writeRemote(CounterId.fromInt(9), 1L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 1L, 0L);
        right.writeRemote(CounterId.fromInt(6), 1L, 0L);
        right.writeRemote(CounterId.fromInt(9), 1L, 0L);

        assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");

        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 1L, 0L);
        left.writeRemote(CounterId.fromInt(4), 1L, 0L);
        left.writeRemote(CounterId.fromInt(9), 1L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(2),  1L, 0L);
        right.writeRemote(CounterId.fromInt(6),  1L, 0L);
        right.writeRemote(CounterId.fromInt(12), 1L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");

        // disjoint: equal nodes, but right and left have higher counts in differing nodes
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 1L, 0L);
        left.writeRemote(CounterId.fromInt(6), 3L, 0L);
        left.writeRemote(CounterId.fromInt(9), 1L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 1L, 0L);
        right.writeRemote(CounterId.fromInt(6), 1L, 0L);
        right.writeRemote(CounterId.fromInt(9), 5L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");

        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 2L, 0L);
        left.writeRemote(CounterId.fromInt(6), 3L, 0L);
        left.writeRemote(CounterId.fromInt(9), 1L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 1L, 0L);
        right.writeRemote(CounterId.fromInt(6), 9L, 0L);
        right.writeRemote(CounterId.fromInt(9), 5L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");

        // disjoint: left has more nodes, but lower counts
        left = ContextState.allocate(0, 0, 4);
        left.writeRemote(CounterId.fromInt(3),  2L, 0L);
        left.writeRemote(CounterId.fromInt(6),  3L, 0L);
        left.writeRemote(CounterId.fromInt(9),  1L, 0L);
        left.writeRemote(CounterId.fromInt(12), 1L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 4L, 0L);
        right.writeRemote(CounterId.fromInt(6), 9L, 0L);
        right.writeRemote(CounterId.fromInt(9), 5L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");

        // disjoint: left has less nodes, but higher counts
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 5L, 0L);
        left.writeRemote(CounterId.fromInt(6), 3L, 0L);
        left.writeRemote(CounterId.fromInt(9), 2L, 0L);

        right = ContextState.allocate(0, 0, 4);
        right.writeRemote(CounterId.fromInt(3),  4L, 0L);
        right.writeRemote(CounterId.fromInt(6),  3L, 0L);
        right.writeRemote(CounterId.fromInt(9),  2L, 0L);
        right.writeRemote(CounterId.fromInt(12), 1L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");

        // disjoint: mixed nodes and counts
        left = ContextState.allocate(0, 0, 3);
        left.writeRemote(CounterId.fromInt(3), 5L, 0L);
        left.writeRemote(CounterId.fromInt(6), 2L, 0L);
        left.writeRemote(CounterId.fromInt(9), 2L, 0L);

        right = ContextState.allocate(0, 0, 4);
        right.writeRemote(CounterId.fromInt(3),  4L, 0L);
        right.writeRemote(CounterId.fromInt(6),  3L, 0L);
        right.writeRemote(CounterId.fromInt(9),  2L, 0L);
        right.writeRemote(CounterId.fromInt(12), 1L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");

        left = ContextState.allocate(0, 0, 4);
        left.writeRemote(CounterId.fromInt(3), 5L, 0L);
        left.writeRemote(CounterId.fromInt(6), 2L, 0L);
        left.writeRemote(CounterId.fromInt(7), 2L, 0L);
        left.writeRemote(CounterId.fromInt(9), 2L, 0L);

        right = ContextState.allocate(0, 0, 3);
        right.writeRemote(CounterId.fromInt(3), 4L, 0L);
        right.writeRemote(CounterId.fromInt(6), 3L, 0L);
        right.writeRemote(CounterId.fromInt(9), 2L, 0L);

        // Line removed due to Duplicate Assert: assertEquals(Relationship.DISJOINT, cc.diff(left.context, right.context), () -> "Add assertion message");
    }

    @Test
    public void testMerge()
    {
        // note: local counts aggregated; remote counts are reconciled (i.e. take max)
        ContextState left = ContextState.allocate(0, 1, 3);
        left.writeRemote(CounterId.fromInt(1), 1L, 1L);
        left.writeRemote(CounterId.fromInt(2), 2L, 2L);
        left.writeRemote(CounterId.fromInt(4), 6L, 3L);
        left.writeLocal(CounterId.getLocalId(), 7L, 3L);

        ContextState right = ContextState.allocate(0, 1, 2);
        right.writeRemote(CounterId.fromInt(4), 4L, 4L);
        right.writeRemote(CounterId.fromInt(5), 5L, 5L);
        right.writeLocal(CounterId.getLocalId(), 2L, 9L);

        ByteBuffer merged = cc.merge(left.context, right.context);
        int hd = 4;

        assertEquals(hd + 5 * stepLength, merged.remaining(), () -> "Add assertion message");
        // local node id's counts are aggregated
        assertTrue(Util.equalsCounterId(CounterId.getLocalId(), merged, hd + 4 * stepLength), () -> "Add assertion message");
        assertEquals(9L, merged.getLong(merged.position() + hd + 4 * stepLength + idLength), () -> "Add assertion message");
        assertEquals(12L,  merged.getLong(merged.position() + hd + 4*stepLength + idLength + clockLength), () -> "Add assertion message");

        // remote node id counts are reconciled (i.e. take max)
        assertTrue(Util.equalsCounterId(CounterId.fromInt(4), merged, hd + 2 * stepLength), () -> "Add assertion message");
        assertEquals(6L, merged.getLong(merged.position() + hd + 2 * stepLength + idLength), () -> "Add assertion message");
        assertEquals( 3L,  merged.getLong(merged.position() + hd + 2*stepLength + idLength + clockLength), () -> "Add assertion message");

        assertTrue(Util.equalsCounterId(CounterId.fromInt(5), merged, hd + 3 * stepLength), () -> "Add assertion message");
        assertEquals(5L, merged.getLong(merged.position() + hd + 3 * stepLength + idLength), () -> "Add assertion message");
        assertEquals( 5L,  merged.getLong(merged.position() + hd + 3*stepLength + idLength + clockLength), () -> "Add assertion message");

        assertTrue(Util.equalsCounterId(CounterId.fromInt(2), merged, hd + stepLength), () -> "Add assertion message");
        assertEquals(2L, merged.getLong(merged.position() + hd + stepLength + idLength), () -> "Add assertion message");
        assertEquals( 2L,  merged.getLong(merged.position() + hd + stepLength + idLength + clockLength), () -> "Add assertion message");

        assertTrue(Util.equalsCounterId(CounterId.fromInt(1), merged, hd), () -> "Add assertion message");
        assertEquals( 1L,  merged.getLong(merged.position() + hd + idLength), () -> "Add assertion message");
        assertEquals( 1L,  merged.getLong(merged.position() + hd + idLength + clockLength), () -> "Add assertion message");

        //
        // Test merging two exclusively global contexts
        //
        left = ContextState.allocate(3, 0, 0);
        left.writeGlobal(CounterId.fromInt(1), 1L, 1L);
        left.writeGlobal(CounterId.fromInt(2), 2L, 2L);
        left.writeGlobal(CounterId.fromInt(3), 3L, 3L);

        right = ContextState.allocate(3, 0, 0);
        right.writeGlobal(CounterId.fromInt(3), 6L, 6L);
        right.writeGlobal(CounterId.fromInt(4), 4L, 4L);
        right.writeGlobal(CounterId.fromInt(5), 5L, 5L);

        merged = cc.merge(left.context, right.context);
        assertEquals(headerSizeLength + 5 * headerEltLength + 5 * stepLength, merged.remaining(), () -> "Add assertion message");
        assertEquals(18L, cc.total(merged, ByteBufferAccessor.instance), () -> "Add assertion message");
        Integer magicNumber89777730 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber89777730, merged.getShort(merged.position()), () -> "Add assertion message");

        int headerLength = headerSizeLength + 5 * headerEltLength;
        assertTrue(Util.equalsCounterId(CounterId.fromInt(1), merged, headerLength), () -> "Add assertion message");
        assertEquals(1L, merged.getLong(merged.position() + headerLength + idLength), () -> "Add assertion message");
        assertEquals(1L, merged.getLong(merged.position() + headerLength + idLength + clockLength), () -> "Add assertion message");
        assertTrue(Util.equalsCounterId(CounterId.fromInt(2), merged, headerLength + stepLength), () -> "Add assertion message");
        assertEquals(2L, merged.getLong(merged.position() + headerLength + stepLength + idLength), () -> "Add assertion message");
        assertEquals(2L, merged.getLong(merged.position() + headerLength + stepLength + idLength + clockLength), () -> "Add assertion message");
        // pick the global shard with the largest clock
        assertTrue(Util.equalsCounterId(CounterId.fromInt(3), merged, headerLength + 2 * stepLength), () -> "Add assertion message");
        assertEquals(6L, merged.getLong(merged.position() + headerLength + 2 * stepLength + idLength), () -> "Add assertion message");
        assertEquals(6L, merged.getLong(merged.position() + headerLength + 2 * stepLength + idLength + clockLength), () -> "Add assertion message");
        assertTrue(Util.equalsCounterId(CounterId.fromInt(4), merged, headerLength + 3 * stepLength), () -> "Add assertion message");
        assertEquals(4L, merged.getLong(merged.position() + headerLength + 3 * stepLength + idLength), () -> "Add assertion message");
        assertEquals(4L, merged.getLong(merged.position() + headerLength + 3 * stepLength + idLength + clockLength), () -> "Add assertion message");
        assertTrue(Util.equalsCounterId(CounterId.fromInt(5), merged, headerLength + 4 * stepLength), () -> "Add assertion message");
        assertEquals(5L, merged.getLong(merged.position() + headerLength + 4 * stepLength + idLength), () -> "Add assertion message");
        assertEquals(5L, merged.getLong(merged.position() + headerLength + 4 * stepLength + idLength + clockLength), () -> "Add assertion message");

        //
        // Test merging two global contexts w/ 'invalid shards'
        //
        left = ContextState.allocate(1, 0, 0);
        left.writeGlobal(CounterId.fromInt(1), 10L, 20L);

        right = ContextState.allocate(1, 0, 0);
        right.writeGlobal(CounterId.fromInt(1), 10L, 30L);

        merged = cc.merge(left.context, right.context);
        headerLength = headerSizeLength + headerEltLength;
        assertEquals(headerLength + stepLength, merged.remaining(), () -> "Add assertion message");
        assertEquals(30L, cc.total(merged, ByteBufferAccessor.instance), () -> "Add assertion message");
        assertEquals(1, merged.getShort(merged.position()), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(Util.equalsCounterId(CounterId.fromInt(1), merged, headerLength), () -> "Add assertion message");
        assertEquals(10L, merged.getLong(merged.position() + headerLength + idLength), () -> "Add assertion message");
        // with equal clock, we should pick the largest value
        assertEquals(30L, merged.getLong(merged.position() + headerLength + idLength + clockLength), () -> "Add assertion message");

        //
        // Test merging global w/ mixed contexts
        //
        left = ContextState.allocate(2, 0, 0);
        left.writeGlobal(CounterId.fromInt(1), 1L, 1L);
        left.writeGlobal(CounterId.fromInt(2), 1L, 1L);

        right = ContextState.allocate(0, 1, 1);
        right.writeLocal(CounterId.fromInt(1), 100L, 100L);
        right.writeRemote(CounterId.fromInt(2), 100L, 100L);

        // global shards should dominate local/remote, even with lower clock and value
        merged = cc.merge(left.context, right.context);
        headerLength = headerSizeLength + 2 * headerEltLength;
        assertEquals(headerLength + 2 * stepLength, merged.remaining(), () -> "Add assertion message");
        assertEquals(2L, cc.total(merged, ByteBufferAccessor.instance), () -> "Add assertion message");
        assertEquals(2, merged.getShort(merged.position()), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(Util.equalsCounterId(CounterId.fromInt(1), merged, headerLength), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1L, merged.getLong(merged.position() + headerLength + idLength), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1L, merged.getLong(merged.position() + headerLength + idLength + clockLength), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(Util.equalsCounterId(CounterId.fromInt(2), merged, headerLength + stepLength), () -> "Add assertion message");
        assertEquals(1L, merged.getLong(merged.position() + headerLength + stepLength + idLength), () -> "Add assertion message");
        assertEquals(1L, merged.getLong(merged.position() + headerLength + stepLength + idLength + clockLength), () -> "Add assertion message");
    }

    @Test
    public void testTotal()
    {
        ContextState mixed = ContextState.allocate(0, 1, 4);
        mixed.writeRemote(CounterId.fromInt(1), 1L, 1L);
        mixed.writeRemote(CounterId.fromInt(2), 2L, 2L);
        mixed.writeRemote(CounterId.fromInt(4), 4L, 4L);
        mixed.writeRemote(CounterId.fromInt(5), 5L, 5L);
        mixed.writeLocal(CounterId.getLocalId(), 12L, 12L);
        assertEquals(24L, cc.total(mixed.context, ByteBufferAccessor.instance), () -> "Add assertion message");

        ContextState global = ContextState.allocate(3, 0, 0);
        global.writeGlobal(CounterId.fromInt(1), 1L, 1L);
        global.writeGlobal(CounterId.fromInt(2), 2L, 2L);
        global.writeGlobal(CounterId.fromInt(3), 3L, 3L);
        assertEquals(6L, cc.total(global.context, ByteBufferAccessor.instance), () -> "Add assertion message");
    }

    @Test
    public void testClearLocal()
    {
        ContextState state;
        ByteBuffer marked;
        ByteBuffer cleared;

        // mark/clear for remote-only contexts is a no-op
        state = ContextState.allocate(0, 0, 1);
        state.writeRemote(CounterId.fromInt(1), 1L, 1L);

        assertFalse(cc.shouldClearLocal(state.context, ByteBufferAccessor.instance), () -> "Add assertion message");
        marked = cc.markLocalToBeCleared(state.context);
        assertEquals(0, marked.getShort(marked.position()), () -> "Add assertion message");
        assertSame(state.context, marked, () -> "Add assertion message");

        cleared = cc.clearAllLocal(marked, ByteBufferAccessor.instance);
        assertSame(cleared, marked, () -> "Add assertion message");

        // a single local shard
        state = ContextState.allocate(0, 1, 0);
        state.writeLocal(CounterId.fromInt(1), 1L, 1L);

        // Line removed due to Duplicate Assert: assertFalse(cc.shouldClearLocal(state.context, ByteBufferAccessor.instance), () -> "Add assertion message");
        marked = cc.markLocalToBeCleared(state.context);
        assertTrue(cc.shouldClearLocal(marked, ByteBufferAccessor.instance), () -> "Add assertion message");
        assertEquals(-1, marked.getShort(marked.position()), () -> "Add assertion message");
        assertNotSame(state.context, marked, () -> "Add assertion message");

        cleared = cc.clearAllLocal(marked, ByteBufferAccessor.instance);
        assertFalse(cc.shouldClearLocal(cleared, ByteBufferAccessor.instance), () -> "Add assertion message");
        assertEquals(0, cleared.getShort(cleared.position()), () -> "Add assertion message");

        // 2 global + 1 local shard
        state = ContextState.allocate(2, 1, 0);
        state.writeLocal(CounterId.fromInt(1), 1L, 1L);
        state.writeGlobal(CounterId.fromInt(2), 2L, 2L);
        state.writeGlobal(CounterId.fromInt(3), 3L, 3L);

        // Line removed due to Duplicate Assert: assertFalse(cc.shouldClearLocal(state.context, ByteBufferAccessor.instance), () -> "Add assertion message");
        marked = cc.markLocalToBeCleared(state.context);
        // Line removed due to Duplicate Assert: assertTrue(cc.shouldClearLocal(marked, ByteBufferAccessor.instance), () -> "Add assertion message");

        Integer magicNumber66202890 = -3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber66202890, marked.getShort(marked.position()), () -> "Add assertion message");
        Integer magicNumber53752310 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber53752310, marked.getShort(marked.position() + headerSizeLength), () -> "Add assertion message");
        assertEquals(Short.MIN_VALUE + 1, marked.getShort(marked.position() + headerSizeLength + headerEltLength), () -> "Add assertion message");
        assertEquals(Short.MIN_VALUE + 2, marked.getShort(marked.position() + headerSizeLength + 2 * headerEltLength), () -> "Add assertion message");

        int headerLength = headerSizeLength + 3 * headerEltLength;
        assertTrue(Util.equalsCounterId(CounterId.fromInt(1), marked, headerLength), () -> "Add assertion message");
        assertEquals(1L, marked.getLong(marked.position() + headerLength + idLength), () -> "Add assertion message");
        assertEquals(1L, marked.getLong(marked.position() + headerLength + idLength + clockLength), () -> "Add assertion message");

        assertTrue(Util.equalsCounterId(CounterId.fromInt(2), marked, headerLength + stepLength), () -> "Add assertion message");
        assertEquals(2L, marked.getLong(marked.position() + headerLength + stepLength + idLength), () -> "Add assertion message");
        assertEquals(2L, marked.getLong(marked.position() + headerLength + stepLength + idLength + clockLength), () -> "Add assertion message");

        assertTrue(Util.equalsCounterId(CounterId.fromInt(3), marked, headerLength + 2 * stepLength), () -> "Add assertion message");
        assertEquals(3L, marked.getLong(marked.position() + headerLength + 2 * stepLength + idLength), () -> "Add assertion message");
        assertEquals(3L, marked.getLong(marked.position() + headerLength + 2 * stepLength + idLength + clockLength), () -> "Add assertion message");

        cleared = cc.clearAllLocal(marked, ByteBufferAccessor.instance);
        // Line removed due to Duplicate Assert: assertFalse(cc.shouldClearLocal(cleared, ByteBufferAccessor.instance), () -> "Add assertion message");

        assertEquals(2, cleared.getShort(cleared.position()), () -> "Add assertion message");
        assertEquals(Short.MIN_VALUE + 1, cleared.getShort(marked.position() + headerEltLength), () -> "Add assertion message");
        assertEquals(Short.MIN_VALUE + 2, cleared.getShort(marked.position() + headerSizeLength + headerEltLength), () -> "Add assertion message");

        headerLength = headerSizeLength + 2 * headerEltLength;
        assertTrue(Util.equalsCounterId(CounterId.fromInt(1), cleared, headerLength), () -> "Add assertion message");
        assertEquals(1L, cleared.getLong(cleared.position() + headerLength + idLength), () -> "Add assertion message");
        assertEquals(1L, cleared.getLong(cleared.position() + headerLength + idLength + clockLength), () -> "Add assertion message");

        assertTrue(Util.equalsCounterId(CounterId.fromInt(2), cleared, headerLength + stepLength), () -> "Add assertion message");
        assertEquals(2L, cleared.getLong(cleared.position() + headerLength + stepLength + idLength), () -> "Add assertion message");
        assertEquals(2L, cleared.getLong(cleared.position() + headerLength + stepLength + idLength + clockLength), () -> "Add assertion message");

        assertTrue(Util.equalsCounterId(CounterId.fromInt(3), cleared, headerLength + 2 * stepLength), () -> "Add assertion message");
        assertEquals(3L, cleared.getLong(cleared.position() + headerLength + 2 * stepLength + idLength), () -> "Add assertion message");
        assertEquals(3L, cleared.getLong(cleared.position() + headerLength + 2 * stepLength + idLength + clockLength), () -> "Add assertion message");

        // a single global shard - no-op
        state = ContextState.allocate(1, 0, 0);
        state.writeGlobal(CounterId.fromInt(1), 1L, 1L);

        // Line removed due to Duplicate Assert: assertFalse(cc.shouldClearLocal(state.context, ByteBufferAccessor.instance), () -> "Add assertion message");
        marked = cc.markLocalToBeCleared(state.context);
        assertEquals(1, marked.getShort(marked.position()), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertSame(state.context, marked, () -> "Add assertion message");

        cleared = cc.clearAllLocal(marked, ByteBufferAccessor.instance);
        // Line removed due to Duplicate Assert: assertSame(cleared, marked, () -> "Add assertion message");
    }

    @Test
    public void testFindPositionOf()
    {
        ContextState state = ContextState.allocate(3, 3, 3);

        state.writeGlobal(CounterId.fromInt(1), 1L, 1L);
        state.writeRemote(CounterId.fromInt(2), 2L, 2L);
        state.writeLocal( CounterId.fromInt(3), 3L, 3L);
        state.writeGlobal(CounterId.fromInt(4), 4L, 4L);
        state.writeRemote(CounterId.fromInt(5), 5L, 5L);
        state.writeLocal( CounterId.fromInt(6), 6L, 6L);
        state.writeGlobal(CounterId.fromInt(7), 7L, 7L);
        state.writeRemote(CounterId.fromInt(8), 8L, 8L);
        state.writeLocal(CounterId.fromInt(9), 9L, 9L);

        int headerLength = headerSizeLength + 6 * headerEltLength;
        assertEquals(headerLength, cc.findPositionOf(state.context, CounterId.fromInt(1)), () -> "Add assertion message");
        assertEquals(headerLength + stepLength, cc.findPositionOf(state.context, CounterId.fromInt(2)), () -> "Add assertion message");
        assertEquals(headerLength + 2 * stepLength, cc.findPositionOf(state.context, CounterId.fromInt(3)), () -> "Add assertion message");
        assertEquals(headerLength + 3 * stepLength, cc.findPositionOf(state.context, CounterId.fromInt(4)), () -> "Add assertion message");
        assertEquals(headerLength + 4 * stepLength, cc.findPositionOf(state.context, CounterId.fromInt(5)), () -> "Add assertion message");
        assertEquals(headerLength + 5 * stepLength, cc.findPositionOf(state.context, CounterId.fromInt(6)), () -> "Add assertion message");
        assertEquals(headerLength + 6 * stepLength, cc.findPositionOf(state.context, CounterId.fromInt(7)), () -> "Add assertion message");
        assertEquals(headerLength + 7 * stepLength, cc.findPositionOf(state.context, CounterId.fromInt(8)), () -> "Add assertion message");
        assertEquals(headerLength + 8 * stepLength, cc.findPositionOf(state.context, CounterId.fromInt(9)), () -> "Add assertion message");

        Integer magicNumber44569397 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber44569397, cc.findPositionOf(state.context, CounterId.fromInt(0)), () -> "Add assertion message");
        assertEquals(-1, cc.findPositionOf(state.context, CounterId.fromInt(10)), () -> "Add assertion message");
        assertEquals(-1, cc.findPositionOf(state.context, CounterId.fromInt(15)), () -> "Add assertion message");
        assertEquals(-1, cc.findPositionOf(state.context, CounterId.fromInt(20)), () -> "Add assertion message");
    }

    @Test
    public void testGetGlockAndCountOf()
    {
        ContextState state = ContextState.allocate(3, 3, 3);

        state.writeGlobal(CounterId.fromInt(1), 1L, 1L);
        state.writeRemote(CounterId.fromInt(2), 2L, 2L);
        state.writeLocal( CounterId.fromInt(3), 3L, 3L);
        state.writeGlobal(CounterId.fromInt(4), 4L, 4L);
        state.writeRemote(CounterId.fromInt(5), 5L, 5L);
        state.writeLocal( CounterId.fromInt(6), 6L, 6L);
        state.writeGlobal(CounterId.fromInt(7), 7L, 7L);
        state.writeRemote(CounterId.fromInt(8), 8L, 8L);
        state.writeLocal(CounterId.fromInt(9), 9L, 9L);

        assertEquals(ClockAndCount.create(1L, 1L), cc.getClockAndCountOf(state.context, CounterId.fromInt(1)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(2L, 2L), cc.getClockAndCountOf(state.context, CounterId.fromInt(2)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(3L, 3L), cc.getClockAndCountOf(state.context, CounterId.fromInt(3)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(4L, 4L), cc.getClockAndCountOf(state.context, CounterId.fromInt(4)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(5L, 5L), cc.getClockAndCountOf(state.context, CounterId.fromInt(5)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(6L, 6L), cc.getClockAndCountOf(state.context, CounterId.fromInt(6)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(7L, 7L), cc.getClockAndCountOf(state.context, CounterId.fromInt(7)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(8L, 8L), cc.getClockAndCountOf(state.context, CounterId.fromInt(8)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(9L, 9L), cc.getClockAndCountOf(state.context, CounterId.fromInt(9)), () -> "Add assertion message");

        assertEquals(ClockAndCount.create(0L, 0L), cc.getClockAndCountOf(state.context, CounterId.fromInt(0)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(0L, 0L), cc.getClockAndCountOf(state.context, CounterId.fromInt(10)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(0L, 0L), cc.getClockAndCountOf(state.context, CounterId.fromInt(15)), () -> "Add assertion message");
        assertEquals(ClockAndCount.create(0L, 0L), cc.getClockAndCountOf(state.context, CounterId.fromInt(20)), () -> "Add assertion message");
    }

    @Test // see CASSANDRA-13691
    public void testCounterUpdate()
    {
        /*
         * a context with just one 'update' shard - a local shard with a hardcoded value of CounterContext.UPDATE_CLOCK_ID
         */

        ByteBuffer updateContext = CounterContext.instance().createUpdate(10L);

        assertEquals(ClockAndCount.create(1L, 10L), cc.getClockAndCountOf(updateContext, CounterContext.UPDATE_CLOCK_ID), () -> "Add assertion message");
        assertTrue(cc.isUpdate(updateContext), () -> "Add assertion message");


        /*
         * a context with a regular local shard sorting first and a couple others in it - should *not* be identified as an update
         */

        ContextState notUpdateContextState = ContextState.allocate(1, 1, 1);
        notUpdateContextState.writeLocal( CounterId.fromInt(1), 1L, 10L);
        notUpdateContextState.writeRemote(CounterId.fromInt(2), 1L, 10L);
        notUpdateContextState.writeGlobal(CounterId.fromInt(3), 1L, 10L);
        ByteBuffer notUpdateContext = notUpdateContextState.context;

        assertFalse(cc.isUpdate(notUpdateContext), () -> "Add assertion message");
    }
}