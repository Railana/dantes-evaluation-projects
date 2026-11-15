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
package org.eclipse.kapua.integration.misc;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Date;


@Category(JUnitTests.class)
public class RolePermissionImplTest {

    KapuaId[] scopeIds;
    Permission permission1, permission2;
    RolePermissionImpl rolePermissionImpl1, rolePermissionImpl2;
    RolePermission rolePermission;
    Date createdOn;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: scopeIds = new KapuaId[]{null, KapuaId.ONE};
        // Removed line due to General Fixture smell: permission1 = Mockito.mock(Permission.class);
        // Removed line due to General Fixture smell: permission2 = Mockito.mock(PermissionImpl.class);
        // Removed line due to General Fixture smell: rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        // Removed line due to General Fixture smell: rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        // Removed line due to General Fixture smell: rolePermission = Mockito.mock(RolePermission.class);
        // Removed line due to General Fixture smell: createdOn = new Date();

        Mockito.when(rolePermission.getId()).thenReturn(KapuaId.ANY);
        Mockito.when(rolePermission.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(rolePermission.getPermission()).thenReturn(permission2);
        Mockito.when(rolePermission.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(rolePermission.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(rolePermission.getCreatedOn()).thenReturn(createdOn);
    }

    @Test
    public void rolePermissionImplWithoutParametersTest() throws Exception {
        rolePermission = Mockito.mock(RolePermission.class);
        Constructor<RolePermissionImpl> rolePermissionImpl = RolePermissionImpl.class.getDeclaredConstructor();
        rolePermissionImpl.setAccessible(true);
        rolePermissionImpl.newInstance();
        Assert.assertTrue("True expected.", Modifier.isProtected(rolePermissionImpl.getModifiers()));
    }

    @Test
    public void rolePermissionImpScopeIdTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        for (KapuaId scopeId : scopeIds) {
            RolePermissionImpl rolePermissionImpl = new RolePermissionImpl(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, rolePermissionImpl.getScopeId());
            Assert.assertEquals("Expected and actual values should be the same.", new PermissionImpl(null, null, null, null), rolePermissionImpl.getPermission());
            Assert.assertNull("Null expected.", rolePermissionImpl.getRoleId());
        }
    }

    @Test
    public void rolePermissionImplScopeIdPermissionTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        permission2 = Mockito.mock(PermissionImpl.class);
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        for (KapuaId scopeId : scopeIds) {
            RolePermissionImpl rolePermissionImpl = new RolePermissionImpl(scopeId, permission2);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, rolePermissionImpl.getScopeId());
            Assert.assertEquals("Expected and actual values should be the same.", permission2, rolePermissionImpl.getPermission());
            Assert.assertNull("Null expected.", rolePermissionImpl.getRoleId());
        }
    }

    @Test
    public void rolePermissionImplScopeIdNullPermissionTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        for (KapuaId scopeId : scopeIds) {
            RolePermissionImpl rolePermissionImpl = new RolePermissionImpl(scopeId, null);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, rolePermissionImpl.getScopeId());
            Assert.assertEquals("Expected and actual values should be the same.", new PermissionImpl(null, null, null, null), rolePermissionImpl.getPermission());
        }
    }

    @Test
    public void rolePermissionImplRolePermissionTest() {
        createdOn = new Date();
        rolePermission = Mockito.mock(RolePermission.class);
        permission2 = Mockito.mock(PermissionImpl.class);
        RolePermissionImpl rolePermissionImpl = new RolePermissionImpl(rolePermission);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, rolePermissionImpl.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, rolePermissionImpl.getRoleId());
        Assert.assertEquals("Expected and actual values should be the same.", permission2, rolePermissionImpl.getPermission());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, rolePermissionImpl.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, rolePermissionImpl.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, rolePermissionImpl.getCreatedOn());
    }

    @Test(expected = NullPointerException.class)
    public void rolePermissionImplNullRolePermissionTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        new RolePermissionImpl((RolePermission) null);
    }

    @Test
    public void setAndGetRoleIdTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        permission2 = Mockito.mock(PermissionImpl.class);
        KapuaId[] roleIds = {null, KapuaId.ONE};

        RolePermissionImpl rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        RolePermissionImpl rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY, permission2);
        RolePermissionImpl rolePermissionImpl3 = new RolePermissionImpl(rolePermission);

        for (KapuaId roleId : roleIds) {
            rolePermissionImpl1.setRoleId(roleId);
            Assert.assertEquals("Expected and actual values should be the same.", roleId, rolePermissionImpl1.getRoleId());

            rolePermissionImpl2.setRoleId(roleId);
            Assert.assertEquals("Expected and actual values should be the same.", roleId, rolePermissionImpl2.getRoleId());

            rolePermissionImpl3.setRoleId(roleId);
            Assert.assertEquals("Expected and actual values should be the same.", roleId, rolePermissionImpl3.getRoleId());
        }
    }

    @Test
    public void setAndGetPermissionToStringTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        permission2 = Mockito.mock(PermissionImpl.class);
        permission1 = Mockito.mock(Permission.class);
        RolePermissionImpl rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        RolePermissionImpl rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY, permission2);
        RolePermissionImpl rolePermissionImpl3 = new RolePermissionImpl(rolePermission);
        Permission[] permissions = {null, permission1, permission2};
        Permission[] expectedPermissions = {new PermissionImpl(null, null, null, null), new PermissionImpl(null, null, null, null), permission2};

        for (int i = 0; i < permissions.length; i++) {
            rolePermissionImpl1.setPermission(permissions[i]);
            Assert.assertEquals("Expected and actual values should be the same.", expectedPermissions[i], rolePermissionImpl1.getPermission());
            Assert.assertEquals("Expected and actual values should be the same.", expectedPermissions[i].toString(), rolePermissionImpl1.toString());

            rolePermissionImpl2.setPermission(permissions[i]);
            Assert.assertEquals("Expected and actual values should be the same.", expectedPermissions[i].toString(), rolePermissionImpl2.toString());

            rolePermissionImpl3.setPermission(permissions[i]);
            Assert.assertEquals("Expected and actual values should be the same.", expectedPermissions[i].toString(), rolePermissionImpl3.toString());
        }

    }

    @Test
    public void hashCodeNullRoleIdNullPermissionTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        Integer magicNumber80814954 = 961; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber80814954, rolePermissionImpl1.hashCode());
    }

    @Test
    public void hashCodeNullPermissionTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        rolePermissionImpl1.setRoleId(KapuaId.ONE);

        Assert.assertEquals("Expected and actual values should be the same.", 993, rolePermissionImpl1.hashCode());
    }

    @Test
    public void hashCodeNullRoleIdTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        permission1 = Mockito.mock(Permission.class);
        rolePermissionImpl1.setPermission(permission1);

        Assert.assertEquals("Expected and actual values should be the same.", 28630112, rolePermissionImpl1.hashCode());
    }

    @Test
    public void hashCodeTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        rolePermissionImpl1.setRoleId(KapuaId.ONE);
        rolePermissionImpl1.setPermission(Mockito.mock(Permission.class));

        Assert.assertEquals("Expected and actual values should be the same.", 28630144, rolePermissionImpl1.hashCode());
    }

    @Test
    public void equalsSameObjectTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        Assert.assertTrue("True expected.", rolePermissionImpl1.equals(rolePermissionImpl1));
    }

    @Test
    public void equalsNullObjectTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        Assert.assertFalse("False expected.", rolePermissionImpl1.equals(null));
    }

    @Test
    public void equalsObjectTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        Assert.assertFalse("False expected.", rolePermissionImpl1.equals(new Object()));
    }

    @Test
    public void equalsNullBothRoleIdsTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        Assert.assertTrue("True expected.", rolePermissionImpl1.equals(rolePermissionImpl2));
    }

    @Test
    public void equalsNullBothRoleIdsDifferentPermissionsTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        permission2 = Mockito.mock(PermissionImpl.class);
        permission1 = Mockito.mock(Permission.class);
        rolePermissionImpl1.setPermission(permission1);
        rolePermissionImpl2.setPermission(permission2);

        Assert.assertFalse("False expected.", rolePermissionImpl1.equals(rolePermissionImpl2));
    }

    @Test
    public void equalsNullRoleIdTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        rolePermissionImpl2.setRoleId(KapuaId.ONE);

        Assert.assertFalse("False expected.", rolePermissionImpl1.equals(rolePermissionImpl2));
    }

    @Test
    public void equalsDifferentRoleIdsTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        rolePermissionImpl1.setRoleId(KapuaId.ONE);
        rolePermissionImpl2.setRoleId(KapuaId.ANY);

        Assert.assertFalse("False expected.", rolePermissionImpl1.equals(rolePermissionImpl2));
    }

    @Test
    public void equalsSameRoleIdsTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        rolePermissionImpl1.setRoleId(KapuaId.ONE);
        rolePermissionImpl2.setRoleId(KapuaId.ONE);

        Assert.assertTrue("True expected.", rolePermissionImpl1.equals(rolePermissionImpl2));
    }

    @Test
    public void equalsSameRoleIdsDifferentPermissionsTest() {
        rolePermission = Mockito.mock(RolePermission.class);
        rolePermissionImpl2 = new RolePermissionImpl(KapuaId.ANY);
        rolePermissionImpl1 = new RolePermissionImpl(KapuaId.ONE);
        permission2 = Mockito.mock(PermissionImpl.class);
        permission1 = Mockito.mock(Permission.class);
        rolePermissionImpl1.setRoleId(KapuaId.ONE);
        rolePermissionImpl1.setPermission(permission1);
        rolePermissionImpl2.setRoleId(KapuaId.ONE);
        rolePermissionImpl2.setPermission(permission2);

        Assert.assertFalse("False expected.", rolePermissionImpl1.equals(rolePermissionImpl2));
    }
}