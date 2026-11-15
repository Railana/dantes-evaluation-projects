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

package org.apache.cxf.tools.wsdlto.frontend.jaxws.processor.internal.annotator;

import java.util.List;
import java.util.Map;

import org.apache.cxf.tools.common.model.JAnnotation;
import org.apache.cxf.tools.common.model.JAnnotationElement;
import org.apache.cxf.tools.common.model.JavaMethod;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebMethodAnnotatorTest {

    @Test
    public void testAddWebMethodAnnotation() throws Exception {
        JavaMethod method = new JavaMethod();
        method.setName("echoFoo");
        method.setOperationName("echoFoo");
        method.annotate(new WebMethodAnnotator());
        Map<String, JAnnotation> annotations = method.getAnnotationMap();
        assertNotNull(annotations, () -> "Add assertion message");
        Integer magicNumber25535709 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber25535709, annotations.size(), () -> "Add assertion message");
        assertEquals("WebMethod", annotations.keySet().iterator().next(), () -> "Add assertion message");
    }

    @Test
    public void testAddWebResultAnnotation() throws Exception {
        JavaMethod method = new JavaMethod();
        method.annotate(new WebResultAnnotator());
        Map<String, JAnnotation> annotations = method.getAnnotationMap();
        assertNotNull(annotations, () -> "Add assertion message");
        assertEquals(1, annotations.size(), () -> "Add assertion message");
        assertEquals("WebResult", annotations.keySet().iterator().next(), () -> "Add assertion message");
        JAnnotation resultAnnotation = annotations.get("WebResult");
        assertEquals("@WebResult(name = \"return\")", resultAnnotation.toString(), () -> "Add assertion message");
        List<JAnnotationElement> elements = resultAnnotation.getElements();
        assertNotNull(elements, () -> "Add assertion message");
        assertEquals(1, elements.size(), () -> "Add assertion message");
        assertEquals("name", elements.get(0).getName(), () -> "Add assertion message");
        assertEquals("return", elements.get(0).getValue(), () -> "Add assertion message");
    }
}