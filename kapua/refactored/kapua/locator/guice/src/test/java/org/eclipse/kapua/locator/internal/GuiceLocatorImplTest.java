/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat - improved tests coverage
 *******************************************************************************/
package org.eclipse.kapua.locator.internal;

import java.util.List;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaLocatorErrorCodes;
import org.eclipse.kapua.locator.guice.GuiceLocatorImpl;
import org.eclipse.kapua.locator.guice.TestService;
import org.eclipse.kapua.locator.internal.guice.FactoryA;
import org.eclipse.kapua.locator.internal.guice.FactoryB;
import org.eclipse.kapua.locator.internal.guice.ServiceA;
import org.eclipse.kapua.locator.internal.guice.ServiceB;
import org.eclipse.kapua.locator.internal.guice.ServiceC;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class GuiceLocatorImplTest {

    private KapuaLocator locator = GuiceLocatorImpl.getInstance();

    // Line removed due to Ignored Test
    @Ignore
    @Test
    public void shouldLoadTestService() {
        MyTestableService service = locator.getService(MyTestableService.class);
        Assert.assertTrue(service instanceof TestMyTestableService);
    }

    @Test
    public void shouldProvideServiceA() {
        Assert.assertNotNull(locator.getService(ServiceA.class), () -> "Add assertion message");
    }
// Line removed due to Ignored Test
    @Test
    public void shouldProvideFactoryA() {
        Assert.assertNotNull(locator.getFactory(FactoryA.class), () -> "Add assertion message");
    }

    @Test
    public void shouldNotFindFactory() {
        assertThrows(Throwable e,
        		() -> {
        			locator.getFactory(FactoryB.class);
        			Assert.fail("getFactory must throw an exception for un-bound factories");
        		});
    }

    @Test
    public void shouldProvideAll() {
        List<KapuaService> result = locator.getServices();
        Integer magicNumber39312628 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber39312628, result.size(), () -> "Add assertion message");

        {
            KapuaService service = result.get(0);
            Assert.assertTrue(service instanceof ServiceA, () -> "Add assertion message");
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void shouldNotLoad() {
        KapuaLocator locator = new GuiceLocatorImpl("locator-1.xml");
        locator.getService(ServiceC.class);
    }

    static interface MyService extends KapuaService {
    }

    interface MyTestableService extends KapuaService {

    }

    public static class MyTestableServiceImpl implements MyTestableService {

    }

    @TestService
    public static class TestMyTestableService implements MyTestableService {

    }

}