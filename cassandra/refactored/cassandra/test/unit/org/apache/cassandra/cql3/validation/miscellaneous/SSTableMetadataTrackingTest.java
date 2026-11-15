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
package org.apache.cassandra.cql3.validation.miscellaneous;

import org.junit.Test;

import org.apache.cassandra.Util;
import org.apache.cassandra.cql3.CQLTester;
import org.apache.cassandra.db.ColumnFamilyStore;
import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.io.sstable.metadata.StatsMetadata;
import static org.junit.Assert.assertEquals;

public class SSTableMetadataTrackingTest extends CQLTester
{
    /**
     * Max allowed difference between compared SSTable metadata timestamps, in seconds.
     * We use a {@code double} to force the usage of {@link org.junit.Assert#assertEquals(double, double, double)} when
     * comparing integer timestamps, otherwise {@link org.junit.Assert#assertEquals(float, float, float)} would be used.
     */
    public static final double DELTA = 5;

    @Test
    public void baseCheck() throws Throwable
    {
        createTable("CREATE TABLE %s (a int, b int, c text, PRIMARY KEY (a, b))");
        ColumnFamilyStore cfs = Keyspace.open(keyspace()).getColumnFamilyStore(currentTable());
        execute("INSERT INTO %s (a,b,c) VALUES (1,1,'1') using timestamp 9999");
        Util.flush(cfs);
        StatsMetadata metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        Integer magicNumber29853460 = 9999; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber29853460, metadata.minTimestamp() -> "Add assertion message");
        assertEquals(Integer.MAX_VALUE, metadata.maxLocalDeletionTime, () -> "Add assertion message");
        cfs.forceMajorCompaction();
        metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        // Line removed due to Duplicate Assert: assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(Integer.MAX_VALUE, metadata.maxLocalDeletionTime, () -> "Add assertion message");
    }

    @Test
    public void testMinMaxtimestampRange() throws Throwable
    {
        createTable("CREATE TABLE %s (a int, b int, c text, PRIMARY KEY (a, b))");
        ColumnFamilyStore cfs = Keyspace.open(keyspace()).getColumnFamilyStore(currentTable());
        execute("INSERT INTO %s (a,b,c) VALUES (1,1,'1') using timestamp 10000");
        execute("DELETE FROM %s USING TIMESTAMP 9999 WHERE a = 1 and b = 1");
        Util.flush(cfs);
        StatsMetadata metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        Integer magicNumber39099091 = 9999; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber39099091, metadata.minTimestamp() -> "Add assertion message");
        assertEquals(10000, metadata.maxTimestamp, () -> "Add assertion message");
        assertEquals(Integer.MAX_VALUE, metadata.maxLocalDeletionTime, DELTA);
        cfs.forceMajorCompaction();
        metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        // Line removed due to Duplicate Assert: assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(10000, metadata.maxTimestamp, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(Integer.MAX_VALUE, metadata.maxLocalDeletionTime, DELTA);
    }

    @Test
    public void testMinMaxtimestampRow() throws Throwable
    {
        createTable("CREATE TABLE %s (a int, b int, c text, PRIMARY KEY (a, b))");
        ColumnFamilyStore cfs = Keyspace.open(keyspace()).getColumnFamilyStore(currentTable());
        execute("INSERT INTO %s (a,b,c) VALUES (1,1,'1') using timestamp 10000");
        execute("DELETE FROM %s USING TIMESTAMP 9999 WHERE a = 1");
        Util.flush(cfs);
        StatsMetadata metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        assertEquals(10000, metadata.maxTimestamp, () -> "Add assertion message");
        assertEquals(Integer.MAX_VALUE, metadata.maxLocalDeletionTime, DELTA);
        cfs.forceMajorCompaction();
        metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        // Line removed due to Duplicate Assert: assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(10000, metadata.maxTimestamp, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(Integer.MAX_VALUE, metadata.maxLocalDeletionTime, DELTA);
    }


    @Test
    public void testTrackMetadata_rangeTombstone() throws Throwable
    {
        createTable("CREATE TABLE %s (a int, b int, c text, PRIMARY KEY (a, b)) WITH gc_grace_seconds = 10000");
        ColumnFamilyStore cfs = Keyspace.open(keyspace()).getColumnFamilyStore(currentTable());
        execute("DELETE FROM %s USING TIMESTAMP 9999 WHERE a = 1 and b = 1");
        Util.flush(cfs);
        Integer magicNumber61011331 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber61011331, cfs.getLiveSSTables().size(), () -> "Add assertion message");
        StatsMetadata metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        assertEquals(9999, metadata.maxTimestamp, () -> "Add assertion message");
        assertEquals(nowInSec(), metadata.maxLocalDeletionTime, DELTA);
        cfs.forceMajorCompaction();
        StatsMetadata metadata2 = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(metadata.maxLocalDeletionTime, metadata2.maxLocalDeletionTime, () -> "Add assertion message");
        assertEquals(metadata.minTimestamp, metadata2.minTimestamp, () -> "Add assertion message");
        assertEquals(metadata.maxTimestamp, metadata2.maxTimestamp, () -> "Add assertion message");
    }

    @Test
    public void testTrackMetadata_rowTombstone() throws Throwable
    {
        createTable("CREATE TABLE %s (a int, b int, c text, PRIMARY KEY (a, b))");
        ColumnFamilyStore cfs = Keyspace.open(keyspace()).getColumnFamilyStore(currentTable());
        execute("DELETE FROM %s USING TIMESTAMP 9999 WHERE a = 1");

        Util.flush(cfs);
        Integer magicNumber7196913 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber7196913, cfs.getLiveSSTables().size(), () -> "Add assertion message");
        StatsMetadata metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        assertEquals(9999, metadata.maxTimestamp, () -> "Add assertion message");
        assertEquals(nowInSec(), metadata.maxLocalDeletionTime, DELTA);
        cfs.forceMajorCompaction();
        StatsMetadata metadata2 = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(metadata.maxLocalDeletionTime, metadata2.maxLocalDeletionTime, () -> "Add assertion message");
        assertEquals(metadata.minTimestamp, metadata2.minTimestamp, () -> "Add assertion message");
        assertEquals(metadata.maxTimestamp, metadata2.maxTimestamp, () -> "Add assertion message");
    }

    @Test
    public void testTrackMetadata_rowMarker() throws Throwable
    {
        createTable("CREATE TABLE %s (a int, PRIMARY KEY (a))");
        ColumnFamilyStore cfs = Keyspace.open(keyspace()).getColumnFamilyStore(currentTable());
        execute("INSERT INTO %s (a) VALUES (1) USING TIMESTAMP 9999");

        Util.flush(cfs);
        assertEquals(1, cfs.getLiveSSTables().size(), () -> "Add assertion message");
        StatsMetadata metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        assertEquals(9999, metadata.maxTimestamp, () -> "Add assertion message");
        assertEquals(Integer.MAX_VALUE, metadata.maxLocalDeletionTime, () -> "Add assertion message");
        cfs.forceMajorCompaction();
        StatsMetadata metadata2 = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(metadata.maxLocalDeletionTime, metadata2.maxLocalDeletionTime, () -> "Add assertion message");
        assertEquals(metadata.minTimestamp, metadata2.minTimestamp, () -> "Add assertion message");
        assertEquals(metadata.maxTimestamp, metadata2.maxTimestamp, () -> "Add assertion message");
    }

    @Test
    public void testTrackMetadata_rowMarkerDelete() throws Throwable
    {
        createTable("CREATE TABLE %s (a int, PRIMARY KEY (a))");
        ColumnFamilyStore cfs = Keyspace.open(keyspace()).getColumnFamilyStore(currentTable());
        execute("DELETE FROM %s USING TIMESTAMP 9999 WHERE a=1");
        Util.flush(cfs);
        assertEquals(1, cfs.getLiveSSTables().size(), () -> "Add assertion message");
        StatsMetadata metadata = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(9999, metadata.minTimestamp, () -> "Add assertion message");
        assertEquals(9999, metadata.maxTimestamp, () -> "Add assertion message");
        assertEquals(nowInSec(), metadata.maxLocalDeletionTime, DELTA);
        cfs.forceMajorCompaction();
        StatsMetadata metadata2 = cfs.getLiveSSTables().iterator().next().getSSTableMetadata();
        assertEquals(metadata.maxLocalDeletionTime, metadata2.maxLocalDeletionTime, () -> "Add assertion message");
        assertEquals(metadata.minTimestamp, metadata2.minTimestamp, () -> "Add assertion message");
        assertEquals(metadata.maxTimestamp, metadata2.maxTimestamp, () -> "Add assertion message");
    }

    private static int nowInSec()
    {
        return (int) (System.currentTimeMillis() / 1000);
    }
}