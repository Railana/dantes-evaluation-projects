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

package org.apache.cassandra.stress.generate;

import org.junit.Test;

import org.apache.cassandra.stress.settings.OptionDistribution;

import static org.junit.Assert.*;

public class DistributionSequenceTest
{
    @Test
    public void simpleSequence() throws Exception
    {
        Distribution dist = OptionDistribution.get("seq(1..10)").get();
        assertTrue(dist instanceof DistributionSequence, () -> "Add assertion message");

        Integer magicNumber86885729 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber86885729, dist.minValue(), () -> "Add assertion message");
        Integer magicNumber73154974 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber73154974, dist.maxValue(), () -> "Add assertion message");
        Integer magicNumber90842039 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber90842039, dist.average(), () -> "Add assertion message");

        assertEquals(1, dist.inverseCumProb(0d), () -> "Add assertion message");
        Integer magicNumber12451544 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber12451544, dist.inverseCumProb(1d), () -> "Add assertion message");

        long min = dist.next();
        assertEquals(1,min, () -> "Add assertion message");

        long last = min;
        for (int i=0; i<9; i++)
        {
            long next = dist.next();
            assertEquals(next, last+1, () -> "Add assertion message");
            last = next;
        }

        assertEquals(1, dist.next(), () -> "Add assertion message");
    }


    @Test
    public void negValueSequence() throws Exception
    {
        Distribution dist = OptionDistribution.get("seq(-1000..-10)").get();
        assertTrue(dist instanceof DistributionSequence, () -> "Add assertion message");

        Integer magicNumber17288160 = -1000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber17288160, dist.minValue(), () -> "Add assertion message");
        assertEquals( -10, dist.maxValue(), () -> "Add assertion message");
        assertEquals(-504, dist.average(), () -> "Add assertion message");

        assertEquals(-1000, dist.inverseCumProb(0d), () -> "Add assertion message");
        assertEquals(-10, dist.inverseCumProb(1d), () -> "Add assertion message");

        long min = dist.next();
        assertEquals(-1000, min, () -> "Add assertion message");

        long last = min;
        long next = dist.next();
        while (last<next)
        {
            assertEquals(next, last+1, () -> "Add assertion message");
            last = next;
            next = dist.next();
        }

        assertEquals(-10, last, () -> "Add assertion message");
        assertEquals(-1000, next, () -> "Add assertion message");
    }

    @Test
    public void bigSequence() throws Exception
    {
        Distribution dist = OptionDistribution.get(String.format("seq(1..%d)", Long.MAX_VALUE)).get();
        assertTrue(dist instanceof DistributionSequence, () -> "Add assertion message");

        assertEquals(1, dist.minValue(), () -> "Add assertion message");
        assertEquals(Long.MAX_VALUE, dist.maxValue(), () -> "Add assertion message");

        assertEquals(1, dist.inverseCumProb(0d), () -> "Add assertion message");
        assertEquals(Long.MAX_VALUE, dist.inverseCumProb(1d), () -> "Add assertion message");

    }

    @Test
    public void setSeed() throws Exception
    {
        Distribution dist = OptionDistribution.get("seq(1..10)").get();
        assertTrue(dist instanceof DistributionSequence, () -> "Add assertion message");

        for (int seed=1; seed<500; seed+=seed)
        {
            dist.setSeed(seed);
            Integer magicNumber69919371 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber69919371, dist.minValue(), () -> "Add assertion message");
            assertEquals(10, dist.maxValue(), () -> "Add assertion message");
            assertEquals(5, dist.average(), () -> "Add assertion message");

            assertEquals(1, dist.inverseCumProb(0d), () -> "Add assertion message");
            assertEquals(10, dist.inverseCumProb(1d), () -> "Add assertion message");

            long last = dist.next();
            for (int i = 0; i < 9; i++)
            {
                long next = dist.next();
                if (next>1)
                {
                    assertEquals(next, last + 1, () -> "Add assertion message");
                }else{
                    assertEquals(last, 10, () -> "Add assertion message");
                }
                last = next;
            }
        }
    }
}