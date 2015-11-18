/*
 * Copyright 2014 WSO2 Inc. (http://wso2.org)
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
package org.wso2.carbon.metrics.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.wso2.carbon.metrics.common.MetricsConfiguration;
import org.wso2.carbon.metrics.impl.internal.MetricServiceValueHolder;
import org.wso2.carbon.metrics.manager.Level;
import org.wso2.carbon.metrics.manager.MetricManager;
import org.wso2.carbon.metrics.manager.MetricService;
import org.wso2.carbon.metrics.manager.Timer;
import org.wso2.carbon.metrics.manager.Timer.Context;
import org.wso2.carbon.metrics.manager.internal.ServiceReferenceHolder;

import junit.framework.TestCase;

/**
 * Test Cases for {@link Timer}
 */
public class TimerTest extends TestCase {

    private MetricService metricService;

    protected void setUp() throws Exception {
        super.setUp();
        MetricsConfiguration configuration = Utils.getConfiguration();
        MetricsLevelConfiguration levelConfiguration = Utils.getLevelConfiguration();
        metricService = new MetricServiceImpl.Builder().configure(configuration).build(levelConfiguration);
        ServiceReferenceHolder.getInstance().setMetricService(metricService);
        MetricServiceValueHolder.registerMetricServiceInstance(metricService);
    }

    public void testInitialCount() {
        Timer timer = MetricManager.timer(MetricManager.name(this.getClass(), "test-initial-count"), Level.INFO);
        assertEquals("Initial count should be zero", 0, timer.getCount());
    }

    public void testParentCount() {
        Timer main = MetricManager.timer("org.wso2.main.test-timer", Level.INFO);
        Timer sub = MetricManager.timer("org.wso2.main[+].sub.test-timer", Level.INFO, Level.INFO);
        sub.update(1, TimeUnit.SECONDS);
        main.update(1, TimeUnit.SECONDS);
        assertEquals("Count should be one", 1, sub.getCount());
        assertEquals("Count should be two", 2, main.getCount());
    }

    public void testSameMetric() {
        Timer timer = MetricManager.timer(MetricManager.name(this.getClass(), "test-same-timer"), Level.INFO);
        timer.update(1, TimeUnit.SECONDS);
        assertEquals("Timer count should be one", 1, timer.getCount());

        Timer timer2 = MetricManager.timer(MetricManager.name(this.getClass(), "test-same-timer"), Level.INFO);
        assertEquals("Timer count should be one", 1, timer2.getCount());
    }

    public void testSameMetricWithParent() {
        Timer main = MetricManager.timer("org.wso2.main.test-timer", Level.INFO);
        Timer sub = MetricManager.timer("org.wso2.main[+].sub.test-timer", Level.INFO, Level.INFO);

        Timer main2 = MetricManager.timer("org.wso2.main.test-timer", Level.INFO);
        Timer sub2 = MetricManager.timer("org.wso2.main[+].sub.test-timer", Level.INFO, Level.INFO);

        sub.update(1, TimeUnit.SECONDS);
        assertEquals("Count should be one", 1, sub.getCount());
        assertEquals("Count should be one", 1, sub2.getCount());
        assertEquals("Count should be one", 1, main.getCount());
        assertEquals("Count should be one", 1, main2.getCount());

        main.update(5, TimeUnit.SECONDS);
        assertEquals("Count should be one", 1, sub.getCount());
        assertEquals("Count should be one", 1, sub2.getCount());
        assertEquals("Count should be six", 2, main.getCount());
        assertEquals("Count should be six", 2, main2.getCount());
    }

    public void testMetricWithNonExistingParents() {
        Timer sub2 = MetricManager.timer("org.wso2.main[+].sub1[+].sub2.test-timer", Level.INFO, Level.INFO, Level.INFO);
        Timer sub1 = MetricManager.timer("org.wso2.main[+].sub1.test-timer", Level.INFO, Level.INFO);
        Timer main = MetricManager.timer("org.wso2.main.test-timer", Level.INFO);

        sub2.update(5, TimeUnit.SECONDS);
        assertEquals("Count should be one", 1, sub2.getCount());
        assertEquals("Count should be one", 1, sub1.getCount());
        assertEquals("Count should be one", 1, main.getCount());

        sub1.update(5, TimeUnit.SECONDS);
        assertEquals("Count should be one", 1, sub2.getCount());
        assertEquals("Count should be two", 2, sub1.getCount());
        assertEquals("Count should be two", 2, main.getCount());

        main.update(5, TimeUnit.SECONDS);
        assertEquals("Count should be one", 1, sub2.getCount());
        assertEquals("Count should be two", 2, sub1.getCount());
        assertEquals("Count should be three", 3, main.getCount());
    }

    public void testTime() {
        Timer timer = MetricManager.timer(MetricManager.name(this.getClass(), "test-timer-start"), Level.INFO);
        Context context = timer.start();
        assertTrue("Timer works!", context.stop() > 0);
        assertEquals("Timer count should be one", 1, timer.getCount());
        context.close();

        metricService.setRootLevel(Level.OFF);
        context = timer.start();
        assertEquals("Timer should not work", 0, context.stop());
        context.close();
    }

    public void testTimerUpdateCount() {
        Timer timer = MetricManager.timer(MetricManager.name(this.getClass(), "test-timer-update"), Level.INFO);
        timer.update(1, TimeUnit.SECONDS);
        assertEquals("Timer count should be one", 1, timer.getCount());

        metricService.setRootLevel(Level.OFF);
        timer.update(1, TimeUnit.SECONDS);
        assertEquals("Timer count should be one", 1, timer.getCount());
    }

    public void testTimerCallableInstances() throws Exception {
        Timer timer = MetricManager.timer(MetricManager.name(this.getClass(), "test-timer-callable"), Level.INFO);
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "test";
            }

        };
        String value = timer.time(callable);
        assertEquals("Value should be 'test'", "test", value);

        metricService.setRootLevel(Level.OFF);
        value = timer.time(callable);
        assertNull("Value should be null", value);
    }

}
