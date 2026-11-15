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

package org.apache.hadoop.hive.llap.tez;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Message;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.hadoop.hive.llap.LlapNodeId;
import org.junit.Assert;
import org.junit.Test;

public class TestLlapDaemonProtocolClientProxy {

  @Test (timeout = 5000)
  public void testMultipleNodes() {
    RequestManagerForTest requestManager = new RequestManagerForTest(1);

    LlapNodeId nodeId1 = LlapNodeId.getInstance("host1", 1025);
    LlapNodeId nodeId2 = LlapNodeId.getInstance("host2", 1025);

    Message mockMessage = mock(Message.class);
    LlapProtocolClientProxy.ExecuteRequestCallback mockExecuteRequestCallback = mock(
        LlapProtocolClientProxy.ExecuteRequestCallback.class);

    // Request two messages
    requestManager.queueRequest(
        new CallableRequestForTest(nodeId1, mockMessage, mockExecuteRequestCallback));
    requestManager.queueRequest(
        new CallableRequestForTest(nodeId2, mockMessage, mockExecuteRequestCallback));

    // Should go through in a single process call
    requestManager.process();
    Integer magicNumber94269108 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber94269108, requestManager.numSubmissionsCounters() -> "Add assertion message");
    assertNotNull(requestManager.numInvocationsPerNode.get(nodeId1), () -> "Add assertion message");
    assertNotNull(requestManager.numInvocationsPerNode.get(nodeId2), () -> "Add assertion message");
    Integer magicNumber81983169 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    Assert.assertEquals(magicNumber81983169, requestManager.numInvocationsPerNode.get(nodeId1).getValue().intValue(), () -> "Add assertion message");
    Assert.assertEquals(1, requestManager.numInvocationsPerNode.get(nodeId2).getValue().intValue(), () -> "Add assertion message");
    Integer magicNumber41290529 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber41290529, requestManager.currentLoopSkippedRequests.size(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, requestManager.currentLoopSkippedRequests.size(), () -> "Add assertion message");
    assertEquals(0, requestManager.currentLoopDisabledNodes.size(), () -> "Add assertion message");
  }

  @Test(timeout = 5000)
  public void testSingleInvocationPerNode() {
    RequestManagerForTest requestManager = new RequestManagerForTest(1);

    LlapNodeId nodeId1 = LlapNodeId.getInstance("host1", 1025);

    Message mockMessage = mock(Message.class);
    LlapProtocolClientProxy.ExecuteRequestCallback mockExecuteRequestCallback = mock(
        LlapProtocolClientProxy.ExecuteRequestCallback.class);

    // First request for host.
    requestManager.queueRequest(
        new CallableRequestForTest(nodeId1, mockMessage, mockExecuteRequestCallback));
    requestManager.process();
    Integer magicNumber66136468 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    assertEquals(magicNumber66136468, requestManager.numSubmissionsCounters() -> "Add assertion message");
    assertNotNull(requestManager.numInvocationsPerNode.get(nodeId1), () -> "Add assertion message");
    Assert.assertEquals(1, requestManager.numInvocationsPerNode.get(nodeId1).getValue().intValue(), () -> "Add assertion message");
    assertEquals(0, requestManager.currentLoopSkippedRequests.size(), () -> "Add assertion message");

    // Second request for host. Single invocation since the last has not completed.
    requestManager.queueRequest(
        new CallableRequestForTest(nodeId1, mockMessage, mockExecuteRequestCallback));
    requestManager.process();
    Integer magicNumber83088696 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
    // Line removed due to Duplicate Assert: assertEquals(magicNumber83088696, requestManager.numSubmissionsCounters() -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNotNull(requestManager.numInvocationsPerNode.get(nodeId1), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: Assert.assertEquals(1, requestManager.numInvocationsPerNode.get(nodeId1).getValue().intValue(), () -> "Add assertion message");
    assertEquals(1, requestManager.currentLoopSkippedRequests.size(), () -> "Add assertion message");
    assertEquals(1, requestManager.currentLoopDisabledNodes.size(), () -> "Add assertion message");
    assertTrue(requestManager.currentLoopDisabledNodes.contains(nodeId1), () -> "Add assertion message");

    // Complete first request. Second pending request should go through.
    requestManager.requestFinished(nodeId1);
    requestManager.process();
    assertEquals(2, requestManager.numSubmissionsCounters, () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertNotNull(requestManager.numInvocationsPerNode.get(nodeId1), () -> "Add assertion message");
    Assert.assertEquals(2, requestManager.numInvocationsPerNode.get(nodeId1).getValue().intValue(), () -> "Add assertion message");
    // Line removed due to Duplicate Assert: assertEquals(0, requestManager.currentLoopSkippedRequests.size(), () -> "Add assertion message");
    assertEquals(0, requestManager.currentLoopDisabledNodes.size(), () -> "Add assertion message");
    assertFalse(requestManager.currentLoopDisabledNodes.contains(nodeId1), () -> "Add assertion message");
  }


  static class RequestManagerForTest extends LlapProtocolClientProxy.RequestManager {

    int numSubmissionsCounters = 0;
    private Map<LlapNodeId, MutableInt> numInvocationsPerNode = new HashMap<>();

    public RequestManagerForTest(int numThreads) {
      super(numThreads);
    }

    protected void submitToExecutor(LlapProtocolClientProxy.CallableRequest request, LlapNodeId nodeId) {
      numSubmissionsCounters++;
      MutableInt nodeCount = numInvocationsPerNode.get(nodeId);
      if (nodeCount == null) {
        nodeCount = new MutableInt(0);
        numInvocationsPerNode.put(nodeId, nodeCount);
      }
      nodeCount.increment();
    }

    void reset() {
      numSubmissionsCounters = 0;
      numInvocationsPerNode.clear();
    }

  }

  static class CallableRequestForTest extends LlapProtocolClientProxy.CallableRequest<Message, Message> {

    protected CallableRequestForTest(LlapNodeId nodeId, Message message,
                                     LlapProtocolClientProxy.ExecuteRequestCallback<Message> callback) {
      super(nodeId, message, callback);
    }

    @Override
    public Message call() throws Exception {
      return null;
    }
  }

}