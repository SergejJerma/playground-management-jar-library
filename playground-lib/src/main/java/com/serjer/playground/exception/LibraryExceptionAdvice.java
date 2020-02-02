package com.serjer.playground.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class LibraryExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handleError(HttpServletRequest req, Exception ex) {
        LOGGER.error("Request {} raised {} ", req.getRequestURL(), ex);
    }
}