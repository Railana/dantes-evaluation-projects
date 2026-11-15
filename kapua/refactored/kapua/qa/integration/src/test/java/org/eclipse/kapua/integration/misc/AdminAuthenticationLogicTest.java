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
import org.eclipse.kapua.broker.core.plugin.authentication.AdminAuthenticationLogic;
import org.eclipse.kapua.broker.core.plugin.authentication.AuthorizationEntry;
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
public class AdminAuthenticationLogicTest {

    Map<String, Object> options;
    KapuaSecurityContext kapuaSecurityContext;
    AdminAuthenticationLogic adminAuthenticationLogic;
    Throwable[] throwables;
    Counter adminStealingLinkDisconnectCount;

    @Before
    public void initialize() {
        options = new HashMap<>();
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: options = new HashMap<>();
        // Removed line due to General Fixture smell: kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        // Removed line due to General Fixture smell: adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        // Removed line due to General Fixture smell: throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        adminStealingLinkDisconnectCount = LoginMetric.getInstance().getAdminStealingLinkDisconnect();
    }

    @Test
    public void adminAuthenticationLogicEmptyOptionsTest() {
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        options = new HashMap<>();
        assertThrows(Exception e,
        		() -> new AdminAuthenticationLogic(options));
    }

    @Test
    public void adminAuthenticationLogicTest() {
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        options = new HashMap<>();
        options.put("key1", "value1");
        options.put("key2", "value2");
        options.put("key3", 11);
        options.put("key4", new Object());

        assertThrows(Exception e,
        		() -> new AdminAuthenticationLogic(options));
    }

    @Test(expected = NullPointerException.class)
    public void adminAuthenticationLogicNullTest() {
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        new AdminAuthenticationLogic(null);
    }

    @Test
    public void connectWithoutPrefixTest() throws KapuaException {
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        List<AuthorizationEntry> authorizationEntryList = adminAuthenticationLogic.connect(kapuaSecurityContext);

        Integer magicNumber37610565 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber37610565, authorizationEntryList.size());
        Assert.assertEquals("Expected and actual values should be the same.", "null>", authorizationEntryList.get(0).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.ALL, authorizationEntryList.get(0).getAcl());
        Assert.assertEquals("Expected and actual values should be the same.", "nullnull", authorizationEntryList.get(1).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.WRITE_ADMIN, authorizationEntryList.get(1).getAcl());
    }

    @Test
    public void connectWithPrefixTest() throws KapuaException {
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        options = new HashMap<>();
        options.put("address_prefix", "prefix");
        options.put("address_advisory_prefix", "advisory_prefix");
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);

        List<AuthorizationEntry> authorizationEntryList = adminAuthenticationLogic.connect(kapuaSecurityContext);

        Assert.assertEquals("Expected and actual values should be the same.", 2, authorizationEntryList.size());
        Assert.assertEquals("Expected and actual values should be the same.", "prefix>", authorizationEntryList.get(0).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.ALL, authorizationEntryList.get(0).getAcl());
        Assert.assertEquals("Expected and actual values should be the same.", "prefixadvisory_prefix", authorizationEntryList.get(1).getAddress());
        Assert.assertEquals("Expected and actual values should be the same.", Acl.WRITE_ADMIN, authorizationEntryList.get(1).getAcl());
    }

    @Test(expected = NullPointerException.class)
    public void connectNullTest() throws KapuaException {
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        adminAuthenticationLogic.connect(null);
    }

    @Test
    public void disconnectFalseStealingLinkTrueMissingTest() {
        throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        Mockito.when(kapuaSecurityContext.getOldConnectionId()).thenReturn("ConnectionId");
        Mockito.when(kapuaSecurityContext.getConnectionId()).thenReturn("ConnectionId");
        Mockito.when(kapuaSecurityContext.isMissing()).thenReturn(true);

        for (Throwable throwable : throwables) {
            Assert.assertEquals("Expected and actual values should be the same.", 0, adminStealingLinkDisconnectCount.getCount());
            Assert.assertFalse("False expected.", (adminAuthenticationLogic.disconnect(kapuaSecurityContext, throwable)));
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, adminStealingLinkDisconnectCount.getCount());
        }
    }

    @Test
    public void disconnectFalseStealingLinkFalseMissingTest() {
        throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        Mockito.when(kapuaSecurityContext.getOldConnectionId()).thenReturn("ConnectionId");
        Mockito.when(kapuaSecurityContext.getConnectionId()).thenReturn("ConnectionId");
        Mockito.when(kapuaSecurityContext.isMissing()).thenReturn(false);

        for (Throwable throwable : throwables) {
            Assert.assertEquals("Expected and actual values should be the same.", 0, adminStealingLinkDisconnectCount.getCount());
            Assert.assertTrue("True expected.", (adminAuthenticationLogic.disconnect(kapuaSecurityContext, throwable)));
            // Line removed due to Duplicate Assert: Assert.assertEquals("Expected and actual values should be the same.", 0, adminStealingLinkDisconnectCount.getCount());
        }
    }

    @Test
    public void disconnectTrueStealingLinkFalseMissingTest() {
        throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        Mockito.when(kapuaSecurityContext.getOldConnectionId()).thenReturn("ConnectionId");
        Mockito.when(kapuaSecurityContext.getConnectionId()).thenReturn("NewConnectionId");
        Mockito.when(kapuaSecurityContext.isMissing()).thenReturn(false);

        for (Throwable throwable : throwables) {
            Assert.assertEquals("Expected and actual values should be the same.", 0, adminStealingLinkDisconnectCount.getCount());
            Assert.assertFalse("False expected.", (adminAuthenticationLogic.disconnect(kapuaSecurityContext, throwable)));
            Assert.assertEquals("Expected and actual values should be the same.", 1, adminStealingLinkDisconnectCount.getCount());

            adminStealingLinkDisconnectCount.dec();
        }
    }

    @Test
    public void disconnectTrueStealingLinkTrueMissingTest() {
        throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        kapuaSecurityContext = Mockito.mock(KapuaSecurityContext.class);
        Mockito.when(kapuaSecurityContext.getOldConnectionId()).thenReturn("ConnectionId");
        Mockito.when(kapuaSecurityContext.getConnectionId()).thenReturn("NewConnectionId");
        Mockito.when(kapuaSecurityContext.isMissing()).thenReturn(true);

        for (Throwable throwable : throwables) {
            Assert.assertEquals("Expected and actual values should be the same.", 0, adminStealingLinkDisconnectCount.getCount());
            Assert.assertFalse("False expected.", (adminAuthenticationLogic.disconnect(kapuaSecurityContext, throwable)));
            Assert.assertEquals("Expected and actual values should be the same.", 1, adminStealingLinkDisconnectCount.getCount());

            adminStealingLinkDisconnectCount.dec();
        }
    }

    @Test(expected = NullPointerException.class)
    public void disconnectNullContextTest() {
        throwables = new Throwable[]{null, new Throwable(), new Throwable("message"), new Exception(), new Throwable(new Exception())};
        adminAuthenticationLogic = new AdminAuthenticationLogic(options);
        for (Throwable throwable : throwables) {
            adminAuthenticationLogic.disconnect(null, throwable);
        }
    }
}