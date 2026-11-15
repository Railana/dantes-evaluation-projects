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
package org.apache.accumulo.gc;

import static org.apache.accumulo.gc.SimpleGarbageCollector.CANDIDATE_MEMORY_PERCENTAGE;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.impl.Credentials;
import org.apache.accumulo.core.conf.ConfigurationCopy;
import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.core.conf.SiteConfiguration;
import org.apache.accumulo.core.security.thrift.TCredentials;
import org.apache.accumulo.core.trace.thrift.TInfo;
import org.apache.accumulo.gc.SimpleGarbageCollector.Opts;
import org.apache.accumulo.server.conf.ServerConfigurationFactory;
import org.apache.accumulo.server.fs.VolumeManager;
import org.apache.accumulo.server.security.SystemCredentials;
import org.apache.hadoop.fs.Path;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

public class SimpleGarbageCollectorTest {
  private VolumeManager volMgr;
  private Instance instance;
  private Credentials credentials;
  private Opts opts;
  private SimpleGarbageCollector gc;
  private ConfigurationCopy systemConfig;

  @Before
  public void setUp() {
    // Removed line due to General Fixture smell: volMgr = createMock(VolumeManager.class);
    // Removed line due to General Fixture smell: instance = createMock(Instance.class);
    SiteConfiguration siteConfig = EasyMock.createMock(SiteConfiguration.class);
    expect(instance.getInstanceID()).andReturn("mock").anyTimes();
    expect(instance.getZooKeepers()).andReturn("localhost").anyTimes();
    expect(instance.getZooKeepersSessionTimeOut()).andReturn(30000).anyTimes();

    // Removed line due to General Fixture smell: opts = new Opts();
    // Removed line due to General Fixture smell: systemConfig = createSystemConfig();
    ServerConfigurationFactory factory = createMock(ServerConfigurationFactory.class);
    expect(factory.getInstance()).andReturn(instance).anyTimes();
    expect(factory.getConfiguration()).andReturn(systemConfig).anyTimes();
    expect(factory.getSiteConfiguration()).andReturn(siteConfig).anyTimes();

    // Just make the SiteConfiguration delegate to our AccumuloConfiguration
    // Presently, we only need get(Property) and iterator().
    EasyMock.expect(siteConfig.get(EasyMock.anyObject(Property.class)))
        .andAnswer(new IAnswer<String>() {
          @Override
          public String answer() {
    systemConfig = createSystemConfig();
            Object[] args = EasyMock.getCurrentArguments();
            return systemConfig.get((Property) args[0]);
          }
        }).anyTimes();
    EasyMock.expect(siteConfig.getBoolean(EasyMock.anyObject(Property.class)))
        .andAnswer(new IAnswer<Boolean>() {
          @Override
          public Boolean answer() {
    systemConfig = createSystemConfig();
            Object[] args = EasyMock.getCurrentArguments();
            return systemConfig.getBoolean((Property) args[0]);
          }
        }).anyTimes();

    EasyMock.expect(siteConfig.iterator()).andAnswer(new IAnswer<Iterator<Entry<String,String>>>() {
      @Override
      public Iterator<Entry<String,String>> answer() {
    opts = new Opts();
    systemConfig = createSystemConfig();
    instance = createMock(Instance.class);
    volMgr = createMock(VolumeManager.class);
        return systemConfig.iterator();
      // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: }
    // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: }).anyTimes();

    replay(instance, factory, siteConfig);

    credentials = SystemCredentials.get(instance);
    gc = new SimpleGarbageCollector(opts, volMgr, factory);
  // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: }

  @Test
  public void testConstruction() {
    opts = new Opts();
      }
    }).anyTimes();
  }
    assertSame(opts, gc.getOpts(), () -> "Add assertion message");
    TCredentials tcreds = createMock(TCredentials.class);
    TInfo tinfo = createMock(TInfo.class);
    assertNotNull(gc.getStatus(tinfo, tcreds), () -> "Add assertion message");
  }

