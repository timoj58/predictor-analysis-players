package com.timmytime.predictoranalysisplayers.util;

import io.swagger.models.auth.In;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PredictionResultUtils {

    private static final Logger log = LoggerFactory.getLogger(PredictionResultUtils.class);


    public Function<String, Double> getAverage = (prediction) -> {

        log.info("data is {}", prediction);

        JSONArray results = new JSONArray(prediction);

        //weight the total and dont ceil it....for fuck sake,  no wonder! its 50/50 now. well it was...now its really accurate.
        Double weightedGoals = 0.0;
        for (int i = 0; i < results.length(); i++) {
            if (results.getJSONObject(i).getDouble("score") > 0.0) {
                weightedGoals += (results.getJSONObject(i).getDouble("key") * (results.getJSONObject(i).getDouble("score") / 100));
            }
        }

        return weightedGoals;

    };

    public Function<String, Map<Integer, Double>> getScores = (prediction) -> {

        log.info("data is {}", prediction);

        Map<Integer, Double> result = new HashMap<>();

        JSONArray results = new JSONArray(prediction);

        for(int i = 0; i < results.length(); i++){

            JSONObject r = results.getJSONObject(i);

            if(r.getInt("key") != 0 && r.getDouble("score") > 0.0){
                result.put(r.getInt("key"), r.getDouble("score"));
            }
        }

        return result;
    };

}
