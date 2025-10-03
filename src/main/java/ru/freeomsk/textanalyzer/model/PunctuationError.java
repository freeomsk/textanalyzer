package ru.freeomsk.textanalyzer.model;

public class PunctuationError extends TextError {
    private final String expected;

    public PunctuationError(String text, int position, String description, String errorCode, String expected) {
        super(text, position, description, errorCode);
        this.expected = expected;
    }

    public String getExpected() { return expected; }
}
