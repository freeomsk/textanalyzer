package ru.freeomsk.textanalyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TextProcessor {

    public String[] splitSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new String[0];
        }

        // Улучшенное разделение на предложения
        String[] sentences = text.split("(?<=[.!?])\\s+");
        List<String> result = new ArrayList<>();

        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }

        return result.toArray(new String[0]);
    }

    public String[] tokenizeWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new String[0];
        }

        // Токенизация с сохранением знаков препинания для анализа
        return text.split("\\s+");
    }

    public List<String> extractRussianWords(String text) {
        List<String> words = new ArrayList<>();
        String[] tokens = tokenizeWords(text.toLowerCase());

        Pattern russianWordPattern = Pattern.compile("[а-яё]+");

        for (String token : tokens) {
            // Очищаем слово от знаков препинания
            String cleanToken = token.replaceAll("[^а-яё]", "");
            if (cleanToken.length() > 1 && russianWordPattern.matcher(cleanToken).matches()) {
                words.add(cleanToken);
            }
        }

        return words;
    }

    public boolean isCyrillicWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }
        return word.matches("[а-яё]+");
    }

    public String cleanWord(String word) {
        if (word == null) return "";
        return word.toLowerCase().replaceAll("[^а-яё]", "");
    }

    public int countSyllables(String word) {
        if (word == null || word.isEmpty()) return 0;

        String lowerWord = word.toLowerCase();
        // Подсчет гласных как приблизительное количество слогов
        int count = 0;
        for (char c : lowerWord.toCharArray()) {
            if ("аеёиоуыэюя".indexOf(c) >= 0) {
                count++;
            }
        }
        return Math.max(1, count); // Минимум 1 слог
    }
}