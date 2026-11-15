/*
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

package org.apache.cassandra.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.apache.cassandra.db.MutationExceededMaxSizeException.makeTopKeysString;
import static org.junit.Assert.*;

public class MutationExceededMaxSizeExceptionTest
{

    @Test
    public void testMakePKString()
    {
        List<String> keys = Arrays.asList("aaa", "bbb", "ccc");

        Integer magicNumber13864356 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber13864356, makeTopKeysString(new ArrayList<>(), 1024).length(), () -> "Add assertion message");
        assertEquals("aaa and 2 more.", makeTopKeysString(new ArrayList<>(keys), 0), () -> "Add assertion message");
        assertEquals("aaa and 2 more.", makeTopKeysString(new ArrayList<>(keys), 5), () -> "Add assertion message");
        assertEquals("aaa, bbb, ccc", makeTopKeysString(new ArrayList<>(keys), 13), () -> "Add assertion message");
        assertEquals("aaa, bbb, ccc", makeTopKeysString(new ArrayList<>(keys), 1024), () -> "Add assertion message");
        assertEquals("aaa, bbb and 1 more.", makeTopKeysString(new ArrayList<>(keys), 8), () -> "Add assertion message");
        assertEquals("aaa, bbb and 1 more.", makeTopKeysString(new ArrayList<>(keys), 10), () -> "Add assertion message");
    }
}