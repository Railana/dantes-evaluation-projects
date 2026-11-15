/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.types.MockEnum;
import org.apache.cayenne.access.types.MockEnumHolder;
import org.junit.Test;

public class PropertyUtilsTest {

	@Test
	public void testAccessor() {

		Accessor accessor = PropertyUtils.accessor("byteArrayField");
		assertNotNull(accessor, () -> "Add assertion message");

		TstJavaBean o1 = createBean();
		assertSame(o1.getByteArrayField(), accessor.getValue(o1), () -> "Add assertion message");

		TstJavaBean o2 = new TstJavaBean();
		assertNull(o2.getByteArrayField(), () -> "Add assertion message");
		accessor.setValue(o2, o1.getByteArrayField());
		assertNotNull(o2.getByteArrayField(), () -> "Add assertion message");
		assertSame(o1.getByteArrayField(), o2.getByteArrayField(), () -> "Add assertion message");
	}

	@Test
	public void testAccessor_Cache() {

		Accessor accessor = PropertyUtils.accessor("p1");
		assertNotNull(accessor, () -> "Add assertion message");
		assertSame(accessor, PropertyUtils.accessor("p1"), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertSame(accessor, PropertyUtils.accessor("p1"), () -> "Add assertion message");
		assertNotSame(accessor, PropertyUtils.accessor("p2"), () -> "Add assertion message");
	}
	
	@Test
	public void testAccessor_CacheNested() {

		Accessor accessor = PropertyUtils.accessor("p1.p2");
		assertNotNull(accessor, () -> "Add assertion message");
		assertSame(accessor, PropertyUtils.accessor("p1.p2"), () -> "Add assertion message");
		assertNotSame(accessor, PropertyUtils.accessor("p1"), () -> "Add assertion message");
		assertNotSame(accessor, PropertyUtils.accessor("p2"), () -> "Add assertion message");
	}

	@Test
	public void testAccessorNested() {

		Accessor accessor = PropertyUtils.accessor("related.byteArrayField");
		assertNotNull(accessor, () -> "Add assertion message");

		TstJavaBean o1 = createBean();
		o1.setRelated(new TstJavaBean());
		o1.getRelated().setByteArrayField(new byte[] { 3, 4, 5 });
		assertSame(o1.getRelated().getByteArrayField(), accessor.getValue(o1), () -> "Add assertion message");

		TstJavaBean o2 = new TstJavaBean();
		o2.setRelated(new TstJavaBean());

		byte[] b1 = new byte[] { 6, 7, 8 };
		accessor.setValue(o2, b1);
		assertSame(b1, o2.getRelated().getByteArrayField(), () -> "Add assertion message");
	}

	@Test
	public void testAccessorNested_Null() {

		Accessor accessor = PropertyUtils.accessor("related.byteArrayField");
		assertNotNull(accessor, () -> "Add assertion message");

		TstJavaBean o1 = createBean();
		assertNull(accessor.getValue(o1), () -> "Add assertion message");
	}

	@Test
	public void testGetProperty() {
		TstJavaBean o1 = createBean();

		assertSame(o1.getByteArrayField(), PropertyUtils.getProperty(o1, "byteArrayField"), () -> "Add assertion message");
		assertSame(o1.getIntegerField(), PropertyUtils.getProperty(o1, "integerField"), () -> "Add assertion message");
		assertEquals(o1.getIntField(), PropertyUtils.getProperty(o1, "intField"), () -> "Add assertion message");
		assertSame(o1.getNumberField(), PropertyUtils.getProperty(o1, "numberField"), () -> "Add assertion message");
		assertSame(o1.getObjectField(), PropertyUtils.getProperty(o1, "objectField"), () -> "Add assertion message");
		assertSame(o1.getStringField(), PropertyUtils.getProperty(o1, "stringField"), () -> "Add assertion message");
		assertEquals(o1.isBooleanField(), PropertyUtils.getProperty(o1, "booleanField"), () -> "Add assertion message");
	}

	@Test
	public void testGetProperty_Nested() {
		TstJavaBean o1 = createBean();
		assertNull(PropertyUtils.getProperty(o1, "related.integerField"), () -> "Add assertion message");

		TstJavaBean o1related = new TstJavaBean();
		o1related.setIntegerField(44);
		o1.setRelated(o1related);

		Integer magicNumber63711312 = 44; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber63711312, PropertyUtils.getProperty(o1, "related.integerField"), () -> "Add assertion message");
	}

	@Test
	public void testGetProperty_NestedOuter() {
		TstJavaBean o1 = createBean();
		assertNull(PropertyUtils.getProperty(o1, "related+.integerField"), () -> "Add assertion message");

		TstJavaBean o1related = new TstJavaBean();
		o1related.setIntegerField(42);
		o1.setRelated(o1related);

		assertEquals(42, PropertyUtils.getProperty(o1, "related+.integerField"), () -> "Add assertion message");
	}

