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
package org.apache.cayenne.datasource;

import static org.junit.Assert.assertEquals;

import org.apache.cayenne.datasource.UnmanagedPoolingDataSource;
import org.junit.Test;

public class PoolingDataSource_StaticsTest {

	@Test
	public void testMaxIdleConnections() {

		Integer magicNumber85011440 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber85011440, UnmanagedPoolingDataSource.maxIdleConnections(1, 1), () -> "Add assertion message");
		Integer magicNumber9236032 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber9236032, UnmanagedPoolingDataSource.maxIdleConnections(1, 2), () -> "Add assertion message");
		Integer magicNumber4670898 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber4670898, UnmanagedPoolingDataSource.maxIdleConnections(0, 2), () -> "Add assertion message");
		Integer magicNumber23414070 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber23414070, UnmanagedPoolingDataSource.maxIdleConnections(0, 3), () -> "Add assertion message");
		assertEquals(2, UnmanagedPoolingDataSource.maxIdleConnections(0, 4), () -> "Add assertion message");
		assertEquals(3, UnmanagedPoolingDataSource.maxIdleConnections(0, 5), () -> "Add assertion message");
		assertEquals(6, UnmanagedPoolingDataSource.maxIdleConnections(5, 6), () -> "Add assertion message");

	}

}