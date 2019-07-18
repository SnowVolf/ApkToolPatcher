package ru.svolf.melissa;

import java.util.ArrayList;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import ru.svolf.melissa.model.AdvancedItem;

public class AdvancedItems {
    public static final int
            //PREMIUM
            GOOGLE_MAPS_FIX = 91, NATIVE_LIBS = 92, ODEX_PATCH = 93, SIGNATURE_FALLBACK = 94, INBUILT_DECOMPILER = 95;
    private ArrayList<AdvancedItem> createdMenuItems = new ArrayList<>();

    public AdvancedItems() {
        createdMenuItems.add(new AdvancedItem(App.bindString(R.string.patch_signature_fallback), false, SIGNATURE_FALLBACK));
        createdMenuItems.add(new AdvancedItem(App.bindString(R.string.patch_native_libs), false, NATIVE_LIBS));
        createdMenuItems.add(new AdvancedItem(App.bindString(R.string.patch_maps_fix), false, GOOGLE_MAPS_FIX));
        createdMenuItems.add(new AdvancedItem(App.bindString(R.string.patch_odex), false, ODEX_PATCH));
        createdMenuItems.add(new AdvancedItem(App.bindString(R.string.patch_decompiler), false, INBUILT_DECOMPILER));
    }

    public ArrayList<AdvancedItem> getCreatedMenuItems() {
        return createdMenuItems;
    }
}

