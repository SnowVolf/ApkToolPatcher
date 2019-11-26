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
    private CrumbAdapter adapter;

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
        adapter = new CrumbAdapter(constructCrumbTree(path));
        setAdapter(adapter);
    }

    public void setOnItemClickListener(CrumbAdapter.OnItemClickListener listener){
        if (adapter != null) {
            adapter.setItemClickListener(listener);
        } else throw new RuntimeException("Please call CrumbView.sync() first!");
    }

    /**
     * Из текущего пути в ФС, создаём лмст объектов
     * @param path путь в ФС
     * @return лист с директориями
     */
    private ArrayList<PathItem> constructCrumbTree(String path){
        // Пустой лист объектов
        ArrayList<PathItem> pathItems = new ArrayList<>();

        // Прореряем, не хуйню ли нам подсунули
        if (!path.contains("/")){
            throw new IllegalArgumentException("This is not path string!");
        } else {
            // Сплитим путь ФС на отдельные папки
            final String[] folders = path.split("/");
            // Список т.н. родителей. Каждая папка помнит своего родителя
            ArrayList<String> parents = new ArrayList<>();

            // Начинаем с 1, т.к. путь в ФС с 99% начинвется с "/"
            // напр.: /suka/sosi/hui
            for (int i = 1; i < folders.length;) {
                // Создаем список родителей
                parents.add(folders[i]);
                //Добавляем данные в список папок
                pathItems.add(new PathItem(folders[i], null, i == folders.length - 1));
                Log.d(TAG, "constructCrumbTree: " + folders[i] + ", isLast = " + (i == folders.length - 1));
                i++;
            }
            // Патчим наш список, добавлением родителей к айтему
            // . Почему отдельно? Потому что будет конфликт. Список-то еще не наполнился,
            // а мы уже хотим сделать саблист от него
            for (int i = 0; i < pathItems.size(); i++) {
                // i+ 1 нужен для добавления в этот же список исходной папки.
                // Т.е. итоговый список для папки hui, находящейся по адресу "/suka/sosi/hui" будет таким
                // 0. suka
                // 1. sosi
                // 2. hui
                pathItems.get(i).setParents(parents.subList(0, i + 1));
            }
        }
        return pathItems;
    }
}
