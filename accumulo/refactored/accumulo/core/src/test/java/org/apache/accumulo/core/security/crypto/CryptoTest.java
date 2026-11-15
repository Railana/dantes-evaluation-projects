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

package org.apache.accumulo.core.security.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.accumulo.core.conf.AccumuloConfiguration;
import org.apache.accumulo.core.conf.ConfigurationCopy;
import org.apache.accumulo.core.conf.Property;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import com.google.common.primitives.Bytes;

public class CryptoTest {

  private static final int MARKER_INT = 0xCADEFEDD;
  private static final String MARKER_STRING = "1 2 3 a b c";
  public static final String CONFIG_FILE_SYSTEM_PROP = "org.apache.accumulo.config.file";
  public static final String CRYPTO_ON_CONF = "crypto-on-accumulo-site.xml";
  public static final String CRYPTO_OFF_CONF = "crypto-off-accumulo-site.xml";
  public static final String CRYPTO_ON_KEK_OFF_CONF =
      "crypto-on-no-key-encryption-accumulo-site.xml";

  @Test
  public void testNoCryptoStream() throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_OFF_CONF);

    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);

    assertNotNull(params, () -> "Add assertion message");
    assertEquals("NullCipher", params.getAlgorithmName(), () -> "Add assertion message");
    assertNull(params.getEncryptionMode(), () -> "Add assertion message");
    assertNull(params.getPadding(), () -> "Add assertion message");

    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);
    assertNotNull(cryptoModule, () -> "Add assertion message");
    assertTrue(cryptoModule instanceof CryptoModuleFactory.NullCryptoModule, () -> "Add assertion message");

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    params.setPlaintextOutputStream(out);

    params = cryptoModule.getEncryptingOutputStream(params);
    assertNotNull(params.getEncryptedOutputStream(), () -> "Add assertion message");
    assertEquals(out, params.getEncryptedOutputStream(), () -> "Add assertion message");
  }

  @Test
  public void testCryptoModuleParamsParsing() {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_CONF);

    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);

    assertNotNull(params, () -> "Add assertion message");
    assertEquals("AES", params.getAlgorithmName(), () -> "Add assertion message");
    assertEquals("CFB", params.getEncryptionMode(), () -> "Add assertion message");
    assertEquals("NoPadding", params.getPadding(), () -> "Add assertion message");
    Integer magicNumber8772866 = 128; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber8772866, params.getKeyLength(), () -> "Add assertion message");
    assertEquals("SHA1PRNG", params.getRandomNumberGenerator(), () -> "Add assertion message");
    assertEquals("SUN", params.getRandomNumberGeneratorProvider(), () -> "Add assertion message");
    assertEquals("org.apache.accumulo.core.security.crypto.CachingHDFSSecretKeyEncryptionStrategy",
        params.getKeyEncryptionStrategyClass(), () -> "Add assertion message");
  }

  @Test
  public void testCryptoModuleDoesntLeakSensitive() throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_CONF);

    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    params.setPlaintextOutputStream(baos);

    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);

    cryptoModule.getEncryptingOutputStream(params);
    params.getEncryptedOutputStream().close();

    // If we get here, we have encrypted bytes
    byte[] streamBytes = baos.toByteArray();
    for (Property prop : Property.values()) {
      if (prop.isSensitive()) {
        byte[] toCheck = prop.getKey().getBytes();
        assertEquals(-1, Bytes.indexOf(streamBytes, toCheck), () -> "Add assertion message");
      }
    }

  }

  @Test
  public void testCryptoModuleParamsValidation1() throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_CONF);

    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);
    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);

    assertTrue(cryptoModule instanceof DefaultCryptoModule, () -> "Add assertion message");

    assertThrows(RuntimeException.class, () -> cryptoModule.getEncryptingOutputStream(params));
  }

  @Test
  public void testCryptoModuleParamsValidation2() throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_CONF);

    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);
    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);

    assertTrue(cryptoModule instanceof DefaultCryptoModule, () -> "Add assertion message");

    assertThrows(RuntimeException.class, () -> cryptoModule.getDecryptingInputStream(params));
  }

  private String getStringifiedBytes(String s) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(out);

    dataOut.writeUTF(s);
    dataOut.close();
    byte[] stringMarkerBytes = out.toByteArray();
    return Arrays.toString(stringMarkerBytes);

  }

  private String getStringifiedBytes(int i) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(out);

    dataOut.writeInt(i);
    dataOut.close();
    byte[] stringMarkerBytes = out.toByteArray();
    return Arrays.toString(stringMarkerBytes);

  }

  @Test
  public void testCryptoModuleBasicReadWrite() throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_KEK_OFF_CONF);

    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);
    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);

    assertTrue(cryptoModule instanceof DefaultCryptoModule, () -> "Add assertion message");

    byte[] resultingBytes = setUpSampleEncryptedBytes(cryptoModule, params);

    // If we get here, we have encrypted bytes
    ByteArrayInputStream in = new ByteArrayInputStream(resultingBytes);

    params = CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);
    params.setEncryptedInputStream(in);

    params = cryptoModule.getDecryptingInputStream(params);

    InputStream plaintextIn = params.getPlaintextInputStream();

    assertNotNull(plaintextIn, () -> "Add assertion message");
    assertTrue(plaintextIn != in, () -> "Add assertion message");
    DataInputStream dataIn = new DataInputStream(plaintextIn);
    String markerString = dataIn.readUTF();
    int markerInt = dataIn.readInt();

    assertEquals(MARKER_STRING, markerString, () -> "Add assertion message");
    assertEquals(MARKER_INT, markerInt, () -> "Add assertion message");
  }

  private byte[] setUpSampleEncryptedBytes(CryptoModule cryptoModule, CryptoModuleParameters params)
      throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    params.setPlaintextOutputStream(new NoFlushOutputStream(out));

    params = cryptoModule.getEncryptingOutputStream(params);

    assertNotNull(params.getEncryptedOutputStream());
    assertTrue(params.getEncryptedOutputStream() != out);

    DataOutputStream dataOut = new DataOutputStream(params.getEncryptedOutputStream());
    dataOut.writeUTF(MARKER_STRING);
    dataOut.writeInt(MARKER_INT);
    dataOut.close();

    byte[] resultingBytes = out.toByteArray();
    String stringifiedBytes = Arrays.toString(resultingBytes);

    String stringifiedMarkerBytes = getStringifiedBytes(MARKER_STRING);
    String stringifiedOtherBytes = getStringifiedBytes(MARKER_INT);

    // OK, let's make sure it's encrypted
    assertTrue(!stringifiedBytes.contains(stringifiedMarkerBytes));
    assertTrue(!stringifiedBytes.contains(stringifiedOtherBytes));
    return resultingBytes;
  }

  @Test
  public void testKeyEncryptionAndCheckThatFileCannotBeReadWithoutKEK() throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_CONF);

    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);
    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);

    assertTrue(cryptoModule instanceof DefaultCryptoModule, () -> "Add assertion message");
    assertNotNull(params.getKeyEncryptionStrategyClass(), () -> "Add assertion message");
    assertEquals("org.apache.accumulo.core.security.crypto.CachingHDFSSecretKeyEncryptionStrategy",
        params.getKeyEncryptionStrategyClass(), () -> "Add assertion message");

    byte[] resultingBytes = setUpSampleEncryptedBytes(cryptoModule, params);

    // So now that we have bytes encrypted by a key encrypted to a KEK, turn off the KEK
    // configuration and try
    // to decrypt. We expect this to fail. This also tests our ability to override the key
    // encryption strategy.
    conf = setAndGetAccumuloConfig(CRYPTO_ON_KEK_OFF_CONF);
    params = CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);
    params.setOverrideStreamsSecretKeyEncryptionStrategy(true);

    ByteArrayInputStream in = new ByteArrayInputStream(resultingBytes);
    params.setEncryptedInputStream(in);

    params = cryptoModule.getDecryptingInputStream(params);

    assertNotNull(params.getPlaintextInputStream(), () -> "Add assertion message");
    DataInputStream dataIn = new DataInputStream(params.getPlaintextInputStream());
    assertThrows(IOException.class, () -> dataIn.readUTF());
  }

  @Test
  public void testKeyEncryptionNormalPath() throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_CONF);

    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);
    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);

    assertTrue(cryptoModule instanceof DefaultCryptoModule, () -> "Add assertion message");
    assertNotNull(params.getKeyEncryptionStrategyClass(), () -> "Add assertion message");
    assertEquals("org.apache.accumulo.core.security.crypto.CachingHDFSSecretKeyEncryptionStrategy",
        params.getKeyEncryptionStrategyClass(), () -> "Add assertion message");

    byte[] resultingBytes = setUpSampleEncryptedBytes(cryptoModule, params);

    params = CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);
    params.setOverrideStreamsSecretKeyEncryptionStrategy(true);

    ByteArrayInputStream in = new ByteArrayInputStream(resultingBytes);
    params.setEncryptedInputStream(in);

    params = cryptoModule.getDecryptingInputStream(params);

    assertNotNull(params.getPlaintextInputStream(), () -> "Add assertion message");
    DataInputStream dataIn = new DataInputStream(params.getPlaintextInputStream());

    String markerString = dataIn.readUTF();
    int markerInt = dataIn.readInt();

    assertEquals(MARKER_STRING, markerString, () -> "Add assertion message");
    assertEquals(MARKER_INT, markerInt, () -> "Add assertion message");
  }

  @Test
  public void testChangingCryptoParamsAndCanStillDecryptPreviouslyEncryptedFiles()
      throws IOException {
    AccumuloConfiguration conf = setAndGetAccumuloConfig(CRYPTO_ON_CONF);

    CryptoModule cryptoModule = CryptoModuleFactory.getCryptoModule(conf);
    CryptoModuleParameters params =
        CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);

    assertTrue(cryptoModule instanceof DefaultCryptoModule, () -> "Add assertion message");
    assertNotNull(params.getKeyEncryptionStrategyClass(), () -> "Add assertion message");
    assertEquals("org.apache.accumulo.core.security.crypto.CachingHDFSSecretKeyEncryptionStrategy",
        params.getKeyEncryptionStrategyClass(), () -> "Add assertion message");

    byte[] resultingBytes = setUpSampleEncryptedBytes(cryptoModule, params);

    // Now we're going to create a params object and set its algorithm and key length different
    // from those configured within the site configuration. After doing this, we should
    // still be able to read the file that was created with a different set of parameters.
    params = CryptoModuleFactory.createParamsObjectFromAccumuloConfiguration(conf);
    params.setAlgorithmName("DESede");
    params.setKeyLength(24 * 8);

    ByteArrayInputStream in = new ByteArrayInputStream(resultingBytes);
    params.setEncryptedInputStream(in);

    params = cryptoModule.getDecryptingInputStream(params);

    assertNotNull(params.getPlaintextInputStream(), () -> "Add assertion message");
    DataInputStream dataIn = new DataInputStream(params.getPlaintextInputStream());
    String markerString = dataIn.readUTF();
    int markerInt = dataIn.readInt();

    assertEquals(MARKER_STRING, markerString, () -> "Add assertion message");
    assertEquals(MARKER_INT, markerInt, () -> "Add assertion message");
  }

  private AccumuloConfiguration setAndGetAccumuloConfig(String cryptoConfSetting) {
    ConfigurationCopy result =
        new ConfigurationCopy(AccumuloConfiguration.getDefaultConfiguration());
    Configuration conf = new Configuration(false);
    conf.addResource(cryptoConfSetting);
    for (Entry<String,String> e : conf) {
      result.set(e.getKey(), e.getValue());
    }
    return result;
  }

  @Test
  public void testKeyWrapAndUnwrap() throws NoSuchAlgorithmException, NoSuchPaddingException,
      NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    Cipher keyWrapCipher = Cipher.getInstance("AES/ECB/NoPadding");
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

    byte[] kek = new byte[16];
    random.nextBytes(kek);
    byte[] randomKey = new byte[16];
    random.nextBytes(randomKey);

    keyWrapCipher.init(Cipher.WRAP_MODE, new SecretKeySpec(kek, "AES"));

    Key randKey = new SecretKeySpec(randomKey, "AES");

    byte[] wrappedKey = keyWrapCipher.wrap(randKey);

    assert (wrappedKey != null);
    assert (wrappedKey.length == randomKey.length);

    Cipher keyUnwrapCipher = Cipher.getInstance("AES/ECB/NoPadding");
    keyUnwrapCipher.init(Cipher.UNWRAP_MODE, new SecretKeySpec(kek, "AES"));
    Key unwrappedKey = keyUnwrapCipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

    byte[] unwrappedKeyBytes = unwrappedKey.getEncoded();
    assert (Arrays.equals(unwrappedKeyBytes, randomKey));

  }
}