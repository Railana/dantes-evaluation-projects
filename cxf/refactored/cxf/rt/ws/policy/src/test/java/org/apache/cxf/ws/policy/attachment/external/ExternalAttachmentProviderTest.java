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

package org.apache.cxf.ws.policy.attachment.external;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import org.apache.cxf.Bus;
import org.apache.cxf.service.model.BindingFaultInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyException;
import org.apache.cxf.ws.policy.builder.primitive.PrimitiveAssertion;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 *
 */
public class ExternalAttachmentProviderTest {

    private static final QName TEST_ASSERTION_TYPE = new QName("http://a.b.c", "x");


    private Policy policy;
    private Assertion assertion;
    private PolicyAttachment attachment;
    private Collection<PolicyAttachment> attachments = new ArrayList<>();


    @Test
    public void testBasic() {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        assertNull(eap.getLocation(), () -> "Add assertion message");
        Resource uri = mock(Resource.class);
        eap.setLocation(uri);
        assertSame(uri, eap.getLocation(), () -> "Add assertion message");

    }

    @Test
    public void testGetEffectiveFaultPolicy() {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        BindingFaultInfo bfi = mock(BindingFaultInfo.class);
        setUpAttachment(bfi, false, eap);
        assertNull(eap.getEffectivePolicy(bfi, null), () -> "Add assertion message");

        setUpAttachment(bfi, true, eap);
        assertSame(assertion, eap.getEffectivePolicy(bfi, null).getAssertions().get(0), () -> "Add assertion message");
    }

    @Test
    public void testGetEffectiveMessagePolicy() {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        BindingMessageInfo bmi = mock(BindingMessageInfo.class);
        setUpAttachment(bmi, false, eap);
        assertNull(eap.getEffectivePolicy(bmi, null), () -> "Add assertion message");

        setUpAttachment(bmi, true, eap);
        assertSame(assertion, eap.getEffectivePolicy(bmi, null).getAssertions().get(0), () -> "Add assertion message");
    }

    @Test
    public void testGetEffectiveOperationPolicy() {
        BindingOperationInfo boi = mock(BindingOperationInfo.class);
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        setUpAttachment(boi, false, eap);
        assertNull(eap.getEffectivePolicy(boi, null), () -> "Add assertion message");

        setUpAttachment(boi, true, eap);
        assertSame(assertion, eap.getEffectivePolicy(boi, null).getAssertions().get(0), () -> "Add assertion message");
    }

    @Test
    public void testGetEffectiveEndpointPolicy() {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        EndpointInfo ei = mock(EndpointInfo.class);
        setUpAttachment(ei, false, eap);
        assertNull(eap.getEffectivePolicy(ei, null), () -> "Add assertion message");

        setUpAttachment(ei, true, eap);
        assertSame(assertion, eap.getEffectivePolicy(ei, null).getAssertions().get(0), () -> "Add assertion message");
    }

    @Test
    public void testGetEffectiveServicePolicy() {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        ServiceInfo si = mock(ServiceInfo.class);
        setUpAttachment(si, false, eap);
        assertNull(eap.getEffectivePolicy(si, null), () -> "Add assertion message");

        setUpAttachment(si, true, eap);
        assertSame(assertion, eap.getEffectivePolicy(si, null).getAssertions().get(0), () -> "Add assertion message");
    }

    @Test
    public void testReadDocumentNotExisting() throws MalformedURLException {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        URL url = ExternalAttachmentProviderTest.class.getResource("resources/attachments1.xml");
        String uri = url.toExternalForm();
        uri = uri.replaceAll("attachments1.xml", "attachments0.xml");
        eap.setLocation(new UrlResource(uri));
        assertThrows(PolicyException ex,
        		() -> {
        			eap.readDocument();
        			fail("Expected PolicyException not thrown.");
        		});
    }

    @Test
    public void testReadDocumentWithoutAttachmentElements() throws MalformedURLException {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        URL url = ExternalAttachmentProviderTest.class.getResource("resources/attachments1.xml");
        String uri = url.toExternalForm();
        eap.setLocation(new UrlResource(uri));
        eap.readDocument();
        assertTrue(eap.getAttachments().isEmpty(), () -> "Add assertion message");
    }

