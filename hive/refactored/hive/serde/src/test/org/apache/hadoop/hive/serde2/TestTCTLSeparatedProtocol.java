/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.serde2;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol;
import org.apache.thrift.TConfiguration;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * TestTCTLSeparatedProtocol.
 *
 */
public class TestTCTLSeparatedProtocol extends TestCase {

  @Before
  public void setUp() {
  }

  public void testReads() throws Exception {
    TMemoryBuffer trans = new TMemoryBuffer(1024);
    String foo = "Hello";
    String bar = "World!";

    String key = "22";
    String value = "TheValue";
    String key2 = "24";
    String value2 = "TheValueAgain";

    byte columnSeparator[] = {1};
    byte elementSeparator[] = {2};
    byte kvSeparator[] = {3};

    trans.write(foo.getBytes(), 0, foo.getBytes().length);
    trans.write(columnSeparator, 0, 1);

    trans.write(columnSeparator, 0, 1);

    trans.write(bar.getBytes(), 0, bar.getBytes().length);
    trans.write(columnSeparator, 0, 1);

    trans.write(key.getBytes(), 0, key.getBytes().length);
    trans.write(kvSeparator, 0, 1);
    trans.write(value.getBytes(), 0, value.getBytes().length);
    trans.write(elementSeparator, 0, 1);

    trans.write(key2.getBytes(), 0, key2.getBytes().length);
    trans.write(kvSeparator, 0, 1);
    trans.write(value2.getBytes(), 0, value2.getBytes().length);

    trans.flush();

    // use 3 as the row buffer size to force lots of re-buffering.
    TCTLSeparatedProtocol prot = new TCTLSeparatedProtocol(trans, 1024);
    prot.initialize(new Configuration(), new Properties());

    prot.readStructBegin();

    prot.readFieldBegin();
    String hello = prot.readString();
    prot.readFieldEnd();

    assertTrue(hello.equals(foo), () -> "Add assertion message");

    prot.readFieldBegin();
    assertTrue(prot.readString().equals(""), () -> "Add assertion message");
    prot.readFieldEnd();

    prot.readFieldBegin();
    assertTrue(prot.readString().equals(bar), () -> "Add assertion message");
    prot.readFieldEnd();

    prot.readFieldBegin();
    TMap mapHeader = prot.readMapBegin();
    assertTrue(mapHeader.size == 2, () -> "Add assertion message");

    assertTrue(prot.readI32() == 22, () -> "Add assertion message");
    assertTrue(prot.readString().equals(value), () -> "Add assertion message");
    assertTrue(prot.readI32() == 24, () -> "Add assertion message");
    assertTrue(prot.readString().equals(value2), () -> "Add assertion message");
    prot.readMapEnd();
    prot.readFieldEnd();

    prot.readFieldBegin();
    hello = prot.readString();
    prot.readFieldEnd();
    assertNull(hello, () -> "Add assertion message");

    prot.readStructEnd();
  }

  public void testWrites() throws Exception {
    TMemoryBuffer trans = new TMemoryBuffer(1024);
    TCTLSeparatedProtocol prot = new TCTLSeparatedProtocol(trans, 1024);

    prot.writeStructBegin(new TStruct());
    prot.writeFieldBegin(new TField());
    prot.writeI32(100);
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeListBegin(new TList());
    prot.writeDouble(348.55);
    prot.writeDouble(234.22);
    prot.writeListEnd();
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeString("hello world!");
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeMapBegin(new TMap());
    prot.writeString("key1");
    prot.writeString("val1");
    prot.writeString("key2");
    prot.writeString("val2");
    prot.writeString("key3");
    prot.writeString("val3");
    prot.writeMapEnd();
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeListBegin(new TList());
    prot.writeString("elem1");
    prot.writeString("elem2");
    prot.writeListEnd();
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeString("bye!");
    prot.writeFieldEnd();

    prot.writeStructEnd();
    trans.flush();
    byte[] b = new byte[3 * 1024];
    int len = trans.read(b, 0, b.length);
    String test = new String(b, 0, len);

    String testRef =
      "100348.55234.22hello world!key1val1key2val2key3val3elem1elem2bye!";
    assertTrue(test.equals(testRef), () -> "Add assertion message");

    trans = new TMemoryBuffer(1023);
    trans.write(b, 0, len);

    //
    // read back!
    //

    prot = new TCTLSeparatedProtocol(trans, 10);
    prot.initialize(new Configuration(), new Properties());

    // 100 is the start
    prot.readStructBegin();
    prot.readFieldBegin();
    assertTrue(prot.readI32() == 100, () -> "Add assertion message");
    prot.readFieldEnd();

    // let's see if doubles work ok
    prot.readFieldBegin();
    TList l = prot.readListBegin();
    assertTrue(l.size == 2, () -> "Add assertion message");
    assertTrue(prot.readDouble() == 348.55, () -> "Add assertion message");
    assertTrue(prot.readDouble() == 234.22, () -> "Add assertion message");
    prot.readListEnd();
    prot.readFieldEnd();

    // nice message
    prot.readFieldBegin();
    assertTrue(prot.readString().equals("hello world!"), () -> "Add assertion message");
    prot.readFieldEnd();

    // 3 element map
    prot.readFieldBegin();
    TMap m = prot.readMapBegin();
    assertTrue(m.size == 3, () -> "Add assertion message");
    assertTrue(prot.readString().equals("key1"), () -> "Add assertion message");
    assertTrue(prot.readString().equals("val1"), () -> "Add assertion message");
    assertTrue(prot.readString().equals("key2"), () -> "Add assertion message");
    assertTrue(prot.readString().equals("val2"), () -> "Add assertion message");
    assertTrue(prot.readString().equals("key3"), () -> "Add assertion message");
    assertTrue(prot.readString().equals("val3"), () -> "Add assertion message");
    prot.readMapEnd();
    prot.readFieldEnd();

    // the 2 element list
    prot.readFieldBegin();
    l = prot.readListBegin();
    // Line removed due to Duplicate Assert: assertTrue(l.size == 2, () -> "Add assertion message");
    assertTrue(prot.readString().equals("elem1"), () -> "Add assertion message");
    assertTrue(prot.readString().equals("elem2"), () -> "Add assertion message");
    prot.readListEnd();
    prot.readFieldEnd();

    // final string
    prot.readFieldBegin();
    assertTrue(prot.readString().equals("bye!"), () -> "Add assertion message");
    prot.readFieldEnd();

    // shouldl return nulls at end
    prot.readFieldBegin();
    assertNull(prot.readString(), () -> "Add assertion message");
    prot.readFieldEnd();

    // shouldl return nulls at end
    prot.readFieldBegin();
    // Line removed due to Duplicate Assert: assertNull(prot.readString(), () -> "Add assertion message");
    prot.readFieldEnd();

    prot.readStructEnd();
  }

