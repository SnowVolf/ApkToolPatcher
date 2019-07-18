package apk.tool.patcher.util;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import apk.tool.patcher.App;
import apk.tool.patcher.R;

public class TextUtil {

    public static void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) App.get().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("ApkToolPatcher", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static String getBuildName(Context context) {
        String programBuild = context.getString(R.string.app_name);
        try {
            String pkg = context.getPackageName();
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(pkg, PackageManager.GET_META_DATA);
            programBuild += " v." + pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return programBuild;
    }

    /**
     * Чтобы не создавать один и тот же код много раз
     * 3 строки кода заменяем одной, потом юзаем где хотим
     */
    //контекст берем от Activity, иначе будет падать на некоторых прошивках
    public static void goLink(Activity context, String link) {
        Uri uri = Uri.parse(link);
        final Intent linkIntent = new Intent(Intent.ACTION_VIEW, uri);
        //без этого флага крашится на некоторых устройствах
        linkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.w("ApkToolPatcher", "Activity New Task ok");
        context.startActivity(Intent.createChooser(linkIntent, null));
    }

    public static void shareText(Context context, String string) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        context.startActivity(Intent.createChooser(intent, null));
    }
}