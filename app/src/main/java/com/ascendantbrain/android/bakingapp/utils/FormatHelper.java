package com.ascendantbrain.android.bakingapp.utils;

public class FormatHelper {

    /** remove extraneous trailing zeros and decimal point */
    public static String formatDouble(java.util.Locale locale, Double d) {
        String format = (Math.floor(d) == d) ? "%.0f" : "%s";
        return String.format(locale,format,d);
    }
}
