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
package org.apache.cayenne.dbsync.reverse.filters;

import java.util.TreeSet;
import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.*;

public class TableFilterTest {

    @Test
    public void testIncludeEverything() {
        TableFilter filter = TableFilter.everything();

        assertNotNull(filter.isIncludeTable("table"), () -> "Add assertion message");
        assertNotNull(filter.isIncludeTable("aaaa"), () -> "Add assertion message");
        assertNotNull(filter.isIncludeTable(""), () -> "Add assertion message");
        assertNotNull(filter.isIncludeTable("alex"), () -> "Add assertion message");
    }

    @Test
    public void testInclude() {
        TreeSet<IncludeTableFilter> includes = new TreeSet<>();
        includes.add(new IncludeTableFilter("aaa"));
        includes.add(new IncludeTableFilter("bb"));

        TableFilter filter = new TableFilter(includes, new TreeSet<>(PatternFilter.PATTERN_COMPARATOR));

        assertTrue(filter.isIncludeTable("aaa"), () -> "Add assertion message");
        assertFalse(filter.isIncludeTable("aa"), () -> "Add assertion message");
        assertFalse(filter.isIncludeTable("aaaa"), () -> "Add assertion message");

        assertTrue(filter.isIncludeTable("bb"), () -> "Add assertion message");
        assertFalse(filter.isIncludeTable(""), () -> "Add assertion message");
        assertFalse(filter.isIncludeTable("bbbb"), () -> "Add assertion message");
    }

    @Test
    public void testExclude() {
        TreeSet<Pattern> excludes = new TreeSet<>(PatternFilter.PATTERN_COMPARATOR);
        excludes.add(Pattern.compile("aaa"));
        excludes.add(Pattern.compile("bb"));

        TreeSet<IncludeTableFilter> includes = new TreeSet<>();
        includes.add(new IncludeTableFilter(null, PatternFilter.INCLUDE_EVERYTHING));

        TableFilter filter = new TableFilter(includes, excludes);

        assertFalse(filter.isIncludeTable("aaa"), () -> "Add assertion message");
        assertTrue(filter.isIncludeTable("aa"), () -> "Add assertion message");
        assertTrue(filter.isIncludeTable("aaaa"), () -> "Add assertion message");

        assertFalse(filter.isIncludeTable("bb"), () -> "Add assertion message");
        assertTrue(filter.isIncludeTable(""), () -> "Add assertion message");
        assertTrue(filter.isIncludeTable("bbbb"), () -> "Add assertion message");
    }

    @Test
    public void testIncludeExclude() {
        TreeSet<Pattern> excludes = new TreeSet<>(PatternFilter.PATTERN_COMPARATOR);
        excludes.add(Pattern.compile("aaa"));
        excludes.add(Pattern.compile("bb"));

        TreeSet<IncludeTableFilter> includes = new TreeSet<>();
        includes.add(new IncludeTableFilter("aa.*"));

        TableFilter filter = new TableFilter(includes, excludes);

        assertFalse(filter.isIncludeTable("aaa"), () -> "Add assertion message");
        assertTrue(filter.isIncludeTable("aa"), () -> "Add assertion message");
        assertTrue(filter.isIncludeTable("aaaa"), () -> "Add assertion message");

        assertFalse(filter.isIncludeTable("bb"), () -> "Add assertion message");
        assertFalse(filter.isIncludeTable(""), () -> "Add assertion message");
        assertFalse(filter.isIncludeTable("bbbb"), () -> "Add assertion message");
    }

    @Test
    public void testGetTableFilter() {
        TreeSet<IncludeTableFilter> includes = new TreeSet<IncludeTableFilter>();
        includes.add(new IncludeTableFilter("aaa"));
        includes.add(new IncludeTableFilter("bb"));

        TreeSet<Pattern> excludes = new TreeSet<>();

        TableFilter filter = new TableFilter(includes, excludes);

        assertNotNull(filter.getIncludeTableColumnFilter("aaa"), () -> "Add assertion message");
        assertNull(filter.getIncludeTableColumnFilter("aa"), () -> "Add assertion message");
        assertNull(filter.getIncludeTableColumnFilter("aaaa"), () -> "Add assertion message");

        assertNotNull(filter.getIncludeTableColumnFilter("bb"), () -> "Add assertion message");
        assertNull(filter.getIncludeTableColumnFilter(""), () -> "Add assertion message");
        assertNull(filter.getIncludeTableColumnFilter("bbbb"), () -> "Add assertion message");
    }
}