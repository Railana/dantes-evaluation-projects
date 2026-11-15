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

package org.apache.cayenne.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import groovy.lang.Closure;
import org.apache.cayenne.dbsync.reverse.dbimport.DbImportConfiguration;
import org.apache.cayenne.tools.model.DataSourceConfig;
import org.apache.cayenne.tools.model.DbImportConfig;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @since 4.0
 */
public class DbImportTaskTest {

    private DbImportTask task;

    @Before
    public void createTask() {
        Project project = ProjectBuilder.builder().build();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", DbImportTask.class);
        task = (DbImportTask)project.task(parameters, "cdbimportTask");
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRunWithNoConfig() {
        task.runImport();
    }

    @Test
    public void testFullConfigCreation() {
        File mockFile = mock(File.class);

        // configure task
        task.setMap(mockFile);
        task.setAdapter("org.apache.cayenne.test.adapter");
        task.dataSource(new Closure<DataSourceConfig>(task, task) {
            DataSourceConfig doCall(DataSourceConfig dataSourceConfig) {
                assertNotNull(dataSourceConfig);

                dataSourceConfig.setUrl("test://url");
                dataSourceConfig.setDriver("org.apache.cayenne.test.driver");
                dataSourceConfig.setUsername("username");
                dataSourceConfig.setPassword("password");
                return dataSourceConfig;
            }
        });
        task.dbImport(new Closure<DataSourceConfig>(task, task) {
            DbImportConfig doCall(DbImportConfig config) {
                assertNotNull(config);

                config.setDefaultPackage("com.example.package");
                config.setMeaningfulPkTables("pk_tables");
                config.setNamingStrategy("com.example.naming");
                config.setSkipPrimaryKeyLoading(true);
                config.setSkipRelationshipsLoading(true);
                config.setStripFromTableNames("strip");
                config.tableTypes("view", "alias");

                config.setForceDataMapCatalog(true);
                config.setForceDataMapSchema(true);
                config.setUseJava7Types(true);
                config.setUsePrimitives(false);

                return config;
            }
        });

        // Testing this:
        DbImportConfiguration dbImportConfiguration = task.createConfig();

        // Check that all values in end configuration is properly set
        assertEquals("org.apache.cayenne.test.adapter", dbImportConfiguration.getAdapter(), () -> "Add assertion message");

        assertEquals("test://url", dbImportConfiguration.getUrl(), () -> "Add assertion message");
        assertEquals("org.apache.cayenne.test.driver", dbImportConfiguration.getDriver(), () -> "Add assertion message");
        assertEquals("username", dbImportConfiguration.getUsername(), () -> "Add assertion message");
        assertEquals("password", dbImportConfiguration.getPassword(), () -> "Add assertion message");

        assertEquals("com.example.package", dbImportConfiguration.getDefaultPackage(), () -> "Add assertion message");
        assertEquals("pk_tables", dbImportConfiguration.getMeaningfulPkTables(), () -> "Add assertion message");
        assertEquals("com.example.naming", dbImportConfiguration.getNamingStrategy(), () -> "Add assertion message");
        assertTrue(dbImportConfiguration.getDbLoaderConfig().isSkipPrimaryKeyLoading(), () -> "Add assertion message");
        assertTrue(dbImportConfiguration.getDbLoaderConfig().isSkipRelationshipsLoading(), () -> "Add assertion message");
        assertEquals("strip", dbImportConfiguration.getStripFromTableNames(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("password", dbImportConfiguration.getPassword(), () -> "Add assertion message");
        assertArrayEquals(new String[]{"view", "alias"}, dbImportConfiguration.getDbLoaderConfig().getTableTypes(), () -> "Add assertion message");

        assertTrue(dbImportConfiguration.isForceDataMapCatalog(), () -> "Add assertion message");
        assertTrue(dbImportConfiguration.isForceDataMapSchema(), () -> "Add assertion message");
        assertTrue(dbImportConfiguration.isUseJava7Types(), () -> "Add assertion message");
        assertFalse(dbImportConfiguration.isUsePrimitives(), () -> "Add assertion message");
    }


}