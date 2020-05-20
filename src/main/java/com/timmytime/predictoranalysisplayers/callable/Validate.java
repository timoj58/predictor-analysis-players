package com.timmytime.predictoranalysisplayers.callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class Validate implements Callable {

    private static final Logger log = LoggerFactory.getLogger(Validate.class);

    @Override
    public Object call() throws Exception {
        log.info("calling validate");
        return null;
    }
}
