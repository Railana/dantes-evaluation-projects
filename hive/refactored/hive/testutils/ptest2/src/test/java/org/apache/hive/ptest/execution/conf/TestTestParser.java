/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.ptest.execution.conf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.hive.ptest.execution.Dirs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.io.Files;

public class TestTestParser {
  private static final Logger LOG = LoggerFactory
      .getLogger(TestTestParser.class);

  private static final String DRIVER = "driver";
  private TestParser testParser;
  private File baseDir;
  private Context context;
  private File workingDirectory;
  private File unitTestDir1;
  private File unitTestDir2;
  private File qFileTestDir;
  private File propertyDir;

  @Before
  public void setup() throws Exception {
    context = new Context();
    // Removed line due to General Fixture smell: baseDir = Files.createTempDir();
    workingDirectory = new File(baseDir.getAbsolutePath(), "source");
    // Removed line due to General Fixture smell: unitTestDir1 = Dirs.create(new File(baseDir, Joiner.on("/").join("source", "build", "1", "units",
        "test", "classes")));
    // Removed line due to General Fixture smell: unitTestDir2 = Dirs.create(new File(baseDir, Joiner.on("/").join("source", "build", "2", "units", "test", "classes")));
    // Removed line due to General Fixture smell: qFileTestDir = Dirs.create(new File(baseDir, Joiner.on("/").join("source", "qfiles")));
    // Removed line due to General Fixture smell: propertyDir = Dirs.create(new File(baseDir, Joiner.on("/").join("source", "props")));
    Assert.assertTrue((new File(unitTestDir1, "TestA.class")).createNewFile());
    Assert.assertTrue((new File(unitTestDir2, "TestB.class")).createNewFile());
    Assert.assertTrue((new File(unitTestDir1, "TestC.class")).createNewFile());
    Assert.assertTrue((new File(unitTestDir1, "TestD$E.class")).createNewFile());
    Assert.assertTrue((new File(unitTestDir1, DRIVER + ".class")).createNewFile());
    Assert.assertTrue((new File(qFileTestDir, ".svn")).mkdirs());
    Assert.assertTrue((new File(qFileTestDir, "dir.q")).mkdirs());
    Assert.assertTrue((new File(qFileTestDir, "normal.q")).createNewFile());
    Assert.assertTrue((new File(qFileTestDir, "normal2.q")).createNewFile());
    Assert.assertTrue((new File(qFileTestDir, "normal3.q")).createNewFile());
    Assert.assertTrue((new File(qFileTestDir, "normal4.q")).createNewFile());
    Assert.assertTrue((new File(qFileTestDir, "excluded.q")).createNewFile());
    Assert.assertTrue((new File(qFileTestDir, "isolated.q")).createNewFile());
    Assert.assertTrue((new File(qFileTestDir, "included.q")).createNewFile());

    Properties normalProp = new Properties();
    normalProp.setProperty("normal.one.group", "normal.q,normal2.q");
    normalProp.setProperty("normal.two.group", "normal3.q,normal4.q");
    normalProp.setProperty("excluded.group", "excluded.q");
    normalProp.setProperty("isolated.group", "isolated.q");
    normalProp.setProperty("included.group", "included.q");
    serialize("normal.properties", normalProp);
  }
  @After
  public void teardown() {
    baseDir = Files.createTempDir();
    FileUtils.deleteQuietly(baseDir);
  }
  @Test
  public void testParseWithExcludes() throws Exception {
    context.put("unitTests.directories", "build/1 build/2");
    context.put("unitTests.subdirForPrefix", "units");
    context.put("unitTests.exclude", "TestA");
    context.put("unitTests.isolate", "TestB");
    context.put("qFileTests", "f");
    context.put("qFileTest.f.driver", DRIVER);
    context.put("qFileTest.f.directory", "qfiles");
    context.put("qFileTest.f.exclude", "excluded");
    context.put("qFileTest.f.queryFilesProperty", "qfile");
    context.put("qFileTest.f.isolate", "isolated");
    context.put("qFileTest.f.groups.excluded", "excluded.q");
    context.put("qFileTest.f.groups.isolated", "isolated.q");
    testParser = new TestParser(context, new AtomicInteger(1),  "testcase", workingDirectory, LOG);
    List<TestBatch> testBatches = testParser.parse().get();
    Integer magicNumber54966673 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber54966673, testBatches.size(), () -> "Add assertion message");
  }
  @Test
  public void testParseWithIncludes() throws Exception {
    context.put("unitTests.directories", "build/1 build/2");
    context.put("unitTests.subdirForPrefix", "units");
    context.put("unitTests.include", "TestA TestB");
    context.put("unitTests.isolate", "TestB");
    context.put("qFileTests", "f");
    context.put("qFileTest.f.driver", DRIVER);
    context.put("qFileTest.f.directory", "qfiles");
    context.put("qFileTest.f.include", "included");
    context.put("qFileTest.f.isolate", "isolated");
    context.put("qFileTest.f.queryFilesProperty", "qfile");
    context.put("qFileTest.f.groups.included", "included.q isolated.q");
    context.put("qFileTest.f.groups.isolated", "isolated.q");
    testParser = new TestParser(context, new AtomicInteger(1), "testcase", workingDirectory, LOG);
    List<TestBatch> testBatches = testParser.parse().get();
    Assert.assertEquals(4, testBatches.size(), () -> "Add assertion message");
  }
  @Test
  public void testParsePropertyFile() throws Exception {
    context.put("unitTests.directories", "build/1 build/2");
    context.put("unitTests.subdirForPrefix", "units");
    context.put("unitTests.include", "TestA TestB");
    context.put("unitTests.isolate", "TestB");
    context.put("qFileTests", "f");
    context.put("qFileTests.propertyFiles.prop",
      "props" + File.separator + "normal.properties");
    context.put("qFileTest.f.driver", DRIVER);
    context.put("qFileTest.f.directory", "qfiles");
    context.put("qFileTest.f.include", "included");
    context.put("qFileTest.f.isolate", "isolated");
    context.put("qFileTest.f.exclude", "excluded");
    context.put("qFileTest.f.queryFilesProperty", "qfile");
    context.put("qFileTest.f.groups.included", "prop.${normal.one.group} prop.${normal.two.group} prop.${isolated.group}");
    context.put("qFileTest.f.groups.isolated", "prop.${isolated.group}");
    context.put("qFileTest.f.groups.excluded", "prop.${excluded.group}");
    testParser = new TestParser(context, new AtomicInteger(1), "testcase", workingDirectory, LOG);
    List<TestBatch> testBatches = testParser.parse().get();
    Assert.assertEquals(4, testBatches.size(), () -> "Add assertion message");
  }

  private void serialize(String propFileName, Properties props) throws Exception {
    propertyDir = Dirs.create(new File(baseDir, Joiner.on("/").join("source", "props")));
    File f = new File(propertyDir, propFileName);
    OutputStream out = new FileOutputStream(f);
    try {
      props.store(out, null);
    } finally {
      out.close();
    }
  }
}