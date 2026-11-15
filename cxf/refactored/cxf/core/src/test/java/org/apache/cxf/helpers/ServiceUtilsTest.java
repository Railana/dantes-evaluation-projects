/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.cxf.helpers;

import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.message.Message;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceUtilsTest {
    private Message msg;

    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: msg = mock(Message.class);
    }

    @Test
    public void testmakeNamespaceFromClassName() throws Exception {
        String tns = ServiceUtils.makeNamespaceFromClassName("com.example.ws.Test", "http");
        assertEquals("http://ws.example.com/", tns, () -> "Add assertion message");
    }


    @Test
    public void testRequestResponseTypes() {
        msg = mock(Message.class);
        // lets do server side first
        setupSchemaValidationValue(SchemaValidationType.REQUEST, false);
        assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.REQUEST, false);
        assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.RESPONSE, false);
        assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.RESPONSE, false);
        assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");

        // now client side
        setupSchemaValidationValue(SchemaValidationType.REQUEST, true);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.REQUEST, true);
        // Line removed due to Duplicate Assert: assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.RESPONSE, true);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.RESPONSE, true);
        // Line removed due to Duplicate Assert: assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");
    }

    @Test
    public void testIsSchemaValidationEnabled() {
        msg = mock(Message.class);
        setupSchemaValidationValue(SchemaValidationType.NONE, false);
        assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.NONE, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.NONE, false);
        assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.BOTH, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.NONE, false);
        assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.NONE, false);
        assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.IN, false);
        assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.NONE, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.IN, false);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.BOTH, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.IN, false);
        assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.IN, false);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.OUT, false);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.NONE, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.OUT, false);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.BOTH, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.OUT, false);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.OUT, false);
        assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");

        setupSchemaValidationValue(SchemaValidationType.BOTH, false);
        // Line removed due to Duplicate Assert: assertFalse(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.NONE, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.BOTH, false);
        assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.BOTH, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.BOTH, false);
        // Line removed due to Duplicate Assert: assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.IN, msg), () -> "Add assertion message");
        setupSchemaValidationValue(SchemaValidationType.BOTH, false);
        // Line removed due to Duplicate Assert: assertTrue(ServiceUtils.isSchemaValidationEnabled(SchemaValidationType.OUT, msg), () -> "Add assertion message");
    }

    @Test
    public void testGetSchemaValidationTypeBoolean() {
        msg = mock(Message.class);
        setupSchemaValidationValue(null, false);
        assertEquals(SchemaValidationType.NONE, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue("", false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.NONE, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue(Boolean.FALSE, false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.NONE, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue("false", false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.NONE, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue("FALSE", false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.NONE, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue("fAlse", false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.NONE, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue(Boolean.TRUE, false);
        assertEquals(SchemaValidationType.BOTH, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue("true", false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.BOTH, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue("TRUE", false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.BOTH, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

        setupSchemaValidationValue("tRue", false);
        // Line removed due to Duplicate Assert: assertEquals(SchemaValidationType.BOTH, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");
    }

    @Test
    public void testGetSchemaValidationType() {
        msg = mock(Message.class);
        for (SchemaValidationType type : SchemaValidationType.values()) {
            setupSchemaValidationValue(type.name(), false);
            assertEquals(type, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

            setupSchemaValidationValue(type.name().toLowerCase(), false);
            // Line removed due to Duplicate Assert: assertEquals(type, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");

            setupSchemaValidationValue(StringUtils.capitalize(type.name()), false);
            // Line removed due to Duplicate Assert: assertEquals(type, ServiceUtils.getSchemaValidationType(msg), () -> "Add assertion message");
        }
    }

    private void setupSchemaValidationValue(Object value, boolean isRequestor) {
        msg = mock(Message.class);
        when(msg.getContextualProperty(Message.SCHEMA_VALIDATION_ENABLED)).thenReturn(value);
        when(msg.get(Message.REQUESTOR_ROLE)).thenReturn(isRequestor);
    }
}