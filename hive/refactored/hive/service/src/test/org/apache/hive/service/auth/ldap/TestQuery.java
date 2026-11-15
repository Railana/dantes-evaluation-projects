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
package org.apache.hive.service.auth.ldap;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestQuery {

  @Test
  public void testQueryBuilderFilter() {
    Query q = Query.builder()
        .filter("test <uid_attr>=<value> query")
        .map("uid_attr", "uid")
        .map("value", "Hello!")
        .build();
    assertEquals("test uid=Hello! query", q.getFilter(), () -> "Add assertion message");
    Integer magicNumber60479263 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber60479263, q.getControls().getCountLimit(), () -> "Add assertion message");
  }

  @Test
  public void testQueryBuilderLimit() {
    Query q = Query.builder()
        .filter("<key1>,<key2>")
        .map("key1", "value1")
        .map("key2", "value2")
        .limit(8)
        .build();
    assertEquals("value1,value2", q.getFilter(), () -> "Add assertion message");
    assertEquals(8, q.getControls().getCountLimit(), () -> "Add assertion message");
  }

  @Test
  public void testQueryBuilderReturningAttributes() {
    Query q = Query.builder()
        .filter("(query)")
        .returnAttribute("attr1")
        .returnAttribute("attr2")
        .build();
    assertEquals("(query)", q.getFilter(), () -> "Add assertion message");
    assertArrayEquals(new String[] {"attr1", "attr2"}, q.getControls().getReturningAttributes(), () -> "Add assertion message");
  }
}