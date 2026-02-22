package io.playqd.utils;

import java.time.Duration;

public final class TimeUtils {

    public static String durationToTimeFormat(Duration duration) {
        var days = duration.toDays();
        if (days > 0) {
            var dayText = days > 1 ? "days" : "day";
            var hourText = duration.toHoursPart() > 1 ? "hours" : "hour";
            return String.format("%s %s and %s %s", days, dayText, duration.toHoursPart(), hourText);
        }
        var hours = duration.toHours();
        if (hours > 0) {
            return String.format("%s:%s:%s",
                    hours, unitPartToTimeFormat(duration.toMinutesPart()), unitPartToTimeFormat(duration.toSecondsPart()));
        }
        var durationInMinutes = duration.toMinutes();
        if (durationInMinutes > 0) {
            return String.format("%s:%s",
                    durationInMinutes, unitPartToTimeFormat(duration.toSecondsPart()));
        }
        var durationInSeconds = duration.toSeconds();
        if (durationInSeconds > 0) {
            return String.format("0:%s", unitPartToTimeFormat(duration.toSecondsPart()));
        }
        var durationInMillis = duration.toMillis();
        if (durationInMillis > 0) {
            return String.format(".%s", durationInMillis);
        }
        return "0:00";
    }

    private static String unitPartToTimeFormat(int timeUnitPart) {
        if (timeUnitPart < 10) {
            return "0" + timeUnitPart;
        }
        return "" + timeUnitPart;
    }
}
