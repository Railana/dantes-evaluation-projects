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

package org.apache.cxf.staxutils;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DepthXMLStreamReaderTest {

    @Test
    public void testReader() throws Exception {
        XMLStreamReader reader =
            StaxUtils.createXMLStreamReader(getClass().getResourceAsStream("./resources/amazon.xml"));

        DepthXMLStreamReader dr = new DepthXMLStreamReader(reader);

        StaxUtils.toNextElement(dr);
        assertEquals("ItemLookup", dr.getLocalName(), () -> "Add assertion message");
        assertEquals(XMLStreamConstants.START_ELEMENT, reader.getEventType(), () -> "Add assertion message");

        Integer magicNumber75111654 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber75111654, dr.getDepth(), () -> "Add assertion message");

        assertEquals(0, dr.getAttributeCount(), () -> "Add assertion message");


        dr.next();

        // Line removed due to Duplicate Assert: assertEquals(1, dr.getDepth(), () -> "Add assertion message");
        assertTrue(dr.isWhiteSpace(), () -> "Add assertion message");

        dr.nextTag();

        assertEquals(2, dr.getDepth(), () -> "Add assertion message");
        assertEquals("SubscriptionId", dr.getLocalName(), () -> "Add assertion message");

        dr.next();
        assertEquals("1E5AY4ZG53H4AMC8QH82", dr.getText(), () -> "Add assertion message");

        dr.close();
    }
}