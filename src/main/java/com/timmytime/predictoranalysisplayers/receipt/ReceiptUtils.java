package com.timmytime.predictoranalysisplayers.receipt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class ReceiptUtils {

    private static final Logger log = LoggerFactory.getLogger(ReceiptUtils.class);

    private Map<UUID, ReceiptSupervisor> receiptSupervisors = new HashMap<>();


    public synchronized void waitForTimeoutWithException(long timeout) throws InterruptedException {
        log.info("waiting...");
        wait(timeout);
        log.info("finished {} wait", timeout);
    }


    public Consumer<Receipt> receiptConsumer = receipt -> {
        receiptSupervisors.put(receipt.getId(), new ReceiptSupervisor(receipt));
        //start it
        receiptSupervisors.get(receipt.getId()).start();

        log.info("executing {}", receipt.getId());
        log.info("status {} for {}", receipt.getOnReceived().isDone(), receipt.getOnReceived().getReceipt());

        if (receipt.getOnReceived().getTimeout() != null || receipt.getOnReceived().getRunAndReset()) {
            log.info("starting {} no complete", receipt.getId());
            receipt.getOnReceived().runNoComplete(); //we may need to restart a receipt with a timeout
        } else {
            log.info("starting {} with complete", receipt.getId());
            receipt.getOnReceived().run();
        }

    };

    public Consumer<UUID> interuptReceiptSupervisor = receipt -> {
        receiptSupervisors.get(receipt).interupt();
        receiptSupervisors.remove(receipt);
    };


    //supervisor
    private class ReceiptSupervisor implements Runnable {

        private Receipt receipt;
        private Thread worker;

        public ReceiptSupervisor(Receipt receipt) {
            this.receipt = receipt;
            worker = new Thread(this);
        }

        public void start() {
            worker.start();
        }

        @Override
        public void run() {
            if (receipt.getOnReceived().getTimeout() != null) {
                log.info("receipt {} has timeout {}", receipt.getId(), receipt.getOnReceived().getTimeout());
                try {
                    waitForTimeoutWithException(receipt.getOnReceived().getTimeout());
                    log.info("timeout wait completed for {}", receipt.getId());
                    receiptConsumer.accept(receipt);
                } catch (InterruptedException e) {
                    log.info("interrupted {} no actions required ", receipt.getId());
                }
            }
        }

        public void interupt() {
            log.info("interrupting {}", receipt.getId());
            receipt.getOnReceived().completeTask();
            //dont bother interupting something that has no purpose.
            if (receipt.getOnReceived().getTimeout() != null) {
                worker.interrupt();
            }

            log.info("status {}", receipt.getOnReceived().isDone());
        }

    }


}
