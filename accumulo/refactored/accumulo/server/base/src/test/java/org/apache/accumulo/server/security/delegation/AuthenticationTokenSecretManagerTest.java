/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.server.security.delegation;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.admin.DelegationTokenConfig;
import org.apache.accumulo.core.client.impl.AuthenticationTokenIdentifier;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.security.token.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

public class AuthenticationTokenSecretManagerTest {
  private static final Logger log =
      LoggerFactory.getLogger(AuthenticationTokenSecretManagerTest.class);

  // From org.apache.hadoop.security.token.SecretManager
  private static final String DEFAULT_HMAC_ALGORITHM = "HmacSHA1";
  private static final int KEY_LENGTH = 64;
  private static KeyGenerator keyGen;

  @BeforeClass
  public static void setupKeyGenerator() throws Exception {
    // From org.apache.hadoop.security.token.SecretManager
    keyGen = KeyGenerator.getInstance(DEFAULT_HMAC_ALGORITHM);
    keyGen.init(KEY_LENGTH);
  }

  private Instance instance;
  private String instanceId;
  private DelegationTokenConfig cfg;

  @Before
  public void setupMocks() {
    instance = createMock(Instance.class);
    // Removed line due to General Fixture smell: instanceId = UUID.randomUUID().toString();
    // Removed line due to General Fixture smell: cfg = new DelegationTokenConfig();
    expect(instance.getInstanceID()).andReturn(instanceId).anyTimes();
    replay(instance);
  }

  @After
  public void verifyMocks() {
    verify(instance);
  }

  @Test
  public void testAddKey() {
    // 1 minute
    long tokenLifetime = 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a single key
    AuthenticationKey authKey = new AuthenticationKey(1, 0, tokenLifetime, keyGen.generateKey());
    secretManager.addKey(authKey);

    // Ensure it's in the cache
    Map<Integer,AuthenticationKey> keys = secretManager.getKeys();
    assertNotNull(keys, () -> "Add assertion message");
    Integer magicNumber40817580 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber40817580, keys.size(), () -> "Add assertion message");
    assertEquals(authKey, Iterables.getOnlyElement(keys.values()), () -> "Add assertion message");

    // Add the same key
    secretManager.addKey(authKey);

