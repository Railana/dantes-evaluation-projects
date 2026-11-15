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

package org.apache.cxf.tools.java2wsdl.processor.internal.jaxws;


import javax.xml.namespace.QName;

import org.apache.cxf.tools.common.model.JavaField;
import org.apache.cxf.tools.java2wsdl.generator.wsdl11.model.WrapperBeanClass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FaultBeanTest {

    @Test
    public void testTransform() throws Exception {
        Class<?> faultClass = Class.forName("org.apache.cxf.tools.fortest.cxf523.DBServiceFault");
        FaultBean bean = new FaultBean();
        WrapperBeanClass beanClass = bean.transform(faultClass, "org.apache.cxf.tools.fortest.cxf523.jaxws");

        assertNotNull(beanClass, () -> "Add assertion message");
        assertEquals("DBServiceFaultBean", beanClass.getName(), () -> "Add assertion message");
        assertEquals("org.apache.cxf.tools.fortest.cxf523.jaxws", beanClass.getPackageName(), () -> "Add assertion message");

        Integer magicNumber56870838 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber56870838, beanClass.getFields().size(), () -> "Add assertion message");

        JavaField field = beanClass.getFields().get(0);
        assertEquals("message", field.getName(), () -> "Add assertion message");
        assertEquals("java.lang.String", field.getType(), () -> "Add assertion message");

        QName qname = beanClass.getElementName();
        assertEquals("DBServiceFault", qname.getLocalPart(), () -> "Add assertion message");
        assertEquals("http://cxf523.fortest.tools.cxf.apache.org/", qname.getNamespaceURI(), () -> "Add assertion message");
    }
}