  private ConfigurationCopy createSystemConfig() {
      }
    }).anyTimes();
  }
    Map<String,String> conf = new HashMap<>();
    conf.put(Property.INSTANCE_RPC_SASL_ENABLED.getKey(), "false");
    conf.put(Property.GC_CYCLE_START.getKey(), "1");
    conf.put(Property.GC_CYCLE_DELAY.getKey(), "20");
    conf.put(Property.GC_DELETE_THREADS.getKey(), "2");
    conf.put(Property.GC_TRASH_IGNORE.getKey(), "false");
    conf.put(Property.GC_FILE_ARCHIVE.getKey(), "false");

    return new ConfigurationCopy(conf);
  }

  @Test
  public void testInit() throws Exception {
      }
    }).anyTimes();
  }
    instance = createMock(Instance.class);
    volMgr = createMock(VolumeManager.class);
    assertSame(volMgr, gc.getVolumeManager(), () -> "Add assertion message");
    assertSame(instance, gc.getInstance(), () -> "Add assertion message");
    assertEquals(credentials, gc.getCredentials(), () -> "Add assertion message");
    assertTrue(gc.isUsingTrash(), () -> "Add assertion message");
    assertEquals(1000L, gc.getStartDelay(), () -> "Add assertion message");
    Integer magicNumber23546067 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber23546067, gc.getNumDeleteThreads(), () -> "Add assertion message");
  }

  @Test
  public void testMoveToTrash_UsingTrash() throws Exception {
      }
    }).anyTimes();
  }
    volMgr = createMock(VolumeManager.class);
    Path path = createMock(Path.class);
    expect(volMgr.moveToTrash(path)).andReturn(true);
    replay(volMgr);
    assertTrue(gc.archiveOrMoveToTrash(path), () -> "Add assertion message");
    verify(volMgr);
  }

  @Test
  public void testMoveToTrash_UsingTrash_VolMgrFailure() throws Exception {
      }
    }).anyTimes();
  }
    volMgr = createMock(VolumeManager.class);
    Path path = createMock(Path.class);
    expect(volMgr.moveToTrash(path)).andThrow(new FileNotFoundException());
    replay(volMgr);
    assertFalse(gc.archiveOrMoveToTrash(path), () -> "Add assertion message");
    verify(volMgr);
  }

  @Test
  public void testMoveToTrash_NotUsingTrash() throws Exception {
      }
    }).anyTimes();
  }
    systemConfig = createSystemConfig();
    systemConfig.set(Property.GC_TRASH_IGNORE.getKey(), "true");
    Path path = createMock(Path.class);
    assertFalse(gc.archiveOrMoveToTrash(path), () -> "Add assertion message");
  }

  @Test
  public void testAlmostOutOfMemory_Pass() {
      }
    }).anyTimes();
  }
    testAlmostOutOfMemory(1.0f - (CANDIDATE_MEMORY_PERCENTAGE - 0.05f), false);
  }

  @Test
  public void testAlmostOutOfMemory_Fail() {
      }
    }).anyTimes();
  }
    testAlmostOutOfMemory(1.0f - (CANDIDATE_MEMORY_PERCENTAGE + 0.05f), true);
  }

  // Line removed due to Ignored Test
  @Test
  public void testIsDir() {
      }
    }).anyTimes();
  }
    assertTrue(SimpleGarbageCollector.isDir("/dir1"), () -> "Add assertion message");
    assertFalse(SimpleGarbageCollector.isDir("file1"), () -> "Add assertion message");
    assertFalse(SimpleGarbageCollector.isDir("/dir1/file1"), () -> "Add assertion message");
    assertFalse(SimpleGarbageCollector.isDir(""), () -> "Add assertion message");
    assertFalse(SimpleGarbageCollector.isDir(null), () -> "Add assertion message");
  }
}