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
package org.eclipse.kapua.commons.util.log;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


@Category(JUnitTests.class)
public class ConfigurationPrinterTest {

    ConfigurationPrinter configurationPrinter;
    Logger mockedLogger;
    Logger logger;

    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: configurationPrinter = new ConfigurationPrinter();
        // Removed line due to General Fixture smell: mockedLogger = Mockito.mock(Logger.class);
        // Removed line due to General Fixture smell: logger = LoggerFactory.getLogger(ConfigurationPrinter.class);
    }

    @Test
    public void withParentLoggerTest() {
        configurationPrinter = new ConfigurationPrinter();
        logger = LoggerFactory.getLogger(ConfigurationPrinter.class);

        Assert.assertNull("Null expected!", configurationPrinter.getParentLogger());
        configurationPrinter.withLogger(logger);
        Assert.assertEquals("Expected and actual values should be the same.", logger, configurationPrinter.getParentLogger());
        configurationPrinter.withLogger(null);
        // Line removed due to Duplicate Assert: Assert.assertNull("Null expected!", configurationPrinter.getParentLogger());
    }

    @Test
    public void withLogLevelTest() {
        configurationPrinter = new ConfigurationPrinter();
        ConfigurationPrinter.LogLevel[] logLevels = new ConfigurationPrinter.LogLevel[]{
                ConfigurationPrinter.LogLevel.DEBUG,
                ConfigurationPrinter.LogLevel.ERROR,
                ConfigurationPrinter.LogLevel.INFO,
                ConfigurationPrinter.LogLevel.TRACE,
                ConfigurationPrinter.LogLevel.WARN
        };
        for (ConfigurationPrinter.LogLevel logLevel : logLevels) {
            configurationPrinter.withLogLevel(logLevel);
            Assert.assertEquals("Expected and actual values should be the same.", logLevel,
                    configurationPrinter.getLogLevel());
        }
    }

    @Test
    public void withTitleTest() {
        mockedLogger = Mockito.mock(Logger.class);
        String[] specialSymbols = new String[]{"!", "\"", "#", "$", "%", "&", "'", "(", ")", "=", "/", "?", "+", "*", "<", ">", ",", ";",
                ".", ":", "-", "_", "⁄", "@", "‹", "›", "€", "–", "°", "·", "", "Œ", "„", "‰", "”", "’", "Ø", "∏", "{", "}", "Æ", "æ", "Ò", "", "Å",
                "Í", "Î", "~", "«", "◊", "Ñ", "¯", "È", "ˇ", " "};
        String[] title = new String[]{"", "a", "qwertyuiiopasdfghjklšđčćžzxcvbnm!", "1234567890", "QWERTYUIOPŠĐASDFGHJKLČĆŽZXCVBNM"};
        for (String titleVal : title) {
            for (String specSymVal : specialSymbols) {
                ConfigurationPrinter
                        .create()
                        .withLogger(mockedLogger)
                        .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                        .withTitle(titleVal + specSymVal)
                        .printLog();
                Mockito.verify(mockedLogger, Mockito.times(1)).info("=================== {} ===================", titleVal + specSymVal);
            }
        }
    }

    @Test
    public void withTitleAlignmentTest() {
        configurationPrinter = new ConfigurationPrinter();
        ConfigurationPrinter.TitleAlignment[] titleAlignments = new ConfigurationPrinter.TitleAlignment[]{
                ConfigurationPrinter.TitleAlignment.CENTER,
                ConfigurationPrinter.TitleAlignment.LEFT,
                ConfigurationPrinter.TitleAlignment.RIGHT
        };

        for (ConfigurationPrinter.TitleAlignment titleAlignment : titleAlignments) {
            configurationPrinter.withTitleAlignment(titleAlignment);
            Assert.assertEquals("Expected and actual values should be the same.", titleAlignment, configurationPrinter.getTitleAlignment());
        }
    }

    @Test
    public void withTitleAlignmentNullTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).info("=================== {} ===================", "Title");
    }

    @Test
    public void withTitleAlignmentCenterTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                .withTitleAlignment(ConfigurationPrinter.TitleAlignment.CENTER)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).info("=================== {} ===================", "Title");
    }

    @Test
    public void withTitleAlignmentRightTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                .withTitleAlignment(ConfigurationPrinter.TitleAlignment.RIGHT)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).info("===================================== {} =", "Title");
    }

    @Test
    public void withTitleAlignmentLeftTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                .withTitleAlignment(ConfigurationPrinter.TitleAlignment.LEFT)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).info("= {} =====================================", "Title");
    }

    @Test
    public void getConfigurationsNullTest() {
        configurationPrinter = new ConfigurationPrinter();
        Assert.assertEquals("Expected and actual values are not the same!", new ArrayList<>(), configurationPrinter.getConfigurations());
    }

    @Test
    public void addHeaderNullTests() {
        configurationPrinter = new ConfigurationPrinter();
        Assert.assertNotNull("Not null expected!", configurationPrinter.addHeader(null));
    }

    @Test
    public void addHeaderTest() {
        configurationPrinter = new ConfigurationPrinter();
        String[] specialSymbols = new String[]{"!", "\"", "#", "$", "%", "&", "'", "(", ")", "=", "/", "?", "+", "*", "<", ">", ",", ";",
                ".", ":", "-", "_", "⁄", "@", "‹", "›", "€", "€", "–", "°", "·", "", "Œ", "„", "‰", "”", "’", "Ø", "∏", "{", "}", "Æ", "æ", "Ò", "", "Å",
                "Í", "Î", "~", "«", "◊", "Ñ", "¯", "È", "ˇ", " "};
        String[] title = new String[]{"", "a", "qwertyuiiopasdfghjklšđčćžzxcvbnm!", "1234567890", "QWERTYUIOPŠĐASDFGHJKLČĆŽZXCVBNM"};
        for (String titleVal : title) {
            for (String specSymVal : specialSymbols) {
                Assert.assertNotNull("Not null expected!", configurationPrinter.addHeader(titleVal + specSymVal));
            }
        }
    }

    @Test
    public void addParameterNullTest() {
        configurationPrinter = new ConfigurationPrinter();
        Object object = new Object();
        Assert.assertNotNull("Not null expected!", configurationPrinter.addParameter(null, null));
        Assert.assertNotNull("Not null expected!", configurationPrinter.addParameter("string", null));
        Assert.assertNotNull("Not null expected!", configurationPrinter.addParameter(null, object));
    }

    @Test
    public void addParameterTest() {
        configurationPrinter = new ConfigurationPrinter();
        Object[] objects = new Object[]{"[string]", 1, 1.1234, 1123456789L, 1.123456D, 123912839F, true, false, 'a', (byte) 100, (short) 10,};
        String[] stringArray = new String[]{"", "a", "abc", "ABC", "123",
                "qwertyuiopšđasdfghjklčćžzxcvbnmQWERTYUIOPŠĐASDFGHJKLČĆŽZXCVBNM123457890"};
        for (String stringArrayVal : stringArray) {
            for (Object object : objects) {
                Assert.assertNotNull("Not null expected!", configurationPrinter.addParameter(stringArrayVal, object));
            }
        }
    }

    @Test
    public void increaseIndentationTest() {
        configurationPrinter = new ConfigurationPrinter();
        for (int i = 0; i < 10; i++) {
            Assert.assertNotNull("Not null expected!", configurationPrinter.increaseIndentation());
        }
    }

    @Test
    public void decreaseIndentationTest() {
        configurationPrinter = new ConfigurationPrinter();
        for (int i = 0; i < 10; i++) {
            configurationPrinter.increaseIndentation();
        }
        for (int i = 10; i >= 0; i--) {
            Assert.assertNotNull("Not null expected!", configurationPrinter.decreaseIndentation());
        }
    }

    @Test
    public void printLogParentLoggerNullTest() {
        configurationPrinter = new ConfigurationPrinter();
        configurationPrinter.printLog();
        Assert.assertNotNull("Not null expected!", configurationPrinter.getParentLogger());
        // Line removed due to Duplicate Assert: Assert.assertNotNull("Not null expected!", configurationPrinter.getParentLogger());
    }

    @Test
    public void printLogTest() {
        configurationPrinter = new ConfigurationPrinter();
        logger = LoggerFactory.getLogger(ConfigurationPrinter.class);
        Logger logger = LoggerFactory.getLogger(ConfigurationPrinter.class);
        Object[] objects = new Object[]{"[string]", 1, 1.1234, 1123456789L, 1.123456D, 123912839F, true, false, 'a', (byte) 100, (short) 10,};
        configurationPrinter.withLogger(logger);
        configurationPrinter.withTitle("Title");
        configurationPrinter.addHeader("Header");
        ConfigurationPrinter.LogLevel[] enumArray = new ConfigurationPrinter.LogLevel[]{
                ConfigurationPrinter.LogLevel.DEBUG,
                ConfigurationPrinter.LogLevel.ERROR,
                ConfigurationPrinter.LogLevel.INFO,
                ConfigurationPrinter.LogLevel.TRACE,
                ConfigurationPrinter.LogLevel.WARN};
        for (Object object : objects) {
            for (ConfigurationPrinter.LogLevel enumArrayVal : enumArray) {
                configurationPrinter.withLogLevel(enumArrayVal);
                configurationPrinter.withTitleAlignment(ConfigurationPrinter.TitleAlignment.CENTER);
                configurationPrinter.addParameter("parameter", object);
                configurationPrinter.printLog();
            }
        }
        for (Object object : objects) {
            for (ConfigurationPrinter.LogLevel enumArrayVal : enumArray) {
                configurationPrinter.withLogLevel(enumArrayVal);
                configurationPrinter.withTitleAlignment(ConfigurationPrinter.TitleAlignment.LEFT);
                configurationPrinter.addParameter("parameter", object);
                configurationPrinter.printLog();
            }
        }
        for (Object object : objects) {
            for (ConfigurationPrinter.LogLevel enumArrayVal : enumArray) {
                configurationPrinter.withLogLevel(enumArrayVal);
                configurationPrinter.withTitleAlignment(ConfigurationPrinter.TitleAlignment.RIGHT);
                configurationPrinter.addParameter("parameter", object);
                configurationPrinter.printLog();
            }
        }
    }

    @Test
    public void printLogLogLevelNullTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).info("=================== {} ===================", "Title");
    }

    @Test
    public void printLogLogLevelDebugTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.DEBUG)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).debug("=================== {} ===================", "Title");
    }

    @Test
    public void printLogLogLevelErrorTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.ERROR)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).error("=================== {} ===================", "Title");
    }

    @Test
    public void printLogLogLevelInfoTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).info("=================== {} ===================", "Title");
    }

    @Test
    public void printLogLogLevelTraceTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.TRACE)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).trace("=================== {} ===================", "Title");
    }

    @Test
    public void printLogLogLevelWarnTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.WARN)
                .withTitle("Title")
                .printLog();
        Mockito.verify(mockedLogger).warn("=================== {} ===================", "Title");
    }

    @Test
    public void printLogTitleNullTest() {
        mockedLogger = Mockito.mock(Logger.class);
        ConfigurationPrinter
                .create()
                .withLogger(mockedLogger)
                .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                .printLog();
        Mockito.verify(mockedLogger).info("=================== {} ===================", "Info");
    }

    @Test
    public void createTest() {
        Assert.assertThat("Instance of ConfigurationPrinter expected.", ConfigurationPrinter.create(), IsInstanceOf.instanceOf(ConfigurationPrinter.class));
    }

    @Test
    public void logLevelEnumTest() {
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.LogLevel.DEBUG, ConfigurationPrinter.LogLevel.valueOf("DEBUG"));
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.LogLevel.ERROR, ConfigurationPrinter.LogLevel.valueOf("ERROR"));
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.LogLevel.INFO, ConfigurationPrinter.LogLevel.valueOf("INFO"));
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.LogLevel.TRACE, ConfigurationPrinter.LogLevel.valueOf("TRACE"));
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.LogLevel.WARN, ConfigurationPrinter.LogLevel.valueOf("WARN"));
    }

    @Test
    public void titleAlignmentEnumTest() {
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.TitleAlignment.LEFT, ConfigurationPrinter.TitleAlignment.valueOf("LEFT"));
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.TitleAlignment.RIGHT, ConfigurationPrinter.TitleAlignment.valueOf("RIGHT"));
        Assert.assertEquals("Expected and actual values should be the same!", ConfigurationPrinter.TitleAlignment.CENTER, ConfigurationPrinter.TitleAlignment.valueOf("CENTER"));
    }
}