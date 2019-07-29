package apk.tool.patcher.filesystem;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;

import java.io.File;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.about.HelpActivity;
import apk.tool.patcher.ui.modules.base.BaseActivity;
import apk.tool.patcher.util.Cs;
import apk.tool.patcher.util.PathF;

public class ExternalCard extends BaseActivity {
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_external_card);
        TextView textView = findViewById(R.id.extcard_info);
        if (textView != null) {
            textView.setText("bla-bla-bla");
        }

    }

    public void btnok_clicked(View view) {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    public void btnhomefolder_clicked(View view) {
        Intent intent = new Intent();
        intent.putExtra(Cs.EXTRA_FOLDER_NAME, getPath(true));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void btnhelp_clicked(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Cs.EXTRA_HELP_TOPIC, "external_sdcard.html");
        startActivity(intent);
    }

    public static String getPath(boolean absPath) {
        File[] externalFilesDirs;
        try {
            externalFilesDirs = ContextCompat.getExternalFilesDirs(App.get(), null);
        } catch (NullPointerException e) {
            externalFilesDirs = null;
        }
        if (externalFilesDirs == null) {
            return null;
        }
        for (int i = 1; i < externalFilesDirs.length; i++) {
            File file = externalFilesDirs[i];
            if (file != null) {
                String externalStorageState;
                externalStorageState = EnvironmentCompat.getStorageState(file);
                if (externalStorageState.equals(Environment.MEDIA_MOUNTED) || externalStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY) || externalStorageState.equals(Environment.MEDIA_SHARED)) {
                    String absolutePath = file.getAbsolutePath();
                    if (absPath) {
                        return absolutePath;
                    }
                    int indexOf = absolutePath.indexOf("/Android/");
                    if (indexOf == -1) {
                        return null;
                    }
                    return absolutePath.substring(0, indexOf);
                }
            }
        }
        return null;
    }

    public static boolean isExtCardRootWritableBeforeElevation() {
        String path = getPath(false);
        if (path == null) {
            return false;
        }
        SharedPreferences sharedPref = App.get().getPreferences();
        String str = "extsd_writable";
        if (sharedPref.getBoolean(str, false)) {
            return true;
        }
        File file = new File(PathF.addEndSlash(path) + System.currentTimeMillis());
        if (!file.mkdir()) {
            return false;
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
        Editor editor = sharedPref.edit();
        editor.putBoolean("extsd_writable", true);
        editor.apply();
        return true;
    }

    public static boolean isEntireCardWritable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isExtCardRootWritable() {
        return isEntireCardWritable() || isExtCardRootWritableBeforeElevation();
    }

    public static boolean isExtCardHomeFolder(String str) {
        String path = getPath(true);
        //noinspection RedundantIfStatement
        if (path == null || !str.startsWith(path)) {
            return false;
        }
        return true;
    }

    public static boolean isExtCard(String str) {
        String path = getPath(false);
        //noinspection RedundantIfStatement
        if (path == null || str.length() == 0 || !str.startsWith(path)) {
            return false;
        }
        return true;
    }

    public static boolean isPathWritableBeforeElevation(String str) {
        return !isExtCard(str) || isExtCardRootWritableBeforeElevation() || isExtCardHomeFolder(str);
    }

    public static boolean isPathWritable(String str) {
        return isEntireCardWritable() || isPathWritableBeforeElevation(str);
    }

    public static boolean isPathWritableInfo(AppCompatActivity activity, String str) {
        if (isPathWritable(str)) {
            return true;
        }
        activity.startActivityForResult(new Intent(activity, ExternalCard.class), Cs.REQ_CODE_EXTCARD);
        return false;
    }

    public static void homeUsedInfo(AppCompatActivity activity) {
        Toast makeText = Toast.makeText(activity, "Выберите домашнюю папку", Toast.LENGTH_LONG);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }
}
