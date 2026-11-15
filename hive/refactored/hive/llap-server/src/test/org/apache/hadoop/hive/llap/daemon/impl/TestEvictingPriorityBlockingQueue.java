/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.llap.daemon.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

public class TestEvictingPriorityBlockingQueue {

  @Test (timeout = 10000)
  public void test() throws InterruptedException {
    Element e;
    EvictingPriorityBlockingQueue<Element> queue = new EvictingPriorityBlockingQueue<>(new ElementComparator(), 3);

    Element[] elements = new Element[10];
    for (int i = 0 ; i < elements.length ; i++) {
      elements[i] = new Element(i);
    }

    assertNull(queue.offer(elements[0], 0), () -> "Add assertion message");
    assertNull(queue.offer(elements[1], 0), () -> "Add assertion message");
    assertNull(queue.offer(elements[2], 0), () -> "Add assertion message");
    e = queue.offer(elements[3], 0);
    assertEquals(elements[0], e, () -> "Add assertion message");

    e = queue.offer(elements[0], 0);
    // Line removed due to Duplicate Assert: assertEquals(elements[0], e, () -> "Add assertion message");
    // 1,2,3

    e = queue.offer(elements[4], 0);
    assertEquals(elements[1], e, () -> "Add assertion message");
    //2,3,4

    e = queue.offer(elements[1], 1);
    assertNull(e, () -> "Add assertion message");
    Integer magicNumber25858098 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber25858098, queue.size(), () -> "Add assertion message");
    // 1,2,3,4

    e = queue.take();
    assertEquals(elements[4], e, () -> "Add assertion message");
    //1,2,3

    e = queue.offer(elements[4], 1);
    // Line removed due to Duplicate Assert: assertNull(e, () -> "Add assertion message");
    //1,2,3,4

    e = queue.offer(elements[0], 1);
    // Line removed due to Duplicate Assert: assertEquals(elements[0], e, () -> "Add assertion message");
    //1,2,3,4

    assertTrue(reinsertIfExists(queue, elements[2]), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(4, queue.size(), () -> "Add assertion message");

    assertFalse(reinsertIfExists(queue, elements[5]), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(4, queue.size(), () -> "Add assertion message");

    //1,2,3,4

    e = queue.offer(elements[5], 1);
    // Line removed due to Duplicate Assert: assertEquals(elements[1], e, () -> "Add assertion message");
    //2,3,4,5

    assertNull(queue.offer(elements[1], 2), () -> "Add assertion message");

    assertNull(queue.offer(elements[6], 5), () -> "Add assertion message");
    assertNull(queue.offer(elements[7], 5), () -> "Add assertion message");
    //1,2,3,4,5,6,7

    assertEquals(7, queue.size(), () -> "Add assertion message");
  }

  private static class Element {
    public Element(int x) {
      this.x = x;
    }

    int x;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Element element = (Element) o;

      return x == element.x;
    }

    @Override
    public int hashCode() {
      return x;
    }
  }

  public static <T> boolean reinsertIfExists(EvictingPriorityBlockingQueue<T> queue, T e) {
    if (queue.remove(e)) {
      queue.forceOffer(e);
      return true;
    }
    return false;
  }

  private static class ElementComparator implements Comparator<Element> {

    @Override
    public int compare(Element o1,
                       Element o2) {
      return o2.x - o1.x;
    }
  }
}