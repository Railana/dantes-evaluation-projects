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
package org.apache.cxf.systest.jaxrs.security.oidc;

import java.net.URL;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.KeyType;
import org.apache.cxf.systest.jaxrs.security.oauth2.common.OAuth2TestUtils;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;

import org.junit.BeforeClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Some tests for the OIDC Keys Service
 */
public class OIDCKeysServiceTest extends AbstractBusClientServerTestBase {

    private static final SpringBusTestServer JCACHE_SERVER = new SpringBusTestServer("oidc-keys-jcache");


    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("Server failed to launch", launchServer(JCACHE_SERVER));
    }

    @org.junit.Test
    public void testGetRSAPublicKey() throws Exception {
        URL busFile = OIDCFlowTest.class.getResource("client.xml");

        String address = "https://localhost:" + JCACHE_SERVER.getPort() + "/services/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                            "alice", "security", busFile.toString());
        client.accept("application/json");

        client.path("keys/");
        Response response = client.get();
        JsonWebKeys jsonWebKeys = response.readEntity(JsonWebKeys.class);

        Integer magicNumber73812053 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber73812053, jsonWebKeys.getKeys().size(), () -> "Add assertion message");

        JsonWebKey jsonWebKey = jsonWebKeys.getKeys().get(0);
        assertEquals(KeyType.RSA, jsonWebKey.getKeyType(), () -> "Add assertion message");
        assertEquals("alice", jsonWebKey.getKeyId(), () -> "Add assertion message");
        assertNotNull(jsonWebKey.getProperty("n"), () -> "Add assertion message");
        assertNotNull(jsonWebKey.getProperty("e"), () -> "Add assertion message");
        // Check we don't send the private key back
        checkPrivateKeyParametersNotPresent(jsonWebKeys);
    }

    @org.junit.Test
    public void testGetJWKRSAPublicKey() throws Exception {
        URL busFile = OIDCFlowTest.class.getResource("client.xml");

        String address = "https://localhost:" + JCACHE_SERVER.getPort() + "/services2/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                            "alice", "security", busFile.toString());
        client.accept("application/json");

        client.path("keys/");
        Response response = client.get();
        JsonWebKeys jsonWebKeys = response.readEntity(JsonWebKeys.class);

        assertEquals(1, jsonWebKeys.getKeys().size(), () -> "Add assertion message");

        JsonWebKey jsonWebKey = jsonWebKeys.getKeys().get(0);
        assertEquals(KeyType.RSA, jsonWebKey.getKeyType(), () -> "Add assertion message");
        assertEquals("2011-04-29", jsonWebKey.getKeyId(), () -> "Add assertion message");
        assertNotNull(jsonWebKey.getProperty("n"), () -> "Add assertion message");
        assertNotNull(jsonWebKey.getProperty("e"), () -> "Add assertion message");
        // Check we don't send the private key back
        checkPrivateKeyParametersNotPresent(jsonWebKeys);
    }

    @org.junit.Test
    public void testGetJWKECPublicKey() throws Exception {
        URL busFile = OIDCFlowTest.class.getResource("client.xml");

        String address = "https://localhost:" + JCACHE_SERVER.getPort() + "/services3/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                            "alice", "security", busFile.toString());
        client.accept("application/json");

        client.path("keys/");
        Response response = client.get();
        JsonWebKeys jsonWebKeys = response.readEntity(JsonWebKeys.class);

        assertEquals(1, jsonWebKeys.getKeys().size(), () -> "Add assertion message");

        JsonWebKey jsonWebKey = jsonWebKeys.getKeys().get(0);
        assertEquals(KeyType.EC, jsonWebKey.getKeyType(), () -> "Add assertion message");
        assertEquals("ECKey", jsonWebKey.getKeyId(), () -> "Add assertion message");
        assertNotNull(jsonWebKey.getProperty("x"), () -> "Add assertion message");
        assertNotNull(jsonWebKey.getProperty("y"), () -> "Add assertion message");
        // Check we don't send the private key back
        checkPrivateKeyParametersNotPresent(jsonWebKeys);
    }

    @org.junit.Test
    public void testGetJWKHMAC() throws Exception {
        URL busFile = OIDCFlowTest.class.getResource("client.xml");

        String address = "https://localhost:" + JCACHE_SERVER.getPort() + "/services4/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                            "alice", "security", busFile.toString());
        client.accept("application/json");

        client.path("keys/");
        Response response = client.get();
        JsonWebKeys jsonWebKeys = response.readEntity(JsonWebKeys.class);

        // We don't allow sending secret keys back from the key service by default
        assertNull(jsonWebKeys.getKeys(), () -> "Add assertion message");
    }

    @org.junit.Test
    public void testGetJWKHMACExplicitlyAllowed() throws Exception {
        URL busFile = OIDCFlowTest.class.getResource("client.xml");

        String address = "https://localhost:" + JCACHE_SERVER.getPort() + "/services5/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                            "alice", "security", busFile.toString());
        client.accept("application/json");

        client.path("keys/");
        Response response = client.get();
        JsonWebKeys jsonWebKeys = response.readEntity(JsonWebKeys.class);

        // Here we explicitly allow sending back secret keys
        assertEquals(1, jsonWebKeys.getKeys().size(), () -> "Add assertion message");
    }

    @org.junit.Test
    public void testGetJWKMultipleKeys() throws Exception {
        URL busFile = OIDCFlowTest.class.getResource("client.xml");

        String address = "https://localhost:" + JCACHE_SERVER.getPort() + "/services6/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                            "alice", "security", busFile.toString());
        client.accept("application/json");

        client.path("keys/");
        Response response = client.get();
        JsonWebKeys jsonWebKeys = response.readEntity(JsonWebKeys.class);

        assertEquals(2, jsonWebKeys.getKeys().size(), () -> "Add assertion message");

        // Check we don't send the private key back
        checkPrivateKeyParametersNotPresent(jsonWebKeys);
    }

    private void checkPrivateKeyParametersNotPresent(JsonWebKeys jsonWebKeys) {
        for (JsonWebKey jsonWebKey : jsonWebKeys.getKeys()) {
            assertNull(jsonWebKey.getProperty("d"));
            assertNull(jsonWebKey.getProperty("p"));
            assertNull(jsonWebKey.getProperty("q"));
            assertNull(jsonWebKey.getProperty("dp"));
            assertNull(jsonWebKey.getProperty("dq"));
            assertNull(jsonWebKey.getProperty("qi"));
        }
    }


}