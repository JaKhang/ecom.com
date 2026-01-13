package com.nlu.store.core.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link PathExtractor} based on Regular Expressions.
 * <p>
 * This class converts URI templates (e.g., {@code /users/{id}}) into compiled {@link Pattern} objects
 * to extract named variables.
 * </p>
 * <p>
 * <strong>Performance Note:</strong> It utilizes an internal {@link ConcurrentHashMap} to cache
 * compiled patterns, ensuring that the expensive regex compilation happens only once per template.
 * </p>
 *
 * @author Ja Khang
 */
public class DefaultPathExtractor implements PathExtractor {

    /**
     * Cache to store compiled Patterns to avoid recompilation overhead on every request.
     * Key: The URI template string.
     * Value: The compiled Regex Pattern.
     */
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractPathValue(String template, String name, String path) {
        // Normalize null path to root
        if (path == null) path = "/";

        // 1. Retrieve the Pattern from Cache or build a new one if it doesn't exist
        // Atomic operation ensures thread safety without explicit synchronization blocks
        Pattern pattern = PATTERN_CACHE.computeIfAbsent(template, this::buildRegexFromTemplate);

        // 2. Perform the matching
        Matcher matcher = pattern.matcher(path);

        // Check if the whole path matches the pattern
        if (matcher.matches()) {
            try {
                // Retrieve value from the Named Group
                return matcher.group(name);
            } catch (IllegalArgumentException e) {
                // The specified group name does not exist in the pattern
                // This happens if the template doesn't contain the requested variable name
                return null;
            }
        }

        // Path does not match the template structure
        return null;
    }

    /**
     * Helper: Converts a URL Template into a Regex Pattern.
     * <p>
     * Transformation rules:
     * <ul>
     *     <li>{@code {id}} &rarr; Named Group {@code (?<id>[^/]+)}</li>
     *     <li>{@code *} &rarr; Wildcard {@code [^/]+}</li>
     *     <li>{@code v1|v2} &rarr; Non-capturing group {@code (?:v1|v2)}</li>
     *     <li>Static text &rarr; Quoted literal</li>
     * </ul>
     * </p>
     *
     * @param template the raw URI template.
     * @return the compiled Regex Pattern.
     */
    private Pattern buildRegexFromTemplate(String template) {
        StringBuilder regexBuilder = new StringBuilder("^");
        String[] segments = template.split("/");

        for (String segment : segments) {
            if (segment.isEmpty()) {
                // Skip empty segments (usually caused by a leading slash in the split)
                continue;
            }

            // Always add a slash before each valid segment
            // (Assumes the path starts with /, so the regex mirrors that structure)
            regexBuilder.append("/");

            if (segment.equals("*")) {
                // Wildcard support: Matches any character except a slash
                regexBuilder.append("[^/]+");
            }
            else if (segment.startsWith("{") && segment.endsWith("}")) {
                // Named Group support: {id} -> (?<id>[^/]+)
                String varName = segment.substring(1, segment.length() - 1);

                // CRITICAL FIX: Do NOT use Pattern.quote() inside the group name syntax (?<name>...)
                // Group names must be alphanumeric. Quoting them adds \Q...\E which breaks the syntax.
                regexBuilder.append("(?<").append(varName).append(">[^/]+)");
            }
            else if (segment.contains("|")) {
                // OR Operator support: users|people -> (?:users|people)
                // Uses a Non-capturing group
                regexBuilder.append("(?:").append(segment).append(")");
            }
            else {
                // Static text: Escape special regex characters for safety (e.g., dots, brackets)
                regexBuilder.append(Pattern.quote(segment));
            }
        }

        // Handle trailing slash if the template explicitly ends with one
        if (template.endsWith("/")) {
            regexBuilder.append("/");
        }

        regexBuilder.append("$");
        return Pattern.compile(regexBuilder.toString());
    }
}
