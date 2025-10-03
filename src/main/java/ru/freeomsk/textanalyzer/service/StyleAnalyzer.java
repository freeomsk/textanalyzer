package ru.freeomsk.textanalyzer.service;

//import com.textanalyzer.model.StyleIssue;
//import com.textanalyzer.util.TextUtils;
import org.springframework.stereotype.Service;
import ru.freeomsk.textanalyzer.model.StyleIssue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class StyleAnalyzer {

    public List<StyleIssue> analyzeStyle(String text) {
        List<StyleIssue> issues = new ArrayList<>();

        issues.addAll(checkRepeatedWords(text));
        issues.addAll(checkLongSentences(text));
        issues.addAll(checkWordFrequency(text));
        issues.addAll(checkPassiveConstructions(text));

        return issues;
    }

    private List<StyleIssue> checkRepeatedWords(String text) {
        List<StyleIssue> issues = new ArrayList<>();

        Pattern repeatedPattern = Pattern.compile(
                "\\b(\\w+)\\s+\\1\\b",
                Pattern.CASE_INSENSITIVE
        );

        var matcher = repeatedPattern.matcher(text);
        while (matcher.find()) {
            issues.add(new StyleIssue(
                    matcher.group(),
                    matcher.start(),
                    "Повторение слова - тавтология",
                    "STYLE_REPETITION",
                    0.7
            ));
        }

        return issues;
    }

    private List<StyleIssue> checkLongSentences(String text) {
        List<StyleIssue> issues = new ArrayList<>();
        String[] sentences = text.split("[.!?]+");

        for (String sentence : sentences) {
            int wordCount = sentence.trim().split("\\s+").length;
            if (wordCount > 25) {
                issues.add(new StyleIssue(
                        sentence.substring(0, Math.min(30, sentence.length())) + "...",
                        text.indexOf(sentence),
                        "Слишком длинное предложение (" + wordCount + " слов)",
                        "STYLE_LONG_SENTENCE",
                        0.5
                ));
            }
        }

        return issues;
    }

    private List<StyleIssue> checkWordFrequency(String text) {
        List<StyleIssue> issues = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");

        var frequencyMap = new java.util.HashMap<String, Integer>();
        for (String word : words) {
            if (word.length() > 3) {
                frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
            }
        }

        for (var entry : frequencyMap.entrySet()) {
            if (entry.getValue() > 3) {
                issues.add(new StyleIssue(
                        entry.getKey(),
                        -1,
                        "Слово повторяется слишком часто (" + entry.getValue() + " раз)",
                        "STYLE_FREQUENCY",
                        0.6
                ));
            }
        }

        return issues;
    }

    private List<StyleIssue> checkPassiveConstructions(String text) {
        List<StyleIssue> issues = new ArrayList<>();

        Pattern passivePattern = Pattern.compile(
                "\\b(был|была|было|были)\\s+\\w+н\\w*\\b",
                Pattern.CASE_INSENSITIVE
        );

        var matcher = passivePattern.matcher(text);
        while (matcher.find()) {
            issues.add(new StyleIssue(
                    matcher.group(),
                    matcher.start(),
                    "Пассивная конструкция -考虑使用主动语态",
                    "STYLE_PASSIVE",
                    0.4
            ));
        }

        return issues;
    }
}