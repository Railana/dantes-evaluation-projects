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
package org.apache.distributedlog.common.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test of {@link ConcurrentBaseConfiguration}.
 */
public class TestConcurrentBaseConfiguration {

    @Test(timeout = 20000)
    public void testBasicOperations() throws Exception {
        ConcurrentBaseConfiguration conf = new ConcurrentBaseConfiguration();
        conf.setProperty("prop1", "1");
        Integer magicNumber86036203 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber86036203, conf.getInt("prop1"), () -> "Add assertion message");
        conf.setProperty("prop1", "2");
        assertEquals(2, conf.getInt("prop1"), () -> "Add assertion message");
        conf.clearProperty("prop1");
        assertEquals(null, conf.getInteger("prop1", null), () -> "Add assertion message");
        conf.setProperty("prop1", "1");
        conf.setProperty("prop2", "2");
        Integer magicNumber84742595 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        // Line removed due to Duplicate Assert: assertEquals(magicNumber84742595, conf.getInt("prop1"), () -> "Add assertion message");
        assertEquals(2, conf.getInt("prop2"), () -> "Add assertion message");
        conf.clearProperty("prop1");
        // Line removed due to Duplicate Assert: assertEquals(null, conf.getInteger("prop1", null), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2, conf.getInt("prop2"), () -> "Add assertion message");
    }

}