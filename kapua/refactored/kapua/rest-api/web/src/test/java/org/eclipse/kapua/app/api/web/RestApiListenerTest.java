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
package org.eclipse.kapua.app.api.web;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import javax.servlet.ServletContextEvent;


@Category(JUnitTests.class)
public class RestApiListenerTest {

    RestApiListener restApiListener;
    ServletContextEvent servletContextEvent;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: restApiListener = new RestApiListener();
        // Removed line due to General Fixture smell: servletContextEvent = Mockito.mock(ServletContextEvent.class);
    }

    @Test
    public void contextInitializedTest() {
        servletContextEvent = Mockito.mock(ServletContextEvent.class);
        restApiListener = new RestApiListener();
        assertThrows(Exception e,
        		() -> restApiListener.contextInitialized(servletContextEvent));
    }

    @Test
    public void contextInitializedNullTest() {
        restApiListener = new RestApiListener();
        assertThrows(Exception e,
        		() -> restApiListener.contextInitialized(null));
    }

    @Test
    public void contextDestroyedTest() {
        servletContextEvent = Mockito.mock(ServletContextEvent.class);
        restApiListener = new RestApiListener();
        assertThrows(Exception e,
        		() -> restApiListener.contextDestroyed(servletContextEvent));
    }

    @Test
    public void contextDestroyedNullTest() {
        restApiListener = new RestApiListener();
        assertThrows(Exception e,
        		() -> restApiListener.contextDestroyed(null));
    }
}