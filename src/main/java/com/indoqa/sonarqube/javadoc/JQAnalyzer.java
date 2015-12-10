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

import static java.util.regex.Pattern.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JQAnalyzer {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("[a-z][A-Z0-9]");
    private static final String JAVADOC_PATTERN = "\\Q/**\\E.*?\\Q*/\\E";
    private static final String ANNOTATION_PATTERN = "@[^@\\s]+(\\s*\\(.*?\\))?\\s*";
    private static final String METHOD_PATTERN = "[^;]*?";

    private static final Pattern JAVADOC_AND_METHOD_PATTERN = Pattern.compile(
        "(" + JAVADOC_PATTERN + ")\\s*(" + ANNOTATION_PATTERN + ")*\\s*(" + METHOD_PATTERN + ")\\s*(\\{|;)", MULTILINE | DOTALL);

    private static final char METHOD_PARAMETER_START = '(';
    private static final char LINE_SEPARATOR = '\n';

    private static final String[] JAVADOC_KEYWORDS = {"@param", "@author", "@return", "@link", "@throws", "@see", "@deprecated", "*/",
        "/**"};
    private static final String[] STOPWORDS = {"a", "an", "the", "from", "to", "as", "are", "in", "is", "return", "returns", "set",
        "get"};

    private static int countOccurrences(String value, int limit, char character) {
        int result = 0;

        for (int i = 0; i < limit; i++) {
            if (value.charAt(i) == character) {
                result++;
            }
        }

        return result;
    }

    private static JavadocDescription createMethodDescription(String javaDoc, String signature, int line) {
        JavadocDescription result = new JavadocDescription();

        result.setJavadoc(javaDoc);
        result.setLine(line);

        result.setSignature(signature);
        result.setUniqueSignatureTokens(getUniqueTokens(getTokens(signature)));

        List<String> javadocTokens = getJavadocTokens(javaDoc);
        result.setJavadocTokenCount(javadocTokens.size());
        result.setUniqueJavadocTokens(removeAll(getUniqueTokens(javadocTokens), STOPWORDS));

        return result;
    }

    private static String getJavadocContent(String javadoc) {
        Pattern pattern = Pattern.compile("^\\s*(/\\*\\*|\\*/|\\*)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(javadoc);
        return matcher.replaceAll("");
    }

    private static List<String> getJavadocTokens(String javaDoc) {
        return removeAll(getTokens(getJavadocContent(javaDoc)), JAVADOC_KEYWORDS);
    }

    private static String getSource(File file, Charset charset) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            List<String> lines = Files.readAllLines(file.toPath(), charset);
            for (String eachLine : lines) {
                stringBuilder.append(eachLine);
                stringBuilder.append(LINE_SEPARATOR);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file '" + file.getAbsolutePath() + "': " + e.getMessage());
        }

        return stringBuilder.toString();
    }

    private static List<String> getTokens(String input) {
        List<String> result = new ArrayList<>();

        String[] values = input.split("[^a-zA-Z0-9\\*\\+\\-\\/\\@]+");
        for (String eachValue : values) {
            if (eachValue.trim().length() == 0) {
                continue;
            }

            result.add(eachValue);
        }

        return result;
    }

    private static Set<String> getUniqueTokens(Collection<String> tokens) {
        Set<String> result = new TreeSet<>(tokens);

        result = splitCamelCase(result);
        result = toLowerCase(result);

        return result;
    }

    private static <T extends Collection<String>> T removeAll(T values, String[] valuesToBeRemoved) {
        for (String eachValueToBeRemoved : valuesToBeRemoved) {
            while (values.remove(eachValueToBeRemoved)) {
                // do nothing
            }
        }

        return values;
    }

    private static Set<String> splitCamelCase(Set<String> values) {
        Set<String> result = new HashSet<>();

        for (Iterator<String> iterator = values.iterator(); iterator.hasNext();) {
            String eachValue = iterator.next();
            int lastStart = 0;

            Matcher matcher = CAMEL_CASE_PATTERN.matcher(eachValue);
            while (matcher.find()) {
                result.add(eachValue.substring(lastStart, matcher.start() + 1).toLowerCase());
                lastStart = matcher.start() + 1;
            }
            result.add(eachValue.substring(lastStart).toLowerCase());
        }

        return result;
    }

    private static Set<String> toLowerCase(Set<String> tokens) {
        Set<String> result = new TreeSet<>();

        for (String eachToken : tokens) {
            result.add(eachToken.toLowerCase());
        }

        return result;
    }

    public List<JavadocDescription> getJavadocDescriptions(File file, Charset charset) {
        List<JavadocDescription> result = new ArrayList<>();

        String source = getSource(file, charset);

        Matcher matcher = JAVADOC_AND_METHOD_PATTERN.matcher(source);
        while (matcher.find()) {
            String signature = matcher.group(matcher.groupCount() - 1);
            if (signature.contains("class") || signature.contains("package") || !signature.contains("(")) {
                continue;
            }

            String method = matcher.group();
            String javaDoc = matcher.group(1);
            int line = countOccurrences(source, matcher.start(), LINE_SEPARATOR) + 1
                + countOccurrences(method, method.lastIndexOf(METHOD_PARAMETER_START), LINE_SEPARATOR);

            JavadocDescription methodDescription = createMethodDescription(javaDoc, signature, line);

            result.add(methodDescription);
        }

        return result;
    }
}
