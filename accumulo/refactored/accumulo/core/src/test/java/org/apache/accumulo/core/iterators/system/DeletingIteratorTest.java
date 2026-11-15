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
package org.apache.accumulo.core.iterators.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.SortedMapIterator;
import org.apache.hadoop.io.Text;
import org.junit.Test;

public class DeletingIteratorTest {

  private static final Collection<ByteSequence> EMPTY_COL_FAMS = new ArrayList<>();

  @Test
  public void test1() {
    Text colf = new Text("a");
    Text colq = new Text("b");
    Value dvOld = new Value("old".getBytes());
    Value dvDel = new Value("old".getBytes());
    Value dvNew = new Value("new".getBytes());

    TreeMap<Key,Value> tm = new TreeMap<>();
    Key k;

    for (int i = 0; i < 2; i++) {
      for (long j = 0; j < 5; j++) {
        k = new Key(new Text(String.format("%03d", i)), colf, colq, j);
        tm.put(k, dvOld);
      }
    }
    k = new Key(new Text(String.format("%03d", 0)), colf, colq, 5);
    k.setDeleted(true);
    tm.put(k, dvDel);
    for (int i = 0; i < 2; i++) {
      for (long j = 6; j < 11; j++) {
        k = new Key(new Text(String.format("%03d", i)), colf, colq, j);
        tm.put(k, dvNew);
      }
    }
    assertTrue("Initial size was " + tm.size(), tm.size() == 21);

    Text checkRow = new Text("000");
    try {
      DeletingIterator it = new DeletingIterator(new SortedMapIterator(tm), false);
      it.seek(new Range(), EMPTY_COL_FAMS, false);

      TreeMap<Key,Value> tmOut = new TreeMap<>();
      while (it.hasTop()) {
        tmOut.put(it.getTopKey(), it.getTopValue());
        it.next();
      }
      assertTrue("size after no propagation was " + tmOut.size(), tmOut.size() == 15);
      for (Entry<Key,Value> e : tmOut.entrySet()) {
        if (e.getKey().getRow().equals(checkRow)) {
          byte[] b = e.getValue().get();
          assertTrue(b[0] == 'n', () -> "Add assertion message");
          assertTrue(b[1] == 'e', () -> "Add assertion message");
          assertTrue(b[2] == 'w', () -> "Add assertion message");
        }
      }
    } catch (IOException e) {
      assertFalse(true, () -> "Add assertion message");
    }

    try {
      DeletingIterator it = new DeletingIterator(new SortedMapIterator(tm), true);
      it.seek(new Range(), EMPTY_COL_FAMS, false);
      TreeMap<Key,Value> tmOut = new TreeMap<>();
      while (it.hasTop()) {
        tmOut.put(it.getTopKey(), it.getTopValue());
        it.next();
      }
      assertTrue("size after propagation was " + tmOut.size(), tmOut.size() == 16);
      for (Entry<Key,Value> e : tmOut.entrySet()) {
        if (e.getKey().getRow().equals(checkRow)) {
          byte[] b = e.getValue().get();
          if (e.getKey().isDeleted()) {
            assertTrue(b[0] == 'o', () -> "Add assertion message");
            assertTrue(b[1] == 'l', () -> "Add assertion message");
            assertTrue(b[2] == 'd', () -> "Add assertion message");
          } else {
            // Line removed due to Duplicate Assert: assertTrue(b[0] == 'n', () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertTrue(b[1] == 'e', () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertTrue(b[2] == 'w', () -> "Add assertion message");
          }
        }
      }
    } catch (IOException e) {
      // Line removed due to Duplicate Assert: assertFalse(true, () -> "Add assertion message");
    }
  }

