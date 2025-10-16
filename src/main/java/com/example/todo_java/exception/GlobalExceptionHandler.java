package com.example.todo_java.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;

record ErrorBody(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}

@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    private ErrorBody body(HttpStatus status, String message, String path) {
        return new ErrorBody(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, path);
    }

    // 404 – recurso inexistente (findById orElseThrow / delete inexistente)
    @ExceptionHandler({EntityNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorBody> handleNotFound(Exception ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.NOT_FOUND;
        // mensagem neutra (não exponha ex.getMessage())
        return ResponseEntity.status(st).body(body(st, "Recurso não encontrado.", req.getRequestURI()));
    }

    // 400 – validação @Valid (DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Parâmetros inválidos.");
        return ResponseEntity.status(st).body(body(st, msg, req.getRequestURI()));
    }

    // 400 – JSON malformado / enum inválido (mensagem padronizada)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorBody> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        String msg = "Corpo da requisição inválido.";
        return ResponseEntity.status(st).body(body(st, msg, req.getRequestURI()));
    }


    // 400 – tipo errado no path/query (ex.: id não numérico)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorBody> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st).body(body(st, "Parâmetro inválido.", req.getRequestURI()));
    }

    // 405 – método não suportado
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorBody> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(st).body(body(st, "Método não suportado.", req.getRequestURI()));
    }

    // 500 – fallback controlado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(st).body(body(st, "Erro interno.", req.getRequestURI()));
    }

}
