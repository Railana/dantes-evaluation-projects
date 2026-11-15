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
public class DeviceManagementNotificationConverterTest extends Assert {

    DeviceManagementNotificationConverter deviceManagementNotificationConverter;
    byte[] value, connectorDescriptorBytes, kapuaIdBytes;
    Exchange exchange;
    CamelKapuaMessage camelKapuaMessage;
    Counter converterDeviceManagementNotificationMessage;
    ConnectorDescriptorProvider provider;
    ConnectorDescriptor connectorDescriptor;
    String connectorDescriptorString, kapuaIdString;
    KapuaMessage kapuaMessage;
    Translator translator1, translator2;
    DefaultMessage defaultMessage;
    KapuaId kapuaId;
    org.eclipse.kapua.transport.message.jms.JmsMessage jmsMessage;
    org.eclipse.kapua.message.Message messageKapua;

    @Before
    public void initialize() throws TranslateException {
        // Removed line due to General Fixture smell: deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        // Removed line due to General Fixture smell: value = new byte[]{1, 2, 3, 4};
        // Removed line due to General Fixture smell: exchange = Mockito.mock(Exchange.class);
        // Removed line due to General Fixture smell: camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        converterDeviceManagementNotificationMessage = MetricServiceFactory.getInstance().getCounter(ConverterMetrics.METRIC_MODULE_NAME, ConverterMetrics.METRIC_COMPONENT_NAME, ConverterMetrics.METRIC_KAPUA_MESSAGE, ConverterMetrics.METRIC_MESSAGES, ConverterMetrics.METRIC_NOTIFY, ConverterMetrics.METRIC_COUNT);
        Utils.initCounter(converterDeviceManagementNotificationMessage);
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

        TranslatorCache.cacheTranslator(org.eclipse.kapua.service.device.call.message.kura.app.notification.KuraNotifyMessage.class, org.eclipse.kapua.service.device.management.message.notification.KapuaNotifyMessage.class, translator1);
        TranslatorCache.cacheTranslator(org.eclipse.kapua.transport.message.jms.JmsMessage.class, org.eclipse.kapua.service.device.call.message.kura.app.notification.KuraNotifyMessage.class, translator2);

        // Removed line due to General Fixture smell: jmsMessage = new org.eclipse.kapua.transport.message.jms.JmsMessage(new JmsTopic("topic"), new Date(10L), new JmsPayload(value));
        // Removed line due to General Fixture smell: messageKapua = translator2.translate(jmsMessage);
    }

    @After
    public void tearDown() {
        Utils.initCounter(converterDeviceManagementNotificationMessage);
    }

    @Test
    public void convertToManagementNotificationNullTest() {
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        assertThrows(Exception e,
        		() -> {
        			deviceManagementNotificationConverter.convertToManagementNotification(null, null);
        			fail("NullPointerException expected.");
        		});
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }

