/*
 * Copyright (C) 2016-2023 ActionTech.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */

package com.actiontech.dble.cluster;

import com.actiontech.dble.backend.datasource.PhysicalDbGroup;
import com.actiontech.dble.backend.datasource.PhysicalDbInstance;
import com.actiontech.dble.backend.datasource.ShardingNode;
import com.actiontech.dble.cluster.values.RawJson;
import com.actiontech.dble.config.ConfigFileName;
import com.actiontech.dble.config.ConfigInitializer;
import com.actiontech.dble.config.converter.DBConverter;
import com.actiontech.dble.config.converter.SequenceConverter;
import com.actiontech.dble.config.converter.ShardingConverter;
import com.actiontech.dble.config.converter.UserConverter;
import com.actiontech.dble.config.model.ClusterConfig;
import com.actiontech.dble.config.model.sharding.SchemaConfig;
import com.actiontech.dble.config.model.sharding.table.*;
import com.actiontech.dble.config.model.user.*;
import com.actiontech.dble.route.function.AbstractPartitionAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ClusterHelpTest {

    UserConverter userConverter = new UserConverter();
    ShardingConverter shardingConverter = new ShardingConverter();
    RawJson sequencePropsToJson = null;

    ConfigInitializer configInitializerByJson = new ConfigInitializer(userConverter.userXmlToJson(), DBConverter.dbXmlToJson(), shardingConverter.shardingXmlToJson(), sequencePropsToJson);

    ConfigInitializer configInitializerByXml = new ConfigInitializer();

    @Before
    public void setUp() {
        if (ClusterConfig.getInstance().getSequenceHandlerType() == ClusterConfig.SEQUENCE_HANDLER_ZK_GLOBAL_INCREMENT) {
            sequencePropsToJson = SequenceConverter.sequencePropsToJson(ConfigFileName.SEQUENCE_FILE_NAME);
        } else if (ClusterConfig.getInstance().getSequenceHandlerType() == ClusterConfig.SEQUENCE_HANDLER_MYSQL) {
            sequencePropsToJson = SequenceConverter.sequencePropsToJson(ConfigFileName.SEQUENCE_DB_FILE_NAME);
        }
    }

    @Test
    public void testShardingXml() {
        Map<ERTable, Set<ERTable>> erTableSetMapByXml = configInitializerByXml.getErRelations();
        Map<String, AbstractPartitionAlgorithm> functionMapByXml = configInitializerByXml.getFunctions();
        Map<String, SchemaConfig> schemaConfigMapByXml = configInitializerByXml.getSchemas();
        Map<String, ShardingNode> shardingNodeMapByXml = configInitializerByXml.getShardingNodes();
        Map<ERTable, Set<ERTable>> erTableSetMapByJson = configInitializerByJson.getErRelations();
        Map<String, AbstractPartitionAlgorithm> functionMapByJson = configInitializerByJson.getFunctions();
        Map<String, SchemaConfig> schemaConfigMapByJson = configInitializerByJson.getSchemas();
        Map<String, ShardingNode> shardingNodeMapByJson = configInitializerByJson.getShardingNodes();

        Assert.assertEquals(erTableSetMapByXml.size(), erTableSetMapByJson.size(), () -> "Add assertion message");
        for (Map.Entry<ERTable, Set<ERTable>> erTableSetEntry : erTableSetMapByXml.entrySet()) {
            Set<ERTable> erTableJson = erTableSetMapByJson.get(erTableSetEntry.getKey());
            Assert.assertEquals(erTableJson, erTableSetEntry.getValue(), () -> "Add assertion message");
        }

        Assert.assertEquals(functionMapByXml.size(), functionMapByJson.size(), () -> "Add assertion message");
        for (Map.Entry<String, AbstractPartitionAlgorithm> algorithmEntry : functionMapByXml.entrySet()) {
            AbstractPartitionAlgorithm algorithmJson = functionMapByJson.get(algorithmEntry.getKey());
            Assert.assertEquals(algorithmJson, algorithmEntry.getValue(), () -> "Add assertion message");
        }


        Assert.assertEquals(shardingNodeMapByXml.size(), shardingNodeMapByJson.size(), () -> "Add assertion message");
        for (Map.Entry<String, ShardingNode> shardingNodeConfigEntry : shardingNodeMapByXml.entrySet()) {
            ShardingNode shardingNodeJson = shardingNodeMapByJson.get(shardingNodeConfigEntry.getKey());
            Assert.assertTrue(shardingNodeJson.equalsBaseInfo(shardingNodeConfigEntry.getValue()), () -> "Add assertion message");
        }

        Assert.assertEquals(schemaConfigMapByXml.size(), schemaConfigMapByJson.size(), () -> "Add assertion message");
        for (Map.Entry<String, SchemaConfig> schemaConfigEntry : schemaConfigMapByXml.entrySet()) {
            SchemaConfig schemaConfigJson = schemaConfigMapByJson.get(schemaConfigEntry.getKey());
            Assert.assertTrue(schemaConfigJson.equalsBaseInfo(schemaConfigEntry.getValue()), () -> "Add assertion message");
            Assert.assertEquals(schemaConfigJson.getTables().size(), schemaConfigEntry.getValue().getTables().size(), () -> "Add assertion message");
            for (Map.Entry<String, BaseTableConfig> tableConfigEntry : schemaConfigJson.getTables().entrySet()) {
                Assert.assertTrue(tableConfigEntry.getValue().equalsBaseInfo(schemaConfigEntry.getValue().getTables().get(tableConfigEntry.getKey())), () -> "Add assertion message");

                BaseTableConfig baseTableConfig = schemaConfigEntry.getValue().getTables().get(tableConfigEntry.getKey());
                if (baseTableConfig instanceof ShardingTableConfig) {
                    Assert.assertTrue(((ShardingTableConfig) tableConfigEntry.getValue()).equalsBaseInfo((ShardingTableConfig) baseTableConfig), () -> "Add assertion message");
                } else if (baseTableConfig instanceof ChildTableConfig) {
                    Assert.assertTrue(((ChildTableConfig) tableConfigEntry.getValue()).equalsBaseInfo((ChildTableConfig) baseTableConfig), () -> "Add assertion message");
                } else if (baseTableConfig instanceof GlobalTableConfig) {
                    Assert.assertTrue(((GlobalTableConfig) tableConfigEntry.getValue()).equalsBaseInfo((GlobalTableConfig) baseTableConfig), () -> "Add assertion message");
                } else {
                    Assert.assertTrue(tableConfigEntry.getValue().equalsBaseInfo(baseTableConfig), () -> "Add assertion message");
                }
            }
        }

    }

    @Test
    public void testDXml() {
        Map<String, PhysicalDbGroup> dbGroupsByXml = configInitializerByXml.getDbGroups();
        Assert.assertEquals(dbGroupsByXml.size(), configInitializerByJson.getDbGroups().size(), () -> "Add assertion message");
        for (Map.Entry<String, PhysicalDbGroup> physicalDbGroupEntry : dbGroupsByXml.entrySet()) {
            PhysicalDbGroup physicalDbGroup = configInitializerByJson.getDbGroups().get(physicalDbGroupEntry.getKey());
            Assert.assertTrue(physicalDbGroupEntry.getValue().equalsBaseInfo(physicalDbGroup), () -> "Add assertion message");
            Assert.assertEquals(physicalDbGroupEntry.getValue().getAllDbInstanceMap().size(), physicalDbGroup.getAllDbInstanceMap().size(), () -> "Add assertion message");
            for (Map.Entry<String, PhysicalDbInstance> physicalDbInstanceEntry : physicalDbGroupEntry.getValue().getAllDbInstanceMap().entrySet()) {
                Assert.assertEquals(physicalDbInstanceEntry.getValue(), physicalDbGroup.getAllDbInstanceMap().get(physicalDbInstanceEntry.getKey()), () -> "Add assertion message");
            }
        }


        for (Map.Entry<String, PhysicalDbGroup> physicalDbGroupEntry : configInitializerByJson.getDbGroups().entrySet()) {
            PhysicalDbGroup physicalDbGroup = dbGroupsByXml.get(physicalDbGroupEntry.getKey());
            // Line removed due to Duplicate Assert: Assert.assertTrue(physicalDbGroupEntry.getValue().equalsBaseInfo(physicalDbGroup), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: Assert.assertEquals(physicalDbGroupEntry.getValue().getAllDbInstanceMap().size(), physicalDbGroup.getAllDbInstanceMap().size(), () -> "Add assertion message");
            for (Map.Entry<String, PhysicalDbInstance> physicalDbInstanceEntry : physicalDbGroupEntry.getValue().getAllDbInstanceMap().entrySet()) {
                // Line removed due to Duplicate Assert: Assert.assertEquals(physicalDbInstanceEntry.getValue(), physicalDbGroup.getAllDbInstanceMap().get(physicalDbInstanceEntry.getKey()), () -> "Add assertion message");
            }
        }
    }


    @Test
    public void testUserXml() {
        Map<UserName, UserConfig> users = configInitializerByXml.getUsers();
        Map<String, Properties> blacklistConfig = configInitializerByXml.getBlacklistConfig();
        Map<UserName, UserConfig> userConfigMap = configInitializerByJson.getUsers();
        Map<String, Properties> blackListConfigMap = configInitializerByJson.getBlacklistConfig();
        Assert.assertEquals(users.size(), userConfigMap.size(), () -> "Add assertion message");
        Assert.assertEquals(blacklistConfig, blackListConfigMap, () -> "Add assertion message");

        for (Map.Entry<UserName, UserConfig> userConfigEntry : users.entrySet()) {
            UserConfig userConfig = userConfigMap.get(userConfigEntry.getKey());
            if (userConfig instanceof ShardingUserConfig) {
                Assert.assertTrue(((ShardingUserConfig) userConfigEntry.getValue()).equalsBaseInfo((ShardingUserConfig) userConfig), () -> "Add assertion message");
            } else if (userConfig instanceof RwSplitUserConfig) {
                Assert.assertTrue(((RwSplitUserConfig) userConfigEntry.getValue()).equalsBaseInfo((RwSplitUserConfig) userConfig), () -> "Add assertion message");
            } else if (userConfig instanceof ManagerUserConfig) {
                Assert.assertTrue(((ManagerUserConfig) userConfigEntry.getValue()).equalsBaseInfo((ManagerUserConfig) userConfig), () -> "Add assertion message");
            } else {
                Assert.assertTrue(userConfigEntry.getValue().equalsBaseInfo(userConfig), () -> "Add assertion message");
            }
        }
        for (Map.Entry<UserName, UserConfig> userConfigEntry : userConfigMap.entrySet()) {
            UserConfig userConfig = users.get(userConfigEntry.getKey());
            if (userConfig instanceof ShardingUserConfig) {
                // Line removed due to Duplicate Assert: Assert.assertTrue(((ShardingUserConfig) userConfigEntry.getValue()).equalsBaseInfo((ShardingUserConfig) userConfig), () -> "Add assertion message");
            } else if (userConfig instanceof RwSplitUserConfig) {
                // Line removed due to Duplicate Assert: Assert.assertTrue(((RwSplitUserConfig) userConfigEntry.getValue()).equalsBaseInfo((RwSplitUserConfig) userConfig), () -> "Add assertion message");
            } else if (userConfig instanceof ManagerUserConfig) {
                // Line removed due to Duplicate Assert: Assert.assertTrue(((ManagerUserConfig) userConfigEntry.getValue()).equalsBaseInfo((ManagerUserConfig) userConfig), () -> "Add assertion message");
            } else {
                // Line removed due to Duplicate Assert: Assert.assertTrue(userConfigEntry.getValue().equalsBaseInfo(userConfig), () -> "Add assertion message");
            }
        }

    }
}