package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.facade.PlayerFacade;
import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerAppearance;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.receipt.ReceiptManager;
import com.timmytime.predictoranalysisplayers.repo.mongo.FantasyOutcomeRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.response.data.Player;
import com.timmytime.predictoranalysisplayers.service.ValidationService;
import com.timmytime.predictoranalysisplayers.util.DateUtils;
import com.timmytime.predictoranalysisplayers.util.LambdaUtils;
import com.timmytime.predictoranalysisplayers.util.PredictionResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.groupingBy;

@Service("validationService")
public class ValidationServiceImpl implements ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);

    private final PlayerFacade playerFacade;
    private final PlayerFormRepo playerFormRepo;
    private final FantasyOutcomeRepo fantasyOutcomeRepo;
    private final ReceiptManager receiptManager;

    private final DateUtils dateUtils = new DateUtils();
    private final PredictionResultUtils predictionResultUtils = new PredictionResultUtils();
    private final LambdaUtils lambdaUtils;

    @Value("${prediction.cutoff}")
    private Integer predictionCutOff;

    @Autowired
    public ValidationServiceImpl(
            PlayerFacade playerFacade,
            PlayerFormRepo playerFormRepo,
            FantasyOutcomeRepo fantasyOutcomeRepo,
            LambdaUtils lambdaUtils,
            ReceiptManager receiptManager
    ){
        this.playerFacade = playerFacade;
        this.playerFormRepo = playerFormRepo;
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
        this.lambdaUtils = lambdaUtils;
        this.receiptManager = receiptManager;
    }

    /*
     TODO tidy up and tests, this is just to get some things validated, for now not important
     but better than leaving lots of events open.

     probably can use this, to show the number of successful ratings, success is true.  given cutoff.

     however we also include false below.  so need to refactor this in.  and rebuild.

     */

    @Override
    public void validate(UUID receiptId, Boolean startMachineLearning) {

        //not sure of this -> ie should a prediction under the cutoff, be classed as a success if it fails?
        //overall.  not sure of the value of this yet.  leave as is, can alter and re-validate all records under new rules, in future.
        log.info("starting validation");
        if(startMachineLearning) {
            lambdaUtils.startMachineLearning();
        }

        Set<UUID> playersWhoDidNotPlay = new HashSet<>();

        Map<UUID, List<FantasyOutcome>>
        playerEvents = fantasyOutcomeRepo.findBySuccessNull()
                .stream()
                .filter(f -> f.getEventDate().isBefore(LocalDateTime.now()))
                .collect(groupingBy(FantasyOutcome::getPlayerId));

       playerEvents.keySet().stream()
               .forEach(player -> {
                   List<FantasyOutcome> events = playerEvents.get(player);

                   playerFormRepo.findById(player).ifPresentOrElse(
                           playerForm -> {
                               //grab the event firstly (need event date too it seems)...this will be present eventually.  so assume its here.

                               Map<UUID, List<FantasyOutcome>> event = events.stream().collect(groupingBy(FantasyOutcome::getOpponent));

                               event.keySet().stream()
                                       .forEach(key -> {

                                           List<FantasyOutcome> eventItems = event.get(key);

                                           Optional<PlayerAppearance> playerAppearance
                                                   = playerForm.getPlayerAppearances()
                                                   .stream()
                                                   .filter(f -> f.getOpponent().equals(key)
                                                           &&
                                                           //time zone issue...need to ignore time when matching.  day is sufficient.
                                                           dateUtils.convertToLocalDate.apply(f.getDate()).equals(eventItems.get(0).getEventDate().toLocalDate()))
                                                   .findFirst();

                                           playerAppearance.ifPresentOrElse(then -> {
                                                       log.info("validating {}", playerForm.getLabel());
                                                       log.info("matched appearance {} vs {}", then.getHomeTeam(), then.getAwayTeam());

                                                       //we only have events where they exist.  so...
                                                       eventItems.stream()
                                                               .forEach(item -> {
                                                                   item.setSuccess(validateEvent(item, then));
                                                                  fantasyOutcomeRepo.save(item);
                                                               });

                                                   }
                                                   , () -> {
                                                       log.info("player {} did not play in game, removing events", playerForm.getLabel());
                                                       playersWhoDidNotPlay.add(player);
                                                   });


                                       });

                           }, () -> {
                               log.info("player {} not on file anymore", player);
                               //check last appearance date > 2 years.
                               playerFacade.findById(player).ifPresent(
                                       then -> {
                                           if(then.getLastAppearance().isBefore(LocalDate.now().minusYears(2L))){
                                               log.info("player {} is out of scope", then.getLabel());
                                               fantasyOutcomeRepo.findByPlayerIdAndSuccessNull(player)
                                                       .stream()
                                                       .forEach(record -> fantasyOutcomeRepo.deleteById(record.getId()));
                                           }
                                       }
                               );
                           });


               });

       //remove any records of players who did not play
        log.info("removing events where player did not play");
        playersWhoDidNotPlay.stream().forEach(
                player ->
        fantasyOutcomeRepo.findByPlayerIdAndSuccessNull(player)
                .stream()
                .forEach(record -> fantasyOutcomeRepo.deleteById(record.getId())));

         receiptManager.receiptReceived.accept(receiptId); //fire off completion.  (will fail for now).
    }


    private Boolean validateEvent(FantasyOutcome fantasyOutcome, PlayerAppearance playerAppearance){

        Map<String, Boolean> result = new HashMap<>();
        result.put("result", Boolean.FALSE);


        switch (fantasyOutcome.getFantasyEventType()){
            case YELLOW_CARD:
            case RED_CARD:
            case GOALS:
            case ASSISTS:
                playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(fantasyOutcome.getFantasyEventType()))
                        .findFirst().ifPresentOrElse(stat ->

                        predictionResultUtils.getScores.apply(fantasyOutcome.getPrediction()).values().stream().findFirst()
                                .ifPresent(check -> {
                                    if((stat.getValue().intValue() > 0
                                            &&
                                            Double.valueOf(predictionCutOff) <= check)
                                    || (stat.getValue().intValue() == 0
                                            &&
                                            check < Double.valueOf(predictionCutOff))){
                                        result.put("result", Boolean.TRUE);
                                    }
                                })
                ,() ->
                            predictionResultUtils.getScores.apply(fantasyOutcome.getPrediction()).values().stream().findFirst()
                                    .ifPresent(check -> {
                                        if(check < Double.valueOf(predictionCutOff)){  //we are true in this instance, as it did not occur
                                            result.put("result", Boolean.TRUE);
                                        }
                                    })
                        );
                break;
            case MINUTES:
                if((playerAppearance.getDuration() >= 60 && predictionResultUtils.getAverage.apply(fantasyOutcome.getPrediction()) >= 60)
                        || (playerAppearance.getDuration() < 60 && predictionResultUtils.getAverage.apply(fantasyOutcome.getPrediction()) < 60)){
                    result.put("result", Boolean.TRUE);
                }
                break;
            case SAVES:
            case GOALS_CONCEDED:
                Integer predicted = (int)Math.ceil(predictionResultUtils.getAverage.apply(fantasyOutcome.getPrediction()));
                playerAppearance.getStatMetrics().stream().filter(f -> f.getEventType().equals(fantasyOutcome.getFantasyEventType())).findFirst().ifPresent(
                        e -> {
                            if(predicted >= e.getValue()){
                               result.put("result", Boolean.TRUE);
                            }
                        }
                );
                break;
        }

        return result.get("result");

    }
}
