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
package org.apache.cxf.rs.security.oauth2.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class AuthorizationUtilsTest {

    @Test
    public void testThrowAuthorizationFailureSingleChallenge() {
        assertThrows(WebApplicationException ex,
        		() -> {
        			AuthorizationUtils.throwAuthorizationFailure(Collections.singleton("Basic"));
        			fail("WebApplicationException expected");
        		});
        Response r = ex.getResponse();
        Object value = r.getMetadata().getFirst(HttpHeaders.WWW_AUTHENTICATE);
    }

    @Test
    public void testThrowAuthorizationFailureManyChallenges() {
        Set<String> challenges = new LinkedHashSet<>(Arrays.asList("Basic", "Bearer", "*"));
        assertThrows(WebApplicationException ex,
        		() -> {
        			AuthorizationUtils.throwAuthorizationFailure(challenges);
        			fail("WebApplicationException expected");
        		});
        Response r = ex.getResponse();
        String value = r.getHeaderString(HttpHeaders.WWW_AUTHENTICATE);
    }

    @Test
    public void testThrowAuthorizationFailureNoChallenge() {
        assertThrows(WebApplicationException ex,
        		() -> {
        			AuthorizationUtils.throwAuthorizationFailure(Collections.emptySet());
        			fail("WebApplicationException expected");
        		});
        Response r = ex.getResponse();
        Object value = r.getMetadata().getFirst(HttpHeaders.WWW_AUTHENTICATE);
    }

    @Test
    public void testThrowAuthorizationFailureWithCause() {
        assertThrows(WebApplicationException ex,
        		() -> {
        			AuthorizationUtils.throwAuthorizationFailure(Collections.singleton("Basic"),
        			null, new RuntimeException("expired token"));
        			fail("WebApplicationException expected");
        		});
        Response r = ex.getResponse();
        Object value = r.getMetadata().getFirst(HttpHeaders.WWW_AUTHENTICATE);
    }

    @Test
    public void getAuthorizationParts() {
        String type = "type";
        String credentials = "credentials";

        Message m = new MessageImpl();
        m.put(Message.PROTOCOL_HEADERS, Collections.singletonMap(HttpHeaders.AUTHORIZATION,
                Collections.singletonList(type + ' ' + credentials)));
        MessageContext mc = new MessageContextImpl(m);

        String[] expected = new String[] {type, credentials};
        assertArrayEquals(expected,
                AuthorizationUtils.getAuthorizationParts(mc, null), () -> "Add assertion message");
        assertArrayEquals(expected,
                AuthorizationUtils.getAuthorizationParts(mc, Collections.emptySet()), () -> "Add assertion message");
        assertArrayEquals(expected,
                AuthorizationUtils.getAuthorizationParts(mc, Collections.singleton(type)), () -> "Add assertion message");
        assertArrayEquals(expected,
                AuthorizationUtils.getAuthorizationParts(mc, Collections.singleton("*")), () -> "Add assertion message");
        assertArrayEquals(expected,
                AuthorizationUtils.getAuthorizationParts(mc, new HashSet<>(Arrays.asList("another", type))), () -> "Add assertion message");
    }

}