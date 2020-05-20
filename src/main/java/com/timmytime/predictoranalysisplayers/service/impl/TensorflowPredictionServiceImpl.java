package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.Predict;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.repo.mongo.FantasyOutcomeRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import com.timmytime.predictoranalysisplayers.service.TensorflowPredictionService;
import com.timmytime.predictoranalysisplayers.util.PredictionResultUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;

@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowPredictionServiceImpl.class);

    @Value("${training.receipt.timeout}")
    private Long timeout;

    private final TeamFacade teamFacade;
    private final PlayerFormRepo playerFormRepo;
    private final TensorflowFacade tensorflowFacade;
    private final ReceiptManager receiptManager;
    private final FantasyOutcomeRepo fantasyOutcomeRepo;


    private final PredictionResultUtils predictionResultUtils = new PredictionResultUtils();

    private Map<UUID, JSONObject> receiptMap = new HashMap<>();


    @Autowired
    public TensorflowPredictionServiceImpl(
             TeamFacade teamFacade,
             PlayerFormRepo playerFormRepo,
             TensorflowFacade tensorflowFacade,
             ReceiptManager receiptManager,
             FantasyOutcomeRepo fantasyOutcomeRepo
    ){
        this.teamFacade = teamFacade;
        this.playerFormRepo = playerFormRepo;
        this.tensorflowFacade = tensorflowFacade;
        this.receiptManager = receiptManager;
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
    }

    @Override
    public void predict(UUID receipt) {

        receiptMap.clear();

        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(competition ->
                        teamFacade.getTeamsByCompetition(competition.name().toLowerCase())
                                .stream()
                                .forEach(team ->
                                        playerFormRepo.findByTeam(team.getId())
                                                .stream()
                                                .forEach(player -> {
                                                    log.info("predicting {}", player.getLabel());
                                                   // tensorflowFacade.predict(player.getId(), null);

                                                    //build receipts the future will do prediction, and store to database
                                                })
                                )
                );

    }

    @Override
    public void predict(UUID player, String home, UUID opponent) {

        //this is for testing at present.....will use above call in future.
        receiptMap.clear();


        UUID goalsReceipt = receiptManager.generateId.get();
        UUID assistsReceipt = receiptManager.generateId.get();
        UUID minutesReceipt = receiptManager.generateId.get();
        UUID concededReceipt = receiptManager.generateId.get();
        UUID savesReceipt = receiptManager.generateId.get();
        UUID completed = receiptManager.generateId.get();


        List<Receipt> receipts = new ArrayList<>();

        receipts.add(create(player, new PlayerEventOutcomeCsv(player, opponent, home), FantasyEventTypes.GOALS, goalsReceipt, assistsReceipt));
        receipts.add(create(player, new PlayerEventOutcomeCsv(player, opponent, home), FantasyEventTypes.ASSISTS, assistsReceipt, minutesReceipt));
        receipts.add(create(player, new PlayerEventOutcomeCsv(player, opponent, home), FantasyEventTypes.MINUTES, minutesReceipt, concededReceipt));
        receipts.add(create(player, new PlayerEventOutcomeCsv(player, opponent, home), FantasyEventTypes.GOALS_CONCEDED, concededReceipt, savesReceipt));
        receipts.add(create(player, new PlayerEventOutcomeCsv(player, opponent, home), FantasyEventTypes.SAVES, savesReceipt, completed));
        receipts.add(
                receiptManager.generateReceipt.apply(
                        completed,
                        new ReceiptTask(new Completed(), null, timeout)
                       )
        );

        receiptManager.sendReceipt.accept(receipts.get(0));
    }

    @Override
    public void receiveReceipt(JSONObject data, UUID receipt) {

        this.receiptMap.get(receipt).put("result",predictionResultUtils.normalize.apply(data));
        //could write it away now (in reality)....given size of map
        receiptManager.receiptReceived.accept(receipt);
    }


    private Receipt create(UUID player, PlayerEventOutcomeCsv data, FantasyEventTypes fantasyEventTypes, UUID receiptId, UUID nextReceiptId){

        receiptMap.put(receiptId,
                new JSONObject()
                        .put("opponent", data.getOpponent())
                        .put("player", player)
                        .put("home", data.getHome())
                        .put("event", fantasyEventTypes.name())
        );


        return receiptManager.generateReceipt.apply(
                receiptId,
                new ReceiptTask(
                        new Predict(
                                tensorflowFacade,
                                player,
                                fantasyEventTypes,
                                data,
                                receiptId
                        ),
                        nextReceiptId, timeout)
        );

    };


    public class Completed implements Callable{

        @Override
        public Object call() {

            try {

                receiptMap.values().stream()
                        .forEach(result -> {
                            FantasyOutcome fantasyOutcome = new FantasyOutcome(receiptManager.generateId.get());

                            fantasyOutcome.setHome(result.getString("home"));
                            fantasyOutcome.setOpponent(UUID.fromString(result.get("opponent").toString()));
                            fantasyOutcome.setPlayerId(UUID.fromString(result.get("player").toString()));
                            fantasyOutcome.setFantasyEventType(FantasyEventTypes.valueOf(result.getString("event")));
                            fantasyOutcome.setPrediction(result.get("result").toString());

                            fantasyOutcomeRepo.save(fantasyOutcome);
                        });


            }catch (Exception e){
                log.error("completion", e);
            }

            return null;
        }
    }

}
