package ru.svolf.melissa.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InterestSmaliItem implements Parcelable {
    private String smaliPath;
    private String pieceOfCode;

    public InterestSmaliItem(String smaliPath, String pieceOfCode) {
        this.smaliPath = smaliPath;
        this.pieceOfCode = pieceOfCode;
    }

    protected InterestSmaliItem(Parcel in) {
        smaliPath = in.readString();
        pieceOfCode = in.readString();
    }

    public static final Creator<InterestSmaliItem> CREATOR = new Creator<InterestSmaliItem>() {
        @Override
        public InterestSmaliItem createFromParcel(Parcel in) {
            return new InterestSmaliItem(in);
        }

        @Override
        public InterestSmaliItem[] newArray(int size) {
            return new InterestSmaliItem[size];
        }
    };

    public String getSmaliPath() {
        return smaliPath;
    }

    public String getSmaliName() {
        return getSmaliPath().substring(getSmaliPath().lastIndexOf("/") + 1);
    }

    public String getPieceOfCode() {
        return pieceOfCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(smaliPath);
        dest.writeString(pieceOfCode);
    }
}
