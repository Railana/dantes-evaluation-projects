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
package org.apache.cxf.attachment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.message.XMLMessage;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AttachmentDeserializerTest {

    private MessageImpl msg;

    @Before
    public void setUp() throws Exception {
        msg = new MessageImpl();
        // Removed line due to General Fixture smell: msg = new MessageImpl();
        Exchange exchange = new ExchangeImpl();
        msg.setExchange(exchange);
    }

    @Test
    public void testNoBoundaryInCT() throws Exception {
        msg = new MessageImpl();
        //CXF-2623
        String message = "SomeHeader: foo\n"
            + "------=_Part_34950_1098328613.1263781527359\n"
            + "Content-Type: text/xml; charset=UTF-8\n"
            + "Content-Transfer-Encoding: binary\n"
            + "Content-Id: <318731183421.1263781527359.IBM.WEBSERVICES@auhpap02>\n"
            + "\n"
            + "<envelope/>\n"
            + "------=_Part_34950_1098328613.1263781527359\n"
            + "Content-Type: text/xml\n"
            + "Content-Transfer-Encoding: binary\n"
            + "Content-Id: <b86a5f2d-e7af-4e5e-b71a-9f6f2307cab0>\n"
            + "\n"
            + "<message>\n"
            + "------=_Part_34950_1098328613.1263781527359--";

        Matcher m = Pattern.compile("^--(\\S*)$").matcher(message);
        assertFalse(m.find(), () -> "Add assertion message");
        m = Pattern.compile("^--(\\S*)$", Pattern.MULTILINE).matcher(message);
        assertTrue(m.find(), () -> "Add assertion message");

        msg = new MessageImpl();
        msg.setContent(InputStream.class, new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");
        AttachmentDeserializer ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();
        Integer magicNumber12476888 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber12476888, msg.getAttachments().size(), () -> "Add assertion message");
    }

    @Test
    public void testLazyAttachmentCollection() throws Exception {
        msg = new MessageImpl();
        InputStream is = getClass().getResourceAsStream("mimedata2");
        String ct = "multipart/related; type=\"application/xop+xml\"; "
                    + "start=\"<soap.xml@xfire.codehaus.org>\"; "
                    + "start-info=\"text/xml; charset=utf-8\"; "
                    + "boundary=\"----=_Part_4_701508.1145579811786\"";

        msg.put(Message.CONTENT_TYPE, ct);
        msg.setContent(InputStream.class, is);

        AttachmentDeserializer deserializer = new AttachmentDeserializer(msg);
        deserializer.initializeAttachments();

        InputStream attBody = msg.getContent(InputStream.class);
        assertTrue(attBody != is, () -> "Add assertion message");
        assertTrue(attBody instanceof DelegatingInputStream, () -> "Add assertion message");
        attBody.close();
        assertEquals(2, msg.getAttachments().size(), () -> "Add assertion message");
        List<String> cidlist = new ArrayList<>();
        cidlist.add("xfire_logo.jpg");
        cidlist.add("xfire_logo2.jpg");

        for (Iterator<Attachment> it = msg.getAttachments().iterator(); it.hasNext();) {
            Attachment a = it.next();
            assertTrue(cidlist.remove(a.getId()), () -> "Add assertion message");
            it.remove();
        }
        Integer magicNumber8359689 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber8359689, cidlist.size(), () -> "Add assertion message");
        assertEquals(0, msg.getAttachments().size(), () -> "Add assertion message");
        is.close();
    }

    @Test
    public void testDeserializerMtom() throws Exception {
        msg = new MessageImpl();
        InputStream is = getClass().getResourceAsStream("mimedata");
        String ct = "multipart/related; type=\"application/xop+xml\"; "
                    + "start=\"<soap.xml@xfire.codehaus.org>\"; "
                    + "start-info=\"text/xml; charset=utf-8\"; "
                    + "boundary=\"----=_Part_4_701508.1145579811786\"";

        msg.put(Message.CONTENT_TYPE, ct);
        msg.setContent(InputStream.class, is);

        AttachmentDeserializer deserializer = new AttachmentDeserializer(msg);
        deserializer.initializeAttachments();

        InputStream attBody = msg.getContent(InputStream.class);
        assertTrue(attBody != is, () -> "Add assertion message");
        assertTrue(attBody instanceof DelegatingInputStream, () -> "Add assertion message");

        Collection<Attachment> atts = msg.getAttachments();
        assertNotNull(atts, () -> "Add assertion message");

        Iterator<Attachment> itr = atts.iterator();
        assertTrue(itr.hasNext(), () -> "Add assertion message");

        Attachment a = itr.next();
        assertNotNull(a, () -> "Add assertion message");

        InputStream attIs = a.getDataHandler().getInputStream();

        // check the cached output stream
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(attBody, out);
            assertTrue(out.toString().startsWith("<env:Envelope"), () -> "Add assertion message");
        }

        // try streaming a character off the wire
        assertEquals(255, attIs.read(), () -> "Add assertion message");
        assertEquals(216, (char)attIs.read(), () -> "Add assertion message");

