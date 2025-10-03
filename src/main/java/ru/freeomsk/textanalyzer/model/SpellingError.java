package ru.freeomsk.textanalyzer.model;

import java.util.List;

public class SpellingError extends TextError {
    private final List<String> suggestions;

    public SpellingError(String text, int position, List<String> suggestions, String description, String errorCode) {
        super(text, position, description, errorCode);
        this.suggestions = suggestions;
    }

    public List<String> getSuggestions() { return suggestions; }
}