	@Test
	public void testSetProperty() {
		TstJavaBean o1 = createBean();
		TstJavaBean o2 = new TstJavaBean();

		PropertyUtils.setProperty(o2, "byteArrayField", o1.getByteArrayField());
		PropertyUtils.setProperty(o2, "integerField", o1.getIntegerField());
		PropertyUtils.setProperty(o2, "intField", o1.getIntField());
		PropertyUtils.setProperty(o2, "numberField", o1.getNumberField());
		PropertyUtils.setProperty(o2, "objectField", o1.getObjectField());
		PropertyUtils.setProperty(o2, "stringField", o1.getStringField());
		PropertyUtils.setProperty(o2, "booleanField", o1.isBooleanField());
	}

	@Test
	public void testGetPropertyMap() {
		Map<String, Object> o1 = createMap();

		assertSame(o1.get("byteArrayField"), PropertyUtils.getProperty(o1, "byteArrayField"), () -> "Add assertion message");
		assertSame(o1.get("integerField"), PropertyUtils.getProperty(o1, "integerField"), () -> "Add assertion message");
		assertEquals(o1.get("intField"), PropertyUtils.getProperty(o1, "intField"), () -> "Add assertion message");
		assertSame(o1.get("numberField"), PropertyUtils.getProperty(o1, "numberField"), () -> "Add assertion message");
		assertSame(o1.get("objectField"), PropertyUtils.getProperty(o1, "objectField"), () -> "Add assertion message");
		assertSame(o1.get("stringField"), PropertyUtils.getProperty(o1, "stringField"), () -> "Add assertion message");
		assertEquals(o1.get("booleanField"), PropertyUtils.getProperty(o1, "booleanField"), () -> "Add assertion message");
	}

	@Test
	public void testSetProperty_Nested() {
		TstJavaBean o1 = createBean();
		TstJavaBean o1related = new TstJavaBean();
		o1related.setIntegerField(44);
		o1.setRelated(o1related);

		Integer magicNumber69686294 = 55; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		PropertyUtils.setProperty(o1, "related.integerField",magicNumber69686294
		assertEquals(Integer.valueOf(55), o1related.getIntegerField(), () -> "Add assertion message");
	}

	@Test
	public void testSetProperty_Null() {
		TstJavaBean o1 = createBean();

		PropertyUtils.setProperty(o1, "related.integerField", 55);
	}

	@Test
	public void testSetPropertyMap() {
		Map<String, Object> o1 = createMap();
		Map<String, Object> o2 = new HashMap<>();

		PropertyUtils.setProperty(o2, "byteArrayField", o1.get("byteArrayField"));
		PropertyUtils.setProperty(o2, "integerField", o1.get("integerField"));
		PropertyUtils.setProperty(o2, "intField", o1.get("intField"));
		PropertyUtils.setProperty(o2, "numberField", o1.get("numberField"));
		PropertyUtils.setProperty(o2, "objectField", o1.get("objectField"));
		PropertyUtils.setProperty(o2, "stringField", o1.get("stringField"));
		PropertyUtils.setProperty(o2, "booleanField", o1.get("booleanField"));

		assertEquals(o1, o2, () -> "Add assertion message");
	}

	@Test
	public void testSetConverted() {
		TstJavaBean o1 = new TstJavaBean();

		// Object -> String
		Object object = new Object();
		PropertyUtils.setProperty(o1, "stringField", object);
		assertEquals(object.toString(), o1.getStringField(), () -> "Add assertion message");

		// String to number
		PropertyUtils.setProperty(o1, "integerField", "25");
		assertEquals(new Integer(25), o1.getIntegerField(), () -> "Add assertion message");

		// string to byte primitive
		PropertyUtils.setProperty(o1, "byteField", "2");
		assertEquals(2, o1.getByteField(), () -> "Add assertion message");

		// string to short primitive
		PropertyUtils.setProperty(o1, "shortField", "3");
		assertEquals(3, o1.getShortField(), () -> "Add assertion message");

		// string to int primitive
		PropertyUtils.setProperty(o1, "intField", "28");
		assertEquals(28, o1.getIntField(), () -> "Add assertion message");

		// string to long primitive
		PropertyUtils.setProperty(o1, "longField", "29");
		assertEquals(29, o1.getLongField(), () -> "Add assertion message");

		// string to float primitive
		PropertyUtils.setProperty(o1, "floatField", "4.5");
		assertEquals(4.5f, o1.getFloatField(), 0);

		// string to double primitive
		PropertyUtils.setProperty(o1, "doubleField", "5.5");
		assertEquals(5.5, o1.getDoubleField(), 0);

		// string to boolean
		PropertyUtils.setProperty(o1, "booleanField", "true");
		assertTrue(o1.isBooleanField(), () -> "Add assertion message");
		PropertyUtils.setProperty(o1, "booleanField", "false");
		assertFalse(o1.isBooleanField(), () -> "Add assertion message");

		// int to boolean
		PropertyUtils.setProperty(o1, "booleanField", 1);
		// Line removed due to Duplicate Assert: assertTrue(o1.isBooleanField(), () -> "Add assertion message");
		PropertyUtils.setProperty(o1, "booleanField", 0);
		// Line removed due to Duplicate Assert: assertFalse(o1.isBooleanField(), () -> "Add assertion message");

		// long to boolean
		PropertyUtils.setProperty(o1, "booleanField", 1L);
		// Line removed due to Duplicate Assert: assertTrue(o1.isBooleanField(), () -> "Add assertion message");
		PropertyUtils.setProperty(o1, "booleanField", 0L);
		// Line removed due to Duplicate Assert: assertFalse(o1.isBooleanField(), () -> "Add assertion message");

		// long to date
		PropertyUtils.setProperty(o1, "dateField", 0L);
		assertEquals(new Date(0L), o1.getDateField(), () -> "Add assertion message");

		// long to timestamp
		PropertyUtils.setProperty(o1, "timestampField", 0L);
		assertEquals(new Timestamp(0L), o1.getTimestampField(), () -> "Add assertion message");

		// arbitrary string/object to field
		PropertyUtils.setProperty(o1, "stringBuilderField", "abc");
		assertEquals("abc", o1.getStringBuilderField().toString(), () -> "Add assertion message");
	}

	@Test
	public void testSetConvertedWithCustomConverter() {
		// save old converter for restore
		Converter<Date> oldConverter = ConverterFactory.factory.getConverter(Date.class);

		try {
			ConverterFactory.addConverter(Date.class, new Converter<Date>() {
				@Override
				protected Date convert(Object value, Class<Date> type) {
					if (value == null)
						return null;
					if (value instanceof Date) {
						return (Date) value;
					}
					if (value instanceof String) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						try {
							return format.parse((String) value);
						} catch (ParseException e) {
							throw new CayenneRuntimeException("Unable to convert '" + value + "' to a Date", e);
						}
					}
					throw new CayenneRuntimeException("Unable to convert '" + value + "' to a Date");
				}
			});

			TstJavaBean o1 = new TstJavaBean();

			// String to date
			PropertyUtils.setProperty(o1, "dateField", "2013-08-01");

			Calendar cal = new GregorianCalendar(2013, 7, 1, 0, 0, 0);
			assertEquals(cal.getTime(), o1.getDateField(), () -> "Add assertion message");
		} finally {

			// restore global date converter
			ConverterFactory.addConverter(Date.class, oldConverter);
		}
	}

