/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.integration.misc;

import org.eclipse.kapua.KapuaEntityExistsException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoDAO;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Category(JUnitTests.class)
public class AccessInfoDAOTest {

    EntityManager entityManager;
    AccessInfoCreator accessInfoCreator;
    KapuaId scopeId, accessInfoId;
    AccessInfoImpl entityToFindOrDelete;
    KapuaQuery kapuaQuery;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: entityManager = Mockito.mock(EntityManager.class);
        // Removed line due to General Fixture smell: accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        // Removed line due to General Fixture smell: scopeId = KapuaId.ONE;
        // Removed line due to General Fixture smell: accessInfoId = KapuaId.ANY;
        // Removed line due to General Fixture smell: entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        // Removed line due to General Fixture smell: kapuaQuery = Mockito.mock(KapuaQuery.class);
    }

    @Test
    public void createTest() throws KapuaException {
        accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(accessInfoCreator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfoCreator.getUserId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessInfoDAO.create(entityManager, accessInfoCreator) instanceof AccessInfo);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessInfoDAO.create(entityManager, accessInfoCreator).getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessInfoDAO.create(entityManager, accessInfoCreator).getUserId());
    }

    @Test(expected = KapuaEntityExistsException.class)
    public void createEntityExistsExceptionTest() throws KapuaException {
        accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(accessInfoCreator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfoCreator.getUserId()).thenReturn(KapuaId.ONE);
        Mockito.doThrow(new EntityExistsException()).when(entityManager).flush();

        AccessInfoDAO.create(entityManager, accessInfoCreator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionWithoutCauseTest() throws KapuaException {
        accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(accessInfoCreator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfoCreator.getUserId()).thenReturn(KapuaId.ONE);
        Mockito.doThrow(new PersistenceException()).when(entityManager).flush();

        AccessInfoDAO.create(entityManager, accessInfoCreator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionWithNotSQLCauseTest() throws KapuaException {
        accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(accessInfoCreator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfoCreator.getUserId()).thenReturn(KapuaId.ONE);
        Mockito.doThrow(new PersistenceException(new Exception(new Exception()))).when(entityManager).flush();

        AccessInfoDAO.create(entityManager, accessInfoCreator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionWithSQLCauseTest() throws KapuaException {
        accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(accessInfoCreator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfoCreator.getUserId()).thenReturn(KapuaId.ONE);
        Mockito.doThrow(new PersistenceException(new SQLException("reason", "23505", new Exception()))).when(entityManager).flush();

        AccessInfoDAO.create(entityManager, accessInfoCreator);
    }

    @Test(expected = NullPointerException.class)
    public void createNullEntityManagerTest() throws KapuaException {
        accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        Mockito.when(accessInfoCreator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfoCreator.getUserId()).thenReturn(KapuaId.ONE);

        AccessInfoDAO.create(null, accessInfoCreator);
    }

    @Test(expected = NullPointerException.class)
    public void createNullAccessInfoCreatorTest() throws KapuaException {
        accessInfoCreator = Mockito.mock(AccessInfoCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(accessInfoCreator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfoCreator.getUserId()).thenReturn(KapuaId.ONE);

        AccessInfoDAO.create(entityManager, (AccessInfoCreator) null);
    }

    @Test
    public void findSameScopeIdsTest() {
        entityManager = Mockito.mock(EntityManager.class);
        entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        accessInfoId = KapuaId.ANY;
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessInfoDAO.find(entityManager, scopeId, accessInfoId) instanceof AccessInfo);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessInfoDAO.find(entityManager, scopeId, accessInfoId).getScopeId());
    }

    @Test
    public void findDifferentScopeIdsTest() {
        entityManager = Mockito.mock(EntityManager.class);
        entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        accessInfoId = KapuaId.ANY;
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ANY);

        Assert.assertNull("Null expected.", AccessInfoDAO.find(entityManager, scopeId, accessInfoId));
    }

    @Test
    public void findNullEntityToFindTest() {
        entityManager = Mockito.mock(EntityManager.class);
        accessInfoId = KapuaId.ANY;
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(null);

        Assert.assertNull("Null expected.", AccessInfoDAO.find(entityManager, scopeId, accessInfoId));
    }

    @Test
    public void findNullEntityToFindScopeIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        accessInfoId = KapuaId.ANY;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(null);

        Assert.assertTrue("True expected.", AccessInfoDAO.find(entityManager, KapuaId.ANY, accessInfoId) instanceof AccessInfo);
    }

    @Test(expected = NullPointerException.class)
    public void findNullEntityManagerTest() {
        accessInfoId = KapuaId.ANY;
        scopeId = KapuaId.ONE;
        AccessInfoDAO.find(null, scopeId, accessInfoId);
    }

    @Test
    public void findNullScopeIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        accessInfoId = KapuaId.ANY;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessInfoDAO.find(entityManager, null, accessInfoId) instanceof AccessInfo);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessInfoDAO.find(entityManager, null, accessInfoId).getScopeId());
    }

    @Test
    public void findNullAccessInfoIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(AccessInfoImpl.class, null)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessInfoDAO.find(entityManager, scopeId, null) instanceof AccessInfo);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessInfoDAO.find(entityManager, scopeId, null).getScopeId());
    }

    @Test
    public void queryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<AccessInfoImpl> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessInfoImpl> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessInfoImpl> criteriaQuery3 = Mockito.mock(CriteriaQuery.class);
        Root<AccessInfoImpl> entityRoot = Mockito.mock(Root.class);
        EntityType<AccessInfoImpl> entityType = Mockito.mock(EntityType.class);
        List<String> list = new ArrayList<>();
        TypedQuery<AccessInfoImpl> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(AccessInfoImpl.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(AccessInfoImpl.class)).thenReturn(entityRoot);
        Mockito.when(entityRoot.getModel()).thenReturn(entityType);
        Mockito.when(criteriaQuery1.select(entityRoot)).thenReturn(criteriaQuery2);
        Mockito.when(criteriaQuery2.distinct(true)).thenReturn(criteriaQuery3);
        Mockito.when(kapuaQuery.getFetchAttributes()).thenReturn(list);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);

        Assert.assertTrue("True expected.", AccessInfoDAO.query(entityManager, kapuaQuery) instanceof AccessInfoListResult);
    }

    @Test(expected = NullPointerException.class)
    public void queryNullEntityManagerTest() throws KapuaException {
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        AccessInfoDAO.query(null, kapuaQuery);
    }

    @Test(expected = NullPointerException.class)
    public void queryNullKapuaQueryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<AccessInfoImpl> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessInfoImpl> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessInfoImpl> criteriaQuery3 = Mockito.mock(CriteriaQuery.class);
        Root<AccessInfoImpl> entityRoot = Mockito.mock(Root.class);
        EntityType<AccessInfoImpl> entityType = Mockito.mock(EntityType.class);
        TypedQuery<AccessInfoImpl> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(AccessInfoImpl.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(AccessInfoImpl.class)).thenReturn(entityRoot);
        Mockito.when(entityRoot.getModel()).thenReturn(entityType);
        Mockito.when(criteriaQuery1.select(entityRoot)).thenReturn(criteriaQuery2);
        Mockito.when(criteriaQuery2.distinct(true)).thenReturn(criteriaQuery3);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);

        AccessInfoDAO.query(entityManager, null);
    }

    @Test
    public void countTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<Long> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        Root<AccessInfoImpl> entityRoot = Mockito.mock(Root.class);
        TypedQuery<Long> query = Mockito.mock(TypedQuery.class);
        Selection<Long> selection = Mockito.mock(Selection.class);
        Expression<Long> expression = Mockito.mock(Expression.class);
        long[] longNumberList = {0L, 10L, 100L, 1000000L, 9223372036854775807L, -100L, -9223372036854775808L};

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(Long.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(AccessInfoImpl.class)).thenReturn(entityRoot);
        Mockito.when(criteriaBuilder.countDistinct(entityRoot)).thenReturn(expression);
        Mockito.when(criteriaQuery1.select(selection)).thenReturn(criteriaQuery2);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);
        for (long number : longNumberList) {
            Mockito.doReturn(number).when(query).getSingleResult();

            Assert.assertEquals("Expected and actual values should be the same.", number, AccessInfoDAO.count(entityManager, kapuaQuery));
        }
    }

    @Test(expected = NullPointerException.class)
    public void countNullEntityManagerTest() throws KapuaException {
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        AccessInfoDAO.count(null, kapuaQuery);
    }

    @Test(expected = NullPointerException.class)
    public void countNullKapuaQueryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        AccessInfoDAO.count(entityManager, null);
    }

    @Test
    public void deleteTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        accessInfoId = KapuaId.ANY;
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessInfoDAO.delete(entityManager, scopeId, accessInfoId) instanceof AccessInfo);
    }

    @Test(expected = NullPointerException.class)
    public void deleteNullEntityManagerTest() throws KapuaEntityNotFoundException {
        accessInfoId = KapuaId.ANY;
        scopeId = KapuaId.ONE;
        AccessInfoDAO.delete(null, scopeId, accessInfoId);
    }

    @Test
    public void deleteNullScopeIdTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        entityToFindOrDelete = Mockito.mock(AccessInfoImpl.class);
        accessInfoId = KapuaId.ANY;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(entityToFindOrDelete);

        Assert.assertTrue("True expected.", AccessInfoDAO.delete(entityManager, null, accessInfoId) instanceof AccessInfo);
    }

    @Test(expected = NullPointerException.class)
    public void deleteNullAccessInfoIdIdTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        scopeId = KapuaId.ONE;
        AccessInfoDAO.delete(entityManager, scopeId, null);
    }

    @Test(expected = KapuaEntityNotFoundException.class)
    public void deleteNullEntityToDeleteTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        accessInfoId = KapuaId.ANY;
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(AccessInfoImpl.class, accessInfoId)).thenReturn(null);

        AccessInfoDAO.delete(entityManager, scopeId, accessInfoId);
    }
}