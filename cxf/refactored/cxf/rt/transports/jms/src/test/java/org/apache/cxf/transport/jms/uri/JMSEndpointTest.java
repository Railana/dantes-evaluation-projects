/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.transport.jms.uri;

import java.util.Map;

import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.jms.uri.JMSEndpoint.DeliveryModeType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JMSEndpointTest {

    private static final String TEST_VALUE = "testValue";

    @Test
    public void testBasicQueue() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:queue:Foo.Bar?concurrentConsumers=21");
        assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals("Foo.Bar", endpoint.getDestinationName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        Integer magicNumber20986484 = 21; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber20986484, endpoint.getConcurrentConsumers(), () -> "Add assertion message");
    }

    @Test
    public void testQueueParameters() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:queue:Foo.Bar?foo=bar&foo2=bar2&useConduitIdSelector=false");
        assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(endpoint.getDestinationName(), "Foo.Bar", () -> "Add assertion message");
        assertEquals(endpoint.getJmsVariant(), JMSEndpoint.QUEUE, () -> "Add assertion message");
        assertFalse(endpoint.isUseConduitIdSelector(), () -> "Add assertion message");
        assertEquals(endpoint.getParameters().size(), 2, () -> "Add assertion message");
        assertEquals(endpoint.getParameter("foo"), "bar", () -> "Add assertion message");
        assertEquals(endpoint.getParameter("foo2"), "bar2", () -> "Add assertion message");
    }

    @Test
    public void testBasicTopic() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:topic:Foo.Bar");
        assertEquals(JMSEndpoint.TOPIC, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(endpoint.getDestinationName(), "Foo.Bar", () -> "Add assertion message");
        assertEquals(endpoint.getJmsVariant(), JMSEndpoint.TOPIC, () -> "Add assertion message");
    }

    @Test
    public void testTopicParameters() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:topic:Foo.Bar?foo=bar&foo2=bar2");
        assertEquals(JMSEndpoint.TOPIC, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(endpoint.getParameters().size(), 2, () -> "Add assertion message");
        assertEquals(endpoint.getParameter("foo"), "bar", () -> "Add assertion message");
        assertEquals(endpoint.getParameter("foo2"), "bar2", () -> "Add assertion message");
    }

    @Test
    public void testBasicJNDI() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:jndi:Foo.Bar");
        assertEquals(JMSEndpoint.JNDI, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(endpoint.getDestinationName(), "Foo.Bar", () -> "Add assertion message");
        assertEquals(endpoint.getJmsVariant(), JMSEndpoint.JNDI, () -> "Add assertion message");
    }

    @Test
    public void testJNDIParameters() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:jndi:Foo.Bar?" + "jndiInitialContextFactory"
            + "=org.apache.activemq.jndi.ActiveMQInitialContextFactory"
            + "&jndiConnectionFactoryName=ConnectionFactory"
            + "&jndiURL=tcp://localhost:61616");
        assertEquals(JMSEndpoint.JNDI, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(endpoint.getParameters().size(), 0, () -> "Add assertion message");
        assertEquals(endpoint.getDestinationName(), "Foo.Bar", () -> "Add assertion message");
        assertEquals(endpoint.getJndiInitialContextFactory(),
                     "org.apache.activemq.jndi.ActiveMQInitialContextFactory", () -> "Add assertion message");
        assertEquals(endpoint.getJndiConnectionFactoryName(),
                     "ConnectionFactory", () -> "Add assertion message");
        assertEquals(endpoint.getJndiURL(), "tcp://localhost:61616", () -> "Add assertion message");
    }

    @Test
    public void testReplyToNameParameters() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:queue:Foo.Bar?replyToName=FOO.Tar");
        assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals("Foo.Bar", endpoint.getDestinationName(), () -> "Add assertion message");
        assertNull(endpoint.getTopicReplyToName(), () -> "Add assertion message");
        assertEquals("FOO.Tar", endpoint.getReplyToName(), () -> "Add assertion message");
        try {
            new JMSEndpoint("jms:queue:Foo.Bar?replyToName=FOO.Tar&topicReplyToName=FOO.Zar");
            fail("Expecting exception here");
        } catch (IllegalArgumentException ex) {
            // expect the exception
        }

        endpoint = new JMSEndpoint("jms:queue:Foo.Bar?topicReplyToName=FOO.Zar");
        // Line removed due to Duplicate Assert: assertEquals("Foo.Bar", endpoint.getDestinationName(), () -> "Add assertion message");
        assertNull(endpoint.getReplyToName(), () -> "Add assertion message");
        assertEquals("FOO.Zar", endpoint.getTopicReplyToName(), () -> "Add assertion message");
    }

    @Test
    public void testJNDIWithAdditionalParameters() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:jndi:Foo.Bar?" + "jndiInitialContextFactory"
            + "=org.apache.activemq.jndi.ActiveMQInitialContextFactory"
            + "&jndiConnectionFactoryName=ConnectionFactory"
            + "&jndiURL=tcp://localhost:61616"
            + "&jndi-com.sun.jndi.someParameter=someValue"
            + "&durableSubscriptionName=dur");
        assertEquals(JMSEndpoint.JNDI, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(endpoint.getParameters().size(), 0, () -> "Add assertion message");
        assertEquals("org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                     endpoint.getJndiInitialContextFactory(), () -> "Add assertion message");
        assertEquals("ConnectionFactory", endpoint.getJndiConnectionFactoryName(), () -> "Add assertion message");
        assertEquals("tcp://localhost:61616", endpoint.getJndiURL(), () -> "Add assertion message");
        assertEquals("dur", endpoint.getDurableSubscriptionName(), () -> "Add assertion message");
        Map<String, String> addParas = endpoint.getJndiParameters();
        assertEquals(1, addParas.size(), () -> "Add assertion message");
        assertEquals("someValue", addParas.get("com.sun.jndi.someParameter"), () -> "Add assertion message");
    }

    @Test
    public void testSharedParameters() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:queue:Foo.Bar?" + "deliveryMode=NON_PERSISTENT"
            + "&timeToLive=100" + "&priority=5" + "&replyToName=foo.bar2");
        assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(0, endpoint.getParameters().size(), () -> "Add assertion message");
        assertEquals(DeliveryModeType.NON_PERSISTENT, endpoint.getDeliveryMode(), () -> "Add assertion message");
        Integer magicNumber50537658 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber50537658, endpoint.getTimeToLive(), () -> "Add assertion message");
        assertEquals(5, endpoint.getPriority(), () -> "Add assertion message");
        assertEquals("foo.bar2", endpoint.getReplyToName(), () -> "Add assertion message");
    }

    @Test
    public void testRequestUri() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:jndi:Foo.Bar"
            + "?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory"
            + "&targetService=greetMe"
            + "&replyToName=replyQueue"
            + "&timeToLive=1000"
            + "&priority=3"
            + "&foo=bar"
            + "&foo2=bar2");
        assertEquals(JMSEndpoint.JNDI, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals(2, endpoint.getParameters().size(), () -> "Add assertion message");
        String requestUri = endpoint.getRequestURI();
        // Checking what's the request uri should have
        assertTrue(requestUri.startsWith("jms:jndi:Foo.Bar?"), () -> "Add assertion message");
        assertTrue(requestUri.contains("foo=bar"), () -> "Add assertion message");
        assertTrue(requestUri.contains("foo2=bar2"), () -> "Add assertion message");
        // Checking what's the request uri should not have
        assertFalse(requestUri.contains("jndiInitialContextFactory"), () -> "Add assertion message");
        assertFalse(requestUri.contains("targetService"), () -> "Add assertion message");
        assertFalse(requestUri.contains("replyToName"), () -> "Add assertion message");
        assertFalse(requestUri.contains("priority=3"), () -> "Add assertion message");
    }

    @Test
    public void testRequestUriWithMessageType() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms:queue:Foo.Bar?messageType=text");
        assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals("text", endpoint.getMessageType().value(), () -> "Add assertion message");

        endpoint = new JMSEndpoint("jms:queue:Foo.Bar");
        // Line removed due to Duplicate Assert: assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals("byte", endpoint.getMessageType().value(), () -> "Add assertion message");

        endpoint = new JMSEndpoint("jms:queue:Foo.Bar?messageType=binary");
        // Line removed due to Duplicate Assert: assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
        assertEquals("binary", endpoint.getMessageType().value(), () -> "Add assertion message");

    }

    @Test
    public void nonSoapJMS() throws Exception {
        JMSEndpoint endpoint = new JMSEndpoint("jms://");
        assertEquals(JMSEndpoint.QUEUE, endpoint.getJmsVariant(), () -> "Add assertion message");
    }

    @Test
    public void testTransactionManager() {
        JMSEndpoint endpoint = new JMSEndpoint("jms:queue:Foo.Bar?jndiTransactionManagerName=test");
        assertEquals("test", endpoint.getJndiTransactionManagerName(), () -> "Add assertion message");
    }

    @Test
    public void testJaxWsProps() throws Exception {
        EndpointInfo ei = new EndpointInfo();
        ei.setProperty(JMSEndpoint.JAXWS_PROPERTY_PREFIX + "durableSubscriptionName", TEST_VALUE);
        JMSEndpoint endpoint = new JMSEndpoint(ei, "jms:queue:Foo.Bar");
        assertEquals(endpoint.getDurableSubscriptionName(), TEST_VALUE, () -> "Add assertion message");
    }

}