package ru.freeomsk.textanalyzer.dictionary;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RussianDictionaryService {

    private final Set<String> dictionary;
    private final Set<String> stopWords;
    private final Map<String, String> commonMistakes;

    public RussianDictionaryService() {
        this.dictionary = loadRussianDictionary();
        this.stopWords = loadStopWords();
        this.commonMistakes = loadCommonMistakes();
    }

    private Set<String> loadRussianDictionary() {
        Set<String> dict = new HashSet<>();

        try {
            ClassPathResource resource = new ClassPathResource("dict/russian_words.txt");
            if (resource.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        resource.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String word = line.trim().toLowerCase();
                        if (word.length() > 1 && !word.startsWith("#")) {
                            dict.add(word);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки словаря из файла: " + e.getMessage());
        }

        dict.addAll(createBasicRussianDictionary());
        return dict;
    }

    private Set<String> createBasicRussianDictionary() {
        // Используем Arrays.asList вместо Set.of
        return new HashSet<>(Arrays.asList(
                "привет", "здравствуйте", "пицца", "пиццу", "ананас", "ананасы", "ананасами",
                "заказ", "оформление", "ответ", "текст", "ошибка", "проверка", "грамматика",
                "орфография", "пунктуация", "стилистика", "программа", "анализатор",
                "слово", "предложение", "язык", "русский", "английский", "пример",
                "результат", "система", "функция", "метод", "класс", "объект",
                "данные", "информация", "файл", "директория", "проект", "разработка",
                "хотеть", "хочу", "хотел", "хотела", "хотелось", "заказать", "оформить",
                "оформляю", "ждать", "жду", "проверить", "проверяю", "найти", "исправить",
                "писать", "написать", "говорить", "сказать", "работать", "создать",
                "использовать", "получить", "сделать", "выполнить", "реализовать",
                "правильный", "неправильный", "русский", "английский", "хороший",
                "плохой", "красивый", "интересный", "сложный", "простой", "быстрый",
                "медленный", "новый", "старый", "основной", "дополнительный", "важный",
                "правильно", "неправильно", "быстро", "медленно", "хорошо", "плохо",
                "очень", "совсем", "почти", "возможно", "точно", "верно",
                "я", "ты", "он", "она", "оно", "мы", "вы", "они", "мой", "твой", "свой",
                "в", "на", "за", "под", "над", "перед", "после", "из", "от", "до", "по",
                "и", "а", "но", "или", "что", "чтобы", "как", "когда", "где", "куда"
        ));
    }

    private Set<String> loadStopWords() {
        // Используем Arrays.asList вместо Set.of
        return new HashSet<>(Arrays.asList(
                "бы", "ли", "же", "вот", "как", "так", "это", "что", "кто",
                "где", "когда", "почему", "зачем", "какой", "какая", "какое", "какие",
                "мне", "тебе", "ему", "ей", "нам", "вам", "им", "меня", "тебя", "его", "её"
        ));
    }

    private Map<String, String> loadCommonMistakes() {
        // Используем традиционное создание Map вместо Map.of
        Map<String, String> mistakes = new HashMap<>();
        mistakes.put("здавствуйте", "здравствуйте");
        mistakes.put("привед", "привет");
        mistakes.put("пака", "пока");
        mistakes.put("симпотичный", "симпатичный");
        mistakes.put("агенство", "агентство");
        mistakes.put("компания", "кампания");
        mistakes.put("впринципе", "в принципе");
        mistakes.put("итд", "и т.д.");
        mistakes.put("итп", "и т.п.");
        mistakes.put("зделать", "сделать");
        mistakes.put("вообщем", "в общем");
        mistakes.put("очет", "отчет");
        mistakes.put("придёт", "придет");
        return mistakes;
    }

    public boolean isWordValid(String word) {
        String cleanWord = word.toLowerCase().replaceAll("[^а-яё]", "");
        if (cleanWord.length() < 2) return true;
        if (stopWords.contains(cleanWord)) return true;

        if (dictionary.contains(cleanWord)) {
            return true;
        }

        if (commonMistakes.containsKey(cleanWord)) {
            return false;
        }

        return checkMorphologicalVariants(cleanWord);
    }

    private boolean checkMorphologicalVariants(String word) {
        if (word.length() <= 3) return false;

        String[] possibleEndings = {"ый", "ий", "ая", "яя", "ое", "ее", "ой", "ей"};
        for (String ending : possibleEndings) {
            if (word.endsWith(ending)) {
                String base = word.substring(0, word.length() - ending.length());
                if (dictionary.contains(base)) {
                    return true;
                }
            }
        }

        String[] verbEndings = {"ть", "ться", "л", "ла", "ло", "ли", "ю", "ешь", "ет", "ем", "ете", "ут", "ют"};
        for (String ending : verbEndings) {
            if (word.endsWith(ending)) {
                String base = word.substring(0, word.length() - ending.length());
                if (dictionary.contains(base) || dictionary.contains(base + "ть")) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<String> getSuggestions(String word) {
        String cleanWord = word.toLowerCase().replaceAll("[^а-яё]", "");
        List<String> suggestions = new ArrayList<>();

        if (commonMistakes.containsKey(cleanWord)) {
            suggestions.add(commonMistakes.get(cleanWord));
        }

        suggestions.addAll(findSimilarWords(cleanWord, new ArrayList<>(dictionary), 2));
        suggestions.addAll(generateMorphologicalSuggestions(cleanWord));

        return suggestions.stream().distinct().limit(5).collect(Collectors.toList());
    }

    private List<String> findSimilarWords(String target, List<String> dictionary, int maxDistance) {
        return dictionary.stream()
                .filter(word -> calculateLevenshteinDistance(target, word) <= maxDistance)
                .sorted(Comparator.comparingInt(word -> calculateLevenshteinDistance(target, word)))
                .limit(3)
                .collect(Collectors.toList());
    }

    private int calculateLevenshteinDistance(String x, String y) {
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

    private List<String> generateMorphologicalSuggestions(String word) {
        List<String> suggestions = new ArrayList<>();

        if (word.startsWith("здравствуйте")) {
            suggestions.add("здравствуйте");
        }
        if (word.startsWith("привет") && word.length() > 6) {
            suggestions.add("привет");
        }

        return suggestions;
    }

    public Set<String> getDictionary() {
        return Collections.unmodifiableSet(dictionary);
    }

    public void addWordToDictionary(String word) {
        dictionary.add(word.toLowerCase());
    }

    public int getDictionarySize() {
        return dictionary.size();
    }
}