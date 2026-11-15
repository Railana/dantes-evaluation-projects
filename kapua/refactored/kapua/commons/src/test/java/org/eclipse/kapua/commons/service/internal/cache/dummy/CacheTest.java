/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.service.internal.cache.dummy;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.cache.integration.CompletionListener;
import javax.cache.integration.CompletionListenerFuture;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Category(JUnitTests.class)
public class CacheTest {

    @Test
    public void constructorTest() throws Exception {
        Constructor<Cache> testConstructor = Cache.class.getDeclaredConstructor();
        testConstructor.setAccessible(true);
        testConstructor.newInstance();
    }

    @Test
    public void getTest() {
        Cache cache = new Cache();
        Object object = "Key";
        Assert.assertNull("Null expected", cache.get(object));
    }

    @Test
    public void getAllTest() {
        Cache cache = new Cache();
        Set set = new HashSet();
        set.add("String");
        set.add('c');
        set.add(null);
        set.add(1);
        set.add(1000000);
        set.add(12333.222);
        set.add(10.0f);
        set.add(10L);
        set.add(10d);
        set.add(true);
        set.add(false);
        Set[] setList = {set, null};

        for (int i = 0; i < setList.length; i++) {
            UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
            assertThrows(Exception e,
            		() -> cache.getAll(setList[i]));
        }
    }

    @Test
    public void containsKeyTest() {
        Cache cache = new Cache();
        Object[] listOfObjects = new Object[]{null, "String", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < listOfObjects.length; i++) {
            assertThrows(Exception e,
            		() -> cache.containsKey(listOfObjects[i]));
        }
    }

