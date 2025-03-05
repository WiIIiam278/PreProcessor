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

package net.william278.preprocessor.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gradle Extension for setting general PreProcessor variables
 */
public class PreprocessExtension {
    /**
     * the vars that shall be used for the custom if-statements
     */
    public Map<String, Object> vars = new HashMap<>();
    /**
     * custom keywords, where the key is something the target file name should end with (e.g. '.json') and the Keywords are the custom keywords for this file type.
     */
    public Map<String, Keywords> keywords = new HashMap<>();
    /**
     * A map where each occurrence of a key in the code will be replaced with the respective value
     */
    public Map<String, String> remapper = new LinkedHashMap<>();
}
