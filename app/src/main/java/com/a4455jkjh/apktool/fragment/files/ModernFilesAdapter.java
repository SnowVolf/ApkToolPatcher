package com.a4455jkjh.apktool.fragment.files;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.a4455jkjh.apktool.fragment.FilesFragment;
import com.a4455jkjh.apktool.util.Settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apk.tool.patcher.R;

public class ModernFilesAdapter extends RecyclerView.Adapter<ModernFilesAdapter.ModernViewHolder> implements Refreshable  {
    private static final String TAG = "ModernFilesAdapter";
    private final FilesFragment frag;
    private final List<Item> items;
    private final WatchDog path;
    private static final File rootDir = Environment.getExternalStorageDirectory();
    private BuildItem build;
    private File curDir;

    private ModernFilesAdapter(FilesFragment act, WatchDog path) {
        Log.d(TAG, "ModernFilesAdapter() called with: act = [" + act + "], path = [" + path + "]");
        this.frag = act;
        this.path = path;
        build = null;
        items = new ArrayList<>();
        init(null);
    }

    public void init(Bundle savedInstanceState) {
        Log.d(TAG, "init() called with: savedInstanceState = [" + savedInstanceState + "]");
        File rootDir = this.rootDir;
        if (savedInstanceState != null) {
            String cur_dir = savedInstanceState.getString("CUR_DIR_PATH", rootDir.getAbsolutePath());
            refresh(new File(cur_dir));
        } else {
            File f = new File(Settings.projectPath);
            if (f.exists() && f.isDirectory())
                refresh(f);
            else
                refresh(rootDir);

        }
    }

    public static ModernFilesAdapter init(FilesFragment act, RecyclerView files, WatchDog fileObserver) {
        Log.d(TAG, "init() called with: act = [" + act + "], files = [" + files + "], fileObserver = [" + fileObserver + "]");
        ModernFilesAdapter adapter = new ModernFilesAdapter(act, fileObserver);
        files.setAdapter(adapter);
        return adapter;
    }

    @NonNull
    @Override
    public ModernViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_entry, parent, false);
        return new ModernViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ModernViewHolder holder, int position) {
        items.get(position).setup(holder.icon, holder.name);
    }

    @Override
    public long getItemId(int p1) {
        return items.get(p1).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void refresh() {
        refresh(curDir);
    }

    public Object getItem(int p1) {
        return items.get(p1);
    }

    public void createFileOrDir(final int mode) {
        FragmentActivity act = frag.getActivity();
        final EditText name = new EditText(act);
        int title;
        if (mode == R.id.new_file)
            title = R.string.new_file;
        else
            title = R.string.new_dir;
        final Button ok = new AlertDialog.Builder(act).
                setTitle(title).
                setView(name).
                setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        File target = new File(curDir, name.getText().toString());
                        if (mode == R.id.new_file) {
                            try {
                                target.createNewFile();
                                frag.edit(target);
                                refresh();
                            } catch (IOException e) {}
                        } else {
                            if (target.mkdir())
                                refresh(target);
                        }
                    }
                }).
                setNegativeButton(R.string.cancel, null).
                show().
                getButton(DialogInterface.BUTTON_POSITIVE);
        name.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }
            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }
            @Override
            public void afterTextChanged(Editable p1) {
                if (p1.length() == 0) {
                    ok.setEnabled(false);
                    return;
                }
                File target = new File(curDir, p1.toString());
                if (target.exists()) {
                    ok.setEnabled(false);
                    return;
                }
                ok.setEnabled(true);
            }
        });
        ok.setEnabled(false);
    }

    public void refresh(File dir) {
        curDir = dir;
        BuildItem build = this.build;
        if (build != null && !build.isSubDir(dir))
            build = null;
        path.watchForFile(dir.getAbsolutePath());
        items.clear();
        // Фикс, если тупой юзер перейдёт в /storage/emulated/, которая только для чтения
        if (dir.listFiles() != null) {
            for (File f : dir.listFiles()) {
                items.add(new FileItem(f));
//            Log.d(TAG, "refresh: add item with path = " + f);
                if (f.isFile() && f.getName().equals("apktool.json"))
                    build = new BuildItem(dir);
            }
            if (build != null)
                items.add(build);
            this.build = build;
            Collections.sort(items);
        }
        Log.d(TAG, "refresh: total items in dir = " + items.size());
        notifyDataSetChanged();
    }

    public boolean goBack() {
        if (curDir.equals(rootDir))
            return false;
        refresh(curDir.getParentFile());
        return true;
    }

    public void save(Bundle outState) {
        outState.putString("CUR_DIR_PATH", curDir.getAbsolutePath());
    }

    public class ModernViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView icon;

        public ModernViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            File check = new File(curDir, name.getText().toString());
            if (check.isDirectory()){
                refresh(check);
            } else {
                frag.edit(check);
            }
        }
    }
}
