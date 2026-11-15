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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;


@Category(JUnitTests.class)
public class ScratchCodeImplTest {

    KapuaId[] scopeIds;
    KapuaEid[] mfaOptionIds, newMfaOptionIds;
    String[] codes, newCodes;
    ScratchCode scratchCode;
    Date modifiedOn;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: scopeIds = new KapuaId[]{null, KapuaId.ONE};
        // Removed line due to General Fixture smell: mfaOptionIds = new KapuaEid[]{null, new KapuaEid()};
        // Removed line due to General Fixture smell: newMfaOptionIds = new KapuaEid[]{null, new KapuaEid()};
        // Removed line due to General Fixture smell: codes = new String[]{null, "", "!!code-1", "#1(code-a.,/codeey)9--99", "!$$ 1-2 code//", "code(....)<00>", "co..,,000de@"};
        // Removed line due to General Fixture smell: newCodes = new String[]{null, "", "NEW!!code-1", "#1(code-a.,/newcodeey)9--99", "new...!$$ 1-2 code//", "NEWCODE newcode(....)<00>", "n..w co..,,000de@"};
        // Removed line due to General Fixture smell: scratchCode = Mockito.mock(ScratchCode.class);
        // Removed line due to General Fixture smell: modifiedOn = new Date();
    }

    @Test
    public void scratchCodeImplWithoutParametersTest() {
        scratchCode = Mockito.mock(ScratchCode.class);
        ScratchCodeImpl scratchCodeImpl = new ScratchCodeImpl();
        Assert.assertNull("Null expected.", scratchCodeImpl.getScopeId());
        Assert.assertNull("Null expected.", scratchCodeImpl.getMfaOptionId());
        Assert.assertNull("Null expected.", scratchCodeImpl.getCode());
    }

    @Test
    public void scratchCodeImplScopeIdParameterTest() {
        scratchCode = Mockito.mock(ScratchCode.class);
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        for (KapuaId scopeId : scopeIds) {
            ScratchCodeImpl scratchCodeImpl = new ScratchCodeImpl(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, scratchCodeImpl.getScopeId());
            Assert.assertNull("Null expected.", scratchCodeImpl.getMfaOptionId());
            Assert.assertNull("Null expected.", scratchCodeImpl.getCode());
        }
    }

    @Test
    public void scratchCodeImplScopeIdMfaOptionIdCodeParametersTest() {
        scratchCode = Mockito.mock(ScratchCode.class);
        codes = new String[]{null, "", "!!code-1", "#1(code-a.,/codeey)9--99", "!$$ 1-2 code//", "code(....)<00>", "co..,,000de@"};
        mfaOptionIds = new KapuaEid[]{null, new KapuaEid()};
        scopeIds = new KapuaId[]{null, KapuaId.ONE};
        for (KapuaId scopeId : scopeIds) {
            for (KapuaId mfaOptionId : mfaOptionIds) {
                for (String code : codes) {
                    ScratchCodeImpl scratchCodeImpl = new ScratchCodeImpl(scopeId, mfaOptionId, code);
                    Assert.assertEquals("Expected and actual values should be the same.", scopeId, scratchCodeImpl.getScopeId());
                    Assert.assertEquals("Expected and actual values should be the same.", mfaOptionId, scratchCodeImpl.getMfaOptionId());
                    Assert.assertEquals("Expected and actual values should be the same.", code, scratchCodeImpl.getCode());
                }
            }
        }
    }

    @Test
    public void scratchCodeImplScratchCodeParameterTest() throws KapuaException {
        modifiedOn = new Date();
        scratchCode = Mockito.mock(ScratchCode.class);
        Mockito.when(scratchCode.getCode()).thenReturn("code");
        Mockito.when(scratchCode.getMfaOptionId()).thenReturn(KapuaId.ONE);
        Mockito.when(scratchCode.getModifiedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(scratchCode.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(scratchCode.getOptlock()).thenReturn(10);

        ScratchCodeImpl scratchCodeImpl = new ScratchCodeImpl(scratchCode);

        Assert.assertNull("Null expected.", scratchCodeImpl.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, scratchCodeImpl.getMfaOptionId());
        Assert.assertEquals("Expected and actual values should be the same.", "code", scratchCodeImpl.getCode());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, scratchCodeImpl.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, scratchCodeImpl.getModifiedOn());
        Integer magicNumber93199953 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber93199953, scratchCodeImpl.getOptlock());
    }


    @Test
    public void setAndGetMfaOptionIdTest() throws KapuaException {
        scratchCode = Mockito.mock(ScratchCode.class);
        newMfaOptionIds = new KapuaEid[]{null, new KapuaEid()};
        ScratchCodeImpl scratchCodeImpl1 = new ScratchCodeImpl();
        for (KapuaId newMfaOptionId : newMfaOptionIds) {
            scratchCodeImpl1.setMfaOptionId(newMfaOptionId);
            Assert.assertEquals("Expected and actual values should be the same.", newMfaOptionId, scratchCodeImpl1.getMfaOptionId());
        }

        ScratchCodeImpl scratchCodeImpl2 = new ScratchCodeImpl(KapuaId.ONE);
        for (KapuaId newMfaOptionId : newMfaOptionIds) {
            scratchCodeImpl2.setMfaOptionId(newMfaOptionId);
            Assert.assertEquals("Expected and actual values should be the same.", newMfaOptionId, scratchCodeImpl2.getMfaOptionId());
        }

        ScratchCodeImpl scratchCodeImpl3 = new ScratchCodeImpl(KapuaId.ONE, new KapuaEid(), "code");
        for (KapuaId newMfaOptionId : newMfaOptionIds) {
            scratchCodeImpl3.setMfaOptionId(newMfaOptionId);
            Assert.assertEquals("Expected and actual values should be the same.", newMfaOptionId, scratchCodeImpl3.getMfaOptionId());
        }

        ScratchCodeImpl scratchCodeImpl4 = new ScratchCodeImpl(scratchCode);
        for (KapuaId newMfaOptionId : newMfaOptionIds) {
            scratchCodeImpl4.setMfaOptionId(newMfaOptionId);
            Assert.assertEquals("Expected and actual values should be the same.", newMfaOptionId, scratchCodeImpl4.getMfaOptionId());
        }
    }

    @Test
    public void setAndGetCodeTest() throws KapuaException {
        scratchCode = Mockito.mock(ScratchCode.class);
        newCodes = new String[]{null, "", "NEW!!code-1", "#1(code-a.,/newcodeey)9--99", "new...!$$ 1-2 code//", "NEWCODE newcode(....)<00>", "n..w co..,,000de@"};
        ScratchCodeImpl scratchCodeImpl1 = new ScratchCodeImpl();
        for (String code : newCodes) {
            scratchCodeImpl1.setCode(code);
            Assert.assertEquals("Expected and actual values should be the same.", code, scratchCodeImpl1.getCode());
        }

        ScratchCodeImpl scratchCodeImpl2 = new ScratchCodeImpl(KapuaId.ONE);
        for (String code : newCodes) {
            scratchCodeImpl2.setCode(code);
            Assert.assertEquals("Expected and actual values should be the same.", code, scratchCodeImpl2.getCode());
        }

        ScratchCodeImpl scratchCodeImpl3 = new ScratchCodeImpl(KapuaId.ONE, new KapuaEid(), "code");
        for (String code : newCodes) {
            scratchCodeImpl3.setCode(code);
            Assert.assertEquals("Expected and actual values should be the same.", code, scratchCodeImpl3.getCode());
        }

        ScratchCodeImpl scratchCodeImpl4 = new ScratchCodeImpl(scratchCode);
        for (String code : newCodes) {
            scratchCodeImpl4.setCode(code);
            Assert.assertEquals("Expected and actual values should be the same.", code, scratchCodeImpl4.getCode());
        }
    }
}