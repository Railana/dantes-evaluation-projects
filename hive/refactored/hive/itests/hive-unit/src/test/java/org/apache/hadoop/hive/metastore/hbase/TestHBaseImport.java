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

import org.apache.hadoop.hive.metastore.api.SQLForeignKey;
import org.apache.hadoop.hive.metastore.api.SQLPrimaryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.hive.metastore.ObjectStore;
import org.apache.hadoop.hive.metastore.RawStore;
import org.apache.hadoop.hive.metastore.TestObjectStore;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.FunctionType;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.ResourceType;
import org.apache.hadoop.hive.metastore.api.ResourceUri;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test that import from an RDBMS based metastore works
 */
public class TestHBaseImport extends HBaseIntegrationTests {

  private static final Logger LOG = LoggerFactory.getLogger(TestHBaseImport.class.getName());

  private static final String[] tableNames = new String[] {"allnonparttable", "allparttable"};
  private static final String[] partVals = new String[] {"na", "emea", "latam", "apac"};
  private static final String[] funcNames = new String[] {"allfunc1", "allfunc2"};
  private static final String[] indexNames = new String[] {"allindex1", "allindex2"};
  private static final String[] pkNames = new String[] {"allnonparttable_pk", "allparttable_pk"};
  private static final String[] fkNames = new String[] {"", "allparttable_fk"};

