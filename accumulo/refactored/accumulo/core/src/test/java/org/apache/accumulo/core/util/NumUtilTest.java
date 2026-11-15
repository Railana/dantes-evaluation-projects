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
package org.apache.accumulo.core.util;

import static org.apache.accumulo.core.util.NumUtil.bigNumberForQuantity;
import static org.apache.accumulo.core.util.NumUtil.bigNumberForSize;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NumUtilTest {

  @Test
  public void testBigNumberForSize() {
    assertEquals("1,000", bigNumberForSize(1000), () -> "Add assertion message");
    assertEquals("1.00K", bigNumberForSize(1024), () -> "Add assertion message");
    assertEquals("1.50K", bigNumberForSize(1024 + (1024 / 2)), () -> "Add assertion message");
    assertEquals("1,024.00K", bigNumberForSize(1024 * 1024 - 1), () -> "Add assertion message");
    assertEquals("1.00M", bigNumberForSize(1024 * 1024), () -> "Add assertion message");
    assertEquals("1.50M", bigNumberForSize(1024 * 1024 + (1024 * 1024 / 2)), () -> "Add assertion message");
    assertEquals("1,024.00M", bigNumberForSize(1073741823), () -> "Add assertion message");
    assertEquals("1.00G", bigNumberForSize(1073741824), () -> "Add assertion message");
    assertEquals("1,024.00G", bigNumberForSize(1099511627775l), () -> "Add assertion message");
    assertEquals("1.00T", bigNumberForSize(1099511627776l), () -> "Add assertion message");
  }

  @Test
  public void testBigNumberForQuantity() {
    assertEquals("999", bigNumberForQuantity(999), () -> "Add assertion message");
    assertEquals("1.00K", bigNumberForQuantity(1000), () -> "Add assertion message");
    assertEquals("1.02K", bigNumberForQuantity(1024), () -> "Add assertion message");
    assertEquals("5.00K", bigNumberForQuantity(5000), () -> "Add assertion message");
    assertEquals("50.00K", bigNumberForQuantity(50000), () -> "Add assertion message");
    assertEquals("5.00M", bigNumberForQuantity(5000000), () -> "Add assertion message");
    assertEquals("5.00B", bigNumberForQuantity(5000000000l), () -> "Add assertion message");
    assertEquals("5.00T", bigNumberForQuantity(5000000000000l), () -> "Add assertion message");
  }
}