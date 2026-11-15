/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.exception;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class RestApiRuntimeExceptionTest {

    RestApiErrorCodes errorCode;
    Object stringObject, intObject, charObject;
    Throwable cause;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: errorCode = RestApiErrorCodes.SESSION_NOT_POPULATED;
        // Removed line due to General Fixture smell: stringObject = "String Object";
        // Removed line due to General Fixture smell: intObject = 10;
        // Removed line due to General Fixture smell: charObject = 'c';
        // Removed line due to General Fixture smell: cause = new Throwable();
    }

    @Test
    public void restApiRuntimeExceptionCodeTest() {
        errorCode = RestApiErrorCodes.SESSION_NOT_POPULATED;
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(errorCode);

        Assert.assertEquals("Expected and actual values should be the same.", RestApiErrorCodes.SESSION_NOT_POPULATED, restApiRuntimeException.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", "Error: ", restApiRuntimeException.getMessage());
        Assert.assertNull("Null expected.", restApiRuntimeException.getCause());
    }

    @Test
    public void restApiRuntimeExceptionNullCodeTest() {
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(null);

        Assert.assertNull("Null expected.", restApiRuntimeException.getCode());
        Assert.assertNull("Null expected.", restApiRuntimeException.getCause());
        Assert.assertEquals("Error: ", restApiRuntimeException.getMessage(), () -> "Add assertion message");
    }

    @Test
    public void restApiRuntimeExceptionCodeArgumentsTest() {
        errorCode = RestApiErrorCodes.SESSION_NOT_POPULATED;
        charObject = 'c';
        intObject = 10;
        stringObject = "String Object";
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(errorCode, stringObject, intObject, charObject);

        Assert.assertEquals("Expected and actual values should be the same.", RestApiErrorCodes.SESSION_NOT_POPULATED, restApiRuntimeException.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", "Error: " + stringObject + ", " + intObject + ", " + charObject, restApiRuntimeException.getMessage());
        Assert.assertNull("Null expected.", restApiRuntimeException.getCause());
    }

    @Test
    public void restApiRuntimeExceptionNullCodeArgumentsTest() {
        charObject = 'c';
        intObject = 10;
        stringObject = "String Object";
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(null, stringObject, intObject, charObject);

        Assert.assertNull("Null expected.", restApiRuntimeException.getCode());
        Assert.assertNull("Null expected.", restApiRuntimeException.getCause());
        Assert.assertEquals("Error: String Object, 10, c", restApiRuntimeException.getMessage(), () -> "Add assertion message");
    }

    @Test
    public void restApiRuntimeExceptionCodeNullArgumentsTest() {
        errorCode = RestApiErrorCodes.SESSION_NOT_POPULATED;
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(errorCode, null);

        Assert.assertEquals("Expected and actual values should be the same.", RestApiErrorCodes.SESSION_NOT_POPULATED, restApiRuntimeException.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", "Error: ", restApiRuntimeException.getMessage());
        Assert.assertNull("Null expected.", restApiRuntimeException.getCause());
    }

    @Test
    public void restApiRuntimeExceptionCodeCauseArgumentsTest() {
        errorCode = RestApiErrorCodes.SESSION_NOT_POPULATED;
        cause = new Throwable();
        charObject = 'c';
        intObject = 10;
        stringObject = "String Object";
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(errorCode, cause, stringObject, intObject, charObject);

        Assert.assertEquals("Expected and actual values should be the same.", RestApiErrorCodes.SESSION_NOT_POPULATED, restApiRuntimeException.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", "Error: " + stringObject + ", " + intObject + ", " + charObject, restApiRuntimeException.getMessage());
        Assert.assertEquals("Expected and actual values should be the same.", cause, restApiRuntimeException.getCause());
    }

    @Test
    public void restApiRuntimeExceptionNullCodeCauseArgumentsTest() {
        cause = new Throwable();
        charObject = 'c';
        intObject = 10;
        stringObject = "String Object";
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(null, cause, stringObject, intObject, charObject);

        Assert.assertNull("Null expected.", restApiRuntimeException.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", cause, restApiRuntimeException.getCause());
        Assert.assertEquals("Error: String Object, 10, c", restApiRuntimeException.getMessage(), () -> "Add assertion message");
    }

    @Test
    public void restApiRuntimeExceptionCodeNullCauseArgumentsTest() {
        errorCode = RestApiErrorCodes.SESSION_NOT_POPULATED;
        charObject = 'c';
        intObject = 10;
        stringObject = "String Object";
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(errorCode, null, stringObject, intObject, charObject);

        Assert.assertEquals("Expected and actual values should be the same.", RestApiErrorCodes.SESSION_NOT_POPULATED, restApiRuntimeException.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", "Error: " + stringObject + ", " + intObject + ", " + charObject, restApiRuntimeException.getMessage());
        Assert.assertNull("Null expected.", restApiRuntimeException.getCause());
    }

    @Test
    public void restApiRuntimeExceptionCodeCauseNullArgumentsTest() {
        errorCode = RestApiErrorCodes.SESSION_NOT_POPULATED;
        cause = new Throwable();
        RestApiRuntimeException restApiRuntimeException = new RestApiRuntimeException(errorCode, cause, null);

        Assert.assertEquals("Expected and actual values should be the same.", RestApiErrorCodes.SESSION_NOT_POPULATED, restApiRuntimeException.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", "Error: ", restApiRuntimeException.getMessage());
        Assert.assertEquals("Expected and actual values should be the same.", cause, restApiRuntimeException.getCause());
    }
}