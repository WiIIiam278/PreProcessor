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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Sometime in the future, the PreProcess will also auto-handle the imports
 */
@SuppressWarnings("unused")
@ApiStatus.Experimental
public class ImportManager {
    private static @NotNull List<String> organizeImports(@NotNull List<String> sourceLines) {
        Set<String> imports = new TreeSet<>();
        String packageLine = "";
        List<String> codeLines = new ArrayList<>();

        for (String line : sourceLines) {
            if (line.trim().startsWith("import ")) {
                imports.add(getImportClassName(line));
            } else if (line.trim().startsWith("package ")) {
                packageLine = line;
            } else {
                codeLines.add(line);
            }
        }

        while (codeLines.get(0).trim().isEmpty()) {
            codeLines.remove(0);
        }

        // Reconstruct the modified lines
        List<String> modifiedLines = new ArrayList<>();
        modifiedLines.add(packageLine);
        modifiedLines.add("");

        System.out.println(codeLines);

        modifiedLines.addAll(imports.stream().filter(imp -> {
            String[] impArr = imp.split("\\.");
            return anyStringInListContains(codeLines, impArr[impArr.length - 1]);
        }).map(imp -> "import " + imp + ";").collect(Collectors.toList()));
        modifiedLines.add("");

        modifiedLines.addAll(codeLines);

        return modifiedLines;
    }

    private static String getImportClassName(String importLine) {
        Pattern pattern = Pattern.compile("import\\s+([\\w.]+);");
        Matcher matcher = pattern.matcher(importLine);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private static boolean anyStringInListContains(@NotNull List<String> list, String str) {
        for (String s : list) {
            if (!s.trim().startsWith("//") && s.trim().contains(str)) {
                return true;
            }
        }
        return false;
    }
}
