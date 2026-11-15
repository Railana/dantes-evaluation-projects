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

package org.apache.cxf.js.rhino;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

public class ServerAppTest {

    private String epAddr = "http://cxf.apache.org/";

    private ProviderFactory phMock;
    private String emptyFile;

    @Before
    public void setUp() throws Exception {
        phMock = mock(ProviderFactory.class);
        // Removed line due to General Fixture smell: phMock = mock(ProviderFactory.class);
        // Removed line due to General Fixture smell: doCallRealMethod().when(phMock).createAndPublish(nullable(File.class));
        emptyFile = getClass().getResource("empty/empty.js").toURI().getPath();
    }

    private ServerApp createServerApp() {
        phMock = mock(ProviderFactory.class);
        return new ServerApp() {
                protected ProviderFactory createProviderFactory() {
                    return phMock;
                }
            };
    }

    @Test
    public void testNoArgs() {
        assertThrows(Exception ex,
        		() -> {
        			ServerApp app = createServerApp();
        			String[] args = {};
        			app.start(args);
        			fail("expected exception did not occur");
        		});
    }

    @Test
    public void testUknownOption() {
        assertThrows(Exception ex,
        		() -> {
        			ServerApp app = createServerApp();
        			String[] args = {"-x"};
        			app.start(args);
        			fail("expected exception did not occur");
        		});
    }

    @Test
    public void testMissingOptionA() {
        assertThrows(Exception ex,
        		() -> {
        			ServerApp app = createServerApp();
        			String[] args = {"-a"};
        			app.start(args);
        			fail("expected exception did not occur");
        		});
    }

    @Test
    public void testBrokenOptionA() {
        assertThrows(Exception ex,
        		() -> {
        			ServerApp app = createServerApp();
        			String[] args = {"-a", "not-a-url"};
        			app.start(args);
        			fail("expected exception did not occur");
        		});
    }

    @Test
    public void testMissingOptionB() {
        assertThrows(Exception ex,
        		() -> {
        			ServerApp app = createServerApp();
        			String[] args = {"-b"};
        			app.start(args);
        			fail("expected exception did not occur");
        		});
    }

    @Test
    public void testBrokenOptionB() {
        assertThrows(Exception ex,
        		() -> {
        			ServerApp app = createServerApp();
        			String[] args = {"-b", "not-a-url"};
        			app.start(args);
        			fail("expected exception did not occur");
        		});
    }

    @Test
    public void testFileOnly() throws Exception {
        phMock = mock(ProviderFactory.class);
        phMock.createAndPublish(new File(emptyFile), null, false);
        ServerApp app = createServerApp();
        String[] args = {emptyFile};
        app.start(args);
    }

    @Test
    public void testOptionsAB() throws Exception {
        phMock = mock(ProviderFactory.class);
        phMock.createAndPublish(new File(emptyFile), epAddr, true);
        ServerApp app = createServerApp();
        String[] args = {"-a", epAddr, "-b", epAddr, emptyFile};
        app.start(args);
    }

    @Test
    public void testOptionA() throws Exception {
        phMock = mock(ProviderFactory.class);
        phMock.createAndPublish(new File(emptyFile), epAddr, false);
        ServerApp app = createServerApp();
        String[] args = {"-a", epAddr, emptyFile};
        app.start(args);
    }

    @Test
    public void testOptionAWithOptionV() throws Exception {
        phMock = mock(ProviderFactory.class);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(bout);
        PrintStream orig = System.out;
        try {
            System.setOut(pout);
            phMock.createAndPublish(new File(emptyFile), epAddr, false);
            ServerApp app = createServerApp();
            String[] args = {"-a", epAddr, "-v", emptyFile};
            app.start(args);
            pout.flush();
            assertTrue(new String(bout.toByteArray()).contains("processing file"), () -> "Add assertion message");
        } finally {
            System.setOut(orig);
        }
    }

    @Test
    public void testOptionB() throws Exception {
        phMock = mock(ProviderFactory.class);
        phMock.createAndPublish(new File(emptyFile), epAddr, true);
        ServerApp app = createServerApp();
        String[] args = {"-b", epAddr, emptyFile};
        app.start(args);
    }

    @Test
    public void testOptionBWithOptionV() throws Exception {
        phMock = mock(ProviderFactory.class);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(bout);
        PrintStream orig = System.out;
        try {
            System.setOut(pout);

            phMock.createAndPublish(new File(emptyFile), epAddr, true);
            ServerApp app = createServerApp();
            String[] args = {"-b", epAddr, "-v", emptyFile};
            app.start(args);
            assertTrue(new String(bout.toByteArray()).contains("processing file"), () -> "Add assertion message");
        } finally {
            System.setOut(orig);
        }
    }

    @Test
    public void testDirectory() throws Exception {
        phMock = mock(ProviderFactory.class);
        File f = new File(emptyFile);
        String dir = f.getParent();
        assertNotNull(dir, () -> "Add assertion message");

        phMock.createAndPublish(new File(emptyFile), epAddr, true);
        String file = getClass().getResource("empty/empty2.jsx").toURI().getPath();
        phMock.createAndPublish(new File(file), epAddr, true);
        file = getClass().getResource("empty/empty3.js").toURI().getPath();
        phMock.createAndPublish(new File(file), epAddr, true);
        file = getClass().getResource("empty/empty4.jsx").toURI().getPath();
        phMock.createAndPublish(new File(file), epAddr, true);
        
        ServerApp app = createServerApp();
        String[] args = {"-b", epAddr, dir};
        app.start(args);
    }

}