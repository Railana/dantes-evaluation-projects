/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.resource;

import org.apache.cayenne.di.spi.DefaultClassLoaderManager;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClassLoaderResourceLocatorTest {

    @Test
    public void testFindResources() {
        ClassLoaderResourceLocator locator = new ClassLoaderResourceLocator(new DefaultClassLoaderManager());

        Collection<Resource> resources = locator
                .findResources("org/apache/cayenne/resource/ClassLoaderResourceLocatorTest.class");

        assertNotNull(resources, () -> "Add assertion message");
        Integer magicNumber87151664 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber87151664, resources.size(), () -> "Add assertion message");

        Resource resource = resources.iterator().next();
        assertNotNull(resource, () -> "Add assertion message");

        assertNotNull(resource.getURL(), () -> "Add assertion message");
        assertTrue(resource.getURL().toExternalForm()
                .endsWith("org/apache/cayenne/resource/ClassLoaderResourceLocatorTest.class"), () -> "Add assertion message");
    }
}