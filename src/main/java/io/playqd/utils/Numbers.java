package io.playqd.utils;

import java.text.NumberFormat;

public final class Numbers {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    public static String format(int number) {
        return NUMBER_FORMAT.format(number);
    }
}
