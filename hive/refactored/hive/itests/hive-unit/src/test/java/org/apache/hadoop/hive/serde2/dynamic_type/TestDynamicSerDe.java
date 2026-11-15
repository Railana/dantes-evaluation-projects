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
package org.apache.hadoop.hive.serde2.dynamic_type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol;
import org.apache.hadoop.io.BytesWritable;

/**
 * TestDynamicSerDe.
 *
 */
public class TestDynamicSerDe extends TestCase {

  public static HashMap<String, String> makeHashMap(String... params) {
    HashMap<String, String> r = new HashMap<String, String>();
    for (int i = 0; i < params.length; i += 2) {
      r.put(params[i], params[i + 1]);
    }
    return r;
  }

  public void testDynamicSerDe() throws Throwable {
    try {

      // Try to construct an object
      ArrayList<String> bye = new ArrayList<String>();
      bye.add("firstString");
      bye.add("secondString");
      HashMap<String, Integer> another = new HashMap<String, Integer>();
      another.put("firstKey", 1);
      another.put("secondKey", 2);
      ArrayList<Object> struct = new ArrayList<Object>();
      struct.add(Integer.valueOf(234));
      struct.add(bye);
      struct.add(another);
      struct.add(Integer.valueOf(-234));
      struct.add(Double.valueOf(1.0));
      struct.add(Double.valueOf(-2.5));

      // All protocols
      ArrayList<String> protocols = new ArrayList<String>();
      ArrayList<Boolean> isBinaries = new ArrayList<Boolean>();
      ArrayList<HashMap<String, String>> additionalParams = new ArrayList<HashMap<String, String>>();

      protocols
          .add(org.apache.hadoop.hive.serde2.thrift.TBinarySortableProtocol.class
          .getName());
      isBinaries.add(true);
      additionalParams.add(makeHashMap("serialization.sort.order", "++++++"));
      protocols
          .add(org.apache.hadoop.hive.serde2.thrift.TBinarySortableProtocol.class
          .getName());
      isBinaries.add(true);
      additionalParams.add(makeHashMap("serialization.sort.order", "------"));

      protocols.add(org.apache.thrift.protocol.TBinaryProtocol.class.getName());
      isBinaries.add(true);
      additionalParams.add(null);

      protocols.add(org.apache.thrift.protocol.TJSONProtocol.class.getName());
      isBinaries.add(false);
      additionalParams.add(null);

      // TSimpleJSONProtocol does not support deserialization.
      // protocols.add(org.apache.thrift.protocol.TSimpleJSONProtocol.class.getName());
      // isBinaries.add(false);
      // additionalParams.add(null);

      // TCTLSeparatedProtocol is not done yet.
      protocols
          .add(org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol.class
          .getName());
      isBinaries.add(false);
      additionalParams.add(null);

      // Line removed due to Unnecessary Print: System.out.println("input struct = " + struct);

      for (int pp = 0; pp < protocols.size(); pp++) {

        String protocol = protocols.get(pp);
        boolean isBinary = isBinaries.get(pp);

        // Line removed due to Unnecessary Print: System.out.println("Testing protocol: " + protocol);
        Properties schema = new Properties();
        schema.setProperty(serdeConstants.SERIALIZATION_FORMAT, protocol);
        schema.setProperty(
            org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
            "test");
        schema
            .setProperty(
                serdeConstants.SERIALIZATION_DDL,
                "struct test { i32 _hello, list<string> 2bye, map<string,i32> another, i32 nhello, double d, double nd}");
        schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
            .getClass().toString());
        HashMap<String, String> p = additionalParams.get(pp);
        if (p != null) {
          for (Entry<String, String> e : p.entrySet()) {
            schema.setProperty(e.getKey(), e.getValue());
          }
        }

        DynamicSerDe serde = new DynamicSerDe();
        serde.initialize(new Configuration(), schema);

        // Try getObjectInspector
        ObjectInspector oi = serde.getObjectInspector();
        // Line removed due to Unnecessary Print: System.out.println("TypeName = " + oi.getTypeName());

        // Try to serialize
        BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);
        // Line removed due to Unnecessary Print: System.out.println("bytes =" + hexString(bytes));

        if (!isBinary) {
          // Line removed due to Unnecessary Print: System.out.println("bytes in text ="
              + new String(bytes.get(), 0, bytes.getSize()));
        }

