package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.callable.Predict;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.EventFacade;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.repo.mongo.FantasyOutcomeRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import com.timmytime.predictoranalysisplayers.service.TensorflowPredictionService;
import com.timmytime.predictoranalysisplayers.util.LambdaUtils;
import com.timmytime.predictoranalysisplayers.util.PredictionResultUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowPredictionServiceImpl.class);

    @Value("${training.receipt.timeout}")
    private Long timeout;

    private final TeamFacade teamFacade;
    private final EventFacade eventFacade;
    private final PlayerFormRepo playerFormRepo;
    private final TensorflowFacade tensorflowFacade;
    private final ReceiptManager receiptManager;
    private final FantasyOutcomeRepo fantasyOutcomeRepo;


    private final PredictionResultUtils predictionResultUtils = new PredictionResultUtils();

    private Map<UUID, JSONObject> receiptMap = new HashMap<>();


    @Autowired
    public TensorflowPredictionServiceImpl(
             TeamFacade teamFacade,
             EventFacade eventFacade,
             PlayerFormRepo playerFormRepo,
             TensorflowFacade tensorflowFacade,
             ReceiptManager receiptManager,
             FantasyOutcomeRepo fantasyOutcomeRepo
    ){
        this.teamFacade = teamFacade;
        this.eventFacade = eventFacade;
        this.playerFormRepo = playerFormRepo;
        this.tensorflowFacade = tensorflowFacade;
        this.receiptManager = receiptManager;
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
    }

    @Override
    public void predict(UUID receipt) {

        tensorflowFacade.init("goals");
        tensorflowFacade.init("assists");
        tensorflowFacade.init("yellow");
        tensorflowFacade.init("red");
        tensorflowFacade.init("conceded");
        tensorflowFacade.init("minutes");
        tensorflowFacade.init("saves");

        receiptMap.clear();
        List<Receipt> receipts = new ArrayList<>();

        Map<Boolean, UUID> receiptIds = new HashMap<>();
        receiptIds.put(Boolean.TRUE, receiptManager.generateId.get());
        receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());

        /*
          this is all wrong now.  so

          driven from events only....
         */
        Arrays.asList(ApplicableFantasyLeagues.values())
                .stream()
                .forEach(competition ->
                        eventFacade.upcomingEvents(competition.getCountry(), competition.name().toLowerCase())
                        .stream()
                        .forEach(response ->
                                response.getUpcomingEventResponses()
                        .stream()
                        .forEach(event -> {

                                log.info("predicting event for {} vs {}", event.getHome().getLabel(), event.getAway().getLabel());

                                List<PlayerForm> homePlayers = playerFormRepo.findByTeam(event.getHome().getId())
                                        .stream()
                                        .filter(f -> f.getLastAppearance() > LocalDate.now().minusMonths(6).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
                                        .collect(Collectors.toList());
                                List<PlayerForm> awayPlayers = playerFormRepo.findByTeam(event.getAway().getId())
                                        .stream()
                                        .filter(f -> f.getLastAppearance() > LocalDate.now().minusMonths(6).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
                                        .collect(Collectors.toList());

                            Arrays.asList(FantasyEventTypes.values())
                                        .stream()
                                        .filter(f -> f.getPredict())
                                        .forEach(fantasyEventTypes -> {
                                            homePlayers.stream()
                                                    .forEach(homePlayer -> {
                                                        if (homePlayer.isGoalKeeper() && fantasyEventTypes.equals(FantasyEventTypes.SAVES)
                                                                || !fantasyEventTypes.equals(FantasyEventTypes.SAVES)) {
                                                            receipts.add(
                                                                    create(homePlayer.getId(), new PlayerEventOutcomeCsv(
                                                                                    homePlayer.getId(),
                                                                                    event.getAway().getId(),
                                                                                    "home"),
                                                                            fantasyEventTypes,
                                                                            Boolean.FALSE,
                                                                            event.getEventDate().getTime(),
                                                                            receiptIds.get(Boolean.TRUE),
                                                                            receiptIds.get(Boolean.FALSE))
                                                            );
                                                            receiptIds.put(Boolean.TRUE, receiptIds.get(Boolean.FALSE));
                                                            receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());
                                                        }
                                                    });

                                            awayPlayers.stream()
                                                    .forEach(awayPlayer -> {
                                                        if (awayPlayer.isGoalKeeper() && fantasyEventTypes.equals(FantasyEventTypes.SAVES)
                                                                || !fantasyEventTypes.equals(FantasyEventTypes.SAVES)) {
                                                            receipts.add(
                                                                    create(awayPlayer.getId(), new PlayerEventOutcomeCsv(
                                                                                    awayPlayer.getId(),
                                                                                    event.getHome().getId(),
                                                                                    "away"),
                                                                            fantasyEventTypes,
                                                                            Boolean.FALSE,
                                                                            event.getEventDate().getTime(),
                                                                            receiptIds.get(Boolean.TRUE),
                                                                            receiptIds.get(Boolean.FALSE))
                                                            );
                                                            receiptIds.put(Boolean.TRUE, receiptIds.get(Boolean.FALSE));
                                                            receiptIds.put(Boolean.FALSE, receiptManager.generateId.get());
                                                        }
                                                    });
                                        });

                            }))
                );


        receipts.add(
                receiptManager.generateReceipt.apply(
                        receiptIds.get(Boolean.TRUE),
                        new ReceiptTask(new SaveEvents(receipt), receipt)
                )
        );


        if(!receipts.isEmpty()){
            receiptManager.sendReceipt.accept(receipts.get(0));
        }

    }


    @Override
    public void receiveReceipt(JSONObject data, UUID receipt) {

        this.receiptMap.get(receipt).put("result", predictionResultUtils.normalize.apply(data));
        receiptManager.receiptReceived.accept(receipt);
    }


    private Receipt create(UUID player, PlayerEventOutcomeCsv data, FantasyEventTypes fantasyEventTypes, Boolean init, Long date, UUID receiptId, UUID nextReceiptId){

        receiptMap.put(receiptId,
                new JSONObject()
                        .put("opponent", data.getOpponent())
                        .put("player", player)
                        .put("home", data.getHome())
                        .put("date", date)
                        .put("event", fantasyEventTypes.name())
        );


        return receiptManager.generateReceipt.apply(
                receiptId,
                new ReceiptTask(
                        new Predict(
                                tensorflowFacade,
                                init,
                                fantasyEventTypes,
                                data,
                                receiptId
                        ),
                        nextReceiptId, timeout)
        );

    };


    public class SaveEvents implements Callable{

        private final UUID receipt;

        public SaveEvents(
                UUID receipt
        ){
            this.receipt = receipt;
        }

        @Override
        public Object call() {

            try {

                //TODO this needs to be done for every receipt, rather than wait, given thousands of receipts to process.
                log.info("saving fantasy predictions");

                receiptMap.values().stream()
                        .forEach(result -> {
                            FantasyOutcome fantasyOutcome = new FantasyOutcome(receiptManager.generateId.get());

                            fantasyOutcome.setHome(result.getString("home"));
                            fantasyOutcome.setOpponent(UUID.fromString(result.get("opponent").toString()));
                            fantasyOutcome.setPlayerId(UUID.fromString(result.get("player").toString()));
                            fantasyOutcome.setFantasyEventType(FantasyEventTypes.valueOf(result.getString("event")));
                            fantasyOutcome.setPrediction(result.toString());
                            fantasyOutcome.setEventDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(result.getLong("date")), ZoneId.systemDefault()));

                            fantasyOutcomeRepo.save(fantasyOutcome);
                        });


            }catch (Exception e){
                log.error("completion", e);
            }


            log.info("clearing machine models");

            tensorflowFacade.destroy("goals");
            tensorflowFacade.destroy("saves");
            tensorflowFacade.destroy("assists");
            tensorflowFacade.destroy("minutes");
            tensorflowFacade.destroy("conceded");
            tensorflowFacade.destroy("yellow");
            tensorflowFacade.destroy("red");

            //ok forgot to fire a new receipt lol.....ffs

            receiptManager.receiptReceived.accept(receipt);

            return null;
        }
    }

}
