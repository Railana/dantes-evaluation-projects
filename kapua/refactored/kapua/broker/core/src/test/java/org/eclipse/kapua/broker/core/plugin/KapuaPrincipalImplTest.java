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
package org.eclipse.kapua.broker.core.plugin;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdImpl;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.shiro.AccessTokenImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

@Category(JUnitTests.class)
public class KapuaPrincipalImplTest extends Assert {

    AccessToken accessToken1;
    AccessToken accessToken2;
    String[] username;
    String[] clientId;
    String[] clientIp;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: accessToken1 = new AccessTokenImpl();
        // Removed line due to General Fixture smell: accessToken2 = new AccessTokenImpl();
        // Removed line due to General Fixture smell: username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        // Removed line due to General Fixture smell: clientId = new String[]{"clientId", "id1234567890", "id-!~#$%^&*()_=|?><,./", null};
        // Removed line due to General Fixture smell: clientIp = new String[]{"192.168.1.1", "clientIp", null};
    }

    @Test
    public void kapuaPrincipalImplTest() {
        clientIp = new String[]{"192.168.1.1", "clientIp", null};
        clientId = new String[]{"clientId", "id1234567890", "id-!~#$%^&*()_=|?><,./", null};
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken1.setTokenId("token id");
        accessToken1.setUserId(KapuaId.ONE);
        accessToken1.setScopeId(KapuaId.ONE);

        for (String name : username) {
            for (String id : clientId) {
                for (String ip : clientIp) {
                    KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, name, id, ip);
                    assertEquals("Expected and actual values should be the same.", name, kapuaPrincipal.getName());
                    assertEquals("Expected and actual values should be the same.", "token id", kapuaPrincipal.getTokenId());
                    assertEquals("Expected and actual values should be the same.", KapuaId.ONE, kapuaPrincipal.getUserId());
                    assertEquals("Expected and actual values should be the same.", KapuaId.ONE, kapuaPrincipal.getAccountId());
                    assertEquals("Expected and actual values should be the same.", id, kapuaPrincipal.getClientId());
                    assertEquals("Expected and actual values should be the same.", ip, kapuaPrincipal.getClientIp());
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void kapuaPrincipalImplNullTokenTest() {
        clientIp = new String[]{"192.168.1.1", "clientIp", null};
        clientId = new String[]{"clientId", "id1234567890", "id-!~#$%^&*()_=|?><,./", null};
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        for (String name : username) {
            for (String id : clientId) {
                for (String ip : clientIp) {
                    new KapuaPrincipalImpl(null, name, id, ip);
                }
            }
        }
    }

    @Test
    public void hasCodeNullAccountIdTest() {
        clientId = new String[]{"clientId", "id1234567890", "id-!~#$%^&*()_=|?><,./", null};
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, "username", "clientId", "192.168.1.1");
        Integer magicNumber47582016 = -265712489; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("Expected and actual values should be the same.",magicNumber47582016, kapuaPrincipal.hashCode());
    }

    @Test
    public void hasCodeNullAccountIdNullNameIdTest() {
        clientId = new String[]{"clientId", "id1234567890", "id-!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, null, "clientId", "192.168.1.1");
        assertEquals("Expected and actual values should be the same.", 961, kapuaPrincipal.hashCode());
    }

    @Test
    public void hasCodeNullNameIdTest() {
        clientId = new String[]{"clientId", "id1234567890", "id-!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken1.setScopeId(new KapuaIdImpl(BigInteger.TEN));
        KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, null, "clientId", "192.168.1.1");
        assertEquals("Expected and actual values should be the same.", 2232, kapuaPrincipal.hashCode());
    }

    @Test
    public void hasCodeTest() {
        clientId = new String[]{"clientId", "id1234567890", "id-!~#$%^&*()_=|?><,./", null};
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken1.setScopeId(new KapuaIdImpl(BigInteger.TEN));
        KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, "username", "clientId", "192.168.1.1");
        assertEquals("Expected and actual values should be the same.", -265711218, kapuaPrincipal.hashCode());
    }

    @Test
    public void equalsSameObjectTest() {
        clientIp = new String[]{"192.168.1.1", "clientIp", null};
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, "username", "client1", "clientIp");
        assertTrue("True expected.", kapuaPrincipal.equals(kapuaPrincipal));
    }

    @Test
    public void equalsNullTest() {
        clientIp = new String[]{"192.168.1.1", "clientIp", null};
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, "username", "client1", "clientIp");
        assertFalse("False expected.", kapuaPrincipal.equals(null));
    }

    @Test
    public void equalsDifferentClassTest() {
        clientIp = new String[]{"192.168.1.1", "clientIp", null};
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal = new KapuaPrincipalImpl(accessToken1, "username", "client1", "clientIp");
        assertFalse("False expected.", kapuaPrincipal.equals(new Object()));
    }

    @Test
    public void equalsNullAccountIdTest() {
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, "username1", "client1", "192.168.1.1");
        accessToken2.setScopeId(KapuaId.ONE);
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, "username2", "client2", "192.168.1.2");
        assertFalse("False expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }

    @Test
    public void equalsNullAccountIdsTest() {
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, "username1", "client1", "192.168.1.1");
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, "username2", "client2", "192.168.1.2");
        assertFalse("False expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }

    @Test
    public void equalsDifferentAccountIdsTest() {
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        accessToken1.setScopeId(new KapuaIdImpl(BigInteger.ONE));
        accessToken2.setScopeId(new KapuaIdImpl(BigInteger.TEN));
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, "username1", "client1", "192.168.1.1");
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, "username2", "client2", "192.168.1.2");
        assertFalse("False expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }

    @Test
    public void equalsSameAccountIdsTest() {
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        accessToken1.setScopeId(new KapuaIdImpl(BigInteger.ONE));
        accessToken2.setScopeId(new KapuaIdImpl(BigInteger.ONE));
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, "username1", "client1", "192.168.1.1");
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, "username2", "client2", "192.168.1.2");
        assertFalse("False expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }

    @Test
    public void equalsNullNameTest() {
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, null, "client1", "192.168.1.1");
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, "username2", "client2", "192.168.1.2");
        assertFalse("False expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }

    @Test
    public void equalsDifferentNamesTest() {
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, "username1", "client1", "192.168.1.1");
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, "username2", "client2", "192.168.1.2");
        assertFalse("False expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }

    @Test
    public void equalsNullNamesTest() {
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, null, "client1", "192.168.1.1");
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, null, "client2", "192.168.1.2");
        assertTrue("True expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }

    @Test
    public void equalsSameNamesTest() {
        username = new String[]{"username", "username1234567890", "name!~#$%^&*()_=|?><,./", null};
        accessToken1 = new AccessTokenImpl();
        accessToken2 = new AccessTokenImpl();
        KapuaPrincipalImpl kapuaPrincipal1 = new KapuaPrincipalImpl(accessToken1, "username1", "client1", "192.168.1.1");
        KapuaPrincipalImpl kapuaPrincipal2 = new KapuaPrincipalImpl(accessToken2, "username1", "client2", "192.168.1.2");
        assertTrue("True expected.", kapuaPrincipal1.equals(kapuaPrincipal2));
    }
}