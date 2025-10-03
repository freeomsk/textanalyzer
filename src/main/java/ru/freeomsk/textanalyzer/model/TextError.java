package ru.freeomsk.textanalyzer.model;

public class TextError {
    private final String text;
    private final int position;
    private final String description;
    private final String errorCode;

    public TextError(String text, int position, String description, String errorCode) {
        this.text = text;
        this.position = position;
        this.description = description;
        this.errorCode = errorCode;
    }

    // Getters
    public String getText() { return text; }
    public int getPosition() { return position; }
    public String getDescription() { return description; }
    public String getErrorCode() { return errorCode; }

    public String getDetailedDescription() {
        String posInfo = position >= 0 ? " (позиция: " + position + ")" : "";
        return "[" + errorCode + "] \"" + text + "\"" + posInfo + " - " + description;
    }
}