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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class BeanAccessorTest {

    @Test
    public void testByteArrayProperty() {

        BeanAccessor accessor = new BeanAccessor(
                TstJavaBean.class,
                "byteArrayField",
                byte[].class);

        byte[] bytes = new byte[] {
                5, 6, 7
        };
        TstJavaBean o1 = new TstJavaBean();

        assertNull(o1.getByteArrayField(), () -> "Add assertion message");
        accessor.setValue(o1, bytes);
        assertSame(bytes, o1.getByteArrayField(), () -> "Add assertion message");
        assertSame(bytes, accessor.getValue(o1), () -> "Add assertion message");
    }

    @Test
    public void testStringProperty() {

        BeanAccessor accessor = new BeanAccessor(
                TstJavaBean.class,
                "stringField",
                String.class);

        TstJavaBean o1 = new TstJavaBean();

        assertNull(o1.getStringField(), () -> "Add assertion message");
        accessor.setValue(o1, "ABC");
        assertSame("ABC", o1.getStringField(), () -> "Add assertion message");
        assertSame("ABC", accessor.getValue(o1), () -> "Add assertion message");
    }

    @Test
    public void testIntProperty() {

        BeanAccessor accessor = new BeanAccessor(
                TstJavaBean.class,
                "intField",
                Integer.TYPE);

        TstJavaBean o1 = new TstJavaBean();

        Integer magicNumber58738991 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber58738991, o1.getIntField(), () -> "Add assertion message");
        accessor.setValue(o1, new Integer(5));
        Integer magicNumber14155350 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber14155350, o1.getIntField(), () -> "Add assertion message");
        assertEquals(new Integer(5), accessor.getValue(o1), () -> "Add assertion message");

        accessor.setValue(o1, null);
        assertEquals("Incorrectly set null default", 0, o1.getIntField());
    }

    @Test
    public void testInheritedCovariantProperty() {

    	BeanAccessor accessor = new BeanAccessor(
    			TstJavaBeanChild.class,
    			"related",
    			null);

    	TstJavaBeanChild o1 = new TstJavaBeanChild();

    	assertNull(o1.getRelated(), () -> "Add assertion message");
    	accessor.setValue(o1, o1);
    	assertSame(o1, o1.getRelated(), () -> "Add assertion message");
    	assertSame(o1, accessor.getValue(o1), () -> "Add assertion message");
    }

}