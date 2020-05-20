package com.timmytime.predictoranalysisplayers.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

public class DateUtils {

    public  Function<LocalDateTime, Date> convert = date ->
            Date.from(date.atZone(ZoneId.systemDefault()).toInstant());

    public Function<String, Date> getDate = fromDate ->
    {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }
    };

}
