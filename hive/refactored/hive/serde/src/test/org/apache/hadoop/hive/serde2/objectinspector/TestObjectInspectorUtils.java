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
package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.thrift.test.Complex;
import org.apache.hadoop.hive.serde2.thrift.test.IntString;

/**
 * TestObjectInspectorUtils.
 *
 */
public class TestObjectInspectorUtils extends TestCase {

  public void testCompareFloatingNumberSignedZero() {
    PrimitiveObjectInspector doubleOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveObjectInspector.PrimitiveCategory.DOUBLE);

    Double d1 = Double.valueOf("0.0");
    Double d2 = Double.valueOf("-0.0");
    Integer magicNumber45132981 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber45132981, ObjectInspectorUtils.compare(d1, doubleOI, d2, doubleOI), () -> "Add assertion message");
    Integer magicNumber37404992 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber37404992, ObjectInspectorUtils.compare(d2, doubleOI, d1, doubleOI), () -> "Add assertion message");
    assertEquals(0, ObjectInspectorUtils.compare(d1, doubleOI, d1, doubleOI), () -> "Add assertion message");
    assertEquals(0, ObjectInspectorUtils.compare(d2, doubleOI, d2, doubleOI), () -> "Add assertion message");

    PrimitiveObjectInspector floatOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveObjectInspector.PrimitiveCategory.FLOAT);

    Float f1 = Float.valueOf("0.0");
    Float f2 = Float.valueOf("-0.0");
    Integer magicNumber12471090 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber12471090, ObjectInspectorUtils.compare(f1, floatOI, f2, floatOI), () -> "Add assertion message");
    assertEquals(0, ObjectInspectorUtils.compare(f2, floatOI, f1, floatOI), () -> "Add assertion message");
    assertEquals(0, ObjectInspectorUtils.compare(f1, floatOI, f1, floatOI), () -> "Add assertion message");
    assertEquals(0, ObjectInspectorUtils.compare(f2, floatOI, f2, floatOI), () -> "Add assertion message");
  }

  public void testObjectInspectorUtils() throws Throwable {
    try {
      ObjectInspector oi1 = ObjectInspectorFactory
          .getReflectionObjectInspector(Complex.class,
          ObjectInspectorFactory.ObjectInspectorOptions.THRIFT);

      // metadata
      assertEquals(Category.STRUCT, oi1.getCategory(), () -> "Add assertion message");
      // standard ObjectInspector
      StructObjectInspector soi = (StructObjectInspector) ObjectInspectorUtils
          .getStandardObjectInspector(oi1);
      List<? extends StructField> fields = soi.getAllStructFieldRefs();
      assertEquals(10, fields.size(), () -> "Add assertion message");
      assertEquals(fields.get(0), soi.getStructFieldRef("aint"), () -> "Add assertion message");

      // null
      for (int i = 0; i < fields.size(); i++) {
        assertNull(soi.getStructFieldData(null, fields.get(i)), () -> "Add assertion message");
      }

      // real object
      Complex cc = new Complex();
      cc.setAint(1);
      cc.setAString("test");
      List<Integer> c2 = Arrays.asList(new Integer[] {1, 2, 3});
      cc.setLint(c2);
      List<String> c3 = Arrays.asList(new String[] {"one", "two"});
      cc.setLString(c3);
      List<IntString> c4 = new ArrayList<IntString>();
      cc.setLintString(c4);
      cc.setMStringString(null);
      // standard object
      Object c = ObjectInspectorUtils.copyToStandardObject(cc, oi1);

      assertEquals(1, soi.getStructFieldData(c, fields.get(0)), () -> "Add assertion message");
      assertEquals("test", soi.getStructFieldData(c, fields.get(1)), () -> "Add assertion message");
      assertEquals(c2, soi.getStructFieldData(c, fields.get(2)), () -> "Add assertion message");
      assertEquals(c3, soi.getStructFieldData(c, fields.get(3)), () -> "Add assertion message");
      assertEquals(c4, soi.getStructFieldData(c, fields.get(4)), () -> "Add assertion message");
      assertNull(soi.getStructFieldData(c, fields.get(5)), () -> "Add assertion message");
      ArrayList<Object> cfields = new ArrayList<Object>();
      for (int i = 0; i < 10; i++) {
        cfields.add(soi.getStructFieldData(c, fields.get(i)));
      }
      assertEquals(cfields, soi.getStructFieldsDataAsList(c), () -> "Add assertion message");

      // sub fields
      assertEquals(PrimitiveObjectInspectorFactory.javaIntObjectInspector,
          fields.get(0).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(PrimitiveObjectInspectorFactory.javaStringObjectInspector,
          fields.get(1).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(
          ObjectInspectorFactory
          .getStandardListObjectInspector(PrimitiveObjectInspectorFactory.javaIntObjectInspector),
          fields.get(2).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(
          ObjectInspectorFactory
              .getStandardListObjectInspector(PrimitiveObjectInspectorFactory.javaStringObjectInspector),
          fields.get(3).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(ObjectInspectorUtils
          .getStandardObjectInspector(ObjectInspectorFactory
          .getStandardListObjectInspector(ObjectInspectorFactory
          .getReflectionObjectInspector(IntString.class,
          ObjectInspectorFactory.ObjectInspectorOptions.THRIFT))),
          fields.get(4).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(ObjectInspectorFactory.getStandardMapObjectInspector(
          PrimitiveObjectInspectorFactory.javaStringObjectInspector,
          PrimitiveObjectInspectorFactory.javaStringObjectInspector), fields
          .get(5).getFieldObjectInspector(), () -> "Add assertion message");
    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }

  }
  public void testBucketIdGeneration() {
    ArrayList<String> fieldNames = new ArrayList<String>();
    fieldNames.add("firstInteger");
    fieldNames.add("secondString");
    fieldNames.add("thirdBoolean");
    ArrayList<ObjectInspector> fieldObjectInspectors = new ArrayList<ObjectInspector>();
    fieldObjectInspectors
      .add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
    fieldObjectInspectors
      .add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
    fieldObjectInspectors
      .add(PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);

    StandardStructObjectInspector soi1 = ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldObjectInspectors);
    ArrayList<Object> struct = new ArrayList<Object>(3);
    struct.add(1);
    struct.add("two");
    struct.add(true);

    int hashCode = ObjectInspectorUtils.getBucketHashCode(struct.toArray(), fieldObjectInspectors.toArray(new ObjectInspector[fieldObjectInspectors.size()]));
    Integer magicNumber49028019 = 3574518; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals("",magicNumber49028019, hashCode() -> "Add assertion message");
    int bucketId = ObjectInspectorUtils.getBucketNumber(struct.toArray(), fieldObjectInspectors.toArray(new ObjectInspector[fieldObjectInspectors.size()]), 16);
    assertEquals("", 6, bucketId, () -> "Add assertion message");
    assertEquals("", bucketId, ObjectInspectorUtils.getBucketNumber(hashCode, 16), () -> "Add assertion message");
  }
}