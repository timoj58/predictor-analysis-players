package com.timmytime.predictoranalysisplayers.response.data;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UpcomingEventResponse implements Serializable {

    private Team home;
    private Team away;
    private Date eventDate;

    private String country;

    //need the match bets too...match only for now.  (homea /away / draw).


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpcomingEventResponse that = (UpcomingEventResponse) o;
        return Objects.equals(home, that.home) &&
                Objects.equals(away, that.away);
    }

    @Override
    public int hashCode() {
        return Objects.hash(home, away);
    }


}
