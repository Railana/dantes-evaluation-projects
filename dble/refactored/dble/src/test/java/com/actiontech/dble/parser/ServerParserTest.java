/*
 * Copyright (C) 2016-2023 ActionTech.
 * based on code by MyCATCopyrightHolder Copyright (c) 2013, OpenCloudDB/MyCAT.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */
package com.actiontech.dble.parser;

import com.actiontech.dble.server.parser.ServerParse;
import com.actiontech.dble.server.parser.ServerParseFactory;
import com.actiontech.dble.server.parser.ServerParseSelect;
import com.actiontech.dble.server.parser.ServerParseShow;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author mycat
 */
public class ServerParserTest {

    ServerParse serverParse = ServerParseFactory.getShardingParser();

    @Test
    public void testIsBegin() {
        Assert.assertEquals(ServerParse.BEGIN, serverParse.parse("begin"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.BEGIN, serverParse.parse("BEGIN"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.BEGIN, serverParse.parse("BegIn"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse("BegIn X"), () -> "Add assertion message");
    }

    @Test
    public void testIsCommit() {
        Assert.assertEquals(ServerParse.COMMIT, serverParse.parse("commit"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.COMMIT, serverParse.parse("COMMIT"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.COMMIT, serverParse.parse("cOmmiT "), () -> "Add assertion message");
    }


    @Test
    public void testComment() {
        Assert.assertEquals(ServerParse.MYSQL_CMD_COMMENT, serverParse.parse("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.MYSQL_CMD_COMMENT, serverParse.parse("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.MYSQL_CMD_COMMENT, serverParse.parse("/*!40101 SET @saved_cs_client     = @@character_set_client */"), () -> "Add assertion message");

        Assert.assertEquals(ServerParse.MYSQL_COMMENT, serverParse.parse("/*SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.MYSQL_COMMENT, serverParse.parse("/*SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.MYSQL_COMMENT, serverParse.parse("/*SET @saved_cs_client     = @@character_set_client */"), () -> "Add assertion message");
    }

    @Test
    public void testHintComment() {
        Assert.assertEquals(ServerParse.SELECT, 0xff & serverParse.parse("/*#dble:sharding=DN1*/SELECT ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.UPDATE, 0xff & serverParse.parse("/*#dble: sharding = DN1 */ UPDATE ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.DELETE, 0xff & serverParse.parse("/*#dble: sql = SELECT id FROM user */ DELETE ..."), () -> "Add assertion message");
    }

    @Test
    public void testOldHintComment() {
        Assert.assertEquals(ServerParse.SELECT, 0xff & serverParse.parse("/*!dble:sharding=DN1*/SELECT ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.UPDATE, 0xff & serverParse.parse("/*!dble: sharding = DN1 */ UPDATE ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.DELETE, 0xff & serverParse.parse("/*!dble: sql = SELECT id FROM user */ DELETE ..."), () -> "Add assertion message");
    }

    @Test
    public void testDoubleDashComment() {
        Assert.assertEquals(ServerParse.MYSQL_COMMENT, serverParse.parse("--     "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.MYSQL_COMMENT, serverParse.parse("--\t    "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse("- \n"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.MYSQL_COMMENT, serverParse.parse("--         select * from test"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.INSERT, serverParse.parse("-- select *\n-- fdfadsfad\ninsert into test values(1)"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.INSERT, serverParse.parse("--\tselect *\n-- fdfadsfad\ninsert into test values(1)"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse("-- select *\nfdfadsfad\ninsert into test values(1)"), () -> "Add assertion message");
    }

    @Test
    public void testIsDelete() {
        Assert.assertEquals(ServerParse.DELETE, serverParse.parse("delete ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.DELETE, serverParse.parse("DELETE ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.DELETE, serverParse.parse("DeletE ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsInsert() {
        Assert.assertEquals(ServerParse.INSERT, serverParse.parse("insert ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.INSERT, serverParse.parse("INSERT ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.INSERT, serverParse.parse("InserT ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsReplace() {
        Assert.assertEquals(ServerParse.REPLACE, serverParse.parse("replace ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.REPLACE, serverParse.parse("REPLACE ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.REPLACE, serverParse.parse("rEPLACe ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsRollback() {
        Assert.assertEquals(ServerParse.ROLLBACK, serverParse.parse("rollback"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.ROLLBACK, serverParse.parse("rollback  work   /* dble_dest_expect:M */"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.ROLLBACK, serverParse.parse("ROLLBACK"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.ROLLBACK, serverParse.parse("rolLBACK "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse("rolLBACK @@xxxx"), () -> "Add assertion message");
    }

    @Test
    public void testIsSelect() {
        Assert.assertEquals(ServerParse.SELECT, 0xff & serverParse.parse("select ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SELECT, 0xff & serverParse.parse("SELECT ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SELECT, 0xff & serverParse.parse("sELECt ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsSet() {
        Assert.assertEquals(ServerParse.SET, 0xff & serverParse.parse("set ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SET, 0xff & serverParse.parse("SET ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SET, 0xff & serverParse.parse("sEt ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsShow() {
        Assert.assertEquals(ServerParse.SHOW, 0xff & serverParse.parse("show ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SHOW, 0xff & serverParse.parse("SHOW ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SHOW, 0xff & serverParse.parse("sHOw ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsStart() {
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse("start ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse("START ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse("stART ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsUpdate() {
        Assert.assertEquals(ServerParse.UPDATE, serverParse.parse("update ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.UPDATE, serverParse.parse("UPDATE ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.UPDATE, serverParse.parse("UPDate ..."), () -> "Add assertion message");
    }

    @Test
    public void testIsShowDatabases() {
        Assert.assertEquals(ServerParseShow.DATABASES, ServerParseShow.parse("show databases", 4), () -> "Add assertion message");
        Assert.assertEquals(ServerParseShow.DATABASES, ServerParseShow.parse("SHOW DATABASES", 4), () -> "Add assertion message");
        Assert.assertEquals(ServerParseShow.DATABASES, ServerParseShow.parse("SHOW databases ", 4), () -> "Add assertion message");
        //"show database " statement need stricter inspection #1961
        Assert.assertNotEquals(ServerParseShow.DATABASES, ServerParseShow.parse("show databases ...", 4));
        Assert.assertNotEquals(ServerParseShow.DATABASES, ServerParseShow.parse("show databases...", 4));
        Assert.assertNotEquals(ServerParseShow.DATABASES, ServerParseShow.parse("show databases like... ", 4));
        Assert.assertNotEquals(ServerParseShow.DATABASES, ServerParseShow.parse("show databases where...", 4));
        Assert.assertEquals(ServerParseShow.DATABASES, ServerParseShow.parse("show databases like ...", 4), () -> "Add assertion message");
        Assert.assertEquals(ServerParseShow.DATABASES, ServerParseShow.parse("show databases where ...", 4), () -> "Add assertion message");
    }

    @Test
    public void testIsShowOther() {
        Assert.assertEquals(ServerParseShow.OTHER, ServerParseShow.parse("show ...", 4), () -> "Add assertion message");
        Assert.assertEquals(ServerParseShow.OTHER, ServerParseShow.parse("SHOW ...", 4), () -> "Add assertion message");
        Assert.assertEquals(ServerParseShow.OTHER, ServerParseShow.parse("SHOW ... ", 4), () -> "Add assertion message");
    }

    @Test
    public void testIsKill() {
        Assert.assertEquals(ServerParse.KILL, 0xff & serverParse.parse(" kill  ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.KILL, 0xff & serverParse.parse("kill 111111 ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.KILL, 0xff & serverParse.parse("KILL  1335505632"), () -> "Add assertion message");
    }

    @Test
    public void testIsKillQuery() {
        Assert.assertEquals(ServerParse.KILL_QUERY, 0xff & serverParse.parse(" kill query ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.KILL_QUERY, 0xff & serverParse.parse("kill   query 111111 ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.KILL_QUERY, 0xff & serverParse.parse("KILL QUERY 1335505632"), () -> "Add assertion message");
    }

    @Test
    public void testIsSavepoint() {
        Assert.assertEquals(ServerParse.SAVEPOINT, serverParse.parse(" savepoint  ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SAVEPOINT, serverParse.parse("SAVEPOINT "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.SAVEPOINT, serverParse.parse(" SAVEpoint   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsUse() {
        Assert.assertEquals(ServerParse.USE, 0xff & serverParse.parse(" use  ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.USE, 0xff & serverParse.parse("USE "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.USE, 0xff & serverParse.parse(" Use   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsStartTransaction() {
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse(" start transeeee "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.OTHER, serverParse.parse(" start transaction  ..."), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.START_TRANSACTION, serverParse.parse("START TRANSACTION   "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.START_TRANSACTION, serverParse.parse(" staRT   TRANSaction  "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.START_TRANSACTION, serverParse.parse(" start transaction    /*!adfadfasdf*/  "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.UNSUPPORT, serverParse.parse(" start transaction read   "), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.UNSUPPORT, serverParse.parse(" start transaction read  asdads  "), () -> "Add assertion message");
        Assert.assertEquals(true, serverParse.parse(" start transaction readxasdads  ") != ServerParse.START_TRANSACTION, () -> "Add assertion message");
        Assert.assertEquals(true, serverParse.parse(" start transactionread ") != ServerParse.START_TRANSACTION, () -> "Add assertion message");
    }

    @Test
    public void testIsSelectVersionComment() {
        Assert.assertEquals(ServerParseSelect.VERSION_COMMENT,
                ServerParseSelect.parse(" select @@version_comment  ", 7), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.VERSION_COMMENT, ServerParseSelect.parse("SELECT @@VERSION_COMMENT", 6), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.VERSION_COMMENT,
                ServerParseSelect.parse(" selECT    @@VERSION_comment  ", 7), () -> "Add assertion message");
    }

    @Test
    public void testIsSelectVersion() {
        Assert.assertEquals(ServerParseSelect.VERSION, ServerParseSelect.parse(" select version ()  ", 7), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.VERSION, ServerParseSelect.parse("SELECT VERSION(  )", 6), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.VERSION, ServerParseSelect.parse(" selECT    VERSION()  ", 7), () -> "Add assertion message");
    }

    @Test
    public void testIsSelectDatabase() {
        Assert.assertEquals(ServerParseSelect.DATABASE, ServerParseSelect.parse(" select database()  ", 7), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.DATABASE, ServerParseSelect.parse("SELECT DATABASE()", 7), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.DATABASE, ServerParseSelect.parse(" selECT    DATABASE()  ", 7), () -> "Add assertion message");
    }

    @Test
    public void testIsSelectUser() {
        Assert.assertEquals(ServerParseSelect.USER, ServerParseSelect.parse(" select user()  ", 7), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.USER, ServerParseSelect.parse("SELECT USER()", 6), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.USER, ServerParseSelect.parse(" selECT    USER()  ", 7), () -> "Add assertion message");
    }

    @Test
    public void testIsSelectCurrentUser() {
        Assert.assertEquals(ServerParseSelect.CURRENT_USER, ServerParseSelect.parse(" select current_user()  ", 7), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.CURRENT_USER, ServerParseSelect.parse("SELECT CURRENT_USER()", 6), () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.CURRENT_USER, ServerParseSelect.parse(" selECT    current_USER()  ", 7), () -> "Add assertion message");
    }

    @Test
    public void testIdentity() {
        String stmt = "select @@identity";
        int indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterIdentity(stmt, stmt.indexOf('i'));
        Assert.assertEquals(stmt.length(), indexAfterLastInsertIdFunc, () -> "Add assertion message");
        Assert.assertEquals(ServerParseSelect.IDENTITY, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select  @@identity as id";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.IDENTITY, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select  @@identitY  id";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.IDENTITY, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select  /*foo*/@@identitY  id";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.IDENTITY, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select/*foo*/ @@identitY  id";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.IDENTITY, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select/*foo*/ @@identitY As id";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.IDENTITY, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");

        stmt = "select  @@identity ,";
        Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select  @@identity as, ";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select  @@identity as id  , ";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select  @@identity ass id   ";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");

    }

    @Test
    public void testLastInsertId() {
        String stmt = " last_insert_iD()";
        int indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        Assert.assertEquals(stmt.length(), indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_iD ()";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.length(), indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_iD ( /**/ )";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.length(), indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_iD (  )  ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        Assert.assertEquals(stmt.lastIndexOf(')') + 1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id(  )";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.lastIndexOf(')') + 1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = "last_iNsert_id(  ) ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.lastIndexOf(')') + 1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_iD";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        Integer magicNumber75544050 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber75544050, indexAfterLastInsertIdFunc() -> "Add assertion message");
        stmt = " last_insert_i     ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(-1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_i    d ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(-1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id (     ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(-1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id(  d)     ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(-1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id(  ) d    ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.lastIndexOf(')') + 1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id(d)";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(-1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id(#\r\nd) ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(-1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id(#\n\r) ";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.lastIndexOf(')') + 1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id (#\n\r)";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.lastIndexOf(')') + 1, indexAfterLastInsertIdFunc, () -> "Add assertion message");
        stmt = " last_insert_id(#\n\r)";
        indexAfterLastInsertIdFunc = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, stmt.indexOf('l'));
        // Line removed due to Duplicate Assert: Assert.assertEquals(stmt.lastIndexOf(')') + 1, indexAfterLastInsertIdFunc, () -> "Add assertion message");

        stmt = "select last_insert_id(#\n\r)";
        Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) as id";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) as `id`";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) as 'id'";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)  id";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)  `id`";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)  'id'";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) a";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        // NOTE: this should be invalid, ignore this bug
        stmt = "select last_insert_id(#\n\r) as";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) asd";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        // NOTE: this should be invalid, ignore this bug
        stmt = "select last_insert_id(#\n\r) as 777";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        // NOTE: this should be invalid, ignore this bug
        stmt = "select last_insert_id(#\n\r)  777";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)as `77``7`";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)ass";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)as 'a'";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)as 'a\\''";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)as 'a'''";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)as 'a\"'";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "   select last_insert_id(#\n\r) As 'a\"'";
        Assert.assertEquals(ServerParseSelect.LAST_INSERT_ID, ServerParseSelect.parse(stmt, 9), () -> "Add assertion message");

        stmt = "select last_insert_id(#\n\r)as 'a\"\\'";
        Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)as `77``7` ,";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r)as `77`7`";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) as,";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) ass a";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
        stmt = "select last_insert_id(#\n\r) as 'a";
        // Line removed due to Duplicate Assert: Assert.assertEquals(ServerParseSelect.OTHER, ServerParseSelect.parse(stmt, 6), () -> "Add assertion message");
    }

    @Test
    public void testLockTable() {
        Assert.assertEquals(ServerParse.LOCK, serverParse.parse("lock tables ttt write;"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.LOCK, serverParse.parse(" lock tables ttt read;"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.LOCK, serverParse.parse("lock tables"), () -> "Add assertion message");
    }

    @Test
    public void testUnlockTable() {
        Assert.assertEquals(ServerParse.UNLOCK, serverParse.parse("unlock tables"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.UNLOCK, serverParse.parse(" unlock	 tables"), () -> "Add assertion message");
    }


    @Test
    public void testCreateView() {
        Assert.assertEquals(ServerParse.CREATE_VIEW, serverParse.parse("create view asdfasdf as asdfasdfasdfsdf"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.ALTER_VIEW, serverParse.parse("ALTER view x_xx_xx as select * from suntest"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.REPLACE_VIEW, serverParse.parse("create or replace  view x_xx_xx as select * from suntest"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.DROP_VIEW, serverParse.parse("DROP  view x_xx_xx"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.DDL, serverParse.parse("create or replace viasdfasdfew asdfasdf as asdfasdfasdfsdf"), () -> "Add assertion message");
        Assert.assertEquals(ServerParse.DDL, serverParse.parse("create   "), () -> "Add assertion message");
    }

    @Test
    public void testDropPrepare() {
        Assert.assertEquals(ServerParse.SCRIPT_PREPARE, serverParse.parse("DROP PREPARE stmt_name"), () -> "Add assertion message");
    }
}