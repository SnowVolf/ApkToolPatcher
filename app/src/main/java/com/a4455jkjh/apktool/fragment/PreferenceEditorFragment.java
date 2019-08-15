package com.a4455jkjh.apktool.fragment;

import android.os.*;
import android.preference.*;
import apk.tool.patcher.*;


public class PreferenceEditorFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_editor);
	}
}
