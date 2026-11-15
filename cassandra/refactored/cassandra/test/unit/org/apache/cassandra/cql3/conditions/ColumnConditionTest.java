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
package org.apache.cassandra.cql3.conditions;

import java.nio.ByteBuffer;
import java.util.*;

import org.junit.Assert;
import org.junit.Test;

import org.apache.cassandra.cql3.*;
import org.apache.cassandra.db.Clustering;
import org.apache.cassandra.db.marshal.Int32Type;
import org.apache.cassandra.db.marshal.ListType;
import org.apache.cassandra.db.marshal.MapType;
import org.apache.cassandra.db.marshal.SetType;
import org.apache.cassandra.db.rows.*;
import org.apache.cassandra.exceptions.InvalidRequestException;
import org.apache.cassandra.schema.ColumnMetadata;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.TimeUUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.apache.cassandra.cql3.Operator.*;
import static org.apache.cassandra.utils.ByteBufferUtil.EMPTY_BYTE_BUFFER;


public class ColumnConditionTest
{
    public static final ByteBuffer ZERO = Int32Type.instance.fromString("0");
    public static final ByteBuffer ONE = Int32Type.instance.fromString("1");
    public static final ByteBuffer TWO = Int32Type.instance.fromString("2");

    private static Row newRow(ColumnMetadata definition, ByteBuffer value)
    {
        BufferCell cell = new BufferCell(definition, 0L, Cell.NO_TTL, Cell.NO_DELETION_TIME, value, null);
        return BTreeRow.singleCellRow(Clustering.EMPTY, cell);
    }

    private static Row newRow(ColumnMetadata definition, List<ByteBuffer> values)
    {
        Row.Builder builder = BTreeRow.sortedBuilder();
        builder.newRow(Clustering.EMPTY);
        long now = System.currentTimeMillis();
        if (values != null)
        {
            for (int i = 0, m = values.size(); i < m; i++)
            {
                TimeUUID uuid = TimeUUID.Generator.atUnixMillis(now, i);
                ByteBuffer key = uuid.toBytes();
                ByteBuffer value = values.get(i);
                BufferCell cell = new BufferCell(definition,
                                                 0L,
                                                 Cell.NO_TTL,
                                                 Cell.NO_DELETION_TIME,
                                                 value,
                                                 CellPath.create(key));
                builder.addCell(cell);
            }
        }
        return builder.build();
    }

    private static Row newRow(ColumnMetadata definition, SortedSet<ByteBuffer> values)
    {
        Row.Builder builder = BTreeRow.sortedBuilder();
        builder.newRow(Clustering.EMPTY);
        if (values != null)
        {
            for (ByteBuffer value : values)
            {
                BufferCell cell = new BufferCell(definition,
                                                 0L,
                                                 Cell.NO_TTL,
                                                 Cell.NO_DELETION_TIME,
                                                 ByteBufferUtil.EMPTY_BYTE_BUFFER,
                                                 CellPath.create(value));
                builder.addCell(cell);
            }
        }
        return builder.build();
    }

    private static Row newRow(ColumnMetadata definition, Map<ByteBuffer, ByteBuffer> values)
    {
        Row.Builder builder = BTreeRow.sortedBuilder();
        builder.newRow(Clustering.EMPTY);
        if (values != null)
        {
            for (Map.Entry<ByteBuffer, ByteBuffer> entry : values.entrySet())
            {
                BufferCell cell = new BufferCell(definition,
                                                 0L,
                                                 Cell.NO_TTL,
                                                 Cell.NO_DELETION_TIME,
                                                 entry.getValue(),
                                                 CellPath.create(entry.getKey()));
                builder.addCell(cell);
            }
        }
        return builder.build();
    }

