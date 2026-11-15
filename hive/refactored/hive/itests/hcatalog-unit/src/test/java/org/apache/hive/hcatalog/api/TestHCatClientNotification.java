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
package org.apache.hive.hcatalog.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.hive.hcatalog.messaging.AddPartitionMessage;
import org.apache.hive.hcatalog.messaging.CreateDatabaseMessage;
import org.apache.hive.hcatalog.messaging.CreateTableMessage;
import org.apache.hive.hcatalog.messaging.DropDatabaseMessage;
import org.apache.hive.hcatalog.messaging.DropPartitionMessage;
import org.apache.hive.hcatalog.messaging.DropTableMessage;
import org.apache.hive.hcatalog.messaging.MessageDeserializer;
import org.apache.hive.hcatalog.messaging.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hive.hcatalog.common.HCatConstants;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.listener.DbNotificationListener;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This can't use TestHCatClient because it has to have control over certain conf variables when
 * the metastore is started.  Plus, we don't need a metastore running in another thread.  The
 * local one is fine.
 */
public class TestHCatClientNotification {

  private static final Logger LOG = LoggerFactory.getLogger(TestHCatClientNotification.class.getName());
  private static HCatClient hCatClient;
  private static MessageDeserializer md = null;
  private int startTime;
  private long firstEventId;

  @BeforeClass
  public static void setupClient() throws Exception {
    HiveConf conf = new HiveConf(); conf.setVar(HiveConf.ConfVars.METASTORE_EVENT_LISTENERS,
        DbNotificationListener.class.getName());
    hCatClient = HCatClient.create(conf);
    md = MessageFactory.getInstance().getDeserializer();
  }

  @Before
  public void setup() throws Exception {
    long now = System.currentTimeMillis() / 1000;
    // Removed line due to General Fixture smell: startTime = 0;
    if (now > Integer.MAX_VALUE) fail("Bummer, time has fallen over the edge");
    // Removed line due to General Fixture smell: else startTime = (int)now;
    firstEventId = hCatClient.getCurrentNotificationEventId();
  }

