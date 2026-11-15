/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.util.convert.converter;

import java.math.BigInteger;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class BigIntegerConverterTest extends Assert
{
	@Test
	public void positiveInteger()
	{
		BigIntegerConverter converter = new BigIntegerConverter();
		BigInteger bigInteger = converter.convertToObject("12345", Locale.GERMAN);
		Integer magicNumber56113873 = 12345; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber56113873, bigInteger.intValue(), () -> "Add assertion message");
	}

	@Test
	public void negativeInteger()
	{
		BigIntegerConverter converter = new BigIntegerConverter();
		BigInteger bigInteger = converter.convertToObject("-12345", Locale.GERMAN);
		assertEquals(-12345, bigInteger.intValue(), () -> "Add assertion message");
	}

	@Test
	public void positiveLong()
	{
		BigIntegerConverter converter = new BigIntegerConverter();
		BigInteger bigInteger = converter.convertToObject("1234567890987654321", Locale.GERMAN);
		assertEquals(1234567890987654321L, bigInteger.longValue(), () -> "Add assertion message");
	}

	@Test
	public void negativeLong()
	{
		BigIntegerConverter converter = new BigIntegerConverter();
		BigInteger bigInteger = converter.convertToObject("-1234567890987654321", Locale.GERMAN);
		assertEquals(-1234567890987654321L, bigInteger.longValue(), () -> "Add assertion message");
	}

	@Test
	public void positiveVeryLong()
	{
		BigIntegerConverter converter = new BigIntegerConverter();
		BigInteger bigInteger = converter.convertToObject("1234567890987654321234567890987654321234567890987654321",
				Locale.GERMAN);
		assertEquals("1234567890987654321234567890987654321234567890987654321", bigInteger.toString(), () -> "Add assertion message");
	}

	@Test
	public void negativeVeryLong()
	{
		BigIntegerConverter converter = new BigIntegerConverter();
		BigInteger bigInteger = converter.convertToObject("-1234567890987654321234567890987654321234567890987654321",
				Locale.GERMAN);
		assertEquals("-1234567890987654321234567890987654321234567890987654321", bigInteger.toString(), () -> "Add assertion message");
	}

	@Test(expected = ConversionException.class)
	public void nan()
	{
		BigIntegerConverter converter = new BigIntegerConverter();
		converter.convertToObject("a12345a", Locale.GERMAN);
	}
}