    private static boolean conditionApplies(ByteBuffer rowValue, Operator op, ByteBuffer conditionValue)
    {
        ColumnMetadata definition = ColumnMetadata.regularColumn("ks", "cf", "c", Int32Type.instance);
        ColumnCondition condition = ColumnCondition.condition(definition, op, Terms.of(new Constants.Value(conditionValue)));
        ColumnCondition.Bound bound = condition.bind(QueryOptions.DEFAULT);
        return bound.appliesTo(newRow(definition, rowValue));
    }

    private static boolean conditionApplies(List<ByteBuffer> rowValue, Operator op, List<ByteBuffer> conditionValue)
    {
        ColumnMetadata definition = ColumnMetadata.regularColumn("ks", "cf", "c", ListType.getInstance(Int32Type.instance, true));
        ColumnCondition condition = ColumnCondition.condition(definition, op, Terms.of(new Lists.Value(conditionValue)));
        ColumnCondition.Bound bound = condition.bind(QueryOptions.DEFAULT);
        return bound.appliesTo(newRow(definition, rowValue));
    }

    private static boolean conditionContainsApplies(List<ByteBuffer> rowValue, Operator op, ByteBuffer conditionValue)
    {
        ColumnMetadata definition = ColumnMetadata.regularColumn("ks", "cf", "c", ListType.getInstance(Int32Type.instance, true));
        ColumnCondition condition = ColumnCondition.condition(definition, op, Terms.of(new Constants.Value(conditionValue)));
        ColumnCondition.Bound bound = condition.bind(QueryOptions.DEFAULT);
        return bound.appliesTo(newRow(definition, rowValue));
    }

    private static boolean conditionContainsApplies(Map<ByteBuffer, ByteBuffer> rowValue, Operator op, ByteBuffer conditionValue)
    {
        ColumnMetadata definition = ColumnMetadata.regularColumn("ks", "cf", "c", MapType.getInstance(Int32Type.instance, Int32Type.instance, true));
        ColumnCondition condition = ColumnCondition.condition(definition, op, Terms.of(new Constants.Value(conditionValue)));
        ColumnCondition.Bound bound = condition.bind(QueryOptions.DEFAULT);
        return bound.appliesTo(newRow(definition, rowValue));
    }

    private static boolean conditionApplies(SortedSet<ByteBuffer> rowValue, Operator op, SortedSet<ByteBuffer> conditionValue)
    {
        ColumnMetadata definition = ColumnMetadata.regularColumn("ks", "cf", "c", SetType.getInstance(Int32Type.instance, true));
        ColumnCondition condition = ColumnCondition.condition(definition, op, Terms.of(new Sets.Value(conditionValue)));
        ColumnCondition.Bound bound = condition.bind(QueryOptions.DEFAULT);
        return bound.appliesTo(newRow(definition, rowValue));
    }

    private static boolean conditionContainsApplies(SortedSet<ByteBuffer> rowValue, Operator op, ByteBuffer conditionValue)
    {
        ColumnMetadata definition = ColumnMetadata.regularColumn("ks", "cf", "c", SetType.getInstance(Int32Type.instance, true));
        ColumnCondition condition = ColumnCondition.condition(definition, op, Terms.of(new Constants.Value(conditionValue)));
        ColumnCondition.Bound bound = condition.bind(QueryOptions.DEFAULT);
        return bound.appliesTo(newRow(definition, rowValue));
    }

    private static boolean conditionApplies(SortedMap<ByteBuffer, ByteBuffer> rowValue, Operator op, SortedMap<ByteBuffer, ByteBuffer> conditionValue)
    {
        ColumnMetadata definition = ColumnMetadata.regularColumn("ks", "cf", "c", MapType.getInstance(Int32Type.instance, Int32Type.instance, true));
        ColumnCondition condition = ColumnCondition.condition(definition, op, Terms.of(new Maps.Value(conditionValue)));
        ColumnCondition.Bound bound = condition.bind(QueryOptions.DEFAULT);
        return bound.appliesTo(newRow(definition, rowValue));
    }

    @FunctionalInterface
    public interface CheckedFunction {
        void apply();
    }