  // seek test
  @Test
  public void test2() throws IOException {
    TreeMap<Key,Value> tm = new TreeMap<>();

    newKeyValue(tm, "r000", 4, false, "v4");
    newKeyValue(tm, "r000", 3, false, "v3");
    newKeyValue(tm, "r000", 2, true, "v2");
    newKeyValue(tm, "r000", 1, false, "v1");

    DeletingIterator it = new DeletingIterator(new SortedMapIterator(tm), false);

    // SEEK two keys before delete
    it.seek(newRange("r000", 4), EMPTY_COL_FAMS, false);

    assertTrue(it.hasTop(), () -> "Add assertion message");
    assertEquals(newKey("r000", 4), it.getTopKey(), () -> "Add assertion message");
    assertEquals("v4", it.getTopValue().toString(), () -> "Add assertion message");

    it.next();

    // Line removed due to Duplicate Assert: assertTrue(it.hasTop(), () -> "Add assertion message");
    assertEquals(newKey("r000", 3), it.getTopKey(), () -> "Add assertion message");
    assertEquals("v3", it.getTopValue().toString(), () -> "Add assertion message");

    it.next();

    assertFalse(it.hasTop(), () -> "Add assertion message");

    // SEEK passed delete
    it.seek(newRange("r000", 1), EMPTY_COL_FAMS, false);

    // Line removed due to Duplicate Assert: assertFalse(it.hasTop(), () -> "Add assertion message");

    // SEEK to delete
    it.seek(newRange("r000", 2), EMPTY_COL_FAMS, false);

    // Line removed due to Duplicate Assert: assertFalse(it.hasTop(), () -> "Add assertion message");

    // SEEK right before delete
    it.seek(newRange("r000", 3), EMPTY_COL_FAMS, false);

    // Line removed due to Duplicate Assert: assertTrue(it.hasTop(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(newKey("r000", 3), it.getTopKey(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals("v3", it.getTopValue().toString(), () -> "Add assertion message");

    it.next();

    // Line removed due to Duplicate Assert: assertFalse(it.hasTop(), () -> "Add assertion message");
  }

  // test delete with same timestamp as existing key
  @Test
  public void test3() throws IOException {
    TreeMap<Key,Value> tm = new TreeMap<>();

    newKeyValue(tm, "r000", 3, false, "v3");
    newKeyValue(tm, "r000", 2, false, "v2");
    newKeyValue(tm, "r000", 2, true, "");
    newKeyValue(tm, "r000", 1, false, "v1");

    DeletingIterator it = new DeletingIterator(new SortedMapIterator(tm), false);
    it.seek(new Range(), EMPTY_COL_FAMS, false);

    assertTrue(it.hasTop(), () -> "Add assertion message");
    assertEquals(newKey("r000", 3), it.getTopKey(), () -> "Add assertion message");
    assertEquals("v3", it.getTopValue().toString(), () -> "Add assertion message");

    it.next();

    assertFalse(it.hasTop(), () -> "Add assertion message");

    it.seek(newRange("r000", 2), EMPTY_COL_FAMS, false);

    // Line removed due to Duplicate Assert: assertFalse(it.hasTop(), () -> "Add assertion message");
  }

  // test range inclusiveness
  @Test
  public void test4() throws IOException {
    TreeMap<Key,Value> tm = new TreeMap<>();

    newKeyValue(tm, "r000", 3, false, "v3");
    newKeyValue(tm, "r000", 2, false, "v2");
    newKeyValue(tm, "r000", 2, true, "");
    newKeyValue(tm, "r000", 1, false, "v1");

    DeletingIterator it = new DeletingIterator(new SortedMapIterator(tm), false);

    it.seek(newRange("r000", 3), EMPTY_COL_FAMS, false);

    assertTrue(it.hasTop(), () -> "Add assertion message");
    assertEquals(newKey("r000", 3), it.getTopKey(), () -> "Add assertion message");
    assertEquals("v3", it.getTopValue().toString(), () -> "Add assertion message");

    it.next();

    assertFalse(it.hasTop(), () -> "Add assertion message");

    it.seek(newRange("r000", 3, false), EMPTY_COL_FAMS, false);

    // Line removed due to Duplicate Assert: assertFalse(it.hasTop(), () -> "Add assertion message");
  }

  private Range newRange(String row, long ts, boolean inclusive) {
    return new Range(newKey(row, ts), inclusive, null, true);
  }

  private Range newRange(String row, long ts) {
    return newRange(row, ts, true);
  }

  private Key newKey(String row, long ts) {
    return new Key(new Text(row), ts);
  }

  private void newKeyValue(TreeMap<Key,Value> tm, String row, long ts, boolean deleted,
      String val) {
    Key k = newKey(row, ts);
    k.setDeleted(deleted);
    tm.put(k, new Value(val.getBytes()));
  }
}