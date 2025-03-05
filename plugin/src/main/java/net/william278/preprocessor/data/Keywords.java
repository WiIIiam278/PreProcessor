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

/**
 * The defined keywords that will be taken into account by the preprocessor
 */
public final class Keywords {
    private final String IF;
    private final String ELSEIF;
    private final String ELSE;
    private final String ENDIF;
    private final String EVAL;

    public Keywords(String IF, String ELSEIF, String ELSE, String ENDIF, String EVAL) {
        this.IF = IF;
        this.ELSEIF = ELSEIF;
        this.ELSE = ELSE;
        this.ENDIF = ENDIF;
        this.EVAL = EVAL;
    }

    public String IF() {
        return IF;
    }

    public String ELSEIF() {
        return ELSEIF;
    }

    public String ELSE() {
        return ELSE;
    }

    public String ENDIF() {
        return ENDIF;
    }

    public String EVAL() {
        return EVAL;
    }

    /**
     * Default Keywords and fallback, if no custom keywords are defined for the target file
     */
    public static final Keywords DEFAULT_KEYWORDS = new Keywords("//#if", "//#elseif", "//#else", "//#endif", "//$$");
}