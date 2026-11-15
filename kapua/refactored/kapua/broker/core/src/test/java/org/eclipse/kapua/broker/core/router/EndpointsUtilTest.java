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
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Category(JUnitTests.class)
public class EndpointsUtilTest extends Assert {

    Exchange exchange;
    Object[] values;
    String[] previousList;
    Map<String, Object> properties;
    Pattern[] patterns;
    String[] topics;
    Message message;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: exchange = Mockito.mock(Exchange.class);
        values = new Object[]{null, true, false, 'c', "String", 10, 10L, 10.11f, 10.11d, 1, 0};
        // Removed line due to General Fixture smell: previousList = new String[]{"", "Previous", "Previous1234567890", "Previous!@#$%^&*()_+?><|/."};
        // Removed line due to General Fixture smell: properties = new HashMap<>();
        // Removed line due to General Fixture smell: patterns = new Pattern[]{Pattern.compile("String"), null};
        // Removed line due to General Fixture smell: topics = new String[]{"", "originalTopic", "Topic", "Topic1234567890", "Topic!@#$%^&*()_=-/."};
        // Removed line due to General Fixture smell: message = Mockito.mock(Message.class);
    }

    @Test
    public void matchesTest() {
        patterns = new Pattern[]{Pattern.compile("String"), null};
        previousList = new String[]{"", "Previous", "Previous1234567890", "Previous!@#$%^&*()_+?><|/."};
        properties = new HashMap<>();
        exchange = Mockito.mock(Exchange.class);
        for (Object value : values) {
            for (String previous : previousList) {
                for (Pattern pattern : patterns) {
                    assertFalse("False expected.", EndpointsUtil.matches(exchange, value, previous, properties, pattern));
                }
            }
        }
    }

    @Test
    public void matchesNullExchangeTest() {
        patterns = new Pattern[]{Pattern.compile("String"), null};
        previousList = new String[]{"", "Previous", "Previous1234567890", "Previous!@#$%^&*()_+?><|/."};
        properties = new HashMap<>();
        for (Object value : values) {
            for (String previous : previousList) {
                for (Pattern pattern : patterns) {
                    assertFalse("False expected.", EndpointsUtil.matches(null, value, previous, properties, pattern));
                }
            }
        }
    }

    @Test
    public void matchesNullPropertiesTest() {
        patterns = new Pattern[]{Pattern.compile("String"), null};
        previousList = new String[]{"", "Previous", "Previous1234567890", "Previous!@#$%^&*()_+?><|/."};
        exchange = Mockito.mock(Exchange.class);
        for (Object value : values) {
            for (String previous : previousList) {
                for (Pattern pattern : patterns) {
                    assertFalse("False expected.", EndpointsUtil.matches(exchange, value, previous, null, pattern));
                }
            }
        }
    }

    @Test
    public void matchesNullPreviousTrueTest() {
        properties = new HashMap<>();
        exchange = Mockito.mock(Exchange.class);
        message = Mockito.mock(Message.class);
        Pattern pattern = Pattern.compile("Topic");
        for (Object value : values) {
            Mockito.when(exchange.getIn()).thenReturn(message);
            Mockito.when(message.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("Topic");
            assertTrue("True expected.", EndpointsUtil.matches(exchange, value, null, properties, pattern));
        }
    }

    @Test
    public void matchesNullPreviousFalseTest() {
        properties = new HashMap<>();
        exchange = Mockito.mock(Exchange.class);
        message = Mockito.mock(Message.class);
        topics = new String[]{"", "originalTopic", "Topic", "Topic1234567890", "Topic!@#$%^&*()_=-/."};
        Pattern pattern = Pattern.compile("Different Topic");
        for (Object value : values) {
            for (String topic : topics) {
                Mockito.when(exchange.getIn()).thenReturn(message);
                Mockito.when(message.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn(topic);
                assertFalse("False expected.", EndpointsUtil.matches(exchange, value, null, properties, pattern));
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void matchesNullPreviousNullPatternTest() {
        properties = new HashMap<>();
        exchange = Mockito.mock(Exchange.class);
        message = Mockito.mock(Message.class);
        for (Object value : values) {
            Mockito.when(exchange.getIn()).thenReturn(message);
            Mockito.when(message.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class)).thenReturn("Topic");
            EndpointsUtil.matches(exchange, value, null, properties, null);
        }
    }
}