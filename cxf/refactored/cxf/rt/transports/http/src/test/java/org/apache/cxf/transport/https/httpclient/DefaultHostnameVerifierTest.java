/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.cxf.transport.https.httpclient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DefaultHostnameVerifier
 *
 * Copied from httpclient
 */
public class DefaultHostnameVerifierTest {

    private DefaultHostnameVerifier impl;
    private PublicSuffixMatcher publicSuffixMatcher;
    private DefaultHostnameVerifier implWithPublicSuffixCheck;

    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: impl = new DefaultHostnameVerifier();
        publicSuffixMatcher = new PublicSuffixMatcher(DomainType.ICANN, Arrays.asList("com", "co.jp", "gov.uk"), null);
        // Removed line due to General Fixture smell: implWithPublicSuffixCheck = new DefaultHostnameVerifier(publicSuffixMatcher);
    }

    // CHECKSTYLE:OFF
    @Test
    public void testVerify() throws Exception {
        implWithPublicSuffixCheck = new DefaultHostnameVerifier(publicSuffixMatcher);
        impl = new DefaultHostnameVerifier();
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream in;
        X509Certificate x509;
        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);

        impl.verify("foo.com", x509);
        exceptionPlease(impl, "a.foo.com", x509);
        exceptionPlease(impl, "bar.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("\u82b1\u5b50.co.jp", x509);
        exceptionPlease(impl, "a.\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_FOO_BAR);
        x509 = (X509Certificate) cf.generateCertificate(in);
        exceptionPlease(impl, "foo.com", x509);
        exceptionPlease(impl, "a.foo.com", x509);
        impl.verify("bar.com", x509);
        exceptionPlease(impl, "a.bar.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_FOO_BAR_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        exceptionPlease(impl, "foo.com", x509);
        exceptionPlease(impl, "a.foo.com", x509);
        impl.verify("bar.com", x509);
        exceptionPlease(impl, "a.bar.com", x509);

        /*
               Java isn't extracting international subjectAlts properly.  (Or
               OpenSSL isn't storing them properly).
         */
        // DEFAULT.verify("\u82b1\u5b50.co.jp", x509 );
        // impl.verify("\u82b1\u5b50.co.jp", x509 );
        exceptionPlease(impl, "a.\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_NO_CNS_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("foo.com", x509);
        exceptionPlease(impl, "a.foo.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_NO_CNS_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("foo.com", x509);
        exceptionPlease(impl, "a.foo.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_THREE_CNS_FOO_BAR_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        exceptionPlease(impl, "foo.com", x509);
        exceptionPlease(impl, "a.foo.com", x509);
        exceptionPlease(impl, "bar.com", x509);
        exceptionPlease(impl, "a.bar.com", x509);
        impl.verify("\u82b1\u5b50.co.jp", x509);
        exceptionPlease(impl, "a.\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_WILD_FOO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        exceptionPlease(impl, "foo.com", x509);
        impl.verify("www.foo.com", x509);
        impl.verify("\u82b1\u5b50.foo.com", x509);
        exceptionPlease(impl, "a.b.foo.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_WILD_CO_JP);
        x509 = (X509Certificate) cf.generateCertificate(in);
        // Silly test because no-one would ever be able to lookup an IP address
        // using "*.co.jp".
        impl.verify("*.co.jp", x509);
        impl.verify("foo.co.jp", x509);
        impl.verify("\u82b1\u5b50.co.jp", x509);

        exceptionPlease(implWithPublicSuffixCheck, "foo.co.jp", x509);
        exceptionPlease(implWithPublicSuffixCheck, "\u82b1\u5b50.co.jp", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_WILD_FOO_BAR_HANAKO);
        x509 = (X509Certificate) cf.generateCertificate(in);
        // try the foo.com variations
        exceptionPlease(impl, "foo.com", x509);
        exceptionPlease(impl, "www.foo.com", x509);
        exceptionPlease(impl, "\u82b1\u5b50.foo.com", x509);
        exceptionPlease(impl, "a.b.foo.com", x509);
        // try the bar.com variations
        exceptionPlease(impl, "bar.com", x509);
        impl.verify("www.bar.com", x509);
        impl.verify("\u82b1\u5b50.bar.com", x509);
        exceptionPlease(impl, "a.b.bar.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.X509_MULTIPLE_VALUE_AVA);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("repository.infonotary.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.S_GOOGLE_COM);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("*.google.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.S_GOOGLE_COM);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("*.Google.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.IP_1_1_1_1);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("1.1.1.1", x509);

        exceptionPlease(impl, "1.1.1.2", x509);
        exceptionPlease(impl, "dummy-value.com", x509);

        in = new ByteArrayInputStream(CertificatesToPlayWith.EMAIL_ALT_SUBJECT_NAME);
        x509 = (X509Certificate) cf.generateCertificate(in);
        impl.verify("www.company.com", x509);
    }
    // CHECKSTYLE:ON

    @Test
    public void testSubjectAlt() throws Exception {
        impl = new DefaultHostnameVerifier();
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final InputStream in = new ByteArrayInputStream(CertificatesToPlayWith.X509_MULTIPLE_SUBJECT_ALT);
        final X509Certificate x509 = (X509Certificate) cf.generateCertificate(in);

        Assert.assertEquals("CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=CH",
                            x509.getSubjectDN().getName(), () -> "Add assertion message");

        impl.verify("localhost.localdomain", x509);
        impl.verify("127.0.0.1", x509);

        try {
            impl.verify("localhost", x509);
            Assert.fail("SSLException should have been thrown");
        } catch (final SSLException ex) {
            // expected
        }
        try {
            impl.verify("local.host", x509);
            // Line removed due to Duplicate Assert: Assert.fail("SSLException should have been thrown");
        } catch (final SSLException ex) {
            // expected
        }
        try {
            impl.verify("127.0.0.2", x509);
            // Line removed due to Duplicate Assert: Assert.fail("SSLException should have been thrown");
        } catch (final SSLException ex) {
            // expected
        }
    }

    public void exceptionPlease(final DefaultHostnameVerifier hv, final String host,
                                final X509Certificate x509) {
        try {
            hv.verify(host, x509);
            Assert.fail("HostnameVerifier shouldn't allow [" + host + "]");
        } catch (final SSLException e) {
            // whew!  we're okay!
        }
    }

    @Test
    public void testDomainRootMatching() {

        Assert.assertFalse(DefaultHostnameVerifier.matchDomainRoot("a.b.c", null), () -> "Add assertion message");
        Assert.assertTrue(DefaultHostnameVerifier.matchDomainRoot("a.b.c", "a.b.c"), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchDomainRoot("aa.b.c", "a.b.c"), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchDomainRoot("a.b.c", "aa.b.c"), () -> "Add assertion message");
        Assert.assertTrue(DefaultHostnameVerifier.matchDomainRoot("a.a.b.c", "a.b.c"), () -> "Add assertion message");
    }

    @Test
    public void testIdentityMatching() {

        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("a.b.c", "*.b.c"), () -> "Add assertion message");
        Assert.assertTrue(DefaultHostnameVerifier.matchIdentityStrict("a.b.c", "*.b.c"), () -> "Add assertion message");

        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("s.a.b.c", "*.b.c"), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("s.a.b.c", "*.b.c"), () -> "Add assertion message");

        Assert.assertFalse(DefaultHostnameVerifier.matchIdentity("a.gov.uk", "*.gov.uk", publicSuffixMatcher), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("a.gov.uk",
                                                                       "*.gov.uk", publicSuffixMatcher), () -> "Add assertion message");

        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("s.a.gov.uk", "*.a.gov.uk", publicSuffixMatcher), () -> "Add assertion message");
        Assert.assertTrue(DefaultHostnameVerifier.matchIdentityStrict("s.a.gov.uk",
                                                                      "*.a.gov.uk", publicSuffixMatcher), () -> "Add assertion message");

        Assert.assertFalse(DefaultHostnameVerifier.matchIdentity("s.a.gov.uk", "*.gov.uk", publicSuffixMatcher), () -> "Add assertion message");
        // BBad 2TLD/no subdomain allowed
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("s.a.gov.uk",
                                                                       "*.gov.uk",
                                                                       publicSuffixMatcher), () -> "Add assertion message");

        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("a.gov.com", "*.gov.com", publicSuffixMatcher), () -> "Add assertion message");
        Assert.assertTrue(DefaultHostnameVerifier.matchIdentityStrict("a.gov.com",
                                                                      "*.gov.com", publicSuffixMatcher), () -> "Add assertion message");

        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("s.a.gov.com", "*.gov.com", publicSuffixMatcher), () -> "Add assertion message");
        // no subdomain allowed
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("s.a.gov.com",
                                                                       "*.gov.com", publicSuffixMatcher), () -> "Add assertion message");

        Assert.assertFalse(DefaultHostnameVerifier.matchIdentity("a.gov.uk", "a*.gov.uk", publicSuffixMatcher), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("a.gov.uk",
                                                                       "a*.gov.uk", publicSuffixMatcher), () -> "Add assertion message");

        // Bad 2TLD
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentity("s.a.gov.uk", "a*.gov.uk", publicSuffixMatcher), () -> "Add assertion message");
     // Bad 2TLD/no subdomain allowed
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("s.a.gov.uk",
                                                                       "a*.gov.uk", publicSuffixMatcher), () -> "Add assertion message");

        Assert.assertFalse(DefaultHostnameVerifier.matchIdentity("a.b.c", "*.b.*"), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("a.b.c", "*.b.*"), () -> "Add assertion message");

        Assert.assertFalse(DefaultHostnameVerifier.matchIdentity("a.b.c", "*.*.c"), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("a.b.c", "*.*.c"), () -> "Add assertion message");
    }

    @Test
    public void testHttpClient1097() {
        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("a.b.c", "a*.b.c"), () -> "Add assertion message");
        Assert.assertTrue(DefaultHostnameVerifier.matchIdentityStrict("a.b.c", "a*.b.c"), () -> "Add assertion message");

        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("a.a.b.c", "a*.b.c"), () -> "Add assertion message");
        Assert.assertFalse(DefaultHostnameVerifier.matchIdentityStrict("a.a.b.c", "a*.b.c"), () -> "Add assertion message");
    }

    @Test
    public void testHttpClient1255() {
        Assert.assertTrue(DefaultHostnameVerifier.matchIdentity("mail.a.b.c.com", "m*.a.b.c.com"), () -> "Add assertion message");
        Assert.assertTrue(DefaultHostnameVerifier.matchIdentityStrict("mail.a.b.c.com", "m*.a.b.c.com"), () -> "Add assertion message");
    }

    @Test // Check compressed IPv6 hostname matching
    public void testHttpClient1316() throws Exception {
        final String host1 = "2001:0db8:aaaa:bbbb:cccc:0:0:0001";
        DefaultHostnameVerifier.matchIPv6Address(host1,
                                                 Arrays.asList(SubjectName.IP("2001:0db8:aaaa:bbbb:cccc:0:0:0001")));
        DefaultHostnameVerifier.matchIPv6Address(host1,
                                                 Arrays.asList(SubjectName.IP("2001:0db8:aaaa:bbbb:cccc::1")));
        try {
            DefaultHostnameVerifier.matchIPv6Address(host1,
                                                     Arrays.asList(SubjectName.IP("2001:0db8:aaaa:bbbb:cccc::10")));
            Assert.fail("SSLException expected");
        } catch (final SSLException expected) {
        }
        final String host2 = "2001:0db8:aaaa:bbbb:cccc::1";
        DefaultHostnameVerifier.matchIPv6Address(host2,
                                                 Arrays.asList(SubjectName.IP("2001:0db8:aaaa:bbbb:cccc:0:0:0001")));
        DefaultHostnameVerifier.matchIPv6Address(host2,
                                                 Arrays.asList(SubjectName.IP("2001:0db8:aaaa:bbbb:cccc::1")));
        try {
            DefaultHostnameVerifier.matchIPv6Address(host2,
                                                     Arrays.asList(SubjectName.IP("2001:0db8:aaaa:bbbb:cccc::10")));
            // Line removed due to Duplicate Assert: Assert.fail("SSLException expected");
        } catch (final SSLException expected) {
        }
    }

    @Test
    public void testExtractCN() throws Exception {
        Assert.assertEquals("blah", DefaultHostnameVerifier.extractCN("cn=blah, ou=blah, o=blah"), () -> "Add assertion message");
        Assert.assertEquals("blah", DefaultHostnameVerifier.extractCN("cn=blah, cn=yada, cn=booh"), () -> "Add assertion message");
        Assert.assertEquals("blah",
                            DefaultHostnameVerifier.extractCN("c = pampa ,  cn  =    blah    , ou = blah , o = blah"), () -> "Add assertion message");
        Assert.assertEquals("blah", DefaultHostnameVerifier.extractCN("cn=\"blah\", ou=blah, o=blah"), () -> "Add assertion message");
        Assert.assertEquals("blah  blah", DefaultHostnameVerifier.extractCN("cn=\"blah  blah\", ou=blah, o=blah"), () -> "Add assertion message");
        Assert.assertEquals("blah, blah", DefaultHostnameVerifier.extractCN("cn=\"blah, blah\", ou=blah, o=blah"), () -> "Add assertion message");
        Assert.assertEquals("blah, blah", DefaultHostnameVerifier.extractCN("cn=blah\\, blah, ou=blah, o=blah"), () -> "Add assertion message");
        Assert.assertEquals("blah", DefaultHostnameVerifier.extractCN("c = cn=uuh, cn=blah, ou=blah, o=blah"), () -> "Add assertion message");
        try {
            DefaultHostnameVerifier.extractCN("blah,blah");
            Assert.fail("SSLException expected");
        } catch (final SSLException expected) {
        }
        try {
            DefaultHostnameVerifier.extractCN("cn,o=blah");
            // Line removed due to Duplicate Assert: Assert.fail("SSLException expected");
        } catch (final SSLException expected) {
        }
    }

}