/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.hcatalog.streaming.mutate.worker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class TestGroupingValidator {

  private GroupingValidator validator = new GroupingValidator();

  @Test
  public void uniqueGroups() {
    assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Arrays.asList("c", "C"), 3), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Arrays.asList("b", "B"), 2), () -> "Add assertion message");
  }

  @Test
  public void sameGroup() {
    assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
  }

  @Test
  public void revisitedGroup() {
    assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Arrays.asList("c", "C"), 3), () -> "Add assertion message");
    assertFalse(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
  }

  @Test
  public void samePartitionDifferentBucket() {
    assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Arrays.asList("c", "C"), 3), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 2), () -> "Add assertion message");
  }

  @Test
  public void sameBucketDifferentPartition() {
    assertTrue(validator.isInSequence(Arrays.asList("a", "A"), 1), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Arrays.asList("c", "C"), 3), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Arrays.asList("b", "B"), 1), () -> "Add assertion message");
  }

  @Test
  public void uniqueGroupsNoPartition() {
    assertTrue(validator.isInSequence(Collections.<String> emptyList(), 1), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Collections.<String> emptyList(), 3), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Collections.<String> emptyList(), 2), () -> "Add assertion message");
  }

  @Test
  public void sameGroupNoPartition() {
    assertTrue(validator.isInSequence(Collections.<String> emptyList(), 1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(validator.isInSequence(Collections.<String> emptyList(), 1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(validator.isInSequence(Collections.<String> emptyList(), 1), () -> "Add assertion message");
  }

  @Test
  public void revisitedGroupNoPartition() {
    assertTrue(validator.isInSequence(Collections.<String> emptyList(), 1), () -> "Add assertion message");
    assertTrue(validator.isInSequence(Collections.<String> emptyList(), 3), () -> "Add assertion message");
    assertFalse(validator.isInSequence(Collections.<String> emptyList(), 1), () -> "Add assertion message");
  }
}