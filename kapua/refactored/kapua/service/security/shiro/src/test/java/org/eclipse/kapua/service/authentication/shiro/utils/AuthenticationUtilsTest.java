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
package org.eclipse.kapua.service.authentication.shiro.utils;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaCryptoSettingKeys;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


@Category(JUnitTests.class)
public class AuthenticationUtilsTest {

    String[] plainValues, encryptedValues;
    private String cypherKeyPropKey = KapuaCryptoSettingKeys.CIPHER_KEY.key();
    private String cryptoShaAlgorithmPropKey = KapuaCryptoSettingKeys.CRYPTO_SHA_ALGORITHM.key();

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: plainValues = new String[]{"plain_..val9&^%ue123!!", "   value#999 ?><,,..;''a  ", "valu   e plain*&^%     $#45", "value,,,,va?><  ", "... s_er%%67nsaa4356&^%   a *(me"};
        // Removed line due to General Fixture smell: encryptedValues = new String[]{"2c3mAagxwaEuAhmR1UzyafpKdA8R-poaS2upJPj4kzE", "QprB8vCeyft4pU8AJdxSWlIFL1b02s-UqTQwirKj9Dw", "RrRtzYPLFDgVdmKo9kOipZv723WBs2J3IxSoPwSJM7g",
                "gcGjWNELoVl9R-71-Nm8aAoNgf3lxr5FziYhj8dmML0", "lCysXXE00k64hm_FzQ8aK1GlVMFqR6So3knfnb5R_CQKDYH95ca-Rc4mIY_HZjC9"};
    }

    @After
    public void tearDown() {
        System.clearProperty(cypherKeyPropKey);
        System.clearProperty(cryptoShaAlgorithmPropKey);
    }

    @Test
    public void authenticationUtilsTest() throws Exception {
        Constructor<AuthenticationUtils> authenticationUtils = AuthenticationUtils.class.getDeclaredConstructor();
        authenticationUtils.setAccessible(true);
        authenticationUtils.newInstance();
        Assert.assertTrue("True expected.", Modifier.isPrivate(authenticationUtils.getModifiers()));
    }

    @Test
    public void cryptCredentialBCRYPTAlgorithmTest() throws KapuaException {
        plainValues = new String[]{"plain_..val9&^%ue123!!", "   value#999 ?><,,..;''a  ", "valu   e plain*&^%     $#45", "value,,,,va?><  ", "... s_er%%67nsaa4356&^%   a *(me"};
        for (String plainValue : plainValues) {
            Assert.assertTrue("True expected.", AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, plainValue).startsWith("$2a$12$"));
            Integer magicNumber77835955 = 60; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            Assert.assertEquals("Expected and actual values should be the same.",magicNumber77835955, AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, "plain value").length());
        }
    }

    @Test
    public void cryptCredentialSHAAlgorithmTest() throws KapuaException {
        plainValues = new String[]{"plain_..val9&^%ue123!!", "   value#999 ?><,,..;''a  ", "valu   e plain*&^%     $#45", "value,,,,va?><  ", "... s_er%%67nsaa4356&^%   a *(me"};
        String[] shaAlgorithm = {"SHA256", "SHA512", "algorithm", ""};
        int[] expectedLength = {77, 141, 141, 141};

        for (int i = 0; i < shaAlgorithm.length; i++) {
            System.setProperty(cryptoShaAlgorithmPropKey, shaAlgorithm[i]);
            for (String plainValue : plainValues) {
                Assert.assertTrue("True expected.", AuthenticationUtils.cryptCredential(CryptAlgorithm.SHA, plainValue).contains("=:"));
                Assert.assertEquals("Expected and actual values should be the same.", expectedLength[i], AuthenticationUtils.cryptCredential(CryptAlgorithm.SHA, plainValue).length());
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void cryptCredentialNullAlgorithmTest() throws KapuaException {
        AuthenticationUtils.cryptCredential(null, "plain value");
    }

    @Test(expected = KapuaIllegalNullArgumentException.class)
    public void cryptCredentialNullPlainValueTest() throws KapuaException {
        AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, null);
    }

    @Test(expected = KapuaIllegalNullArgumentException.class)
    public void cryptCredentialEmptyPlainValueTest() throws KapuaException {
        AuthenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, "");
    }

    @Test
    public void encryptAesTest() {
        plainValues = new String[]{"plain_..val9&^%ue123!!", "   value#999 ?><,,..;''a  ", "valu   e plain*&^%     $#45", "value,,,,va?><  ", "... s_er%%67nsaa4356&^%   a *(me"};
        System.setProperty(cypherKeyPropKey, "rv;ipse329183!@#");
        for (String plainValue : plainValues) {
            assertThrows(Exception e,
            		() -> AuthenticationUtils.encryptAes(plainValue));
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void encryptAesIncorrectKeyTest() {
        plainValues = new String[]{"plain_..val9&^%ue123!!", "   value#999 ?><,,..;''a  ", "valu   e plain*&^%     $#45", "value,,,,va?><  ", "... s_er%%67nsaa4356&^%   a *(me"};
        System.setProperty(cypherKeyPropKey, "rv;ipse32918@#");
        for (String plainValue : plainValues) {
            AuthenticationUtils.encryptAes(plainValue);
        }
    }

    @Test(expected = NullPointerException.class)
    public void encryptAesNullTest() {
        System.setProperty(cypherKeyPropKey, "rv;ipse329183!@#");
        AuthenticationUtils.encryptAes(null);
    }

    @Test
    public void encryptAesEmptyValueTest() {
        System.setProperty(cypherKeyPropKey, "rv;ipse329183!@#");
        assertThrows(Exception e,
        		() -> AuthenticationUtils.encryptAes(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void encryptAesEmptyKeyTest() {
        System.setProperty(cypherKeyPropKey, "");
        AuthenticationUtils.encryptAes("plain value");
    }

    @Test
    public void decryptAesTest() {
        encryptedValues = new String[]{"2c3mAagxwaEuAhmR1UzyafpKdA8R-poaS2upJPj4kzE", "QprB8vCeyft4pU8AJdxSWlIFL1b02s-UqTQwirKj9Dw", "RrRtzYPLFDgVdmKo9kOipZv723WBs2J3IxSoPwSJM7g",
        System.setProperty(cypherKeyPropKey, "rv;ipse329183!@#");
        for (String encryptedValue : encryptedValues) {
            assertThrows(Exception e,
            		() -> AuthenticationUtils.decryptAes(encryptedValue));
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void decryptAesIncorrectKeyTest() {
        encryptedValues = new String[]{"2c3mAagxwaEuAhmR1UzyafpKdA8R-poaS2upJPj4kzE", "QprB8vCeyft4pU8AJdxSWlIFL1b02s-UqTQwirKj9Dw", "RrRtzYPLFDgVdmKo9kOipZv723WBs2J3IxSoPwSJM7g",
        System.setProperty(cypherKeyPropKey, "rv;ipse32918@#");
        for (String encryptedValue : encryptedValues) {
            AuthenticationUtils.decryptAes(encryptedValue);
        }
    }

    @Test(expected = NullPointerException.class)
    public void decryptAesNullTest() {
        System.setProperty(cypherKeyPropKey, "rv;ipse329183!@#");
        AuthenticationUtils.decryptAes(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decryptAesNllTest() {
        System.setProperty(cypherKeyPropKey, "rv;ipse329183!@#");
        AuthenticationUtils.decryptAes("value");
    }

    @Test
    public void decryptAesEmptyValueTest() {
        System.setProperty(cypherKeyPropKey, "rv;ipse329183!@#");
        assertThrows(Exception e,
        		() -> AuthenticationUtils.decryptAes(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decryptAesEmptyKeyTest() {
        System.setProperty(cypherKeyPropKey, "");
        AuthenticationUtils.decryptAes("2c3mAagxwaEuAhmR1UzyafpKdA8R-poaS2upJPj4kzE");
    }
}