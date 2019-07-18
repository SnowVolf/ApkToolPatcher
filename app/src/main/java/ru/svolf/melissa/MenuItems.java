package ru.svolf.melissa;


import java.util.ArrayList;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import ru.svolf.melissa.model.MenuItem;

public class MenuItems {
    public static final int REMOVE_ADS = 0, REMOVE_ADS_ACTIVITIES = 2, SIGNATURE = 3, NO_ROOT = 4,
            ANALYTICS = 5, UPDATE = 6, TRANSLATE = 7, ID_DECODER = 8, DELETE_LOCALES = 9, PLAY_SERVICES = 10,
            INTEREST_SMALI = 11, MOD_GUARD = 12, TOAST = 13, UNICODE2UTF = 14, SERVICES_JAR = 15,
            EXT_DECOMPILER = 17;
    private ArrayList<MenuItem> createdMenuItems = new ArrayList<>();

    public MenuItems() {
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_del_ads), true, REMOVE_ADS));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_del_ads_activities), true, REMOVE_ADS_ACTIVITIES));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_signature), false, SIGNATURE));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_root), true, NO_ROOT));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_analytics), true, ANALYTICS));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_update), false, UPDATE));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_xml2mtd), false, TRANSLATE));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_decode_resid), true, ID_DECODER));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_delete_locales), true, DELETE_LOCALES));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_google_play), true, PLAY_SERVICES));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_interest_smali), true, INTEREST_SMALI));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_mod_guard), true, MOD_GUARD));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_toast), false, TOAST));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_unicode2utf), false, UNICODE2UTF));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_services_jar), false, SERVICES_JAR));
        createdMenuItems.add(new MenuItem(App.bindString(R.string.patch_open_apktool), false, EXT_DECOMPILER));
    }

    public ArrayList<MenuItem> getCreatedMenuItems() {
        return createdMenuItems;
    }
}

