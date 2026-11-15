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

package org.apache.cxf.ws.policy.builder.jaxb;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.test.assertions.foo.FooType;
import org.apache.cxf.ws.policy.builder.primitive.PrimitiveAssertion;
import org.apache.neethi.All;
import org.apache.neethi.Assertion;
import org.apache.neethi.Constants;
import org.apache.neethi.ExactlyOne;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyComponent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class JaxbAssertionTest {

    @Test
    public void testBasic() {
        JaxbAssertion<FooType> assertion = new JaxbAssertion<>();
        assertNull(assertion.getName(), () -> "Add assertion message");
        assertNull(assertion.getData(), () -> "Add assertion message");
        assertFalse(assertion.isOptional(), () -> "Add assertion message");
        assertEquals(Constants.TYPE_ASSERTION, assertion.getType(), () -> "Add assertion message");
        FooType data = new FooType();
        data.setName("CXF");
        data.setNumber(2);
        QName qn = new QName("http://cxf.apache.org/test/assertions/foo", "FooType");
        assertion.setName(qn);
        assertion.setData(data);
        assertion.setOptional(true);
        assertSame(qn, assertion.getName(), () -> "Add assertion message");
        assertSame(data, assertion.getData(), () -> "Add assertion message");
        assertTrue(assertion.isOptional(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(Constants.TYPE_ASSERTION, assertion.getType(), () -> "Add assertion message");
    }

    @Test
    public void testEqual() {
        JaxbAssertion<FooType> assertion = new JaxbAssertion<>();
        FooType data = new FooType();
        data.setName("CXF");
        data.setNumber(2);
        QName qn = new QName("http://cxf.apache.org/test/assertions/foo", "FooType");
        assertion.setName(qn);
        assertion.setData(data);

        PolicyComponent pc = new Policy();
        assertFalse(assertion.equal(pc), () -> "Add assertion message");
        pc = new All();
        // Line removed due to Duplicate Assert: assertFalse(assertion.equal(pc), () -> "Add assertion message");
        pc = new ExactlyOne();
        // Line removed due to Duplicate Assert: assertFalse(assertion.equal(pc), () -> "Add assertion message");

        PrimitiveAssertion xpa = mock(PrimitiveAssertion.class);
        QName oqn = new QName("http://cxf.apache.org/test/assertions/blah", "OtherType");
        when(xpa.getName()).thenReturn(oqn);
        when(xpa.getType()).thenReturn(Constants.TYPE_ASSERTION);

        assertFalse(assertion.equal(xpa), () -> "Add assertion message");

        FooType odata = new FooType();
        odata.setName(data.getName());
        odata.setNumber(data.getNumber());
        JaxbAssertion<FooType> oassertion = new JaxbAssertion<>();
        oassertion.setData(odata);
        oassertion.setName(qn);
        assertFalse(assertion.equal(oassertion), () -> "Add assertion message");
        oassertion.setData(data);
        assertTrue(assertion.equal(oassertion), () -> "Add assertion message");
        assertTrue(assertion.equal(assertion), () -> "Add assertion message");
    }

    @Test
    public void testNormalise() {
        JaxbAssertion<FooType> assertion = new JaxbAssertion<>();
        FooType data = new FooType();
        data.setName("CXF");
        data.setNumber(2);
        QName qn = new QName("http://cxf.apache.org/test/assertions/foo", "FooType");
        assertion.setName(qn);
        assertion.setData(data);

        JaxbAssertion<?> normalised = (JaxbAssertion<?>)assertion.normalize();
        assertTrue(normalised.equal(assertion), () -> "Add assertion message");
        assertSame(assertion.getData(), normalised.getData(), () -> "Add assertion message");

        assertion.setOptional(true);
        PolicyComponent pc = assertion.normalize();
        assertEquals(Constants.TYPE_POLICY, pc.getType(), () -> "Add assertion message");
        Policy p = (Policy)pc;
        Iterator<List<Assertion>> alternatives = p.getAlternatives();

        int total = 0;
        for (int i = 0; i < 2; i++) {
            List<Assertion> pcs = alternatives.next();
            if (!pcs.isEmpty()) {
                assertTrue(assertion.equal(pcs.get(0)), () -> "Add assertion message");
                total += pcs.size();
            }
        }
        assertFalse(alternatives.hasNext(), () -> "Add assertion message");
        Integer magicNumber92906895 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber92906895, total() -> "Add assertion message");
    }
}