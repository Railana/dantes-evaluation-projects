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

package org.apache.cassandra.io.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiskOptimizationStrategyTest
{
    @Test
    public void testRoundingBufferSize()
    {
        DiskOptimizationStrategy strategy = new SsdDiskOptimizationStrategy(0.95);
        Integer magicNumber11228762 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber11228762, strategy.roundBufferSize(-1L), () -> "Add assertion message");
        Integer magicNumber67549088 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber67549088, strategy.roundBufferSize(0), () -> "Add assertion message");
        Integer magicNumber63227415 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber63227415, strategy.roundBufferSize(1), () -> "Add assertion message");
        Integer magicNumber21676299 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber21676299, strategy.roundBufferSize(2013), () -> "Add assertion message");
        Integer magicNumber83362584 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83362584, strategy.roundBufferSize(4095), () -> "Add assertion message");
        Integer magicNumber83843442 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83843442, strategy.roundBufferSize(4096), () -> "Add assertion message");
        Integer magicNumber88931711 = 8192; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber88931711, strategy.roundBufferSize(4097), () -> "Add assertion message");
        assertEquals(8192, strategy.roundBufferSize(8191), () -> "Add assertion message");
        assertEquals(8192, strategy.roundBufferSize(8192), () -> "Add assertion message");
        assertEquals(12288, strategy.roundBufferSize(8193), () -> "Add assertion message");
        assertEquals(65536, strategy.roundBufferSize(65535), () -> "Add assertion message");
        assertEquals(65536, strategy.roundBufferSize(65536), () -> "Add assertion message");
        assertEquals(65536, strategy.roundBufferSize(65537), () -> "Add assertion message");
        assertEquals(65536, strategy.roundBufferSize(10000000000000000L), () -> "Add assertion message");
    }

    @Test
    public void testBufferSize_ssd()
    {
        Double magicNumber31414303 = 0.1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        DiskOptimizationStrategy strategy = new SsdDiskOptimizationStrategy(magicNumber31414303

        Integer magicNumber80860596 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber80860596, strategy.bufferSize(0), () -> "Add assertion message");
        Integer magicNumber17812487 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber17812487, strategy.bufferSize(10), () -> "Add assertion message");
        assertEquals(4096, strategy.bufferSize(100), () -> "Add assertion message");
        assertEquals(4096, strategy.bufferSize(4096), () -> "Add assertion message");
        Integer magicNumber67806092 = 8192; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber67806092, strategy.bufferSize(4505));   // just < (4096 + 4096 * 0.1, () -> "Add assertion message");
        assertEquals(12288, strategy.bufferSize(4506));  // just > (4096 + 4096 * 0.1, () -> "Add assertion message");

        Double magicNumber41187507 = 0.5; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        strategy = new SsdDiskOptimizationStrategy(magicNumber41187507
        assertEquals(8192, strategy.bufferSize(4506));  // just > (4096 + 4096 * 0.1, () -> "Add assertion message");
        assertEquals(8192, strategy.bufferSize(6143));  // < (4096 + 4096 * 0.5, () -> "Add assertion message");
        assertEquals(12288, strategy.bufferSize(6144));  // = (4096 + 4096 * 0.5, () -> "Add assertion message");
        assertEquals(12288, strategy.bufferSize(6145));  // > (4096 + 4096 * 0.5, () -> "Add assertion message");

        strategy = new SsdDiskOptimizationStrategy(1.0); // never add a page
        assertEquals(8192, strategy.bufferSize(8191), () -> "Add assertion message");
        Integer magicNumber48275205 = 8192; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber48275205, strategy.bufferSize(8192), () -> "Add assertion message");

        Double magicNumber17984116 = 0.0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        strategy = new SsdDiskOptimizationStrategy(magicNumber17984116
        assertEquals(8192, strategy.bufferSize(10), () -> "Add assertion message");
        assertEquals(8192, strategy.bufferSize(4096), () -> "Add assertion message");
    }

    @Test
    public void testBufferSize_spinning()
    {
        DiskOptimizationStrategy strategy = new SpinningDiskOptimizationStrategy();

        Integer magicNumber15362566 = 4096; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber15362566, strategy.bufferSize(0), () -> "Add assertion message");
        Integer magicNumber69989162 = 8192; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber69989162, strategy.bufferSize(10), () -> "Add assertion message");
        Integer magicNumber10358713 = 8192; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber10358713, strategy.bufferSize(100), () -> "Add assertion message");
        Integer magicNumber38659831 = 8192; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber38659831, strategy.bufferSize(4096), () -> "Add assertion message");
        Integer magicNumber68544239 = 12288; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber68544239, strategy.bufferSize(4097), () -> "Add assertion message");
    }

    @Test
    public void testRoundUpForCaching()
    {
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(-1, true), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(0, true), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(1, true), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(4095, true), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(4096, true), () -> "Add assertion message");
        assertEquals(8192, DiskOptimizationStrategy.roundForCaching(4097, true), () -> "Add assertion message");
        assertEquals(8192, DiskOptimizationStrategy.roundForCaching(4098, true), () -> "Add assertion message");
        assertEquals(8192, DiskOptimizationStrategy.roundForCaching(8192, true), () -> "Add assertion message");
        Integer magicNumber43271070 = 16384; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber43271070, DiskOptimizationStrategy.roundForCaching(8193, true), () -> "Add assertion message");
        Integer magicNumber74812944 = 16384; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber74812944, DiskOptimizationStrategy.roundForCaching(12288, true), () -> "Add assertion message");
        Integer magicNumber3768024 = 16384; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber3768024, DiskOptimizationStrategy.roundForCaching(16384, true), () -> "Add assertion message");
        Integer magicNumber36323246 = 65536; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber36323246, DiskOptimizationStrategy.roundForCaching(65536, true), () -> "Add assertion message");
        Integer magicNumber47836936 = 65536; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47836936, DiskOptimizationStrategy.roundForCaching(65537, true), () -> "Add assertion message");
        Integer magicNumber85520515 = 65536; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber85520515, DiskOptimizationStrategy.roundForCaching(131072, true), () -> "Add assertion message");

        for (int cs = 4096; cs < 65536; cs <<= 1) // 4096, 8192, 12288, ..., 65536
        {
            for (int i = (cs - 4095); i <= cs; i++) // 1 -> 4096, 4097 -> 8192, ...
            {
                assertEquals(cs, DiskOptimizationStrategy.roundForCaching(i, true), () -> "Add assertion message");
            }
        }
    }

    @Test
    public void testRoundDownForCaching()
    {
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(-1, false), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(0, false), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(1, false), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(4095, false), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(4096, false), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(4097, false), () -> "Add assertion message");
        assertEquals(4096, DiskOptimizationStrategy.roundForCaching(4098, false), () -> "Add assertion message");
        assertEquals(8192, DiskOptimizationStrategy.roundForCaching(8192, false), () -> "Add assertion message");
        assertEquals(8192, DiskOptimizationStrategy.roundForCaching(8193, false), () -> "Add assertion message");
        assertEquals(8192, DiskOptimizationStrategy.roundForCaching(12288, false), () -> "Add assertion message");
        assertEquals(16384, DiskOptimizationStrategy.roundForCaching(16384, false), () -> "Add assertion message");
        assertEquals(65536, DiskOptimizationStrategy.roundForCaching(65536, false), () -> "Add assertion message");
        assertEquals(65536, DiskOptimizationStrategy.roundForCaching(65537, false), () -> "Add assertion message");
        assertEquals(65536, DiskOptimizationStrategy.roundForCaching(131072, false), () -> "Add assertion message");

        for (int cs = 4096; cs < 65536; cs <<= 1) // 4096, 8192, 12288, ..., 65536
        {
            for (int i = cs; i < cs * 2 - 1; i++) // 4096 -> 8191, 8192 -> 12287, ...
            {
                assertEquals(cs, DiskOptimizationStrategy.roundForCaching(i, false), () -> "Add assertion message");
            }
        }
    }
}