package com.timmytime.predictoranalysisplayers.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

public class DateUtils {

    public static Function<LocalDateTime, Date> convert = date ->
            Date.from(date.atZone(ZoneId.systemDefault()).toInstant());

}