    @Test
    public void testReadDocumentAttachmentElementWithoutAppliesTo() throws MalformedURLException {
        ExternalAttachmentProvider eap = new ExternalAttachmentProvider();
        URL url = ExternalAttachmentProviderTest.class.getResource("resources/attachments2.xml");
        String uri = url.toExternalForm();
        eap.setLocation(new UrlResource(uri));
        eap.readDocument();
        assertTrue(eap.getAttachments().isEmpty(), () -> "Add assertion message");
    }

    @Test
    public void testReadDocumentUnknownDomainExpression() throws MalformedURLException {

        Bus bus = mock(Bus.class);

        DomainExpressionBuilderRegistry debr = mock(DomainExpressionBuilderRegistry.class);
        when(bus.getExtension(DomainExpressionBuilderRegistry.class)).thenReturn(debr);
        when(debr.build(isA(Element.class)))
            .thenThrow(new PolicyException(new Exception()));
        URL url = ExternalAttachmentProviderTest.class.getResource("resources/attachments3.xml");
        String uri = url.toExternalForm();

        ExternalAttachmentProvider eap = new ExternalAttachmentProvider(bus);
        eap.setLocation(new UrlResource(uri));
        try {
            eap.readDocument();
            fail("Expected PolicyException not thrown.");
        } catch (PolicyException ex) {
            // expected
        }
    }

    @Test
    public void testReadDocumentEPRDomainExpression() throws MalformedURLException {

        Bus bus = mock(Bus.class);

        DomainExpressionBuilderRegistry debr = mock(DomainExpressionBuilderRegistry.class);
        when(bus.getExtension(DomainExpressionBuilderRegistry.class)).thenReturn(debr);
        DomainExpression de = mock(DomainExpression.class);
        when(debr.build(isA(Element.class))).thenReturn(de);
        PolicyBuilder pb = mock(PolicyBuilder.class);
        when(bus.getExtension(PolicyBuilder.class)).thenReturn(pb);
        Policy p = mock(Policy.class);
        when(pb.getPolicy(isA(Element.class))).thenReturn(p);


        ExternalAttachmentProvider eap = new ExternalAttachmentProvider(bus);
        URL url = ExternalAttachmentProviderTest.class.getResource("resources/attachments4.xml");
        String uri = url.toExternalForm();
        eap.setLocation(new UrlResource(uri));
        eap.readDocument();
        Integer magicNumber20506555 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber20506555, eap.getAttachments().size(), () -> "Add assertion message");
        PolicyAttachment pa = eap.getAttachments().iterator().next();
        assertSame(p, pa.getPolicy(), () -> "Add assertion message");
        assertEquals(1, pa.getDomainExpressions().size(), () -> "Add assertion message");
        assertSame(de, pa.getDomainExpressions().iterator().next(), () -> "Add assertion message");
    }

    void setUpAttachment(Object subject, boolean applies, ExternalAttachmentProvider eap) {
        attachments.clear();
        attachment = mock(PolicyAttachment.class);
        attachments.add(attachment);
        policy = new Policy();
        assertion = new PrimitiveAssertion(TEST_ASSERTION_TYPE);
        policy.addAssertion(assertion);
        eap.setAttachments(attachments);
        if (subject instanceof ServiceInfo) {
            when(attachment.appliesTo((ServiceInfo)subject)).thenReturn(applies);
        } else if (subject instanceof EndpointInfo) {
            when(attachment.appliesTo((EndpointInfo)subject)).thenReturn(applies);
        } else if (subject instanceof BindingOperationInfo) {
            when(attachment.appliesTo((BindingOperationInfo)subject)).thenReturn(applies);
        } else if (subject instanceof BindingMessageInfo) {
            when(attachment.appliesTo((BindingMessageInfo)subject)).thenReturn(applies);
        } else if (subject instanceof BindingFaultInfo) {
            when(attachment.appliesTo((BindingFaultInfo)subject)).thenReturn(applies);
        } else {
            System.err.println("subject class: " + subject.getClass());
        }
        if (applies) {
            when(attachment.getPolicy()).thenReturn(policy);
        }
    }



}