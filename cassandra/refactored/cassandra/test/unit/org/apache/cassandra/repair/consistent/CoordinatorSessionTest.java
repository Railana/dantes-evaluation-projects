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

package org.apache.cassandra.repair.consistent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import org.apache.cassandra.dht.Range;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.locator.InetAddressAndPort;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.repair.AbstractRepairTest;
import org.apache.cassandra.repair.CoordinatedRepairResult;
import org.apache.cassandra.repair.RepairSessionResult;
import org.apache.cassandra.repair.messages.FailSession;
import org.apache.cassandra.repair.messages.FinalizeCommit;
import org.apache.cassandra.repair.messages.FinalizePropose;
import org.apache.cassandra.repair.messages.PrepareConsistentRequest;
import org.apache.cassandra.repair.messages.RepairMessage;
import org.apache.cassandra.utils.concurrent.AsyncPromise;
import org.apache.cassandra.utils.concurrent.Future;
import org.apache.cassandra.utils.concurrent.Promise;

import static org.apache.cassandra.repair.consistent.ConsistentSession.State.FAILED;
import static org.apache.cassandra.repair.consistent.ConsistentSession.State.FINALIZE_PROMISED;
import static org.apache.cassandra.repair.consistent.ConsistentSession.State.PREPARED;
import static org.apache.cassandra.repair.consistent.ConsistentSession.State.PREPARING;
import static org.apache.cassandra.repair.consistent.ConsistentSession.State.REPAIRING;

import static org.apache.cassandra.utils.TimeUUID.Generator.nextTimeUUID;

public class CoordinatorSessionTest extends AbstractRepairTest
{

    static CoordinatorSession.Builder createBuilder()
    {
        CoordinatorSession.Builder builder = CoordinatorSession.builder();
        builder.withState(PREPARING);
        builder.withSessionID(nextTimeUUID());
        builder.withCoordinator(COORDINATOR);
        builder.withUUIDTableIds(Sets.newHashSet(UUID.randomUUID(), UUID.randomUUID()));
        builder.withRepairedAt(System.currentTimeMillis());
        builder.withRanges(Sets.newHashSet(RANGE1, RANGE2, RANGE3));
        builder.withParticipants(Sets.newHashSet(PARTICIPANT1, PARTICIPANT2, PARTICIPANT3));
        return builder;
    }

    static CoordinatorSession createSession()
    {
        return createBuilder().build();
    }

    static InstrumentedCoordinatorSession createInstrumentedSession()
    {
        return new InstrumentedCoordinatorSession(createBuilder());
    }

    private static RepairSessionResult createResult(CoordinatorSession coordinator)
    {
        return new RepairSessionResult(coordinator.sessionID, "ks", coordinator.ranges, null, false);
    }

    private static void assertMessageSent(InstrumentedCoordinatorSession coordinator, InetAddressAndPort participant, RepairMessage expected)
    {
        Assert.assertTrue(coordinator.sentMessages.containsKey(participant));
        Assert.assertEquals(1, coordinator.sentMessages.get(participant).size());
        Assert.assertEquals(expected, coordinator.sentMessages.get(participant).get(0));
    }

    private static class InstrumentedCoordinatorSession extends CoordinatorSession
    {
        public InstrumentedCoordinatorSession(Builder builder)
        {
            super(builder);
        }

        Map<InetAddressAndPort, List<RepairMessage>> sentMessages = new HashMap<>();

        protected void sendMessage(InetAddressAndPort destination, Message<RepairMessage> message)
        {
            if (!sentMessages.containsKey(destination))
            {
                sentMessages.put(destination, new ArrayList<>());
            }
            sentMessages.get(destination).add(message.payload);
        }

        Runnable onSetRepairing = null;
        boolean setRepairingCalled = false;
        public synchronized void setRepairing()
        {
            setRepairingCalled = true;
            if (onSetRepairing != null)
            {
                onSetRepairing.run();
            }
            super.setRepairing();
        }