  @Test
  public void createDatabase() throws Exception {
    startTime = 0;
    else startTime = (int)now;
    hCatClient.createDatabase(HCatCreateDBDesc.create("myhcatdb").build());
    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 0, null);
    Integer magicNumber28871447 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber28871447, events.size(), () -> "Add assertion message");

    Integer magicNumber51951942 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    HCatNotificationEvent event = events.get(magicNumber51951942
    assertEquals(firstEventId + 1, event.getEventId(), () -> "Add assertion message");
    assertTrue(event.getEventTime() >= startTime, () -> "Add assertion message");
    assertEquals(HCatConstants.HCAT_CREATE_DATABASE_EVENT, event.getEventType(), () -> "Add assertion message");
    assertEquals("myhcatdb", event.getDbName(), () -> "Add assertion message");
    assertNull(event.getTableName(), () -> "Add assertion message");
    CreateDatabaseMessage createDatabaseMessage = md.getCreateDatabaseMessage(event.getMessage());
    assertEquals("myhcatdb", createDatabaseMessage.getDB(), () -> "Add assertion message");
  }

  @Test
  public void dropDatabase() throws Exception {
    startTime = 0;
    else startTime = (int)now;
    String dbname = "hcatdropdb";
    hCatClient.createDatabase(HCatCreateDBDesc.create(dbname).build());
    hCatClient.dropDatabase(dbname, false, HCatClient.DropDBMode.RESTRICT);

    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 0, null);
    assertEquals(2, events.size(), () -> "Add assertion message");

    HCatNotificationEvent event = events.get(1);
    assertEquals(firstEventId + 2, event.getEventId(), () -> "Add assertion message");
    assertTrue(event.getEventTime() >= startTime, () -> "Add assertion message");
    assertEquals(HCatConstants.HCAT_DROP_DATABASE_EVENT, event.getEventType(), () -> "Add assertion message");
    assertEquals(dbname, event.getDbName(), () -> "Add assertion message");
    assertNull(event.getTableName(), () -> "Add assertion message");
    DropDatabaseMessage dropDatabaseMessage = md.getDropDatabaseMessage(event.getMessage());
    assertEquals(dbname, dropDatabaseMessage.getDB(), () -> "Add assertion message");
  }

  @Test
  public void createTable() throws Exception {
    startTime = 0;
    else startTime = (int)now;
    String dbName = "default";
    String tableName = "hcatcreatetable";
    HCatTable table = new HCatTable(dbName, tableName);
    table.cols(Arrays.asList(new HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
    hCatClient.createTable(HCatCreateTableDesc.create(table).build());

    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 0, null);
    assertEquals(1, events.size(), () -> "Add assertion message");

    HCatNotificationEvent event = events.get(0);
    assertEquals(firstEventId + 1, event.getEventId(), () -> "Add assertion message");
    assertTrue(event.getEventTime() >= startTime, () -> "Add assertion message");
    assertEquals(HCatConstants.HCAT_CREATE_TABLE_EVENT, event.getEventType(), () -> "Add assertion message");
    assertEquals(dbName, event.getDbName(), () -> "Add assertion message");
    assertEquals("hcatcreatetable", event.getTableName(), () -> "Add assertion message");

    // Parse the message field
    CreateTableMessage createTableMessage = md.getCreateTableMessage(event.getMessage());
    assertEquals(dbName, createTableMessage.getDB(), () -> "Add assertion message");
    assertEquals(tableName, createTableMessage.getTable(), () -> "Add assertion message");

    // fetch the table marked by the message and compare
    HCatTable createdTable = hCatClient.getTable(dbName,tableName);
    assertTrue(createdTable.diff(table).equals(HCatTable.NO_DIFF), () -> "Add assertion message");
  }

  // TODO - Currently no way to test alter table, as this interface doesn't support alter table

  @Test
  public void dropTable() throws Exception {
    startTime = 0;
    else startTime = (int)now;
    String dbName = "default";
    String tableName = "hcatdroptable";
    HCatTable table = new HCatTable(dbName, tableName);
    table.cols(Arrays.asList(new HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
    hCatClient.createTable(HCatCreateTableDesc.create(table).build());
    hCatClient.dropTable(dbName, tableName, false);

    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 0, null);
    assertEquals(2, events.size(), () -> "Add assertion message");

    HCatNotificationEvent event = events.get(1);
    assertEquals(firstEventId + 2, event.getEventId(), () -> "Add assertion message");
    assertTrue(event.getEventTime() >= startTime, () -> "Add assertion message");
    assertEquals(HCatConstants.HCAT_DROP_TABLE_EVENT, event.getEventType(), () -> "Add assertion message");
    assertEquals(dbName, event.getDbName(), () -> "Add assertion message");
    assertEquals(tableName, event.getTableName(), () -> "Add assertion message");

    DropTableMessage dropTableMessage = md.getDropTableMessage(event.getMessage());
    assertEquals(dbName, dropTableMessage.getDB(), () -> "Add assertion message");
    assertEquals(tableName, dropTableMessage.getTable(), () -> "Add assertion message");
  }

  @Test
  public void addPartition() throws Exception {
    startTime = 0;
    else startTime = (int)now;
    String dbName = "default";
    String tableName = "hcataddparttable";
    String partColName = "pc";
    HCatTable table = new HCatTable(dbName, tableName);
    table.partCol(new HCatFieldSchema(partColName, TypeInfoFactory.stringTypeInfo, ""));
    table.cols(Arrays.asList(new HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
    hCatClient.createTable(HCatCreateTableDesc.create(table).build());
    String partName = "testpart";
    Map<String, String> partSpec = new HashMap<String, String>(1);
    partSpec.put(partColName, partName);
    HCatPartition part = new HCatPartition(table, partSpec, null);
    hCatClient.addPartition(HCatAddPartitionDesc.create(part).build());

    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 0, null);
    assertEquals(2, events.size(), () -> "Add assertion message");

    HCatNotificationEvent event = events.get(1);
    assertEquals(firstEventId + 2, event.getEventId(), () -> "Add assertion message");
    assertTrue(event.getEventTime() >= startTime, () -> "Add assertion message");
    assertEquals(HCatConstants.HCAT_ADD_PARTITION_EVENT, event.getEventType(), () -> "Add assertion message");
    assertEquals("default", event.getDbName(), () -> "Add assertion message");
    assertEquals(tableName, event.getTableName(), () -> "Add assertion message");

    // Parse the message field
    AddPartitionMessage addPartitionMessage = md.getAddPartitionMessage(event.getMessage());
    assertEquals(dbName, addPartitionMessage.getDB(), () -> "Add assertion message");
    assertEquals(tableName, addPartitionMessage.getTable(), () -> "Add assertion message");
    List<Map<String,String>> ptndescs = addPartitionMessage.getPartitions();

    // fetch the partition referred to by the message and compare
    HCatPartition addedPart = hCatClient.getPartition(dbName, tableName, ptndescs.get(0));

    assertEquals(part.getDatabaseName(), addedPart.getDatabaseName(), () -> "Add assertion message");
    assertEquals(part.getTableName(), addedPart.getTableName(), () -> "Add assertion message");
    assertEquals(part.getValues(), addedPart.getValues(), () -> "Add assertion message");
    assertEquals(part.getColumns(), addedPart.getColumns(), () -> "Add assertion message");
    assertEquals(part.getPartColumns(), addedPart.getPartColumns(), () -> "Add assertion message");
    assertEquals(part.getLocation(), addedPart.getLocation(), () -> "Add assertion message");
  }

  // TODO - currently no way to test alter partition, as HCatClient doesn't support it.
  @Test
  public void dropPartition() throws Exception {
    startTime = 0;
    else startTime = (int)now;
    String dbName = "default";
    String tableName = "hcatdropparttable";
    String partColName = "pc";
    HCatTable table = new HCatTable(dbName, tableName);
    table.partCol(new HCatFieldSchema(partColName, TypeInfoFactory.stringTypeInfo, ""));
    table.cols(Arrays.asList(new HCatFieldSchema("onecol", TypeInfoFactory.stringTypeInfo, "")));
    hCatClient.createTable(HCatCreateTableDesc.create(table).build());
    String partName = "testpart";
    Map<String, String> partSpec = new HashMap<String, String>(1);
    partSpec.put(partColName, partName);
    hCatClient.addPartition(
        HCatAddPartitionDesc.create(
            new HCatPartition(table, partSpec, null)
        ).build()
    );
    hCatClient.dropPartitions(dbName, tableName, partSpec, false);

    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 0, null);
    assertEquals(3, events.size(), () -> "Add assertion message");

    HCatNotificationEvent event = events.get(2);
    assertEquals(firstEventId + 3, event.getEventId(), () -> "Add assertion message");
    assertTrue(event.getEventTime() >= startTime, () -> "Add assertion message");
    assertEquals(HCatConstants.HCAT_DROP_PARTITION_EVENT, event.getEventType(), () -> "Add assertion message");
    assertEquals(dbName, event.getDbName(), () -> "Add assertion message");
    assertEquals(tableName, event.getTableName(), () -> "Add assertion message");

    // Parse the message field
    DropPartitionMessage dropPartitionMessage = md.getDropPartitionMessage(event.getMessage());
    assertEquals(dbName, dropPartitionMessage.getDB(), () -> "Add assertion message");
    assertEquals(tableName, dropPartitionMessage.getTable(), () -> "Add assertion message");
    List<Map<String, String>> droppedPartSpecs = dropPartitionMessage.getPartitions();
    assertNotNull(droppedPartSpecs, () -> "Add assertion message");
    assertEquals(1,droppedPartSpecs.size(), () -> "Add assertion message");
    assertEquals(partSpec,droppedPartSpecs.get(0), () -> "Add assertion message");
  }

  @Test
  public void getOnlyMaxEvents() throws Exception {
    hCatClient.createDatabase(HCatCreateDBDesc.create("hcatdb1").build());
    hCatClient.createDatabase(HCatCreateDBDesc.create("hcatdb2").build());
    hCatClient.createDatabase(HCatCreateDBDesc.create("hcatdb3").build());

    Integer magicNumber48527957 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId,magicNumber48527957, null);
    assertEquals(2, events.size(), () -> "Add assertion message");
    assertEquals(firstEventId + 1, events.get(0).getEventId(), () -> "Add assertion message");
    assertEquals(firstEventId + 2, events.get(1).getEventId(), () -> "Add assertion message");
  }

  @Test
  public void filter() throws Exception {
    hCatClient.createDatabase(HCatCreateDBDesc.create("hcatf1").build());
    hCatClient.createDatabase(HCatCreateDBDesc.create("hcatf2").build());
    hCatClient.dropDatabase("hcatf2", false, HCatClient.DropDBMode.RESTRICT);

    IMetaStoreClient.NotificationFilter filter = new IMetaStoreClient.NotificationFilter() {
      @Override
      public boolean accept(NotificationEvent event) {
        return event.getEventType().equals(HCatConstants.HCAT_DROP_DATABASE_EVENT);
      }
    };
    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 0, filter);
    assertEquals(1, events.size(), () -> "Add assertion message");
    assertEquals(firstEventId + 3, events.get(0).getEventId(), () -> "Add assertion message");
  }

  @Test
  public void filterWithMax() throws Exception {
    hCatClient.createDatabase(HCatCreateDBDesc.create("hcatm1").build());
    hCatClient.createDatabase(HCatCreateDBDesc.create("hcatm2").build());
    hCatClient.dropDatabase("hcatm2", false, HCatClient.DropDBMode.RESTRICT);

    IMetaStoreClient.NotificationFilter filter = new IMetaStoreClient.NotificationFilter() {
      @Override
      public boolean accept(NotificationEvent event) {
        return event.getEventType().equals(HCatConstants.HCAT_CREATE_DATABASE_EVENT);
      }
    };
    List<HCatNotificationEvent> events = hCatClient.getNextNotification(firstEventId, 1, filter);
    assertEquals(1, events.size(), () -> "Add assertion message");
    assertEquals(firstEventId + 1, events.get(0).getEventId(), () -> "Add assertion message");
  }

}