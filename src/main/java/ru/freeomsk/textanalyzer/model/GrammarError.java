package ru.freeomsk.textanalyzer.model;

public class GrammarError extends TextError {
    private final String rule;

    public GrammarError(String text, int position, String description, String errorCode, String rule) {
        super(text, position, description, errorCode);
        this.rule = rule;
    }

    public String getRule() { return rule; }
}