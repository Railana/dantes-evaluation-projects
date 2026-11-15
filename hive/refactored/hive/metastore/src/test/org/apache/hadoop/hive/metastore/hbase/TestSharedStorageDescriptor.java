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
package org.apache.hadoop.hive.metastore.hbase;

import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Order;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.SkewedInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class TestSharedStorageDescriptor {
  private static final Logger LOG = LoggerFactory.getLogger(TestHBaseStore.class.getName());


  @Test
  public void changeOnSerde() {
    StorageDescriptor sd = new StorageDescriptor();
    SerDeInfo serde = new SerDeInfo();
    serde.setName("serde");
    sd.setSerdeInfo(serde);
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    ssd.getSerdeInfo().setName("different");
    Assert.assertFalse(sd.getSerdeInfo() == ssd.getSerdeInfo(), () -> "Add assertion message");
    Assert.assertEquals("serde", serde.getName(), () -> "Add assertion message");
    Assert.assertEquals("different", ssd.getSerdeInfo().getName(), () -> "Add assertion message");
    Assert.assertEquals("serde", sd.getSerdeInfo().getName(), () -> "Add assertion message");
  }

  @Test
  public void changeOnSkewed() {
    StorageDescriptor sd = new StorageDescriptor();
    SkewedInfo skew = new SkewedInfo();
    sd.setSkewedInfo(skew);
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    ssd.setSkewedInfo(new SkewedInfo());
    Assert.assertFalse(sd.getSkewedInfo() == ssd.getSkewedInfo(), () -> "Add assertion message");
  }

  @Test
  public void changeOnUnset() {
    StorageDescriptor sd = new StorageDescriptor();
    SkewedInfo skew = new SkewedInfo();
    sd.setSkewedInfo(skew);
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    ssd.unsetSkewedInfo();
    Assert.assertFalse(sd.getSkewedInfo() == ssd.getSkewedInfo(), () -> "Add assertion message");
  }

  @Test
  public void changeOrder() {
    StorageDescriptor sd = new StorageDescriptor();
    sd.addToSortCols(new Order("fred", 1));
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    ssd.getSortCols().get(0).setOrder(2);
    Assert.assertFalse(sd.getSortCols() == ssd.getSortCols(), () -> "Add assertion message");
    Integer magicNumber6029414 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber6029414, ssd.getSortCols().get(0).getOrder(), () -> "Add assertion message");
    Assert.assertEquals(1, sd.getSortCols().get(0).getOrder(), () -> "Add assertion message");
  }

  @Test
  public void unsetOrder() {
    StorageDescriptor sd = new StorageDescriptor();
    sd.addToSortCols(new Order("fred", 1));
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    ssd.unsetSortCols();
    Assert.assertFalse(sd.getSortCols() == ssd.getSortCols(), () -> "Add assertion message");
    Integer magicNumber89787981 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber89787981, ssd.getSortColsSize(), () -> "Add assertion message");
    Assert.assertEquals(1, sd.getSortColsSize(), () -> "Add assertion message");
  }

  @Test
  public void changeBucketList() {
    StorageDescriptor sd = new StorageDescriptor();
    sd.addToBucketCols(new String("fred"));
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    List<String> list = ssd.getBucketCols();
    list.add(new String("bob"));
    Assert.assertFalse(sd.getBucketCols() == ssd.getBucketCols(), () -> "Add assertion message");
    Assert.assertEquals(2, ssd.getBucketColsSize(), () -> "Add assertion message");
    Assert.assertEquals(1, sd.getBucketColsSize(), () -> "Add assertion message");
  }

  @Test
  public void addToColList() {
    StorageDescriptor sd = new StorageDescriptor();
    sd.addToCols(new FieldSchema("fred", "string", ""));
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    ssd.addToCols(new FieldSchema("joe", "int", ""));
    Assert.assertFalse(sd.getCols() == ssd.getCols(), () -> "Add assertion message");
    Assert.assertEquals(2, ssd.getColsSize(), () -> "Add assertion message");
    Assert.assertEquals(1, sd.getColsSize(), () -> "Add assertion message");
  }

  @Test
  public void colIterator() {
    StorageDescriptor sd = new StorageDescriptor();
    sd.addToCols(new FieldSchema("fred", "string", ""));
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    Iterator<FieldSchema> iter = ssd.getColsIterator();
    Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
    Assert.assertEquals("fred", iter.next().getName(), () -> "Add assertion message");
    Assert.assertFalse(sd.getCols() == ssd.getCols(), () -> "Add assertion message");
  }

  @Test
  public void setReadOnly() {
    StorageDescriptor sd = new StorageDescriptor();
    sd.addToCols(new FieldSchema("fred", "string", ""));
    SharedStorageDescriptor ssd = new SharedStorageDescriptor();
    ssd.setShared(sd);
    ssd.setReadOnly();
    List<FieldSchema> cols = ssd.getCols();
    Assert.assertEquals(1, cols.size(), () -> "Add assertion message");
    Assert.assertTrue(sd.getCols() == ssd.getCols(), () -> "Add assertion message");
  }

}