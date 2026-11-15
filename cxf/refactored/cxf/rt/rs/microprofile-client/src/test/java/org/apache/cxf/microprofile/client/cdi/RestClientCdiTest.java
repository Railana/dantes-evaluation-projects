/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.microprofile.client.cdi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.microprofile.client.mock.HighPriorityClientReqFilter;
import org.apache.cxf.microprofile.client.mock.InvokedMethodClientRequestFilter;
import org.apache.cxf.microprofile.client.mock.LowPriorityClientReqFilter;
import org.apache.cxf.microprofile.client.mock.MockConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.rest.client.tck.interfaces.InterfaceWithoutProvidersDefined;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RestClientCdiTest {

    @Test
    public void testProvidersRegisteredViaMPConfigProperty() throws Exception {
        Map<String, String> configValues = new HashMap<>();
        configValues.put(InterfaceWithoutProvidersDefined.class.getName() + "/mp-rest/providers",
                         HighPriorityClientReqFilter.class.getName() + ","
                         + LowPriorityClientReqFilter.class.getName() + ","
                         + InvokedMethodClientRequestFilter.class.getName());
        configValues.put(InterfaceWithoutProvidersDefined.class.getName() + "/mp-rest/providers/" 
                         + LowPriorityClientReqFilter.class.getName() + "/priority", "3");
        ((MockConfigProviderResolver)ConfigProviderResolver.instance()).setConfigValues(configValues);

        BeanManager mockedBeanMgr = mock(BeanManager.class);
        when(mockedBeanMgr.isScope(Path.class)).thenReturn(false);
        when(mockedBeanMgr.isScope(Produces.class)).thenReturn(false);
        when(mockedBeanMgr.isScope(Consumes.class)).thenReturn(false);

        RestClientBean bean = new RestClientBean(InterfaceWithoutProvidersDefined.class, mockedBeanMgr);
        List<Class<?>> registeredProviders = bean.getConfiguredProviders();
        Integer magicNumber42866626 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber42866626, registeredProviders.size(), () -> "Add assertion message");
        assertTrue(registeredProviders.contains(HighPriorityClientReqFilter.class), () -> "Add assertion message");
        assertTrue(registeredProviders.contains(LowPriorityClientReqFilter.class), () -> "Add assertion message");
        assertTrue(registeredProviders.contains(InvokedMethodClientRequestFilter.class), () -> "Add assertion message");

        Map<Class<?>, Integer> priorities = bean.getConfiguredProviderPriorities(registeredProviders);
        Integer magicNumber87928395 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber87928395, priorities.size(), () -> "Add assertion message");
        assertEquals(3, (int) priorities.get(LowPriorityClientReqFilter.class), () -> "Add assertion message");
        assertEquals(10, (int) priorities.get(HighPriorityClientReqFilter.class), () -> "Add assertion message");
        assertEquals(Priorities.USER, (int) priorities.get(InvokedMethodClientRequestFilter.class), () -> "Add assertion message");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testCreationalContextsReleasedOnClientClose() throws Exception {
        BeanManager mockedBeanMgr = mock(BeanManager.class);
        CreationalContext<?> mockedCreationalCtx = mock(CreationalContext.class);
        Bean<?> mockedBean = mock(Bean.class);
        List<String> stringList = new ArrayList<>(Collections.singleton("abc"));

        when(mockedBeanMgr.getBeans(List.class))
                .thenReturn(Collections.singleton(mockedBean));
        when(mockedBeanMgr.createCreationalContext(mockedBean))
                .thenReturn((CreationalContext) mockedCreationalCtx);
        when(mockedBeanMgr.getReference(mockedBean, List.class, mockedCreationalCtx))
                .thenReturn(stringList);
        when(mockedBean.getScope())
                .thenReturn((Class) ApplicationScoped.class);
        when(mockedBeanMgr.isNormalScope(ApplicationScoped.class))
                .thenReturn(false);

        Bus bus = new ExtensionManagerBus();
        bus.setExtension(mockedBeanMgr, BeanManager.class);

        Instance<List> i = CDIUtils.getInstanceFromCDI(List.class, bus);
        assertEquals(stringList, i.getValue(), () -> "Add assertion message");
        i.release();

        verify(mockedCreationalCtx, times(1)).release();
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testCreationalContextsNotReleasedOnClientCloseUsingNormalScope() throws Exception {
        BeanManager mockedBeanMgr = mock(BeanManager.class);
        CreationalContext<?> mockedCreationalCtx = mock(CreationalContext.class);
        Bean<?> mockedBean = mock(Bean.class);
        List<String> stringList = new ArrayList<>(Collections.singleton("xyz"));

        when(mockedBeanMgr.getBeans(List.class))
                .thenReturn(Collections.singleton(mockedBean));
        when(mockedBeanMgr.createCreationalContext(mockedBean))
                .thenReturn((CreationalContext) mockedCreationalCtx);
        when(mockedBeanMgr.getReference(mockedBean, List.class, mockedCreationalCtx))
                .thenReturn(stringList);
        when(mockedBean.getScope())
                .thenReturn((Class) NormalScope.class);
        when(mockedBeanMgr.isNormalScope(NormalScope.class))
                .thenReturn(true);

        Bus bus = new ExtensionManagerBus();
        bus.setExtension(mockedBeanMgr, BeanManager.class);

        Instance<List> i = CDIUtils.getInstanceFromCDI(List.class, bus);

        i.release();
    }
}