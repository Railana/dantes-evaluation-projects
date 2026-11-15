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

package org.apache.cayenne.access.types;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 4.0
 */
public class DefaultValueObjectTypeRegistryTest {

    DefaultValueObjectTypeRegistry registry;
    ValueObjectType valueObjectType1, valueObjectType2;

    @Before
    public void setUpRegistry() {
        valueObjectType1 = mock(ValueObjectType.class);
        // Removed line due to General Fixture smell: valueObjectType1 = mock(ValueObjectType.class);
        when(valueObjectType1.getValueType()).thenReturn(Integer.class);
        when(valueObjectType1.getTargetType()).thenReturn(Integer.class);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: valueObjectType2 = mock(ValueObjectType.class);
        // Removed line due to General Fixture smell: when(valueObjectType2.getValueType()).thenReturn(Number.class);
        // Removed line due to General Fixture smell: when(valueObjectType2.getTargetType()).thenReturn(Integer.class);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: List<ValueObjectType<?, ?>> list = new ArrayList<>();
        // Removed line due to General Fixture smell: list.add(valueObjectType1);
        // Removed line due to General Fixture smell: list.add(valueObjectType2);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: registry = new DefaultValueObjectTypeRegistry(list);
    // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: @Test
    public void testInitialState() {

        valueObjectType2 = mock(ValueObjectType.class);
        when(valueObjectType2.getValueType()).thenReturn(Number.class);
        when(valueObjectType2.getTargetType()).thenReturn(Integer.class);

        List<ValueObjectType<?, ?>> list = new ArrayList<>();
        list.add(valueObjectType1);
        list.add(valueObjectType2);

        registry = new DefaultValueObjectTypeRegistry(list);
    }

    @Test
        Integer magicNumber68251048 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber68251048, registry.typeCache.size(), () -> "Add assertion message");
        assertTrue(registry.typeCache.containsKey(Integer.class.getName()), () -> "Add assertion message");
        assertTrue(registry.typeCache.containsKey(Number.class.getName()), () -> "Add assertion message");
        assertFalse(registry.typeCache.containsKey(String.class.getName()), () -> "Add assertion message");
        assertFalse(registry.typeCache.containsKey(Float.class.getName()), () -> "Add assertion message");
    }

    @Test
    public void getValueType() throws Exception {

        valueObjectType2 = mock(ValueObjectType.class);
        when(valueObjectType2.getValueType()).thenReturn(Number.class);
        when(valueObjectType2.getTargetType()).thenReturn(Integer.class);

        List<ValueObjectType<?, ?>> list = new ArrayList<>();
        list.add(valueObjectType1);
        list.add(valueObjectType2);

        registry = new DefaultValueObjectTypeRegistry(list);
    }

    @Test
        valueObjectType1 = mock(ValueObjectType.class);
        ValueObjectType<?,?> valueObjectType = registry.getValueType(Integer.class);
        assertSame(valueObjectType1, valueObjectType, () -> "Add assertion message");

        valueObjectType = registry.getValueType(Float.class);
        assertNull(valueObjectType, () -> "Add assertion message");

        valueObjectType = registry.getValueType(String.class);
        // Line removed due to Duplicate Assert: assertNull(valueObjectType, () -> "Add assertion message");

        assertEquals(2, registry.typeCache.size(), () -> "Add assertion message");
        assertFalse(registry.typeCache.containsKey(String.class.getName()), () -> "Add assertion message");
        assertFalse(registry.typeCache.containsKey(Float.class.getName()), () -> "Add assertion message");
    }

}