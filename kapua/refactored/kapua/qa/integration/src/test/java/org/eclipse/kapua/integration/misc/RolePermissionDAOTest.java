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
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionCreator;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionDAO;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionImpl;
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
public class RolePermissionDAOTest {

    EntityManager entityManager;
    RolePermissionCreator creator;
    PermissionImpl permission;
    RolePermissionImpl entityToFindOrDelete;
    KapuaId scopeId, roleId;
    KapuaQuery kapuaQuery;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: entityManager = Mockito.mock(EntityManager.class);
        // Removed line due to General Fixture smell: creator = Mockito.mock(RolePermissionCreator.class);
        // Removed line due to General Fixture smell: permission = Mockito.mock(PermissionImpl.class);
        // Removed line due to General Fixture smell: entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        // Removed line due to General Fixture smell: scopeId = KapuaId.ONE;
        // Removed line due to General Fixture smell: roleId = KapuaId.ONE;
        // Removed line due to General Fixture smell: kapuaQuery = Mockito.mock(KapuaQuery.class);
    }

    @Test
    public void createTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(RolePermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getPermission()).thenReturn(permission);

        Assert.assertTrue("True expected.", RolePermissionDAO.create(entityManager, creator) instanceof RolePermission);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, RolePermissionDAO.create(entityManager, creator).getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, RolePermissionDAO.create(entityManager, creator).getRoleId());
        Assert.assertEquals("Expected and actual values should be the same.", permission, RolePermissionDAO.create(entityManager, creator).getPermission());
    }

    @Test(expected = KapuaEntityExistsException.class)
    public void createEntityExistsExceptionTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(RolePermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new EntityExistsException()).when(entityManager).flush();

        RolePermissionDAO.create(entityManager, creator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(RolePermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new PersistenceException()).when(entityManager).flush();

        RolePermissionDAO.create(entityManager, creator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionWithNotSQLCauseTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(RolePermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new PersistenceException(new Exception(new Exception()))).when(entityManager).flush();

        RolePermissionDAO.create(entityManager, creator);
    }

    @Test(expected = PersistenceException.class)
    public void createPersistenceExceptionWithSQLCauseTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(RolePermissionCreator.class);
        entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getPermission()).thenReturn(permission);
        Mockito.doThrow(new PersistenceException(new SQLException("reason", "23505", new Exception()))).when(entityManager).flush();

        RolePermissionDAO.create(entityManager, creator);
    }

    @Test(expected = NullPointerException.class)
    public void createNullEntityManagerTest() throws KapuaException {
        permission = Mockito.mock(PermissionImpl.class);
        creator = Mockito.mock(RolePermissionCreator.class);
        Mockito.when(creator.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(creator.getPermission()).thenReturn(permission);

        RolePermissionDAO.create(null, creator);
    }

    @Test(expected = NullPointerException.class)
    public void createNullRolePermissionCreatorTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        RolePermissionDAO.create(entityManager, (RolePermissionCreator) null);
    }

    @Test
    public void findSameScopeIdsTest() {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", RolePermissionDAO.find(entityManager, scopeId, roleId) instanceof RolePermission);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, RolePermissionDAO.find(entityManager, scopeId, roleId).getScopeId());
    }

    @Test
    public void findDifferentScopeIdsTest() {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ANY);

        Assert.assertNull("Null expected.", RolePermissionDAO.find(entityManager, scopeId, roleId));
    }

    @Test
    public void findNullEntityToFindTest() {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(null);

        Assert.assertNull("Null expected.", RolePermissionDAO.find(entityManager, scopeId, roleId));
    }

    @Test
    public void findNullEntityToFindScopeIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(null);

        Assert.assertTrue("True expected.", RolePermissionDAO.find(entityManager, KapuaId.ANY, roleId) instanceof RolePermission);
    }

    @Test(expected = NullPointerException.class)
    public void findNullEntityManagerTest() {
        roleId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        RolePermissionDAO.find(null, scopeId, roleId);
    }

    @Test
    public void findNullScopeIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", RolePermissionDAO.find(entityManager, null, roleId) instanceof RolePermission);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, RolePermissionDAO.find(entityManager, null, roleId).getScopeId());
    }

    @Test
    public void findNullRoleIdTest() {
        entityManager = Mockito.mock(EntityManager.class);
        scopeId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        Mockito.when(entityManager.find(RolePermissionImpl.class, null)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", RolePermissionDAO.find(entityManager, scopeId, null) instanceof RolePermission);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, RolePermissionDAO.find(entityManager, scopeId, null).getScopeId());
    }

    @Test
    public void queryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<RolePermissionImpl> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<RolePermissionImpl> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<RolePermissionImpl> criteriaQuery3 = Mockito.mock(CriteriaQuery.class);
        Root<RolePermissionImpl> entityRoot = Mockito.mock(Root.class);
        EntityType<RolePermissionImpl> entityType = Mockito.mock(EntityType.class);
        List<String> list = new ArrayList<>();
        TypedQuery<RolePermissionImpl> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(RolePermissionImpl.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(RolePermissionImpl.class)).thenReturn(entityRoot);
        Mockito.when(entityRoot.getModel()).thenReturn(entityType);
        Mockito.when(criteriaQuery1.select(entityRoot)).thenReturn(criteriaQuery2);
        Mockito.when(criteriaQuery2.distinct(true)).thenReturn(criteriaQuery3);
        Mockito.when(kapuaQuery.getFetchAttributes()).thenReturn(list);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);

        Assert.assertTrue("True expected.", RolePermissionDAO.query(entityManager, kapuaQuery) instanceof RolePermissionListResult);
    }

    @Test(expected = NullPointerException.class)
    public void queryNullEntityManagerTest() throws KapuaException {
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        RolePermissionDAO.query(null, kapuaQuery);
    }

    @Test(expected = NullPointerException.class)
    public void queryNullKapuaQueryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<RolePermissionImpl> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<RolePermissionImpl> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<RolePermissionImpl> criteriaQuery3 = Mockito.mock(CriteriaQuery.class);
        Root<RolePermissionImpl> entityRoot = Mockito.mock(Root.class);
        EntityType<RolePermissionImpl> entityType = Mockito.mock(EntityType.class);
        TypedQuery<RolePermissionImpl> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(RolePermissionImpl.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(RolePermissionImpl.class)).thenReturn(entityRoot);
        Mockito.when(entityRoot.getModel()).thenReturn(entityType);
        Mockito.when(criteriaQuery1.select(entityRoot)).thenReturn(criteriaQuery2);
        Mockito.when(criteriaQuery2.distinct(true)).thenReturn(criteriaQuery3);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);

        RolePermissionDAO.query(entityManager, null);
    }

    @Test
    public void countTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> criteriaQuery1 = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<Long> criteriaQuery2 = Mockito.mock(CriteriaQuery.class);
        Root<RolePermissionImpl> entityRoot = Mockito.mock(Root.class);
        TypedQuery<Long> query = Mockito.mock(TypedQuery.class);
        Selection<Long> selection = Mockito.mock(Selection.class);
        Expression<Long> expression = Mockito.mock(Expression.class);
        long[] longNumberList = {0L, 10L, 100L, 1000000L, 9223372036854775807L, -100L, -9223372036854775808L};

        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(Long.class)).thenReturn(criteriaQuery1);
        Mockito.when(criteriaQuery1.from(RolePermissionImpl.class)).thenReturn(entityRoot);
        Mockito.when(criteriaBuilder.countDistinct(entityRoot)).thenReturn(expression);
        Mockito.when(criteriaQuery1.select(selection)).thenReturn(criteriaQuery2);
        Mockito.when(entityManager.createQuery(criteriaQuery1)).thenReturn(query);
        for (long number : longNumberList) {
            Mockito.doReturn(number).when(query).getSingleResult();

            Assert.assertEquals("Expected and actual values should be the same.", number, RolePermissionDAO.count(entityManager, kapuaQuery));
        }
    }

    @Test(expected = NullPointerException.class)
    public void countNullEntityManagerTest() throws KapuaException {
        kapuaQuery = Mockito.mock(KapuaQuery.class);
        RolePermissionDAO.count(null, kapuaQuery);
    }

    @Test(expected = NullPointerException.class)
    public void countNullKapuaQueryTest() throws KapuaException {
        entityManager = Mockito.mock(EntityManager.class);
        RolePermissionDAO.count(entityManager, null);
    }

    @Test
    public void deleteTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(entityToFindOrDelete);
        Mockito.when(entityToFindOrDelete.getScopeId()).thenReturn(KapuaId.ONE);

        Assert.assertTrue("True expected.", RolePermissionDAO.delete(entityManager, scopeId, roleId) instanceof RolePermission);
    }

    @Test(expected = NullPointerException.class)
    public void deleteNullEntityManagerTest() throws KapuaEntityNotFoundException {
        roleId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        RolePermissionDAO.delete(null, scopeId, roleId);
    }

    @Test
    public void deleteNullScopeIdTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        entityToFindOrDelete = Mockito.mock(RolePermissionImpl.class);
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(entityToFindOrDelete);

        Assert.assertTrue("True expected.", RolePermissionDAO.delete(entityManager, null, roleId) instanceof RolePermission);
    }

    @Test(expected = NullPointerException.class)
    public void deleteNullRolePermissionIdTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        scopeId = KapuaId.ONE;
        RolePermissionDAO.delete(entityManager, scopeId, null);
    }

    @Test(expected = KapuaEntityNotFoundException.class)
    public void deleteNullEntityToDeleteTest() throws KapuaEntityNotFoundException {
        entityManager = Mockito.mock(EntityManager.class);
        roleId = KapuaId.ONE;
        scopeId = KapuaId.ONE;
        Mockito.when(entityManager.find(RolePermissionImpl.class, roleId)).thenReturn(null);

        RolePermissionDAO.delete(entityManager, scopeId, roleId);
    }
}