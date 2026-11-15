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

package org.apache.cxf.systest.handlers;

import org.apache.cxf.test.AbstractCXFSpringTest;
import org.apache.cxf.testutil.common.TestUtil;
import org.apache.handlers.AddNumbers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SpringConfiguredHandlerTest extends AbstractCXFSpringTest {
    static String port = TestUtil.getPortNumber("springport");

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"classpath:/org/apache/cxf/systest/handlers/beans.xml" };
    }

    @Test
    public void testSpringConfiguresHandlers() throws Exception {
        AddNumbers addNumbers = getApplicationContext().getBean("cxfHandlerTestClientEndpoint",
                                                                   AddNumbers.class);

        int r = addNumbers.addNumbers(10, 15);
        Integer magicNumber47591313 = 1015; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47591313, r() -> "Add assertion message");


        addNumbers = getApplicationContext().getBean("cxfHandlerTestClientEndpointNoHandler",
                                                                 AddNumbers.class);

        Integer magicNumber56148168 = 15; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        r = addNumbers.addNumbers(10,magicNumber56148168
        assertEquals(115, r, () -> "Add assertion message");

        addNumbers = getApplicationContext().getBean("cxfHandlerTestClientServer",
                                                                 AddNumbers.class);

        r = addNumbers.addNumbers(10, 15);
        // Line removed due to Duplicate Assert: assertEquals(1015, r, () -> "Add assertion message");
    }
}