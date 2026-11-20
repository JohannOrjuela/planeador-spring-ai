package com.example.chat.exception;

import com.example.chat.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ChatResponse> handle(RuntimeException exception) {
        log.error("Error procesando la solicitud", exception);
        return ResponseEntity.status(502).body(new ChatResponse(null, "", 0, "error"));
    }
}