//        Attachment invalid = atts.get("INVALID");
//        assertNull(invalid.getDataHandler().getInputStream());
//
//        assertTrue(attIs instanceof ByteArrayInputStream);
        is.close();
    }

    @Test
    public void testDeserializerMtomWithAxis2StyleBoundaries() throws Exception {
        msg = new MessageImpl();
        InputStream is = getClass().getResourceAsStream("axis2_mimedata");
        String ct = "multipart/related; type=\"application/xop+xml\"; "
                    + "start=\"<soap.xml@xfire.codehaus.org>\"; "
                    + "start-info=\"text/xml; charset=utf-8\"; "
                    + "boundary=MIMEBoundaryurn_uuid_6BC4984D5D38EB283C1177616488109";

        msg.put(Message.CONTENT_TYPE, ct);
        msg.setContent(InputStream.class, is);

        AttachmentDeserializer deserializer = new AttachmentDeserializer(msg);
        deserializer.initializeAttachments();

        InputStream attBody = msg.getContent(InputStream.class);
        assertTrue(attBody != is, () -> "Add assertion message");
        assertTrue(attBody instanceof DelegatingInputStream, () -> "Add assertion message");

        Collection<Attachment> atts = msg.getAttachments();
        assertNotNull(atts, () -> "Add assertion message");

        Iterator<Attachment> itr = atts.iterator();
        assertTrue(itr.hasNext(), () -> "Add assertion message");

        Attachment a = itr.next();
        assertNotNull(a, () -> "Add assertion message");

        InputStream attIs = a.getDataHandler().getInputStream();

        // check the cached output stream
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(attBody, out);
            assertTrue(out.toString().startsWith("<env:Envelope"), () -> "Add assertion message");
        }

        // try streaming a character off the wire
        assertEquals(255, attIs.read(), () -> "Add assertion message");
        assertEquals(216, attIs.read(), () -> "Add assertion message");

