/*
 * Copyright 2016 WSO2 Inc. (http://wso2.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.metrics.core;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.metrics.core.config.model.ConsoleReporterConfig;
import org.wso2.carbon.metrics.core.config.model.CsvReporterConfig;
import org.wso2.carbon.metrics.core.config.model.JmxReporterConfig;
import org.wso2.carbon.metrics.core.config.model.MetricsConfig;
import org.wso2.carbon.metrics.core.config.model.ReservoirConfig;
import org.wso2.carbon.metrics.core.config.model.ReservoirParametersConfig;
import org.wso2.carbon.metrics.core.config.model.Slf4jReporterConfig;
import org.wso2.carbon.metrics.core.impl.reservoir.ReservoirType;

import java.util.concurrent.TimeUnit;

/**
 * Test Cases for {@link MetricsConfig}.
 */
public class MetricsConfigTest extends BaseMetricTest {

    static MetricsConfig metricsConfig;

    @BeforeClass
    private void load() throws ConfigurationException {
        metricsConfig = TestUtils.getConfigProvider("metrics-reporter.yaml")
                .getConfigurationObject(MetricsConfig.class);
    }

    @Test
    public void testConfigLoad() {
        Assert.assertEquals(metricsConfig.isEnabled(), true);
    }

    @Test
    public void testJmxConfig() {
        Assert.assertEquals(metricsConfig.getJmx().getName(), "org.wso2.carbon:type=MetricsTest");
        Assert.assertTrue(metricsConfig.getJmx().isRegisterMBean());
    }

    @Test
    public void testJmxReporterConfigLoad() {
        JmxReporterConfig config = metricsConfig.getReporting().getJmx().iterator().next();
        Assert.assertEquals(config.getName(), "JMX");
        Assert.assertEquals(config.isEnabled(), true);
        Assert.assertEquals(config.getDomain(), "org.wso2.carbon.metrics.test");
        Assert.assertEquals(config.isUseRegexFilters(), false);
    }

    @Test
    public void testConsoleReporterConfigLoad() {
        ConsoleReporterConfig config = metricsConfig.getReporting().getConsole().iterator().next();
        Assert.assertEquals(config.getName(), "Console");
        Assert.assertEquals(config.isEnabled(), true);
        Assert.assertEquals(config.getPollingPeriod(), 600L);
    }

    @Test
    public void testCsvReporterConfigLoad() {
        CsvReporterConfig config = metricsConfig.getReporting().getCsv().iterator().next();
        Assert.assertEquals(config.getName(), "CSV");
        Assert.assertEquals(config.isEnabled(), true);
        Assert.assertEquals(config.getPollingPeriod(), 600L);
        Assert.assertEquals(config.getLocation(), "target/metrics");
    }

    @Test
    public void testSlf4jReporterConfigLoad() {
        Slf4jReporterConfig config = metricsConfig.getReporting().getSlf4j().iterator().next();
        Assert.assertEquals(config.getName(), "SLF4J");
        Assert.assertEquals(config.isEnabled(), true);
        Assert.assertEquals(config.getPollingPeriod(), 600L);
        Assert.assertEquals(config.getLoggerName(), "metrics.test");
        Assert.assertEquals(config.getMarkerName(), "metrics");
    }

    @Test
    public void testReservoirConfigLoad() {
        ReservoirConfig config = metricsConfig.getReservoir();
        Assert.assertEquals(config.getType(), ReservoirType.UNIFORM);

        ReservoirParametersConfig parametersConfig = config.getParameters();
        Assert.assertEquals(parametersConfig.getSize(), 2048);
        Assert.assertEquals(parametersConfig.getWindow(), 30);
        Assert.assertEquals(parametersConfig.getWindowUnit(), TimeUnit.MINUTES);
        Assert.assertEquals(parametersConfig.getNumberOfSignificantValueDigits(), 3);
        Assert.assertTrue(parametersConfig.isResetOnSnapshot());
    }

    @Test
    public void testReporterCount() {
        Assert.assertEquals(metricsConfig.getReporting().getReporterBuilders().size(), 4);
    }

}