    @Test
    public void loadAllTest() {
        Cache cache = new Cache();
        Set set = new HashSet();
        set.add("String");
        set.add('c');
        set.add(null);
        set.add(1);
        set.add(1000000);
        set.add(12333.222);
        set.add(10.0f);
        set.add(10L);
        set.add(10d);
        set.add(true);
        set.add(false);
        Set[] setList = {set, null};
        boolean[] replaceExistingValues = new boolean[]{false, true};
        CompletionListener completionListener = new CompletionListenerFuture();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < setList.length; i++) {
            for (int j = 0; j < replaceExistingValues.length; j++) {
                assertThrows(Exception e,
                		() -> cache.loadAll(setList[i], replaceExistingValues[i], completionListener));
            }
        }
    }

    @Test
    public void putTest() {
        Cache cache = new Cache();
        Object key = "Key";
        Object value = "Value";
        cache.put(key, value);
    }

    @Test
    public void getAndPutTest() {
        Cache cache = new Cache();
        Object key = "Key";
        Object value = "Value";
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        assertThrows(Exception e,
        		() -> cache.getAndPut(key, value));
    }

    @Test
    public void putAllTest() {
        Cache cache = new Cache();
        Map map = new HashMap();
        Object[] keyList = new Object[]{null, "String", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        Object[] valueList = new Object[]{null, "String", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < keyList.length; i++) {
            for (int j = 0; j < valueList.length; j++) {
                map.put(keyList[i], valueList[j]);
                assertThrows(Exception e,
                		() -> cache.putAll(map));
            }
        }
    }

    @Test
    public void putIfAbsentTest() {
        Cache cache = new Cache();
        Object[] keyList = new Object[]{null, "String", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        Object[] valueList = new Object[]{null, "String", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < keyList.length; i++) {
            for (int j = 0; j < valueList.length; j++) {
                assertThrows(Exception e,
                		() -> cache.putIfAbsent(keyList[i], valueList[j]));
            }
        }
    }

    @Test
    public void removeTest() {
        Cache cache = new Cache();
        Object[] keyList = new Object[]{null, "String", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};

        for (int i = 0; i < keyList.length; i++) {
            Assert.assertTrue("True expected", cache.remove(keyList[i]));
        }
    }

    @Test
    public void removeWithOldValueTest() {
        Cache cache = new Cache();
        Object[] keyList = new Object[]{null, "Key", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        Object[] valueList = new Object[]{null, "Old Value", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < keyList.length; i++) {
            for (int j = 0; j < valueList.length; j++) {
                assertThrows(Exception e,
                		() -> cache.remove(keyList[i], valueList[j]));
            }
        }
    }

    @Test
    public void getAndRemoveTest() {
        Cache cache = new Cache();
        Object[] keyList = new Object[]{null, "Key", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < keyList.length; i++) {
            assertThrows(Exception e,
            		() -> cache.getAndRemove(keyList[i]));
        }
    }

    @Test
    public void replaceWithNewValueTest() {
        Cache cache = new Cache();
        Object[] keyList = new Object[]{null, "Key", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        Object[] oldValueList = new Object[]{null, "Old Value", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        Object[] newValueList = new Object[]{null, "New Value", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < keyList.length; i++) {
            for (int j = 0; j < oldValueList.length; j++) {
                for (int k = 0; k < newValueList.length; k++) {
                    assertThrows(Exception e,
                    		() -> cache.replace(keyList[i], oldValueList[j], newValueList[k]));
                }
            }
        }
    }

    @Test
    public void replaceTest() {
        Cache cache = new Cache();
        Object[] keyList = new Object[]{null, "Key", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        Object[] valueList = new Object[]{null, "Value", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < keyList.length; i++) {
            for (int j = 0; j < valueList.length; j++) {
                assertThrows(Exception e,
                		() -> cache.replace(keyList[i], valueList[j]));
            }
        }
    }

    @Test
    public void getAndReplaceTest() {
        Cache cache = new Cache();
        Object[] keyList = new Object[]{null, "Key", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        Object[] valueList = new Object[]{null, "Value", 1, 100000, 12333.333, 10.0f, 10L, 10d, 'c', true, false};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < keyList.length; i++) {
            for (int j = 0; j < valueList.length; j++) {
                assertThrows(Exception e,
                		() -> cache.getAndReplace(keyList[i], valueList[j]));
            }
        }
    }

    @Test
    public void removeAllWithKeyTest() {
        Cache cache = new Cache();
        Set set = new HashSet();
        set.add("String");
        set.add('c');
        set.add(null);
        set.add(1);
        set.add(1000000);
        set.add(12333.222);
        set.add(10.0f);
        set.add(10L);
        set.add(10d);
        set.add(true);
        set.add(false);
        Set[] setList = {set, null};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < setList.length; i++) {
            assertThrows(Exception e,
            		() -> cache.removeAll(setList[i]));
        }
    }

    @Test
    public void removeAllTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        assertThrows(Exception e,
        		() -> cache.removeAll());
    }

    @Test
    public void clearTest() {
        Cache cache = new Cache();
        cache.clear();
    }

    @Test
    public void getNameTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        assertThrows(Exception e,
        		() -> cache.getName());
    }

    @Test
    public void getCacheManagerTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        assertThrows(Exception e,
        		() -> cache.getCacheManager());
    }

    @Test
    public void closeTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        assertThrows(Exception e,
        		() -> cache.close());
    }

    @Test
    public void isClosedTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        assertThrows(Exception e,
        		() -> cache.isClosed());
    }

    @Test
    public void registerCacheEntryListenerTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        try {
            cache.registerCacheEntryListener(null);
        } catch (Exception e) {
            Assert.assertEquals("UnsupportedOperationException expected", unsupportedOperationException.toString(), e.toString());
        }
    }

    @Test
    public void deregisterCacheEntryListenerTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        try {
            cache.deregisterCacheEntryListener(null);
        } catch (Exception e) {
            Assert.assertEquals("UnsupportedOperationException expected", unsupportedOperationException.toString(), e.toString());
        }
    }

    @Test
    public void iteratorTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        try {
            cache.iterator();
        } catch (Exception e) {
            Assert.assertEquals("UnsupportedOperationException expected", unsupportedOperationException.toString(), e.toString());
        }
    }

    @Test
    public void unwrapTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        Class[] clazz = new Class[]{Integer.class, Character.class, String.class, Long.class, Double.class, Byte.class, Boolean.class};

        for (int i = 0; i < clazz.length; i++) {
            try {
                cache.unwrap(clazz[i]);
            } catch (Exception e) {
                Assert.assertEquals("UnsupportedOperationException expected", unsupportedOperationException.toString(), e.toString());
            }
        }
    }

    @Test
    public void invokeAllTest() {
        Cache cache = new Cache();
        Set set = new HashSet();
        set.add("String");
        set.add('c');
        set.add(null);
        set.add(1);
        set.add(1000000);
        set.add(12333.222);
        set.add(10.0f);
        set.add(10L);
        set.add(10d);
        set.add(true);
        set.add(false);
        Set[] setList = {set, null};
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (int i = 0; i < setList.length; i++) {
            assertThrows(Exception e,
            		() -> cache.invokeAll(setList[i], null, "String", "string", "str"));
        }
    }

    @Test
    public void invokeTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        assertThrows(Exception e,
        		() -> cache.invoke("key", null, "String", "string", "str"));
    }

    @Test
    public void getConfigurationTest() {
        Cache cache = new Cache();
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();
        Class[] clazz = new Class[]{Integer.class, Character.class, String.class, Long.class, Double.class, Byte.class, Boolean.class};

        for (int i = 0; i < clazz.length; i++) {
            assertThrows(Exception e,
            		() -> cache.getConfiguration(clazz[i]));
        }
    }
}