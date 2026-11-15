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
package org.eclipse.kapua.broker.core.pool;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import javax.jms.Session;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.JMSException;

@Category(JUnitTests.class)
public class JmsProducerWrapperTest extends Assert {

    private class JmsProducerWrapperImpl extends JmsProducerWrapper {
        /**
         * @param vmconnFactory
         * @param destination   if it's null the producer will not be bound to any destination so it can sends messages to the whole topic space.<BR>
         *                      Otherwise if it isn't null the producer will be bound to a queue destination as specified by the parameter.
         * @param transacted
         * @param start         start activeMQ connection
         * @throws JMSException
         * @throws KapuaException
         */
        protected JmsProducerWrapperImpl(ActiveMQConnectionFactory vmconnFactory, String destination, boolean transacted, boolean start) throws JMSException, KapuaException {
            super(vmconnFactory, destination, transacted, start);
        }
    }

    Session session;
    Connection connection;
    String destination;
    MessageProducer producer;
    ActiveMQConnectionFactory activeMQConnectionFactory;
    boolean[] transacted;
    boolean[] start;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: session = Mockito.mock(Session.class);
        // Removed line due to General Fixture smell: connection = Mockito.mock(Connection.class);
        // Removed line due to General Fixture smell: destination = "Destination";
        // Removed line due to General Fixture smell: producer = Mockito.mock(MessageProducer.class);
        // Removed line due to General Fixture smell: activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        // Removed line due to General Fixture smell: transacted = new boolean[]{true, false};
        // Removed line due to General Fixture smell: start = new boolean[]{true, false};
    }

    @Test
    public void jmsProducerWrapperEmptyDestinationTest() throws JMSException, KapuaException {
        start = new boolean[]{true, false};
        transacted = new boolean[]{true, false};
        activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        producer = Mockito.mock(MessageProducer.class);
        connection = Mockito.mock(Connection.class);
        session = Mockito.mock(Session.class);
        destination = "Destination";
        for (boolean transactedValue : transacted) {
            for (boolean startValue : start) {
                Mockito.when(activeMQConnectionFactory.createConnection()).thenReturn(connection);
                Mockito.when(activeMQConnectionFactory.createConnection().createSession(transactedValue, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
                Mockito.when(session.createProducer(session.createQueue(destination))).thenReturn(producer);
                JmsProducerWrapper jmsProducerWrapper = new JmsProducerWrapperImpl(activeMQConnectionFactory, "", transactedValue, startValue);

                assertEquals("Expected and actual values should be the same.", "", jmsProducerWrapper.destination);
                assertEquals("Expected and actual values should be the same.", session, jmsProducerWrapper.session);
                assertEquals("Expected and actual values should be the same.", connection, jmsProducerWrapper.connection);
                assertEquals("Expected and actual values should be the same.", producer, jmsProducerWrapper.producer);
            }
        }
    }

    @Test
    public void jmsProducerWrapperTest() throws JMSException, KapuaException {
        start = new boolean[]{true, false};
        transacted = new boolean[]{true, false};
        activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        producer = Mockito.mock(MessageProducer.class);
        connection = Mockito.mock(Connection.class);
        session = Mockito.mock(Session.class);
        destination = "Destination";
        for (boolean transactedValue : transacted) {
            for (boolean startValue : start) {
                Mockito.when(activeMQConnectionFactory.createConnection()).thenReturn(connection);
                Mockito.when(activeMQConnectionFactory.createConnection().createSession(transactedValue, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
                Mockito.when(session.createProducer(session.createQueue(destination))).thenReturn(producer);
                JmsProducerWrapper jmsProducerWrapper = new JmsProducerWrapperImpl(activeMQConnectionFactory, "Destination", transactedValue, startValue);

                assertEquals("Expected and actual values should be the same.", "Destination", jmsProducerWrapper.destination);
                assertEquals("Expected and actual values should be the same.", session, jmsProducerWrapper.session);
                assertEquals("Expected and actual values should be the same.", connection, jmsProducerWrapper.connection);
                assertEquals("Expected and actual values should be the same.", producer, jmsProducerWrapper.producer);
            }
        }
    }

    @Test
    public void jmsProducerWrapperNullDestinationTest() throws JMSException, KapuaException {
        start = new boolean[]{true, false};
        transacted = new boolean[]{true, false};
        activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        producer = Mockito.mock(MessageProducer.class);
        connection = Mockito.mock(Connection.class);
        session = Mockito.mock(Session.class);
        destination = "Destination";
        for (boolean transactedValue : transacted) {
            for (boolean startValue : start) {
                Mockito.when(activeMQConnectionFactory.createConnection()).thenReturn(connection);
                Mockito.when(activeMQConnectionFactory.createConnection().createSession(transactedValue, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
                Mockito.when(session.createProducer(null)).thenReturn(producer);
                JmsProducerWrapper jmsProducerWrapper = new JmsProducerWrapperImpl(activeMQConnectionFactory, null, transactedValue, startValue);

                assertNull("Null expected.", jmsProducerWrapper.destination);
                assertEquals("Expected and actual values should be the same.", session, jmsProducerWrapper.session);
                assertEquals("Expected and actual values should be the same.", connection, jmsProducerWrapper.connection);
                assertEquals("Expected and actual values should be the same.", producer, jmsProducerWrapper.producer);
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void jmsProducerWrapperNullFactoryTest() throws JMSException, KapuaException {
        start = new boolean[]{true, false};
        transacted = new boolean[]{true, false};
        for (boolean transactedValue : transacted) {
            for (boolean startValue : start) {
                new JmsProducerWrapperImpl(null, "Destination", transactedValue, startValue);
            }
        }
    }

    @Test
    public void closeTest() throws JMSException, KapuaException {
        activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        producer = Mockito.mock(MessageProducer.class);
        connection = Mockito.mock(Connection.class);
        session = Mockito.mock(Session.class);
        destination = "Destination";
        Mockito.when(activeMQConnectionFactory.createConnection()).thenReturn(connection);
        Mockito.when(activeMQConnectionFactory.createConnection().createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        Mockito.when(session.createProducer(session.createQueue(destination))).thenReturn(producer);
        JmsProducerWrapper jmsProducerWrapper = new JmsProducerWrapperImpl(activeMQConnectionFactory, "Destination", true, true);
        try {
            jmsProducerWrapper.close();
        } catch (Exception e) {
            fail("Exception not expected.");
        }
    }

    @Test
    public void closeExceptionTest() throws JMSException, KapuaException {
        activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        producer = Mockito.mock(MessageProducer.class);
        connection = Mockito.mock(Connection.class);
        session = Mockito.mock(Session.class);
        destination = "Destination";
        Mockito.when(activeMQConnectionFactory.createConnection()).thenReturn(connection);
        Mockito.when(activeMQConnectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        Mockito.when(session.createProducer(session.createQueue(destination))).thenReturn(producer);
        JmsProducerWrapper jmsProducerWrapper = new JmsProducerWrapperImpl(activeMQConnectionFactory, "Destination", false, false);
        Mockito.doThrow(new JMSException("Message")).when(connection).close();

        jmsProducerWrapper.close();
    }

    @Test
    public void getDestinationTest() throws KapuaException, JMSException {
        start = new boolean[]{true, false};
        transacted = new boolean[]{true, false};
        activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        producer = Mockito.mock(MessageProducer.class);
        connection = Mockito.mock(Connection.class);
        session = Mockito.mock(Session.class);
        destination = "Destination";
        for (boolean transactedValue : transacted) {
            for (boolean startValue : start) {
                Mockito.when(activeMQConnectionFactory.createConnection()).thenReturn(connection);
                Mockito.when(activeMQConnectionFactory.createConnection().createSession(transactedValue, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
                Mockito.when(session.createProducer(session.createQueue(destination))).thenReturn(producer);
                JmsProducerWrapper jmsProducerWrapper = new JmsProducerWrapperImpl(activeMQConnectionFactory, "Destination", transactedValue, startValue);

                assertEquals("Expected and actual values should be the same.", "Destination", jmsProducerWrapper.getDestination());
            }
        }
    }

    @Test
    public void finalizeTest() throws Throwable {
        activeMQConnectionFactory = Mockito.mock(ActiveMQConnectionFactory.class);
        producer = Mockito.mock(MessageProducer.class);
        connection = Mockito.mock(Connection.class);
        session = Mockito.mock(Session.class);
        destination = "Destination";
        Mockito.when(activeMQConnectionFactory.createConnection()).thenReturn(connection);
        Mockito.when(activeMQConnectionFactory.createConnection().createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        Mockito.when(session.createProducer(session.createQueue(destination))).thenReturn(producer);
        JmsProducerWrapper jmsProducerWrapper = new JmsProducerWrapperImpl(activeMQConnectionFactory, "Destination", true, true);
        try {
            jmsProducerWrapper.finalize();
        } catch (Exception e) {
            fail("Exception not expected.");
        }
    }
}