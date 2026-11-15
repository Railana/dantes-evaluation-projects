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
package org.apache.cxf.aegis.xml.stax;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.aegis.xml.MessageReader;
import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.test.AbstractCXFTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReaderTest extends AbstractCXFTest {
    @Test
    public void testLiteralReader() throws Exception {
        ElementReader lr = getStreamReader("bean11.xml");
        testReading(lr);

        lr = getStreamReader("read1.xml");
        testReading2(lr);
    }

    private ElementReader getStreamReader(String resource) throws FactoryConfigurationError,
        XMLStreamException {
        /*
         * XMLInputFactory factory = XMLInputFactory.newInstance();
         * XMLStreamReader reader = factory.createXMLStreamReader(
         * getResourceAsStream(resource));
         */
        XMLStreamReader reader = StaxUtils.createXMLStreamReader(getResourceAsStream(resource), null);

        while (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
            reader.next();
        }

        return new ElementReader(reader);
    }

    public void testReading(MessageReader reader) {
        assertEquals("Envelope", reader.getLocalName(), () -> "Add assertion message");

        // make sure we can repeat this
        assertTrue(reader.hasMoreElementReaders(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(reader.hasMoreElementReaders(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(reader.hasMoreElementReaders(), () -> "Add assertion message");

        MessageReader header = reader.getNextElementReader();
        assertEquals("Header", header.getLocalName(), () -> "Add assertion message");
        assertEquals(Soap11.getInstance().getNamespace(), header.getNamespace(), () -> "Add assertion message");
        assertFalse(header.hasMoreElementReaders(), () -> "Add assertion message");

        MessageReader body = reader.getNextElementReader();
        assertEquals("Body", body.getLocalName(), () -> "Add assertion message");
        assertFalse(body.hasMoreElementReaders(), () -> "Add assertion message");
    }

    public void testReading2(MessageReader reader) throws Exception {
        assertEquals("test", reader.getLocalName(), () -> "Add assertion message");
        assertEquals("urn:test", reader.getNamespace(), () -> "Add assertion message");

        // make sure we can repeat this
        assertTrue(reader.hasMoreAttributeReaders(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(reader.hasMoreAttributeReaders(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(reader.hasMoreAttributeReaders(), () -> "Add assertion message");

        MessageReader one = reader.getNextAttributeReader();
        assertEquals("one", one.getValue(), () -> "Add assertion message");

        MessageReader two = reader.getNextAttributeReader();
        assertEquals("two", two.getValue(), () -> "Add assertion message");

        assertFalse(reader.hasMoreAttributeReaders(), () -> "Add assertion message");

        assertTrue(reader.hasMoreElementReaders(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(reader.hasMoreElementReaders(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(reader.hasMoreElementReaders(), () -> "Add assertion message");

        MessageReader child = reader.getNextElementReader();
        assertEquals("child", child.getLocalName(), () -> "Add assertion message");
        assertTrue(child.hasMoreElementReaders(), () -> "Add assertion message");
    }
}