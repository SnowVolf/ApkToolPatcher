package com.gmail.heagoo.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class ApkInfoParser {

    public static AppInfo parse(Context ctx, String apkPath) throws Exception {
        AppInfo apkInfo = null;

        PackageManager packageManager = ctx.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath, 0);
        if (packageInfo != null) {
            apkInfo = new AppInfo();
            packageInfo.applicationInfo.sourceDir = apkPath;
            packageInfo.applicationInfo.publicSourceDir = apkPath;
            apkInfo.label = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            apkInfo.packageName = packageInfo.packageName;
            apkInfo.icon = packageInfo.applicationInfo.loadIcon(packageManager);
        }

        return apkInfo;
    }

    public static class AppInfo {
        public String label;
        public String packageName;
        public Drawable icon;
    }
}
