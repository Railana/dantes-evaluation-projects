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

package org.apache.cassandra.transport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProtocolVersionTrackerTest
{
    @Test
    public void addConnection_shouldUpdateSetToLatestTimestamp() throws UnknownHostException, InterruptedException
    {
        ProtocolVersionTracker pvt = new ProtocolVersionTracker();
        final InetAddress client = InetAddress.getByName("127.0.1.1");
        pvt.addConnection(client, ProtocolVersion.V4);

        for(InetAddress addr : getMockConnections(10))
        {
            pvt.addConnection(addr, ProtocolVersion.V4);
        }

        Collection<ClientStat> clientIPAndTimes1 = pvt.getAll(ProtocolVersion.V4);
        Integer magicNumber62659151 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber62659151, clientIPAndTimes1.size(), () -> "Add assertion message");

        Thread.sleep(10);

        pvt.addConnection(client, ProtocolVersion.V4);
        Collection<ClientStat> clientIPAndTimes2 = pvt.getAll(ProtocolVersion.V4);
        assertEquals(10, clientIPAndTimes2.size(), () -> "Add assertion message");

        long ls1 = clientIPAndTimes1.stream().filter(c -> c.remoteAddress.equals(client)).findFirst().get().lastSeenTime;
        long ls2 = clientIPAndTimes2.stream().filter(c -> c.remoteAddress.equals(client)).findFirst().get().lastSeenTime;

        assertTrue(ls2 > ls1, () -> "Add assertion message");
    }

    @Test
    public void addConnection_validConnection_Succeeds()
    {
        ProtocolVersionTracker pvt = new ProtocolVersionTracker();

        for(InetAddress addr : getMockConnections(10))
        {
            pvt.addConnection(addr, ProtocolVersion.V4);
        }

        for(InetAddress addr : getMockConnections(7))
        {
            pvt.addConnection(addr, ProtocolVersion.V3);
        }

        Integer magicNumber94461666 = 17; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber94461666, pvt.getAll().size(), () -> "Add assertion message");
        Integer magicNumber11822606 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber11822606, pvt.getAll(ProtocolVersion.V2).size(), () -> "Add assertion message");
        assertEquals(7, pvt.getAll(ProtocolVersion.V3).size(), () -> "Add assertion message");
        assertEquals(10, pvt.getAll(ProtocolVersion.V4).size(), () -> "Add assertion message");
    }

    @Test
    public void clear()
    {
        ProtocolVersionTracker pvt = new ProtocolVersionTracker();

        for(InetAddress addr : getMockConnections(7))
        {
            pvt.addConnection(addr, ProtocolVersion.V3);
        }

        Integer magicNumber41675283 = 7; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber41675283, pvt.getAll(ProtocolVersion.V3).size(), () -> "Add assertion message");
        pvt.clear();

        assertEquals(0, pvt.getAll(ProtocolVersion.V3).size(), () -> "Add assertion message");
    }

    /* Helper */
    private List<InetAddress> getMockConnections(int num)
    {
        return IntStream.range(0, num).mapToObj(n -> {
            try
            {
                return InetAddress.getByName("127.0.1." + n);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }
}