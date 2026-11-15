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
package org.eclipse.kapua.service.account.xml;

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;

@Category(JUnitTests.class)
public class AccountParentPathXmlAdapterTest {

    private static final AccountParentPathXmlAdapter XML_ADAPTER = new AccountParentPathXmlAdapter();

    @Test
    public void testMarshal() {
        String parentAccountPath = "/1/2/3";

        String marshalled = XML_ADAPTER.marshal(parentAccountPath);

        Assert.assertNotNull(marshalled, () -> "Add assertion message");
        Assert.assertEquals("/AQ/Ag/Aw", marshalled, () -> "Add assertion message");
    }

    @Test
    public void testMarshalNull() {
        String marshalled = XML_ADAPTER.marshal(null);

        Assert.assertNull(marshalled, () -> "Add assertion message");
    }

    @Test
    public void testUnmarshal() throws KapuaIllegalArgumentException {
        String parentAccountPath = "/AQ/Ag/Aw";

        String unmarshalled = XML_ADAPTER.unmarshal(parentAccountPath);

        Assert.assertNotNull(unmarshalled, () -> "Add assertion message");
        Assert.assertEquals("/1/2/3", unmarshalled, () -> "Add assertion message");
    }

    @Test
    public void testUnmarshalNull() throws KapuaIllegalArgumentException {
        String unmarshalled = XML_ADAPTER.unmarshal(null);

        Assert.assertNull(unmarshalled, () -> "Add assertion message");
    }

    @Test
    public void testUnmarshalInvalid() {
        List<String> parentAccountPaths = Arrays.asList("/AQ/Ag/Awwerty###", "///", "A//B", "!@#$%^&*()_");

        for (String parentAccountPath : parentAccountPaths) {
            assertThrows(KapuaIllegalArgumentException kapuaIllegalArgumentException,
            		() -> XML_ADAPTER.unmarshal(parentAccountPath));
        }
    }
}