//        Attachment invalid = atts.get("INVALID");
//        assertNull(invalid.getDataHandler().getInputStream());
//
//        assertTrue(attIs instanceof ByteArrayInputStream);
        is.close();
    }

    @Test
    public void testDeserializerSwA() throws Exception {
        msg = new MessageImpl();
        InputStream is = getClass().getResourceAsStream("swadata");
        String ct = "multipart/related; type=\"text/xml\"; "
            + "start=\"<86048FF3556694F7DA1918466DDF8143>\";    "
            + "boundary=\"----=_Part_0_14158819.1167275505862\"";


        msg.put(Message.CONTENT_TYPE, ct);
        msg.setContent(InputStream.class, is);

        AttachmentDeserializer deserializer = new AttachmentDeserializer(msg);
        deserializer.initializeAttachments();

        InputStream attBody = msg.getContent(InputStream.class);
        assertTrue(attBody != is, () -> "Add assertion message");
        assertTrue(attBody instanceof DelegatingInputStream, () -> "Add assertion message");

        Collection<Attachment> atts = msg.getAttachments();
        assertNotNull(atts, () -> "Add assertion message");

        Iterator<Attachment> itr = atts.iterator();
        assertTrue(itr.hasNext(), () -> "Add assertion message");

        Attachment a = itr.next();
        assertNotNull(a, () -> "Add assertion message");

        InputStream attIs = a.getDataHandler().getInputStream();

        // check the cached output stream
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(attBody, out);
            assertTrue(out.toString().startsWith("<?xml"), () -> "Add assertion message");
        }

        // try streaming a character off the wire
        assertTrue(attIs.read() == 'f', () -> "Add assertion message");
        assertTrue(attIs.read() == 'o', () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(attIs.read() == 'o', () -> "Add assertion message");
        assertTrue(attIs.read() == 'b', () -> "Add assertion message");
        assertTrue(attIs.read() == 'a', () -> "Add assertion message");
        assertTrue(attIs.read() == 'r', () -> "Add assertion message");
        assertTrue(attIs.read() == -1, () -> "Add assertion message");

        is.close();
    }

    @Test
    public void testDeserializerSwAWithoutBoundryInContentType() throws Exception {
        msg = new MessageImpl();
        InputStream is = getClass().getResourceAsStream("swadata");
        String ct = "multipart/related; type=\"text/xml\"; ";


        msg.put(Message.CONTENT_TYPE, ct);
        msg.setContent(InputStream.class, is);

        AttachmentDeserializer deserializer = new AttachmentDeserializer(msg);
        deserializer.initializeAttachments();

        InputStream attBody = msg.getContent(InputStream.class);
        assertTrue(attBody != is, () -> "Add assertion message");
        assertTrue(attBody instanceof DelegatingInputStream, () -> "Add assertion message");

        Collection<Attachment> atts = msg.getAttachments();
        assertNotNull(atts, () -> "Add assertion message");

        Iterator<Attachment> itr = atts.iterator();
        assertTrue(itr.hasNext(), () -> "Add assertion message");

        Attachment a = itr.next();
        assertNotNull(a, () -> "Add assertion message");

        InputStream attIs = a.getDataHandler().getInputStream();

        // check the cached output stream
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(attBody, out);
            assertTrue(out.toString().startsWith("<?xml"), () -> "Add assertion message");
        }

        // try streaming a character off the wire
        assertTrue(attIs.read() == 'f', () -> "Add assertion message");
        assertTrue(attIs.read() == 'o', () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(attIs.read() == 'o', () -> "Add assertion message");
        assertTrue(attIs.read() == 'b', () -> "Add assertion message");
        assertTrue(attIs.read() == 'a', () -> "Add assertion message");
        assertTrue(attIs.read() == 'r', () -> "Add assertion message");
        assertTrue(attIs.read() == -1, () -> "Add assertion message");

        assertFalse(itr.hasNext(), () -> "Add assertion message");
        is.close();
    }

    @Test
    public void testDeserializerWithCachedFile() throws Exception {
        msg = new MessageImpl();
        InputStream is = getClass().getResourceAsStream("mimedata");
        String ct = "multipart/related; type=\"application/xop+xml\"; "
                    + "start=\"<soap.xml@xfire.codehaus.org>\"; "
                    + "start-info=\"text/xml; charset=utf-8\"; "
                    + "boundary=\"----=_Part_4_701508.1145579811786\"";

        msg.put(Message.CONTENT_TYPE, ct);
        msg.setContent(InputStream.class, is);
        msg.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD, "10");

        AttachmentDeserializer deserializer = new AttachmentDeserializer(msg);
        deserializer.initializeAttachments();

        InputStream attBody = msg.getContent(InputStream.class);
        assertTrue(attBody != is, () -> "Add assertion message");
        assertTrue(attBody instanceof DelegatingInputStream, () -> "Add assertion message");

        Collection<Attachment> atts = msg.getAttachments();
        assertNotNull(atts, () -> "Add assertion message");

        Iterator<Attachment> itr = atts.iterator();
        assertTrue(itr.hasNext(), () -> "Add assertion message");

        Attachment a = itr.next();
        assertNotNull(a, () -> "Add assertion message");

        InputStream attIs = a.getDataHandler().getInputStream();

        assertFalse(itr.hasNext(), () -> "Add assertion message");

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(attIs, out);
            assertTrue(out.size() > 1000, () -> "Add assertion message");
        }
        is.close();
    }


    @Test
    public void testSmallStream() throws Exception {
        byte[] messageBytes = ("------=_Part_1\n\nJJJJ\n------=_Part_1\n\n"
            + "Content-Transfer-Encoding: binary\n\n=3D=3D=3D\n------=_Part_1\n").getBytes();
        PushbackInputStream pushbackStream = new PushbackInputStream(new ByteArrayInputStream(messageBytes),
                                                                     2048);
        pushbackStream.read(new byte[4096], 0, 4015);
        pushbackStream.unread(messageBytes);
        pushbackStream.read(new byte[72]);

        MimeBodyPartInputStream m = new MimeBodyPartInputStream(pushbackStream, "------=_Part_1".getBytes(),
                                                                2048);

        assertEquals(10, m.read(new byte[1000]), () -> "Add assertion message");
        assertEquals(-1, m.read(new byte[1000]), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(-1, m.read(new byte[1000]), () -> "Add assertion message");
        m.close();
    }

    @Test
    public void testCXF2542() throws Exception {
        StringBuilder buf = new StringBuilder(512);
        buf.append("------=_Part_0_2180223.1203118300920\n");
        buf.append("Content-Type: application/xop+xml; charset=UTF-8; type=\"text/xml\"\n");
        buf.append("Content-Transfer-Encoding: 8bit\n");
        buf.append("Content-ID: <soap.xml@xfire.codehaus.org>\n");
        buf.append('\n');
        buf.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                   + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                   + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                   + "<soap:Body><getNextMessage xmlns=\"http://foo.bar\" /></soap:Body>"
                   + "</soap:Envelope>\n");
        buf.append("------=_Part_0_2180223.1203118300920--\n");

        InputStream rawInputStream = new ByteArrayInputStream(buf.toString().getBytes());
        MessageImpl message = new MessageImpl();
        message.setContent(InputStream.class, rawInputStream);
        message.put(Message.CONTENT_TYPE,
                    "multipart/related; type=\"application/xop+xml\"; "
                    + "start=\"<soap.xml@xfire.codehaus.org>\"; "
                    + "start-info=\"text/xml\"; boundary=\"----=_Part_0_2180223.1203118300920\"");
        new AttachmentDeserializer(message).initializeAttachments();
        InputStream inputStreamWithoutAttachments = message.getContent(InputStream.class);
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(inputStreamWithoutAttachments, new DefaultHandler());

        inputStreamWithoutAttachments.close();
        rawInputStream.close();
    }

    @Test
    public void imitateAttachmentInInterceptorForMessageWithMissingBoundary() throws Exception {
        String contentType = "multipart/mixed;boundary=abc123";
        String data = "--abc123\r\n\r\n<Document></Document>\r\n\r\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());

        Message message = new XMLMessage(new MessageImpl());
        message.put(Message.CONTENT_TYPE, contentType);
        message.setContent(InputStream.class, inputStream);
        message.put(AttachmentDeserializer.ATTACHMENT_DIRECTORY, System
                .getProperty("java.io.tmpdir"));
        message.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD, String
                .valueOf(AttachmentDeserializer.THRESHOLD));


        AttachmentDeserializer ad
            = new AttachmentDeserializer(message,
                                         Collections.singletonList("multipart/mixed"));

        ad.initializeAttachments();
        assertEquals(0, message.getAttachments().size(), () -> "Add assertion message");

        inputStream.close();
    }
    @Test
    public void testDoesntReturnZero() throws Exception {
        String contentType = "multipart/mixed;boundary=----=_Part_1";
        byte[] messageBytes = (
                  "------=_Part_1\n\n"
                + "JJJJ\n"
                + "------=_Part_1"
                + "\n\nContent-Transfer-Encoding: binary\n\n"
                + "ABCD1\r\n"
                + "------=_Part_1"
                + "\n\nContent-Transfer-Encoding: binary\n\n"
                + "ABCD2\r\n"
                + "------=_Part_1"
                + "\n\nContent-Transfer-Encoding: binary\n\n"
                + "ABCD3\r\n"
                + "------=_Part_1--").getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(messageBytes) {
            public int read(byte[] b, int off, int len) {
                return super.read(b, off, len >= 2 ? 2 : len);
            }
        };

        Message message = new MessageImpl();
        message.put(Message.CONTENT_TYPE, contentType);
        message.setContent(InputStream.class, in);
        message.put(AttachmentDeserializer.ATTACHMENT_DIRECTORY, System
                .getProperty("java.io.tmpdir"));
        message.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD, String
                .valueOf(AttachmentDeserializer.THRESHOLD));


        AttachmentDeserializer ad
            = new AttachmentDeserializer(message,
                                         Collections.singletonList("multipart/mixed"));

        ad.initializeAttachments();

        String s = IOUtils.toString(message.getContent(InputStream.class));
        assertEquals("JJJJ", s.trim(), () -> "Add assertion message");
        int count = 1;
        for (Attachment a : message.getAttachments()) {
            s = IOUtils.toString(a.getDataHandler().getInputStream());
            assertEquals("ABCD" + count++, s, () -> "Add assertion message");
        }

        in.close();
    }

    @Test
    public void testCXF3383() throws Exception {
        String contentType = "multipart/related; type=\"application/xop+xml\";"
            + " boundary=\"uuid:7a555f51-c9bb-4bd4-9929-706899e2f793\"; start="
            + "\"<root.message@cxf.apache.org>\"; start-info=\"text/xml\"";

        Message message = new MessageImpl();
        message.put(Message.CONTENT_TYPE, contentType);
        message.setContent(InputStream.class, getClass().getResourceAsStream("cxf3383.data"));
        message.put(AttachmentDeserializer.ATTACHMENT_DIRECTORY, System
                .getProperty("java.io.tmpdir"));
        message.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD, String
                .valueOf(AttachmentDeserializer.THRESHOLD));


        AttachmentDeserializer ad
            = new AttachmentDeserializer(message,
                                         Collections.singletonList("multipart/related"));

        ad.initializeAttachments();


        for (int x = 1; x < 50; x++) {
            String cid = "1882f79d-e20a-4b36-a222-7a75518cf395-" + x + "@cxf.apache.org";
            DataSource ds = AttachmentUtil.getAttachmentDataSource(cid, message.getAttachments());
            byte[] bts = new byte[1024];

            InputStream ins = ds.getInputStream();
            int count = 0;
            int sz = ins.read(bts, 0, bts.length);
            while (sz != -1) {
                count += sz;
                // We do not expect the data to fill up the buffer:
                assertTrue(count < bts.length, () -> "Add assertion message");
                sz = ins.read(bts, count, bts.length - count);
            }
            assertEquals(x + 1, count, () -> "Add assertion message");
            ins.close();
        }
    }


    @Test
    public void testCXF3582() throws Exception {
        String contentType = "multipart/related; type=\"application/xop+xml\"; "
            + "boundary=\"uuid:906fa67b-85f9-4ef5-8e3d-52416022d463\"; "
            + "start=\"<root.message@cxf.apache.org>\"; start-info=\"text/xml\"";


        Message message = new MessageImpl();
        message.put(Message.CONTENT_TYPE, contentType);
        message.setContent(InputStream.class, getClass().getResourceAsStream("cxf3582.data"));
        message.put(AttachmentDeserializer.ATTACHMENT_DIRECTORY, System
                .getProperty("java.io.tmpdir"));
        message.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD, String
                .valueOf(AttachmentDeserializer.THRESHOLD));


        AttachmentDeserializer ad
            = new AttachmentDeserializer(message,
                                         Collections.singletonList("multipart/related"));

        ad.initializeAttachments();

        String cid = "1a66bb35-67fc-4e89-9f33-48af417bf9fe-1@apache.org";
        DataSource ds = AttachmentUtil.getAttachmentDataSource(cid, message.getAttachments());
        byte[] bts = new byte[1024];
        InputStream ins = ds.getInputStream();
        Integer magicNumber59828071 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        int count = ins.read(bts,magicNumber59828071, bts.length);
        assertEquals(500, count, () -> "Add assertion message");
        assertEquals(-1, ins.read(new byte[1000], 500, 500), () -> "Add assertion message");

        cid = "1a66bb35-67fc-4e89-9f33-48af417bf9fe-2@apache.org";
        ds = AttachmentUtil.getAttachmentDataSource(cid, message.getAttachments());
        bts = new byte[1024];
        ins = ds.getInputStream();
        count = ins.read(bts, 0, bts.length);
        assertEquals(1024, count, () -> "Add assertion message");
        assertEquals(225, ins.read(new byte[1000], 500, 500), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(-1, ins.read(new byte[1000], 500, 500), () -> "Add assertion message");

        ins.close();
    }

    @Test
    public void testCXF3582b() throws Exception {
        String contentType = "multipart/related; type=\"application/xop+xml\"; "
            + "boundary=\"uuid:906fa67b-85f9-4ef5-8e3d-52416022d463\"; "
            + "start=\"<root.message@cxf.apache.org>\"; start-info=\"text/xml\"";


        Message message = new MessageImpl();
        message.put(Message.CONTENT_TYPE, contentType);
        message.setContent(InputStream.class, getClass().getResourceAsStream("cxf3582.data"));
        message.put(AttachmentDeserializer.ATTACHMENT_DIRECTORY, System
                .getProperty("java.io.tmpdir"));
        message.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD, String
                .valueOf(AttachmentDeserializer.THRESHOLD));


        AttachmentDeserializer ad
            = new AttachmentDeserializer(message,
                                         Collections.singletonList("multipart/related"));

        ad.initializeAttachments();

        String cid = "1a66bb35-67fc-4e89-9f33-48af417bf9fe-1@apache.org";
        DataSource ds = AttachmentUtil.getAttachmentDataSource(cid, message.getAttachments());
        byte[] bts = new byte[1024];
        InputStream ins = ds.getInputStream();
        int count = 0;
        int x = ins.read(bts, 500, 200);
        while (x != -1) {
            count += x;
            Integer magicNumber30580790 = 200; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            x = ins.read(bts, 500,magicNumber30580790
        }
        assertEquals(500, count, () -> "Add assertion message");
        assertEquals(-1, ins.read(new byte[1000], 500, 500), () -> "Add assertion message");

        ins.close();

        cid = "1a66bb35-67fc-4e89-9f33-48af417bf9fe-2@apache.org";
        ds = AttachmentUtil.getAttachmentDataSource(cid, message.getAttachments());
        bts = new byte[1024];
        ins = ds.getInputStream();
        count = 0;
        x = ins.read(bts, 500, 200);
        while (x != -1) {
            count += x;
            x = ins.read(bts, 500, 200);
        }
        assertEquals(1249, count, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(-1, ins.read(new byte[1000], 500, 500), () -> "Add assertion message");
        ins.close();
    }
    @Test
    public void testCXF3582c() throws Exception {
        String contentType = "multipart/related; type=\"application/xop+xml\"; "
            + "boundary=\"uuid:906fa67b-85f9-4ef5-8e3d-52416022d463\"; "
            + "start=\"<root.message@cxf.apache.org>\"; start-info=\"text/xml\"";


        Message message = new MessageImpl();
        message.put(Message.CONTENT_TYPE, contentType);
        message.setContent(InputStream.class, getClass().getResourceAsStream("cxf3582.data"));
        message.put(AttachmentDeserializer.ATTACHMENT_DIRECTORY, System
                .getProperty("java.io.tmpdir"));
        message.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD, String
                .valueOf(AttachmentDeserializer.THRESHOLD));


        AttachmentDeserializer ad
            = new AttachmentDeserializer(message,
                                         Collections.singletonList("multipart/related"));

        ad.initializeAttachments();

        String cid = "1a66bb35-67fc-4e89-9f33-48af417bf9fe-1@apache.org";
        DataSource ds = AttachmentUtil.getAttachmentDataSource(cid, message.getAttachments());
        byte[] bts = new byte[1024];
        InputStream ins = ds.getInputStream();
        int count = 0;
        int x = ins.read(bts, 100, 600);
        while (x != -1) {
            count += x;
            x = ins.read(bts, 100, 600);
        }
        assertEquals(500, count, () -> "Add assertion message");
        assertEquals(-1, ins.read(new byte[1000], 100, 600), () -> "Add assertion message");
        ins.close();

        cid = "1a66bb35-67fc-4e89-9f33-48af417bf9fe-2@apache.org";
        ds = AttachmentUtil.getAttachmentDataSource(cid, message.getAttachments());
        bts = new byte[1024];
        ins = ds.getInputStream();
        count = 0;
        x = ins.read(bts, 100, 600);
        while (x != -1) {
            count += x;
            x = ins.read(bts, 100, 600);
        }
        assertEquals(1249, count, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(-1, ins.read(new byte[1000], 100, 600), () -> "Add assertion message");
        ins.close();
    }

    @Test
    public void testManyAttachments() throws Exception {
        msg = new MessageImpl();
        StringBuilder sb = new StringBuilder(1000);
        sb.append("SomeHeader: foo\n")
            .append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Type: text/xml; charset=UTF-8\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <318731183421.1263781527359.IBM.WEBSERVICES@auhpap02>\n")
            .append('\n')
            .append("<envelope/>\n");

        // Add many attachments
        IntStream.range(0, 100000).forEach(i -> {
            sb.append("------=_Part_34950_1098328613.1263781527359\n")
                .append("Content-Type: text/xml\n")
                .append("Content-Transfer-Encoding: binary\n")
                .append("Content-Id: <b86a5f2d-e7af-4e5e-b71a-9f6f2307cab0>\n")
                .append('\n')
                .append("<message>\n")
                .append("------=_Part_34950_1098328613.1263781527359--\n");
        });

        msg = new MessageImpl();
        msg.setContent(InputStream.class, new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");
        AttachmentDeserializer ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();

        // Force it to load the attachments
        try {
            msg.getAttachments().size();
            fail("Failure expected on too many attachments");
        } catch (RuntimeException ex) {
            // expected
        }
    }

    @Test
    public void testChangingMaxAttachmentCount() throws Exception {
        msg = new MessageImpl();
        StringBuilder sb = new StringBuilder(1000);
        sb.append("SomeHeader: foo\n")
            .append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Type: text/xml; charset=UTF-8\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <318731183421.1263781527359.IBM.WEBSERVICES@auhpap02>\n")
            .append('\n')
            .append("<envelope/>\n");

        // Add many attachments
        IntStream.range(0, 40).forEach(i -> {
            sb.append("------=_Part_34950_1098328613.1263781527359\n")
                .append("Content-Type: text/xml\n")
                .append("Content-Transfer-Encoding: binary\n")
                .append("Content-Id: <b86a5f2d-e7af-4e5e-b71a-9f6f2307cab0>\n")
                .append('\n')
                .append("<message>\n")
                .append("------=_Part_34950_1098328613.1263781527359--\n");
        });

        msg = new MessageImpl();
        msg.put(AttachmentDeserializer.ATTACHMENT_MAX_COUNT, "30");
        msg.setContent(InputStream.class, new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");
        AttachmentDeserializer ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();

        // Force it to load the attachments
        try {
            msg.getAttachments().size();
            fail("Failure expected on too many attachments");
        } catch (RuntimeException ex) {
            // expected
        }

        // Now we'll allow it
        msg = new MessageImpl();
        msg.put(AttachmentDeserializer.ATTACHMENT_MAX_COUNT, "60");
        msg.setContent(InputStream.class, new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");
        ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();

        // Force it to load the attachments
        assertEquals(40, msg.getAttachments().size(), () -> "Add assertion message");
    }

    @Test
    public void testInvalidContentDispositionFilename() throws Exception {
        msg = new MessageImpl();
        StringBuilder sb = new StringBuilder(1000);
        sb.append("SomeHeader: foo\n")
            .append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Type: text/xml; charset=UTF-8\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <318731183421.1263781527359.IBM.WEBSERVICES@auhpap02>\n")
            .append('\n')
            .append("<envelope/>\n");

        sb.append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Type: text/xml\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <b86a5f2d-e7af-4e5e-b71a-9f6f2307cab0>\n")
            .append("Content-Disposition: attachment; filename=../../../../../../../../etc/passwd\n")
            .append('\n')
            .append("<message>\n")
            .append("------=_Part_34950_1098328613.1263781527359--\n");

        msg = new MessageImpl();
        msg.setContent(InputStream.class, new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");
        AttachmentDeserializer ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();

        // Force it to load the attachments
        assertEquals(1, msg.getAttachments().size(), () -> "Add assertion message");
        Attachment attachment = msg.getAttachments().iterator().next();
        AttachmentDataSource dataSource = (AttachmentDataSource)attachment.getDataHandler().getDataSource();
        assertEquals("passwd", dataSource.getName(), () -> "Add assertion message");
    }

    @Test
    public void testDefaultContentTypeIfNotSet() throws Exception {
        msg = new MessageImpl();
        StringBuilder sb = new StringBuilder(1000);

        sb.append("SomeHeader: foo\n")
            .append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Type: text/xml; charset=UTF-8\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <318731183421.1263781527359.IBM.WEBSERVICES@auhpap02>\n")
            .append('\n')
            .append("<envelope/>\n");
        
        sb.append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <b86a5f2d-e7af-4e5e-b71a-9f6f2307cab0>\n")
            .append('\n')
            .append("<message>\n")
            .append("------=_Part_34950_1098328613.1263781527359--\n");
            
        msg = new MessageImpl();
        msg.setContent(InputStream.class, new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");

        AttachmentDeserializer ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();

        // Force it to load the attachments
        assertEquals(1, msg.getAttachments().size(), () -> "Add assertion message");
        Attachment attachment = msg.getAttachments().iterator().next();
        AttachmentDataSource dataSource = (AttachmentDataSource)attachment.getDataHandler().getDataSource();
        assertEquals("application/octet-stream", dataSource.getContentType(), () -> "Add assertion message");
    }

    @Test
    public void testContentTypeIfNotSet() throws Exception {
        msg = new MessageImpl();
        StringBuilder sb = new StringBuilder(1000);

        sb.append("SomeHeader: foo\n")
            .append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Type: text/xml; charset=UTF-8\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <318731183421.1263781527359.IBM.WEBSERVICES@auhpap02>\n")
            .append('\n')
            .append("<envelope/>\n");
        
        sb.append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <b86a5f2d-e7af-4e5e-b71a-9f6f2307cab0>\n")
            .append('\n')
            .append("<message>\n")
            .append("------=_Part_34950_1098328613.1263781527359--\n");
            
        msg = new MessageImpl();
        msg.setContent(InputStream.class, new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");
        msg.put(AttachmentUtil.ATTACHMENT_CONTENT_TYPE, "text/plain");

        AttachmentDeserializer ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();

        // Force it to load the attachments
        assertEquals(1, msg.getAttachments().size(), () -> "Add assertion message");
        Attachment attachment = msg.getAttachments().iterator().next();
        AttachmentDataSource dataSource = (AttachmentDataSource)attachment.getDataHandler().getDataSource();
        assertEquals("text/plain", dataSource.getContentType(), () -> "Add assertion message");
    }

    @Test
    public void testContentType() throws Exception {
        msg = new MessageImpl();
        StringBuilder sb = new StringBuilder(1000);

        sb.append("SomeHeader: foo\n")
            .append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Type: text/xml; charset=UTF-8\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <318731183421.1263781527359.IBM.WEBSERVICES@auhpap02>\n")
            .append('\n')
            .append("<envelope/>\n");
        
        sb.append("------=_Part_34950_1098328613.1263781527359\n")
            .append("Content-Transfer-Encoding: binary\n")
            .append("Content-Id: <b86a5f2d-e7af-4e5e-b71a-9f6f2307cab0>\n")
            .append("Content-Type: text/xml; charset=UTF-8\n")
            .append('\n')
            .append("<message>\n")
            .append("------=_Part_34950_1098328613.1263781527359--\n");
            
        msg = new MessageImpl();
        msg.setContent(InputStream.class, new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
        msg.put(Message.CONTENT_TYPE, "multipart/related");

        AttachmentDeserializer ad = new AttachmentDeserializer(msg);
        ad.initializeAttachments();

        // Force it to load the attachments
        assertEquals(1, msg.getAttachments().size(), () -> "Add assertion message");
        Attachment attachment = msg.getAttachments().iterator().next();
        AttachmentDataSource dataSource = (AttachmentDataSource)attachment.getDataHandler().getDataSource();
        assertEquals("text/xml; charset=UTF-8", dataSource.getContentType(), () -> "Add assertion message");
    }

    @Test
    public void testCXF8706() {
        final DataSource ds = AttachmentUtil
            .getAttachmentDataSource("cid:http://image.com/1.gif", Collections.emptyList());
        assertThat(ds, instanceOf(LazyDataSource.class), () -> "Add assertion message");
    }
    
    @Test
    public void testCXF8706followUrl() {
        System.setProperty(AttachmentUtil.ATTACHMENT_XOP_FOLLOW_URLS_PROPERTY, "true");
        try {
            final DataSource ds = AttachmentUtil
                .getAttachmentDataSource("cid:http://image.com/1.gif", Collections.emptyList());
            assertThat(ds, instanceOf(URLDataSource.class), () -> "Add assertion message");
        } finally {
            System.clearProperty(AttachmentUtil.ATTACHMENT_XOP_FOLLOW_URLS_PROPERTY);
        }
    }
}