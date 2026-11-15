/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.distributedlog.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.bookkeeper.common.testing.annotations.FlakyTest;
import org.apache.bookkeeper.feature.Feature;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.common.config.PropertiesWriter;
import org.junit.Test;

/**
 * Test case for dynamic configuration based feature provider.
 */
public class TestDynamicConfigurationFeatureProvider {

    /**
     * Make sure config is reloaded.
     *Give FileChangedReloadingStrategy some time to allow reloading
     * Make sure now!=lastChecked
     * {@link org.apache.commons.configuration.reloading.FileChangedReloadingStrategy#reloadingRequired()}
     */
    private void ensureConfigReloaded() throws InterruptedException {
        // sleep 1 ms so that System.currentTimeMillis() !=
        // lastChecked (the time we construct FileChangedReloadingStrategy
        Thread.sleep(1);
    }

    @Test(timeout = 60000)
    public void testLoadFeaturesFromBase() throws Exception {
        PropertiesWriter writer = new PropertiesWriter();
        writer.setProperty("feature_1", "10000");
        writer.setProperty("feature_2", "5000");
        writer.save();

        DistributedLogConfiguration conf = new DistributedLogConfiguration()
                .setDynamicConfigReloadIntervalSec(Integer.MAX_VALUE)
                .setFileFeatureProviderBaseConfigPath(writer.getFile().toURI().toURL().getPath());
        DynamicConfigurationFeatureProvider provider =
                new DynamicConfigurationFeatureProvider("", conf, NullStatsLogger.INSTANCE);
        provider.start();
        ensureConfigReloaded();

        Feature feature1 = provider.getFeature("feature_1");
        assertTrue(feature1.isAvailable(), () -> "Add assertion message");
        Integer magicNumber42271552 = 10000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber42271552, feature1.availability(), () -> "Add assertion message");
        Feature feature2 = provider.getFeature("feature_2");
        assertTrue(feature2.isAvailable(), () -> "Add assertion message");
        assertEquals(5000, feature2.availability(), () -> "Add assertion message");
        Feature feature3 = provider.getFeature("feature_3");
        assertFalse(feature3.isAvailable(), () -> "Add assertion message");
        assertEquals(0, feature3.availability(), () -> "Add assertion message");
        Feature feature4 = provider.getFeature("unknown_feature");
        assertFalse(feature4.isAvailable(), () -> "Add assertion message");
        assertEquals(0, feature4.availability(), () -> "Add assertion message");

