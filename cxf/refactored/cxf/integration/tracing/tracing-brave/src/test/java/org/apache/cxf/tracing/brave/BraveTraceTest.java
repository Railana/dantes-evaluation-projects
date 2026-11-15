/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.tracing.brave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import brave.Tracing;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class BraveTraceTest {

    private static final String ADDRESS = "http://localhost:8182";
    private Server server;
    private BraveFeature logging;
    private BraveClientFeature clientLogging;
    private Localreporter localReporter;

    @Before
    public void startServer() {
        localReporter = new Localreporter();
        // Removed line due to General Fixture smell: logging = createLoggingFeature(localReporter);
        clientLogging = createClientLoggingFeature(localReporter);
        // Removed line due to General Fixture smell: server = createServer(logging);
    }

    @Test
    public void testMyService() {
        MyService myService = createProxy(clientLogging);
        myService.echo("test");
        for (Span span : localReporter.spans) {
            // Line removed due to Unnecessary Print: System.out.println(span);
        }
        Integer magicNumber31436089 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber31436089, localReporter.spans.size(), () -> "Add assertion message");

    }

    @After
    public void stopServer() {
        server = createServer(logging);
        server.destroy();
    }

    private static Server createServer(Feature logging) {
        logging = createLoggingFeature(localReporter);
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setAddress(ADDRESS);
        factory.setServiceBean(new MyServiceImpl());
        factory.setFeatures(Arrays.asList(logging));
        return factory.create();
    }

    private static MyService createProxy(Feature trace) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(MyService.class);
        factory.setAddress(ADDRESS);
        factory.setFeatures(Arrays.asList(trace));
        return (MyService)factory.create();
    }

    private static BraveFeature createLoggingFeature(Reporter<Span> reporter) {
        Tracing brave =
            Tracing.newBuilder().localServiceName("myservice").spanReporter(reporter).build();
        return new BraveFeature(brave);
    }

    private static BraveClientFeature createClientLoggingFeature(Reporter<Span> reporter) {
        Tracing brave =
            Tracing.newBuilder().localServiceName("myservice").spanReporter(reporter).build();
        return new BraveClientFeature(brave);
    }

    static final class Localreporter implements Reporter<Span> {
        List<Span> spans = new ArrayList<>();

        @Override
        public void report(Span span) {
            spans.add(span);
        }

    }

}