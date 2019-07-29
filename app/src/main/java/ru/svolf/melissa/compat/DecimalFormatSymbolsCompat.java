package ru.svolf.melissa.compat;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *  on 26.08.2016.
 */

public class DecimalFormatSymbolsCompat {
    /**
     * Returns a new {@code DecimalFormatSymbols} instance for the user's default locale.
     * See "<a href="../util/Locale.html#default_locale">Be wary of the default locale</a>".
     *
     * @return an instance of {@code DecimalFormatSymbols}
     * @since 1.6
     */
    public static DecimalFormatSymbols getInstanceCompat() {
        return DecimalFormatSymbols.getInstance();
    }

    /**
     * Returns a new {@code DecimalFormatSymbols} for the given locale.
     *
     * @param locale the locale
     * @return an instance of {@code DecimalFormatSymbols}
     * @throws NullPointerException if {@code locale == null}
     * @since 1.6
     */
    @SuppressWarnings("WeakerAccess")
    public static DecimalFormatSymbols getInstanceCompat(Locale locale) {
        return DecimalFormatSymbols.getInstance(locale);
    }

}
