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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.ws.policy.builder.primitive.PrimitiveAssertion;
import org.apache.neethi.Assertion;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 *
 */
public class IgnorablePolicyInterceptorProviderTest {
    private static final QName ONEWAY_QNAME = new QName("http://tempuri.org/policy", "OneWay");
    private static final QName DUPLEX_QNAME = new QName("http://tempuri.org/policy", "Duplex");

    @Test
    public void testProvider() {
        Bus bus = null;
        try {
            bus = new SpringBusFactory().createBus("/org/apache/cxf/ws/policy/ignorable-policy.xml", false);

            PolicyInterceptorProviderRegistry pipreg = bus
                .getExtension(PolicyInterceptorProviderRegistry.class);

            assertNotNull(pipreg, () -> "Add assertion message");

            Set<PolicyInterceptorProvider> pips = pipreg.get(ONEWAY_QNAME);

            assertNotNull(pips, () -> "Add assertion message");
            assertFalse(pips.isEmpty(), () -> "Add assertion message");

            Set<PolicyInterceptorProvider> pips2 = pipreg.get(DUPLEX_QNAME);

            assertNotNull(pips2, () -> "Add assertion message");
            assertFalse(pips2.isEmpty(), () -> "Add assertion message");

            assertEquals(pips.iterator().next(), pips2.iterator().next(), () -> "Add assertion message");

        } finally {
            if (null != bus) {
                bus.shutdown(true);
                BusFactory.setDefaultBus(null);
            }
        }
    }

    @Test
    public void testInterceptorAssertion() {
        Bus bus = null;
        try {
            bus = new SpringBusFactory().createBus("/org/apache/cxf/ws/policy/ignorable-policy.xml", false);

            PolicyInterceptorProviderRegistry pipreg = bus
                .getExtension(PolicyInterceptorProviderRegistry.class);

            assertNotNull(pipreg, () -> "Add assertion message");

            Set<PolicyInterceptorProvider> pips = pipreg.get(ONEWAY_QNAME);

            assertNotNull(pips, () -> "Add assertion message");
            assertFalse(pips.isEmpty(), () -> "Add assertion message");

            PolicyInterceptorProvider pip = pips.iterator().next();

            List<Interceptor<Message>> list;
            list = CastUtils.cast(pip.getOutInterceptors());
            verifyAssertion(list);

            list = CastUtils.cast(pip.getInInterceptors());
            verifyAssertion(list);

            list = CastUtils.cast(pip.getOutFaultInterceptors());
            verifyAssertion(list);

            list = CastUtils.cast(pip.getInFaultInterceptors());
            verifyAssertion(list);

        } finally {
            if (null != bus) {
                bus.shutdown(true);
                BusFactory.setDefaultBus(null);
            }
        }
    }

    private void verifyAssertion(List<Interceptor<Message>> list) {
        Message message = new MessageImpl();
        AssertionInfoMap aim = createTestAssertions();
        message.put(AssertionInfoMap.class, aim);
        try {
            aim.check();
            fail("not yet asserted");
        } catch (PolicyException e) {
            // ok
        }
        for (Interceptor<Message> p : list) {
            p.handleMessage(message);
        }

        aim.check();
    }

    @Test
    public void testTwoBuses() {
        Bus cxf1 = null;
        Bus cxf2 = null;
        try {
            ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("/org/apache/cxf/ws/policy/ignorable-policy2.xml");
            cxf1 = (Bus)context.getBean("cxf1");
            assertNotNull(cxf1, () -> "Add assertion message");

            cxf2 = (Bus)context.getBean("cxf2");
            assertNotNull(cxf2, () -> "Add assertion message");

            PolicyInterceptorProviderRegistry pipreg1 = cxf1
                .getExtension(PolicyInterceptorProviderRegistry.class);
            assertNotNull(pipreg1, () -> "Add assertion message");

            PolicyInterceptorProviderRegistry pipreg2 = cxf2
                .getExtension(PolicyInterceptorProviderRegistry.class);
            assertNotNull(pipreg2, () -> "Add assertion message");

            Set<PolicyInterceptorProvider> pips1 = pipreg1.get(ONEWAY_QNAME);

            assertNotNull(pips1, () -> "Add assertion message");
            assertFalse(pips1.isEmpty(), () -> "Add assertion message");

            Set<PolicyInterceptorProvider> pips2 = pipreg2.get(ONEWAY_QNAME);

            assertNotNull(pips2, () -> "Add assertion message");
            assertFalse(pips2.isEmpty(), () -> "Add assertion message");

            assertEquals(pips1.iterator().next(), pips2.iterator().next(), () -> "Add assertion message");

            context.close();
        } finally {
            if (null != cxf1) {
                cxf1.shutdown(true);
            }
            if (null != cxf2) {
                cxf2.shutdown(true);
            }
            BusFactory.setDefaultBus(null);
        }
    }

    private AssertionInfoMap createTestAssertions() {
        AssertionInfoMap aim = new AssertionInfoMap(CastUtils.cast(Collections.EMPTY_LIST,
                                                                   PolicyAssertion.class));
        Assertion a = new PrimitiveAssertion(ONEWAY_QNAME);
        Assertion b = new PrimitiveAssertion(DUPLEX_QNAME);

        AssertionInfo ai = new AssertionInfo(a);
        AssertionInfo bi = new AssertionInfo(b);

        aim.put(ONEWAY_QNAME, Collections.singleton(ai));
        aim.put(DUPLEX_QNAME, Collections.singleton(bi));

        return aim;
    }
}