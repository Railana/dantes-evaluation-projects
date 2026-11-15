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
package org.eclipse.kapua.broker.core.converter;

import com.codahale.metrics.Counter;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.listener.CamelConstants;
import org.eclipse.kapua.broker.core.message.CamelKapuaMessage;
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptorProvider;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptorProviders;
import org.eclipse.kapua.broker.core.utils.Utils;
import org.eclipse.kapua.commons.metric.MetricServiceFactory;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.cache.TranslatorCache;
import org.eclipse.kapua.translator.exception.TranslateException;
import org.eclipse.kapua.transport.message.jms.JmsPayload;
import org.eclipse.kapua.transport.message.jms.JmsTopic;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Base64;
import java.util.Date;

@Category(JUnitTests.class)
public class KapuaDataConverterTest extends Assert {

    Exchange exchange;
    CamelKapuaMessage camelKapuaMessage;
    Object objectValue;
    byte[] byteArray, connectorDescriptorBytes, kapuaIdBytes;
    KapuaDataConverter kapuaDataConverter;
    Message message;
    Counter converterDataMessage;
    ConnectorDescriptorProvider provider;
    ConnectorDescriptor connectorDescriptor;
    String connectorDescriptorString, kapuaIdString;
    KapuaMessage kapuaMessage;
    Translator translator1, translator2;
    DefaultMessage defaultMessage;
    KapuaId kapuaId = KapuaId.ONE;
    org.eclipse.kapua.transport.message.jms.JmsMessage jmsMessage;
    org.eclipse.kapua.message.Message messageKapua;

    @Before
    public void initialize() throws TranslateException {
        // Removed line due to General Fixture smell: exchange = Mockito.mock(Exchange.class);
        // Removed line due to General Fixture smell: camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        // Removed line due to General Fixture smell: objectValue = new Object();
        // Removed line due to General Fixture smell: byteArray = new byte[]{1, 2, 3, 4, 5, 6};
        // Removed line due to General Fixture smell: kapuaDataConverter = new KapuaDataConverter();
        // Removed line due to General Fixture smell: message = Mockito.mock(Message.class);
        converterDataMessage = MetricServiceFactory.getInstance().getCounter(ConverterMetrics.METRIC_MODULE_NAME, ConverterMetrics.METRIC_COMPONENT_NAME, ConverterMetrics.METRIC_KAPUA_MESSAGE, ConverterMetrics.METRIC_MESSAGES, ConverterMetrics.METRIC_DATA, ConverterMetrics.METRIC_COUNT);
        Utils.initCounter(converterDataMessage);
        // Removed line due to General Fixture smell: provider = ConnectorDescriptorProviders.getInstance();
        // Removed line due to General Fixture smell: connectorDescriptor = provider.getDescriptor("foo");
        // Removed line due to General Fixture smell: connectorDescriptorBytes = SerializationUtils.serialize(connectorDescriptor);
        // Removed line due to General Fixture smell: connectorDescriptorString = Base64.getEncoder().encodeToString(connectorDescriptorBytes);
        // Removed line due to General Fixture smell: kapuaMessage = Mockito.mock(KapuaMessage.class);
        // Removed line due to General Fixture smell: translator1 = Mockito.mock(Translator.class);
        // Removed line due to General Fixture smell: translator2 = Mockito.mock(Translator.class);
        // Removed line due to General Fixture smell: defaultMessage = Mockito.mock(DefaultMessage.class);
        // Removed line due to General Fixture smell: kapuaId = KapuaId.ONE;
        // Removed line due to General Fixture smell: kapuaIdBytes = SerializationUtils.serialize(kapuaId);
        // Removed line due to General Fixture smell: kapuaIdString = Base64.getEncoder().encodeToString(kapuaIdBytes);

        TranslatorCache.cacheTranslator(org.eclipse.kapua.service.device.call.message.kura.data.KuraDataMessage.class, org.eclipse.kapua.message.device.data.KapuaDataMessage.class, translator1);
        TranslatorCache.cacheTranslator(org.eclipse.kapua.transport.message.jms.JmsMessage.class, org.eclipse.kapua.service.device.call.message.kura.data.KuraDataMessage.class, translator2);

        // Removed line due to General Fixture smell: jmsMessage = new org.eclipse.kapua.transport.message.jms.JmsMessage(new JmsTopic("topic"), new Date(10L), new JmsPayload(byteArray));
        translator2.translate(jmsMessage);
    }

