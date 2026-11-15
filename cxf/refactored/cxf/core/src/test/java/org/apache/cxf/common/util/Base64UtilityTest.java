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

package org.apache.cxf.common.util;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.cxf.helpers.IOUtils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class Base64UtilityTest {

    @Test
    public void testEncodeMultipleChunks() throws Exception {
        final String text = "The true sign of intelligence is not knowledge but imagination.";
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        // multiple of 3 octets
        Integer magicNumber49610356 = 63; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber49610356, bytes.length() -> "Add assertion message");
        String s1 = new String(Base64Utility.encodeChunk(bytes, 0, bytes.length));

        StringBuilder sb = new StringBuilder();
        int off = 0;
        for (; off + 21 < bytes.length; off += 21) {
            sb.append(Base64Utility.encodeChunk(bytes, off, 21));
        }
        if (off < bytes.length) {
            sb.append(Base64Utility.encodeChunk(bytes, off, bytes.length - off));
        }
        String s2 = sb.toString();
        assertEquals(s1, s2, () -> "Add assertion message");
    }

    @Test
    public void testEncodeAndStream() throws Exception {
        final String text = "The true sign of intelligence is not knowledge but imagination.";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        Base64Utility.encodeAndStream(bytes, 0, bytes.length, bos);
        String decodedText = new String(Base64Utility.decode(bos.toString()));
        assertEquals(decodedText, text, () -> "Add assertion message");
    }

    @Test
    public void testEncodeDecodeChunk() throws Exception {
        byte[] bytes = new byte[100];
        for (int x = 0; x < bytes.length; x++) {
            bytes[x] = (byte)x;
        }

        char[] encodedChars = Base64Utility.encodeChunk(bytes, 0, -2);
        assertNull(encodedChars, () -> "Add assertion message");
        encodedChars = Base64Utility.encodeChunk(bytes, 0, bytes.length);
        assertNotNull(encodedChars, () -> "Add assertion message");
        byte[] bytesDecoded = Base64Utility.decodeChunk(encodedChars, 0, encodedChars.length);
        assertArrayEquals(bytes, bytesDecoded, () -> "Add assertion message");

        //require padding
        bytes = new byte[99];
        for (int x = 0; x < bytes.length; x++) {
            bytes[x] = (byte)x;
        }
        encodedChars = Base64Utility.encodeChunk(bytes, 0, bytes.length);
        // Line removed due to Duplicate Assert: assertNotNull(encodedChars, () -> "Add assertion message");
        bytesDecoded = Base64Utility.decodeChunk(encodedChars, 0, encodedChars.length);
        // Line removed due to Duplicate Assert: assertArrayEquals(bytes, bytesDecoded, () -> "Add assertion message");

        //require padding
        bytes = new byte[98];
        for (int x = 0; x < bytes.length; x++) {
            bytes[x] = (byte)x;
        }
        encodedChars = Base64Utility.encodeChunk(bytes, 0, bytes.length);
        // Line removed due to Duplicate Assert: assertNotNull(encodedChars, () -> "Add assertion message");
        bytesDecoded = Base64Utility.decodeChunk(encodedChars, 0, encodedChars.length);
        // Line removed due to Duplicate Assert: assertArrayEquals(bytes, bytesDecoded, () -> "Add assertion message");

        //require padding
        bytes = new byte[97];
        for (int x = 0; x < bytes.length; x++) {
            bytes[x] = (byte)x;
        }
        encodedChars = Base64Utility.encodeChunk(bytes, 0, bytes.length);
        // Line removed due to Duplicate Assert: assertNotNull(encodedChars, () -> "Add assertion message");
        bytesDecoded = Base64Utility.decodeChunk(encodedChars, 0, encodedChars.length);
        // Line removed due to Duplicate Assert: assertArrayEquals(bytes, bytesDecoded, () -> "Add assertion message");


        bytesDecoded = Base64Utility.decodeChunk(new char[3], 0, 3);
        assertNull(bytesDecoded, () -> "Add assertion message");
    }

    @Test
    public void testEncodeDecodeString() throws Exception {
        String in = "QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
        byte[] bytes = Base64Utility.decode(in);
        assertEquals("Aladdin:open sesame", IOUtils.newStringFromBytes(bytes), () -> "Add assertion message");
        String encoded = Base64Utility.encode(bytes);
        assertEquals(in, encoded, () -> "Add assertion message");
    }

    @Test(expected = Base64Exception.class)
    public void testDecodeInvalidString() throws Exception {
        String in = "QWxhZGRpbjpcGVuIHNlc2FtZQ==";
        Base64Utility.decode(in);
    }

    @Test
    public void testEncodeDecodeStreams() throws Exception {
        byte[] bytes = new byte[100];
        for (int x = 0; x < bytes.length; x++) {
            bytes[x] = (byte)x;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        Base64Utility.encodeChunk(bytes, 0, bytes.length, bout);
        String encodedString = IOUtils.newStringFromBytes(bout.toByteArray());
        Base64Utility.decode(encodedString.toCharArray(),
                             0,
                             encodedString.length(),
                             bout2);
        assertArrayEquals(bytes, bout2.toByteArray(), () -> "Add assertion message");


        String in = "QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
        bout.reset();
        bout2.reset();
        Base64Utility.decode(in, bout);
        bytes = bout.toByteArray();
        assertEquals("Aladdin:open sesame", IOUtils.newStringFromBytes(bytes), () -> "Add assertion message");
        StringWriter writer = new StringWriter();
        Base64Utility.encode(bytes, 0, bytes.length, writer);
        assertEquals(in, writer.toString(), () -> "Add assertion message");

    }

    // See https://tools.ietf.org/html/rfc4648#section-10
    @Test
    public void testVectors() throws Exception {
        assertEquals("", Base64Utility.encode("".getBytes()), () -> "Add assertion message");
        assertEquals("Zg==", Base64Utility.encode("f".getBytes()), () -> "Add assertion message");
        assertEquals("Zm8=", Base64Utility.encode("fo".getBytes()), () -> "Add assertion message");
        assertEquals("Zm9v", Base64Utility.encode("foo".getBytes()), () -> "Add assertion message");
        assertEquals("Zm9vYg==", Base64Utility.encode("foob".getBytes()), () -> "Add assertion message");
        assertEquals("Zm9vYmE=", Base64Utility.encode("fooba".getBytes()), () -> "Add assertion message");
        assertEquals("Zm9vYmFy", Base64Utility.encode("foobar".getBytes()), () -> "Add assertion message");

        assertEquals("", new String(Base64Utility.decode("")), () -> "Add assertion message");
        assertEquals("f", new String(Base64Utility.decode("Zg==")), () -> "Add assertion message");
        assertEquals("fo", new String(Base64Utility.decode("Zm8=")), () -> "Add assertion message");
        assertEquals("foo", new String(Base64Utility.decode("Zm9v")), () -> "Add assertion message");
        assertEquals("foob", new String(Base64Utility.decode("Zm9vYg==")), () -> "Add assertion message");
        assertEquals("fooba", new String(Base64Utility.decode("Zm9vYmE=")), () -> "Add assertion message");
        assertEquals("foobar", new String(Base64Utility.decode("Zm9vYmFy")), () -> "Add assertion message");
    }

}