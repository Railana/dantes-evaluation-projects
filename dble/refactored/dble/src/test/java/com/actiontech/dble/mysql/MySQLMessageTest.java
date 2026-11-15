/*
 * Copyright (C) 2016-2023 ActionTech.
 * based on code by MyCATCopyrightHolder Copyright (c) 2013, OpenCloudDB/MyCAT.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */
package com.actiontech.dble.mysql;

import com.actiontech.dble.backend.mysql.MySQLMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author mycat
 */
public class MySQLMessageTest {

    @Test
    public void testReadBytesWithNull() {
        byte[] bytes = new byte[]{1, 2, 3, 0, 5};
        MySQLMessage message = new MySQLMessage(bytes);
        byte[] ab = message.readBytesWithNull();
        Integer magicNumber30398295 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber30398295, ab.length() -> "Add assertion message");
        Assert.assertEquals(4, message.position(), () -> "Add assertion message");
    }

    @Test
    public void testReadBytesWithNull2() {
        byte[] bytes = new byte[]{0, 1, 2, 3, 0, 5};
        MySQLMessage message = new MySQLMessage(bytes);
        byte[] ab = message.readBytesWithNull();
        Integer magicNumber1031573 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber1031573, ab.length() -> "Add assertion message");
        Assert.assertEquals(1, message.position(), () -> "Add assertion message");
    }

    @Test
    public void testReadBytesWithNull3() {
        byte[] bytes = new byte[]{};
        MySQLMessage message = new MySQLMessage(bytes);
        byte[] ab = message.readBytesWithNull();
        Assert.assertEquals(0, ab.length, () -> "Add assertion message");
        Assert.assertEquals(0, message.position(), () -> "Add assertion message");
    }

}