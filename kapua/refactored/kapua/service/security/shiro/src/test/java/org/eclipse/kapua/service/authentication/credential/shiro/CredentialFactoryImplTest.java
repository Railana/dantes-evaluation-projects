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
package org.eclipse.kapua.service.authentication.credential.shiro;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.credential.CredentialType;
import org.eclipse.kapua.service.authentication.credential.CredentialStatus;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialQuery;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class CredentialFactoryImplTest {

    CredentialFactoryImpl credentialFactoryImpl;
    KapuaId[] scopeIds;
    KapuaEid[] userIds;
    String[] credentialKeys;
    CredentialType[] credentialTypes;
    CredentialStatus[] credentialStatuses;
    Date[] dates;
    Credential credential;
    Date expirationDate, modifiedOn;

    @Before
    public void initialize() {
        credentialFactoryImpl = new CredentialFactoryImpl();
        // Removed line due to General Fixture smell: scopeIds = new KapuaId[]{null, KapuaId.ONE};
        // Removed line due to General Fixture smell: userIds = new KapuaEid[]{null, new KapuaEid()};
        // Removed line due to General Fixture smell: credentialKeys = new String[]{null, "", "!!credentialKey-1", "#1(credentialKey.,/Key)9--99", "!$$ 1-2 key//", "credential_K_ey(....)<00>"};
        // Removed line due to General Fixture smell: credentialTypes = new CredentialType[]{null, CredentialType.PASSWORD, CredentialType.API_KEY, CredentialType.JWT};
        // Removed line due to General Fixture smell: credentialStatuses = new CredentialStatus[]{null, CredentialStatus.ENABLED, CredentialStatus.DISABLED};
        // Removed line due to General Fixture smell: dates = new Date[]{null, new Date()};
        // Removed line due to General Fixture smell: credential = Mockito.mock(Credential.class);
        // Removed line due to General Fixture smell: expirationDate = new Date();
        // Removed line due to General Fixture smell: modifiedOn = new Date();
    }

    @Test
    public void newCreatorScopeIdUserIdMfaSecretKeyParametersTest() {
        dates = new Date[]{null, new Date()};
        credentialStatuses = new CredentialStatus[]{null, CredentialStatus.ENABLED, CredentialStatus.DISABLED};
        credentialTypes = new CredentialType[]{null, CredentialType.PASSWORD, CredentialType.API_KEY, CredentialType.JWT};
        credentialKeys = new String[]{null, "", "!!credentialKey-1", "#1(credentialKey.,/Key)9--99", "!$$ 1-2 key//", "credential_K_ey(....)<00>"};
        userIds = new KapuaEid[]{null, new KapuaEid()};
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        credential = Mockito.mock(Credential.class);
        for (KapuaId scopeId : scopeIds) {
            for (KapuaEid userId : userIds) {
                for (CredentialType credentialType : credentialTypes) {
                    for (String credentialKey : credentialKeys) {
                        for (CredentialStatus credentialStatus : credentialStatuses) {
                            for (Date date : dates) {
                                CredentialCreatorImpl credentialCreatorImpl = credentialFactoryImpl.newCreator(scopeId, userId, credentialType, credentialKey, credentialStatus, date);
                                Assert.assertEquals("Expected and actual values should be the same.", scopeId, credentialCreatorImpl.getScopeId());
                                Assert.assertEquals("Expected and actual values should be the same.", userId, credentialCreatorImpl.getUserId());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialType, credentialCreatorImpl.getCredentialType());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialKey, credentialCreatorImpl.getCredentialPlainKey());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialStatus, credentialCreatorImpl.getCredentialStatus());
                                Assert.assertEquals("Expected and actual values should be the same.", date, credentialCreatorImpl.getExpirationDate());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void newListResultTest() {
        credential = Mockito.mock(Credential.class);
        Assert.assertTrue("True expected.", credentialFactoryImpl.newListResult() instanceof CredentialListResult);
    }

    @Test
    public void newEntityTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        credential = Mockito.mock(Credential.class);
        for (KapuaId scopeId : scopeIds) {
            Credential credential = credentialFactoryImpl.newEntity(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, credential.getScopeId());
        }
    }

    @Test
    public void newMfaOptionTest() {
        dates = new Date[]{null, new Date()};
        credentialStatuses = new CredentialStatus[]{null, CredentialStatus.ENABLED, CredentialStatus.DISABLED};
        credentialTypes = new CredentialType[]{null, CredentialType.PASSWORD, CredentialType.API_KEY, CredentialType.JWT};
        credentialKeys = new String[]{null, "", "!!credentialKey-1", "#1(credentialKey.,/Key)9--99", "!$$ 1-2 key//", "credential_K_ey(....)<00>"};
        userIds = new KapuaEid[]{null, new KapuaEid()};
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        credential = Mockito.mock(Credential.class);
        for (KapuaId scopeId : scopeIds) {
            for (KapuaEid userId : userIds) {
                for (CredentialType credentialType : credentialTypes) {
                    for (String credentialKey : credentialKeys) {
                        for (CredentialStatus credentialStatus : credentialStatuses) {
                            for (Date date : dates) {
                                Credential credential = credentialFactoryImpl.newCredential(scopeId, userId, credentialType, credentialKey, credentialStatus, date);
                                Assert.assertEquals("Expected and actual values should be the same.", scopeId, credential.getScopeId());
                                Assert.assertEquals("Expected and actual values should be the same.", userId, credential.getUserId());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialType, credential.getCredentialType());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialKey, credential.getCredentialKey());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialStatus, credential.getStatus());
                                Assert.assertEquals("Expected and actual values should be the same.", date, credential.getExpirationDate());

                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void newQueryTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        credential = Mockito.mock(Credential.class);
        for (KapuaId scopeId : scopeIds) {
            CredentialQuery credentialQuery = credentialFactoryImpl.newQuery(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, credentialQuery.getScopeId());
        }
    }

    @Test
    public void newCreatorScopeIdParameterTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        credential = Mockito.mock(Credential.class);
        for (KapuaId scopeId : scopeIds) {
            CredentialCreator credentialCreator = credentialFactoryImpl.newCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, credentialCreator.getScopeId());
        }
    }

    @Test
    public void cloneTest() {
        modifiedOn = new Date();
        expirationDate = new Date();
        credential = Mockito.mock(Credential.class);
        Mockito.when(credential.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(credential.getUserId()).thenReturn(KapuaId.ONE);
        Mockito.when(credential.getCredentialType()).thenReturn(CredentialType.JWT);
        Mockito.when(credential.getCredentialKey()).thenReturn("key");
        Mockito.when(credential.getExpirationDate()).thenReturn(expirationDate);
        Mockito.when(credential.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(credential.getModifiedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(credential.getOptlock()).thenReturn(10);

        Credential credentialResult = credentialFactoryImpl.clone(credential);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, credentialResult.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, credentialResult.getUserId());
        Assert.assertEquals("Expected and actual values should be the same.", CredentialType.JWT, credentialResult.getCredentialType());
        Assert.assertEquals("Expected and actual values should be the same.", "key", credentialResult.getCredentialKey());
        Assert.assertEquals("Expected and actual values should be the same.", expirationDate, credentialResult.getExpirationDate());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, credentialResult.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, credentialResult.getModifiedBy());
        Integer magicNumber28084478 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber28084478, credentialResult.getOptlock());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        credential = Mockito.mock(Credential.class);
        credentialFactoryImpl.clone(null);
    }
}