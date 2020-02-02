package com.serjer.playground.exception;
public class NotFoundException extends RuntimeException{

    public NotFoundException(String message) {
        super(message);
    }
}