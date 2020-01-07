package ru.svolf.melissa.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

public class ChronometerWithPause extends Chronometer {
    private long timeWhenStopped = 0;
    private boolean isRunning = false;

    private final String getTimeKey() {
        return "KEY_TIMER_TIME" + getId();
    }
    private final String getIsRunningKey() {
        return "KEY_TIMER_RUNNING" + getId();
    }

    public ChronometerWithPause(Context context) {
        super(context);
    }

    public ChronometerWithPause(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChronometerWithPause(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void start() {
        setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
        isRunning = true;
        super.start();
    }

    @Override
    public void stop() {
        isRunning = false;
        timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
        super.stop();
    }

    public void reset() {
        stop();
        isRunning = false;
        setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getCurrentTime() {
        return timeWhenStopped;
    }

    public void setCurrentTime(long time) {
        timeWhenStopped = time;
        setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
    }

    public void saveInstanceState(Bundle outState) {
        if (isRunning) {
            timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
        }
        outState.putLong(getTimeKey(), getCurrentTime());
        outState.putBoolean(getIsRunningKey(), isRunning());
    }

    public void restoreInstanceState(Bundle inState) {
        isRunning = inState.getBoolean(getIsRunningKey());
        setCurrentTime(inState.getLong(getTimeKey()));
        timeWhenStopped = SystemClock.elapsedRealtime() - getBase();
        if (isRunning) {
            super.start();
        }
    }
}