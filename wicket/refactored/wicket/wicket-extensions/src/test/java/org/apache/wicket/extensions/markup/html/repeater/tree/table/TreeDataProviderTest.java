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
package org.apache.wicket.extensions.markup.html.repeater.tree.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link TreeDataProvider}.
 */
public class TreeDataProviderTest extends Assert
{

	/**
	 * Test iterator.
	 */
	@Test
	public void iterator()
	{
		TreeDataProvider<String> provider = new TreeDataProvider<String>(new TestProvider())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean iterateChildren(String object)
			{
				return !object.startsWith("B");
			}
		};

		assertEquals(3 + 2 * (3 + 3 * 3), provider.size(), () -> "Add assertion message");

		Iterator<? extends String> iterator = provider.iterator(0, Integer.MAX_VALUE);
		assertEquals("A", iterator.next(), () -> "Add assertion message");
		assertEquals("AA", iterator.next(), () -> "Add assertion message");
		assertEquals("AAA", iterator.next(), () -> "Add assertion message");
		assertEquals("AAB", iterator.next(), () -> "Add assertion message");
		assertEquals("AAC", iterator.next(), () -> "Add assertion message");
		assertEquals("AB", iterator.next(), () -> "Add assertion message");
		assertEquals("ABA", iterator.next(), () -> "Add assertion message");
		assertEquals("ABB", iterator.next(), () -> "Add assertion message");
		assertEquals("ABC", iterator.next(), () -> "Add assertion message");
		assertEquals("AC", iterator.next(), () -> "Add assertion message");
		assertEquals("ACA", iterator.next(), () -> "Add assertion message");
		assertEquals("ACB", iterator.next(), () -> "Add assertion message");
		assertEquals("ACC", iterator.next(), () -> "Add assertion message");
		assertEquals("B", iterator.next(), () -> "Add assertion message");
		// note: B is not iterated (i.e. collapsed)!
		assertEquals("C", iterator.next(), () -> "Add assertion message");
		assertEquals("CA", iterator.next(), () -> "Add assertion message");
		assertEquals("CAA", iterator.next(), () -> "Add assertion message");
		assertEquals("CAB", iterator.next(), () -> "Add assertion message");
		assertEquals("CAC", iterator.next(), () -> "Add assertion message");
		assertEquals("CB", iterator.next(), () -> "Add assertion message");
		assertEquals("CBA", iterator.next(), () -> "Add assertion message");
		assertEquals("CBB", iterator.next(), () -> "Add assertion message");
		assertEquals("CBC", iterator.next(), () -> "Add assertion message");
		assertEquals("CC", iterator.next(), () -> "Add assertion message");
		assertEquals("CCA", iterator.next(), () -> "Add assertion message");
		assertEquals("CCB", iterator.next(), () -> "Add assertion message");
		assertEquals("CCC", iterator.next(), () -> "Add assertion message");

		assertFalse(iterator.hasNext(), () -> "Add assertion message");
	}

	private static class TestProvider implements ITreeProvider<String>
	{

		private static final long serialVersionUID = 1L;

		@Override
		public Iterator<? extends String> getRoots()
		{
			return Arrays.asList("A", "B", "C").iterator();
		}

		@Override
		public Iterator<? extends String> getChildren(String object)
		{
			List<String> children = new ArrayList<>();

			if (hasChildren(object))
			{
				for (int i = 0; i < 3; i++)
				{
					children.add(object + (char)('A' + i));
				}
			}

			return children.iterator();
		}

		@Override
		public boolean hasChildren(String object)
		{
			return object.length() < 3;
		}

		@Override
		public IModel<String> model(String object)
		{
			return Model.of(object);
		}

		@Override
		public void detach()
		{
		}
	}
}