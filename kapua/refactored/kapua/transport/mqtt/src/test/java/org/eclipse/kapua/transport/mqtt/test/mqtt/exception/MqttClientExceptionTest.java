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
package org.eclipse.kapua.transport.mqtt.test.mqtt.exception;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientAlreadyConnectedException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientCallbackSetException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientCleanException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientConnectException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientDisconnectException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientErrorCodes;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientNotConnectedException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientPublishException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientSubscribeException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientTerminateException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientUnsubscribeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

@Category(JUnitTests.class)
public class MqttClientExceptionTest {

    private final Throwable cause = new Throwable("This is the cause");

    private final String clientId = "clientId";
    private final String username = "username";
    private final URI uri;

    private final MqttTopic mqttTopic = new MqttTopic("mqttTopic");
    private final MqttPayload mqttPayload = new MqttPayload("mqttPayload".getBytes());
    private final MqttMessage mqttMessage = new MqttMessage(mqttTopic, new Date(), mqttPayload);

    @Before
    public void setUp() {
        this.uri = new URI("mqtt://localhost:1883");
    }

    @Test
    public void testMqttClientErrorCodesHaveMessages() {
        for (MqttClientErrorCodes errorCode : MqttClientErrorCodes.values()) {
            MqttClientException mqttClientException = new MqttClientException(errorCode, clientId);

            Assert.assertNotEquals("Error: clientId", mqttClientException.getMessage());
            Assert.assertNotEquals("Error: clientId", mqttClientException.getLocalizedMessage());
        }
    }

