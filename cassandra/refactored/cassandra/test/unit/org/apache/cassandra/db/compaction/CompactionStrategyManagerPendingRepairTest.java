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

package org.apache.cassandra.db.compaction;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;

import org.apache.cassandra.io.sstable.format.SSTableReader;
import org.apache.cassandra.notifications.SSTableAddedNotification;
import org.apache.cassandra.notifications.SSTableDeletingNotification;
import org.apache.cassandra.notifications.SSTableListChangedNotification;
import org.apache.cassandra.notifications.SSTableRepairStatusChanged;
import org.apache.cassandra.repair.NoSuchRepairSessionException;
import org.apache.cassandra.repair.consistent.LocalSessionAccessor;
import org.apache.cassandra.service.ActiveRepairService;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.cassandra.utils.TimeUUID;

/**
 * Tests CompactionStrategyManager's handling of pending repair sstables
 */
public class CompactionStrategyManagerPendingRepairTest extends AbstractPendingRepairTest
{

    private boolean transientContains(SSTableReader sstable)
    {
        return csm.getTransientRepairsUnsafe().containsSSTable(sstable);
    }

    private boolean pendingContains(SSTableReader sstable)
    {
        return csm.getPendingRepairsUnsafe().containsSSTable(sstable);
    }

    private boolean repairedContains(SSTableReader sstable)
    {
        return csm.getRepairedUnsafe().containsSSTable(sstable);
    }

    private boolean unrepairedContains(SSTableReader sstable)
    {
        return csm.getUnrepairedUnsafe().containsSSTable(sstable);
    }

    private boolean hasPendingStrategiesFor(TimeUUID sessionID)
    {
        return !Iterables.isEmpty(csm.getPendingRepairsUnsafe().getStrategiesFor(sessionID));
    }

    private boolean hasTransientStrategiesFor(TimeUUID sessionID)
    {
        return !Iterables.isEmpty(csm.getTransientRepairsUnsafe().getStrategiesFor(sessionID));
    }

    /**
     * Pending repair strategy should be created when we encounter a new pending id
     */
    @Test
    public void sstableAdded()
    {
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);
        Assert.assertTrue(Iterables.isEmpty(csm.getPendingRepairsUnsafe().allStrategies()), () -> "Add assertion message");

        SSTableReader sstable = makeSSTable(true);
        Assert.assertFalse(sstable.isRepaired(), () -> "Add assertion message");
        Assert.assertFalse(sstable.isPendingRepair(), () -> "Add assertion message");

        mutateRepaired(sstable, repairID, false);
        // Line removed due to Duplicate Assert: Assert.assertFalse(sstable.isRepaired(), () -> "Add assertion message");
        Assert.assertTrue(sstable.isPendingRepair(), () -> "Add assertion message");
        Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");

