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
package org.eclipse.kapua.locator;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class KapuaLocatorExceptionTest {

    String expectedErrorMessage;
    KapuaLocatorErrorCodes[] kapuaLocatorErrorCodes;
    Object argument1, argument2, argument3;
    Throwable[] throwables;
    String[] expectedMessageNullArguments;
    String[] expectedMessage;

    @Before
    public void initialize() {
        expectedErrorMessage = "kapua-locator-service-error-messages";
        // Removed line due to General Fixture smell: kapuaLocatorErrorCodes = new KapuaLocatorErrorCodes[]{KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, KapuaLocatorErrorCodes.SERVICE_PROVIDER_INVALID,
                KapuaLocatorErrorCodes.FACTORY_UNAVAILABLE, KapuaLocatorErrorCodes.FACTORY_PROVIDER_INVALID, KapuaLocatorErrorCodes.COMPONENT_UNAVAILABLE,
                KapuaLocatorErrorCodes.COMPONENT_PROVIDER_INVALID, KapuaLocatorErrorCodes.INVALID_CONFIGURATION};
        // Removed line due to General Fixture smell: argument1 = "argument1";
        // Removed line due to General Fixture smell: argument2 = 42;
        // Removed line due to General Fixture smell: argument3 = 'c';
        // Removed line due to General Fixture smell: throwables = new Throwable[]{new Throwable(), null};
        // Removed line due to General Fixture smell: expectedMessageNullArguments = new String[]{"Service unavailable {0}", "{0} is not a valid service provider for {1}", "Factory unavailable {0}",
                "{0} is not a valid factory provider for {1}", "Error: ", "Error: ", "Error: "};
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: expectedMessage = new String[]{"Service unavailable " + argument1, argument1 + " is not a valid service provider for " + argument2, "Factory unavailable " + argument1,
                // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: "Error: " + argument1 + ", " + argument2 + ", " + argument3};
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorErrorCodesParameterTest() {
        expectedMessageNullArguments = new String[]{"Service unavailable {0}", "{0} is not a valid service provider for {1}", "Factory unavailable {0}",
        kapuaLocatorErrorCodes = new KapuaLocatorErrorCodes[]{KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, KapuaLocatorErrorCodes.SERVICE_PROVIDER_INVALID,
        for (int i = 0; i < kapuaLocatorErrorCodes.length; i++) {
            KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(kapuaLocatorErrorCodes[i]);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaLocatorErrorCodes[i], kapuaLocatorException.getCode());
            Assert.assertNull("Null expected.", kapuaLocatorException.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", expectedMessageNullArguments[i], kapuaLocatorException.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
        }
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorNullErrorCodesParameterTest() {
        KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(null);
        Assert.assertNull("Null expected.", kapuaLocatorException.getCode());
        Assert.assertNull("Null expected.", kapuaLocatorException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
        Assert.assertEquals("Error: ", kapuaLocatorException.getMessage(), () -> "Add assertion message");
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorErrorCodesObjectParametersTest() {
        kapuaLocatorErrorCodes = new KapuaLocatorErrorCodes[]{KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, KapuaLocatorErrorCodes.SERVICE_PROVIDER_INVALID,
        argument3 = 'c';
        argument2 = 42;
                // Removed line due to General Fixture smell: argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                // Removed line due to General Fixture smell: "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        argument1 = "argument1";
        expectedMessage = new String[]{"Service unavailable " + argument1, argument1 + " is not a valid service provider for " + argument2, "Factory unavailable " + argument1,
                argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        for (int i = 0; i < kapuaLocatorErrorCodes.length; i++) {
            KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(kapuaLocatorErrorCodes[i], argument1, argument2, argument3);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaLocatorErrorCodes[i], kapuaLocatorException.getCode());
            Assert.assertNull("Null expected.", kapuaLocatorException.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", expectedMessage[i], kapuaLocatorException.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
        }
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorErrorCodesNullObjectParametersTest() {
        expectedMessageNullArguments = new String[]{"Service unavailable {0}", "{0} is not a valid service provider for {1}", "Factory unavailable {0}",
        kapuaLocatorErrorCodes = new KapuaLocatorErrorCodes[]{KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, KapuaLocatorErrorCodes.SERVICE_PROVIDER_INVALID,
        for (int i = 0; i < kapuaLocatorErrorCodes.length; i++) {
            KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(kapuaLocatorErrorCodes[i], null);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaLocatorErrorCodes[i], kapuaLocatorException.getCode());
            Assert.assertNull("Null expected.", kapuaLocatorException.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", expectedMessageNullArguments[i], kapuaLocatorException.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
        }
    }

    @Test
    public void kapuaLocatorExceptionNullKapuaLocatorErrorCodesObjectParametersTest() {
        argument3 = 'c';
        argument2 = 42;
                // Removed line due to General Fixture smell: argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                // Removed line due to General Fixture smell: "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        argument1 = "argument1";
        expectedMessage = new String[]{"Service unavailable " + argument1, argument1 + " is not a valid service provider for " + argument2, "Factory unavailable " + argument1,
                argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(null, argument1, argument2, argument3);
        Assert.assertNull("Null expected.", kapuaLocatorException.getCode());
        Assert.assertNull("Null expected.", kapuaLocatorException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
        Assert.assertEquals("Error: argument1, 42, c", kapuaLocatorException.getMessage(), () -> "Add assertion message");
    }

    @Test
    public void kapuaLocatorExceptionNullKapuaLocatorErrorCodesNullObjectParametersTest() {
        KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(null, null);
        Assert.assertNull("Null expected.", kapuaLocatorException.getCode());
        Assert.assertNull("Null expected.", kapuaLocatorException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
        Assert.assertEquals("Error: ", kapuaLocatorException.getMessage(), () -> "Add assertion message");
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorErrorCodesThrowableObjectParametersTest() {
        kapuaLocatorErrorCodes = new KapuaLocatorErrorCodes[]{KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, KapuaLocatorErrorCodes.SERVICE_PROVIDER_INVALID,
        throwables = new Throwable[]{new Throwable(), null};
        argument3 = 'c';
        argument2 = 42;
                // Removed line due to General Fixture smell: argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                // Removed line due to General Fixture smell: "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        argument1 = "argument1";
        expectedMessage = new String[]{"Service unavailable " + argument1, argument1 + " is not a valid service provider for " + argument2, "Factory unavailable " + argument1,
                argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        for (Throwable throwable : throwables) {
            for (int i = 0; i < kapuaLocatorErrorCodes.length; i++) {
                KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(kapuaLocatorErrorCodes[i], throwable, argument1, argument2, argument3);
                Assert.assertEquals("Expected and actual values should be the same.", kapuaLocatorErrorCodes[i], kapuaLocatorException.getCode());
                Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaLocatorException.getCause());
                Assert.assertEquals("Expected and actual values should be the same.", expectedMessage[i], kapuaLocatorException.getMessage());
                Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
            }
        }
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorErrorCodesThrowableNullObjectParametersTest() {
        expectedMessageNullArguments = new String[]{"Service unavailable {0}", "{0} is not a valid service provider for {1}", "Factory unavailable {0}",
        kapuaLocatorErrorCodes = new KapuaLocatorErrorCodes[]{KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, KapuaLocatorErrorCodes.SERVICE_PROVIDER_INVALID,
        throwables = new Throwable[]{new Throwable(), null};
        for (Throwable throwable : throwables) {
            for (int i = 0; i < kapuaLocatorErrorCodes.length; i++) {
                KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(kapuaLocatorErrorCodes[i], throwable, null);
                Assert.assertEquals("Expected and actual values should be the same.", kapuaLocatorErrorCodes[i], kapuaLocatorException.getCode());
                Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaLocatorException.getCause());
                Assert.assertEquals("Expected and actual values should be the same.", expectedMessageNullArguments[i], kapuaLocatorException.getMessage());
                Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
            }
        }
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorNullErrorCodesThrowableObjectParametersTest() {
        throwables = new Throwable[]{new Throwable(), null};
        argument3 = 'c';
        argument2 = 42;
                // Removed line due to General Fixture smell: argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                // Removed line due to General Fixture smell: "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        argument1 = "argument1";
        expectedMessage = new String[]{"Service unavailable " + argument1, argument1 + " is not a valid service provider for " + argument2, "Factory unavailable " + argument1,
                argument1 + " is not a valid factory provider for " + argument2, "Error: " + argument1 + ", " + argument2 + ", " + argument3, "Error: " + argument1 + ", " + argument2 + ", " + argument3,
                "Error: " + argument1 + ", " + argument2 + ", " + argument3};
        for (Throwable throwable : throwables) {
            KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(null, throwable, argument1, argument2, argument3);
            Assert.assertNull("Null expected.", kapuaLocatorException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaLocatorException.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
            Assert.assertEquals("Error: argument1, 42, c", kapuaLocatorException.getMessage(), () -> "Add assertion message");
        }
    }

    @Test
    public void kapuaLocatorExceptionKapuaLocatorNullErrorCodesThrowableNullObjectParametersTest() {
        throwables = new Throwable[]{new Throwable(), null};
        for (Throwable throwable : throwables) {
            KapuaLocatorException kapuaLocatorException = new KapuaLocatorException(null, throwable, null);
            Assert.assertNull("Null expected", kapuaLocatorException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaLocatorException.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaLocatorException.getKapuaErrorMessagesBundle());
            Assert.assertEquals("Error: ", kapuaLocatorException.getMessage(), () -> "Add assertion message");
        }
    }
}