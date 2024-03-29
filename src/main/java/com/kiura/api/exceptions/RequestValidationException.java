package com.kiura.api.exceptions;

import lombok.Getter;

@Getter
public class RequestValidationException extends RuntimeException{

    private Integer statusCode;

    public RequestValidationException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
