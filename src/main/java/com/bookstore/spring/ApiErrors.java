package com.bookstore.spring;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> badReq(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "error","validation",
                "details", e.getBindingResult().toString()
        ));
    }
}