        // add the sstable
        csm.handleNotification(new SSTableAddedNotification(Collections.singleton(sstable), null), cfs.getTracker());
        Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(unrepairedContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(pendingContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
    }

    @Test
    public void sstableListChangedAddAndRemove()
    {
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);

        SSTableReader sstable1 = makeSSTable(true);
        mutateRepaired(sstable1, repairID, false);

        SSTableReader sstable2 = makeSSTable(true);
        mutateRepaired(sstable2, repairID, false);

        Assert.assertFalse(repairedContains(sstable1), () -> "Add assertion message");
        Assert.assertFalse(unrepairedContains(sstable1), () -> "Add assertion message");
        Assert.assertFalse(repairedContains(sstable2), () -> "Add assertion message");
        Assert.assertFalse(unrepairedContains(sstable2), () -> "Add assertion message");
        Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");

        // add only
        SSTableListChangedNotification notification;
        notification = new SSTableListChangedNotification(Collections.singleton(sstable1),
                                                          Collections.emptyList(),
                                                          OperationType.COMPACTION);
        csm.handleNotification(notification, cfs.getTracker());

        // Line removed due to Duplicate Assert: Assert.assertFalse(repairedContains(sstable1), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(unrepairedContains(sstable1), () -> "Add assertion message");
        Assert.assertTrue(pendingContains(sstable1), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(repairedContains(sstable2), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(unrepairedContains(sstable2), () -> "Add assertion message");
        Assert.assertFalse(pendingContains(sstable2), () -> "Add assertion message");
        Assert.assertTrue(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");

        // remove and add
        notification = new SSTableListChangedNotification(Collections.singleton(sstable2),
                                                          Collections.singleton(sstable1),
                                                          OperationType.COMPACTION);
        csm.handleNotification(notification, cfs.getTracker());

        // Line removed due to Duplicate Assert: Assert.assertFalse(repairedContains(sstable1), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(unrepairedContains(sstable1), () -> "Add assertion message");
        Assert.assertFalse(pendingContains(sstable1), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(repairedContains(sstable2), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(unrepairedContains(sstable2), () -> "Add assertion message");
        Assert.assertTrue(pendingContains(sstable2), () -> "Add assertion message");
    }

    @Test
    public void sstableRepairStatusChanged()
    {
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);

        // add as unrepaired
        SSTableReader sstable = makeSSTable(false);
        Assert.assertTrue(unrepairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");

        SSTableRepairStatusChanged notification;

        // change to pending repaired
        mutateRepaired(sstable, repairID, false);
        notification = new SSTableRepairStatusChanged(Collections.singleton(sstable));
        csm.handleNotification(notification, cfs.getTracker());
        Assert.assertFalse(unrepairedContains(sstable), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertTrue(pendingContains(sstable), () -> "Add assertion message");

        // change to repaired
        mutateRepaired(sstable, System.currentTimeMillis());
        notification = new SSTableRepairStatusChanged(Collections.singleton(sstable));
        csm.handleNotification(notification, cfs.getTracker());
        // Line removed due to Duplicate Assert: Assert.assertFalse(unrepairedContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(pendingContains(sstable), () -> "Add assertion message");
    }

    @Test
    public void sstableDeleted()
    {
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);

        SSTableReader sstable = makeSSTable(true);
        mutateRepaired(sstable, repairID, false);
        csm.handleNotification(new SSTableAddedNotification(Collections.singleton(sstable), null), cfs.getTracker());
        Assert.assertTrue(pendingContains(sstable), () -> "Add assertion message");

        // delete sstable
        SSTableDeletingNotification notification = new SSTableDeletingNotification(sstable);
        csm.handleNotification(notification, cfs.getTracker());
        Assert.assertFalse(pendingContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(unrepairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
    }

    /**
     * CompactionStrategyManager.getStrategies should include
     * pending repair strategies when appropriate
     */
    @Test
    public void getStrategies()
    {
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);

        List<List<AbstractCompactionStrategy>> strategies;

        strategies = csm.getStrategies();
        Integer magicNumber177303 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber177303, strategies.size(), () -> "Add assertion message");
        Assert.assertTrue(strategies.get(2).isEmpty(), () -> "Add assertion message");

        SSTableReader sstable = makeSSTable(true);
        mutateRepaired(sstable, repairID, false);
        csm.handleNotification(new SSTableAddedNotification(Collections.singleton(sstable), null), cfs.getTracker());

        strategies = csm.getStrategies();
        // Line removed due to Duplicate Assert: Assert.assertEquals(3, strategies.size(), () -> "Add assertion message");
        Assert.assertFalse(strategies.get(2).isEmpty(), () -> "Add assertion message");
    }

    /**
     * Tests that finalized repairs result in cleanup compaction tasks
     * which reclassify the sstables as repaired
     */
    @Test
    public void cleanupCompactionFinalized() throws NoSuchRepairSessionException
    {
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);
        SSTableReader sstable = makeSSTable(true);
        mutateRepaired(sstable, repairID, false);
        csm.handleNotification(new SSTableAddedNotification(Collections.singleton(sstable), null), cfs.getTracker());
        LocalSessionAccessor.finalizeUnsafe(repairID);
        Assert.assertTrue(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertTrue(pendingContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(sstable.isPendingRepair(), () -> "Add assertion message");
        Assert.assertFalse(sstable.isRepaired(), () -> "Add assertion message");

        cfs.getCompactionStrategyManager().enable(); // enable compaction to fetch next background task
        AbstractCompactionTask compactionTask = csm.getNextBackgroundTask(FBUtilities.nowInSeconds());
        Assert.assertNotNull(compactionTask, () -> "Add assertion message");
        Assert.assertSame(PendingRepairManager.RepairFinishedCompactionTask.class, compactionTask.getClass(), () -> "Add assertion message");

        // run the compaction
        compactionTask.execute(ActiveCompactionsTracker.NOOP);

        Assert.assertTrue(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(unrepairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(pendingContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");

        // sstable should have pendingRepair cleared, and repairedAt set correctly
        long expectedRepairedAt = ActiveRepairService.instance.getParentRepairSession(repairID).repairedAt;
        Assert.assertFalse(sstable.isPendingRepair(), () -> "Add assertion message");
        Assert.assertTrue(sstable.isRepaired(), () -> "Add assertion message");
        Assert.assertEquals(expectedRepairedAt, sstable.getSSTableMetadata().repairedAt, () -> "Add assertion message");
    }

    /**
     * Tests that failed repairs result in cleanup compaction tasks
     * which reclassify the sstables as unrepaired
     */
    @Test
    public void cleanupCompactionFailed()
    {
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);
        SSTableReader sstable = makeSSTable(true);
        mutateRepaired(sstable, repairID, false);
        csm.handleNotification(new SSTableAddedNotification(Collections.singleton(sstable), null), cfs.getTracker());
        LocalSessionAccessor.failUnsafe(repairID);

        Assert.assertTrue(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertTrue(pendingContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(sstable.isPendingRepair(), () -> "Add assertion message");
        Assert.assertFalse(sstable.isRepaired(), () -> "Add assertion message");

        cfs.getCompactionStrategyManager().enable(); // enable compaction to fetch next background task
        AbstractCompactionTask compactionTask = csm.getNextBackgroundTask(FBUtilities.nowInSeconds());
        Assert.assertNotNull(compactionTask, () -> "Add assertion message");
        Assert.assertSame(PendingRepairManager.RepairFinishedCompactionTask.class, compactionTask.getClass(), () -> "Add assertion message");

        // run the compaction
        compactionTask.execute(ActiveCompactionsTracker.NOOP);

        Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(unrepairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");

        // sstable should have pendingRepair cleared, and repairedAt set correctly
        Assert.assertFalse(sstable.isPendingRepair(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(sstable.isRepaired(), () -> "Add assertion message");
        Assert.assertEquals(ActiveRepairService.UNREPAIRED_SSTABLE, sstable.getSSTableMetadata().repairedAt, () -> "Add assertion message");
    }

    @Test
    public void finalizedSessionTransientCleanup()
    {
        Assert.assertTrue(cfs.getLiveSSTables().isEmpty(), () -> "Add assertion message");
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);
        SSTableReader sstable = makeSSTable(true);
        mutateRepaired(sstable, repairID, true);
        csm.handleNotification(new SSTableAddedNotification(Collections.singleton(sstable), null), cfs.getTracker());
        LocalSessionAccessor.finalizeUnsafe(repairID);

        Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertTrue(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertTrue(transientContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(pendingContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(unrepairedContains(sstable), () -> "Add assertion message");

        cfs.getCompactionStrategyManager().enable(); // enable compaction to fetch next background task
        AbstractCompactionTask compactionTask = csm.getNextBackgroundTask(FBUtilities.nowInSeconds());
        Assert.assertNotNull(compactionTask, () -> "Add assertion message");
        Assert.assertSame(PendingRepairManager.RepairFinishedCompactionTask.class, compactionTask.getClass(), () -> "Add assertion message");

        // run the compaction
        compactionTask.execute(ActiveCompactionsTracker.NOOP);

        // Line removed due to Duplicate Assert: Assert.assertTrue(cfs.getLiveSSTables().isEmpty(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
    }

    @Test
    public void failedSessionTransientCleanup()
    {
        Assert.assertTrue(cfs.getLiveSSTables().isEmpty(), () -> "Add assertion message");
        TimeUUID repairID = registerSession(cfs, true, true);
        LocalSessionAccessor.prepareUnsafe(repairID, COORDINATOR, PARTICIPANTS);
        SSTableReader sstable = makeSSTable(true);
        mutateRepaired(sstable, repairID, true);
        csm.handleNotification(new SSTableAddedNotification(Collections.singleton(sstable), null), cfs.getTracker());
        LocalSessionAccessor.failUnsafe(repairID);

        Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertTrue(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertTrue(transientContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(pendingContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertFalse(unrepairedContains(sstable), () -> "Add assertion message");

        cfs.getCompactionStrategyManager().enable(); // enable compaction to fetch next background task
        AbstractCompactionTask compactionTask = csm.getNextBackgroundTask(FBUtilities.nowInSeconds());
        Assert.assertNotNull(compactionTask, () -> "Add assertion message");
        Assert.assertSame(PendingRepairManager.RepairFinishedCompactionTask.class, compactionTask.getClass(), () -> "Add assertion message");

        // run the compaction
        compactionTask.execute(ActiveCompactionsTracker.NOOP);

        Assert.assertFalse(cfs.getLiveSSTables().isEmpty(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(hasPendingStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(hasTransientStrategiesFor(repairID), () -> "Add assertion message");
        Assert.assertFalse(transientContains(sstable), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(pendingContains(sstable), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(repairedContains(sstable), () -> "Add assertion message");
        Assert.assertTrue(unrepairedContains(sstable), () -> "Add assertion message");
    }
}