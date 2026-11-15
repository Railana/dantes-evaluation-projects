package com.actiontech.dble.util;

import com.actiontech.dble.services.manager.information.ManagerTableUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ManagerTableTest {

    @Test
    public void check() {
        List<String> realList = ManagerTableUtil.getTables("schema", "select * from test");
        Assert.assertEquals("schema.test", realList.get(0), () -> "Add assertion message");

        realList = ManagerTableUtil.getTables("schema", "select * from testdb.test");
        Assert.assertEquals("testdb.test", realList.get(0), () -> "Add assertion message");

        realList = ManagerTableUtil.getTables("schema", "select * from `testdb`.`test`");
        // Line removed due to Duplicate Assert: Assert.assertEquals("testdb.test", realList.get(0), () -> "Add assertion message");

        realList = ManagerTableUtil.getTables("schema", "select * from `testdb`.test inner join test1");
        // Line removed due to Duplicate Assert: Assert.assertEquals("testdb.test", realList.get(0), () -> "Add assertion message");
        Assert.assertEquals("schema.test1", realList.get(1), () -> "Add assertion message");


        realList = ManagerTableUtil.getTables("schema", "/*!dble:shardingNode=dn1*/ select n.id,s.name from sharding_2_t1 n join sharding_4_t1 s on n.id=s.id ");
        Assert.assertEquals("schema.sharding_2_t1", realList.get(0), () -> "Add assertion message");
        Assert.assertEquals("schema.sharding_4_t1", realList.get(1), () -> "Add assertion message");

        realList = ManagerTableUtil.getTables("schema", "/*#dble:sharding=DN1*/select n.id,s.name from sharding_2_t1 n join sharding_4_t1 s on n.id=s.id ");
        // Line removed due to Duplicate Assert: Assert.assertEquals("schema.sharding_2_t1", realList.get(0), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals("schema.sharding_4_t1", realList.get(1), () -> "Add assertion message");

        realList = ManagerTableUtil.getTables("schema", "/*#dble:sql = SELECT id FROM user*/select n.id,s.name from sharding_2_t1 n join sharding_4_t1 s on n.id=s.id ");
        // Line removed due to Duplicate Assert: Assert.assertEquals("schema.sharding_2_t1", realList.get(0), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals("schema.sharding_4_t1", realList.get(1), () -> "Add assertion message");

        realList = ManagerTableUtil.getTables("schema", "select n.id,s.name from sharding_2_t1 n join sharding_2_t1 s on n.id=s.id ");
        Integer magicNumber81280541 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber81280541, realList.size(), () -> "Add assertion message");
    }
}