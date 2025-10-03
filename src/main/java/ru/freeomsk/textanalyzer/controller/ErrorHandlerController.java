package ru.freeomsk.textanalyzer.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class ErrorHandlerController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Неизвестная ошибка сервера",
                            "status", 500
                    ));
        }

        if (statusCode == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Ресурс не найден",
                            "status", 404,
                            "path", request.getAttribute("jakarta.servlet.error.request_uri")
                    ));
        }

        return ResponseEntity.status(statusCode)
                .body(Map.of(
                        "success", false,
                        "message", "Ошибка сервера: " + statusCode,
                        "status", statusCode
                ));
    }
}