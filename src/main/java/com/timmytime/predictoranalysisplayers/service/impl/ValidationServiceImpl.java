package com.timmytime.predictoranalysisplayers.service.impl;

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
import com.timmytime.predictoranalysisplayers.util.PredictionResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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

    @Value("${prediction.cutoff}")
    private Integer predictionCutOff;

    @Autowired
    public ValidationServiceImpl(
            PlayerFacade playerFacade,
            PlayerFormRepo playerFormRepo,
            FantasyOutcomeRepo fantasyOutcomeRepo,
            ReceiptManager receiptManager
    ){
        this.playerFacade = playerFacade;
        this.playerFormRepo = playerFormRepo;
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
        this.receiptManager = receiptManager;
    }

    /*
     TODO tidy up and tests, this is just to get some things validated, for now not important
     but better than leaving lots of events open.
     */

    @Override
    public void validate(UUID receiptId) {
        Set<UUID> playersWhoDidNotPlay = new HashSet<>();

        Map<UUID, List<FantasyOutcome>>
        playerEvents = fantasyOutcomeRepo.findBySuccessNull()
                .stream()
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
                                                        /*
                                                          validation rules.

                                                          goals / assists / yellow / red card.

                                                          tuneable.  if the % is over cutoff, and occured, its success

                                                          conceded -> is the CEIL of prediction >= actual....to review. clean sheets hard
                                                          saves -> use CEIL method again.
                                                          minutes -> over under 60 is fine.

                                                          lots of work to do at present.  perhaps just skip validation for now.

                                                          fix rest, and come back to this at some point.

                                                         */
                                                       eventItems.stream()
                                                               .forEach(item -> {

                                                                   item.setSuccess(Boolean.FALSE);

                                                                   switch (item.getFantasyEventType()){
                                                                       case YELLOW_CARD:
                                                                       case RED_CARD:
                                                                       case GOALS:
                                                                       case ASSISTS:
                                                                           then.getStatMetrics().stream().filter(f -> f.getEventType().equals(item.getFantasyEventType()))
                                                                                   .findFirst().ifPresent(stat ->

                                                                                       predictionResultUtils.getScores.apply(item.getPrediction()).values().stream().findFirst()
                                                                                               .ifPresent(check -> {
                                                                                                   if(stat.getValue() > 0
                                                                                                           &&
                                                                                                           Double.valueOf(predictionCutOff) <= check){
                                                                                                       item.setSuccess(Boolean.TRUE);
                                                                                                   }
                                                                                               })
                                                                           );
                                                                           break;
                                                                       case MINUTES:
                                                                           if((then.getDuration() >= 60 && predictionResultUtils.getAverage.apply(item.getPrediction()) >= 60)
                                                                           || (then.getDuration() < 60 && predictionResultUtils.getAverage.apply(item.getPrediction()) < 60)){
                                                                               item.setSuccess(Boolean.TRUE);
                                                                           }
                                                                           break;
                                                                       case SAVES:
                                                                       case GOALS_CONCEDED:
                                                                           Integer predicted = (int)Math.ceil(predictionResultUtils.getAverage.apply(item.getPrediction()));
                                                                           then.getStatMetrics().stream().filter(f -> f.getEventType().equals(item.getFantasyEventType())).findFirst().ifPresent(
                                                                                   e -> {
                                                                                       if(predicted >= e.getValue()){
                                                                                           item.setSuccess(Boolean.TRUE);
                                                                                       }
                                                                                   }
                                                                           );
                                                                           break;
                                                                   }

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
}
