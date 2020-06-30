package com.timmytime.predictoranalysisplayers.transformer;

import com.timmytime.predictoranalysisplayers.enumerator.FantasyEventTypes;
import com.timmytime.predictoranalysisplayers.response.FantasyEvent;
import com.timmytime.predictoranalysisplayers.response.MatchSelectionResponse;
import com.timmytime.predictoranalysisplayers.response.PlayerResponse;
import com.timmytime.predictoranalysisplayers.response.MatchPrediction;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatchSelectionResponseTransformer {
    private static final Logger log = LoggerFactory.getLogger(MatchSelectionResponseTransformer.class);

    private Function<Map<Integer, Double>, Double> score = predictions -> {

        if(predictions.isEmpty()){
            return 0.0;
        }

        return predictions.values().stream().mapToDouble(m -> m).sum();
    };

    private BiFunction<List<PlayerEventScore>, FantasyEventTypes, MatchSelectionResponse> create = (playerEventScores, fantasyEventTypes) ->
            new MatchSelectionResponse(
                    fantasyEventTypes,
                    playerEventScores.stream()
                            .filter(f -> f.score > 0.0)
                            .sorted(Comparator.comparing(PlayerEventScore::getScore).reversed())
                            .map(m -> new PlayerResponse(
                                    m.getPlayerResponse(),
                                    new FantasyEvent(m.score, fantasyEventTypes.name().toLowerCase())
                                    )
                            )
                            .collect(Collectors.toList()));


    public Function<MatchPrediction, List<MatchSelectionResponse>> transform = matchPrediction -> {

        List<MatchSelectionResponse> matchSelectionResponses = new ArrayList<>();

        //so focus only on goals, assists, yellow cards.  top 5 for each.  then grab the corresponding players needed
        List<PlayerResponse> combined = Stream.concat(
                matchPrediction.getHomePlayers().stream(),
                matchPrediction.getAwayPlayers().stream())
                .collect(Collectors.toList());

        List<PlayerEventScore> goals = new ArrayList<>();
        List<PlayerEventScore> assists = new ArrayList<>();
        List<PlayerEventScore> saves = new ArrayList<>();
        List<PlayerEventScore> yellows = new ArrayList<>();

        //ok the hard part...so wait.  a bit.  need to filter the map..on combined totals pretty much.
        //or map the lot to a simple map of <PlayerId, Value> or each event. then select top 5 sorted....
        combined.stream()
                .forEach(player -> {

                    goals.add(
                            new PlayerEventScore(player, player.getFantasyResponse()
                            .stream()
                            .mapToDouble(m -> score.apply(m.getGoals())).findFirst().orElse(0.0))
                    );

                    assists.add(
                            new PlayerEventScore(player, player.getFantasyResponse()
                                    .stream()
                                    .mapToDouble(m -> score.apply(m.getAssists())).findFirst().orElse(0.0))
                    );

                    if(player.getSaves() != null) {
                        saves.add(
                                new PlayerEventScore(player, player.getFantasyResponse()
                                        .stream()
                                        .mapToDouble(m -> m.getSaves()).findFirst().orElse(0.0))
                        );
                    }

                    yellows.add(
                            new PlayerEventScore(player, player.getFantasyResponse()
                                    .stream()
                                    .mapToDouble(m -> score.apply(m.getYellowCards())).findFirst().orElse(0.0))
                    );

                });



        matchSelectionResponses.add(create.apply(goals, FantasyEventTypes.GOALS));
        matchSelectionResponses.add(create.apply(assists, FantasyEventTypes.ASSISTS));
        matchSelectionResponses.add(create.apply(yellows, FantasyEventTypes.YELLOW_CARD));
        matchSelectionResponses.add(create.apply(saves, FantasyEventTypes.SAVES));

        return matchSelectionResponses.stream().sorted(Comparator.comparing(MatchSelectionResponse::getOrder)).collect(Collectors.toList());
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
