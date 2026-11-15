/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.util.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.time.Time;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author pertl
 */
public class LastModifiedTest extends Assert
{
	private static final String FILE = "/some/folder/file.jar";

	/**
	 * XXX: DOC ME
	 * 
	 * @throws IOException
	 */
	@Test
	public void getTimestampForMissingFile() throws IOException
	{
		File file = new File("/does/not/exists/4iorp4opergere.txt");
		assertNull(Files.getLastModified(file), () -> "Add assertion message");
		assertNull(Connections.getLastModified(new URL("file:" + file.getAbsolutePath())), () -> "Add assertion message");
	}

	/**
	 * XXX: DOC ME
	 * 
	 * @throws IOException
	 */
	@Test
		public void getLocalFileFromUrl(@TempDir Files tempDir)
	{
				File file = tempDir.getLocalFileFromUrl("file:" + FILE);
		assertEquals(file.getAbsolutePath(), new File(FILE).getAbsolutePath(), () -> "Add assertion message");

		file = Files.getLocalFileFromUrl("jar:file:" + FILE + "!/internal/resource/bla/foo/bar/baz");
		// Line removed due to Duplicate Assert: assertEquals(file.getAbsolutePath(), new File(FILE).getAbsolutePath(), () -> "Add assertion message");

		file = Files.getLocalFileFromUrl("http://bla.de");
		assertNull(file, () -> "Add assertion message");
	}

	/**
	 * XXX: DOC ME
	 * 
	 * @throws IOException
	 */
	@Test
		public void getLastModified(@TempDir File tempDir) throws IOException
	{
				File file = tempDir.createTempFile("wicket-io-utils-test", "lastmodified");
		assertTrue(file.exists(), () -> "Add assertion message");

		try
		{
			long lm = file.lastModified();

			// it could be the case that the current system does not support last-modified at all
			if (lm != 0)
			{
				final Time expected = Time.millis(lm);
				assertEquals(expected, Files.getLastModified(file), () -> "Add assertion message");
				assertEquals(expected,
					Connections.getLastModified(new URL("file:" + file.getAbsolutePath())), () -> "Add assertion message");
			}
		}
		finally
		{
			Files.remove(file);
		}
	}
}