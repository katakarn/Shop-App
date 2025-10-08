package com.shopapp.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND) // ไม่จำเป็นต้องใช้ @ResponseStatus เพราะ ControllerAdvice จัดการแล้ว
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}