        provider.stop();
    }

    @FlakyTest("https://issues.apache.org/jira/browse/DL-40")
    public void testLoadFeaturesFromOverlay() throws Exception {
        PropertiesWriter writer = new PropertiesWriter();
        writer.setProperty("feature_1", "10000");
        writer.setProperty("feature_2", "5000");
        writer.save();

        PropertiesWriter overlayWriter = new PropertiesWriter();
        overlayWriter.setProperty("feature_2", "6000");
        overlayWriter.setProperty("feature_4", "6000");
        overlayWriter.save();

        DistributedLogConfiguration conf = new DistributedLogConfiguration()
                .setDynamicConfigReloadIntervalSec(Integer.MAX_VALUE)
                .setFileFeatureProviderBaseConfigPath(writer.getFile().toURI().toURL().getPath())
                .setFileFeatureProviderOverlayConfigPath(overlayWriter.getFile().toURI().toURL().getPath());
        DynamicConfigurationFeatureProvider provider =
                new DynamicConfigurationFeatureProvider("", conf, NullStatsLogger.INSTANCE);
        provider.start();
        ensureConfigReloaded();

        Feature feature1 = provider.getFeature("feature_1");
        assertTrue(feature1.isAvailable(), () -> "Add assertion message");
        assertEquals(10000, feature1.availability(), () -> "Add assertion message");
        Feature feature2 = provider.getFeature("feature_2");
        assertTrue(feature2.isAvailable(), () -> "Add assertion message");
        assertEquals(6000, feature2.availability(), () -> "Add assertion message");
        Feature feature3 = provider.getFeature("feature_3");
        assertFalse(feature3.isAvailable(), () -> "Add assertion message");
        assertEquals(0, feature3.availability(), () -> "Add assertion message");
        Feature feature4 = provider.getFeature("feature_4");
        assertTrue(feature4.isAvailable(), () -> "Add assertion message");
        assertEquals(6000, feature4.availability(), () -> "Add assertion message");
        Feature feature5 = provider.getFeature("unknown_feature");
        assertFalse(feature5.isAvailable(), () -> "Add assertion message");
        assertEquals(0, feature5.availability(), () -> "Add assertion message");

        provider.stop();
    }

    @Test(timeout = 60000)
    public void testReloadFeaturesFromOverlay() throws Exception {
        PropertiesWriter writer = new PropertiesWriter();
        writer.setProperty("feature_1", "10000");
        writer.setProperty("feature_2", "5000");
        writer.save();

        PropertiesWriter overlayWriter = new PropertiesWriter();
        overlayWriter.setProperty("feature_2", "6000");
        overlayWriter.setProperty("feature_4", "6000");
        overlayWriter.save();

        DistributedLogConfiguration conf = new DistributedLogConfiguration()
                .setDynamicConfigReloadIntervalSec(Integer.MAX_VALUE)
                .setFileFeatureProviderBaseConfigPath(writer.getFile().toURI().toURL().getPath())
                .setFileFeatureProviderOverlayConfigPath(overlayWriter.getFile().toURI().toURL().getPath());
        DynamicConfigurationFeatureProvider provider =
                new DynamicConfigurationFeatureProvider("", conf, NullStatsLogger.INSTANCE);
        provider.start();
        ensureConfigReloaded();

        Feature feature1 = provider.getFeature("feature_1");
        assertTrue(feature1.isAvailable(), () -> "Add assertion message");
        assertEquals(10000, feature1.availability(), () -> "Add assertion message");
        Feature feature2 = provider.getFeature("feature_2");
        assertTrue(feature2.isAvailable(), () -> "Add assertion message");
        assertEquals(6000, feature2.availability(), () -> "Add assertion message");
        Feature feature3 = provider.getFeature("feature_3");
        assertFalse(feature3.isAvailable(), () -> "Add assertion message");
        assertEquals(0, feature3.availability(), () -> "Add assertion message");
        Feature feature4 = provider.getFeature("feature_4");
        assertTrue(feature4.isAvailable(), () -> "Add assertion message");
        assertEquals(6000, feature4.availability(), () -> "Add assertion message");
        Feature feature5 = provider.getFeature("unknown_feature");
        assertFalse(feature5.isAvailable(), () -> "Add assertion message");
        assertEquals(0, feature5.availability(), () -> "Add assertion message");

        // dynamic load config
        provider.getFeatureConf().setProperty("feature_1", 3000);
        provider.getFeatureConf().setProperty("feature_2", 7000);
        provider.getFeatureConf().setProperty("feature_3", 8000);
        provider.getFeatureConf().setProperty("feature_4", 9000);
        provider.onReload(provider.getFeatureConf());
        feature1 = provider.getFeature("feature_1");
        // Line removed due to Duplicate Assert: assertTrue(feature1.isAvailable(), () -> "Add assertion message");
        assertEquals(3000, feature1.availability(), () -> "Add assertion message");
        feature2 = provider.getFeature("feature_2");
        // Line removed due to Duplicate Assert: assertTrue(feature2.isAvailable(), () -> "Add assertion message");
        assertEquals(7000, feature2.availability(), () -> "Add assertion message");
        feature3 = provider.getFeature("feature_3");
        assertTrue(feature3.isAvailable(), () -> "Add assertion message");
        assertEquals(8000, feature3.availability(), () -> "Add assertion message");
        feature4 = provider.getFeature("feature_4");
        // Line removed due to Duplicate Assert: assertTrue(feature4.isAvailable(), () -> "Add assertion message");
        assertEquals(9000, feature4.availability(), () -> "Add assertion message");

        provider.stop();
    }

}