package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Callable;

public class Train implements Callable {

    private static final Logger log = LoggerFactory.getLogger(Train.class);

    private final TensorflowFacade tensorflowFacade;

    private final FantasyEventTypes fantasyEventTypes;
    private final UUID receipt;

    public Train(TensorflowFacade tensorflowFacade, FantasyEventTypes fantasyEventTypes, UUID receipt) {
        this.tensorflowFacade = tensorflowFacade;
        this.fantasyEventTypes = fantasyEventTypes;
        this.receipt = receipt;
    }

    @Override
    public Object call() {
        log.info("calling {}", fantasyEventTypes.name());
        try {
            return tensorflowFacade.train(fantasyEventTypes, receipt);
        } catch (Exception e) {
            log.error("training error", e);
            return null;
        }
    }
}
