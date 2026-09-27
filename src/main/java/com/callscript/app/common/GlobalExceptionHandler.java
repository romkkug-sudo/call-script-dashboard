package com.callscript.app.common;

import com.callscript.app.calllog.CallLogNotFoundException;
import com.callscript.app.script.ScriptNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScriptNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleScriptNotFound(ScriptNotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(CallLogNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCallLogNotFound(CallLogNotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    private ResponseEntity<Map<String, String>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", message));
    }
}
