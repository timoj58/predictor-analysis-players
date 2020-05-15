package com.timmytime.predictoranalysisplayers.enumerator;

public enum FantasyEventTypes {
    GOALS(Boolean.TRUE),
    GOAL_TYPE(Boolean.FALSE),
    OWN_GOALS(Boolean.FALSE),
    SHOTS(Boolean.FALSE),
    ON_TARGET(Boolean.FALSE),
    FOULS_COMMITED(Boolean.FALSE),
    FOULS_RECEIVED(Boolean.FALSE),
    ASSISTS(Boolean.TRUE),
    PENALTY_SAVED(Boolean.FALSE),
    PENALTY_MISSED(Boolean.FALSE),
    MINUTES(Boolean.TRUE),
    RED_CARD(Boolean.FALSE),
    YELLOW_CARD(Boolean.FALSE),
    GOALS_CONCEDED(Boolean.TRUE),
    SAVES(Boolean.TRUE),
    UNKNOWN(Boolean.FALSE);

    /*
      reviewed ESPN its possible to get ASSISTS and PEN's

      need to fix all the data retrospectively for this now...hmmm.

      leave it for the top 5 leagues only -> and only for these stats.

     */

    private Boolean predict;

    FantasyEventTypes(Boolean predict){
        this.predict = predict;
    }

    public Boolean getPredict() {
        return predict;
    }

    public void setPredict(Boolean predict) {
        this.predict = predict;
    }

}