  private static final List<Integer> masterKeySeqs = new ArrayList<Integer>();
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @BeforeClass
  public static void startup() throws Exception {
    HBaseIntegrationTests.startMiniCluster();
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);
    TestObjectStore.dropAllStoreObjects(rdbms);
  }

  @AfterClass
  public static void shutdown() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);
    TestObjectStore.dropAllStoreObjects(rdbms);
    for (int seq : masterKeySeqs) {
      rdbms.removeMasterKey(seq);
    }
    HBaseIntegrationTests.shutdownMiniCluster();
  }

  @Before
  public void setup() throws IOException {
    setupConnection();
    setupHBaseStore();
  }

  @Test
  public void importAll() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"alldb1", "alldb2"};
    String[] roles = new String[] {"allrole1", "allrole2"};
    String[] tokenIds = new String[] {"alltokenid1", "alltokenid2"};
    String[] tokens = new String[] {"alltoken1", "alltoken2"};
    String[] masterKeys = new String[] {"allmk1", "allmk2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now);

    int baseNumRoles = store.listRoleNames() == null ? 0 : store.listRoleNames().size();
    int baseNumDbs = store.getAllDatabases() == null ? 0 : store.getAllDatabases().size();

    HBaseImport importer = new HBaseImport("-a");
    importer.setConnections(rdbms, store);
    importer.run();

    for (int i = 0; i < roles.length; i++) {
      Role role = store.getRole(roles[i]);
      Assert.assertNotNull(role, () -> "Add assertion message");
      Assert.assertEquals(roles[i], role.getRoleName(), () -> "Add assertion message");
    }
    // Make sure there aren't any extra roles
    Assert.assertEquals(baseNumRoles + 2, store.listRoleNames().size(), () -> "Add assertion message");

    for (int i = 0; i < dbNames.length; i++) {
      Database db = store.getDatabase(dbNames[i]);
      Assert.assertNotNull(db, () -> "Add assertion message");
      // check one random value in the db rather than every value
      Assert.assertEquals("file:/tmp", db.getLocationUri(), () -> "Add assertion message");

      Table table = store.getTable(db.getName(), tableNames[0]);
      Assert.assertNotNull(table, () -> "Add assertion message");
      Assert.assertEquals(now, table.getLastAccessTime(), () -> "Add assertion message");
      Assert.assertEquals("input", table.getSd().getInputFormat(), () -> "Add assertion message");

      table = store.getTable(db.getName(), tableNames[1]);
      // Line removed due to Duplicate Assert: Assert.assertNotNull(table, () -> "Add assertion message");

      for (int j = 0; j < partVals.length; j++) {
        Partition part = store.getPartition(dbNames[i], tableNames[1], Arrays.asList(partVals[j]));
        Assert.assertNotNull(part, () -> "Add assertion message");
        Assert.assertEquals("file:/tmp/region=" + partVals[j], part.getSd().getLocation(), () -> "Add assertion message");
      }

      Integer magicNumber87918334 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      Assert.assertEquals(magicNumber87918334, store.getPartitions(dbNames[i], tableNames[1], -1).size(), () -> "Add assertion message");
      // Including two index table
      Assert.assertEquals(4, store.getAllTables(dbNames[i]).size(), () -> "Add assertion message");

      Integer magicNumber18585917 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      Assert.assertEquals(magicNumber18585917, store.getIndexes(dbNames[i], tableNames[0], -1).size(), () -> "Add assertion message");
      Integer magicNumber2880082 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      Assert.assertEquals(magicNumber2880082, store.getIndexes(dbNames[i], tableNames[1], -1).size(), () -> "Add assertion message");

      Assert.assertEquals(2, store.getFunctions(dbNames[i], "*").size(), () -> "Add assertion message");
      for (int j = 0; j < funcNames.length; j++) {
        Assert.assertNotNull(store.getFunction(dbNames[i], funcNames[j]), () -> "Add assertion message");
      }
    }

    Assert.assertEquals(baseNumDbs + 2, store.getAllDatabases().size(), () -> "Add assertion message");

    // I can't test total number of tokens or master keys because the import grabs all and copies
    // them, which means it grabs the ones imported by importSecurity test (if it's already run).
    // Depending on it already running would make the tests order dependent, which junit doesn't
    // guarantee.
    for (int i = 0; i < tokenIds.length; i++) {
      Assert.assertEquals(tokens[i], store.getToken(tokenIds[i]), () -> "Add assertion message");
    }
    String[] hbaseKeys = store.getMasterKeys();
    Set<String> keys = new HashSet<>(Arrays.asList(hbaseKeys));
    for (int i = 0; i < masterKeys.length; i++) {
      Assert.assertTrue(keys.contains(masterKeys[i]), () -> "Add assertion message");
    }
  }

  @Test
  public void importOneDb() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"onedbdb1", "onedbdb2"};
    String[] roles = new String[] {"onedbrole1", "onedbrole2"};
    String[] tokenIds = new String[] {"onedbtokenid1", "onedbtokenid2"};
    String[] tokens = new String[] {"onedbtoken1", "onedbtoken2"};
    String[] masterKeys = new String[] {"onedbmk1", "onedbmk2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now);

    int baseNumRoles = store.listRoleNames() == null ? 0 : store.listRoleNames().size();
    int baseNumDbs = store.getAllDatabases() == null ? 0 : store.getAllDatabases().size();
    int baseNumToks = store.getAllTokenIdentifiers() == null ? 0 :
        store.getAllTokenIdentifiers().size();
    int baseNumKeys =  store.getMasterKeys() == null ? 0 : store.getMasterKeys().length;

    HBaseImport importer = new HBaseImport("-d", dbNames[0]);
    importer.setConnections(rdbms, store);
    importer.run();

    // Make sure there aren't any extra roles
    Assert.assertEquals(baseNumRoles, store.listRoleNames().size(), () -> "Add assertion message");

    Database db = store.getDatabase(dbNames[0]);
    Assert.assertNotNull(db, () -> "Add assertion message");
    // check one random value in the db rather than every value
    Assert.assertEquals("file:/tmp", db.getLocationUri(), () -> "Add assertion message");

    Table table = store.getTable(db.getName(), tableNames[0]);
    Assert.assertNotNull(table, () -> "Add assertion message");
    Assert.assertEquals(now, table.getLastAccessTime(), () -> "Add assertion message");
    Assert.assertEquals("input", table.getSd().getInputFormat(), () -> "Add assertion message");

    table = store.getTable(db.getName(), tableNames[1]);
    // Line removed due to Duplicate Assert: Assert.assertNotNull(table, () -> "Add assertion message");

    for (int j = 0; j < partVals.length; j++) {
      Partition part = store.getPartition(dbNames[0], tableNames[1], Arrays.asList(partVals[j]));
      Assert.assertNotNull(part, () -> "Add assertion message");
      Assert.assertEquals("file:/tmp/region=" + partVals[j], part.getSd().getLocation(), () -> "Add assertion message");
    }

    Assert.assertEquals(4, store.getPartitions(dbNames[0], tableNames[1], -1).size(), () -> "Add assertion message");
    // Including two index table
    Integer magicNumber24884034 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber24884034, store.getAllTables(dbNames[0]).size(), () -> "Add assertion message");

    Assert.assertEquals(2, store.getIndexes(dbNames[0], tableNames[0], -1).size(), () -> "Add assertion message");
    Assert.assertEquals(0, store.getIndexes(dbNames[0], tableNames[1], -1).size(), () -> "Add assertion message");

    Assert.assertEquals(2, store.getFunctions(dbNames[0], "*").size(), () -> "Add assertion message");
    for (int j = 0; j < funcNames.length; j++) {
      Assert.assertNotNull(store.getFunction(dbNames[0], funcNames[j]), () -> "Add assertion message");
    }

    Assert.assertEquals(baseNumDbs + 1, store.getAllDatabases().size(), () -> "Add assertion message");

    Assert.assertEquals(baseNumToks, store.getAllTokenIdentifiers().size(), () -> "Add assertion message");
    String[] hbaseKeys = store.getMasterKeys();
    Assert.assertEquals(baseNumKeys, hbaseKeys.length, () -> "Add assertion message");

    // Have to do this last as it will throw an exception
    thrown.expect(NoSuchObjectException.class);
    store.getDatabase(dbNames[1]);
  }

  @Test
  public void importOneFunc() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"onefuncdb1", "onefuncdb2"};
    String[] roles = new String[] {"onefuncrole1", "onefuncrole2"};
    String[] tokenIds = new String[] {"onefunctokenid1", "onefunctokenid2"};
    String[] tokens = new String[] {"onefunctoken1", "onefunctoken2"};
    String[] masterKeys = new String[] {"onefuncmk1", "onefuncmk2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now);

    int baseNumRoles = store.listRoleNames() == null ? 0 : store.listRoleNames().size();
    int baseNumDbs = store.getAllDatabases() == null ? 0 : store.getAllDatabases().size();
    int baseNumToks = store.getAllTokenIdentifiers() == null ? 0 :
        store.getAllTokenIdentifiers().size();
    int baseNumKeys =  store.getMasterKeys() == null ? 0 : store.getMasterKeys().length;

    // Create the database so I can put the function in it.
    store.createDatabase(
        new Database(dbNames[0], "no description", "file:/tmp", emptyParameters));

    HBaseImport importer = new HBaseImport("-f", dbNames[0] + "." + funcNames[0]);
    importer.setConnections(rdbms, store);
    importer.run();

    // Make sure there aren't any extra roles
    Assert.assertEquals(baseNumRoles, store.listRoleNames().size(), () -> "Add assertion message");

    Database db = store.getDatabase(dbNames[0]);
    Assert.assertNotNull(db, () -> "Add assertion message");

    Assert.assertEquals(0, store.getAllTables(dbNames[0]).size(), () -> "Add assertion message");
    Integer magicNumber50475279 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber50475279, store.getFunctions(dbNames[0], "*").size(), () -> "Add assertion message");
    Assert.assertNotNull(store.getFunction(dbNames[0], funcNames[0]), () -> "Add assertion message");
    Assert.assertNull(store.getFunction(dbNames[0], funcNames[1]), () -> "Add assertion message");

    Assert.assertEquals(baseNumDbs + 1, store.getAllDatabases().size(), () -> "Add assertion message");

    Assert.assertEquals(baseNumToks, store.getAllTokenIdentifiers().size(), () -> "Add assertion message");
    String[] hbaseKeys = store.getMasterKeys();
    Assert.assertEquals(baseNumKeys, hbaseKeys.length, () -> "Add assertion message");
  }

  @Test
  public void importOneTableNonPartitioned() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"onetabdb1", "onetabdb2"};
    String[] roles = new String[] {"onetabrole1", "onetabrole2"};
    String[] tokenIds = new String[] {"onetabtokenid1", "onetabtokenid2"};
    String[] tokens = new String[] {"onetabtoken1", "onetabtoken2"};
    String[] masterKeys = new String[] {"onetabmk1", "onetabmk2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now);

    int baseNumRoles = store.listRoleNames() == null ? 0 : store.listRoleNames().size();
    int baseNumDbs = store.getAllDatabases() == null ? 0 : store.getAllDatabases().size();
    int baseNumToks = store.getAllTokenIdentifiers() == null ? 0 :
        store.getAllTokenIdentifiers().size();
    int baseNumKeys =  store.getMasterKeys() == null ? 0 : store.getMasterKeys().length;

    // Create the database so I can put the table in it.
    store.createDatabase(
        new Database(dbNames[0], "no description", "file:/tmp", emptyParameters));

    HBaseImport importer = new HBaseImport("-t", dbNames[0] + "." + tableNames[0]);
    importer.setConnections(rdbms, store);
    importer.run();

    // Make sure there aren't any extra roles
    Assert.assertEquals(baseNumRoles, store.listRoleNames().size(), () -> "Add assertion message");

    Database db = store.getDatabase(dbNames[0]);
    Assert.assertNotNull(db, () -> "Add assertion message");

    Table table = store.getTable(db.getName(), tableNames[0]);
    Assert.assertNotNull(table, () -> "Add assertion message");
    Assert.assertEquals(1, store.getAllTables(db.getName()).size(), () -> "Add assertion message");
    Assert.assertNull(store.getTable(db.getName(), tableNames[1]), () -> "Add assertion message");

    List<Index> indexes = store.getIndexes(db.getName(), tableNames[0], -1);
    Assert.assertEquals(2, indexes.size(), () -> "Add assertion message");

    Assert.assertEquals(0, store.getFunctions(dbNames[0], "*").size(), () -> "Add assertion message");
    Assert.assertEquals(baseNumDbs + 1, store.getAllDatabases().size(), () -> "Add assertion message");

    Assert.assertEquals(baseNumToks, store.getAllTokenIdentifiers().size(), () -> "Add assertion message");
    String[] hbaseKeys = store.getMasterKeys();
    Assert.assertEquals(baseNumKeys, hbaseKeys.length, () -> "Add assertion message");
  }

  @Test
  public void importTablesWithConstraints() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"onetabwcdb1", "onetabwcdb2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, dbNames, now, true);

    // Create the database so I can put the table in it.
    store.createDatabase(
        new Database(dbNames[0], "no description", "file:/tmp", emptyParameters));

    HBaseImport importer = new HBaseImport("-d", dbNames[0]);
    importer.setConnections(rdbms, store);
    importer.run();

    Database db = store.getDatabase(dbNames[0]);
    Assert.assertNotNull(db, () -> "Add assertion message");

    Table table = store.getTable(db.getName(), tableNames[1]);
    Assert.assertNotNull(table, () -> "Add assertion message");

    List<SQLPrimaryKey> pk = store.getPrimaryKeys(dbNames[0], tableNames[1]);
    Assert.assertNotNull(pk, () -> "Add assertion message");
    Assert.assertEquals(1, pk.size(), () -> "Add assertion message");
    Assert.assertEquals(dbNames[0], pk.get(0).getTable_db(), () -> "Add assertion message");
    Assert.assertEquals(tableNames[1], pk.get(0).getTable_name(), () -> "Add assertion message");
    Assert.assertEquals(0, pk.get(0).getKey_seq(), () -> "Add assertion message");
    Assert.assertEquals("col1", pk.get(0).getColumn_name(), () -> "Add assertion message");
    Assert.assertEquals(dbNames[0] + "_" + pkNames[1], pk.get(0).getPk_name(), () -> "Add assertion message");
    Assert.assertTrue(pk.get(0).isEnable_cstr(), () -> "Add assertion message");
    Assert.assertFalse(pk.get(0).isValidate_cstr(), () -> "Add assertion message");
    Assert.assertTrue(pk.get(0).isRely_cstr(), () -> "Add assertion message");

    List<SQLForeignKey> fk =
        store.getForeignKeys(dbNames[0], tableNames[0], dbNames[0], tableNames[1]);
    Assert.assertNotNull(fk, () -> "Add assertion message");
    Assert.assertEquals(1, fk.size(), () -> "Add assertion message");
    Assert.assertEquals(dbNames[0], fk.get(0).getPktable_db(), () -> "Add assertion message");
    Assert.assertEquals(tableNames[0], fk.get(0).getPktable_name(), () -> "Add assertion message");
    Assert.assertEquals("col1", fk.get(0).getPkcolumn_name(), () -> "Add assertion message");
    Assert.assertEquals(dbNames[0], fk.get(0).getFktable_db(), () -> "Add assertion message");
    Assert.assertEquals(tableNames[1], fk.get(0).getFktable_name(), () -> "Add assertion message");
    Assert.assertEquals("col1", fk.get(0).getFkcolumn_name(), () -> "Add assertion message");
    Assert.assertEquals(0, fk.get(0).getKey_seq(), () -> "Add assertion message");
    Assert.assertEquals(1, fk.get(0).getUpdate_rule(), () -> "Add assertion message");
    Assert.assertEquals(2, fk.get(0).getDelete_rule(), () -> "Add assertion message");
    Assert.assertEquals(dbNames[0] + "_" + fkNames[1], fk.get(0).getFk_name(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: Assert.assertTrue(pk.get(0).isEnable_cstr(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: Assert.assertFalse(pk.get(0).isValidate_cstr(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: Assert.assertTrue(pk.get(0).isRely_cstr(), () -> "Add assertion message");


  }

  @Test
  public void importOneTablePartitioned() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"onetabpartdb1", "onetabpartodb2"};
    String[] roles = new String[] {"onetabpartorole1", "onetabpartorole2"};
    String[] tokenIds = new String[] {"onetabpartotokenid1", "onetabpartotokenid2"};
    String[] tokens = new String[] {"onetabpartotoken1", "onetabpartotoken2"};
    String[] masterKeys = new String[] {"onetabpartomk1", "onetabpartomk2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now);

    int baseNumRoles = store.listRoleNames() == null ? 0 : store.listRoleNames().size();
    int baseNumDbs = store.getAllDatabases() == null ? 0 : store.getAllDatabases().size();
    int baseNumToks = store.getAllTokenIdentifiers() == null ? 0 :
        store.getAllTokenIdentifiers().size();
    int baseNumKeys =  store.getMasterKeys() == null ? 0 : store.getMasterKeys().length;

    // Create the database so I can put the table in it.
    store.createDatabase(
        new Database(dbNames[0], "no description", "file:/tmp", emptyParameters));

    HBaseImport importer = new HBaseImport("-t", dbNames[0] + "." + tableNames[1]);
    importer.setConnections(rdbms, store);
    importer.run();

    // Make sure there aren't any extra roles
    Assert.assertEquals(baseNumRoles, store.listRoleNames().size(), () -> "Add assertion message");

    Database db = store.getDatabase(dbNames[0]);
    Assert.assertNotNull(db, () -> "Add assertion message");

    Table table = store.getTable(db.getName(), tableNames[1]);
    Assert.assertNotNull(table, () -> "Add assertion message");
    Assert.assertEquals(1, store.getAllTables(db.getName()).size(), () -> "Add assertion message");

    for (int j = 0; j < partVals.length; j++) {
      Partition part = store.getPartition(dbNames[0], tableNames[1], Arrays.asList(partVals[j]));
      Assert.assertNotNull(part, () -> "Add assertion message");
      Assert.assertEquals("file:/tmp/region=" + partVals[j], part.getSd().getLocation(), () -> "Add assertion message");
    }
    Integer magicNumber21383351 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber21383351, store.getPartitions(dbNames[0], tableNames[1], -1).size(), () -> "Add assertion message");

    Assert.assertNull(store.getTable(db.getName(), tableNames[0]), () -> "Add assertion message");

    List<Index> indexes = store.getIndexes(db.getName(), tableNames[1], -1);
    Assert.assertEquals(0, indexes.size(), () -> "Add assertion message");

    Assert.assertEquals(0, store.getFunctions(dbNames[0], "*").size(), () -> "Add assertion message");
    Assert.assertEquals(baseNumDbs + 1, store.getAllDatabases().size(), () -> "Add assertion message");

    Assert.assertEquals(baseNumToks, store.getAllTokenIdentifiers().size(), () -> "Add assertion message");
    String[] hbaseKeys = store.getMasterKeys();
    Assert.assertEquals(baseNumKeys, hbaseKeys.length, () -> "Add assertion message");
  }

  @Test
  public void importSecurity() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"securitydb1", "securitydb2"};
    String[] roles = new String[] {"securityrole1", "securityrole2"};
    String[] tokenIds = new String[] {"securitytokenid1", "securitytokenid2"};
    String[] tokens = new String[] {"securitytoken1", "securitytoken2"};
    String[] masterKeys = new String[] {"securitymk1", "securitymk2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now);

    int baseNumRoles = store.listRoleNames() == null ? 0 : store.listRoleNames().size();
    int baseNumDbs = store.getAllDatabases() == null ? 0 : store.getAllDatabases().size();

    HBaseImport importer = new HBaseImport("-k");
    importer.setConnections(rdbms, store);
    importer.run();

    Assert.assertEquals(baseNumRoles, store.listRoleNames().size(), () -> "Add assertion message");

    Assert.assertEquals(baseNumDbs, store.getAllDatabases().size(), () -> "Add assertion message");

    // I can't test total number of tokens or master keys because the import grabs all and copies
    // them, which means it grabs the ones imported by importAll test (if it's already run).
    // Depending on it already running would make the tests order dependent, which junit doesn't
    // guarantee.
    for (int i = 0; i < tokenIds.length; i++) {
      Assert.assertEquals(tokens[i], store.getToken(tokenIds[i]), () -> "Add assertion message");
    }
    String[] hbaseKeys = store.getMasterKeys();
    Set<String> keys = new HashSet<>(Arrays.asList(hbaseKeys));
    for (int i = 0; i < masterKeys.length; i++) {
      Assert.assertTrue(keys.contains(masterKeys[i]), () -> "Add assertion message");
    }
  }

  // TODO test for bogus function name
  // TODO test for bogus table name
  // TODO test for non-existent items

  @Test
  public void importOneRole() throws Exception {
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"oneroledb1", "oneroledb2"};
    String[] roles = new String[] {"onerolerole1", "onerolerole2"};
    String[] tokenIds = new String[] {"oneroletokenid1", "oneroletokenid2"};
    String[] tokens = new String[] {"oneroletoken1", "oneroletoken2"};
    String[] masterKeys = new String[] {"onerolemk1", "onerolemk2"};
    int now = (int)System.currentTimeMillis() / 1000;

    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now);

    int baseNumRoles = store.listRoleNames() == null ? 0 : store.listRoleNames().size();
    int baseNumDbs = store.getAllDatabases() == null ? 0 : store.getAllDatabases().size();
    int baseNumToks = store.getAllTokenIdentifiers() == null ? 0 :
        store.getAllTokenIdentifiers().size();
    int baseNumKeys =  store.getMasterKeys() == null ? 0 : store.getMasterKeys().length;

    HBaseImport importer = new HBaseImport("-r", roles[0]);
    importer.setConnections(rdbms, store);
    importer.run();

    Role role = store.getRole(roles[0]);
    Assert.assertNotNull(role, () -> "Add assertion message");
    Assert.assertEquals(roles[0], role.getRoleName(), () -> "Add assertion message");

    // Make sure there aren't any extra roles
    Assert.assertEquals(baseNumRoles + 1, store.listRoleNames().size(), () -> "Add assertion message");
    Assert.assertEquals(baseNumDbs, store.getAllDatabases().size(), () -> "Add assertion message");

    Assert.assertEquals(baseNumToks, store.getAllTokenIdentifiers().size(), () -> "Add assertion message");
    String[] hbaseKeys = store.getMasterKeys();
    Assert.assertEquals(baseNumKeys, hbaseKeys.length, () -> "Add assertion message");

    // Have to do this last as it will throw an exception
    thrown.expect(NoSuchObjectException.class);
    store.getRole(roles[1]);
  }

  private void setupObjectStore(RawStore rdbms, String[] roles, String[] dbNames,
                                String[] tokenIds, String[] tokens, String[] masterKeys, int now)
      throws MetaException, InvalidObjectException, NoSuchObjectException {
    setupObjectStore(rdbms, roles, dbNames, tokenIds, tokens, masterKeys, now, false);
  }

  private void setupObjectStore(RawStore rdbms, String[] dbNames, int now,
                                boolean putConstraintsOnTables)
      throws MetaException, InvalidObjectException, NoSuchObjectException {
    setupObjectStore(rdbms, null, dbNames, null, null, null, now, putConstraintsOnTables);
  }

  private void setupObjectStore(RawStore rdbms, String[] roles, String[] dbNames,
                                String[] tokenIds, String[] tokens, String[] masterKeys, int now,
                                boolean putConstraintsOnTables)
      throws MetaException, InvalidObjectException, NoSuchObjectException {
    if (roles != null) {
      for (int i = 0; i < roles.length; i++) {
        rdbms.addRole(roles[i], "me");
      }
    }

    for (int i = 0; i < dbNames.length; i++) {
      rdbms.createDatabase(
          new Database(dbNames[i], "no description", "file:/tmp", emptyParameters));

      List<FieldSchema> cols = new ArrayList<>();
      cols.add(new FieldSchema("col1", "int", "nocomment"));
      SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
      StorageDescriptor sd = new StorageDescriptor(cols, "file:/tmp", "input", "output", false, 0,
          serde, null, null, emptyParameters);
      rdbms.createTable(new Table(tableNames[0], dbNames[i], "me", now, now, 0, sd, null,
          emptyParameters, null, null, null));
      if (putConstraintsOnTables) {
        rdbms.addPrimaryKeys(Collections.singletonList(
            new SQLPrimaryKey(dbNames[i], tableNames[0], "col1", 0, dbNames[i] + "_" + pkNames[0],
                true, false, true)
        ));
      }

      List<FieldSchema> partCols = new ArrayList<>();
      partCols.add(new FieldSchema("region", "string", ""));
      rdbms.createTable(new Table(tableNames[1], dbNames[i], "me", now, now, 0, sd, partCols,
          emptyParameters, null, null, null));
      if (putConstraintsOnTables) {
        rdbms.addPrimaryKeys(Arrays.asList(
            new SQLPrimaryKey(dbNames[i], tableNames[1], "col1", 0, dbNames[i] + "_" + pkNames[1],
                true, false, true)
        ));
        rdbms.addForeignKeys(Collections.singletonList(
            new SQLForeignKey(dbNames[i], tableNames[0], "col1", dbNames[i], tableNames[1],
                "col1", 0, 1, 2, dbNames[i] + "_" + fkNames[1], dbNames[i] + "_" + pkNames[0],
                true, false, true)
        ));

      }

      for (int j = 0; j < partVals.length; j++) {
        StorageDescriptor psd = new StorageDescriptor(sd);
        psd.setLocation("file:/tmp/region=" + partVals[j]);
        Partition part = new Partition(Arrays.asList(partVals[j]), dbNames[i], tableNames[1],
            now, now, psd, emptyParameters);
        rdbms.addPartition(part);
      }

      for (String funcName : funcNames) {
        LOG.debug("Creating new function " + dbNames[i] + "." + funcName);
        rdbms.createFunction(new Function(funcName, dbNames[i], "classname", "ownername",
            PrincipalType.USER, (int) System.currentTimeMillis() / 1000, FunctionType.JAVA,
            Arrays.asList(new ResourceUri(ResourceType.JAR, "uri"))));
      }

      for (String indexName : indexNames) {
        LOG.debug("Creating new index " + dbNames[i] + "." + tableNames[0] + "." + indexName);
        String indexTableName = tableNames[0] + "__" + indexName + "__";
        rdbms.createTable(new Table(indexTableName, dbNames[i], "me", now, now, 0, sd, partCols,
            emptyParameters, null, null, null));
        rdbms.addIndex(new Index(indexName, null, dbNames[i], tableNames[0],
            now, now, indexTableName, sd, emptyParameters, false));
      }
    }
    if (tokenIds != null) {
      for (int i = 0; i < tokenIds.length; i++) rdbms.addToken(tokenIds[i], tokens[i]);
    }
    if (masterKeys != null) {
      for (int i = 0; i < masterKeys.length; i++) {
        masterKeySeqs.add(rdbms.addMasterKey(masterKeys[i]));
      }
    }
  }

  @Test
  public void parallel() throws Exception {
    int parallelFactor = 10;
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"paralleldb1"};
    int now = (int)System.currentTimeMillis() / 1000;

    for (int i = 0; i < dbNames.length; i++) {
      rdbms.createDatabase(
          new Database(dbNames[i], "no description", "file:/tmp", emptyParameters));

      List<FieldSchema> cols = new ArrayList<>();
      cols.add(new FieldSchema("col1", "int", "nocomment"));
      SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
      StorageDescriptor sd = new StorageDescriptor(cols, "file:/tmp", "input", "output", false, 0,
          serde, null, null, emptyParameters);

      List<FieldSchema> partCols = new ArrayList<>();
      partCols.add(new FieldSchema("region", "string", ""));
      for (int j = 0; j < parallelFactor; j++) {
        rdbms.createTable(new Table("t" + j, dbNames[i], "me", now, now, 0, sd, partCols,
            emptyParameters, null, null, null));
        for (int k = 0; k < parallelFactor; k++) {
          StorageDescriptor psd = new StorageDescriptor(sd);
          psd.setLocation("file:/tmp/region=" + k);
          Partition part = new Partition(Arrays.asList("p" + k), dbNames[i], "t" + j,
              now, now, psd, emptyParameters);
          rdbms.addPartition(part);
        }
      }
    }

    HBaseImport importer = new HBaseImport("-p", "2", "-b", "2", "-d", dbNames[0]);
    importer.setConnections(rdbms, store);
    importer.run();

    for (int i = 0; i < dbNames.length; i++) {
      Database db = store.getDatabase(dbNames[i]);
      Assert.assertNotNull(db, () -> "Add assertion message");

      for (int j = 0; j < parallelFactor; j++) {
        Table table = store.getTable(db.getName(), "t" + j);
        Assert.assertNotNull(table, () -> "Add assertion message");
        Assert.assertEquals(now, table.getLastAccessTime(), () -> "Add assertion message");
        Assert.assertEquals("input", table.getSd().getInputFormat(), () -> "Add assertion message");

        for (int k = 0; k < parallelFactor; k++) {
          Partition part =
              store.getPartition(dbNames[i], "t" + j, Arrays.asList("p" + k));
          Assert.assertNotNull(part, () -> "Add assertion message");
          Assert.assertEquals("file:/tmp/region=" + k, part.getSd().getLocation(), () -> "Add assertion message");
        }

        Assert.assertEquals(parallelFactor, store.getPartitions(dbNames[i], "t" + j, -1).size(), () -> "Add assertion message");
      }
      Assert.assertEquals(parallelFactor, store.getAllTables(dbNames[i]).size(), () -> "Add assertion message");

    }
  }

  // Same as the test above except we create 9 of everything instead of 10.  This is important
  // because in using a batch size of 2 the previous test guarantees 10 /2 =5 , meaning we'll
  // have 5 writes on the partition queue with exactly 2 entries.  In this test we'll handle the
  // case where the last entry in the queue has fewer partitions.
  @Test
  public void parallelOdd() throws Exception {
    int parallelFactor = 9;
    RawStore rdbms;
    rdbms = new ObjectStore();
    rdbms.setConf(conf);

    String[] dbNames = new String[] {"oddparalleldb1"};
    int now = (int)System.currentTimeMillis() / 1000;

    for (int i = 0; i < dbNames.length; i++) {
      rdbms.createDatabase(
          new Database(dbNames[i], "no description", "file:/tmp", emptyParameters));

      List<FieldSchema> cols = new ArrayList<>();
      cols.add(new FieldSchema("col1", "int", "nocomment"));
      SerDeInfo serde = new SerDeInfo("serde", "seriallib", null);
      StorageDescriptor sd = new StorageDescriptor(cols, "file:/tmp", "input", "output", false, 0,
          serde, null, null, emptyParameters);

      List<FieldSchema> partCols = new ArrayList<>();
      partCols.add(new FieldSchema("region", "string", ""));
      for (int j = 0; j < parallelFactor; j++) {
        rdbms.createTable(new Table("t" + j, dbNames[i], "me", now, now, 0, sd, partCols,
            emptyParameters, null, null, null));
        for (int k = 0; k < parallelFactor; k++) {
          StorageDescriptor psd = new StorageDescriptor(sd);
          psd.setLocation("file:/tmp/region=" + k);
          Partition part = new Partition(Arrays.asList("p" + k), dbNames[i], "t" + j,
              now, now, psd, emptyParameters);
          rdbms.addPartition(part);
        }
      }
    }

    HBaseImport importer = new HBaseImport("-p", "2", "-b", "2", "-d", dbNames[0]);
    importer.setConnections(rdbms, store);
    importer.run();

    for (int i = 0; i < dbNames.length; i++) {
      Database db = store.getDatabase(dbNames[i]);
      Assert.assertNotNull(db, () -> "Add assertion message");

      for (int j = 0; j < parallelFactor; j++) {
        Table table = store.getTable(db.getName(), "t" + j);
        Assert.assertNotNull(table, () -> "Add assertion message");
        Assert.assertEquals(now, table.getLastAccessTime(), () -> "Add assertion message");
        Assert.assertEquals("input", table.getSd().getInputFormat(), () -> "Add assertion message");

        for (int k = 0; k < parallelFactor; k++) {
          Partition part =
              store.getPartition(dbNames[i], "t" + j, Arrays.asList("p" + k));
          Assert.assertNotNull(part, () -> "Add assertion message");
          Assert.assertEquals("file:/tmp/region=" + k, part.getSd().getLocation(), () -> "Add assertion message");
        }

        Assert.assertEquals(parallelFactor, store.getPartitions(dbNames[i], "t" + j, -1).size(), () -> "Add assertion message");
      }
      Assert.assertEquals(parallelFactor, store.getAllTables(dbNames[i]).size(), () -> "Add assertion message");

    }
  }
}