    // Ensure we still have only one key
    keys = secretManager.getKeys();
    // Line removed due to Duplicate Assert: assertNotNull(keys, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, keys.size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(authKey, Iterables.getOnlyElement(keys.values()), () -> "Add assertion message");
  }

  @Test
  public void testRemoveKey() {
    // 1 minute
    long tokenLifetime = 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a single key
    AuthenticationKey authKey = new AuthenticationKey(1, 0, tokenLifetime, keyGen.generateKey());
    secretManager.addKey(authKey);

    // Ensure it's in the cache
    Map<Integer,AuthenticationKey> keys = secretManager.getKeys();
    assertNotNull(keys, () -> "Add assertion message");
    assertEquals(1, keys.size(), () -> "Add assertion message");
    assertEquals(authKey, Iterables.getOnlyElement(keys.values()), () -> "Add assertion message");

    assertTrue(secretManager.removeKey(authKey.getKeyId()), () -> "Add assertion message");
    assertEquals(0, secretManager.getKeys().size(), () -> "Add assertion message");
  }

  @Test
  public void testGenerateToken() throws Exception {
    cfg = new DelegationTokenConfig();
    instanceId = UUID.randomUUID().toString();
    // start of the test
    long then = System.currentTimeMillis();

    // 1 minute
    long tokenLifetime = 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a current key
    secretManager
        .addKey(new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey()));

    String principal = "user@EXAMPLE.COM";
    Entry<Token<AuthenticationTokenIdentifier>,AuthenticationTokenIdentifier> pair =
        secretManager.generateToken(principal, cfg);

    assertNotNull(pair, () -> "Add assertion message");
    Token<AuthenticationTokenIdentifier> token = pair.getKey();
    assertNotNull(token, () -> "Add assertion message");
    assertEquals(AuthenticationTokenIdentifier.TOKEN_KIND, token.getKind(), () -> "Add assertion message");

    // Reconstitute the token identifier (will happen when clients are involved)
    AuthenticationTokenIdentifier id = new AuthenticationTokenIdentifier();
    id.readFields(new DataInputStream(new ByteArrayInputStream(token.getIdentifier())));
    long now = System.currentTimeMillis();

    // Issue date should be after the test started, but before we deserialized the token
    assertTrue("Issue date did not fall within the expected upper bound. Expected less than " + now
        + ", but was " + id.getIssueDate(), id.getIssueDate() <= now);
    assertTrue("Issue date did not fall within the expected lower bound. Expected greater than "
        + then + ", but was " + id.getIssueDate(), id.getIssueDate() >= then);

    // Expiration is the token lifetime plus the issue date
    assertEquals(id.getIssueDate() + tokenLifetime, id.getExpirationDate(), () -> "Add assertion message");

    // Verify instance ID
    assertEquals(instanceId, id.getInstanceId(), () -> "Add assertion message");

    // The returned id should be the same as the reconstructed id
    assertEquals(pair.getValue(), id, () -> "Add assertion message");
  }

  @Test
  public void testVerifyPassword() throws Exception {
    cfg = new DelegationTokenConfig();
    // start of the test
    long then = System.currentTimeMillis();

    // 1 minute
    long tokenLifetime = 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a current key
    secretManager
        .addKey(new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey()));

    String principal = "user@EXAMPLE.COM";
    Entry<Token<AuthenticationTokenIdentifier>,AuthenticationTokenIdentifier> pair =
        secretManager.generateToken(principal, cfg);
    Token<AuthenticationTokenIdentifier> token = pair.getKey();

    AuthenticationTokenIdentifier id = new AuthenticationTokenIdentifier();
    id.readFields(new DataInputStream(new ByteArrayInputStream(token.getIdentifier())));

    byte[] password = secretManager.retrievePassword(id);

    // The passwords line up against multiple calls with the same ID
    assertArrayEquals(password, secretManager.retrievePassword(id), () -> "Add assertion message");

    // Make a second token for the same user
    Entry<Token<AuthenticationTokenIdentifier>,AuthenticationTokenIdentifier> pair2 =
        secretManager.generateToken(principal, cfg);
    Token<AuthenticationTokenIdentifier> token2 = pair2.getKey();
    // Reconstitute the token identifier (will happen when clients are involved)
    AuthenticationTokenIdentifier id2 = new AuthenticationTokenIdentifier();
    id2.readFields(new DataInputStream(new ByteArrayInputStream(token2.getIdentifier())));

    // Get the password
    byte[] password2 = secretManager.retrievePassword(id2);

    // It should be different than the password for the first user.
    assertFalse("Different tokens for the same user shouldn't have the same password",
        Arrays.equals(password, password2));
  }

  @Test(expected = InvalidToken.class)
  public void testExpiredPasswordsThrowError() throws Exception {
    cfg = new DelegationTokenConfig();
    // start of the test
    long then = System.currentTimeMillis();

    // 500ms lifetime
    long tokenLifetime = 500;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a current key
    secretManager
        .addKey(new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey()));

    String principal = "user@EXAMPLE.COM";
    Entry<Token<AuthenticationTokenIdentifier>,AuthenticationTokenIdentifier> pair =
        secretManager.generateToken(principal, cfg);
    Token<AuthenticationTokenIdentifier> token = pair.getKey();

    // Add a small buffer to make sure we move past the expiration of 0 for the token.
    Thread.sleep(1000);

    // Reconstitute the token identifier (will happen when clients are involved)
    AuthenticationTokenIdentifier id = new AuthenticationTokenIdentifier();
    id.readFields(new DataInputStream(new ByteArrayInputStream(token.getIdentifier())));

    secretManager.retrievePassword(id);
  }

  @Test(expected = InvalidToken.class)
  public void testTokenIssuedInFuture() throws Exception {
    cfg = new DelegationTokenConfig();
    // start of the test
    long then = System.currentTimeMillis();

    long tokenLifetime = 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a current key
    secretManager
        .addKey(new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey()));

    String principal = "user@EXAMPLE.COM";
    Entry<Token<AuthenticationTokenIdentifier>,AuthenticationTokenIdentifier> pair =
        secretManager.generateToken(principal, cfg);
    Token<AuthenticationTokenIdentifier> token = pair.getKey();

    // Reconstitute the token identifier (will happen when clients are involved)
    AuthenticationTokenIdentifier id = new AuthenticationTokenIdentifier();
    id.readFields(new DataInputStream(new ByteArrayInputStream(token.getIdentifier())));

    // Increase the value of issueDate
    id.setIssueDate(Long.MAX_VALUE);

    secretManager.retrievePassword(id);
  }

  @Test(expected = InvalidToken.class)
  public void testRolledMasterKey() throws Exception {
    cfg = new DelegationTokenConfig();
    // start of the test
    long then = System.currentTimeMillis();

    long tokenLifetime = 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a current key
    AuthenticationKey authKey1 =
        new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey());
    secretManager.addKey(authKey1);

    String principal = "user@EXAMPLE.COM";
    Entry<Token<AuthenticationTokenIdentifier>,AuthenticationTokenIdentifier> pair =
        secretManager.generateToken(principal, cfg);
    Token<AuthenticationTokenIdentifier> token = pair.getKey();

    AuthenticationTokenIdentifier id = new AuthenticationTokenIdentifier();
    id.readFields(new DataInputStream(new ByteArrayInputStream(token.getIdentifier())));

    long now = System.currentTimeMillis();
    secretManager.addKey(new AuthenticationKey(2, now, now + tokenLifetime, keyGen.generateKey()));

    // Should succeed -- the SecretManager still has authKey1
    secretManager.retrievePassword(id);

    // Remove authKey1
    secretManager.removeKey(authKey1.getKeyId());

    // Should fail -- authKey1 (presumably) expired, cannot authenticate
    secretManager.retrievePassword(id);
  }

  @Test(timeout = 20 * 1000)
  public void testMasterKeyExpiration() throws Exception {
    ZooAuthenticationKeyDistributor keyDistributor =
        createMock(ZooAuthenticationKeyDistributor.class);
    // start of the test
    long then = System.currentTimeMillis();

    // 10s lifetime
    long tokenLifetime = 10 * 1000l;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Make 2 keys, and add only one. The second has double the expiration of the first
    AuthenticationKey authKey1 =
        new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey()),
        authKey2 = new AuthenticationKey(2, then + tokenLifetime, then + tokenLifetime * 2,
            keyGen.generateKey());
    secretManager.addKey(authKey1);

    keyDistributor.remove(authKey1);
    expectLastCall().once();

    replay(keyDistributor);

    // Make sure expiration doesn't trigger anything yet
    Integer magicNumber84029473 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber84029473, secretManager.removeExpiredKeys(keyDistributor), () -> "Add assertion message");
    assertEquals(1, secretManager.getKeys().size(), () -> "Add assertion message");

    // Add the second key, still no expiration
    secretManager.addKey(authKey2);
    // Line removed due to Duplicate Assert: assertEquals(0, secretManager.removeExpiredKeys(keyDistributor), () -> "Add assertion message");
    assertEquals(2, secretManager.getKeys().size(), () -> "Add assertion message");
    assertEquals(authKey2, secretManager.getCurrentKey(), () -> "Add assertion message");

    // Wait for the expiration
    long now = System.currentTimeMillis();
    while (now - (then + tokenLifetime) < 0) {
      Thread.sleep(500);
      now = System.currentTimeMillis();
    }

    // Expire the first
    assertEquals(1, secretManager.removeExpiredKeys(keyDistributor), () -> "Add assertion message");

    // Ensure the second still exists
    // Line removed due to Duplicate Assert: assertEquals(1, secretManager.getKeys().size(), () -> "Add assertion message");
    assertEquals(authKey2, Iterables.getOnlyElement(secretManager.getKeys().values()), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(authKey2, secretManager.getCurrentKey(), () -> "Add assertion message");

    verify(keyDistributor);
  }

  @Test
  public void testRestrictExpirationDate() throws Exception {
    cfg = new DelegationTokenConfig();
    // start of the test
    long then = System.currentTimeMillis();

    // 1 hr
    long tokenLifetime = 60 * 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a current key
    secretManager
        .addKey(new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey()));

    // 1 minute
    cfg.setTokenLifetime(1, TimeUnit.MINUTES);

    String principal = "user@EXAMPLE.COM";
    Entry<Token<AuthenticationTokenIdentifier>,AuthenticationTokenIdentifier> pair =
        secretManager.generateToken(principal, cfg);

    assertNotNull(pair, () -> "Add assertion message");

    long now = System.currentTimeMillis();
    long actualExpiration = pair.getValue().getExpirationDate();
    long approximateLifetime = actualExpiration - now;

    log.info("actualExpiration={}, approximateLifetime={}", actualExpiration, approximateLifetime);

    // We don't know the exact lifetime, but we know that it can be no more than what was requested
    assertTrue(
        "Expected lifetime to be on thet order of the token lifetime, but was "
            + approximateLifetime,
        approximateLifetime <= cfg.getTokenLifetime(TimeUnit.MILLISECONDS));
  }

  @Test(expected = AccumuloException.class)
  public void testInvalidRequestedExpirationDate() throws Exception {
    cfg = new DelegationTokenConfig();
    // start of the test
    long then = System.currentTimeMillis();

    // 1 hr
    long tokenLifetime = 60 * 60 * 1000;
    AuthenticationTokenSecretManager secretManager =
        new AuthenticationTokenSecretManager(instance, tokenLifetime);

    // Add a current key
    secretManager
        .addKey(new AuthenticationKey(1, then, then + tokenLifetime, keyGen.generateKey()));

    // A longer timeout than the secret key has
    cfg.setTokenLifetime(tokenLifetime + 1, TimeUnit.MILLISECONDS);

    // Should throw an exception
    secretManager.generateToken("user@EXAMPLE.COM", cfg);
  }
}