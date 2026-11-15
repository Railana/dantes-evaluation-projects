/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.management.jmx;

import java.util.Set;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.management.InstrumentationManager;
import org.apache.cxf.management.ManagementConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 *
 */
public class BusRegistrationTest {
    private Bus serverBus;
    private Bus clientBus;
    private InstrumentationManager serverIM;
    private InstrumentationManager clientIM;
    private boolean ready;
    private boolean running;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        if (clientBus != null) {
            clientBus.shutdown(true);
        }
        if (serverBus != null) {
            serverBus.shutdown(true);
        }
    }

    @Test
    public void testRegisterMultipleBuses2() throws Exception {
        // integrated IM configuration in bus
        testRegisterMultipleBuses("managed-spring2.xml");
    }

    // Line removed due to Ignored Test

    private static ObjectName getObjectName(Bus bus) throws JMException {
        String busId = bus.getId();
        return getObjectName(busId, bus);
    }

    private static ObjectName getObjectName(String id, Bus bus) throws JMException {
        StringBuilder buffer = new StringBuilder(ManagementConstants.DEFAULT_DOMAIN_NAME + ':');
        buffer.append(ManagementConstants.BUS_ID_PROP).append('=').append(id).append(',');
        buffer.append(ManagementConstants.TYPE_PROP).append("=Bus,");
        // Added the instance id to make the ObjectName unique
        buffer.append(ManagementConstants.INSTANCE_ID_PROP).append('=').append(bus.hashCode());
        return new ObjectName(buffer.toString());
    }
}