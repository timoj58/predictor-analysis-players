package com.timmytime.predictoranalysisplayers.transformer;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.response.FantasyResponse;
import com.timmytime.predictoranalysisplayers.util.PredictionResultUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class FantasyResponseTransformer {

    private final PredictionResultUtils predictionResultUtils = new PredictionResultUtils();

    private BiFunction<List<FantasyOutcome>, Predicate<FantasyOutcome>, String> filter = (outcomes, predicate) ->
            outcomes.stream()
                    .filter(predicate)
                    .findFirst()
                    .orElseThrow()
                    .getPrediction();

    public Function<List<FantasyOutcome>, FantasyResponse> transform = outcomes -> {
        FantasyResponse fantasyResponse = new FantasyResponse();


        fantasyResponse.setMinutes(predictionResultUtils.getAverage.apply(
                        filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.MINUTES))
                        ));


        fantasyResponse.setAssists(predictionResultUtils.getScores.apply(
                filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.ASSISTS))
        ));

        fantasyResponse.setGoals(predictionResultUtils.getScores.apply(
                filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.GOALS))
        ));

        try {
            fantasyResponse.setSaves(predictionResultUtils.getAverage.apply(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.SAVES))
            ));
        }catch (Exception e){
            fantasyResponse.setSaves(null);
        }

        fantasyResponse.setConceded(predictionResultUtils.getAverage.apply(
                filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.GOALS_CONCEDED))
        ));

        fantasyResponse.setRedCards(predictionResultUtils.getScores.apply(
                filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.RED_CARD))
        ));

        fantasyResponse.setYellowCards(predictionResultUtils.getScores.apply(
                filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.YELLOW_CARD))
        ));



        return fantasyResponse;
    };

}
