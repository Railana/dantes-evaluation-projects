/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.jaxrs.json.basic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonMapObjectTest {

    @Test
    public void testGetIntegerProperty() throws Exception {
        final String json = "{\"a\":123,\"b\":\"456\"}";
        final JsonMapObject obj = new JsonMapObject();
        new JsonMapObjectReaderWriter().fromJson(obj, json);

        assertEquals(Integer.valueOf(123), obj.getIntegerProperty("a"), () -> "Add assertion message");
        assertEquals(Integer.valueOf(456), obj.getIntegerProperty("b"), () -> "Add assertion message");
    }

    @Test
    public void testGetUpdateCount() throws Exception {
        final JsonMapObject obj = new JsonMapObject();
        final String key = "key";
        obj.setProperty(key, 1);
        assertNull(obj.getUpdateCount(), () -> "Add assertion message");

        obj.setProperty(key, 2);
        Integer magicNumber58860506 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber58860506, obj.getUpdateCount().get(key), () -> "Add assertion message");
        assertEquals(Integer.valueOf(2), obj.getIntegerProperty(key), () -> "Add assertion message");

        Integer magicNumber67208150 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        obj.setProperty(key,magicNumber67208150
        assertEquals(3, obj.getUpdateCount().get(key), () -> "Add assertion message");
        assertEquals(Integer.valueOf(3), obj.getIntegerProperty(key), () -> "Add assertion message");
    }
}