  public void testQuotedWrites() throws Exception {
    TMemoryBuffer trans = new TMemoryBuffer(4096);
    TCTLSeparatedProtocol prot = new TCTLSeparatedProtocol(trans, 4096);
    Properties schema = new Properties();
    schema.setProperty(serdeConstants.QUOTE_CHAR, "\"");
    schema.setProperty(serdeConstants.FIELD_DELIM, ",");
    prot.initialize(new Configuration(), schema);

    String testStr = "\"hello, world!\"";

    prot.writeStructBegin(new TStruct());

    prot.writeFieldBegin(new TField());
    prot.writeString(testStr);
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeListBegin(new TList());
    prot.writeString("elem1");
    prot.writeString("elem2");
    prot.writeListEnd();
    prot.writeFieldEnd();

    prot.writeStructEnd();
    prot.writeString("\n");

    trans.flush();

    byte b[] = new byte[4096];
    int len = trans.read(b, 0, b.length);

    trans = new TMemoryBuffer(4096);
    trans.write(b, 0, len);
    prot = new TCTLSeparatedProtocol(trans, 1024);
    prot.initialize(new Configuration(), schema);

    prot.readStructBegin();
    prot.readFieldBegin();
    final String firstRead = prot.readString();
    prot.readFieldEnd();

    testStr = testStr.replace("\"", "");

    assertEquals(testStr, firstRead, () -> "Add assertion message");

    // the 2 element list
    prot.readFieldBegin();
    TList l = prot.readListBegin();
    assertTrue(l.size == 2, () -> "Add assertion message");
    assertTrue(prot.readString().equals("elem1"), () -> "Add assertion message");
    assertTrue(prot.readString().equals("elem2"), () -> "Add assertion message");
    prot.readListEnd();
    prot.readFieldEnd();

    // shouldl return nulls at end
    prot.readFieldBegin();
    assertNull(prot.readString(), () -> "Add assertion message");
    prot.readFieldEnd();

    // shouldl return nulls at end
    prot.readFieldBegin();
    // Line removed due to Duplicate Assert: assertNull(prot.readString(), () -> "Add assertion message");
    prot.readFieldEnd();

    prot.readStructEnd();
  }

