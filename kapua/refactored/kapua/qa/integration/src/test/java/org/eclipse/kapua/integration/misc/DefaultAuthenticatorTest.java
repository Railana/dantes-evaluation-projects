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
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.plugin.Acl;
import org.eclipse.kapua.broker.core.plugin.KapuaSecurityContext;
import org.eclipse.kapua.broker.core.plugin.authentication.AuthorizationEntry;
import org.eclipse.kapua.broker.core.plugin.authentication.DefaultAuthenticator;
import org.eclipse.kapua.broker.core.plugin.metric.ClientMetric;
import org.eclipse.kapua.broker.core.plugin.metric.LoginMetric;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Category(JUnitTests.class)
public class DefaultAuthenticatorTest {

    KapuaSecurityContext kapuaSecurityContext;
    Map<String, Object> options;
    DefaultAuthenticator defaultAuthenticator;
    Throwable[] throwables;
    Counter kapuasysTokenAttemptCount, connectedKapuasysCount, disconnectedKapuasysCount, disconnectedClient;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        // Removed line due to General Fixture smell: options = new HashMap<>();
        // Removed line due to General Fixture smell: throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        // Removed line due to General Fixture smell: kapuasysTokenAttemptCount = LoginMetric.getInstance().getKapuasysTokenAttempt();
        // Removed line due to General Fixture smell: connectedKapuasysCount = ClientMetric.getInstance().getConnectedKapuasys();
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: disconnectedKapuasysCount = ClientMetric.getInstance().getDisconnectedKapuasys();
        // Removed line due to General Fixture smell: disconnectedClient = ClientMetric.getInstance().getDisconnectedClient();
    }

    @Test
    public void defaultAuthenticatorEmptyOptionsTest() {
        options = new HashMap<>();
        assertThrows(Exception e,
        		() -> new DefaultAuthenticator(options));
    }

    @Test
    public void defaultAuthenticatorTest() {
        options = new HashMap<>();
        options.put("key1", "value");
        options.put("key2", 11);
        options.put("key3", new Object());

        assertThrows(Exception e,
        		() -> new DefaultAuthenticator(options));
    }

    @Test
    public void defaultAuthenticatorNullOptionsTest() {
        assertThrows(Exception e,
        		() -> {
        			new DefaultAuthenticator(null);
        			Assert.fail("KapuaException expected.");
        		});
    }

    @Test
    public void connectAdminEmptyOptionsTest() throws KapuaException {
        disconnectedClient = ClientMetric.getInstance().getDisconnectedClient();
        connectedKapuasysCount = ClientMetric.getInstance().getConnectedKapuasys();
        disconnectedKapuasysCount = ClientMetric.getInstance().getDisconnectedKapuasys();
        kapuasysTokenAttemptCount = LoginMetric.getInstance().getKapuasysTokenAttempt();
        options = new HashMap<>();
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        defaultAuthenticator = new DefaultAuthenticator(options);

        Mockito.when(kapuaSecurityContext.getUserName()).thenReturn("kapua-sys");

        Integer magicNumber48699236 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber48699236, kapuasysTokenAttemptCount.getCount());
        Integer magicNumber14095012 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber14095012, connectedKapuasysCount.getCount());
        Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedKapuasysCount.getCount());
        Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedClient.getCount());

        List<AuthorizationEntry> authorizationEntryList = defaultAuthenticator.connect(kapuaSecurityContext);

        Assert.assertEquals("Expected and actual values should be the same.", 2, authorizationEntryList.size());
        Assert.assertEquals("Expected and actual values should be the same.", "null>", authorizationEntryList.get(0).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.ALL, authorizationEntryList.get(0).getAcl());
        Assert.assertEquals("Expected and actual values should be the same.", "nullnull", authorizationEntryList.get(1).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.WRITE_ADMIN, authorizationEntryList.get(1).getAcl());

        Assert.assertEquals("Expected and actual values should be the same.", 1, kapuasysTokenAttemptCount.getCount());
        Assert.assertEquals("Expected and actual values should be the same.", 1, connectedKapuasysCount.getCount());
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedKapuasysCount.getCount());
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedClient.getCount());

        kapuasysTokenAttemptCount.dec();
        connectedKapuasysCount.dec();
    }

    @Test
    public void connectAdminTest() throws KapuaException {
        disconnectedClient = ClientMetric.getInstance().getDisconnectedClient();
        connectedKapuasysCount = ClientMetric.getInstance().getConnectedKapuasys();
        disconnectedKapuasysCount = ClientMetric.getInstance().getDisconnectedKapuasys();
        kapuasysTokenAttemptCount = LoginMetric.getInstance().getKapuasysTokenAttempt();
        options = new HashMap<>();
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        options.put("address_prefix", "prefix");
        options.put("address_advisory_prefix", "advisory_prefix");
        defaultAuthenticator = new DefaultAuthenticator(options);

        Mockito.when(kapuaSecurityContext.getUserName()).thenReturn("kapua-sys");

        Integer magicNumber88419883 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber88419883, kapuasysTokenAttemptCount.getCount());
        Assert.assertEquals("Expected and actual values should be the same.", 0, connectedKapuasysCount.getCount());
        Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedKapuasysCount.getCount());
        Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedClient.getCount());

        List<AuthorizationEntry> authorizationEntryList = defaultAuthenticator.connect(kapuaSecurityContext);

        Assert.assertEquals("Expected and actual values should be the same.", 2, authorizationEntryList.size());
        Assert.assertEquals("Expected and actual values should be the same.", "prefix>", authorizationEntryList.get(0).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.ALL, authorizationEntryList.get(0).getAcl());
        Assert.assertEquals("Expected and actual values should be the same.", "prefixadvisory_prefix", authorizationEntryList.get(1).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.WRITE_ADMIN, authorizationEntryList.get(1).getAcl());

        Assert.assertEquals("Expected and actual values should be the same.", 1, kapuasysTokenAttemptCount.getCount());
        Assert.assertEquals("Expected and actual values should be the same.", 1, connectedKapuasysCount.getCount());
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedKapuasysCount.getCount());
        // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedClient.getCount());

        kapuasysTokenAttemptCount.dec();
        connectedKapuasysCount.dec();
    }

    @Test
    public void disconnectAdminTest() throws KapuaException {
        disconnectedClient = ClientMetric.getInstance().getDisconnectedClient();
        connectedKapuasysCount = ClientMetric.getInstance().getConnectedKapuasys();
        disconnectedKapuasysCount = ClientMetric.getInstance().getDisconnectedKapuasys();
        kapuasysTokenAttemptCount = LoginMetric.getInstance().getKapuasysTokenAttempt();
        throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        options = new HashMap<>();
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        defaultAuthenticator = new DefaultAuthenticator(options);
        Mockito.when(kapuaSecurityContext.getUserName()).thenReturn("kapua-sys");

        for (Throwable throwable : throwables) {
            Integer magicNumber67537861 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            Assert.assertEquals("Expected and actual values should be the same.",magicNumber67537861, kapuasysTokenAttemptCount.getCount());
            Assert.assertEquals("Expected and actual values should be the same.", 0, connectedKapuasysCount.getCount());
            Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedKapuasysCount.getCount());
            Integer magicNumber44312909 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            Assert.assertEquals("Expected and actual values should be the same.",magicNumber44312909, disconnectedClient.getCount());
            defaultAuthenticator.disconnect(kapuaSecurityContext, throwable);
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, kapuasysTokenAttemptCount.getCount());
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, connectedKapuasysCount.getCount());
            Assert.assertEquals("Expected and actual values should be the same.", 1, disconnectedKapuasysCount.getCount());
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedClient.getCount());

            disconnectedKapuasysCount.dec();
        }
    }

    @Test
    public void disconnectUserTest() throws KapuaException {
        disconnectedClient = ClientMetric.getInstance().getDisconnectedClient();
        connectedKapuasysCount = ClientMetric.getInstance().getConnectedKapuasys();
        disconnectedKapuasysCount = ClientMetric.getInstance().getDisconnectedKapuasys();
        kapuasysTokenAttemptCount = LoginMetric.getInstance().getKapuasysTokenAttempt();
        throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        options = new HashMap<>();
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        defaultAuthenticator = new DefaultAuthenticator(options);
        Mockito.when(kapuaSecurityContext.getOldConnectionId()).thenReturn("ConnectionId");
        Mockito.when(kapuaSecurityContext.getConnectionId()).thenReturn("NewConnectionId");
        Mockito.when(kapuaSecurityContext.getClientId()).thenReturn("clientid");

        Mockito.when(kapuaSecurityContext.getUserName()).thenReturn("user");
        for (Throwable throwable : throwables) {
            Assert.assertEquals("Expected and actual values should be the same.", 0, kapuasysTokenAttemptCount.getCount());
            Assert.assertEquals("Expected and actual values should be the same.", 0, connectedKapuasysCount.getCount());
            Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedKapuasysCount.getCount());
            Integer magicNumber14919030 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            Assert.assertEquals("Expected and actual values should be the same.",magicNumber14919030, disconnectedClient.getCount());
            defaultAuthenticator.disconnect(kapuaSecurityContext, throwable);
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, kapuasysTokenAttemptCount.getCount());
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, connectedKapuasysCount.getCount());
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, disconnectedKapuasysCount.getCount());
            Assert.assertEquals("Expected and actual values should be the same.", 1, disconnectedClient.getCount());

            disconnectedClient.dec();
        }
    }
}