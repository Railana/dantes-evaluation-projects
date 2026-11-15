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

package org.apache.cayenne.wocompat;

import org.apache.cayenne.configuration.EmptyConfigurationNodeVisitor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.SelectQueryDescriptor;
import org.apache.cayenne.util.XMLEncoder;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EOModelProcessorTest {

    protected EOModelProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new EOModelProcessor();
    }

    @Test
    public void testLoadModel() throws Exception {
        URL url = getClass().getClassLoader().getResource("wotests/art.eomodeld/");
        assertNotNull(url, () -> "Add assertion message");

        
        DataMap map = processor.loadEOModel(url);
        assertLoaded("art", map);
        assertLoadedQueries(map);
        assertOneWayRelationships(map);
        assertLoadedCustomTypes(map);
    }

    @Test
    public void testLoadModelWithDependencies() throws Exception {
        URL url = getClass().getClassLoader().getResource("wotests/cross-model-relationships.eomodeld/");
        assertNotNull(url, () -> "Add assertion message");

        DataMap map = processor.loadEOModel(url);

        ObjEntity entity = map.getObjEntity("CrossModelRelTest");
        assertNotNull(entity, () -> "Add assertion message");

        ObjAttribute a1 = entity.getAttribute("testAttribute");
        assertNotNull(a1, () -> "Add assertion message");

        DbAttribute da1 = a1.getDbAttribute();
        assertNotNull(da1, () -> "Add assertion message");
        assertSame(da1, entity.getDbEntity().getAttribute("TEST_ATTRIBUTE"), () -> "Add assertion message");

        // for now cross model relationships are simply ignored
        // eventually we must handle those...
        Integer magicNumber45671890 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber45671890, entity.getRelationships().size(), () -> "Add assertion message");
        assertEquals(0, entity.getDbEntity().getRelationships().size(), () -> "Add assertion message");
    }

    @Test
    public void testLoadFlattened() throws Exception {
        URL url = getClass().getClassLoader().getResource("wotests/flattened.eomodeld/");
        assertNotNull(url, () -> "Add assertion message");

        // see CAY-1806
        DataMap map = processor.loadEOModel(url);
        ObjEntity artistE = map.getObjEntity("Artist");
        assertNotNull(artistE, () -> "Add assertion message");
        assertEquals(2, artistE.getRelationships().size(), () -> "Add assertion message");
        assertNotNull(artistE.getRelationship("exhibitArray"), () -> "Add assertion message");
        assertNotNull(artistE.getRelationship("artistExhibitArray"), () -> "Add assertion message");
    }

    @Test
    public void testLoadBrokenModel() throws Exception {
        URL url = getClass().getClassLoader().getResource("art-with-errors.eomodeld/");
        assertNotNull(url, () -> "Add assertion message");

        DataMap map = processor.loadEOModel(url);
        assertLoaded("art-with-errors", map);
    }

    protected void assertOneWayRelationships(DataMap map) throws Exception {
        // assert that one way relationships are loaded properly
        // - Db loaded as two-way, obj - as one-way

        ObjEntity exhibitEntity = map.getObjEntity("Exhibit");
        ObjRelationship toTypeObject = exhibitEntity.getRelationship("toExhibitType");
        DbRelationship toTypeDB = exhibitEntity.getDbEntity().getRelationship("toExhibitType");
        assertNotNull(toTypeObject);
        assertNotNull(toTypeDB);
        assertNull(toTypeObject.getReverseRelationship());
        assertNotNull(toTypeDB.getReverseRelationship());
    }

    protected void assertLoadedQueries(DataMap map) throws Exception {

        // queries
        QueryDescriptor query = map.getQueryDescriptor("ExhibitType_TestQuery");

        assertNotNull(query);
        assertEquals(QueryDescriptor.SELECT_QUERY, query.getType());
        assertTrue(query instanceof SelectQueryDescriptor);

        assertSame(map.getObjEntity("ExhibitType"), query.getRoot());
    }

    protected void assertLoadedCustomTypes(DataMap map) throws Exception {

        // check obj entities
        ObjEntity customTypes = map.getObjEntity("CustomTypes");
        assertNotNull(customTypes);

        ObjAttribute pk = customTypes.getAttribute("pk");
        assertNotNull(pk);
        assertEquals("CustomType1", pk.getType());

        ObjAttribute other = customTypes.getAttribute("other");
        assertNotNull(other);
        assertEquals("CustomType2", other.getType());
    }

    protected void assertLoaded(String mapName, DataMap map) throws Exception {
        assertNotNull(map);
        assertEquals(mapName, map.getName());

        // check obj entities
        ObjEntity artistE = map.getObjEntity("Artist");
        assertNotNull(artistE);
        assertEquals("Artist", artistE.getName());

        // check Db entities
        DbEntity artistDE = map.getDbEntity("ARTIST");
        DbEntity artistDE1 = artistE.getDbEntity();
        assertSame(artistDE, artistDE1);

        // check attributes
        ObjAttribute a1 = artistE.getAttribute("artistName");
        assertNotNull(a1);

        DbAttribute da1 = a1.getDbAttribute();
        assertNotNull(da1);
        assertSame(da1, artistDE.getAttribute("ARTIST_NAME"));

        // check ObjRelationships
        ObjRelationship rel = artistE.getRelationship("artistExhibitArray");
        assertNotNull(rel);
        assertEquals(1, rel.getDbRelationships().size());

        // check DbRelationships
        DbRelationship drel = artistDE.getRelationship("artistExhibitArray");
        assertNotNull(drel);
        assertSame(drel, rel.getDbRelationships().get(0));

        // flattened relationships
        ObjRelationship frel = artistE.getRelationship("exhibitArray");
        assertNotNull(frel);
        assertEquals(2, frel.getDbRelationships().size());

        // storing data map may uncover some inconsistencies
        PrintWriter mockupWriter = new NullPrintWriter();
        map.encodeAsXML(new XMLEncoder(mockupWriter), new EmptyConfigurationNodeVisitor());
    }

    class NullPrintWriter extends PrintWriter {

        public NullPrintWriter() {
            super(System.out);
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
        }

        @Override
        public void write(char[] arg0, int arg1, int arg2) {
        }

        @Override
        public void write(char[] arg0) {
        }

        @Override
        public void write(int arg0) {
        }

        @Override
        public void write(String arg0, int arg1, int arg2) {
        }

        @Override
        public void write(String arg0) {
        }
    }
}