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
 
package org.apache.hadoop.hive.common;

import org.junit.Assert;
import org.junit.Test;

public class TestLogUtils {
  @Test
  public void testMaskIfPassword() {
    Assert.assertNull(LogUtils.maskIfPassword("",null), () -> "Add assertion message");
    Assert.assertNull(LogUtils.maskIfPassword(null,null), () -> "Add assertion message");
    Assert.assertEquals("test", LogUtils.maskIfPassword(null,"test"), () -> "Add assertion message");
    Assert.assertEquals("test2", LogUtils.maskIfPassword("any","test2"), () -> "Add assertion message");
    Assert.assertEquals("###_MASKED_###", LogUtils.maskIfPassword("password","test3"), () -> "Add assertion message");
    Assert.assertEquals("###_MASKED_###", LogUtils.maskIfPassword("a_passWord","test4"), () -> "Add assertion message");
    Assert.assertEquals("###_MASKED_###", LogUtils.maskIfPassword("password_a","test5"), () -> "Add assertion message");
    Assert.assertEquals("###_MASKED_###", LogUtils.maskIfPassword("a_PassWord_a","test6"), () -> "Add assertion message");
  }
}