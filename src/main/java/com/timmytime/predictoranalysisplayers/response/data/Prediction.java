package com.timmytime.predictoranalysisplayers.response.data;

public class Prediction {


    private String key;
    private Double score;

    public Prediction() {

    }

    public Prediction(String key, Double score) {
        this.key = key;
        this.score = score;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "{ \"key\": \"" + key + "\"," +
                " \"score\": \"" + score + "\"" + "}";
    }

}

