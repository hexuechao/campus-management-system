package com.example.campusmanagement.exception;

import com.example.campusmanagement.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(
            MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        Result<Void> result =
                new Result<>(400, message, null);

        return ResponseEntity
                .badRequest()
                .body(result);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(
            BusinessException e) {

        Result<Void> result =
                new Result<>(400, e.getMessage(), null);

        return ResponseEntity
                .badRequest()
                .body(result);
    }
}
