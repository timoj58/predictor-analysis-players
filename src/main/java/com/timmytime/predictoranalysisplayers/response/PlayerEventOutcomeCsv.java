package com.timmytime.predictoranalysisplayers.response;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerEventOutcomeCsv {

    private UUID opponent;
    private String home;
    private Integer minutes;
    private Integer conceded;
    private Integer goals;
    private Integer assists;
    private Integer saves;


    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(opponent).append(",");

        stringBuilder.append(home)
                .append(",")
                .append(minutes)
                .append(",")
                .append(saves)
                .append(",")
                .append(conceded)
                .append(",")
                .append(goals)
                .append(",")
                .append(assists);

        return stringBuilder.toString();
    }


    //cant use mapper due to it calling toString (which is for CSV)
    public String getJson() {
        return "{\"opponent\": \"" + opponent + "\"," +
                "\"home\": \"" + home + "\"," +
                "\"minutes\": \"" + minutes + "\"," +
                "\"saves\": \"" + saves + "\"," +
                "\"goals\": \"" + goals + "\"," +
                "\"assists\": \"" + assists + "\"," +
                "\"conceded\": \"" + conceded + "\" }";
    }


}
