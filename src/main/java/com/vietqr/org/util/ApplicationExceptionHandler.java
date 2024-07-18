package com.vietqr.org.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.vietqr.org.dto.ResponseMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    // exception handler for validate request body
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessageDTO> handleInvalidArgument(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessageDTO("FAILED", "E46"));
    }

    // exception handler for validate entity
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ResponseMessageDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessageDTO("FAILED", "E46"));
    }

    // exception handler for validate field type
    // Example: int field but request param is string (abc)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseMessageDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessageDTO("FAILED", "E46"));
    }

    // exception handler for validate json format
    // Example: { "abc": "abc" } -> { "abc": "abc }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessageDTO> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessageDTO("FAILED", "E46"));
    }

    // exception handler for parse json error
    // Example: { "abc": 1 } -> { "abc": "abc" }
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ResponseMessageDTO> handleJsonParseException(JsonParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessageDTO("FAILED", "E46"));
    }

    // exception handler for validate other request body
    // Example: request param, request header
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseMessageDTO> handleBindException(BindException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessageDTO("FAILED", "E46"));
    }
}
