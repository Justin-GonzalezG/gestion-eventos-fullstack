package com.eventos.cl.ms_usuarios.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

//Atrapa los errores globalmente en todo el microservicio
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Atrapar errores de la etiqueta @Valid (Cuando el Postman envia campos vacios.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationErrors(MethodArgumentNotValidException ex){
        
        Map<String, String> errores = new LinkedHashMap<>();
        
        // Recorremos cada error de validacion y lo metemos a un diccionario ordenado
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errores); // 400 Bad Request
    }

    // Atrapar errores de logica de negocio (ej. "Usuario no encontrado".)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRuntimeException(RuntimeException ex){
        
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error); // 400 Bad Request
    }
}
