package apk.tool.patcher.ui.modules.settings;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.ListPreference;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.base.BaseSettingsFragment;

/**
 * Created by Snow Volf on 02.09.2017, 12:29
 */

public class SettingsFragment extends BaseSettingsFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.patcher_preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        setCurrentValue((ListPreference) findPreference("sys.language"));
        setCurrentValue((ListPreference) findPreference("ui.theme"));
        setCurrentValue((ListPreference) findPreference("list_grid_size"));
        setCurrentValue((ListPreference) findPreference("list_anim"));
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
                setCurrentValue((ListPreference) findPreference(key));
                break;
            }
            case "list_grid_size": {
                setCurrentValue((ListPreference) findPreference(key));
                break;
            }
            case "list_anim": {
                setCurrentValue((ListPreference) findPreference(key));
                break;
            }
            case "ui.theme": {
                setCurrentValue((ListPreference) findPreference(key));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("org.openintents.action.REFRESH_THEME"));
                break;
            }
        }
    }
}
