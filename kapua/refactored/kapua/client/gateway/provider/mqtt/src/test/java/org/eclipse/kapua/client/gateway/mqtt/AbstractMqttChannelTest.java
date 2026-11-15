/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.client.gateway.mqtt;

import org.eclipse.kapua.client.gateway.BinaryPayloadCodec;
import org.eclipse.kapua.client.gateway.Credentials;
import org.eclipse.kapua.client.gateway.ErrorHandler;
import org.eclipse.kapua.client.gateway.MessageHandler;
import org.eclipse.kapua.client.gateway.Payload;
import org.eclipse.kapua.client.gateway.Topic;
import org.eclipse.kapua.client.gateway.spi.Channel;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;


@Category(JUnitTests.class)
public class AbstractMqttChannelTest {

    private BinaryPayloadCodec codec;
    private MqttNamespace namespace;
    private String clientId;
    private AbstractMqttChannel mqttChannel;
    private AbstractMqttChannel.Builder builder;

    @Before
    public void init() {
        // Removed line due to General Fixture smell: codec = Mockito.mock(BinaryPayloadCodec.class);
        // Removed line due to General Fixture smell: namespace = Mockito.mock(MqttNamespace.class);
        // Removed line due to General Fixture smell: clientId = "clientId";
        // Removed line due to General Fixture smell: mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        // Removed line due to General Fixture smell: builder = new ActualBuilder();
    }

    private class ActualMqttChannel extends AbstractMqttChannel {

        public ActualMqttChannel(BinaryPayloadCodec codec, MqttNamespace namespace, String clientId) {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        clientId = "clientId";
        namespace = Mockito.mock(MqttNamespace.class);
            super(codec, namespace, clientId);
        }

        @Override
        protected CompletionStage<?> publishMqtt(String topic, ByteBuffer payload) {
            return null;
        }

        @Override
        protected CompletionStage<?> subscribeMqtt(String topic, MqttMessageHandler messageHandler) {
            return null;
        }

        @Override
        protected void unsubscribeMqtt(Set<String> mqttTopics) throws Exception {

        }

        @Override
        public void handleInit(Context context) {

        }

        @Override
        public void handleClose(Context context) {
        builder = new ActualBuilder();

        }
    }

    private class ActualBuilder extends AbstractMqttChannel.Builder {

        @Override
        protected AbstractMqttChannel.Builder builder() {
            return null;
        }

        @Override
        public Channel build() throws Exception {
            return null;
        }
    }

    @Test
    public void builderCodecTest() {
        builder = new ActualBuilder();
        codec = Mockito.mock(BinaryPayloadCodec.class);
        Assert.assertNull("Expected null value.", builder.codec());
        builder.codec(codec);
        Assert.assertEquals("Expected and actual values should be the same.", codec, builder.codec());
        Assert.assertThat("Instance of BinaryPayloadCodec expected", builder.codec(), IsInstanceOf.instanceOf(BinaryPayloadCodec.class));
    }

    @Test
    public void builderCodecNullTest() {
        builder = new ActualBuilder();
        codec = Mockito.mock(BinaryPayloadCodec.class);
        builder.codec(null);
        Assert.assertNull("Expected null value.", builder.codec());
    }

    @Test
    public void builderNamespaceTest() {
        builder = new ActualBuilder();
        namespace = Mockito.mock(MqttNamespace.class);
        Assert.assertNull("Expected null value.", builder.namespace());
        builder.namespace(namespace);
        Assert.assertEquals("Expected and actual values should be the same.", namespace, builder.namespace());
        Assert.assertThat("Instance of MqttNamespace expected", builder.namespace(), IsInstanceOf.instanceOf(MqttNamespace.class));
    }

    @Test
    public void builderNamespaceNullTest() {
        builder = new ActualBuilder();
        namespace = Mockito.mock(MqttNamespace.class);
        builder.namespace(null);
        Assert.assertNull("Expected null value.", builder.namespace());
    }

