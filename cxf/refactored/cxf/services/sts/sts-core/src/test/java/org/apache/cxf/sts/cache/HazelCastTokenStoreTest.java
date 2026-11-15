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
package org.apache.cxf.sts.cache;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.tokenstore.TokenStore;

import org.junit.BeforeClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HazelCastTokenStoreTest {

    private static TokenStore store;

    @BeforeClass
    public static void init() throws Exception {
        store = new HazelCastTokenStore("default");
    }

    // tests TokenStore apis for storing in the cache.
    @org.junit.Test
    public void testTokenAdd() throws Exception {
        String key = "key";
        SecurityToken token = new SecurityToken(key);
        store.add(token);
        SecurityToken cachedToken = store.getToken(key);
        assertEquals(token.getId(), cachedToken.getId(), () -> "Add assertion message");
        store.remove(token.getId());
        assertNull(store.getToken(key), () -> "Add assertion message");

        String newKey = "xyz";
        store.add(newKey, token);
        // Line removed due to Duplicate Assert: assertNull(store.getToken(key), () -> "Add assertion message");
        cachedToken = store.getToken(newKey);
        assertEquals(key, cachedToken.getId(), () -> "Add assertion message");
        store.remove(newKey);
        assertNull(store.getToken(newKey), () -> "Add assertion message");
    }

    // tests TokenStore apis for storing in the cache with various expiration times
    @org.junit.Test
    public void testTokenAddExpiration() throws Exception {
        SecurityToken expiredToken = new SecurityToken("expiredToken");
        ZonedDateTime expiry = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(5L);
        expiredToken.setExpires(expiry.toInstant());
        store.add(expiredToken);
        assertTrue(store.getTokenIdentifiers().isEmpty(), () -> "Add assertion message");

        SecurityToken farFutureToken = new SecurityToken("farFuture");
        expiry = ZonedDateTime.now(ZoneOffset.UTC).plusYears(50L);
        farFutureToken.setExpires(expiry.toInstant());
        store.add(farFutureToken);

        assertTrue(store.getTokenIdentifiers().size() == 1, () -> "Add assertion message");
        store.remove(farFutureToken.getId());
        // Line removed due to Duplicate Assert: assertTrue(store.getTokenIdentifiers().isEmpty(), () -> "Add assertion message");
    }

    // tests TokenStore apis for removing from the cache.
    @org.junit.Test
    public void testTokenRemove() {
        SecurityToken token1 = new SecurityToken("token1");
        SecurityToken token2 = new SecurityToken("token2");
        SecurityToken token3 = new SecurityToken("token3");
        store.add(token1);
        store.add(token2);
        store.add(token3);
        assertTrue(store.getTokenIdentifiers().size() == 3, () -> "Add assertion message");
        store.remove(token3.getId());
        assertNull(store.getToken("test3"), () -> "Add assertion message");
        store.remove(token1.getId());
        store.remove(token2.getId());
        assertTrue(store.getTokenIdentifiers().isEmpty(), () -> "Add assertion message");
    }
}