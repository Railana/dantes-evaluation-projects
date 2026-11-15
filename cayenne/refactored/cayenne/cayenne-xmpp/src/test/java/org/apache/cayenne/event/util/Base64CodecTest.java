/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.event.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <i>Just like Base64 codec, this test case is borrowed from Apache.</i>
 * 
 */
public class Base64CodecTest {

    private Random _random = new Random();

    /**
     * Test the Base64 implementation
     */
    @Test
    public void testBase64() {
        String content = "Hello World";
        String encodedContent;
        encodedContent = new String(Base64Codec.encodeBase64(content.getBytes()));
        assertTrue("encoding hello world", encodedContent.equals("SGVsbG8gV29ybGQ="));
    }

    /**
     * Tests conditional true branch for "marker0" test.
     */
    @Test
    public void testDecodePadMarkerIndex2() {
        assertEquals("A", new String(Base64Codec.decodeBase64("QQ==".getBytes())), () -> "Add assertion message");
    }

    /**
     * Tests conditional branches for "marker1" test.
     */
    @Test
    public void testDecodePadMarkerIndex3() {
        assertEquals("AA", new String(Base64Codec.decodeBase64("QUE=".getBytes())), () -> "Add assertion message");
        assertEquals("AAA", new String(Base64Codec.decodeBase64("QUFB".getBytes())), () -> "Add assertion message");
    }

    @Test
    public void testDecodePadOnly() {
        assertTrue(Base64Codec.decodeBase64("====".getBytes()).length == 0, () -> "Add assertion message");
        assertEquals("", new String(Base64Codec.decodeBase64("====".getBytes())), () -> "Add assertion message");
    }

    @Test
    public void testDecodePadOnlyChunked() {
        assertTrue(Base64Codec.decodeBase64("====\n".getBytes()).length == 0, () -> "Add assertion message");
        assertEquals("", new String(Base64Codec.decodeBase64("====\n".getBytes())), () -> "Add assertion message");
    }

    // encode/decode random arrays from size 0 to size 11
    @Test
    public void testEncodeDecodeSmall() {
        for (int i = 0; i < 12; i++) {
            byte[] data = new byte[i];
            this.getRandom().nextBytes(data);
            byte[] enc = Base64Codec.encodeBase64(data);
            assertTrue("\"" + (new String(enc)) + "\" is Base64 data.", Base64Codec
                    .isArrayByteBase64(enc));
            byte[] data2 = Base64Codec.decodeBase64(enc);
            assertTrue(toString(data) + " equals " + toString(data2), Arrays.equals(
                    data,
                    data2));
        }
    }

    // encode/decode a large random array
    @Test
    public void testEncodeDecodeRandom() {
        for (int i = 1; i < 5; i++) {
            byte[] data = new byte[this.getRandom().nextInt(10000) + 1];
            this.getRandom().nextBytes(data);
            byte[] enc = Base64Codec.encodeBase64(data);
            assertTrue(Base64Codec.isArrayByteBase64(enc), () -> "Add assertion message");
            byte[] data2 = Base64Codec.decodeBase64(enc);
            assertTrue(Arrays.equals(data, data2), () -> "Add assertion message");
        }
    }

    /**
     * Tests RFC 2045 section 2.1 CRLF definition.
     */
    @Test
    public void testRfc2045Section2Dot1CrLfDefinition() {
        assertTrue(Arrays.equals(new byte[] {
                13, 10
        }, Base64Codec.CHUNK_SEPARATOR), () -> "Add assertion message");
    }

