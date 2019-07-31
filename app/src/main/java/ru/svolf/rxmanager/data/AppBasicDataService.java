package ru.svolf.rxmanager.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.svolf.rxmanager.util.AppBasicInfoComparator;

/**
 * Retrieve apps installed on device
 * <p>
 * Created by Martin Styk on 14.06.2017.
 */
public class AppBasicDataService {

    private PackageManager packageManager;

    public AppBasicDataService(@NonNull PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    public AppBasicDataService(@NonNull Context context) {
        this.packageManager = context.getPackageManager();
    }

    @NonNull
    public List<AppListData> getAll() {

        List<PackageInfo> applications = packageManager.getInstalledPackages(0);

        List<AppListData> packages = new ArrayList<>(applications.size());

        for (PackageInfo packageInfo : applications) {
            if (packageInfo.applicationInfo != null)
                packages.add(new AppListData(packageInfo, packageManager));
        }

        Collections.sort(packages, AppBasicInfoComparator.INSTANCE);

        return packages;
    }

    @NonNull
    public List<AppListData> getForSources(boolean allowSystem, @NonNull AppSource... appSources) {

        List<AppListData> appListData = getAll();
        List<AppListData> results = new ArrayList<>();
        List<AppSource> appSourceList = Arrays.asList(appSources);

        for (AppListData data : appListData) {
            if (appSourceList.contains(data.getSource()) && (allowSystem || !data.isSystemApp())) {
                results.add(data);
            }
        }

        return results;
    }

    @NonNull
    public List<String> getAllPackageNames() {

        List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        List<String> packages = new ArrayList<>(applications.size());

        for (ApplicationInfo applicationInfo : applications) {
            packages.add(applicationInfo.packageName);
        }

        Collections.sort(packages);

        return packages;
    }

}
