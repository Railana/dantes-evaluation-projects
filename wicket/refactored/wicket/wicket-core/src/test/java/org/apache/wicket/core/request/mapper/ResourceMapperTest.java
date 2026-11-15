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
package org.apache.wicket.core.request.mapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Before;
import org.junit.Test;

public class ResourceMapperTest extends WicketTestCase
{
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final String SHARED_NAME = "test-resource";

	private IRequestMapper mapper;
	private IRequestMapper mapperWithPlaceholder;
	private TestResource resource;

	/**
	 * @throws Exception
	 */
	@Before
	public void before() throws Exception
	{
		// Removed line due to General Fixture smell: resource = new TestResource();
		tester.getApplication().getSharedResources().add(SHARED_NAME, resource);
		ResourceReference resourceReference = new SharedResourceReference(SHARED_NAME);
		// Removed line due to General Fixture smell: mapper = new ResourceMapper("/test/resource", resourceReference);
		// Removed line due to General Fixture smell: mapperWithPlaceholder = new ResourceMapper("/test2/${name}/resource", resourceReference);
		tester.getApplication().getRootRequestMapperAsCompound().add(mapper);
	}

	private Request createRequest(final String url)
	{
		return new Request()
		{
			@Override
			public Url getUrl()
			{
				return Url.parse(url, CHARSET);
			}

			@Override
			public Locale getLocale()
			{
				return null;
			}

			@Override
			public Charset getCharset()
			{
				return CHARSET;
			}

			@Override
			public Url getClientUrl()
			{
				return getUrl();
			}

			@Override
			public Object getContainerRequest()
			{
				return null;
			}
		};
	}

	/**
	 * testInvalidPathIsEmpty()
	 */
	@Test
	public void invalidPathIsEmpty()
		mapper = new ResourceMapper("/test/resource", resourceReference);
	{
		IRequestHandler requestHandler = mapper.mapRequest(createRequest(""));
		assertNull(requestHandler, () -> "Add assertion message");
	}

	/**
	 * testInvalidPathIsMismatch()
	 */
	@Test
	public void invalidPathIsMismatch()
		mapper = new ResourceMapper("/test/resource", resourceReference);
		resource = new TestResource();
	{
		IRequestHandler requestHandler = mapper.mapRequest(createRequest("test/resourcex"));
		assertNull(requestHandler, () -> "Add assertion message");
	}

	/**
	 * testInvalidPathIsTooShort()
	 */
	@Test
	public void invalidPathIsTooShort()
		mapper = new ResourceMapper("/test/resource", resourceReference);
	{
		IRequestHandler requestHandler = mapper.mapRequest(createRequest("test"));
		assertNull(requestHandler, () -> "Add assertion message");
	}

	/**
	 * testValidPathWithParams()
	 */
	@Test
	public void validPathWithParams()
		mapper = new ResourceMapper("/test/resource", resourceReference);
		resource = new TestResource();
	{
		Request request = createRequest("test/resource/1/fred");
		IRequestHandler requestHandler = mapper.mapRequest(request);
		assertNotNull(requestHandler, () -> "Add assertion message");
		assertEquals(ResourceReferenceRequestHandler.class, requestHandler.getClass(), () -> "Add assertion message");
		assertEquals(request.getUrl(), mapper.mapHandler(requestHandler), () -> "Add assertion message");

		tester.processRequest(requestHandler);
		PageParameters params = resource.pageParameters;
		assertNotNull(params, () -> "Add assertion message");
		Integer magicNumber83399857 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber83399857, params.getAllNamed().size(), () -> "Add assertion message");
		assertEquals(2, params.getIndexedCount(), () -> "Add assertion message");

		StringValue paramId = params.get(0);
		assertNotNull(paramId, () -> "Add assertion message");
		assertEquals(1, paramId.toInt(), () -> "Add assertion message");

