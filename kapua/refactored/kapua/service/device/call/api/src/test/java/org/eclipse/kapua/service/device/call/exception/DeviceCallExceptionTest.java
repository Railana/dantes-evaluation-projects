/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.call.exception;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.device.call.exception.model.TestCodesDeviceCallException;
import org.eclipse.kapua.service.device.call.exception.model.TestDeviceMessage;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.MessageFormat;

/**
 * {@link DeviceCallException}s tests.
 *
 * @since 1.0.0
 */
@Category(JUnitTests.class)
public class DeviceCallExceptionTest {

    private final Throwable cause = new Throwable("This is the cause");

    private final Long aTimeout = 15000L;
    private final DeviceMessage<?, ?> deviceCallMessage = new TestDeviceMessage();

    @Test
    public void testDeviceCallErrorCodesHaveMessages() {
        for (DeviceCallErrorCodes errorCode : DeviceCallErrorCodes.values()) {
            DeviceCallException deviceCallException = new TestCodesDeviceCallException(errorCode);

            Assert.assertNotEquals("DeviceCallErrorCodes." + errorCode + " doesn't have an error message", "Error: ", deviceCallException.getMessage());
            Assert.assertNotEquals("DeviceCallErrorCodes." + errorCode + " doesn't have an error message", "Error: ", deviceCallException.getLocalizedMessage());
        }
    }

    @Test
    public void testDeviceCallSendException() {
        String exceptionMessage = "An error occurred when sending the message: " + deviceCallMessage;

        // Without cause
        DeviceCallSendException deviceCallSendException = new DeviceCallSendException(deviceCallMessage);

        Assert.assertEquals(DeviceCallErrorCodes.SEND_ERROR, deviceCallSendException.getCode(), () -> "Add assertion message");
        Assert.assertNull(deviceCallSendException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(deviceCallMessage, deviceCallSendException.getRequestMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, deviceCallSendException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, deviceCallSendException.getLocalizedMessage(), () -> "Add assertion message");

        // With cause
        exceptionMessage = exceptionMessage + ". Caused by: " + cause.getMessage();

        deviceCallSendException = new DeviceCallSendException(cause, deviceCallMessage);

        Assert.assertEquals(DeviceCallErrorCodes.SEND_ERROR_WITH_CAUSE, deviceCallSendException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, deviceCallSendException.getCause(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(deviceCallMessage, deviceCallSendException.getRequestMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, deviceCallSendException.getMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, deviceCallSendException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testDeviceCallTimeoutException() {
        String exceptionMessage = "The request has not received a response within the timeout of: " + MessageFormat.format("{0}", aTimeout) + "ms";

        DeviceCallTimeoutException deviceCallTimeoutException = new DeviceCallTimeoutException(cause, aTimeout);

        Assert.assertEquals(DeviceCallErrorCodes.TIMEOUT, deviceCallTimeoutException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, deviceCallTimeoutException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(aTimeout, deviceCallTimeoutException.getTimeout(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, deviceCallTimeoutException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, deviceCallTimeoutException.getLocalizedMessage(), () -> "Add assertion message");
    }
}