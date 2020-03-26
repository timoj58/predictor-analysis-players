package com.timmytime.predictoranalysisplayers.util.rest;

import org.springframework.http.HttpStatus;

public class RestTemplateException extends RuntimeException {
    private HttpStatus httpStatus;

    public RestTemplateException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
