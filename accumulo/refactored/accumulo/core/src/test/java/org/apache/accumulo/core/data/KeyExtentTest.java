/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.accumulo.core.data.impl.KeyExtent;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

public class KeyExtentTest {
  KeyExtent nke(String t, String er, String per) {
    return new KeyExtent(t, er == null ? null : new Text(er), per == null ? null : new Text(per));
  }

  KeyExtent ke;
  TreeSet<KeyExtent> set0;

  @Before
  public void setup() {
    // Removed line due to General Fixture smell: set0 = new TreeSet<>();
  }

  @Test
  public void testDecodingMetadataRow() {
    Text flattenedExtent = new Text("foo;bar");

    ke = new KeyExtent(flattenedExtent, (Text) null);

    assertEquals(new Text("bar"), ke.getEndRow(), () -> "Add assertion message");
    assertEquals("foo", ke.getTableId(), () -> "Add assertion message");
    assertNull(ke.getPrevEndRow(), () -> "Add assertion message");

    flattenedExtent = new Text("foo<");

    ke = new KeyExtent(flattenedExtent, (Text) null);

    assertNull(ke.getEndRow(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("foo", ke.getTableId(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNull(ke.getPrevEndRow(), () -> "Add assertion message");

    flattenedExtent = new Text("foo;bar;");

    ke = new KeyExtent(flattenedExtent, (Text) null);

    assertEquals(new Text("bar;"), ke.getEndRow(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("foo", ke.getTableId(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNull(ke.getPrevEndRow(), () -> "Add assertion message");

  }

  @Test
  public void testFindContainingExtents() {
    set0 = new TreeSet<>();
    assertNull(KeyExtent.findContainingExtent(nke("t", null, null), set0), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", "1", "0"), set0), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", "1", null), set0), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", null, "0"), set0), () -> "Add assertion message");

    TreeSet<KeyExtent> set1 = new TreeSet<>();

    set1.add(nke("t", null, null));

    assertEquals(nke("t", null, null), KeyExtent.findContainingExtent(nke("t", null, null), set1), () -> "Add assertion message");
    assertEquals(nke("t", null, null), KeyExtent.findContainingExtent(nke("t", "1", "0"), set1), () -> "Add assertion message");
    assertEquals(nke("t", null, null), KeyExtent.findContainingExtent(nke("t", "1", null), set1), () -> "Add assertion message");
    assertEquals(nke("t", null, null), KeyExtent.findContainingExtent(nke("t", null, "0"), set1), () -> "Add assertion message");

    TreeSet<KeyExtent> set2 = new TreeSet<>();

    set2.add(nke("t", "g", null));
    set2.add(nke("t", null, "g"));

    assertNull(KeyExtent.findContainingExtent(nke("t", null, null), set2), () -> "Add assertion message");
    assertEquals(nke("t", "g", null), KeyExtent.findContainingExtent(nke("t", "c", "a"), set2), () -> "Add assertion message");
    assertEquals(nke("t", "g", null), KeyExtent.findContainingExtent(nke("t", "c", null), set2), () -> "Add assertion message");

    assertEquals(nke("t", "g", null), KeyExtent.findContainingExtent(nke("t", "g", "a"), set2), () -> "Add assertion message");
    assertEquals(nke("t", "g", null), KeyExtent.findContainingExtent(nke("t", "g", null), set2), () -> "Add assertion message");

    assertNull(KeyExtent.findContainingExtent(nke("t", "h", "a"), set2), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", "h", null), set2), () -> "Add assertion message");

    assertNull(KeyExtent.findContainingExtent(nke("t", "z", "f"), set2), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", null, "f"), set2), () -> "Add assertion message");

    assertEquals(nke("t", null, "g"), KeyExtent.findContainingExtent(nke("t", "z", "g"), set2), () -> "Add assertion message");
    assertEquals(nke("t", null, "g"), KeyExtent.findContainingExtent(nke("t", null, "g"), set2), () -> "Add assertion message");

    assertEquals(nke("t", null, "g"), KeyExtent.findContainingExtent(nke("t", "z", "h"), set2), () -> "Add assertion message");
    assertEquals(nke("t", null, "g"), KeyExtent.findContainingExtent(nke("t", null, "h"), set2), () -> "Add assertion message");

    TreeSet<KeyExtent> set3 = new TreeSet<>();

    set3.add(nke("t", "g", null));
    set3.add(nke("t", "s", "g"));
    set3.add(nke("t", null, "s"));

    assertNull(KeyExtent.findContainingExtent(nke("t", null, null), set3), () -> "Add assertion message");

    assertEquals(nke("t", "g", null), KeyExtent.findContainingExtent(nke("t", "g", null), set3), () -> "Add assertion message");
    assertEquals(nke("t", "s", "g"), KeyExtent.findContainingExtent(nke("t", "s", "g"), set3), () -> "Add assertion message");
    assertEquals(nke("t", null, "s"), KeyExtent.findContainingExtent(nke("t", null, "s"), set3), () -> "Add assertion message");

    assertNull(KeyExtent.findContainingExtent(nke("t", "t", "g"), set3), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", "t", "f"), set3), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", "s", "f"), set3), () -> "Add assertion message");

    assertEquals(nke("t", "s", "g"), KeyExtent.findContainingExtent(nke("t", "r", "h"), set3), () -> "Add assertion message");
    assertEquals(nke("t", "s", "g"), KeyExtent.findContainingExtent(nke("t", "s", "h"), set3), () -> "Add assertion message");
    assertEquals(nke("t", "s", "g"), KeyExtent.findContainingExtent(nke("t", "r", "g"), set3), () -> "Add assertion message");

    assertEquals(nke("t", null, "s"), KeyExtent.findContainingExtent(nke("t", null, "t"), set3), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", null, "r"), set3), () -> "Add assertion message");

    assertEquals(nke("t", "g", null), KeyExtent.findContainingExtent(nke("t", "f", null), set3), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t", "h", null), set3), () -> "Add assertion message");

    TreeSet<KeyExtent> set4 = new TreeSet<>();

    set4.add(nke("t1", "d", null));
    set4.add(nke("t1", "q", "d"));
    set4.add(nke("t1", null, "q"));
    set4.add(nke("t2", "g", null));
    set4.add(nke("t2", "s", "g"));
    set4.add(nke("t2", null, "s"));

    assertNull(KeyExtent.findContainingExtent(nke("t", null, null), set4), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("z", null, null), set4), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t11", null, null), set4), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t1", null, null), set4), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t2", null, null), set4), () -> "Add assertion message");

    assertNull(KeyExtent.findContainingExtent(nke("t", "g", null), set4), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("z", "g", null), set4), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t11", "g", null), set4), () -> "Add assertion message");
    assertNull(KeyExtent.findContainingExtent(nke("t1", "g", null), set4), () -> "Add assertion message");

    assertEquals(nke("t2", "g", null), KeyExtent.findContainingExtent(nke("t2", "g", null), set4), () -> "Add assertion message");
    assertEquals(nke("t2", "s", "g"), KeyExtent.findContainingExtent(nke("t2", "s", "g"), set4), () -> "Add assertion message");
    assertEquals(nke("t2", null, "s"), KeyExtent.findContainingExtent(nke("t2", null, "s"), set4), () -> "Add assertion message");

    assertEquals(nke("t1", "d", null), KeyExtent.findContainingExtent(nke("t1", "d", null), set4), () -> "Add assertion message");
    assertEquals(nke("t1", "q", "d"), KeyExtent.findContainingExtent(nke("t1", "q", "d"), set4), () -> "Add assertion message");
    assertEquals(nke("t1", null, "q"), KeyExtent.findContainingExtent(nke("t1", null, "q"), set4), () -> "Add assertion message");

  }

  private static boolean overlaps(KeyExtent extent, SortedMap<KeyExtent,Object> extents) {
    return !KeyExtent.findOverlapping(extent, extents).isEmpty();
  }

  @Test
  public void testOverlaps() {
    set0 = new TreeSet<>();
    SortedMap<KeyExtent,Object> set0 = new TreeMap<>();
    set0.put(nke("a", null, null), null);

    // Nothing overlaps with the empty set
    assertFalse(overlaps(nke("t", null, null), null), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", null, null), set0), () -> "Add assertion message");

    SortedMap<KeyExtent,Object> set1 = new TreeMap<>();

    // Everything overlaps with the infinite range
    set1.put(nke("t", null, null), null);
    assertTrue(overlaps(nke("t", null, null), set1), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", "b", "a"), set1), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", null, "a"), set1), () -> "Add assertion message");

    set1.put(nke("t", "b", "a"), null);
    // Line removed due to Duplicate Assert: assertTrue(overlaps(nke("t", null, null), set1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(overlaps(nke("t", "b", "a"), set1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(overlaps(nke("t", null, "a"), set1), () -> "Add assertion message");

    // simple overlaps
    SortedMap<KeyExtent,Object> set2 = new TreeMap<>();
    set2.put(nke("a", null, null), null);
    set2.put(nke("t", "m", "j"), null);
    set2.put(nke("z", null, null), null);
    assertTrue(overlaps(nke("t", null, null), set2), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", "m", "j"), set2), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", "z", "a"), set2), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", "j", "a"), set2), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", "z", "m"), set2), () -> "Add assertion message");

    // non-overlaps
    assertFalse(overlaps(nke("t", "b", "a"), set2), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", "z", "y"), set2), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", "b", null), set2), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", null, "y"), set2), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", "j", null), set2), () -> "Add assertion message");
    assertFalse(overlaps(nke("t", null, "m"), set2), () -> "Add assertion message");

    // infinite overlaps
    SortedMap<KeyExtent,Object> set3 = new TreeMap<>();
    set3.put(nke("t", "j", null), null);
    set3.put(nke("t", null, "m"), null);
    assertTrue(overlaps(nke("t", "k", "a"), set3), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", "k", null), set3), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", "z", "k"), set3), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", null, "k"), set3), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", null, null), set3), () -> "Add assertion message");

    // falls between
    assertFalse(overlaps(nke("t", "l", "k"), set3), () -> "Add assertion message");

    SortedMap<KeyExtent,Object> set4 = new TreeMap<>();
    set4.put(nke("t", null, null), null);
    assertTrue(overlaps(nke("t", "k", "a"), set4), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", "k", null), set4), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", "z", "k"), set4), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", null, "k"), set4), () -> "Add assertion message");
    assertTrue(overlaps(nke("t", null, null), set4), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertTrue(overlaps(nke("t", null, null), set4), () -> "Add assertion message");

    for (String er : new String[] {"z", "y", "r", null}) {
      for (String per : new String[] {"a", "b", "d", null}) {
        assertTrue(nke("t", "y", "b").overlaps(nke("t", er, per)), () -> "Add assertion message");
        assertTrue(nke("t", "y", null).overlaps(nke("t", er, per)), () -> "Add assertion message");
        assertTrue(nke("t", null, "b").overlaps(nke("t", er, per)), () -> "Add assertion message");
        assertTrue(nke("t", null, null).overlaps(nke("t", er, per)), () -> "Add assertion message");
      }
    }

    assertFalse(nke("t", "y", "b").overlaps(nke("t", "z", "y")), () -> "Add assertion message");
    assertFalse(nke("t", "y", "b").overlaps(nke("t", null, "y")), () -> "Add assertion message");
    assertFalse(nke("t", "y", null).overlaps(nke("t", "z", "y")), () -> "Add assertion message");
    assertFalse(nke("t", "y", null).overlaps(nke("t", null, "y")), () -> "Add assertion message");
    assertFalse(nke("t", "y", "b").overlaps(nke("t", "b", "a")), () -> "Add assertion message");
    assertFalse(nke("t", "y", "b").overlaps(nke("t", "b", null)), () -> "Add assertion message");
    assertFalse(nke("t", null, "b").overlaps(nke("t", "b", "a")), () -> "Add assertion message");
    assertFalse(nke("t", null, "b").overlaps(nke("t", "b", null)), () -> "Add assertion message");
  }

  @Test
  public void testWriteReadFields() throws Exception {
    ke = nke("t", "e", "b");
    assertEquals(ke, writeAndReadFields(ke), () -> "Add assertion message");

    ke = nke("t", "e", null);
    // Line removed due to Duplicate Assert: assertEquals(ke, writeAndReadFields(ke), () -> "Add assertion message");

    ke = nke("t", null, "b");
    // Line removed due to Duplicate Assert: assertEquals(ke, writeAndReadFields(ke), () -> "Add assertion message");

    ke = nke("t", null, null);
    // Line removed due to Duplicate Assert: assertEquals(ke, writeAndReadFields(ke), () -> "Add assertion message");
  }

  private KeyExtent writeAndReadFields(KeyExtent in) throws IOException {
    KeyExtent out = new KeyExtent();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    in.write(new DataOutputStream(baos));

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    out.readFields(new DataInputStream(bais));

    return out;
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testKeyExtentsForSimpleRange() {
    Collection<KeyExtent> results;

    results = KeyExtent.getKeyExtentsForRange(null, null, null);
    assertTrue("Non-empty set returned from no extents", results.isEmpty());

    results = KeyExtent.getKeyExtentsForRange(null, null, Collections.<KeyExtent>emptySet());
    // Line removed due to Duplicate Assert: assertTrue("Non-empty set returned from no extents", results.isEmpty());

    KeyExtent t = nke("t", null, null);
    results = KeyExtent.getKeyExtentsForRange(null, null, Collections.<KeyExtent>singleton(t));
    Integer magicNumber88131884 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals("Single tablet should always be returned",magicNumber88131884, results.size());
    assertEquals(t, results.iterator().next(), () -> "Add assertion message");
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testKeyExtentsForRange() {
    set0 = new TreeSet<>();
    KeyExtent b = nke("t", "b", null);
    KeyExtent e = nke("t", "e", "b");
    KeyExtent h = nke("t", "h", "e");
    KeyExtent m = nke("t", "m", "h");
    KeyExtent z = nke("t", null, "m");

    set0.addAll(Arrays.asList(b, e, h, m, z));

    Collection<KeyExtent> results;

    results = KeyExtent.getKeyExtentsForRange(null, null, set0);
    assertEquals("infinite range should return full set", 5, results.size());
    assertTrue("infinite range should return full set",
        results.containsAll(Arrays.asList(b, e, h, m, z)));

    results = KeyExtent.getKeyExtentsForRange(new Text("a"), new Text("z"), set0);
    assertEquals("full overlap should return full set", 5, results.size(), 5);
    assertTrue("full overlap should return full set",
        results.containsAll(Arrays.asList(b, e, h, m, z)));

    results = KeyExtent.getKeyExtentsForRange(null, new Text("f"), set0);
    assertEquals("end row should return head set", 3, results.size());
    assertTrue("end row should return head set", results.containsAll(Arrays.asList(b, e, h)));

    results = KeyExtent.getKeyExtentsForRange(new Text("f"), null, set0);
    assertEquals("start row should return tail set", 3, results.size());
    assertTrue("start row should return tail set", results.containsAll(Arrays.asList(h, m, z)));

    results = KeyExtent.getKeyExtentsForRange(new Text("f"), new Text("g"), set0);
    assertEquals("slice should return correct subset", 1, results.size());
    assertTrue("slice should return correct subset", results.contains(h));
  }

  @Test
  public void testDecodeEncode() {
    assertNull(KeyExtent.decodePrevEndRow(KeyExtent.encodePrevEndRow(null)), () -> "Add assertion message");

    Text x = new Text();
    assertEquals(x, KeyExtent.decodePrevEndRow(KeyExtent.encodePrevEndRow(x)), () -> "Add assertion message");
  }
}