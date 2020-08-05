package main.utils;

import java.time.*;

public final class TimeUtil {
    public final static ZoneId TIME_ZONE = ZoneId.of("UTC");
    public final static ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    public static long getTimestampFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET).getEpochSecond();
    }

    public static LocalDateTime getLocalDateTimeFromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }

    public static void returnToPresentIfOld(LocalDateTime localDateTime) {
        if (localDateTime.isBefore(LocalDateTime.now(TIME_ZONE))) {
            localDateTime = LocalDateTime.now(TIME_ZONE);
        }
    }

    public static LocalDateTime convertLocalTimeInUtcTime(LocalDateTime localDateTime) {
        ZonedDateTime localZone = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZone = localZone.withZoneSameInstant(TIME_ZONE);
        return utcZone.toLocalDateTime();
    }
}