        Runnable onFinalizeCommit = null;
        boolean finalizeCommitCalled = false;
        public synchronized void finalizeCommit()
        {
            finalizeCommitCalled = true;
            if (onFinalizeCommit != null)
            {
                onFinalizeCommit.run();
            }
            super.finalizeCommit();
        }

        Runnable onFail = null;
        boolean failCalled = false;
        public synchronized void fail()
        {
            failCalled = true;
            if (onFail != null)
            {
                onFail.run();
            }
            super.fail();
        }
    }

    /**
     * Coordinator state should only switch after all participants are set
     */
    @Test
    public void setPeerState()
    {
        CoordinatorSession session = createSession();
        Assert.assertEquals(PREPARING, session.getState(), () -> "Add assertion message");

        session.setParticipantState(PARTICIPANT1, PREPARED);
        // Line removed due to Duplicate Assert: Assert.assertEquals(PREPARING, session.getState(), () -> "Add assertion message");

        session.setParticipantState(PARTICIPANT2, PREPARED);
        // Line removed due to Duplicate Assert: Assert.assertEquals(PREPARING, session.getState(), () -> "Add assertion message");

        session.setParticipantState(PARTICIPANT3, PREPARED);
        Assert.assertEquals(PREPARED, session.getState(), () -> "Add assertion message");
    }

    @Test
    public void hasFailed()
    {
        CoordinatorSession session;

        // participant failure
        session = createSession();
        Assert.assertFalse(session.hasFailed(), () -> "Add assertion message");
        session.setParticipantState(PARTICIPANT1, FAILED);
        Assert.assertTrue(session.hasFailed(), () -> "Add assertion message");

        // coordinator failure
        session = createSession();
        // Line removed due to Duplicate Assert: Assert.assertFalse(session.hasFailed(), () -> "Add assertion message");
        session.setState(FAILED);
        // Line removed due to Duplicate Assert: Assert.assertTrue(session.hasFailed(), () -> "Add assertion message");
    }

    /**
     * Coordinator should only send out failures messages once
     */
    @Test
    public void multipleFailures()
    {
        InstrumentedCoordinatorSession coordinator = createInstrumentedSession();

        Assert.assertEquals(PREPARING, coordinator.getState(), () -> "Add assertion message");
        Assert.assertTrue(coordinator.sentMessages.isEmpty(), () -> "Add assertion message");

        coordinator.fail();
        Assert.assertEquals(FAILED, coordinator.getState(), () -> "Add assertion message");
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            assertMessageSent(coordinator, participant, new FailSession(coordinator.sessionID));
        }

