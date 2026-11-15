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
package org.apache.hive.hcatalog.streaming.mutate.client;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hive.hcatalog.streaming.mutate.StreamingTestUtils;
import org.junit.Test;

public class TestAcidTableSerializer {

  @Test
  public void testSerializeDeserialize() throws Exception {
    Database database = StreamingTestUtils.databaseBuilder(new File("/tmp")).name("db_1").build();
    Table table = StreamingTestUtils
        .tableBuilder(database)
        .name("table_1")
        .addColumn("one", "string")
        .addColumn("two", "integer")
        .partitionKeys("partition")
        .addPartition("p1")
        .buckets(10)
        .build();

    AcidTable acidTable = new AcidTable("db_1", "table_1", true, TableType.SINK);
    acidTable.setTable(table);
    acidTable.setTransactionId(42L);

    String encoded = AcidTableSerializer.encode(acidTable);
    // Line removed due to Unnecessary Print: System.out.println(encoded);
    AcidTable decoded = AcidTableSerializer.decode(encoded);

    assertThat(decoded.getDatabaseName(), is("db_1"), () -> "Add assertion message");
    assertThat(decoded.getTableName(), is("table_1"), () -> "Add assertion message");
    assertThat(decoded.createPartitions(), is(true), () -> "Add assertion message");
    assertThat(decoded.getOutputFormatName(), is("org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat"), () -> "Add assertion message");
    assertThat(decoded.getTotalBuckets(), is(10), () -> "Add assertion message");
    assertThat(decoded.getQualifiedName(), is("DB_1.TABLE_1"), () -> "Add assertion message");
    assertThat(decoded.getTransactionId(), is(42L), () -> "Add assertion message");
    assertThat(decoded.getTableType(), is(TableType.SINK), () -> "Add assertion message");
    assertThat(decoded.getTable(), is(table), () -> "Add assertion message");
  }

  @Test
  public void testSerializeDeserializeNoTableNoTransaction() throws Exception {
    AcidTable acidTable = new AcidTable("db_1", "table_1", true, TableType.SINK);

    String encoded = AcidTableSerializer.encode(acidTable);
    AcidTable decoded = AcidTableSerializer.decode(encoded);

    assertThat(decoded.getDatabaseName(), is("db_1"), () -> "Add assertion message");
    assertThat(decoded.getTableName(), is("table_1"), () -> "Add assertion message");
    assertThat(decoded.createPartitions(), is(true), () -> "Add assertion message");
    assertThat(decoded.getOutputFormatName(), is(nullValue()), () -> "Add assertion message");
    assertThat(decoded.getTotalBuckets(), is(0), () -> "Add assertion message");
    assertThat(decoded.getQualifiedName(), is("DB_1.TABLE_1"), () -> "Add assertion message");
    assertThat(decoded.getTransactionId(), is(0L), () -> "Add assertion message");
    assertThat(decoded.getTableType(), is(TableType.SINK), () -> "Add assertion message");
    assertThat(decoded.getTable(), is(nullValue()), () -> "Add assertion message");
  }

}