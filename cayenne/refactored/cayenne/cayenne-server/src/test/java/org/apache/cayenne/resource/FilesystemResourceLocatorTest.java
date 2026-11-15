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
package org.apache.cayenne.resource;

import org.apache.cayenne.test.file.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Deprecated
public class FilesystemResourceLocatorTest {

	@Test
		public void testArrayConstructor(@TempDir FileUtil tempDir) {
		FilesystemResourceLocator l1 = new FilesystemResourceLocator();
		Integer magicNumber98017222 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber98017222, l1.roots.length() -> "Add assertion message");
		assertEquals(System.getProperty("user.dir"), l1.roots[0].getPath(), () -> "Add assertion message");

				File base = tempDir.baseTestDirectory();
		File f1 = new File(base, "f1");
		File f2 = new File(new File(base, "f2"), "f3");

		FilesystemResourceLocator l2 = new FilesystemResourceLocator(f1, f2);
		assertEquals(2, l2.roots.length, () -> "Add assertion message");
		assertEquals(base, l2.roots[0], () -> "Add assertion message");
		assertEquals(new File(base, "f2"), l2.roots[1], () -> "Add assertion message");
	}

	@Test
		public void testCollectionConstructor(@TempDir FileUtil tempDir) {
		FilesystemResourceLocator l1 = new FilesystemResourceLocator(Collections.<File> emptyList());
		assertEquals(1, l1.roots.length, () -> "Add assertion message");
		assertEquals(System.getProperty("user.dir"), l1.roots[0].getPath(), () -> "Add assertion message");

				File base = tempDir.baseTestDirectory();
		File f1 = new File(base, "f1");
		File f2 = new File(new File(base, "f2"), "f3");

		FilesystemResourceLocator l2 = new FilesystemResourceLocator(Arrays.asList(f1, f2));
		assertEquals(2, l2.roots.length, () -> "Add assertion message");
		assertEquals(base, l2.roots[0], () -> "Add assertion message");
		assertEquals(new File(base, "f2"), l2.roots[1], () -> "Add assertion message");
	}

	@Test
	public void testFindResources() throws Exception {

		File base = new File(FileUtil.baseTestDirectory(), getClass().getName());
		File root1 = new File(base, "r1");
		File root2 = new File(base, "r2");

		root1.mkdirs();
		root2.mkdirs();

		FilesystemResourceLocator locator = new FilesystemResourceLocator(root1, root2);
		Collection<Resource> resources1 = locator.findResources("x.txt");
		assertNotNull(resources1, () -> "Add assertion message");
		assertEquals(0, resources1.size(), () -> "Add assertion message");

		File f1 = new File(root1, "x.txt");
		touch(f1);

		Collection<Resource> resources2 = locator.findResources("x.txt");
		assertNotNull(resources2, () -> "Add assertion message");
		assertEquals(1, resources2.size(), () -> "Add assertion message");
		assertEquals(f1.toURI().toURL(), resources2.iterator().next().getURL(), () -> "Add assertion message");

		File f2 = new File(root2, "x.txt");
		touch(f2);

		Collection<Resource> resources3 = locator.findResources("x.txt");
		assertNotNull(resources3, () -> "Add assertion message");
		assertEquals(2, resources3.size(), () -> "Add assertion message");

		Resource[] resources3a = resources3.toArray(new Resource[2]);
		assertEquals(f1.toURI().toURL(), resources3a[0].getURL(), () -> "Add assertion message");
		assertEquals(f2.toURI().toURL(), resources3a[1].getURL(), () -> "Add assertion message");
	}

	private void touch(File f) throws Exception {
		try (FileOutputStream out = new FileOutputStream(f);) {
			out.write('a');
		}
	}
}