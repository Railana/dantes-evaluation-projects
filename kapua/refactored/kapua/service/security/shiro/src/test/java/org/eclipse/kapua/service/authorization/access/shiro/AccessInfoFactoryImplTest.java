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

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.authorization.access.AccessInfoQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class AccessInfoFactoryImplTest {

    AccessInfoFactoryImpl accessInfoFactoryImpl;
    KapuaId scopeId;
    AccessInfo accessInfo;
    Date createdOn,modifiedOn;


    @Before
    public void initialize() {
        accessInfoFactoryImpl = new AccessInfoFactoryImpl();
        // Removed line due to General Fixture smell: scopeId = KapuaId.ONE;
        // Removed line due to General Fixture smell: accessInfo = Mockito.mock(AccessInfo.class);
        // Removed line due to General Fixture smell: createdOn = new Date();
        // Removed line due to General Fixture smell: modifiedOn = new Date();

        Mockito.when(accessInfo.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfo.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(accessInfo.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfo.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(accessInfo.getModifiedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(accessInfo.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(accessInfo.getOptlock()).thenReturn(11);
        Mockito.when(accessInfo.getUserId()).thenReturn(KapuaId.ANY);
    }

    @Test
    public void newEntityTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        scopeId = KapuaId.ONE;
        AccessInfo accessInfo = accessInfoFactoryImpl.newEntity(scopeId);

        Assert.assertNull("Null expected.", accessInfo.getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        AccessInfo accessInfo = accessInfoFactoryImpl.newEntity(null);

        Assert.assertNull("Null expected.", accessInfo.getScopeId());
    }

    @Test
    public void newCreatorTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        scopeId = KapuaId.ONE;
        AccessInfoCreator accessInfoCreator = accessInfoFactoryImpl.newCreator(scopeId);

        Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessInfoCreator.getScopeId());
    }

    @Test
    public void newCreatorNullTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        AccessInfoCreator accessInfoCreator = accessInfoFactoryImpl.newCreator(null);

        Assert.assertNull("Null expected.", accessInfoCreator.getScopeId());
    }

    @Test
    public void newQueryTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        scopeId = KapuaId.ONE;
        AccessInfoQuery accessInfoQuery = accessInfoFactoryImpl.newQuery(scopeId);

        Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessInfoQuery.getScopeId());
    }

    @Test
    public void newQueryNullTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        AccessInfoQuery accessInfoQuery = accessInfoFactoryImpl.newQuery(null);

        Assert.assertNull("Null expected.", accessInfoQuery.getScopeId());
    }

    @Test
    public void newListResultTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        AccessInfoListResult accessInfoListResult = accessInfoFactoryImpl.newListResult();

        Assert.assertTrue("True expected.", accessInfoListResult.isEmpty());
    }

    @Test
    public void cloneTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        modifiedOn = new Date();
        createdOn = new Date();
        AccessInfo resultAccessInfo = accessInfoFactoryImpl.clone(accessInfo);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultAccessInfo.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultAccessInfo.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultAccessInfo.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultAccessInfo.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultAccessInfo.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, resultAccessInfo.getModifiedOn());
        Integer magicNumber17937228 = 11; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber17937228, resultAccessInfo.getOptlock());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultAccessInfo.getUserId());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        accessInfo = Mockito.mock(AccessInfo.class);
        accessInfoFactoryImpl.clone(null);
    }
}