package com.timmytime.predictoranalysisplayers.receipt;

import com.google.common.base.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReceiptManager {

    private static final Logger log = LoggerFactory.getLogger(ReceiptManager.class);

    private final ReceiptUtils receiptUtils;

    private ReceiptUtils getReceiptUtils(){return receiptUtils;}

    private List<Receipt> sent = new ArrayList<>();
    private List<UUID> received = new ArrayList<>();
    private List<Receipt> generated = new ArrayList<>();


    /*
    for testing
     */
    public Consumer<List<Receipt>> print = receipts -> {

        log.info("receipts total {}", receipts.size());

        receipts.stream()
                .forEach(receipt -> {
                    log.info("receipt {} followed by ? ",
                            receipt.getId());
                    receipts.stream()
                            .filter(f -> f.getOnReceived().getReceipt() != null)
                            .filter(f -> f.getOnReceived().getReceipt().equals(f.getId())).findFirst().ifPresentOrElse(
                            then -> log.info("followed by {}", then.getId()), () -> log.info("its null")
                    );

                });

    };

    @Autowired
    public ReceiptManager(ReceiptUtils receiptUtils) {
        this.receiptUtils = receiptUtils;
    }

    public void linkCompletionReceipt(List<Receipt> receipts, Receipt completionReceipt) {
        //now link the final event to the completion event.
        receipts
                .stream()
                .filter(f -> f.getOnReceived().getReceipt() == null)
                .findFirst()
                .ifPresent(then -> then.getOnReceived().setReceipt(completionReceipt.getId()));

        receipts.add(completionReceipt);
    }

    public Function<List<Receipt>, Boolean> finished = receipts ->
            received.containsAll(receipts.stream().map(m -> m.getId()).collect(Collectors.toList()));

    //different to pending, as some receipts may not have been sent (hence not pending) but we need to confirm this
    public Function<List<UUID>, Boolean> receiptsSentAndReceived = receipts ->
            sent.stream().map(m -> m.getId()).collect(Collectors.toList()).containsAll(receipts)
                    && received.containsAll(receipts);

    public Supplier<List<Receipt>> pendingReceipts = () ->
            sent.stream().filter(f -> !received.contains(f.getId())).collect(Collectors.toList());

    //for our first job
    public Consumer<Receipt> sendReceipt = receipt -> {
        sent.add(receipt);
        getReceiptUtils().receiptConsumer.accept(receipt);
    };

    Function<UUID, Receipt> receiptActions = id -> {
        received.add(id);
        getReceiptUtils().interuptReceiptSupervisor.accept(id);

        return sent.stream().filter(f -> f.getId().equals(id)).findFirst().get();
    };


    Consumer<Receipt> nextReceiptActions = nextReceipt -> {
        if (!sent.contains(nextReceipt)) {
            sent.add(nextReceipt);
        }
        getReceiptUtils().receiptConsumer.accept(nextReceipt);
    };

    public Consumer<UUID> receiptReceived = id -> {
        log.info("receipt {} received", id);
        Receipt receipt = receiptActions.apply(id);
        //have we got a next action linked?
        if (receipt.getOnReceived().getReceipt() != null) {
            nextReceiptActions.accept(
                    generated
                            .stream()
                            .filter(f -> f.getId().equals(receipt.getOnReceived().getReceipt()))
                            .findFirst()
                            .get()
            );
        }
    };

    public Supplier<UUID> generateId = () -> UUID.randomUUID();


    public BiFunction<UUID, ReceiptTask, Receipt> generateReceipt = (id, onReceived) -> {
        Receipt receipt = new Receipt(id, onReceived);
        generated.add(receipt);
        log.info("receipt {} generated", receipt.getId());

        return receipt;
    };

    public void clear() {
        received.clear();
        sent.clear();
        generated.clear();
    }

}
