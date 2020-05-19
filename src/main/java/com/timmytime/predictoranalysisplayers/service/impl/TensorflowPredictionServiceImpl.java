package com.timmytime.predictoranalysisplayers.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoranalysisplayers.callable.Predict;
import com.timmytime.predictoranalysisplayers.callable.Train;
import com.timmytime.predictoranalysisplayers.enumerator.ApplicableFantasyLeagues;
import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.TeamFacade;
import com.timmytime.predictoranalysisplayers.facade.TensorflowFacade;
import com.timmytime.predictoranalysisplayers.model.redis.FantasyResponse;
import com.timmytime.predictoranalysisplayers.receipt.Receipt;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptTask;
import com.timmytime.predictoranalysisplayers.repo.redis.FantasyResponseRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import com.timmytime.predictoranalysisplayers.response.data.Prediction;
import com.timmytime.predictoranalysisplayers.service.TensorflowPredictionService;
import com.timmytime.predictoranalysisplayers.util.PredictionResultUtils;
import org.bouncycastle.jcajce.provider.digest.SHA512;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.ldap.HasControls;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("tensorflowPredictionService")
public class TensorflowPredictionServiceImpl implements TensorflowPredictionService {

    private static final Logger log = LoggerFactory.getLogger(TensorflowPredictionServiceImpl.class);

    @Value("${training.receipt.timeout}")
    private Long timeout;

    private final TeamFacade teamFacade;
    private final PlayerFormRepo playerFormRepo;
    private final TensorflowFacade tensorflowFacade;
    private final ReceiptManager receiptManager;
    private final FantasyResponseRepo fantasyResponseRepo;

    private Map<UUID,FantasyEventTypes> lookup = new HashMap<>();
    private Map<FantasyEventTypes, String> results = new HashMap<>();
    private FantasyResponse fantasyResponse = new FantasyResponse();

    private final PredictionResultUtils predictionResultUtils = new PredictionResultUtils();



    @Autowired
    public TensorflowPredictionServiceImpl(
             TeamFacade teamFacade,
             PlayerFormRepo playerFormRepo,
             TensorflowFacade tensorflowFacade,
             ReceiptManager receiptManager,
             FantasyResponseRepo fantasyResponseRepo
    ){
        this.teamFacade = teamFacade;
        this.playerFormRepo = playerFormRepo;
        this.tensorflowFacade = tensorflowFacade;
        this.receiptManager = receiptManager;
        this.fantasyResponseRepo = fantasyResponseRepo;
    }

    @Override
    public void predict(UUID receipt) {
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
    public void predict(UUID player, FantasyEventTypes fantasyEventTypes, String home, UUID opponent) {

        PlayerEventOutcomeCsv playerEventOutcomeCsv = new PlayerEventOutcomeCsv();

        log.info("predicting {}", player.toString());

        playerEventOutcomeCsv.setHome(home);
        playerEventOutcomeCsv.setOpponent(opponent);
        playerEventOutcomeCsv.setPlayer(player);

        tensorflowFacade.predict(player, fantasyEventTypes, playerEventOutcomeCsv, UUID.randomUUID());
    }

    @Override
    public void predict(UUID player, String home, UUID opponent) {

        fantasyResponse.setId(player);
        fantasyResponse.setLabel(playerFormRepo.findById(player).get().getLabel());

        lookup.clear();
        results.clear();

        UUID goalsReceipt = receiptManager.generateId.get();
        UUID assistsReceipt = receiptManager.generateId.get();
        UUID minutesReceipt = receiptManager.generateId.get();
        UUID concededReceipt = receiptManager.generateId.get();
        UUID savesReceipt = receiptManager.generateId.get();
        UUID completed = receiptManager.generateId.get();


        lookup.put(goalsReceipt, FantasyEventTypes.GOALS);
        lookup.put(assistsReceipt, FantasyEventTypes.ASSISTS);
        lookup.put(minutesReceipt, FantasyEventTypes.MINUTES);
        lookup.put(concededReceipt, FantasyEventTypes.GOALS_CONCEDED);
        lookup.put(savesReceipt, FantasyEventTypes.SAVES);



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
        this.results.put(lookup.get(receipt), normalize.apply(data).toString());
        receiptManager.receiptReceived.accept(receipt);
    }

    @Override
    public FantasyResponse getFantasyResponse(UUID player) {
        return fantasyResponseRepo.findById(player).get();
    }

    private Receipt create(UUID player, PlayerEventOutcomeCsv data, FantasyEventTypes fantasyEventTypes, UUID receiptId, UUID nextReceiptId){


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

    public Function<JSONObject, List<Prediction>> normalize = result -> {


        //get our keys.
        Map<String, List<Double>> byIndex = new HashMap<>();


        Iterator<String> keys = result.keys();
        while (keys.hasNext()) {
            String key = keys.next();

            String keyLabel = result.getJSONObject(key).get("label").toString();
            if (!byIndex.containsKey(keyLabel)) {
                byIndex.put(keyLabel, new ArrayList<>());
            }

            byIndex.get(keyLabel).add(Double.valueOf(result.getJSONObject(key).get("score").toString()));
        }


        List<Prediction> normalized = new ArrayList<>();

        byIndex.keySet().stream().forEach(
                key -> normalized.add(
                        new Prediction(key,
                                byIndex.get(key)
                                        .stream()
                                        .mapToDouble(m -> m).average().getAsDouble()))
        );

        return normalized
                .stream()
                .sorted((o1, o2) -> o2.getScore().compareTo(o1.getScore()))
                .collect(Collectors.toList());
    };

    public class Completed implements Callable{

        @Override
        public Object call() {

            try {

                log.info("saving record {}", fantasyResponse.getId());

                fantasyResponse.setMinutes(predictionResultUtils.getAverage.apply(
                        results.get(FantasyEventTypes.MINUTES)
                ));

                fantasyResponse.setAssists(predictionResultUtils.getScores.apply(
                        results.get(FantasyEventTypes.ASSISTS)
                ));

                fantasyResponse.setGoals(predictionResultUtils.getScores.apply(
                        results.get(FantasyEventTypes.GOALS)
                ));

                fantasyResponse.setSaves(predictionResultUtils.getAverage.apply(
                        results.get(FantasyEventTypes.SAVES)
                ));

                fantasyResponse.setConceded(predictionResultUtils.getAverage.apply(
                        results.get(FantasyEventTypes.GOALS_CONCEDED)
                ));


                fantasyResponseRepo.save(fantasyResponse);

            }catch (Exception e){
                log.error("completion", e);
            }

            return null;
        }
    }

}
