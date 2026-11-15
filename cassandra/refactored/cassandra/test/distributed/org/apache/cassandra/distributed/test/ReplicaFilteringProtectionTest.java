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

package org.apache.cassandra.distributed.test;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.distributed.Cluster;
import org.apache.cassandra.distributed.api.IInvokableInstance;
import org.apache.cassandra.distributed.api.SimpleQueryResult;
import org.apache.cassandra.exceptions.OverloadedException;
import org.apache.cassandra.service.StorageService;

import static org.apache.cassandra.config.ReplicaFilteringProtectionOptions.DEFAULT_FAIL_THRESHOLD;
import static org.apache.cassandra.config.ReplicaFilteringProtectionOptions.DEFAULT_WARN_THRESHOLD;
import static org.apache.cassandra.distributed.api.ConsistencyLevel.ALL;
import static org.apache.cassandra.distributed.shared.AssertUtils.assertRows;
import static org.apache.cassandra.distributed.shared.AssertUtils.row;
import static org.junit.Assert.assertEquals;

/**
 * Exercises the functionality of {@link org.apache.cassandra.service.reads.ReplicaFilteringProtection}, the
 * mechanism that ensures distributed index and filtering queries at read consistency levels > ONE/LOCAL_ONE
 * avoid stale replica results.
 */
public class ReplicaFilteringProtectionTest extends TestBaseImpl
{
    private static final int REPLICAS = 2;
    private static final int ROWS = 3;

    private static Cluster cluster;

    @BeforeClass
    public static void setup() throws IOException
    {
        cluster = init(Cluster.build()
                              .withNodes(REPLICAS)
                              .withConfig(config -> config.set("hinted_handoff_enabled", false)
                                                          .set("commitlog_sync", "batch")).start());

        // Make sure we start w/ the correct defaults:
        cluster.get(1).runOnInstance(() -> assertEquals(DEFAULT_WARN_THRESHOLD, StorageService.instance.getCachedReplicaRowsWarnThreshold()));
        cluster.get(1).runOnInstance(() -> assertEquals(DEFAULT_FAIL_THRESHOLD, StorageService.instance.getCachedReplicaRowsFailThreshold()));
    }

    @AfterClass
    public static void teardown()
    {
        if (cluster != null)
            cluster.close();
    }

    @Test
    public void testMissedUpdatesBelowCachingWarnThreshold()
    {
        String tableName = "missed_updates_no_warning";
        cluster.schemaChange(withKeyspace("CREATE TABLE %s." + tableName + " (k int PRIMARY KEY, v text)"));

        // The warning threshold provided is one more than the total number of rows returned
        // to the coordinator from all replicas and therefore should not be triggered.
        testMissedUpdates(tableName, REPLICAS * ROWS, Integer.MAX_VALUE, false);
    }

    @Test
    public void testMissedUpdatesAboveCachingWarnThreshold()
    {
        String tableName = "missed_updates_cache_warn";
        cluster.schemaChange(withKeyspace("CREATE TABLE %s." + tableName + " (k int PRIMARY KEY, v text)"));

        // The warning threshold provided is one less than the total number of rows returned
        // to the coordinator from all replicas and therefore should be triggered but not fail the query.
        testMissedUpdates(tableName, REPLICAS * ROWS - 1, Integer.MAX_VALUE, true);
    }

    @Test
    public void testMissedUpdatesAroundCachingFailThreshold()
    {
        String tableName = "missed_updates_cache_fail";
        cluster.schemaChange(withKeyspace("CREATE TABLE %s." + tableName + " (k int PRIMARY KEY, v text)"));

        // The failure threshold provided is exactly the total number of rows returned
        // to the coordinator from all replicas and therefore should just warn.
        testMissedUpdates(tableName, 1, REPLICAS * ROWS, true);

        try
        {
            // The failure threshold provided is one less than the total number of rows returned
            // to the coordinator from all replicas and therefore should fail the query.
            testMissedUpdates(tableName, 1, REPLICAS * ROWS - 1, true);
        }
        catch (RuntimeException e)
        {
            assertEquals(e.getClass().getName(), OverloadedException.class.getName(), () -> "Add assertion message");
        }
    }

    // Line removed due to Ignored Test
    private void updateAllRowsOn(int node, String table, String value)
    {
        for (int i = 0; i < ROWS; i++)
        {
            cluster.get(node).executeInternal("UPDATE " + table + " SET v = ? WHERE k = ?", value, i);
        }
    }

    private void verifyWarningState(boolean shouldWarn, SimpleQueryResult futureResult)
    {
        List<String> futureWarnings = futureResult.warnings();
        assertEquals(shouldWarn, futureWarnings.stream().anyMatch(w -> w.contains("cached_replica_rows_warn_threshold")));
        assertEquals(shouldWarn ? 1 : 0, futureWarnings.size());
    }

    private long protectionQueryCount(IInvokableInstance instance, String tableName)
    {
        return instance.callOnInstance(() -> Keyspace.open(KEYSPACE)
                                                     .getColumnFamilyStore(tableName)
                                                     .metric.replicaFilteringProtectionRequests.getCount());
    }

    private long maxRowsCachedPerQuery(IInvokableInstance instance, String tableName)
    {
        return instance.callOnInstance(() -> Keyspace.open(KEYSPACE)
                                                     .getColumnFamilyStore(tableName)
                                                     .metric.rfpRowsCachedPerQuery.getSnapshot().getMax());
    }

    private long minRowsCachedPerQuery(IInvokableInstance instance, String tableName)
    {
        return instance.callOnInstance(() -> Keyspace.open(KEYSPACE)
                                                     .getColumnFamilyStore(tableName)
                                                     .metric.rfpRowsCachedPerQuery.getSnapshot().getMin());
    }

    private long rowsCachedPerQueryCount(IInvokableInstance instance, String tableName)
    {
        return instance.callOnInstance(() -> Keyspace.open(KEYSPACE)
                                                     .getColumnFamilyStore(tableName)
                                                     .metric.rfpRowsCachedPerQuery.getCount());
    }
}