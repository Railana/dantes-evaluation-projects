/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.rs.security.oauth2.grants.code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JPACodeDataProviderTest {
    private EntityManagerFactory emFactory;
    private Connection connection;
    private JPACodeDataProvider provider;

    @Before
    public void setUp() throws Exception {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:mem:oauth-jpa", "sa", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception during HSQL database init.");
        }
        try {
            emFactory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
            provider = new JPACodeDataProvider();
            getProvider().setEntityManagerFactory(emFactory);
            initializeProvider(provider);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception during JPA EntityManager creation.");
        }
    }

    protected String getPersistenceUnitName() {
        return "test-hibernate-cxf-rt-rs-security-oauth2";
    }

    protected void initializeProvider(JPACodeDataProvider dataProvider) {
        dataProvider.setSupportedScopes(Collections.singletonMap("a", "A Scope"));
    }

    protected JPACodeDataProvider getProvider() {
        return provider;
    }

    @Test
    public void testAddGetDeleteCodeGrants() {
        Client c = addClient("111", "bob");

        AuthorizationCodeRegistration atr = new AuthorizationCodeRegistration();
        atr.setClient(c);
        atr.setApprovedScope(Collections.singletonList("a"));
        atr.setSubject(c.getResourceOwnerSubject());

        ServerAuthorizationCodeGrant grant = getProvider().createCodeGrant(atr);

        List<ServerAuthorizationCodeGrant> grants = getProvider().getCodeGrants(c, c.getResourceOwnerSubject());
        assertNotNull(grants, () -> "Add assertion message");
        Integer magicNumber94615827 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber94615827, grants.size(), () -> "Add assertion message");
        assertEquals(grant.getCode(), grants.get(0).getCode(), () -> "Add assertion message");

        grants = getProvider().getCodeGrants(c, null);
        // Line removed due to Duplicate Assert: assertNotNull(grants, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, grants.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(grant.getCode(), grants.get(0).getCode(), () -> "Add assertion message");

        ServerAuthorizationCodeGrant grant2 = getProvider().removeCodeGrant(grant.getCode());
        assertEquals(grant.getCode(), grant2.getCode(), () -> "Add assertion message");

        grants = getProvider().getCodeGrants(c, null);
        // Line removed due to Duplicate Assert: assertNotNull(grants, () -> "Add assertion message");
        assertEquals(0, grants.size(), () -> "Add assertion message");
    }

    @Test
    public void testResetClient() {
        Client c = addClient("111", "bob");
        c.setClientSecret("newSecret");
        getProvider().setClient(c);
        Client savedClient = getProvider().getClient(c.getClientId());
        assertEquals(c.getClientSecret(), savedClient.getClientSecret(), () -> "Add assertion message");
    }

    @Test
    public void testAddGetDeleteCodeGrants2() {
        Client c = addClient("111", "bob");

        AuthorizationCodeRegistration atr = new AuthorizationCodeRegistration();
        atr.setClient(c);
        atr.setApprovedScope(Collections.singletonList("a"));
        atr.setSubject(c.getResourceOwnerSubject());

        getProvider().createCodeGrant(atr);

        List<ServerAuthorizationCodeGrant> grants = getProvider().getCodeGrants(c, c.getResourceOwnerSubject());
        assertNotNull(grants, () -> "Add assertion message");
        assertEquals(1, grants.size(), () -> "Add assertion message");
        getProvider().removeClient(c.getClientId());
        grants = getProvider().getCodeGrants(c, c.getResourceOwnerSubject());
        // Line removed due to Duplicate Assert: assertNotNull(grants, () -> "Add assertion message");
        assertEquals(0, grants.size(), () -> "Add assertion message");
    }

    private Client addClient(String clientId, String userLogin) {
        Client c = new Client();
        c.setRedirectUris(Collections.singletonList("http://client/redirect"));
        c.setClientId(clientId);
        c.setResourceOwnerSubject(new UserSubject(userLogin));
        getProvider().setClient(c);
        return c;
    }

    @After
    public void tearDown() throws Exception {
        try {
            if (provider != null) {
                getProvider().close();
            }
            if (emFactory != null) {
                emFactory.close();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            try {
                connection.createStatement().execute("SHUTDOWN");
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

}