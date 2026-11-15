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

package org.apache.cxf.jaxb.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.databinding.DataReader;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.staxutils.StaxStreamFilter;
import org.apache.hello_world_doc_lit_bare.types.TradePriceData;
import org.apache.hello_world_rpclit.types.MyComplexStruct;
import org.apache.hello_world_soap_http.types.GreetMe;
import org.apache.hello_world_soap_http.types.GreetMeResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class XMLStreamDataReaderTest {

    private XMLInputFactory factory;
    private XMLStreamReader reader;
    private InputStream is;

    @Before
    public void setUp() throws Exception {
        // Removed line due to General Fixture smell: factory = XMLInputFactory.newInstance();
    }

    @After
    public void tearDown() throws IOException {
        is.close();
    }

    @Test
    public void testSetProperty() throws Exception {
        MyCustomHandler handler = new MyCustomHandler();

        DataReaderImpl<XMLStreamReader> dr = newDataReader(handler);

        // Should fail if custom handler doesn't skip formatting error
        Object val = dr.read(reader);
        assertTrue(val instanceof GreetMe, () -> "Add assertion message");
        assertEquals("TestSOAPInputPMessage", ((GreetMe)val).getRequestType(), () -> "Add assertion message");

        assertTrue(handler.getUsed(), () -> "Add assertion message");
    }

    @Test
    public void testSetPropertyWithCustomExceptionHandling() throws Exception {
        MyCustomMarshallerHandler handler = new MyCustomMarshallerHandler();

        DataReaderImpl<XMLStreamReader> dr = newDataReader(handler);

        // Should fail if custom handler doesn't skip formatting error
        assertThrows(Fault f,
        		() -> {
        			dr.read(reader);
        			fail("Expected exception");
        		});

        // Check handler used
        assertTrue(handler.getUsed(), () -> "Add assertion message");
        assertFalse(handler.isOnMarshalComplete(), () -> "Add assertion message");
        assertTrue(handler.isOnUnmarshalComplete(), () -> "Add assertion message");
    }

    private DataReaderImpl<XMLStreamReader> newDataReader(ValidationEventHandler handler) throws Exception {
        JAXBDataBinding db = getDataBinding(GreetMe.class);

        reader = getTestReader("../resources/SetPropertyValidationFailureReq.xml");
        assertNotNull(reader);

        DataReaderImpl<XMLStreamReader> dr = (DataReaderImpl<XMLStreamReader>)db.createReader(XMLStreamReader.class);
        assertNotNull(dr);

        // Build message to set custom event handler
        org.apache.cxf.message.Message message = new org.apache.cxf.message.MessageImpl();
        message.put(JAXBDataBinding.READER_VALIDATION_EVENT_HANDLER, handler);
        message.put("unwrap.jaxb.element", true);

        dr.setProperty("org.apache.cxf.message.Message", message);

        return dr;
    }

    @Test
    public void testReadWrapper() throws Exception {
        JAXBDataBinding db = getDataBinding(GreetMe.class);

        reader = getTestReader("../resources/GreetMeDocLiteralReq.xml");
        assertNotNull(reader, () -> "Add assertion message");

        DataReader<XMLStreamReader> dr = db.createReader(XMLStreamReader.class);
        assertNotNull(dr, () -> "Add assertion message");
        Object val = dr.read(reader);
        assertNotNull(val, () -> "Add assertion message");
        assertTrue(val instanceof GreetMe, () -> "Add assertion message");
        assertEquals("TestSOAPInputPMessage", ((GreetMe)val).getRequestType(), () -> "Add assertion message");
    }

    @Test
    public void testReadWrapperReturn() throws Exception {
        JAXBDataBinding db = getDataBinding(GreetMeResponse.class);

        reader = getTestReader("../resources/GreetMeDocLiteralResp.xml");
        assertNotNull(reader, () -> "Add assertion message");

        DataReader<XMLStreamReader> dr = db.createReader(XMLStreamReader.class);
        assertNotNull(dr, () -> "Add assertion message");

        Object retValue = dr.read(reader);

        assertNotNull(retValue, () -> "Add assertion message");
        assertTrue(retValue instanceof GreetMeResponse, () -> "Add assertion message");
        assertEquals("TestSOAPOutputPMessage", ((GreetMeResponse)retValue).getResponseType(), () -> "Add assertion message");
    }

    @Test
    public void testReadRPC() throws Exception {
        JAXBDataBinding db = getDataBinding(MyComplexStruct.class);

        QName[] tags = {new QName("http://apache.org/hello_world_rpclit", "sendReceiveData")};

        reader = getTestReader("../resources/greetMeRpcLitReq.xml");
        assertNotNull(reader, () -> "Add assertion message");

        XMLStreamReader localReader = getTestFilteredReader(reader, tags);

        DataReader<XMLStreamReader> dr = db.createReader(XMLStreamReader.class);
        assertNotNull(dr, () -> "Add assertion message");
        Object val = dr.read(new QName("http://apache.org/hello_world_rpclit", "in"),
                             localReader,
                             MyComplexStruct.class);
        assertNotNull(val, () -> "Add assertion message");

        assertTrue(val instanceof MyComplexStruct, () -> "Add assertion message");
        assertEquals("this is element 1", ((MyComplexStruct)val).getElem1(), () -> "Add assertion message");
        assertEquals("this is element 2", ((MyComplexStruct)val).getElem2(), () -> "Add assertion message");
        Integer magicNumber84694309 = 42; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber84694309((MyComplexStruct)val).getElem3(), () -> "Add assertion message");
    }


    @Test
    public void testReadBare() throws Exception {
        JAXBDataBinding db = getDataBinding(TradePriceData.class);

        reader = getTestReader("../resources/sayHiDocLitBareReq.xml");
        assertNotNull(reader, () -> "Add assertion message");

        DataReader<XMLStreamReader> dr = db.createReader(XMLStreamReader.class);
        assertNotNull(dr, () -> "Add assertion message");
        QName elName = new QName("http://apache.org/hello_world_doc_lit_bare/types", "inout");
        MessagePartInfo part = new MessagePartInfo(elName, null);
        part.setElement(true);
        part.setElementQName(elName);
        part.setTypeClass(TradePriceData.class);
        Object val = dr.read(part, reader);

        assertNotNull(val, () -> "Add assertion message");
        assertTrue(val instanceof TradePriceData, () -> "Add assertion message");
        assertEquals("CXF", ((TradePriceData)val).getTickerSymbol(), () -> "Add assertion message");
        assertEquals(Float.valueOf(1.0f), Float.valueOf(((TradePriceData)val).getTickerPrice()), () -> "Add assertion message");
    }

    private JAXBDataBinding getDataBinding(Class<?>... clz) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(clz);
        return new JAXBDataBinding(ctx);
    }

    private XMLStreamReader getTestFilteredReader(XMLStreamReader r, QName[] q) throws Exception {
        factory = XMLInputFactory.newInstance();
        StaxStreamFilter filter = new StaxStreamFilter(q);
        return factory.createFilteredReader(r, filter);
    }

    private XMLStreamReader getTestReader(String resource) throws Exception {
        factory = XMLInputFactory.newInstance();
        is = getTestStream(resource);
        assertNotNull(is);
        return factory.createXMLStreamReader(is);
    }

    private InputStream getTestStream(String resource) {
        return getClass().getResourceAsStream(resource);
    }
}