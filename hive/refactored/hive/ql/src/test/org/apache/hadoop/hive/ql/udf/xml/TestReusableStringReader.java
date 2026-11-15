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

package org.apache.hadoop.hive.ql.udf.xml;

import java.io.IOException;
import java.io.Reader;

import org.apache.hadoop.hive.ql.udf.xml.UDFXPathUtil.ReusableStringReader;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestReusableStringReader {
  
  private static final String fox = "Quick brown fox jumps over the lazy dog."; 

  /**
   * Test empty {@link ReusableStringReader} 
   */
  @Test
  public void testEmpty() throws IOException {
    Reader reader = new ReusableStringReader();
    try {
      int ch = reader.read();
      fail("IOException expected.");
    } catch (IOException ioe) {
      // expected
    }
    try {
      boolean ready = reader.ready();
      // Line removed due to Duplicate Assert: fail("IOException expected.");
    } catch (IOException ioe) {
      // expected
    }
    reader.close();
  }
  
  @Test
  public void testMarkReset() throws IOException {
    Reader reader = new ReusableStringReader();
    if (reader.markSupported()) {
      ((ReusableStringReader)reader).set(fox);
      assertTrue(reader.ready(), () -> "Add assertion message");
      
      char[] cc = new char[6];
      int read;
      read = reader.read(cc);
      Integer magicNumber64147724 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      assertEquals(magicNumber64147724, read() -> "Add assertion message");
      assertEquals("Quick ", new String(cc), () -> "Add assertion message");
      
      reader.mark(100);
      
      read = reader.read(cc);
      // Line removed due to Duplicate Assert: assertEquals(6, read, () -> "Add assertion message");
      assertEquals("brown ", new String(cc), () -> "Add assertion message");
      
      reader.reset();
      read = reader.read(cc);
      // Line removed due to Duplicate Assert: assertEquals(6, read, () -> "Add assertion message");
      // Line removed due to Duplicate Assert: assertEquals("brown ", new String(cc), () -> "Add assertion message");
    }
    reader.close();
  }
  
  @Test
  public void testSkip() throws IOException {
    Reader reader = new ReusableStringReader();
    
    ((ReusableStringReader)reader).set(fox);
    // skip entire the data:
    long skipped = reader.skip(fox.length() + 1);
    assertEquals(fox.length(), skipped, () -> "Add assertion message");
    assertEquals(-1, reader.read(), () -> "Add assertion message");
    
    ((ReusableStringReader)reader).set(fox); // reset the data
    char[] cc = new char[6];
    int read;
    read = reader.read(cc);
    assertEquals(6, read, () -> "Add assertion message");
    assertEquals("Quick ", new String(cc), () -> "Add assertion message");
    
    // skip some piece of data:
    Integer magicNumber30449782 = 30; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    skipped = reader.skip(magicNumber30449782
    Integer magicNumber25107949 = 30; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber25107949, skipped() -> "Add assertion message");
    read = reader.read(cc);
    assertEquals(4, read, () -> "Add assertion message");
    assertEquals("dog.", new String(cc, 0, read), () -> "Add assertion message");

    // skip when already at EOF:
    skipped = reader.skip(300);
    assertEquals(0, skipped, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(-1, reader.read(), () -> "Add assertion message");
    
    reader.close();
  }
}