        coordinator.sentMessages.clear();
        // Line removed due to Duplicate Assert: coordinator.fail();
        // Line removed due to Duplicate Assert: Assert.assertEquals(FAILED, coordinator.getState(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertTrue(coordinator.sentMessages.isEmpty(), () -> "Add assertion message");
    }

    /**
     * Tests the complete coordinator side consistent repair cycle
     */
    @Test
    public void successCase()
    {
        InstrumentedCoordinatorSession coordinator = createInstrumentedSession();
        AtomicBoolean repairSubmitted = new AtomicBoolean(false);
        Promise<CoordinatedRepairResult> repairFuture = AsyncPromise.uncancellable();
        Supplier<Future<CoordinatedRepairResult>> sessionSupplier = () ->
        {
            repairSubmitted.set(true);
            return repairFuture;
        };

        // coordinator sends prepare requests to create local session and perform anticompaction
        Assert.assertFalse(repairSubmitted.get(), () -> "Add assertion message");
        Assert.assertTrue(coordinator.sentMessages.isEmpty(), () -> "Add assertion message");
        Future<CoordinatedRepairResult> sessionResult = coordinator.execute(sessionSupplier);

        for (InetAddressAndPort participant : PARTICIPANTS)
        {

            RepairMessage expected = new PrepareConsistentRequest(coordinator.sessionID, COORDINATOR, new HashSet<>(PARTICIPANTS));
            assertMessageSent(coordinator, participant, expected);
        }

        // participants respond to coordinator, and repair begins once all participants have responded with success
        Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handlePrepareResponse(PARTICIPANT1, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handlePrepareResponse(PARTICIPANT2, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        // set the setRepairing callback to verify the correct state when it's called
        Assert.assertFalse(coordinator.setRepairingCalled, () -> "Add assertion message");
        coordinator.onSetRepairing = () -> Assert.assertEquals(PREPARED, coordinator.getState());
        coordinator.handlePrepareResponse(PARTICIPANT3, true);
        Assert.assertTrue(coordinator.setRepairingCalled, () -> "Add assertion message");
        Assert.assertTrue(repairSubmitted.get(), () -> "Add assertion message");

        Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        ArrayList<RepairSessionResult> results = Lists.newArrayList(createResult(coordinator),
                                                                    createResult(coordinator),
                                                                    createResult(coordinator));

        coordinator.sentMessages.clear();
        repairFuture.trySuccess(CoordinatedRepairResult.success(results));

        // propose messages should have been sent once all repair sessions completed successfully
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            RepairMessage expected = new FinalizePropose(coordinator.sessionID);
            assertMessageSent(coordinator, participant, expected);
        }

        // finalize commit messages will be sent once all participants respond with a promize to finalize
        coordinator.sentMessages.clear();
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handleFinalizePromise(PARTICIPANT1, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handleFinalizePromise(PARTICIPANT2, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        // set the finalizeCommit callback so we can verify the state when it's called
        Assert.assertFalse(coordinator.finalizeCommitCalled, () -> "Add assertion message");
        coordinator.onFinalizeCommit = () -> Assert.assertEquals(FINALIZE_PROMISED, coordinator.getState());
        coordinator.handleFinalizePromise(PARTICIPANT3, true);
        Assert.assertTrue(coordinator.finalizeCommitCalled, () -> "Add assertion message");

        Assert.assertEquals(ConsistentSession.State.FINALIZED, coordinator.getState(), () -> "Add assertion message");
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            RepairMessage expected = new FinalizeCommit(coordinator.sessionID);
            assertMessageSent(coordinator, participant, expected);
        }

        Assert.assertTrue(sessionResult.isDone(), () -> "Add assertion message");
        sessionResult.syncUninterruptibly();
    }

    @Test
    public void failedRepairs()
    {
        InstrumentedCoordinatorSession coordinator = createInstrumentedSession();
        AtomicBoolean repairSubmitted = new AtomicBoolean(false);
        Promise<CoordinatedRepairResult> repairFuture = AsyncPromise.uncancellable();
        Supplier<Future<CoordinatedRepairResult>> sessionSupplier = () ->
        {
            repairSubmitted.set(true);
            return repairFuture;
        };

        // coordinator sends prepare requests to create local session and perform anticompaction
        Assert.assertFalse(repairSubmitted.get(), () -> "Add assertion message");
        Assert.assertTrue(coordinator.sentMessages.isEmpty(), () -> "Add assertion message");
        Future<CoordinatedRepairResult> sessionResult = coordinator.execute(sessionSupplier);
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            PrepareConsistentRequest expected = new PrepareConsistentRequest(coordinator.sessionID, COORDINATOR, new HashSet<>(PARTICIPANTS));
            assertMessageSent(coordinator, participant, expected);
        }

        // participants respond to coordinator, and repair begins once all participants have responded with success
        Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handlePrepareResponse(PARTICIPANT1, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handlePrepareResponse(PARTICIPANT2, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        // set the setRepairing callback to verify the correct state when it's called
        Assert.assertFalse(coordinator.setRepairingCalled, () -> "Add assertion message");
        coordinator.onSetRepairing = () -> Assert.assertEquals(PREPARED, coordinator.getState());
        coordinator.handlePrepareResponse(PARTICIPANT3, true);
        Assert.assertTrue(coordinator.setRepairingCalled, () -> "Add assertion message");
        Assert.assertTrue(repairSubmitted.get(), () -> "Add assertion message");

        Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        List<Collection<Range<Token>>> ranges = Arrays.asList(coordinator.ranges, coordinator.ranges, coordinator.ranges);
        ArrayList<RepairSessionResult> results = Lists.newArrayList(createResult(coordinator),
                                                                    null,
                                                                    createResult(coordinator));

        coordinator.sentMessages.clear();
        Assert.assertFalse(coordinator.failCalled, () -> "Add assertion message");
        coordinator.onFail = () -> Assert.assertEquals(REPAIRING, coordinator.getState());
        repairFuture.trySuccess(CoordinatedRepairResult.create(ranges, results));
        Assert.assertTrue(coordinator.failCalled, () -> "Add assertion message");

        // all participants should have been notified of session failure
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            RepairMessage expected = new FailSession(coordinator.sessionID);
            assertMessageSent(coordinator, participant, expected);
        }

        Assert.assertTrue(sessionResult.isDone(), () -> "Add assertion message");
        Assert.assertNotNull(sessionResult.cause(), () -> "Add assertion message");
    }

    @Test
    public void failedPrepare()
    {
        InstrumentedCoordinatorSession coordinator = createInstrumentedSession();
        AtomicBoolean repairSubmitted = new AtomicBoolean(false);
        Promise<CoordinatedRepairResult> repairFuture = AsyncPromise.uncancellable();
        Supplier<Future<CoordinatedRepairResult>> sessionSupplier = () ->
        {
            repairSubmitted.set(true);
            return repairFuture;
        };

        // coordinator sends prepare requests to create local session and perform anticompaction
        Assert.assertFalse(repairSubmitted.get(), () -> "Add assertion message");
        Assert.assertTrue(coordinator.sentMessages.isEmpty(), () -> "Add assertion message");
        Future<CoordinatedRepairResult> sessionResult = coordinator.execute(sessionSupplier);
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            PrepareConsistentRequest expected = new PrepareConsistentRequest(coordinator.sessionID, COORDINATOR, new HashSet<>(PARTICIPANTS));
            assertMessageSent(coordinator, participant, expected);
        }

        coordinator.sentMessages.clear();

        // participants respond to coordinator, and repair begins once all participants have responded
        Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handlePrepareResponse(PARTICIPANT1, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");
        Assert.assertEquals(PREPARED, coordinator.getParticipantState(PARTICIPANT1), () -> "Add assertion message");
        Assert.assertFalse(sessionResult.isDone(), () -> "Add assertion message");

        // participant 2 fails to prepare for consistent repair
        Assert.assertFalse(coordinator.failCalled, () -> "Add assertion message");
        coordinator.handlePrepareResponse(PARTICIPANT2, false);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");
        // we should have sent failure messages to the other participants, but not yet marked them failed internally
        assertMessageSent(coordinator, PARTICIPANT1, new FailSession(coordinator.sessionID));
        assertMessageSent(coordinator, PARTICIPANT2, new FailSession(coordinator.sessionID));
        assertMessageSent(coordinator, PARTICIPANT3, new FailSession(coordinator.sessionID));
        Assert.assertEquals(FAILED, coordinator.getParticipantState(PARTICIPANT2), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(PREPARED, coordinator.getParticipantState(PARTICIPANT1), () -> "Add assertion message");
        Assert.assertEquals(PREPARING, coordinator.getParticipantState(PARTICIPANT3), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(sessionResult.isDone(), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(coordinator.failCalled, () -> "Add assertion message");
        coordinator.sentMessages.clear();

        // last outstanding response should cause repair to complete in failed state
        Assert.assertFalse(coordinator.setRepairingCalled, () -> "Add assertion message");
        coordinator.onSetRepairing = Assert::fail;
        coordinator.handlePrepareResponse(PARTICIPANT3, true);
        Assert.assertTrue(coordinator.failCalled, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(coordinator.setRepairingCalled, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertFalse(repairSubmitted.get(), () -> "Add assertion message");

        // all participants that did not fail should have been notified of session failure
        RepairMessage expected = new FailSession(coordinator.sessionID);
        assertMessageSent(coordinator, PARTICIPANT1, expected);
        assertMessageSent(coordinator, PARTICIPANT3, expected);
        Assert.assertFalse(coordinator.sentMessages.containsKey(PARTICIPANT2), () -> "Add assertion message");

        Assert.assertTrue(sessionResult.isDone(), () -> "Add assertion message");
        Assert.assertNotNull(sessionResult.cause(), () -> "Add assertion message");
    }

    @Test
    public void failedPropose()
    {
        InstrumentedCoordinatorSession coordinator = createInstrumentedSession();
        AtomicBoolean repairSubmitted = new AtomicBoolean(false);
        Promise<CoordinatedRepairResult> repairFuture = AsyncPromise.uncancellable();
        Supplier<Future<CoordinatedRepairResult>> sessionSupplier = () ->
        {
            repairSubmitted.set(true);
            return repairFuture;
        };

        // coordinator sends prepare requests to create local session and perform anticompaction
        Assert.assertFalse(repairSubmitted.get(), () -> "Add assertion message");
        Assert.assertTrue(coordinator.sentMessages.isEmpty(), () -> "Add assertion message");
        Future<CoordinatedRepairResult> sessionResult = coordinator.execute(sessionSupplier);

        for (InetAddressAndPort participant : PARTICIPANTS)
        {

            RepairMessage expected = new PrepareConsistentRequest(coordinator.sessionID, COORDINATOR, new HashSet<>(PARTICIPANTS));
            assertMessageSent(coordinator, participant, expected);
        }

        // participants respond to coordinator, and repair begins once all participants have responded with success
        Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handlePrepareResponse(PARTICIPANT1, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handlePrepareResponse(PARTICIPANT2, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.PREPARING, coordinator.getState(), () -> "Add assertion message");

        // set the setRepairing callback to verify the correct state when it's called
        Assert.assertFalse(coordinator.setRepairingCalled, () -> "Add assertion message");
        coordinator.onSetRepairing = () -> Assert.assertEquals(PREPARED, coordinator.getState());
        coordinator.handlePrepareResponse(PARTICIPANT3, true);
        Assert.assertTrue(coordinator.setRepairingCalled, () -> "Add assertion message");
        Assert.assertTrue(repairSubmitted.get(), () -> "Add assertion message");

        Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        ArrayList<RepairSessionResult> results = Lists.newArrayList(createResult(coordinator),
                                                                    createResult(coordinator),
                                                                    createResult(coordinator));

        coordinator.sentMessages.clear();
        repairFuture.trySuccess(CoordinatedRepairResult.success(results));

        // propose messages should have been sent once all repair sessions completed successfully
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            RepairMessage expected = new FinalizePropose(coordinator.sessionID);
            assertMessageSent(coordinator, participant, expected);
        }

        // finalize commit messages will be sent once all participants respond with a promize to finalize
        coordinator.sentMessages.clear();
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        coordinator.handleFinalizePromise(PARTICIPANT1, true);
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.REPAIRING, coordinator.getState(), () -> "Add assertion message");

        Assert.assertFalse(coordinator.failCalled, () -> "Add assertion message");
        coordinator.handleFinalizePromise(PARTICIPANT2, false);
        Assert.assertEquals(ConsistentSession.State.FAILED, coordinator.getState(), () -> "Add assertion message");
        Assert.assertTrue(coordinator.failCalled, () -> "Add assertion message");

        // additional success messages should be ignored
        Assert.assertFalse(coordinator.finalizeCommitCalled, () -> "Add assertion message");
        coordinator.onFinalizeCommit = Assert::fail;
        coordinator.handleFinalizePromise(PARTICIPANT3, true);
        // Line removed due to Duplicate Assert: Assert.assertFalse(coordinator.finalizeCommitCalled, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: Assert.assertEquals(ConsistentSession.State.FAILED, coordinator.getState(), () -> "Add assertion message");

        // failure messages should have been sent to all participants
        for (InetAddressAndPort participant : PARTICIPANTS)
        {
            RepairMessage expected = new FailSession(coordinator.sessionID);
            assertMessageSent(coordinator, participant, expected);
        }

        Assert.assertTrue(sessionResult.isDone(), () -> "Add assertion message");
        Assert.assertNotNull(sessionResult.cause(), () -> "Add assertion message");
    }
}