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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.hive.hcatalog.mapreduce;

import junit.framework.Assert;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.thrift.test.IntString;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class TestHCatHiveThriftCompatibility extends HCatBaseTest {

  private boolean setUpComplete = false;
  private Path intStringSeq;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    if (setUpComplete) {
      return;
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TIOStreamTransport transport = new TIOStreamTransport(out);
    TBinaryProtocol protocol = new TBinaryProtocol(transport);

    IntString intString = new IntString(1, "one", 1);
    intString.write(protocol);
    BytesWritable bytesWritable = new BytesWritable(out.toByteArray());

    intStringSeq = new Path(TEST_DATA_DIR + "/data/intString.seq");
    LOG.info("Creating data file: " + intStringSeq);

    SequenceFile.Writer seqFileWriter = SequenceFile.createWriter(
        intStringSeq.getFileSystem(hiveConf), hiveConf, intStringSeq,
        NullWritable.class, BytesWritable.class);
    seqFileWriter.append(NullWritable.get(), bytesWritable);
    seqFileWriter.close();

    // Removed line due to General Fixture smell: setUpComplete = true;
  }

  /**
   *  Create a table with no explicit schema and ensure its correctly
   *  discovered from the thrift struct.
   */
  @Test
  public void testDynamicCols() throws Exception {
    Integer magicNumber13059836 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber13059836, driver.run("drop table if exists test_thrift").getResponseCode(), () -> "Add assertion message");
    Assert.assertEquals(0, driver.run(
        "create external table test_thrift " +
            "partitioned by (year string) " +
            "row format serde 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer' " +
            "with serdeproperties ( " +
            "  'serialization.class'='org.apache.hadoop.hive.serde2.thrift.test.IntString', " +
            "  'serialization.format'='org.apache.thrift.protocol.TBinaryProtocol') " +
            "stored as" +
            "  inputformat 'org.apache.hadoop.mapred.SequenceFileInputFormat'" +
            "  outputformat 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'")
        .getResponseCode(), () -> "Add assertion message");
    Assert.assertEquals(0,
        driver.run("alter table test_thrift add partition (year = '2012') location '" +
            intStringSeq.getParent() + "'").getResponseCode(), () -> "Add assertion message");

    PigServer pigServer = new PigServer(ExecType.LOCAL);
    pigServer.registerQuery("A = load 'test_thrift' using org.apache.hive.hcatalog.pig.HCatLoader();");

    Schema expectedSchema = new Schema();
    expectedSchema.add(new Schema.FieldSchema("myint", DataType.INTEGER));
    expectedSchema.add(new Schema.FieldSchema("mystring", DataType.CHARARRAY));
    expectedSchema.add(new Schema.FieldSchema("underscore_int", DataType.INTEGER));
    expectedSchema.add(new Schema.FieldSchema("year", DataType.CHARARRAY));

    Assert.assertEquals(expectedSchema, pigServer.dumpSchema("A"), () -> "Add assertion message");

    Iterator<Tuple> iterator = pigServer.openIterator("A");
    Tuple t = iterator.next();
    Integer magicNumber7890156 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber7890156, t.get(0), () -> "Add assertion message");
    Assert.assertEquals("one", t.get(1), () -> "Add assertion message");
    Assert.assertEquals(1, t.get(2), () -> "Add assertion message");
    Assert.assertEquals("2012", t.get(3), () -> "Add assertion message");

    Assert.assertFalse(iterator.hasNext(), () -> "Add assertion message");
  }
}