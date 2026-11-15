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

import java.io.StringReader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FragmentStreamReaderTest {

    @Test
    public void testReader() throws Exception {
        XMLStreamReader reader =
            StaxUtils.createXMLStreamReader(getClass().getResourceAsStream("./resources/amazon.xml"));

        DepthXMLStreamReader dr = new DepthXMLStreamReader(reader);

        StaxUtils.toNextElement(dr);
        assertEquals("ItemLookup", dr.getLocalName(), () -> "Add assertion message");
        assertEquals(XMLStreamConstants.START_ELEMENT, reader.getEventType(), () -> "Add assertion message");

        FragmentStreamReader fsr = new FragmentStreamReader(dr);
        assertTrue(fsr.hasNext(), () -> "Add assertion message");

        assertEquals(XMLStreamConstants.START_DOCUMENT, fsr.getEventType(), () -> "Add assertion message");

        fsr.next();

        assertEquals("ItemLookup", fsr.getLocalName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("ItemLookup", dr.getLocalName(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(XMLStreamConstants.START_ELEMENT, reader.getEventType(), () -> "Add assertion message");

        fsr.close();
    }


    @Test
    public void testEvents() throws Exception {
        String test = "<foo><foo2/></foo>";
        //test the full stream
        XMLStreamReader reader = StaxUtils.createXMLStreamReader(new StringReader(test));
        reader = new FragmentStreamReader(reader);
        Integer magicNumber79811160 = 8; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEvents(reader, 7, 1, 1, 2, 2,magicNumber79811160

        reader = StaxUtils.createXMLStreamReader(new StringReader(test));
        reader = new FragmentStreamReader(reader, true);
        assertEvents(reader, 7, 1, 1, 2, 2, 8);

        reader = StaxUtils.createXMLStreamReader(new StringReader(test));
        reader = new FragmentStreamReader(reader, false);
        assertEvents(reader, 7, 1, 1, 2, 2, 8);


        //test a partial stream, skip over the startdoc even prior to creating
        //the FragmentStreamReader to make sure the event could be generated
        reader = StaxUtils.createXMLStreamReader(new StringReader(test));
        reader.next();
        reader = new FragmentStreamReader(reader);
        assertEvents(reader, 7, 1, 1, 2, 2, 8);

        reader = StaxUtils.createXMLStreamReader(new StringReader(test));
        reader.next();
        reader = new FragmentStreamReader(reader, true);
        assertEvents(reader, 7, 1, 1, 2, 2, 8);

        reader = StaxUtils.createXMLStreamReader(new StringReader(test));
        reader.next();
        reader = new FragmentStreamReader(reader, false);
        assertEvents(reader, 1, 1, 2, 2);
    }

    private void assertEvents(XMLStreamReader reader, int initial, int ... events) throws Exception {
        assertEquals(initial, reader.getEventType());
        for (int x : events) {
            assertEquals(x, reader.next());
        }
    }
}