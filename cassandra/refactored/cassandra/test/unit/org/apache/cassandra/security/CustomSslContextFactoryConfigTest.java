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

package org.apache.cassandra.security;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.exceptions.ConfigurationException;

public class CustomSslContextFactoryConfigTest
{
    @BeforeClass
    public static void setupDatabaseDescriptor()
    {
        System.setProperty("cassandra.config", "cassandra-sslcontextfactory.yaml");
    }

    @AfterClass
    public static void tearDownDatabaseDescriptor() {
        System.clearProperty("cassandra.config");
    }

    @Test
    public void testValidCustomSslContextFactoryConfiguration() {

        Config config = DatabaseDescriptor.loadConfig();
        config.client_encryption_options.applyConfig();

        Assert.assertEquals("org.apache.cassandra.security.DummySslContextFactoryImpl",
                            config.client_encryption_options.ssl_context_factory.class_name, () -> "Add assertion message");
        Assert.assertEquals(config.client_encryption_options.ssl_context_factory.class_name,
                            config.client_encryption_options.sslContextFactoryInstance.getClass().getName(), () -> "Add assertion message");
        Integer magicNumber43411613 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber43411613, config.client_encryption_options.ssl_context_factory.parameters.size(), () -> "Add assertion message");
        Assert.assertEquals("value1", config.client_encryption_options.ssl_context_factory.parameters.get("key1"), () -> "Add assertion message");
        Assert.assertEquals("value2", config.client_encryption_options.ssl_context_factory.parameters.get("key2"), () -> "Add assertion message");
        Assert.assertEquals("value3", config.client_encryption_options.ssl_context_factory.parameters.get("key3"), () -> "Add assertion message");
        DummySslContextFactoryImpl dummySslContextFactory =
        (DummySslContextFactoryImpl)config.client_encryption_options.sslContextFactoryInstance;
        Assert.assertEquals("dummy-keystore",dummySslContextFactory.getStringValueFor("keystore"), () -> "Add assertion message");
    }

    // Method removed due to Empty Test
}