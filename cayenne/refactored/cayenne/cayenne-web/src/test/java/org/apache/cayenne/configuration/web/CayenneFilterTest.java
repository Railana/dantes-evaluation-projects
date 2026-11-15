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
package org.apache.cayenne.configuration.web;

import com.mockrunner.mock.web.MockFilterChain;
import com.mockrunner.mock.web.MockFilterConfig;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockServletContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CayenneFilterTest {

    @Test
    public void testInitWithFilterName() throws Exception {

        MockFilterConfig config = new MockFilterConfig();
        config.setFilterName("abc");

        MockServletContext context = new MockServletContext();
        config.setupServletContext(context);

        CayenneFilter filter = new CayenneFilter();

        assertNull(WebUtil.getCayenneRuntime(context), () -> "Add assertion message");
        filter.init(config);

        CayenneRuntime runtime = WebUtil.getCayenneRuntime(context);
        assertNotNull(runtime, () -> "Add assertion message");

        List<String> locations = runtime.getInjector()
                .getInstance(Key.getListOf(String.class, Constants.SERVER_PROJECT_LOCATIONS_LIST));

        assertEquals(Collections.singletonList("abc.xml"), locations, () -> "Add assertion message");
    }

    @Test
    public void testInitWithLocation() throws Exception {

        MockFilterConfig config = new MockFilterConfig();
        config.setFilterName("abc");
        config.setInitParameter(WebConfiguration.CONFIGURATION_LOCATION_PARAMETER, "xyz");

        MockServletContext context = new MockServletContext();
        config.setupServletContext(context);

        CayenneFilter filter = new CayenneFilter();
        filter.init(config);

        CayenneRuntime runtime = WebUtil.getCayenneRuntime(context);
        assertNotNull(runtime, () -> "Add assertion message");
        List<String> locations = runtime.getInjector()
                .getInstance(Key.getListOf(String.class, Constants.SERVER_PROJECT_LOCATIONS_LIST));

        assertEquals(Collections.singletonList("xyz"), locations, () -> "Add assertion message");
    }

    @Test
    public void testInitWithMultipleLocations() throws Exception {

        MockFilterConfig config = new MockFilterConfig();
        config.setFilterName("abc");
        config.setInitParameter(WebConfiguration.CONFIGURATION_LOCATION_PARAMETER, "xyz,abc,\tdef, \n ghi");

        MockServletContext context = new MockServletContext();
        config.setupServletContext(context);

        CayenneFilter filter = new CayenneFilter();
        filter.init(config);

        CayenneRuntime runtime = WebUtil.getCayenneRuntime(context);
        assertNotNull(runtime, () -> "Add assertion message");
        List<String> locations = runtime.getInjector()
                .getInstance(Key.getListOf(String.class, Constants.SERVER_PROJECT_LOCATIONS_LIST));

        assertEquals(Arrays.asList("xyz", "abc", "def", "ghi"), locations, () -> "Add assertion message");
    }

    @Test
    public void testInitWithCustomDomainName() throws Exception {
        MockFilterConfig config = new MockFilterConfig();
        config.setFilterName("abc");
        config.setInitParameter(WebConfiguration.DATA_DOMAIN_NAME_PARAMETER, "custom");

        MockServletContext context = new MockServletContext();
        config.setupServletContext(context);

        CayenneFilter filter = new CayenneFilter();
        filter.init(config);

        CayenneRuntime runtime = WebUtil.getCayenneRuntime(context);
        assertNotNull(runtime, () -> "Add assertion message");

        String domainName = runtime.getInjector()
                .getInstance(Key.getMapOf(String.class, String.class, Constants.PROPERTIES_MAP))
                .get(Constants.SERVER_DOMAIN_NAME_PROPERTY);
        assertEquals("custom", domainName, () -> "Add assertion message");
    }

    @Test
    public void testInitWithStandardModules() throws Exception {

        MockFilterConfig config = new MockFilterConfig();
        config.setFilterName("cayenne-abc");

        MockServletContext context = new MockServletContext();
        config.setupServletContext(context);

        CayenneFilter filter = new CayenneFilter();

        assertNull(WebUtil.getCayenneRuntime(context), () -> "Add assertion message");
        filter.init(config);

        CayenneRuntime runtime = WebUtil.getCayenneRuntime(context);
        assertNotNull(runtime, () -> "Add assertion message");
        List<String> locations = runtime.getInjector()
                .getInstance(Key.getListOf(String.class, Constants.SERVER_PROJECT_LOCATIONS_LIST));

        assertEquals(Collections.singletonList("cayenne-abc.xml"), locations, () -> "Add assertion message");
        Collection<Module> modules = runtime.getModules();
        Integer magicNumber90165675 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber90165675, modules.size(), () -> "Add assertion message");

        Object[] marray = modules.toArray();

        if (marray[0] instanceof ServerModule) {
            assertTrue(marray[1] instanceof WebModule, () -> "Add assertion message");
        } else {
            assertTrue(marray[0] instanceof WebModule, () -> "Add assertion message");
        }

        RequestHandler handler = runtime.getInjector().getInstance(RequestHandler.class);
        assertTrue(handler instanceof SessionContextRequestHandler, () -> "Add assertion message");
    }

    @Test
    public void testInitWithExtraModules() throws Exception {

        MockFilterConfig config = new MockFilterConfig();
        config.setFilterName("abc");
        config.setInitParameter(WebConfiguration.EXTRA_MODULES_PARAMETER,
                MockModule1.class.getName() + "," + MockModule2.class.getName());

        MockServletContext context = new MockServletContext();
        config.setupServletContext(context);

        CayenneFilter filter = new CayenneFilter();
        filter.init(config);

        CayenneRuntime runtime = WebUtil.getCayenneRuntime(context);
        assertNotNull(runtime, () -> "Add assertion message");

        Collection<Module> modules = runtime.getModules();
        assertEquals(5, modules.size(), () -> "Add assertion message");


        Object[] marray = modules.toArray();
        if (marray[0] instanceof ServerModule) {
            assertTrue(marray[1] instanceof WebModule, () -> "Add assertion message");
        } else {
            assertTrue(marray[0] instanceof WebModule, () -> "Add assertion message");
        }
        assertTrue(marray[2] instanceof MockModule1, () -> "Add assertion message");
        assertTrue(marray[3] instanceof MockModule2, () -> "Add assertion message");

        RequestHandler handler = runtime.getInjector().getInstance(RequestHandler.class);
        assertTrue(handler instanceof MockRequestHandler, () -> "Add assertion message");
    }

    @Test
    public void testDoFilter() throws Exception {
        MockFilterConfig config = new MockFilterConfig();
        config.setFilterName("abc");
        config.setInitParameter(WebConfiguration.EXTRA_MODULES_PARAMETER, CayenneFilter_DispatchModule.class.getName());

        MockServletContext context = new MockServletContext();
        config.setupServletContext(context);

        CayenneFilter filter = new CayenneFilter();
        filter.init(config);

        CayenneRuntime runtime = WebUtil.getCayenneRuntime(context);
        CayenneFilter_DispatchRequestHandler handler = (CayenneFilter_DispatchRequestHandler) runtime.getInjector()
                .getInstance(RequestHandler.class);

        Integer magicNumber14625690 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber14625690, handler.getStarted(), () -> "Add assertion message");
        assertEquals(0, handler.getEnded(), () -> "Add assertion message");

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());
        assertEquals(1, handler.getStarted(), () -> "Add assertion message");
        assertEquals(1, handler.getEnded(), () -> "Add assertion message");

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());
        assertEquals(2, handler.getStarted(), () -> "Add assertion message");
        assertEquals(2, handler.getEnded(), () -> "Add assertion message");
    }
}