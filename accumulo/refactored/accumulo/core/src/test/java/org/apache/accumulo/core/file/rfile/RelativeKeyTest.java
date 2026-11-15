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
package org.apache.accumulo.core.file.rfile;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.accumulo.core.data.ArrayByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.PartialKey;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.file.rfile.RelativeKey.SkippR;
import org.apache.accumulo.core.util.MutableByteSequence;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RelativeKeyTest {

  @Test
  public void testCommonPrefix() {
    // exact matches
    ArrayByteSequence exact = new ArrayByteSequence("abc");
    Integer magicNumber22154060 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber22154060, RelativeKey.getCommonPrefix(exact, exact), () -> "Add assertion message");
    Integer magicNumber23285254 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber23285254, commonPrefixHelper("", ""), () -> "Add assertion message");
    Integer magicNumber6831009 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber6831009, commonPrefixHelper("a", "a"), () -> "Add assertion message");
    Integer magicNumber72775949 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber72775949, commonPrefixHelper("aa", "aa"), () -> "Add assertion message");
    Integer magicNumber69320330 = -1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber69320330, commonPrefixHelper("aaa", "aaa"), () -> "Add assertion message");
    assertEquals(-1, commonPrefixHelper("abab", "abab"), () -> "Add assertion message");
    assertEquals(-1,
        commonPrefixHelper(new String("aaa"), new ArrayByteSequence("aaa").toString()), () -> "Add assertion message");
    assertEquals(-1,
        commonPrefixHelper("abababababab".substring(3, 6), "ccababababcc".substring(3, 6)), () -> "Add assertion message");

    // no common prefix
    assertEquals(0, commonPrefixHelper("", "a"), () -> "Add assertion message");
    Integer magicNumber46499148 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber46499148, commonPrefixHelper("a", ""), () -> "Add assertion message");
    assertEquals(0, commonPrefixHelper("a", "b"), () -> "Add assertion message");
    Integer magicNumber44215408 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber44215408, commonPrefixHelper("aaaa", "bbbb"), () -> "Add assertion message");

    // some common prefix
    assertEquals(1, commonPrefixHelper("a", "ab"), () -> "Add assertion message");
    assertEquals(1, commonPrefixHelper("ab", "ac"), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, commonPrefixHelper("ab", "ac"), () -> "Add assertion message");
    assertEquals(2, commonPrefixHelper("aa", "aaaa"), () -> "Add assertion message");
    assertEquals(4, commonPrefixHelper("aaaaa", "aaaab"), () -> "Add assertion message");
  }

  private int commonPrefixHelper(String a, String b) {
    return RelativeKey.getCommonPrefix(new ArrayByteSequence(a), new ArrayByteSequence(b));
  }

  @Test
  public void testReadWritePrefix() throws IOException {
    Key prevKey = new Key("row1", "columnfamily1", "columnqualifier1", "columnvisibility1", 1000);
    Key newKey = new Key("row2", "columnfamily2", "columnqualifier2", "columnvisibility2", 3000);
    RelativeKey expected = new RelativeKey(prevKey, newKey);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(baos);
    expected.write(out);

    RelativeKey actual = new RelativeKey();
    actual.setPrevKey(prevKey);
    actual.readFields(new DataInputStream(new ByteArrayInputStream(baos.toByteArray())));

    assertEquals(expected.getKey(), actual.getKey(), () -> "Add assertion message");
  }

  private static ArrayList<Key> expectedKeys;
  private static ArrayList<Value> expectedValues;
  private static ArrayList<Integer> expectedPositions;
  private static ByteArrayOutputStream baos;

  @BeforeClass
  public static void initSource() throws IOException {
    int initialListSize = 10000;

    baos = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(baos);

    expectedKeys = new ArrayList<>(initialListSize);
    expectedValues = new ArrayList<>(initialListSize);
    expectedPositions = new ArrayList<>(initialListSize);

    Key prev = null;
    int val = 0;
    for (int row = 0; row < 4; row++) {
      String rowS = RFileTest.formatString("r_", row);
      for (int cf = 0; cf < 4; cf++) {
        String cfS = RFileTest.formatString("cf_", cf);
        for (int cq = 0; cq < 4; cq++) {
          String cqS = RFileTest.formatString("cq_", cq);
          for (int cv = 'A'; cv < 'A' + 4; cv++) {
            String cvS = "" + (char) cv;
            for (int ts = 4; ts > 0; ts--) {
              Key k = RFileTest.newKey(rowS, cfS, cqS, cvS, ts);
              k.setDeleted(true);
              Value v = RFileTest.newValue("" + val);
              expectedPositions.add(out.size());
              new RelativeKey(prev, k).write(out);
              prev = k;
              v.write(out);
              expectedKeys.add(k);
              expectedValues.add(v);

              k = RFileTest.newKey(rowS, cfS, cqS, cvS, ts);
              v = RFileTest.newValue("" + val);
              expectedPositions.add(out.size());
              new RelativeKey(prev, k).write(out);
              prev = k;
              v.write(out);
              expectedKeys.add(k);
              expectedValues.add(v);

              val++;
            }
          }
        }
      }
    }
  }

  private DataInputStream in;

  @Before
  public void setupDataInputStream() {
    in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    // Removed line due to General Fixture smell: in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    in.mark(0);
  }

  @Test
  public void testSeekBeforeEverything() throws IOException {
    in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    Key seekKey = new Key();
    Key prevKey = new Key();
    Key currKey = null;
    MutableByteSequence value = new MutableByteSequence(new byte[64], 0, 0);

    RelativeKey.SkippR skippr =
        RelativeKey.fastSkip(in, seekKey, value, prevKey, currKey, expectedKeys.size());
    Integer magicNumber87530992 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber87530992, skippr.skipped() -> "Add assertion message");
    assertEquals(new Key(), skippr.prevKey, () -> "Add assertion message");
    assertEquals(expectedKeys.get(0), skippr.rk.getKey(), () -> "Add assertion message");
    assertEquals(expectedValues.get(0).toString(), value.toString(), () -> "Add assertion message");

    // ensure we can advance after fastskip
    skippr.rk.readFields(in);
    assertEquals(expectedKeys.get(1), skippr.rk.getKey(), () -> "Add assertion message");

    in.reset();

    seekKey = new Key("a", "b", "c", "d", 1);
    seekKey.setDeleted(true);
    skippr = RelativeKey.fastSkip(in, seekKey, value, prevKey, currKey, expectedKeys.size());
    // Line removed due to Duplicate Assert: assertEquals(1, skippr.skipped, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(new Key(), skippr.prevKey, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(expectedKeys.get(0), skippr.rk.getKey(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(expectedValues.get(0).toString(), value.toString(), () -> "Add assertion message");

    skippr.rk.readFields(in);
    // Line removed due to Duplicate Assert: assertEquals(expectedKeys.get(1), skippr.rk.getKey(), () -> "Add assertion message");
  }

  @Test(expected = EOFException.class)
  public void testSeekAfterEverythingWrongCount() throws IOException {
    in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    Key seekKey = new Key("s", "t", "u", "v", 1);
    Key prevKey = new Key();
    Key currKey = null;
    MutableByteSequence value = new MutableByteSequence(new byte[64], 0, 0);

    RelativeKey.fastSkip(in, seekKey, value, prevKey, currKey, expectedKeys.size() + 1);
  }

  public void testSeekAfterEverything() throws IOException {
    in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    Key seekKey = new Key("s", "t", "u", "v", 1);
    Key prevKey = new Key();
    Key currKey = null;
    MutableByteSequence value = new MutableByteSequence(new byte[64], 0, 0);

    SkippR skippr = RelativeKey.fastSkip(in, seekKey, value, prevKey, currKey, expectedKeys.size());
    assertEquals(expectedKeys.size(), skippr.skipped, () -> "Add assertion message");
  }

  @Test
  public void testSeekMiddle() throws IOException {
    in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    int seekIndex = expectedKeys.size() / 2;
    Key seekKey = expectedKeys.get(seekIndex);
    Key prevKey = new Key();
    Key currKey = null;
    MutableByteSequence value = new MutableByteSequence(new byte[64], 0, 0);

    RelativeKey.SkippR skippr =
        RelativeKey.fastSkip(in, seekKey, value, prevKey, currKey, expectedKeys.size());

    assertEquals(seekIndex + 1, skippr.skipped, () -> "Add assertion message");
    assertEquals(expectedKeys.get(seekIndex - 1), skippr.prevKey, () -> "Add assertion message");
    assertEquals(expectedKeys.get(seekIndex), skippr.rk.getKey(), () -> "Add assertion message");
    assertEquals(expectedValues.get(seekIndex).toString(), value.toString(), () -> "Add assertion message");

    skippr.rk.readFields(in);
    assertEquals(expectedValues.get(seekIndex + 1).toString(), value.toString(), () -> "Add assertion message");

    // try fast skipping to a key that does not exist
    in.reset();
    Key fKey = expectedKeys.get(seekIndex).followingKey(PartialKey.ROW_COLFAM_COLQUAL);
    int i;
    for (i = seekIndex; expectedKeys.get(i).compareTo(fKey) < 0; i++) {}

    int left = expectedKeys.size();

    skippr =
        RelativeKey.fastSkip(in, expectedKeys.get(i), value, prevKey, currKey, expectedKeys.size());
    assertEquals(i + 1, skippr.skipped, () -> "Add assertion message");
    left -= skippr.skipped;
    assertEquals(expectedKeys.get(i - 1), skippr.prevKey, () -> "Add assertion message");
    assertEquals(expectedKeys.get(i), skippr.rk.getKey(), () -> "Add assertion message");
    assertEquals(expectedValues.get(i).toString(), value.toString(), () -> "Add assertion message");

    // try fast skipping to our current location
    skippr = RelativeKey.fastSkip(in, expectedKeys.get(i), value, expectedKeys.get(i - 1),
        expectedKeys.get(i), left);
    assertEquals(0, skippr.skipped, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(expectedKeys.get(i - 1), skippr.prevKey, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(expectedKeys.get(i), skippr.rk.getKey(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(expectedValues.get(i).toString(), value.toString(), () -> "Add assertion message");

    // try fast skipping 1 column family ahead from our current location, testing fastskip from
    // middle of block as opposed to stating at beginning of block
    fKey = expectedKeys.get(i).followingKey(PartialKey.ROW_COLFAM);
    int j;
    for (j = i; expectedKeys.get(j).compareTo(fKey) < 0; j++) {}
    skippr =
        RelativeKey.fastSkip(in, fKey, value, expectedKeys.get(i - 1), expectedKeys.get(i), left);
    assertEquals(j - i, skippr.skipped, () -> "Add assertion message");
    assertEquals(expectedKeys.get(j - 1), skippr.prevKey, () -> "Add assertion message");
    assertEquals(expectedKeys.get(j), skippr.rk.getKey(), () -> "Add assertion message");
    assertEquals(expectedValues.get(j).toString(), value.toString(), () -> "Add assertion message");

  }
}