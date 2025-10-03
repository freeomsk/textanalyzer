# Простой тест API
Write-Host "Быстрый тест API" -ForegroundColor Green

# 1. Проверка работоспособности
Write-Host "`n1. Проверка работоспособности" -ForegroundColor Yellow
$health = Invoke-RestMethod -Uri "http://localhost:8080/api/text-analysis/health" -Method GET
Write-Host "   Статус: $($health.status)"
Write-Host "   Словарь: $($health.dictionarySize) words"

# 2. Анализ текста
Write-Host "`n2. Анализ текста" -ForegroundColor Yellow
$result = Invoke-RestMethod -Uri "http://localhost:8080/api/text-analysis/analyze" -Method POST -Body '{"text":"Здавствуйте, как дела?","language":"ru"}' -ContentType "application/json"

if ($result.success) {
    Write-Host "   Успешно: да" -ForegroundColor Green

    # Подсчет ошибок
    $spelling = if ($result.result.spellingErrors) { $result.result.spellingErrors.Count } else { 0 }
    $grammar = if ($result.result.grammarErrors) { $result.result.grammarErrors.Count } else { 0 }
    $punctuation = if ($result.result.punctuationErrors) { $result.result.punctuationErrors.Count } else { 0 }
    $style = if ($result.result.styleIssues) { $result.result.styleIssues.Count } else { 0 }

    $total = $spelling + $grammar + $punctuation + $style
    Write-Host "   Общее количество ошибок: $total" -ForegroundColor White
    Write-Host "   - Орфография: $spelling" -ForegroundColor Red
    Write-Host "   - Грамматика: $grammar" -ForegroundColor Magenta
    Write-Host "   - Пунктцация: $punctuation" -ForegroundColor Cyan
    Write-Host "   - Стиль: $style" -ForegroundColor DarkYellow

    # Показать первую орфографическую ошибку с предложениями
    if ($spelling -gt 0 -and $result.result.spellingErrors[0]) {
        $firstError = $result.result.spellingErrors[0]
        Write-Host "   Пример ошибки: '$($firstError.text)'" -ForegroundColor Red
        if ($firstError.suggestions -and $firstError.suggestions.Count -gt 0) {
            Write-Host "   Варианты исправления: $($firstError.suggestions -join ', ')" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "   Неуспешно: $($result.message)" -ForegroundColor Red
}