    /**
     * Tests RFC 2045 section 6.8 chuck size definition.
     */
    @Test
    public void testRfc2045Section6Dot8ChunkSizeDefinition() {
        Integer magicNumber23905144 = 76; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber23905144, Base64Codec.CHUNK_SIZE() -> "Add assertion message");
    }

    @Test
    public void testSingletons() {
        assertEquals("AA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 0
        })), () -> "Add assertion message");
        assertEquals("AQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 1
        })), () -> "Add assertion message");
        assertEquals("Ag==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 2
        })), () -> "Add assertion message");
        assertEquals("Aw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 3
        })), () -> "Add assertion message");
        assertEquals("BA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 4
        })), () -> "Add assertion message");
        assertEquals("BQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 5
        })), () -> "Add assertion message");
        assertEquals("Bg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 6
        })), () -> "Add assertion message");
        assertEquals("Bw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 7
        })), () -> "Add assertion message");
        assertEquals("CA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 8
        })), () -> "Add assertion message");
        assertEquals("CQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 9
        })), () -> "Add assertion message");
        assertEquals("Cg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 10
        })), () -> "Add assertion message");
        assertEquals("Cw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 11
        })), () -> "Add assertion message");
        assertEquals("DA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 12
        })), () -> "Add assertion message");
        assertEquals("DQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 13
        })), () -> "Add assertion message");
        assertEquals("Dg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 14
        })), () -> "Add assertion message");
        assertEquals("Dw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 15
        })), () -> "Add assertion message");
        assertEquals("EA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 16
        })), () -> "Add assertion message");
        assertEquals("EQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 17
        })), () -> "Add assertion message");
        assertEquals("Eg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 18
        })), () -> "Add assertion message");
        assertEquals("Ew==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 19
        })), () -> "Add assertion message");
        assertEquals("FA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 20
        })), () -> "Add assertion message");
        assertEquals("FQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 21
        })), () -> "Add assertion message");
        assertEquals("Fg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 22
        })), () -> "Add assertion message");
        assertEquals("Fw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 23
        })), () -> "Add assertion message");
        assertEquals("GA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 24
        })), () -> "Add assertion message");
        assertEquals("GQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 25
        })), () -> "Add assertion message");
        assertEquals("Gg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 26
        })), () -> "Add assertion message");
        assertEquals("Gw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 27
        })), () -> "Add assertion message");
        assertEquals("HA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 28
        })), () -> "Add assertion message");
        assertEquals("HQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 29
        })), () -> "Add assertion message");
        assertEquals("Hg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 30
        })), () -> "Add assertion message");
        assertEquals("Hw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 31
        })), () -> "Add assertion message");
        assertEquals("IA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 32
        })), () -> "Add assertion message");
        assertEquals("IQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 33
        })), () -> "Add assertion message");
        assertEquals("Ig==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 34
        })), () -> "Add assertion message");
        assertEquals("Iw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 35
        })), () -> "Add assertion message");
        assertEquals("JA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 36
        })), () -> "Add assertion message");
        assertEquals("JQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 37
        })), () -> "Add assertion message");
        assertEquals("Jg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 38
        })), () -> "Add assertion message");
        assertEquals("Jw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 39
        })), () -> "Add assertion message");
        assertEquals("KA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 40
        })), () -> "Add assertion message");
        assertEquals("KQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 41
        })), () -> "Add assertion message");
        assertEquals("Kg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 42
        })), () -> "Add assertion message");
        assertEquals("Kw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 43
        })), () -> "Add assertion message");
        assertEquals("LA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 44
        })), () -> "Add assertion message");
        assertEquals("LQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 45
        })), () -> "Add assertion message");
        assertEquals("Lg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 46
        })), () -> "Add assertion message");
        assertEquals("Lw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 47
        })), () -> "Add assertion message");
        assertEquals("MA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 48
        })), () -> "Add assertion message");
        assertEquals("MQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 49
        })), () -> "Add assertion message");
        assertEquals("Mg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 50
        })), () -> "Add assertion message");
        assertEquals("Mw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 51
        })), () -> "Add assertion message");
        assertEquals("NA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 52
        })), () -> "Add assertion message");
        assertEquals("NQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 53
        })), () -> "Add assertion message");
        assertEquals("Ng==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 54
        })), () -> "Add assertion message");
        assertEquals("Nw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 55
        })), () -> "Add assertion message");
        assertEquals("OA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 56
        })), () -> "Add assertion message");
        assertEquals("OQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 57
        })), () -> "Add assertion message");
        assertEquals("Og==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 58
        })), () -> "Add assertion message");
        assertEquals("Ow==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 59
        })), () -> "Add assertion message");
        assertEquals("PA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 60
        })), () -> "Add assertion message");
        assertEquals("PQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 61
        })), () -> "Add assertion message");
        assertEquals("Pg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 62
        })), () -> "Add assertion message");
        assertEquals("Pw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 63
        })), () -> "Add assertion message");
        assertEquals("QA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 64
        })), () -> "Add assertion message");
        assertEquals("QQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 65
        })), () -> "Add assertion message");
        assertEquals("Qg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 66
        })), () -> "Add assertion message");
        assertEquals("Qw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 67
        })), () -> "Add assertion message");
        assertEquals("RA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 68
        })), () -> "Add assertion message");
        assertEquals("RQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 69
        })), () -> "Add assertion message");
        assertEquals("Rg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 70
        })), () -> "Add assertion message");
        assertEquals("Rw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 71
        })), () -> "Add assertion message");
        assertEquals("SA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 72
        })), () -> "Add assertion message");
        assertEquals("SQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 73
        })), () -> "Add assertion message");
        assertEquals("Sg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 74
        })), () -> "Add assertion message");
        assertEquals("Sw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 75
        })), () -> "Add assertion message");
        assertEquals("TA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 76
        })), () -> "Add assertion message");
        assertEquals("TQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 77
        })), () -> "Add assertion message");
        assertEquals("Tg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 78
        })), () -> "Add assertion message");
        assertEquals("Tw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 79
        })), () -> "Add assertion message");
        assertEquals("UA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 80
        })), () -> "Add assertion message");
        assertEquals("UQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 81
        })), () -> "Add assertion message");
        assertEquals("Ug==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 82
        })), () -> "Add assertion message");
        assertEquals("Uw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 83
        })), () -> "Add assertion message");
        assertEquals("VA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 84
        })), () -> "Add assertion message");
        assertEquals("VQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 85
        })), () -> "Add assertion message");
        assertEquals("Vg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 86
        })), () -> "Add assertion message");
        assertEquals("Vw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 87
        })), () -> "Add assertion message");
        assertEquals("WA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 88
        })), () -> "Add assertion message");
        assertEquals("WQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 89
        })), () -> "Add assertion message");
        assertEquals("Wg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 90
        })), () -> "Add assertion message");
        assertEquals("Ww==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 91
        })), () -> "Add assertion message");
        assertEquals("XA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 92
        })), () -> "Add assertion message");
        assertEquals("XQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 93
        })), () -> "Add assertion message");
        assertEquals("Xg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 94
        })), () -> "Add assertion message");
        assertEquals("Xw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 95
        })), () -> "Add assertion message");
        assertEquals("YA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 96
        })), () -> "Add assertion message");
        assertEquals("YQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 97
        })), () -> "Add assertion message");
        assertEquals("Yg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 98
        })), () -> "Add assertion message");
        assertEquals("Yw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 99
        })), () -> "Add assertion message");
        assertEquals("ZA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 100
        })), () -> "Add assertion message");
        assertEquals("ZQ==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 101
        })), () -> "Add assertion message");
        assertEquals("Zg==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 102
        })), () -> "Add assertion message");
        assertEquals("Zw==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 103
        })), () -> "Add assertion message");
        assertEquals("aA==", new String(Base64Codec.encodeBase64(new byte[] {
            (byte) 104
        })), () -> "Add assertion message");
    }

    @Test
    public void testSingletonsChunked() {
        assertEquals("AA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 0
        })), () -> "Add assertion message");
        assertEquals("AQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 1
        })), () -> "Add assertion message");
        assertEquals("Ag==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 2
        })), () -> "Add assertion message");
        assertEquals("Aw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 3
        })), () -> "Add assertion message");
        assertEquals("BA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 4
        })), () -> "Add assertion message");
        assertEquals("BQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 5
        })), () -> "Add assertion message");
        assertEquals("Bg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 6
        })), () -> "Add assertion message");
        assertEquals("Bw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 7
        })), () -> "Add assertion message");
        assertEquals("CA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 8
        })), () -> "Add assertion message");
        assertEquals("CQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 9
        })), () -> "Add assertion message");
        assertEquals("Cg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 10
        })), () -> "Add assertion message");
        assertEquals("Cw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 11
        })), () -> "Add assertion message");
        assertEquals("DA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 12
        })), () -> "Add assertion message");
        assertEquals("DQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 13
        })), () -> "Add assertion message");
        assertEquals("Dg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 14
        })), () -> "Add assertion message");
        assertEquals("Dw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 15
        })), () -> "Add assertion message");
        assertEquals("EA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 16
        })), () -> "Add assertion message");
        assertEquals("EQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 17
        })), () -> "Add assertion message");
        assertEquals("Eg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 18
        })), () -> "Add assertion message");
        assertEquals("Ew==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 19
        })), () -> "Add assertion message");
        assertEquals("FA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 20
        })), () -> "Add assertion message");
        assertEquals("FQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 21
        })), () -> "Add assertion message");
        assertEquals("Fg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 22
        })), () -> "Add assertion message");
        assertEquals("Fw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 23
        })), () -> "Add assertion message");
        assertEquals("GA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 24
        })), () -> "Add assertion message");
        assertEquals("GQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 25
        })), () -> "Add assertion message");
        assertEquals("Gg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 26
        })), () -> "Add assertion message");
        assertEquals("Gw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 27
        })), () -> "Add assertion message");
        assertEquals("HA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 28
        })), () -> "Add assertion message");
        assertEquals("HQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 29
        })), () -> "Add assertion message");
        assertEquals("Hg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 30
        })), () -> "Add assertion message");
        assertEquals("Hw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 31
        })), () -> "Add assertion message");
        assertEquals("IA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 32
        })), () -> "Add assertion message");
        assertEquals("IQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 33
        })), () -> "Add assertion message");
        assertEquals("Ig==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 34
        })), () -> "Add assertion message");
        assertEquals("Iw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 35
        })), () -> "Add assertion message");
        assertEquals("JA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 36
        })), () -> "Add assertion message");
        assertEquals("JQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 37
        })), () -> "Add assertion message");
        assertEquals("Jg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 38
        })), () -> "Add assertion message");
        assertEquals("Jw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 39
        })), () -> "Add assertion message");
        assertEquals("KA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 40
        })), () -> "Add assertion message");
        assertEquals("KQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 41
        })), () -> "Add assertion message");
        assertEquals("Kg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 42
        })), () -> "Add assertion message");
        assertEquals("Kw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 43
        })), () -> "Add assertion message");
        assertEquals("LA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 44
        })), () -> "Add assertion message");
        assertEquals("LQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 45
        })), () -> "Add assertion message");
        assertEquals("Lg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 46
        })), () -> "Add assertion message");
        assertEquals("Lw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 47
        })), () -> "Add assertion message");
        assertEquals("MA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 48
        })), () -> "Add assertion message");
        assertEquals("MQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 49
        })), () -> "Add assertion message");
        assertEquals("Mg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 50
        })), () -> "Add assertion message");
        assertEquals("Mw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 51
        })), () -> "Add assertion message");
        assertEquals("NA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 52
        })), () -> "Add assertion message");
        assertEquals("NQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 53
        })), () -> "Add assertion message");
        assertEquals("Ng==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 54
        })), () -> "Add assertion message");
        assertEquals("Nw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 55
        })), () -> "Add assertion message");
        assertEquals("OA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 56
        })), () -> "Add assertion message");
        assertEquals("OQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 57
        })), () -> "Add assertion message");
        assertEquals("Og==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 58
        })), () -> "Add assertion message");
        assertEquals("Ow==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 59
        })), () -> "Add assertion message");
        assertEquals("PA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 60
        })), () -> "Add assertion message");
        assertEquals("PQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 61
        })), () -> "Add assertion message");
        assertEquals("Pg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 62
        })), () -> "Add assertion message");
        assertEquals("Pw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 63
        })), () -> "Add assertion message");
        assertEquals("QA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 64
        })), () -> "Add assertion message");
        assertEquals("QQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 65
        })), () -> "Add assertion message");
        assertEquals("Qg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 66
        })), () -> "Add assertion message");
        assertEquals("Qw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 67
        })), () -> "Add assertion message");
        assertEquals("RA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 68
        })), () -> "Add assertion message");
        assertEquals("RQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 69
        })), () -> "Add assertion message");
        assertEquals("Rg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 70
        })), () -> "Add assertion message");
        assertEquals("Rw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 71
        })), () -> "Add assertion message");
        assertEquals("SA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 72
        })), () -> "Add assertion message");
        assertEquals("SQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 73
        })), () -> "Add assertion message");
        assertEquals("Sg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 74
        })), () -> "Add assertion message");
        assertEquals("Sw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 75
        })), () -> "Add assertion message");
        assertEquals("TA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 76
        })), () -> "Add assertion message");
        assertEquals("TQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 77
        })), () -> "Add assertion message");
        assertEquals("Tg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 78
        })), () -> "Add assertion message");
        assertEquals("Tw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 79
        })), () -> "Add assertion message");
        assertEquals("UA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 80
        })), () -> "Add assertion message");
        assertEquals("UQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 81
        })), () -> "Add assertion message");
        assertEquals("Ug==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 82
        })), () -> "Add assertion message");
        assertEquals("Uw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 83
        })), () -> "Add assertion message");
        assertEquals("VA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 84
        })), () -> "Add assertion message");
        assertEquals("VQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 85
        })), () -> "Add assertion message");
        assertEquals("Vg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 86
        })), () -> "Add assertion message");
        assertEquals("Vw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 87
        })), () -> "Add assertion message");
        assertEquals("WA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 88
        })), () -> "Add assertion message");
        assertEquals("WQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 89
        })), () -> "Add assertion message");
        assertEquals("Wg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 90
        })), () -> "Add assertion message");
        assertEquals("Ww==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 91
        })), () -> "Add assertion message");
        assertEquals("XA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 92
        })), () -> "Add assertion message");
        assertEquals("XQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 93
        })), () -> "Add assertion message");
        assertEquals("Xg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 94
        })), () -> "Add assertion message");
        assertEquals("Xw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 95
        })), () -> "Add assertion message");
        assertEquals("YA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 96
        })), () -> "Add assertion message");
        assertEquals("YQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 97
        })), () -> "Add assertion message");
        assertEquals("Yg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 98
        })), () -> "Add assertion message");
        assertEquals("Yw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 99
        })), () -> "Add assertion message");
        assertEquals("ZA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 100
        })), () -> "Add assertion message");
        assertEquals("ZQ==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 101
        })), () -> "Add assertion message");
        assertEquals("Zg==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 102
        })), () -> "Add assertion message");
        assertEquals("Zw==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 103
        })), () -> "Add assertion message");
        assertEquals("aA==\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
            (byte) 104
        })), () -> "Add assertion message");
    }

    @Test
    public void testTriplets() {
        assertEquals("AAAA", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 0
        })), () -> "Add assertion message");
        assertEquals("AAAB", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 1
        })), () -> "Add assertion message");
        assertEquals("AAAC", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 2
        })), () -> "Add assertion message");
        assertEquals("AAAD", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 3
        })), () -> "Add assertion message");
        assertEquals("AAAE", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 4
        })), () -> "Add assertion message");
        assertEquals("AAAF", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 5
        })), () -> "Add assertion message");
        assertEquals("AAAG", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 6
        })), () -> "Add assertion message");
        assertEquals("AAAH", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 7
        })), () -> "Add assertion message");
        assertEquals("AAAI", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 8
        })), () -> "Add assertion message");
        assertEquals("AAAJ", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 9
        })), () -> "Add assertion message");
        assertEquals("AAAK", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 10
        })), () -> "Add assertion message");
        assertEquals("AAAL", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 11
        })), () -> "Add assertion message");
        assertEquals("AAAM", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 12
        })), () -> "Add assertion message");
        assertEquals("AAAN", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 13
        })), () -> "Add assertion message");
        assertEquals("AAAO", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 14
        })), () -> "Add assertion message");
        assertEquals("AAAP", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 15
        })), () -> "Add assertion message");
        assertEquals("AAAQ", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 16
        })), () -> "Add assertion message");
        assertEquals("AAAR", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 17
        })), () -> "Add assertion message");
        assertEquals("AAAS", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 18
        })), () -> "Add assertion message");
        assertEquals("AAAT", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 19
        })), () -> "Add assertion message");
        assertEquals("AAAU", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 20
        })), () -> "Add assertion message");
        assertEquals("AAAV", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 21
        })), () -> "Add assertion message");
        assertEquals("AAAW", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 22
        })), () -> "Add assertion message");
        assertEquals("AAAX", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 23
        })), () -> "Add assertion message");
        assertEquals("AAAY", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 24
        })), () -> "Add assertion message");
        assertEquals("AAAZ", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 25
        })), () -> "Add assertion message");
        assertEquals("AAAa", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 26
        })), () -> "Add assertion message");
        assertEquals("AAAb", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 27
        })), () -> "Add assertion message");
        assertEquals("AAAc", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 28
        })), () -> "Add assertion message");
        assertEquals("AAAd", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 29
        })), () -> "Add assertion message");
        assertEquals("AAAe", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 30
        })), () -> "Add assertion message");
        assertEquals("AAAf", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 31
        })), () -> "Add assertion message");
        assertEquals("AAAg", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 32
        })), () -> "Add assertion message");
        assertEquals("AAAh", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 33
        })), () -> "Add assertion message");
        assertEquals("AAAi", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 34
        })), () -> "Add assertion message");
        assertEquals("AAAj", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 35
        })), () -> "Add assertion message");
        assertEquals("AAAk", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 36
        })), () -> "Add assertion message");
        assertEquals("AAAl", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 37
        })), () -> "Add assertion message");
        assertEquals("AAAm", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 38
        })), () -> "Add assertion message");
        assertEquals("AAAn", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 39
        })), () -> "Add assertion message");
        assertEquals("AAAo", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 40
        })), () -> "Add assertion message");
        assertEquals("AAAp", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 41
        })), () -> "Add assertion message");
        assertEquals("AAAq", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 42
        })), () -> "Add assertion message");
        assertEquals("AAAr", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 43
        })), () -> "Add assertion message");
        assertEquals("AAAs", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 44
        })), () -> "Add assertion message");
        assertEquals("AAAt", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 45
        })), () -> "Add assertion message");
        assertEquals("AAAu", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 46
        })), () -> "Add assertion message");
        assertEquals("AAAv", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 47
        })), () -> "Add assertion message");
        assertEquals("AAAw", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 48
        })), () -> "Add assertion message");
        assertEquals("AAAx", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 49
        })), () -> "Add assertion message");
        assertEquals("AAAy", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 50
        })), () -> "Add assertion message");
        assertEquals("AAAz", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 51
        })), () -> "Add assertion message");
        assertEquals("AAA0", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 52
        })), () -> "Add assertion message");
        assertEquals("AAA1", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 53
        })), () -> "Add assertion message");
        assertEquals("AAA2", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 54
        })), () -> "Add assertion message");
        assertEquals("AAA3", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 55
        })), () -> "Add assertion message");
        assertEquals("AAA4", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 56
        })), () -> "Add assertion message");
        assertEquals("AAA5", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 57
        })), () -> "Add assertion message");
        assertEquals("AAA6", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 58
        })), () -> "Add assertion message");
        assertEquals("AAA7", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 59
        })), () -> "Add assertion message");
        assertEquals("AAA8", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 60
        })), () -> "Add assertion message");
        assertEquals("AAA9", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 61
        })), () -> "Add assertion message");
        assertEquals("AAA+", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 62
        })), () -> "Add assertion message");
        assertEquals("AAA/", new String(Base64Codec.encodeBase64(new byte[] {
                (byte) 0, (byte) 0, (byte) 63
        })), () -> "Add assertion message");
    }

    @Test
    public void testTripletsChunked() {
        assertEquals("AAAA\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 0
        })), () -> "Add assertion message");
        assertEquals("AAAB\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 1
        })), () -> "Add assertion message");
        assertEquals("AAAC\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 2
        })), () -> "Add assertion message");
        assertEquals("AAAD\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 3
        })), () -> "Add assertion message");
        assertEquals("AAAE\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 4
        })), () -> "Add assertion message");
        assertEquals("AAAF\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 5
        })), () -> "Add assertion message");
        assertEquals("AAAG\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 6
        })), () -> "Add assertion message");
        assertEquals("AAAH\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 7
        })), () -> "Add assertion message");
        assertEquals("AAAI\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 8
        })), () -> "Add assertion message");
        assertEquals("AAAJ\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 9
        })), () -> "Add assertion message");
        assertEquals("AAAK\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 10
        })), () -> "Add assertion message");
        assertEquals("AAAL\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 11
        })), () -> "Add assertion message");
        assertEquals("AAAM\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 12
        })), () -> "Add assertion message");
        assertEquals("AAAN\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 13
        })), () -> "Add assertion message");
        assertEquals("AAAO\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 14
        })), () -> "Add assertion message");
        assertEquals("AAAP\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 15
        })), () -> "Add assertion message");
        assertEquals("AAAQ\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 16
        })), () -> "Add assertion message");
        assertEquals("AAAR\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 17
        })), () -> "Add assertion message");
        assertEquals("AAAS\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 18
        })), () -> "Add assertion message");
        assertEquals("AAAT\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 19
        })), () -> "Add assertion message");
        assertEquals("AAAU\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 20
        })), () -> "Add assertion message");
        assertEquals("AAAV\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 21
        })), () -> "Add assertion message");
        assertEquals("AAAW\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 22
        })), () -> "Add assertion message");
        assertEquals("AAAX\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 23
        })), () -> "Add assertion message");
        assertEquals("AAAY\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 24
        })), () -> "Add assertion message");
        assertEquals("AAAZ\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 25
        })), () -> "Add assertion message");
        assertEquals("AAAa\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 26
        })), () -> "Add assertion message");
        assertEquals("AAAb\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 27
        })), () -> "Add assertion message");
        assertEquals("AAAc\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 28
        })), () -> "Add assertion message");
        assertEquals("AAAd\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 29
        })), () -> "Add assertion message");
        assertEquals("AAAe\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 30
        })), () -> "Add assertion message");
        assertEquals("AAAf\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 31
        })), () -> "Add assertion message");
        assertEquals("AAAg\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 32
        })), () -> "Add assertion message");
        assertEquals("AAAh\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 33
        })), () -> "Add assertion message");
        assertEquals("AAAi\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 34
        })), () -> "Add assertion message");
        assertEquals("AAAj\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 35
        })), () -> "Add assertion message");
        assertEquals("AAAk\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 36
        })), () -> "Add assertion message");
        assertEquals("AAAl\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 37
        })), () -> "Add assertion message");
        assertEquals("AAAm\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 38
        })), () -> "Add assertion message");
        assertEquals("AAAn\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 39
        })), () -> "Add assertion message");
        assertEquals("AAAo\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 40
        })), () -> "Add assertion message");
        assertEquals("AAAp\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 41
        })), () -> "Add assertion message");
        assertEquals("AAAq\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 42
        })), () -> "Add assertion message");
        assertEquals("AAAr\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 43
        })), () -> "Add assertion message");
        assertEquals("AAAs\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 44
        })), () -> "Add assertion message");
        assertEquals("AAAt\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 45
        })), () -> "Add assertion message");
        assertEquals("AAAu\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 46
        })), () -> "Add assertion message");
        assertEquals("AAAv\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 47
        })), () -> "Add assertion message");
        assertEquals("AAAw\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 48
        })), () -> "Add assertion message");
        assertEquals("AAAx\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 49
        })), () -> "Add assertion message");
        assertEquals("AAAy\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 50
        })), () -> "Add assertion message");
        assertEquals("AAAz\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 51
        })), () -> "Add assertion message");
        assertEquals("AAA0\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 52
        })), () -> "Add assertion message");
        assertEquals("AAA1\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 53
        })), () -> "Add assertion message");
        assertEquals("AAA2\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 54
        })), () -> "Add assertion message");
        assertEquals("AAA3\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 55
        })), () -> "Add assertion message");
        assertEquals("AAA4\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 56
        })), () -> "Add assertion message");
        assertEquals("AAA5\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 57
        })), () -> "Add assertion message");
        assertEquals("AAA6\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 58
        })), () -> "Add assertion message");
        assertEquals("AAA7\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 59
        })), () -> "Add assertion message");
        assertEquals("AAA8\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 60
        })), () -> "Add assertion message");
        assertEquals("AAA9\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 61
        })), () -> "Add assertion message");
        assertEquals("AAA+\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 62
        })), () -> "Add assertion message");
        assertEquals("AAA/\r\n", new String(Base64Codec.encodeBase64Chunked(new byte[] {
                (byte) 0, (byte) 0, (byte) 63
        })), () -> "Add assertion message");
    }

    @Test
    public void testKnownEncodings() {
        assertEquals(
                "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg==",
                new String(Base64Codec
                        .encodeBase64("The quick brown fox jumped over the lazy dogs."
                                .getBytes())), () -> "Add assertion message");
        assertEquals(
                "YmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJs\r\nYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFo\r\nIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBi\r\nbGFoIGJsYWg=\r\n",
                new String(
                        Base64Codec
                                .encodeBase64Chunked("blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah"
                                        .getBytes())), () -> "Add assertion message");
        assertEquals(
                "SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg==",
                new String(
                        Base64Codec
                                .encodeBase64("It was the best of times, it was the worst of times."
                                        .getBytes())), () -> "Add assertion message");
        assertEquals(
                "aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw==",
                new String(Base64Codec.encodeBase64("http://jakarta.apache.org/commmons"
                        .getBytes())), () -> "Add assertion message");
        assertEquals(
                "QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg==",
                new String(
                        Base64Codec
                                .encodeBase64("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
                                        .getBytes())), () -> "Add assertion message");
        assertEquals("eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=", new String(
                Base64Codec.encodeBase64("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }".getBytes())), () -> "Add assertion message");
        assertEquals(
                "eHl6enkh",
                new String(Base64Codec.encodeBase64("xyzzy!".getBytes())), () -> "Add assertion message");
    }

    @Test
    public void testKnownDecodings() {
        assertEquals(
                "The quick brown fox jumped over the lazy dogs.",
                new String(
                        Base64Codec
                                .decodeBase64("VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg=="
                                        .getBytes())), () -> "Add assertion message");
        assertEquals(
                "It was the best of times, it was the worst of times.",
                new String(
                        Base64Codec
                                .decodeBase64("SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg=="
                                        .getBytes())), () -> "Add assertion message");
        assertEquals("http://jakarta.apache.org/commmons", new String(Base64Codec
                .decodeBase64("aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw=="
                        .getBytes())), () -> "Add assertion message");
        assertEquals(
                "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz",
                new String(
                        Base64Codec
                                .decodeBase64("QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg=="
                                        .getBytes())), () -> "Add assertion message");
        assertEquals("{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }", new String(Base64Codec
                .decodeBase64("eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=".getBytes())), () -> "Add assertion message");
        assertEquals(
                "xyzzy!",
                new String(Base64Codec.decodeBase64("eHl6enkh".getBytes())), () -> "Add assertion message");
    }

    @Test
    public void testIgnoringNonBase64InDecode() throws Exception {
        assertEquals(
                "The quick brown fox jumped over the lazy dogs.",
                new String(
                        Base64Codec
                                .decodeBase64("VGhlIH@$#$@%F1aWN@#@#@@rIGJyb3duIGZve\n\r\t%#%#%#%CBqd##$#$W1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg=="
                                        .getBytes())), () -> "Add assertion message");
    }

    @Test
    public void testDecodeWithWhitespace() throws Exception {

        String orig = "I am a late night coder.";

        byte[] encodedArray = Base64Codec.encodeBase64(orig.getBytes());
        StringBuffer intermediate = new StringBuffer(new String(encodedArray));

        intermediate.insert(2, ' ');
        intermediate.insert(5, '\t');
        intermediate.insert(10, '\r');
        intermediate.insert(15, '\n');

        byte[] encodedWithWS = intermediate.toString().getBytes();
        byte[] decodedWithWS = Base64Codec.decodeBase64(encodedWithWS);

        String dest = new String(decodedWithWS);

        assertTrue("Dest string doesn't equals the original", dest.equals(orig));
    }

    @Test
    public void testDiscardWhitespace() throws Exception {

        String orig = "I am a late night coder.";

        byte[] encodedArray = Base64Codec.encodeBase64(orig.getBytes());
        StringBuffer intermediate = new StringBuffer(new String(encodedArray));

        intermediate.insert(2, ' ');
        intermediate.insert(5, '\t');
        intermediate.insert(10, '\r');
        intermediate.insert(15, '\n');

        byte[] encodedWithWS = intermediate.toString().getBytes();
        byte[] encodedNoWS = Base64Codec.discardWhitespace(encodedWithWS);
        byte[] decodedWithWS = Base64Codec.decodeBase64(encodedWithWS);
        byte[] decodedNoWS = Base64Codec.decodeBase64(encodedNoWS);

        String destFromWS = new String(decodedWithWS);
        String destFromNoWS = new String(decodedNoWS);

        assertTrue("Dest string doesn't eausl original", destFromWS.equals(orig));
        assertTrue("Dest string doesn't eausl original", destFromNoWS.equals(orig));
    }

    private String toString(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            buf.append(data[i]);
            if (i != data.length - 1) {
                buf.append(",");
            }
        }
        return buf.toString();
    }

    /**
     * @return Returns the _random.
     */
    public Random getRandom() {
        return this._random;
    }
}