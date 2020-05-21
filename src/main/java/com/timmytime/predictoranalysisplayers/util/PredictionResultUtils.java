package com.timmytime.predictoranalysisplayers.util;

import com.timmytime.predictoranalysisplayers.response.data.Prediction;
import io.swagger.models.auth.In;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PredictionResultUtils {

    private static final Logger log = LoggerFactory.getLogger(PredictionResultUtils.class);

    public Function<String, Double> getAverage = (prediction) -> {

        JSONArray results = new JSONObject(prediction).getJSONArray("result");

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


        Map<Integer, Double> result = new HashMap<>();

        JSONArray results = new JSONObject(prediction).getJSONArray("result");

        for(int i = 0; i < results.length(); i++){

            JSONObject r = results.getJSONObject(i);

            if(r.getInt("key") != 0 && r.getDouble("score") >= 1.0){
                result.put(r.getInt("key"), r.getDouble("score"));
            }
        }

        return result;
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


}
