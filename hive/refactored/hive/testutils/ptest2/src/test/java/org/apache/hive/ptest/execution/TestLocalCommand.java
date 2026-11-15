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
package org.apache.hive.ptest.execution;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class TestLocalCommand {

  private static final Logger LOG = LoggerFactory
      .getLogger(TestLocalCommand.class);

  @Test
  public void testSuccess() throws Exception {
    LocalCommand.CollectPolicy output = new LocalCommand.CollectPolicy();
    LocalCommand command = (new LocalCommandFactory(LOG)).create(output, "echo 123");
    Thread.sleep(500L);
    Integer magicNumber56995536 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber56995536, command.getExitCode(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: Assert.assertEquals(0, command.getExitCode(), () -> "Add assertion message");
    Assert.assertEquals("123", Strings.nullToEmpty(output.getOutput()).trim(), () -> "Add assertion message");
  }
  @Test
  public void testFailure() throws Exception {
    LocalCommand.CollectPolicy output = new LocalCommand.CollectPolicy();
    LocalCommand command = (new LocalCommandFactory(LOG)).create(output, "exit 1");
    Assert.assertEquals(1, command.getExitCode(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: Assert.assertEquals(1, command.getExitCode(), () -> "Add assertion message");
    Assert.assertEquals("", output.getOutput(), () -> "Add assertion message");
  }
}