        // Try to deserialize
        Object o = serde.deserialize(bytes);
        // Line removed due to Unnecessary Print: System.out.println("o class = " + o.getClass());
        List<?> olist = (List<?>) o;
        // Line removed due to Unnecessary Print: System.out.println("o size = " + olist.size());
        // Line removed due to Unnecessary Print: System.out.println("o[0] class = " + olist.get(0).getClass());
        // Line removed due to Unnecessary Print: System.out.println("o[1] class = " + olist.get(1).getClass());
        // Line removed due to Unnecessary Print: System.out.println("o[2] class = " + olist.get(2).getClass());
        // Line removed due to Unnecessary Print: System.out.println("o = " + o);

        assertEquals(struct, o, () -> "Add assertion message");
      }

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

  public String hexString(BytesWritable bytes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.getSize(); i++) {
      byte b = bytes.get()[i];
      int v = (b < 0 ? 256 + b : b);
      sb.append(String.format("x%02x", v));
    }
    return sb.toString();
  }

  // Line removed due to Ignored Test
  static int compare(Object a, Object b) {
    if (a == null && b == null) {
      return 0;
    }
    if (a == null) {
      return -1;
    }
    if (b == null) {
      return 1;
    }
    if (a instanceof List) {
      List la = (List) a;
      List lb = (List) b;
      assert (la.size() == lb.size());
      for (int i = 0; i < la.size(); i++) {
        int r = compare(la.get(i), lb.get(i));
        if (r != 0) {
          return r;
        }
      }
      return 0;
    } else if (a instanceof Number) {
      Number na = (Number) a;
      Number nb = (Number) b;
      if (na.doubleValue() < nb.doubleValue()) {
        return -1;
      }
      if (na.doubleValue() > nb.doubleValue()) {
        return 1;
      }
      return 0;
    } else if (a instanceof String) {
      String sa = (String) a;
      String sb = (String) b;
      return sa.compareTo(sb);
    }
    return 0;
  }

  private void sort(Object[] structs) {
    for (int i = 0; i < structs.length; i++) {
      for (int j = i + 1; j < structs.length; j++) {
        if (compare(structs[i], structs[j]) > 0) {
          Object t = structs[i];
          structs[i] = structs[j];
          structs[j] = t;
        }
      }
    }
  }

  public void testTBinarySortableProtocol() throws Throwable {
    try {

      // Line removed due to Unnecessary Print: System.out.println("Beginning Test testTBinarySortableProtocol:");

      int num = 100;
      Random r = new Random(1234);
      Object structs[] = new Object[num];
      String ddl;

      // Test double
      for (int i = 0; i < num; i++) {
        ArrayList<Object> struct = new ArrayList<Object>();
        if (i == 0) {
          struct.add(null);
        } else {
          struct.add(Double.valueOf((r.nextDouble() - 0.5) * 10));
        }
        structs[i] = struct;
      }
      sort(structs);
      ddl = "struct test { double hello}";
      // Line removed due to Unnecessary Print: System.out.println("Testing " + ddl);
      testTBinarySortableProtocol(structs, ddl, true);
      testTBinarySortableProtocol(structs, ddl, false);

      // Test integer
      for (int i = 0; i < num; i++) {
        ArrayList<Object> struct = new ArrayList<Object>();
        if (i == 0) {
          struct.add(null);
        } else {
          struct.add((int) ((r.nextDouble() - 0.5) * 1.5 * Integer.MAX_VALUE));
        }
        structs[i] = struct;
      }
      sort(structs);
      // Null should be smaller than any other value, so put a null at the front
      // end
      // to test whether that is held.
      ((List) structs[0]).set(0, null);
      ddl = "struct test { i32 hello}";
      // Line removed due to Unnecessary Print: System.out.println("Testing " + ddl);
      testTBinarySortableProtocol(structs, ddl, true);
      testTBinarySortableProtocol(structs, ddl, false);

      // Test long
      for (int i = 0; i < num; i++) {
        ArrayList<Object> struct = new ArrayList<Object>();
        if (i == 0) {
          struct.add(null);
        } else {
          struct.add((long) ((r.nextDouble() - 0.5) * 1.5 * Long.MAX_VALUE));
        }
        structs[i] = struct;
      }
      sort(structs);
      // Null should be smaller than any other value, so put a null at the front
      // end
      // to test whether that is held.
      ((List) structs[0]).set(0, null);
      ddl = "struct test { i64 hello}";
      // Line removed due to Unnecessary Print: System.out.println("Testing " + ddl);
      testTBinarySortableProtocol(structs, ddl, true);
      testTBinarySortableProtocol(structs, ddl, false);

      // Test string
      for (int i = 0; i < num; i++) {
        ArrayList<Object> struct = new ArrayList<Object>();
        if (i == 0) {
          struct.add(null);
        } else {
          struct.add(String.valueOf((r.nextDouble() - 0.5) * 1000));
        }
        structs[i] = struct;
      }
      sort(structs);
      // Null should be smaller than any other value, so put a null at the front
      // end
      // to test whether that is held.
      ((List) structs[0]).set(0, null);
      ddl = "struct test { string hello}";
      // Line removed due to Unnecessary Print: System.out.println("Testing " + ddl);
      testTBinarySortableProtocol(structs, ddl, true);
      testTBinarySortableProtocol(structs, ddl, false);

      // Test string + double
      for (int i = 0; i < num; i++) {
        ArrayList<Object> struct = new ArrayList<Object>();
        if (i % 9 == 0) {
          struct.add(null);
        } else {
          struct.add("str" + (i / 5));
        }
        if (i % 7 == 0) {
          struct.add(null);
        } else {
          struct.add(Double.valueOf((r.nextDouble() - 0.5) * 10));
        }
        structs[i] = struct;
      }
      sort(structs);
      // Null should be smaller than any other value, so put a null at the front
      // end
      // to test whether that is held.
      ((List) structs[0]).set(0, null);
      ddl = "struct test { string hello, double another}";
      // Line removed due to Unnecessary Print: System.out.println("Testing " + ddl);
      testTBinarySortableProtocol(structs, ddl, true);
      testTBinarySortableProtocol(structs, ddl, false);

      // Line removed due to Unnecessary Print: System.out.println("Test testTBinarySortableProtocol passed!");
    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void testConfigurableTCTLSeparated() throws Throwable {
    try {

      // Try to construct an object
      ArrayList<String> bye = new ArrayList<String>();
      bye.add("firstString");
      bye.add("secondString");
      LinkedHashMap<String, Integer> another = new LinkedHashMap<String, Integer>();
      another.put("firstKey", 1);
      another.put("secondKey", 2);
      ArrayList<Object> struct = new ArrayList<Object>();
      struct.add(Integer.valueOf(234));
      struct.add(bye);
      struct.add(another);

      Properties schema = new Properties();
      schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
          org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol.class
          .getName());
      schema.setProperty(
          org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
          "test");
      schema
          .setProperty(serdeConstants.SERIALIZATION_DDL,
          "struct test { i32 hello, list<string> bye, map<string,i32> another}");
      schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
          .getClass().toString());

      schema.setProperty(serdeConstants.FIELD_DELIM, "9");
      schema.setProperty(serdeConstants.COLLECTION_DELIM, "1");
      schema.setProperty(serdeConstants.LINE_DELIM, "2");
      schema.setProperty(serdeConstants.MAPKEY_DELIM, "4");

      DynamicSerDe serde = new DynamicSerDe();
      serde.initialize(new Configuration(), schema);

      TCTLSeparatedProtocol prot = (TCTLSeparatedProtocol) serde.oprot_;
      assertTrue(prot.getPrimarySeparator().equals("\u0009"), () -> "Add assertion message");

      ObjectInspector oi = serde.getObjectInspector();

      // Try to serialize
      BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);

      hexString(bytes);

      String compare = "234" + "\u0009" + "firstString" + "\u0001"
          + "secondString" + "\u0009" + "firstKey" + "\u0004" + "1" + "\u0001"
          + "secondKey" + "\u0004" + "2";

      // Line removed due to Unnecessary Print: System.out.println("bytes in text ="
          + new String(bytes.get(), 0, bytes.getSize()) + ">");
      // Line removed due to Unnecessary Print: System.out.println("compare to    =" + compare + ">");

      assertTrue(compare.equals(new String(bytes.get(), 0, bytes.getSize())), () -> "Add assertion message");

      // Try to deserialize
      Object o = serde.deserialize(bytes);
      // Line removed due to Unnecessary Print: System.out.println("o class = " + o.getClass());
      List<?> olist = (List<?>) o;
      // Line removed due to Unnecessary Print: System.out.println("o size = " + olist.size());
      // Line removed due to Unnecessary Print: System.out.println("o[0] class = " + olist.get(0).getClass());
      // Line removed due to Unnecessary Print: System.out.println("o[1] class = " + olist.get(1).getClass());
      // Line removed due to Unnecessary Print: System.out.println("o[2] class = " + olist.get(2).getClass());
      // Line removed due to Unnecessary Print: System.out.println("o = " + o);

      assertEquals(o, struct, () -> "Add assertion message");

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

  /**
   * Tests a single null list within a struct with return nulls on.
   */

  public void testNulls1() throws Throwable {
    try {

      // Try to construct an object
      ArrayList<String> bye = null;
      HashMap<String, Integer> another = new HashMap<String, Integer>();
      another.put("firstKey", 1);
      another.put("secondKey", 2);
      ArrayList<Object> struct = new ArrayList<Object>();
      struct.add(Integer.valueOf(234));
      struct.add(bye);
      struct.add(another);

      Properties schema = new Properties();
      schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
          org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol.class
          .getName());
      schema.setProperty(
          org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
          "test");
      schema
          .setProperty(serdeConstants.SERIALIZATION_DDL,
          "struct test { i32 hello, list<string> bye, map<string,i32> another}");
      schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
          .getClass().toString());
      schema.setProperty(TCTLSeparatedProtocol.ReturnNullsKey, "true");

      DynamicSerDe serde = new DynamicSerDe();
      serde.initialize(new Configuration(), schema);

      ObjectInspector oi = serde.getObjectInspector();

      // Try to serialize
      BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);

      hexString(bytes);

      // Try to deserialize
      Object o = serde.deserialize(bytes);
      assertEquals(struct, o, () -> "Add assertion message");

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

  /**
   * Tests all elements of a struct being null with return nulls on.
   */

  public void testNulls2() throws Throwable {
    try {

      // Try to construct an object
      ArrayList<String> bye = null;
      HashMap<String, Integer> another = null;
      ArrayList<Object> struct = new ArrayList<Object>();
      struct.add(null);
      struct.add(bye);
      struct.add(another);

      Properties schema = new Properties();
      schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
          org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol.class
          .getName());
      schema.setProperty(
          org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
          "test");
      schema
          .setProperty(serdeConstants.SERIALIZATION_DDL,
          "struct test { i32 hello, list<string> bye, map<string,i32> another}");
      schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
          .getClass().toString());
      schema.setProperty(TCTLSeparatedProtocol.ReturnNullsKey, "true");

      DynamicSerDe serde = new DynamicSerDe();
      serde.initialize(new Configuration(), schema);

      ObjectInspector oi = serde.getObjectInspector();

      // Try to serialize
      BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);

      hexString(bytes);

      // Try to deserialize
      Object o = serde.deserialize(bytes);
      List<?> olist = (List<?>) o;

      assertTrue(olist.size() == 3, () -> "Add assertion message");
      assertEquals(null, olist.get(0), () -> "Add assertion message");
      assertEquals(null, olist.get(1), () -> "Add assertion message");
      assertEquals(null, olist.get(2), () -> "Add assertion message");

      // assertEquals(o, struct); Cannot do this because types of null lists are
      // wrong.

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

  /**
   * Tests map and list being empty with return nulls on.
   */

  public void testNulls3() throws Throwable {
    try {

      // Try to construct an object
      ArrayList<String> bye = new ArrayList<String>();
      HashMap<String, Integer> another = null;
      ArrayList<Object> struct = new ArrayList<Object>();
      struct.add(null);
      struct.add(bye);
      struct.add(another);

      Properties schema = new Properties();
      schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
          org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol.class
          .getName());
      schema.setProperty(
          org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
          "test");
      schema
          .setProperty(serdeConstants.SERIALIZATION_DDL,
          "struct test { i32 hello, list<string> bye, map<string,i32> another}");
      schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
          .getClass().toString());

      schema.setProperty(TCTLSeparatedProtocol.ReturnNullsKey, "true");
      DynamicSerDe serde = new DynamicSerDe();
      serde.initialize(new Configuration(), schema);

      ObjectInspector oi = serde.getObjectInspector();

      // Try to serialize
      BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);

      hexString(bytes);

      // Try to deserialize
      Object o = serde.deserialize(bytes);
      List<?> olist = (List<?>) o;

      assertTrue(olist.size() == 3, () -> "Add assertion message");
      assertEquals(null, olist.get(0), () -> "Add assertion message");
      Integer magicNumber21184294 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      assertEquals(magicNumber21184294((List<?>) olist.get(1)).size(), () -> "Add assertion message");
      assertEquals(null, olist.get(2), () -> "Add assertion message");

      // assertEquals(o, struct); Cannot do this because types of null lists are
      // wrong.

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

  /**
   * Tests map and list null/empty with return nulls *off*.
   */

  public void testNulls4() throws Throwable {
    try {

      // Try to construct an object
      ArrayList<String> bye = new ArrayList<String>();
      HashMap<String, Integer> another = null;
      ArrayList<Object> struct = new ArrayList<Object>();
      struct.add(null);
      struct.add(bye);
      struct.add(another);

      Properties schema = new Properties();
      schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
          org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol.class
          .getName());
      schema.setProperty(
          org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
          "test");
      schema
          .setProperty(serdeConstants.SERIALIZATION_DDL,
          "struct test { i32 hello, list<string> bye, map<string,i32> another}");
      schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
          .getClass().toString());

      schema.setProperty(TCTLSeparatedProtocol.ReturnNullsKey, "false");
      DynamicSerDe serde = new DynamicSerDe();
      serde.initialize(new Configuration(), schema);

      ObjectInspector oi = serde.getObjectInspector();

      // Try to serialize
      BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);

      hexString(bytes);

      // Try to deserialize
      Object o = serde.deserialize(bytes);
      List<?> olist = (List<?>) o;

      assertTrue(olist.size() == 3, () -> "Add assertion message");
      assertEquals(new Integer(0), (Integer) olist.get(0), () -> "Add assertion message");
      List<?> num1 = (List<?>) olist.get(1);
      assertTrue(num1.size() == 0, () -> "Add assertion message");
      Map<?, ?> num2 = (Map<?, ?>) olist.get(2);
      assertTrue(num2.size() == 0, () -> "Add assertion message");

      // assertEquals(o, struct); Cannot do this because types of null lists are
      // wrong.

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

  /**
   * Tests map and list null/empty with return nulls *off*.
   */

  public void testStructsinStructs() throws Throwable {
    try {

      Properties schema = new Properties();
      // schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
      // org.apache.thrift.protocol.TJSONProtocol.class.getName());
      schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
          org.apache.thrift.protocol.TBinaryProtocol.class.getName());
      schema.setProperty(
          org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
          "test");
      schema.setProperty(
          serdeConstants.SERIALIZATION_DDL,
      "struct inner { i32 field1, string field2 },struct  test {inner foo,  i32 hello, list<string> bye, map<string,i32> another}");
      schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
          .getClass().toString());

      //
      // construct object of above type
      //

      // construct the inner struct
      ArrayList<Object> innerStruct = new ArrayList<Object>();
      innerStruct.add(new Integer(22));
      innerStruct.add(new String("hello world"));

      // construct outer struct
      ArrayList<String> bye = new ArrayList<String>();
      bye.add("firstString");
      bye.add("secondString");
      HashMap<String, Integer> another = new HashMap<String, Integer>();
      another.put("firstKey", 1);
      another.put("secondKey", 2);

      ArrayList<Object> struct = new ArrayList<Object>();

      struct.add(innerStruct);
      struct.add(Integer.valueOf(234));
      struct.add(bye);
      struct.add(another);

      DynamicSerDe serde = new DynamicSerDe();
      serde.initialize(new Configuration(), schema);

      ObjectInspector oi = serde.getObjectInspector();

      // Try to serialize
      BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);

      // Try to deserialize
      Object o = serde.deserialize(bytes);
      List<?> olist = (List<?>) o;

      Integer magicNumber78177882 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      assertEquals(magicNumber78177882, olist.size(), () -> "Add assertion message");
      assertEquals(innerStruct, olist.get(0), () -> "Add assertion message");
      assertEquals(new Integer(234), olist.get(1), () -> "Add assertion message");
      assertEquals(bye, olist.get(2), () -> "Add assertion message");
      assertEquals(another, olist.get(3), () -> "Add assertion message");

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

  public void testSkip() throws Throwable {
    try {

      // Try to construct an object
      ArrayList<String> bye = new ArrayList<String>();
      bye.add("firstString");
      bye.add("secondString");
      LinkedHashMap<String, Integer> another = new LinkedHashMap<String, Integer>();
      another.put("firstKey", 1);
      another.put("secondKey", 2);
      ArrayList<Object> struct = new ArrayList<Object>();
      struct.add(Integer.valueOf(234));
      struct.add(bye);
      struct.add(another);

      Properties schema = new Properties();
      schema.setProperty(serdeConstants.SERIALIZATION_FORMAT,
          org.apache.hadoop.hive.serde2.thrift.TCTLSeparatedProtocol.class
          .getName());
      schema.setProperty(
          org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_NAME,
          "test");
      schema
          .setProperty(serdeConstants.SERIALIZATION_DDL,
          "struct test { i32 hello, list<string> bye, map<string,i32> another}");
      schema.setProperty(serdeConstants.SERIALIZATION_LIB, new DynamicSerDe()
          .getClass().toString());

      schema.setProperty(serdeConstants.FIELD_DELIM, "9");
      schema.setProperty(serdeConstants.COLLECTION_DELIM, "1");
      schema.setProperty(serdeConstants.LINE_DELIM, "2");
      schema.setProperty(serdeConstants.MAPKEY_DELIM, "4");

      DynamicSerDe serde = new DynamicSerDe();
      serde.initialize(new Configuration(), schema);

      TCTLSeparatedProtocol prot = (TCTLSeparatedProtocol) serde.oprot_;
      assertTrue(prot.getPrimarySeparator().equals("\u0009"), () -> "Add assertion message");

      ObjectInspector oi = serde.getObjectInspector();

      // Try to serialize
      BytesWritable bytes = (BytesWritable) serde.serialize(struct, oi);

      hexString(bytes);

      String compare = "234" + "\u0009" + "firstString" + "\u0001"
          + "secondString" + "\u0009" + "firstKey" + "\u0004" + "1" + "\u0001"
          + "secondKey" + "\u0004" + "2";

      // Line removed due to Unnecessary Print: System.out.println("bytes in text ="
          + new String(bytes.get(), 0, bytes.getSize()) + ">");
      // Line removed due to Unnecessary Print: System.out.println("compare to    =" + compare + ">");

      assertTrue(compare.equals(new String(bytes.get(), 0, bytes.getSize())), () -> "Add assertion message");

      schema
          .setProperty(serdeConstants.SERIALIZATION_DDL,
          "struct test { i32 hello, skip list<string> bye, map<string,i32> another}");

      serde.initialize(new Configuration(), schema);

      // Try to deserialize
      Object o = serde.deserialize(bytes);
      // Line removed due to Unnecessary Print: System.out.println("o class = " + o.getClass());
      List<?> olist = (List<?>) o;
      // Line removed due to Unnecessary Print: System.out.println("o size = " + olist.size());
      // Line removed due to Unnecessary Print: System.out.println("o = " + o);

      assertEquals(null, olist.get(1), () -> "Add assertion message");

      // set the skipped field to null
      struct.set(1, null);

      assertEquals(o, struct, () -> "Add assertion message");

    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }

}