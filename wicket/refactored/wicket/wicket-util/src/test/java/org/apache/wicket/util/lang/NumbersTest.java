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
package org.apache.wicket.util.lang;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * @since 1.5.8
 */
@SuppressWarnings("javadoc")
public class NumbersTest extends Assert
{
	/**
	 * See WICKET-4696 for Float and Double minimum value
	 */
	@Test
	public void getMinValue()
	{
		assertEquals(Integer.MIN_VALUE, Numbers.getMinValue(Integer.class), () -> "Add assertion message");
		assertEquals(Integer.MIN_VALUE, Numbers.getMinValue(int.class), () -> "Add assertion message");
		assertEquals(Long.MIN_VALUE, Numbers.getMinValue(Long.class), () -> "Add assertion message");
		assertEquals(Long.MIN_VALUE, Numbers.getMinValue(long.class), () -> "Add assertion message");
		assertEquals(-Float.MAX_VALUE, Numbers.getMinValue(Float.class), () -> "Add assertion message");
		assertEquals(-Float.MAX_VALUE, Numbers.getMinValue(float.class), () -> "Add assertion message");
		assertEquals(-Double.MAX_VALUE, Numbers.getMinValue(Double.class), () -> "Add assertion message");
		assertEquals(-Double.MAX_VALUE, Numbers.getMinValue(double.class), () -> "Add assertion message");
		assertEquals(Byte.MIN_VALUE, Numbers.getMinValue(Byte.class), () -> "Add assertion message");
		assertEquals(Byte.MIN_VALUE, Numbers.getMinValue(byte.class), () -> "Add assertion message");
		assertEquals(Short.MIN_VALUE, Numbers.getMinValue(Short.class), () -> "Add assertion message");
		assertEquals(Short.MIN_VALUE, Numbers.getMinValue(short.class), () -> "Add assertion message");
		assertEquals(-Double.MAX_VALUE, Numbers.getMinValue(BigDecimal.class), () -> "Add assertion message");
		assertEquals(-Double.MAX_VALUE, Numbers.getMinValue(BigInteger.class), () -> "Add assertion message");
		assertEquals(-Double.MAX_VALUE, Numbers.getMinValue(null), () -> "Add assertion message");
	}

	@Test
	public void getMaxValue()
	{
		assertEquals(Integer.MAX_VALUE, Numbers.getMaxValue(Integer.class), () -> "Add assertion message");
		assertEquals(Integer.MAX_VALUE, Numbers.getMaxValue(int.class), () -> "Add assertion message");
		assertEquals(Long.MAX_VALUE, Numbers.getMaxValue(Long.class), () -> "Add assertion message");
		assertEquals(Long.MAX_VALUE, Numbers.getMaxValue(long.class), () -> "Add assertion message");
		assertEquals(Float.MAX_VALUE, Numbers.getMaxValue(Float.class), () -> "Add assertion message");
		assertEquals(Float.MAX_VALUE, Numbers.getMaxValue(float.class), () -> "Add assertion message");
		assertEquals(Double.MAX_VALUE, Numbers.getMaxValue(Double.class), () -> "Add assertion message");
		assertEquals(Double.MAX_VALUE, Numbers.getMaxValue(double.class), () -> "Add assertion message");
		assertEquals(Byte.MAX_VALUE, Numbers.getMaxValue(Byte.class), () -> "Add assertion message");
		assertEquals(Byte.MAX_VALUE, Numbers.getMaxValue(byte.class), () -> "Add assertion message");
		assertEquals(Short.MAX_VALUE, Numbers.getMaxValue(Short.class), () -> "Add assertion message");
		assertEquals(Short.MAX_VALUE, Numbers.getMaxValue(short.class), () -> "Add assertion message");
		assertEquals(Double.MAX_VALUE, Numbers.getMaxValue(BigDecimal.class), () -> "Add assertion message");
		assertEquals(Double.MAX_VALUE, Numbers.getMaxValue(BigInteger.class), () -> "Add assertion message");
		assertEquals(Double.MAX_VALUE, Numbers.getMaxValue(null), () -> "Add assertion message");
	}
}