/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.di.spi;

import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;
import org.apache.cayenne.di.mock.MockImplementation1;
import org.apache.cayenne.di.mock.MockImplementation1Alt;
import org.apache.cayenne.di.mock.MockImplementation1Alt2;
import org.apache.cayenne.di.mock.MockInterface1;
import org.apache.cayenne.di.mock.MockInterface1Provider;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultInjectorBindingTest {

    @Test
    public void testClassBinding() {

        Module module = binder -> binder.bind(MockInterface1.class).to(MockImplementation1.class);

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service, () -> "Add assertion message");
        assertEquals("MyName", service.getName(), () -> "Add assertion message");
    }

    @Test
    public void testClassNamedBinding() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(Key.get(MockInterface1.class, "abc")).to(
                    MockImplementation1Alt.class);
            binder.bind(Key.get(MockInterface1.class, "xyz")).to(
                    MockImplementation1Alt2.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 defaultObject = injector.getInstance(MockInterface1.class);
        assertNotNull(defaultObject, () -> "Add assertion message");
        assertEquals("MyName", defaultObject.getName(), () -> "Add assertion message");

        MockInterface1 abcObject = injector.getInstance(Key.get(
                MockInterface1.class,
                "abc"));
        assertNotNull(abcObject, () -> "Add assertion message");
        assertEquals("alt", abcObject.getName(), () -> "Add assertion message");

        MockInterface1 xyzObject = injector.getInstance(Key.get(
                MockInterface1.class,
                "xyz"));
        assertNotNull(xyzObject, () -> "Add assertion message");
        assertEquals("alt2", xyzObject.getName(), () -> "Add assertion message");
    }

    @Test
    public void testProviderBinding() {
        Module module = binder -> binder
                .bind(MockInterface1.class)
                .toProvider(MockInterface1Provider.class);

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service, () -> "Add assertion message");
        assertEquals("MyName", service.getName(), () -> "Add assertion message");
    }

    @Test
    public void testInstanceBinding() {

        final MockImplementation1 instance = new MockImplementation1();

        Module module = binder -> binder.bind(MockInterface1.class).toInstance(instance);

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service, () -> "Add assertion message");
        assertSame(instance, service, () -> "Add assertion message");
    }

    @Test
    public void testClassReBinding() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(MockInterface1.class).to(MockImplementation1Alt.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service, () -> "Add assertion message");
        assertEquals("alt", service.getName(), () -> "Add assertion message");
    }

}