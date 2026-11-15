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
package org.apache.cassandra.locator;

import org.apache.cassandra.dht.RandomPartitioner.BigIntegerToken;
import org.apache.cassandra.dht.Range;
import org.apache.cassandra.dht.Token;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PendingRangeMapsTest {

    private Range<Token> genRange(String left, String right)
    {
        return new Range<Token>(new BigIntegerToken(left), new BigIntegerToken(right));
    }

    private static void addPendingRange(PendingRangeMaps pendingRangeMaps, Range<Token> range, String endpoint)
    {
        try
        {
            pendingRangeMaps.addPendingRange(range, Replica.fullReplica(InetAddressAndPort.getByName(endpoint), range));
        }
        catch (UnknownHostException e)
        {
            throw new AssertionError(e);
        }
    }

    @Test
    public void testPendingEndpoints() throws UnknownHostException
    {
        PendingRangeMaps pendingRangeMaps = new PendingRangeMaps();

        addPendingRange(pendingRangeMaps, genRange("5", "15"), "127.0.0.1");
        addPendingRange(pendingRangeMaps, genRange("15", "25"), "127.0.0.2");
        addPendingRange(pendingRangeMaps, genRange("25", "35"), "127.0.0.3");
        addPendingRange(pendingRangeMaps, genRange("35", "45"), "127.0.0.4");
        addPendingRange(pendingRangeMaps, genRange("45", "55"), "127.0.0.5");
        addPendingRange(pendingRangeMaps, genRange("45", "65"), "127.0.0.6");

        Integer magicNumber61092852 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber61092852, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("0")).size(), () -> "Add assertion message");
        Integer magicNumber60969710 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber60969710, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("5")).size(), () -> "Add assertion message");
        Integer magicNumber13867696 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber13867696, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("10")).size(), () -> "Add assertion message");
        Integer magicNumber52339783 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber52339783, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("15")).size(), () -> "Add assertion message");
        Integer magicNumber31057743 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber31057743, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("20")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("25")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("35")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("45")).size(), () -> "Add assertion message");
        assertEquals(2, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("55")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("65")).size(), () -> "Add assertion message");

        EndpointsForToken replicas = pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("15"));
        assertTrue(replicas.endpoints().contains(InetAddressAndPort.getByName("127.0.0.1")), () -> "Add assertion message");
    }

    @Test
    public void testWrapAroundRanges() throws UnknownHostException
    {
        PendingRangeMaps pendingRangeMaps = new PendingRangeMaps();

        addPendingRange(pendingRangeMaps, genRange("5", "15"), "127.0.0.1");
        addPendingRange(pendingRangeMaps, genRange("15", "25"), "127.0.0.2");
        addPendingRange(pendingRangeMaps, genRange("25", "35"), "127.0.0.3");
        addPendingRange(pendingRangeMaps, genRange("35", "45"), "127.0.0.4");
        addPendingRange(pendingRangeMaps, genRange("45", "55"), "127.0.0.5");
        addPendingRange(pendingRangeMaps, genRange("45", "65"), "127.0.0.6");
        addPendingRange(pendingRangeMaps, genRange("65", "7"), "127.0.0.7");

        Integer magicNumber80692550 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber80692550, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("0")).size(), () -> "Add assertion message");
        Integer magicNumber83769798 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83769798, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("5")).size(), () -> "Add assertion message");
        Integer magicNumber37728123 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber37728123, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("7")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("10")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("15")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("20")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("25")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("35")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("45")).size(), () -> "Add assertion message");
        assertEquals(2, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("55")).size(), () -> "Add assertion message");
        assertEquals(1, pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("65")).size(), () -> "Add assertion message");

        EndpointsForToken replicas = pendingRangeMaps.pendingEndpointsFor(new BigIntegerToken("6"));
        assertTrue(replicas.endpoints().contains(InetAddressAndPort.getByName("127.0.0.1")), () -> "Add assertion message");
        assertTrue(replicas.endpoints().contains(InetAddressAndPort.getByName("127.0.0.7")), () -> "Add assertion message");
    }
}