package apk.tool.patcher.ui.modules.decompiler;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Managers.ToolsManager;
import ru.atomofiron.apknator.Utils.Cmd;

public class StartFragment extends Fragment {

    ApkToolActivity mainActivity;
    Context co;
    Activity ac;
    boolean needDie = false;

    public StartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        co = getContext();
        mainActivity = (ApkToolActivity) getActivity();

        if (checkPerm())
            init();
        if (needDie)
            mainActivity.finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    public void init() {
        SysUtils.Log("StartFragment: init()");

        final SharedPreferences sp = SysUtils.SP(co);
        final SharedPreferences.Editor ed = sp.edit();
        File dir = new File(SysUtils.getToolsPath(co));
        try {
            if (sp.getInt(SysUtils.PREFS_APP_VERSION_CODE, 0) != App.get().getPackageManager()
                    .getPackageInfo(App.get()
                            .getPackageName(), 0).versionCode) {
                Cmd.easyExec("rm -r " + dir.getAbsolutePath());
                if (dir.exists()) {
                    SysUtils.Log("cant_delete_tools_dir");
                    SysUtils.Toast(co, getString(R.string.cant_delete_tools_dir));
                    needDie = true;
                    return;
                } else
                    ed.putInt(SysUtils.PREFS_APP_VERSION_CODE, App.get().getPackageManager()
                            .getPackageInfo(App.get()
                                    .getPackageName(), 0).versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    if (new File(sdcardPath).canRead()) {
                        ed.putString(SysUtils.PREFS_PATH_SDCARD, sdcardPath);
                        if (sp.getString(SysUtils.PREFS_HOME_PATH, "").isEmpty())
                            ed.putString(SysUtils.PREFS_HOME_PATH, sdcardPath);
                    }
                }
                if (sp.getString(SysUtils.PREFS_PATCH_PATH, "").isEmpty())
                    ed.putString(SysUtils.PREFS_PATCH_PATH, Environment.getExternalStorageDirectory()
                            + "/Android/data/com.android.vending.billing.InAppBillingService.LOCK/files/LuckyPatcher");

                if (sp.getString(SysUtils.PREFS_LIBLD, "").isEmpty())
                    ed.putString(SysUtils.PREFS_LIBLD, SysUtils.ARCH.contains("86") ? "libld86.so" :
                            SysUtils.ARCH.contains("64") ? "libld64.so" : "libld.so");

                ed.apply();

                final String answer = ToolsManager.updateTools(co);
                if (!answer.isEmpty())
                    SysUtils.Log("ToolsManager answer: " + answer);
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!answer.isEmpty())
                            SysUtils.Toast(co, answer);
                        mainActivity.turnOn();
                    }
                });
            }

        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            init();
        else {
            SysUtils.Toast(co, getString(R.string.no_perm));
            ac.finish();
        }
    }

    boolean checkPerm() {
        if (Environment.getExternalStorageDirectory().canWrite() || SysUtils.granted(co, SysUtils.PERM))
            return true;

        SysUtils.Log("not granted");
        if (Build.VERSION.SDK_INT >= 23) {// && shouldShowRequestPermissionRationale(SysUtils.PERM)) {
            SysUtils.Log("requestPermissions...");
            requestPermissions(new String[]{SysUtils.PERM}, 1);
        } else {
            SysUtils.Log(getString(R.string.storage_err));
            SysUtils.Toast(co, getString(R.string.storage_err));
            SysUtils.Log("checkPerm false");
            ac.finish();
        }
        return false;
    }
}
