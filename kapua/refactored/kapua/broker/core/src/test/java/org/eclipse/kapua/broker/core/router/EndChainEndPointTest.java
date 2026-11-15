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
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

@Category(JUnitTests.class)
public class EndChainEndPointTest extends Assert {

    EndChainEndPoint endChainEndPoint;
    Exchange[] exchanges;
    Object[] values;
    String[] previous;
    Map<String, Object> properties;
    StringBuffer buffer;
    String[] prefixList;

    @Before
    public void initialize() {
        endChainEndPoint = new EndChainEndPoint();
        // Removed line due to General Fixture smell: exchanges = new Exchange[]{null, Mockito.mock(Exchange.class)};
        // Removed line due to General Fixture smell: values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        // Removed line due to General Fixture smell: previous = new String[]{null, "", "Previous", "Previous1234567890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        // Removed line due to General Fixture smell: properties = new HashMap<>();
        // Removed line due to General Fixture smell: buffer = new StringBuffer();
        // Removed line due to General Fixture smell: prefixList = new String[]{"", "Prefix", "pre-fix", "123 prefix", "pre123fi_X21", "!PRE789fix", "PR#$"};
    }

    @Test
    public void matchesEmptyPropertiesTest() {
        previous = new String[]{null, "", "Previous", "Previous1234567890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        exchanges = new Exchange[]{null, Mockito.mock(Exchange.class)};
        properties = new HashMap<>();
        for (Exchange exchange : exchanges) {
            for (Object value : values) {
                for (String previousValue : previous) {
                    assertTrue("True expected.", endChainEndPoint.matches(exchange, value, previousValue, properties));
                }
            }
        }
    }

    @Test
    public void matchesTest() {
        previous = new String[]{null, "", "Previous", "Previous1234567890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        exchanges = new Exchange[]{null, Mockito.mock(Exchange.class)};
        properties = new HashMap<>();
        properties.put("key1", "value");
        properties.put("key2", 10);
        for (Exchange exchange : exchanges) {
            for (Object value : values) {
                for (String previousValue : previous) {
                    assertTrue("True expected.", endChainEndPoint.matches(exchange, value, previousValue, properties));
                }
            }
        }
    }

    @Test
    public void matchesNullPropertiesTest() {
        previous = new String[]{null, "", "Previous", "Previous1234567890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        exchanges = new Exchange[]{null, Mockito.mock(Exchange.class)};
        for (Exchange exchange : exchanges) {
            for (Object value : values) {
                for (String previousValue : previous) {
                    assertTrue("True expected.", endChainEndPoint.matches(exchange, value, previousValue, null));
                }
            }
        }
    }

    @Test
    public void getEndPointEmptyPropertiesTest() {
        previous = new String[]{null, "", "Previous", "Previous1234567890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        exchanges = new Exchange[]{null, Mockito.mock(Exchange.class)};
        properties = new HashMap<>();
        for (Exchange exchange : exchanges) {
            for (Object value : values) {
                for (String previousValue : previous) {
                    assertNull("Null expected.", endChainEndPoint.getEndPoint(exchange, value, previousValue, properties));
                }
            }
        }
    }

    @Test
    public void getEndPointTest() {
        previous = new String[]{null, "", "Previous", "Previous1234567890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        exchanges = new Exchange[]{null, Mockito.mock(Exchange.class)};
        properties = new HashMap<>();
        properties.put("key1", "value");
        properties.put("key2", 10);
        for (Exchange exchange : exchanges) {
            for (Object value : values) {
                for (String previousValue : previous) {
                    assertNull("Null expected.", endChainEndPoint.getEndPoint(exchange, value, previousValue, properties));
                }
            }
        }
    }

    @Test
    public void getEndPointNullPropertiesTest() {
        previous = new String[]{null, "", "Previous", "Previous1234567890", "PrEviouS-1pr23", "pR-12!viouS", "Previous!123#", "() _ + ?><|/.Previous!@#$ % ^&*"};
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        exchanges = new Exchange[]{null, Mockito.mock(Exchange.class)};
        for (Exchange exchange : exchanges) {
            for (Object value : values) {
                for (String previousValue : previous) {
                    assertNull("Null expected.", endChainEndPoint.getEndPoint(exchange, value, previousValue, null));
                }
            }
        }
    }

    @Test
    public void toLogTest() {
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        prefixList = new String[]{"", "Prefix", "pre-fix", "123 prefix", "pre123fi_X21", "!PRE789fix", "PR#$"};
        buffer = new StringBuffer();
        for (String prefix : prefixList) {
            endChainEndPoint.toLog(buffer, prefix);
            assertEquals("Expected and actual values should be the same.", "End chain", buffer.toString());
            buffer.delete(0, 9);
        }
    }

    @Test(expected = NullPointerException.class)
    public void toLogNullBufferTest() {
        prefixList = new String[]{"", "Prefix", "pre-fix", "123 prefix", "pre123fi_X21", "!PRE789fix", "PR#$"};
        for (String prefix : prefixList) {
            endChainEndPoint.toLog(null, prefix);
        }
    }

    @Test
    public void toLogNullPrefixTest() {
        values = new Object[]{null, new Object(), "value", 10, 10.10, true, false, 'c',};
        buffer = new StringBuffer();
        endChainEndPoint.toLog(buffer, null);
        assertEquals("Expected and actual values should be the same.", "End chain", buffer.toString());
    }
}