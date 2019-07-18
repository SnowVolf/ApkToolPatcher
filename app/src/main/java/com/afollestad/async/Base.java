package com.afollestad.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Base {

    private static LogRelay mLogRelay;

    public static void setLogRelay(LogRelay relay) {
        mLogRelay = relay;
    }

    protected static void LOG(@NonNull Class<?> context, @NonNull String message, @Nullable Object... args) {
        if (args != null)
            message = String.format(message, args);
        Log.d(context.getSimpleName(), message);
        if (mLogRelay != null)
            mLogRelay.onRelay(Html.fromHtml(String.format("<b>%s</b>: %s", context.getSimpleName(), message)));
    }

    protected void LOG(@NonNull String message, @Nullable Object... args) {
        LOG(getClass(), message, args);
    }

    public interface LogRelay {
        void onRelay(Spanned message);
    }
}