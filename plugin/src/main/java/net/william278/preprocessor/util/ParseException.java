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

/**
 * Exception while parsing / reading the preprocessor code in a file
 */
public class ParseException extends RuntimeException {
    /**
     * The Line where the exception happened
     */
    private int lineNumber = -1;
    /**
     * The file where the exception occurred
     */
    private String fileName = "";

    /**
     * @param message the error message
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * @param message    the error message
     * @param lineNumber the line where the parsing exception happened
     * @param fileName   the file where parsing exception happened
     */
    public ParseException(String message, int lineNumber, String fileName) {
        this(message);
        setLineNumber(lineNumber);
        setFileName(fileName);
    }

    /**
     * @param lineNumber the line where the parsing exception happened
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @param fileName the file where parsing exception happened
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + (lineNumber > -1 ? " In line: " + lineNumber : "") + (fileName != null && !fileName.trim().isEmpty() ? " of file: " + fileName : "");
    }
}
