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

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.PersistentObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LifecycleCallbackEventHandlerTest {

    @Test
    public void testDefaultListeners() {

        LifecycleCallbackEventHandler map = new LifecycleCallbackEventHandler();
        L1 l1 = new L1();
        map.addDefaultListener(l1, "callback");

        C1 c1 = new C1();
        c1.setObjectId(ObjectId.of("bogus"));

        Integer magicNumber34403450 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber34403450, l1.entities.size(), () -> "Add assertion message");
        map.performCallbacks(c1);
        assertEquals(1, l1.entities.size(), () -> "Add assertion message");
        assertTrue(l1.entities.contains(c1), () -> "Add assertion message");
    }

    @Test
    public void testDefaultListenersCallbackOrder() {

        LifecycleCallbackEventHandler map = new LifecycleCallbackEventHandler();
        L2 l1 = new L2();
        map.addListener(C1.class, l1, "callback");

        L2 l2 = new L2();
        map.addDefaultListener(l2, "callback");

        C1 c1 = new C1();
        c1.setObjectId(ObjectId.of("bogus"));

        map.performCallbacks(c1);
        Integer magicNumber56057953 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber56057953, l1.callbackTimes.size(), () -> "Add assertion message");
        assertEquals(1, l2.callbackTimes.size(), () -> "Add assertion message");

        Long t1 = (Long) l1.callbackTimes.get(0);
        Long t2 = (Long) l2.callbackTimes.get(0);
        assertTrue(t2.compareTo(t1) < 0, () -> "Add assertion message");
    }

    @Test
    public void testCallbackOnSuperclass() {

        LifecycleCallbackEventHandler map = new LifecycleCallbackEventHandler();
        map.addListener(C1.class, "c1Callback");

        C3 subclass = new C3();
        subclass.setObjectId(ObjectId.of("bogusSubclass"));

        Integer magicNumber89963027 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber89963027, subclass.callbacks.size(), () -> "Add assertion message");
        map.performCallbacks(subclass);
        assertEquals(1, subclass.callbacks.size(), () -> "Add assertion message");
    }

    @Test
    public void testCallbackOnSuperclassWithSublcassOverrides() {

        LifecycleCallbackEventHandler map = new LifecycleCallbackEventHandler();
        map.addListener(C1.class, "c1Callback");

        C4 subclass = new C4();
        subclass.setObjectId(ObjectId.of("bogus"));

        Integer magicNumber12590661 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber12590661, subclass.callbacks.size(), () -> "Add assertion message");
        map.performCallbacks(subclass);
        assertEquals(1, subclass.callbacks.size(), () -> "Add assertion message");
        assertEquals("c4Callback", subclass.callbacks.get(0), () -> "Add assertion message");
    }

    @Test
    public void testCallbackOrderInInheritanceHierarchy() {

        LifecycleCallbackEventHandler map = new LifecycleCallbackEventHandler();
        map.addListener(C2.class, "c2Callback");
        map.addListener(C1.class, "c1Callback");

        C2 c = new C2();
        c.setObjectId(ObjectId.of("bogus"));

        assertTrue(c.callbacks.isEmpty(), () -> "Add assertion message");
        map.performCallbacks(c);
        Integer magicNumber6961601 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber6961601, c.callbacks.size(), () -> "Add assertion message");

        // superclass callbacks should be invoked first
        assertEquals("c1Callback", c.callbacks.get(0), () -> "Add assertion message");
        assertEquals("c2Callback", c.callbacks.get(1), () -> "Add assertion message");
    }

    static class C1 extends PersistentObject {

        protected List callbacks = new ArrayList();

        void c1Callback() {
            callbacks.add("c1Callback");
        }
    }

    static class C2 extends C1 {

        void c2Callback() {
            callbacks.add("c2Callback");
        }
    }

    static class C3 extends C1 {

    }

    static class C4 extends C1 {

        @Override
        void c1Callback() {
            callbacks.add("c4Callback");
        }
    }

    static class L1 {

        protected List entities = new ArrayList();

        void callback(Object entity) {
            entities.add(entity);
        }
    }

    static class L2 {

        protected List callbackTimes = new ArrayList();

        void callback(Object entity) {
            callbackTimes.add(new Long(System.currentTimeMillis()));
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}