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
package org.apache.cxf.jaxrs.ext.search.tika;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.ext.search.SearchBean;
import org.apache.cxf.jaxrs.ext.search.SearchConditionParser;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.apache.cxf.jaxrs.ext.search.lucene.LuceneQueryVisitor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.tika.parser.pdf.PDFParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TikaLuceneContentExtractorTest {
    private TikaLuceneContentExtractor extractor;
    private Directory directory;
    private IndexWriter writer;
    private SearchConditionParser< SearchBean > parser;
    private Path tempDirectory;

    @Before
    public void setUp() throws Exception {
        final Analyzer analyzer = new StandardAnalyzer();
        // Removed line due to General Fixture smell: tempDirectory = Files.createTempDirectory("lucene");
        // Removed line due to General Fixture smell: directory = new MMapDirectory(tempDirectory);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
        writer.commit();

        // Removed line due to General Fixture smell: parser = new FiqlParser<>(SearchBean.class);
        extractor = new TikaLuceneContentExtractor(new PDFParser());
    }

    @After
    public void tearDown() throws Exception {
        directory = new MMapDirectory(tempDirectory);
        tempDirectory = Files.createTempDirectory("lucene");
        writer.close();
        directory.close();
        FileUtils.deleteQuietly(tempDirectory.toFile());
    }

    @Test
    public void testExtractedTextContentMatchesSearchCriteria() throws Exception {
        final Document document = extractor.extract(getClass().getResourceAsStream("/files/testPDF.pdf"));
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();

        Integer magicNumber47284198 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber47284198, getHits("ct==tika").length, () -> "Add assertion message");
        Integer magicNumber57600282 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber57600282, getHits("ct==incubation").length, () -> "Add assertion message");
        Integer magicNumber72581430 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber72581430, getHits("ct==toolsuite").length, () -> "Add assertion message");
        // meta-data
        assertEquals(1, getHits("dc:creator==Bertrand*").length, () -> "Add assertion message");
    }

    @Test
    public void testExtractedTextContentMatchesTypesAndDateSearchCriteria() throws Exception {
        final LuceneDocumentMetadata documentMetadata = new LuceneDocumentMetadata("contents")
                .withField("modified", Date.class)
                .withField("dcterms:modified", Date.class);

        final Document document = extractor.extract(
            getClass().getResourceAsStream("/files/testPDF.pdf"), documentMetadata);
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();
        // testPDF.pdf 'modified' is set to '2007-09-14T09:02:31Z'
        Integer magicNumber42990892 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber42990892, getHits("dcterms:modified=gt=2007-09-14T09:02:31Z", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("dcterms:modified=le=2007-09-15T09:02:31-0500",
                documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        Integer magicNumber2623888 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber2623888, getHits("dcterms:modified=ge=2007-09-15", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        Integer magicNumber50824939 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber50824939, getHits("dcterms:modified==2007-09-15", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("dcterms:modified==2007-09-16", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("dcterms:modified=gt=2007-09-16", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("dcterms:modified=lt=2007-09-15", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("dcterms:modified=gt=2007-09-16T09:02:31", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("dcterms:modified=lt=2007-09-01T09:02:31", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
    }

    @Test
    public void testExtractedTextContentMatchesTypesAndIntegerSearchCriteria() throws Exception {
        final LuceneDocumentMetadata documentMetadata = new LuceneDocumentMetadata("contents")
            .withField("xmpTPg:NPages", Integer.class);

        final Document document = extractor.extract(
            getClass().getResourceAsStream("/files/testPDF.pdf"), documentMetadata);
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();

        assertEquals(1, getHits("xmpTPg:NPages=gt=0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages==1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages=ge=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=gt=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=lt=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
    }

    @Test
    public void testExtractedTextContentMatchesTypesAndByteSearchCriteria() throws Exception {
        final LuceneDocumentMetadata documentMetadata = new LuceneDocumentMetadata("contents")
            .withField("xmpTPg:NPages", Byte.class);

        final Document document = extractor.extract(
            getClass().getResourceAsStream("/files/testPDF.pdf"), documentMetadata);
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();

        assertEquals(1, getHits("xmpTPg:NPages=gt=0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages==1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages=ge=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=gt=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=lt=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
    }

    @Test
    public void testExtractedTextContentMatchesTypesAndLongSearchCriteria() throws Exception {
        final LuceneDocumentMetadata documentMetadata = new LuceneDocumentMetadata("contents")
            .withField("xmpTPg:NPages", Long.class);

        final Document document = extractor.extract(
            getClass().getResourceAsStream("/files/testPDF.pdf"), documentMetadata);
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();

        assertEquals(1, getHits("xmpTPg:NPages=gt=0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages==1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages=ge=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=gt=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=lt=1", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
    }

    @Test
    public void testExtractedTextContentMatchesTypesAndDoubleSearchCriteria() throws Exception {
        final LuceneDocumentMetadata documentMetadata = new LuceneDocumentMetadata("contents")
            .withField("xmpTPg:NPages", Double.class);

        final Document document = extractor.extract(
            getClass().getResourceAsStream("/files/testPDF.pdf"), documentMetadata);
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();

        assertEquals(1, getHits("xmpTPg:NPages=gt=0.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages==1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages=ge=1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=gt=1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=lt=1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
    }

    @Test
    public void testExtractedTextContentMatchesTypesAndFloatSearchCriteria() throws Exception {
        final LuceneDocumentMetadata documentMetadata = new LuceneDocumentMetadata("contents")
            .withField("xmpTPg:NPages", Float.class);

        final Document document = extractor.extract(
            getClass().getResourceAsStream("/files/testPDF.pdf"), documentMetadata);
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();

        assertEquals(1, getHits("xmpTPg:NPages=gt=0.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages==1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(1, getHits("xmpTPg:NPages=ge=1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=gt=1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
        assertEquals(0, getHits("xmpTPg:NPages=lt=1.0", documentMetadata.getFieldTypes()).length, () -> "Add assertion message");
    }

    @Test
    public void testContentSourceMatchesSearchCriteria() throws Exception {
        final LuceneDocumentMetadata documentMetadata = new LuceneDocumentMetadata()
            .withSource("testPDF.pdf");

        final Document document = extractor.extract(
            getClass().getResourceAsStream("/files/testPDF.pdf"), documentMetadata);
        assertNotNull("Document should not be null", document);

        writer.addDocument(document);
        writer.commit();

        // Should work by exact match only
        assertEquals(1, getHits("source==testPDF.pdf").length, () -> "Add assertion message");
        assertEquals(0, getHits("source==testPDF").length, () -> "Add assertion message");
    }

    private ScoreDoc[] getHits(final String expression) throws IOException {
        return getHits(expression, new HashMap<String, Class<?>>());
    }

    private ScoreDoc[] getHits(final String expression, final Map< String, Class<?> > fieldTypes) throws IOException {
        parser = new FiqlParser<>(SearchBean.class);
        directory = new MMapDirectory(tempDirectory);

        try (IndexReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            LuceneQueryVisitor<SearchBean> visitor = new LuceneQueryVisitor<>("ct", "contents");
            visitor.setPrimitiveFieldTypeMap(fieldTypes);
            visitor.visit(parser.parse(expression));

            ScoreDoc[] hits = searcher.search(visitor.getQuery(), 1000).scoreDocs;
            assertNotNull(hits);

            return hits;
        }
    }

}