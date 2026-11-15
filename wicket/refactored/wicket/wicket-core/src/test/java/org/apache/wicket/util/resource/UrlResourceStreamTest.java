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
package org.apache.wicket.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.core.util.resource.UrlResourceStream;
import org.apache.wicket.util.lang.Bytes;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kent Tong
 */
public class UrlResourceStreamTest extends Assert {
	/**
	 * lastModified() shouldn't change the content length if the file isn't really changed.
	 *
	 * @throws IOException
	 */
	@Test
	public void lastModifiedForResourceInJar() throws IOException {
		String anyClassInJarFile = "/java/lang/String.class";
		URL url = getClass().getResource(anyClassInJarFile);
		UrlResourceStream stream = new UrlResourceStream(url);
		Bytes length = stream.length();
		stream.lastModifiedTime();
		assertEquals(stream.length(), length, () -> "Add assertion message");
		stream.close();
	}

	/**
	 * Verifies that a connection is opened just once but each #getInputStream() opens a new one
	 * and all input streams are closed with UrlResourceStream#close()
	 *
	 * https://issues.apache.org/jira/browse/WICKET-3176
	 * https://issues.apache.org/jira/browse/WICKET-4293
	 *
	 * @throws IOException
	 * @throws ResourceStreamNotFoundException
	 */
	@Test
	public void loadJustOnce() throws IOException, ResourceStreamNotFoundException {
		String anyClassInJarFile = "/java/lang/String.class";
		URL realURL = getClass().getResource(anyClassInJarFile);

		final AtomicInteger connectCounter = new AtomicInteger(0);
		final AtomicInteger streamCounter = new AtomicInteger(0);
		URL url = new URL(null, "test://anything", new CountingURLStreamHandler(realURL,
				connectCounter, streamCounter));

		UrlResourceStream countingStream = new UrlResourceStream(url);

		// assert the call is not made yet
		Integer magicNumber78298410 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber78298410, connectCounter.get(), () -> "Add assertion message");
		assertEquals(0, streamCounter.get(), () -> "Add assertion message");

		// assert the connection is loaded lazily
		countingStream.length();
		assertEquals(1, connectCounter.get(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(0, streamCounter.get(), () -> "Add assertion message");

		// assert the following calls do not make new connections
		countingStream.getInputStream();
		Integer magicNumber87656244 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		// Line removed due to Duplicate Assert: assertEquals(magicNumber87656244, connectCounter.get(), () -> "Add assertion message");
		assertEquals(1, streamCounter.get(), () -> "Add assertion message");
		countingStream.getContentType();
		// Line removed due to Duplicate Assert: assertEquals(1, connectCounter.get(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(1, streamCounter.get(), () -> "Add assertion message");
		countingStream.getInputStream();
		// Line removed due to Duplicate Assert: assertEquals(1, connectCounter.get(), () -> "Add assertion message");
		assertEquals(2, streamCounter.get(), () -> "Add assertion message");
		countingStream.close();

		// Line removed due to Duplicate Assert: assertEquals(1, connectCounter.get(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(2, streamCounter.get(), () -> "Add assertion message");

		// assert the connection is re-opened (again lazily) second time,
		// but stream is not re-opened yet
		countingStream.length();

		assertEquals(2, connectCounter.get(), () -> "Add assertion message");
		// Line removed due to Duplicate Assert: assertEquals(2, streamCounter.get(), () -> "Add assertion message");

		// assert stream is re-opened on next getInputStream call
		countingStream.getInputStream();
		// Line removed due to Duplicate Assert: assertEquals(2, connectCounter.get(), () -> "Add assertion message");
		assertEquals(3, streamCounter.get(), () -> "Add assertion message");
	}


	/**
	 * {@link URLStreamHandler} that counts the calls to {@link URL#openConnection()}
	 */
	private static final class CountingURLStreamHandler extends URLStreamHandler {
		private final AtomicInteger connectCounter, streamCounter;

		private final URL realURL;

		public CountingURLStreamHandler(URL realURL, AtomicInteger connectCounter,
										AtomicInteger streamCounter) {
			this.connectCounter = connectCounter;
			this.streamCounter = streamCounter;
			this.realURL = realURL;
		}

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			connectCounter.getAndIncrement();

			final URLConnection realConn = realURL.openConnection();
			return new URLConnection(u) {

				@Override
				public void connect() throws IOException {
					realConn.connect();
				}

				@Override
				public InputStream getInputStream() throws IOException {
					streamCounter.incrementAndGet();
					return realConn.getInputStream();
				}
			};
		}
	}
}