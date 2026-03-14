package io.playqd.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    public static String ldFormatted(LocalDate ld) {
        return ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String ldtFormatted(LocalDateTime ldt) {
        return ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