    @Test
    public void testMqttClientAlreadyConnectedException() {
        String exceptionMessage = "MqttClient " + clientId + " is already connected.";

        MqttClientAlreadyConnectedException mqttClientAlreadyConnectedException = new MqttClientAlreadyConnectedException(clientId);

        Assert.assertEquals(MqttClientErrorCodes.ALREADY_CONNECTED, mqttClientAlreadyConnectedException.getCode(), () -> "Add assertion message");
        Assert.assertNull(mqttClientAlreadyConnectedException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientAlreadyConnectedException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientAlreadyConnectedException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientAlreadyConnectedException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientCallbackSetException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot set response callback on topic: " + mqttTopic;

        MqttClientCallbackSetException mqttClientCallbackSetException = new MqttClientCallbackSetException(cause, clientId, mqttTopic);

        Assert.assertEquals(MqttClientErrorCodes.CALLBACK_SET_ERROR, mqttClientCallbackSetException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientCallbackSetException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientCallbackSetException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(mqttTopic, mqttClientCallbackSetException.getTopic(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientCallbackSetException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientCallbackSetException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientCleanException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot be terminated properly. This can lead to resource leaks.";

        MqttClientCleanException mqttClientCleanException = new MqttClientCleanException(cause, clientId);

        Assert.assertEquals(MqttClientErrorCodes.CLEAN_ERROR, mqttClientCleanException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientCleanException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientCleanException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientCleanException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientCleanException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientConnectException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot connect to " + uri + " with username " + username;

        // Without cause
        MqttClientConnectException mqttClientConnectException = new MqttClientConnectException(clientId, username, uri);

        Assert.assertEquals(MqttClientErrorCodes.CONNECT_ERROR, mqttClientConnectException.getCode(), () -> "Add assertion message");
        Assert.assertNull(mqttClientConnectException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientConnectException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(username, mqttClientConnectException.getUsername(), () -> "Add assertion message");
        Assert.assertEquals(uri, mqttClientConnectException.getUri(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientConnectException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientConnectException.getLocalizedMessage(), () -> "Add assertion message");

        // With cause
        mqttClientConnectException = new MqttClientConnectException(cause, clientId, username, uri);

        // Line removed due to Duplicate Assert: Assert.assertEquals(MqttClientErrorCodes.CONNECT_ERROR, mqttClientConnectException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientConnectException.getCause(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(clientId, mqttClientConnectException.getClientId(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(username, mqttClientConnectException.getUsername(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(uri, mqttClientConnectException.getUri(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, mqttClientConnectException.getMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, mqttClientConnectException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientDisconnectException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot be disconnected properly.";

        MqttClientDisconnectException mqttClientDisconnectException = new MqttClientDisconnectException(cause, clientId);

        Assert.assertEquals(MqttClientErrorCodes.DISCONNECT_ERROR, mqttClientDisconnectException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientDisconnectException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientDisconnectException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientDisconnectException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientDisconnectException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot connect to {2} with username {1}";

        // Without cause
        MqttClientException mqttClientException = new MqttClientException(MqttClientErrorCodes.CONNECT_ERROR, clientId);

        Assert.assertEquals(MqttClientErrorCodes.CONNECT_ERROR, mqttClientException.getCode(), () -> "Add assertion message");
        Assert.assertNull(mqttClientException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientException.getLocalizedMessage(), () -> "Add assertion message");

        // With cause
        mqttClientException = new MqttClientException(MqttClientErrorCodes.CONNECT_ERROR, cause, clientId);

        // Line removed due to Duplicate Assert: Assert.assertEquals(MqttClientErrorCodes.CONNECT_ERROR, mqttClientException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientException.getCause(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(clientId, mqttClientException.getClientId(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, mqttClientException.getMessage(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(exceptionMessage, mqttClientException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientNotConnectedException() {
        String exceptionMessage = "MqttClient " + clientId + " is not connected.";

        MqttClientNotConnectedException mqttClientNotConnectedException = new MqttClientNotConnectedException(clientId);

        Assert.assertEquals(MqttClientErrorCodes.NOT_CONNECTED, mqttClientNotConnectedException.getCode(), () -> "Add assertion message");
        Assert.assertNull(mqttClientNotConnectedException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientNotConnectedException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientNotConnectedException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientNotConnectedException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientPublishException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot publish MqttMessage(" + mqttPayload.getBody().length + " bytes) to topic: " + mqttTopic;

        MqttClientPublishException mqttClientPublishException = new MqttClientPublishException(cause, clientId, mqttTopic, mqttMessage);

        Assert.assertEquals(MqttClientErrorCodes.PUBLISH_ERROR, mqttClientPublishException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientPublishException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientPublishException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(mqttTopic, mqttClientPublishException.getTopic(), () -> "Add assertion message");
        Assert.assertEquals(mqttMessage, mqttClientPublishException.getMqttMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientPublishException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientPublishException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientSubscribeException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot subscribe to topic: " + mqttTopic;

        MqttClientSubscribeException mqttClientSubscribeException = new MqttClientSubscribeException(cause, clientId, mqttTopic);

        Assert.assertEquals(MqttClientErrorCodes.SUBSCRIBE_ERROR, mqttClientSubscribeException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientSubscribeException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientSubscribeException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(mqttTopic, mqttClientSubscribeException.getTopic(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientSubscribeException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientSubscribeException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientTerminateException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot be terminated properly. This can lead to resource leaks.";

        MqttClientTerminateException mqttClientTerminateException = new MqttClientTerminateException(cause, clientId);

        Assert.assertEquals(MqttClientErrorCodes.TERMINATE_ERROR, mqttClientTerminateException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientTerminateException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientTerminateException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientTerminateException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientTerminateException.getLocalizedMessage(), () -> "Add assertion message");
    }

    @Test
    public void testMqttClientUnsubscribeException() {
        String exceptionMessage = "MqttClient " + clientId + " cannot unsubscribe from topic: " + mqttTopic;

        MqttClientUnsubscribeException mqttClientSubscribeException = new MqttClientUnsubscribeException(cause, clientId, mqttTopic);

        Assert.assertEquals(MqttClientErrorCodes.UNSUBSCRIBE_ERROR, mqttClientSubscribeException.getCode(), () -> "Add assertion message");
        Assert.assertEquals(cause, mqttClientSubscribeException.getCause(), () -> "Add assertion message");
        Assert.assertEquals(clientId, mqttClientSubscribeException.getClientId(), () -> "Add assertion message");
        Assert.assertEquals(mqttTopic, mqttClientSubscribeException.getTopic(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientSubscribeException.getMessage(), () -> "Add assertion message");
        Assert.assertEquals(exceptionMessage, mqttClientSubscribeException.getLocalizedMessage(), () -> "Add assertion message");
    }
}