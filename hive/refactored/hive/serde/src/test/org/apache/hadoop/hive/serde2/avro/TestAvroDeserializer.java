/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.serde2.avro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.VoidObjectInspector;
import org.junit.Test;

public class TestAvroDeserializer {
  private final GenericData GENERIC_DATA = GenericData.get();

  @Test
  public void canDeserializeVoidType() throws IOException, SerDeException {
    String schemaString = "{\n" +
        "  \"type\": \"record\", \n" +
        "  \"name\": \"nullTest\",\n" +
        "  \"fields\" : [\n" +
        "    {\"name\": \"isANull\", \"type\": \"null\"}\n" +
        "  ]\n" +
        "}";
    Schema s = AvroSerdeUtils.getSchemaFor(schemaString);
    GenericData.Record record = new GenericData.Record(s);

    record.put("isANull", null);
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);

    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();

    ArrayList<Object> row = (ArrayList<Object>)de.deserialize(aoig.getColumnNames(),
            aoig.getColumnTypes(), garw, s);
    Integer magicNumber29348077 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber29348077, row.size(), () -> "Add assertion message");
    Object theVoidObject = row.get(0);
    assertNull(theVoidObject, () -> "Add assertion message");

    StandardStructObjectInspector oi = (StandardStructObjectInspector)aoig.getObjectInspector();
    StructField fieldRef = oi.getStructFieldRef("isANull");

    Object shouldBeNull = oi.getStructFieldData(row, fieldRef);
    assertNull(shouldBeNull, () -> "Add assertion message");
    assertTrue(fieldRef.getFieldObjectInspector() instanceof VoidObjectInspector, () -> "Add assertion message");
  }

  @Test
  public void canDeserializeMapsWithPrimitiveKeys() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.MAP_WITH_PRIMITIVE_VALUE_TYPE);
    GenericData.Record record = new GenericData.Record(s);

    Map<String, Long> m = new Hashtable<String, Long>();
    m.put("one", 1l);
    m.put("two", 2l);
    m.put("three", 3l);

    record.put("aMap", m);
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");
    // Line removed due to Unnecessary Print: System.out.println("record = " + record);

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);

    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();

    ArrayList<Object> row = (ArrayList<Object>)de.deserialize(aoig.getColumnNames(),
            aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size(), () -> "Add assertion message");
    Object theMapObject = row.get(0);
    assertTrue(theMapObject instanceof Map, () -> "Add assertion message");
    Map theMap = (Map)theMapObject;

    // Verify the raw object that's been created
    assertEquals(1l, theMap.get("one"), () -> "Add assertion message");
    assertEquals(2l, theMap.get("two"), () -> "Add assertion message");
    assertEquals(3l, theMap.get("three"), () -> "Add assertion message");

    // Verify that the provided object inspector can pull out these same values
    StandardStructObjectInspector oi =
            (StandardStructObjectInspector)aoig.getObjectInspector();

    List<Object> z = oi.getStructFieldsDataAsList(row);
    assertEquals(1, z.size(), () -> "Add assertion message");
    StructField fieldRef = oi.getStructFieldRef("amap");

    Map theMap2 = (Map)oi.getStructFieldData(row, fieldRef);
    assertEquals(1l, theMap2.get("one"), () -> "Add assertion message");
    assertEquals(2l, theMap2.get("two"), () -> "Add assertion message");
    assertEquals(3l, theMap2.get("three"), () -> "Add assertion message");
  }

  @Test
  public void canDeserializeArrays() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.ARRAY_WITH_PRIMITIVE_ELEMENT_TYPE);
    GenericData.Record record = new GenericData.Record(s);

    List<String> list = new ArrayList<String>();
    list.add("Eccleston");
    list.add("Tennant");
    list.add("Smith");

    record.put("anArray", list);
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");
    // Line removed due to Unnecessary Print: System.out.println("Array-backed record = " + record);

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);

    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row = (ArrayList<Object>)de.deserialize(aoig.getColumnNames(),
            aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size(), () -> "Add assertion message");
    Object theArrayObject = row.get(0);
    assertTrue(theArrayObject instanceof List, () -> "Add assertion message");
    List theList = (List)theArrayObject;

    // Verify the raw object that's been created
    assertEquals("Eccleston", theList.get(0), () -> "Add assertion message");
    assertEquals("Tennant", theList.get(1), () -> "Add assertion message");
    assertEquals("Smith", theList.get(2), () -> "Add assertion message");

    // Now go the correct way, through objectinspectors
    StandardStructObjectInspector oi =
            (StandardStructObjectInspector)aoig.getObjectInspector();
    StructField fieldRefToArray = oi.getStructFieldRef("anArray");

    Object anArrayData = oi.getStructFieldData(row, fieldRefToArray);
    StandardListObjectInspector anArrayOI =
            (StandardListObjectInspector)fieldRefToArray.getFieldObjectInspector();
    assertEquals(3, anArrayOI.getListLength(anArrayData), () -> "Add assertion message");

    JavaStringObjectInspector elementOI =
            (JavaStringObjectInspector)anArrayOI.getListElementObjectInspector();

    Object firstElement = anArrayOI.getListElement(anArrayData, 0);
    assertEquals("Eccleston", elementOI.getPrimitiveJavaObject(firstElement), () -> "Add assertion message");
    assertTrue(firstElement instanceof String, () -> "Add assertion message");

    Object secondElement = anArrayOI.getListElement(anArrayData, 1);
    assertEquals("Tennant", elementOI.getPrimitiveJavaObject(secondElement), () -> "Add assertion message");
    assertTrue(secondElement instanceof String, () -> "Add assertion message");

    Object thirdElement = anArrayOI.getListElement(anArrayData, 2);
    assertEquals("Smith", elementOI.getPrimitiveJavaObject(thirdElement), () -> "Add assertion message");
    assertTrue(thirdElement instanceof String, () -> "Add assertion message");

  }

  public void canDeserializeRecordsInternal(Schema s, Schema fileSchema) throws SerDeException, IOException {
    GenericData.Record record = new GenericData.Record(s);
    GenericData.Record innerRecord = new GenericData.Record(s.getField("aRecord").schema());
    innerRecord.put("int1", 42);
    innerRecord.put("boolean1", true);
    innerRecord.put("long1", 42432234234l);
    record.put("aRecord", innerRecord);
    assertTrue(GENERIC_DATA.validate(s, record));

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record, fileSchema);

    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size());
    Object theRecordObject = row.get(0);
    System.out.println("theRecordObject = " + theRecordObject.getClass().getCanonicalName());

    // The original record was lost in the deserialization, so just go the
    // correct way, through objectinspectors
    StandardStructObjectInspector oi = (StandardStructObjectInspector)aoig.getObjectInspector();
    List<? extends StructField> allStructFieldRefs = oi.getAllStructFieldRefs();
    assertEquals(1, allStructFieldRefs.size());
    StructField fieldRefForaRecord = allStructFieldRefs.get(0);
    assertEquals("arecord", fieldRefForaRecord.getFieldName());
    Object innerRecord2 = oi.getStructFieldData(row, fieldRefForaRecord);

    // Extract innerRecord field refs
    StandardStructObjectInspector innerRecord2OI =
            (StandardStructObjectInspector) fieldRefForaRecord.getFieldObjectInspector();

    List<? extends StructField> allStructFieldRefs1 = innerRecord2OI.getAllStructFieldRefs();
    assertEquals(3, allStructFieldRefs1.size());
    assertEquals("int1", allStructFieldRefs1.get(0).getFieldName());
    assertEquals("boolean1", allStructFieldRefs1.get(1).getFieldName());
    assertEquals("long1", allStructFieldRefs1.get(2).getFieldName());

    innerRecord2OI.getStructFieldsDataAsList(innerRecord2);
    assertEquals(42, innerRecord2OI.getStructFieldData(innerRecord2, allStructFieldRefs1.get(0)));
    assertEquals(true, innerRecord2OI.getStructFieldData(innerRecord2, allStructFieldRefs1.get(1)));
    assertEquals(42432234234l, innerRecord2OI.getStructFieldData(innerRecord2, allStructFieldRefs1.get(2)));
  }

  @Test
  public void canDeserializeRecords() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.RECORD_SCHEMA);
    canDeserializeRecordsInternal(s, s);
  }

  @Test
  public void canDeserializeNullableRecords() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.RECORD_SCHEMA);
    Schema fileSchema = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.NULLABLE_RECORD_SCHEMA);
    canDeserializeRecordsInternal(s, fileSchema);
  }

  private class ResultPair { // Because Pairs give Java the vapors.
    public final ObjectInspector oi;
    public final Object value;
    public final Object unionObject;

    private ResultPair(ObjectInspector oi, Object value, Object unionObject) {
      this.oi = oi;
      this.value = value;
      this.unionObject = unionObject;
    }
  }

  @Test
  public void canDeserializeUnions() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.UNION_SCHEMA);
    GenericData.Record record = new GenericData.Record(s);

    record.put("aUnion", "this is a string");

    ResultPair result = unionTester(s, record);
    assertTrue(result.value instanceof String, () -> "Add assertion message");
    assertEquals("this is a string", result.value, () -> "Add assertion message");
    UnionObjectInspector uoi = (UnionObjectInspector)result.oi;
    assertEquals(1, uoi.getTag(result.unionObject), () -> "Add assertion message");

    // Now the other enum possibility
    record = new GenericData.Record(s);
    record.put("aUnion", 99);
    result = unionTester(s, record);
    assertTrue(result.value instanceof Integer, () -> "Add assertion message");
    assertEquals(99, result.value, () -> "Add assertion message");
    uoi = (UnionObjectInspector)result.oi;
    assertEquals(0, uoi.getTag(result.unionObject), () -> "Add assertion message");
  }
  
  @Test
  public void canDeserializeEvolvedUnions1() throws SerDeException, IOException {
    Schema ws = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.UNION_SCHEMA);
    Schema rs = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.UNION_SCHEMA_2);

    GenericData.Record record = new GenericData.Record(ws);

    record.put("aUnion", "this is a string");

    ResultPair result = unionTester(ws, rs, record);
    assertTrue(result.value instanceof String, () -> "Add assertion message");
    assertEquals("this is a string", result.value, () -> "Add assertion message");
    UnionObjectInspector uoi = (UnionObjectInspector)result.oi;
    // The null in union type should be removed
    assertEquals(1, uoi.getTag(result.unionObject), () -> "Add assertion message");

    // Now the other enum possibility
    record = new GenericData.Record(ws);
    record.put("aUnion", 99);
    result = unionTester(ws, rs, record);
    assertTrue(result.value instanceof Integer, () -> "Add assertion message");
    assertEquals(99, result.value, () -> "Add assertion message");
    uoi = (UnionObjectInspector)result.oi;
    // The null in union type should be removed
    assertEquals(0, uoi.getTag(result.unionObject), () -> "Add assertion message");
  }

  @Test
  public void canDeserializeEvolvedUnions2() throws SerDeException, IOException {
    Schema ws = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.UNION_SCHEMA_3);
    Schema rs = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.UNION_SCHEMA_4);
    
    GenericData.Record record = new GenericData.Record(ws);

    record.put("aUnion", 90);

    ResultPair result = unionTester(ws, rs, record);
    assertTrue(result.value instanceof Integer, () -> "Add assertion message");
    assertEquals(90, result.value, () -> "Add assertion message");
    UnionObjectInspector uoi = (UnionObjectInspector)result.oi;
    assertEquals(0, uoi.getTag(result.unionObject), () -> "Add assertion message");

    // Now the other enum possibility
    record = new GenericData.Record(ws);
    record.put("aUnion", 99.9f);
    result = unionTester(ws, rs, record);
    assertTrue(result.value instanceof Float, () -> "Add assertion message");
    assertEquals(99.9f, result.value, () -> "Add assertion message");
    uoi = (UnionObjectInspector)result.oi;
    assertEquals(1, uoi.getTag(result.unionObject), () -> "Add assertion message");
  }

  private ResultPair unionTester(Schema ws, GenericData.Record record)
          throws SerDeException, IOException {
      return unionTester(ws, ws, record);
  }

  private ResultPair unionTester(Schema ws, Schema rs, GenericData.Record record)
          throws SerDeException, IOException {
    assertTrue(GENERIC_DATA.validate(ws, record));
    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);

    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(rs);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, rs);
    assertEquals(1, row.size());
    StandardStructObjectInspector oi = (StandardStructObjectInspector)aoig.getObjectInspector();
    List<? extends StructField> fieldRefs = oi.getAllStructFieldRefs();
    assertEquals(1, fieldRefs.size());
    StructField fieldRef = fieldRefs.get(0);
    assertEquals("aunion", fieldRef.getFieldName());
    Object theUnion = oi.getStructFieldData(row, fieldRef);

    assertTrue(fieldRef.getFieldObjectInspector() instanceof UnionObjectInspector);
    UnionObjectInspector fieldObjectInspector =
            (UnionObjectInspector)fieldRef.getFieldObjectInspector();
    Object value = fieldObjectInspector.getField(theUnion);

    return new ResultPair(fieldObjectInspector, value, theUnion);
  }

  @Test // Enums are one of two types we fudge for Hive. Enums go in, Strings come out.
  public void canDeserializeEnums() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.ENUM_SCHEMA);
    GenericData.Record record = new GenericData.Record(s);

    record.put("baddies", new GenericData.EnumSymbol(s.getField("baddies").schema(),"DALEKS"));
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);
    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size(), () -> "Add assertion message");
    StandardStructObjectInspector oi = (StandardStructObjectInspector)aoig.getObjectInspector();
    List<? extends StructField> fieldRefs = oi.getAllStructFieldRefs();
    assertEquals(1, fieldRefs.size(), () -> "Add assertion message");
    StructField fieldRef = fieldRefs.get(0);

    assertEquals("baddies", fieldRef.getFieldName(), () -> "Add assertion message");

    Object theStringObject = oi.getStructFieldData(row, fieldRef);
    assertTrue(fieldRef.getFieldObjectInspector() instanceof StringObjectInspector, () -> "Add assertion message");
    StringObjectInspector soi = (StringObjectInspector)fieldRef.getFieldObjectInspector();

    String finalValue = soi.getPrimitiveJavaObject(theStringObject);
    assertEquals("DALEKS", finalValue, () -> "Add assertion message");
  }

  @Test // Fixed doesn't exist in Hive. Fixeds go in, lists of bytes go out.
  public void canDeserializeFixed() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.FIXED_SCHEMA);
    GenericData.Record record = new GenericData.Record(s);

    byte [] bytes = "ANANCIENTBLUEBOX".getBytes();
    record.put("hash", new GenericData.Fixed(s, bytes));
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);
    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size(), () -> "Add assertion message");
    Object byteObject = row.get(0);
    assertTrue(byteObject instanceof byte[], () -> "Add assertion message");
    byte[] outBytes = (byte[]) byteObject;
    // Verify the raw object that's been created
    for(int i = 0; i < bytes.length; i++) {
      assertEquals(bytes[i], outBytes[i], () -> "Add assertion message");
    }

    // Now go the correct way, through objectinspectors
    StandardStructObjectInspector oi = (StandardStructObjectInspector)aoig.getObjectInspector();
    List<Object> fieldsDataAsList = oi.getStructFieldsDataAsList(row);
    assertEquals(1, fieldsDataAsList.size(), () -> "Add assertion message");
    StructField fieldRef = oi.getStructFieldRef("hash");

    outBytes = (byte[]) oi.getStructFieldData(row, fieldRef);
    for(int i = 0; i < outBytes.length; i++) {
      // Line removed due to Duplicate Assert: assertEquals(bytes[i], outBytes[i], () -> "Add assertion message");
    }
  }

  @Test
  public void canDeserializeBytes() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.BYTES_SCHEMA);
    GenericData.Record record = new GenericData.Record(s);

    byte [] bytes = "ANANCIENTBLUEBOX".getBytes();

    ByteBuffer bb = ByteBuffer.wrap(bytes);
    bb.rewind();
    record.put("bytesField", bb);
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);
    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size(), () -> "Add assertion message");
    Object byteObject = row.get(0);
    assertTrue(byteObject instanceof byte[], () -> "Add assertion message");
    byte[] outBytes = (byte[]) byteObject;
    // Verify the raw object that's been created
    for(int i = 0; i < bytes.length; i++) {
      assertEquals(bytes[i], outBytes[i], () -> "Add assertion message");
    }

    // Now go the correct way, through objectinspectors
    StandardStructObjectInspector oi = (StandardStructObjectInspector)aoig.getObjectInspector();
    List<Object> fieldsDataAsList = oi.getStructFieldsDataAsList(row);
    assertEquals(1, fieldsDataAsList.size(), () -> "Add assertion message");
    StructField fieldRef = oi.getStructFieldRef("bytesField");

    outBytes = (byte[]) oi.getStructFieldData(row, fieldRef);
    for(int i = 0; i < outBytes.length; i++) {
      // Line removed due to Duplicate Assert: assertEquals(bytes[i], outBytes[i], () -> "Add assertion message");
    }
  }

  @Test
  public void canDeserializeNullableTypes() throws IOException, SerDeException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.NULLABLE_STRING_SCHEMA);
    GenericData.Record record = new GenericData.Record(s);
    record.put("nullableString", "this is a string");

    verifyNullableType(record, s, "nullableString", "this is a string");

    record = new GenericData.Record(s);
    record.put("nullableString", null);
    verifyNullableType(record, s, "nullableString", null);
  }

   @Test
   public void canDeserializeNullableEnums() throws IOException, SerDeException {
     Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.NULLABLE_ENUM_SCHEMA);
     GenericData.Record record = new GenericData.Record(s);
     record.put("nullableEnum", new GenericData.EnumSymbol(AvroSerdeUtils.getOtherTypeFromNullableType(s.getField("nullableEnum").schema()), "CYBERMEN"));

     verifyNullableType(record, s, "nullableEnum", "CYBERMEN");

     record = new GenericData.Record(s);
     record.put("nullableEnum", null);
     verifyNullableType(record, s, "nullableEnum", null);
   }

  @Test
  public void canDeserializeMapWithNullablePrimitiveValues() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator
        .MAP_WITH_NULLABLE_PRIMITIVE_VALUE_TYPE_SCHEMA);
    GenericData.Record record = new GenericData.Record(s);

    Map<String, Long> m = new HashMap<String, Long>();
    m.put("one", 1l);
    m.put("two", 2l);
    m.put("three", 3l);
    m.put("mu", null);

    record.put("aMap", m);
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");
    // Line removed due to Unnecessary Print: System.out.println("record = " + record);

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);

    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();

    ArrayList<Object> row = (ArrayList<Object>)de.deserialize(aoig.getColumnNames(),
            aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size(), () -> "Add assertion message");
    Object theMapObject = row.get(0);
    assertTrue(theMapObject instanceof Map, () -> "Add assertion message");
    Map theMap = (Map)theMapObject;

    // Verify the raw object that's been created
    assertEquals(1l, theMap.get("one"), () -> "Add assertion message");
    assertEquals(2l, theMap.get("two"), () -> "Add assertion message");
    assertEquals(3l, theMap.get("three"), () -> "Add assertion message");
    assertTrue(theMap.containsKey("mu"), () -> "Add assertion message");
    assertEquals(null, theMap.get("mu"), () -> "Add assertion message");

    // Verify that the provided object inspector can pull out these same values
    StandardStructObjectInspector oi =
            (StandardStructObjectInspector)aoig.getObjectInspector();

    List<Object> z = oi.getStructFieldsDataAsList(row);
    assertEquals(1, z.size(), () -> "Add assertion message");
    StructField fieldRef = oi.getStructFieldRef("amap");

    Map theMap2 = (Map)oi.getStructFieldData(row, fieldRef);
    assertEquals(1l, theMap2.get("one"), () -> "Add assertion message");
    assertEquals(2l, theMap2.get("two"), () -> "Add assertion message");
    assertEquals(3l, theMap2.get("three"), () -> "Add assertion message");
    assertTrue(theMap2.containsKey("mu"), () -> "Add assertion message");
    assertEquals(null, theMap2.get("mu"), () -> "Add assertion message");
  }
  
  @Test
  public void canDeserializeMapsWithJavaLangStringKeys() throws IOException, SerDeException {
    // Ensures maps can be deserialized when avro.java.string=String.
    // See http://stackoverflow.com/a/19868919/312944 for why that might be used.
    String schemaString = "{\n" +
        "  \"namespace\": \"testing\",\n" +
        "  \"name\": \"oneMap\",\n" +
        "  \"type\": \"record\",\n" +
        "  \"fields\": [\n" +
        "    {\n" +
        "      \"name\":\"aMap\",\n" +
        "      \"type\":{\"type\":\"map\",\n" +
        "      \"avro.java.string\":\"String\",\n" +
        "      \"values\":\"long\"}\n" +
        "\t}\n" +
        "  ]\n" +
        "}";
    Schema s = AvroSerdeUtils.getSchemaFor(schemaString);
    GenericData.Record record = new GenericData.Record(s);

    Map<String, Long> m = new Hashtable<String, Long>();
    m.put("one", 1l);
    m.put("two", 2l);
    m.put("three", 3l);

    record.put("aMap", m);
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");
    // Line removed due to Unnecessary Print: System.out.println("record = " + record);

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);

    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();

    ArrayList<Object> row = (ArrayList<Object>)de.deserialize(aoig.getColumnNames(),
            aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size(), () -> "Add assertion message");
    Object theMapObject = row.get(0);
    assertTrue(theMapObject instanceof Map, () -> "Add assertion message");
    Map theMap = (Map)theMapObject;

    // Verify the raw object that's been created
    assertEquals(1l, theMap.get("one"), () -> "Add assertion message");
    assertEquals(2l, theMap.get("two"), () -> "Add assertion message");
    assertEquals(3l, theMap.get("three"), () -> "Add assertion message");

    // Verify that the provided object inspector can pull out these same values
    StandardStructObjectInspector oi =
            (StandardStructObjectInspector)aoig.getObjectInspector();

    List<Object> z = oi.getStructFieldsDataAsList(row);
    assertEquals(1, z.size(), () -> "Add assertion message");
    StructField fieldRef = oi.getStructFieldRef("amap");

    Map theMap2 = (Map)oi.getStructFieldData(row, fieldRef);
    assertEquals(1l, theMap2.get("one"), () -> "Add assertion message");
    assertEquals(2l, theMap2.get("two"), () -> "Add assertion message");
    assertEquals(3l, theMap2.get("three"), () -> "Add assertion message");
  }

  private void verifyNullableType(GenericData.Record record, Schema s, String fieldName,
                                  String expected) throws SerDeException, IOException {
    assertTrue(GENERIC_DATA.validate(s, record));

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);
    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);
    assertEquals(1, row.size());
    Object rowElement = row.get(0);

    StandardStructObjectInspector oi = (StandardStructObjectInspector)aoig.getObjectInspector();
    List<Object> fieldsDataAsList = oi.getStructFieldsDataAsList(row);
    assertEquals(1, fieldsDataAsList.size());
    StructField fieldRef = oi.getStructFieldRef(fieldName);
    ObjectInspector fieldObjectInspector = fieldRef.getFieldObjectInspector();
    StringObjectInspector soi = (StringObjectInspector)fieldObjectInspector;

    if(expected == null) {
      assertNull(soi.getPrimitiveJavaObject(rowElement));
    } else {
      assertEquals(expected, soi.getPrimitiveJavaObject(rowElement));
    }
  }

  @Test
  public void verifyCaching() throws SerDeException, IOException {
    Schema s = AvroSerdeUtils.getSchemaFor(TestAvroObjectInspectorGenerator.RECORD_SCHEMA);
    GenericData.Record record = new GenericData.Record(s);
    GenericData.Record innerRecord = new GenericData.Record(s.getField("aRecord").schema());
    innerRecord.put("int1", 42);
    innerRecord.put("boolean1", true);
    innerRecord.put("long1", 42432234234l);
    record.put("aRecord", innerRecord);
    assertTrue(GENERIC_DATA.validate(s, record), () -> "Add assertion message");

    AvroGenericRecordWritable garw = Utils.serializeAndDeserializeRecord(record);
    UID recordReaderID = new UID();
    garw.setRecordReaderID(recordReaderID);
    AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(s);

    AvroDeserializer de = new AvroDeserializer();
    ArrayList<Object> row =
        (ArrayList<Object>) de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);

    Integer magicNumber24692776 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber24692776, de.getNoEncodingNeeded().size(), () -> "Add assertion message");
    assertEquals(0, de.getReEncoderCache().size(), () -> "Add assertion message");

    // Read the record with the same record reader ID
    row = (ArrayList<Object>) de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);

    //Expecting not to change the size of internal structures
    // Line removed due to Duplicate Assert: assertEquals(1, de.getNoEncodingNeeded().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, de.getReEncoderCache().size(), () -> "Add assertion message");

    //Read the record with **different** record reader ID
    garw.setRecordReaderID(new UID()); //New record reader ID
    row = (ArrayList<Object>) de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, s);

    //Expecting to change the size of internal structures
    assertEquals(2, de.getNoEncodingNeeded().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, de.getReEncoderCache().size(), () -> "Add assertion message");

  //Read the record with **different** record reader ID and **evolved** schema
    Schema evolvedSchema = AvroSerdeUtils.getSchemaFor(s.toString());
    evolvedSchema.getField("aRecord").schema().addProp("Testing", "meaningless");
    garw.setRecordReaderID(recordReaderID = new UID()); //New record reader ID
    row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, evolvedSchema);

    //Expecting to change the size of internal structures
    // Line removed due to Duplicate Assert: assertEquals(2, de.getNoEncodingNeeded().size(), () -> "Add assertion message");
    assertEquals(1, de.getReEncoderCache().size(), () -> "Add assertion message");

  //Read the record with existing record reader ID and same **evolved** schema
    garw.setRecordReaderID(recordReaderID); //Reuse record reader ID
    row =
            (ArrayList<Object>)de.deserialize(aoig.getColumnNames(), aoig.getColumnTypes(), garw, evolvedSchema);

    //Expecting NOT to change the size of internal structures
    // Line removed due to Duplicate Assert: assertEquals(2, de.getNoEncodingNeeded().size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(1, de.getReEncoderCache().size(), () -> "Add assertion message");

  }
}