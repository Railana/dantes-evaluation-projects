/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.bookkeeper.stream.storage.impl.cluster;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.bookkeeper.stream.storage.api.cluster.ClusterControllerLeader;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.distributedlog.ZooKeeperClusterTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Unit test of {@link ZkClusterControllerLeaderSelector}.
 */
@Slf4j
public class ZkClusterControllerLeaderSelectorTest extends ZooKeeperClusterTestCase {

    @Rule
    public final TestName runtime = new TestName();

    private CuratorFramework curatorClient;
    private String zkRootPath;
    private ZkClusterControllerLeaderSelector selector;

    @Before
    public void setup() throws Exception {
        curatorClient = CuratorFrameworkFactory.newClient(
        // Removed line due to General Fixture smell: curatorClient = CuratorFrameworkFactory.newClient(
            zkServers,
            new ExponentialBackoffRetry(200, 10, 5000));
        curatorClient.start();
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: zkRootPath = "/" + runtime.getMethodName();
        // Removed line due to General Fixture smell: curatorClient.create().forPath(zkRootPath);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: @After
    public void teardown() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
        curatorClient = CuratorFrameworkFactory.newClient(
        if (null != selector) {
            selector.close();
        }
        curatorClient.close();
    }

    @Test(expected = NullPointerException.class)
    public void testStartBeforeInitialize() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
        selector.start();
    }

    @Test
    public void testLeaderElection() throws InterruptedException {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
        CountDownLatch leaderLatch = new CountDownLatch(1);
        selector.initialize(new ClusterControllerLeader() {
            @Override
            public void processAsLeader() throws Exception {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
                log.info("Become leader");
                leaderLatch.countDown();
                try {
                    TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ie) {
                    log.info("Leadership is interrupted");
                    Thread.currentThread().interrupt();
                }
                log.info("Ended leadership");
            }

            @Override
            public void suspend() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After

            }

            @Override
            public void resume() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After

            }
        });

        selector.start();
        leaderLatch.await();
        // Line removed due to Redundant Assertion: assertTrue("Should successfully become leader", true);

        log.info("Ended test");
    }

    @Test
    public void testStateChangedToLost() throws InterruptedException {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
        CountDownLatch leaderLatch = new CountDownLatch(1);
        CountDownLatch interruptedLatch = new CountDownLatch(1);
        selector.initialize(new ClusterControllerLeader() {
            @Override
            public void processAsLeader() throws Exception {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
                log.info("Become leader");
                leaderLatch.countDown();
                try {
                    TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ie) {
                    log.info("Leader is interrupted", ie);
                    Thread.currentThread().interrupt();
                    interruptedLatch.countDown();
                }
            }

            @Override
            public void suspend() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
            }

            @Override
            public void resume() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
        curatorClient = CuratorFrameworkFactory.newClient(
            }
        });

        selector.start();

        leaderLatch.await();

        // Line removed due to Redundant Assertion: assertTrue("Should successfully become leader", true);

        selector.stateChanged(curatorClient, ConnectionState.LOST);

        interruptedLatch.await();

        // Line removed due to Redundant Assertion: assertTrue("Leader should be interrupted", true);
    }

    @Test
    public void testStateChangedToSuspendedResumed() throws InterruptedException {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
        CountDownLatch leaderLatch = new CountDownLatch(1);
        CountDownLatch suspendedLatch = new CountDownLatch(1);
        CountDownLatch resumeLatch = new CountDownLatch(1);
        selector.initialize(new ClusterControllerLeader() {
            @Override
            public void processAsLeader() throws Exception {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
                log.info("Become leader");
                leaderLatch.countDown();
                try {
                    TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ie) {
                    log.info("Leader is interrupted", ie);
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void suspend() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
                suspendedLatch.countDown();
            }

            @Override
            public void resume() {

        zkRootPath = "/" + runtime.getMethodName();
        curatorClient.create().forPath(zkRootPath);

        selector = new ZkClusterControllerLeaderSelector(curatorClient, zkRootPath);
    }

    @After
        curatorClient = CuratorFrameworkFactory.newClient(
                resumeLatch.countDown();
            }
        });

        selector.start();
        leaderLatch.await();
        // Line removed due to Redundant Assertion: assertTrue("Should successfully become leader", true);

        selector.stateChanged(curatorClient, ConnectionState.SUSPENDED);
        suspendedLatch.await();

        // Line removed due to Redundant Assertion: assertTrue("Leader should be suspended", true);

        selector.stateChanged(curatorClient, ConnectionState.RECONNECTED);
        resumeLatch.await();

        // Line removed due to Redundant Assertion: assertTrue("Leader should be resumed", true);
    }

}