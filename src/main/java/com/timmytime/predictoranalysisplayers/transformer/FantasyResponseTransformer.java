package com.timmytime.predictoranalysisplayers.transformer;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.model.mongo.FantasyOutcome;
import com.timmytime.predictoranalysisplayers.response.FantasyResponse;
import com.timmytime.predictoranalysisplayers.util.PredictionResultUtils;

import java.util.List;
import java.util.function.Function;

public class FantasyResponseTransformer {

    private final PredictionResultUtils predictionResultUtils = new PredictionResultUtils();

    public Function<List<FantasyOutcome>, FantasyResponse> transform = outcomes -> {
        FantasyResponse fantasyResponse = new FantasyResponse();


        fantasyResponse.setMinutes(predictionResultUtils.getAverage.apply(
                        outcomes.stream()
                        .filter(f -> f.getFantasyEventType().equals(FantasyEventTypes.MINUTES))
                        .findFirst()
                        .get()
                        .getPrediction()
                ));

        fantasyResponse.setAssists(predictionResultUtils.getScores.apply(
                outcomes.stream()
                        .filter(f -> f.getFantasyEventType().equals(FantasyEventTypes.ASSISTS))
                        .findFirst()
                        .get()
                        .getPrediction()
        ));

        fantasyResponse.setGoals(predictionResultUtils.getScores.apply(
                outcomes.stream()
                        .filter(f -> f.getFantasyEventType().equals(FantasyEventTypes.GOALS))
                        .findFirst()
                        .get()
                        .getPrediction()
        ));

        fantasyResponse.setSaves(predictionResultUtils.getAverage.apply(
                outcomes.stream()
                        .filter(f -> f.getFantasyEventType().equals(FantasyEventTypes.SAVES))
                        .findFirst()
                        .get()
                        .getPrediction()
        ));

        fantasyResponse.setConceded(predictionResultUtils.getAverage.apply(
                outcomes.stream()
                        .filter(f -> f.getFantasyEventType().equals(FantasyEventTypes.GOALS_CONCEDED))
                        .findFirst()
                        .get()
                        .getPrediction()
        ));



        return fantasyResponse;
    };

}
