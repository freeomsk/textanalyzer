package ru.freeomsk.textanalyzer.model;

public class StyleIssue extends TextError {
    private final double severity;

    public StyleIssue(String text, int position, String description, String errorCode, double severity) {
        super(text, position, description, errorCode);
        this.severity = severity;
    }

    public double getSeverity() { return severity; }
}