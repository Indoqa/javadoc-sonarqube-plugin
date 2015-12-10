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

import java.util.List;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.resources.Project;

public class JQSensor implements Sensor {

    private static final int MINIMUM_ORIGINAL_TOKEN_COUNT = 3;

    private FileSystem fileSystem;

    public JQSensor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void analyse(Project project, SensorContext sensorContext) {
        FilePredicates predicates = this.fileSystem.predicates();
        Iterable<InputFile> inputFiles = this.fileSystem
            .inputFiles(predicates.and(predicates.hasLanguage("java"), predicates.hasType(InputFile.Type.MAIN)));

        JQAnalyzer analyzer = new JQAnalyzer();

        for (InputFile eachInputFile : inputFiles) {
            double totalTokenCount = 0;
            double totalOriginalTokenCount = 0;
            int count = 0;
            int obsoleteCount = 0;

            List<JavadocDescription> javadocDescriptions = analyzer.getJavadocDescriptions(eachInputFile.file(),
                this.fileSystem.encoding());
            for (JavadocDescription eachDescription : javadocDescriptions) {
                totalTokenCount += eachDescription.getJavadocTokenCount();
                totalOriginalTokenCount += eachDescription.getOriginalUniqueJavadocTokenCount();
                count++;

                if (eachDescription.getOriginalUniqueJavadocTokenCount() < MINIMUM_ORIGINAL_TOKEN_COUNT) {
                    obsoleteCount++;
                }
            }

            if (count == 0) {
                sensorContext.saveMeasure(eachInputFile, ORIGINAL_TOKEN_COUNT, Double.valueOf(0));
                sensorContext.saveMeasure(eachInputFile, TOKEN_COUNT, Double.valueOf(0));
                sensorContext.saveMeasure(eachInputFile, COUNT, Double.valueOf(0));
                sensorContext.saveMeasure(eachInputFile, OBSOLETE_COUNT, Double.valueOf(0));
            } else {
                sensorContext.saveMeasure(eachInputFile, ORIGINAL_TOKEN_COUNT, totalOriginalTokenCount);
                sensorContext.saveMeasure(eachInputFile, TOKEN_COUNT, totalTokenCount);
                sensorContext.saveMeasure(eachInputFile, COUNT, Double.valueOf(count));
                sensorContext.saveMeasure(eachInputFile, OBSOLETE_COUNT, Double.valueOf(obsoleteCount));
            }
        }
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }

    @Override
    public String toString() {
        return "Javadoc Quality Sensor";
    }
}
