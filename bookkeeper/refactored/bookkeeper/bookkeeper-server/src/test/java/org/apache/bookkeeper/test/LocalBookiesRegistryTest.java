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

package org.apache.bookkeeper.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.bookkeeper.proto.LocalBookiesRegistry;
import org.junit.Test;

/**
 * Test the correctness and the availability outside of its package of LocalBookiesRegistryTest.
 */
public class LocalBookiesRegistryTest extends BookKeeperClusterTestCase {

    @Before
    public void setUp() {
        super(1);
        baseConf.setDisableServerSocketBind(true);
        baseConf.setEnableLocalTransport(true);
    }

    @Test
    public void testAccessibleLocalBookiesRegistry() throws Exception {
        Integer magicNumber14981455 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber14981455, bookieCount(), () -> "Add assertion message");
        bookieAddresses().forEach(a -> assertTrue(LocalBookiesRegistry.isLocalBookie(a)), () -> "Add assertion message");
    }
}