package apk.tool.patcher.ui.modules.misc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.odex.filechooser.SelectAdapter;
import apk.tool.patcher.ui.widget.PinchZoomItemTouchListener;
import apk.tool.patcher.util.Preferences;
import apk.tool.patcher.util.SysUtils;
import ru.svolf.melissa.fragment.dialog.SweetContentDialog;
import ru.svolf.melissa.swipeback.SwipeBackActivity;
import ru.svolf.melissa.swipeback.SwipeBackLayout;

public class SelectActivity extends SwipeBackActivity implements SelectAdapter.ClickListener, PinchZoomItemTouchListener.PinchZoomListener {
    public final static File EXTERNAL_STORAGE = Environment.getExternalStorageDirectory();
    public File currentPath;
    private File[] files;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private GridLayoutManager layoutManager;
    private TextView mCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);

        setContentView(R.layout.activity_select);
        mCaption = findViewById(R.id.content_caption);
        recyclerView = findViewById(R.id.recycler_select_view_RecyclerView);

        if (savedInstanceState != null) {
            currentPath = new File(savedInstanceState.getString("current_path"));
        } else {
            currentPath = EXTERNAL_STORAGE;
        }
        if (checkPerm()) {
            files = getFilesData(currentPath);
            layoutManager = new GridLayoutManager(this, Preferences.getGridSize());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new SelectAdapter(files, this);
            recyclerView.setAdapter(adapter);
            mCaption.setText(currentPath.getName());
            mCaption.setSelected(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("current_path", currentPath.getPath());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(int position) {
        File file = files[position];
        if (file.isDirectory()) {
            if (file.canRead()) {
                layoutManager.scrollToPosition(0);
                files = getFilesData(file);
                ((SelectAdapter) adapter).onFilesUpdate(files);
                mCaption.setText(currentPath.getName());

            } else {
                Toast.makeText(getApplicationContext(), "No access", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemLongClick(int position) {
        final File file = files[position];

        if (file.isDirectory()) {
            if (file.canWrite()) {
                SweetContentDialog dialog = new SweetContentDialog(this);
                dialog.setTitle(R.string.caption_set_folder);
                dialog.setMessage(String.format(getString(R.string.message_set_folder), file.getName()));
                dialog.setPositive(R.drawable.ic_check, App.bindString(android.R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("path", file.getPath());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                dialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "No write permission", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), file.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    private File[] getFilesData(File dir) {
        currentPath = dir;

        File[] arr = dir.listFiles();

        Arrays.sort(arr, new Comparator<File>() {

            String name1, name2;

            @Override
            public int compare(File p1, File p2) {
                name1 = p1.getName().toLowerCase();
                name2 = p2.getName().toLowerCase();
                if (p1.isDirectory())
                    return p2.isDirectory() ? name1.compareTo(name2) : -1;
                else
                    return p2.isDirectory() ? 1 : name1.compareTo(name2);
            }
        });
        return arr;
    }

    @Override
    public void onBackPressed() {
        if (!currentPath.getPath().equals(EXTERNAL_STORAGE.getPath())) {
            File parentPath;
            if (currentPath.getPath().equals("/")) {
                parentPath = EXTERNAL_STORAGE;
            } else if (currentPath.getParentFile().canRead()) {
                parentPath = currentPath.getParentFile();
            } else if (currentPath.getParentFile().getParentFile().canRead()) {
                parentPath = currentPath.getParentFile().getParentFile();
            } else {
                parentPath = new File("/");
            }
            layoutManager.scrollToPosition(0);
            files = getFilesData(parentPath);
            if (mCaption != null) {
                mCaption.setText(parentPath.getName());
            }
            ((SelectAdapter) adapter).onFilesUpdate(files);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            files = getFilesData(currentPath);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_select_view_RecyclerView);
            layoutManager = new GridLayoutManager(this, Preferences.getGridSize());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new SelectAdapter(files, this);
            recyclerView.setAdapter(adapter);
        } else {
            SysUtils.Toast(this, "no permission");
            finish();
        }
    }

    boolean checkPerm() {
        if (Environment.getExternalStorageDirectory().canWrite() || SysUtils.granted(this, SysUtils.PERM))
            return true;

        SysUtils.Log("not granted");
        if (Build.VERSION.SDK_INT >= 23) {// && shouldShowRequestPermissionRationale(SysUtils.PERM)) {
            SysUtils.Log("requestPermissions...");
            requestPermissions(new String[]{SysUtils.PERM}, 1);
        } else {
            SysUtils.Toast(this, "Storage error");
            SysUtils.Log("checkPerm false");
            finish();
        }
        return false;
    }

    @Override
    public void onPinchZoom(int position) {
        Toast.makeText(this, "Position = " + position, Toast.LENGTH_SHORT).show();
    }
}
