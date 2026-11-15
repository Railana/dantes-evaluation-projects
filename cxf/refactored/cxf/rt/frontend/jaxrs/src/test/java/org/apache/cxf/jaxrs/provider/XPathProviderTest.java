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
package org.apache.cxf.jaxrs.provider;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.jaxrs.resources.Book;
import org.apache.cxf.jaxrs.resources.BookStore;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XPathProviderTest {

    @Test
    public void testIsReadableClassName() {
        XPathProvider<?> provider = new XPathProvider<>();
        provider.setExpression("/");
        assertTrue(provider.isReadable(Book.class, null, null, null), () -> "Add assertion message");
        assertTrue(provider.isReadable(BookStore.class, null, null, null), () -> "Add assertion message");
        provider.setClassName(Book.class.getName());
        assertFalse(provider.isReadable(BookStore.class, null, null, null), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(provider.isReadable(Book.class, null, null, null), () -> "Add assertion message");
        provider.setClassName(null);
        // Line removed due to Duplicate Assert: assertTrue(provider.isReadable(Book.class, null, null, null), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(provider.isReadable(BookStore.class, null, null, null), () -> "Add assertion message");
    }

    @Test
    public void testIsReadableClassNames() {
        XPathProvider<?> provider = new XPathProvider<>();
        assertFalse(provider.isReadable(Book.class, null, null, null), () -> "Add assertion message");
        assertFalse(provider.isReadable(BookStore.class, null, null, null), () -> "Add assertion message");
        Map<String, String> map = new HashMap<>();
        map.put(Book.class.getName(), "/");
        provider.setExpressions(map);
        // Line removed due to Duplicate Assert: assertFalse(provider.isReadable(BookStore.class, null, null, null), () -> "Add assertion message");
        assertTrue(provider.isReadable(Book.class, null, null, null), () -> "Add assertion message");
    }

    @Test
    public void testReadFrom() throws Exception {
        String value = "<Book><name>The Book</name><id>2</id></Book>";
        XPathProvider<Book> provider = new XPathProvider<>();
        provider.setExpression("/Book");
        provider.setClassName(Book.class.getName());
        provider.setForceDOM(true);
        Book book = provider.readFrom(Book.class, null, null, null, null,
                          new ByteArrayInputStream(value.getBytes()));
        assertNotNull(book, () -> "Add assertion message");
        assertEquals(2L, book.getId(), () -> "Add assertion message");
        assertEquals("The Book", book.getName(), () -> "Add assertion message");
    }

}