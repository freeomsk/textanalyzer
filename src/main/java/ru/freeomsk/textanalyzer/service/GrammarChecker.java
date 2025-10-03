package ru.freeomsk.textanalyzer.service;

//import com.textanalyzer.model.GrammarError;
import org.springframework.stereotype.Service;
import ru.freeomsk.textanalyzer.model.GrammarError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class GrammarChecker {

    public List<GrammarError> checkGrammar(String text) {
        List<GrammarError> errors = new ArrayList<>();

        errors.addAll(checkSubjectPredicateAgreement(text));
        errors.addAll(checkCaseUsage(text));
        errors.addAll(checkVerbForms(text));

        return errors;
    }

    private List<GrammarError> checkSubjectPredicateAgreement(String text) {
        List<GrammarError> errors = new ArrayList<>();
        String[] sentences = text.split("[.!?]+");

        for (String sentence : sentences) {
            String[] words = sentence.trim().split("\\s+");
            if (words.length < 2) continue;

            // Простая проверка: если существительное в единственном числе,
            // а глагол во множественном (и наоборот)
            for (int i = 0; i < words.length - 1; i++) {
                if (isNounSingular(words[i]) && isVerbPlural(words[i + 1])) {
                    errors.add(createGrammarError(
                            words[i] + " " + words[i + 1],
                            sentence,
                            "Несогласование подлежащего и сказуемого в числе",
                            "GRAM_AGREEMENT_NUMBER",
                            "Числовое согласование"
                    ));
                }
            }
        }

        return errors;
    }

    private List<GrammarError> checkCaseUsage(String text) {
        List<GrammarError> errors = new ArrayList<>();

        // Проверка падежного управления с предлогами
        Pattern[] casePatterns = {
                Pattern.compile("\\b(о|об|про)\\s+\\w+а\\b", Pattern.CASE_INSENSITIVE), // предложный падеж
                Pattern.compile("\\b(без|до|от|у)\\s+\\w+е\\b", Pattern.CASE_INSENSITIVE) // родительный падеж
        };

        for (Pattern pattern : casePatterns) {
            var matcher = pattern.matcher(text);
            while (matcher.find()) {
                errors.add(createGrammarError(
                        matcher.group(),
                        text,
                        "Возможно, неправильное использование падежа",
                        "GRAM_CASE_USAGE",
                        "Падежное управление"
                ));
            }
        }

        return errors;
    }

    private List<GrammarError> checkVerbForms(String text) {
        List<GrammarError> errors = new ArrayList<>();

        // Проверка видовременных форм
        Pattern verbPattern = Pattern.compile(
                "\\b(буду|будет|будут)\\s+\\w+ть\\b",
                Pattern.CASE_INSENSITIVE
        );

        var matcher = verbPattern.matcher(text);
        while (matcher.find()) {
            errors.add(createGrammarError(
                    matcher.group(),
                    text,
                    "Проверьте видовременную форму глагола",
                    "GRAM_VERB_FORM",
                    "Вид глагола"
            ));
        }

        return errors;
    }

    private boolean isNounSingular(String word) {
        return word.matches(".*[аяоей]$") && !word.matches(".*[ыи]$");
    }

    private boolean isVerbPlural(String word) {
        return word.matches(".*(ют|ят|али|или)$");
    }

    private GrammarError createGrammarError(String text, String fullText,
                                            String description, String errorCode, String rule) {
        int position = fullText.indexOf(text);
        return new GrammarError(text, position, description, errorCode, rule);
    }
}
