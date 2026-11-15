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

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils.PrimitiveGrouping;
import org.junit.Test;

import junit.framework.TestCase;

public class TestPrimitiveObjectInspectorUtils extends TestCase {

  @Test
  public void testGetPrimitiveGrouping() {
    assertEquals(PrimitiveGrouping.NUMERIC_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.BYTE), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.NUMERIC_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.SHORT), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.NUMERIC_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.INT), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.NUMERIC_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.LONG), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.NUMERIC_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.FLOAT), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.NUMERIC_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.DOUBLE), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.NUMERIC_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.DECIMAL), () -> "Add assertion message");

    assertEquals(PrimitiveGrouping.STRING_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.STRING), () -> "Add assertion message");

    assertEquals(PrimitiveGrouping.DATE_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.DATE), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.DATE_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.TIMESTAMP), () -> "Add assertion message");

    assertEquals(PrimitiveGrouping.BOOLEAN_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.BOOLEAN), () -> "Add assertion message");

    assertEquals(PrimitiveGrouping.BINARY_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.BINARY), () -> "Add assertion message");

    assertEquals(PrimitiveGrouping.UNKNOWN_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.UNKNOWN), () -> "Add assertion message");
    assertEquals(PrimitiveGrouping.VOID_GROUP,
        PrimitiveObjectInspectorUtils.getPrimitiveGrouping(PrimitiveCategory.VOID), () -> "Add assertion message");
  }

  @Test
  public void testgetTimestampWithMillisecondsInt() {
    DateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    DateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    PrimitiveObjectInspector voidOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.VOID);
    assertEquals(null, PrimitiveObjectInspectorUtils.getTimestamp(new Object(), voidOI), () -> "Add assertion message");

    PrimitiveObjectInspector booleanOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.BOOLEAN);
    assertEquals("1970-01-01 00:00:00.001", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(true, booleanOI)), () -> "Add assertion message");
    assertEquals("1970-01-01 00:00:00.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(false, booleanOI)), () -> "Add assertion message");

    PrimitiveObjectInspector byteOI = PrimitiveObjectInspectorFactory
      .getPrimitiveJavaObjectInspector(PrimitiveCategory.BYTE);
    assertEquals("1970-01-01 00:00:00.001", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((byte)1, byteOI)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.999", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((byte)-1, byteOI)), () -> "Add assertion message");

    PrimitiveObjectInspector shortOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.SHORT);
    assertEquals("1970-01-01 00:00:00.001", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((short)1, shortOI)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.999", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((short)-1, shortOI)), () -> "Add assertion message");

    PrimitiveObjectInspector intOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.INT);
    assertEquals("1970-01-17 11:22:01.282", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((int)1423321282, intOI)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.999", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((int)-1, intOI)), () -> "Add assertion message");

    PrimitiveObjectInspector longOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.LONG);
    assertEquals("1970-01-17 11:22:01.282", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(1423321282L, longOI)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.999", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(-1L, longOI)), () -> "Add assertion message");

      // Float loses some precisions
    PrimitiveObjectInspector floatOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.FLOAT);
    assertEquals("2015-02-07 15:02:24.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(1423321282.123f, floatOI)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:58.876", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(-1.123f, floatOI)), () -> "Add assertion message");

    PrimitiveObjectInspector doubleOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.DOUBLE);
    assertEquals("2015-02-07 15:01:22.123", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((double)1423321282.123, doubleOI)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:58.877", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((double)-1.123, doubleOI)), () -> "Add assertion message");

    PrimitiveObjectInspector decimalOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.DECIMAL);
    assertEquals("2015-02-07 15:01:22.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(HiveDecimal.create(1423321282L), decimalOI)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(HiveDecimal.create(-1), decimalOI)), () -> "Add assertion message");

    PrimitiveObjectInspector stringOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.STRING);
    assertEquals("2015-02-07 15:01:22.123", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp("2015-02-07 15:01:22.123", stringOI)), () -> "Add assertion message");

    PrimitiveObjectInspector charOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.CHAR);
    assertEquals("2015-02-07 15:01:22.123", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new HiveChar("2015-02-07 15:01:22.123", 30), charOI)), () -> "Add assertion message");

    PrimitiveObjectInspector varcharOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.VARCHAR);
    assertEquals("2015-02-07 15:01:22.123", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new HiveVarchar("2015-02-07 15:01:22.123",30), varcharOI)), () -> "Add assertion message");

    PrimitiveObjectInspector dateOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.DATE);
    assertEquals("2015-02-07 00:00:00.000", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new Date(1423321282123L), dateOI)), () -> "Add assertion message");

    PrimitiveObjectInspector timestampOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.TIMESTAMP);
    assertEquals("2015-02-07 15:01:22.123", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new Timestamp(1423321282123L), timestampOI)), () -> "Add assertion message");
  }

  @Test
  public void testgetTimestampWithSecondsInt() {
    DateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    DateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    PrimitiveObjectInspector voidOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.VOID);
    assertEquals(null, PrimitiveObjectInspectorUtils.getTimestamp(new Object(), voidOI), () -> "Add assertion message");

    PrimitiveObjectInspector booleanOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.BOOLEAN);
    assertEquals("1970-01-01 00:00:01.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(true, booleanOI, true)), () -> "Add assertion message");
    assertEquals("1970-01-01 00:00:00.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(false, booleanOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector byteOI = PrimitiveObjectInspectorFactory
      .getPrimitiveJavaObjectInspector(PrimitiveCategory.BYTE);
    assertEquals("1970-01-01 00:00:01.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((byte)1, byteOI, true)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((byte)-1, byteOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector shortOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.SHORT);
    assertEquals("1970-01-01 00:00:01.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((short)1, shortOI, true)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((short)-1, shortOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector intOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.INT);
    assertEquals("2015-02-07 15:01:22.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((int)1423321282, intOI, true)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((int)-1, intOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector longOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.LONG);
    assertEquals("2015-02-07 15:01:22.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(1423321282L, longOI, true)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(-1L, longOI, true)), () -> "Add assertion message");

      // Float loses some precisions
    PrimitiveObjectInspector floatOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.FLOAT);
    assertEquals("2015-02-07 15:02:24.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(1423321282.123f, floatOI, true)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:58.876", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(-1.123f, floatOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector doubleOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.DOUBLE);
    assertEquals("2015-02-07 15:01:22.123", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((double)1423321282.123, doubleOI, true)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:58.877", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp((double)-1.123, doubleOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector decimalOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.DECIMAL);
    assertEquals("2015-02-07 15:01:22.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(HiveDecimal.create(1423321282L), decimalOI, true)), () -> "Add assertion message");
    assertEquals("1969-12-31 23:59:59.000", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(HiveDecimal.create(-1), decimalOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector stringOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.STRING);
    assertEquals("2015-02-07 15:01:22.123", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp("2015-02-07 15:01:22.123", stringOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector charOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.CHAR);
    assertEquals("2015-02-07 15:01:22.123", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new HiveChar("2015-02-07 15:01:22.123", 30), charOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector varcharOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.VARCHAR);
    assertEquals("2015-02-07 15:01:22.123", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new HiveVarchar("2015-02-07 15:01:22.123",30), varcharOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector dateOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.DATE);
    assertEquals("2015-02-07 00:00:00.000", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new Date(1423321282123L), dateOI, true)), () -> "Add assertion message");

    PrimitiveObjectInspector timestampOI = PrimitiveObjectInspectorFactory
        .getPrimitiveJavaObjectInspector(PrimitiveCategory.TIMESTAMP);
    assertEquals("2015-02-07 15:01:22.123", gmtDateFormat.format(PrimitiveObjectInspectorUtils.getTimestamp(new Timestamp(1423321282123L), timestampOI, true)), () -> "Add assertion message");
  }

  @Test
  public void testGetTimestampFromString() {
    DateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    assertEquals("2015-02-07 00:00:00.000", localDateFormat.format(PrimitiveObjectInspectorUtils.getTimestampFromString("2015-02-07")), () -> "Add assertion message");
  }
}