package ru.freeomsk.textanalyzer.controller;

import ru.freeomsk.textanalyzer.model.AnalysisResult;
import ru.freeomsk.textanalyzer.service.AdvancedTextAnalyzer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/text-analysis")
@CrossOrigin(origins = "*")
public class TextAnalysisController {

    private final AdvancedTextAnalyzer textAnalyzer;

    public TextAnalysisController(AdvancedTextAnalyzer textAnalyzer) {
        this.textAnalyzer = textAnalyzer;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeText(@RequestBody AnalysisRequest request) {
        try {
            if (request.text() == null || request.text().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Текст для анализа не может быть пустым"
                ));
            }

            if (request.language() == null || !request.language().equals("ru")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Поддерживается только русский язык (ru)"
                ));
            }

            AnalysisResult result = textAnalyzer.analyzeText(request.text(), request.language());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Анализ завершен успешно",
                    "result", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка анализа: " + e.getMessage(),
                            "result", null
                    ));
        }
    }

    @PostMapping("/batch-analyze")
    public ResponseEntity<Map<String, Object>> analyzeMultipleTexts(
            @RequestBody List<AnalysisRequest> requests) {

        try {
            if (requests == null || requests.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Список запросов не может быть пустым"
                ));
            }

            List<AnalysisResult> results = requests.stream()
                    .map(request -> textAnalyzer.analyzeText(request.text(), request.language()))
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Пакетный анализ завершен",
                    "results", results,
                    "totalProcessed", results.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка пакетного анализа: " + e.getMessage(),
                            "results", null
                    ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            int dictionarySize = textAnalyzer.getDictionarySize();
            return ResponseEntity.ok(Map.of(
                    "status", "OK",
                    "service", "Text Analyzer",
                    "version", "1.0.0",
                    "dictionarySize", dictionarySize,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "ERROR",
                            "message", "Ошибка проверки здоровья: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/dictionary/size")
    public ResponseEntity<Map<String, Object>> getDictionarySize() {
        try {
            int size = textAnalyzer.getDictionarySize();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "dictionarySize", size,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка получения размера словаря: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/dictionary/add")
    public ResponseEntity<Map<String, Object>> addWordToDictionary(@RequestBody Map<String, String> request) {
        try {
            String word = request.get("word");
            if (word == null || word.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Слово не может быть пустым"
                ));
            }

            textAnalyzer.addCustomWord(word.trim());
            int newSize = textAnalyzer.getDictionarySize();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Слово '" + word + "' добавлено в словарь",
                    "dictionarySize", newSize
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Ошибка добавления слова: " + e.getMessage()
                    ));
        }
    }

    // DTO record
    public record AnalysisRequest(String text, String language) {}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
        ));
    }
}