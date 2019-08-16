package com.a4455jkjh.apktool;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.a4455jkjh.apktool.util.Settings;

import apk.tool.patcher.R;

public abstract class ThemedActivity extends FragmentActivity {

	protected abstract void init(Bundle savedInstanceState);

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(this);
		init(savedInstanceState);
	}

	public static void setTheme(Activity act) {
		if (Settings.lightTheme) {
			act.setTheme(R.style.AppThemeLight1);
		} else {
			act.setTheme(R.style.AppTheme1);
			if (Build.VERSION.SDK_INT >= 23) {
				View view = act.getWindow().getDecorView();
				int flags = view.getSystemUiVisibility() &
					(~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				view.setSystemUiVisibility(flags);
			}
		}
	}



	@Override
	protected void onResume() {
		super.onResume();
		Settings.loadSettings(this);
		if (Settings.isThemeChanged) {
			Settings.isThemeChanged = false;
			recreate();
		}
	}
}
