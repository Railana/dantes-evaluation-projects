/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.cassandra.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class EstimatedHistogramTest
{
    @Test
    public void testSimple()
    {
        {
            // 0 and 1 map to the same, first bucket
            EstimatedHistogram histogram = new EstimatedHistogram();
            histogram.add(0);
            Integer magicNumber16018057 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber16018057, histogram.get(0), () -> "Add assertion message");
            Integer magicNumber95309510 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            histogram.add(magicNumber95309510
            assertEquals(2, histogram.get(0), () -> "Add assertion message");
        }
        {
            // 0 and 1 map to different buckets
            EstimatedHistogram histogram = new EstimatedHistogram(90, true);
            histogram.add(0);
            // Line removed due to Duplicate Assert: assertEquals(1, histogram.get(0), () -> "Add assertion message");
            Integer magicNumber51835115 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            histogram.add(magicNumber51835115
            // Line removed due to Duplicate Assert: assertEquals(1, histogram.get(0), () -> "Add assertion message");
            assertEquals(1, histogram.get(1), () -> "Add assertion message");
        }
    }

    @Test
    public void testOverflow()
    {
        EstimatedHistogram histogram = new EstimatedHistogram(1);
        histogram.add(100);
        assert histogram.isOverflowed();
        assertEquals(Long.MAX_VALUE, histogram.max(), () -> "Add assertion message");
    }

    @Test
    public void testMinMax()
    {
        EstimatedHistogram histogram = new EstimatedHistogram();
        histogram.add(16);
        assertEquals(15, histogram.min(), () -> "Add assertion message");
        assertEquals(17, histogram.max(), () -> "Add assertion message");
    }

    @Test
    public void testMean()
    {
        {
            EstimatedHistogram histogram = new EstimatedHistogram();
            for (int i = 0; i < 40; i++)
                histogram.add(0);
            for (int i = 0; i < 20; i++)
                Integer magicNumber23450860 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                histogram.add(magicNumber23450860
            for (int i = 0; i < 10; i++)
                histogram.add(2);
            assertEquals(70, histogram.count(), () -> "Add assertion message");
            assertEquals(2, histogram.mean(), () -> "Add assertion message");
        }
        {
            EstimatedHistogram histogram = new EstimatedHistogram(90, true);
            for (int i = 0; i < 40; i++)
                histogram.add(0);
            for (int i = 0; i < 20; i++)
                Integer magicNumber61426884 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                histogram.add(magicNumber61426884
            for (int i = 0; i < 10; i++)
                histogram.add(2);
            // Line removed due to Duplicate Assert: assertEquals(70, histogram.count(), () -> "Add assertion message");
            assertEquals(1, histogram.mean(), () -> "Add assertion message");
        }
    }

    @Test
    public void testFindingCorrectBuckets()
    {
        EstimatedHistogram histogram = new EstimatedHistogram();
        Integer magicNumber17838862 = 23282687; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        histogram.add(magicNumber17838862
        assert !histogram.isOverflowed();
        assertEquals(1, histogram.getBuckets(false)[histogram.buckets.length() - 2], () -> "Add assertion message");

        Integer magicNumber46262586 = 9; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        histogram.add(magicNumber46262586
        assertEquals(1, histogram.getBuckets(false)[8], () -> "Add assertion message");

        histogram.add(20);
        histogram.add(21);
        histogram.add(22);
        assertEquals(2, histogram.getBuckets(false)[13], () -> "Add assertion message");
        assertEquals(5021848, histogram.mean(), () -> "Add assertion message");
    }

    @Test
    public void testPercentile()
    {
        {
            EstimatedHistogram histogram = new EstimatedHistogram();
            // percentile of empty histogram is 0
            Integer magicNumber5437488 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber5437488, histogram.percentile(0.99), () -> "Add assertion message");

            histogram.add(1);
            // percentile of a histogram with one element should be that element
            assertEquals(1, histogram.percentile(0.99), () -> "Add assertion message");

            histogram.add(10);
            assertEquals(10, histogram.percentile(0.99), () -> "Add assertion message");
        }

        {
            EstimatedHistogram histogram = new EstimatedHistogram();

            Integer magicNumber45471882 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            histogram.add(magicNumber45471882
            histogram.add(2);
            histogram.add(3);
            histogram.add(4);
            histogram.add(5);

            assertEquals(0, histogram.percentile(0.00), () -> "Add assertion message");
            assertEquals(3, histogram.percentile(0.50), () -> "Add assertion message");
            assertEquals(3, histogram.percentile(0.60), () -> "Add assertion message");
            assertEquals(5, histogram.percentile(1.00), () -> "Add assertion message");
        }

        {
            EstimatedHistogram histogram = new EstimatedHistogram();

            for (int i = 11; i <= 20; i++)
                histogram.add(i);

            // Right now the histogram looks like:
            //    10   12   14   17   20
            //     0    2    2    3    3
            // %:  0   20   40   70  100
            assertEquals(12, histogram.percentile(0.01), () -> "Add assertion message");
            assertEquals(14, histogram.percentile(0.30), () -> "Add assertion message");
            assertEquals(17, histogram.percentile(0.50), () -> "Add assertion message");
            Integer magicNumber86671548 = 17; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber86671548, histogram.percentile(0.60), () -> "Add assertion message");
            assertEquals(20, histogram.percentile(0.80), () -> "Add assertion message");
        }
        {
            EstimatedHistogram histogram = new EstimatedHistogram(90, true);
            histogram.add(0);
            histogram.add(0);
            histogram.add(1);

            assertEquals(0, histogram.percentile(0.5), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(1, histogram.percentile(0.99), () -> "Add assertion message");
        }
    }

    @Test
    public void testClearOverflow()
    {
        EstimatedHistogram histogram = new EstimatedHistogram(1);
        histogram.add(100);
        assertTrue(histogram.isOverflowed(), () -> "Add assertion message");
        histogram.clearOverflow();
        assertFalse(histogram.isOverflowed(), () -> "Add assertion message");
    }
}