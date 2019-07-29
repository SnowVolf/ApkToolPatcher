package ru.svolf.melissa.compat;

import android.os.ParcelFileDescriptor;

/**
 *  on 28.09.2016.
 */

public class ParcelFileDescriptorCompat {
    public static int detachFdSupport(ParcelFileDescriptor parcelFileDescriptor) {
        int result = -1;
        result = parcelFileDescriptor.detachFd();
        return result;
    }
}
