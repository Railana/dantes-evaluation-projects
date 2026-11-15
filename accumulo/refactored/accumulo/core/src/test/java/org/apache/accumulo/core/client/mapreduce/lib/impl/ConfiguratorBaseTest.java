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
package org.apache.accumulo.core.client.mapreduce.lib.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Base64;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.ClientConfiguration;
import org.apache.accumulo.core.client.ClientConfiguration.ClientProperty;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken.AuthenticationTokenSerializer;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 */
public class ConfiguratorBaseTest {

  private static enum PrivateTestingEnum {
    SOMETHING, SOMETHING_ELSE
  }

  @Test
  public void testEnumToConfKey() {
    assertEquals(this.getClass().getSimpleName() + ".PrivateTestingEnum.Something",
        ConfiguratorBase.enumToConfKey(this.getClass(), PrivateTestingEnum.SOMETHING), () -> "Add assertion message");
    assertEquals(this.getClass().getSimpleName() + ".PrivateTestingEnum.SomethingElse",
        ConfiguratorBase.enumToConfKey(this.getClass(), PrivateTestingEnum.SOMETHING_ELSE), () -> "Add assertion message");
  }

  @Test
  public void testSetConnectorInfoClassOfQConfigurationStringAuthenticationToken()
      throws AccumuloSecurityException {
    Configuration conf = new Configuration();
    assertFalse(ConfiguratorBase.isConnectorInfoSet(this.getClass(), conf), () -> "Add assertion message");
    ConfiguratorBase.setConnectorInfo(this.getClass(), conf, "testUser",
        new PasswordToken("testPassword"));
    assertTrue(ConfiguratorBase.isConnectorInfoSet(this.getClass(), conf), () -> "Add assertion message");
    assertEquals("testUser", ConfiguratorBase.getPrincipal(this.getClass(), conf), () -> "Add assertion message");
    AuthenticationToken token = ConfiguratorBase.getAuthenticationToken(this.getClass(), conf);
    assertEquals(PasswordToken.class, token.getClass(), () -> "Add assertion message");
    assertEquals(new PasswordToken("testPassword"), token, () -> "Add assertion message");
    assertEquals(
        "inline:" + PasswordToken.class.getName() + ":"
            + Base64.getEncoder().encodeToString(
                AuthenticationTokenSerializer.serialize(new PasswordToken("testPassword"))),
        conf.get(
            ConfiguratorBase.enumToConfKey(this.getClass(), ConfiguratorBase.ConnectorInfo.TOKEN)), () -> "Add assertion message");
  }

  @Test
  public void testSetConnectorInfoClassOfQConfigurationStringString()
      throws AccumuloSecurityException {
    Configuration conf = new Configuration();
    assertFalse(ConfiguratorBase.isConnectorInfoSet(this.getClass(), conf), () -> "Add assertion message");
    ConfiguratorBase.setConnectorInfo(this.getClass(), conf, "testUser", "testFile");
    assertTrue(ConfiguratorBase.isConnectorInfoSet(this.getClass(), conf), () -> "Add assertion message");
    assertEquals("testUser", ConfiguratorBase.getPrincipal(this.getClass(), conf), () -> "Add assertion message");
    assertEquals("file:testFile", conf.get(
        ConfiguratorBase.enumToConfKey(this.getClass(), ConfiguratorBase.ConnectorInfo.TOKEN)), () -> "Add assertion message");
  }

