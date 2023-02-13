package apk.tool.patcher.ui.about;


import android.os.Bundle;

import androidx.preference.Preference;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.base.BaseSettingsFragment;
import apk.tool.patcher.ui.misc.UpdateDialogFragment;
import apk.tool.patcher.util.TextUtil;

/**
 * Created by Snow Volf on 02.09.2017, 12:29
 */

public class AboutFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.patcher_about, rootKey);
        Preference version = findPreference("app_ver");
        if (version != null) {
            version.setTitle(TextUtil.getBuildName(requireContext()));
            version.setSummary(TextUtil.getCopyrightString());
            version.setOnPreferenceClickListener(preference -> {
                UpdateDialogFragment updateDialogFragment = UpdateDialogFragment.newInstance(UpdateDialogFragment.JSON_LINK);
                updateDialogFragment.show(getActivity().getSupportFragmentManager(), "UDF");
                return true;
            });
        }
    }

}
