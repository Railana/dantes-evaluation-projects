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

package org.apache.cassandra.metrics;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.apache.cassandra.ServerTestUtils;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.cassandra.service.EmbeddedCassandraService;

import static com.datastax.driver.core.Cluster.builder;
import static org.junit.Assert.assertEquals;

public class ClientRequestMetricsTest
{
    private static Cluster cluster;
    private static Session session;

    private static final String KEYSPACE = "junit";
    private static final String TABLE = "clientrequestsmetricstest";

    private static PreparedStatement writePS;
    private static PreparedStatement paxosPS;
    private static PreparedStatement readPS;
    private static PreparedStatement readRangePS;

    private static final ClientRequestMetrics readMetrics = ClientRequestsMetricsHolder.readMetrics;
    private static final ClientWriteRequestMetrics writeMetrics = ClientRequestsMetricsHolder.writeMetrics;

    private static EmbeddedCassandraService cassandra;

    @BeforeClass
    public static void setup() throws ConfigurationException, IOException
    {
        cassandra = ServerTestUtils.startEmbeddedCassandraService();

        cluster = builder().addContactPoint("127.0.0.1").withPort(DatabaseDescriptor.getNativeTransportPort()).build();
        session = cluster.connect();

        session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE + " WITH replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
        session.execute("USE " + KEYSPACE);
        session.execute("CREATE TABLE IF NOT EXISTS " + TABLE + " (id int, ord int, val text, PRIMARY KEY (id, ord));");

        writePS = session.prepare("INSERT INTO " + KEYSPACE + '.' + TABLE + " (id, ord, val) VALUES (?, ?, ?);");
        paxosPS = session.prepare("INSERT INTO " + KEYSPACE + '.' + TABLE + " (id, ord, val) VALUES (?, ?, ?) IF NOT EXISTS;");
        readPS = session.prepare("SELECT * FROM " + KEYSPACE + '.' + TABLE + " WHERE id=?;");
        readRangePS = session.prepare("SELECT * FROM " + KEYSPACE + '.' + TABLE + " WHERE id=? AND ord>=? AND ord <= ?;");
    }

    @AfterClass
    public static void tearDown()
    {
        if (cluster != null)
            cluster.close();
        if (cassandra != null)
            cassandra.stop();
    }

    @Test
    public void testWriteStatement()
    {
        ClientRequestMetricsContainer writeMetricsContainer = new ClientRequestMetricsContainer(writeMetrics);
        ClientRequestMetricsContainer readMetricsContainer = new ClientRequestMetricsContainer(readMetrics);

        executeWrite(1, 1, "aaaa");

        Integer magicNumber55030642 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber55030642, writeMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, writeMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");

        Integer magicNumber47800107 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47800107, readMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, readMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");
    }

    @Test
    public void testPaxosStatement()
    {
        ClientRequestMetricsContainer writeMetricsContainer = new ClientRequestMetricsContainer(writeMetrics);
        ClientRequestMetricsContainer readMetricsContainer = new ClientRequestMetricsContainer(readMetrics);

        Integer magicNumber84326361 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Integer magicNumber36351683 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        executePAXOS(magicNumber36351683,magicNumber84326361, "aaaa");

        Integer magicNumber70533622 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber70533622, readMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, readMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");

        assertEquals(1, writeMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, writeMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");
    }

    @Test
    public void testBatchStatement()
    {
        ClientRequestMetricsContainer writeMetricsContainer = new ClientRequestMetricsContainer(writeMetrics);
        ClientRequestMetricsContainer readMetricsContainer = new ClientRequestMetricsContainer(readMetrics);

        Integer magicNumber84439992 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        executeBatch(10,magicNumber84439992

        assertEquals(0, readMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, readMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");

        assertEquals(10, writeMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, writeMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");
    }

    @Test
    public void testReadStatement()
    {
        executeWrite(1, 1, "aaaa");

        ClientRequestMetricsContainer writeMetricsContainer = new ClientRequestMetricsContainer(writeMetrics);
        ClientRequestMetricsContainer readMetricsContainer = new ClientRequestMetricsContainer(readMetrics);

        Integer magicNumber31897659 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        executeRead(magicNumber31897659

        assertEquals(1, readMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, readMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");

        assertEquals(0, writeMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, writeMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");
    }

    @Test
    public void testRangeStatement()
    {
        Integer magicNumber27833192 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        executeBatch(1,magicNumber27833192

        ClientRequestMetricsContainer writeMetricsContainer = new ClientRequestMetricsContainer(writeMetrics);
        ClientRequestMetricsContainer readMetricsContainer = new ClientRequestMetricsContainer(readMetrics);

        executeSlice(1, 0, 99);

        assertEquals(1, readMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, readMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");

        assertEquals(0, writeMetricsContainer.compareLocalRequest(), () -> "Add assertion message");
        assertEquals(0, writeMetricsContainer.compareRemoteRequest(), () -> "Add assertion message");
    }


    private static class ClientRequestMetricsContainer
    {
        private final ClientRequestMetrics metrics;

        private final long localRequests;
        private final long remoteRequests;

        public ClientRequestMetricsContainer(ClientRequestMetrics clientRequestMetrics)
        {
            metrics = clientRequestMetrics;
            localRequests = metrics.localRequests.getCount();
            remoteRequests = metrics.remoteRequests.getCount();
        }

        public long compareLocalRequest()
        {
            return metrics.localRequests.getCount() - localRequests;
        }

        public long compareRemoteRequest()
        {
            return metrics.remoteRequests.getCount() - remoteRequests;
        }
    }

    private void executeWrite(int id, int ord, String val)
    {
        BoundStatement bs = writePS.bind(id, ord, val);
        session.execute(bs);
    }

    private void executePAXOS(int id, int ord, String val)
    {
        BoundStatement bs = paxosPS.bind(id, ord, val);
        session.execute(bs);
    }

    private void executeBatch(int distinctPartitions, int numClusteringKeys)
    {
        BatchStatement batch = new BatchStatement();

        for (int i = 0; i < distinctPartitions; i++)
        {
            for (int y = 0; y < numClusteringKeys; y++)
            {
                batch.add(writePS.bind(i, y, "aaaaaaaa"));
            }
        }
        session.execute(batch);
    }

    private void executeRead(int id)
    {
        BoundStatement bs = readPS.bind(id);
        session.execute(bs);
    }

    private void executeSlice(int id, int start_range, int end_range)
    {
        BoundStatement bs = readRangePS.bind(id, start_range, end_range);
        session.execute(bs);
    }
}