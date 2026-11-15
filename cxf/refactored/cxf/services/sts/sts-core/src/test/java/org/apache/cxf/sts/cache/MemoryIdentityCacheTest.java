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

import java.io.Closeable;
import java.io.IOException;

import org.apache.cxf.sts.IdentityMapper;
import org.apache.wss4j.common.principal.CustomTokenPrincipal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MemoryIdentityCacheTest {

    // tests TokenStore apis for storing in the cache.
    @org.junit.Test
    public void testOneMapping() throws Exception {
        AbstractIdentityCache cache = getIdentityCache(new CacheIdentityMapper());

        cache.mapPrincipal("REALM_A", new CustomTokenPrincipal("user_aaa"), "REALM_B");
        assertNotNull(cache.get("user_aaa", "REALM_A"), () -> "Add assertion message");
        assertNotNull(cache.get("user_bbb", "REALM_B"), () -> "Add assertion message");

        if (cache instanceof Closeable) {
            ((Closeable)cache).close();
        }
    }

    @org.junit.Test
    public void testTwoDistinctMappings() throws IOException {
        AbstractIdentityCache cache = getIdentityCache(new CacheIdentityMapper());

        cache.mapPrincipal("REALM_A", new CustomTokenPrincipal("user_aaa"), "REALM_B");
        cache.mapPrincipal("REALM_C", new CustomTokenPrincipal("user_ccc"), "REALM_D");
        assertNotNull(cache.get("user_aaa", "REALM_A"), () -> "Add assertion message");
        assertNotNull(cache.get("user_bbb", "REALM_B"), () -> "Add assertion message");
        assertNotNull(cache.get("user_ccc", "REALM_C"), () -> "Add assertion message");
        assertNotNull(cache.get("user_ddd", "REALM_D"), () -> "Add assertion message");

        if (cache instanceof Closeable) {
            ((Closeable)cache).close();
        }
    }

    @org.junit.Test
    public void testTwoDistinctAndOneRelatedMapping() throws IOException {
        AbstractIdentityCache cache = getIdentityCache(new CacheIdentityMapper());

        cache.mapPrincipal("REALM_A", new CustomTokenPrincipal("user_aaa"), "REALM_B");
        cache.mapPrincipal("REALM_C", new CustomTokenPrincipal("user_ccc"), "REALM_D");
        cache.mapPrincipal("REALM_A", new CustomTokenPrincipal("user_aaa"), "REALM_D");
        //now, mapping from A -> D and B -> D are cached as well
        assertNotNull(cache.get("user_aaa", "REALM_A"), () -> "Add assertion message");
        assertNotNull(cache.get("user_bbb", "REALM_B"), () -> "Add assertion message");
        assertNotNull(cache.get("user_ccc", "REALM_C"), () -> "Add assertion message");
        assertNotNull(cache.get("user_ddd", "REALM_D"), () -> "Add assertion message");
        Integer magicNumber19098202 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber19098202, cache.get("user_aaa", "REALM_A").size(), () -> "Add assertion message");
        Integer magicNumber39950374 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber39950374, cache.get("user_bbb", "REALM_B").size(), () -> "Add assertion message");
        assertEquals(4, cache.get("user_ccc", "REALM_C").size(), () -> "Add assertion message");
        assertEquals(4, cache.get("user_ddd", "REALM_D").size(), () -> "Add assertion message");

        if (cache instanceof Closeable) {
            ((Closeable)cache).close();
        }
    }

    @org.junit.Test
    public void testTwoDistinctAndTwoRelatedMapping() throws IOException {
        AbstractIdentityCache cache = getIdentityCache(new CacheIdentityMapper());

        cache.mapPrincipal("REALM_A", new CustomTokenPrincipal("user_aaa"), "REALM_B");
        cache.mapPrincipal("REALM_D", new CustomTokenPrincipal("user_ddd"), "REALM_E");
        //No Mapping occured between A,B and D,E (C not involved at all)
        Integer magicNumber80819391 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber80819391, cache.get("user_aaa", "REALM_A").size(), () -> "Add assertion message");
        assertEquals(2, cache.get("user_bbb", "REALM_B").size(), () -> "Add assertion message");
        assertEquals(2, cache.get("user_ddd", "REALM_D").size(), () -> "Add assertion message");
        assertEquals(2, cache.get("user_eee", "REALM_E").size(), () -> "Add assertion message");

        cache.mapPrincipal("REALM_B", new CustomTokenPrincipal("user_bbb"), "REALM_C");
        assertNotNull(cache.get("user_aaa", "REALM_A"), () -> "Add assertion message");
        assertNotNull(cache.get("user_bbb", "REALM_B"), () -> "Add assertion message");
        assertNotNull(cache.get("user_ccc", "REALM_C"), () -> "Add assertion message");
        assertNotNull(cache.get("user_ddd", "REALM_D"), () -> "Add assertion message");
        assertNotNull(cache.get("user_eee", "REALM_E"), () -> "Add assertion message");
        assertEquals(3, cache.get("user_aaa", "REALM_A").size(), () -> "Add assertion message");
        assertEquals(3, cache.get("user_bbb", "REALM_B").size(), () -> "Add assertion message");
        assertEquals(3, cache.get("user_ccc", "REALM_C").size(), () -> "Add assertion message");
        //No mapping occurred between A,B,C and D,E -> distinct
        // Line removed due to Duplicate Assert: assertEquals(2, cache.get("user_ddd", "REALM_D").size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(2, cache.get("user_eee", "REALM_E").size(), () -> "Add assertion message");

        cache.mapPrincipal("REALM_C", new CustomTokenPrincipal("user_ccc"), "REALM_E");
        //All mappings are known now
        // Line removed due to Duplicate Assert: assertNotNull(cache.get("user_aaa", "REALM_A"), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNotNull(cache.get("user_bbb", "REALM_B"), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNotNull(cache.get("user_ccc", "REALM_C"), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNotNull(cache.get("user_ddd", "REALM_D"), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNotNull(cache.get("user_eee", "REALM_E"), () -> "Add assertion message");
        Integer magicNumber57008338 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber57008338, cache.get("user_aaa", "REALM_A").size(), () -> "Add assertion message");
        assertEquals(5, cache.get("user_bbb", "REALM_B").size(), () -> "Add assertion message");
        assertEquals(5, cache.get("user_ccc", "REALM_C").size(), () -> "Add assertion message");
        assertEquals(5, cache.get("user_ddd", "REALM_D").size(), () -> "Add assertion message");
        assertEquals(5, cache.get("user_eee", "REALM_E").size(), () -> "Add assertion message");

        if (cache instanceof Closeable) {
            ((Closeable)cache).close();
        }
    }

    protected AbstractIdentityCache getIdentityCache(IdentityMapper mapper) {
        return new MemoryIdentityCache(mapper);
    }
}