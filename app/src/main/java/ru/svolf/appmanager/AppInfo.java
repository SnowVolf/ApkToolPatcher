package ru.svolf.appmanager;


public class AppInfo {
    private String packageName;
    private boolean system;

    public AppInfo(String pkgName, boolean system) {
        this.packageName = pkgName;
        this.system = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isSystem() {
        return system;
    }
}
