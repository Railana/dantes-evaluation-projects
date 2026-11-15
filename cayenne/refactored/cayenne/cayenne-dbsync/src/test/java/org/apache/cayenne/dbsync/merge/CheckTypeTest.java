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

package org.apache.cayenne.dbsync.merge;

import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.factory.MySQLMergerTokenFactory;
import org.apache.cayenne.dbsync.merge.factory.PostgresMergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.map.*;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class CheckTypeTest {

    DbEntity dbEntity;

    DbAttribute original;

    DbAttribute imported;

    MergerTokenFactory mergerTokenFactory;

    MergerDiffPair<DbAttribute> diffPair;

    DbAttributeMerger dbAttributeMerger;

    @Before
    public void setUp(){
        dbEntity = new DbEntity("NEW_TABLE");
        // Removed line due to General Fixture smell: dbEntity = new DbEntity("NEW_TABLE");

        original = new DbAttribute("NAME");
        original.setEntity(dbEntity);
        dbEntity.addAttribute(original);

        imported = new DbAttribute("NAME");
        imported.setEntity(dbEntity);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: mergerTokenFactory = new MySQLMergerTokenFactory();
        // Removed line due to General Fixture smell: diffPair = new MergerDiffPair<>(original, imported);
        // Removed line due to General Fixture smell: dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    // Removed line due to General Fixture smell: }
// Removed line due to General Fixture smell: 
    // Removed line due to General Fixture smell: @Test
    public void testCheckBooleanBitTypeMySQL() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setType(Types.BOOLEAN);
        imported.setType(Types.BIT);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        Integer magicNumber37282137 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        assertEquals(magicNumber37282137, mergerTokens.size(), () -> "Add assertion message");
    }

    @Test
    public void testCheckBlobLongvarbinaryTypeMySQL() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setType(Types.BLOB);
        imported.setType(Types.LONGVARBINARY);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(0, mergerTokens.size(), () -> "Add assertion message");
    }

    @Test
    public void testCheckBooleanIntegerTypeMySQL() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setType(Types.BOOLEAN);
        imported.setType(Types.INTEGER);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(1, mergerTokens.size(), () -> "Add assertion message");

        MergerToken mergerToken1 = (MergerToken) mergerTokens.toArray()[0];
        String mergerToken = "NEW_TABLE.NAME type: INTEGER -> BOOLEAN";
        assertEquals(mergerToken, mergerToken1.getTokenValue(), () -> "Add assertion message");
    }

    @Test
    public void testCheckBooleanBitTypePostgres() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setType(Types.BOOLEAN);
        imported.setType(Types.BIT);

        mergerTokenFactory = new PostgresMergerTokenFactory();

        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(1, mergerTokens.size(), () -> "Add assertion message");

        MergerToken mergerToken1 = (MergerToken) mergerTokens.toArray()[0];
        String mergerToken = "NEW_TABLE.NAME type: BIT -> BOOLEAN";
        assertEquals(mergerToken, mergerToken1.getTokenValue(), () -> "Add assertion message");
    }

    @Test
    public void testCheckNumericDecimalType() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setType(Types.NUMERIC);
        imported.setType(Types.DECIMAL);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(0, mergerTokens.size(), () -> "Add assertion message");
    }

    @Test
    public void testCheckMaxLengthType() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setType(Types.CHAR);
        original.setMaxLength(1);
        imported.setType(Types.CHAR);
        imported.setMaxLength(2);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(1, mergerTokens.size(), () -> "Add assertion message");

        MergerToken mergerToken1 = (MergerToken) mergerTokens.toArray()[0];
        String mergerToken = "NEW_TABLE.NAME maxLength: 2 -> 1";
        assertEquals(mergerToken, mergerToken1.getTokenValue(), () -> "Add assertion message");
    }

    @Test
    public void testCheckScaleType() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setScale(1);
        imported.setScale(2);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(1, mergerTokens.size(), () -> "Add assertion message");

        MergerToken mergerToken1 = (MergerToken) mergerTokens.toArray()[0];
        String mergerToken = "NEW_TABLE.NAME scale: 2 -> 1";
        assertEquals(mergerToken, mergerToken1.getTokenValue(), () -> "Add assertion message");
    }

    @Test
    public void testCheckAttributePrecisionType() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        original.setAttributePrecision(1);
        imported.setAttributePrecision(2);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(1, mergerTokens.size(), () -> "Add assertion message");

        MergerToken mergerToken1 = (MergerToken) mergerTokens.toArray()[0];
        String mergerToken = "NEW_TABLE.NAME precision: 2 -> 1";
        assertEquals(mergerToken, mergerToken1.getTokenValue(), () -> "Add assertion message");
    }

    @Test
    public void testTimeType() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test
        original.setType(Types.TIMESTAMP);
        original.setMaxLength(19);

        imported.setType(Types.TIMESTAMP);
        imported.setMaxLength(0);

        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(1, mergerTokens.size(), () -> "Add assertion message");

        MergerToken mergerToken1 = (MergerToken) mergerTokens.toArray()[0];
        String mergerToken = "NEW_TABLE.NAME maxLength: 0 -> 19";
        assertEquals(mergerToken, mergerToken1.getTokenValue(), () -> "Add assertion message");
    }

    @Test
    public void testCheckTypeWithoutChanges() {

        mergerTokenFactory = new MySQLMergerTokenFactory();
        diffPair = new MergerDiffPair<>(original, imported);
        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
    }

    @Test

        diffPair = new MergerDiffPair<>(original, imported);

        dbAttributeMerger = new DbAttributeMerger(mergerTokenFactory, null);
        Collection<MergerToken> mergerTokens = dbAttributeMerger.createTokensForSame(diffPair);
        assertEquals(0, mergerTokens.size(), () -> "Add assertion message");
    }
}