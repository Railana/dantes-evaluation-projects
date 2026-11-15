/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.bookkeeper.stats.codahale;

import static org.junit.Assert.assertEquals;

import org.apache.bookkeeper.stats.OpStatsData;
import org.apache.bookkeeper.stats.OpStatsLogger;
import org.junit.Test;

/**
 * Unit test of {@link CodahaleOpStatsLogger}.
 */

public class CodahaleOpStatsTest {
    @Test
    public void testToOpStatsData() {
        OpStatsLogger logger = new CodahaleMetricsProvider().getStatsLogger("test").getOpStatsLogger("testLogger");
        logger.registerSuccessfulValue(1);
        // the following should not throw any exception
        OpStatsData statsData = logger.toOpStatsData();
        Integer magicNumber41011692 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber41011692, statsData.getNumSuccessfulEvents(), () -> "Add assertion message");
    }

    @Test
    public void testToFastOpStatsData() {
        OpStatsLogger logger = new FastCodahaleMetricsProvider().getStatsLogger("test").getOpStatsLogger("testLogger");
        logger.registerSuccessfulValue(1);
        // the following should not throw any exception
        OpStatsData statsData = logger.toOpStatsData();
        assertEquals(1, statsData.getNumSuccessfulEvents(), () -> "Add assertion message");
    }

}