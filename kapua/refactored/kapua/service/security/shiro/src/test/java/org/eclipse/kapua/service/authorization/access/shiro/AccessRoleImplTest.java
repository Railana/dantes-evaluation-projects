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
package org.eclipse.kapua.service.authorization.access.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class AccessRoleImplTest {

    AccessRoleImpl accessRoleImpl1, accessRoleImpl2, accessRoleImpl;
    AccessRole accessRole;
    Date createdOn;

    @Before
    public void initialize() throws KapuaException {
        // Removed line due to General Fixture smell: accessRoleImpl1 = new AccessRoleImpl();
        // Removed line due to General Fixture smell: accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        // Removed line due to General Fixture smell: accessRole = Mockito.mock(AccessRole.class);
        // Removed line due to General Fixture smell: createdOn = new Date();

        Mockito.when(accessRole.getId()).thenReturn(KapuaId.ANY);
        Mockito.when(accessRole.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessRole.getCreatedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(accessRole.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(accessRole.getAccessInfoId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessRole.getRoleId()).thenReturn(KapuaId.ANY);

        // Removed line due to General Fixture smell: accessRoleImpl = new AccessRoleImpl(accessRole);
    }

    @Test
    public void accessRoleImplWithoutParametersTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        Assert.assertNull("Null expected.", accessRoleImpl1.getScopeId());
        Assert.assertNull("Null expected.", accessRoleImpl1.getAccessInfoId());
        Assert.assertNull("Null expected.", accessRoleImpl1.getRoleId());
    }

    @Test
    public void accessRoleImplScopeIdParameterTest() {
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessRoleImpl2.getScopeId());
        Assert.assertNull("Null expected.", accessRoleImpl2.getAccessInfoId());
        Assert.assertNull("Null expected.", accessRoleImpl2.getRoleId());
    }

    @Test
    public void accessRoleImplNullScopeIdParameterTest() {
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        AccessRoleImpl accessRoleImplNullScopeId = new AccessRoleImpl((KapuaId) null);
        Assert.assertNull("Null expected.", accessRoleImplNullScopeId.getScopeId());
        Assert.assertNull("Null expected.", accessRoleImplNullScopeId.getAccessInfoId());
        Assert.assertNull("Null expected.", accessRoleImplNullScopeId.getRoleId());
    }

    @Test
    public void accessRoleImplAccessRoleParameterTest() {
        accessRoleImpl = new AccessRoleImpl(accessRole);
        createdOn = new Date();
        accessRole = Mockito.mock(AccessRole.class);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessRoleImpl.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, accessRoleImpl.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessRoleImpl.getAccessInfoId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl.getRoleId());
    }

    @Test(expected = NullPointerException.class)
    public void accessRoleImplNullAccessRoleParameterTest() throws KapuaException {
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        new AccessRoleImpl((AccessRole) null);
    }

    @Test
    public void setAndGetAccessInfoIdTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl1.setAccessInfoId(KapuaId.ANY);
        accessRoleImpl2.setAccessInfoId(KapuaId.ANY);
        accessRoleImpl.setAccessInfoId(KapuaId.ANY);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl1.getAccessInfoId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl2.getAccessInfoId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl.getAccessInfoId());

        accessRoleImpl1.setAccessInfoId(null);
        accessRoleImpl2.setAccessInfoId(null);
        accessRoleImpl.setAccessInfoId(null);
        Assert.assertNull("Null expected.", accessRoleImpl1.getAccessInfoId());
        Assert.assertNull("Null expected.", accessRoleImpl2.getAccessInfoId());
        Assert.assertNull("Null expected.", accessRoleImpl.getAccessInfoId());
    }

    @Test
    public void setAndGetRoleIdTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl1.setRoleId(KapuaId.ANY);
        accessRoleImpl2.setRoleId(KapuaId.ANY);
        accessRoleImpl.setRoleId(KapuaId.ANY);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl1.getRoleId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl2.getRoleId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleImpl.getRoleId());

        accessRoleImpl1.setRoleId(null);
        accessRoleImpl2.setRoleId(null);
        accessRoleImpl.setRoleId(null);
        Assert.assertNull("Null expected.", accessRoleImpl1.getRoleId());
        Assert.assertNull("Null expected.", accessRoleImpl2.getRoleId());
        Assert.assertNull("Null expected.", accessRoleImpl.getRoleId());
    }

    @Test
    public void hashCodeNullAccessInfoIdNullRoleIdTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        Integer magicNumber50637531 = 961; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber50637531, accessRoleImpl1.hashCode());
    }

    @Test
    public void hashCodeNullRoleIdTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl1.setAccessInfoId(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", 1953, accessRoleImpl1.hashCode());
    }

    @Test
    public void hashCodeNullAccessInfoIdTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl1.setRoleId(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", 993, accessRoleImpl1.hashCode());
    }

    @Test
    public void hashCodeTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl1.setAccessInfoId(KapuaId.ONE);
        accessRoleImpl1.setRoleId(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", 1985, accessRoleImpl1.hashCode());
    }

    @Test
    public void equalsSameObjectTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        Assert.assertTrue("True expected.", accessRoleImpl1.equals(accessRoleImpl1));
    }

    @Test
    public void equalsNullObjectTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        Assert.assertFalse("False expected.", accessRoleImpl1.equals(null));
    }

    @Test
    public void equalsObjectTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        Assert.assertFalse("False expected.", accessRoleImpl1.equals(new Object()));
    }

    @Test
    public void equalsNullAccessInfoIdsNullRoleIdsTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        Assert.assertTrue("True expected.", accessRoleImpl1.equals(accessRoleImpl2));
    }

    @Test
    public void equalsNullThisAccessInfoIdNullRoleIdsTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl2.setAccessInfoId(KapuaId.ONE);
        Assert.assertFalse("False expected.", accessRoleImpl1.equals(accessRoleImpl2));
    }

    @Test
    public void equalsNullAccessInfoIdsNullThisRoleIdTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl2.setRoleId(KapuaId.ANY);
        Assert.assertFalse("False expected.", accessRoleImpl1.equals(accessRoleImpl2));
    }

    @Test
    public void equalsDifferentAccessInfoIdsTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl1.setAccessInfoId(KapuaId.ONE);
        accessRoleImpl1.setAccessInfoId(KapuaId.ANY);
        Assert.assertFalse("False expected.", accessRoleImpl1.equals(accessRoleImpl2));
    }

    @Test
    public void equalsEqualAccessInfoIdsNullRoleIdsTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl1.setAccessInfoId(KapuaId.ONE);
        accessRoleImpl2.setAccessInfoId(KapuaId.ONE);
        Assert.assertTrue("True expected.", accessRoleImpl1.equals(accessRoleImpl2));
    }

    @Test
    public void equalsEqualAccessInfoIdsDifferentRoleIdsTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl1.setAccessInfoId(KapuaId.ONE);
        accessRoleImpl2.setAccessInfoId(KapuaId.ONE);
        accessRoleImpl1.setRoleId(KapuaId.ANY);
        accessRoleImpl2.setRoleId(KapuaId.ONE);

        Assert.assertFalse("False expected.", accessRoleImpl1.equals(accessRoleImpl2));
    }

    @Test
    public void equalsEqualAccessInfoIdsEqualRoleIdsTest() {
        accessRoleImpl1 = new AccessRoleImpl();
        accessRoleImpl = new AccessRoleImpl(accessRole);
        accessRole = Mockito.mock(AccessRole.class);
        accessRoleImpl2 = new AccessRoleImpl(KapuaId.ONE);
        accessRoleImpl1.setAccessInfoId(KapuaId.ONE);
        accessRoleImpl2.setAccessInfoId(KapuaId.ONE);
        accessRoleImpl1.setRoleId(KapuaId.ONE);
        accessRoleImpl2.setRoleId(KapuaId.ONE);

        Assert.assertTrue("True expected.", accessRoleImpl1.equals(accessRoleImpl2));
    }
}