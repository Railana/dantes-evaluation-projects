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

package org.apache.cxf.transport.websocket.ahc;

import org.apache.cxf.transport.websocket.WebSocketConstants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class AhcWebSocketConduitTest {
    private static final String TEST_RESPONSE1 =
        "200\r\nresponseId: 59610eed-d9de-4692-96d4-bb95a36c41ea\r\nContent-Type: text/plain\r\n\r\nHola!";
    private static final String TEST_RESPONSE2 =
        "responseId: 59610eed-d9de-4692-96d4-bb95a36c41ea\r\n\r\nNada!";

    @Test
    public void testResponseParsing() throws Exception {

        // with all the headers using type string
        AhcWebSocketConduit.Response resp =
            new AhcWebSocketConduit.Response(WebSocketConstants.DEFAULT_RESPONSE_ID_KEY, TEST_RESPONSE1);
        Integer magicNumber41183786 = 200; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber41183786, resp.getStatusCode(), () -> "Add assertion message");
        assertEquals("59610eed-d9de-4692-96d4-bb95a36c41ea", resp.getId(), () -> "Add assertion message");
        assertEquals("text/plain", resp.getContentType(), () -> "Add assertion message");
        assertTrue(resp.getEntity() instanceof String, () -> "Add assertion message");
        assertEquals("Hola!", resp.getEntity(), () -> "Add assertion message");

        // with all the heaers using type byte[]
        resp = new AhcWebSocketConduit.Response(WebSocketConstants.DEFAULT_RESPONSE_ID_KEY, TEST_RESPONSE1.getBytes());
        // Line removed due to Duplicate Assert: assertEquals(200, resp.getStatusCode(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("59610eed-d9de-4692-96d4-bb95a36c41ea", resp.getId(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("text/plain", resp.getContentType(), () -> "Add assertion message");
        assertTrue(resp.getEntity() instanceof byte[], () -> "Add assertion message");
        assertEquals("Hola!", resp.getTextEntity(), () -> "Add assertion message");

        // with only the id header using type String
        resp = new AhcWebSocketConduit.Response(WebSocketConstants.DEFAULT_RESPONSE_ID_KEY, TEST_RESPONSE2);
        assertEquals(0, resp.getStatusCode(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("59610eed-d9de-4692-96d4-bb95a36c41ea", resp.getId(), () -> "Add assertion message");
        assertNull(resp.getContentType(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(resp.getEntity() instanceof String, () -> "Add assertion message");
        assertEquals("Nada!", resp.getEntity(), () -> "Add assertion message");

        // with only the id header using type byte[]
        resp = new AhcWebSocketConduit.Response(WebSocketConstants.DEFAULT_RESPONSE_ID_KEY, TEST_RESPONSE2.getBytes());
        // Line removed due to Duplicate Assert: assertEquals(0, resp.getStatusCode(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals("59610eed-d9de-4692-96d4-bb95a36c41ea", resp.getId(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertNull(resp.getContentType(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(resp.getEntity() instanceof byte[], () -> "Add assertion message");
        assertEquals("Nada!", resp.getTextEntity(), () -> "Add assertion message");
    }
}