    @Test
    public void convertToManagementNotificationNullExchangeTest() {
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        try {
            deviceManagementNotificationConverter.convertToManagementNotification(null, value);
            fail("NullPointerException expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new NullPointerException().toString(), e.toString());
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }

    @Test
    public void convertToManagementNotificationNullValueTest() {
        exchange = Mockito.mock(Exchange.class);
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        Mockito.doReturn(Mockito.mock(org.apache.camel.Message.class)).when(exchange).getIn();
        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());

        try {
            deviceManagementNotificationConverter.convertToManagementNotification(exchange, null);
            fail("KapuaException expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, null, "Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass()).toString(), e.toString());
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }

    @Test
    public void convertToManagementNotificationTest() throws KapuaException {
        messageKapua = translator2.translate(jmsMessage);
        kapuaIdString = Base64.getEncoder().encodeToString(kapuaIdBytes);
        kapuaId = KapuaId.ONE;
        defaultMessage = Mockito.mock(DefaultMessage.class);
        translator2 = Mockito.mock(Translator.class);
        translator1 = Mockito.mock(Translator.class);
        kapuaMessage = Mockito.mock(KapuaMessage.class);
        connectorDescriptorString = Base64.getEncoder().encodeToString(connectorDescriptorBytes);
        connectorDescriptor = provider.getDescriptor("foo");
        exchange = Mockito.mock(Exchange.class);
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        System.setProperty(MessageConstants.PROPERTY_ORIGINAL_TOPIC, "topic");
        Mockito.when(exchange.getIn()).thenReturn(defaultMessage);
        Mockito.when(defaultMessage.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class)).thenReturn(10L);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID, String.class)).thenReturn(kapuaIdString);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CLIENT_ID, String.class)).thenReturn("clientid");
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_DEVICE_PROTOCOL, String.class)).thenReturn(connectorDescriptorString);
        Mockito.when(defaultMessage.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("topic");
        Mockito.doReturn(messageKapua).when(translator2).translate(new org.eclipse.kapua.transport.message.jms.JmsMessage(new JmsTopic("topic"), new Date(10L), new JmsPayload(value)));
        Mockito.when(translator1.translate(messageKapua)).thenReturn(kapuaMessage);
        Mockito.when(kapuaMessage.getClientId()).thenReturn("id");

        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        try {
            assertThat("Instance of CamelKapuaMessage expected.", deviceManagementNotificationConverter.convertToManagementNotification(exchange, value), IsInstanceOf.instanceOf(CamelKapuaMessage.class));
        } catch (Exception e) {
            fail("Exception not expected.");
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());
    }

    @Test
    public void convertToManagementNotificationOnExceptionNullTest() {
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        try {
            deviceManagementNotificationConverter.convertToManagementNotificationOnException(null, null);
            fail("NullPointerException expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new NullPointerException().toString(), e.toString());
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }

    @Test
    public void convertToManagementNotificationOnExceptionNullExchangeTest() {
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        try {
            assertThat("Instance of CamelKapuaMessage expected.", deviceManagementNotificationConverter.convertToManagementNotificationOnException(null, camelKapuaMessage), IsInstanceOf.instanceOf(CamelKapuaMessage.class));
        } catch (Exception e) {
            fail("Exception not expected.");
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }

    @Test
    public void convertToManagementNotificationOnExceptionNullObjectTest() {
        exchange = Mockito.mock(Exchange.class);
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        Mockito.when(exchange.getIn()).thenReturn(Mockito.mock(org.apache.camel.Message.class));

        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        try {
            deviceManagementNotificationConverter.convertToManagementNotificationOnException(exchange, null);
            fail("KapuaException expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, null, "Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass()).toString(), e.toString());
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }

    @Test
    public void convertToManagementNotificationOnExceptionCamelMessageTest() {
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        exchange = Mockito.mock(Exchange.class);
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        try {
            assertThat("Instance of CamelKapuaMessage expected.", deviceManagementNotificationConverter.convertToManagementNotificationOnException(exchange, camelKapuaMessage), IsInstanceOf.instanceOf(CamelKapuaMessage.class));
        } catch (Exception e) {
            fail("Exception not expected.");
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }

    @Test
    public void convertToManagementNotificationOnExceptionObjectMessageTest() throws KapuaException {
        messageKapua = translator2.translate(jmsMessage);
        kapuaIdString = Base64.getEncoder().encodeToString(kapuaIdBytes);
        kapuaId = KapuaId.ONE;
        defaultMessage = Mockito.mock(DefaultMessage.class);
        translator2 = Mockito.mock(Translator.class);
        translator1 = Mockito.mock(Translator.class);
        kapuaMessage = Mockito.mock(KapuaMessage.class);
        connectorDescriptorString = Base64.getEncoder().encodeToString(connectorDescriptorBytes);
        connectorDescriptor = provider.getDescriptor("foo");
        exchange = Mockito.mock(Exchange.class);
        value = new byte[]{1, 2, 3, 4};
        deviceManagementNotificationConverter = new DeviceManagementNotificationConverter();
        System.setProperty(MessageConstants.PROPERTY_ORIGINAL_TOPIC, "topic");
        Mockito.when(exchange.getIn()).thenReturn(defaultMessage);
        Mockito.when(defaultMessage.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class)).thenReturn(10L);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID, String.class)).thenReturn(kapuaIdString);
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CLIENT_ID, String.class)).thenReturn("clientid");
        Mockito.when(exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_DEVICE_PROTOCOL, String.class)).thenReturn(connectorDescriptorString);
        Mockito.when(defaultMessage.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("topic");
        Mockito.doReturn(messageKapua).when(translator2).translate(new org.eclipse.kapua.transport.message.jms.JmsMessage(new JmsTopic("topic"), new Date(10L), new JmsPayload(value)));
        Mockito.when(translator1.translate(messageKapua)).thenReturn(kapuaMessage);
        Mockito.when(kapuaMessage.getClientId()).thenReturn("id");

        assertEquals("Expected and actual values should be the same.", 0L, converterDeviceManagementNotificationMessage.getCount());
        try {
            assertThat("Instance of CamelKapuaMessage expected.", deviceManagementNotificationConverter.convertToManagementNotificationOnException(exchange, value), IsInstanceOf.instanceOf(CamelKapuaMessage.class));
        } catch (Exception e) {
            fail("Exception not expected.");
        }
        assertEquals("Expected and actual values should be the same.", 1L, converterDeviceManagementNotificationMessage.getCount());

    }
}