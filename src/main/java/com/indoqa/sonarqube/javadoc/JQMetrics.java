/*
 * Licensed to the Indoqa Software Design und Beratung GmbH (Indoqa) under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Indoqa licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indoqa.sonarqube.javadoc;

import static org.sonar.api.measures.CoreMetrics.DOMAIN_DOCUMENTATION;
import static org.sonar.api.measures.Metric.*;
import static org.sonar.api.measures.Metric.ValueType.*;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.Builder;
import org.sonar.api.measures.Metrics;

public class JQMetrics implements Metrics {

    private static final String COUNT_KEY = "javadoc-count";
    private static final String TOKEN_COUNT_KEY = "javadoc-token-count";
    private static final String AVERAGE_TOKEN_COUNT_KEY = "avg-javadoc-token-count";

    private static final String ORIGINAL_TOKEN_COUNT_KEY = "org-javadoc-token-count";
    private static final String AVERAGE_ORIGINAL_TOKEN_COUNT_KEY = "avg-org-javadoc-token-count";

    private static final String OBSOLETE_COUNT_KEY = "obsolete-javadoc-count";

    public static final Metric<Integer> COUNT = new Builder(COUNT_KEY, "Javadoc Count", INT)
        .setDescription("The number of Javadoc comments for methods.")
        .setQualitative(true)
        .setDirection(DIRECTION_BETTER)
        .setDomain(DOMAIN_DOCUMENTATION)
        .create();

    public static final Metric<Integer> TOKEN_COUNT = new Builder(TOKEN_COUNT_KEY, "Javadoc Token Count", INT)
        .setDescription("The number of tokens in the Javadoc comments for methods.")
        .setQualitative(true)
        .setDirection(DIRECTION_BETTER)
        .setDomain(DOMAIN_DOCUMENTATION)
        .create();

    public static final Metric<Float> AVERAGE_TOKEN_COUNT = new Builder(AVERAGE_TOKEN_COUNT_KEY, "Average Javadoc Token Count", FLOAT)
        .setDescription("The average number of tokens in the Javadoc comments for methods.")
        .setQualitative(true)
        .setDirection(DIRECTION_BETTER)
        .setDomain(DOMAIN_DOCUMENTATION)
        .create();

    public static final Metric<Integer> ORIGINAL_TOKEN_COUNT = new Builder(ORIGINAL_TOKEN_COUNT_KEY,
        "Original Javadoc Token Count", INT)
            .setDescription("The number of unique original tokens in the Javadoc comments for methods.")
            .setQualitative(true)
            .setDirection(DIRECTION_BETTER)
            .setDomain(DOMAIN_DOCUMENTATION)
            .create();

    public static final Metric<Float> AVERAGE_ORIGINAL_TOKEN_COUNT = new Builder(AVERAGE_ORIGINAL_TOKEN_COUNT_KEY,
        "Average Original Javadoc Token Count", FLOAT)
            .setDescription("The average number of unique original tokens in the Javadoc comments for methods.")
            .setQualitative(true)
            .setDirection(DIRECTION_BETTER)
            .setDomain(DOMAIN_DOCUMENTATION)
            .create();

    public static final Metric<Integer> OBSOLETE_COUNT = new Builder(OBSOLETE_COUNT_KEY, "Obsolete Javadoc Count", INT)
        .setDescription("The number of Javadoc comments containing less than 3 unique original tokens.")
        .setQualitative(true)
        .setDirection(DIRECTION_WORST)
        .setDomain(DOMAIN_DOCUMENTATION)
        .create();

    @Override
    @SuppressWarnings("rawtypes")
    public List<Metric> getMetrics() {
        ArrayList<Metric> result = new ArrayList<>();

        result.add(COUNT);
        result.add(TOKEN_COUNT);
        result.add(AVERAGE_TOKEN_COUNT);
        result.add(ORIGINAL_TOKEN_COUNT);
        result.add(AVERAGE_ORIGINAL_TOKEN_COUNT);
        result.add(OBSOLETE_COUNT);

        return result;
    }
}