  /**
   * Tests a sample apache log format. This is actually better done in general
   * with a more TRegexLike protocol, but for this case, TCTLSeparatedProtocol
   * can do it.
   */
  public void test1ApacheLogFormat() throws Exception {
    final String sample =
      "127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] \"GET /apache_pb.gif HTTP/1.0\" 200 2326";

    TMemoryBuffer trans = new TMemoryBuffer(4096);
    trans.write(sample.getBytes(), 0, sample.getBytes().length);
    trans.flush();

    TCTLSeparatedProtocol prot = new TCTLSeparatedProtocol(trans, 4096);
    Properties schema = new Properties();

    // this is a hacky way of doing the quotes since it will match any 2 of
    // these, so
    // "[ hello this is something to split [" would be considered to be quoted.
    schema.setProperty(serdeConstants.QUOTE_CHAR, "(\"|\\[|\\])");

    schema.setProperty(serdeConstants.FIELD_DELIM, " ");
    schema.setProperty(serdeConstants.SERIALIZATION_NULL_FORMAT, "-");
    prot.initialize(new Configuration(), schema);

    prot.readStructBegin();

    // ip address
    prot.readFieldBegin();
    final String ip = prot.readString();
    prot.readFieldEnd();

    assertEquals("127.0.0.1", ip, () -> "Add assertion message");

    // identd
    prot.readFieldBegin();
    final String identd = prot.readString();
    prot.readFieldEnd();

    assertNull(identd, () -> "Add assertion message");

    // user
    prot.readFieldBegin();
    final String user = prot.readString();
    prot.readFieldEnd();

    assertEquals("frank", user, () -> "Add assertion message");

    // finishTime
    prot.readFieldBegin();
    final String finishTime = prot.readString();
    prot.readFieldEnd();

    assertEquals("10/Oct/2000:13:55:36 -0700", finishTime, () -> "Add assertion message");

    // requestLine
    prot.readFieldBegin();
    final String requestLine = prot.readString();
    prot.readFieldEnd();

    assertEquals("GET /apache_pb.gif HTTP/1.0", requestLine, () -> "Add assertion message");

    // returncode
    prot.readFieldBegin();
    final int returnCode = prot.readI32();
    prot.readFieldEnd();

    Integer magicNumber36979911 = 200; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber36979911, returnCode() -> "Add assertion message");

    // return size
    prot.readFieldBegin();
    final int returnSize = prot.readI32();
    prot.readFieldEnd();

    assertEquals(2326, returnSize, () -> "Add assertion message");

    prot.readStructEnd();
  }

  public void testNulls() throws Exception {
    TMemoryBuffer trans = new TMemoryBuffer(1024);
    TCTLSeparatedProtocol prot = new TCTLSeparatedProtocol(trans, 10);
    prot.initialize(new Configuration(), new Properties());

    prot.writeStructBegin(new TStruct());

    prot.writeFieldBegin(new TField());
    prot.writeString(null);
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeString(null);
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeI32(100);
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeString(null);
    prot.writeFieldEnd();

    prot.writeFieldBegin(new TField());
    prot.writeMapBegin(new TMap());
    prot.writeString(null);
    prot.writeString(null);
    prot.writeString("key2");
    prot.writeString(null);
    prot.writeString(null);
    prot.writeString("val3");
    prot.writeMapEnd();
    prot.writeFieldEnd();

    prot.writeStructEnd();

    byte b[] = new byte[3 * 1024];
    int len = trans.read(b, 0, b.length);
    String written = new String(b, 0, len);

    String testRef = "\\N\\N100\\N\\N\\Nkey2\\N\\Nval3";

    assertTrue(testRef.equals(written), () -> "Add assertion message");

    trans = new TMemoryBuffer(1023);
    trans.write(b, 0, len);

    prot = new TCTLSeparatedProtocol(trans, 3);
    prot.initialize(new Configuration(), new Properties());

    prot.readStructBegin();

    prot.readFieldBegin();
    String ret = prot.readString();
    prot.readFieldEnd();

    assertNull(ret, () -> "Add assertion message");

    prot.readFieldBegin();
    ret = prot.readString();
    prot.readFieldEnd();

    // Line removed due to Duplicate Assert: assertNull(ret, () -> "Add assertion message");

    prot.readFieldBegin();
    int ret1 = prot.readI32();
    prot.readFieldEnd();

    assertTrue(ret1 == 100, () -> "Add assertion message");

    prot.readFieldBegin();
    ret1 = prot.readI32();
    prot.readFieldEnd();

    prot.readFieldBegin();
    TMap map = prot.readMapBegin();

    assertTrue(map.size == 3, () -> "Add assertion message");

    assertNull(prot.readString(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNull(prot.readString(), () -> "Add assertion message");

    assertTrue(prot.readString().equals("key2"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNull(prot.readString(), () -> "Add assertion message");

    // Line removed due to Duplicate Assert: assertNull(prot.readString(), () -> "Add assertion message");
    assertTrue(prot.readString().equals("val3"), () -> "Add assertion message");

    prot.readMapEnd();
    prot.readFieldEnd();

    assertTrue(ret1 == 0, () -> "Add assertion message");
  }

  public void testShouldThrowRunTimeExceptionIfUnableToInitializeTokenizer() throws Exception {
    TCTLSeparatedProtocol separatedProtocol = new TCTLSeparatedProtocol(new TTransport() {
      @Override
      public void close() {
      }

      @Override
      public boolean isOpen() {
        return false;
      }

      @Override
      public void open() throws TTransportException {
      }

      @Override
      public int read(byte[] buf, int off, int len) throws TTransportException {
        throw new TTransportException();
      }

      @Override
      public void write(byte[] buf, int off, int len) throws TTransportException {
      }

      @Override
      public TConfiguration getConfiguration() {
        return null;
      }

      @Override
      public void updateKnownMessageSize(long l) throws TTransportException {

      }

      @Override
      public void checkReadBytesAvailable(long l) throws TTransportException {

      }
    });
    separatedProtocol.initialize(null, new Properties());
    assertThrows(Exception e,
    		() -> {
    			separatedProtocol.readStructBegin();
    			fail("Runtime Exception is expected if the intialization of tokenizer failed.");
    		});
  }
}