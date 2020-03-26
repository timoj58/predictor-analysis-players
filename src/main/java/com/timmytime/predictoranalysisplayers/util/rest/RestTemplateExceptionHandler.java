package com.timmytime.predictoranalysisplayers.util.rest;


import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.StringWriter;

public class RestTemplateExceptionHandler implements ResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateExceptionHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {

        return (clientHttpResponse.getStatusCode().series().equals(HttpStatus.Series.SERVER_ERROR)
                || clientHttpResponse.getStatusCode().series().equals(HttpStatus.Series.CLIENT_ERROR));

    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(clientHttpResponse.getBody(), stringWriter);
        log.error("Response error: {}", clientHttpResponse.getStatusCode());

        throw new RestTemplateException(clientHttpResponse.getStatusText(), clientHttpResponse.getStatusCode());

    }
}
