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
package org.eclipse.kapua.service.authentication.credential.mfa.shiro;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionCreator;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionListResult;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class MfaOptionFactoryImplTest {

    MfaOptionFactoryImpl mfaOptionFactoryImpl;
    KapuaId[] scopeIds;
    KapuaEid[] userIds;
    String[] mfaSecretKeys;
    MfaOption mfaOption;
    Date trustExpirationDate;
    Date modifiedOn;

    @Before
    public void initialize() {
        mfaOptionFactoryImpl = new MfaOptionFactoryImpl();
        // Removed line due to General Fixture smell: scopeIds = new KapuaId[]{null, KapuaId.ONE};
        // Removed line due to General Fixture smell: userIds = new KapuaEid[]{null, new KapuaEid()};
        // Removed line due to General Fixture smell: mfaSecretKeys = new String[]{null, "", "!!mfaSecretKey-1", "#1(newMfa.,/SecretKey)9--99", "!$$ 1-2 key//", "mfa_key(....)<00>"};
        // Removed line due to General Fixture smell: mfaOption = Mockito.mock(MfaOption.class);
        // Removed line due to General Fixture smell: trustExpirationDate = new Date();
        // Removed line due to General Fixture smell: modifiedOn = new Date();
    }

    @Test
    public void newCreatorScopeIdUserIdMfaSecretKeyParametersTest() {
        mfaSecretKeys = new String[]{null, "", "!!mfaSecretKey-1", "#1(newMfa.,/SecretKey)9--99", "!$$ 1-2 key//", "mfa_key(....)<00>"};
        userIds = new KapuaEid[]{null, new KapuaEid()};
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        mfaOption = Mockito.mock(MfaOption.class);
        for (KapuaId scopeId : scopeIds) {
            for (KapuaEid userId : userIds) {
                for (String mfaSecretKey : mfaSecretKeys) {
                    MfaOptionCreatorImpl mfaOptionCreatorImpl = mfaOptionFactoryImpl.newCreator(scopeId, userId, mfaSecretKey);
                    Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOptionCreatorImpl.getScopeId());
                    Assert.assertEquals("Expected and actual values should be the same.", userId, mfaOptionCreatorImpl.getUserId());
                    Assert.assertEquals("Expected and actual values should be the same.", mfaSecretKey, mfaOptionCreatorImpl.getMfaSecretKey());
                }
            }
        }
    }

    @Test
    public void newListResultTest() {
        mfaOption = Mockito.mock(MfaOption.class);
        Assert.assertTrue("Instance of MfaOptionListResult expected.", mfaOptionFactoryImpl.newListResult() instanceof MfaOptionListResult);
    }

    @Test
    public void newEntityTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        mfaOption = Mockito.mock(MfaOption.class);
        for (KapuaId scopeId : scopeIds) {
            MfaOption mfaOption = mfaOptionFactoryImpl.newEntity(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOption.getScopeId());
        }
    }

    @Test
    public void newMfaOptionTest() {
        mfaSecretKeys = new String[]{null, "", "!!mfaSecretKey-1", "#1(newMfa.,/SecretKey)9--99", "!$$ 1-2 key//", "mfa_key(....)<00>"};
        userIds = new KapuaEid[]{null, new KapuaEid()};
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        mfaOption = Mockito.mock(MfaOption.class);
        for (KapuaId scopeId : scopeIds) {
            for (KapuaId userId : userIds) {
                for (String mfaSecretKey : mfaSecretKeys) {
                    MfaOption mfaOption = mfaOptionFactoryImpl.newMfaOption(scopeId, userId, mfaSecretKey);
                    Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOption.getScopeId());
                    Assert.assertEquals("Expected and actual values should be the same.", userId, mfaOption.getUserId());
                    Assert.assertEquals("Expected and actual values should be the same.", mfaSecretKey, mfaOption.getMfaSecretKey());
                }
            }
        }
    }

    @Test
    public void newQueryTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        mfaOption = Mockito.mock(MfaOption.class);
        for (KapuaId scopeId : scopeIds) {
            MfaOptionQuery mfaOptionQuery = mfaOptionFactoryImpl.newQuery(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOptionQuery.getScopeId());
        }
    }

    @Test
    public void newCreatorScopeIdParameterTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        mfaOption = Mockito.mock(MfaOption.class);
        for (KapuaId scopeId : scopeIds) {
            MfaOptionCreator mfaOptionCreator = mfaOptionFactoryImpl.newCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOptionCreator.getScopeId());
        }
    }

    @Test
    public void cloneTest() {
        modifiedOn = new Date();
        trustExpirationDate = new Date();
        mfaOption = Mockito.mock(MfaOption.class);
        Mockito.when(mfaOption.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(mfaOption.getUserId()).thenReturn(KapuaId.ONE);
        Mockito.when(mfaOption.getMfaSecretKey()).thenReturn("mfa secret key");
        Mockito.when(mfaOption.getTrustKey()).thenReturn("thrust key");
        Mockito.when(mfaOption.getTrustExpirationDate()).thenReturn(trustExpirationDate);
        Mockito.when(mfaOption.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(mfaOption.getModifiedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(mfaOption.getOptlock()).thenReturn(10);

        MfaOption mfaOptionResult = mfaOptionFactoryImpl.clone(mfaOption);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, mfaOptionResult.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, mfaOptionResult.getUserId());
        Assert.assertEquals("Expected and actual values should be the same.", "mfa secret key", mfaOptionResult.getMfaSecretKey());
        Assert.assertEquals("Expected and actual values should be the same.", "thrust key", mfaOptionResult.getTrustKey());
        Assert.assertEquals("Expected and actual values should be the same.", trustExpirationDate, mfaOptionResult.getTrustExpirationDate());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, mfaOptionResult.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, mfaOptionResult.getModifiedBy());
        Integer magicNumber23853648 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber23853648, mfaOptionResult.getOptlock());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        mfaOption = Mockito.mock(MfaOption.class);
        mfaOptionFactoryImpl.clone(null);
    }
}