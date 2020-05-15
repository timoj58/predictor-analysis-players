package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Callable;

public class TrainPlayer implements Callable {

    private static final Logger log = LoggerFactory.getLogger(TrainPlayer.class);

    private final TensorflowFacade tensorflowFacade;

    private final FantasyEventTypes fantasyEventTypes;
    private final UUID player;
    private final UUID receipt;

    public TrainPlayer(TensorflowFacade tensorflowFacade, FantasyEventTypes fantasyEventTypes, UUID player, UUID receipt) {
        this.tensorflowFacade = tensorflowFacade;
        this.fantasyEventTypes = fantasyEventTypes;
        this.receipt = receipt;
        this.player = player;
    }

    @Override
    public Object call() {
        log.info("calling {} {}", player, fantasyEventTypes.name());
        try {
            return tensorflowFacade.train(player, fantasyEventTypes, receipt);
        } catch (Exception e) {
            log.error("training error", e);
            return null;
        }
    }
}
