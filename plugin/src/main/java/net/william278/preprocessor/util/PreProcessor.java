/*
 * This file is part of WiIIiam278/PreProcessor, licensed under CC BY-NC-SA 4.0 (the "License").
 * The License applies under the Adapted Material clause of CC BY-NC-SA 4.0 (see Section 1 - Definitions)
 * WiIIiam278/PreProcessor is a derivative work of ToCraft/PreProcessor (https://github.com/ToCraft/PreProcessor)
 *
 *  Copyright (c) To_Craft <development@tocraft.dev>
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 * You can obtain a copy of the license at: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package net.william278.preprocessor.util;

import net.william278.preprocessor.data.Keywords;
import net.william278.preprocessor.data.PreprocessExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The actual preprocessing is happening here
 */
public class PreProcessor {
    private final boolean removeComments;
    /**
     * @see PreprocessExtension#vars
     */
    private final Map<String, Object> vars;
    /**
     * @see PreprocessExtension#keywords
     */
    private final Map<String, Keywords> keywordsMap;

    /**
     * @param vars the vars that shall be used for the custom if-statements
     */
    public PreProcessor(Map<String, Object> vars) {
        this(vars, new HashMap<>());
    }

    public PreProcessor(Boolean removeComments, Map<String, Object> vars) {
        this(removeComments, vars, new HashMap<>());
    }

    /**
     * @param vars        the vars that shall be used for the custom if-statements
     * @param keywordsMap custom keywords, where the key is something the target file name should end with (e.g. '.json') and the Keywords are the custom keywords for this file type.
     */
    public PreProcessor(Map<String, Object> vars, Map<String, Keywords> keywordsMap) {
        this(false, vars, keywordsMap);
    }

    public PreProcessor(boolean removeComments, Map<String, Object> vars, Map<String, Keywords> keywordsMap) {
        this.removeComments = removeComments;
        this.vars = vars;
        this.keywordsMap = keywordsMap;
    }

    private static final Pattern EXPR_PATTERN = Pattern.compile("(.+)(==|!=|<=|>=|<|>)(.+)");
    private static final String OR_PATTERN = Pattern.quote("||");
    private static final String AND_PATTERN = Pattern.quote("&&");

    private String getVarValue(@Nullable String key) {
        if (key != null) {
            Object value = vars.get(key);
            return value != null ? value.toString() : key;
        } else {
            return null;
        }
    }

    /**
     * @param condition will be read and evaluated
     * @return the value of the evaluated condition
     */
    public boolean evalExpression(String condition) {
        return evalExpression(condition, -1, null);
    }

    /**
     * @param condition  will be read and evaluated
     * @param lineNumber required for error throwing
     * @param fileName   required for error throwing
     * @return the value of the evaluated condition
     */
    public boolean evalExpression(@NotNull String condition, int lineNumber, @Nullable String fileName) {
        String[] parts = condition.split(OR_PATTERN);
        if (parts.length > 1) {
            return Arrays.stream(parts).anyMatch(it -> evalExpression(it.trim(), lineNumber, fileName));
        }
        parts = condition.split(AND_PATTERN);
        if (parts.length > 1) {
            return Arrays.stream(parts).allMatch(it -> evalExpression(it.trim(), lineNumber, fileName));
        }

        Matcher matcher = EXPR_PATTERN.matcher(condition);
        if (matcher.matches()) {
            try {
                int lhs = Integer.parseInt(getVarValue(matcher.group(1).trim()));
                int rhs = Integer.parseInt(getVarValue(matcher.group(3).trim()));
                boolean bool;
                switch (matcher.group(2)) {
                    case "==": {
                        bool = lhs == rhs;
                        break;
                    }
                    case "!=": {
                        bool = lhs != rhs;
                        break;
                    }
                    case ">=": {
                        bool = lhs >= rhs;
                        break;
                    }
                    case "<=": {
                        bool = lhs <= rhs;
                        break;
                    }
                    case ">": {
                        bool = lhs > rhs;
                        break;
                    }
                    case "<": {
                        bool = lhs < rhs;
                        break;
                    }
                    default: {
                        throw new ParseException("Invalid Expression!", lineNumber, fileName);
                    }
                }
                return bool;
            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage(), lineNumber, fileName);
            }
        }

        String result = getVarValue(condition);

