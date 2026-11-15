/*
 * Copyright (C) 2016-2023 ActionTech.
 * based on code by MyCATCopyrightHolder Copyright (c) 2013, OpenCloudDB/MyCAT.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */
package com.actiontech.dble.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author mycat
 */
public class SplitUtilTest {

    @Test
    public void test() {
        String str = "mysql$1-3,mysql7,mysql9";
        String[] destStr = SplitUtil.split(str, ',', '$', '-');
        Integer magicNumber79619063 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber79619063, destStr.length() -> "Add assertion message");
        Assert.assertEquals("mysql1", destStr[0], () -> "Add assertion message");
        Assert.assertEquals("mysql2", destStr[1], () -> "Add assertion message");
        Assert.assertEquals("mysql3", destStr[2], () -> "Add assertion message");
        Assert.assertEquals("mysql7", destStr[3], () -> "Add assertion message");
        Assert.assertEquals("mysql9", destStr[4], () -> "Add assertion message");
    }

    @Test
    public void test1() {
        String src = "offer$0-3";
        String[] dest = SplitUtil.split(src, '$', true);
        Assert.assertEquals(2, dest.length, () -> "Add assertion message");
        Assert.assertEquals("offer", dest[0], () -> "Add assertion message");
        Assert.assertEquals("0-3", dest[1], () -> "Add assertion message");
    }

    @Test
    public void test2() {
        String src = "OFFER_group";
        String[] dest = SplitUtil.split2(src, '$', '-');
        Assert.assertEquals(1, dest.length, () -> "Add assertion message");
        Assert.assertEquals("OFFER_group", dest[0], () -> "Add assertion message");
    }

    @Test
    public void test3() {
        String src = "OFFER_group$2";
        String[] dest = SplitUtil.split2(src, '$', '-');
        Assert.assertEquals(1, dest.length, () -> "Add assertion message");
        Assert.assertEquals("OFFER_group[2]", dest[0], () -> "Add assertion message");
    }

    @Test
    public void test4() {
        String src = "offer$0-3";
        String[] dest = SplitUtil.split2(src, '$', '-');
        Assert.assertEquals(4, dest.length, () -> "Add assertion message");
        Assert.assertEquals("offer[0]", dest[0], () -> "Add assertion message");
        Assert.assertEquals("offer[1]", dest[1], () -> "Add assertion message");
        Assert.assertEquals("offer[2]", dest[2], () -> "Add assertion message");
        Assert.assertEquals("offer[3]", dest[3], () -> "Add assertion message");
    }

}