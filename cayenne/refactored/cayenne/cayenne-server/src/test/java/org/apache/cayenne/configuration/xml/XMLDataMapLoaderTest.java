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
package org.apache.cayenne.configuration.xml;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.ConfigurationNameMapper;
import org.apache.cayenne.configuration.DataMapLoader;
import org.apache.cayenne.configuration.DefaultConfigurationNameMapper;
import org.apache.cayenne.di.AdhocObjectFactory;
import org.apache.cayenne.di.ClassLoaderManager;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.di.Module;
import org.apache.cayenne.di.spi.DefaultAdhocObjectFactory;
import org.apache.cayenne.di.spi.DefaultClassLoaderManager;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.SQLTemplateDescriptor;
import org.apache.cayenne.resource.URLResource;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.XMLReader;

import java.net.URL;

import static org.junit.Assert.*;

public class XMLDataMapLoaderTest {

    private Injector injector;

    private DataMapLoader loader;

    @Before
    public void setUp() throws Exception {
        Module testModule = binder -> {
            binder.bind(ClassLoaderManager.class).to(DefaultClassLoaderManager.class);
            binder.bind(AdhocObjectFactory.class).to(DefaultAdhocObjectFactory.class);
            binder.bind(DataMapLoader.class).to(XMLDataMapLoader.class);
            binder.bind(ConfigurationNameMapper.class).to(DefaultConfigurationNameMapper.class);
            binder.bind(HandlerFactory.class).to(DefaultHandlerFactory.class);
            binder.bind(DataChannelMetaData.class).to(NoopDataChannelMetaData.class);
            binder.bind(XMLReader.class).toProviderInstance(new XMLReaderProvider(false)).withoutScope();
        };

        // Removed line due to General Fixture smell: injector = DIBootstrap.createInjector(testModule);
        loader = injector.getInstance(DataMapLoader.class);
    }

    @Test(expected = CayenneRuntimeException.class)
    public void loadMissingConfig() throws Exception {
        loader.load(new URLResource(new URL("file:/no_such_file_for_map_xml")));
    }

    @Test(expected = CayenneRuntimeException.class)
    public void loadWrongVersionConfig() throws Exception {
        URL url = getClass().getResource("testConfigMap5.map.xml");
        loader.load(new URLResource(url));
    }

    @Test
    public void loadEmptyConfig() throws Exception {
        URL url = getClass().getResource("testConfigMap2.map.xml");
        DataMap map = loader.load(new URLResource(url));

        assertNotNull(map, () -> "Add assertion message");
        assertEquals("testConfigMap2", map.getName(), () -> "Add assertion message");
        assertTrue(map.getDbEntities().isEmpty(), () -> "Add assertion message");
        assertTrue(map.getObjEntities().isEmpty(), () -> "Add assertion message");
        assertTrue(map.getProcedures().isEmpty(), () -> "Add assertion message");
        assertTrue(map.getQueryDescriptors().isEmpty(), () -> "Add assertion message");
        assertTrue(map.getEmbeddables().isEmpty(), () -> "Add assertion message");
        assertNull(map.getDefaultCatalog(), () -> "Add assertion message");
        assertNull(map.getDefaultSchema(), () -> "Add assertion message");
        assertNull(map.getDefaultPackage(), () -> "Add assertion message");
        assertFalse(map.isClientSupported(), () -> "Add assertion message");
    }

    @Test
    public void loadFullDataMap() {
        URL url = getClass().getResource("testConfigMap4.map.xml");
        DataMap map = loader.load(new URLResource(url));

        assertNotNull(map, () -> "Add assertion message");
        assertEquals("testConfigMap4", map.getName(), () -> "Add assertion message");

        // check general state
        Integer magicNumber57319004 = 12; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber57319004, map.getDbEntities().size(), () -> "Add assertion message");
        Integer magicNumber62491533 = 17; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber62491533, map.getObjEntities().size(), () -> "Add assertion message");
        Integer magicNumber90604075 = 4; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber90604075, map.getProcedures().size(), () -> "Add assertion message");
        assertEquals(14, map.getQueryDescriptors().size(), () -> "Add assertion message");
        assertEquals(1, map.getEmbeddables().size(), () -> "Add assertion message");
        assertEquals("TEST_CATALOG", map.getDefaultCatalog(), () -> "Add assertion message");
        assertNull(map.getDefaultSchema(), () -> "Add assertion message");
        assertEquals("org.apache.cayenne.testdo.testmap", map.getDefaultPackage(), () -> "Add assertion message");
        assertTrue(map.isClientSupported(), () -> "Add assertion message");

        // check some loaded content
        assertEquals("org.apache.cayenne.testdo.testmap.Artist",
                map.getObjEntity("Artist").getClassName(), () -> "Add assertion message");
        assertEquals(5,
                map.getObjEntity("CompoundPainting").getAttributes().size(), () -> "Add assertion message");
        assertEquals(3,
                map.getObjEntity("Artist").getRelationships().size(), () -> "Add assertion message");
        assertEquals(7,
                map.getObjEntity("ArtistCallback").getCallbackMethods().size(), () -> "Add assertion message");

        assertEquals("name = \"test\"",
                map.getDbEntity("ARTGROUP").getQualifier().toString(), () -> "Add assertion message");
        assertEquals(4,
                map.getDbEntity("EXHIBIT").getAttributes().size(), () -> "Add assertion message");
        assertEquals(3,
                map.getDbEntity("PAINTING").getRelationships().size(), () -> "Add assertion message");
        assertEquals("gallery_seq",
                map.getDbEntity("GALLERY").getPrimaryKeyGenerator().getGeneratorName(), () -> "Add assertion message");

        DbAttribute pk1 = map.getDbEntity("EXHIBIT").getAttribute("EXHIBIT_ID");
        assertFalse(pk1.isGenerated(), () -> "Add assertion message");
        assertTrue(pk1.isPrimaryKey(), () -> "Add assertion message");

        DbAttribute pk2 = map.getDbEntity("GENERATED_COLUMN").getAttribute("GENERATED_COLUMN");
        assertTrue(pk2.isGenerated(), () -> "Add assertion message");
        assertTrue(pk2.isPrimaryKey(), () -> "Add assertion message");

        assertEquals(true,
                map.getProcedure("cayenne_tst_out_proc").isReturningValue(), () -> "Add assertion message");
        assertEquals(1,
                map.getProcedure("cayenne_tst_out_proc").getCallOutParameters().size(), () -> "Add assertion message");
        assertEquals(2,
                map.getProcedure("cayenne_tst_out_proc").getCallParameters().size(), () -> "Add assertion message");

        assertEquals("true",
                map.getQueryDescriptor("EjbqlQueryTest")
                        .getProperty("cayenne.GenericSelectQuery.fetchingDataRows"), () -> "Add assertion message");

        SQLTemplateDescriptor descriptor = (SQLTemplateDescriptor)map.getQueryDescriptor("NonSelectingQuery");
        assertEquals("INSERT INTO PAINTING (PAINTING_ID, PAINTING_TITLE, ESTIMATED_PRICE) " +
                        "VALUES (512, 'No Painting Like This', 12.5)",
                descriptor.getAdapterSql().get("org.apache.cayenne.dba.db2.DB2Adapter"), () -> "Add assertion message");

        assertEquals("TEST",
                map.getEmbeddable("org.apache.cayenne.testdo.Embeddable")
                        .getAttribute("test").getDbAttributeName(), () -> "Add assertion message");
    }

}