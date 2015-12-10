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

import static com.indoqa.sonarqube.javadoc.JQMetrics.*;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;

public class JQDecorator extends AbstractDecorator {

    @DependsUpon
    public Metric<?> dependsUponMetrics() {
        return CoreMetrics.NCLOC;
    }

    @Override
    public String toString() {
        return "Javadoc Quality Decorator";
    }

    @Override
    protected void decorateDir(Resource resource, DecoratorContext context) {
        double count = this.getSumOfChildren(context, COUNT);
        double tokenCount = this.getSumOfChildren(context, TOKEN_COUNT);
        double originalTokenCount = this.getSumOfChildren(context, ORIGINAL_TOKEN_COUNT);
        double obsoleteCount = this.getSumOfChildren(context, OBSOLETE_COUNT);

        context.saveMeasure(TOKEN_COUNT, tokenCount);
        context.saveMeasure(ORIGINAL_TOKEN_COUNT, originalTokenCount);
        context.saveMeasure(COUNT, count);
        context.saveMeasure(OBSOLETE_COUNT, obsoleteCount);

        if (count == 0) {
            context.saveMeasure(AVERAGE_TOKEN_COUNT, 0d);
            context.saveMeasure(AVERAGE_ORIGINAL_TOKEN_COUNT, 0d);
        } else {
            context.saveMeasure(AVERAGE_TOKEN_COUNT, tokenCount / count);
            context.saveMeasure(AVERAGE_ORIGINAL_TOKEN_COUNT, originalTokenCount / count);
        }
    }

    @Override
    protected void decorateFile(Resource resource, DecoratorContext context) {
        double tokenCount = this.getValue(context, TOKEN_COUNT);
        double originalTokenCount = this.getValue(context, ORIGINAL_TOKEN_COUNT);
        double count = this.getValue(context, COUNT);

        if (count == 0) {
            context.saveMeasure(AVERAGE_TOKEN_COUNT, 0d);
            context.saveMeasure(AVERAGE_ORIGINAL_TOKEN_COUNT, 0d);
        } else {
            context.saveMeasure(AVERAGE_TOKEN_COUNT, tokenCount / count);
            context.saveMeasure(AVERAGE_ORIGINAL_TOKEN_COUNT, originalTokenCount / count);
        }
    }
}
