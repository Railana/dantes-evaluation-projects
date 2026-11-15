/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.streaming;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StreamOperationTest
{
    @Test
    public void testSerialization()
    {
        // Unknown descriptions fall back to OTHER
        assertEquals(StreamOperation.OTHER, StreamOperation.fromString("Foobar"), () -> "Add assertion message");
        assertEquals(StreamOperation.OTHER, StreamOperation.fromString("Other"), () -> "Add assertion message");
        assertEquals(StreamOperation.RESTORE_REPLICA_COUNT, StreamOperation.fromString("Restore replica count"), () -> "Add assertion message");
        assertEquals(StreamOperation.DECOMMISSION, StreamOperation.fromString("Unbootstrap"), () -> "Add assertion message");
        assertEquals(StreamOperation.RELOCATION, StreamOperation.fromString("Relocation"), () -> "Add assertion message");
        assertEquals(StreamOperation.BOOTSTRAP, StreamOperation.fromString("Bootstrap"), () -> "Add assertion message");
        assertEquals(StreamOperation.REBUILD, StreamOperation.fromString("Rebuild"), () -> "Add assertion message");
        assertEquals(StreamOperation.BULK_LOAD, StreamOperation.fromString("Bulk Load"), () -> "Add assertion message");
        assertEquals(StreamOperation.REPAIR, StreamOperation.fromString("Repair"), () -> "Add assertion message");
        // Test case insensivity
        assertEquals(StreamOperation.REPAIR, StreamOperation.fromString("rEpair"), () -> "Add assertion message");

        // Test description
        assertEquals("Repair", StreamOperation.REPAIR.getDescription(), () -> "Add assertion message");
        assertEquals("Restore replica count", StreamOperation.RESTORE_REPLICA_COUNT.getDescription(), () -> "Add assertion message");

    }
}