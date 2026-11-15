/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;


@Category(JUnitTests.class)
public class ListBodyWriterTest {

    ListBodyWriter listBodyWriter;
    Type genericType;
    Annotation[] annotations;
    MediaType mediaType;
    List list;
    MultivaluedMap<String, Object> httpHeaders;
    JAXBContext jaxbContext;
    Marshaller marshaller;
    OutputStream outputStream;

    @Before
    public void initialize() {
        listBodyWriter = new ListBodyWriter();
        // Removed line due to General Fixture smell: genericType = Mockito.mock(Type.class);
        // Removed line due to General Fixture smell: annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        // Removed line due to General Fixture smell: mediaType = MediaType.valueOf("some/text");
        // Removed line due to General Fixture smell: list = new LinkedList();
        // Removed line due to General Fixture smell: httpHeaders = Mockito.mock(MultivaluedMap.class);
        // Removed line due to General Fixture smell: jaxbContext = Mockito.mock(JAXBContext.class);
        // Removed line due to General Fixture smell: marshaller = Mockito.mock(Marshaller.class);
        // Removed line due to General Fixture smell: outputStream = new OutputStream() {
            @Override
            public void write(int b) {
            }
        };
    }

    @Test
    public void isWriteableTest() {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, genericType, annotations, mediaType));
    }

    @Test
    public void isWriteableNullTypeTest() {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(null, genericType, annotations, mediaType));
    }

    @Test
    public void isWriteableNullGenericTypeTest() {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        list = new LinkedList();
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, null, annotations, mediaType));
    }

    @Test
    public void isWriteableNullAnnotationsTest() {
        mediaType = MediaType.valueOf("some/text");
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, genericType, null, mediaType));
    }

    @Test
    public void isWriteableNullMediaTypeTest() {
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, genericType, annotations, null));
    }

    @Test
    public void getSizeTest() {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Integer magicNumber98824701 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber98824701, listBodyWriter.getSize(list, String.class, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullListTest() {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(null, String.class, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullTypeTest() {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, null, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullGenericTypeTest() {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        list = new LinkedList();
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, String.class, null, annotations, mediaType));
    }

    @Test
    public void getSizeNullAnnotationsTest() {
        mediaType = MediaType.valueOf("some/text");
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, String.class, genericType, null, mediaType));
    }

    @Test
    public void getSizeNullMediaTypeTest() {
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        genericType = Mockito.mock(Type.class);
        list = new LinkedList();
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, String.class, genericType, annotations, null));
    }

    @Test(expected = WebApplicationException.class)
    public void writeToNullProvidersTest() throws IOException {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        outputStream = new OutputStream() {
        httpHeaders = Mockito.mock(MultivaluedMap.class);
        list = new LinkedList();
        listBodyWriter.providers = null;

        listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream);
    }

    @Test(expected = WebApplicationException.class)
    public void writeToNullJaxbContextTest() throws IOException {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        outputStream = new OutputStream() {
        httpHeaders = Mockito.mock(MultivaluedMap.class);
        list = new LinkedList();
        listBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(null);

        listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream);
    }

    @Test
    public void writeToTest() throws Exception {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        outputStream = new OutputStream() {
        marshaller = Mockito.mock(Marshaller.class);
        jaxbContext = Mockito.mock(JAXBContext.class);
        httpHeaders = Mockito.mock(MultivaluedMap.class);
        list = new LinkedList();
        listBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(jaxbContext);
        Mockito.when(jaxbContext.createMarshaller()).thenReturn(marshaller);

        assertThrows(Exception e,
        		() -> listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream));
    }

    @Test(expected = WebApplicationException.class)
    public void writeToInternalServerErrorTest() throws Exception {
        mediaType = MediaType.valueOf("some/text");
        annotations = new Annotation[]{Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)};
        outputStream = new OutputStream() {
        jaxbContext = Mockito.mock(JAXBContext.class);
        httpHeaders = Mockito.mock(MultivaluedMap.class);
        list = new LinkedList();
        listBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(jaxbContext);
        Mockito.when(jaxbContext.createMarshaller()).thenThrow(new JAXBException("message"));

        listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream);
    }
}