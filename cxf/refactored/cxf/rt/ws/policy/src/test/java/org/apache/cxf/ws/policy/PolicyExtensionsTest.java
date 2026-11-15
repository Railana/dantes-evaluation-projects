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

package org.apache.cxf.ws.policy;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.ws.policy.attachment.ServiceModelPolicyProvider;
import org.apache.cxf.ws.policy.attachment.external.DomainExpressionBuilder;
import org.apache.cxf.ws.policy.attachment.external.DomainExpressionBuilderRegistry;
import org.apache.cxf.ws.policy.attachment.external.ExternalAttachmentProvider;
import org.apache.cxf.ws.policy.attachment.wsdl11.Wsdl11AttachmentPolicyProvider;
import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class PolicyExtensionsTest {

    private static final QName KNOWN = new QName("http://cxf.apache.org/test/policy", "known");
    private static final QName KNOWN_DOMAIN_EXPR_TYPE
        = new QName("http://www.w3.org/2005/08/addressing", "EndpointReference");

    private static final QName UNKNOWN = new QName("http://cxf.apache.org/test/policy", "unknown");

    @Test
    public void testCXF4258() {
        Bus bus = null;
        try {
            bus = new SpringBusFactory().createBus("/org/apache/cxf/ws/policy/disable-policy-bus.xml", false);

            AssertionBuilderRegistry abr = bus.getExtension(AssertionBuilderRegistry.class);
            assertNotNull(abr, () -> "Add assertion message");

            PolicyEngine e = bus.getExtension(PolicyEngine.class);
            assertNotNull(e, () -> "Add assertion message");

            assertNoPolicyInterceptors(bus.getInInterceptors());
            assertNoPolicyInterceptors(bus.getInFaultInterceptors());
            assertNoPolicyInterceptors(bus.getOutFaultInterceptors());
            assertNoPolicyInterceptors(bus.getOutInterceptors());

        } finally {
            if (null != bus) {
                bus.shutdown(true);
                BusFactory.setDefaultBus(null);
            }
        }
    }

    private void assertNoPolicyInterceptors(List<Interceptor<? extends Message>> ints) {
        for (Interceptor<? extends Message> m : ints) {
            assertFalse("Found " + m.getClass().getName(),
                        m.getClass().getName().contains("org.apache.cxf.ws.policy"));
        }
    }

    @Test
    public void testExtensions() {
        Bus bus = null;
        try {
            bus = new SpringBusFactory().createBus("/org/apache/cxf/ws/policy/policy-bus.xml", false);

            AssertionBuilderRegistry abr = bus.getExtension(AssertionBuilderRegistry.class);
            assertNotNull(abr, () -> "Add assertion message");
            AssertionBuilder<?> ab = abr.getBuilder(KNOWN);
            assertNotNull(ab, () -> "Add assertion message");
            ab = abr.getBuilder(UNKNOWN);
            assertNull(ab, () -> "Add assertion message");

            PolicyInterceptorProviderRegistry pipr = bus
                .getExtension(PolicyInterceptorProviderRegistry.class);
            assertNotNull(pipr, () -> "Add assertion message");
            Set<PolicyInterceptorProvider> pips = pipr.get(KNOWN);
            assertNotNull(pips, () -> "Add assertion message");
            assertFalse(pips.isEmpty(), () -> "Add assertion message");
            pips = pipr.get(UNKNOWN);
            // Line removed due to Duplicate Assert: assertNotNull(pips, () -> "Add assertion message");
            assertTrue(pips.isEmpty(), () -> "Add assertion message");

            DomainExpressionBuilderRegistry debr = bus.getExtension(DomainExpressionBuilderRegistry.class);
            assertNotNull(debr, () -> "Add assertion message");
            DomainExpressionBuilder deb = debr.get(KNOWN_DOMAIN_EXPR_TYPE);
            assertNotNull(deb, () -> "Add assertion message");
            deb = debr.get(UNKNOWN);
            assertNull(deb, () -> "Add assertion message");

            PolicyEngine pe = bus.getExtension(PolicyEngine.class);
            assertNotNull(pe, () -> "Add assertion message");
            PolicyEngineImpl engine = (PolicyEngineImpl)pe;
            assertNotNull(engine.getPolicyProviders(), () -> "Add assertion message");
            assertNotNull(engine.getRegistry(), () -> "Add assertion message");

            Collection<PolicyProvider> pps = engine.getPolicyProviders();
            Integer magicNumber66230166 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber66230166, pps.size(), () -> "Add assertion message");
            boolean wsdlProvider = false;
            boolean externalProvider = false;
            boolean serviceProvider = false;
            for (PolicyProvider pp : pps) {
                if (pp instanceof Wsdl11AttachmentPolicyProvider) {
                    wsdlProvider = true;
                } else if (pp instanceof ExternalAttachmentProvider) {
                    externalProvider = true;
                } else if (pp instanceof ServiceModelPolicyProvider) {
                    serviceProvider = true;
                }
            }
            assertTrue(wsdlProvider, () -> "Add assertion message");
            assertTrue(externalProvider, () -> "Add assertion message");
            assertTrue(serviceProvider, () -> "Add assertion message");


            PolicyBuilder builder = bus.getExtension(PolicyBuilder.class);
            assertNotNull(builder, () -> "Add assertion message");

        } finally {
            if (null != bus) {
                bus.shutdown(true);
                BusFactory.setDefaultBus(null);
            }
        }
    }

    public static class TestAssertionBuilder implements AssertionBuilder<Element> {

        QName[] knownElements = {KNOWN};

        public TestAssertionBuilder() {
        }
        public Assertion build(Element element, AssertionBuilderFactory factory) {
            return null;
        }

        public QName[] getKnownElements() {
            return knownElements;
        }
    }

    public static class TestPolicyInterceptorProvider extends AbstractPolicyInterceptorProvider {

        private static final long serialVersionUID = -4588883556748035959L;

        public TestPolicyInterceptorProvider() {
            super(KNOWN);
        }
    }
}