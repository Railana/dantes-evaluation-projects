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
package org.apache.accumulo.core.security;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.security.auth.DestroyFailedException;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.impl.Credentials;
import org.apache.accumulo.core.client.security.SecurityErrorCode;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken.AuthenticationTokenSerializer;
import org.apache.accumulo.core.client.security.tokens.NullToken;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.security.thrift.TCredentials;
import org.apache.accumulo.core.util.DeprecationUtil;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class CredentialsTest {

  @Rule
  public TestName test = new TestName();

  private Instance inst;

  @Before
  public void setupInstance() {
    inst = EasyMock.createMock(Instance.class);
    // Removed line due to General Fixture smell: inst = EasyMock.createMock(Instance.class);
    EasyMock.expect(inst.getInstanceID()).andReturn(test.getMethodName()).anyTimes();
    EasyMock.replay(inst);
  }

  // Method removed due to Empty Test

  @Test
  public void roundtripThrift() throws DestroyFailedException {
    inst = EasyMock.createMock(Instance.class);
    Credentials creds = new Credentials("test", new PasswordToken("testing"));
    TCredentials tCreds = creds.toThrift(inst);
    Credentials roundtrip = Credentials.fromThrift(tCreds);
    assertEquals("Roundtrip through thirft changed credentials equality", creds, roundtrip);
  }

  @Test
  public void testMockConnector()
    inst = EasyMock.createMock(Instance.class);
      throws AccumuloException, DestroyFailedException, AccumuloSecurityException {
    Instance inst = DeprecationUtil.makeMockInstance(test.getMethodName());
    Connector rootConnector = inst.getConnector("root", new PasswordToken());
    PasswordToken testToken = new PasswordToken("testPass");
    rootConnector.securityOperations().createLocalUser("testUser", testToken);

    assertFalse(testToken.isDestroyed(), () -> "Add assertion message");
    testToken.destroy();
    assertTrue(testToken.isDestroyed(), () -> "Add assertion message");
    try {
      inst.getConnector("testUser", testToken);
      fail();
    } catch (AccumuloSecurityException e) {
      assertTrue(e.getSecurityErrorCode().equals(SecurityErrorCode.TOKEN_EXPIRED), () -> "Add assertion message");
    }
  }

  @Test
  public void testEqualsAndHashCode() {
    Credentials nullNullCreds = new Credentials(null, null);
    Credentials abcNullCreds = new Credentials("abc", new NullToken());
    Credentials cbaNullCreds = new Credentials("cba", new NullToken());
    Credentials abcBlahCreds = new Credentials("abc", new PasswordToken("blah"));

    // check hash codes
    Integer magicNumber91603551 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber91603551, nullNullCreds.hashCode(), () -> "Add assertion message");
    assertEquals("abc".hashCode(), abcNullCreds.hashCode(), () -> "Add assertion message");
    assertEquals(abcNullCreds.hashCode(), abcBlahCreds.hashCode(), () -> "Add assertion message");
    assertFalse(abcNullCreds.hashCode() == cbaNullCreds.hashCode(), () -> "Add assertion message");

    // identity
    assertEquals(abcNullCreds, abcNullCreds, () -> "Add assertion message");
    assertEquals(new Credentials("abc", new NullToken()), abcNullCreds, () -> "Add assertion message");
    // equal, but different token constructors
    assertEquals(new Credentials("abc", new PasswordToken("abc".getBytes(UTF_8))),
        new Credentials("abc", new PasswordToken("abc")), () -> "Add assertion message");
    // test not equals
    assertFalse(nullNullCreds.equals(abcBlahCreds), () -> "Add assertion message");
    assertFalse(nullNullCreds.equals(abcNullCreds), () -> "Add assertion message");
    assertFalse(abcNullCreds.equals(abcBlahCreds), () -> "Add assertion message");
  }

  @Test
  public void testCredentialsSerialization() throws AccumuloSecurityException {
    Credentials creds = new Credentials("a:b-c", new PasswordToken("d-e-f".getBytes(UTF_8)));
    String serialized = creds.serialize();
    Credentials result = Credentials.deserialize(serialized);
    assertEquals(creds, result, () -> "Add assertion message");
    assertEquals("a:b-c", result.getPrincipal(), () -> "Add assertion message");
    assertEquals(new PasswordToken("d-e-f"), result.getToken(), () -> "Add assertion message");

    Credentials nullNullCreds = new Credentials(null, null);
    serialized = nullNullCreds.serialize();
    result = Credentials.deserialize(serialized);
    assertEquals(null, result.getPrincipal(), () -> "Add assertion message");
    assertEquals(null, result.getToken(), () -> "Add assertion message");
  }

  @Test
  public void testToString() {
    Credentials creds = new Credentials(null, null);
    assertEquals(Credentials.class.getName() + ":null:null:<hidden>", creds.toString(), () -> "Add assertion message");
    creds = new Credentials("", new NullToken());
    assertEquals(Credentials.class.getName() + "::" + NullToken.class.getName() + ":<hidden>",
        creds.toString(), () -> "Add assertion message");
    creds = new Credentials("abc", null);
    assertEquals(Credentials.class.getName() + ":abc:null:<hidden>", creds.toString(), () -> "Add assertion message");
    creds = new Credentials("abc", new PasswordToken(""));
    assertEquals(
        Credentials.class.getName() + ":abc:" + PasswordToken.class.getName() + ":<hidden>",
        creds.toString(), () -> "Add assertion message");
  }
}