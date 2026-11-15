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
package org.eclipse.kapua.service.authentication.shiro.realm;

import org.apache.shiro.authc.AuthenticationInfo;
import org.eclipse.kapua.plugin.sso.openid.JwtProcessor;
import org.eclipse.kapua.plugin.sso.openid.exception.OpenIDException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.shiro.JwtCredentialsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(JUnitTests.class)
public class JwtCredentialsMatcherTest {

    JwtProcessor jwtProcessor;
    JwtCredentialsImpl authenticationToken;
    AuthenticationInfo authenticationInfo;
    Credential credential;
    JwtCredentialsMatcher jwtCredentialsMatcher;

    @Before
    public void initialize() {
        jwtProcessor = Mockito.mock(JwtProcessor.class);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: jwtProcessor = Mockito.mock(JwtProcessor.class);
        // Removed line due to General Fixture smell: authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        // Removed line due to General Fixture smell: authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        // Removed line due to General Fixture smell: credential = Mockito.mock(Credential.class);
        jwtCredentialsMatcher = new JwtCredentialsMatcher(jwtProcessor);
    }

    @Test
    public void jwtCredentialsMatcherNullTest() {
        assertThrows(Exception e,
        		() -> new JwtCredentialsMatcher(null));
    }

    @Test(expected = NullPointerException.class)
    public void doCredentialsMatchNullAuthenticationTokenTest() {
        authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        jwtCredentialsMatcher.doCredentialsMatch(null, authenticationInfo);
    }

    @Test
    public void doCredentialsMatchNullAuthenticationInfoNullJwtTest() {
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        Mockito.when(authenticationToken.getIdToken()).thenReturn(null);
        Assert.assertFalse("False expected.", jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, null));
    }

    @Test(expected = NullPointerException.class)
    public void doCredentialsMatchNullAuthenticationInfoTest() {
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        Mockito.when(authenticationToken.getIdToken()).thenReturn("idToken");
        jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, null);
    }

    @Test
    public void doCredentialsMatchNullJwtTest() {
        authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        Mockito.when(authenticationToken.getIdToken()).thenReturn(null);

        Assert.assertFalse("False expected.", jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }

    @Test
    public void doCredentialsMatchStringCredentialTest() {
        credential = Mockito.mock(Credential.class);
        authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        Mockito.when(authenticationToken.getIdToken()).thenReturn("idToken");
        Mockito.when(authenticationInfo.getCredentials()).thenReturn("credential");

        Assert.assertFalse("False expected.", jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }

    @Test
    public void doCredentialsMatchDifferentKeysTest() {
        credential = Mockito.mock(Credential.class);
        authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        Mockito.when(authenticationToken.getIdToken()).thenReturn("idToken");
        Mockito.when(authenticationInfo.getCredentials()).thenReturn(credential);
        Mockito.when(credential.getCredentialKey()).thenReturn("credential key");

        Assert.assertFalse("False expected.", jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }

    @Test
    public void doCredentialsMatchEqualKeysFalseTest() throws OpenIDException {
        credential = Mockito.mock(Credential.class);
        authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        jwtProcessor = Mockito.mock(JwtProcessor.class);
        Mockito.when(authenticationToken.getIdToken()).thenReturn("idToken");
        Mockito.when(authenticationInfo.getCredentials()).thenReturn(credential);
        Mockito.when(credential.getCredentialKey()).thenReturn("idToken");
        Mockito.when(jwtProcessor.validate("idToken")).thenReturn(false);

        Assert.assertFalse("False expected.", jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }

    @Test
    public void doCredentialsMatchEqualKeysTrueTest() throws OpenIDException {
        credential = Mockito.mock(Credential.class);
        authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        jwtProcessor = Mockito.mock(JwtProcessor.class);
        Mockito.when(authenticationToken.getIdToken()).thenReturn("idToken");
        Mockito.when(authenticationInfo.getCredentials()).thenReturn(credential);
        Mockito.when(credential.getCredentialKey()).thenReturn("idToken");
        Mockito.when(jwtProcessor.validate("idToken")).thenReturn(true);

        Assert.assertTrue("True expected.", jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }

    @Test
    public void doCredentialsMatchEqualKeysOpenIDExceptionTest() throws OpenIDException {
        credential = Mockito.mock(Credential.class);
        authenticationInfo = Mockito.mock(AuthenticationInfo.class);
        authenticationToken = Mockito.mock(JwtCredentialsImpl.class);
        jwtProcessor = Mockito.mock(JwtProcessor.class);
        JwtCredentialsMatcher jwtCredentialsMatcher = new JwtCredentialsMatcher(jwtProcessor);
        Mockito.when(authenticationToken.getIdToken()).thenReturn("idToken");
        Mockito.when(authenticationInfo.getCredentials()).thenReturn(credential);
        Mockito.when(credential.getCredentialKey()).thenReturn("idToken");
        Mockito.when(jwtProcessor.validate("idToken")).thenThrow(Mockito.mock(OpenIDException.class));

        Assert.assertFalse("False expected.", jwtCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }
}