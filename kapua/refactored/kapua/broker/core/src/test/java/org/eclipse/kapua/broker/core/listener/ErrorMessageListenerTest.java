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
package org.eclipse.kapua.broker.core.listener;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import com.codahale.metrics.Counter;

import java.util.Base64;

@Category(JUnitTests.class)
public class ErrorMessageListenerTest extends Assert {

    ErrorMessageListener errorMessageListener;
    Exchange exchange;
    Message exchangeMessage;
    Object[] messages;
    Counter error;
    Counter errorLifeCycleMessage;
    Throwable throwable;
    byte[] throwableBytes;
    String stringThrowable;

    @Before
    public void initialize() {
        errorMessageListener = new ErrorMessageListener();
        // Removed line due to General Fixture smell: exchange = Mockito.mock(Exchange.class);
        // Removed line due to General Fixture smell: exchangeMessage = Mockito.mock(Message.class);
        // Removed line due to General Fixture smell: messages = new Object[]{null, new Object()};
        // Removed line due to General Fixture smell: error = errorMessageListener.registerCounter("messages", "generic", "count");
        // Removed line due to General Fixture smell: errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        // Removed line due to General Fixture smell: throwable = new Throwable("message");
        // Removed line due to General Fixture smell: throwableBytes = SerializationUtils.serialize(throwable);
        // Removed line due to General Fixture smell: stringThrowable = Base64.getEncoder().encodeToString(throwableBytes);
    }

    @Test
    public void errorMessageListenerNameTest() {
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        assertEquals("Expected and actual values should be the same.", "error", errorMessageListener.name);
    }

    @Test
    public void processMessageNullThrowableNullExchangeTest() throws KapuaException {
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(null);
        Mockito.when(exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_EXCEPTION)).thenReturn(null);
        Mockito.when(exchange.getException()).thenReturn(null);
        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            errorMessageListener.processMessage(exchange, message);
            assertEquals("Expected and actual values should be the same.", 1L, error.getCount());

            error.dec();
        }
    }

    @Test
    public void processMessageNullExchangeTest() throws KapuaException {
        errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(null);
        Mockito.when(exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_EXCEPTION)).thenReturn(new Throwable());
        Mockito.when(exchange.getException()).thenReturn(null);

        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());
            errorMessageListener.processMessage(exchange, message);
            assertEquals("Expected and actual values should be the same.", 1L, error.getCount());
            // Line removed due to Duplicate Assert: assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());

            error.dec();
        }
    }

    @Test
    public void processMessageNullThrowableTest() throws KapuaException {
        errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(null);
        Mockito.when(exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_EXCEPTION)).thenReturn(null);
        Mockito.when(exchange.getException()).thenReturn(new Exception());

        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());
            errorMessageListener.processMessage(exchange, message);
            assertEquals("Expected and actual values should be the same.", 1L, error.getCount());
            // Line removed due to Duplicate Assert: assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());

            error.dec();
        }
    }

    @Test
    public void processMessageTest() throws KapuaException {
        stringThrowable = Base64.getEncoder().encodeToString(throwableBytes);
        errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(stringThrowable);

        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());
            errorMessageListener.processMessage(exchange, message);
            assertEquals("Expected and actual values should be the same.", 1L, error.getCount());
            // Line removed due to Duplicate Assert: assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());

            error.dec();
        }
    }

    @Test
    public void lifeCycleMessageNullThrowableNullExchangeTest() throws KapuaException {
        errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(null);
        Mockito.when(exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_EXCEPTION)).thenReturn(null);
        Mockito.when(exchange.getException()).thenReturn(null);

        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());
            errorMessageListener.lifeCycleMessage(exchange, message);
            // Line removed due to Duplicate Assert: assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 1L, errorLifeCycleMessage.getCount());

            errorLifeCycleMessage.dec();
        }
    }

    @Test
    public void lifeCycleMessageNullExchangeTest() throws KapuaException {
        errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(null);
        Mockito.when(exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_EXCEPTION)).thenReturn(new Throwable());
        Mockito.when(exchange.getException()).thenReturn(null);

        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());
            errorMessageListener.lifeCycleMessage(exchange, message);
            // Line removed due to Duplicate Assert: assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 1L, errorLifeCycleMessage.getCount());

            errorLifeCycleMessage.dec();
        }
    }

    @Test
    public void lifeCycleMessageNullThrowableTest() throws KapuaException {
        errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(null);
        Mockito.when(exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_EXCEPTION)).thenReturn(null);
        Mockito.when(exchange.getException()).thenReturn(new Exception());

        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());
            errorMessageListener.lifeCycleMessage(exchange, message);
            // Line removed due to Duplicate Assert: assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 1L, errorLifeCycleMessage.getCount());

            errorLifeCycleMessage.dec();
        }
    }

    @Test
    public void lifeCycleMessageTest() throws KapuaException {
        stringThrowable = Base64.getEncoder().encodeToString(throwableBytes);
        errorLifeCycleMessage = errorMessageListener.registerCounter("messages", "life_cycle", "count");
        error = errorMessageListener.registerCounter("messages", "generic", "count");
        messages = new Object[]{null, new Object()};
        exchangeMessage = Mockito.mock(Message.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(exchangeMessage);
        Mockito.when(exchangeMessage.getHeader(MessageConstants.HEADER_KAPUA_PROCESSING_EXCEPTION, String.class)).thenReturn(stringThrowable);

        for (Object message : messages) {
            assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 0L, errorLifeCycleMessage.getCount());
            errorMessageListener.lifeCycleMessage(exchange, message);
            // Line removed due to Duplicate Assert: assertEquals("Expected and actual values should be the same.", 0L, error.getCount());
            assertEquals("Expected and actual values should be the same.", 1L, errorLifeCycleMessage.getCount());

            errorLifeCycleMessage.dec();
        }
    }
}