    @Test
    public void builderClientIdTest() {
        builder = new ActualBuilder();
        clientId = "clientId";
        Assert.assertNull("Expected null value.", builder.clientId());
        builder.clientId(clientId);
        Assert.assertEquals("Expected and actual values should be the same.", clientId, builder.clientId());
        Assert.assertThat("Instance of String expected", builder.clientId(), IsInstanceOf.instanceOf(String.class));
    }

    @Test
    public void builderClientIdNullTest() {
        builder = new ActualBuilder();
        clientId = "clientId";
        builder.clientId(null);
        Assert.assertNull("Expected null value.", builder.clientId());
    }

    @Test
    public void builderCredentialsTest() {
        builder = new ActualBuilder();
        Assert.assertNull("Expected null value.", builder.credentials());
        final Credentials.UserAndPassword userAndPassword = Credentials.userAndPassword("kapua-broker", "kapua-password");
        builder.credentials(userAndPassword);
        Assert.assertEquals("Expected and actual values should be the same.", userAndPassword, builder.credentials());
        Assert.assertThat("Instance of Credentials.UserAndPassword expected", builder.credentials(), IsInstanceOf.instanceOf(Credentials.UserAndPassword.class));
    }

    @Test
    public void builderCredentialsNullTest() {
        builder = new ActualBuilder();
        builder.credentials(null);
        Assert.assertNull("Expected null value.", builder.credentials());
    }

    @Test
    public void builderBrokerStringIdTest() throws URISyntaxException {
        builder = new ActualBuilder();
        Assert.assertNull("Expected null value.", builder.broker());
        final String broker = "broker";
        builder.broker(broker);
        Assert.assertEquals("Expected and actual values should be the same.", new URI(broker), builder.broker());
        Assert.assertThat("Instance of URI expected", builder.broker(), IsInstanceOf.instanceOf(URI.class));

    }

    @Test(expected = NullPointerException.class)
    public void builderBrokerStringIdNullTest() throws URISyntaxException {
        builder = new ActualBuilder();
        builder.broker((String) null);
        builder.broker();
    }

    @Test
    public void builderBrokerURIIdTest() throws URISyntaxException {
        builder = new ActualBuilder();
        Assert.assertNull("Expected null value.", builder.broker());
        final URI broker = new URI("string");
        builder.broker(broker);
        Assert.assertEquals("Expected and actual values should be the same.", broker, builder.broker());
        Assert.assertThat("Instance of URI expected", builder.broker(), IsInstanceOf.instanceOf(URI.class));
    }

    @Test(expected = NullPointerException.class)
    public void builderBrokerURIIdNullTest() throws URISyntaxException {
        builder = new ActualBuilder();
        builder.broker((URI) null);
        builder.broker();
    }

