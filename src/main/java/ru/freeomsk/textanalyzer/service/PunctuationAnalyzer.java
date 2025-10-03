package ru.freeomsk.textanalyzer.service;

import org.springframework.stereotype.Service;
import ru.freeomsk.textanalyzer.model.PunctuationError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PunctuationAnalyzer {

    private final Pattern punctuationPattern;

    public PunctuationAnalyzer() {
        String rules =
                "(?<spaceBefore>[\\s][.,!?;:])|" +           // пробел перед знаком
                        "(?<missingSpaceAfter>[.,!?;:](?![\\s]))|" + // нет пробела после знака
                        "(?<multipleSpaces>[ ]{2,})";                // multiple spaces

        this.punctuationPattern = Pattern.compile(rules);
    }

    public List<PunctuationError> checkPunctuation(String text) {
        List<PunctuationError> errors = new ArrayList<>();

        var matcher = punctuationPattern.matcher(text);
        while (matcher.find()) {
            String errorText = text.substring(matcher.start(), matcher.end());
            String errorType = "";
            String expected = "";

            if (matcher.group("spaceBefore") != null) {
                errorType = "Пробел перед знаком препинания";
                expected = "Уберите пробел перед знаком препинания";
            } else if (matcher.group("missingSpaceAfter") != null) {
                errorType = "Отсутствует пробел после знака препинания";
                expected = "Добавьте пробел после знака препинания";
            } else if (matcher.group("multipleSpaces") != null) {
                errorType = "Лишние пробелы";
                expected = "Уберите лишние пробелы";
            }

            errors.add(new PunctuationError(
                    errorText,
                    matcher.start(),
                    errorType,
                    "PUNC_" + getErrorTypeCode(matcher),
                    expected
            ));
        }

        // Проверка парных символов
        errors.addAll(checkPairedCharacters(text));

        return errors;
    }

    private List<PunctuationError> checkPairedCharacters(String text) {
        List<PunctuationError> errors = new ArrayList<>();

        // Проверка кавычек
        long quoteCount = text.chars().filter(ch -> ch == '"').count();
        if (quoteCount % 2 != 0) {
            errors.add(new PunctuationError(
                    "\"",
                    text.indexOf('"'),
                    "Непарные кавычки",
                    "PUNC_UNPAIRED_QUOTE",
                    "Добавьте закрывающую кавычку"
            ));
        }

        // Проверка скобок
        int openBracket = text.indexOf('(');
        int closeBracket = text.indexOf(')');
        if ((openBracket >= 0 && closeBracket < 0) || (openBracket < 0 && closeBracket >= 0)) {
            errors.add(new PunctuationError(
                    openBracket >= 0 ? "(" : ")",
                    Math.max(openBracket, closeBracket),
                    "Непарные скобки",
                    "PUNC_UNPAIRED_BRACKET",
                    "Добавьте парную скобку"
            ));
        }

        return errors;
    }

    private String getErrorTypeCode(java.util.regex.Matcher matcher) {
        if (matcher.group("spaceBefore") != null) return "SPACE_BEFORE";
        if (matcher.group("missingSpaceAfter") != null) return "MISSING_SPACE_AFTER";
        if (matcher.group("multipleSpaces") != null) return "MULTIPLE_SPACES";
        return "UNKNOWN";
    }
}