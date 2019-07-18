package com.afollestad.async;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface Subscription {

    @UiThread
    void result(@NonNull Result result);
}