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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReMapper {
    private final Map<String, String> map;

    public ReMapper(Map<String, String> map) {
        this.map = map;
    }

    /**
     * @param lines    the file, already read as lines
     * @return the preprocessed lines
     */
    public List<String> convertSource(@NotNull List<String> lines) {
        List<String> rmLines = new ArrayList<>();

        for (String line : lines) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                line = line.replaceAll(entry.getKey(), entry.getValue());
            }
            rmLines.add(line);
        }

        return rmLines;
    }
}
