/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.model.query;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.RandomUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Category(JUnitTests.class)
public class KapuaListResultImplTest {

    private final static Random RANDOM = RandomUtils.getInstance();

    @Test
    public void kapuaListResultImplTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        Assert.assertNotNull(kapuaListResult.getItems(), () -> "Add assertion message");
        Assert.assertTrue(kapuaListResult.getItems().isEmpty(), () -> "Add assertion message");
        Assert.assertFalse(kapuaListResult.isLimitExceeded(), () -> "Add assertion message");
        Assert.assertNull(kapuaListResult.getTotalCount(), () -> "Add assertion message");
    }

    @Test
    public void addItemsTest() {
        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);

        List<AbstractKapuaEntity> items = new ArrayList<>();
        items.add(kapuaEntity1);
        items.add(kapuaEntity2);

        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();
        kapuaListResult.addItems(items);

        Integer magicNumber95372800 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber95372800, kapuaListResult.getItems().size(), () -> "Add assertion message");
        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItems().get(0), () -> "Add assertion message");
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItems().get(1), () -> "Add assertion message");
        Assert.assertEquals(items, kapuaListResult.getItems(), () -> "Add assertion message");
    }

    @Test
    public void addItemTest() {
        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);

        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();
        kapuaListResult.addItem(kapuaEntity1);
        kapuaListResult.addItem(kapuaEntity2);

        Integer magicNumber87228237 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber87228237, kapuaListResult.getItems().size(), () -> "Add assertion message");
        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItems().get(0), () -> "Add assertion message");
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItems().get(1), () -> "Add assertion message");
    }

    @Test
    public void clearItemsTest() {
        int testSize = 10;
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        for (int i = 0; i < testSize; i++) {
            kapuaListResult.addItem(Mockito.mock(AbstractKapuaEntity.class));
        }

        Assert.assertEquals(testSize, kapuaListResult.getSize(), () -> "Add assertion message");

        kapuaListResult.clearItems();

        Assert.assertEquals(0, kapuaListResult.getSize(), () -> "Add assertion message");
    }

    @Test
    public void getFirstItemTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity1);

        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity2);

        Assert.assertEquals(kapuaEntity1, kapuaListResult.getFirstItem(), () -> "Add assertion message");
        Assert.assertNotEquals(kapuaEntity2, kapuaListResult.getFirstItem());
    }

    @Test
    public void getItemsTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity1);

        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity2);

        Integer magicNumber25660315 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals(magicNumber25660315, kapuaListResult.getItems().size(), () -> "Add assertion message");
        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItems().get(0), () -> "Add assertion message");
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItems().get(1), () -> "Add assertion message");
    }

    @Test
    public void getItemsPredicateTest() {
        int testSize = 10;
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(i))));

            kapuaListResult.addItem(kapuaEntity);
        }

        List<AbstractKapuaEntity> filteredItems = kapuaListResult.getItems(i -> i.getId().getId().longValue() % 2 == 0);

        Assert.assertEquals(5, filteredItems.size(), () -> "Add assertion message");
        for (AbstractKapuaEntity filteredItem : filteredItems) {
            Assert.assertEquals(0, filteredItem.getId().getId().longValue() % 2, () -> "Add assertion message");
        }
    }

    @Test
    public void getItemsAsMapKeyTest() {
        int testSize = 10;
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(i))));

            kapuaListResult.addItem(kapuaEntity);
        }

        Map<KapuaId, AbstractKapuaEntity> itemsAsMap = kapuaListResult.getItemsAsMap(AbstractKapuaEntity::getId);

        Assert.assertEquals(kapuaListResult.getSize(), itemsAsMap.size(), () -> "Add assertion message");
        for (Map.Entry<KapuaId, AbstractKapuaEntity> entry : itemsAsMap.entrySet()) {
            Assert.assertEquals(entry.getKey(), entry.getValue().getId(), () -> "Add assertion message");
        }
    }

    @Test
    public void getItemsAsMapKeyValueTest() {
        int testSize = 10;
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(i))));

            kapuaListResult.addItem(kapuaEntity);
        }

        Map<KapuaId, KapuaId> itemsAsMap = kapuaListResult.getItemsAsMap(AbstractKapuaEntity::getId, AbstractKapuaEntity::getId);

        Assert.assertEquals(kapuaListResult.getSize(), itemsAsMap.size(), () -> "Add assertion message");
        for (Map.Entry<KapuaId, KapuaId> entry : itemsAsMap.entrySet()) {
            Assert.assertEquals(entry.getKey(), entry.getValue(), () -> "Add assertion message");
        }
    }


    @Test
    public void getItemTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity1);

        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity2);

        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItem(0), () -> "Add assertion message");
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItem(1), () -> "Add assertion message");
    }

    @Test
    public void getSizeTest() {
        int testSize = 10;
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        for (int i = 0; i < testSize; i++) {
            kapuaListResult.addItem(Mockito.mock(AbstractKapuaEntity.class));
        }

        Assert.assertEquals(testSize, kapuaListResult.getSize(), () -> "Add assertion message");
    }

    @Test
    public void isEmptyTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();
        Assert.assertTrue(kapuaListResult.isEmpty(), () -> "Add assertion message");

        kapuaListResult.addItem(Mockito.mock(AbstractKapuaEntity.class));
        Assert.assertFalse(kapuaListResult.isEmpty(), () -> "Add assertion message");
    }

    @Test
    public void setGetLimitExceededTrueTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();
        kapuaListResult.setLimitExceeded(true);

        Assert.assertTrue(kapuaListResult.isLimitExceeded(), () -> "Add assertion message");
    }

    @Test
    public void setGetLimitExceededFalseTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();
        kapuaListResult.setLimitExceeded(false);

        Assert.assertFalse(kapuaListResult.isLimitExceeded(), () -> "Add assertion message");
    }

    @Test
    public void setGetTotalCountDefaultTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        Assert.assertNull(kapuaListResult.getTotalCount(), () -> "Add assertion message");
    }

    @Test
    public void setGetTotalCountValueTest() {
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();
        kapuaListResult.setTotalCount(10L);

        Assert.assertEquals(new Long(10L), kapuaListResult.getTotalCount(), () -> "Add assertion message");
    }

    @Test
    public void sortComparatorTest() {
        int testSize = 10;
        KapuaListResultImpl<AbstractKapuaEntity> kapuaListResult = new KapuaListResultImpl<>();

        // Add the max value
        AbstractKapuaEntity kapuaEntityMax = Mockito.mock(AbstractKapuaEntity.class);
        Mockito.when(kapuaEntityMax.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(Long.MAX_VALUE))));
        kapuaListResult.addItem(kapuaEntityMax);

        // Add values between
        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(RANDOM.nextLong()))));

            kapuaListResult.addItem(kapuaEntity);
        }

        // Add the min value
        AbstractKapuaEntity kapuaEntityMin = Mockito.mock(AbstractKapuaEntity.class);
        Mockito.when(kapuaEntityMin.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(Long.MIN_VALUE))));
        kapuaListResult.addItem(kapuaEntityMin);

        // Check result are not sorted by `.getId()` ascending
        Assert.assertTrue(kapuaListResult.getItem(0).getId().getId().compareTo(kapuaListResult.getItem(1).getId().getId()) > 0, () -> "Add assertion message");
        Assert.assertTrue(kapuaListResult.getItem(0).getId().getId().compareTo(kapuaListResult.getItem(kapuaListResult.getSize() - 1).getId().getId()) > 0, () -> "Add assertion message");

        // Sort
        kapuaListResult.sort(Comparator.comparing(i -> i.getId().getId()));

        // Check result are sorted
        for (int i = 0; i < kapuaListResult.getSize() - 1; i++) {
            Assert.assertTrue(kapuaListResult.getItem(i).getId().getId().compareTo(kapuaListResult.getItem(i + 1).getId().getId()) < 0, () -> "Add assertion message");
        }
    }
}