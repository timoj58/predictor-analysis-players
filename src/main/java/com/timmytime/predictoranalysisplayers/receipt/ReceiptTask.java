package com.timmytime.predictoranalysisplayers.receipt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ReceiptTask extends FutureTask {

    private static final Logger log = LoggerFactory.getLogger(ReceiptTask.class);

    private UUID receipt;
    private Long timeout;
    private Boolean runAndReset = Boolean.FALSE;


    public ReceiptTask(Callable callable, UUID receipt) {
        super(callable);
        this.receipt = receipt;
        this.timeout = null;
    }

    public ReceiptTask(Callable callable, UUID receipt, Long timeout) {
        super(callable);
        this.receipt = receipt;
        this.timeout = timeout;
    }

    public ReceiptTask(Callable callable, UUID receipt, Boolean runAndReset) {
        super(callable);
        this.receipt = receipt;
        this.runAndReset = runAndReset;
    }


    public Boolean getRunAndReset() {
        return runAndReset;
    }

    public void setRunAndReset(Boolean runAndReset) {
        this.runAndReset = runAndReset;
    }

    public void runNoComplete() {
        this.runAndReset();
        runAndReset = Boolean.FALSE; //now normal execution next time.
    }

    public void completeTask() {
        this.cancel(Boolean.TRUE);
    }


    public UUID getReceipt() {
        return receipt;
    }

    public void setReceipt(UUID receipt) {
        this.receipt = receipt;
    }


    public Long getTimeout() {
        return timeout;
    }

}
