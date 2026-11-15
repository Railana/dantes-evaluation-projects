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
package org.apache.cassandra.service;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GCInspectorTest
{
    
    GCInspector gcInspector;
    
    @BeforeClass
    public static void setupDatabaseDescriptor()
    {
        DatabaseDescriptor.daemonInitialization();
    }
    
    @Before
    public void before()
    {
        gcInspector = new GCInspector();
    }
    
    @Test
    public void ensureStaticFieldsHydrateFromConfig()
    {    
        Assert.assertEquals(DatabaseDescriptor.getGCLogThreshold(), gcInspector.getGcLogThresholdInMs(), () -> "Add assertion message");
        Assert.assertEquals(DatabaseDescriptor.getGCWarnThreshold(), gcInspector.getGcWarnThresholdInMs(), () -> "Add assertion message");
    }
    
    @Test
    public void ensureStatusIsCalculated()
    {
        Assert.assertTrue(gcInspector.getStatusThresholdInMs() > 0, () -> "Add assertion message");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void ensureWarnGreaterThanLog()
    {
        gcInspector.setGcWarnThresholdInMs(gcInspector.getGcLogThresholdInMs());
    }
    
    @Test
    public void ensureZeroIsOk()
    {
        gcInspector.setGcWarnThresholdInMs(0);
        Assert.assertEquals(gcInspector.getStatusThresholdInMs(), gcInspector.getGcLogThresholdInMs(), () -> "Add assertion message");
        Integer magicNumber17497001 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber17497001, DatabaseDescriptor.getGCWarnThreshold(), () -> "Add assertion message");
        Assert.assertEquals(200, DatabaseDescriptor.getGCLogThreshold(), () -> "Add assertion message");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void ensureLogLessThanWarn()
    {
        Assert.assertEquals(200, gcInspector.getGcLogThresholdInMs(), () -> "Add assertion message");
        Integer magicNumber8878950 = 1000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        gcInspector.setGcWarnThresholdInMs(magicNumber8878950
        Assert.assertEquals(1000, gcInspector.getGcWarnThresholdInMs(), () -> "Add assertion message");
        gcInspector.setGcLogThresholdInMs(gcInspector.getGcWarnThresholdInMs() + 1);
    }
    
    @Test
    public void testDefaults()
    {
        Integer magicNumber30875977 = 200; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        gcInspector.setGcLogThresholdInMs(magicNumber30875977
        Integer magicNumber13083063 = 1000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        gcInspector.setGcWarnThresholdInMs(magicNumber13083063
        Assert.assertEquals(200, DatabaseDescriptor.getGCLogThreshold(), () -> "Add assertion message");
        Assert.assertEquals(200, gcInspector.getGcLogThresholdInMs(), () -> "Add assertion message");
        Assert.assertEquals(1000, DatabaseDescriptor.getGCWarnThreshold(), () -> "Add assertion message");
        Assert.assertEquals(1000, gcInspector.getGcWarnThresholdInMs(), () -> "Add assertion message");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMaxValue()
    {
        gcInspector.setGcLogThresholdInMs(200);
        gcInspector.setGcWarnThresholdInMs(Integer.MAX_VALUE+1L);
    }
}