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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.eclipse.kapua.app.api.core.filter.KapuaSessionCleanupFilter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


@Category(JUnitTests.class)
public class KapuaSessionCleanupFilterTest {

    KapuaSessionCleanupFilter kapuaSessionCleanupFilter;
    URL shiroIniUrl;
    Ini shiroIni;
    InputStream input;
    SecurityManager securityManager;
    ServletRequest request;
    ServletResponse response;
    FilterChain chain;
    KapuaSession kapuaSession;

    @Before
    public void initialize() throws IOException {
        // Removed line due to General Fixture smell: kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        // Removed line due to General Fixture smell: shiroIniUrl = getClass().getResource("/shiro.ini");
        // Removed line due to General Fixture smell: shiroIni = new Ini();
        // Removed line due to General Fixture smell: input = shiroIniUrl.openStream();
        shiroIni.load(input);
        // Removed line due to General Fixture smell: securityManager = new IniSecurityManagerFactory(shiroIni).getInstance();

        // Removed line due to General Fixture smell: request = Mockito.mock(ServletRequest.class);
        // Removed line due to General Fixture smell: response = Mockito.mock(ServletResponse.class);
        // Removed line due to General Fixture smell: chain = Mockito.mock(FilterChain.class);
        // Removed line due to General Fixture smell: kapuaSession = new KapuaSession();
    }

    //COMMENT: Method init(FilterConfig filterConfig) is empty
    @Test
    public void initTest() {
        kapuaSession = new KapuaSession();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        assertThrows(Exception e,
        		() -> kapuaSessionCleanupFilter.init(Mockito.mock(FilterConfig.class)));
    }

    @Test
    public void initNullTest() {
        kapuaSession = new KapuaSession();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        assertThrows(Exception e,
        		() -> kapuaSessionCleanupFilter.init(null));
    }

    //COMMENT: Method destroy() is empty
    @Test
    public void destroyTest() {
        kapuaSession = new KapuaSession();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        assertThrows(Exception e,
        		() -> kapuaSessionCleanupFilter.destroy());
    }

    @Test
    public void doFilterTest() {
        kapuaSession = new KapuaSession();
        chain = Mockito.mock(FilterChain.class);
        response = Mockito.mock(ServletResponse.class);
        request = Mockito.mock(ServletRequest.class);
        securityManager = new IniSecurityManagerFactory(shiroIni).getInstance();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        SecurityUtils.setSecurityManager(securityManager);
        KapuaSecurityUtils.setSession(kapuaSession);

        assertThrows(Exception e,
        		() -> kapuaSessionCleanupFilter.doFilter(request, response, chain));
    }

    @Test
    public void doFilterNullRequestTest() {
        kapuaSession = new KapuaSession();
        chain = Mockito.mock(FilterChain.class);
        response = Mockito.mock(ServletResponse.class);
        securityManager = new IniSecurityManagerFactory(shiroIni).getInstance();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        SecurityUtils.setSecurityManager(securityManager);
        KapuaSecurityUtils.setSession(kapuaSession);

        assertThrows(Exception e,
        		() -> kapuaSessionCleanupFilter.doFilter(null, response, chain));
    }

    @Test
    public void doFilterNullResponseTest() {
        kapuaSession = new KapuaSession();
        chain = Mockito.mock(FilterChain.class);
        request = Mockito.mock(ServletRequest.class);
        securityManager = new IniSecurityManagerFactory(shiroIni).getInstance();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        SecurityUtils.setSecurityManager(securityManager);
        KapuaSecurityUtils.setSession(kapuaSession);

        assertThrows(Exception e,
        		() -> kapuaSessionCleanupFilter.doFilter(request, null, chain));
    }

    @Test
    public void doFilterNullChainTest() {
        kapuaSession = new KapuaSession();
        response = Mockito.mock(ServletResponse.class);
        request = Mockito.mock(ServletRequest.class);
        securityManager = new IniSecurityManagerFactory(shiroIni).getInstance();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        SecurityUtils.setSecurityManager(securityManager);
        KapuaSecurityUtils.setSession(kapuaSession);

        assertThrows(Exception e,
        		() -> {
        			kapuaSessionCleanupFilter.doFilter(request, response, null);
        			Assert.fail("Exception expected.");
        		});
    }

    @Test
    public void doFilterNullSessionTest() {
        kapuaSession = new KapuaSession();
        chain = Mockito.mock(FilterChain.class);
        response = Mockito.mock(ServletResponse.class);
        request = Mockito.mock(ServletRequest.class);
        securityManager = new IniSecurityManagerFactory(shiroIni).getInstance();
        kapuaSessionCleanupFilter = new KapuaSessionCleanupFilter();
        SecurityUtils.setSecurityManager(securityManager);
        try {
            kapuaSessionCleanupFilter.doFilter(request, response, chain);
        } catch (Exception e) {
            Assert.fail("Exception not expected.");
        }
    }
}