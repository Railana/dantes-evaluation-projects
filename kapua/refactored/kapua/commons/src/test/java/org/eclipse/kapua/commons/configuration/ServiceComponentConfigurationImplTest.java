/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.commons.configuration.metatype.TocdImpl;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;


@Category(JUnitTests.class)
public class ServiceComponentConfigurationImplTest {

    ServiceComponentConfigurationImpl serviceComponentConfiguration;

    @Before
    public void createInstanceOfClass() {
        // Removed line due to General Fixture smell: serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
    }

    @Test
    public void createInstanceWithSecondConstructorTest() {
        ServiceComponentConfigurationImpl componentConfiguration = new ServiceComponentConfigurationImpl("12");
        Assert.assertEquals(componentConfiguration.getId(), "12", () -> "Add assertion message");
    }

    @Test
    public void getIdIfNullTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        Assert.assertNull(serviceComponentConfiguration.getId(), () -> "Add assertion message");
    }

    @Test
    public void getIdIfIsSetTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setId("1");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "1", () -> "Add assertion message");
    }

    @Test
    public void setIdToNullValueTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setId(null);
        Assert.assertNull(serviceComponentConfiguration.getId(), () -> "Add assertion message");
    }

    @Test
    public void setIdToLargeValueTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setId("12312312312312331232313123123123");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "12312312312312331232313123123123", () -> "Add assertion message");
    }

    @Test
    public void setIdToSymbolsTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setId("@!#");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "@!#", () -> "Add assertion message");
    }

    @Test
    public void setIdToEmptyStringTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setId("");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "", () -> "Add assertion message");
    }

    @Test
    public void getNameIfNullTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        Assert.assertNull(serviceComponentConfiguration.getName(), () -> "Add assertion message");
    }

    @Test
    public void setNameIfNotNullTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setName("name");
        Assert.assertEquals(serviceComponentConfiguration.getName(), "name", () -> "Add assertion message");
    }

    @Test
    public void setNameToNullTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setName(null);
        Assert.assertNull(serviceComponentConfiguration.getName(), () -> "Add assertion message");
    }

    @Test
    public void setNameToNameThatContainsSpacesTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setName("regular Name");
        Assert.assertEquals(serviceComponentConfiguration.getName(), "regular Name", () -> "Add assertion message");
    }

    @Test
    public void setNameToNameThatContainsSymbolsTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setName("regular Name !$%&/&@");
        Assert.assertEquals(serviceComponentConfiguration.getName(), "regular Name !$%&/&@", () -> "Add assertion message");
    }

    @Test
    public void setDefinitionToNullTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setDefinition(null);
        Assert.assertNull(serviceComponentConfiguration.getDefinition(), () -> "Add assertion message");
    }

    @Test
    public void setDefinitionToRegularValueTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        KapuaTocd tocd = new TocdImpl();
        serviceComponentConfiguration.setDefinition(tocd);
        Assert.assertEquals(serviceComponentConfiguration.getDefinition(), tocd, () -> "Add assertion message");
    }

    @Test
    public void setPropertiesToNullTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        serviceComponentConfiguration.setProperties(null);
        Assert.assertNull(serviceComponentConfiguration.getProperties(), () -> "Add assertion message");
    }

    @Test
    public void setPropertiesToRegularValueTest() {
        serviceComponentConfiguration = new ServiceComponentConfigurationImpl();
        Map<String, Object> properties = new HashMap<>();
        properties.put("property1", 10);
        properties.put("property2", "string");
        properties.put("property3", 'c');
        properties.put("property4", (double)10);
        serviceComponentConfiguration.setProperties(properties);
        Assert.assertEquals(properties, serviceComponentConfiguration.getProperties(), () -> "Add assertion message");
    }
}