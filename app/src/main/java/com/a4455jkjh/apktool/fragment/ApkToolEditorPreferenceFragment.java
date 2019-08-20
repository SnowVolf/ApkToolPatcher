package com.a4455jkjh.apktool.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import apk.tool.patcher.R;

public class ApkToolEditorPreferenceFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.preference_editor);
	}

}
