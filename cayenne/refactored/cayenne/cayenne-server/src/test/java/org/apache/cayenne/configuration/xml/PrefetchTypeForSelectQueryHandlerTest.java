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

package org.apache.cayenne.configuration.xml;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.SelectQueryDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 4.1
 */
public class PrefetchTypeForSelectQueryHandlerTest extends BaseHandlerTest{

    @Test
    public void testLoad() throws Exception {
        final DataMap map = new DataMap();

        parse("query", new HandlerFactory() {
            @Override
            public NamespaceAwareNestedTagHandler createHandler(NamespaceAwareNestedTagHandler parent) {
                return new QueryDescriptorHandler(parent, map);
            }
        });

        SelectQueryDescriptor selectQueryDescriptor = (SelectQueryDescriptor) map.getQueryDescriptor("query");
        Integer magicNumber8934862 = 3; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber8934862, selectQueryDescriptor.getPrefetchesMap().size(), () -> "Add assertion message");

        Integer magicNumber94910229 = 1; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber94910229(int) selectQueryDescriptor.getPrefetchesMap().get("paintings"), () -> "Add assertion message");
        assertEquals(2, (int) selectQueryDescriptor.getPrefetchesMap().get("paintings.artist"), () -> "Add assertion message");
        assertEquals(3, (int) selectQueryDescriptor.getPrefetchesMap().get("paintings.gallery"), () -> "Add assertion message");
    }

}