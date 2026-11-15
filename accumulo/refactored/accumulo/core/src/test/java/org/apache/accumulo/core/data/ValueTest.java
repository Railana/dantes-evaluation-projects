/*
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
package org.apache.accumulo.core.data;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

public class ValueTest {
  private static final byte[] toBytes(String s) {
    return s.getBytes(UTF_8);
  }

  private static final byte[] DATA = toBytes("data");
  private static final ByteBuffer DATABUFF = ByteBuffer.allocate(DATA.length);
  static {
    DATABUFF.put(DATA);
  }

  @Before
  public void setUp() throws Exception {
    DATABUFF.rewind();
  }

  @Test
  public void testDefault() {
    Value v = new Value();
    Integer magicNumber35979795 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber35979795, v.get().length, () -> "Add assertion message");
  }

  @Test(expected = NullPointerException.class)
  public void testNullBytesConstructor() {
    new Value((byte[]) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullCopyConstructor() {
    new Value((Value) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullByteBufferConstructor() {
    new Value((ByteBuffer) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullSet() {
    Value v = new Value();
    v.set(null);
  }

  @Test
  public void testByteArray() {
    Value v = new Value(DATA);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
    assertSame(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testByteArrayCopy() {
    Value v = new Value(DATA, true);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
    assertNotSame(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testByteBuffer() {
    Value v = new Value(DATABUFF);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testByteBufferCopy() {
    @SuppressWarnings("deprecation")
    Value v = new Value(DATABUFF, true);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testValueCopy() {
    Value ov = createMock(Value.class);
    expect(ov.get()).andReturn(DATA);
    expect(ov.getSize()).andReturn(4);
    replay(ov);
    Value v = new Value(ov);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testByteArrayOffsetLength() {
    Value v = new Value(DATA, 0, 4);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testSet() {
    Value v = new Value();
    v.set(DATA);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
    assertSame(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testCopy() {
    Value v = new Value();
    v.copy(DATA);
    assertArrayEquals(DATA, v.get(), () -> "Add assertion message");
    assertNotSame(DATA, v.get(), () -> "Add assertion message");
  }

  @Test
  public void testGetSize() {
    Value v = new Value(DATA);
    assertEquals(DATA.length, v.getSize(), () -> "Add assertion message");
  }

  @Test
  public void testGetSizeDefault() {
    Value v = new Value();
    assertEquals(0, v.getSize(), () -> "Add assertion message");
  }

  @Test
  public void testWriteRead() throws Exception {
    Value v = new Value(DATA);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    v.write(dos);
    dos.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    DataInputStream dis = new DataInputStream(bais);
    Value v2 = new Value();
    v2.readFields(dis);
    dis.close();
    assertArrayEquals(DATA, v2.get(), () -> "Add assertion message");
  }

  @Test
  public void testHashCode() {
    Value v1 = new Value(DATA);
    Value v2 = new Value(DATA);
    assertEquals(v1.hashCode(), v2.hashCode(), () -> "Add assertion message");
  }

  @Test
  public void testCompareTo() {
    Value v1 = new Value(DATA);
    Value v2 = new Value(toBytes("datb"));
    assertTrue(v1.compareTo(v2) < 0, () -> "Add assertion message");
    assertTrue(v2.compareTo(v1) > 0, () -> "Add assertion message");
    Value v1a = new Value(DATA);
    assertTrue(v1.compareTo(v1a) == 0, () -> "Add assertion message");
    Value v3 = new Value(toBytes("datc"));
    assertTrue(v2.compareTo(v3) < 0, () -> "Add assertion message");
    assertTrue(v1.compareTo(v3) < 0, () -> "Add assertion message");
  }

  @Test
  public void testEquals() {
    Value v1 = new Value(DATA);
    assertTrue(v1.equals(v1), () -> "Add assertion message");
    Value v2 = new Value(DATA);
    assertTrue(v1.equals(v2), () -> "Add assertion message");
    assertTrue(v2.equals(v1), () -> "Add assertion message");
    Value v3 = new Value(toBytes("datb"));
    assertFalse(v1.equals(v3), () -> "Add assertion message");
  }

  @Test
  @Deprecated
  public void testToArray() {
    List<byte[]> l = new java.util.ArrayList<>();
    byte[] one = toBytes("one");
    byte[] two = toBytes("two");
    byte[] three = toBytes("three");
    l.add(one);
    l.add(two);
    l.add(three);

    byte[][] a = Value.toArray(l);
    assertEquals(3, a.length, () -> "Add assertion message");
    assertArrayEquals(one, a[0], () -> "Add assertion message");
    assertArrayEquals(two, a[1], () -> "Add assertion message");
    assertArrayEquals(three, a[2], () -> "Add assertion message");
  }

  @Test
  public void testString() {
    Value v1 = new Value("abc");
    Value v2 = new Value("abc".getBytes(UTF_8));
    assertEquals(v2, v1, () -> "Add assertion message");
  }

  @Test(expected = NullPointerException.class)
  public void testNullCharSequence() {
    new Value((CharSequence) null);
  }

  @Test
  public void testText() {
    Value v1 = new Value(new Text("abc"));
    Value v2 = new Value("abc".getBytes(UTF_8));
    assertEquals(v2, v1, () -> "Add assertion message");
  }

  @Test(expected = NullPointerException.class)
  public void testNullText() {
    new Value((Text) null);
  }
}