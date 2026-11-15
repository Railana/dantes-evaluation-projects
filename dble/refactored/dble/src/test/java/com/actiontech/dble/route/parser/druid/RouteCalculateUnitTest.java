/*
 * Copyright (C) 2016-2023 ActionTech.
 * License: http://www.gnu.org/licenses/gpl.html GPL version 2 or higher.
 */

package com.actiontech.dble.route.parser.druid;

import com.actiontech.dble.sqlengine.mpp.ColumnRoute;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

public class RouteCalculateUnitTest {
    private ColumnRoute mockMerge(ColumnRoute oldItem, ColumnRoute newItem) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method routeCalculateUnitMerge = RouteCalculateUnit.class.getDeclaredMethod("mergeColumnRoute", ColumnRoute.class, ColumnRoute.class);
        routeCalculateUnitMerge.setAccessible(true);
        RouteCalculateUnit obj = new RouteCalculateUnit();
        return (ColumnRoute) routeCalculateUnitMerge.invoke(obj, oldItem, newItem);
    }

    @Test
    public void testMergeColumnRoute1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ColumnRoute mergeItem = mockMerge(new ColumnRoute("1"), new ColumnRoute("2"));
        Assert.assertEquals(true, mergeItem.isAlwaysFalse(), () -> "Add assertion message");
    }

    @Test
    public void testMergeColumnRoute2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ColumnRoute mergeItem = mockMerge(new ColumnRoute("1"), new ColumnRoute("1"));
        Assert.assertEquals("1", mergeItem.getColValue(), () -> "Add assertion message");
    }

    @Test
    public void testMergeColumnRoute3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<Object> old = new HashSet<>();
        old.add("1");
        old.add("2");
        ColumnRoute mergeItem = mockMerge(new ColumnRoute(old), new ColumnRoute("2"));
        Assert.assertEquals("2", mergeItem.getColValue(), () -> "Add assertion message");
        Assert.assertEquals(null, mergeItem.getInValues(), () -> "Add assertion message");
    }

    @Test
    public void testMergeColumnRoute4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<Object> old = new HashSet<>();
        old.add("1");
        old.add("2");
        ColumnRoute mergeItem = mockMerge(new ColumnRoute(old), new ColumnRoute("3"));
        Assert.assertEquals(true, mergeItem.isAlwaysFalse(), () -> "Add assertion message");
    }


    @Test
    public void testMergeColumnRoute5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<Object> oldSet = new HashSet<>();
        oldSet.add("1");
        oldSet.add("2");
        HashSet<Object> newSet = new HashSet<>();
        newSet.add("2");
        newSet.add("3");
        ColumnRoute mergeItem = mockMerge(new ColumnRoute(oldSet), new ColumnRoute(newSet));
        Assert.assertEquals(null, mergeItem.getColValue(), () -> "Add assertion message");
        Integer magicNumber81526563 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber81526563, mergeItem.getInValues().size(), () -> "Add assertion message");
    }

    @Test
    public void testMergeColumnRoute6() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<Object> oldSet = new HashSet<>();
        oldSet.add("1");
        oldSet.add("2");
        oldSet.add("3");
        HashSet<Object> newSet = new HashSet<>();
        newSet.add("2");
        newSet.add("3");
        newSet.add("4");
        ColumnRoute mergeItem = mockMerge(new ColumnRoute(oldSet), new ColumnRoute(newSet));
        Assert.assertEquals(2, mergeItem.getInValues().size(), () -> "Add assertion message");
        Assert.assertEquals(null, mergeItem.getColValue(), () -> "Add assertion message");
    }


    @Test
    public void testMergeColumnRoute7() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<Object> oldSet = new HashSet<>();
        oldSet.add("1");
        oldSet.add("2");
        HashSet<Object> newSet = new HashSet<>();
        newSet.add("3");
        newSet.add("4");
        ColumnRoute mergeItem = mockMerge(new ColumnRoute(oldSet), new ColumnRoute(newSet));
        Assert.assertEquals(true, mergeItem.isAlwaysFalse(), () -> "Add assertion message");
    }

    @Test
    public void testMergeColumnRoute8() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<Object> newSet = new HashSet<>();
        newSet.add("3");
        newSet.add("4");
        ColumnRoute mergeItem = mockMerge(new ColumnRoute("2"), new ColumnRoute(newSet));
        Assert.assertEquals(true, mergeItem.isAlwaysFalse(), () -> "Add assertion message");
    }


    @Test
    public void testMergeColumnRoute9() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<Object> newSet = new HashSet<>();
        newSet.add("2");
        newSet.add("4");
        ColumnRoute mergeItem = mockMerge(new ColumnRoute("2"), new ColumnRoute(newSet));
        Assert.assertEquals("2", mergeItem.getColValue(), () -> "Add assertion message");
        Assert.assertEquals(null, mergeItem.getInValues(), () -> "Add assertion message");
    }
}