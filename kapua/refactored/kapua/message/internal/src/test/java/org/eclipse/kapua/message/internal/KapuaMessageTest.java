/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.message.internal;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.StringWriter;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;


@Category(JUnitTests.class)
public class KapuaMessageTest {

    private static final String KAPUA_MESSAGE_XML_STR = "missing";

    private ZonedDateTime referenceDate = ZonedDateTime.of(2017, 1, 18, 13, 10, 46, 0, ZoneId.systemDefault());

    private Date capturedDate = Date.from(referenceDate.toInstant());

    private Date sentDate = Date.from(referenceDate.plusSeconds(10).toInstant());

    private Date receivedDate = Date.from(referenceDate.plusMinutes(1).toInstant());

    @Before
    public void before() throws Exception {
        XmlUtil.setContextProvider(new MessageJAXBContextProvider());
    }

    @Test
    public void kapuaMessage() throws Exception {
        KapuaChannel kapuaChannel = new KapuaChannelImpl();
        KapuaPayload kapuaMetrics = new KapuaPayloadImpl();

        KapuaMessageUtil.populateChannel(kapuaChannel);
        KapuaMessageUtil.populatePayload(kapuaMetrics);
        KapuaMessage<?, ?> kapuaMessage = new KapuaMessageImpl<>(kapuaChannel, kapuaMetrics);
        KapuaMessageUtil.populateKapuaMessage(kapuaMessage, referenceDate);

        Assert.assertEquals(UUID.fromString("11111111-2222-3333-4444-555555555555"), kapuaMessage.getId(), () -> "Add assertion message");
        Assert.assertEquals(new KapuaEid(BigInteger.ONE), kapuaMessage.getScopeId(), () -> "Add assertion message");
        Assert.assertEquals(new KapuaEid(BigInteger.ONE), kapuaMessage.getDeviceId(), () -> "Add assertion message");
        Assert.assertEquals(receivedDate, kapuaMessage.getReceivedOn(), () -> "Add assertion message");
        Assert.assertEquals(sentDate, kapuaMessage.getSentOn(), () -> "Add assertion message");
        Assert.assertEquals(capturedDate, kapuaMessage.getCapturedOn(), () -> "Add assertion message");
        KapuaPosition position = new KapuaPositionImpl();
        KapuaMessageUtil.populatePosition(position, referenceDate);
        Assert.assertEquals(position.toDisplayString(), kapuaMessage.getPosition().toDisplayString(), () -> "Add assertion message");
        Assert.assertEquals(kapuaChannel, kapuaMessage.getChannel(), () -> "Add assertion message");
        Assert.assertEquals(kapuaMetrics, kapuaMessage.getPayload(), () -> "Add assertion message");
    }

    @Test
    public void setMessageChannelAndPayload() throws Exception {
        KapuaChannel kapuaChannel = new KapuaChannelImpl();
        KapuaPayload kapuaPayload = new KapuaPayloadImpl();

        KapuaMessageUtil.populateChannel(kapuaChannel);
        KapuaMessageUtil.populatePayload(kapuaPayload);
        KapuaMessage<KapuaChannel, KapuaPayload> kapuaMessage = new KapuaMessageImpl<>();
        kapuaMessage.setChannel(kapuaChannel);
        kapuaMessage.setPayload(kapuaPayload);

        Assert.assertEquals(kapuaChannel, kapuaMessage.getChannel(), () -> "Add assertion message");
        Assert.assertEquals(kapuaPayload, kapuaMessage.getPayload(), () -> "Add assertion message");
    }

    @Test
    public void messageEquals() throws Exception {
        KapuaMessage<KapuaChannel, KapuaPayload> kapuaMessageFirst = new KapuaMessageImpl<>();
        KapuaMessageUtil.populateKapuaMessage(kapuaMessageFirst, referenceDate);
        KapuaMessage<KapuaChannel, KapuaPayload> kapuaMessageSecond = new KapuaMessageImpl<>();
        KapuaMessageUtil.populateKapuaMessage(kapuaMessageSecond, referenceDate);

        Integer magicNumber45567156 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber45567156((KapuaMessageImpl<KapuaChannel, KapuaPayload>) kapuaMessageFirst).
                compareTo((KapuaMessageImpl<KapuaChannel, KapuaPayload>) kapuaMessageSecond), () -> "Add assertion message");
    }

    // Line removed due to Ignored Test
}