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

package org.apache.cxf.xkms.cache;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.apache.cxf.common.classloader.ClassLoaderUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A test for the XKMSClientCache
 */
public class XKMSClientCacheTest {

    private final XKMSClientCache cache;
    private final X509Certificate alice;
    private final X509Certificate bob;

    @Before
    public void setUp() {
        cache = new EHCacheXKMSClientCache();

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(ClassLoaderUtils.getResourceAsStream("keys/alice.jks",
                                                           XKMSClientCacheTest.class),
                                                           "password".toCharArray());
        alice = (X509Certificate)keystore.getCertificate("alice");

        keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(ClassLoaderUtils.getResourceAsStream("keys/bob.jks",
                                                           XKMSClientCacheTest.class),
                                                           "password".toCharArray());
        bob = (X509Certificate)keystore.getCertificate("bob");
    }

    @org.junit.Test
    public void testCache() {
        assertNotNull(alice, () -> "Add assertion message");
        assertNotNull(bob, () -> "Add assertion message");

        XKMSCacheToken aliceToken = new XKMSCacheToken();
        aliceToken.setX509Certificate(alice);

        // Put
        storeCertificateInCache(alice, false);
        storeCertificateInCache(bob, false);

        // Get
        XKMSCacheToken cachedToken = cache.get(alice.getSubjectX500Principal().getName());
        assertEquals(alice, cachedToken.getX509Certificate(), () -> "Add assertion message");
        assertFalse(cachedToken.isXkmsValidated(), () -> "Add assertion message");

        cache.get(getKeyForIssuerSerial(alice.getIssuerX500Principal().getName(),
                                        alice.getSerialNumber()));
        // Line removed due to Duplicate Assert: assertEquals(alice, cachedToken.getX509Certificate(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(cachedToken.isXkmsValidated(), () -> "Add assertion message");

        cachedToken = cache.get(bob.getSubjectX500Principal().getName());
        assertEquals(bob, cachedToken.getX509Certificate(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(cachedToken.isXkmsValidated(), () -> "Add assertion message");

        cache.get(getKeyForIssuerSerial(bob.getIssuerX500Principal().getName(),
                                        bob.getSerialNumber()));
        // Line removed due to Duplicate Assert: assertEquals(bob, cachedToken.getX509Certificate(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertFalse(cachedToken.isXkmsValidated(), () -> "Add assertion message");

        // Validate
        cachedToken = cache.get(alice.getSubjectX500Principal().getName());
        cachedToken.setXkmsValidated(true);

        cachedToken = cache.get(alice.getSubjectX500Principal().getName());
        assertTrue(cachedToken.isXkmsValidated(), () -> "Add assertion message");
        cache.get(getKeyForIssuerSerial(alice.getIssuerX500Principal().getName(),
                                        alice.getSerialNumber()));
        // Line removed due to Duplicate Assert: assertTrue(cachedToken.isXkmsValidated(), () -> "Add assertion message");
    }

    private void storeCertificateInCache(X509Certificate certificate, boolean validated) {
        XKMSCacheToken cacheToken = new XKMSCacheToken(certificate);
        cacheToken.setXkmsValidated(validated);

        // Store it using IssuerSerial
        String issuerSerialKey =
            getKeyForIssuerSerial(certificate.getIssuerX500Principal().getName(),
                                  certificate.getSerialNumber());
        cache.put(issuerSerialKey, cacheToken);

            // Store it using the Subject DN as well
        String subjectDNKey = certificate.getSubjectX500Principal().getName();
        cache.put(subjectDNKey, cacheToken);
    }

    private String getKeyForIssuerSerial(String issuer, BigInteger serial) {
        return issuer + "-" + serial.toString(16);
    }
}