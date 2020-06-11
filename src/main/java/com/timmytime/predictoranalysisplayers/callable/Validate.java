package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class Validate implements Callable {

    private static final Logger log = LoggerFactory.getLogger(Validate.class);

    private final ValidationService validationService;
    private final UUID receipt;

    public Validate(
            ValidationService validationService,
            UUID receipt
    ){
        this.validationService = validationService;
        this.receipt = receipt;
    }

    @Override
    public Object call() throws Exception {
        log.info("calling validate");
        try {
            CompletableFuture.runAsync( () -> validationService.validate(receipt, Boolean.TRUE));
        }catch (Exception e){
            log.error("validate", e);
        }
        return null;
    }
}
