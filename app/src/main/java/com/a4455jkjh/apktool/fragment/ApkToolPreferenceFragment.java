package com.a4455jkjh.apktool.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import apk.tool.patcher.R;

public class ApkToolPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.preference_apktool);
		init();
	}

	private void init() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
		boolean b  = sp.getBoolean("custom_signature_file", false);
		findPreference("keystore").setEnabled(b);
		findPreference("gen_key").setEnabled(b);
		findPreference("custom_signature_file").
				setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2) {
		boolean val = ((Boolean)p2).booleanValue();
		findPreference("keystore").setEnabled(val);
		findPreference("gen_key").setEnabled(val);
		return true;
	}


}
