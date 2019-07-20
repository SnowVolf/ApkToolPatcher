package apk.tool.patcher.ui.modules.about;


import android.os.Bundle;

import androidx.preference.Preference;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.base.BaseSettingsFragment;
import apk.tool.patcher.util.TextUtil;

/**
 * Created by Snow Volf on 02.09.2017, 12:29
 */

public class AboutFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.patcher_about);
        Preference version = findPreference("app_ver");
        version.setTitle(TextUtil.getBuildName(getContext()));
    }

}
