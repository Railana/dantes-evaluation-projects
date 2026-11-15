/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.commons.util;

import java.lang.reflect.Constructor;
import java.util.Date;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class KapuaDateUtilsTest {

    @Test
    public void testConstructor() throws Exception {
        Constructor<KapuaDateUtils> dateUtils = KapuaDateUtils.class.getDeclaredConstructor();
        dateUtils.setAccessible(true);
        dateUtils.newInstance();
    }

    @Test
    public void testGetKapuaSysDate() {
        Assert.assertNotNull(KapuaDateUtils.getKapuaSysDate(), () -> "Add assertion message");

    }

    @Test
    public void testGetTimeZone() {
        Assert.assertNotNull(KapuaDateUtils.getTimeZone(), () -> "Add assertion message");

    }

    @Test
    public void testParseDate() throws Exception {
        String[] listOfFalseStrings = new String[]{null,"2017/12/12T11:22:10.999Z","2017:12:12T:12:12:12.000Z",
                "2017_12_12T_12_12_12_000Z","2017;12;12T11;11;11;000Z","2017-12-12T12-12-12-000Z","2017:12:12T12/12/12/000Z",
                "2017.12.12T12.12.12.000Z","2017/12/12T12:12:12:000X"};
        int sizeOfFalseStrings = listOfFalseStrings.length;
        String[] listOfPermittedStrings = new String[] { "2017-12-12T11:22:10.999Z" };
        int sizeOfPermittedStrings = listOfPermittedStrings.length;
        // Negative tests
        for (int i = 0; i < sizeOfFalseStrings; i++) {
            if (listOfFalseStrings[i] == null) {
                assertThrows(Exception ex,
                		() -> Assert.assertNull(KapuaDateUtils.parseDate(listOfFalseStrings[i]), () -> "Add assertion message"));
            } else {
                try {
                    KapuaDateUtils.parseDate(listOfFalseStrings[i]);
                    Assert.fail("Exception expected for: " + listOfFalseStrings[i]);
                } catch (Exception ex) {
                    // Expected
                }
            }
        }
        // Positive tests
        for (int i = 0; i < sizeOfPermittedStrings; i++) {
            assertThrows(Exception ex,
            		() -> KapuaDateUtils.parseDate(listOfPermittedStrings[i]));
        }
    }

    @Test
    public void testFormatDate() {
        Date falseDate = null;
        Date permittedDate = new Date();
        // Negative tests
        assertThrows(Exception ex,
        		() -> Assert.assertNull(KapuaDateUtils.formatDate(falseDate), () -> "Add assertion message"));
        // Positive tests
        assertThrows(Exception ex,
        		() -> KapuaDateUtils.formatDate(permittedDate));
    }
}