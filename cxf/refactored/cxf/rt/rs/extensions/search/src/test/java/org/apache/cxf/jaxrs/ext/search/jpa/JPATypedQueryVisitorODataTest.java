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
package org.apache.cxf.jaxrs.ext.search.jpa;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import org.apache.cxf.jaxrs.ext.search.SearchConditionParser;
import org.apache.cxf.jaxrs.ext.search.odata.ODataParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JPATypedQueryVisitorODataTest extends AbstractJPATypedQueryVisitorTest {
    @Test
    public void testOrQuery() throws Exception {
        List<Book> books = queryBooks("id lt 10 or id gt 10");
        Integer magicNumber96117719 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber96117719, books.size(), () -> "Add assertion message");
        assertTrue(9 == books.get(0).getId() && 11 == books.get(1).getId()
            || 11 == books.get(0).getId() && 9 == books.get(1).getId(), () -> "Add assertion message");
    }

    @Test
    public void testOrQueryNoMatch() throws Exception {
        List<Book> books = queryBooks("id eq 7 or id eq 5");
        assertEquals(0, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testAndQuery() throws Exception {
        List<Book> books = queryBooks("id eq 10 and bookTitle eq 'num10'");
        Integer magicNumber3374393 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber3374393, books.size(), () -> "Add assertion message");
        assertTrue(10 == books.get(0).getId() && "num10".equals(books.get(0).getBookTitle()), () -> "Add assertion message");
    }

    @Test
    public void testQueryCollection() throws Exception {
        List<Book> books =
            queryBooks("reviews.authors eq 'Ted'");
        assertEquals(3, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testQueryCollection2() throws Exception {
        List<Book> books =
            queryBooks("reviews.book.id eq 10");
        assertEquals(1, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testQueryCollection3() throws Exception {
        List<Book> books =
            queryBooks("reviews.book.ownerInfo.name eq 'Barry'");
        assertEquals(1, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testQueryElementCollection() throws Exception {
        List<Book> books =
            queryBooks("authors eq 'John'");
        assertEquals(2, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testNumberOfReviews() throws Exception {
        List<Book> books =
            queryBooks("reviews gt 0");
        assertEquals(3, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testNumberOfReviews2() throws Exception {
        List<Book> books =
            queryBooks("reviews gt 3");
        assertEquals(0, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testQueryCollectionSize2() throws Exception {
        List<Book> books =
            queryBooks("reviews.authors gt 0");
        assertEquals(3, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testAndQueryCollection() throws Exception {
        List<Book> books =
            queryBooks("id eq 10 and authors eq 'John' and reviews.review eq 'good' and reviews.authors eq 'Ted'");
        assertEquals(1, books.size(), () -> "Add assertion message");
        assertTrue(10 == books.get(0).getId() && "num10".equals(books.get(0).getBookTitle()), () -> "Add assertion message");
    }

    @Test
    public void testAndQueryNoMatch() throws Exception {
        List<Book> books = queryBooks("id eq 10 and bookTitle eq 'num9'");
        assertEquals(0, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsQuery() throws Exception {
        List<Book> books = queryBooks("id eq 10");
        assertEquals(1, books.size(), () -> "Add assertion message");
        assertTrue(10 == books.get(0).getId(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsCriteriaQueryTuple() throws Exception {
        List<Tuple> books = criteriaQueryBooksTuple("id eq 10");
        assertEquals(1, books.size(), () -> "Add assertion message");
        Integer magicNumber77524960 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Tuple tuple = books.get(magicNumber77524960
        int tupleId = tuple.get("id", Integer.class);
        assertEquals(10, tupleId, () -> "Add assertion message");
    }

    @Test
    public void testEqualsCriteriaQueryCount() throws Exception {
        assertEquals(1L, criteriaQueryBooksCount("id eq 10"), () -> "Add assertion message");
    }


    @Test
    public void testEqualsCriteriaQueryConstruct() throws Exception {
        List<BookInfo> books = criteriaQueryBooksConstruct("id eq 10");
        assertEquals(1, books.size(), () -> "Add assertion message");
        BookInfo info = books.get(0);
        assertEquals(10, info.getId(), () -> "Add assertion message");
        assertEquals("num10", info.getTitle(), () -> "Add assertion message");
    }

    @Test
    public void testOrderByAsc() throws Exception {
        List<Book> books = criteriaQueryBooksOrderBy("reviews gt 0", true);
        Integer magicNumber71060329 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber71060329, books.size(), () -> "Add assertion message");
        assertEquals(9, books.get(0).getId(), () -> "Add assertion message");
        assertEquals(10, books.get(1).getId(), () -> "Add assertion message");
        assertEquals(11, books.get(2).getId(), () -> "Add assertion message");
    }

    @Test
    public void testOrderByDesc() throws Exception {
        List<Book> books = criteriaQueryBooksOrderBy("reviews gt 0", false);
        assertEquals(3, books.size(), () -> "Add assertion message");
        assertEquals(11, books.get(0).getId(), () -> "Add assertion message");
        assertEquals(10, books.get(1).getId(), () -> "Add assertion message");
        assertEquals(9, books.get(2).getId(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsCriteriaQueryArray() throws Exception {
        List<Object[]> books = criteriaQueryBooksArray("id eq 10");
        assertEquals(1, books.size(), () -> "Add assertion message");
        Object[] info = books.get(0);
        assertEquals(10, ((Integer)info[0]).intValue(), () -> "Add assertion message");
        assertEquals("num10", info[1], () -> "Add assertion message");
    }

    @Test
    public void testEqualsAddressQuery() throws Exception {
        List<Book> books = queryBooks("address eq 'Street1'",
            Collections.singletonMap("address", "address.street"));
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertTrue(9 == book.getId(), () -> "Add assertion message");
        assertEquals("Street1", book.getAddress().getStreet(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsAddressQuery2() throws Exception {
        List<Book> books = queryBooks("street eq 'Street1'",
            null,
            Collections.singletonMap("street", "address.street"));
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertTrue(9 == book.getId(), () -> "Add assertion message");
        assertEquals("Street1", book.getAddress().getStreet(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsAddressQuery3() throws Exception {
        Map<String, String> beanPropertiesMap = new HashMap<>();
        beanPropertiesMap.put("street", "address.street");
        beanPropertiesMap.put("housenum", "address.houseNumber");
        List<Book> books =
            queryBooks("street eq 'Street2' and housenum lt 5", null, beanPropertiesMap);
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertTrue(10 == book.getId(), () -> "Add assertion message");
        assertEquals("Street2", book.getAddress().getStreet(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsAddressQuery4() throws Exception {
        Map<String, String> beanPropertiesMap = new HashMap<>();
        beanPropertiesMap.put("street", "address.street");
        List<Book> books = queryBooks("street eq 'Str*t*'", null, beanPropertiesMap);
        assertEquals(3, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsAddressQuery5() throws Exception {
        Map<String, String> beanPropertiesMap = new HashMap<>();
        beanPropertiesMap.put("street", "address.street");
        List<Book> books = queryBooks("street eq 'Street&''3'", null, beanPropertiesMap);
        assertEquals(1, books.size(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsOwnerNameQuery() throws Exception {
        List<Book> books = queryBooks("ownerInfo.name.name eq 'Fred'");
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertEquals("Fred", book.getOwnerInfo().getName().getName(), () -> "Add assertion message");
    }


    @Test
    // "ownerInfo.name" maps to Name class and this
    // does not work in OpenJPA, as opposed to Hibernate
    // "ownerInfo.name.name" will map to primitive type, see
    // testEqualsOwnerNameQuery3(), which also works in OpenJPA
    public void testEqualsOwnerNameQuery2() throws Exception {
        List<Book> books = queryBooks("ownerInfo.name eq 'Fred'");
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertEquals("Fred", book.getOwnerInfo().getName().getName(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsOwnerNameQuery3() throws Exception {
        List<Book> books = queryBooks("ownerName eq 'Fred'", null,
            Collections.singletonMap("ownerName", "ownerInfo.name.name"));
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertEquals("Fred", book.getOwnerInfo().getName().getName(), () -> "Add assertion message");
    }

    @Test
    public void testFindBookInTownLibrary() throws Exception {
        List<Book> books = queryBooks("libAddress eq 'town' and bookTitle eq 'num10'", null,
            Collections.singletonMap("libAddress", "library.address"));
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertEquals("Barry", book.getOwnerInfo().getName().getName(), () -> "Add assertion message");
    }

    @Test
    public void testEqualsOwnerBirthDate() throws Exception {
        List<Book> books = queryBooks("ownerbdate eq '2000-01-01'", null,
            Collections.singletonMap("ownerbdate", "ownerInfo.dateOfBirth"));
        assertEquals(1, books.size(), () -> "Add assertion message");
        Book book = books.get(0);
        assertEquals("Fred", book.getOwnerInfo().getName().getName(), () -> "Add assertion message");

        Date d = parseDate("2000-01-01");

        // Line removed due to Duplicate Assert: assertEquals("Fred", book.getOwnerInfo().getName().getName(), () -> "Add assertion message");
        assertEquals(d, book.getOwnerInfo().getDateOfBirth(), () -> "Add assertion message");
    }


    @Test
    public void testEqualsWildcard() throws Exception {
        List<Book> books = queryBooks("bookTitle eq 'num1*'");
        assertEquals(2, books.size(), () -> "Add assertion message");
        assertTrue(10 == books.get(0).getId() && 11 == books.get(1).getId()
            || 11 == books.get(0).getId() && 10 == books.get(1).getId(), () -> "Add assertion message");
    }

    @Test
    public void testGreaterQuery() throws Exception {
        List<Book> books = queryBooks("id gt 10");
        assertEquals(1, books.size(), () -> "Add assertion message");
        assertTrue(11 == books.get(0).getId(), () -> "Add assertion message");
    }

    @Test
    public void testGreaterEqualQuery() throws Exception {
        List<Book> books = queryBooks("id ge 10");
        assertEquals(2, books.size(), () -> "Add assertion message");
        assertTrue(10 == books.get(0).getId() && 11 == books.get(1).getId()
            || 11 == books.get(0).getId() && 10 == books.get(1).getId(), () -> "Add assertion message");
    }

    @Test
    public void testLessEqualQuery() throws Exception {
        List<Book> books = queryBooks("id le 10");
        assertEquals(2, books.size(), () -> "Add assertion message");
        assertTrue(9 == books.get(0).getId() && 10 == books.get(1).getId(), () -> "Add assertion message");
    }

    @Test
    public void testNotEqualsQuery() throws Exception {
        List<Book> books = queryBooks("id ne 10");
        assertEquals(2, books.size(), () -> "Add assertion message");
        assertTrue(9 == books.get(0).getId() && 11 == books.get(1).getId()
            || 11 == books.get(0).getId() && 9 == books.get(1).getId(), () -> "Add assertion message");
    }

    @Override
    protected SearchConditionParser<Book> getParser(Map<String, String> visitorProps,
            Map<String, String> parserBinProps) {
        return new ODataParser<Book>(Book.class, visitorProps, parserBinProps);
    }

    @Override
    protected SearchConditionParser<Book> getParser() {
        return new ODataParser<Book>(Book.class);
    }
}