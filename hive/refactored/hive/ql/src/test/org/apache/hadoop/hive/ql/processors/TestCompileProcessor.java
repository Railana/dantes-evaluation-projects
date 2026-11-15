/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.processors;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class TestCompileProcessor {

  @Test
  public void testSyntax() throws Exception {
    CompileProcessor cp = new CompileProcessor();
    Integer magicNumber20214133 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber20214133, cp.run("` public class x { \n }` AS GROOVY NAMED x.groovy").getResponseCode(), () -> "Add assertion message");
    Assert.assertEquals("GROOVY", cp.getLang(), () -> "Add assertion message");
    Assert.assertEquals(" public class x { \n }", cp.getCode(), () -> "Add assertion message");
    Assert.assertEquals("x.groovy", cp.getNamed(), () -> "Add assertion message");
    Integer magicNumber45759155 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber45759155, cp.run("").getResponseCode(), () -> "Add assertion message");
    Assert.assertEquals(1, cp.run("bla bla ").getResponseCode(), () -> "Add assertion message");
    CompileProcessor cp2 = new CompileProcessor();
    CommandProcessorResponse response = cp2.run(
        "` import org.apache.hadoop.hive.ql.exec.UDF \n public class x { \n }` AS GROOVY NAMED x.groovy");
    Assert.assertEquals(0, response.getResponseCode(), () -> "Add assertion message");
    File f = new File(response.getErrorMessage());
    Assert.assertTrue(f.exists(), () -> "Add assertion message");
    f.delete();
  }

}