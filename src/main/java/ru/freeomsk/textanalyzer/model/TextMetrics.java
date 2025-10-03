package ru.freeomsk.textanalyzer.model;

public class TextMetrics {
    private final int charCount;
    private final int wordCount;
    private final int sentenceCount;
    private final double readabilityIndex;
    private final double waterPercentage;

    public TextMetrics(int charCount, int wordCount, int sentenceCount,
                       double readabilityIndex, double waterPercentage) {
        this.charCount = charCount;
        this.wordCount = wordCount;
        this.sentenceCount = sentenceCount;
        this.readabilityIndex = readabilityIndex;
        this.waterPercentage = waterPercentage;
    }

    // Getters
    public int getCharCount() { return charCount; }
    public int getWordCount() { return wordCount; }
    public int getSentenceCount() { return sentenceCount; }
    public double getReadabilityIndex() { return readabilityIndex; }
    public double getWaterPercentage() { return waterPercentage; }
}