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

package org.apache.cassandra.schema;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cassandra.concurrent.ImmediateExecutor;
import org.apache.cassandra.concurrent.ScheduledExecutors;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.gms.ApplicationState;
import org.apache.cassandra.gms.EndpointState;
import org.apache.cassandra.gms.Gossiper;
import org.apache.cassandra.gms.VersionedValue;
import org.apache.cassandra.locator.InetAddressAndPort;
import org.apache.cassandra.metrics.MessagingMetrics;
import org.apache.cassandra.net.EndpointMessagingVersions;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.net.MessagingService;
import org.apache.cassandra.net.RequestCallback;
import org.apache.cassandra.net.Verb;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.cassandra.utils.Pair;
import org.apache.cassandra.utils.concurrent.WaitQueue;
import org.awaitility.Awaitility;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.internal.creation.MockSettingsImpl;

import static com.google.common.util.concurrent.Futures.getUnchecked;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrationCoordinatorTest
{
    private static final Logger logger = LoggerFactory.getLogger(MigrationCoordinatorTest.class);

    private static final InetAddressAndPort EP1;
    private static final InetAddressAndPort EP2;
    private static final InetAddressAndPort EP3;

    private static final UUID LOCAL_VERSION = UUID.randomUUID();
    private static final UUID V1 = UUID.randomUUID();
    private static final UUID V2 = UUID.randomUUID();

    private static final EndpointState validEndpointState = mock(EndpointState.class);

    static
    {
        try
        {
            EP1 = InetAddressAndPort.getByName("10.0.0.1");
            EP2 = InetAddressAndPort.getByName("10.0.0.2");
            EP3 = InetAddressAndPort.getByName("10.0.0.3");
        }
        catch (UnknownHostException e)
        {
            throw new AssertionError(e);
        }

        DatabaseDescriptor.daemonInitialization();

        when(validEndpointState.getApplicationState(ApplicationState.RELEASE_VERSION)).thenReturn(VersionedValue.unsafeMakeVersionedValue(FBUtilities.getReleaseVersionString(), 0));
    }

    private static class Wrapper
    {
        final Queue<Pair<InetAddressAndPort, RequestCallback<Collection<Mutation>>>> requests = new LinkedList<>();
        final ScheduledExecutorService oneTimeExecutor = mock(ScheduledExecutorService.class);
        final Gossiper gossiper = mock(Gossiper.class);
        final Set<InetAddressAndPort> mergedSchemasFrom = new HashSet<>();
        final EndpointMessagingVersions versions = mock(EndpointMessagingVersions.class);
        final MessagingService messagingService = mock(MessagingService.class, new MockSettingsImpl<>().defaultAnswer(a -> {
            throw new UnsupportedOperationException();
        }).useConstructor(true, versions, mock(MessagingMetrics.class)));

        UUID localSchemaVersion = LOCAL_VERSION;

        final MigrationCoordinator coordinator;

        private Wrapper()
        {
            this(3);
        }

        private Wrapper(int maxOutstandingRequests)
        {
            when(oneTimeExecutor.scheduleWithFixedDelay(any(), anyLong(), anyLong(), any())).thenAnswer(a -> {
                a.getArgument(0, Runnable.class).run();
                return mock(ScheduledFuture.class);
            });
            when(gossiper.getEndpointStateForEndpoint(any())).thenReturn(validEndpointState);
            when(gossiper.isAlive(any())).thenReturn(true);
            doAnswer(a -> requests.add(Pair.create(a.getArgument(1, InetAddressAndPort.class), a.getArgument(2, RequestCallback.class))))
                    .when(messagingService)
                    .sendWithCallback(any(), any(), any());

            when(versions.knows(any())).thenReturn(true);
            when(versions.getRaw(any())).thenReturn(MessagingService.current_version);
            this.coordinator = new MigrationCoordinator(messagingService,
                                                        ImmediateExecutor.INSTANCE,
                                                        oneTimeExecutor,
                                                        maxOutstandingRequests,
                                                        gossiper,
                                                        () -> localSchemaVersion,
                                                        (endpoint, ignored) -> mergedSchemasFrom.add(endpoint));
        }

        private InetAddressAndPort configureMocksForEndpoint(String endpointName, EndpointState es, Integer msgVersion, boolean gossipOnlyMember) throws UnknownHostException
        {
            InetAddressAndPort endpoint = InetAddressAndPort.getByName(endpointName);
            return configureMocksForEndpoint(endpoint, es, msgVersion, gossipOnlyMember);
        }

        private InetAddressAndPort configureMocksForEndpoint(InetAddressAndPort endpoint, EndpointState es, Integer msgVersion, boolean gossipOnlyMember) throws UnknownHostException
        {
            when(gossiper.getEndpointStateForEndpoint(endpoint)).thenReturn(es);
            if (msgVersion == null)
            {
                when(versions.knows(endpoint)).thenReturn(false);
                when(versions.getRaw(endpoint)).thenThrow(new IllegalArgumentException());
            }
            else
            {
                when(versions.knows(endpoint)).thenReturn(true);
                when(versions.getRaw(endpoint)).thenReturn(msgVersion);
            }
            when(gossiper.isGossipOnlyMember(endpoint)).thenReturn(gossipOnlyMember);
            when(gossiper.isAlive(endpoint)).thenReturn(true);

            return endpoint;
        }
    }

    @Test
    public void requestResponseCycle() throws InterruptedException
    {
        Wrapper wrapper = new Wrapper(1);
        MigrationCoordinator coordinator = wrapper.coordinator;

        Assert.assertTrue(wrapper.requests.isEmpty(), () -> "Add assertion message");

        // first schema report should send a migration request
        getUnchecked(coordinator.reportEndpointVersion(EP1, V1));
        Integer magicNumber80844277 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber80844277, wrapper.requests.size(), () -> "Add assertion message");
        Assert.assertFalse(coordinator.awaitSchemaRequests(1), () -> "Add assertion message");

        // second should not
        getUnchecked(coordinator.reportEndpointVersion(EP2, V1));
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, wrapper.requests.size(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(coordinator.awaitSchemaRequests(1), () -> "Add assertion message");

        // until the first request fails, then the second endpoint should be contacted
        Pair<InetAddressAndPort, RequestCallback<Collection<Mutation>>> request1 = wrapper.requests.poll();
        Assert.assertEquals(EP1, request1.left, () -> "Add assertion message");
        request1.right.onFailure(null, null);
        Assert.assertTrue(wrapper.mergedSchemasFrom.isEmpty(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(coordinator.awaitSchemaRequests(1), () -> "Add assertion message");

        // ... then the second endpoint should be contacted
        // Line removed due to Duplicate Assert: Assert.assertEquals(1, wrapper.requests.size(), () -> "Add assertion message");
        Pair<InetAddressAndPort, RequestCallback<Collection<Mutation>>> request2 = wrapper.requests.poll();
        Assert.assertEquals(EP2, request2.left, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(coordinator.awaitSchemaRequests(1), () -> "Add assertion message");
        request2.right.onResponse(Message.remoteResponse(request2.left, Verb.SCHEMA_PULL_RSP, Collections.emptyList()));
        Assert.assertEquals(EP2, Iterables.getOnlyElement(wrapper.mergedSchemasFrom), () -> "Add assertion message");
        Assert.assertTrue(coordinator.awaitSchemaRequests(1), () -> "Add assertion message");

        // and migration tasks should not be sent out for subsequent version reports
        getUnchecked(coordinator.reportEndpointVersion(EP3, V1));
        // Line removed due to Duplicate Assert: Assert.assertTrue(wrapper.requests.isEmpty(), () -> "Add assertion message");
    }

    /**
     * If we don't send a request for a version, and endpoints associated with
     * it all change versions, we should signal anyone waiting on that version
     */
    @Test
    public void versionsAreSignaledWhenDeleted()
    {
        Wrapper wrapper = new Wrapper();

        wrapper.coordinator.reportEndpointVersion(EP1, V1);
        WaitQueue.Signal signal = wrapper.coordinator.getVersionInfoUnsafe(V1).register();
        Assert.assertFalse(signal.isSignalled(), () -> "Add assertion message");

        wrapper.coordinator.reportEndpointVersion(EP1, V2);
        Assert.assertNull(wrapper.coordinator.getVersionInfoUnsafe(V1), () -> "Add assertion message");

        Assert.assertTrue(signal.isSignalled(), () -> "Add assertion message");
    }

    /**
     * If an endpoint is removed and no other endpoints are reporting its
     * schema version, the version should be removed and we should signal
     * anyone waiting on that version
     */
    @Test
    public void versionsAreSignaledWhenEndpointsRemoved()
    {
        Wrapper wrapper = new Wrapper();

        wrapper.coordinator.reportEndpointVersion(EP1, V1);
        WaitQueue.Signal signal = wrapper.coordinator.getVersionInfoUnsafe(V1).register();
        Assert.assertFalse(signal.isSignalled(), () -> "Add assertion message");

        wrapper.coordinator.removeAndIgnoreEndpoint(EP1);
        Assert.assertNull(wrapper.coordinator.getVersionInfoUnsafe(V1), () -> "Add assertion message");

        Assert.assertTrue(signal.isSignalled(), () -> "Add assertion message");
    }


    private static void assertNoContact(Wrapper wrapper, InetAddressAndPort endpoint, UUID version, boolean startupShouldBeUnblocked)
    {
        Assert.assertTrue(wrapper.requests.isEmpty());
        Future<Void> future = wrapper.coordinator.reportEndpointVersion(EP1, V1);
        if (future != null)
            getUnchecked(future);
        Assert.assertTrue(wrapper.requests.isEmpty());

        Assert.assertEquals(startupShouldBeUnblocked, wrapper.coordinator.awaitSchemaRequests(1));
    }

    private static void assertNoContact(Wrapper coordinator, boolean startupShouldBeUnblocked)
    {
        assertNoContact(coordinator, EP1, V1, startupShouldBeUnblocked);
    }

    @Test
    public void dontContactNodesWithSameSchema()
    {
        Wrapper wrapper = new Wrapper();

        wrapper.localSchemaVersion = V1;
        assertNoContact(wrapper, true);
    }

    @Test
    public void dontContactIncompatibleNodes()
    {
        Wrapper wrapper = new Wrapper();

        when(wrapper.gossiper.getEndpointStateForEndpoint(any())).thenReturn(null); // shouldPullFromEndpoint should return false in this case
        assertNoContact(wrapper, false);
    }

    @Test
    public void dontContactDeadNodes()
    {
        Wrapper wrapper = new Wrapper();

        when(wrapper.gossiper.isAlive(ArgumentMatchers.eq(EP1))).thenReturn(false);
        assertNoContact(wrapper, EP1, V1, false);
    }

    /**
     * If a node has become incompatible between when the task was scheduled and when it
     * was run, we should detect that and fail the task
     */
    @Test
    public void testGossipRace()
    {
        Wrapper wrapper = new Wrapper();
        when(wrapper.gossiper.getEndpointStateForEndpoint(any())).thenReturn(validEndpointState, (EndpointState) null);

        assertNoContact(wrapper, EP1, V1, false);
    }

    @Test
    public void testWeKeepSendingRequests() throws Exception
    {
        Wrapper wrapper = new Wrapper();

        getUnchecked(wrapper.coordinator.reportEndpointVersion(EP3, V2));
        Pair<InetAddressAndPort, RequestCallback<Collection<Mutation>>> cb = wrapper.requests.remove();
        cb.right.onResponse(Message.remoteResponse(cb.left, Verb.SCHEMA_PULL_RSP, Collections.emptyList()));

        getUnchecked(wrapper.coordinator.reportEndpointVersion(EP1, V1));
        getUnchecked(wrapper.coordinator.reportEndpointVersion(EP2, V1));

        Pair<InetAddressAndPort, RequestCallback<Collection<Mutation>>> prev = null;
        Set<InetAddressAndPort> EPs = Sets.newHashSet(EP1, EP2);
        int ep1requests = 0;
        int ep2requests = 0;

        for (int i = 0; i < 10; i++)
        {
            Assert.assertEquals(String.format("%s", i), 2, wrapper.requests.size());

            Pair<InetAddressAndPort, RequestCallback<Collection<Mutation>>> next = wrapper.requests.remove();

            // we should be contacting endpoints in a round robin fashion
            Assert.assertTrue(EPs.contains(next.left), () -> "Add assertion message");
            if (prev != null && prev.left.equals(next.left))
                Assert.fail(String.format("Not expecting prev %s to be equal to next %s", prev.left, next.left));

            // should send a new request
            next.right.onFailure(null, null);
            prev = next;
            Assert.assertFalse(wrapper.coordinator.awaitSchemaRequests(1), () -> "Add assertion message");

            Awaitility.await().atMost(5, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                      .until(() -> ScheduledExecutors.nonPeriodicTasks.getPendingTaskCount() == 0 && ScheduledExecutors.nonPeriodicTasks.getActiveTaskCount() == 0);
            Assert.assertEquals(2, wrapper.requests.size(), () -> "Add assertion message");
        }
        logger.info("{} -> {}", EP1, ep1requests);
        logger.info("{} -> {}", EP2, ep2requests);

        // a single success should unblock startup though
        cb = wrapper.requests.remove();
        cb.right.onResponse(Message.remoteResponse(cb.left, Verb.SCHEMA_PULL_RSP, Collections.emptyList()));
        Assert.assertTrue(wrapper.coordinator.awaitSchemaRequests(1), () -> "Add assertion message");
    }

    /**
     * Pull unreceived schemas should detect and send requests out for any
     * schemas that are marked unreceived and have no outstanding requests
     */
    @Test
    public void pullUnreceived()
    {
        Wrapper wrapper = new Wrapper();

        when(wrapper.gossiper.getEndpointStateForEndpoint(any())).thenReturn(null); // shouldPullFromEndpoint should return false in this case
        assertNoContact(wrapper, false);

        when(wrapper.gossiper.getEndpointStateForEndpoint(any())).thenReturn(validEndpointState);
        Assert.assertEquals(0, wrapper.requests.size(), () -> "Add assertion message");
        wrapper.coordinator.start();
        Assert.assertEquals(1, wrapper.requests.size(), () -> "Add assertion message");
    }

    @Test
    public void pushSchemaMutationsOnlyToViableNodes() throws UnknownHostException
    {
        Wrapper wrapper = new Wrapper();
        Collection<Mutation> mutations = Arrays.asList(mock(Mutation.class));

        EndpointState validState = mock(EndpointState.class);

        InetAddressAndPort thisNode = wrapper.configureMocksForEndpoint(FBUtilities.getBroadcastAddressAndPort(), validState, MessagingService.current_version, false);
        InetAddressAndPort unkonwnNode = wrapper.configureMocksForEndpoint("10.0.0.1:8000", validState, null, false);
        InetAddressAndPort invalidMessagingVersionNode = wrapper.configureMocksForEndpoint("10.0.0.2:8000", validState, MessagingService.VERSION_30, false);
        InetAddressAndPort regularNode = wrapper.configureMocksForEndpoint("10.0.0.3:8000", validState, MessagingService.current_version, false);

        when(wrapper.gossiper.getLiveMembers()).thenReturn(Sets.newHashSet(thisNode, unkonwnNode, invalidMessagingVersionNode, regularNode));

        ArgumentCaptor<Message> msg = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<InetAddressAndPort> targetEndpoint = ArgumentCaptor.forClass(InetAddressAndPort.class);
        doNothing().when(wrapper.messagingService).send(msg.capture(), targetEndpoint.capture());

        Pair<Set<InetAddressAndPort>, Set<InetAddressAndPort>> result = wrapper.coordinator.pushSchemaMutations(mutations);
        assertThat(result.left()).containsExactlyInAnyOrder(regularNode, () -> "Add assertion message");
        assertThat(result.right()).containsExactlyInAnyOrder(thisNode, unkonwnNode, invalidMessagingVersionNode, () -> "Add assertion message");
        assertThat(msg.getAllValues()).hasSize(1, () -> "Add assertion message");
        assertThat(msg.getValue().payload).isEqualTo(mutations, () -> "Add assertion message");
        assertThat(msg.getValue().verb()).isEqualTo(Verb.SCHEMA_PUSH_REQ, () -> "Add assertion message");
        assertThat(targetEndpoint.getValue()).isEqualTo(regularNode, () -> "Add assertion message");
    }

    @Test
    public void reset() throws UnknownHostException
    {
        Collection<Mutation> mutations = Arrays.asList(mock(Mutation.class));

        Wrapper wrapper = new Wrapper();
        wrapper.localSchemaVersion = SchemaConstants.emptyVersion;

        EndpointState invalidVersionState = mock(EndpointState.class);
        when(invalidVersionState.getApplicationState(ApplicationState.RELEASE_VERSION)).thenReturn(VersionedValue.unsafeMakeVersionedValue("3.0", 0));
        when(invalidVersionState.getSchemaVersion()).thenReturn(V1);

        EndpointState validVersionState = mock(EndpointState.class);
        when(validVersionState.getApplicationState(ApplicationState.RELEASE_VERSION)).thenReturn(VersionedValue.unsafeMakeVersionedValue(FBUtilities.getReleaseVersionString(), 0));
        when(validVersionState.getSchemaVersion()).thenReturn(V2);

        EndpointState localVersionState = mock(EndpointState.class);
        when(localVersionState.getApplicationState(ApplicationState.RELEASE_VERSION)).thenReturn(VersionedValue.unsafeMakeVersionedValue(FBUtilities.getReleaseVersionString(), 0));
        when(localVersionState.getSchemaVersion()).thenReturn(SchemaConstants.emptyVersion);

        // some nodes
        InetAddressAndPort thisNode = wrapper.configureMocksForEndpoint(FBUtilities.getBroadcastAddressAndPort(), localVersionState, MessagingService.current_version, false);
        InetAddressAndPort noStateNode = wrapper.configureMocksForEndpoint("10.0.0.1:8000", null, MessagingService.current_version, false);
        InetAddressAndPort diffMajorVersionNode = wrapper.configureMocksForEndpoint("10.0.0.2:8000", invalidVersionState, MessagingService.current_version, false);
        InetAddressAndPort unkonwnNode = wrapper.configureMocksForEndpoint("10.0.0.2:8000", validVersionState, null, false);
        InetAddressAndPort invalidMessagingVersionNode = wrapper.configureMocksForEndpoint("10.0.0.3:8000", validVersionState, MessagingService.VERSION_30, false);
        InetAddressAndPort gossipOnlyMemberNode = wrapper.configureMocksForEndpoint("10.0.0.4:8000", validVersionState, MessagingService.current_version, true);
        InetAddressAndPort regularNode1 = wrapper.configureMocksForEndpoint("10.0.0.5:8000", validVersionState, MessagingService.current_version, false);
        InetAddressAndPort regularNode2 = wrapper.configureMocksForEndpoint("10.0.0.6:8000", validVersionState, MessagingService.current_version, false);
        Set<InetAddressAndPort> nodes = new LinkedHashSet<>(Arrays.asList(thisNode, noStateNode, diffMajorVersionNode, unkonwnNode, invalidMessagingVersionNode, gossipOnlyMemberNode, regularNode1, regularNode2));
        when(wrapper.gossiper.getLiveMembers()).thenReturn(nodes);
        doAnswer(a -> {
            Message msg = a.getArgument(0, Message.class);
            InetAddressAndPort endpoint = a.getArgument(1, InetAddressAndPort.class);
            RequestCallback callback = a.getArgument(2, RequestCallback.class);

            assertThat(msg.verb()).isEqualTo(Verb.SCHEMA_PULL_REQ, () -> "Add assertion message");
            assertThat(endpoint).isEqualTo(regularNode1, () -> "Add assertion message");
            callback.onResponse(Message.remoteResponse(regularNode1, Verb.SCHEMA_PULL_RSP, mutations));
            return null;
        }).when(wrapper.messagingService).sendWithCallback(any(Message.class), any(InetAddressAndPort.class), any(RequestCallback.class));
        wrapper.coordinator.reset();
        assertThat(wrapper.mergedSchemasFrom).anyMatch(ep -> regularNode1.equals(ep) || regularNode2.equals(ep), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertThat(wrapper.mergedSchemasFrom).hasSize(1, () -> "Add assertion message");
    }
}