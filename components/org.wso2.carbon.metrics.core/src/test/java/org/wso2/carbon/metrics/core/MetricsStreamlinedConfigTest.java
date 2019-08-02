/*
 * Copyright 2019 WSO2 Inc. (http://wso2.org)
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

import org.testng.annotations.BeforeClass;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.metrics.core.config.model.MetricsConfig;

/**
 * Test Cases for {@link MetricsConfig}.
 */
public class MetricsStreamlinedConfigTest extends MetricsConfigTest {

    @BeforeClass
    private void load() throws ConfigurationException {
        metricsConfig = TestUtils.getConfigProvider("metrics-reporter2.yaml")
                .getConfigurationObject(MetricsConfig.class);
    }
}
