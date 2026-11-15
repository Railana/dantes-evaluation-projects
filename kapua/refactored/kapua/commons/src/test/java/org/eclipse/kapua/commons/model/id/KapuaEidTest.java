/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.model.id;

import org.eclipse.kapua.commons.util.RandomUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;


@Category(JUnitTests.class)
@RunWith(value = Parameterized.class)
public class KapuaEidTest {

    private final static Random RANDOM = RandomUtils.getInstance();

    private final BigInteger eid;

    @Parameters
    public static Iterable<Object[]> eids() {
        return Arrays.asList(
                new Object[]{new BigInteger(64, RANDOM)},
                new Object[]{new BigInteger(64, RANDOM)});
    }

    public KapuaEidTest(BigInteger eid) {
        this.eid = eid;
    }

    @Test
    public void kapuaEidConstructorTest() {
        KapuaId id = new KapuaEid();
        KapuaEid kapuaEid = new KapuaEid(id);
        Assert.assertEquals("Expected and actual values should be the same!", id.getId(), kapuaEid.getId());
    }

    @Test
    public void parseKapuaIdTest() {
        KapuaEid kapuaEid = new KapuaEid(eid);
        KapuaEid parsedKapuaEid = KapuaEid.parseKapuaId(kapuaEid);

        Assert.assertTrue(kapuaEid == parsedKapuaEid, () -> "Add assertion message");
        Assert.assertEquals(kapuaEid, parsedKapuaEid, () -> "Add assertion message");
        Assert.assertEquals(eid, kapuaEid.getId(), () -> "Add assertion message");
        Assert.assertEquals(eid.toString(), kapuaEid.toString(), () -> "Add assertion message");

        KapuaId kapuaIdAny = KapuaId.ANY;
        KapuaEid parsedKapuaIdAny = KapuaEid.parseKapuaId(kapuaIdAny);

        Assert.assertTrue(parsedKapuaIdAny != kapuaIdAny, () -> "Add assertion message");
        Assert.assertEquals(parsedKapuaIdAny.getId(), kapuaIdAny.getId(), () -> "Add assertion message");
    }

    @Test
    public void parseCompactIdTest() {
        KapuaEid kid = new KapuaEid(eid);
        String sid = kid.toCompactId();

        Assert.assertEquals(eid, kid.getId(), () -> "Add assertion message");
        Assert.assertEquals(eid.toString(), kid.toString(), () -> "Add assertion message");

        KapuaEid kid1 = KapuaEid.parseCompactId(sid);
        Assert.assertEquals(eid, kid1.getId(), () -> "Add assertion message");
        Assert.assertEquals(kid.toString(), kid1.toString(), () -> "Add assertion message");
        Assert.assertEquals(kid.toCompactId(), kid1.toCompactId(), () -> "Add assertion message");
    }

    @Test
    public void getIdTest() {
        KapuaEid kapuaEid = new KapuaEid(eid);
        Assert.assertEquals(eid, kapuaEid.getId(), () -> "Add assertion message");
    }

    @Test
    public void toStringTest() {
        KapuaEid kapuaEid = new KapuaEid(eid);
        Assert.assertEquals(eid.toString(), kapuaEid.toString(), () -> "Add assertion message");
    }

    @Test
    public void hashCodeTest() {
        KapuaEid kapuaEid = new KapuaEid(eid);
        KapuaEid kapuaEidNull = new KapuaEid();

        int actualHash = kapuaEid.hashCode();
        int expectedHash = 31 + eid.hashCode();
        int actualHashNull = kapuaEidNull.hashCode();
        int expectedHashNull = 31;

        Assert.assertEquals("eid != null", expectedHash, actualHash);
        Assert.assertEquals("eid == null", expectedHashNull, actualHashNull);
    }

    @Test
    public void equalsTest() {
        KapuaEid kapuaEid = new KapuaEid(eid);
        short shortNum = 10000;
        KapuaEid kapuaEidNull = new KapuaEid();
        BigInteger otherId = new BigInteger(64, RANDOM);
        KapuaEid kapuaEidOtherId = new KapuaEid(otherId);
        KapuaEid kapuaEidSameId = new KapuaEid(eid);

        Assert.assertTrue("Expected true", kapuaEid.equals(kapuaEid));
        Assert.assertFalse("Expected false", kapuaEid.equals(null));

        Object[] values = new Object[]{"name", 1, 's', 1.25f, 1.50d, 3L, shortNum, false};
        for (Object object : values) {
            Assert.assertFalse("Expected false", kapuaEid.equals(object));
        }

        Assert.assertFalse("Expected false", kapuaEidNull.equals(kapuaEid));
        Assert.assertFalse("Expected false", kapuaEid.equals(kapuaEidOtherId));
        Assert.assertTrue("Expected true", kapuaEid.equals(kapuaEidSameId));
    }
}