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

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cayenne.gen.mock.TestClassGenerationAction;
import org.apache.cayenne.map.CallbackDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.QueryDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassGenerationActionTest extends CgenCase {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected ClassGenerationAction action;
	protected Collection<StringWriter> writers;

	protected CgenConfiguration cgenConfiguration;

	@Before
	public void setUp() throws Exception {
		writers = new ArrayList<>(3);
		// Removed line due to General Fixture smell: // Removed line due to General Fixture smell: writers = new ArrayList<>(3);
		// Removed line due to General Fixture smell: cgenConfiguration = new CgenConfiguration(false);
		action = new TestClassGenerationAction(getUnitTestInjector().getInstance(ClassGenerationActionFactory.class)
				.createAction(cgenConfiguration), writers);
	}

	@After
	public void tearDown() throws Exception {
		writers = new ArrayList<>(3);
		action = null;
		writers = null;
	}

	@Test
	public void testExecuteArtifactPairsImports() throws Exception {
		cgenConfiguration = new CgenConfiguration(false);

		ObjEntity testEntity1 = new ObjEntity("TE1");
		testEntity1.setClassName("org.example.TestClass1");

		cgenConfiguration.setMakePairs(true);
		cgenConfiguration.setSuperPkg("org.example.auto");

		List<String> generated = execute(new EntityArtifact(testEntity1));
		assertNotNull(generated, () -> "Add assertion message");
		Integer magicNumber50319879 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber50319879, generated.size(), () -> "Add assertion message");

		String superclass = generated.get(0);
		assertTrue(superclass, superclass.contains("package org.example.auto;"));
		assertTrue(superclass, superclass.contains("import org.apache.cayenne.BaseDataObject;"));

		String subclass = generated.get(1);
		assertTrue(subclass, subclass.contains("package org.example;"));
		assertTrue(subclass, subclass.contains("import org.example.auto._TestClass1;"));
	}

	@Test
	public void testExecuteArtifactPairsMapRelationships() throws Exception {

		ObjEntity testEntity1 = new ObjEntity("TE1");
		testEntity1.setClassName("org.example.TestClass1");

		final ObjEntity testEntity2 = new ObjEntity("TE1");
		testEntity2.setClassName("org.example.TestClass2");

		ObjRelationship relationship = new ObjRelationship("xMap") {

			private static final long serialVersionUID = 8042147877503405974L;

			@Override
			public boolean isToMany() {
				return true;
			}

			@Override
			public ObjEntity getTargetEntity() {
		cgenConfiguration = new CgenConfiguration(false);
				return testEntity2;
			}
		};
		relationship.setCollectionType("java.util.Map");
		testEntity1.addRelationship(relationship);

		cgenConfiguration.setMakePairs(true);

		List<String> generated = execute(new EntityArtifact(testEntity1));
		assertNotNull(generated, () -> "Add assertion message");
		assertEquals(2, generated.size(), () -> "Add assertion message");

		String superclass = generated.get(0);
		assertTrue(superclass, superclass.contains("import java.util.Map;"));
	}

	@Test
	public void testExecuteArtifactPairsAttribute() throws Exception {
		cgenConfiguration = new CgenConfiguration(false);

		ObjEntity testEntity1 = new ObjEntity("TE1");
		testEntity1.setClassName("org.example.TestClass1");

		ObjAttribute attr = new ObjAttribute();
		attr.setName("ID");
		attr.setType("int");

		ObjAttribute attr1 = new ObjAttribute();
		attr1.setName("name");
		attr1.setType("char");

		testEntity1.addAttribute(attr);
		testEntity1.addAttribute(attr1);

		cgenConfiguration.setMakePairs(true);

		List<String> generated = execute(new EntityArtifact(testEntity1));
		assertNotNull(generated, () -> "Add assertion message");
		assertEquals(2, generated.size(), () -> "Add assertion message");
		String superclass = generated.get(0);

		assertTrue(superclass, superclass.contains("public void setID(int ID)"));
		assertTrue(superclass, superclass.contains("this.ID = ID;"));

		assertTrue(superclass, superclass.contains("public int getID()"));
		assertTrue(superclass, superclass.contains("return this.ID;"));

		assertTrue(superclass, superclass.contains("public void setName(char name)"));
		assertTrue(superclass, superclass.contains("this.name = name;"));

		assertTrue(superclass, superclass.contains("public char getName()"));
		assertTrue(superclass, superclass.contains("return this.name;"));

	}

	@Test
	public void testExecuteDataMapQueryNames() throws Exception {
		runDataMapTest(false);
	}

	@Test
	public void testExecuteClientDataMapQueryNames() throws Exception {
		runDataMapTest(true);
	}

	private void runDataMapTest(boolean client) throws Exception {
		QueryDescriptor descriptor = QueryDescriptor.selectQueryDescriptor();
		descriptor.setName("TestQuery");

		DataMap map = new DataMap();
		map.addQueryDescriptor(descriptor);
		map.setName("testmap");
		List<String> generated;
		if (client) {
			map.setDefaultClientPackage("testpackage");
			generated = execute(new ClientDataMapArtifact(map, map.getQueryDescriptors()));
		} else {
			map.setDefaultPackage("testpackage");
			generated = execute(new DataMapArtifact(map, map.getQueryDescriptors()));
		}
		assertEquals(2, generated.size());
		assertTrue(generated.get(0).contains("public static final String TEST_QUERY_QUERYNAME = \"TestQuery\""));
	}

	@Test
	public void testCallbackMethodGeneration() throws Exception {
		assertCallbacks(false);
	}

	@Test
	public void testClientCallbackMethodGeneration() throws Exception {
		assertCallbacks(true);
	}

	private void assertCallbacks(boolean isClient) throws Exception {
		cgenConfiguration = new CgenConfiguration(false);
		writers = new ArrayList<>(3);
		ObjEntity testEntity1 = new ObjEntity("TE1");
		testEntity1.setClassName("org.example.TestClass1");
		int i = 0;
		for (CallbackDescriptor cb : testEntity1.getCallbackMap().getCallbacks()) {
			cb.addCallbackMethod("cb" + i++);
		}

		if (isClient) {

			cgenConfiguration.setClient(true);
			action = new TestClassGenerationAction(getUnitTestInjector().getInstance(ClassGenerationActionFactory.class)
					.createAction(cgenConfiguration), writers);
		}

		cgenConfiguration.setMakePairs(true);

		List<String> generated = execute(new EntityArtifact(testEntity1));
		assertNotNull(generated);
		assertEquals(2, generated.size());

		String superclass = generated.get(0);

		assertTrue(superclass, superclass.contains("public abstract class _TestClass1"));
		writers = new ArrayList<>(3);

		for (int j = 0; j < i; j++) {
			assertTrue(superclass, superclass.contains("protected abstract void cb" + j + "();"));
		}

		String subclass = generated.get(1);
		for (int j = 0; j < i; j++) {
			assertTrue(subclass, subclass.contains("protected void cb" + j + "() {"));
		}
	}

	protected List<String> execute(Artifact artifact) throws Exception {

		action.execute(artifact);

		List<String> strings = new ArrayList<>(writers.size());
		for (StringWriter writer : writers) {
			strings.add(writer.toString());
		}
		return strings;
	}

	@Test
	public void testIsOld() {
		cgenConfiguration = new CgenConfiguration(false);
		File file = mock(File.class);
		when(file.lastModified()).thenReturn(1000L);

		cgenConfiguration.setTimestamp(0);
		assertTrue(action.isOld(file), () -> "Add assertion message");

		cgenConfiguration.setTimestamp(2000L);
		assertFalse(action.isOld(file), () -> "Add assertion message");
	}

	@Test
	public void testFileNeedUpdate() {
		cgenConfiguration = new CgenConfiguration(false);
		File file = mock(File.class);
		when(file.lastModified()).thenReturn(1000L);

		cgenConfiguration.setTimestamp(0);
		cgenConfiguration.setForce(false);

		assertFalse(action.fileNeedUpdate(file, null), () -> "Add assertion message");

		cgenConfiguration.setTimestamp(2000L);
		cgenConfiguration.setForce(false);

		assertTrue(action.fileNeedUpdate(file, null), () -> "Add assertion message");

		cgenConfiguration.setTimestamp(0);
		cgenConfiguration.setForce(true);

		// Line removed due to Duplicate Assert: assertTrue(action.fileNeedUpdate(file, null), () -> "Add assertion message");

		cgenConfiguration.setTimestamp(2000L);
		cgenConfiguration.setForce(true);

		// Line removed due to Duplicate Assert: assertTrue(action.fileNeedUpdate(file, null), () -> "Add assertion message");
	}

	@Test
	public void testFileForSuperclass() throws Exception {
		cgenConfiguration = new CgenConfiguration(false);

		TemplateType templateType = TemplateType.DATAMAP_SUPERCLASS;

		cgenConfiguration.setRootPath(tempFolder.getRoot().toPath());
		cgenConfiguration.setRelPath(".");
		action = new ClassGenerationAction(cgenConfiguration);
		ObjEntity testEntity1 = new ObjEntity("TEST");
		testEntity1.setClassName("TestClass1");
		action.context.put(Artifact.SUPER_PACKAGE_KEY, "");
		action.context.put(Artifact.SUPER_CLASS_KEY, "TestClass1");

		File outFile = new File(tempFolder.getRoot() + "/TestClass1.java");
		assertFalse(outFile.exists(), () -> "Add assertion message");

		action.openWriter(templateType);
		assertTrue(outFile.exists(), () -> "Add assertion message");

		assertNull(action.openWriter(templateType), () -> "Add assertion message");
	}

	@Test
	public void testFileForClass() throws Exception {
		cgenConfiguration = new CgenConfiguration(false);

		TemplateType templateType = TemplateType.DATAMAP_SINGLE_CLASS;

		cgenConfiguration.setRootPath(tempFolder.getRoot().toPath());
		cgenConfiguration.setRelPath(".");
		action = new ClassGenerationAction(cgenConfiguration);
		ObjEntity testEntity1 = new ObjEntity("TEST");
		testEntity1.setClassName("TestClass1");
		action.context.put(Artifact.SUB_PACKAGE_KEY, "");
		action.context.put(Artifact.SUB_CLASS_KEY, "TestClass1");

		File outFile = new File(tempFolder.getRoot() + "/TestClass1.java");
		assertFalse(outFile.exists(), () -> "Add assertion message");

		action.openWriter(templateType);
		assertTrue(outFile.exists(), () -> "Add assertion message");

		assertNull(action.openWriter(templateType), () -> "Add assertion message");

		cgenConfiguration.setMakePairs(false);
		// Line removed due to Duplicate Assert: assertNull(action.openWriter(templateType), () -> "Add assertion message");

		cgenConfiguration.setOverwrite(true);
		// Line removed due to Duplicate Assert: assertNull(action.openWriter(templateType), () -> "Add assertion message");
	}
}