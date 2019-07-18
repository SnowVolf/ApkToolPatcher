package com.afollestad.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Result implements Iterable<Action<?>> {

    private HashMap<String, Action<?>> mMap;

    protected Result() {
        mMap = new HashMap<>();
    }

    @Nullable
    public Action<?> get(String id) {
        return mMap.get(id);
    }

    protected void put(@NonNull Action action) {
        //noinspection ConstantConditions
        if (action.id() == null) return;
        mMap.put(action.id(), action);
    }

    @Nullable
    public String[] ids() {
        if (mMap.size() == 0) return null;
        return mMap.keySet().toArray(new String[mMap.keySet().size()]);
    }

    @Override
    public Iterator<Action<?>> iterator() {
        return mMap.values().iterator();
    }


}