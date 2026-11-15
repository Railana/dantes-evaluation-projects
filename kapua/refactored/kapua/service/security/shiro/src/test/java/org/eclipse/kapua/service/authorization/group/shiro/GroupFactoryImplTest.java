/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class GroupFactoryImplTest {

    GroupFactoryImpl groupFactoryImpl;
    KapuaId scopeId;
    String[] names;
    Group group;
    Date createdOn, modifiedOn;

    @Before
    public void initialize() {
        groupFactoryImpl = new GroupFactoryImpl();
        // Removed line due to General Fixture smell: scopeId = KapuaId.ONE;
        // Removed line due to General Fixture smell: names = new String[]{"", "  na123)(&*^&NAME  <>", "Na-,,..,,Me name ---", "-&^454536 na___,,12 NAME name    ", "! 2#@ na     meNEMA 2323", "12&^%4   ,,,. '|<>*(", "       ,,123name;;'", "12#name--765   ,.aaa!!#$%^<> "};
        // Removed line due to General Fixture smell: group = Mockito.mock(Group.class);
        // Removed line due to General Fixture smell: createdOn = new Date();
        // Removed line due to General Fixture smell: modifiedOn = new Date();

        // Removed line due to General Fixture smell: Mockito.when(group.getName()).thenReturn("group name");
        Mockito.when(group.getDescription()).thenReturn("description");
        Mockito.when(group.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(group.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(group.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(group.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(group.getModifiedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(group.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(group.getOptlock()).thenReturn(11);
    }

    @Test
    public void newCreatorScopeIdNameParametersTest() {
        names = new String[]{"", "  na123)(&*^&NAME  <>", "Na-,,..,,Me name ---", "-&^454536 na___,,12 NAME name    ", "! 2#@ na     meNEMA 2323", "12&^%4   ,,,. '|<>*(", "       ,,123name;;'", "12#name--765   ,.aaa!!#$%^<> "};
        scopeId = KapuaId.ONE;
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        for (String name : names) {
            GroupCreator groupCreator = groupFactoryImpl.newCreator(scopeId, name);
            Assert.assertEquals("Expected and actual values should be the same.", name, groupCreator.getName());
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, groupCreator.getScopeId());
        }
    }

    @Test
    public void newCreatorNullScopeIdNameParametersTest() {
        names = new String[]{"", "  na123)(&*^&NAME  <>", "Na-,,..,,Me name ---", "-&^454536 na___,,12 NAME name    ", "! 2#@ na     meNEMA 2323", "12&^%4   ,,,. '|<>*(", "       ,,123name;;'", "12#name--765   ,.aaa!!#$%^<> "};
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        for (String name : names) {
            GroupCreator groupCreator = groupFactoryImpl.newCreator(null, name);
            Assert.assertEquals("Expected and actual values should be the same.", name, groupCreator.getName());
            Assert.assertNull("Null expected.", groupCreator.getScopeId());
        }
    }

    @Test
    public void newCreatorScopeIdNullNameParametersTest() {
        scopeId = KapuaId.ONE;
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        GroupCreator groupCreator = groupFactoryImpl.newCreator(scopeId, null);
        Assert.assertNull("Null expected.", groupCreator.getName());
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, groupCreator.getScopeId());
    }

    @Test
    public void newEntityTest() {
        scopeId = KapuaId.ONE;
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        Group group = groupFactoryImpl.newEntity(scopeId);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, group.getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        Group group = groupFactoryImpl.newEntity(null);
        Assert.assertNull("Null expected.", group.getScopeId());
    }

    @Test
    public void newListResultTest() {
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        GroupListResult groupListResult = groupFactoryImpl.newListResult();
        Assert.assertTrue("True expected.", groupListResult.isEmpty());
    }

    @Test
    public void newQueryTest() {
        scopeId = KapuaId.ONE;
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        GroupQuery groupQuery = groupFactoryImpl.newQuery(scopeId);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, groupQuery.getScopeId());
    }

    @Test
    public void newQueryNullTest() {
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        GroupQuery groupQuery = groupFactoryImpl.newQuery(null);
        Assert.assertNull("Null expected.", groupQuery.getScopeId());
    }

    @Test
    public void newCreatorTest() {
        scopeId = KapuaId.ONE;
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        GroupCreator groupCreator = groupFactoryImpl.newCreator(scopeId);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, groupCreator.getScopeId());
    }

    @Test
    public void newCreatorNullTest() {
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        GroupCreator groupCreator = groupFactoryImpl.newCreator(null);
        Assert.assertNull("Null expected.", groupCreator.getScopeId());
    }

    @Test
    public void cloneTest() {
        modifiedOn = new Date();
        createdOn = new Date();
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        Group resultGroup = groupFactoryImpl.clone(group);
        Assert.assertEquals("Expected and actual values should be the same.", "group name", resultGroup.getName());
        Assert.assertEquals("Expected and actual values should be the same.", "description", resultGroup.getDescription());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultGroup.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultGroup.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultGroup.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultGroup.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultGroup.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, resultGroup.getModifiedOn());
        Integer magicNumber43123318 = 11; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber43123318, resultGroup.getOptlock());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        group = Mockito.mock(Group.class);
        Mockito.when(group.getName()).thenReturn("group name");
        groupFactoryImpl.clone(null);
    }
}