    private static void assertThrowsIRE(CheckedFunction runnable, String errorMessage)
    {
        try
        {
            runnable.apply();
            fail("Expected InvalidRequestException was not thrown");
        } catch (InvalidRequestException e)
        {
            Assert.assertTrue("Expected error message to contain '" + errorMessage + "', but got '" + e.getMessage() + "'",
                              e.getMessage().contains(errorMessage));
        }
    }

    @Test
    public void testSimpleBoundIsSatisfiedByValue() throws InvalidRequestException
    {
        // EQ
        assertTrue(conditionApplies(ONE, EQ, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(TWO, EQ, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(ONE, EQ, TWO), () -> "Add assertion message");
        assertFalse(conditionApplies(ONE, EQ, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionApplies(EMPTY_BYTE_BUFFER, EQ, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(EMPTY_BYTE_BUFFER, EQ, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionApplies(ONE, EQ, null), () -> "Add assertion message");
        assertFalse(conditionApplies(null, EQ, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies((ByteBuffer) null, EQ, (ByteBuffer) null), () -> "Add assertion message");

        // NEQ
        assertFalse(conditionApplies(ONE, NEQ, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(TWO, NEQ, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(ONE, NEQ, TWO), () -> "Add assertion message");
        assertTrue(conditionApplies(ONE, NEQ, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertTrue(conditionApplies(EMPTY_BYTE_BUFFER, NEQ, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(EMPTY_BYTE_BUFFER, NEQ, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertTrue(conditionApplies(ONE, NEQ, null), () -> "Add assertion message");
        assertTrue(conditionApplies(null, NEQ, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies((ByteBuffer) null, NEQ, (ByteBuffer) null), () -> "Add assertion message");

        // LT
        assertFalse(conditionApplies(ONE, LT, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(TWO, LT, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(ONE, LT, TWO), () -> "Add assertion message");
        assertFalse(conditionApplies(ONE, LT, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertTrue(conditionApplies(EMPTY_BYTE_BUFFER, LT, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(EMPTY_BYTE_BUFFER, LT, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertThrowsIRE(() -> conditionApplies(ONE, LT, null), "Invalid comparison with null for operator \"<\"");
        assertFalse(conditionApplies(null, LT, ONE), () -> "Add assertion message");

        // LTE
        assertTrue(conditionApplies(ONE, LTE, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(TWO, LTE, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(ONE, LTE, TWO), () -> "Add assertion message");
        assertFalse(conditionApplies(ONE, LTE, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertTrue(conditionApplies(EMPTY_BYTE_BUFFER, LTE, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(EMPTY_BYTE_BUFFER, LTE, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertThrowsIRE(() -> conditionApplies(ONE, LTE, null), "Invalid comparison with null for operator \"<=\"");
        assertFalse(conditionApplies(null, LTE, ONE), () -> "Add assertion message");

        // GT
        assertFalse(conditionApplies(ONE, GT, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(TWO, GT, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(ONE, GT, TWO), () -> "Add assertion message");
        assertTrue(conditionApplies(ONE, GT, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionApplies(EMPTY_BYTE_BUFFER, GT, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(EMPTY_BYTE_BUFFER, GT, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertThrowsIRE(() -> conditionApplies(ONE, GT, null), "Invalid comparison with null for operator \">\"");
        assertFalse(conditionApplies(null, GT, ONE), () -> "Add assertion message");

        // GTE
        assertTrue(conditionApplies(ONE, GTE, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(TWO, GTE, ONE), () -> "Add assertion message");
        assertFalse(conditionApplies(ONE, GTE, TWO), () -> "Add assertion message");
        assertTrue(conditionApplies(ONE, GTE, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionApplies(EMPTY_BYTE_BUFFER, GTE, ONE), () -> "Add assertion message");
        assertTrue(conditionApplies(EMPTY_BYTE_BUFFER, GTE, EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertThrowsIRE(() -> conditionApplies(ONE, GTE, null), "Invalid comparison with null for operator \">=\"");
        assertFalse(conditionApplies(null, GTE, ONE), () -> "Add assertion message");
    }

    private static List<ByteBuffer> list(ByteBuffer... values)
    {
        return Arrays.asList(values);
    }

    @Test
    // sets use the same check as lists
    public void testListCollectionBoundAppliesTo() throws InvalidRequestException
    {
        // EQ
        assertTrue(conditionApplies(list(ONE), EQ, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(), EQ, list()), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), EQ, list(ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ZERO), EQ, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE, ONE), EQ, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), EQ, list(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), EQ, list()), () -> "Add assertion message");
        assertFalse(conditionApplies(list(), EQ, list(ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(list(ONE), EQ, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), EQ, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), EQ, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // NEQ
        assertFalse(conditionApplies(list(ONE), NEQ, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(), NEQ, list()), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), NEQ, list(ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ZERO), NEQ, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE, ONE), NEQ, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), NEQ, list(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), NEQ, list()), () -> "Add assertion message");
        assertTrue(conditionApplies(list(), NEQ, list(ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(list(ONE), NEQ, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), NEQ, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), NEQ, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // LT
        assertFalse(conditionApplies(list(ONE), LT, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(), LT, list()), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), LT, list(ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ZERO), LT, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE, ONE), LT, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), LT, list(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), LT, list()), () -> "Add assertion message");
        assertTrue(conditionApplies(list(), LT, list(ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(list(ONE), LT, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), LT, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), LT, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // LTE
        assertTrue(conditionApplies(list(ONE), LTE, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(), LTE, list()), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), LTE, list(ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ZERO), LTE, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE, ONE), LTE, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), LTE, list(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), LTE, list()), () -> "Add assertion message");
        assertTrue(conditionApplies(list(), LTE, list(ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(list(ONE), LTE, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), LTE, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), LTE, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // GT
        assertFalse(conditionApplies(list(ONE), GT, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(), GT, list()), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), GT, list(ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ZERO), GT, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE, ONE), GT, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), GT, list(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), GT, list()), () -> "Add assertion message");
        assertFalse(conditionApplies(list(), GT, list(ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(list(ONE), GT, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), GT, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), GT, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // GTE
        assertTrue(conditionApplies(list(ONE), GTE, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(), GTE, list()), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), GTE, list(ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ZERO), GTE, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE, ONE), GTE, list(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ONE), GTE, list(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ONE), GTE, list()), () -> "Add assertion message");
        assertFalse(conditionApplies(list(), GTE, list(ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(list(ONE), GTE, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), GTE, list(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), GTE, list(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        //CONTAINS
        assertTrue(conditionContainsApplies(list(ZERO, ONE, TWO), CONTAINS, ONE), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(list(ZERO, ONE), CONTAINS, TWO), () -> "Add assertion message");

        assertFalse(conditionContainsApplies(list(ZERO, ONE, TWO), CONTAINS, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS, ONE), () -> "Add assertion message");
        assertTrue(conditionContainsApplies(list(ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
    }

    private static SortedSet<ByteBuffer> set(ByteBuffer... values)
    {
        SortedSet<ByteBuffer> results = new TreeSet<>(Int32Type.instance);
        results.addAll(Arrays.asList(values));
        return results;
    }

    @Test
    public void testSetCollectionBoundAppliesTo() throws InvalidRequestException
    {
        // EQ
        assertTrue(conditionApplies(set(ONE), EQ, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(), EQ, set()), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), EQ, set(ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ZERO), EQ, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE, TWO), EQ, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), EQ, set(ONE, TWO)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), EQ, set()), () -> "Add assertion message");
        assertFalse(conditionApplies(set(), EQ, set(ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(set(ONE), EQ, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), EQ, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), EQ, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // NEQ
        assertFalse(conditionApplies(set(ONE), NEQ, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(), NEQ, set()), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), NEQ, set(ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ZERO), NEQ, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE, TWO), NEQ, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), NEQ, set(ONE, TWO)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), NEQ, set()), () -> "Add assertion message");
        assertTrue(conditionApplies(set(), NEQ, set(ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(set(ONE), NEQ, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), NEQ, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), NEQ, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // LT
        assertFalse(conditionApplies(set(ONE), LT, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(), LT, set()), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), LT, set(ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ZERO), LT, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE, TWO), LT, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), LT, set(ONE, TWO)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), LT, set()), () -> "Add assertion message");
        assertTrue(conditionApplies(set(), LT, set(ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(set(ONE), LT, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), LT, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), LT, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // LTE
        assertTrue(conditionApplies(set(ONE), LTE, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(), LTE, set()), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), LTE, set(ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ZERO), LTE, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE, TWO), LTE, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), LTE, set(ONE, TWO)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), LTE, set()), () -> "Add assertion message");
        assertTrue(conditionApplies(set(), LTE, set(ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(set(ONE), LTE, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), LTE, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), LTE, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // GT
        assertFalse(conditionApplies(set(ONE), GT, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(), GT, set()), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), GT, set(ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ZERO), GT, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE, TWO), GT, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), GT, set(ONE, TWO)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), GT, set()), () -> "Add assertion message");
        assertFalse(conditionApplies(set(), GT, set(ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(set(ONE), GT, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), GT, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), GT, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // GTE
        assertTrue(conditionApplies(set(ONE), GTE, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(), GTE, set()), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), GTE, set(ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ZERO), GTE, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE, TWO), GTE, set(ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ONE), GTE, set(ONE, TWO)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ONE), GTE, set()), () -> "Add assertion message");
        assertFalse(conditionApplies(set(), GTE, set(ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(set(ONE), GTE, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), GTE, set(ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), GTE, set(ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // CONTAINS
        assertTrue(conditionContainsApplies(set(ZERO, ONE, TWO), CONTAINS, ONE), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(set(ZERO, ONE), CONTAINS, TWO), () -> "Add assertion message");

        assertFalse(conditionContainsApplies(set(ZERO, ONE, TWO), CONTAINS, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS, ONE), () -> "Add assertion message");
        assertTrue(conditionContainsApplies(set(ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
    }

    // values should be a list of key, value, key, value, ...
    private static SortedMap<ByteBuffer, ByteBuffer> map(ByteBuffer... values)
    {
        SortedMap<ByteBuffer, ByteBuffer> map = new TreeMap<>();
        for (int i = 0; i < values.length; i += 2)
            map.put(values[i], values[i + 1]);

        return map;
    }

    @Test
    public void testMapCollectionBoundIsSatisfiedByValue() throws InvalidRequestException
    {
        // EQ
        assertTrue(conditionApplies(map(ONE, ONE), EQ, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(), EQ, map()), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), EQ, map(ZERO, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ZERO, ONE), EQ, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), EQ, map(ONE, ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ZERO), EQ, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE, TWO, ONE), EQ, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), EQ, map(ONE, ONE, TWO, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), EQ, map()), () -> "Add assertion message");
        assertFalse(conditionApplies(map(), EQ, map(ONE, ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(map(ONE, ONE), EQ, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), EQ, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), EQ, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), EQ, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), EQ, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), EQ, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // NEQ
        assertFalse(conditionApplies(map(ONE, ONE), NEQ, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(), NEQ, map()), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), NEQ, map(ZERO, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ZERO, ONE), NEQ, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), NEQ, map(ONE, ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ZERO), NEQ, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE, TWO, ONE), NEQ, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), NEQ, map(ONE, ONE, TWO, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), NEQ, map()), () -> "Add assertion message");
        assertTrue(conditionApplies(map(), NEQ, map(ONE, ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(map(ONE, ONE), NEQ, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), NEQ, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), NEQ, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), NEQ, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), NEQ, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), NEQ, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // LT
        assertFalse(conditionApplies(map(ONE, ONE), LT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(), LT, map()), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LT, map(ZERO, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ZERO, ONE), LT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LT, map(ONE, ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ZERO), LT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE, TWO, ONE), LT, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), LT, map(ONE, ONE, TWO, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LT, map()), () -> "Add assertion message");
        assertTrue(conditionApplies(map(), LT, map(ONE, ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(map(ONE, ONE), LT, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), LT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LT, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), LT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), LT, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), LT, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // LTE
        assertTrue(conditionApplies(map(ONE, ONE), LTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(), LTE, map()), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LTE, map(ZERO, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ZERO, ONE), LTE, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LTE, map(ONE, ZERO)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ZERO), LTE, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE, TWO, ONE), LTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), LTE, map(ONE, ONE, TWO, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LTE, map()), () -> "Add assertion message");
        assertTrue(conditionApplies(map(), LTE, map(ONE, ONE)), () -> "Add assertion message");

        assertFalse(conditionApplies(map(ONE, ONE), LTE, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), LTE, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), LTE, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), LTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), LTE, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), LTE, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // GT
        assertFalse(conditionApplies(map(ONE, ONE), GT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(), GT, map()), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GT, map(ZERO, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ZERO, ONE), GT, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GT, map(ONE, ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ZERO), GT, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE, TWO, ONE), GT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), GT, map(ONE, ONE, TWO, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GT, map()), () -> "Add assertion message");
        assertFalse(conditionApplies(map(), GT, map(ONE, ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(map(ONE, ONE), GT, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), GT, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GT, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), GT, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), GT, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), GT, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        // GTE
        assertTrue(conditionApplies(map(ONE, ONE), GTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(), GTE, map()), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GTE, map(ZERO, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ZERO, ONE), GTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GTE, map(ONE, ZERO)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ZERO), GTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE, TWO, ONE), GTE, map(ONE, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ONE), GTE, map(ONE, ONE, TWO, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GTE, map()), () -> "Add assertion message");
        assertFalse(conditionApplies(map(), GTE, map(ONE, ONE)), () -> "Add assertion message");

        assertTrue(conditionApplies(map(ONE, ONE), GTE, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), GTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ONE), GTE, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");
        assertFalse(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), GTE, map(ONE, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), GTE, map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE)), () -> "Add assertion message");
        assertTrue(conditionApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), GTE, map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER)), () -> "Add assertion message");

        //CONTAINS
        assertTrue(conditionContainsApplies(map(ZERO, ONE), CONTAINS, ONE), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(map(ZERO, ONE), CONTAINS, ZERO), () -> "Add assertion message");

        assertFalse(conditionContainsApplies(map(ONE, ONE), CONTAINS, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertTrue(conditionContainsApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), CONTAINS, ONE), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), CONTAINS, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS, ONE), () -> "Add assertion message");
        assertTrue(conditionContainsApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");

        //CONTAINS KEY
        assertTrue(conditionContainsApplies(map(ZERO, ONE), CONTAINS_KEY, ZERO), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(map(ZERO, ONE), CONTAINS_KEY, ONE), () -> "Add assertion message");

        assertFalse(conditionContainsApplies(map(ONE, ONE), CONTAINS_KEY, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), CONTAINS_KEY, ONE), () -> "Add assertion message");
        assertTrue(conditionContainsApplies(map(ByteBufferUtil.EMPTY_BYTE_BUFFER, ONE), CONTAINS_KEY, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");
        assertTrue(conditionContainsApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS_KEY, ONE), () -> "Add assertion message");
        assertFalse(conditionContainsApplies(map(ONE, ByteBufferUtil.EMPTY_BYTE_BUFFER), CONTAINS_KEY, ByteBufferUtil.EMPTY_BYTE_BUFFER), () -> "Add assertion message");

    }
}