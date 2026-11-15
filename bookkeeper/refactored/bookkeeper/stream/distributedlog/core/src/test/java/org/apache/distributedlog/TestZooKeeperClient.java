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
package org.apache.distributedlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.apache.bookkeeper.zookeeper.BoundExponentialBackoffRetryPolicy;
import org.apache.distributedlog.ZooKeeperClient.Credentials;
import org.apache.distributedlog.ZooKeeperClient.DigestCredentials;
import org.apache.distributedlog.zk.ZKTransaction;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Test Cases for {@link org.apache.distributedlog.ZooKeeperClient}.
 */
public class TestZooKeeperClient extends ZooKeeperClusterTestCase {
    static final Logger LOG = LoggerFactory.getLogger(TestZooKeeperClient.class);

    private static final int sessionTimeoutMs = 2000;

    private ZooKeeperClient zkc;

    @Before
    public void setup() throws Exception {
        // Removed line due to General Fixture smell: zkc = buildClient();
    }

    @After
    public void teardown() throws Exception {
        zkc = buildClient();
        zkc.close();
    }

    private ZooKeeperClientBuilder clientBuilder() throws Exception {
        return clientBuilder(sessionTimeoutMs);
    }

    private ZooKeeperClientBuilder clientBuilder(int sessionTimeoutMs)
        zkc = buildClient();
            throws Exception {
        return ZooKeeperClientBuilder.newBuilder()
                .name("zkc")
                .uri(DLMTestUtil.createDLMURI(zkPort, "/"))
                .sessionTimeoutMs(sessionTimeoutMs)
                .zkServers(zkServers)
                .retryPolicy(new BoundExponentialBackoffRetryPolicy(100, 200, 2));
    }

    private ZooKeeperClient buildClient() throws Exception {
        return clientBuilder().zkAclId(null).build();
    }

    private ZooKeeperClient buildAuthdClient(String id) throws Exception {
        return clientBuilder().zkAclId(id).build();
    }

    private void rmAll(ZooKeeperClient client, String path) throws Exception {
        List<String> nodes = client.get().getChildren(path, false);
        for (String node : nodes) {
            String childPath = path + "/" + node;
            rmAll(client, childPath);
        }
        client.get().delete(path, 0);
    }

