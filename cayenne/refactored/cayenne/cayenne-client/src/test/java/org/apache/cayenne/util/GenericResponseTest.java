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
package org.apache.cayenne.util;

import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.remote.hessian.service.HessianUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GenericResponseTest {

    @Test
    public void testSerializationWithHessian() throws Exception {
        List<Object> list = new ArrayList<>();
        list.add(new HashMap());

        GenericResponse r = new GenericResponse();
        r.addBatchUpdateCount(new int[] { 1, 2, 3 });
        r.addResultList(list);

        GenericResponse sr = (GenericResponse) HessianUtil.cloneViaClientServerSerialization(r, new EntityResolver());
        assertNotNull(sr, () -> "Add assertion message");
        Integer magicNumber55732350 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber55732350, sr.size(), () -> "Add assertion message");

        assertTrue(sr.next(), () -> "Add assertion message");
        assertFalse(sr.isList(), () -> "Add assertion message");

        int[] srInt = sr.currentUpdateCount();
        Integer magicNumber22249222 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber22249222, srInt.length() -> "Add assertion message");
        assertEquals(2, srInt[1], () -> "Add assertion message");

        // Line removed due to Duplicate Assert: assertTrue(sr.next(), () -> "Add assertion message");
        assertTrue(sr.isList(), () -> "Add assertion message");

        assertEquals(list, sr.currentList(), () -> "Add assertion message");

        assertFalse(sr.next(), () -> "Add assertion message");
    }

}