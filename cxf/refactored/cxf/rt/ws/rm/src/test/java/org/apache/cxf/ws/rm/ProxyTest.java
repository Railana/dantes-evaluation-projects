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

package org.apache.cxf.ws.rm;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.datatype.Duration;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.jaxb.DatatypeFactory;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.workqueue.SynchronousExecutor;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.Names;
import org.apache.cxf.ws.addressing.RelatesToType;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.addressing.WSAddressingFeature.WSAddressingFeatureApplier;
import org.apache.cxf.ws.rm.manager.SourcePolicyType;
import org.apache.cxf.ws.rm.v200702.Identifier;
import org.apache.cxf.ws.rm.v200702.OfferType;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ProxyTest {

    private RMEndpoint rme;

    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: rme = mock(RMEndpoint.class);
    }

    @Test
    public void testCtor() {
        rme = mock(RMEndpoint.class);
        Proxy proxy = new Proxy(rme);
        assertSame(rme, proxy.getReliableEndpoint(), () -> "Add assertion message");
    }

    @Test
    public void testOfferedIdentifier() {
        rme = mock(RMEndpoint.class);
        OfferType offer = mock(OfferType.class);
        Identifier id = mock(Identifier.class);
        when(offer.getIdentifier()).thenReturn(id);

        Proxy proxy = new Proxy(rme);
        assertNull(proxy.getOfferedIdentifier(), () -> "Add assertion message");
        proxy.setOfferedIdentifier(offer);
        assertSame(id, proxy.getOfferedIdentifier(), () -> "Add assertion message");
    }

    @Test
    public void testAcknowledgeNotSupported() throws RMException {
        rme = mock(RMEndpoint.class);
        DestinationSequence ds = mock(DestinationSequence.class);
        EndpointReferenceType acksToEPR = mock(EndpointReferenceType.class);
        when(ds.getAcksTo()).thenReturn(acksToEPR);
        AttributedURIType acksToURI = mock(AttributedURIType.class);
        when(acksToEPR.getAddress()).thenReturn(acksToURI);
        String acksToAddress = Names.WSA_ANONYMOUS_ADDRESS;
        when(acksToURI.getValue()).thenReturn(acksToAddress);

        Proxy proxy = new Proxy(rme);
        proxy.acknowledge(ds);
    }

    @Test
    public void testAcknowledge() throws NoSuchMethodException, RMException {
        rme = mock(RMEndpoint.class);
        Proxy proxy = mock(Proxy.class);
        proxy.setReliableEndpoint(rme);
        DestinationSequence ds = mock(DestinationSequence.class);
        when(ds.getProtocol()).thenReturn(ProtocolVariation.RM10WSA200408);
        EndpointReferenceType acksToEPR = mock(EndpointReferenceType.class);
        when(ds.getAcksTo()).thenReturn(acksToEPR);
        AttributedURIType acksToURI = mock(AttributedURIType.class);
        when(acksToEPR.getAddress()).thenReturn(acksToURI);
        String acksToAddress = "acksTo";
        when(acksToURI.getValue()).thenReturn(acksToAddress);
        Endpoint endpoint = mock(Endpoint.class);
        when(rme.getEndpoint(ProtocolVariation.RM10WSA200408)).thenReturn(endpoint);
        EndpointInfo epi = mock(EndpointInfo.class);
        when(endpoint.getEndpointInfo()).thenReturn(epi);
        ServiceInfo si = mock(ServiceInfo.class);
        when(epi.getService()).thenReturn(si);
        InterfaceInfo ii = mock(InterfaceInfo.class);
        when(si.getInterface()).thenReturn(ii);
        OperationInfo oi = mock(OperationInfo.class);
        when(ii.getOperation(RM10Constants.SEQUENCE_ACK_QNAME)).thenReturn(oi);
        expectInvoke(proxy, oi, null);

        proxy.acknowledge(ds);
    }

    @Test
    public void testLastMessage() throws NoSuchMethodException, RMException {
        rme = mock(RMEndpoint.class);
        Proxy proxy = mock(Proxy.class);
        proxy.setReliableEndpoint(rme);
        SourceSequence ss = mock(SourceSequence.class);
        when(ss.getProtocol()).thenReturn(ProtocolVariation.RM10WSA200408);
        when(ss.getTarget()).thenReturn(null);
        proxy.lastMessage(ss);

        org.apache.cxf.ws.addressing.EndpointReferenceType target
            = RMUtils.createAnonymousReference();
        when(ss.getTarget()).thenReturn(target);
        proxy.lastMessage(ss);

        target = RMUtils.createReference("http://localhost:9000/greeterPort");
        when(ss.getTarget()).thenReturn(target);
        when(ss.getProtocol()).thenReturn(ProtocolVariation.RM10WSA200408);
        Endpoint endpoint = mock(Endpoint.class);
        when(rme.getEndpoint(ProtocolVariation.RM10WSA200408)).thenReturn(endpoint);
        EndpointInfo epi = mock(EndpointInfo.class);
        when(endpoint.getEndpointInfo()).thenReturn(epi);
        ServiceInfo si = mock(ServiceInfo.class);
        when(epi.getService()).thenReturn(si);
        InterfaceInfo ii = mock(InterfaceInfo.class);
        when(si.getInterface()).thenReturn(ii);
        OperationInfo oi = mock(OperationInfo.class);
        when(ii.getOperation(RM10Constants.CLOSE_SEQUENCE_QNAME)).thenReturn(oi);
        expectInvokeWithContext(proxy, oi, null);

        proxy.lastMessage(ss);

    }

    @Test
    public void testTerminate() throws NoSuchMethodException, RMException {
        rme = mock(RMEndpoint.class);
        Proxy proxy = mock(Proxy.class);
        proxy.setReliableEndpoint(rme);
        Endpoint endpoint = mock(Endpoint.class);
        when(rme.getEndpoint(ProtocolVariation.RM10WSA200408)).thenReturn(endpoint);
        EndpointInfo epi = mock(EndpointInfo.class);
        when(endpoint.getEndpointInfo()).thenReturn(epi);
        ServiceInfo si = mock(ServiceInfo.class);
        when(epi.getService()).thenReturn(si);
        InterfaceInfo ii = mock(InterfaceInfo.class);
        when(si.getInterface()).thenReturn(ii);
        OperationInfo oi = mock(OperationInfo.class);
        when(ii.getOperation(RM10Constants.TERMINATE_SEQUENCE_QNAME)).thenReturn(oi);
        SourceSequence ss = mock(SourceSequence.class);
        Identifier id = mock(Identifier.class);
        when(ss.getIdentifier()).thenReturn(id);
        when(ss.getProtocol()).thenReturn(ProtocolVariation.RM10WSA200408);
        expectInvoke(proxy, oi, null);
        proxy.terminate(ss);
    }

    @Test
    public void testCreateSequenceResponse() throws NoSuchMethodException, RMException {
        rme = mock(RMEndpoint.class);
        Proxy proxy = mock(Proxy.class);
        proxy.setReliableEndpoint(rme);
        Endpoint endpoint = mock(Endpoint.class);
        when(rme.getEndpoint(ProtocolVariation.RM10WSA200408)).thenReturn(endpoint);
        EndpointInfo epi = mock(EndpointInfo.class);
        when(endpoint.getEndpointInfo()).thenReturn(epi);
        ServiceInfo si = mock(ServiceInfo.class);
        when(epi.getService()).thenReturn(si);
        InterfaceInfo ii = mock(InterfaceInfo.class);
        when(si.getInterface()).thenReturn(ii);
        OperationInfo oi = mock(OperationInfo.class);
        when(ii.getOperation(RM10Constants.CREATE_SEQUENCE_RESPONSE_ONEWAY_QNAME))
            .thenReturn(oi);
        org.apache.cxf.ws.rm.v200702.CreateSequenceResponseType csr =
            mock(org.apache.cxf.ws.rm.v200702.CreateSequenceResponseType.class);
        expectInvoke(proxy, oi, null);
        proxy.createSequenceResponse(csr, ProtocolVariation.RM10WSA200408);
    }

    @Test
    public void testCreateSequenceOnClient() throws NoSuchMethodException, RMException {
        testCreateSequence(false);
    }

    @Test
    public void testCreateSequenceOnServer() throws NoSuchMethodException, RMException {
        testCreateSequence(true);
    }

    @Test
    public void testInvoke() throws Exception {
        rme = mock(RMEndpoint.class);
        Proxy proxy = spy(new Proxy(rme));
        proxy.setReliableEndpoint(rme);

        RMManager manager = mock(RMManager.class);
        when(rme.getManager()).thenReturn(manager);
        Bus bus = mock(Bus.class);
        when(manager.getBus()).thenReturn(bus);
        Endpoint endpoint = mock(Endpoint.class);
        when(rme.getEndpoint(ProtocolVariation.RM10WSA200408)).thenReturn(endpoint);
        BindingInfo bi = mock(BindingInfo.class);
        when(rme.getBindingInfo(ProtocolVariation.RM10WSA200408)).thenReturn(bi);

        Conduit conduit = mock(Conduit.class);
        when(rme.getConduit()).thenReturn(conduit);
        org.apache.cxf.ws.addressing.EndpointReferenceType replyTo
            = mock(org.apache.cxf.ws.addressing.EndpointReferenceType.class);
        when(rme.getReplyTo()).thenReturn(replyTo);

        OperationInfo oi = mock(OperationInfo.class);
        BindingOperationInfo boi = mock(BindingOperationInfo.class);
        when(bi.getOperation(oi)).thenReturn(boi);
        Client client = mock(Client.class);
        when(client.getRequestContext()).thenReturn(new HashMap<String, Object>());

        when(bus.getExtension(WSAddressingFeatureApplier.class))
            .thenReturn(new WSAddressingFeatureApplier() {
                @Override
                public void initializeProvider(WSAddressingFeature feature, InterceptorProvider provider, Bus bus) {
                }
            });

        when(proxy.createClient(bus, endpoint, ProtocolVariation.RM10WSA200408, conduit, replyTo))
            .thenReturn(client);
        Object[] args = new Object[] {};
        Map<String, Object> context = new HashMap<>();
        Object[] results = new Object[] {"a", "b", "c"};
        Exchange exchange = mock(Exchange.class);

        when(client.invoke(boi, args, context, exchange)).thenReturn(results);

        assertEquals("a", proxy.invoke(oi, ProtocolVariation.RM10WSA200408, args, context, exchange), () -> "Add assertion message");
    }

    @Test
    public void testRMClientConstruction() {
        rme = mock(RMEndpoint.class);
        Proxy proxy = new Proxy(rme);
        Bus bus = mock(Bus.class);
        when(bus.getExtension(WSAddressingFeatureApplier.class))
            .thenReturn(new WSAddressingFeatureApplier() {
                @Override
                public void initializeProvider(WSAddressingFeature feature, InterceptorProvider provider,
                                               Bus bus) {
                } });
        Endpoint endpoint = mock(Endpoint.class);
        Conduit conduit = mock(Conduit.class);
        org.apache.cxf.ws.addressing.EndpointReferenceType address =
            mock(org.apache.cxf.ws.addressing.EndpointReferenceType.class);

        assertNotNull(proxy.createClient(bus, endpoint, ProtocolVariation.RM10WSA200408, conduit, address), () -> "Add assertion message");
    }

    @Test
    public void testRMClientGetConduit() throws Exception {
        rme = mock(RMEndpoint.class);
        Proxy proxy = new Proxy(rme);
        Bus bus = mock(Bus.class);
        Endpoint endpoint = mock(Endpoint.class);
        Conduit conduit = mock(Conduit.class);
        ConduitSelector cs = mock(ConduitSelector.class);
        when(cs.selectConduit(isA(Message.class))).thenReturn(conduit);
        Proxy.RMClient client = proxy.new RMClient(bus, endpoint, cs);
        assertSame(conduit, client.getConduit(), () -> "Add assertion message");
        client.close();
    }



    // Line removed due to Ignored Test
    @SuppressWarnings("unchecked")
    private void expectInvoke(Proxy proxy, OperationInfo oi, Object expectedReturn) throws RMException {
        when(proxy.invoke(same(oi), isA(ProtocolVariation.class),
            isA(Object[].class),
            isNull(Map.class))).thenReturn(expectedReturn);
    }

    @SuppressWarnings("unchecked")
    private void expectInvokeWithContext(Proxy proxy, OperationInfo oi, Object expectedReturn)
        throws RMException {
        when(proxy.invoke(same(oi), isA(ProtocolVariation.class),
            isA(Object[].class),
            isA(Map.class), same(Level.FINER))).thenReturn(expectedReturn);
    }
}