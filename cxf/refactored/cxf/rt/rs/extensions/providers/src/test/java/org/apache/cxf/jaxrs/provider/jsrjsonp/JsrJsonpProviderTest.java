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
package org.apache.cxf.jaxrs.provider.jsrjsonp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class JsrJsonpProviderTest {
    private JsrJsonpProvider provider;

    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: provider = new JsrJsonpProvider();
    }

    @Test(expected = IOException.class)
    public void testReadWithNullStream() throws Exception {
        provider = new JsrJsonpProvider();
        provider.readFrom(JsonStructure.class, null, null, null, null, null);
    }

    @Test
    public void testGetSizeReturnsMinusOne() throws Exception {
        provider = new JsrJsonpProvider();
        assertThat(provider.getSize(null, JsonStructure.class, null, null, null),
            equalTo(-1L), () -> "Add assertion message");
    }

    @Test
    public void testReadableTypes() throws Exception {
        provider = new JsrJsonpProvider();
        assertThat(provider.isReadable(JsonArray.class, null, null, null),
            equalTo(true), () -> "Add assertion message");
        assertThat(provider.isReadable(JsonStructure.class, null, null, null),
            equalTo(true), () -> "Add assertion message");
        assertThat(provider.isReadable(JsonObject.class, null, null, null),
            equalTo(true), () -> "Add assertion message");
        assertThat(provider.isReadable(JsonValue.class, null, null, null),
            equalTo(false), () -> "Add assertion message");
    }

    @Test
    public void testWritableTypes() throws Exception {
        provider = new JsrJsonpProvider();
        assertThat(provider.isWriteable(JsonArray.class, null, null, null),
            equalTo(true), () -> "Add assertion message");
        assertThat(provider.isWriteable(JsonStructure.class, null, null, null),
            equalTo(true), () -> "Add assertion message");
        assertThat(provider.isWriteable(JsonObject.class, null, null, null),
            equalTo(true), () -> "Add assertion message");
        assertThat(provider.isWriteable(JsonValue.class, null, null, null),
            equalTo(false), () -> "Add assertion message");
    }

    @Test(expected = IOException.class)
    public void testWriteWithNullStream() throws Exception {
        provider = new JsrJsonpProvider();
        final JsonObject obj = Json.createObjectBuilder()
            .add("firstName", "Tom")
            .add("lastName", "Tommyknocker")
            .build();

        provider.writeTo(obj, JsonObject.class, null, null, null, null, null);
    }

    @Test
    public void testReadMalformedJson() throws Exception {
        provider = new JsrJsonpProvider();
        byte[] bytes = "junk".getBytes();

        assertThrows(WebApplicationException ex,
        		() -> {
        			provider.readFrom(JsonStructure.class, null, null, null, null,
        			new ByteArrayInputStream(bytes));
        			fail("400 BAD REQUEST is expected");
        		});
        equalTo(Response.Status.BAD_REQUEST.getStatusCode()), () -> "Add assertion message");
    }

    @Test
    public void testReadJsonObject() throws Exception {
        provider = new JsrJsonpProvider();
        final StringWriter writer = new StringWriter();

        Json.createGenerator(writer)
            .writeStartObject()
            .write("firstName", "Tom")
            .write("lastName", "Tommyknocker")
            .writeEnd()
            .close();

        final String str = writer.toString();
        writer.close();

        final JsonStructure obj = provider.readFrom(JsonStructure.class, null, null, null, null,
            new ByteArrayInputStream(str.getBytes()));

        assertThat(obj, instanceOf(JsonObject.class), () -> "Add assertion message");
        assertThat(((JsonObject)obj).getString("firstName"), equalTo("Tom"), () -> "Add assertion message");
        assertThat(((JsonObject)obj).getString("lastName"), equalTo("Tommyknocker"), () -> "Add assertion message");
    }

    @Test
    public void testReadJsonArray() throws Exception {
        provider = new JsrJsonpProvider();
        final StringWriter writer = new StringWriter();

        Json.createGenerator(writer)
            .writeStartArray()
            .write("Tom")
            .write("Tommyknocker")
            .writeEnd()
            .close();

        final JsonStructure obj = provider.readFrom(JsonStructure.class, null, null, null, null,
            new ByteArrayInputStream(writer.toString().getBytes()));

        assertThat(obj, instanceOf(JsonArray.class), () -> "Add assertion message");
        assertThat(((JsonArray)obj).getString(0), equalTo("Tom"), () -> "Add assertion message");
        assertThat(((JsonArray)obj).getString(1), equalTo("Tommyknocker"), () -> "Add assertion message");
        assertThat(((JsonArray)obj).size(), equalTo(2), () -> "Add assertion message");
    }

    @Test
    public void testWriteJsonObject() throws Exception {
        provider = new JsrJsonpProvider();
        final JsonObject obj = Json.createObjectBuilder()
            .add("firstName", "Tom")
            .add("lastName", "Tommyknocker")
            .build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        provider.writeTo(obj, JsonStructure.class, null, null, null, null, out);
        out.close();

        assertThat(new String(out.toByteArray()),
            equalTo("{\"firstName\":\"Tom\",\"lastName\":\"Tommyknocker\"}"), () -> "Add assertion message");
    }

    @Test
    public void testWriteJsonArray() throws Exception {
        provider = new JsrJsonpProvider();
        final JsonArray obj = Json.createArrayBuilder()
            .add(
                Json.createObjectBuilder()
                    .add("firstName", "Tom")
                    .add("lastName", "Tommyknocker")
            )
            .add(
                Json.createObjectBuilder()
                    .add("firstName", "Bob")
                    .add("lastName", "Bobbyknocker")
            )
            .build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        provider.writeTo(obj, JsonStructure.class, null, null, null, null, out);
        out.close();

        assertThat(new String(out.toByteArray()),
            equalTo("[{\"firstName\":\"Tom\",\"lastName\":\"Tommyknocker\"},"
                    + "{\"firstName\":\"Bob\",\"lastName\":\"Bobbyknocker\"}]"), () -> "Add assertion message");
    }

}