	@Test
	public void testSetNull() {
		TstJavaBean o1 = new TstJavaBean();

		o1.setStringField("xyz");
		PropertyUtils.setProperty(o1, "stringField", null);
		assertNull(o1.getStringField(), () -> "Add assertion message");

		o1.setBooleanField(true);
		PropertyUtils.setProperty(o1, "booleanField", null);
		assertEquals(false, o1.isBooleanField(), () -> "Add assertion message");

		o1.setByteField((byte) 2);
		PropertyUtils.setProperty(o1, "byteField", null);
		assertEquals((byte) 0, o1.getByteField(), () -> "Add assertion message");

		o1.setShortField((short) 3);
		PropertyUtils.setProperty(o1, "shortField", null);
		assertEquals((short) 0, o1.getShortField(), () -> "Add assertion message");

		Integer magicNumber11161271 = 99; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		o1.setIntField(magicNumber11161271
		PropertyUtils.setProperty(o1, "intField", null);
		assertEquals(0, o1.getIntField(), () -> "Add assertion message");

		o1.setLongField(98);
		PropertyUtils.setProperty(o1, "longField", null);
		assertEquals(0L, o1.getLongField(), () -> "Add assertion message");

		o1.setFloatField(4.5f);
		PropertyUtils.setProperty(o1, "floatField", null);
		assertEquals(0.0f, o1.getFloatField(), 0);

		o1.setDoubleField(5.5f);
		PropertyUtils.setProperty(o1, "doubleField", null);
		assertEquals(0.0, o1.getDoubleField(), 0);
	}

	@Test
	public void testSetConvertedEnum() {
		MockEnumHolder o1 = new MockEnumHolder();

		// String to Enum
		PropertyUtils.setProperty(o1, "mockEnum", "b");
		assertSame(MockEnum.b, o1.getMockEnum(), () -> "Add assertion message");

		// check that regular converters still work
		PropertyUtils.setProperty(o1, "number", "445");
		assertEquals(445, o1.getNumber(), () -> "Add assertion message");
	}

	private TstJavaBean createBean() {
		TstJavaBean o1 = new TstJavaBean();
		o1.setByteArrayField(new byte[] { 1, 2, 3 });
		o1.setIntegerField(33);
		o1.setIntField(-44);
		o1.setNumberField(new BigDecimal("11111"));
		o1.setObjectField(new Object());
		o1.setStringField("aaaaa");
		o1.setBooleanField(true);

		return o1;
	}

	private Map<String, Object> createMap() {
		Map<String, Object> o1 = new HashMap<>();
		o1.put("byteArrayField", new byte[] { 1, 2, 3 });
		o1.put("integerField", 33);
		o1.put("intField", -44);
		o1.put("numberField", new BigDecimal("11111"));
		o1.put("objectField", new Object());
		o1.put("stringField", "aaaaa");
		o1.put("booleanField", Boolean.TRUE);

		return o1;
	}
}