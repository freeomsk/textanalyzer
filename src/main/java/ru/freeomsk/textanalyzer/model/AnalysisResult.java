package ru.freeomsk.textanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {
    private final String originalText;
    private final String language;
    private final List<SpellingError> spellingErrors;
    private final List<GrammarError> grammarErrors;
    private final List<PunctuationError> punctuationErrors;
    private final List<StyleIssue> styleIssues;
    private final TextMetrics metrics;

    public AnalysisResult(String originalText, String language) {
        this.originalText = originalText;
        this.language = language;
        this.spellingErrors = new ArrayList<>();
        this.grammarErrors = new ArrayList<>();
        this.punctuationErrors = new ArrayList<>();
        this.styleIssues = new ArrayList<>();
        this.metrics = new TextMetrics(0, 0, 0, 0, 0);
    }

    public AnalysisResult(String originalText, String language,
                          List<SpellingError> spellingErrors,
                          List<GrammarError> grammarErrors,
                          List<PunctuationError> punctuationErrors,
                          List<StyleIssue> styleIssues,
                          TextMetrics metrics) {
        this.originalText = originalText;
        this.language = language;
        this.spellingErrors = spellingErrors;
        this.grammarErrors = grammarErrors;
        this.punctuationErrors = punctuationErrors;
        this.styleIssues = styleIssues;
        this.metrics = metrics;
    }

    // Getters
    public String getOriginalText() { return originalText; }
    public String getLanguage() { return language; }
    public List<SpellingError> getSpellingErrors() { return spellingErrors; }
    public List<GrammarError> getGrammarErrors() { return grammarErrors; }
    public List<PunctuationError> getPunctuationErrors() { return punctuationErrors; }
    public List<StyleIssue> getStyleIssues() { return styleIssues; }
    public TextMetrics getMetrics() { return metrics; }

    public int getTotalErrors() {
        return spellingErrors.size() + grammarErrors.size() +
                punctuationErrors.size() + styleIssues.size();
    }

    public int calculateQualityScore() {
        int totalErrors = getTotalErrors();
        int maxScore = 100;
        int penalty = Math.min(totalErrors * 5, 70);
        return Math.max(maxScore - penalty, 30);
    }
}
