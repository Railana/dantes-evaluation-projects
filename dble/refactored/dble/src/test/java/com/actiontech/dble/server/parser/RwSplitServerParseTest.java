package com.actiontech.dble.server.parser;

import org.junit.Assert;
import org.junit.Test;


public class RwSplitServerParseTest {


    ServerParse serverParse = ServerParseFactory.getRwSplitParser();

    @Test
    public void testIsGrant() {
        Assert.assertEquals(RwSplitServerParse.GRANT, 0xff & serverParse.parse(" grant  ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.GRANT, 0xff & serverParse.parse("GRANT ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.GRANT, 0xff & serverParse.parse(" Grant   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsRevoke() {
        Assert.assertEquals(RwSplitServerParse.REVOKE, 0xff & serverParse.parse(" revoke  ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.REVOKE, 0xff & serverParse.parse("REVOKE ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.REVOKE, 0xff & serverParse.parse(" Revoke   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsInstall() {
        Assert.assertEquals(RwSplitServerParse.INSTALL, 0xff & serverParse.parse(" install  ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.INSTALL, 0xff & serverParse.parse("INSTALL ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.INSTALL, 0xff & serverParse.parse(" Install   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsRename() {
        Assert.assertEquals(RwSplitServerParse.RENAME, 0xff & serverParse.parse(" rename  ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.RENAME, 0xff & serverParse.parse("RENAME ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.RENAME, 0xff & serverParse.parse(" Rename   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsUnInstall() {
        Assert.assertEquals(RwSplitServerParse.UNINSTALL, 0xff & serverParse.parse(" uninstall  ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNINSTALL, 0xff & serverParse.parse("UNINSTALL ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNINSTALL, 0xff & serverParse.parse(" Uninstall   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsStartTransaction() {
        Assert.assertEquals(RwSplitServerParse.START_TRANSACTION, 0xff & serverParse.parse("  START TRANSACTION"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.START_TRANSACTION, 0xff & serverParse.parse(" start transaction"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.START_TRANSACTION, 0xff & serverParse.parse("  Start Transaction"), () -> "Add assertion message");
    }

    @Test
    public void testIsStartSlave() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse("  START SLAVE  ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" start slave ..."), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse("  Start Slave   a"), () -> "Add assertion message");
    }

    @Test
    public void testIsAnalyze() {
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse("   ANALYZE TABLE test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse(" analyze table test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse("  Analyze Table test"), () -> "Add assertion message");
    }

    @Test
    public void testIsCache() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse("    CACHE INDEX test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" cache index test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" Cache Index test"), () -> "Add assertion message");
    }

    @Test
    public void testIsCheck() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" CHECK TABLE test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" check table test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" Check Table test"), () -> "Add assertion message");
    }

    @Test
    public void testIsFlush() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" flush"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" FLUSH"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" Flush"), () -> "Add assertion message");
    }

    @Test
    public void testIsLoadIndex() {
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse("  LOAD INDEX INTO CACHE"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse(" load index into cache"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse(" Load Index Into Cache"), () -> "Add assertion message");
    }

    @Test
    public void testIsOptimize() {
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse("   OPTIMIZE TABLE test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse(" optimize table test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse(" Optimize Table Test"), () -> "Add assertion message");
    }

    @Test
    public void testIsRepair() {
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse("  REPAIR TABLE test "), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse(" repair table test "), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.UNSUPPORT & 0xff, 0xff & serverParse.parse(" Repair Table test "), () -> "Add assertion message");
    }

    @Test
    public void testIsReset() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse("   RESET test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" reset test"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" Reset test"), () -> "Add assertion message");
    }

    @Test
    public void testIsStop() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse("   STOP SLAVE"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" stop slave"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" Stop Slave"), () -> "Add assertion message");
    }

    @Test
    public void testIsResetSlave() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse("   RESET SLAVE"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" reset slave"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" Reset Slave"), () -> "Add assertion message");
    }

    @Test
    public void testIsChange() {
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse("   CHANGE MASTER TO"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" change master to"), () -> "Add assertion message");
        Assert.assertEquals(RwSplitServerParse.OTHER & 0xff, 0xff & serverParse.parse(" Change Master to"), () -> "Add assertion message");
    }
}