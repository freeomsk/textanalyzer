Write-Host "Тестированире Text Analyzer API..." -ForegroundColor Green

# Тест 1: Проверка работоспособности
Write-Host "`n1. Проверка работоспособности:" -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/api/text-analysis/health" -Method GET
    Write-Host "Статус: $($health.status)" -ForegroundColor White
    Write-Host "Размер словаря: $($health.dictionarySize)" -ForegroundColor White
} catch {
    Write-Host "Проверка работоспособности не пройдена: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Тест 2: Анализ текста
Write-Host "`n2. Анализ текста:" -ForegroundColor Yellow
try {
    $analysis = Invoke-RestMethod -Uri "http://localhost:8080/api/text-analysis/analyze" -Method POST -Body '{"text":"Здавствуйте, мне бы хотелось заказать пиццу с ананасами. Я не уверена правильно ли я оформляю заказ?","language":"ru"}' -ContentType "application/json"

    if ($analysis.success) {
        Write-Host "Анализ успешен!" -ForegroundColor Green

        # Безопасный подсчет ошибок
        $totalErrors = 0
        if ($analysis.result.spellingErrors) { $totalErrors += $analysis.result.spellingErrors.Count }
        if ($analysis.result.grammarErrors) { $totalErrors += $analysis.result.grammarErrors.Count }
        if ($analysis.result.punctuationErrors) { $totalErrors += $analysis.result.punctuationErrors.Count }
        if ($analysis.result.styleIssues) { $totalErrors += $analysis.result.styleIssues.Count }

        Write-Host "Всего ошибок: $totalErrors" -ForegroundColor White

        # Орфографические ошибки
        if ($analysis.result.spellingErrors -and $analysis.result.spellingErrors.Count -gt 0) {
            Write-Host "Найдены орфографические ошибки:" -ForegroundColor Red
            foreach ($spellingError in $analysis.result.spellingErrors) {
                Write-Host "  - $($spellingError.text)" -ForegroundColor Red
                if ($spellingError.suggestions -and $spellingError.suggestions.Count -gt 0) {
                    Write-Host "    Варианты исправления: $($spellingError.suggestions -join ', ')" -ForegroundColor Yellow
                }
            }
        } else {
            Write-Host "Орфографических ошибок не обнаружено." -ForegroundColor Green
        }

        # Грамматические ошибки
        if ($analysis.result.grammarErrors -and $analysis.result.grammarErrors.Count -gt 0) {
            Write-Host "Найдены грамматические ошибки:" -ForegroundColor Magenta
            foreach ($grammarError in $analysis.result.grammarErrors) {
                Write-Host "  - $($grammarError.text): $($grammarError.description)" -ForegroundColor Magenta
            }
        } else {
            Write-Host "Грамматических ошибок не обнаружено." -ForegroundColor Green
        }

        # Пунктуационные ошибки
        if ($analysis.result.punctuationErrors -and $analysis.result.punctuationErrors.Count -gt 0) {
            Write-Host "Найдены пунктуационные ошибки:" -ForegroundColor Cyan
            foreach ($punctError in $analysis.result.punctuationErrors) {
                Write-Host "  - $($punctError.text): $($punctError.description)" -ForegroundColor Cyan
            }
        }

        # Стилистические замечания
        if ($analysis.result.styleIssues -and $analysis.result.styleIssues.Count -gt 0) {
            Write-Host "Обнаружены проблемы со стилем:" -ForegroundColor DarkYellow
            foreach ($styleIssue in $analysis.result.styleIssues) {
                Write-Host "  - $($styleIssue.text): $($styleIssue.description)" -ForegroundColor DarkYellow
            }
        }

        # Метрики текста
        if ($analysis.result.metrics) {
            Write-Host "`n Метрики текста:" -ForegroundColor Cyan
            Write-Host "  Символы: $($analysis.result.metrics.charCount)" -ForegroundColor White
            Write-Host "  Слова: $($analysis.result.metrics.wordCount)" -ForegroundColor White
            Write-Host "  Предложения: $($analysis.result.metrics.sentenceCount)" -ForegroundColor White
            if ($analysis.result.metrics.readabilityIndex) {
                Write-Host "  Читабельность: $([math]::Round($analysis.result.metrics.readabilityIndex, 1))" -ForegroundColor White
            }
            if ($analysis.result.metrics.waterPercentage) {
                Write-Host "  Процентное содержание воды: $([math]::Round($analysis.result.metrics.waterPercentage, 1))%" -ForegroundColor White
            }
        }

        # Простая оценка качества (на клиенте)
        $qualityScore = 100 - ($totalErrors * 3)
        if ($qualityScore -lt 30) { $qualityScore = 30 }
        Write-Host "Расчетный показатель качества: $qualityScore/100" -ForegroundColor Cyan

    } else {
        Write-Host "Анализ не удался: $($analysis.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "Запрос на анализ не выполнен: $($_.Exception.Message)" -ForegroundColor Red
}

# Тест 3: Размер словаря
Write-Host "`n3. Размер словаря:" -ForegroundColor Yellow
try {
    $size = Invoke-RestMethod -Uri "http://localhost:8080/api/text-analysis/dictionary/size" -Method GET
    Write-Host "Текущий размер словаря: $($size.dictionarySize)" -ForegroundColor White
} catch {
    Write-Host "Запрос размера словаря не удался: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nТест завершен!" -ForegroundColor Green