  @Test
  public void testSetZooKeeperInstance() {
    Configuration conf = new Configuration();
    ConfiguratorBase.setZooKeeperInstance(this.getClass(), conf,
        ClientConfiguration.create().withInstance("testInstanceName").withZkHosts("testZooKeepers")
            .withSsl(true).withZkTimeout(1234));
    ClientConfiguration clientConf = ClientConfiguration.deserialize(conf.get(ConfiguratorBase
        .enumToConfKey(this.getClass(), ConfiguratorBase.InstanceOpts.CLIENT_CONFIG)));
    assertEquals("testInstanceName", clientConf.get(ClientProperty.INSTANCE_NAME), () -> "Add assertion message");
    assertEquals("testZooKeepers", clientConf.get(ClientProperty.INSTANCE_ZK_HOST), () -> "Add assertion message");
    assertEquals("true", clientConf.get(ClientProperty.INSTANCE_RPC_SSL_ENABLED), () -> "Add assertion message");
    assertEquals("1234", clientConf.get(ClientProperty.INSTANCE_ZK_TIMEOUT), () -> "Add assertion message");
    assertEquals(ZooKeeperInstance.class.getSimpleName(), conf
        .get(ConfiguratorBase.enumToConfKey(this.getClass(), ConfiguratorBase.InstanceOpts.TYPE)), () -> "Add assertion message");

    // We want to test that the correct parameters from the config get passed to the ZKI
    // but that keeps us from being able to make assertions on a valid instance name at ZKI creation
    // Instance instance = ConfiguratorBase.getInstance(this.getClass(), conf);
    // assertEquals(ZooKeeperInstance.class.getName(), instance.getClass().getName());
    // assertEquals("testInstanceName", ((ZooKeeperInstance) instance).getInstanceName());
    // assertEquals("testZooKeepers", ((ZooKeeperInstance) instance).getZooKeepers());
    // assertEquals(1234000, ((ZooKeeperInstance) instance).getZooKeepersSessionTimeOut());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testSetMockInstance() {
    Class<?> mockClass = org.apache.accumulo.core.client.mock.MockInstance.class;
    Configuration conf = new Configuration();
    ConfiguratorBase.setMockInstance(this.getClass(), conf, "testInstanceName");
    assertEquals("testInstanceName", conf
        .get(ConfiguratorBase.enumToConfKey(this.getClass(), ConfiguratorBase.InstanceOpts.NAME)), () -> "Add assertion message");
    assertEquals(null, conf.get(ConfiguratorBase.enumToConfKey(this.getClass(),
        ConfiguratorBase.InstanceOpts.ZOO_KEEPERS)), () -> "Add assertion message");
    assertEquals(mockClass.getSimpleName(), conf
        .get(ConfiguratorBase.enumToConfKey(this.getClass(), ConfiguratorBase.InstanceOpts.TYPE)), () -> "Add assertion message");
    Instance instance = ConfiguratorBase.getInstance(this.getClass(), conf);
    assertEquals(mockClass.getName(), instance.getClass().getName(), () -> "Add assertion message");
  }

  @Test
  public void testSetLogLevel() {
    Configuration conf = new Configuration();
    Level currentLevel = Logger.getLogger(this.getClass()).getLevel();

    ConfiguratorBase.setLogLevel(this.getClass(), conf, Level.DEBUG);
    Logger.getLogger(this.getClass()).setLevel(currentLevel);
    assertEquals(Level.DEBUG, ConfiguratorBase.getLogLevel(this.getClass(), conf), () -> "Add assertion message");

    ConfiguratorBase.setLogLevel(this.getClass(), conf, Level.INFO);
    Logger.getLogger(this.getClass()).setLevel(currentLevel);
    assertEquals(Level.INFO, ConfiguratorBase.getLogLevel(this.getClass(), conf), () -> "Add assertion message");

    ConfiguratorBase.setLogLevel(this.getClass(), conf, Level.FATAL);
    Logger.getLogger(this.getClass()).setLevel(currentLevel);
    assertEquals(Level.FATAL, ConfiguratorBase.getLogLevel(this.getClass(), conf), () -> "Add assertion message");
  }

  @Test
  public void testSetVisibiltyCacheSize() {
    Configuration conf = new Configuration();
    assertEquals(Constants.DEFAULT_VISIBILITY_CACHE_SIZE,
        ConfiguratorBase.getVisibilityCacheSize(conf), () -> "Add assertion message");
    ConfiguratorBase.setVisibilityCacheSize(conf, 2000);
    Integer magicNumber570382 = 2000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber570382, ConfiguratorBase.getVisibilityCacheSize(conf), () -> "Add assertion message");
  }
}