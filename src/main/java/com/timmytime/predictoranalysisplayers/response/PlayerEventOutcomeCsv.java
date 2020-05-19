package com.timmytime.predictoranalysisplayers.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerEventOutcomeCsv {

    private UUID player;
    private UUID opponent;
    private String home;
    private Integer minutes;
    private Integer conceded;
    private Integer goals;
    private Integer assists;
    private Integer saves;


    public PlayerEventOutcomeCsv(UUID player, UUID opponent, String home){
        this.player = player;
        this.opponent = opponent;
        this.home = home;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(player).append(",");

        stringBuilder
                .append(opponent)
                .append(",")
                .append(home)
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
                "\"player\": \"" + player + "\"," +
                "\"minutes\": \"" + minutes + "\"," +
                "\"saves\": \"" + saves + "\"," +
                "\"goals\": \"" + goals + "\"," +
                "\"assists\": \"" + assists + "\"," +
                "\"conceded\": \"" + conceded + "\" }";
    }


}
