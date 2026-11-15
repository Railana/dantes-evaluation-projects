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
package org.apache.distributedlog.limiter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;



/**
 * TestRequestLimiter.
 */
public class TestRequestLimiter {

    class MockRequest {
    }

    class MockRequestLimiter implements RequestLimiter<MockRequest> {
        int count;
        MockRequestLimiter() {
            this.count = 0;
        }
        public void apply(MockRequest request) {
            count++;
        }
        public int getCount() {
            return count;
        }
    }

    @Test(timeout = 60000)
    public void testChainedRequestLimiter() throws Exception {
        MockRequestLimiter limiter1 = new MockRequestLimiter();
        MockRequestLimiter limiter2 = new MockRequestLimiter();
        ChainedRequestLimiter.Builder<MockRequest> limiterBuilder =
                new ChainedRequestLimiter.Builder<MockRequest>();
        limiterBuilder.addLimiter(limiter1)
                      .addLimiter(limiter2);
        ChainedRequestLimiter<MockRequest> limiter = limiterBuilder.build();
        Integer magicNumber31091459 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber31091459, limiter1.getCount(), () -> "Add assertion message");
        assertEquals(0, limiter2.getCount(), () -> "Add assertion message");
        limiter.apply(new MockRequest());
        Integer magicNumber86114194 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber86114194, limiter1.getCount(), () -> "Add assertion message");
        Integer magicNumber13436785 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber13436785, limiter2.getCount(), () -> "Add assertion message");
        limiter.apply(new MockRequest());
        assertEquals(2, limiter1.getCount(), () -> "Add assertion message");
        assertEquals(2, limiter2.getCount(), () -> "Add assertion message");
    }
}