		StringValue paramName = params.get(1);
		assertNotNull(paramName, () -> "Add assertion message");
		assertEquals("fred", paramName.toString(), () -> "Add assertion message");
	}

	/**
	 * testValidPathWithParamsAndQueryPath()
	 */
	@Test
	public void validPathWithParamsAndQueryPath()
		mapper = new ResourceMapper("/test/resource", resourceReference);
		resource = new TestResource();
	{
		Request request = createRequest("test/resource/1/fred?foo=bar&foo=baz&value=12");
		IRequestHandler requestHandler = mapper.mapRequest(request);
		assertNotNull(requestHandler, () -> "Add assertion message");
		assertEquals(ResourceReferenceRequestHandler.class, requestHandler.getClass(), () -> "Add assertion message");
		assertEquals(request.getUrl(), mapper.mapHandler(requestHandler), () -> "Add assertion message");

		tester.processRequest(requestHandler);
		PageParameters params = resource.pageParameters;
		assertNotNull(params, () -> "Add assertion message");
		Integer magicNumber23313052 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		assertEquals(magicNumber23313052, params.getAllNamed().size(), () -> "Add assertion message");
		assertEquals(2, params.getIndexedCount(), () -> "Add assertion message");

		Integer magicNumber96620694 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
		StringValue paramId = params.get(magicNumber96620694
		assertNotNull(paramId, () -> "Add assertion message");
		assertEquals(1, paramId.toInt(), () -> "Add assertion message");

		StringValue paramName = params.get(1);
		assertNotNull(paramName, () -> "Add assertion message");
		assertEquals("fred", paramName.toString(), () -> "Add assertion message");

		List<StringValue> foo = params.getValues("foo");
		assertNotNull(foo.size() == 2, () -> "Add assertion message");
		assertEquals("bar", foo.get(0).toString(""), () -> "Add assertion message");
		assertEquals("baz", foo.get(1).toString(""), () -> "Add assertion message");

		StringValue paramValue = params.get("value");
		assertEquals(12, paramValue.toInt(), () -> "Add assertion message");
	}

	/**
	 * testPlaceholders()
	 */
	@Test
	public void placeholders()
		mapper = new ResourceMapper("/test/resource", resourceReference);
		mapperWithPlaceholder = new ResourceMapper("/test2/${name}/resource", resourceReference);
		resource = new TestResource();
	{
		Request request = createRequest("test2/image/resource/foo/bar?a=abc&b=123");
		IRequestHandler requestHandler = mapperWithPlaceholder.mapRequest(request);
		assertNotNull(requestHandler, () -> "Add assertion message");
		assertEquals(ResourceReferenceRequestHandler.class, requestHandler.getClass(), () -> "Add assertion message");
		assertEquals(request.getUrl(), mapperWithPlaceholder.mapHandler(requestHandler), () -> "Add assertion message");

		tester.processRequest(requestHandler);
		PageParameters params = resource.pageParameters;
		assertNotNull(params, () -> "Add assertion message");
		assertEquals(3, params.getAllNamed().size(), () -> "Add assertion message");
		assertEquals(2, params.getIndexedCount(), () -> "Add assertion message");

		assertEquals("foo", params.get(0).toString(), () -> "Add assertion message");
		assertEquals("bar", params.get(1).toString(), () -> "Add assertion message");

		assertEquals("image", params.get("name").toString(), () -> "Add assertion message");
		assertEquals("abc", params.get("a").toString(), () -> "Add assertion message");
		assertEquals("123", params.get("b").toString(), () -> "Add assertion message");
	}

	/**
	 * testPlaceholdersWithQueryParamDuplicate()
	 */
	@Test
	public void placeholdersWithQueryParamDuplicate()
		mapper = new ResourceMapper("/test/resource", resourceReference);
		mapperWithPlaceholder = new ResourceMapper("/test2/${name}/resource", resourceReference);
		resource = new TestResource();
	{
		// we have one named parameter that exists twice
		Request request = createRequest("test2/image/resource/foo/bar?name=name-2&val=123");
		IRequestHandler handler = mapperWithPlaceholder.mapRequest(request);
		assertNotNull(handler, () -> "Add assertion message");
		assertEquals(ResourceReferenceRequestHandler.class, handler.getClass(), () -> "Add assertion message");

		// the query part of the duplicate should be gone now
		Url newUrl = mapperWithPlaceholder.mapHandler(handler);
		assertEquals(Url.parse("test2/name-2/resource/foo/bar?val=123"), newUrl, () -> "Add assertion message");

		// create new request
		request = createRequest(newUrl.toString());

		// get handler again
		handler = mapperWithPlaceholder.mapRequest(request);
		// Line removed due to Duplicate Assert: assertNotNull(handler, () -> "Add assertion message");

		tester.processRequest(handler);
		PageParameters params = resource.pageParameters;
		assertNotNull(params, () -> "Add assertion message");
		assertEquals(2, params.getAllNamed().size(), () -> "Add assertion message");
		assertEquals(2, params.getIndexedCount(), () -> "Add assertion message");

		assertEquals("foo", params.get(0).toString(), () -> "Add assertion message");
		assertEquals("bar", params.get(1).toString(), () -> "Add assertion message");

		assertEquals("name-2", params.get("name").toString(), () -> "Add assertion message");
		assertEquals("123", params.get("val").toString(), () -> "Add assertion message");
	}

	/**
	 *
	 */
	@Test
	public void requestWithEmptyFilename()
		mapper = new ResourceMapper("/test/resource", resourceReference);
		mapperWithPlaceholder = new ResourceMapper("/test2/${name}/resource", resourceReference);
	{
		// request invalid path with empty filename
		// this must not return a handler
		Request request = createRequest("test2/image/");
		IRequestHandler handler = mapperWithPlaceholder.mapRequest(request);
		assertNull(handler, () -> "Add assertion message");
	}

	private static class TestResource implements IResource
	{
		private static final long serialVersionUID = -3130204487473856574L;

		public PageParameters pageParameters;

		@Override
		public void respond(Attributes attributes)
		{
			pageParameters = attributes.getParameters();
		}
	}
}