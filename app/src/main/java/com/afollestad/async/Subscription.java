package com.afollestad.async;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface Subscription {

    @UiThread
    void result(@NonNull Result result);
}