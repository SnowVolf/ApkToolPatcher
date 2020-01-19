package apk.tool.patcher.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import apk.tool.patcher.BuildConfig;
import apk.tool.patcher.R;

public class SysUtils {

    public static final String aarch64 = "aarch64";
    public static final String i386 = "i386";
    public static final String arm = "arm";
    public static final String ARCH = System.getProperty("os.arch");

    public static final String ACTION = "ACTION";
    public static final String PATH = "PATH";
    public static final String TIME = "TIME";
    public static final String OUTPUT = "OUTPUT";
    public static final String LOG = "LOG";
    public static final String PID = "PID";
    public static final String FINISH = "FINISH";
    public static final String SUCCESS = "SUCCESS";
    public static final String TASK_NUM = "TASK_NUM";
    public static final String FILENAME = "FILENAME";
    public static final String ICON = "ICON";

    public static final String PERM = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String PREFS_VIBRATE = "vibrate";
    public static final String PREFS_NOTIFY = "notify";
    public static final String PREFS_USE_ROOT = "use_root";
    public static final String PREFS_USE_SDCARD_TOOLS = "use_sdcard_tools";

    public static final String PREFS_TOOLS_PATH_SDCARD = "tools_path_sdcard";
    public static final String PREFS_PATH_SDCARD = "path_sdcard";
    public static final String PREFS_HOME_PATH = "home_path";
    public static final String PREFS_PATCH_PATH = "save_path";
    public static final String PREFS_LIBLD = "libld";
    public static final String PREFS_REMEMBER_PATH = "remember_path";
    public static final String PREFS_REMEMBERED_PATH = "remembered_path";
    public static final String PREFS_AUTOSIGN = "autosign";
    public static final String PREFS_AUTOMINIMIZE = "autominimize";
    public static final String PREFS_APP_VERSION_CODE = "app_version";
    public static final String PREFS_LIST_ANIM = "list_anim";
    public static final String PREFS_CLEAR_FRAMEWORKS = "clear_frameworks";
    public static final String PREFS_LINE_EFFECT = "list_item_effect";
    public static final String PREFS_API_LEVEL = "api_level";

    public static final String TOOL_APKTOOL = "apktool";
    public static final String TOOL_AAPT = "aapt";
    public static final String TOOL_SIGNAPK = "signapk";
    public static final String TOOL_SMALI = "smali";
    public static final String TOOL_BAKSMALI = "baksmali";
    public static final String TOOL_DX = "dx";
    public static final String[] TOOLS_ARR = {TOOL_APKTOOL, TOOL_AAPT, TOOL_SIGNAPK, TOOL_SMALI, TOOL_BAKSMALI, TOOL_DX};

    public static final int MENU_CLEAR = 0;
    public static final int MENU_SEARCH = 1;
    public static final int MENU_PREV = 2;
    public static final int MENU_NEXT = 3;
    public static final int MENU_SAVE = 4;
    public static final int MENU_SAVE_PATCH = 5;
    public static final int MENU_CLOSE = 6;

    public static final String FRAGMENT_START = "FRAGMENT_START";
    public static final String FRAGMENT_FILES = "FRAGMENT_FILES";
    public static final String FRAGMENT_TOOLS = "FRAGMENT_TOOLS";
    public static final String FRAGMENT_SMALI = "FRAGMENT_SMALI";
    public static final String FRAGMENT_PREVIEW = "FRAGMENT_PREVIEW";
    public static final String FRAGMENT_KEYSTORE = "FRAGMENT_KEYSTORE";
    public static final String FRAGMENT_PREFS = "FRAGMENT_PREFS";

    public static final int MENU_SDCARD = 1;
    public static final int MENU_HOME = 2;

    public static final int FR_NAV_MENU = 0;
    public static final int FR_NAV_TASK = 1;
    public static final int FR_SNACK = 3;
    public static final int FR_SMALI = 4;
    public static final int FR_PREVIEW = 5;
    public static final int FR_VERSIONS_CHANGE = 6;
    public static final int FR_KEYSTORE = 7;

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final int[] ANIM_IDS = {-1, R.anim.fileslist_line_right, R.anim.fileslist_line_above, R.anim.fileslist_line_bottom,};

    public static void Log(String log) {
        if (SysUtils.DEBUG)
            Log.e("atomofiron", log);
        else
            Log.i("atomofiron", "[DEBUG] " + log);
    }

    public static void Toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static int max(int x, int y) {
        return x > y ? x : y;
    }

    public static int min(int x, int y) {
        return x < y ? x : y;
    }

    public static Boolean granted(Context context, String permission) {
        return (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    public static String getPath(SharedPreferences sp, String what) {
        return sp.getString(what, "");
    }

    public static String getHumanSize(double fileSize) {
        String[] metr = {"B", "KB", "MB", "GB"};
        int l = 0;
        while (fileSize >= 1024) {
            fileSize = fileSize / 1024;
            l++;
        }
        return new DecimalFormat("#0.00").format(fileSize).concat(metr[l]);
    }

    public static String rmSlashN(String text) {
        while (text.contains("\n\n")) text = text.replace("\n\n", "\n");
        if (text.startsWith("\n"))
            text = text.substring(1);
        if (text.endsWith("\n"))
            text = text.substring(0, text.length() - 1);

        return text;
    }

    public static String getText(View view, int id) {
        return ((EditText) view.findViewById(id)).getText().toString();
    }

    public static void openFile(Context context, String path) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(new File(path)), "text/plain"));
        } catch (ActivityNotFoundException ignored) {

        }
    }

    //methods for fastscroller
    public static float clamp(float min, float max, float value) {
        float minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    public static float getViewRawY(View view) {
        int[] location = new int[2];
        location[0] = 0;
        location[1] = (int) view.getY();
        ((View) view.getParent()).getLocationInWindow(location);
        return location[1];
    }

    /**
     * Animates filenames textview to marquee after a delay.
     * Make sure to set {@link TextView#setSelected(boolean)} to false in order to stop the marquee later
     */
    public static void marqueeAfterDelay(int delayInMillis, final TextView marqueeView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // marquee works only when text view has focus
                marqueeView.setSelected(true);
            }
        }, delayInMillis);
    }

    public static ArrayList<String> appendToArrayList(String[] array, String appendix, boolean appendToTop) {
        ArrayList<String> list = new ArrayList<>();
        if (appendToTop) {
            list.add(appendix);
        }

        list.addAll(Arrays.asList(array));

        if (!appendToTop) {
            list.add(appendix);
        }
        return list;
    }

    public static String[] appendToArray(String[] array, String appendix, boolean appendToTop) {
        return appendToArrayList(array, appendix, appendToTop).toArray(new String[0]);
    }

    public static String[] subArray(String[] array, int startOffset, int endOffset) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(array).subList(startOffset, endOffset + 1));
        return list.toArray(new String[0]);
    }

    public static String[] subArray(String[] array, int startOffset) {
        return subArray(array, startOffset, array.length);
    }

    public interface ActionListener {
        void onAction(int action, String data, int intData, boolean booldata);// Bundle bundle);
    }

    public static class FileComparator implements Comparator {
        public final int compare(Object pFirst, Object pSecond) {
            File first = (File) pFirst;
            File second = (File) pSecond;
            if (first.isDirectory() && !second.isDirectory())
                return -1;
            else if (!first.isDirectory() && second.isDirectory())
                return 1;
            if (first.getName().compareToIgnoreCase(second.getName()) < 0)
                return -1;
            else
                return 1;
        }
    }
}
