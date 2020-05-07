package com.timmytime.predictoranalysisplayers.response;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerEventOutcomeCsv {

    private UUID opponent;
    private Boolean home;
    private Integer minutesPlayed;
    private Integer goalsConceded;
    private Integer goalsScored;
    private Integer goalsAssisted;
    private Integer saves;


    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(opponent).append(",");

        stringBuilder.append(home)
                .append(",")
                .append(minutesPlayed)
                .append(",")
                .append(saves)
                .append(",")
                .append(goalsConceded)
                .append(",")
                .append(goalsScored)
                .append(",")
                .append(goalsAssisted);

        return stringBuilder.toString();
    }


    //cant use mapper due to it calling toString (which is for CSV)
    public String getJson() {
        return "{\"opponent\": \"" + opponent + "\"," +
                "\"home\": \"" + home + "\"," +
                "\"minutesPlayed\": \"" + minutesPlayed + "\"," +
                "\"saves\": \"" + saves + "\"," +
                "\"goalsScored\": \"" + goalsScored + "\"," +
                "\"goalsAssisted\": \"" + goalsAssisted + "\"," +
                "\"goalsConceded\": \"" + goalsConceded + "\" }";
    }


}
