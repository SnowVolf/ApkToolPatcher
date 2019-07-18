package ru.svolf.melissa.compat;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public class Compat {

    public static Spanned htmlCompat(String htmlString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY);
        }
        return Html.fromHtml(htmlString);
    }

}