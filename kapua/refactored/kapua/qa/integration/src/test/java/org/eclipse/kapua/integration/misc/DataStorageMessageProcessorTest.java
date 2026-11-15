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
package org.eclipse.kapua.integration.misc;

import com.codahale.metrics.Counter;
import org.apache.camel.Exchange;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.listener.DataStorageMessageProcessor;
import org.eclipse.kapua.broker.core.listener.DataStoreMetrics;
import org.eclipse.kapua.broker.core.message.CamelKapuaMessage;
import org.eclipse.kapua.commons.metric.MetricServiceFactory;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.UUID;


@Category(JUnitTests.class)
public class DataStorageMessageProcessorTest {

    DataStorageMessageProcessor dataStorageMessageProcessor;
    CamelKapuaMessage camelKapuaMessage;
    KapuaMessage kapuaMessage;
    Exchange exchange;
    Counter queueConfigurationErrorCount, queueGenericErrorCount;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: dataStorageMessageProcessor = new DataStorageMessageProcessor();
        // Removed line due to General Fixture smell: camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        // Removed line due to General Fixture smell: kapuaMessage = Mockito.mock(KapuaMessage.class);
        // Removed line due to General Fixture smell: exchange = Mockito.mock(Exchange.class);
        // Removed line due to General Fixture smell: queueConfigurationErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_CONFIGURATION, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        // Removed line due to General Fixture smell: queueGenericErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_GENERIC, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);

        Mockito.when(camelKapuaMessage.getMessage()).thenReturn(kapuaMessage);
        Mockito.when(kapuaMessage.getId()).thenReturn(new UUID(10, 1));
        Mockito.when(camelKapuaMessage.getDatastoreId()).thenReturn("datastore ID");
    }

    @Test
    public void processConfigurationErrorMessageTest() throws KapuaException {
        queueConfigurationErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_CONFIGURATION, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        exchange = Mockito.mock(Exchange.class);
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueConfigurationErrorCount.getCount());
        dataStorageMessageProcessor.processConfigurationErrorMessage(exchange, camelKapuaMessage);
        Assert.assertEquals("Expected and actual values should be the same.", -1L, queueConfigurationErrorCount.getCount());

        queueConfigurationErrorCount.inc();
    }

    @Test
    public void processConfigurationErrorMessageNullExchangeTest() throws KapuaException {
        queueConfigurationErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_CONFIGURATION, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueConfigurationErrorCount.getCount());
        dataStorageMessageProcessor.processConfigurationErrorMessage(null, camelKapuaMessage);
        Assert.assertEquals("Expected and actual values should be the same.", -1L, queueConfigurationErrorCount.getCount());

        queueConfigurationErrorCount.inc();
    }

    @Test
    public void processConfigurationErrorNullMessageTest() {
        queueConfigurationErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_CONFIGURATION, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        exchange = Mockito.mock(Exchange.class);
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueConfigurationErrorCount.getCount());
        assertThrows(Exception e,
        		() -> {
        			dataStorageMessageProcessor.processConfigurationErrorMessage(exchange, null);
        			Assert.fail("NullPointerException expected.");
        		});
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0L, queueConfigurationErrorCount.getCount());
    }

    @Test
    public void processConfigurationErrorNullTest() {
        queueConfigurationErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_CONFIGURATION, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueConfigurationErrorCount.getCount());
        try {
            dataStorageMessageProcessor.processConfigurationErrorMessage(null, null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertEquals("NullPointerException expected.", new NullPointerException().toString(), e.toString());
        }
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0L, queueConfigurationErrorCount.getCount());
    }

    @Test
    public void processGenericErrorMessageTest() throws KapuaException {
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        exchange = Mockito.mock(Exchange.class);
        queueGenericErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_GENERIC, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueGenericErrorCount.getCount());
        dataStorageMessageProcessor.processGenericErrorMessage(exchange, camelKapuaMessage);
        Assert.assertEquals("Expected and actual values should be the same.", -1L, queueGenericErrorCount.getCount());

        queueGenericErrorCount.inc();
    }

    @Test
    public void processGenericErrorMessageNullExchangeTest() throws KapuaException {
        camelKapuaMessage = Mockito.mock(CamelKapuaMessage.class);
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        queueGenericErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_GENERIC, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueGenericErrorCount.getCount());
        dataStorageMessageProcessor.processGenericErrorMessage(null, camelKapuaMessage);
        Assert.assertEquals("Expected and actual values should be the same.", -1L, queueGenericErrorCount.getCount());

        queueGenericErrorCount.inc();
    }

    @Test
    public void processGenericErrorNullMessageTest() {
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        exchange = Mockito.mock(Exchange.class);
        queueGenericErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_GENERIC, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueGenericErrorCount.getCount());
        try {
            dataStorageMessageProcessor.processGenericErrorMessage(exchange, null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertEquals("NullPointerException expected.", new NullPointerException().toString(), e.toString());
        }
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0L, queueGenericErrorCount.getCount());
    }

    @Test
    public void processGenericErrorNullTest() {
        dataStorageMessageProcessor = new DataStorageMessageProcessor();
        queueGenericErrorCount = MetricServiceFactory.getInstance().getCounter(DataStoreMetrics.METRIC_MODULE_NAME, DataStoreMetrics.METRIC_COMPONENT_NAME, DataStoreMetrics.METRIC_STORE, DataStoreMetrics.METRIC_QUEUE, DataStoreMetrics.METRIC_GENERIC, DataStoreMetrics.METRIC_ERROR, DataStoreMetrics.METRIC_COUNT);
        Assert.assertEquals("Expected and actual values should be the same.", 0L, queueGenericErrorCount.getCount());
        try {
            dataStorageMessageProcessor.processGenericErrorMessage(null, null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertEquals("NullPointerException expected.", new NullPointerException().toString(), e.toString());
        }
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0L, queueGenericErrorCount.getCount());
    }
}