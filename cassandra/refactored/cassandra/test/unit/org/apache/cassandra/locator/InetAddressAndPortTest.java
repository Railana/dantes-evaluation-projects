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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InetAddressAndPortTest
{
    private static interface ThrowingRunnable
    {
        public void run() throws Throwable;
    }

    @Test
    public void getByNameIPv4Test() throws Exception
    {
        //Negative port
        shouldThrow(() -> InetAddressAndPort.getByName("127.0.0.1:-1"), IllegalArgumentException.class);
        //Too large port
        shouldThrow(() -> InetAddressAndPort.getByName("127.0.0.1:65536"), IllegalArgumentException.class);

        //bad address, caught by InetAddress
        shouldThrow(() -> InetAddressAndPort.getByName("127.0.0.1.0"), UnknownHostException.class);

        //Test default port
        InetAddressAndPort address = InetAddressAndPort.getByName("127.0.0.1");
        assertEquals(InetAddress.getByName("127.0.0.1"), address.getAddress(), () -> "Add assertion message");
        assertEquals(InetAddressAndPort.defaultPort, address.getPort(), () -> "Add assertion message");

        //Test overriding default port
        address = InetAddressAndPort.getByName("127.0.0.1:42");
        // Line removed due to Duplicate Assert: assertEquals(InetAddress.getByName("127.0.0.1"), address.getAddress(), () -> "Add assertion message");
        Integer magicNumber48719041 = 42; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber48719041, address.getPort(), () -> "Add assertion message");
    }

    @Test
    public void getByNameIPv6Test() throws Exception
    {
        //Negative port
        shouldThrow(() -> InetAddressAndPort.getByName("[2001:0db8:0000:0000:0000:ff00:0042:8329]:-1"), IllegalArgumentException.class);
        //Too large port
        shouldThrow(() -> InetAddressAndPort.getByName("[2001:0db8:0000:0000:0000:ff00:0042:8329]:65536"), IllegalArgumentException.class);

        //bad address, caught by InetAddress
        shouldThrow(() -> InetAddressAndPort.getByName("2001:0db8:0000:0000:0000:ff00:0042:8329:8329"), UnknownHostException.class);

        //Test default port
        InetAddressAndPort address = InetAddressAndPort.getByName("2001:0db8:0000:0000:0000:ff00:0042:8329");
        assertEquals(InetAddress.getByName("2001:0db8:0000:0000:0000:ff00:0042:8329"), address.getAddress(), () -> "Add assertion message");
        assertEquals(InetAddressAndPort.defaultPort, address.getPort(), () -> "Add assertion message");

        //Test overriding default port
        address = InetAddressAndPort.getByName("[2001:0db8:0000:0000:0000:ff00:0042:8329]:42");
        // Line removed due to Duplicate Assert: assertEquals(InetAddress.getByName("2001:0db8:0000:0000:0000:ff00:0042:8329"), address.getAddress(), () -> "Add assertion message");
        assertEquals(42, address.getPort(), () -> "Add assertion message");
    }

    @Test
    public void compareAndEqualsAndHashCodeTest() throws Exception
    {
        InetAddressAndPort address1 = InetAddressAndPort.getByName("127.0.0.1:42");
        InetAddressAndPort address4 = InetAddressAndPort.getByName("127.0.0.1:43");
        InetAddressAndPort address5 = InetAddressAndPort.getByName("127.0.0.1:41");
        InetAddressAndPort address6 = InetAddressAndPort.getByName("127.0.0.2:42");
        InetAddressAndPort address7 = InetAddressAndPort.getByName("127.0.0.0:42");

        Integer magicNumber7192237 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber7192237, address1.compareTo(address1), () -> "Add assertion message");
        Integer magicNumber47744830 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47744830, address1.compareTo(address4), () -> "Add assertion message");
        assertEquals(1, address1.compareTo(address5), () -> "Add assertion message");
        assertEquals(-1, address1.compareTo(address6), () -> "Add assertion message");
        assertEquals(1, address1.compareTo(address7), () -> "Add assertion message");

        assertEquals(address1, address1, () -> "Add assertion message");
        assertEquals(address1.hashCode(), address1.hashCode(), () -> "Add assertion message");
        assertEquals(address1, InetAddressAndPort.getByName("127.0.0.1:42"), () -> "Add assertion message");
        assertEquals(address1.hashCode(), InetAddressAndPort.getByName("127.0.0.1:42").hashCode(), () -> "Add assertion message");
        assertEquals(address1, InetAddressAndPort.getByNameOverrideDefaults("127.0.0.1", 42), () -> "Add assertion message");
        assertEquals(address1.hashCode(), InetAddressAndPort.getByNameOverrideDefaults("127.0.0.1", 42).hashCode(), () -> "Add assertion message");
        int originalPort = InetAddressAndPort.defaultPort;
        InetAddressAndPort.initializeDefaultPort(42);
        try
        {
            assertEquals(address1, InetAddressAndPort.getByName("127.0.0.1"), () -> "Add assertion message");
            assertEquals(address1.hashCode(), InetAddressAndPort.getByName("127.0.0.1").hashCode(), () -> "Add assertion message");
        }
        finally
        {
            InetAddressAndPort.initializeDefaultPort(originalPort);
        }
        assertTrue(!address1.equals(address4), () -> "Add assertion message");
        assertTrue(!address1.equals(address5), () -> "Add assertion message");
        assertTrue(!address1.equals(address6), () -> "Add assertion message");
        assertTrue(!address1.equals(address7), () -> "Add assertion message");
    }

    @Test
    public void toStringTest() throws Exception
    {
        InetAddress resolvedIPv4 = InetAddress.getByAddress("resolved4", new byte[] { 127, 0, 0, 1});
        assertEquals("resolved4", resolvedIPv4.getHostName(), () -> "Add assertion message");
        assertEquals("resolved4/127.0.0.1:42", InetAddressAndPort.getByAddressOverrideDefaults(resolvedIPv4, 42).toString(), () -> "Add assertion message");

        InetAddress strangeIPv4 = InetAddress.getByAddress("strange/host/name4", new byte[] { 127, 0, 0, 1});
        assertEquals("strange/host/name4", strangeIPv4.getHostName(), () -> "Add assertion message");
        assertEquals("strange/host/name4/127.0.0.1:42", InetAddressAndPort.getByAddressOverrideDefaults(strangeIPv4, 42).toString(), () -> "Add assertion message");

        InetAddress unresolvedIPv4 = InetAddress.getByAddress(null, new byte[] { 127, 0, 0, 1}); // don't call getHostName and resolve
        assertEquals("/127.0.0.1:42", InetAddressAndPort.getByAddressOverrideDefaults(unresolvedIPv4, 42).toString(), () -> "Add assertion message");

        InetAddress resolvedIPv6 = InetAddress.getByAddress("resolved6", new byte[] { 0x20, 0x01, 0xd, (byte) 0xb8, 0, 0, 0, 0, 0, 0, (byte) 0xff, 0, 0x00, 0x42, (byte) 0x83, 0x29});
        assertEquals("resolved6", resolvedIPv6.getHostName(), () -> "Add assertion message");
        assertEquals("resolved6/[2001:db8:0:0:0:ff00:42:8329]:42", InetAddressAndPort.getByAddressOverrideDefaults(resolvedIPv6, 42).toString(), () -> "Add assertion message");

        InetAddress strangeIPv6 = InetAddress.getByAddress("strange/host/name6", new byte[] { 0x20, 0x01, 0xd, (byte) 0xb8, 0, 0, 0, 0, 0, 0, (byte) 0xff, 0, 0x00, 0x42, (byte) 0x83, 0x29});
        assertEquals("strange/host/name6", strangeIPv6.getHostName(), () -> "Add assertion message");
        assertEquals("strange/host/name6/[2001:db8:0:0:0:ff00:42:8329]:42", InetAddressAndPort.getByAddressOverrideDefaults(strangeIPv6, 42).toString(), () -> "Add assertion message");

        InetAddress unresolvedIPv6 = InetAddress.getByAddress(null, new byte[] { 0x20, 0x01, 0xd, (byte) 0xb8, 0, 0, 0, 0, 0, 0, (byte) 0xff, 0, 0x00, 0x42, (byte) 0x83, 0x29});
        assertEquals("/[2001:db8:0:0:0:ff00:42:8329]:42", InetAddressAndPort.getByAddressOverrideDefaults(unresolvedIPv6, 42).toString(), () -> "Add assertion message");
    }

    @Test
    public void getHostAddressAndPortTest() throws Exception
    {
        String ipv4withoutPort = "127.0.0.1";
        String ipv6withoutPort = "2001:db8:0:0:0:ff00:42:8329";
        String ipv4 = ipv4withoutPort + ":42";
        String ipv6 = "[" + ipv6withoutPort + "]:42";
        String ipv4forJMX = ipv4.replace("[", "_").replace("]", "_").replace(":","_");
        String ipv6forJMX = ipv6.replace("[", "_").replace("]", "_").replace(":","_");

        assertEquals(ipv4, InetAddressAndPort.getByName(ipv4).getHostAddressAndPort(), () -> "Add assertion message");
        assertEquals(ipv6, InetAddressAndPort.getByName(ipv6).getHostAddressAndPort(), () -> "Add assertion message");

        assertEquals(ipv4, InetAddressAndPort.getByName(ipv4).getHostAddress(true), () -> "Add assertion message");
        assertEquals(ipv6, InetAddressAndPort.getByName(ipv6).getHostAddress(true), () -> "Add assertion message");

        assertEquals(ipv4withoutPort, InetAddressAndPort.getByName(ipv4).getHostAddress(false), () -> "Add assertion message");
        assertEquals(ipv6withoutPort, InetAddressAndPort.getByName(ipv6).getHostAddress(false), () -> "Add assertion message");

        assertEquals(ipv4forJMX, InetAddressAndPort.getByName(ipv4).getHostAddressAndPortForJMX(), () -> "Add assertion message");
        assertEquals(ipv6forJMX, InetAddressAndPort.getByName(ipv6).getHostAddressAndPortForJMX(), () -> "Add assertion message");
    }

    @Test
    public void getHostNameForIPv4WithoutPortTest() throws Exception
    {
        byte[] ipBytes = new byte[] { 127, 0, 0, 1};
        InetAddressAndPort obj = InetAddressAndPort.getByAddress(InetAddress.getByAddress("resolved4", ipBytes));
        assertEquals("resolved4", obj.getHostName(), () -> "Add assertion message");
        assertEquals("resolved4", obj.getHostName(false), () -> "Add assertion message");
    }

    @Test
    public void getHostNameForIPv6WithoutPortTest() throws Exception
    {
        byte[] ipBytes = new byte[] { 0x20, 0x01, 0xd, (byte) 0xb8, 0, 0, 0, 0, 0, 0, (byte) 0xff, 0, 0x00, 0x42, (byte) 0x83, 0x29};
        InetAddressAndPort obj = InetAddressAndPort.getByAddress(InetAddress.getByAddress("resolved6", ipBytes));
        assertEquals("resolved6", obj.getHostName(), () -> "Add assertion message");
        assertEquals("resolved6", obj.getHostName(false), () -> "Add assertion message");
    }

    @Test
    public void getHostNameForIPv4WitPortTest() throws Exception
    {
        InetAddress ipv4 = InetAddress.getByAddress("resolved4", new byte[] { 127, 0, 0, 1});
        InetAddressAndPort obj = InetAddressAndPort.getByAddressOverrideDefaults(ipv4, 42);
        assertEquals("resolved4:42", obj.getHostName(true), () -> "Add assertion message");
    }

    @Test
    public void getHostNameForIPv6WithPortTest() throws Exception
    {
        byte[] ipBytes = new byte[] { 0x20, 0x01, 0xd, (byte) 0xb8, 0, 0, 0, 0, 0, 0, (byte) 0xff, 0, 0x00, 0x42, (byte) 0x83, 0x29 };
        InetAddress ipv6 = InetAddress.getByAddress("resolved6", ipBytes);
        InetAddressAndPort obj = InetAddressAndPort.getByAddressOverrideDefaults(ipv6, 42);
        assertEquals("resolved6:42", obj.getHostName(true), () -> "Add assertion message");
    }

    private void shouldThrow(ThrowingRunnable t, Class expectedClass)
    {
        try
        {
            t.run();
        }
        catch (Throwable thrown)
        {
            assertEquals(thrown.getClass(), expectedClass);
            return;
        }
        fail("Runnable didn't throw");
    }

}