        if (result != null && !result.equals(condition)) {
            try {
                return Integer.parseInt(result) != 0;
            } catch (NumberFormatException ignored) {
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean _evalCondition(@NotNull String condition, int lineNumber, String fileName) {
        if (!condition.startsWith(" ")) {
            throw new ParseException("Expected space before condition!", lineNumber, fileName);
        } else {
            return evalExpression(condition.trim(), lineNumber, fileName);
        }
    }

    /**
     * @param lines the file, already read as lines
     * @return the preprocessed lines
     */
    public List<String> convertSource(List<String> lines) {
        return convertSource(lines, null);
    }

    /**
     * @param lines    the file, already read as lines
     * @param fileName the file name for error throwing
     * @return the preprocessed lines
     */
    public List<String> convertSource(@NotNull List<String> lines, @Nullable String fileName) {
        Stack<IfStackEntry> stack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();
        boolean active = true;
        int n = 0;

        Keywords keywords = keywordsMap.getOrDefault(getExtension(fileName), Keywords.DEFAULT_KEYWORDS);

        List<String> mappedLines = new ArrayList<>();
        for (final String line : lines) {
            n++;

            String trimmed = line.trim();
            int indentation = line.length() - line.trim().length();
            if (trimmed.startsWith(keywords.IF())) {
                boolean result = _evalCondition(trimmed.substring(keywords.IF().length()), n, fileName);
                stack.push(new IfStackEntry(result, false, result));
                indentStack.push(indentation);
                active = active && result;
                if (!removeComments) {
                    mappedLines.add(line);
                }
            } else if (trimmed.startsWith(keywords.ELSEIF())) {
                if (stack.isEmpty()) {
                    throw new ParseException("elseif without If-Statement!", n, fileName);
                }
                if (stack.lastElement().elseFound) {
                    throw new ParseException("elseif after else!", n, fileName);
                }

                indentStack.pop();
                indentStack.push(indentation);

                if (stack.lastElement().trueFound) {
                    IfStackEntry last = stack.pop();
                    stack.push(new IfStackEntry(false, last.elseFound, last.trueFound));
                    active = false;
                } else {
                    boolean result = _evalCondition(trimmed.substring(keywords.ELSEIF().length()), n, fileName);
                    stack.pop();
                    stack.push(new IfStackEntry(result, false, result));
                    active = stack.stream().allMatch(it -> it.currentValue);
                }
                if (!removeComments) {
                    mappedLines.add(line);
                }
            } else if (trimmed.startsWith(keywords.ELSE())) {
                if (stack.isEmpty()) {
                    throw new ParseException("Unexpected else", n, fileName);
                }
                IfStackEntry entry = stack.pop();
                stack.push(new IfStackEntry(!entry.trueFound, true, entry.trueFound));
                indentStack.pop();
                indentStack.push(indentation);
                active = stack.stream().allMatch(it -> it.currentValue);
                if (!removeComments) {
                    mappedLines.add(line);
                }
            } else if (trimmed.startsWith(keywords.ENDIF())) {
                if (stack.isEmpty()) {
                    throw new ParseException("endif without If-Statement!", n, fileName);
                }
                stack.pop();
                indentStack.pop();
                active = stack.stream().allMatch(it -> it.currentValue);
                if (!removeComments) {
                    mappedLines.add(line);
                }
            } else {
                if (active) {
                    if (trimmed.startsWith(keywords.EVAL())) {
                        mappedLines.add(line.replaceFirst(Matcher.quoteReplacement(keywords.EVAL()) + " ?", ""));
                    } else {
                        mappedLines.add(line);
                    }
                } else if (!removeComments) {
                    int currIndent = indentStack.peek();
                    if (trimmed.isEmpty()) {
                        mappedLines.add(indentation(currIndent) + keywords.EVAL());
                    } else if (!trimmed.startsWith(keywords.EVAL()) && currIndent <= indentation) {
                        mappedLines.add(indentation(currIndent) + keywords.EVAL() + " " + line.substring(currIndent));
                    } else {
                        mappedLines.add(line);
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            throw new ParseException("Missing endif!", n, fileName);
        } else {
            return mappedLines;
        }
    }

    /**
     * @param reMapper ReMapper to be used to replace matches in the files
     * @param inFile  the file that shall be preprocessed
     * @param outFile the file where the preprocessed lines shall be written to
     */
    public void convertFile(@NotNull ReMapper reMapper, @NotNull File inFile, @NotNull File outFile) {
        try {
            List<String> lines = Files.readAllLines(inFile.toPath());
            lines = this.convertSource(lines, inFile.getName());
            lines = reMapper.convertSource(lines);

            //noinspection ResultOfMethodCallIgnored
            outFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(outFile)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            // some error while reading. Just copy the file
            try {
                //noinspection ResultOfMethodCallIgnored
                outFile.getParentFile().mkdirs();
                Files.copy(inFile.toPath(), outFile.toPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static final class IfStackEntry {
        private final boolean currentValue;
        private final boolean elseFound;
        private final boolean trueFound;

        public IfStackEntry(boolean currentValue, boolean elseFound, boolean trueFound) {
            this.currentValue = currentValue;
            this.elseFound = elseFound;
            this.trueFound = trueFound;
        }

    }

    private static @NotNull String getExtension(@Nullable String fileName) {
        String extension = "";
        if (fileName != null) {
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i + 1);
            }
        }
        return extension.toLowerCase().trim();
    }

    private static @NotNull String indentation(int n) {
        return new String(new char[n]).replace("\0", " ");
    }
}
