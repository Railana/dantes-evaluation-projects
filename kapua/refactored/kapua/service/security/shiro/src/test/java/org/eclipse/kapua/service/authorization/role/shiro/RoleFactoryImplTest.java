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
package org.eclipse.kapua.service.authorization.role.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleQuery;
import org.eclipse.kapua.service.authorization.role.RoleListResult;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class RoleFactoryImplTest {

    RoleFactoryImpl roleFactoryImpl;
    KapuaId scopeId;
    Role role;
    Date createdOn, modifiedOn;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: roleFactoryImpl = new RoleFactoryImpl();
        // Removed line due to General Fixture smell: scopeId = KapuaId.ONE;
        // Removed line due to General Fixture smell: createdOn = new Date();
        // Removed line due to General Fixture smell: modifiedOn = new Date();
        // Removed line due to General Fixture smell: role = Mockito.mock(Role.class);

        // Removed line due to General Fixture smell: Mockito.when(role.getName()).thenReturn("role name");
        // Removed line due to General Fixture smell: Mockito.when(role.getDescription()).thenReturn("role description");
        Mockito.when(role.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(role.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(role.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(role.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(role.getModifiedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(role.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(role.getOptlock()).thenReturn(11);
    }

    @Test
    public void newEntityTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        scopeId = KapuaId.ONE;
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newEntity(scopeId) instanceof Role);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, roleFactoryImpl.newEntity(scopeId).getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newEntity(null) instanceof Role);
        Assert.assertNull("Null expected.", roleFactoryImpl.newEntity(null).getScopeId());
    }

    @Test
    public void newCreatorTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        scopeId = KapuaId.ONE;
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newCreator(scopeId) instanceof RoleCreator);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, roleFactoryImpl.newCreator(scopeId).getScopeId());
    }

    @Test
    public void newCreatorNullTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newCreator(null) instanceof RoleCreator);
        Assert.assertNull("Null expected.", roleFactoryImpl.newCreator(null).getScopeId());
    }

    @Test
    public void newQueryTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        scopeId = KapuaId.ONE;
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newQuery(scopeId) instanceof RoleQuery);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, roleFactoryImpl.newQuery(scopeId).getScopeId());
    }

    @Test
    public void newQueryNullTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newQuery(null) instanceof RoleQuery);
        Assert.assertNull("Null expected.", roleFactoryImpl.newQuery(null).getScopeId());
    }

    @Test
    public void newListResultTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newListResult() instanceof RoleListResult);
        Assert.assertTrue("True expected.", roleFactoryImpl.newListResult().isEmpty());
    }

    @Test
    public void newRolePermissionTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Assert.assertTrue("True expected.", roleFactoryImpl.newRolePermission() instanceof RolePermission);
    }

    @Test
    public void cloneTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        modifiedOn = new Date();
        createdOn = new Date();
        Role resultRole = roleFactoryImpl.clone(role);

        Assert.assertEquals("Expected and actual values should be the same.", "role name", resultRole.getName());
        Assert.assertEquals("Expected and actual values should be the same.", "role description", resultRole.getDescription());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultRole.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultRole.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultRole.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultRole.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultRole.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, resultRole.getModifiedOn());
        Integer magicNumber23460379 = 11; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber23460379, resultRole.getOptlock());
    }

    @Test
    public void cloneNullTest() {
        roleFactoryImpl = new RoleFactoryImpl();
        role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        assertThrows(Exception e,
        		() -> roleFactoryImpl.clone(null));
    }
}