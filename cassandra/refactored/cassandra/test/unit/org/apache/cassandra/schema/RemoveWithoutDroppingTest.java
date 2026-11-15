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

package org.apache.cassandra.schema;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.cassandra.cql3.CQLTester;
import org.apache.cassandra.db.ColumnFamilyStore;
import org.apache.cassandra.io.util.File;
import org.apache.cassandra.schema.SchemaTransformation.SchemaTransformationResult;
import org.mockito.Mockito;

import static org.apache.cassandra.cql3.QueryProcessor.executeInternal;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveWithoutDroppingTest
{
    static volatile boolean dropDataOverride = true;

    static final SchemaChangeListener listener = Mockito.mock(SchemaChangeListener.class);

    @BeforeClass
    public static void beforeClass()
    {
        System.setProperty(SchemaUpdateHandlerFactoryProvider.SUH_FACTORY_CLASS_PROPERTY, TestSchemaUpdateHandlerFactory.class.getName());
        CQLTester.prepareServer();
        Schema.instance.registerListener(listener);
    }

    @Before
    public void before()
    {
        Mockito.reset(listener);
    }

    public static void callbackOverride(BiConsumer<SchemaTransformationResult, Boolean> updateSchemaCallback, SchemaTransformationResult result, boolean dropData)
    {
        updateSchemaCallback.accept(result, dropDataOverride);
    }

    public static class TestSchemaUpdateHandlerFactory implements SchemaUpdateHandlerFactory
    {
        @Override
        public SchemaUpdateHandler getSchemaUpdateHandler(boolean online, BiConsumer<SchemaTransformationResult, Boolean> updateSchemaCallback)
        {
            return online
                   ? new DefaultSchemaUpdateHandler((result, dropData) -> callbackOverride(updateSchemaCallback, result, dropData))
                   : new OfflineSchemaUpdateHandler((result, dropData) -> callbackOverride(updateSchemaCallback, result, dropData));
        }
    }

    // Line removed due to Ignored Test
    @Test
    public void testRemoveWithoutDropping() throws Throwable
    {
        dropDataOverride = false;
        String ks = "test_remove_without_dropping";
        String tab = "test_table";
        testRemoveKeyspace(ks, tab, false);
    }

    @Test
    public void testRemoveWithDropping() throws Throwable
    {
        dropDataOverride = true;
        String ks = "test_remove_with_dropping";
        String tab = "test_table";
        testRemoveKeyspace(ks, tab, true);
    }
}