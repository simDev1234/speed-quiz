package com.example.ranking.global.util;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class KoreaTimeUtil {

    public static final String ASIA_SEOUL_TIME_ZONE = "Asia/Seoul";

    public ZonedDateTime getKoreaLocalDateTime(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.of(ASIA_SEOUL_TIME_ZONE));
    }

    public String getKoreaLocalDateTime(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return getKoreaLocalDateTime(localDateTime).format(dateTimeFormatter);
    }

}
