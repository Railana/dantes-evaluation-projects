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
 *******************************************************************************/
package org.eclipse.kapua.commons.configuration.metatype;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class KapuaMetatypeFactoryImplTest {

    @Test
    public void testKapuaTocd() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaTocd(), () -> "Add assertion message");
    }

    @Test
    public void testkapuaTad() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaTad(), () -> "Add assertion message");
    }

    @Test
    public void testKapuaTscalar() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaTscalar("String"), () -> "Add assertion message");
    }

    @Test
    public void testNewKapuaToption() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaToption(), () -> "Add assertion message");
    }

    @Test
    public void testNewKapuaTicon() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaTicon(), () -> "Add assertion message");
    }

    @Test
    public void testNewKapuaTmetadata() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaTmetadata(), () -> "Add assertion message");
    }

    @Test
    public void testNewKapuaTdesignate() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaTdesignate(), () -> "Add assertion message");
    }

    @Test
    public void testNewKapuaTobject() {
        KapuaMetatypeFactoryImpl factory = new KapuaMetatypeFactoryImpl();
        Assert.assertNotNull(factory.newKapuaTobject(), () -> "Add assertion message");
    }
}