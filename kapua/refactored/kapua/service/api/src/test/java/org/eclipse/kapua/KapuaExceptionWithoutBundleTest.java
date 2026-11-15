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
package org.eclipse.kapua;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class KapuaExceptionWithoutBundleTest {

    String expectedKapuaErrorMessagesBundle;
    Object argument1, argument2, argument3;
    Throwable[] throwables;
    KapuaErrorCode[] kapuaErrorCodes;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: expectedKapuaErrorMessagesBundle = "non-existing-file.properties";
        // Removed line due to General Fixture smell: argument1 = "user";
        // Removed line due to General Fixture smell: argument2 = 1;
        // Removed line due to General Fixture smell: argument3 = 'c';
        // Removed line due to General Fixture smell: throwables = new Throwable[]{new Throwable(), null};
        kapuaErrorCodes = new KapuaErrorCode[]{KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR, null, MissingKapuaErrorCodes.NOT_EXISTING};

    }

    @Test
    public void kapuaExceptionWithoutBundleKapuaErrorCodeParameterTest() {
        expectedKapuaErrorMessagesBundle = "non-existing-file.properties";
        for (KapuaErrorCode kapuaErrorCode : kapuaErrorCodes) {
            KapuaExceptionWithoutBundle kapuaExceptionWithoutBundle = new KapuaExceptionWithoutBundle(kapuaErrorCode);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaErrorCode, kapuaExceptionWithoutBundle.getCode());
            Assert.assertNull("Null expected.", kapuaExceptionWithoutBundle.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", "Error: ", kapuaExceptionWithoutBundle.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", "Error: ", kapuaExceptionWithoutBundle.getLocalizedMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedKapuaErrorMessagesBundle, kapuaExceptionWithoutBundle.getKapuaErrorMessagesBundle());
        }
    }

    @Test
    public void kapuaExceptionWithoutBundleKapuaErrorCodeObjectParameterTest() {
        expectedKapuaErrorMessagesBundle = "non-existing-file.properties";
        argument3 = 'c';
        argument2 = 1;
        argument1 = "user";
        for (KapuaErrorCode kapuaErrorCode : kapuaErrorCodes) {
            KapuaExceptionWithoutBundle kapuaExceptionWithoutBundle = new KapuaExceptionWithoutBundle(kapuaErrorCode, argument1, argument2, argument3);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaErrorCode, kapuaExceptionWithoutBundle.getCode());
            Assert.assertNull("Null expected.", kapuaExceptionWithoutBundle.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", "Error: " + argument1 + ", " + argument2 + ", " + argument3, kapuaExceptionWithoutBundle.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", "Error: " + argument1 + ", " + argument2 + ", " + argument3, kapuaExceptionWithoutBundle.getLocalizedMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedKapuaErrorMessagesBundle, kapuaExceptionWithoutBundle.getKapuaErrorMessagesBundle());
        }
    }

    @Test
    public void kapuaExceptionWithoutBundleKapuaErrorCodeNullObjectParameterTest() {
        expectedKapuaErrorMessagesBundle = "non-existing-file.properties";
        for (KapuaErrorCode kapuaErrorCode : kapuaErrorCodes) {
            KapuaExceptionWithoutBundle kapuaExceptionWithoutBundle = new KapuaExceptionWithoutBundle(kapuaErrorCode, null);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaErrorCode, kapuaExceptionWithoutBundle.getCode());
            Assert.assertNull("Null expected.", kapuaExceptionWithoutBundle.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", "Error: ", kapuaExceptionWithoutBundle.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", "Error: ", kapuaExceptionWithoutBundle.getLocalizedMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedKapuaErrorMessagesBundle, kapuaExceptionWithoutBundle.getKapuaErrorMessagesBundle());
        }
    }

    @Test
    public void kapuaExceptionWithoutBundleKapuaErrorCodeThrowableObjectParameterTest() {
        expectedKapuaErrorMessagesBundle = "non-existing-file.properties";
        throwables = new Throwable[]{new Throwable(), null};
        argument3 = 'c';
        argument2 = 1;
        argument1 = "user";
        for (KapuaErrorCode kapuaErrorCode : kapuaErrorCodes) {
            for (Throwable throwable : throwables) {
                KapuaExceptionWithoutBundle kapuaExceptionWithoutBundle = new KapuaExceptionWithoutBundle(kapuaErrorCode, throwable, argument1, argument2, argument3);
                Assert.assertEquals("Expected and actual values should be the same.", kapuaErrorCode, kapuaExceptionWithoutBundle.getCode());
                Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaExceptionWithoutBundle.getCause());
                Assert.assertEquals("Expected and actual values should be the same.", "Error: " + argument1 + ", " + argument2 + ", " + argument3, kapuaExceptionWithoutBundle.getMessage());
                Assert.assertEquals("Expected and actual values should be the same.", "Error: " + argument1 + ", " + argument2 + ", " + argument3, kapuaExceptionWithoutBundle.getLocalizedMessage());
                Assert.assertEquals("Expected and actual values should be the same.", expectedKapuaErrorMessagesBundle, kapuaExceptionWithoutBundle.getKapuaErrorMessagesBundle());
            }
        }
    }

    @Test
    public void kapuaExceptionWithoutBundleKapuaErrorCodeThrowableNullObjectParameterTest() {
        expectedKapuaErrorMessagesBundle = "non-existing-file.properties";
        throwables = new Throwable[]{new Throwable(), null};
        for (KapuaErrorCode kapuaErrorCode : kapuaErrorCodes) {
            for (Throwable throwable : throwables) {
                KapuaExceptionWithoutBundle kapuaExceptionWithoutBundle = new KapuaExceptionWithoutBundle(kapuaErrorCode, throwable, null);
                Assert.assertEquals("Expected and actual values should be the same.", kapuaErrorCode, kapuaExceptionWithoutBundle.getCode());
                Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaExceptionWithoutBundle.getCause());
                Assert.assertEquals("Expected and actual values should be the same.", "Error: ", kapuaExceptionWithoutBundle.getMessage());
                Assert.assertEquals("Expected and actual values should be the same.", "Error: ", kapuaExceptionWithoutBundle.getLocalizedMessage());
                Assert.assertEquals("Expected and actual values should be the same.", expectedKapuaErrorMessagesBundle, kapuaExceptionWithoutBundle.getKapuaErrorMessagesBundle());
            }
        }
    }

    @Test(expected = KapuaExceptionWithoutBundle.class)
    public void throwingExceptionKapuaErrorCodeParameterTest() throws KapuaExceptionWithoutBundle {
        for (KapuaErrorCode kapuaErrorCode : kapuaErrorCodes) {
            throw new KapuaExceptionWithoutBundle(kapuaErrorCode);
        }
    }

    @Test(expected = KapuaExceptionWithoutBundle.class)
    public void throwingExceptionKapuaErrorCodeObjectParameterTest() throws KapuaExceptionWithoutBundle {
        argument3 = 'c';
        argument2 = 1;
        argument1 = "user";
        for (KapuaErrorCode kapuaErrorCode : kapuaErrorCodes) {
            throw new KapuaExceptionWithoutBundle(kapuaErrorCode, argument1, argument2, argument3);
        }
    }
}