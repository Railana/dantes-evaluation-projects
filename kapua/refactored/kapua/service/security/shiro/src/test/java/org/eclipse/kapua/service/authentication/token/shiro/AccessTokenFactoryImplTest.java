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
package org.eclipse.kapua.service.authentication.token.shiro;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenQuery;
import org.eclipse.kapua.service.authentication.token.LoginInfo;
import org.eclipse.kapua.service.authentication.token.AccessTokenListResult;
import org.eclipse.kapua.service.authentication.token.AccessTokenCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class AccessTokenFactoryImplTest {

    AccessTokenFactoryImpl accessTokenFactoryImpl;
    KapuaId[] scopeIds;
    KapuaEid[] userIds;
    String[] tokenIds, refreshTokens;
    Date[] expiresOnDates, refreshExpiresOnDates;
    AccessToken accessToken;
    Date modifiedOn, createdOn, invalidatedOn;

    @Before
    public void initialize() {
        accessTokenFactoryImpl = new AccessTokenFactoryImpl();
        // Removed line due to General Fixture smell: scopeIds = new KapuaId[]{null, KapuaId.ONE};
        // Removed line due to General Fixture smell: userIds = new KapuaEid[]{null, new KapuaEid()};
        // Removed line due to General Fixture smell: tokenIds = new String[]{null, "", "!!token id-1", "#1(token.,/tokenID)9--99", "!$$ 1-2 id//", "to_ken_id(....)<00>"};
        // Removed line due to General Fixture smell: refreshTokens = new String[]{null, "", "!!token refresh token-1", "#1(REfreSF.,/token_refresh)9--99", "!$$ 1-2 id//", "to_ken_refRESH token(....)<00>"};
        // Removed line due to General Fixture smell: expiresOnDates = new Date[]{null, new Date()};
        // Removed line due to General Fixture smell: refreshExpiresOnDates = new Date[]{null, new Date()};
        // Removed line due to General Fixture smell: accessToken = Mockito.mock(AccessToken.class);
        // Removed line due to General Fixture smell: modifiedOn = new Date();
        // Removed line due to General Fixture smell: createdOn = new Date();
        // Removed line due to General Fixture smell: invalidatedOn = new Date();
    }

    @Test
    public void newCreatorMultipleParametersTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        refreshExpiresOnDates = new Date[]{null, new Date()};
        expiresOnDates = new Date[]{null, new Date()};
        refreshTokens = new String[]{null, "", "!!token refresh token-1", "#1(REfreSF.,/token_refresh)9--99", "!$$ 1-2 id//", "to_ken_refRESH token(....)<00>"};
        tokenIds = new String[]{null, "", "!!token id-1", "#1(token.,/tokenID)9--99", "!$$ 1-2 id//", "to_ken_id(....)<00>"};
        userIds = new KapuaEid[]{null, new KapuaEid()};
        accessToken = Mockito.mock(AccessToken.class);
        for (KapuaId scopeId : scopeIds) {
            for (KapuaEid userId : userIds) {
                for (String tokenId : tokenIds) {
                    for (Date expiresOnDate : expiresOnDates) {
                        for (String refreshToken : refreshTokens) {
                            for (Date refreshExpiresOnDate : refreshExpiresOnDates) {
                                AccessTokenCreatorImpl accessTokenCreatorImpl = accessTokenFactoryImpl.newCreator(scopeId, userId, tokenId, expiresOnDate, refreshToken, refreshExpiresOnDate);
                                Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessTokenCreatorImpl.getScopeId());
                                Assert.assertEquals("Expected and actual values should be the same.", userId, accessTokenCreatorImpl.getUserId());
                                Assert.assertEquals("Expected and actual values should be the same.", tokenId, accessTokenCreatorImpl.getTokenId());
                                Assert.assertEquals("Expected and actual values should be the same.", expiresOnDate, accessTokenCreatorImpl.getExpiresOn());
                                Assert.assertEquals("Expected and actual values should be the same.", refreshToken, accessTokenCreatorImpl.getRefreshToken());
                                Assert.assertEquals("Expected and actual values should be the same.", refreshExpiresOnDate, accessTokenCreatorImpl.getRefreshExpiresOn());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void newEntityTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        accessToken = Mockito.mock(AccessToken.class);
        for (KapuaId scopeId : scopeIds) {
            AccessToken accessToken = accessTokenFactoryImpl.newEntity(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessToken.getScopeId());
        }
    }

    @Test
    public void newCreatorScopeIdParameterTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        accessToken = Mockito.mock(AccessToken.class);
        for (KapuaId scopeId : scopeIds) {
            AccessTokenCreator accessTokenCreator = accessTokenFactoryImpl.newCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessTokenCreator.getScopeId());
        }
    }

    @Test
    public void newQueryTest() {
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        accessToken = Mockito.mock(AccessToken.class);
        for (KapuaId scopeId : scopeIds) {
            AccessTokenQuery accessTokenQuery = accessTokenFactoryImpl.newQuery(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessTokenQuery.getScopeId());
        }
    }

    @Test
    public void newListResultTest() {
        accessToken = Mockito.mock(AccessToken.class);
        Assert.assertTrue("True expected.", accessTokenFactoryImpl.newListResult() instanceof AccessTokenListResult);
    }

    @Test
    public void cloneTest() {
        refreshExpiresOnDates = new Date[]{null, new Date()};
        expiresOnDates = new Date[]{null, new Date()};
        invalidatedOn = new Date();
        createdOn = new Date();
        modifiedOn = new Date();
        accessToken = Mockito.mock(AccessToken.class);
        Mockito.when(accessToken.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(accessToken.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getUserId()).thenReturn(KapuaId.ANY);
        Mockito.when(accessToken.getTokenId()).thenReturn("token id");
        Mockito.when(accessToken.getExpiresOn()).thenReturn(expiresOnDates[1]);
        Mockito.when(accessToken.getRefreshToken()).thenReturn("refresh token");
        Mockito.when(accessToken.getRefreshExpiresOn()).thenReturn(refreshExpiresOnDates[1]);
        Mockito.when(accessToken.getInvalidatedOn()).thenReturn(invalidatedOn);
        Mockito.when(accessToken.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(accessToken.getModifiedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getOptlock()).thenReturn(10);

        AccessToken accessTokenResult = accessTokenFactoryImpl.clone(accessToken);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getId());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, accessTokenResult.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessTokenResult.getUserId());
        Assert.assertEquals("Expected and actual values should be the same.", "token id", accessTokenResult.getTokenId());
        Assert.assertEquals("Expected and actual values should be the same.", expiresOnDates[1], accessTokenResult.getExpiresOn());
        Assert.assertEquals("Expected and actual values should be the same.", "refresh token", accessTokenResult.getRefreshToken());
        Assert.assertEquals("Expected and actual values should be the same.", refreshExpiresOnDates[1], accessTokenResult.getRefreshExpiresOn());
        Assert.assertEquals("Expected and actual values should be the same.", invalidatedOn, accessTokenResult.getInvalidatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, accessTokenResult.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getModifiedBy());
        Integer magicNumber35498036 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber35498036, accessTokenResult.getOptlock());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        accessToken = Mockito.mock(AccessToken.class);
        accessTokenFactoryImpl.clone(null);
    }

    @Test
    public void newLoginInfoTest() {
        accessToken = Mockito.mock(AccessToken.class);
        Assert.assertTrue("True expected.", accessTokenFactoryImpl.newLoginInfo() instanceof LoginInfo);
    }
}