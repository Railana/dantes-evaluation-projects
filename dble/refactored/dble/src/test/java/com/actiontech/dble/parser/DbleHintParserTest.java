/*
 * Copyright (C) 2016-2023 ActionTech.
 * based on code by MyCATCopyrightHolder Copyright (c) 2013, OpenCloudDB/MyCAT.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */
package com.actiontech.dble.parser;

import com.actiontech.dble.route.parser.DbleHintParser;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLSyntaxErrorException;

/**
 * @author mycat
 */
public class DbleHintParserTest {

    @Test
    public void testDbleHint() throws SQLSyntaxErrorException {

        DbleHintParser.HintInfo hintInfo;
        hintInfo = DbleHintParser.parse("/*!dble:sql  =   select * from sbtest */ call p_show_time()");
        Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        Assert.assertEquals(DbleHintParser.SQL, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals("select * from sbtest", hintInfo.getHintValue(), () -> "Add assertion message");
        Assert.assertEquals("call p_show_time()", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parse("/*!dble:shardingNode =dn5  */ call p_show_time()");
        // Line removed due to Duplicate Assert: Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        Assert.assertEquals(DbleHintParser.SHARDING_NODE, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals("dn5", hintInfo.getHintValue(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals("call p_show_time()", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parse("/*!dble:shardingNode =' dn5 '  */ call p_show_time()");
        // Line removed due to Duplicate Assert: Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(DbleHintParser.SHARDING_NODE, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals(" dn5 ", hintInfo.getHintValue(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals("call p_show_time()", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parse("/*!dble:db_type= master */ call p_show_time()");
        // Line removed due to Duplicate Assert: Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        Assert.assertEquals(DbleHintParser.DB_TYPE, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals("master", hintInfo.getHintValue(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals("call p_show_time()", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parse("/*!dble:db_instance_url = 127.0.0.1:3307 */ call p_show_time()");
        // Line removed due to Duplicate Assert: Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        Assert.assertEquals(DbleHintParser.DB_INSTANCE_URL, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals("127.0.0.1:3307", hintInfo.getHintValue(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals("call p_show_time()", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parse("/*!dble:db_instance_url=127.0.0.1:3307 */");
        // Line removed due to Duplicate Assert: Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(DbleHintParser.DB_INSTANCE_URL, hintInfo.getType(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals("127.0.0.1:3307", hintInfo.getHintValue(), () -> "Add assertion message");
        Assert.assertEquals("", hintInfo.getRealSql(), () -> "Add assertion message");


        hintInfo = DbleHintParser.parse("/*#dble:plan=a&b&c$inner2left$in2join */ sss");
        // Line removed due to Duplicate Assert: Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        Assert.assertEquals(DbleHintParser.PLAN, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals("a&b&c$inner2left$in2join", hintInfo.getHintValue(), () -> "Add assertion message");
        Assert.assertEquals("sss", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parse("/ * 127.0.0.1:3307 */ sss");
        Assert.assertNull(hintInfo, () -> "Add assertion message");

        assertThrows(Exception ex,
        		() -> DbleHintParser.parse("/*!dble:db_instance_url=127.0.0.1:3307 *"));

        assertThrows(Exception ex,
        		() -> DbleHintParser.parse("/*!dble:db_instance_url     */"));

        assertThrows(Exception ex,
        		() -> DbleHintParser.parse("/*#dble:plan a&b */"));


    }

    @Test
    public void testUproxyHint() throws SQLSyntaxErrorException {
        DbleHintParser.HintInfo hintInfo;
        hintInfo = DbleHintParser.parseRW("insert into test values/* master */ (11111)");
        Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        Assert.assertEquals(DbleHintParser.UPROXY_MASTER, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals("master", hintInfo.getHintValue(), () -> "Add assertion message");
        Assert.assertEquals("insert into test values/* master */ (11111)", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parseRW("insert into /* uproxy_dest: 127.0.0.1:3307 */  test values (11111)");
        // Line removed due to Duplicate Assert: Assert.assertNotNull(hintInfo, () -> "Add assertion message");
        Assert.assertEquals(DbleHintParser.UPROXY_DEST, hintInfo.getType(), () -> "Add assertion message");
        Assert.assertEquals("127.0.0.1:3307", hintInfo.getHintValue(), () -> "Add assertion message");
        Assert.assertEquals("insert into /* uproxy_dest: 127.0.0.1:3307 */  test values (11111)", hintInfo.getRealSql(), () -> "Add assertion message");

        hintInfo = DbleHintParser.parseRW("insert into /* uproxy_dest : 127.0.0.1:3307 */  test values (11111)");
        Assert.assertNull(hintInfo, () -> "Add assertion message");
    }

}