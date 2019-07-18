package apk.tool.patcher.ui.modules.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;

import java.io.File;
import java.util.Arrays;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.base.BaseSettingsFragment;
import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Managers.ToolsManager;

public class DecompilerSettingsFragment extends BaseSettingsFragment implements
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    Context co;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        co = getActivity();
        sp = SysUtils.SP(co);

        findPreference(SysUtils.PREFS_LIBLD).setOnPreferenceChangeListener(this);
        findPreference(SysUtils.PREFS_CLEAR_FRAMEWORKS).setOnPreferenceClickListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CheckBoxPreference p = (CheckBoxPreference) findPreference(SysUtils.PREFS_LINE_EFFECT);
            p.setChecked(false);
            p.setEnabled(false);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case SysUtils.PREFS_LIBLD:
                sp.edit().putString(SysUtils.PREFS_LIBLD, (String) newValue).apply(); // onPreferenceChange() вызывается до изменения
                ToolsManager.updateLD(co);
                break;
            case SysUtils.PREFS_USE_SDCARD_TOOLS:
                SysUtils.Toast(co, getString(R.string.need_restart));
                break;

        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case SysUtils.PREFS_CLEAR_FRAMEWORKS:
                boolean allRight = true;
                File[] files = new File(SysUtils.getTmpPath(co) + "/.local/share/apktool/framework").listFiles();
                SysUtils.Log("files " + Arrays.toString(files));
                if (files != null)
                    for (File file : files)
                        allRight = file.isFile() && file.getName().endsWith(".apk") && !file.getName().equals("1.apk")
                                && allRight && file.delete();

                SysUtils.Toast(co, getString(allRight ? R.string.fr_cleared : R.string.del_fr_err));
                break;
        }
        return false;
    }

}
