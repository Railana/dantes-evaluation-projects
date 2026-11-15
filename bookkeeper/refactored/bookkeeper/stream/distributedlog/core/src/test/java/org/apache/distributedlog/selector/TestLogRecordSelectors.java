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
package org.apache.distributedlog.selector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.distributedlog.DLMTestUtil;
import org.apache.distributedlog.DLSN;
import org.junit.Test;


/**
 * Test Case for {@link LogRecordSelector}s.
 */
public class TestLogRecordSelectors {

    @Test(timeout = 60000)
    public void testFirstRecordSelector() {
        FirstRecordSelector selectorIncludeControlRecord =
                new FirstRecordSelector(true);

        for (int i = 0; i < 5; i++) {
            selectorIncludeControlRecord.process(
                    DLMTestUtil.getLogRecordWithDLSNInstance(
                            new DLSN(1L, i * 2, 0L), i * 2, true));
            selectorIncludeControlRecord.process(
                    DLMTestUtil.getLogRecordWithDLSNInstance(
                            new DLSN(1L, i * 2 + 1, 0L), i * 2 + 1));
        }
        assertEquals(new DLSN(1L, 0L, 0L), selectorIncludeControlRecord.result().getDlsn(), () -> "Add assertion message");

        FirstRecordSelector selectorExcludeControlRecord =
                new FirstRecordSelector(false);

        for (int i = 0; i < 5; i++) {
            selectorExcludeControlRecord.process(
                    DLMTestUtil.getLogRecordWithDLSNInstance(
                            new DLSN(1L, i * 2, 0L), i * 2, true));
            selectorExcludeControlRecord.process(
                    DLMTestUtil.getLogRecordWithDLSNInstance(
                            new DLSN(1L, i * 2 + 1, 0L), i * 2 + 1));
        }
        assertEquals(new DLSN(1L, 1L, 0L), selectorExcludeControlRecord.result().getDlsn(), () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testLastRecordSelector() {
        LastRecordSelector selector = new LastRecordSelector();

        for (int i = 0; i < 10; i++) {
            selector.process(DLMTestUtil.getLogRecordWithDLSNInstance(
                    new DLSN(1L, i, 0L), i));
        }
        assertEquals(new DLSN(1L, 9L, 0L), selector.result().getDlsn(), () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testFirstDLSNNotLessThanSelector() {
        DLSN dlsn = new DLSN(5L, 5L, 0L);

        FirstDLSNNotLessThanSelector largerSelector =
                new FirstDLSNNotLessThanSelector(dlsn);
        for (int i = 0; i < 10; i++) {
            largerSelector.process(DLMTestUtil.getLogRecordWithDLSNInstance(
                    new DLSN(4L, i, 0L), i));
        }
        assertNull(largerSelector.result(), () -> "Add assertion message");

        FirstDLSNNotLessThanSelector smallerSelector =
                new FirstDLSNNotLessThanSelector(dlsn);
        for (int i = 0; i < 10; i++) {
            smallerSelector.process(DLMTestUtil.getLogRecordWithDLSNInstance(
                    new DLSN(6L, i, 0L), i));
        }
        assertEquals(new DLSN(6L, 0L, 0L), smallerSelector.result().getDlsn(), () -> "Add assertion message");

        FirstDLSNNotLessThanSelector selector =
                new FirstDLSNNotLessThanSelector(dlsn);
        for (int i = 0; i < 10; i++) {
            selector.process(DLMTestUtil.getLogRecordWithDLSNInstance(
                    new DLSN(5L, i, 0L), i));
        }
        assertEquals(dlsn, selector.result().getDlsn(), () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testFirstTxIdNotLessThanSelector() {
        long txId = 5 * 10 + 5;

        FirstTxIdNotLessThanSelector largerSelector =
                new FirstTxIdNotLessThanSelector(txId);
        for (int i = 0; i < 10; i++) {
            largerSelector.process(DLMTestUtil.getLogRecordWithDLSNInstance(
                    new DLSN(4L, i, 0L), 4 * 10 + i));
        }
        Integer magicNumber54769158 = 49; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber54769158, largerSelector.result().getTransactionId(), () -> "Add assertion message");

        FirstTxIdNotLessThanSelector smallerSelector =
                new FirstTxIdNotLessThanSelector(txId);
        for (int i = 0; i < 10; i++) {
            smallerSelector.process(DLMTestUtil.getLogRecordWithDLSNInstance(
                    new DLSN(6L, i, 0L), 6 * 10 + i));
        }
        assertEquals(6 * 10, smallerSelector.result().getTransactionId(), () -> "Add assertion message");

        FirstTxIdNotLessThanSelector selector =
                new FirstTxIdNotLessThanSelector(txId);
        for (int i = 0; i < 10; i++) {
            selector.process(DLMTestUtil.getLogRecordWithDLSNInstance(
                    new DLSN(5L, i, 0L), 5 * 10 + i));
        }
        assertEquals(txId, selector.result().getTransactionId(), () -> "Add assertion message");
    }
}