/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class KapuaConfigurableServiceSchemaUtilsTest {

    @Test(expected = NullPointerException.class)
    public void scriptSessionWithNullArgumentsTest() {
        KapuaConfigurableServiceSchemaUtils.scriptSession(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void scriptSessionWithNullPathTest() {
        KapuaConfigurableServiceSchemaUtils.scriptSession(null, KapuaConfigurableServiceSchemaUtils.DEFAULT_FILTER);
    }

    @Test(expected = NullPointerException.class)
    public void scriptSessionWithNullFilterTest() {
        KapuaConfigurableServiceSchemaUtils.scriptSession(KapuaConfigurableServiceSchemaUtils.DEFAULT_PATH, null);
    }

    @Test
    public void scriptSessionWithDefaultValuesTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.scriptSession(KapuaConfigurableServiceSchemaUtils.DEFAULT_PATH, KapuaConfigurableServiceSchemaUtils.DEFAULT_FILTER));
    }

    @Test
    public void scriptSessionWithEmptyPathValueTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.scriptSession("", KapuaConfigurableServiceSchemaUtils.DEFAULT_FILTER));
    }

    @Test
    public void scriptSessionWithWrongPathValueTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.scriptSession("wrong/path", KapuaConfigurableServiceSchemaUtils.DEFAULT_FILTER));
    }

    @Test
    public void scriptSessionWithEmptyFilterValueTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.scriptSession(KapuaConfigurableServiceSchemaUtils.DEFAULT_PATH, ""));
    }

    @Test
    public void scriptSessionWithWrongFilterValueTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.scriptSession(KapuaConfigurableServiceSchemaUtils.DEFAULT_PATH, "*.wrong"));
    }

    @Test
    public void createSchemaObjectsWithDefaultPathTest() throws KapuaException {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.createSchemaObjects(KapuaConfigurableServiceSchemaUtils.DEFAULT_PATH));
    }

    @Test
    public void createSchemaObjectsWithWrongPathTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.createSchemaObjects("wrong/path"));
    }

    @Test
    public void createSchemaObjectsWithRandomPathTest() {
        String[] paths = {"", "asdf", "wqeqw@123", "@123ˇ^ˇ°", "c", "www.test.asd"};
        for (String path : paths) {
            assertThrows(Exception e,
            		() -> KapuaConfigurableServiceSchemaUtils.createSchemaObjects(path));
        }
    }

    @Test(expected = NullPointerException.class)
    public void createSchemaObjectsWithNullPathTest() throws KapuaException {
        KapuaConfigurableServiceSchemaUtils.createSchemaObjects(null);

    }

    @Test
    public void dropSchemaObjectsWithDefaultPathTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.dropSchemaObjects(KapuaConfigurableServiceSchemaUtils.DEFAULT_PATH));
    }

    @Test
    public void dropSchemaObjectsWithRandomPathTest() {
        String[] paths = {"asd", "w/qww", "123214", "asdasd13213", "!#@{}"};
        for (String path : paths) {
            assertThrows(Exception e,
            		() -> KapuaConfigurableServiceSchemaUtils.dropSchemaObjects(path));
        }
    }

    @Test
    public void dropSchemaObjectsWithWrongPathTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.dropSchemaObjects("wrong/path"));
    }

    @Test
    public void dropSchemaObjectsWithEmptyPathTest() {
        assertThrows(Exception e,
        		() -> KapuaConfigurableServiceSchemaUtils.dropSchemaObjects(""));
    }

    @Test(expected = NullPointerException.class)
    public void dropSchemaObjectsWithNullPathTest() {
        KapuaConfigurableServiceSchemaUtils.dropSchemaObjects(null);
    }
}