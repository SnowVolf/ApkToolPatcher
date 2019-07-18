package com.afollestad.async;

import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Pool extends Base {

    public static final int MODE_PARALLEL = 1;
    public static final int MODE_SERIES = 2;
    private final static Object LOCK = new Object();
    private final Handler mHandler;
    @Mode
    private final int mMode;
    private final ArrayList<Action> mQueue;
    private Subscription mSubscription;
    private Result mResult;
    private boolean mExecuted = false;
    private int mSize;
    @UiThread
    protected Pool(@NonNull Action[] actions, @Mode int mode) {
        mHandler = new Handler();
        mQueue = new ArrayList<>();
        Collections.addAll(mQueue, actions);
        mMode = mode;
        prepare();
    }

    private void prepare() {
        synchronized (LOCK) {
            for (int i = 0; i < mQueue.size(); i++)
                mQueue.get(i).setPool(this, i);
            mSize = mQueue.size();
            LOG("Prepared %d actions for execution...", mQueue.size());
        }
    }

    public boolean isExecuting() {
        synchronized (LOCK) {
            return mQueue != null && !mQueue.isEmpty();
        }
    }

    public void waitForExecution() {
        if (!isExecuting())
            throw new IllegalStateException("Pool is not currently executing.");
        while (isExecuting()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public Result getResult() {
        return mResult;
    }

    @UiThread
    protected Pool execute() {
        synchronized (LOCK) {
            if (mExecuted || mQueue.size() == 0)
                throw new IllegalStateException("This Pool has already been executed or cancelled.");
            mExecuted = true;
            mResult = new Result();
            if (mMode == MODE_SERIES) {
                LOG("Executing actions in SERIES mode...");
                mQueue.get(0).execute();
            } else if (mMode == MODE_PARALLEL) {
                LOG("Executing actions in PARALLEL mode...");
                for (Action a : mQueue)
                    a.execute();
            } else {
                throw new IllegalStateException("Unknown mode: " + mMode);
            }
            return this;
        }
    }

    @UiThread
    public void cancel() {
        synchronized (LOCK) {
            LOG("Cancelling all actions...");
            for (Action a : mQueue)
                a.cancel();
            mQueue.clear();
            Async.pop(this);
        }
    }

    public Pool subscribe(Subscription subscription) {
        mSubscription = subscription;
        return this;
    }

    @UiThread
    protected void pop(Action action) {
        LOG("Removing action %d (%s) from pool.", action.getPoolIndex(), action.id());
        action.setPool(null, -1);
        mQueue.remove(action);
        if (mResult == null)
            mResult = new Result();
        mResult.put(action);

        if (mQueue.isEmpty()) {
            LOG("All actions are subscribe executing.");
            if (mSubscription != null) mSubscription.result(mResult);
            Async.pop(this);
        } else if (mMode == MODE_SERIES) {
            final Action nextAction = mQueue.get(0);
            LOG("Executing next action in the series: %d", nextAction.getPoolIndex());
            nextAction.execute();
        }
    }

    @UiThread
    public void push(@NonNull Action... actions) {
        for (Action a : actions)
            push(a);
    }

    @UiThread
    public void push(@NonNull Action action) {
        synchronized (LOCK) {
            mSize++;
            action.setPool(this, mSize - 1);
            mQueue.add(action);
            LOG("Pushing action %d (%s) into the Pool.", action.getPoolIndex(), action.id());

            if (mExecuted) {
                if (mMode == MODE_SERIES) {
                    if (mQueue.isEmpty())
                        action.execute();
                } else if (mMode == MODE_PARALLEL) {
                    action.execute();
                } else {
                    throw new IllegalStateException("Unknown mode: " + mMode);
                }
            }
        }
    }

    @WorkerThread
    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    @IntDef({MODE_PARALLEL, MODE_SERIES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }
}