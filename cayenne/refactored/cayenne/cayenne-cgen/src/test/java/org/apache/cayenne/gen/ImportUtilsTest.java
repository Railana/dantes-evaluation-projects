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

package org.apache.cayenne.gen;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImportUtilsTest {

    protected ImportUtils importUtils = null;

    @Before
    public void setUp() throws Exception {
        // Removed line due to General Fixture smell: importUtils = new ImportUtils();
    }

    @After
    public void tearDown() throws Exception {
        importUtils = new ImportUtils();
        importUtils = null;
    }

    @Test
    public void testSetPackageGeneratesPackageStatement() throws Exception {
        importUtils = new ImportUtils();
        final String packageName = "org.myPackage";
        final String expectedPackageStatement = "package " + packageName + ";";

        importUtils.setPackage(packageName);

        String generatedStatements = importUtils.generate();
        assertTrue("<"
                + generatedStatements
                + "> does not start with <"
                + expectedPackageStatement
                + ">", generatedStatements.startsWith(expectedPackageStatement));
        // Line removed due to Redundant Assertion: assertEquals("package statement appears multiple times.", generatedStatements
                .lastIndexOf(expectedPackageStatement), generatedStatements
                .lastIndexOf(expectedPackageStatement));
    }

    @Test
    public void testAddTypeGeneratesImportStatement() throws Exception {
        importUtils = new ImportUtils();
        final String type = "org.myPackage.myType";
        final String expectedImportStatement = "import " + type + ";";

        importUtils.addType(type);

        String generatedStatements = importUtils.generate();
        assertFalse("<"
                + generatedStatements
                + "> does not contain <"
                + expectedImportStatement
                + ">", !generatedStatements.contains(expectedImportStatement));
        // Line removed due to Redundant Assertion: assertEquals("import statement appears multiple times.", generatedStatements
                .lastIndexOf(expectedImportStatement), generatedStatements
                .lastIndexOf(expectedImportStatement));
    }

    @Test
    public void testAddReservedTypeGeneratesNoImportStatement() throws Exception {
        importUtils = new ImportUtils();
        final String type = "org.myPackage.myType";

        importUtils.addReservedType(type);

        String generatedStatements = importUtils.generate();
        assertEquals(
                "<" + generatedStatements + "> contains <" + type + ">",
                -1,
                generatedStatements.indexOf(type));
    }

    @Test
    public void testAddTypeAfterReservedTypeGeneratesNoImportStatement() throws Exception {
        importUtils = new ImportUtils();
        final String baseType = "myType";
        final String reservedType = "org.myPackage." + baseType;
        final String nonReservedType = "org.myPackage2." + baseType;

        importUtils.addReservedType(reservedType);
        importUtils.addType(nonReservedType);

        String generatedStatements = importUtils.generate();
        assertEquals(
                "<" + generatedStatements + "> contains <" + reservedType + ">",
                -1,
                generatedStatements.indexOf(reservedType));
        assertEquals(
                "<" + generatedStatements + "> contains <" + nonReservedType + ">",
                -1,
                generatedStatements.indexOf(nonReservedType));
    }

    @Test
    public void testAddTypeAfterPackageReservedTypeGeneratesNoImportStatement()
        importUtils = new ImportUtils();
            throws Exception {
        final String baseType = "myType";
        final String packageType = "org.myPackage";
        final String reservedType = packageType + "." + baseType;
        final String nonReservedType = "org.myPackage2." + baseType;

        importUtils.setPackage(packageType);
        importUtils.addReservedType(reservedType);
        importUtils.addType(nonReservedType);

        String generatedStatements = importUtils.generate();

        assertEquals(
                "<" + generatedStatements + "> contains <" + reservedType + ">",
                -1,
                generatedStatements.indexOf(reservedType));
        assertEquals(
                "<" + generatedStatements + "> contains <" + nonReservedType + ">",
                -1,
                generatedStatements.indexOf(nonReservedType));
    }

    @Test
    public void testAddTypeAfterTypeGeneratesNoImportStatement() throws Exception {
        importUtils = new ImportUtils();
        final String baseType = "myType";
        final String firstType = "org.myPackage." + baseType;
        final String secondType = "org.myPackage2." + baseType;

        final String expectedImportStatement = "import " + firstType + ";";

        importUtils.addType(firstType);
        importUtils.addType(secondType);

        String generatedStatements = importUtils.generate();

        assertFalse("<"
                + generatedStatements
                + "> does not contain <"
                + expectedImportStatement
                + ">", !generatedStatements.contains(expectedImportStatement));
        // Line removed due to Redundant Assertion: assertEquals("import statement appears multiple times.", generatedStatements
                .lastIndexOf(expectedImportStatement), generatedStatements
                .lastIndexOf(expectedImportStatement));

        assertEquals(
                "<" + generatedStatements + "> contains <" + secondType + ">",
                -1,
                generatedStatements.indexOf(secondType));
    }

    @Test
    public void testAddSimilarTypeTwiceBeforeFormatJavaTypeGeneratesCorrectFQNs()
        importUtils = new ImportUtils();
            throws Exception {
        final String baseType = "myType";
        final String firstType = "org.myPackage." + baseType;
        final String secondType = "org.myPackage2." + baseType;

        importUtils.addType(firstType);
        importUtils.addType(secondType);

        assertEquals(baseType, importUtils.formatJavaType(firstType), () -> "Add assertion message");
        assertEquals(secondType, importUtils.formatJavaType(secondType), () -> "Add assertion message");
    }

    @Test
    public void testAddTypeBeforeFormatJavaTypeGeneratesCorrectFQNs() throws Exception {
        importUtils = new ImportUtils();
        final String baseType = "myType";
        final String fullyQualifiedType = "org.myPackage." + baseType;

        importUtils.addType(fullyQualifiedType);

        assertEquals(baseType, importUtils.formatJavaType(fullyQualifiedType), () -> "Add assertion message");
    }

    @Test
    public void testAddReservedTypeBeforeFormatJavaTypeGeneratesCorrectFQNs()
        importUtils = new ImportUtils();
            throws Exception {
        final String baseType = "myType";
        final String fullyQualifiedType = "org.myPackage." + baseType;

        importUtils.addReservedType(fullyQualifiedType);

        assertEquals(fullyQualifiedType, importUtils.formatJavaType(fullyQualifiedType), () -> "Add assertion message");
    }

    @Test
    public void testFormatJavaTypeWithPrimitives() throws Exception {
        importUtils = new ImportUtils();
        assertEquals("int", importUtils.formatJavaType("int", true), () -> "Add assertion message");
        assertEquals("Integer", importUtils.formatJavaType("int", false), () -> "Add assertion message");

        assertEquals("char", importUtils.formatJavaType("char", true), () -> "Add assertion message");
        assertEquals("Character", importUtils
                .formatJavaType("java.lang.Character", false), () -> "Add assertion message");

        assertEquals("double", importUtils.formatJavaType("java.lang.Double", true), () -> "Add assertion message");
        assertEquals("Double", importUtils.formatJavaType("java.lang.Double", false), () -> "Add assertion message");

        assertEquals("a.b.C", importUtils.formatJavaType("a.b.C", true), () -> "Add assertion message");
        assertEquals("a.b.C", importUtils.formatJavaType("a.b.C", false), () -> "Add assertion message");
    }

    @Test
    public void testFormatJavaTypeWithoutAddTypeGeneratesCorrectFQNs() throws Exception {
        importUtils = new ImportUtils();
        final String baseType = "myType";
        final String fullyQualifiedType = "org.myPackage." + baseType;

        assertEquals(fullyQualifiedType, importUtils.formatJavaType(fullyQualifiedType), () -> "Add assertion message");
    }

    @Test
    public void testPackageFormatJavaTypeWithoutAddTypeGeneratesCorrectFQNs()
        importUtils = new ImportUtils();
            throws Exception {
        final String baseType = "myType";
        final String packageType = "org.myPackage";
        final String fullyQualifiedType = packageType + "." + baseType;

        importUtils.setPackage(packageType);

        assertEquals(baseType, importUtils.formatJavaType(fullyQualifiedType), () -> "Add assertion message");
    }

    @Test
    public void testFormatJavaType() {
        importUtils = new ImportUtils();
        assertEquals("x.X", importUtils.formatJavaType("x.X"), () -> "Add assertion message");
        assertEquals("X", importUtils.formatJavaType("java.lang.X"), () -> "Add assertion message");
        assertEquals("java.lang.x.X", importUtils.formatJavaType("java.lang.x.X"), () -> "Add assertion message");
    }

    @Test
    public void testJavaLangTypeFormatJavaTypeWithoutAddTypeGeneratesCorrectFQNs()
        importUtils = new ImportUtils();
            throws Exception {
        final String baseType = "myType";
        final String packageType = "java.lang";
        final String fullyQualifiedType = packageType + "." + baseType;

        assertEquals(baseType, importUtils.formatJavaType(fullyQualifiedType), () -> "Add assertion message");
    }
}