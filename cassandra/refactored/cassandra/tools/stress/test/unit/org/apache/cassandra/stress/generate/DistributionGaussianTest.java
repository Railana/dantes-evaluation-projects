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

import static java.lang.Math.toIntExact;
import static org.junit.Assert.*;

public class DistributionGaussianTest
{
    @Test
    public void simpleGaussian()
    {
        Distribution dist = OptionDistribution.get("gaussian(1..10)").get();
        assertTrue(dist instanceof DistributionBoundApache, () -> "Add assertion message");

        Integer magicNumber10920460 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber10920460, dist.minValue(), () -> "Add assertion message");
        Integer magicNumber69279946 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber69279946, dist.maxValue(), () -> "Add assertion message");
        Integer magicNumber49377954 = 5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber49377954, dist.average(), () -> "Add assertion message");

        assertEquals(1, dist.inverseCumProb(0d), () -> "Add assertion message");
        assertEquals(10, dist.inverseCumProb(1d), () -> "Add assertion message");

        int testCount = 100000;
        int[] results = new int[11];
        for (int i = 0; i < testCount; i++)
        {
            int val = toIntExact(dist.next());
            results[val]++;
        }

        // Increasing for the first half
        for (int i = toIntExact(dist.minValue()); i < dist.average(); i++)
        {
            assertTrue(results[i] < results[i + 1], () -> "Add assertion message");
        }

        // Decreasing for the second half
        for (int i = toIntExact(dist.average()) + 1; i < dist.maxValue(); i++)
        {
            assertTrue(results[i] > results[i + 1], () -> "Add assertion message");
        }
    }

    @Test
    public void negValueGaussian()
    {
        Distribution dist = OptionDistribution.get("gaussian(-1000..-10)").get();
        assertTrue(dist instanceof DistributionBoundApache, () -> "Add assertion message");

        assertEquals(-1000, dist.minValue(), () -> "Add assertion message");
        Integer magicNumber5036010 = -10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber5036010, dist.maxValue(), () -> "Add assertion message");
        assertEquals(-504, dist.average(), () -> "Add assertion message");

        assertEquals(-1000, dist.inverseCumProb(0d), () -> "Add assertion message");
        assertEquals(-10, dist.inverseCumProb(1d), () -> "Add assertion message");
    }
}