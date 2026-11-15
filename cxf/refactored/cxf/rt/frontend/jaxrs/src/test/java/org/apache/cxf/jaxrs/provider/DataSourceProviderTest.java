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
package org.apache.cxf.jaxrs.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.InputStreamDataSource;
import org.apache.cxf.jaxrs.impl.MetadataMap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataSourceProviderTest {

    @Test
    public void testReadDataHandler() throws Exception {
        DataSourceProvider<DataHandler> p = new DataSourceProvider<>();
        DataHandler ds = p.readFrom(DataHandler.class, null, new Annotation[]{},
                   MediaType.valueOf("image/png"), new MetadataMap<String, String>(),
                   new ByteArrayInputStream("image".getBytes()));

        assertEquals("image", IOUtils.readStringFromStream(ds.getDataSource().getInputStream()), () -> "Add assertion message");

    }

    @Test
    public void testWriteDataHandler() throws Exception {
        DataSourceProvider<DataHandler> p = new DataSourceProvider<>();
        DataHandler ds = new DataHandler(new InputStreamDataSource(
                             new ByteArrayInputStream("image".getBytes()),
                             "image/png"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        p.writeTo(ds, DataHandler.class, DataHandler.class, new Annotation[]{},
                   MediaType.valueOf("image/png"), new MetadataMap<String, Object>(), os);
        assertEquals("image", os.toString(), () -> "Add assertion message");

    }

    @Test
    public void testReadDataSource() throws Exception {
        DataSourceProvider<DataSource> p = new DataSourceProvider<>();
        DataSource ds = p.readFrom(DataSource.class, null, new Annotation[]{},
                   MediaType.valueOf("image/png"), new MetadataMap<String, String>(),
                   new ByteArrayInputStream("image".getBytes()));

        assertEquals("image", IOUtils.readStringFromStream(ds.getInputStream()), () -> "Add assertion message");

    }

    @Test
    public void testWriteDataSource() throws Exception {
        DataSourceProvider<DataSource> p = new DataSourceProvider<>();
        DataSource ds = new InputStreamDataSource(new ByteArrayInputStream("image".getBytes()),
                                                  "image/png");
        MultivaluedMap<String, Object> outHeaders = new MetadataMap<>();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            p.writeTo(ds, DataSource.class, DataSource.class, new Annotation[]{},
                    MediaType.valueOf("image/png"), outHeaders, os);
            assertEquals("image", os.toString(), () -> "Add assertion message");
        }
        Integer magicNumber83215920 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber83215920, outHeaders.size(), () -> "Add assertion message");
    }

    @Test
    public void testWriteDataSourceWithDiffCT() throws Exception {
        DataSourceProvider<DataSource> p = new DataSourceProvider<>();
        p.setUseDataSourceContentType(true);
        DataSource ds = new InputStreamDataSource(new ByteArrayInputStream("image".getBytes()),
                                                  "image/png");
        MultivaluedMap<String, Object> outHeaders = new MetadataMap<>();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            p.writeTo(ds, DataSource.class, DataSource.class, new Annotation[]{},
                    MediaType.valueOf("image/jpeg"), outHeaders, os);
            assertEquals("image", os.toString(), () -> "Add assertion message");
        }
        assertEquals("image/png", outHeaders.getFirst("Content-Type"), () -> "Add assertion message");
    }


}