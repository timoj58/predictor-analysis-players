package com.timmytime.predictoranalysisplayers.util.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateHelper {

    protected static final Logger log = LoggerFactory.getLogger(RestTemplateHelper.class);
    protected RestTemplate restTemplate;

    public RestTemplateHelper() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateExceptionHandler());
    }
}
