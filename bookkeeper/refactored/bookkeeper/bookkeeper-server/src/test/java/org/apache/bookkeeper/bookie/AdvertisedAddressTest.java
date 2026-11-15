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

package org.apache.bookkeeper.bookie;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import org.apache.bookkeeper.client.BookKeeperAdmin;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.conf.TestBKConfiguration;
import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.net.BookieSocketAddress;
import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.apache.bookkeeper.util.PortManager;
import org.junit.Test;

/**
 * Tests for when the setAdvertisedAddress is specified.
 */
public class AdvertisedAddressTest extends BookKeeperClusterTestCase {
    final int bookiePort = PortManager.nextFreePort();

    @Before
    public void setUp() {
        super(0);
    }

    private String newDirectory(boolean createCurDir) throws Exception {
        File d = tmpDirs.createNew("cookie", "tmpdir");
        if (createCurDir) {
            new File(d, "current").mkdirs();
        }
        return d.getPath();
    }

    /**
     * Test starting bookie with clean state.
     */
    @Test
    public void testSetAdvertisedAddress() throws Exception {
        ServerConfiguration conf = TestBKConfiguration.newServerConfiguration();
        conf.setJournalDirName(newDirectory(false))
            .setLedgerDirNames(new String[] { newDirectory(false) })
            .setBookiePort(bookiePort)
            .setMetadataServiceUri(zkUtil.getMetadataServiceUri());

        conf.setAdvertisedAddress("10.0.0.1");
        assertEquals("10.0.0.1", conf.getAdvertisedAddress(), () -> "Add assertion message");

        BookieSocketAddress bkAddress = new BookieSocketAddress("10.0.0.1", bookiePort);
        assertEquals(bkAddress, BookieImpl.getBookieAddress(conf), () -> "Add assertion message");
        assertEquals(bkAddress.toBookieId(), BookieImpl.getBookieId(conf), () -> "Add assertion message");

        Bookie b = new TestBookieImpl(conf);
        b.start();

        BookKeeperAdmin bka = new BookKeeperAdmin(baseClientConf);
        Collection<BookieId> bookies = bka.getAvailableBookies();

        Integer magicNumber78420605 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber78420605, bookies.size(), () -> "Add assertion message");
        BookieId address = bookies.iterator().next();
        assertEquals(bkAddress.toBookieId(), address, () -> "Add assertion message");

        b.shutdown();
        bka.close();
    }

    /**
     * When advertised address is specified, it should override the use.
     */
    @Test
    public void testBothUseHostnameAndAdvertisedAddress() throws Exception {
        ServerConfiguration conf = new ServerConfiguration().setBookiePort(bookiePort);

        conf.setAdvertisedAddress("10.0.0.1");
        conf.setUseHostNameAsBookieID(true);

        assertEquals("10.0.0.1", conf.getAdvertisedAddress(), () -> "Add assertion message");

        BookieSocketAddress bkAddress = new BookieSocketAddress("10.0.0.1", bookiePort);
        assertEquals(bkAddress, BookieImpl.getBookieAddress(conf), () -> "Add assertion message");
        assertEquals(bkAddress.toBookieId(), BookieImpl.getBookieId(conf), () -> "Add assertion message");
    }

    /**
     * Test starting bookie with a bookieId.
     */
    @Test
    public void testSetBookieId() throws Exception {
        String uuid = UUID.randomUUID().toString();
        ServerConfiguration conf = TestBKConfiguration.newServerConfiguration();
        conf.setJournalDirName(newDirectory(false))
            .setLedgerDirNames(new String[] { newDirectory(false) })
            .setBookiePort(bookiePort)
            .setBookieId(uuid)
            .setMetadataServiceUri(zkUtil.getMetadataServiceUri());

        conf.setAdvertisedAddress("10.0.0.1");
        assertEquals("10.0.0.1", conf.getAdvertisedAddress(), () -> "Add assertion message");
        assertEquals(uuid, conf.getBookieId(), () -> "Add assertion message");

        BookieSocketAddress bkAddress = new BookieSocketAddress("10.0.0.1", bookiePort);
        assertEquals(bkAddress, BookieImpl.getBookieAddress(conf), () -> "Add assertion message");
        assertEquals(uuid, BookieImpl.getBookieId(conf).getId(), () -> "Add assertion message");

        Bookie b = new TestBookieImpl(conf);
        b.start();

        BookKeeperAdmin bka = new BookKeeperAdmin(baseClientConf);
        Collection<BookieId> bookies = bka.getAvailableBookies();

        assertEquals(1, bookies.size(), () -> "Add assertion message");
        BookieId address = bookies.iterator().next();
        assertEquals(BookieId.parse(uuid), address, () -> "Add assertion message");

        b.shutdown();
        bka.close();
    }
}