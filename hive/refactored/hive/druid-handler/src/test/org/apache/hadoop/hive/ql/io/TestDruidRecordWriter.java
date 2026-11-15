/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.metamx.common.Granularity;
import io.druid.data.input.Firehose;
import io.druid.data.input.InputRow;
import io.druid.data.input.impl.DimensionSchema;
import io.druid.data.input.impl.DimensionsSpec;
import io.druid.data.input.impl.InputRowParser;
import io.druid.data.input.impl.MapInputRowParser;
import io.druid.data.input.impl.StringDimensionSchema;
import io.druid.data.input.impl.TimeAndDimsParseSpec;
import io.druid.data.input.impl.TimestampSpec;
import io.druid.granularity.QueryGranularities;
import io.druid.query.aggregation.AggregatorFactory;
import io.druid.query.aggregation.LongSumAggregatorFactory;
import io.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import io.druid.segment.QueryableIndex;
import io.druid.segment.QueryableIndexStorageAdapter;
import io.druid.segment.indexing.DataSchema;
import io.druid.segment.indexing.RealtimeTuningConfig;
import io.druid.segment.indexing.granularity.UniformGranularitySpec;
import io.druid.segment.loading.DataSegmentPusher;
import io.druid.segment.loading.LocalDataSegmentPuller;
import io.druid.segment.loading.LocalDataSegmentPusher;
import io.druid.segment.loading.LocalDataSegmentPusherConfig;
import io.druid.segment.loading.SegmentLoadingException;
import io.druid.segment.realtime.firehose.IngestSegmentFirehose;
import io.druid.segment.realtime.firehose.WindowedStorageAdapter;
import io.druid.timeline.DataSegment;
import org.apache.calcite.adapter.druid.DruidTable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.Constants;
import org.apache.hadoop.hive.druid.DruidStorageHandler;
import org.apache.hadoop.hive.druid.DruidStorageHandlerUtils;
import org.apache.hadoop.hive.druid.io.DruidRecordWriter;
import org.apache.hadoop.hive.druid.serde.DruidWritable;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestDruidRecordWriter {
  private ObjectMapper objectMapper = DruidStorageHandlerUtils.JSON_MAPPER;

  private static final Interval INTERVAL_FULL = new Interval("2014-10-22T00:00:00Z/P1D");

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private DruidRecordWriter druidRecordWriter;

  final List<ImmutableMap<String, Object>> expectedRows = ImmutableList.of(
          ImmutableMap.<String, Object>of(
                  DruidTable.DEFAULT_TIMESTAMP_COLUMN,
                  DateTime.parse("2014-10-22T00:00:00.000Z").getMillis(),
                  "host", ImmutableList.of("a.example.com"),
                  "visited_sum", 190L,
                  "unique_hosts", 1.0d
          ),
          ImmutableMap.<String, Object>of(
                  DruidTable.DEFAULT_TIMESTAMP_COLUMN,
                  DateTime.parse("2014-10-22T01:00:00.000Z").getMillis(),
                  "host", ImmutableList.of("b.example.com"),
                  "visited_sum", 175L,
                  "unique_hosts", 1.0d
          ),
          ImmutableMap.<String, Object>of(
                  DruidTable.DEFAULT_TIMESTAMP_COLUMN,
                  DateTime.parse("2014-10-22T02:00:00.000Z").getMillis(),
                  "host", ImmutableList.of("c.example.com"),
                  "visited_sum", 270L,
                  "unique_hosts", 1.0d
          )
  );

  // This test need this patch https://github.com/druid-io/druid/pull/3483
  // Line removed due to Ignored Test
  private void verifyRows(List<ImmutableMap<String, Object>> expectedRows,
          List<InputRow> actualRows
  ) {
    System.out.println("actualRows = " + actualRows);
    Assert.assertEquals(expectedRows.size(), actualRows.size());

    for (int i = 0; i < expectedRows.size(); i++) {
      Map<String, Object> expected = expectedRows.get(i);
      InputRow actual = actualRows.get(i);

      Assert.assertEquals(ImmutableList.of("host"), actual.getDimensions());

      Assert.assertEquals(expected.get(DruidTable.DEFAULT_TIMESTAMP_COLUMN),
              actual.getTimestamp().getMillis()
      );
      Assert.assertEquals(expected.get("host"), actual.getDimension("host"));
      Assert.assertEquals(expected.get("visited_sum"), actual.getLongMetric("visited_sum"));
      Assert.assertEquals(
              (Double) expected.get("unique_hosts"),
              (Double) HyperUniquesAggregatorFactory
                      .estimateCardinality(actual.getRaw("unique_hosts")),
              0.001
      );
    }
  }

  @Test
  public void testSerDesr() throws IOException {
    String segment = "{\"dataSource\":\"datasource2015\",\"interval\":\"2015-06-01T00:00:00.000-04:00/2015-06-02T00:00:00.000-04:00\",\"version\":\"2016-11-04T19:24:01.732-04:00\",\"loadSpec\":{\"type\":\"hdfs\",\"path\":\"hdfs://cn105-10.l42scl.hortonworks.com:8020/apps/hive/warehouse/druid.db/.hive-staging_hive_2016-11-04_19-23-50_168_1550339856804207572-1/_task_tmp.-ext-10002/_tmp.000000_0/datasource2015/20150601T000000.000-0400_20150602T000000.000-0400/2016-11-04T19_24_01.732-04_00/0/index.zip\"},\"dimensions\":\"dimension1\",\"metrics\":\"bigint\",\"shardSpec\":{\"type\":\"linear\",\"partitionNum\":0},\"binaryVersion\":9,\"size\":1765,\"identifier\":\"datasource2015_2015-06-01T00:00:00.000-04:00_2015-06-02T00:00:00.000-04:00_2016-11-04T19:24:01.732-04:00\"}";
    DataSegment dataSegment = objectMapper.reader(DataSegment.class)
            .readValue(segment);
    Assert.assertTrue(dataSegment.getDataSource().equals("datasource2015"), () -> "Add assertion message");
  }

}