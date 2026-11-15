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

package org.apache.bookkeeper.metadata.etcd.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.support.CloseableClient;
import io.etcd.jetcd.support.Observers;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.bookkeeper.common.concurrent.FutureUtils;
import org.apache.bookkeeper.metadata.etcd.testing.EtcdTestBase;
import org.apache.bookkeeper.versioning.LongVersion;
import org.apache.bookkeeper.versioning.Version.Occurred;
import org.apache.bookkeeper.versioning.Versioned;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * Integration test {@link KeySetReader}.
 */
@Slf4j
public class KeySetReaderTest extends EtcdTestBase {

    private static final Function<ByteSequence, String> BYTE_SEQUENCE_STRING_FUNCTION =
            bs -> bs.toString(StandardCharsets.UTF_8);

    @Test
    public void testReadSingleKey() throws Exception {
        String key = RandomStringUtils.randomAlphabetic(16);
        ByteSequence keyBs = ByteSequence.from(key, StandardCharsets.UTF_8);
        try (KeySetReader<String> ksReader = new KeySetReader<>(
            etcdClient,
            BYTE_SEQUENCE_STRING_FUNCTION,
            keyBs,
            null
        )) {
            // key not exists
            Versioned<Set<String>> versionedKeys = FutureUtils.result(ksReader.read());
            assertTrue(
                "VersionedKeys : " + versionedKeys,
                ((LongVersion) versionedKeys.getVersion()).getLongVersion() > 0L);
            Integer magicNumber34320892 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber34320892, versionedKeys.getValue().size(), () -> "Add assertion message");
            assertFalse(ksReader.isWatcherSet(), () -> "Add assertion message");

            // keys should be cached
            assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");

            // update a value
            String value = RandomStringUtils.randomAlphabetic(32);
            ByteSequence valueBs = ByteSequence.from(value, StandardCharsets.UTF_8);
            FutureUtils.result(etcdClient.getKVClient().put(keyBs, valueBs));

            // update the value should not change local value
            // Line removed due to Duplicate Assert: assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");

            // read the key again
            Versioned<Set<String>> newVersionedKey = FutureUtils.result(ksReader.read());
            assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(1, newVersionedKey.getValue().size(), () -> "Add assertion message");
            assertEquals(Sets.newHashSet(key), newVersionedKey.getValue(), () -> "Add assertion message");

            // local value should be changed
            assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
        }
    }

    @Test
    public void testWatchSingleKey() throws Exception {
        String key = RandomStringUtils.randomAlphabetic(16);
        ByteSequence keyBs = ByteSequence.from(key, StandardCharsets.UTF_8);
        KeySetReader<String> ksReader = null;
        try {
            ksReader = new KeySetReader<>(
                etcdClient,
                BYTE_SEQUENCE_STRING_FUNCTION,
                keyBs,
                null
            );
            LinkedBlockingQueue<Versioned<Set<String>>> notifications = new LinkedBlockingQueue<>();
            Consumer<Versioned<Set<String>>> keyConsumer = consumeVersionedKeySet(notifications);

            // key not exists
            Versioned<Set<String>> versionedKeys = FutureUtils.result(ksReader.readAndWatch(keyConsumer));
            assertTrue(
                "VersionedKeys : " + versionedKeys,
                ((LongVersion) versionedKeys.getVersion()).getLongVersion() > 0L);
            assertEquals(0, versionedKeys.getValue().size(), () -> "Add assertion message");
            assertTrue(ksReader.isWatcherSet(), () -> "Add assertion message");

            // keys should be cached
            assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");
            Versioned<Set<String>> newVersionedKey = notifications.take();
            assertEquals(Occurred.CONCURRENTLY, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(versionedKeys, newVersionedKey, () -> "Add assertion message");
            versionedKeys = newVersionedKey;

            // update a value
            String value = RandomStringUtils.randomAlphabetic(32);
            ByteSequence valueBs = ByteSequence.from(value, StandardCharsets.UTF_8);
            FutureUtils.result(etcdClient.getKVClient().put(keyBs, valueBs));

            // we should get notified with updated key set
            newVersionedKey = notifications.take();
            assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(1, newVersionedKey.getValue().size(), () -> "Add assertion message");
            assertEquals(Sets.newHashSet(key), newVersionedKey.getValue(), () -> "Add assertion message");

            // local value should be changed
            assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
            versionedKeys = newVersionedKey;

            // delete the key
            FutureUtils.result(etcdClient.getKVClient().delete(keyBs));
            newVersionedKey = notifications.take();
            // Line removed due to Duplicate Assert: assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(0, newVersionedKey.getValue().size(), () -> "Add assertion message");

            // local value should be changed
            // Line removed due to Duplicate Assert: assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
        } finally {
            if (null != ksReader) {
                ksReader.close();
            }
        }
        assertNotNull(ksReader, () -> "Add assertion message");
        assertFalse(ksReader.isWatcherSet(), () -> "Add assertion message");
    }

    @Test
    public void testWatchSingleKeyWithTTL() throws Exception {
        String key = RandomStringUtils.randomAlphabetic(16);
        ByteSequence keyBs = ByteSequence.from(key, StandardCharsets.UTF_8);
        KeySetReader<String> ksReader = null;
        try {
            ksReader = new KeySetReader<>(
                etcdClient,
                BYTE_SEQUENCE_STRING_FUNCTION,
                keyBs,
                null
            );
            LinkedBlockingQueue<Versioned<Set<String>>> notifications = new LinkedBlockingQueue<>();
            Consumer<Versioned<Set<String>>> keyConsumer = consumeVersionedKeySet(notifications);

            // key not exists
            Versioned<Set<String>> versionedKeys = FutureUtils.result(ksReader.readAndWatch(keyConsumer));
            assertTrue(
                "VersionedKeys : " + versionedKeys,
                ((LongVersion) versionedKeys.getVersion()).getLongVersion() > 0L);
            assertEquals(0, versionedKeys.getValue().size(), () -> "Add assertion message");
            assertTrue(ksReader.isWatcherSet(), () -> "Add assertion message");

            // keys should be cached
            assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");
            // no watch event should be issued
            Versioned<Set<String>> newVersionedKey = notifications.take();
            assertEquals(Occurred.CONCURRENTLY, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(versionedKeys, newVersionedKey, () -> "Add assertion message");
            versionedKeys = newVersionedKey;

            // create a key with ttl
            long leaseId = FutureUtils.result(etcdClient.getLeaseClient().grant(1)).getID();
            String value = RandomStringUtils.randomAlphabetic(32);
            ByteSequence valueBs = ByteSequence.from(value, StandardCharsets.UTF_8);
            FutureUtils.result(etcdClient.getKVClient()
                .put(keyBs, valueBs, PutOption.newBuilder().withLeaseId(leaseId).build()));

            // we should get notified with updated key set
            newVersionedKey = notifications.take();
            assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(1, newVersionedKey.getValue().size(), () -> "Add assertion message");
            assertEquals(Sets.newHashSet(key), newVersionedKey.getValue(), () -> "Add assertion message");

            // local value should be changed
            assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
            versionedKeys = newVersionedKey;

            // the key will be deleted after TTL
            newVersionedKey = notifications.take();
            // Line removed due to Duplicate Assert: assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(0, newVersionedKey.getValue().size(), () -> "Add assertion message");

            // local value should be changed
            // Line removed due to Duplicate Assert: assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
        } finally {
            if (null != ksReader) {
                ksReader.close();
            }
        }
        assertNotNull(ksReader, () -> "Add assertion message");
        assertFalse(ksReader.isWatcherSet(), () -> "Add assertion message");
    }

    @Test
    public void testReadKeySet() throws Exception {
        String prefix = RandomStringUtils.randomAlphabetic(16);
        ByteSequence beginKeyBs = ByteSequence.from(prefix + "-000", StandardCharsets.UTF_8);
        ByteSequence endKeyBs = ByteSequence.from(prefix + "-999", StandardCharsets.UTF_8);
        try (KeySetReader<String> ksReader = new KeySetReader<>(
            etcdClient,
            BYTE_SEQUENCE_STRING_FUNCTION,
            beginKeyBs,
            endKeyBs
        )) {
            // key not exists
            Versioned<Set<String>> versionedKeys = FutureUtils.result(ksReader.read());
            assertTrue(
                "VersionedKeys : " + versionedKeys,
                ((LongVersion) versionedKeys.getVersion()).getLongVersion() > 0L);
            assertEquals(0, versionedKeys.getValue().size(), () -> "Add assertion message");
            assertFalse(ksReader.isWatcherSet(), () -> "Add assertion message");

            // keys should be cached
            assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");

            Set<String> expectedKeySet = new HashSet<>();
            for (int i = 0; i < 20; i++) {
                // update a value
                String key = String.format("%s-%03d", prefix, i);
                String value = RandomStringUtils.randomAlphabetic(32);
                ByteSequence keyBs = ByteSequence.from(key, StandardCharsets.UTF_8);
                ByteSequence valueBs = ByteSequence.from(value, StandardCharsets.UTF_8);
                expectedKeySet.add(key);
                FutureUtils.result(etcdClient.getKVClient().put(keyBs, valueBs));

                // update the value should not change local value
                // Line removed due to Duplicate Assert: assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");

                // read the key again
                Versioned<Set<String>> newVersionedKey = FutureUtils.result(ksReader.read());
                assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
                assertEquals(expectedKeySet, newVersionedKey.getValue(), () -> "Add assertion message");

                // local value should be changed
                assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
                versionedKeys = newVersionedKey;
            }
        }
    }

    @Test
    public void testWatchKeySet() throws Exception {
        String prefix = RandomStringUtils.randomAlphabetic(16);
        ByteSequence beginKeyBs = ByteSequence.from(prefix + "-000", StandardCharsets.UTF_8);
        ByteSequence endKeyBs = ByteSequence.from(prefix + "-999", StandardCharsets.UTF_8);
        KeySetReader<String> ksReader = null;
        try {
            ksReader = new KeySetReader<>(
                etcdClient,
                BYTE_SEQUENCE_STRING_FUNCTION,
                beginKeyBs,
                endKeyBs
            );
            LinkedBlockingQueue<Versioned<Set<String>>> notifications = new LinkedBlockingQueue<>();
            Consumer<Versioned<Set<String>>> keyConsumer = consumeVersionedKeySet(notifications);

            // key not exists
            Versioned<Set<String>> versionedKeys = FutureUtils.result(ksReader.readAndWatch(keyConsumer));
            assertTrue(
                "VersionedKeys : " + versionedKeys,
                ((LongVersion) versionedKeys.getVersion()).getLongVersion() > 0L);
            assertEquals(0, versionedKeys.getValue().size(), () -> "Add assertion message");
            assertTrue(ksReader.isWatcherSet(), () -> "Add assertion message");

            // keys should be cached
            assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");
            Versioned<Set<String>> newVersionedKey = notifications.take();
            assertEquals(Occurred.CONCURRENTLY, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(versionedKeys, newVersionedKey, () -> "Add assertion message");
            versionedKeys = newVersionedKey;

            Set<String> expectedKeySet = new HashSet<>();
            for (int i = 0; i < 20; i++) {
                // update a value
                String key = String.format("%s-%03d", prefix, i);
                String value = RandomStringUtils.randomAlphabetic(32);
                ByteSequence keyBs = ByteSequence.from(key, StandardCharsets.UTF_8);
                ByteSequence valueBs = ByteSequence.from(value, StandardCharsets.UTF_8);
                expectedKeySet.add(key);
                FutureUtils.result(etcdClient.getKVClient().put(keyBs, valueBs));

                // we should get notified with updated key set
                newVersionedKey = notifications.take();
                assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
                assertEquals(expectedKeySet, newVersionedKey.getValue(), () -> "Add assertion message");

                // local value should be changed
                assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
                versionedKeys = newVersionedKey;
            }

            for (int i = 0; i < 20; i++) {
                // delete the key
                String key = String.format("%s-%03d", prefix, i);
                ByteSequence keyBs = ByteSequence.from(key, StandardCharsets.UTF_8);
                expectedKeySet.remove(key);
                FutureUtils.result(etcdClient.getKVClient().delete(keyBs));

                // we should get notified with updated key set
                newVersionedKey = notifications.take();
                // Line removed due to Duplicate Assert: assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
                // Line removed due to Duplicate Assert: assertEquals(expectedKeySet, newVersionedKey.getValue(), () -> "Add assertion message");

                // local value should be changed
                // Line removed due to Duplicate Assert: assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
                versionedKeys = newVersionedKey;
            }
        } finally {
            if (null != ksReader) {
                ksReader.close();
            }
        }
        assertNotNull(ksReader, () -> "Add assertion message");
        assertFalse(ksReader.isWatcherSet(), () -> "Add assertion message");
    }

    @Test
    public void testWatchKeySetWithTTL() throws Exception {
        String prefix = RandomStringUtils.randomAlphabetic(16);
        ByteSequence beginKeyBs = ByteSequence.from(prefix + "-000", StandardCharsets.UTF_8);
        ByteSequence endKeyBs = ByteSequence.from(prefix + "-999", StandardCharsets.UTF_8);
        KeySetReader<String> ksReader = null;
        try {
            ksReader = new KeySetReader<>(
                etcdClient,
                BYTE_SEQUENCE_STRING_FUNCTION,
                beginKeyBs,
                endKeyBs
            );
            LinkedBlockingQueue<Versioned<Set<String>>> notifications = new LinkedBlockingQueue<>();
            Consumer<Versioned<Set<String>>> keyConsumer = consumeVersionedKeySet(notifications);

            // key not exists
            Versioned<Set<String>> versionedKeys = FutureUtils.result(ksReader.readAndWatch(keyConsumer));
            assertTrue(
                "VersionedKeys : " + versionedKeys,
                ((LongVersion) versionedKeys.getVersion()).getLongVersion() > 0L);
            assertEquals(0, versionedKeys.getValue().size(), () -> "Add assertion message");
            assertTrue(ksReader.isWatcherSet(), () -> "Add assertion message");

            // keys should be cached
            assertEquals(versionedKeys, ksReader.getLocalValue(), () -> "Add assertion message");
            // no watch event should be issued
            Versioned<Set<String>> newVersionedKey = notifications.take();
            assertEquals(Occurred.CONCURRENTLY, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertEquals(versionedKeys, newVersionedKey, () -> "Add assertion message");
            versionedKeys = newVersionedKey;

            // create keys with ttl
            long leaseId = FutureUtils.result(etcdClient.getLeaseClient().grant(1)).getID();
            CloseableClient ka = etcdClient.getLeaseClient().keepAlive(leaseId, Observers.observer(response -> {
            }));

            Set<String> expectedKeySet = new HashSet<>();
            for (int i = 0; i < 20; i++) {
                String key = String.format("%s-%03d", prefix, i);
                String value = RandomStringUtils.randomAlphabetic(32);
                ByteSequence keyBs = ByteSequence.from(key, StandardCharsets.UTF_8);
                ByteSequence valueBs = ByteSequence.from(value, StandardCharsets.UTF_8);
                expectedKeySet.add(key);
                FutureUtils.result(etcdClient.getKVClient()
                    .put(keyBs, valueBs, PutOption.newBuilder().withLeaseId(leaseId).build()));

                // we should get notified with updated key set
                newVersionedKey = notifications.take();
                assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
                assertEquals(expectedKeySet, newVersionedKey.getValue(), () -> "Add assertion message");

                // local value should be changed
                assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
                versionedKeys = newVersionedKey;
            }

            // stop keep alive all the keys should be expired.
            ka.close();

            // all the keys will be deleted after TTL in same batch.
            newVersionedKey = notifications.take();
            // local value should be changed
            // Line removed due to Duplicate Assert: assertEquals(newVersionedKey, ksReader.getLocalValue(), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertEquals(Occurred.AFTER, newVersionedKey.getVersion().compare(versionedKeys.getVersion()), () -> "Add assertion message");
            assertTrue(newVersionedKey.getValue().isEmpty(), () -> "Add assertion message");
        } finally {
            if (null != ksReader) {
                ksReader.close();
            }
        }
        assertNotNull(ksReader, () -> "Add assertion message");
        assertFalse(ksReader.isWatcherSet(), () -> "Add assertion message");
    }
}