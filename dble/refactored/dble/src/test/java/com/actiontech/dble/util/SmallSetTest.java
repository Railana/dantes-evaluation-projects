/*
 * Copyright (C) 2016-2023 ActionTech.
 * based on code by MyCATCopyrightHolder Copyright (c) 2013, OpenCloudDB/MyCAT.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */
package com.actiontech.dble.util;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author mycat
 */
public class SmallSetTest extends TestCase {

    public void assertListEquals(Collection<? extends Object> col, Object... objects) {
        if (objects == null) {
            Assert.assertTrue(col.isEmpty());
        }
        Assert.assertEquals(objects.length, col.size());
        int i = 0;
        for (Object o : col) {
            Assert.assertEquals(objects[i++], o);
        }
    }

    public void testSet() throws Exception {
        SmallSet<Object> sut = new SmallSet<Object>();
        sut.add(1);
        Integer magicNumber88860613 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber88860613, sut.size(), () -> "Add assertion message");
        Iterator<Object> iter = sut.iterator();
        Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        Assert.assertEquals(1, iter.next(), () -> "Add assertion message");
        Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        assertListEquals(sut, 1);
        try {
            iter.next();
            Assert.assertTrue(false, () -> "Add assertion message");
        } catch (Exception e) {
        }

        sut = new SmallSet<Object>();
        sut.add(1);
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, sut.size(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        assertListEquals(sut, 1);
        iter.remove();
        Assert.assertEquals(0, sut.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");

        sut = new SmallSet<Object>();
        sut.add(1);
        Integer magicNumber9904124 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        sut.add(magicNumber9904124
        Assert.assertEquals(2, sut.size(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        Integer magicNumber90331333 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        // Line removed due to Duplicate Assert: Assert.assertEquals(magicNumber90331333, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        Assert.assertEquals(2, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        assertListEquals(sut, 1, 2);
        iter.remove();
        assertListEquals(sut, 1);
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, sut.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");

        sut = new SmallSet<Object>();
        sut.add(1);
        sut.add(2);
        assertListEquals(sut, 1, 2);
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, sut.size(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        Integer magicNumber68163136 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        // Line removed due to Duplicate Assert: Assert.assertEquals(magicNumber68163136, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        iter.remove();
        assertListEquals(sut, 2);
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, sut.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");

        sut = new SmallSet<Object>();
        sut.add(1);
        sut.add(2);
        assertListEquals(sut, 1, 2);
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, sut.size(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        iter.remove();
        assertListEquals(sut, 2);
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, sut.size(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");

        sut = new SmallSet<Object>();
        sut.add(1);
        sut.add(2);
        assertListEquals(sut, 1, 2);
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, sut.size(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        iter.remove();
        assertListEquals(sut, 2);
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, sut.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, iter.next(), () -> "Add assertion message");
        iter.remove();
        assertListEquals(sut);
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");

        sut = new SmallSet<Object>();
        sut.add(1);
        sut.add(2);
        sut.add(3);
        Integer magicNumber59940103 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertListEquals(sut, 1, 2,magicNumber59940103
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, iter.next(), () -> "Add assertion message");
        assertListEquals(sut, 1, 2, 3);
        iter.remove();
        assertListEquals(sut, 2, 3);
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, iter.next(), () -> "Add assertion message");
        iter.remove();
        assertListEquals(sut, 3);
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        Assert.assertEquals(3, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        iter.remove();
        assertListEquals(sut);
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");

        sut = new SmallSet<Object>();
        sut.add(1);
        sut.add(2);
        Integer magicNumber3714376 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        sut.add(magicNumber3714376
        Integer magicNumber62360273 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertListEquals(sut, 1, 2,magicNumber62360273
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, iter.next(), () -> "Add assertion message");
        assertListEquals(sut, 1, 2, 3);
        iter.remove();
        Integer magicNumber96166302 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertListEquals(sut, 2,magicNumber96166302
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(3, iter.next(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        iter.remove();
        assertListEquals(sut, 2);
        // Line removed due to Duplicate Assert: Assert.assertFalse(iter.hasNext(), () -> "Add assertion message");
        iter = sut.iterator();
        // Line removed due to Duplicate Assert: Assert.assertTrue(iter.hasNext(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(2, iter.next(), () -> "Add assertion message");
    }

}