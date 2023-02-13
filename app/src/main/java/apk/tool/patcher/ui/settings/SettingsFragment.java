package apk.tool.patcher.ui.settings;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.ListPreference;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.base.BaseSettingsFragment;

/**
 * Created by Snow Volf on 02.09.2017, 12:29
 */

public class SettingsFragment extends BaseSettingsFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.patcher_preferences, rootKey);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        setCurrentValue(findPreference("sys.language"));
        setCurrentValue(findPreference("ui.theme"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setCurrentValue(ListPreference listPreference) {
        listPreference.setSummary(listPreference.getEntry());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "sys.language": {
                setCurrentValue(findPreference(key));
                break;
            }
            case "ui.theme": {
                setCurrentValue(findPreference(key));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("org.openintents.action.REFRESH_THEME"));
                break;
            }
        }
    }
}
