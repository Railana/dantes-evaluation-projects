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

package org.apache.cxf.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.Binding;
import org.apache.cxf.bus.managers.PhaseManagerImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseManager;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.OperationInfo;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OutgoingChainInterceptorTest {

    private Bus bus;
    private Service service;
    private Endpoint endpoint;
    private BindingOperationInfo bopInfo;
    private OperationInfo opInfo;
    private BindingMessageInfo bmInfo;
    private MessageInfo mInfo;
    private List<Phase> phases;
    private List<Interceptor<? extends Message>> empty;
    private Binding binding;

    @Before
    public void setUp() throws Exception {

        // Removed line due to General Fixture smell: phases = new ArrayList<>();
        phases.add(new Phase(Phase.SEND, 1000));
        // Removed line due to General Fixture smell: empty = new ArrayList<>();

        bus = mock(Bus.class);
        PhaseManager pm = new PhaseManagerImpl();
        when(bus.getExtension(PhaseManager.class)).thenReturn(pm);

        // Removed line due to General Fixture smell: service = mock(Service.class);
        endpoint = mock(Endpoint.class);
        binding = mock(Binding.class);
        when(endpoint.getBinding()).thenReturn(binding);
        MessageImpl m = new MessageImpl();
        when(binding.createMessage()).thenReturn(m);

        when(endpoint.getService()).thenReturn(service);
        when(endpoint.getOutInterceptors()).thenReturn(empty);
        when(service.getOutInterceptors()).thenReturn(empty);
        when(bus.getOutInterceptors()).thenReturn(empty);

        bopInfo = mock(BindingOperationInfo.class);
        // Removed line due to General Fixture smell: opInfo = mock(OperationInfo.class);
        // Removed line due to General Fixture smell: mInfo = mock(MessageInfo.class);
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: bmInfo = mock(BindingMessageInfo.class);
        when(bopInfo.getOperationInfo()).thenReturn(opInfo);
        when(opInfo.getOutput()).thenReturn(mInfo);
        when(opInfo.isOneWay()).thenReturn(false);
        when(bopInfo.getOutput()).thenReturn(bmInfo);
    }

    @Test
    public void testInterceptor() throws Exception {
        opInfo = mock(OperationInfo.class);
        OutgoingChainInterceptor intc = new OutgoingChainInterceptor();

        MessageImpl m = new MessageImpl();
        Exchange exchange = new ExchangeImpl();
        m.setExchange(exchange);
        exchange.put(Bus.class, bus);
        exchange.put(Endpoint.class, endpoint);
        exchange.put(Binding.class, binding);
        exchange.put(BindingOperationInfo.class, bopInfo);
        exchange.setOutMessage(m);
        intc.handleMessage(m);

        verify(bopInfo, times(3)).getOperationInfo();
    }

}