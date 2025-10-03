package ru.freeomsk.textanalyzer.service;

import ru.freeomsk.textanalyzer.dictionary.RussianDictionaryService;
import ru.freeomsk.textanalyzer.model.SpellingError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DictionaryService {

    private final RussianDictionaryService russianDictionary;
    private final TextProcessor textProcessor;

    public DictionaryService(RussianDictionaryService russianDictionary, TextProcessor textProcessor) {
        this.russianDictionary = russianDictionary;
        this.textProcessor = textProcessor;
    }

    public List<SpellingError> checkSpelling(String text) {
        List<SpellingError> errors = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return errors;
        }

        String[] words = textProcessor.tokenizeWords(text);

        for (int i = 0; i < words.length; i++) {
            String originalWord = words[i];
            String cleanWord = textProcessor.cleanWord(originalWord);

            // Пропускаем короткие слова и не-кириллические
            if (cleanWord.length() < 2 || !textProcessor.isCyrillicWord(cleanWord)) {
                continue;
            }

            if (!russianDictionary.isWordValid(cleanWord)) {
                List<String> suggestions = russianDictionary.getSuggestions(cleanWord);
                String errorType = determineErrorType(originalWord);

                errors.add(new SpellingError(
                        originalWord,
                        findWordPosition(text, words, i),
                        suggestions,
                        "Слово не найдено в словаре",
                        "ORPH_" + errorType
                ));
            }
        }

        return errors;
    }

    private String determineErrorType(String word) {
        if (word.matches(".*[a-z].*")) return "LATIN_MIX";
        if (word.matches(".*[0-9].*")) return "NUMBER_MIX";
        if (word.length() > 20) return "TOO_LONG";
        return "UNKNOWN_WORD";
    }

    private int findWordPosition(String text, String[] words, int wordIndex) {
        int position = 0;
        for (int i = 0; i < Math.min(wordIndex, words.length); i++) {
            position += words[i].length() + 1; // +1 for space
        }
        return Math.min(position, text.length());
    }

    public void addCustomWord(String word) {
        russianDictionary.addWordToDictionary(word);
    }

    public int getDictionarySize() {
        return russianDictionary.getDictionarySize();
    }
}