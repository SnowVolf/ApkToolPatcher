package apk.tool.patcher.entity;

import java.util.Observable;

public class SimpleObservable extends Observable {
    @Override
    public synchronized boolean hasChanged() {
        return true;
    }
}