    @Test
    public void abstractMqttChannelTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        clientId = "clientId";
        Assert.assertEquals("Expected and actual values should be the same.", codec, mqttChannel.getCodec());
        Assert.assertEquals("Expected and actual values should be the same.", clientId, mqttChannel.getMqttClientId());
    }

    @Test
    public void abstractMqttChannelCodecNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        clientId = "clientId";
        namespace = Mockito.mock(MqttNamespace.class);
        AbstractMqttChannel mqttChannel = new ActualMqttChannel(null, namespace, clientId);
        Assert.assertNull("Expected null value.", mqttChannel.getCodec());
        Assert.assertEquals("Expected and actual values should be the same.", clientId, mqttChannel.getMqttClientId());
    }

    @Test
    public void abstractMqttChannelNamespaceNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        clientId = "clientId";
        AbstractMqttChannel mqttChannel = new ActualMqttChannel(codec, null, clientId);
        Assert.assertEquals("Expected and actual values should be the same.", codec, mqttChannel.getCodec());
        Assert.assertEquals("Expected and actual values should be the same.", clientId, mqttChannel.getMqttClientId());
    }

    @Test
    public void abstractMqttChannelNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        AbstractMqttChannel mqttChannel = new ActualMqttChannel(null, null, null);
        Assert.assertNull("Expected null value.", mqttChannel.getCodec());
        Assert.assertNull("Expected null value.", mqttChannel.getMqttClientId());
    }

    @Test
    public void abstractMqttChannelClientIdNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        namespace = Mockito.mock(MqttNamespace.class);
        AbstractMqttChannel mqttChannel = new ActualMqttChannel(codec, namespace, null);
        Assert.assertEquals("Expected and actual values should be the same.", codec, mqttChannel.getCodec());
        Assert.assertNull("Expected null.", mqttChannel.getMqttClientId());
    }

    @Test
    public void adaptTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        mqttChannel.adapt(MqttModuleContext.class);
        mqttChannel.adapt(String.class);
    }

    @Test(expected = NullPointerException.class)
    public void adaptClazzNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        mqttChannel.adapt(null);
    }

    @Test
    public void publishTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        segments.add("STRING");
        segments.add("0123456789");
        final Topic topic = Topic.of(segments);
        final ByteBuffer buffer = Mockito.mock(ByteBuffer.class);
        Assert.assertNull("Expected null value.", mqttChannel.publish(applicationId, topic, buffer));
    }

    @Test
    public void publishApplicationIdNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        List<String> segments = new ArrayList<>();
        segments.add("string");
        segments.add("STRING");
        segments.add("0123456789");
        final Topic topic = Topic.of(segments);
        final ByteBuffer buffer = Mockito.mock(ByteBuffer.class);
        Assert.assertNull("Expected null value.", mqttChannel.publish(null, topic, buffer));
    }

    @Test
    public void publishTopicNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        final ByteBuffer buffer = Mockito.mock(ByteBuffer.class);
        Assert.assertNull("Expected null value.", mqttChannel.publish(applicationId, null, buffer));
    }

    @Test
    public void publishByteBufferNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        segments.add("STRING");
        segments.add("0123456789");
        final Topic topic = Topic.of(segments);
        Assert.assertNull("Expected null value.", mqttChannel.publish(applicationId, topic, null));
    }

    @Test
    public void handlePublishTest() throws Exception {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        final Payload payload = Mockito.mock(Payload.class);
        Mockito.when(codec.encode(payload, null)).thenReturn(ByteBuffer.allocate(10));
        mqttChannel.handlePublish(applicationId, topic, payload);
    }

    @Test
    public void handlePublishApplicationIdNullTest() throws Exception {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        final Payload payload = Mockito.mock(Payload.class);
        Assert.assertThat("Instance of CompletionStage expected", mqttChannel.handlePublish(null, topic, payload), IsInstanceOf.instanceOf(CompletionStage.class));
    }

    @Test
    public void handlePublishTopicNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        final Payload payload = Mockito.mock(Payload.class);
        Assert.assertThat("Instance of CompletionStage expected", mqttChannel.handlePublish(applicationId, null, payload), IsInstanceOf.instanceOf(CompletionStage.class));
    }

    @Test(expected = NullPointerException.class)
    public void handlePublishPayloadNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        mqttChannel.handlePublish(applicationId, topic, null);
    }

    @Test
    public void handleSubscribeTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        final MessageHandler messageHandler = Mockito.mock(MessageHandler.class);
        final ErrorHandler<Exception> errorHandler = Mockito.mock(ErrorHandler.class);
        Assert.assertNull("Expected null value.", mqttChannel.handleSubscribe(applicationId, topic, messageHandler, errorHandler));
    }

    @Test
    public void handleSubscribeApplicationIdNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        final MessageHandler messageHandler = Mockito.mock(MessageHandler.class);
        final ErrorHandler<Exception> errorHandler = Mockito.mock(ErrorHandler.class);
        Assert.assertNull("Expected null value.", mqttChannel.handleSubscribe(null, topic, messageHandler, errorHandler));
    }

    @Test
    public void handleSubscribeTopicNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        final MessageHandler messageHandler = Mockito.mock(MessageHandler.class);
        final ErrorHandler<Exception> errorHandler = Mockito.mock(ErrorHandler.class);
        Assert.assertNull("Expected null value.", mqttChannel.handleSubscribe(applicationId, null, messageHandler, errorHandler));
    }

    @Test
    public void handleSubscribeMessageHandlerNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        final ErrorHandler<Exception> errorHandler = Mockito.mock(ErrorHandler.class);
        Assert.assertNull("Expected null value.", mqttChannel.handleSubscribe(applicationId, topic, null, errorHandler));
    }

    @Test
    public void handleSubscribeErrorHandlerNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        final MessageHandler messageHandler = Mockito.mock(MessageHandler.class);
        Assert.assertNull("Expected null value.", mqttChannel.handleSubscribe(applicationId, topic, messageHandler, null));
    }

    @Test
    public void handleMessage() throws Exception {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final MessageHandler handler = Mockito.mock(MessageHandler.class);
        final ByteBuffer buffer = Mockito.mock(ByteBuffer.class);
        mqttChannel.handleMessage(handler, buffer);
        Mockito.verify(handler, Mockito.times(1)).handleMessage(codec.decode(buffer));
    }

    @Test
    public void subscribeTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        final MqttMessageHandler messageHandler = Mockito.mock(MqttMessageHandler.class);
        Assert.assertNull("Expected null value.", mqttChannel.subscribe(applicationId, topic, messageHandler));
    }

    @Test
    public void handleUnsubscribeTest() throws Exception {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        clientId = "clientId";
        namespace = Mockito.mock(MqttNamespace.class);
        final String applicationId = "appId";
        Collection<Topic> topics = new ArrayList<>();
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        topics.add(topic);
        mqttChannel.handleUnsubscribe(applicationId, topics);
        Mockito.verify(namespace, Mockito.times(1)).dataTopic(clientId, applicationId, topic);
    }

    @Test
    public void handleUnsubscribeApplicationIdNullTest() throws Exception {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        clientId = "clientId";
        namespace = Mockito.mock(MqttNamespace.class);
        Collection<Topic> topics = new ArrayList<>();
        List<String> segments = new ArrayList<>();
        segments.add("string");
        final Topic topic = Topic.of(segments);
        topics.add(topic);
        mqttChannel.handleUnsubscribe(null, topics);
        Mockito.verify(namespace, Mockito.times(1)).dataTopic(clientId, null, topic);
    }

    @Test(expected = NullPointerException.class)
    public void handleUnsubscribeTopicsNullTest() throws Exception {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        final String applicationId = "appId";
        mqttChannel.handleUnsubscribe(applicationId, null);
    }

    @Test
    public void getMqttClientIdTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        clientId = "clientId";
        Assert.assertEquals("Expected and actual values should be the same.", clientId, mqttChannel.getMqttClientId());
    }

    @Test
    public void getMqttClientIdNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        namespace = Mockito.mock(MqttNamespace.class);
        AbstractMqttChannel mqttChannel = new ActualMqttChannel(codec, namespace, null);
        Assert.assertNull("Expected null value.", mqttChannel.getMqttClientId());
    }

    @Test
    public void getCodecTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        Assert.assertEquals("Expected and actual values should be the same.", codec, mqttChannel.getCodec());
    }

    @Test
    public void getCodecNullTest() {
        codec = Mockito.mock(BinaryPayloadCodec.class);
        mqttChannel = new ActualMqttChannel(codec, namespace, clientId);
        clientId = "clientId";
        namespace = Mockito.mock(MqttNamespace.class);
        AbstractMqttChannel mqttChannel = new ActualMqttChannel(null, namespace, clientId);
        Assert.assertNull("Expected null value.", mqttChannel.getCodec());
    }
}