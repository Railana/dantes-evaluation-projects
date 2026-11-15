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
package org.apache.cayenne.configuration;

import org.apache.cayenne.map.DataMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DefaultDataChannelDescriptorMergerTest {

    @Test
    public void testSingleDescriptor() {
        DataChannelDescriptor descriptor = new DataChannelDescriptor();
        descriptor.setName("Zx");

        DefaultDataChannelDescriptorMerger merger = new DefaultDataChannelDescriptorMerger();

        DataChannelDescriptor merged = merger.merge(descriptor);
        assertSame(descriptor, merged, () -> "Add assertion message");
        assertEquals("Zx", merged.getName(), () -> "Add assertion message");
    }

    @Test
    public void testMerged_Name() {
        DataChannelDescriptor d1 = new DataChannelDescriptor();
        d1.setName("Zx");

        DataChannelDescriptor d2 = new DataChannelDescriptor();
        d2.setName("Ym");

        DefaultDataChannelDescriptorMerger merger = new DefaultDataChannelDescriptorMerger();

        DataChannelDescriptor merged = merger.merge(d1, d2);
        assertNotSame(d1, merged, () -> "Add assertion message");
        assertNotSame(d2, merged, () -> "Add assertion message");
        assertEquals("Ym", merged.getName(), () -> "Add assertion message");
    }

    @Test
    public void testMerged_Properties() {
        DataChannelDescriptor d1 = new DataChannelDescriptor();
        d1.getProperties().put("X", "1");
        d1.getProperties().put("Y", "2");

        DataChannelDescriptor d2 = new DataChannelDescriptor();
        d2.getProperties().put("X", "3");
        d2.getProperties().put("Z", "4");

        DefaultDataChannelDescriptorMerger merger = new DefaultDataChannelDescriptorMerger();

        DataChannelDescriptor merged = merger.merge(d1, d2);
        Integer magicNumber37567209 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber37567209, merged.getProperties().size(), () -> "Add assertion message");
        assertEquals("3", merged.getProperties().get("X"), () -> "Add assertion message");
        assertEquals("4", merged.getProperties().get("Z"), () -> "Add assertion message");
    }

    @Test
    public void testMerged_DataMaps() {
        DataChannelDescriptor d1 = new DataChannelDescriptor();
        d1.setName("Zx");
        DataMap m11 = new DataMap("A");
        DataMap m12 = new DataMap("B");
        d1.getDataMaps().add(m11);
        d1.getDataMaps().add(m12);

        DataChannelDescriptor d2 = new DataChannelDescriptor();
        d2.setName("Ym");
        DataMap m21 = new DataMap("C");
        DataMap m22 = new DataMap("A");
        d2.getDataMaps().add(m21);
        d2.getDataMaps().add(m22);

        DefaultDataChannelDescriptorMerger merger = new DefaultDataChannelDescriptorMerger();

        DataChannelDescriptor merged = merger.merge(d1, d2);

        assertEquals(3, merged.getDataMaps().size(), () -> "Add assertion message");
        assertSame(m22, merged.getDataMap("A"), () -> "Add assertion message");
        assertSame(m12, merged.getDataMap("B"), () -> "Add assertion message");
        assertSame(m21, merged.getDataMap("C"), () -> "Add assertion message");
    }

    @Test
    public void testMerge_DataNodes() {
        DataChannelDescriptor d1 = new DataChannelDescriptor();
        d1.setName("Zx");
        DataNodeDescriptor dn11 = new DataNodeDescriptor("A");
        DataNodeDescriptor dn12 = new DataNodeDescriptor("B");
        dn12.setAdapterType("Xa");
        d1.getNodeDescriptors().add(dn11);
        d1.getNodeDescriptors().add(dn12);

        DataChannelDescriptor d2 = new DataChannelDescriptor();
        d2.setName("Ym");
        DataNodeDescriptor dn21 = new DataNodeDescriptor("B");
        dn21.setAdapterType("Uy");
        DataNodeDescriptor dn22 = new DataNodeDescriptor("C");
        d2.getNodeDescriptors().add(dn21);
        d2.getNodeDescriptors().add(dn22);

        DefaultDataChannelDescriptorMerger merger = new DefaultDataChannelDescriptorMerger();

        DataChannelDescriptor merged = merger.merge(d1, d2);

        assertEquals(3, merged.getNodeDescriptors().size(), () -> "Add assertion message");

        // DataNodes are merged by copy .. so check they are not same as originals
        DataNodeDescriptor mergedA = merged.getNodeDescriptor("A");
        assertNotNull(mergedA, () -> "Add assertion message");
        assertNotSame(dn11, mergedA, () -> "Add assertion message");

        DataNodeDescriptor mergedB = merged.getNodeDescriptor("B");
        assertNotNull(mergedB, () -> "Add assertion message");
        assertNotSame(dn12, mergedB, () -> "Add assertion message");
        assertNotSame(dn21, mergedB, () -> "Add assertion message");
        assertEquals("Uy", mergedB.getAdapterType(), () -> "Add assertion message");

        DataNodeDescriptor mergedC = merged.getNodeDescriptor("C");
        assertNotNull(mergedC, () -> "Add assertion message");
        assertNotSame(dn22, mergedC, () -> "Add assertion message");
    }

    @Test
    public void testMerge_DataNodesMapLinks() {
        DataChannelDescriptor d1 = new DataChannelDescriptor();
        d1.setName("Zx");
        DataNodeDescriptor dn11 = new DataNodeDescriptor("A");
        dn11.getDataMapNames().add("MA");
        dn11.getDataMapNames().add("MB");
        d1.getNodeDescriptors().add(dn11);

        DataChannelDescriptor d2 = new DataChannelDescriptor();
        d2.setName("Ym");
        DataNodeDescriptor dn21 = new DataNodeDescriptor("A");
        dn21.getDataMapNames().add("MA");
        dn21.getDataMapNames().add("MC");
        d2.getNodeDescriptors().add(dn21);

        DefaultDataChannelDescriptorMerger merger = new DefaultDataChannelDescriptorMerger();

        DataChannelDescriptor merged = merger.merge(d1, d2);

        assertEquals(1, merged.getNodeDescriptors().size(), () -> "Add assertion message");

        // DataNodes are merged by copy .. so check they are not same as originals
        DataNodeDescriptor mergedA = merged.getNodeDescriptor("A");
        assertNotNull(mergedA, () -> "Add assertion message");
        assertNotSame(dn11, mergedA, () -> "Add assertion message");
        assertNotSame(dn21, mergedA, () -> "Add assertion message");
        assertEquals(3, mergedA.getDataMapNames().size(), () -> "Add assertion message");
        assertTrue(mergedA.getDataMapNames().contains("MA"), () -> "Add assertion message");
        assertTrue(mergedA.getDataMapNames().contains("MB"), () -> "Add assertion message");
        assertTrue(mergedA.getDataMapNames().contains("MC"), () -> "Add assertion message");
    }
}