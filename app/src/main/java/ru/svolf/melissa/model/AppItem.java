package ru.svolf.melissa.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppItem implements Parcelable {
    private String packageLabel;
    private String packageName;
    private String packageVersion;
    private String packageFilePath;
    private Drawable packageIcon;
    private boolean isSystem;

    public AppItem() {

    }

    protected AppItem(Parcel in) {
        packageLabel = in.readString();
        packageName = in.readString();
        packageVersion = in.readString();
        packageFilePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageLabel);
        dest.writeString(packageName);
        dest.writeString(packageVersion);
        dest.writeString(packageFilePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppItem> CREATOR = new Creator<AppItem>() {
        @Override
        public AppItem createFromParcel(Parcel in) {
            return new AppItem(in);
        }

        @Override
        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };

    public String getPackageLabel() {
        return packageLabel;
    }

    public void setPackageLabel(String packageLabel) {
        this.packageLabel = packageLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageFilePath() {
        return packageFilePath;
    }

    public void setPackageFilePath(String packageFilePath) {
        this.packageFilePath = packageFilePath;
    }

    public Drawable getPackageIcon() {
        return packageIcon;
    }

    public void setPackageIcon(Drawable packageIcon) {
        this.packageIcon = packageIcon;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
