package com.timmytime.predictoranalysisplayers.callable;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Callable;

public class Predict implements Callable {

    private static final Logger log = LoggerFactory.getLogger(Predict.class);

    private final TensorflowFacade tensorflowFacade;
    private final FantasyEventTypes fantasyEventTypes;
    private final UUID receipt;
    private final Boolean init;
    private final PlayerEventOutcomeCsv data;

    public Predict(
            TensorflowFacade tensorflowFacade,
            Boolean init,
            FantasyEventTypes fantasyEventTypes,
            PlayerEventOutcomeCsv data,
            UUID receipt
    ){
      this.tensorflowFacade = tensorflowFacade;
      this.fantasyEventTypes = fantasyEventTypes;
      this.receipt = receipt;
      this.data = data;
      this.init = init;
    }


    @Override
    public Object call() throws Exception {

        log.info("predicting {} {} ", data.getPlayer(), fantasyEventTypes.name());

        try {
            return tensorflowFacade.predict(fantasyEventTypes, data, init, receipt);
        }catch (Exception e){
            log.error("predict", e);
            return null;
        }

    }
}
