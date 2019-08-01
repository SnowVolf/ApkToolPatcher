package apk.tool.patcher.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.App;

public class Preferences {

    public static int getFilterId() {
        return App.get().getPreferences().getInt("filter_id", 0);
    }

    public static void setFilterId(int id) {
        App.get().getPreferences().edit().putInt("filter_id", id).apply();
    }

    private static int getSavedVersionCode() {
        return App.get().getPreferences().getInt("app_version", 0);
    }

    private static int getVersionCode() {
        PackageInfo info;
        try {
            info = App.get().getPackageManager().getPackageInfo(App.get().getPackageName(), PackageManager.GET_META_DATA);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {

        }
        return 0;
    }

    public static void saveVersionCode() {
        App.get().getPreferences().edit().putInt("app_version", getVersionCode()).apply();
    }

    public static boolean isChangelogShowed() {
        return getSavedVersionCode() == getVersionCode();
    }

    public static boolean isExperimentalMode() {
        return App.get().getPreferences().getBoolean("experimental", false);
    }

    private static String getExcludedLangs() {
        return App.get().getPreferences().getString("patch.exc_langs",
                /*"values|values-ru|values-uk"*/ null);
    }

    private static String getExcludedPackages() {
        return App.get().getPreferences().getString("patch.exc_interest",
                /*"androidx|android/support|io/reactivex"*/null);
    }

    public static boolean hasExcludedPackage(String path) {
        Matcher pkg = Pattern.compile(getExcludedPackages()).matcher(path);
        return pkg.find();
    }

    public static boolean hasExcludedLanguage(String path) {
        Matcher lng = Pattern.compile(getExcludedLangs()).matcher(path);
        return lng.find();
    }

    public static int getGridSize() {
        return Integer.parseInt(App.get().getPreferences().getString("list_grid_size", "2"));
    }

    public static String getApkToolPackage() {
        return App.get().getPreferences().getString("ext.decompiler", null);
    }

    public static boolean isLowMemoryMode() {
        return App.get().getPreferences().getBoolean("exec.low_mem", false);
    }
}
