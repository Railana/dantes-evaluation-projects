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

import javax.xml.xpath.XPathConstants;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.*;

import java.io.File;

public class TestUDFXPathUtil {

  @Test
  public void testEvalIllegalArgs() {
    UDFXPathUtil util = new UDFXPathUtil();
    
    // null args:
    assertNull(util.eval(null, "a/text()", XPathConstants.STRING), () -> "Add assertion message");
    assertNull(util.eval("<a><b>b1</b><b>b2</b><b>b3</b><c>c1</c><c>c2</c></a>", null, XPathConstants.STRING), () -> "Add assertion message");
    assertNull(util.eval("<a><b>b1</b><b>b2</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/text()", null), () -> "Add assertion message");
    
    // empty String args: 
    assertNull(util.eval("", "a/text()", XPathConstants.STRING), () -> "Add assertion message");
    assertNull(util.eval("<a><b>b1</b><b>b2</b><b>b3</b><c>c1</c><c>c2</c></a>", "", XPathConstants.STRING), () -> "Add assertion message");
    
    // wrong expression:
    assertNull(util.eval("<a><b>b1</b><b>b2</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/text(", XPathConstants.STRING), () -> "Add assertion message");
  }

  @Test
  public void testEvalPositive() {
    UDFXPathUtil util = new UDFXPathUtil();
    
    Object result = util.eval("<a><b>b1</b><b>b2</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/c[2]/text()", XPathConstants.STRING);
    assertEquals("c2", result, () -> "Add assertion message");
    
    result = util.evalBoolean("<a><b>true</b><b>false</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/b[1]/text()");
    assertEquals(Boolean.TRUE, result, () -> "Add assertion message");
    result = util.evalBoolean("<a><b>true</b><b>false</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/b[4]");
    assertEquals(Boolean.FALSE, result, () -> "Add assertion message");
    
    result = util.evalString("<a><b>true</b><b>false</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/b[3]/text()");
    assertEquals("b3", result, () -> "Add assertion message");
    result = util.evalString("<a><b>true</b><b>false</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/b[4]/text()");
    assertEquals("", result, () -> "Add assertion message");
    result = util.evalString("<a><b>true</b><b k=\"foo\">FALSE</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/b[2]/@k");
    assertEquals("foo", result, () -> "Add assertion message");
    
    result = util.evalNumber("<a><b>true</b><b>false</b><b>b3</b><c>c1</c><c>-77</c></a>", "a/c[2]");
    Double magicNumber35700073 = -77.0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber35700073, result() -> "Add assertion message");
    result = util.evalNumber("<a><b>true</b><b k=\"foo\">FALSE</b><b>b3</b><c>c1</c><c>c2</c></a>", "a/b[2]/@k");
    assertEquals(Double.NaN, result, () -> "Add assertion message");
    
    result = util.evalNode("<a><b>true</b><b>false</b><b>b3</b><c>c1</c><c>-77</c></a>", "a/c[2]");
    assertNotNull(result, () -> "Add assertion message");
    assertTrue(result instanceof Node, () -> "Add assertion message");
    
    result = util.evalNodeList("<a><b>true</b><b>false</b><b>b3</b><c>c1</c><c>-77</c></a>", "a/*");
    // Line removed due to Duplicate Assert: assertNotNull(result, () -> "Add assertion message");
    assertTrue(result instanceof NodeList, () -> "Add assertion message");
    assertEquals(5, ((NodeList)result).getLength(), () -> "Add assertion message");
  }

  @Test
    public void testEmbedFailure(@TempDir File tempDir) throws Exception {

    String secretValue = String.valueOf(Math.random());
        File tempFile = tempDir.createTempFile("verifyembed", ".tmp");
    tempFile.deleteOnExit();
    String fname = tempFile.getAbsolutePath();

    FileUtils.writeStringToFile(tempFile, secretValue);

    String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<!DOCTYPE test [ \n" +
        "    <!ENTITY embed SYSTEM \"" + fname + "\"> \n" +
        "]>\n" +
        "<foo>&embed;</foo>";
    String evaled = new UDFXPathUtil().evalString(xml, "/foo");
    assertTrue(evaled.isEmpty(), () -> "Add assertion message");
  }
}