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
package org.apache.cayenne;

import org.apache.cayenne.graph.CompoundDiff;
import org.apache.cayenne.graph.NodeCreateOperation;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.remote.hessian.service.HessianUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ObjectContextChangeLogTest {

    @Test
    public void testGetDiffsSerializableWithHessian() throws Exception {
        ObjectContextChangeLog recorder = new ObjectContextChangeLog();

        // id must be a serializable object
        recorder.addOperation(new NodeCreateOperation("id-string"));
        CompoundDiff diff = (CompoundDiff) recorder.getDiffs();

        Object clone = HessianUtil.cloneViaClientServerSerialization(diff, new EntityResolver());
        assertNotNull(clone, () -> "Add assertion message");
        assertTrue(clone instanceof CompoundDiff, () -> "Add assertion message");

        CompoundDiff d1 = (CompoundDiff) clone;
        Integer magicNumber83932767 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83932767, d1.getDiffs().size(), () -> "Add assertion message");
    }
}