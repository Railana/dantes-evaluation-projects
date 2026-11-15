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
package org.eclipse.kapua.broker.core.plugin.authorization;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.security.AuthorizationMap;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.plugin.KapuaSecurityContext;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

@Category(JUnitTests.class)
public class DefaultAuthorizerTest extends Assert {

    DefaultAuthorizer defaultAuthorizer;
    KapuaSecurityContext kapuaSecurityContext;
    ActiveMQDestination[] destinations;
    Set allowedACLs;
    AuthorizationMap authorizationMap;

    @Before
    public void initialize() {
        defaultAuthorizer = new DefaultAuthorizer();
        // Removed line due to General Fixture smell: kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        destinations = new ActiveMQDestination[]{null, Mockito.mock(ActiveMQDestination.class)};
        // Removed line due to General Fixture smell: allowedACLs = new HashSet<>();
        // Removed line due to General Fixture smell: authorizationMap = Mockito.mock(AuthorizationMap.class);

        Mockito.when(kapuaSecurityContext.getAuthorizationMap()).thenReturn(authorizationMap);
    }

    @Test
    public void isAllowedReadTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getReadACLs(destination)).thenReturn(allowedACLs);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(false);

            assertFalse("False expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.READ, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedReadNullACLsTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getReadACLs(destination)).thenReturn(null);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(false);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.READ, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedReadContextIsInTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getReadACLs(destination)).thenReturn(allowedACLs);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(true);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.READ, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedReadContextIsInNullACLsTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getReadACLs(destination)).thenReturn(null);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(true);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.READ, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedWriteTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getWriteACLs(destination)).thenReturn(allowedACLs);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(false);
            assertFalse("False expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.WRITE, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedWriteNullACLsTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getWriteACLs(destination)).thenReturn(null);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(false);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.WRITE, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedWriteContextIsInTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getWriteACLs(destination)).thenReturn(allowedACLs);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(true);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.WRITE, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedWriteContextIsInNullACLsTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getWriteACLs(destination)).thenReturn(null);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(true);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.WRITE, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedAdminTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getAdminACLs(destination)).thenReturn(allowedACLs);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(false);

            assertFalse("False expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.ADMIN, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedAdminNullACLsTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getAdminACLs(destination)).thenReturn(null);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(false);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.ADMIN, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedAdminContextIsInTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getAdminACLs(destination)).thenReturn(allowedACLs);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(true);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.ADMIN, kapuaSecurityContext, destination));
        }
    }

    @Test
    public void isAllowedAdminContextIsInNullACLsTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        authorizationMap = Mockito.mock(AuthorizationMap.class);
        allowedACLs = new HashSet<>();
        for (ActiveMQDestination destination : destinations) {
            Mockito.when(authorizationMap.getAdminACLs(destination)).thenReturn(null);
            Mockito.when(kapuaSecurityContext.isInOneOf(allowedACLs)).thenReturn(true);

            assertTrue("True expected.", defaultAuthorizer.isAllowed(Authorizer.ActionType.ADMIN, kapuaSecurityContext, destination));
        }
    }

    @Test(expected = NullPointerException.class)
    public void isAllowedNullTypeTest() throws KapuaException {
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        for (ActiveMQDestination destination : destinations) {
            defaultAuthorizer.isAllowed(null, kapuaSecurityContext, destination);
        }
    }

    @Test(expected = NullPointerException.class)
    public void isAllowedNullContextTest() throws KapuaException {
        for (ActiveMQDestination destination : destinations) {
            defaultAuthorizer.isAllowed(Authorizer.ActionType.READ, null, destination);
        }
    }
}