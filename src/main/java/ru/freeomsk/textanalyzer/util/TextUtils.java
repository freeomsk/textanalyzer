package ru.freeomsk.textanalyzer.util;

import java.util.Arrays;
import java.util.List;

public class TextUtils {

    public static double calculateReadabilityIndex(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        String[] sentences = text.split("[.!?]+");
        if (sentences.length == 0) return 0;

        String[] words = text.split("\\s+");
        if (words.length == 0) return 0;

        double avgSentenceLength = (double) words.length / sentences.length;

        long complexWords = Arrays.stream(words)
                .filter(word -> countSyllables(word) > 3)
                .count();

        double complexWordRatio = (double) complexWords / words.length;

        double score = 100 - avgSentenceLength - (complexWordRatio * 100);
        return Math.max(0, Math.min(100, score));
    }

    public static int countSyllables(String word) {
        if (word == null || word.isEmpty()) return 0;

        String lowerWord = word.toLowerCase();
        int count = 0;
        boolean lastWasVowel = false;

        for (char c : lowerWord.toCharArray()) {
            boolean isVowel = "аеёиоуыэюя".indexOf(c) >= 0;
            if (isVowel && !lastWasVowel) {
                count++;
            }
            lastWasVowel = isVowel;
        }

        return Math.max(1, count);
    }

    public static double calculateWaterPercentage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        List<String> waterWords = Arrays.asList(
                "является", "являются", "можно", "нужно", "должен",
                "какой-то", "некоторый", "очень", "довольно", "именно",
                "данный", "определенный", "некий", "собственно", "скажем"
        );

        List<String> words = Arrays.stream(text.toLowerCase().split("\\s+"))
                .filter(word -> word.length() > 2)
                .toList();

        if (words.isEmpty()) return 0;

        long waterWordsCount = words.stream()
                .filter(waterWords::contains)
                .count();

        return (double) waterWordsCount / words.size() * 100;
    }

    public static int calculateLevenshteinDistance(String x, String y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }

        return dp[x.length()][y.length()];
    }
}