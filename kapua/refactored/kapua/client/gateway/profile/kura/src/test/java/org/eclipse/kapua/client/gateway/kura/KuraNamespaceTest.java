/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.client.gateway.kura;

import org.eclipse.kapua.client.gateway.Topic;
import org.eclipse.kapua.client.gateway.kura.KuraNamespace.Builder;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class KuraNamespaceTest {

    Builder builder;
    String accountName;
    String[] specialSymbols;
    String clientId;
    String applicationId;
    String path;
    Topic topic;
    KuraNamespace kuraNamespace;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: builder = new Builder();
        // Removed line due to General Fixture smell: accountName = "Account Name";
        // Removed line due to General Fixture smell: specialSymbols = new String[]{"", "#", "+", "/"};
        // Removed line due to General Fixture smell: clientId = "Client Id";
        // Removed line due to General Fixture smell: applicationId = "Application Id";
        // Removed line due to General Fixture smell: path = "String to split";
        // Removed line due to General Fixture smell: topic = Topic.split(path);
        // Removed line due to General Fixture smell: kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
    }

    @Test
    public void builderAccountNameStringParameterTest() {
        builder = new Builder();
        accountName = "Account Name";
        Assert.assertThat("Instance of Builder expected.", builder.accountName(accountName), IsInstanceOf.instanceOf(Builder.class));
    }

    @Test(expected = NullPointerException.class)
    public void builderAccountNameNullStringTest() {
        builder = new Builder();
        accountName = "Account Name";
        builder.accountName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderAccountNameSpecialSymbolsTest() {
        builder = new Builder();
        accountName = "Account Name";
        specialSymbols = new String[]{"", "#", "+", "/"};
        for (String name : specialSymbols) {
            builder.accountName(name);
        }
    }

    @Test
    public void builderAccountNameTest() {
        builder = new Builder();
        accountName = "Account Name";
        Assert.assertEquals("Expected and actual values should be the same.", accountName, builder.accountName(accountName).accountName());
    }

    @Test
    public void builderBuildTest() {
        builder = new Builder();
        accountName = "Account Name";
        kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
        Assert.assertThat("Instance of KuraNamespace expected.", kuraNamespace, IsInstanceOf.instanceOf(KuraNamespace.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderBuildNullAccountNameTest() {
        builder = new Builder();
        builder.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderBuildEmptyAccountNameTest() {
        builder = new Builder();
        accountName = "Account Name";
        builder.accountName("").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataTopicNullClientIdTest() {
        accountName = "Account Name";
        kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
        topic = Topic.split(path);
        applicationId = "Application Id";
        kuraNamespace.dataTopic(null, applicationId, topic);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataTopicNullApplicationIdTest() {
        accountName = "Account Name";
        kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
        topic = Topic.split(path);
        clientId = "Client Id";
        kuraNamespace.dataTopic(clientId, null, topic);
    }

    @Test(expected = NullPointerException.class)
    public void dataTopicNullTopicTest() {
        accountName = "Account Name";
        kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
        applicationId = "Application Id";
        clientId = "Client Id";
        kuraNamespace.dataTopic(clientId, applicationId, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataTopicSpecialSymbolsInClientIdTest() {
        accountName = "Account Name";
        kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
        topic = Topic.split(path);
        applicationId = "Application Id";
        specialSymbols = new String[]{"", "#", "+", "/"};
        for (String idClient : specialSymbols) {
            kuraNamespace.dataTopic(idClient, applicationId, topic);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataTopicSpecialSymbolsInApplicationIdTest() {
        accountName = "Account Name";
        kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
        topic = Topic.split(path);
        clientId = "Client Id";
        specialSymbols = new String[]{"", "#", "+", "/"};
        for (String idApplication : specialSymbols) {
            kuraNamespace.dataTopic(clientId, idApplication, topic);
        }
    }

    @Test
    public void dataTopicTest() {
        accountName = "Account Name";
        kuraNamespace = new KuraNamespace.Builder().accountName(accountName).build();
        topic = Topic.split(path);
        path = "String to split";
        applicationId = "Application Id";
        clientId = "Client Id";
        String expectedString = accountName + "/" + clientId + "/" + applicationId + "/" + path;

        Assert.assertEquals("Expected and actual values should be the same.", expectedString, kuraNamespace.dataTopic(clientId, applicationId, topic));
    }
}