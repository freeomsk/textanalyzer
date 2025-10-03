package ru.freeomsk.textanalyzer.service;

import ru.freeomsk.textanalyzer.model.AnalysisResult;
import ru.freeomsk.textanalyzer.model.GrammarError;
import ru.freeomsk.textanalyzer.model.PunctuationError;
import ru.freeomsk.textanalyzer.model.SpellingError;
import ru.freeomsk.textanalyzer.model.StyleIssue;
import ru.freeomsk.textanalyzer.model.TextMetrics;
import ru.freeomsk.textanalyzer.util.TextUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class AdvancedTextAnalyzer {

    private final DictionaryService dictionaryService;
    private final GrammarChecker grammarChecker;
    private final PunctuationAnalyzer punctuationAnalyzer;
    private final StyleAnalyzer styleAnalyzer;
    private final ExecutorService executorService;

    public AdvancedTextAnalyzer(DictionaryService dictionaryService,
                                GrammarChecker grammarChecker,
                                PunctuationAnalyzer punctuationAnalyzer,
                                StyleAnalyzer styleAnalyzer) {
        this.dictionaryService = dictionaryService;
        this.grammarChecker = grammarChecker;
        this.punctuationAnalyzer = punctuationAnalyzer;
        this.styleAnalyzer = styleAnalyzer;
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public AnalysisResult analyzeText(String text, String language) {
        if (text == null || text.trim().isEmpty()) {
            return new AnalysisResult(text, language);
        }

        try {
            // Параллельный анализ разных аспектов текста
            Future<List<SpellingError>> spellingFuture =
                    executorService.submit(() -> dictionaryService.checkSpelling(text));

            Future<List<GrammarError>> grammarFuture =
                    executorService.submit(() -> grammarChecker.checkGrammar(text));

            Future<List<PunctuationError>> punctuationFuture =
                    executorService.submit(() -> punctuationAnalyzer.checkPunctuation(text));

            Future<List<StyleIssue>> styleFuture =
                    executorService.submit(() -> styleAnalyzer.analyzeStyle(text));

            // Расчет метрик текста
            TextMetrics metrics = calculateMetrics(text);

            // Сбор результатов
            return new AnalysisResult(
                    text,
                    language,
                    spellingFuture.get(),
                    grammarFuture.get(),
                    punctuationFuture.get(),
                    styleFuture.get(),
                    metrics
            );

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ошибка при анализе текста", e);
        }
    }

    private TextMetrics calculateMetrics(String text) {
        int charCount = text.length();
        String[] words = text.split("\\s+");
        int wordCount = words.length;

        String[] sentences = text.split("[.!?]+");
        int sentenceCount = sentences.length;

        double readabilityIndex = TextUtils.calculateReadabilityIndex(text);
        double waterPercentage = TextUtils.calculateWaterPercentage(text);

        return new TextMetrics(charCount, wordCount, sentenceCount, readabilityIndex, waterPercentage);
    }

    public int getDictionarySize() {
        return dictionaryService.getDictionarySize();
    }

    public void addCustomWord(String word) {
        dictionaryService.addCustomWord(word);
    }

    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}