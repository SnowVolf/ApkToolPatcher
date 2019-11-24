package ru.svolf.melissa.widget.crumb;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CrumbView extends RecyclerView {
    public CrumbView(@NonNull Context context) {
        super(context);
    }

    public CrumbView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CrumbView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));

    }

//    private ArrayList constructCrumbTree(){
//
//    }
}
