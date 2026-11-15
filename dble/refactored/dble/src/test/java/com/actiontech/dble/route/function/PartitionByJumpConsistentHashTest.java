/*
 * Copyright (C) 2016-2023 ActionTech.
 * based on code by MyCATCopyrightHolder Copyright (c) 2013, OpenCloudDB/MyCAT.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */
package com.actiontech.dble.route.function;


import org.junit.Assert;
import org.junit.Test;


public class PartitionByJumpConsistentHashTest {

    @Test
    public void test1() {
        PartitionByJumpConsistentHash partition = new PartitionByJumpConsistentHash();
        partition.setHashSlice("0:0");
        partition.setPartitionCount(5);
        partition.init();
        Integer magicNumber33877030 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber33877030(int) partition.calculate("8"), () -> "Add assertion message");
        Assert.assertEquals(4, (int) partition.calculate("5"), () -> "Add assertion message");
    }

    @Test
    public void test2() {
        PartitionByJumpConsistentHash partition = new PartitionByJumpConsistentHash();
        partition.setHashSlice("0:0");
        partition.setPartitionCount(4);
        partition.init();
        Integer magicNumber87259945 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber87259945(int) partition.calculate("8"), () -> "Add assertion message");
        Assert.assertEquals(0, (int) partition.calculate("5"), () -> "Add assertion message");
    }

}