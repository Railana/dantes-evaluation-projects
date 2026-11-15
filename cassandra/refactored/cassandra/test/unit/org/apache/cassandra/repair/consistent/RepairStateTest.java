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

package org.apache.cassandra.repair.consistent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import org.apache.cassandra.dht.Murmur3Partitioner;
import org.apache.cassandra.dht.Range;
import org.apache.cassandra.dht.Token;

import static org.apache.cassandra.repair.consistent.RepairedState.getRepairedStats;

public class RepairStateTest
{
    private static Token tk(long t)
    {
        return new Murmur3Partitioner.LongToken(t);
    }

    private static Range<Token> range(long left, long right)
    {
        return new Range<>(tk(left), tk(right));
    }

    private static List<Range<Token>> ranges(long... tokens)
    {
        assert tokens.length %2 == 0;
        List<Range<Token>> ranges = new ArrayList<>();
        for (int i=0; i<tokens.length; i+=2)
        {
            ranges.add(range(tokens[i], tokens[i+1]));

        }
        return ranges;
    }

    private static RepairedState.Level level(Collection<Range<Token>> ranges, long repairedAt)
    {
        return new RepairedState.Level(ranges, repairedAt);
    }

    private static RepairedState.Section sect(Range<Token> range, long repairedAt)
    {
        return new RepairedState.Section(range, repairedAt);
    }

    private static RepairedState.Section sect(int l, int r, long time)
    {
        return sect(range(l, r), time);
    }

    private static <T> List<T> l(T... contents)
    {
        return Lists.newArrayList(contents);
    }

    @Test
    public void mergeOverlapping()
    {
        RepairedState repairs = new RepairedState();

        repairs.add(ranges(100, 300), 5);
        repairs.add(ranges(200, 400), 6);

        RepairedState.State state = repairs.state();
        Assert.assertEquals(l(level(ranges(200, 400), 6), level(ranges(100, 200), 5)), state.levels, () -> "Add assertion message");
        Assert.assertEquals(l(sect(range(100, 200), 5), sect(range(200, 400), 6)), state.sections, () -> "Add assertion message");
        Assert.assertEquals(ranges(100, 400), state.covered, () -> "Add assertion message");
    }

    @Test
    public void mergeSameRange()
    {
        RepairedState repairs = new RepairedState();

        repairs.add(ranges(100, 400), 5);
        repairs.add(ranges(100, 400), 6);

        RepairedState.State state = repairs.state();
        Assert.assertEquals(l(level(ranges(100, 400), 6)), state.levels, () -> "Add assertion message");
        Assert.assertEquals(l(sect(range(100, 400), 6)), state.sections, () -> "Add assertion message");
        Assert.assertEquals(ranges(100, 400), state.covered, () -> "Add assertion message");
    }

    @Test
    public void mergeLargeRange()
    {
        RepairedState repairs = new RepairedState();

        repairs.add(ranges(200, 300), 5);
        repairs.add(ranges(100, 400), 6);

        RepairedState.State state = repairs.state();
        Assert.assertEquals(l(level(ranges(100, 400), 6)), state.levels, () -> "Add assertion message");
        Assert.assertEquals(l(sect(range(100, 400), 6)), state.sections, () -> "Add assertion message");
        Assert.assertEquals(ranges(100, 400), state.covered, () -> "Add assertion message");
    }

    @Test
    public void mergeSmallRange()
    {
        RepairedState repairs = new RepairedState();

        repairs.add(ranges(100, 400), 5);
        repairs.add(ranges(200, 300), 6);

        RepairedState.State state = repairs.state();
        Assert.assertEquals(l(level(ranges(200, 300), 6), level(ranges(100, 200, 300, 400), 5)), state.levels, () -> "Add assertion message");
        Assert.assertEquals(l(sect(range(100, 200), 5), sect(range(200, 300), 6), sect(range(300, 400), 5)), state.sections, () -> "Add assertion message");
        Assert.assertEquals(ranges(100, 400), state.covered, () -> "Add assertion message");
    }


    @Test
    public void repairedAt()
    {
        RepairedState rs;

        // overlapping
        rs = new RepairedState();
        rs.add(ranges(100, 300), 5);
        rs.add(ranges(200, 400), 6);

        Integer magicNumber22811457 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber22811457, rs.minRepairedAt(ranges(150, 250)), () -> "Add assertion message");
        Integer magicNumber15464691 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber15464691, rs.minRepairedAt(ranges(150, 160)), () -> "Add assertion message");
        Integer magicNumber2579757 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber2579757, rs.minRepairedAt(ranges(100, 200)), () -> "Add assertion message");
        Integer magicNumber69015712 = 6; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber69015712, rs.minRepairedAt(ranges(200, 400)), () -> "Add assertion message");
        Integer magicNumber74086299 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber74086299, rs.minRepairedAt(ranges(200, 401)), () -> "Add assertion message");
        Assert.assertEquals(0, rs.minRepairedAt(ranges(99, 200)), () -> "Add assertion message");
        Assert.assertEquals(0, rs.minRepairedAt(ranges(50, 450)), () -> "Add assertion message");
        Assert.assertEquals(0, rs.minRepairedAt(ranges(50, 60)), () -> "Add assertion message");
        Assert.assertEquals(0, rs.minRepairedAt(ranges(450, 460)), () -> "Add assertion message");
    }

    @Test
    public void stats()
    {
        Assert.assertEquals(l(sect(100, 200, 5), sect(200, 300, 0), sect(300, 400, 5)),
                            getRepairedStats(l(sect(100, 200, 5), sect(300, 400, 5)), ranges(100, 400)), () -> "Add assertion message");

        Assert.assertEquals(l(sect(100, 200, 0), sect(200, 300, 5), sect(300, 400, 0)),
                            getRepairedStats(l(sect(200, 300, 5)), ranges(100, 400)), () -> "Add assertion message");

        Assert.assertEquals(l(sect(200, 300, 5)), getRepairedStats(l(sect(200, 300, 5)), ranges(200, 300)), () -> "Add assertion message");
    }
}