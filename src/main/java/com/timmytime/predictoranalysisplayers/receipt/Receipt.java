package com.timmytime.predictoranalysisplayers.receipt;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Receipt {

    private UUID id;
    private ReceiptTask onReceived;

    public Receipt(UUID id, ReceiptTask onReceived) {
        this.id = id;
        this.onReceived = onReceived;
    }

}
