package com.timmytime.predictoranalysisplayers.transformer;

import com.timmytime.predictoranalysisplayers.model.redisson.MatchSelectionResponse;
import com.timmytime.predictoranalysisplayers.model.redisson.PlayerResponse;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatchSelectionResponseTransformer {

    private Function<Map<Integer, Double>, Double> score = predictions -> {

        if(predictions.isEmpty()){
            return 0.0;
        }

        return predictions.values().stream().mapToDouble(m -> m).sum();
    };


    public Function<MatchPrediction, List<MatchSelectionResponse>> transform = matchPrediction -> {

        List<MatchSelectionResponse> matchSelectionResponses = new ArrayList<>();

        //so focus only on goals, assists, yellow cards.  top 5 for each.  then grab the corresponding players needed
        List<PlayerResponse> combined = Stream.concat(
                matchPrediction.getHomePlayers().stream(),
                matchPrediction.getAwayPlayers().stream())
                .collect(Collectors.toList());

        List<PlayerEventScore> goals = new ArrayList<>();
        List<PlayerEventScore> assists = new ArrayList<>();
        List<PlayerEventScore> yellows = new ArrayList<>();

        //ok the hard part...so wait.  a bit.  need to filter the map..on combined totals pretty much.
        //or map the lot to a simple map of <PlayerId, Value> or each event. then select top 5 sorted....
        combined.stream()
                .forEach(player -> {
                    //now fuck about adding them all in
                    goals.add(
                            new PlayerEventScore(player, player.getFantasyResponse()
                            .stream()
                            .mapToDouble(m -> score.apply(m.getGoals())).findFirst().getAsDouble())
                    );

                    assists.add(
                            new PlayerEventScore(player, player.getFantasyResponse()
                                    .stream()
                                    .mapToDouble(m -> score.apply(m.getAssists())).findFirst().getAsDouble())
                    );

                    yellows.add(
                            new PlayerEventScore(player, player.getFantasyResponse()
                                    .stream()
                                    .mapToDouble(m -> score.apply(m.getYellowCards())).findFirst().getAsDouble())
                    );

                });



        matchSelectionResponses.add(new MatchSelectionResponse("goals", goals.stream().sorted((o1,o2) -> o2.score.compareTo(o1.score)).limit(5).map(m -> m.playerResponse).collect(Collectors.toList())));
        matchSelectionResponses.add(new MatchSelectionResponse("assists", assists.stream().sorted((o1,o2) -> o2.score.compareTo(o1.score)).limit(5).map(m -> m.playerResponse).collect(Collectors.toList())));
        matchSelectionResponses.add(new MatchSelectionResponse("yellow", yellows.stream().sorted((o1,o2) -> o2.score.compareTo(o1.score)).limit(5).map(m -> m.playerResponse).collect(Collectors.toList())));

        return matchSelectionResponses;
    };

    @Getter
    @Setter
    private class PlayerEventScore{

        private PlayerResponse playerResponse;
        private Double score;

        public PlayerEventScore(
                PlayerResponse playerResponse,
                Double score
        ){
            this.playerResponse = playerResponse;
            this.score = score;
        }

    }
}
