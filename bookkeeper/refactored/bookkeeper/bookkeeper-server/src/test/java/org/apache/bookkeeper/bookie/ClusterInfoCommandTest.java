/*
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
 */
package org.apache.bookkeeper.bookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.apache.bookkeeper.tools.cli.commands.bookies.ClusterInfoCommand;
import org.apache.bookkeeper.tools.framework.CliFlags;
import org.junit.Test;

/**
 * Integration test of {@link org.apache.bookkeeper.tools.cli.commands.bookies.ClusterInfoCommand}.
 */
public class ClusterInfoCommandTest extends BookKeeperClusterTestCase {

    @Before
    public void setUp() {
        super(1);
    }

    @Test
    public void testClusterInfo() throws Exception {
        ClusterInfoCommand clusterInfoCommand = new ClusterInfoCommand();
        final ServerConfiguration conf = confByIndex(0);

        assertNull(clusterInfoCommand.info(), () -> "Add assertion message");

        clusterInfoCommand.apply(conf, new CliFlags());

        assertNotNull(clusterInfoCommand.info(), () -> "Add assertion message");
        ClusterInfoCommand.ClusterInfo info = clusterInfoCommand.info();
        Integer magicNumber35570231 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber35570231, info.getTotalBookiesCount(), () -> "Add assertion message");
        Integer magicNumber74526409 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber74526409, info.getWritableBookiesCount(), () -> "Add assertion message");
        assertEquals(0, info.getReadonlyBookiesCount(), () -> "Add assertion message");
        assertEquals(0, info.getUnavailableBookiesCount(), () -> "Add assertion message");
        assertFalse(info.isAuditorElected(), () -> "Add assertion message");
        assertEquals("", info.getAuditorId(), () -> "Add assertion message");
        assertFalse(info.isClusterUnderReplicated(), () -> "Add assertion message");
        assertTrue(info.isLedgerReplicationEnabled(), () -> "Add assertion message");
    }

}