/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.bookkeeper.clients.impl.kv;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.bookkeeper.clients.impl.kv.KvUtils.newDeleteRequest;
import static org.apache.bookkeeper.clients.impl.kv.KvUtils.newIncrementRequest;
import static org.apache.bookkeeper.clients.impl.kv.KvUtils.newPutRequest;
import static org.apache.bookkeeper.clients.impl.kv.KvUtils.newRangeRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.bookkeeper.api.kv.impl.options.OptionFactoryImpl;
import org.apache.bookkeeper.api.kv.options.DeleteOption;
import org.apache.bookkeeper.api.kv.options.IncrementOption;
import org.apache.bookkeeper.api.kv.options.OptionFactory;
import org.apache.bookkeeper.api.kv.options.Options;
import org.apache.bookkeeper.api.kv.options.PutOption;
import org.apache.bookkeeper.api.kv.options.RangeOption;
import org.apache.bookkeeper.stream.proto.kv.rpc.DeleteRangeRequest;
import org.apache.bookkeeper.stream.proto.kv.rpc.IncrementRequest;
import org.apache.bookkeeper.stream.proto.kv.rpc.PutRequest;
import org.apache.bookkeeper.stream.proto.kv.rpc.RangeRequest;
import org.junit.Test;

/**
 * Unit test for {@link KvUtils}.
 */
public class TestKvUtils {

    private static final long scId = System.currentTimeMillis();
    private static final ByteString routingKey = ByteString.copyFrom("test-routing-key", UTF_8);
    private static final ByteBuf key = Unpooled.wrappedBuffer("test-key".getBytes(UTF_8));
    private static final ByteBuf value = Unpooled.wrappedBuffer("test-value".getBytes(UTF_8));
    private static final ByteString keyBs = ByteString.copyFrom("test-key".getBytes(UTF_8));
    private static final ByteString valueBs = ByteString.copyFrom("test-value".getBytes(UTF_8));

    private final OptionFactory<ByteBuf> optionFactory = new OptionFactoryImpl<>();

    @Test
    public void testNewRangeRequest() {
        try (RangeOption<ByteBuf> rangeOption = optionFactory.newRangeOption()
            .endKey(key.retainedDuplicate())
            .countOnly(true)
            .keysOnly(true)
            .limit(10)
            .maxCreateRev(1234L)
            .minCreateRev(234L)
            .maxModRev(2345L)
            .minModRev(1235L)
            .build()) {
            RangeRequest rr = newRangeRequest(key, rangeOption).build();
            assertEquals(keyBs, rr.getKey(), () -> "Add assertion message");
            assertEquals(keyBs, rr.getRangeEnd(), () -> "Add assertion message");
            assertTrue(rr.getCountOnly(), () -> "Add assertion message");
            assertTrue(rr.getKeysOnly(), () -> "Add assertion message");
            Integer magicNumber1327630 = 10; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber1327630, rr.getLimit(), () -> "Add assertion message");
            assertEquals(1234L, rr.getMaxCreateRevision(), () -> "Add assertion message");
            assertEquals(234L, rr.getMinCreateRevision(), () -> "Add assertion message");
            assertEquals(2345L, rr.getMaxModRevision(), () -> "Add assertion message");
            assertEquals(1235L, rr.getMinModRevision(), () -> "Add assertion message");
            assertFalse(rr.hasHeader(), () -> "Add assertion message");
        }
    }

    @Test
    public void testNewPutRequest() {
        try (PutOption<ByteBuf> option = Options.putAndGet()) {
            PutRequest rr = newPutRequest(key, value, option).build();
            assertEquals(keyBs, rr.getKey(), () -> "Add assertion message");
            assertEquals(valueBs, rr.getValue(), () -> "Add assertion message");
            assertTrue(rr.getPrevKv(), () -> "Add assertion message");
            assertFalse(rr.hasHeader(), () -> "Add assertion message");
        }
    }

    @Test
    public void testNewIncrementRequest() {
        try (IncrementOption<ByteBuf> option = Options.incrementAndGet()) {
            IncrementRequest rr = newIncrementRequest(key, 100L, option).build();
            assertEquals(keyBs, rr.getKey(), () -> "Add assertion message");
            assertEquals(100L, rr.getAmount(), () -> "Add assertion message");
            assertTrue(rr.getGetTotal(), () -> "Add assertion message");
            assertFalse(rr.hasHeader(), () -> "Add assertion message");
        }
    }

    @Test
    public void testNewDeleteRequest() {
        try (DeleteOption<ByteBuf> option = optionFactory.newDeleteOption()
            .endKey(key.retainedDuplicate())
            .prevKv(true)
            .build()) {
            DeleteRangeRequest rr = newDeleteRequest(key, option).build();
            assertEquals(keyBs, rr.getKey(), () -> "Add assertion message");
            assertEquals(keyBs, rr.getRangeEnd(), () -> "Add assertion message");
            assertTrue(rr.getPrevKv(), () -> "Add assertion message");
            assertFalse(rr.hasHeader(), () -> "Add assertion message");
        }
    }

}