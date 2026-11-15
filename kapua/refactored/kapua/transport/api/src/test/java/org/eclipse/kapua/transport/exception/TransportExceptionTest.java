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
package org.eclipse.kapua.transport.exception;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.transport.exception.model.TestCodesTransportClientException;
import org.eclipse.kapua.transport.exception.model.TestTransportMessage;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.MessageFormat;

/**
 * {@link TransportException}s tests.
 *
 * @since 1.0.0
 */
@Category(JUnitTests.class)
public class TransportExceptionTest {

    private final Throwable cause = new Throwable("This is the cause");

    private final String serverIp = "serverIp";
    private final Long aTimeout = 15000L;
    private final TransportMessage<?, ?> transportMessage = new TestTransportMessage();

    @Test
    public void testTransportErrorCodesHaveMessages() {
        for (TransportErrorCodes errorCode : TransportErrorCodes.values()) {
            TransportException mqttClientException = new TestCodesTransportClientException(errorCode);

            Assert.assertNotEquals("TransportErrorCodes." + errorCode + " doesn't have an error message", "Error: ", mqttClientException.getMessage());
            Assert.assertNotEquals("TransportErrorCodes." + errorCode + " doesn't have an error message", "Error: ", mqttClientException.getLocalizedMessage());
        }
    }

    @Test
    public void testTransportClientGetException() {
        String exceptionMessage = "Cannot get an instance of the TransportClient to connect to host: " + serverIp;

        // Without cause
        TransportClientGetException transportClientGetException = new TransportClientGetException(serverIp);

        Assert.assertEquals(TransportErrorCodes.CLIENT_GET, transportClientGetException.getCode(), () -> "Add assertion message");
        Assert.assertNull(transportClientGetException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(serverIp, transportClientGetException.getServerIp(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportClientGetException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportClientGetException.getLocalizedMessage(), () -> "Add assertion message");

        // With cause
        exceptionMessage = exceptionMessage + ". Caused by: " + cause.getMessage();

        transportClientGetException = new TransportClientGetException(cause, serverIp);

        Assert.assertEquals(TransportErrorCodes.CLIENT_GET_WITH_CAUSE, transportClientGetException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, transportClientGetException.getCause(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(serverIp, transportClientGetException.getServerIp(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, transportClientGetException.getMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, transportClientGetException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testTransportClientPoolExhaustedException() {
        String exceptionMessage = "Cannot get an instance of the TransportClient to connect to host " + serverIp + " within the configured timeout of " + MessageFormat.format("{0}", aTimeout) + "ms";

        // Without cause
        TransportClientPoolExhaustedException transportClientPoolExhaustedException = new TransportClientPoolExhaustedException(serverIp, aTimeout);

        Assert.assertEquals(TransportErrorCodes.CLIENT_POOL_EXHAUSTED, transportClientPoolExhaustedException.getCode(), () -> "Add assertion message");
        Assert.assertNull(transportClientPoolExhaustedException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(serverIp, transportClientPoolExhaustedException.getServerIp(), () -> "Add assertion message");
        Assert.assertEquals(aTimeout, transportClientPoolExhaustedException.getBorrowWaitTimeout(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportClientPoolExhaustedException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportClientPoolExhaustedException.getLocalizedMessage(), () -> "Add assertion message");

        // With cause
        transportClientPoolExhaustedException = new TransportClientPoolExhaustedException(cause, serverIp, aTimeout);

        // Line removed due to Duplicate Assert: Assert.assertEquals(TransportErrorCodes.CLIENT_POOL_EXHAUSTED, transportClientPoolExhaustedException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, transportClientPoolExhaustedException.getCause(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(serverIp, transportClientPoolExhaustedException.getServerIp(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(aTimeout, transportClientPoolExhaustedException.getBorrowWaitTimeout(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, transportClientPoolExhaustedException.getMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, transportClientPoolExhaustedException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testTransportSendException() {
        String exceptionMessage = "An error occurred when sending the message: " + transportMessage;

        // Without cause
        TransportSendException transportSendException = new TransportSendException(transportMessage);

        Assert.assertEquals(TransportErrorCodes.SEND_ERROR, transportSendException.getCode(), () -> "Add assertion message");
        Assert.assertNull(transportSendException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(transportMessage, transportSendException.getRequestMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportSendException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportSendException.getLocalizedMessage(), () -> "Add assertion message");

        // With cause
        exceptionMessage = exceptionMessage + ". Caused by: " + cause.getMessage();

        transportSendException = new TransportSendException(cause, transportMessage);

        Assert.assertEquals(TransportErrorCodes.SEND_ERROR_WITH_CAUSE, transportSendException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, transportSendException.getCause(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(transportMessage, transportSendException.getRequestMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, transportSendException.getMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, transportSendException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testTransportTimeoutException() {
        String exceptionMessage = "The request has not received a response within the timeout of: " + MessageFormat.format("{0}", aTimeout) + "ms";

        TransportTimeoutException transportTimeoutException = new TransportTimeoutException(aTimeout);

        Assert.assertEquals(TransportErrorCodes.TIMEOUT, transportTimeoutException.getCode(), () -> "Add assertion message");
        Assert.assertNull(transportTimeoutException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(aTimeout, transportTimeoutException.getTimeout(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportTimeoutException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, transportTimeoutException.getLocalizedMessage(), () -> "Add assertion message");
    }
}