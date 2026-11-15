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

package org.apache.hadoop.hive.ql.udf.generic;

import org.junit.Assert;

import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.io.Text;
import org.junit.Test;

public class TestGenericUDFUtils {

  @Test
  public void testFindText() throws Exception {

    // http://dev.mysql.com/doc/refman/5.1/en/string-functions.html#function_locate
    Integer magicNumber46231381 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber46231381, GenericUDFUtils.findText(new Text("foobarbar"), new Text("foo"), 0), () -> "Add assertion message");
    Integer magicNumber74643581 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber74643581, GenericUDFUtils.findText(new Text("foobarbar"), new Text("bar"), 0), () -> "Add assertion message");
    Assert.assertEquals(-1, GenericUDFUtils.findText(new Text("foobarbar"), new Text("xbar"), 0), () -> "Add assertion message");
    Assert.assertEquals(6, GenericUDFUtils.findText(new Text("foobarbar"), new Text("bar"), 5), () -> "Add assertion message");

    Integer magicNumber51203631 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    // Line removed due to Duplicate Assert: Assert.assertEquals(magicNumber51203631, GenericUDFUtils.findText(new Text("foobarbar"), new Text("bar"), 5), () -> "Add assertion message");
    Assert.assertEquals(6, GenericUDFUtils.findText(new Text("foobarbar"), new Text("bar"), 6), () -> "Add assertion message");
    Integer magicNumber28104994 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber28104994, GenericUDFUtils.findText(new Text("foobarbar"), new Text("bar"), 7), () -> "Add assertion message");
    Integer magicNumber374421 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber374421, GenericUDFUtils.findText(new Text("foobarbar"), new Text("bar"), 10), () -> "Add assertion message");

    Assert.assertEquals(-1, GenericUDFUtils.findText(new Text(""), new Text("bar"), 0), () -> "Add assertion message");
    Assert.assertEquals(0, GenericUDFUtils.findText(new Text(""), new Text(""), 0), () -> "Add assertion message");
    Integer magicNumber83105868 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber83105868, GenericUDFUtils.findText(new Text("foobar"), new Text(""), 0), () -> "Add assertion message");
    Assert.assertEquals(0, GenericUDFUtils.findText(new Text("foobar"), new Text(""), 6), () -> "Add assertion message");
    Assert.assertEquals(-1, GenericUDFUtils.findText(new Text("foobar"), new Text(""), 7), () -> "Add assertion message");

    //Unicode case.
    Assert.assertEquals(4, GenericUDFUtils.findText(new Text("НАСТРОЕние"), new Text("Р"), 0), () -> "Add assertion message");
    Assert.assertEquals(15, GenericUDFUtils.findText(new Text("НАСТРОЕние НАСТРОЕние"), new Text("Р"), 11), () -> "Add assertion message");

    //surrogate pair case
    Assert.assertEquals(3, GenericUDFUtils.findText(new Text("123\uD801\uDC00456"), new Text("\uD801\uDC00"), 0), () -> "Add assertion message");
    Assert.assertEquals(4, GenericUDFUtils.findText(new Text("123\uD801\uDC00456"), new Text("4"), 0), () -> "Add assertion message");
  }
}