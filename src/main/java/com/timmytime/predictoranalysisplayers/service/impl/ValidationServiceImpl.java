package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerAppearance;
import com.timmytime.predictoranalysisplayers.model.redis.PlayerForm;
import com.timmytime.predictoranalysisplayers.repo.mongo.FantasyOutcomeRepo;
import com.timmytime.predictoranalysisplayers.repo.redis.PlayerFormRepo;
import com.timmytime.predictoranalysisplayers.service.ValidationService;
import com.timmytime.predictoranalysisplayers.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

@Service("validationService")
public class ValidationServiceImpl implements ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);

    private final PlayerFormRepo playerFormRepo;
    private final FantasyOutcomeRepo fantasyOutcomeRepo;

    private final DateUtils dateUtils = new DateUtils();

    @Autowired
    public ValidationServiceImpl(
            PlayerFormRepo playerFormRepo,
            FantasyOutcomeRepo fantasyOutcomeRepo
    ){
        this.playerFormRepo = playerFormRepo;
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
    }

    @Override
    public void validate(UUID receiptId) {

        //again this is wrong.  needs to be receipt driven.  i think for now.  just turn off validation.
        //its the final case.

       Map<UUID, List<FantasyOutcome>>
        playerEvents = fantasyOutcomeRepo.findBySuccessNull()
                .stream()
                .collect(groupingBy(FantasyOutcome::getPlayerId));

       playerEvents.keySet().stream()
               .forEach(player -> {
                   List<FantasyOutcome> events = playerEvents.get(player);
                   PlayerForm playerForm = playerFormRepo.findById(player).get();
                   log.info("validating {}", playerForm.getLabel());
                   //grab the event firstly (need event date too it seems)...this will be present eventually.  so assume its here.

                   Map<UUID, List<FantasyOutcome>> event = events.stream().collect(groupingBy(FantasyOutcome::getOpponent));

                   event.keySet().stream()
                           .forEach(key -> {

                               List<FantasyOutcome> eventItems = event.get(key);

                               PlayerAppearance playerAppearance
                                       = playerForm.getPlayerAppearances()
                                       .stream()
                                       .filter(f -> f.getOpponent().equals(key)
                                               &&
                                               //TODO local date will not work in this instance...needs to be done at the end i suspect.
                                               f.getDate().equals(dateUtils.convert.apply(eventItems.get(0).getEventDate())))
                                       .findFirst().get();

                               //now we need to match the stats to it

                               //saves...if applicable
                               //goals (how to do this?)
                               //minutes (over or under 60 is fine)
                               //conceded (success if under ceil of average)
                               //assists (how to do this?)

                           });




               });

    }
}
