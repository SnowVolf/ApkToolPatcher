package jp.sblo.pandora.aGrep;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Prefs {
    public static final String KEY_IGNORE_CASE = "IgnoreCase";
    public static final String KEY_REGULAR_EXPRESSION = "RegularExpression";
    public static final String KEY_TARGET_EXTENSIONS_OLD = "TargetExtensions";
    public static final String KEY_TARGET_DIRECTORIES_OLD = "TargetDirectories";
    public static final String KEY_TARGET_EXTENSIONS_NEW = "TargetExtensionsNew";
    public static final String KEY_TARGET_DIRECTORIES_NEW = "TargetDirectoriesNew";
    public static final String KEY_FONTSIZE = "FontSize";
    public static final String KEY_HIGHLIGHTFG = "HighlightFg";
    public static final String KEY_HIGHLIGHTBG = "HighlightBg";
    public static final String KEY_ADD_LINENUMBER = "AddLineNumber";

    public static final String PREF_RECENT = "recent";

    public boolean mRegularExrpression = false;
    public boolean mIgnoreCase = true;
    public int mHighlightBg = 0xFF00FFFF;
    public int mHighlightFg = 0xFF000000;
    public ArrayList<CheckedString> mDirList = new ArrayList<CheckedString>();
    public ArrayList<CheckedString> mExtList = new ArrayList<CheckedString>();
    int mFontSize = 16;
    boolean addLineNumber = false;

    static public Prefs loadPrefes(Context ctx) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

        Prefs prefs = new Prefs();

        // target directory
        String dirs = sp.getString(KEY_TARGET_DIRECTORIES_NEW, "");
        prefs.mDirList = new ArrayList<CheckedString>();
        if (dirs.length() > 0) {
            String[] dirsarr = dirs.split("\\|");
            int size = dirsarr.length;
            for (int i = 0; i < size; i += 2) {
                boolean c = dirsarr[i].equals("true");
                String s = dirsarr[i + 1];
                prefs.mDirList.add(new CheckedString(c, s));
            }
        } else {
            dirs = sp.getString(KEY_TARGET_DIRECTORIES_OLD, "");
            if (dirs.length() > 0) {
                String[] dirsarr = dirs.split("\\|");
                int size = dirsarr.length;
                for (int i = 0; i < size; i++) {
                    prefs.mDirList.add(new CheckedString(dirsarr[i]));
                }
            }
        }
        // target extensions
        String exts = sp.getString(KEY_TARGET_EXTENSIONS_NEW, "");
        prefs.mExtList = new ArrayList<CheckedString>();
        if (exts.length() > 0) {
            String[] arr = exts.split("\\|");
            int size = arr.length;
            for (int i = 0; i < size; i += 2) {
                boolean c = arr[i].equals("true");
                String s = arr[i + 1];
                prefs.mExtList.add(new CheckedString(c, s));
            }
        } else {
            exts = sp.getString(KEY_TARGET_EXTENSIONS_OLD, "txt");
            if (exts.length() > 0) {
                String[] arr = exts.split("\\|");
                int size = arr.length;
                for (int i = 0; i < size; i++) {
                    prefs.mExtList.add(new CheckedString(arr[i]));
                }
            }
        }

        prefs.mRegularExrpression = sp.getBoolean(KEY_REGULAR_EXPRESSION, false);
        prefs.mIgnoreCase = sp.getBoolean(KEY_IGNORE_CASE, true);

        prefs.mFontSize = Integer.parseInt(sp.getString(KEY_FONTSIZE, "-1"));
        prefs.mHighlightFg = sp.getInt(KEY_HIGHLIGHTFG, 0xFF000000);
        prefs.mHighlightBg = sp.getInt(KEY_HIGHLIGHTBG, 0xFF00FFFF);

        prefs.addLineNumber = sp.getBoolean(KEY_ADD_LINENUMBER, false);
        return prefs;
    }

    public static void clearPrefs(Context ctx) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        sp.edit().clear().apply();
    }

    public void savePrefs(Context context) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = sp.edit();

        // target directory
        StringBuilder dirs = new StringBuilder();
        for (CheckedString t : mDirList) {
            dirs.append(t.checked);
            dirs.append('|');
            dirs.append(t.string);
            dirs.append('|');
        }
        if (dirs.length() > 0) {
            dirs.deleteCharAt(dirs.length() - 1);
        }

        // target extensions
        StringBuilder exts = new StringBuilder();
        for (CheckedString t : mExtList) {
            exts.append(t.checked);
            exts.append('|');
            exts.append(t.string);
            exts.append('|');
        }
        if (exts.length() > 0) {
            exts.deleteCharAt(exts.length() - 1);
        }

        editor.putString(KEY_TARGET_DIRECTORIES_NEW, dirs.toString());
        editor.putString(KEY_TARGET_EXTENSIONS_NEW, exts.toString());
        editor.remove(KEY_TARGET_DIRECTORIES_OLD);
        editor.remove(KEY_TARGET_EXTENSIONS_OLD);
        editor.putBoolean(KEY_REGULAR_EXPRESSION, mRegularExrpression);
        editor.putBoolean(KEY_IGNORE_CASE, mIgnoreCase);

        editor.apply();

    }

    public void addRecent(Context context, String searchWord) {
        // 書き出し
        final SharedPreferences rsp = context.getSharedPreferences(PREF_RECENT, Context.MODE_PRIVATE);
        Editor reditor = rsp.edit();
        reditor.putLong(searchWord, System.currentTimeMillis());
        reditor.apply();
    }

    public List<String> getRecent(Context context) {
        // ロード
        final SharedPreferences rsp = context.getSharedPreferences(PREF_RECENT, Context.MODE_PRIVATE);
        Map<String, ?> all = rsp.getAll();

        // ソート
        List<Entry<String, ?>> entries = new ArrayList<Entry<String, ?>>(all.entrySet());
        Collections.sort(entries, new Comparator<Entry<String, ?>>() {
            public int compare(Entry<String, ?> e1, Entry<String, ?> e2) {
                return ((Long) e2.getValue()).compareTo((Long) e1.getValue());
            }
        });
        // 取り出し
        ArrayList<String> result = new ArrayList<String>();
        for (Entry<String, ?> entry : entries) {
            result.add(entry.getKey());
        }

        // 30個目以降は削除
        final int MAX = 30;
        final int size = result.size();
        if (size > MAX) {
            Editor editor = rsp.edit();
            for (int i = size - 1; i >= MAX; i--) {
                editor.remove(result.get(i));
                result.remove(i);
            }
            editor.apply();
        }
        return result;
    }
}
