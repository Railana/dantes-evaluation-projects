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
package org.eclipse.kapua.broker.core.security;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Category(JUnitTests.class)
public class EnhModularRealmAuthorizerTest extends Assert {

    EnhModularRealmAuthorizer enhModularRealmAuthorizer1, enhModularRealmAuthorizer2;
    Collection<Realm> realms;
    Realm realm1, realm2, realm3;
    AuthorizingRealm authorizingRealm1, authorizingRealm2, authorizingRealm3;
    PrincipalCollection principals;
    List<Permission> permissions;
    Permission permission1, permission2, permission3;
    boolean[] booleanArray1, booleanArray2;
    PermissionResolver permissionResolver;
    String stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: enhModularRealmAuthorizer1 = new EnhModularRealmAuthorizer();
        // Removed line due to General Fixture smell: enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        // Removed line due to General Fixture smell: realms = new HashSet<>();
        // Removed line due to General Fixture smell: realm1 = Mockito.mock(Realm.class);
        // Removed line due to General Fixture smell: realm2 = Mockito.mock(Realm.class);
        // Removed line due to General Fixture smell: realm3 = Mockito.mock(Realm.class);
        // Removed line due to General Fixture smell: authorizingRealm1 = Mockito.mock(AuthorizingRealm.class);
        // Removed line due to General Fixture smell: authorizingRealm2 = Mockito.mock(AuthorizingRealm.class);
        // Removed line due to General Fixture smell: authorizingRealm3 = Mockito.mock(AuthorizingRealm.class);
        // Removed line due to General Fixture smell: principals = new SimplePrincipalCollection();
        // Removed line due to General Fixture smell: permissions = new LinkedList<>();
        // Removed line due to General Fixture smell: permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        // Removed line due to General Fixture smell: permission2 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        // Removed line due to General Fixture smell: permission3 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        // Removed line due to General Fixture smell: booleanArray1 = new boolean[]{true, false, true};
        // Removed line due to General Fixture smell: booleanArray2 = new boolean[]{false, true};
        // Removed line due to General Fixture smell: permissionResolver = Mockito.mock(PermissionResolver.class);
        // Removed line due to General Fixture smell: stringPermissionResolver1 = "permissionResolver1";
        // Removed line due to General Fixture smell: stringPermissionResolver2 = "permissionResolver2";
        // Removed line due to General Fixture smell: stringPermissionResolver3 = "permissionResolver3";
    }

    @Test
    public void enhModularRealmAuthorizerWithoutParameterTest() {
        enhModularRealmAuthorizer1 = new EnhModularRealmAuthorizer();
        assertNull("Null expected.", enhModularRealmAuthorizer1.getRealms());
    }

    @Test
    public void enhModularRealmAuthorizerWithEmptyRealmsTest() {
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        assertTrue("True expected.", enhModularRealmAuthorizer2.getRealms().isEmpty());
    }

    @Test
    public void enhModularRealmAuthorizerWithRealmsTest() {
        realm3 = Mockito.mock(Realm.class);
        realm2 = Mockito.mock(Realm.class);
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        assertEquals("Expected and actual values should be the same.", realms, enhModularRealmAuthorizer2.getRealms());
    }

    @Test
    public void enhModularRealmAuthorizerWithNullParameterTest() {
        EnhModularRealmAuthorizer enhModularRealmAuthorizer = new EnhModularRealmAuthorizer(null);
        assertNull("Null expected.", enhModularRealmAuthorizer.getRealms());
    }

    @Test
    public void isPermittedAuthorizerWithoutParameterTest() {
        enhModularRealmAuthorizer1 = new EnhModularRealmAuthorizer();
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        assertThrows(Exception e,
        		() -> {
        			enhModularRealmAuthorizer1.isPermitted(principals, permissions);
        			fail("Exception expected.");
        		});
    }

    @Test
    public void isPermittedAuthorizerWithNullRealmsTest() {
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(null);
        try {
            enhModularRealmAuthorizer2.isPermitted(principals, permissions);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new IllegalStateException("Configuration error:  No realms have been configured!  One or more realms must be present to execute an authorization operation.").toString(), e.toString());
        }
    }

    @Test
    public void isPermittedAuthorizerWithEmptyRealmsTest() {
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        try {
            enhModularRealmAuthorizer2.isPermitted(principals, permissions);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals("Expected and actual values should be the same.", new IllegalStateException("Configuration error:  No realms have been configured!  One or more realms must be present to execute an authorization operation.").toString(), e.toString());
        }
    }

    @Test
    public void isPermittedAuthorizerWithRealmsEmptyPermissionsTest() {
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        realm3 = Mockito.mock(Realm.class);
        realm2 = Mockito.mock(Realm.class);
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions), IsInstanceOf.instanceOf(boolean[].class));
        Integer magicNumber54031077 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals("Expected and actual values should be the same.",magicNumber54031077, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithOneRealmTest() {
        permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        permissions.add(permission1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 1, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithRealmsTest() {
        permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        realm3 = Mockito.mock(Realm.class);
        realm2 = Mockito.mock(Realm.class);
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);
        permissions.add(permission1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 1, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithSingleAuthorizingRealmTest() {
        booleanArray1 = new boolean[]{true, false, true};
        permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        authorizingRealm1 = Mockito.mock(AuthorizingRealm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        permissions.add(permission1);
        realms.add(authorizingRealm1);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithMultipleAuthorizingRealmsTest() {
        booleanArray1 = new boolean[]{true, false, true};
        permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        authorizingRealm3 = Mockito.mock(AuthorizingRealm.class);
        authorizingRealm2 = Mockito.mock(AuthorizingRealm.class);
        authorizingRealm1 = Mockito.mock(AuthorizingRealm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        permissions.add(permission1);
        realms.add(authorizingRealm1);
        realms.add(authorizingRealm2);
        realms.add(authorizingRealm3);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm2.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm3.isPermitted(principals, permissions)).thenReturn(booleanArray1);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 1, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithMultipleAuthorizingRealmsAllTrueTest() {
        booleanArray2 = new boolean[]{false, true};
        booleanArray1 = new boolean[]{true, false, true};
        permission2 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        authorizingRealm2 = Mockito.mock(AuthorizingRealm.class);
        authorizingRealm1 = Mockito.mock(AuthorizingRealm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        permissions.add(permission1);
        permissions.add(permission2);
        realms.add(authorizingRealm1);
        realms.add(authorizingRealm2);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm2.isPermitted(principals, permissions)).thenReturn(booleanArray2);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithMultipleRealmsAndAuthorizingRealmsTest() {
        booleanArray1 = new boolean[]{true, false, true};
        permission3 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permission2 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permissions = new LinkedList<>();
        principals = new SimplePrincipalCollection();
        authorizingRealm3 = Mockito.mock(AuthorizingRealm.class);
        authorizingRealm2 = Mockito.mock(AuthorizingRealm.class);
        authorizingRealm1 = Mockito.mock(AuthorizingRealm.class);
        realm2 = Mockito.mock(Realm.class);
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        permissions.add(permission1);
        permissions.add(permission2);
        permissions.add(permission3);

        realms.add(authorizingRealm1);
        realms.add(authorizingRealm2);
        realms.add(authorizingRealm3);
        realms.add(realm1);
        realms.add(realm2);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm2.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm3.isPermitted(principals, permissions)).thenReturn(booleanArray1);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerNullPermissionsTest() {
        principals = new SimplePrincipalCollection();
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        try {
            enhModularRealmAuthorizer2.isPermitted(principals, (List) null);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals("NullPointerException expected.", new NullPointerException().toString(), e.toString());
        }
    }

    @Test
    public void isPermittedAuthorizerNullPrincipalsTest() {
        permissions = new LinkedList<>();
        realm3 = Mockito.mock(Realm.class);
        realm2 = Mockito.mock(Realm.class);
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(null, permissions), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 0, enhModularRealmAuthorizer2.isPermitted(null, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerNullTest() {
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        try {
            enhModularRealmAuthorizer2.isPermitted(null, (List) null);
            fail("Exception expected.");
        } catch (Exception e) {
            assertEquals("NullPointerException expected.", new NullPointerException().toString(), e.toString());
        }
    }

    @Test
    public void isPermittedStringPermissionsTest() {
        stringPermissionResolver3 = "permissionResolver3";
        stringPermissionResolver2 = "permissionResolver2";
        stringPermissionResolver1 = "permissionResolver1";
        permissionResolver = Mockito.mock(PermissionResolver.class);
        principals = new SimplePrincipalCollection();
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(principals, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3).length);
    }

    @Test
    public void isPermittedStringPermissionsNullPrincipalsTest() {
        stringPermissionResolver3 = "permissionResolver3";
        stringPermissionResolver2 = "permissionResolver2";
        stringPermissionResolver1 = "permissionResolver1";
        permissionResolver = Mockito.mock(PermissionResolver.class);
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(null, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(null, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3).length);
    }

    @Test
    public void isPermittedOneNullStringPermissionTest() {
        stringPermissionResolver1 = "permissionResolver1";
        permissionResolver = Mockito.mock(PermissionResolver.class);
        principals = new SimplePrincipalCollection();
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, null, stringPermissionResolver1), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(principals, null, stringPermissionResolver1).length);
    }

    @Test
    public void isPermittedNullStringPermissionsTest() {
        permissionResolver = Mockito.mock(PermissionResolver.class);
        principals = new SimplePrincipalCollection();
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, null, null), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(principals, null, null).length);
    }

    @Test
    public void isPermittedStringPermissionsNullTest() {
        permissionResolver = Mockito.mock(PermissionResolver.class);
        realm1 = Mockito.mock(Realm.class);
        realms = new HashSet<>();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        assertThat("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(null, null, null), IsInstanceOf.instanceOf(boolean[].class));
        assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(null, null, null).length);
    }
}