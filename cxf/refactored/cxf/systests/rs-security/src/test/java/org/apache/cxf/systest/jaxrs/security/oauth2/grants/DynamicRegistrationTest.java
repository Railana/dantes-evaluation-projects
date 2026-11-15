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
package org.apache.cxf.systest.jaxrs.security.oauth2.grants;

import java.util.Collections;

import javax.ws.rs.core.Response;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.services.ClientRegistration;
import org.apache.cxf.rs.security.oauth2.services.ClientRegistrationResponse;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.systest.jaxrs.security.oauth2.common.OAuth2TestUtils;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.apache.cxf.testutil.common.TestUtil;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Some unit tests for the dynamic registration service in CXF. The tests are run multiple times with different
 * OAuthDataProvider implementations:
 * a) JCACHE_PORT - JCache
 * b) JWT_JCACHE_PORT - JCache with useJwtFormatForAccessTokens enabled
 * c) JPA_PORT - JPA provider
 */
@RunWith(value = org.junit.runners.Parameterized.class)
public class DynamicRegistrationTest extends AbstractBusClientServerTestBase {

    public static final String JCACHE_PORT = TestUtil.getPortNumber("jaxrs-oauth2-dynamic-reg-jcache");
    public static final String JCACHE_PORT2 = TestUtil.getPortNumber("jaxrs-oauth2-dynamic-reg2-jcache");
    public static final String JWT_JCACHE_PORT = TestUtil.getPortNumber("jaxrs-oauth2-dynamic-reg-jcache-jwt");
    public static final String JWT_JCACHE_PORT2 = TestUtil.getPortNumber("jaxrs-oauth2-dynamic-reg2-jcache-jwt");
    public static final String JPA_PORT = TestUtil.getPortNumber("jaxrs-oauth2-dynamic-reg-jpa");
    public static final String JPA_PORT2 = TestUtil.getPortNumber("jaxrs-oauth2-dynamic-reg2-jpa");

    final String port;

    public DynamicRegistrationTest(String port) {
        this.port = port;
    }

    @BeforeClass
    public static void startServers() throws Exception {
        createStaticBus().setExtension(OAuth2TestUtils.clientHTTPConduitConfigurer(), HTTPConduitConfigurer.class);

        assertTrue("server did not launch correctly",
                   launchServer(BookServerOAuth2DynamicRegistrationJCache.class));
        assertTrue("server did not launch correctly",
                   launchServer(BookServerOAuth2DynamicRegistrationJCacheJWT.class));
        assertTrue("server did not launch correctly",
                   launchServer(BookServerOAuth2DynamicRegistrationJPA.class));
    }

    @Parameters(name = "{0}")
    public static String[] data() {
        return new String[] {JCACHE_PORT, JWT_JCACHE_PORT, JPA_PORT};
    }

    @org.junit.Test
    public void testDynamicRegistration() throws Exception {
        String address = "https://localhost:" + port + "/services/";
        // 1. Register a client
        WebClient registrationClient = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                            "alice", "security", null)
            .accept("application/json").type("application/json")
            .path("register/");

        ClientRegistration registration = new ClientRegistration();
        registration.setClientName("new client");
        registration.setRedirectUris(Collections.singletonList("http://www.blah.apache.org"));

        ClientRegistrationResponse registrationResponse =
            registrationClient.post(registration, ClientRegistrationResponse.class);

        assertNotNull(registrationResponse.getClientId(), () -> "Add assertion message");
        assertNotNull(registrationResponse.getClientSecret(), () -> "Add assertion message");
        assertNotNull(registrationResponse.getRegistrationClientUri(), () -> "Add assertion message");
        assertNotNull(registrationResponse.getRegistrationAccessToken(), () -> "Add assertion message");
        assertEquals(Collections.singletonList(OAuthConstants.AUTHORIZATION_CODE_GRANT),
            registrationResponse.getGrantTypes(), () -> "Add assertion message");

        // 2. Get Authorization Code
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                  "alice", "security", null);
        // Save the Cookie for the second request...
        WebClient.getConfig(client).getRequestContext().put(
            org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);

        String code = OAuth2TestUtils.getAuthorizationCode(client, null, registrationResponse.getClientId());
        assertNotNull(code, () -> "Add assertion message");

        // 3. Now get the access token
        client = WebClient.create(address,
                                  registrationResponse.getClientId(), registrationResponse.getClientSecret(), null);

        ClientAccessToken accessToken =
            OAuth2TestUtils.getAccessTokenWithAuthorizationCode(client, code, registrationResponse.getClientId(), null);
        assertNotNull(accessToken.getTokenKey(), () -> "Add assertion message");
    }

    @org.junit.Test
    public void testRedirectURIIsRequired() throws Exception {
        String address = "https://localhost:" + port + "/services/";
        // 1. Register a client
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                                                "alice", "security", null)
            .accept("application/json").type("application/json")
            .path("register/");

        ClientRegistration registration = new ClientRegistration();
        registration.setClientName("new client");
        registration.setScope("newscope");

        Response response = client.post(registration);
        Integer magicNumber57779614 = 400; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber57779614, response.getStatus(), () -> "Add assertion message");
    }

    //
    // Server implementations
    //
    public static class BookServerOAuth2DynamicRegistrationJCache extends AbstractBusTestServerBase {
        @Override
        protected void run() {
            setBus(new SpringBusFactory().createBus(getClass().getResource("dynamic-reg-server-jcache.xml")));
        }
    }

    public static class BookServerOAuth2DynamicRegistrationJCacheJWT extends AbstractBusTestServerBase {
        @Override
        protected void run() {
            setBus(new SpringBusFactory().createBus(getClass().getResource("dynamic-reg-server-jcache-jwt.xml")));
        }
    }

    public static class BookServerOAuth2DynamicRegistrationJPA extends AbstractBusTestServerBase {
        @Override
        protected void run() {
            setBus(new SpringBusFactory().createBus(getClass().getResource("dynamic-reg-server-jpa.xml")));
        }
    }

}