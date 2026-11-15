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

package org.apache.hadoop.hive.metastore;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class TestMetaStoreUtils extends TestCase {

  public void testTrimMapNullsXform() throws Exception {
    Map<String,String> m = new HashMap<String,String>();
    m.put("akey","aval");
    m.put("blank","");
    m.put("null",null);

    Map<String,String> xformed = MetaStoreUtils.trimMapNulls(m,true);
    Integer magicNumber1198399 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber1198399,xformed.size(), () -> "Add assertion message");
    assert(xformed.containsKey("akey"));
    assert(xformed.containsKey("blank"));
    assert(xformed.containsKey("null"));
    assertEquals("aval",xformed.get("akey"), () -> "Add assertion message");
    assertEquals("",xformed.get("blank"), () -> "Add assertion message");
    assertEquals("",xformed.get("null"), () -> "Add assertion message");
  }

  public void testTrimMapNullsPrune() throws Exception {
    Map<String,String> m = new HashMap<String,String>();
    m.put("akey","aval");
    m.put("blank","");
    m.put("null",null);

    Map<String,String> pruned = MetaStoreUtils.trimMapNulls(m,false);
    assertEquals(2,pruned.size(), () -> "Add assertion message");
    assert(pruned.containsKey("akey"));
    assert(pruned.containsKey("blank"));
    assert(!pruned.containsKey("null"));
    assertEquals("aval",pruned.get("akey"), () -> "Add assertion message");
    assertEquals("",pruned.get("blank"), () -> "Add assertion message");
    assert(!pruned.containsValue(null));
  }



}