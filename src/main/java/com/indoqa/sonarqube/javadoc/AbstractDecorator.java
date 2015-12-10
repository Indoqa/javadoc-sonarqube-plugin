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

import java.util.List;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;

public abstract class AbstractDecorator implements Decorator {

    @Override
    public void decorate(Resource resource, DecoratorContext context) {
        if (!this.shouldSaveMeasure(resource)) {
            return;
        }

        if (!this.hasCode(context)) {
            return;
        }

        if (this.isFile(resource)) {
            this.decorateFile(resource, context);
        } else if (this.isDir(resource)) {
            this.decorateDir(resource, context);
        } else if (this.isProj(resource)) {
            this.decorateProject(resource, context);
        }
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }

    protected abstract void decorateDir(Resource resource, DecoratorContext context);

    protected abstract void decorateFile(Resource resource, DecoratorContext context);

    protected void decorateProject(Resource resource, DecoratorContext context) {
        this.decorateDir(resource, context);
    }

    protected double getSumOfChildren(DecoratorContext context, Metric<?> metric) {
        List<DecoratorContext> children = context.getChildren();
        if (children == null || children.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (DecoratorContext eachChild : children) {
            Measure<?> measure = eachChild.getMeasure(metric);
            if (measure != null && measure.getValue() != null) {
                sum += measure.getValue();
            }
        }

        return sum;
    }

    protected double getValue(DecoratorContext context, Metric<?> metric) {
        Measure<?> measure = context.getMeasure(metric);
        if (measure == null || measure.getValue() == null) {
            return 0d;
        }

        return measure.getValue().doubleValue();
    }

    protected boolean hasCode(DecoratorContext context) {
        Measure<?> nclocMeasure = context.getMeasure(CoreMetrics.NCLOC);
        if (nclocMeasure == null) {
            return false;
        }

        if (nclocMeasure.getValue() == null) {
            return false;
        }

        return nclocMeasure.getValue().intValue() > 0;
    }

    protected boolean isDir(Resource resource) {
        return Scopes.isDirectory(resource);
    }

    protected boolean isFile(Resource resource) {
        return Scopes.isFile(resource);
    }

    protected boolean isProj(Resource resource) {
        return Scopes.isProject(resource);
    }

    protected boolean shouldSaveMeasure(final Resource resource) {
        return !Qualifiers.UNIT_TEST_FILE.equals(resource.getQualifier());
    }
}
