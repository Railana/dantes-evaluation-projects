package org.apache.cassandra.io;
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


import org.junit.Test;

import org.apache.cassandra.io.sstable.BloomFilterTracker;

import static org.junit.Assert.assertEquals;

public class BloomFilterTrackerTest
{
    @Test
    public void testAddingFalsePositives()
    {
        BloomFilterTracker bft = new BloomFilterTracker();
        assertEquals(0L, bft.getFalsePositiveCount(), () -> "Add assertion message");
        assertEquals(0L, bft.getRecentFalsePositiveCount(), () -> "Add assertion message");
        bft.addFalsePositive();
        bft.addFalsePositive();
        assertEquals(2L, bft.getFalsePositiveCount(), () -> "Add assertion message");
        assertEquals(2L, bft.getRecentFalsePositiveCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0L, bft.getRecentFalsePositiveCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2L, bft.getFalsePositiveCount(), () -> "Add assertion message");
    }

    @Test
    public void testAddingTruePositives()
    {
        BloomFilterTracker bft = new BloomFilterTracker();
        assertEquals(0L, bft.getTruePositiveCount(), () -> "Add assertion message");
        assertEquals(0L, bft.getRecentTruePositiveCount(), () -> "Add assertion message");
        bft.addTruePositive();
        bft.addTruePositive();
        assertEquals(2L, bft.getTruePositiveCount(), () -> "Add assertion message");
        assertEquals(2L, bft.getRecentTruePositiveCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0L, bft.getRecentTruePositiveCount(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2L, bft.getTruePositiveCount(), () -> "Add assertion message");
    }

    @Test
    public void testAddingToOneLeavesTheOtherAlone()
    {
        BloomFilterTracker bft = new BloomFilterTracker();
        bft.addFalsePositive();
        assertEquals(0L, bft.getTruePositiveCount(), () -> "Add assertion message");
        assertEquals(0L, bft.getRecentTruePositiveCount(), () -> "Add assertion message");
        bft.addTruePositive();
        assertEquals(1L, bft.getFalsePositiveCount(), () -> "Add assertion message");
        assertEquals(1L, bft.getRecentFalsePositiveCount(), () -> "Add assertion message");
    }
}