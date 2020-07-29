package main.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeUtil {

    public final static ZoneId TIME_ZONE = ZoneId.of("UTC");
    public final static ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    public static long getTimestampFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET).getEpochSecond();
    }

    public static LocalDateTime getLocalDateTimeFromTimestamp(long timestamp) {
        return null;
    }

    public static String getDateAsString(LocalDateTime localDateTime) {
        ZonedDateTime localZone = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZone = localZone.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime utcTime = utcZone.toLocalDateTime();

        DateTimeFormatter simpleFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter fullFormat = DateTimeFormatter.ofPattern("d MMM yyyy, EEE, HH:mm");
        LocalDateTime today = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime yesterday = today.minusDays(1);

        if (utcTime.getYear() == today.getYear() &&
                utcTime.getMonth() == today.getMonth() &&
                utcTime.getDayOfMonth() == today.getDayOfMonth()) {
            return "Сегодня, " + simpleFormat.format(utcTime);
        }

        if (utcTime.getYear() == yesterday.getYear() &&
                utcTime.getMonth() == yesterday.getMonth() &&
                utcTime.getDayOfMonth() == yesterday.getDayOfMonth()) {
            return "Вчера, " + simpleFormat.format(utcTime);
        }

        return fullFormat.format(utcTime);
    }
}
