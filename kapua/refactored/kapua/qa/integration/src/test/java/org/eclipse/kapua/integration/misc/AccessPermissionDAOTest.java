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
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionCreator;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.shiro.AccessPermissionDAO;
import org.eclipse.kapua.service.authorization.access.shiro.AccessPermissionImpl;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;
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
public class AccessPermissionDAOTest {

    EntityManager entityManager;
    AccessPermissionCreator creator;
    PermissionImpl permission;
    AccessPermissionImpl accessPermission, entityToFindOrDelete;
    KapuaId scopeId, accessPermissionId;
    KapuaQuery kapuaQuery;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: entityManager = Mockito.mock(EntityManager.class);
        // Removed line due to General Fixture smell: creator = Mockito.mock(AccessPermissionCreator.class);
        // Removed line due to General Fixture smell: permission = Mockito.mock(PermissionImpl.class);
        // Removed line due to General Fixture smell: accessPermission = Mockito.mock(AccessPermissionImpl.class);
        // Removed line due to General Fixture smell: entityToFindOrDelete = Mockito.mock(AccessPermissionImpl.class);
        // Removed line due to General Fixture smell: scopeId = KapuaId.ONE;
        // Removed line due to General Fixture smell: accessPermissionId = KapuaId.ONE;
        // Removed line due to General Fixture smell: kapuaQuery = Mockito.mock(KapuaQuery.class);
    }

    @Test
    public void createTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(AccessPermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getAccessInfoId()).thenReturn(KapuaId.ANY);
        Mockito.when(creator.getPermission()).thenReturn(permission);

        Assert.assertTrue("True expected.", AccessPermissionDAO.create(entityManager, creator) instanceof AccessPermission);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessPermissionDAO.create(entityManager, creator).getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, AccessPermissionDAO.create(entityManager, creator).getAccessInfoId());
        Assert.assertEquals("Expected and actual values should be the same.", permission, AccessPermissionDAO.create(entityManager, creator).getPermission());
    }

    @Test(expected = KapuaEntityExistsException.class)
    public void createEntityExistsExceptionTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(AccessPermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getAccessInfoId()).thenReturn(KapuaId.ANY);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new EntityExistsException()).when(entityManager).flush();

        AccessPermissionDAO.create(entityManager, creator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(AccessPermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getAccessInfoId()).thenReturn(KapuaId.ANY);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new PersistenceException()).when(entityManager).flush();

        AccessPermissionDAO.create(entityManager, creator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionWithNotSQLCauseTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(AccessPermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getAccessInfoId()).thenReturn(KapuaId.ANY);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new PersistenceException(new Exception(new Exception()))).when(entityManager).flush();

        AccessPermissionDAO.create(entityManager, creator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionWithSQLCauseTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(AccessPermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getAccessInfoId()).thenReturn(KapuaId.ANY);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new PersistenceException(new SQLException("reason", "23505", new Exception()))).when(entityManager).flush();

        AccessPermissionDAO.create(entityManager, creator);
    }

    @Test(expected = NullPointerException.class)
    public void createNullEntityManagerTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(AccessPermissionCreator.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getAccessInfoId()).thenReturn(KapuaId.ANY);
        Mockito.when(creator.getPermission()).thenReturn(permission);

        AccessPermissionDAO.create(null, creator);
    }

    @Test(expected = NullPointerException.class)
    public void createNullAccessPermissionCreatorTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        AccessPermissionDAO.create(entityManager, (AccessPermissionCreator) null);
    }

    @Test
    public void findDifferentScopeIdsTest() {
        entityManager = Mockito.mock(EntityManager.class);
        accessPermissionId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(AccessPermissionImpl.class);
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, accessPermissionId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ANY);

        Assert.assertNull("Null expected.", AccessPermissionDAO.find(entityManager, scopeId, accessPermissionId));
    }

    @Test
    public void findNullEntityToFindTest() {
        entityManager = Mockito.mock(EntityManager.class);
        accessPermissionId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, accessPermissionId)).thenReturn(null);

        Assert.assertNull("Null expected.", AccessPermissionDAO.find(entityManager, scopeId, accessPermissionId));
    }

    @Test
    public void findNullEntityToFindScopeIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        accessPermissionId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(AccessPermissionImpl.class);
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, accessPermissionId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(null);

        Assert.assertTrue("True expected.", AccessPermissionDAO.find(entityManager, KapuaId.ANY, accessPermissionId) instanceof AccessPermission);
    }

    @Test(expected = NullPointerException.class)
    public void findNullEntityManagerTest() {
        accessPermissionId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        AccessPermissionDAO.find(null, scopeId, accessPermissionId);
    }

    @Test
    public void findNullScopeIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        accessPermissionId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(AccessPermissionImpl.class);
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, accessPermissionId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessPermissionDAO.find(entityManager, null, accessPermissionId) instanceof AccessPermission);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessPermissionDAO.find(entityManager, null, accessPermissionId).getScopeId());
    }

    @Test
    public void findNullAccessPermissionIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        scopeId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, null)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessPermissionDAO.find(entityManager, scopeId, null) instanceof AccessPermission);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, AccessPermissionDAO.find(entityManager, scopeId, null).getScopeId());
    }

    @Test
    public void queryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<AccessPermissionImpl> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessPermissionImpl> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessPermissionImpl> criteriaQuery3 = Mockito.mock(CriteriaQuery.class);
        Root<AccessPermissionImpl> entityRoot = Mockito.mock(Root.class);
        EntityType<AccessPermissionImpl> entityType = Mockito.mock(EntityType.class);
        List<String> list = new ArrayList<>();
        TypedQuery<AccessPermissionImpl> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(AccessPermissionImpl.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(AccessPermissionImpl.class)).thenReturn(entityRoot);
        Mockito.when(entityRoot.getModel()).thenReturn(entityType);
        Mockito.when(criteriaQuery1.select(entityRoot)).thenReturn(criteriaQuery2);
        Mockito.when(criteriaQuery2.distinct(true)).thenReturn(criteriaQuery3);
        Mockito.when(kapuaQuery.getFetchAttributes()).thenReturn(list);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);

        Assert.assertTrue("True expected.", AccessPermissionDAO.query(entityManager, kapuaQuery) instanceof AccessPermissionListResult);
    }

    @Test(expected = NullPointerException.class)
    public void queryNullEntityManagerTest() throws KapuaException {
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        AccessPermissionDAO.query(null, kapuaQuery);
    }

    @Test(expected = NullPointerException.class)
    public void queryNullKapuaQueryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<AccessPermissionImpl> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessPermissionImpl> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<AccessPermissionImpl> criteriaQuery3 = Mockito.mock(CriteriaQuery.class);
        Root<AccessPermissionImpl> entityRoot = Mockito.mock(Root.class);
        EntityType<AccessPermissionImpl> entityType = Mockito.mock(EntityType.class);
        TypedQuery<AccessPermissionImpl> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(AccessPermissionImpl.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(AccessPermissionImpl.class)).thenReturn(entityRoot);
        Mockito.when(entityRoot.getModel()).thenReturn(entityType);
        Mockito.when(criteriaQuery1.select(entityRoot)).thenReturn(criteriaQuery2);
        Mockito.when(criteriaQuery2.distinct(true)).thenReturn(criteriaQuery3);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);

        AccessPermissionDAO.query(entityManager, null);
    }

    @Test
    public void countTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<Long> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        Root<AccessPermissionImpl> entityRoot = Mockito.mock(Root.class);
        TypedQuery<Long> query = Mockito.mock(TypedQuery.class);
        Selection<Long> selection = Mockito.mock(Selection.class);
        Expression<Long> expression = Mockito.mock(Expression.class);
        long[] longNumberList = {0L, 10L, 100L, 1000000L, 9223372036854775807L, -100L, -9223372036854775808L};

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(Long.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(AccessPermissionImpl.class)).thenReturn(entityRoot);
        Mockito.when(criteriaBuilder.countDistinct(entityRoot)).thenReturn(expression);
        Mockito.when(criteriaQuery1.select(selection)).thenReturn(criteriaQuery2);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);
        for (long number : longNumberList) {
            Mockito.doReturn(number).when(query).getSingleResult();

            Assert.assertEquals("Expected and actual values should be the same.", number, AccessPermissionDAO.count(entityManager, kapuaQuery));
        }
    }

    @Test(expected = NullPointerException.class)
    public void countNullEntityManagerTest() throws KapuaException {
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        AccessPermissionDAO.count(null, kapuaQuery);
    }

    @Test(expected = NullPointerException.class)
    public void countNullKapuaQueryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        AccessPermissionDAO.count(entityManager, null);
    }

    @Test
    public void deleteTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        accessPermissionId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(AccessPermissionImpl.class);
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, accessPermissionId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", AccessPermissionDAO.delete(entityManager, scopeId, accessPermissionId) instanceof AccessPermission);
    }

    @Test(expected = NullPointerException.class)
    public void deleteNullEntityManagerTest() throws KapuaEntityNotFoundException {
        accessPermissionId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        AccessPermissionDAO.delete(null, scopeId, accessPermissionId);
    }

    @Test
    public void deleteNullScopeIdTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        accessPermissionId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(AccessPermissionImpl.class);
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, accessPermissionId)).thenReturn(entityToFindOrDelete);

        Assert.assertTrue("True expected.", AccessPermissionDAO.delete(entityManager, null, accessPermissionId) instanceof AccessPermission);
    }

    @Test(expected = NullPointerException.class)
    public void deleteNullAccessPermissionIdTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        scopeId = KapuaId.ONE;
        AccessPermissionDAO.delete(entityManager, scopeId, null);
    }

    @Test(expected = KapuaEntityNotFoundException.class)
    public void deleteNullEntityToDeleteTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        accessPermissionId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        accessPermission = Mockito.mock(AccessPermissionImpl.class);
        Mockito.when(entityManager.find(AccessPermissionImpl.class, accessPermissionId)).thenReturn(null);

        AccessPermissionDAO.delete(entityManager, scopeId, accessPermissionId);
    }
}