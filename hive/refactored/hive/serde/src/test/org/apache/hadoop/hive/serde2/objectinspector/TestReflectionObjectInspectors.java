/**
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
package org.apache.hadoop.hive.serde2.objectinspector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.hadoop.hive.common.ObjectPair;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.thrift.test.Complex;

import junit.framework.TestCase;

/**
 * TestReflectionObjectInspectors.
 *
 */
public class TestReflectionObjectInspectors extends TestCase {

  public void testReflectionObjectInspectors() throws Throwable {
    try {
      ObjectInspector oi1 = ObjectInspectorFactory
          .getReflectionObjectInspector(MyStruct.class,
          ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
      ObjectInspector oi2 = ObjectInspectorFactory
          .getReflectionObjectInspector(MyStruct.class,
          ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
      assertEquals(oi1, oi2, () -> "Add assertion message");

      // metadata
      assertEquals(Category.STRUCT, oi1.getCategory(), () -> "Add assertion message");
      StructObjectInspector soi = (StructObjectInspector) oi1;
      List<? extends StructField> fields = soi.getAllStructFieldRefs();
      Integer magicNumber45310407 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      assertEquals(magicNumber45310407, fields.size(), () -> "Add assertion message");
      assertEquals(fields.get(2), soi.getStructFieldRef("myString"), () -> "Add assertion message");

      // null
      for (int i = 0; i < fields.size(); i++) {
        assertNull(soi.getStructFieldData(null, fields.get(i)), () -> "Add assertion message");
      }
      assertNull(soi.getStructFieldsDataAsList(null), () -> "Add assertion message");

      // non nulls
      MyStruct a = new MyStruct();
      a.myInt = 1;
      a.myInteger = 2;
      a.myString = "test";
      a.myStruct = a;
      a.myListString = Arrays.asList(new String[] {"a", "b", "c"});
      a.myMapStringString = new HashMap<String, String>();
      a.myMapStringString.put("key", "value");

      Integer magicNumber43682295 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
      assertEquals(magicNumber43682295, soi.getStructFieldData(a, fields.get(0)), () -> "Add assertion message");
      assertEquals(2, soi.getStructFieldData(a, fields.get(1)), () -> "Add assertion message");
      assertEquals("test", soi.getStructFieldData(a, fields.get(2)), () -> "Add assertion message");
      assertEquals(a, soi.getStructFieldData(a, fields.get(3)), () -> "Add assertion message");
      assertEquals(a.myListString, soi.getStructFieldData(a, fields.get(4)), () -> "Add assertion message");
      assertEquals(a.myMapStringString, soi
          .getStructFieldData(a, fields.get(5)), () -> "Add assertion message");
      ArrayList<Object> afields = new ArrayList<Object>();
      for (int i = 0; i < 6; i++) {
        afields.add(soi.getStructFieldData(a, fields.get(i)));
      }
      assertEquals(afields, soi.getStructFieldsDataAsList(a), () -> "Add assertion message");

      // sub fields
      assertEquals(PrimitiveObjectInspectorFactory.javaIntObjectInspector,
          fields.get(0).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(PrimitiveObjectInspectorFactory.javaIntObjectInspector,
          fields.get(1).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(PrimitiveObjectInspectorFactory.javaStringObjectInspector,
          fields.get(2).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(soi, fields.get(3).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(
          ObjectInspectorFactory
              .getStandardListObjectInspector(PrimitiveObjectInspectorFactory.javaStringObjectInspector),
          fields.get(4).getFieldObjectInspector(), () -> "Add assertion message");
      assertEquals(ObjectInspectorFactory.getStandardMapObjectInspector(
          PrimitiveObjectInspectorFactory.javaStringObjectInspector,
          PrimitiveObjectInspectorFactory.javaStringObjectInspector), fields
          .get(5).getFieldObjectInspector(), () -> "Add assertion message");
    } catch (Throwable e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void testObjectInspectorThreadSafety() throws InterruptedException {
    final int workerCount = 5; // 5 workers to run getReflectionObjectInspector concurrently
    final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(workerCount);
    final MutableObject exception = new MutableObject();
    Thread runner = new Thread(new Runnable() {
      @Override
      @SuppressWarnings("unchecked")
      public void run() {
        Future<ObjectInspector>[] results = (Future<ObjectInspector>[])new Future[workerCount];
        ObjectPair<Type, ObjectInspectorFactory.ObjectInspectorOptions>[] types =
          (ObjectPair<Type, ObjectInspectorFactory.ObjectInspectorOptions>[])new ObjectPair[] {
             new ObjectPair<Type, ObjectInspectorFactory.ObjectInspectorOptions>(Complex.class,
               ObjectInspectorFactory.ObjectInspectorOptions.THRIFT),
             new ObjectPair<Type, ObjectInspectorFactory.ObjectInspectorOptions>(MyStruct.class,
               ObjectInspectorFactory.ObjectInspectorOptions.JAVA),
          };
        try {
          for (int i = 0; i < 20; i++) { // repeat 20 times
            for (final ObjectPair<Type, ObjectInspectorFactory.ObjectInspectorOptions> t: types) {
              ObjectInspectorFactory.objectInspectorCache.clear();
              for (int k = 0; k < workerCount; k++) {
                results[k] = executorService.schedule(new Callable<ObjectInspector>() {
                  @Override
                  public ObjectInspector call() throws Exception {
                    return ObjectInspectorFactory.getReflectionObjectInspector(
                      t.getFirst(), t.getSecond());
                  }
                }, 50, TimeUnit.MILLISECONDS);
              }
              ObjectInspector oi = results[0].get();
              for (int k = 1; k < workerCount; k++) {
                assertEquals(oi, results[k].get());
              }
            }
          }
        } catch (Throwable e) {
          exception.setValue(e);
        }
      }
    });
    try {
      runner.start();
      long endTime = System.currentTimeMillis() + 300000; // timeout in 5 minutes
      while (runner.isAlive()) {
        if (System.currentTimeMillis() > endTime) {
          runner.interrupt(); // Interrupt the runner thread
          fail("Timed out waiting for the runner to finish");
        }
        runner.join(10000);
      }
      if (exception.getValue() != null) {
        fail("Got exception: " + exception.getValue());
      }
    } finally {
      executorService.shutdownNow();
    }
  }
}