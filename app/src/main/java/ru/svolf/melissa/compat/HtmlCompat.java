package ru.svolf.melissa.compat;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 *  on 27.09.2016.
 */

public class HtmlCompat {
    public static Spanned fromHtmlSupport(String source) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //noinspection deprecation
            return Html.fromHtml(source);
        } else {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        }
    }
}
