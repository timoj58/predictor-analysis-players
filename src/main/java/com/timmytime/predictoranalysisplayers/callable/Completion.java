package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;

import java.util.UUID;
import java.util.concurrent.Callable;

public class Completion implements Callable {

    private final ReceiptManager receiptManager;
    private final UUID receiptId;

    public Completion(
            ReceiptManager receiptManager,
            UUID receiptId
    ){
        this.receiptManager = receiptManager;
        this.receiptId = receiptId;
    }


    @Override
    public Object call() throws Exception {
        receiptManager.receiptReceived.accept(receiptId);
        return null;
    }
}
