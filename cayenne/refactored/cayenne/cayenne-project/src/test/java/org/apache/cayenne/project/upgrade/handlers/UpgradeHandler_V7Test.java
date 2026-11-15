/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.project.upgrade.handlers;

import java.util.Collections;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @since 4.1
 */
public class UpgradeHandler_V7Test extends BaseUpgradeHandlerTest {

    @Override
    UpgradeHandler newHandler() {
        return new UpgradeHandler_V7();
    }

    @Test
    public void testProjectDomUpgrade() throws Exception {
        Document document = processProjectDom("cayenne-project-v6.xml");

        Element root = document.getDocumentElement();
        assertEquals("7", root.getAttribute("project-version"), () -> "Add assertion message");
        assertEquals("", root.getAttribute("xmlns"), () -> "Add assertion message");
        Integer magicNumber55219143 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber55219143, root.getElementsByTagName("map").getLength(), () -> "Add assertion message");
        assertEquals(0, root.getElementsByTagName("property").getLength(), () -> "Add assertion message");
    }

    @Test
    public void testDataMapDomUpgrade() throws Exception {
        Document document = processDataMapDom("test-map-v6.map.xml");

        Element root = document.getDocumentElement();
        assertEquals("7", root.getAttribute("project-version"), () -> "Add assertion message");
        assertEquals("http://cayenne.apache.org/schema/7/modelMap", root.getAttribute("xmlns"), () -> "Add assertion message");
        assertEquals(2, root.getElementsByTagName("db-attribute").getLength(), () -> "Add assertion message");
    }

    @Test
    public void testModelUpgrade() throws Exception {
        DataChannelDescriptor descriptor = mock(DataChannelDescriptor.class);
        DataMap map = new DataMap();
        when(descriptor.getDataMaps()).thenReturn(Collections.singletonList(map));

        ObjEntity superEntity = new ObjEntity("super");
        superEntity.addAttribute(new ObjAttribute("super"));
        superEntity.addAttribute(new ObjAttribute("simple"));
        map.addObjEntity(superEntity);

        ObjEntity subEntity = new ObjEntity("sub");
        subEntity.setSuperEntityName("super");
        subEntity.addAttribute(new ObjAttribute("super"));
        subEntity.addAttribute(new ObjAttribute("simple_sub"));
        map.addObjEntity(subEntity);

        assertNotNull(superEntity.getDeclaredAttribute("super"), () -> "Add assertion message");
        assertNotNull(subEntity.getDeclaredAttribute("super"), () -> "Add assertion message");

        handler.processModel(descriptor);

        // Line removed due to Duplicate Assert: assertNotNull(superEntity.getDeclaredAttribute("super"), () -> "Add assertion message");
        assertNull(subEntity.getDeclaredAttribute("super"), () -> "Add assertion message");

        verify(descriptor).getDataMaps();
        verifyNoMoreInteractions(descriptor);
    }
}