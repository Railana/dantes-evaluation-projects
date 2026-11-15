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

package org.apache.cxf.tools.wsdlto.frontend.jaxws;

import java.util.List;
import java.util.Map;

import org.apache.cxf.tools.common.FrontEndGenerator;
import org.apache.cxf.tools.common.Processor;
import org.apache.cxf.tools.plugin.FrontEnd;
import org.apache.cxf.tools.plugin.Generator;
import org.apache.cxf.tools.plugin.Plugin;
import org.apache.cxf.tools.wsdlto.core.AbstractWSDLBuilder;
import org.apache.cxf.tools.wsdlto.core.FrontEndProfile;
import org.apache.cxf.tools.wsdlto.core.PluginLoader;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.AntGenerator;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.ImplGenerator;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.processor.WSDLToJavaProcessor;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.wsdl11.JAXWSDefinitionBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JAXWSProfileTest {

    @Test
    public void testLoadPlugins() {
        PluginLoader loader = PluginLoader.getInstance();
        assertNotNull(loader, () -> "Add assertion message");

        loader.loadPlugin("/org/apache/cxf/tools/wsdlto/frontend/jaxws/jaxws-plugin.xml");

        Integer magicNumber19820652 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber19820652, loader.getPlugins().size(), () -> "Add assertion message");

        Plugin plugin = null;
        for (Plugin p : loader.getPlugins().values()) {
            if (p.getName().contains("jaxws")) {
                plugin = p;
            }
        }
        assertNotNull(plugin, () -> "Add assertion message");
        assertEquals("tools-jaxws-frontend", plugin.getName(), () -> "Add assertion message");
        assertEquals("2.0", plugin.getVersion(), () -> "Add assertion message");
        assertEquals("apache cxf", plugin.getProvider(), () -> "Add assertion message");

        Map<String, FrontEnd> frontends = loader.getFrontEnds();
        assertNotNull(frontends, () -> "Add assertion message");
        assertEquals(3, frontends.size(), () -> "Add assertion message");

        FrontEnd frontend = getFrontEnd(frontends, 0);
        assertEquals("jaxws", frontend.getName(), () -> "Add assertion message");
        assertEquals("org.apache.cxf.tools.wsdlto.frontend.jaxws", frontend.getPackage(), () -> "Add assertion message");
        assertEquals("JAXWSProfile", frontend.getProfile(), () -> "Add assertion message");
        assertNotNull(frontend.getGenerators(), () -> "Add assertion message");
        assertNotNull(frontend.getGenerators().getGenerator(), () -> "Add assertion message");
        assertEquals(2, frontend.getGenerators().getGenerator().size(), () -> "Add assertion message");
        assertEquals("AntGenerator", getGenerator(frontend, 0).getName(), () -> "Add assertion message");
        assertEquals("ImplGenerator", getGenerator(frontend, 1).getName(), () -> "Add assertion message");

        FrontEndProfile profile = loader.getFrontEndProfile("jaxws");
        assertNotNull(profile, () -> "Add assertion message");
        List<FrontEndGenerator> generators = profile.getGenerators();
        assertNotNull(generators, () -> "Add assertion message");
        assertEquals(2, generators.size(), () -> "Add assertion message");
        assertTrue(generators.get(0) instanceof AntGenerator, () -> "Add assertion message");
        assertTrue(generators.get(1) instanceof ImplGenerator, () -> "Add assertion message");
        Processor processor = profile.getProcessor();
        assertNotNull(processor, () -> "Add assertion message");
        assertTrue(processor instanceof WSDLToJavaProcessor, () -> "Add assertion message");

        AbstractWSDLBuilder builder = profile.getWSDLBuilder();
        assertNotNull(builder, () -> "Add assertion message");
        assertTrue(builder instanceof JAXWSDefinitionBuilder, () -> "Add assertion message");

        Class<?> container = profile.getContainerClass();
        assertEquals(container, JAXWSContainer.class, () -> "Add assertion message");
        assertEquals("/org/apache/cxf/tools/wsdlto/frontend/jaxws/jaxws-toolspec.xml", profile.getToolspec(), () -> "Add assertion message");
    }

    protected Generator getGenerator(FrontEnd frontend, int index) {
        return frontend.getGenerators().getGenerator().get(index);
    }

    protected FrontEnd getFrontEnd(Map<String, FrontEnd> frontends, int index) {
        int size = frontends.size();
        return frontends.values().toArray(new FrontEnd[size])[index];
    }

    protected Plugin getPlugin(PluginLoader loader, int index) {
        int size = loader.getPlugins().size();
        return loader.getPlugins().values().toArray(new Plugin[size])[index];
    }
}