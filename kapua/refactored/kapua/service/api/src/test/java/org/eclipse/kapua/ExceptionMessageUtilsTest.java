/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.MissingResourceException;


@Category(JUnitTests.class)
public class ExceptionMessageUtilsTest {

    String[] resourceBundleName;
    Locale locale;
    KapuaErrorCode kapuaErrorCode;
    KapuaErrorCode mockedKapuaErrorCode;
    Object[] objectList;
    Object[] emptyObjectList;
    String[] kapuaGenericMessageWithObject;
    String[] kapuaGenericMessage;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: resourceBundleName = new String[]{"kapua-service-error-messages", "Resource Bundle Name"};
        // Removed line due to General Fixture smell: locale = Locale.ENGLISH;
        // Removed line due to General Fixture smell: kapuaErrorCode = KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR;
        // Removed line due to General Fixture smell: mockedKapuaErrorCode = Mockito.mock(KapuaErrorCode.class);
        // Removed line due to General Fixture smell: objectList = new Object[]{0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false};
        // Removed line due to General Fixture smell: emptyObjectList = new Object[]{};
        // Removed line due to General Fixture smell: kapuaGenericMessageWithObject = new String[]{"Operation not allowed on admin role.", "Error: 0, 10, 100000, String, c, -10, -1000000000, -100000000000, 10, 10.0, null, 10.1, true, false", "Error: "};
        // Removed line due to General Fixture smell: kapuaGenericMessage = new String[]{"Operation not allowed on admin role.", "Error: "};
    }

    @Test
    public void exceptionMessageUtilsTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ExceptionMessageUtils> exceptionMessageUtils = ExceptionMessageUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(exceptionMessageUtils.getModifiers()), () -> "Add assertion message");
        exceptionMessageUtils.setAccessible(true);
        exceptionMessageUtils.newInstance();
    }

    @Test
    public void getLocalizedMessageTest() {
        kapuaGenericMessage = new String[]{"Operation not allowed on admin role.", "Error: "};
        kapuaGenericMessageWithObject = new String[]{"Operation not allowed on admin role.", "Error: 0, 10, 100000, String, c, -10, -1000000000, -100000000000, 10, 10.0, null, 10.1, true, false", "Error: "};
        objectList = new Object[]{0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false};
        kapuaErrorCode = KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR;
        locale = Locale.ENGLISH;
        resourceBundleName = new String[]{"kapua-service-error-messages", "Resource Bundle Name"};
        for (int i = 0; i < resourceBundleName.length; i++) {
            Assert.assertEquals("Expected and actual values should be the same.", kapuaGenericMessageWithObject[i], ExceptionMessageUtils.getLocalizedMessage(resourceBundleName[i], locale, kapuaErrorCode, objectList));
        }
    }

    @Test
    public void getLocalizedMessageEmptyObjectListTest() {
        kapuaGenericMessage = new String[]{"Operation not allowed on admin role.", "Error: "};
        emptyObjectList = new Object[]{};
        kapuaErrorCode = KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR;
        locale = Locale.ENGLISH;
        resourceBundleName = new String[]{"kapua-service-error-messages", "Resource Bundle Name"};
        for (int i = 0; i < resourceBundleName.length; i++) {
            Assert.assertEquals("Expected and actual values should be the same.", kapuaGenericMessage[i], ExceptionMessageUtils.getLocalizedMessage(resourceBundleName[i], locale, kapuaErrorCode, emptyObjectList));
        }
    }

    @Test(expected = NullPointerException.class)
    public void getLocalizedMessageNullStringTest() {
        objectList = new Object[]{0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false};
        kapuaErrorCode = KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR;
        locale = Locale.ENGLISH;
        ExceptionMessageUtils.getLocalizedMessage(null, locale, kapuaErrorCode, objectList);
    }

    @Test(expected = NullPointerException.class)
    public void getLocalizedMessageNullLocaleTest() {
        objectList = new Object[]{0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false};
        kapuaErrorCode = KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR;
        resourceBundleName = new String[]{"kapua-service-error-messages", "Resource Bundle Name"};
        for (String name : resourceBundleName) {
            ExceptionMessageUtils.getLocalizedMessage(name, null, kapuaErrorCode, objectList);
        }
    }

    @Test(expected = MissingResourceException.class)
    public void getLocalizedMessageMissingResourceTest() {
        objectList = new Object[]{0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false};
        mockedKapuaErrorCode = Mockito.mock(KapuaErrorCode.class);
        locale = Locale.ENGLISH;
        Mockito.when(mockedKapuaErrorCode.name()).thenThrow(new MissingResourceException("Message", "Class Name", "Key"));
        ExceptionMessageUtils.getLocalizedMessage("kapua-service-error-messages", locale, mockedKapuaErrorCode, objectList);
    }

    @Test
    public void nullErrorCodeLeadsToDefaultErrorMessage() {
        objectList = new Object[]{0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false};
        locale = Locale.ENGLISH;
        resourceBundleName = new String[]{"kapua-service-error-messages", "Resource Bundle Name"};
        final String got = ExceptionMessageUtils.getLocalizedMessage(resourceBundleName[0], locale, null, objectList);
        Assert.assertEquals("Error: 0, 10, 100000, String, c, -10, -1000000000, -100000000000, 10, 10.0, null, 10.1, true, false", got, () -> "Add assertion message");
    }

    @Test
    public void getLocalizedMessageNullObjectTest() {
        kapuaGenericMessage = new String[]{"Operation not allowed on admin role.", "Error: "};
        kapuaErrorCode = KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR;
        locale = Locale.ENGLISH;
        resourceBundleName = new String[]{"kapua-service-error-messages", "Resource Bundle Name"};
        for (int i = 0; i < resourceBundleName.length; i++) {
            Assert.assertEquals("Expected and actual values should be the same.", kapuaGenericMessage[i], ExceptionMessageUtils.getLocalizedMessage(resourceBundleName[i], locale, kapuaErrorCode, null));
        }
    }

    @Test
    public void getLocalizedMessageNullKapuaErrorCodeSecondTest() {
        kapuaGenericMessage = new String[]{"Operation not allowed on admin role.", "Error: "};
        kapuaGenericMessageWithObject = new String[]{"Operation not allowed on admin role.", "Error: 0, 10, 100000, String, c, -10, -1000000000, -100000000000, 10, 10.0, null, 10.1, true, false", "Error: "};
        objectList = new Object[]{0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false};
        locale = Locale.ENGLISH;
        resourceBundleName = new String[]{"kapua-service-error-messages", "Resource Bundle Name"};
        Assert.assertEquals("Expected and actual values should be the same.", kapuaGenericMessageWithObject[1], ExceptionMessageUtils.getLocalizedMessage(resourceBundleName[1], locale, null, objectList));
    }
}