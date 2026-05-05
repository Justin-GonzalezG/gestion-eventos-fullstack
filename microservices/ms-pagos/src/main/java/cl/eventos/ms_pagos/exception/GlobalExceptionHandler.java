package cl.eventos.ms_pagos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntime(RuntimeException ex) {
        
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("timestamp", LocalDateTime.now());
        mapa.put("error", "Error en el proceso de pago");
        mapa.put("detalle", ex.getMessage());

        return new ResponseEntity<>(mapa, HttpStatus.NOT_FOUND);
    }


}