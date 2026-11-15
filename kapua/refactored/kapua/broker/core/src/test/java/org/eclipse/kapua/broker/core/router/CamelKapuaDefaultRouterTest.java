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
package org.eclipse.kapua.broker.core.router;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.broker.core.KapuaBrokerJAXBContextLoader;

import org.eclipse.kapua.broker.core.listener.CamelConstants;
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

@Category(JUnitTests.class)
public class CamelKapuaDefaultRouterTest extends Assert {

    CamelKapuaDefaultRouter defaultRouter;
    private KapuaBrokerJAXBContextLoader kapuaBrokerJAXBContextLoader;
    Exchange exchange;
    Object value;
    Map<String, Object> properties;
    Message message;
    String expectedValue;
    String[] previousList;

    @Before
    public void initialize() throws KapuaException {
        kapuaBrokerJAXBContextLoader = new KapuaBrokerJAXBContextLoader();
        // Removed line due to General Fixture smell: kapuaBrokerJAXBContextLoader = new KapuaBrokerJAXBContextLoader();
        kapuaBrokerJAXBContextLoader.init();
        // Removed line due to General Fixture smell: exchange = Mockito.mock(Exchange.class);
        // Removed line due to General Fixture smell: value = new Object();
        // Removed line due to General Fixture smell: properties = new HashMap<>();
        // Removed line due to General Fixture smell: message = Mockito.mock(Message.class);
        // Removed line due to General Fixture smell: expectedValue = "bean:kapuaDataConverter?method=convertToData,bean:dataStorageMessageProcessor?method=processMessage";
        // Removed line due to General Fixture smell: previousList = new String[]{"", "Previous!#", "Previous#<> 123456-7890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
    }

    @After
    public void resetJAXBContext() {
        kapuaBrokerJAXBContextLoader = new KapuaBrokerJAXBContextLoader();
        kapuaBrokerJAXBContextLoader.reset();
    }

    @Test(expected = KapuaRuntimeException.class)
    public void camelKapuaDefaultRouterResetContextLoaderTest() {
        kapuaBrokerJAXBContextLoader = new KapuaBrokerJAXBContextLoader();
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        kapuaBrokerJAXBContextLoader.reset();
        new CamelKapuaDefaultRouter();
    }

    @Test
    public void camelKapuaDefaultRouterTest() {
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        try {
            new CamelKapuaDefaultRouter();
        } catch (Exception e) {
            fail("Exception not expected.");
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void camelKapuaDefaultRouterIncorrectConfigurationFileTest() {
        System.setProperty("camel.default_route.configuration_file_name", "file.xml");
        new CamelKapuaDefaultRouter();
    }

    @Test(expected = KapuaRuntimeException.class)
    public void camelKapuaDefaultRouterIncorrectConfigurationTest() {
        System.setProperty("camel.default_route.configuration_file_name", "locator.xml");
        new CamelKapuaDefaultRouter();
    }

    @Test
    public void defaultRouteEmptyPropertiesTest() {
        previousList = new String[]{"", "Previous!#", "Previous#<> 123456-7890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        message = Mockito.mock(Message.class);
        properties = new HashMap<>();
        value = new Object();
        exchange = Mockito.mock(Exchange.class);
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        defaultRouter = new CamelKapuaDefaultRouter();
        Mockito.when(exchange.getIn()).thenReturn(message);

        for (String previous : previousList) {
            assertNull("Null expected.", defaultRouter.defaultRoute(exchange, value, previous, properties));
        }
    }

    @Test
    public void defaultRouteTest() {
        previousList = new String[]{"", "Previous!#", "Previous#<> 123456-7890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        message = Mockito.mock(Message.class);
        properties = new HashMap<>();
        value = new Object();
        exchange = Mockito.mock(Exchange.class);
        properties.put("key1", "value");
        properties.put("key2", 10);
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        defaultRouter = new CamelKapuaDefaultRouter();
        Mockito.when(exchange.getIn()).thenReturn(message);

        for (String previous : previousList) {
            assertNull("Null expected.", defaultRouter.defaultRoute(exchange, value, previous, properties));
        }
    }

    @Test
    public void defaultRouteNullPreviousTest() {
        expectedValue = "bean:kapuaDataConverter?method=convertToData,bean:dataStorageMessageProcessor?method=processMessage";
        message = Mockito.mock(Message.class);
        properties = new HashMap<>();
        value = new Object();
        exchange = Mockito.mock(Exchange.class);
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        defaultRouter = new CamelKapuaDefaultRouter();

        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("originalTopic");
        Mockito.when(message.getHeader(CamelConstants.JMS_CORRELATION_ID)).thenReturn("JMSCorrelationID");

        assertEquals("Expected and actual values should be the same.", expectedValue, defaultRouter.defaultRoute(exchange, value, null, properties));
    }

    @Test(expected = NullPointerException.class)
    public void defaultRouteNullTest() {
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        defaultRouter = new CamelKapuaDefaultRouter();
        defaultRouter.defaultRoute(null, null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void defaultRouteNullExchangeTest() {
        previousList = new String[]{"", "Previous!#", "Previous#<> 123456-7890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        properties = new HashMap<>();
        value = new Object();
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        defaultRouter = new CamelKapuaDefaultRouter();

        for (String previous : previousList) {
            defaultRouter.defaultRoute(null, value, previous, properties);
        }
    }

    @Test
    public void defaultRouteNullValueTest() {
        previousList = new String[]{"", "Previous!#", "Previous#<> 123456-7890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        message = Mockito.mock(Message.class);
        properties = new HashMap<>();
        exchange = Mockito.mock(Exchange.class);
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        defaultRouter = new CamelKapuaDefaultRouter();
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("originalTopic");
        Mockito.when(message.getHeader(CamelConstants.JMS_CORRELATION_ID)).thenReturn("JMSCorrelationID");

        for (String previous : previousList) {
            assertNull("Null expected.", defaultRouter.defaultRoute(exchange, null, previous, properties));
        }
    }

    @Test
    public void defaultRouteNullPropertiesTest() {
        previousList = new String[]{"", "Previous!#", "Previous#<> 123456-7890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        message = Mockito.mock(Message.class);
        value = new Object();
        exchange = Mockito.mock(Exchange.class);
        System.setProperty("camel.default_route.configuration_file_name", "camel-routes.xml");
        defaultRouter = new CamelKapuaDefaultRouter();
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("originalTopic");
        Mockito.when(message.getHeader(CamelConstants.JMS_CORRELATION_ID)).thenReturn("JMSCorrelationID");

        for (String previous : previousList) {
            assertNull("Null expected.", defaultRouter.defaultRoute(exchange, value, previous, null));
        }
    }
}