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

package org.apache.cxf.jaxrs.sse;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OutboundSseEventImplTest {

    /**
     * Ensure that the <code>SseImpl</code> returns the correct builder class,
     * <code>OutboundSseEventImpl.BuilderImpl.class</code>.
     */
    @Test
    public void testSseImplReturnsExpectedOutboundSseEventBuilder() {
        Sse sse = new SseImpl();
        assertEquals(sse.newEventBuilder().getClass(), OutboundSseEventImpl.BuilderImpl.class, () -> "Add assertion message");
    }

    /**
     * A user should not need to specify a media type when creating an outbound event. The default
     * should be <code>MediaType.SERVER_SENT_EVENTS_TYPE</code>.
     */
    @Test
    public void testDefaultMediaType() {
        Sse sse = new SseImpl();

        // test newEvent(data)
        OutboundSseEvent event = sse.newEvent("myData");
        assertNull(event.getName(), () -> "Add assertion message");
        assertEquals("myData", event.getData(), () -> "Add assertion message");
        assertEquals(MediaType.TEXT_PLAIN_TYPE, event.getMediaType(), () -> "Add assertion message");

        // test newEvent(name, data)
        event = sse.newEvent("myName", "myData2");
        assertEquals("myName", event.getName(), () -> "Add assertion message");
        assertEquals("myData2", event.getData(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(MediaType.TEXT_PLAIN_TYPE, event.getMediaType(), () -> "Add assertion message");

        // test newEventBuilder()...build()
        event = sse.newEventBuilder().comment("myComment").data("myData3").build();
        assertEquals("myComment", event.getComment(), () -> "Add assertion message");
        assertEquals("myData3", event.getData(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(MediaType.TEXT_PLAIN_TYPE, event.getMediaType(), () -> "Add assertion message");
    }

    /**
     * A user should not need to specify the type of data being sent in an outbound
     * event. In that case the OutboundSseEvent should use the data object's type. Other
     * types may be specified, but the default (if not specified by the user) should be
     * the return value from the object's <code>getClass()</code> method.
     */
    @Test
    public void testDefaultClass() {
        Sse sse = new SseImpl();

        // test newEvent(string)
        OutboundSseEvent event = sse.newEvent("myData");
        assertNull(event.getName(), () -> "Add assertion message");
        assertEquals("myData", event.getData(), () -> "Add assertion message");
        assertEquals(String.class, event.getType(), () -> "Add assertion message");

        // test newEvent(name, data)
        event = sse.newEvent("myName", "myData2");
        assertEquals("myName", event.getName(), () -> "Add assertion message");
        assertEquals("myData2", event.getData(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(String.class, event.getType(), () -> "Add assertion message");

        // test newEventBuilder()...build()
        event = sse.newEventBuilder().comment("myComment").data("myData3").build();
        assertEquals("myComment", event.getComment(), () -> "Add assertion message");
        assertEquals("myData3", event.getData(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(String.class, event.getType(), () -> "Add assertion message");

        // test that object's class is re-enabled when calling different signatures of the data method
        OutboundSseEvent.Builder builder = sse.newEventBuilder();
        builder.data(TestData.class, new TestDataImpl("1", "2"));
        event = builder.build();
        assertEquals(TestData.class, event.getType(), () -> "Add assertion message");
        builder.data("myString");
        event = builder.build();
        // Line removed due to Duplicate Assert: assertEquals(String.class, event.getType(), () -> "Add assertion message");

        // same thing, but don't build in between calls to data
        event = sse.newEventBuilder().data(TestDataImpl.class, new TestDataImpl("3")).data("anotherString").build();
        // Line removed due to Duplicate Assert: assertEquals(String.class, event.getType(), () -> "Add assertion message");
        assertEquals("anotherString", event.getData(), () -> "Add assertion message");
    }

    /**
     * If the user passes null in as the data object or type object for <code>Builder.data(Object)</code>,
     * <code>Builder.data(Class, Object)</code>, or <code>Builder.data(GenericType, Object)</code>, they
     * should expect an IllegalArgumentException to be thrown.
     */
    @Test
    public void testNullsForDataOrTypes() {
        Sse sse = new SseImpl();
        OutboundSseEvent.Builder builder = sse.newEventBuilder();

        try {
            builder.data(null);
            fail("Passing a null data object should have resulted in an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            builder.data(Object.class, null);
            // Line removed due to Duplicate Assert: fail("Passing a null data object should have resulted in an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            builder.data((Class<?>)null, "123");
            // Line removed due to Duplicate Assert: fail("Passing a null data object should have resulted in an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            builder.data(new GenericType<List<String>>() { }, null);
            // Line removed due to Duplicate Assert: fail("Passing a null data object should have resulted in an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            builder.data((GenericType<?>)null, "456");
            // Line removed due to Duplicate Assert: fail("Passing a null data object should have resulted in an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test that event built by the builder contains all of the data passed in
     * to it.
     */
    @Test
    public void testBuilderAPIs() {
        SseImpl sse = new SseImpl();
        OutboundSseEvent.Builder builder = sse.newEventBuilder();
        builder.comment("myComment");
        builder.data(new TestDataImpl("dataNoSpecifiedType"));
        builder.id("id");
        builder.mediaType(MediaType.APPLICATION_JSON_TYPE);
        builder.name("name");
        builder.reconnectDelay(5000);
        OutboundSseEvent event = builder.build();
        assertEquals("myComment", event.getComment(), () -> "Add assertion message");
        assertEquals(TestDataImpl.class, event.getType(), () -> "Add assertion message");
        assertTrue(event.getData() instanceof TestDataImpl, () -> "Add assertion message");
        assertEquals("dataNoSpecifiedType", ((TestDataImpl)event.getData()).getData().get(0), () -> "Add assertion message");

        assertEquals("id", event.getId(), () -> "Add assertion message");
        assertEquals(MediaType.APPLICATION_JSON_TYPE, event.getMediaType(), () -> "Add assertion message");
        assertEquals("name", event.getName(), () -> "Add assertion message");
        Integer magicNumber17001207 = 5000; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber17001207, event.getReconnectDelay(), () -> "Add assertion message");

        // now reuse the builder to build a new event
        builder.comment("myComment2");
        builder.data(TestData.class, new TestDataImpl("data1", "data2"));
        builder.id("id2");
        builder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        builder.name("name2");
        builder.reconnectDelay(9000);
        event = builder.build();
        assertEquals("myComment2", event.getComment(), () -> "Add assertion message");
        assertEquals(new TestDataImpl("data1", "data2"), event.getData(), () -> "Add assertion message");
        assertEquals(TestData.class, event.getType(), () -> "Add assertion message");
        assertEquals("id2", event.getId(), () -> "Add assertion message");
        assertEquals(MediaType.TEXT_PLAIN_TYPE, event.getMediaType(), () -> "Add assertion message");
        assertEquals("name2", event.getName(), () -> "Add assertion message");
        assertEquals(9000, event.getReconnectDelay(), () -> "Add assertion message");
    }

    interface TestData {
        List<String> getData();
    }

    class TestDataImpl implements TestData {
        final List<String> data = new ArrayList<>();
        TestDataImpl(String...entries) {
            for (String entry : entries) {
                data.add(entry);
            }
        }

        @Override
        public List<String> getData() {
            return data;
        }
        @Override
        public boolean equals(Object o) {
            if (o instanceof TestDataImpl && ((TestDataImpl)o).data.size() == data.size()) {
                for (int i = 0; i < data.size(); i++) {
                    if (!((TestDataImpl)o).data.get(i).equals(data.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }
    }
}