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
package org.eclipse.kapua.service.authentication.shiro.mfa;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.shiro.utils.AuthenticationUtils;
import org.eclipse.kapua.service.authentication.shiro.utils.CryptAlgorithm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

@Category(JUnitTests.class)
public class MfaAuthenticatorImplTest extends Assert {

    MfaAuthenticatorImpl mfaAuthenticatorImpl;
    String[] encryptedSecrets, hashedScratchCodes, stringVerificationCodes;
    int[] verificationCodes;

    @Before
    public void initialize() throws KapuaException {
        // Removed line due to General Fixture smell: mfaAuthenticatorImpl = new MfaAuthenticatorImpl();

        // Removed line due to General Fixture smell: encryptedSecrets = new String[]{
                AuthenticationUtils.encryptAes("value to encrypt"),
                AuthenticationUtils.encryptAes("value@#$ en-999crypt"),
                AuthenticationUtils.encryptAes("!<>v87a-lue to encrypt"),
                AuthenticationUtils.encryptAes("value_to$#encr-0y()pt"),
                AuthenticationUtils.encryptAes("va09l-ue|,,,.to00encrypt")
        };

        // Removed line due to General Fixture smell: verificationCodes = new int[]{
                -2147483648,
                -100000,
                -100,
                -1,
                0,
                1,
                100,
                100000,
                2147483647
        };

        // Removed line due to General Fixture smell: hashedScratchCodes = new String[]{
                AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, "val-ue99_<11>"),
                AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, "   !@#$v66a0l-ueee"),
                AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, "val  *&^%087,...ueee   "),
                AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, "_877V.A;;LUE")
        };

        // Removed line due to General Fixture smell: stringVerificationCodes = new String[]{
                "0",
                " 1",
                "100",
                "100000",
                "2147483647"
        };
    }

    @Test
    public void isEnabledTest() {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        assertTrue("True expected.", mfaAuthenticatorImpl.isEnabled());
    }

    @Test
    public void authorizeEncryptedSecretVerificationCodeParametersTest() throws KapuaException {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        verificationCodes = new int[]{
        encryptedSecrets = new String[]{
        for (String encryptedSecret : encryptedSecrets) {
            for (int verificationCode : verificationCodes) {
                if (verificationCode >= 0) {
                    Assert.assertFalse(mfaAuthenticatorImpl.authorize(encryptedSecret, verificationCode), () -> "Add assertion message");
                } else {
                    assertThrows(KapuaIllegalArgumentException e,
                    		() -> {
                    			mfaAuthenticatorImpl.authorize(encryptedSecret, verificationCode);
                    			
                    			Assert.fail("This should have thrown KapuaIllegalArgumentException");
                    		});
                }
            }
        }
    }

    @Test
    public void authorizeNullVerificationCodeParametersTest() throws KapuaException {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        encryptedSecrets = new String[]{
        try {
            mfaAuthenticatorImpl.authorize(encryptedSecrets[0], null);

            Assert.fail("This should have thrown KapuaIllegalNullArgumentException");
        } catch (KapuaIllegalNullArgumentException e) {
            Assert.assertEquals("verificationCode", e.getArgumentName(), () -> "Add assertion message");
            Assert.assertNull(e.getArgumentValue(), () -> "Add assertion message");
        }
    }

    @Test
    public void authorizeNullEncryptedSecretVerificationCodeParametersTest() throws KapuaException {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        assertThrows(KapuaIllegalNullArgumentException e,
        		() -> {
        			mfaAuthenticatorImpl.authorize(null, "123456");
        			
        			Assert.fail("This should have thrown KapuaIllegalNullArgumentException");
        		});
    }

    @Test
    public void authorizeHashedScratchCodeVerificationCodeParametersFalseTest() throws KapuaException {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        stringVerificationCodes = new String[]{
        hashedScratchCodes = new String[]{
        for (String hashedScratchCode : hashedScratchCodes) {
            for (String stringVerificationCode : stringVerificationCodes) {
                Assert.assertFalse(mfaAuthenticatorImpl.authorize(hashedScratchCode, stringVerificationCode), () -> "Add assertion message");
            }
        }
    }

    @Test
    public void authorizeHashedScratchCodeVerificationCodeParametersTrueTest() throws KapuaException {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        Assert.assertTrue(mfaAuthenticatorImpl.authorize("$2a$12$2AZYOAvilJyNvG8b6rBDaOSIcM3mKc6iyNQUYIXOF4ZFEAYdzM7Jm", "plainValue"), () -> "Add assertion message");
    }

    @Test
    public void generateKeyTest() {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        String generatedKey = mfaAuthenticatorImpl.generateKey();

        Assert.assertNotNull(generatedKey, () -> "Add assertion message");
        Integer magicNumber11268311 = 32; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber11268311, generatedKey.length(), () -> "Add assertion message");
    }

    @Test
    public void generateCodesTest() {
        mfaAuthenticatorImpl = new MfaAuthenticatorImpl();
        List<String> generatedScratchCodes = mfaAuthenticatorImpl.generateCodes();

        Assert.assertNotNull(generatedScratchCodes, () -> "Add assertion message");
        Assert.assertEquals(5, generatedScratchCodes.size(), () -> "Add assertion message");
        for (String generatedScratchCode : generatedScratchCodes) {
            Assert.assertNotNull(generatedScratchCode, () -> "Add assertion message");
        }
    }
}