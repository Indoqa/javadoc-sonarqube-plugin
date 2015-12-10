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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class JavadocDescription {

    private int line;
    private String signature;

    private String javadoc;
    private int javadocTokenCount;

    private Set<String> uniqueSignatureTokens = new TreeSet<>();
    private Set<String> uniqueJavadocTokens = new TreeSet<>();
    private int originalUniqueJavadocTokenCount;

    public String getJavadoc() {
        return this.javadoc;
    }

    public int getJavadocTokenCount() {
        return this.javadocTokenCount;
    }

    public int getLine() {
        return this.line;
    }

    public int getOriginalUniqueJavadocTokenCount() {
        return this.originalUniqueJavadocTokenCount;
    }

    public String getSignature() {
        return this.signature;
    }

    public Set<String> getUniqueJavadocTokens() {
        return this.uniqueJavadocTokens;
    }

    public Set<String> getUniqueSignatureTokens() {
        return this.uniqueSignatureTokens;
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

    public void setJavadocTokenCount(int javadocTokenCount) {
        this.javadocTokenCount = javadocTokenCount;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setUniqueJavadocTokens(Set<String> uniqueJavadocTokens) {
        this.uniqueJavadocTokens = uniqueJavadocTokens;

        this.updateOriginalUniqueJavadocTokens();
    }

    public void setUniqueSignatureTokens(Set<String> uniqueSignatureTokens) {
        this.uniqueSignatureTokens = uniqueSignatureTokens;

        this.updateOriginalUniqueJavadocTokens();
    }

    private void updateOriginalUniqueJavadocTokens() {
        HashSet<String> originalUniqueJavadocTokens = new HashSet<>();

        if (this.uniqueSignatureTokens != null) {
            originalUniqueJavadocTokens.addAll(this.uniqueJavadocTokens);
        }

        if (this.uniqueSignatureTokens != null) {
            originalUniqueJavadocTokens.removeAll(this.uniqueSignatureTokens);
        }

        this.originalUniqueJavadocTokenCount = originalUniqueJavadocTokens.size();
    }
}
