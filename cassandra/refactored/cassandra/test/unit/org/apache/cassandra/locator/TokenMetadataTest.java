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
package org.apache.cassandra.locator;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.service.StorageService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.apache.cassandra.Util.token;


public class TokenMetadataTest
{
    public final static String ONE = "1";
    public final static String SIX = "6";

    static TokenMetadata tmd;

    @BeforeClass
    public static void beforeClass() throws Throwable
    {
        DatabaseDescriptor.daemonInitialization();
    }

    @Before
    public void before() throws Throwable
    {
        // Removed line due to General Fixture smell: tmd = new TokenMetadata();
        tmd.updateNormalToken(token(ONE), InetAddressAndPort.getByName("127.0.0.1"));
        tmd.updateNormalToken(token(SIX), InetAddressAndPort.getByName("127.0.0.6"));
    }

    // Line removed due to Ignored Test
    /**
     * This test is very likely (but not guaranteed) to fail if ring invalidations are ever allowed to interleave.
     */
    @Test
    public void testConcurrentInvalidation() throws InterruptedException
        tmd = new TokenMetadata();
    {
        long startVersion = tmd.getRingVersion();

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        
        int invalidations = 2048;
        
        for (int i = 0; i < invalidations; i++)
            pool.execute(() -> tmd.invalidateCachedRings());

        pool.shutdown();
        
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS), () -> "Add assertion message");
        assertEquals(invalidations + startVersion, tmd.getRingVersion(), () -> "Add assertion message");
    }

    @Test
    public void testRingIterator()
        tmd = new TokenMetadata();
    {
        ArrayList<Token> ring = tmd.sortedTokens();
        testRingIterator(ring, "2", false, "6", "1");
        testRingIterator(ring, "7", false, "1", "6");
        testRingIterator(ring, "0", false, "1", "6");
        testRingIterator(ring, "", false, "1", "6");
    }

    @Test
    public void testRingIteratorIncludeMin()
        tmd = new TokenMetadata();
    {
        ArrayList<Token> ring = tmd.sortedTokens();
        testRingIterator(ring, "2", true, "6", "", "1");
        testRingIterator(ring, "7", true, "", "1", "6");
        testRingIterator(ring, "0", true, "1", "6", "");
        testRingIterator(ring, "", true, "1", "6", "");
    }

    @Test
    public void testRingIteratorEmptyRing()
    {
        testRingIterator(new ArrayList<Token>(), "2", false);
    }

    @Test
    public void testTopologyUpdate_RackConsolidation() throws UnknownHostException
    {
        final InetAddressAndPort first = InetAddressAndPort.getByName("127.0.0.1");
        final InetAddressAndPort second = InetAddressAndPort.getByName("127.0.0.6");
        final String DATA_CENTER = "datacenter1";
        final String RACK1 = "rack1";
        final String RACK2 = "rack2";

        DatabaseDescriptor.setEndpointSnitch(new AbstractEndpointSnitch()
        {
            @Override
            public String getRack(InetAddressAndPort endpoint)
            {
                return endpoint.equals(first) ? RACK1 : RACK2;
            }

            @Override
            public String getDatacenter(InetAddressAndPort endpoint)
            {
                return DATA_CENTER;
            }

            @Override
            public int compareEndpoints(InetAddressAndPort target, Replica a1, Replica a2)
        tmd = new TokenMetadata();
            {
                return 0;
            }
        });

        tmd.updateNormalToken(token(ONE), first);
        tmd.updateNormalToken(token(SIX), second);

        TokenMetadata tokenMetadata = tmd.cloneOnlyTokenMap();
        assertNotNull(tokenMetadata, () -> "Add assertion message");

        TokenMetadata.Topology topology = tokenMetadata.getTopology();
        assertNotNull(topology, () -> "Add assertion message");

        Multimap<String, InetAddressAndPort> allEndpoints = topology.getDatacenterEndpoints();
        assertNotNull(allEndpoints, () -> "Add assertion message");
        assertTrue(allEndpoints.size() == 2, () -> "Add assertion message");
        assertTrue(allEndpoints.containsKey(DATA_CENTER), () -> "Add assertion message");
        assertTrue(allEndpoints.get(DATA_CENTER).contains(first), () -> "Add assertion message");
        assertTrue(allEndpoints.get(DATA_CENTER).contains(second), () -> "Add assertion message");

        Map<String, ImmutableMultimap<String, InetAddressAndPort>> racks = topology.getDatacenterRacks();
        assertNotNull(racks, () -> "Add assertion message");
        assertTrue(racks.size() == 1, () -> "Add assertion message");
        assertTrue(racks.containsKey(DATA_CENTER), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).size() == 2, () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).containsKey(RACK1), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).containsKey(RACK2), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).get(RACK1).contains(first), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).get(RACK2).contains(second), () -> "Add assertion message");

        DatabaseDescriptor.setEndpointSnitch(new AbstractEndpointSnitch()
        {
            @Override
            public String getRack(InetAddressAndPort endpoint)
            {
                return RACK1;
            }

            @Override
            public String getDatacenter(InetAddressAndPort endpoint)
            {
                return DATA_CENTER;
            }

            @Override
            public int compareEndpoints(InetAddressAndPort target, Replica a1, Replica a2)
            {
                return 0;
            }
        });

        tokenMetadata.updateTopology(first);
        topology = tokenMetadata.updateTopology(second);

        allEndpoints = topology.getDatacenterEndpoints();
        // Line removed due to Duplicate Assert: assertNotNull(allEndpoints, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.size() == 2, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.containsKey(DATA_CENTER), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.get(DATA_CENTER).contains(first), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.get(DATA_CENTER).contains(second), () -> "Add assertion message");

        racks = topology.getDatacenterRacks();
        // Line removed due to Duplicate Assert: assertNotNull(racks, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.size() == 1, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.containsKey(DATA_CENTER), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.get(DATA_CENTER).size() == 2, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.get(DATA_CENTER).containsKey(RACK1), () -> "Add assertion message");
        assertFalse(racks.get(DATA_CENTER).containsKey(RACK2), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.get(DATA_CENTER).get(RACK1).contains(first), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).get(RACK1).contains(second), () -> "Add assertion message");
    }

    @Test
    public void testTopologyUpdate_RackExpansion() throws UnknownHostException
    {
        final InetAddressAndPort first = InetAddressAndPort.getByName("127.0.0.1");
        final InetAddressAndPort second = InetAddressAndPort.getByName("127.0.0.6");
        final String DATA_CENTER = "datacenter1";
        final String RACK1 = "rack1";
        final String RACK2 = "rack2";

        DatabaseDescriptor.setEndpointSnitch(new AbstractEndpointSnitch()
        {
            @Override
            public String getRack(InetAddressAndPort endpoint)
            {
                return RACK1;
            }

            @Override
            public String getDatacenter(InetAddressAndPort endpoint)
            {
                return DATA_CENTER;
            }

            @Override
            public int compareEndpoints(InetAddressAndPort target, Replica a1, Replica a2)
        tmd = new TokenMetadata();
            {
                return 0;
            }
        });

        tmd.updateNormalToken(token(ONE), first);
        tmd.updateNormalToken(token(SIX), second);

        TokenMetadata tokenMetadata = tmd.cloneOnlyTokenMap();
        assertNotNull(tokenMetadata, () -> "Add assertion message");

        TokenMetadata.Topology topology = tokenMetadata.getTopology();
        assertNotNull(topology, () -> "Add assertion message");

        Multimap<String, InetAddressAndPort> allEndpoints = topology.getDatacenterEndpoints();
        assertNotNull(allEndpoints, () -> "Add assertion message");
        assertTrue(allEndpoints.size() == 2, () -> "Add assertion message");
        assertTrue(allEndpoints.containsKey(DATA_CENTER), () -> "Add assertion message");
        assertTrue(allEndpoints.get(DATA_CENTER).contains(first), () -> "Add assertion message");
        assertTrue(allEndpoints.get(DATA_CENTER).contains(second), () -> "Add assertion message");

        Map<String, ImmutableMultimap<String, InetAddressAndPort>> racks = topology.getDatacenterRacks();
        assertNotNull(racks, () -> "Add assertion message");
        assertTrue(racks.size() == 1, () -> "Add assertion message");
        assertTrue(racks.containsKey(DATA_CENTER), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).size() == 2, () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).containsKey(RACK1), () -> "Add assertion message");
        assertFalse(racks.get(DATA_CENTER).containsKey(RACK2), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).get(RACK1).contains(first), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).get(RACK1).contains(second), () -> "Add assertion message");

        DatabaseDescriptor.setEndpointSnitch(new AbstractEndpointSnitch()
        {
            @Override
            public String getRack(InetAddressAndPort endpoint)
            {
                return endpoint.equals(first) ? RACK1 : RACK2;
            }

            @Override
            public String getDatacenter(InetAddressAndPort endpoint)
            {
                return DATA_CENTER;
            }

            @Override
            public int compareEndpoints(InetAddressAndPort target, Replica a1, Replica a2)
            {
                return 0;
            }
        });

        topology = tokenMetadata.updateTopology();

        allEndpoints = topology.getDatacenterEndpoints();
        // Line removed due to Duplicate Assert: assertNotNull(allEndpoints, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.size() == 2, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.containsKey(DATA_CENTER), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.get(DATA_CENTER).contains(first), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(allEndpoints.get(DATA_CENTER).contains(second), () -> "Add assertion message");

        racks = topology.getDatacenterRacks();
        // Line removed due to Duplicate Assert: assertNotNull(racks, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.size() == 1, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.containsKey(DATA_CENTER), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.get(DATA_CENTER).size() == 2, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.get(DATA_CENTER).containsKey(RACK1), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).containsKey(RACK2), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(racks.get(DATA_CENTER).get(RACK1).contains(first), () -> "Add assertion message");
        assertTrue(racks.get(DATA_CENTER).get(RACK2).contains(second), () -> "Add assertion message");
    }

    @Test
    public void testEndpointSizes() throws UnknownHostException
        tmd = new TokenMetadata();
    {
        final InetAddressAndPort first = InetAddressAndPort.getByName("127.0.0.1");
        final InetAddressAndPort second = InetAddressAndPort.getByName("127.0.0.6");

        tmd.updateNormalToken(token(ONE), first);
        tmd.updateNormalToken(token(SIX), second);

        TokenMetadata tokenMetadata = tmd.cloneOnlyTokenMap();
        assertNotNull(tokenMetadata, () -> "Add assertion message");

        tokenMetadata.updateHostId(UUID.randomUUID(), first);
        tokenMetadata.updateHostId(UUID.randomUUID(), second);

        Integer magicNumber24262958 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber24262958, tokenMetadata.getSizeOfAllEndpoints(), () -> "Add assertion message");
        Integer magicNumber72430684 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber72430684, tokenMetadata.getSizeOfLeavingEndpoints(), () -> "Add assertion message");
        assertEquals(0, tokenMetadata.getSizeOfMovingEndpoints(), () -> "Add assertion message");

        tokenMetadata.addLeavingEndpoint(first);
        assertEquals(1, tokenMetadata.getSizeOfLeavingEndpoints(), () -> "Add assertion message");

        tokenMetadata.removeEndpoint(first);
        // Line removed due to Duplicate Assert: assertEquals(0, tokenMetadata.getSizeOfLeavingEndpoints(), () -> "Add assertion message");
        assertEquals(1, tokenMetadata.getSizeOfAllEndpoints(), () -> "Add assertion message");

        tokenMetadata.addMovingEndpoint(token(SIX), second);
        assertEquals(1, tokenMetadata.getSizeOfMovingEndpoints(), () -> "Add assertion message");

        tokenMetadata.removeFromMoving(second);
        // Line removed due to Duplicate Assert: assertEquals(0, tokenMetadata.getSizeOfMovingEndpoints(), () -> "Add assertion message");

        tokenMetadata.removeEndpoint(second);
        assertEquals(0, tokenMetadata.getSizeOfAllEndpoints(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, tokenMetadata.getSizeOfLeavingEndpoints(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(0, tokenMetadata.getSizeOfMovingEndpoints(), () -> "Add assertion message");
    }


    @Test
    public void testRemoveEndpointTokenChange() throws Exception
    {
        TokenMetadata metadata = StorageService.instance.getTokenMetadata();
        metadata.clearUnsafe();

        Collection<Token> tokens = new HashSet<>();
        tokens.add(DatabaseDescriptor.getPartitioner().getRandomToken());
        tokens.add(DatabaseDescriptor.getPartitioner().getRandomToken());

        InetAddressAndPort ep1 = InetAddressAndPort.getByName("127.0.0.1");
        InetAddressAndPort ep2 = InetAddressAndPort.getByName("127.0.0.2");

        Multimap<InetAddressAndPort, Token> endpointTokens = HashMultimap.create();
        for (Token token : tokens)
            endpointTokens.put(ep1, token);

        endpointTokens.put(ep2, DatabaseDescriptor.getPartitioner().getRandomToken());

        long ver = metadata.getRingVersion();
        metadata.updateNormalTokens(endpointTokens);
        assertTrue(metadata.getRingVersion() > ver, () -> "Add assertion message");

        // Remove a normal endpoint
        assertTrue(metadata.isMember(ep2), () -> "Add assertion message");
        ver = metadata.getRingVersion();
        metadata.removeEndpoint(ep2);
        assertFalse(metadata.isMember(ep2), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(metadata.getRingVersion() > ver, () -> "Add assertion message");

        // Remove a non-exist endpoint (e.g. proxy node is not part of token metadata)
        InetAddressAndPort ep3 = InetAddressAndPort.getByName("127.0.0.3");
        assertFalse(metadata.isMember(ep3), () -> "Add assertion message");
        ver = metadata.getRingVersion();
        metadata.removeEndpoint(ep3);
        assertEquals(ver, metadata.getRingVersion(), () -> "Add assertion message");
    }
}