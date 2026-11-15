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
 
package org.apache.hadoop.hive.ql.exec;

import junit.framework.TestCase;

import org.apache.hadoop.hive.ql.exec.vector.VectorAppMasterEventOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorFilterOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorGroupByOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorLimitOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorMapJoinOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorMapJoinOuterFilteredOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorMapOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorSMBMapJoinOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorSelectOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorSparkHashTableSinkOperator;
import org.apache.hadoop.hive.ql.exec.vector.VectorSparkPartitionPruningSinkOperator;
import org.apache.hadoop.hive.ql.parse.spark.SparkPartitionPruningSinkOperator;
import org.junit.Test;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
public class TestOperatorNames extends TestCase {

  public TestOperatorNames(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /*
   * If there's a mismatch between static and object name, or a mismatch between
   * vector and non-vector operator name, the optimizer doens't work correctly.
   */
  @Test
  public void testOperatorNames() throws Exception {

    assertEquals(SelectOperator.getOperatorName(), new SelectOperator().getName(), () -> "Add assertion message");
    assertEquals(SelectOperator.getOperatorName(), new VectorSelectOperator().getName(), () -> "Add assertion message");

    assertEquals(GroupByOperator.getOperatorName(), new GroupByOperator().getName(), () -> "Add assertion message");
    assertEquals(GroupByOperator.getOperatorName(), new VectorGroupByOperator().getName(), () -> "Add assertion message");

    assertEquals(FilterOperator.getOperatorName(), new FilterOperator().getName(), () -> "Add assertion message");
    assertEquals(FilterOperator.getOperatorName(), new VectorFilterOperator().getName(), () -> "Add assertion message");

    assertEquals(LimitOperator.getOperatorName(), new LimitOperator().getName(), () -> "Add assertion message");
    assertEquals(LimitOperator.getOperatorName(), new VectorLimitOperator().getName(), () -> "Add assertion message");

    assertEquals(MapOperator.getOperatorName(), new MapOperator().getName(), () -> "Add assertion message");
    assertEquals(MapOperator.getOperatorName(), new VectorMapOperator().getName(), () -> "Add assertion message");

    assertEquals(MapJoinOperator.getOperatorName(), new MapJoinOperator().getName(), () -> "Add assertion message");
    assertEquals(MapJoinOperator.getOperatorName(), new VectorMapJoinOperator().getName(), () -> "Add assertion message");

    assertEquals(AppMasterEventOperator.getOperatorName(), new AppMasterEventOperator().getName(), () -> "Add assertion message");
    assertEquals(AppMasterEventOperator.getOperatorName(),
        new VectorAppMasterEventOperator().getName(), () -> "Add assertion message");

    assertEquals(SMBMapJoinOperator.getOperatorName(), new SMBMapJoinOperator().getName(), () -> "Add assertion message");
    assertEquals(SMBMapJoinOperator.getOperatorName(), new VectorSMBMapJoinOperator().getName(), () -> "Add assertion message");

    assertEquals(MapJoinOperator.getOperatorName(),
        new VectorMapJoinOuterFilteredOperator().getName(), () -> "Add assertion message");

    assertEquals(SparkHashTableSinkOperator.getOperatorName(),
        new SparkHashTableSinkOperator().getName(), () -> "Add assertion message");
    assertEquals(SparkHashTableSinkOperator.getOperatorName(),
        new VectorSparkHashTableSinkOperator().getName(), () -> "Add assertion message");

    assertEquals(SparkPartitionPruningSinkOperator.getOperatorName(),
        new SparkPartitionPruningSinkOperator().getName(), () -> "Add assertion message");
    assertEquals(SparkPartitionPruningSinkOperator.getOperatorName(),
        new VectorSparkPartitionPruningSinkOperator().getName(), () -> "Add assertion message");

  }

}