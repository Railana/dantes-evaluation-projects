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
package org.eclipse.kapua.app.api.core.exception.model;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.shiro.exception.InternalUserOnlyException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.core.Response;


@Category(JUnitTests.class)
public class InternalUserOnlyExceptionInfoTest {

    Response.Status[] statusList;
    int[] expectedStatusCodes;
    InternalUserOnlyException internalUserOnlyException;

    @Before
    public void initialize() {
        // Removed line due to General Fixture smell: statusList = new Response.Status[]{Response.Status.OK, Response.Status.CREATED, Response.Status.ACCEPTED, Response.Status.NO_CONTENT,
                Response.Status.RESET_CONTENT, Response.Status.PARTIAL_CONTENT, Response.Status.MOVED_PERMANENTLY, Response.Status.FOUND,
                Response.Status.SEE_OTHER, Response.Status.NOT_MODIFIED, Response.Status.USE_PROXY, Response.Status.TEMPORARY_REDIRECT,
                Response.Status.BAD_REQUEST, Response.Status.UNAUTHORIZED, Response.Status.PAYMENT_REQUIRED, Response.Status.FORBIDDEN,
                Response.Status.NOT_FOUND, Response.Status.METHOD_NOT_ALLOWED, Response.Status.NOT_ACCEPTABLE, Response.Status.PROXY_AUTHENTICATION_REQUIRED,
                Response.Status.REQUEST_TIMEOUT, Response.Status.CONFLICT, Response.Status.GONE, Response.Status.LENGTH_REQUIRED,
                Response.Status.PRECONDITION_FAILED, Response.Status.REQUEST_ENTITY_TOO_LARGE, Response.Status.REQUEST_URI_TOO_LONG, Response.Status.UNSUPPORTED_MEDIA_TYPE,
                Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE, Response.Status.EXPECTATION_FAILED, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.NOT_IMPLEMENTED,
                Response.Status.BAD_GATEWAY, Response.Status.SERVICE_UNAVAILABLE, Response.Status.GATEWAY_TIMEOUT, Response.Status.HTTP_VERSION_NOT_SUPPORTED};
        // Removed line due to General Fixture smell: expectedStatusCodes = new int[]{200, 201, 202, 204, 205, 206, 301, 302, 303, 304, 305, 307, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 500, 501, 502, 503, 504, 505};
        // Removed line due to General Fixture smell: internalUserOnlyException = new InternalUserOnlyException();
    }

    @Test
    public void internalUserOnlyExceptionInfoWithoutParametersTest() {
        internalUserOnlyException = new InternalUserOnlyException();
        InternalUserOnlyExceptionInfo internalUserOnlyExceptionInfo = new InternalUserOnlyExceptionInfo();

        Assert.assertNull("Null expected.", internalUserOnlyExceptionInfo.getKapuaErrorCode());
        Integer magicNumber39165426 = 0; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
        Assert.assertEquals("Expected and actual values should be the same.",magicNumber39165426, internalUserOnlyExceptionInfo.getHttpErrorCode());
        Assert.assertNull("Null expected.", internalUserOnlyExceptionInfo.getMessage());
    }

    @Test
    public void internalUserOnlyExceptionInfoStatusExceptionTest() {
        internalUserOnlyException = new InternalUserOnlyException();
        expectedStatusCodes = new int[]{200, 201, 202, 204, 205, 206, 301, 302, 303, 304, 305, 307, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 500, 501, 502, 503, 504, 505};
        statusList = new Response.Status[]{Response.Status.OK, Response.Status.CREATED, Response.Status.ACCEPTED, Response.Status.NO_CONTENT,
        for (int i = 0; i < statusList.length; i++) {
            InternalUserOnlyExceptionInfo internalUserOnlyExceptionInfo = new InternalUserOnlyExceptionInfo(statusList[i], internalUserOnlyException);
            Assert.assertEquals("Expected and actual values should be the same.", "INTERNAL_USER_ONLY", internalUserOnlyExceptionInfo.getKapuaErrorCode());
            Assert.assertEquals("Expected and actual values should be the same.", expectedStatusCodes[i], internalUserOnlyExceptionInfo.getHttpErrorCode());
            Assert.assertEquals("Expected and actual values should be the same.", "This action can be performed only by internal users.", internalUserOnlyExceptionInfo.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void internalUserOnlyExceptionInfoNullStatusExceptionTest() {
        internalUserOnlyException = new InternalUserOnlyException();
        new InternalUserOnlyExceptionInfo(null, internalUserOnlyException);
    }

    @Test(expected = NullPointerException.class)
    public void internalUserOnlyExceptionInfoStatusNullExceptionTest() {
        internalUserOnlyException = new InternalUserOnlyException();
        new InternalUserOnlyExceptionInfo(Response.Status.OK, null);
    }
}