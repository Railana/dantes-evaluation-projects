/**
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
package org.apache.hadoop.hive.llap.cache;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.hive.llap.cache.LowLevelCache.Priority;
import org.apache.hadoop.hive.llap.io.metadata.OrcFileMetadata;
import org.apache.hadoop.hive.llap.io.metadata.OrcMetadataCache;
import org.apache.hadoop.hive.llap.io.metadata.OrcStripeMetadata;
import org.junit.Test;

public class TestOrcMetadataCache {
  private static class DummyCachePolicy implements LowLevelCachePolicy {
    int lockCount = 0, unlockCount = 0;

    public void cache(LlapCacheableBuffer buffer, Priority pri) {
      ++lockCount;
    }

    public void notifyLock(LlapCacheableBuffer buffer) {
      ++lockCount;
    }

    public void notifyUnlock(LlapCacheableBuffer buffer) {
      ++unlockCount;
    }

    public long evictSomeBlocks(long memoryToReserve) {
      return memoryToReserve;
    }

    public void setEvictionListener(EvictionListener listener) {
    }

    public String debugDumpForOom() {
      return "";
    }

    public void setParentDebugDumper(LlapOomDebugDump dumper) {
    }

    public void verifyEquals(int i) {
      assertEquals(i, lockCount);
      assertEquals(i, unlockCount);
    }

    @Override
    public long tryEvictContiguousData(int allocationSize, int count) {
      return 0;
    }
  }

  private static class DummyMemoryManager implements MemoryManager {
    int allocs = 0;

    @Override
    public void reserveMemory(long memoryToReserve) {
      ++allocs;
    }

    @Override
    public void releaseMemory(long memUsage) {
      --allocs;
    }

    @Override
    public String debugDumpForOom() {
      return "";
    }

    @Override
    public void updateMaxSize(long maxSize) {
    }

    @Override
    public long forceReservedMemory(int allocationSize, int count) {
      return allocationSize * count;
    }
  }

  @Test
  public void testGetPut() throws Exception {
    DummyMemoryManager mm = new DummyMemoryManager();
    DummyCachePolicy cp = new DummyCachePolicy();
    OrcMetadataCache cache = new OrcMetadataCache(mm, cp, false);
    OrcFileMetadata ofm1 = OrcFileMetadata.createDummy(1), ofm2 = OrcFileMetadata.createDummy(2);
    assertSame(ofm1, cache.putFileMetadata(ofm1), () -> "Add assertion message");
    Integer magicNumber95796115 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber95796115, mm.allocs() -> "Add assertion message");
    cp.verifyEquals(1);
    assertSame(ofm2, cache.putFileMetadata(ofm2), () -> "Add assertion message");
    assertEquals(2, mm.allocs, () -> "Add assertion message");
    Integer magicNumber46312049 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    cp.verifyEquals(magicNumber46312049
    assertSame(ofm1, cache.getFileMetadata(1), () -> "Add assertion message");
    assertSame(ofm2, cache.getFileMetadata(2), () -> "Add assertion message");
    cp.verifyEquals(4);
    OrcFileMetadata ofm3 = OrcFileMetadata.createDummy(1);
    assertSame(ofm1, cache.putFileMetadata(ofm3), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(2, mm.allocs, () -> "Add assertion message");
    cp.verifyEquals(5);
    // Line removed due to Duplicate Assert: assertSame(ofm1, cache.getFileMetadata(1), () -> "Add assertion message");
    cp.verifyEquals(6);

    OrcStripeMetadata osm1 = OrcStripeMetadata.createDummy(1), osm2 = OrcStripeMetadata.createDummy(2);
    assertSame(osm1, cache.putStripeMetadata(osm1), () -> "Add assertion message");
    Integer magicNumber73515878 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber73515878, mm.allocs() -> "Add assertion message");
    assertSame(osm2, cache.putStripeMetadata(osm2), () -> "Add assertion message");
    assertEquals(4, mm.allocs, () -> "Add assertion message");
    assertSame(osm1, cache.getStripeMetadata(osm1.getKey()), () -> "Add assertion message");
    assertSame(osm2, cache.getStripeMetadata(osm2.getKey()), () -> "Add assertion message");
    OrcStripeMetadata osm3 = OrcStripeMetadata.createDummy(1);
    assertSame(osm1, cache.putStripeMetadata(osm3), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(4, mm.allocs, () -> "Add assertion message");
    assertSame(osm1, cache.getStripeMetadata(osm3.getKey()), () -> "Add assertion message");
    cp.verifyEquals(12);
  }
}