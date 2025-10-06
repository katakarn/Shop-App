package com.shopapp.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // ทำให้คลาสนี้สามารถจัดการ Exception จาก Controller ทั้งหมดได้
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // วนลูปดึงข้อความ Error จากทุก Field ที่ล้มเหลว
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // ส่งกลับเป็น JSON พร้อม HTTP Status 400 Bad Request
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}