    @After
    public void tearDown() {
        Utils.initCounter(converterDataMessage);
    }

    @Test
    public void convertToDataNullTest() {
        kapuaDataConverter = new KapuaDataConverter();
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        assertThrows(Exception e,
        		() -> kapuaDataConverter.convertToData(null, null));
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataNullExchangeTest() {
        kapuaDataConverter = new KapuaDataConverter();
        objectValue = new Object();
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        assertThrows(Exception e,
        		() -> kapuaDataConverter.convertToData(null, objectValue));
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToNullValueData() {
        message = Mockito.mock(Message.class);
        kapuaDataConverter = new KapuaDataConverter();
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        assertThrows(Exception e,
        		() -> {
        			kapuaDataConverter.convertToData(exchange, null);
        			fail("KapuaException expected.");
        		});
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataTest() throws KapuaException {
        kapuaIdString = Base64.getEncoder().encodeToString(kapuaIdBytes);
        kapuaId = KapuaId.ONE;
        defaultMessage = Mockito.mock(DefaultMessage.class);
        translator2 = Mockito.mock(Translator.class);
        translator1 = Mockito.mock(Translator.class);
        kapuaMessage = Mockito.mock(KapuaMessage.class);
        connectorDescriptorString = Base64.getEncoder().encodeToString(connectorDescriptorBytes);
        connectorDescriptor = provider.getDescriptor("foo");
        message = Mockito.mock(Message.class);
        kapuaDataConverter = new KapuaDataConverter();
        byteArray = new byte[]{1, 2, 3, 4, 5, 6};
        exchange = Mockito.mock(Exchange.class);
        System.setProperty(MessageConstants.PROPERTY_ORIGINAL_TOPIC, "topic");
        Mockito.when(exchange.getIn()).thenReturn(defaultMessage);
        Mockito.when(defaultMessage.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class)).thenReturn(10L);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID, String.class)).thenReturn(kapuaIdString);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CLIENT_ID, String.class)).thenReturn("clientid");
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_DEVICE_PROTOCOL, String.class)).thenReturn(connectorDescriptorString);
        Mockito.when(defaultMessage.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("topic");
        Mockito.doReturn(messageKapua).when(translator2).translate(new org.eclipse.kapua.transport.message.jms.JmsMessage(new JmsTopic("topic"), new Date(10L), new JmsPayload(byteArray)));
        Mockito.when(translator1.translate(messageKapua)).thenReturn(kapuaMessage);
        Mockito.when(kapuaMessage.getClientId()).thenReturn("id");

        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        try {
            assertThat("Instance of CamelKapuaMessage expected.", kapuaDataConverter.convertToData(exchange, byteArray), IsInstanceOf.instanceOf(CamelKapuaMessage.class));
        } catch (Exception e) {
            fail("Exception not expected.");
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataObjectValueTest() {
        message = Mockito.mock(Message.class);
        kapuaDataConverter = new KapuaDataConverter();
        objectValue = new Object();
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        try {
            kapuaDataConverter.convertToData(exchange, objectValue);
            fail("KapuaException expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, null, "Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass()).toString(), e.toString());
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataOnExceptionNullTest() {
        kapuaDataConverter = new KapuaDataConverter();
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        try {
            kapuaDataConverter.convertToDataOnException(null, null);
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new NullPointerException().toString(), e.toString());
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataOnExceptionNullExchangeTest() {
        kapuaDataConverter = new KapuaDataConverter();
        objectValue = new Object();
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        assertThrows(Exception e,
        		() -> kapuaDataConverter.convertToDataOnException(null, objectValue));
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataOnExceptionNullValueData() {
        message = Mockito.mock(Message.class);
        kapuaDataConverter = new KapuaDataConverter();
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        assertThrows(Exception e,
        		() -> {
        			kapuaDataConverter.convertToDataOnException(exchange, null);
        			fail("KapuaException expected.");
        		});
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataOnExceptionEmptyDatastoreIdTest() throws KapuaException {
        kapuaDataConverter = new KapuaDataConverter();
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(camelKapuaMessage.getDatastoreId()).thenReturn(null);
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        assertNull("Null expected.", kapuaDataConverter.convertToDataOnException(exchange, camelKapuaMessage).getDatastoreId());
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataOnExceptionCamelKapuaMessageValueTest() throws KapuaException {
        message = Mockito.mock(Message.class);
        kapuaDataConverter = new KapuaDataConverter();
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(camelKapuaMessage.getDatastoreId()).thenReturn("message");

        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        assertEquals("Expected and actual values should be the same.", "message", kapuaDataConverter.convertToDataOnException(exchange, camelKapuaMessage).getDatastoreId());
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataOnExceptionObjectValueTest() {
        message = Mockito.mock(Message.class);
        kapuaDataConverter = new KapuaDataConverter();
        objectValue = new Object();
        exchange = Mockito.mock(Exchange.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        try {
            kapuaDataConverter.convertToDataOnException(exchange, objectValue);
            fail("KapuaException expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, null, "Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass()).toString(), e.toString());
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }

    @Test
    public void convertToDataOnExceptionTest() throws KapuaException {
        kapuaIdString = Base64.getEncoder().encodeToString(kapuaIdBytes);
        kapuaId = KapuaId.ONE;
        defaultMessage = Mockito.mock(DefaultMessage.class);
        translator2 = Mockito.mock(Translator.class);
        translator1 = Mockito.mock(Translator.class);
        kapuaMessage = Mockito.mock(KapuaMessage.class);
        connectorDescriptorString = Base64.getEncoder().encodeToString(connectorDescriptorBytes);
        connectorDescriptor = provider.getDescriptor("foo");
        message = Mockito.mock(Message.class);
        kapuaDataConverter = new KapuaDataConverter();
        byteArray = new byte[]{1, 2, 3, 4, 5, 6};
        exchange = Mockito.mock(Exchange.class);
        System.setProperty(MessageConstants.PROPERTY_ORIGINAL_TOPIC, "topic");
        Mockito.when(exchange.getIn()).thenReturn(defaultMessage);
        Mockito.when(defaultMessage.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class)).thenReturn(10L);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID, String.class)).thenReturn(kapuaIdString);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CLIENT_ID, String.class)).thenReturn("clientid");
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_DEVICE_PROTOCOL, String.class)).thenReturn(connectorDescriptorString);
        Mockito.when(defaultMessage.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("topic");
        Mockito.doReturn(messageKapua).when(translator2).translate(new org.eclipse.kapua.transport.message.jms.JmsMessage(new JmsTopic("topic"), new Date(10L), new JmsPayload(byteArray)));
        Mockito.when(translator1.translate(messageKapua)).thenReturn(kapuaMessage);
        Mockito.when(kapuaMessage.getClientId()).thenReturn("id");

        assertEquals("Expected and actual values should be the same.", 0L, converterDataMessage.getCount());
        try {
            assertThat("Instance of CamelKapuaMessage expected.", kapuaDataConverter.convertToDataOnException(exchange, byteArray), IsInstanceOf.instanceOf(CamelKapuaMessage.class));
        } catch (Exception e) {
            fail("Exception not expected.");
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDataMessage.getCount());

    }
}