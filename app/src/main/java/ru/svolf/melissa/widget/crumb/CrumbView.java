package ru.svolf.melissa.widget.crumb;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apk.tool.patcher.App;

public class CrumbView extends RecyclerView {
    private static final String TAG = "CrumbView";
    public CrumbView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CrumbView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CrumbView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setHorizontalFadingEdgeEnabled(true);
        setFadingEdgeLength(App.dpToPx(20));
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        manager.setStackFromEnd(true);
        manager.setSmoothScrollbarEnabled(true);
        setLayoutManager(manager);
        if (isInEditMode()){
            sync("/storage/emulated/0");
        }
    }

    public void sync(String path){
        CrumbAdapter adapter = new CrumbAdapter(constructCrumbTree(path));
        setAdapter(adapter);
    }

    private ArrayList<PathItem> constructCrumbTree(String path){
        ArrayList<PathItem> pathItems = new ArrayList<>();

        if (!path.contains("/")){
            throw new IllegalArgumentException("This is not path string!");
        } else {
            final String[] folders = path.split("/");
            for (int i = 1; i < folders.length; i++) {
                pathItems.add(new PathItem(folders[i], i == folders.length - 1));
                Log.d(TAG, "constructCrumbTree: " + folders[i] + ", isLast = " + (i == folders.length - 1));
            }
        }
        return pathItems;
    }
}