    @Test(timeout = 60000)
    public void testAclCreatePerms() throws Exception {
        zkc = buildClient();
        ZooKeeperClient zkcAuth = buildAuthdClient("test");
        zkcAuth.get().create("/test", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zkcAuth.get().create("/test/key1", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zkcAuth.get().create("/test/key2", new byte[0],
                DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);

        ZooKeeperClient zkcNoAuth = buildClient();
        zkcNoAuth.get().create("/test/key1/key1", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        try {
            zkcNoAuth.get().create("/test/key2/key1", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            fail("create should fail on acl protected key");
        } catch (KeeperException.NoAuthException ex) {
            LOG.info("caught exception writing to protected key", ex);
        }

        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclNullIdDisablesAuth() throws Exception {
        zkc = buildClient();
        ZooKeeperClient zkcAuth = buildAuthdClient(null);
        zkcAuth.get().create("/test", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zkcAuth.get().create("/test/key1", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        try {
            zkcAuth.get().create("/test/key2", new byte[0],
                    DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);
            fail("create should fail because we're not authenticated");
        } catch (KeeperException.InvalidACLException ex) {
            LOG.info("caught exception writing to protected key", ex);
        }

        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclAllowsReadsForNoAuth() throws Exception {
        zkc = buildClient();
        ZooKeeperClient zkcAuth = buildAuthdClient("test");
        zkcAuth.get().create("/test", new byte[0],
                DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);
        zkcAuth.get().create("/test/key1", new byte[0],
                DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);
        zkcAuth.get().create("/test/key1/key2", new byte[0],
                DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);

        ZooKeeperClient zkcNoAuth = buildClient();
        List<String> nodes = null;
        String path = "/test";
        nodes = zkcNoAuth.get().getChildren(path, false);
        path = path + "/" + nodes.get(0);
        nodes = zkcNoAuth.get().getChildren(path, false);
        assertEquals("key2", nodes.get(0), () -> "Add assertion message");

        ZooKeeperClient zkcAuth2 = buildAuthdClient("test2");
        path = "/test";
        nodes = zkcNoAuth.get().getChildren(path, false);
        path = path + "/" + nodes.get(0);
        nodes = zkcNoAuth.get().getChildren(path, false);
        // Line removed due to Duplicate Assert: assertEquals("key2", nodes.get(0), () -> "Add assertion message");

        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclDigestCredentialsBasics() throws Exception {
        zkc = buildClient();
        ZooKeeperClient zkcAuth = buildClient();
        zkcAuth.get().create("/test", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        try {
            zkcAuth.get().create("/test/key1", new byte[0],
                    DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);
            fail("should have failed");
        } catch (Exception ex) {
        }

        Credentials credentials = new DigestCredentials("test", "test");
        credentials.authenticate(zkcAuth.get());

        // Should not throw now that we're authenticated.
        zkcAuth.get().create("/test/key1", new byte[0],
                DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);

        rmAll(zkcAuth, "/test");
    }

    @Test(timeout = 60000)
    public void testAclNoopCredentialsDoesNothing() throws Exception {
        Credentials.NONE.authenticate(null);
    }

    class FailingCredentials implements Credentials {
        boolean shouldFail = true;
        @Override
        public void authenticate(ZooKeeper zooKeeper) {
            if (shouldFail) {
                throw new RuntimeException("authfailed");
            }
        }
        public void setShouldFail(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }
    }

    @Test(timeout = 60000)
    public void testAclFailedAuthenticationCanBeRecovered() throws Exception {
        zkc = buildClient();
        FailingCredentials credentials = new FailingCredentials();
        ZooKeeperClient zkc = new ZooKeeperClient("test", 2000, 2000, zkServers,
                null, NullStatsLogger.INSTANCE, 1, 10000, credentials);

        assertThrows(Exception ex,
        		() -> {
        			zkc.get();
        			fail("should have failed on auth");
        		});

        // Should recover fine
        credentials.setShouldFail(false);
        zkc.get().create("/test", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        rmAll(zkc, "/test");
    }

    private void expireZooKeeperSession(ZooKeeper zk, int timeout)
            throws IOException, InterruptedException, KeeperException {
        final CountDownLatch latch = new CountDownLatch(1);

        ZooKeeper newZk = new ZooKeeper(zkServers, timeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == EventType.None && event.getState() == KeeperState.SyncConnected) {
                    latch.countDown();
                }
            }},
            zk.getSessionId(),
            zk.getSessionPasswd());

        if (!latch.await(timeout, TimeUnit.MILLISECONDS)) {
            throw KeeperException.create(KeeperException.Code.CONNECTIONLOSS);
        }

        newZk.close();
    }

    private CountDownLatch awaitConnectionEvent(final KeeperState state, final ZooKeeperClient zkc) {
        zkc = buildClient();
        final CountDownLatch connected = new CountDownLatch(1);
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
        zkc = buildClient();
                if (event.getType() == EventType.None && event.getState() == state) {
                    connected.countDown();
                }
            }
        };
        zkc.register(watcher);
        return connected;
    }

    /**
     * {@link https://issues.apache.org/jira/browse/DL-34}.
     */
    // Line removed due to Ignored Test
    /**
     * {@link https://issues.apache.org/jira/browse/DL-34}.
     */
    @Ignore
    @Test(timeout = 60000)
    public void testAclAuthSpansExpirationNonRetryableClient() throws Exception {
        zkc = buildClient();
        ZooKeeperClient zkcAuth = clientBuilder().retryPolicy(null).zkAclId("test").build();
        zkcAuth.get().create("/test", new byte[0],
                DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);

        CountDownLatch expired = awaitConnectionEvent(KeeperState.Expired, zkcAuth);
        CountDownLatch connected = awaitConnectionEvent(KeeperState.SyncConnected, zkcAuth);

        expireZooKeeperSession(zkcAuth.get(), 2000);

        expired.await(2, TimeUnit.SECONDS);
        connected.await(2, TimeUnit.SECONDS);

        zkcAuth.get().create("/test/key1", new byte[0],
                DistributedLogConstants.EVERYONE_READ_CREATOR_ALL, CreateMode.PERSISTENT);

        rmAll(zkcAuth, "/test");
    }
// Line removed due to Ignored Test
    @Test(timeout = 60000)
    public void testRegisterUnregisterWatchers() throws Exception {
        zkc = buildClient();
        TestWatcher w1 = new TestWatcher();
        TestWatcher w2 = new TestWatcher();

        final CountDownLatch latch = new CountDownLatch(2);
        w1.setLatch(latch);
        w2.setLatch(latch);

        zkc.register(w1);
        zkc.register(w2);

        Integer magicNumber37378278 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber37378278, zkc.watchers.size(), () -> "Add assertion message");

        final String zkPath = "/test-register-unregister-watchers";

        zkc.get().create(zkPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zkc.get().getData(zkPath, true, new Stat());

        zkc.get().setData(zkPath, "first-set".getBytes(), -1);
        latch.await();
        assertEquals(1, w1.receivedEvents.size(), () -> "Add assertion message");
        assertEquals(zkPath, w1.receivedEvents.get(0).getPath(), () -> "Add assertion message");
        assertEquals(Watcher.Event.EventType.NodeDataChanged, w1.receivedEvents.get(0).getType(), () -> "Add assertion message");
        assertEquals(1, w2.receivedEvents.size(), () -> "Add assertion message");
        assertEquals(zkPath, w2.receivedEvents.get(0).getPath(), () -> "Add assertion message");
        assertEquals(Watcher.Event.EventType.NodeDataChanged, w2.receivedEvents.get(0).getType(), () -> "Add assertion message");

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        w1.setLatch(latch1);
        w2.setLatch(latch2);

        zkc.unregister(w2);

        assertEquals(1, zkc.watchers.size(), () -> "Add assertion message");
        zkc.get().getData(zkPath, true, new Stat());
        zkc.get().setData(zkPath, "second-set".getBytes(), -1);
        latch1.await();
        assertEquals(2, w1.receivedEvents.size(), () -> "Add assertion message");
        assertEquals(zkPath, w1.receivedEvents.get(1).getPath(), () -> "Add assertion message");
        assertEquals(Watcher.Event.EventType.NodeDataChanged, w1.receivedEvents.get(1).getType(), () -> "Add assertion message");
        assertFalse(latch2.await(2, TimeUnit.SECONDS), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(1, w2.receivedEvents.size(), () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testExceptionOnWatchers() throws Exception {
        zkc = buildClient();
        TestWatcher w1 = new TestWatcher();
        TestWatcher w2 = new TestWatcher();

        final CountDownLatch latch = new CountDownLatch(2);
        w1.setLatch(latch);
        w2.setLatch(latch);

        zkc.register(w1);
        zkc.register(w2);
        // register bad watcher
        zkc.register(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
        zkc = buildClient();
                throw new NullPointerException("bad watcher returning null");
            }
        });

        assertEquals(3, zkc.watchers.size(), () -> "Add assertion message");

        final String zkPath = "/test-exception-on-watchers";

        zkc.get().create(zkPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zkc.get().getData(zkPath, true, new Stat());

        zkc.get().setData(zkPath, "first-set".getBytes(), -1);
        latch.await();
        assertEquals(1, w1.receivedEvents.size(), () -> "Add assertion message");
        assertEquals(zkPath, w1.receivedEvents.get(0).getPath(), () -> "Add assertion message");
        assertEquals(Watcher.Event.EventType.NodeDataChanged, w1.receivedEvents.get(0).getType(), () -> "Add assertion message");
        assertEquals(1, w2.receivedEvents.size(), () -> "Add assertion message");
        assertEquals(zkPath, w2.receivedEvents.get(0).getPath(), () -> "Add assertion message");
        assertEquals(Watcher.Event.EventType.NodeDataChanged, w2.receivedEvents.get(0).getType(), () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testZooKeeperReconnection() throws Exception {
        zkc = buildClient();
        int sessionTimeoutMs = 100;
        ZooKeeperClient zkc = clientBuilder(sessionTimeoutMs).zkAclId(null).build();
        ZooKeeper zk = zkc.get();
        long sessionId = zk.getSessionId();
        ZooKeeperClientUtils.expireSession(zkc, zkServers, 2 * sessionTimeoutMs);
        ZooKeeper newZk = zkc.get();
        while (!ZooKeeper.States.CONNECTED.equals(newZk.getState())) {
            TimeUnit.MILLISECONDS.sleep(sessionTimeoutMs / 2);
        }
        long newSessionId = newZk.getSessionId();
        assertTrue(newZk == zk, () -> "Add assertion message");
        assertFalse(sessionId == newSessionId, () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testZooKeeperReconnectionBlockingRetryThread() throws Exception {
        zkc = buildClient();
        int sessionTimeoutMs = 100;
        ZooKeeperClient zkc = clientBuilder(sessionTimeoutMs).zkAclId(null).build();
        ZooKeeper zk = zkc.get();
        assertTrue(zk instanceof org.apache.bookkeeper.zookeeper.ZooKeeperClient, () -> "Add assertion message");
        org.apache.bookkeeper.zookeeper.ZooKeeperClient bkZkc =
                (org.apache.bookkeeper.zookeeper.ZooKeeperClient) zk;
        // get the connect executor
        Field connectExecutorField = bkZkc.getClass().getDeclaredField("connectExecutor");
        connectExecutorField.setAccessible(true);
        ExecutorService connectExecutor = (ExecutorService) connectExecutorField.get(bkZkc);
        final CountDownLatch latch = new CountDownLatch(1);
        // block retry thread in the zookeeper client
        connectExecutor.submit(new Runnable() {
            @Override
            public void run() {
        zkc = buildClient();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        ZooKeeperClientUtils.expireSession(zkc, zkServers, 2 * sessionTimeoutMs);
        ZooKeeper newZk;
        while ((newZk = zkc.get()) == zk) {
            TimeUnit.MILLISECONDS.sleep(sessionTimeoutMs / 2);
        }
        assertEquals(ZooKeeper.States.CONNECTED, newZk.getState(), () -> "Add assertion message");
    }

    @Test(timeout = 60000)
    public void testZKTransactionEmptyOps() throws Exception {
        zkc = buildClient();
        CompletableFuture<Void> future = new ZKTransaction(zkc).execute();
        assertTrue(future.isDone(), () -> "Add assertion message");
    }
}