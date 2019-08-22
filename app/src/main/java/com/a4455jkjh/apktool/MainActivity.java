package com.a4455jkjh.apktool;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.a4455jkjh.apktool.fragment.EditorFragment;
import com.a4455jkjh.apktool.fragment.FilesFragment;
import com.a4455jkjh.apktool.fragment.editor.EditorPagerAdapter;
import com.a4455jkjh.apktool.util.Settings;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.settings.SettingsActivity;
import apk.tool.patcher.util.Cs;
import apk.tool.patcher.util.Preferences;

public class MainActivity extends ThemedActivity {
	private CoordinatorLayout coordinator;
	private EditorFragment editor;
	private FilesFragment files;
    private View bottomSheet;

	public boolean dismissFiles() {
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                behavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED){
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
		return false;
	}

	public void showFiles(int idx) {
		files.setPage(idx);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
	}

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.main);
		coordinator = findViewById(R.id.coordinator);
        bottomSheet = findViewById(R.id.bottomView);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.title);
		adjustSheetHeight();
		init(getSupportFragmentManager());
    }
	public void init() {
		onNewIntent(getIntent());
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setWordWrap();
			}
		}, 1500);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		String path = intent.getStringExtra("opened_file");
		int line = intent.getIntExtra("line_number", 0);

		if (path == null){
			return;
		} else {
			editor.open(new File(path), line);
		}
		setWordWrap();
		dismissFiles();
	}

	private void init(FragmentManager supportFragmentManager) {
		Fragment fragment = supportFragmentManager.findFragmentById(R.id.editor);
		FilesFragment files;
		if (fragment == null) {
			EditorFragment editor = new EditorFragment();
			files = new FilesFragment();
			supportFragmentManager.beginTransaction().
				add(R.id.editor, editor).
				add(R.id.bottomView, files).
				commit();
			this.editor = editor;
		} else {
			editor = (EditorFragment) fragment;
			files = (FilesFragment) supportFragmentManager.findFragmentById(R.id.bottomView);
		}
		files.bind(editor);
		this.files = files;

		setWordWrap();

		initFilesFragment();
	}

    private void initFilesFragment() {
        //know if device has NavigationBar
        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        boolean navBar = id > 0 && getResources().getBoolean(id);

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        int peekHeight = App.dpToPx(48)
                + (navBar ? getNavBarHeight() : 0);
        behavior.setPeekHeight(peekHeight);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    editor.focus();
                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED){
                    files.focus();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            	if (slideOffset > 0.25f) {
					bottomSheet.setAlpha(slideOffset);
				}
            }
        });
    }

	/**
	 * Устанавливаем максимальную высоту боттом шита в 2/3 экрана
	 */
    private void adjustSheetHeight(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		//int width = displayMetrics.widthPixels;

		ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
		params.height = Math.round(height * 0.67f);
		bottomSheet.setLayoutParams(params);
	}


	@Override
	public void onBackPressed() {
		checkExit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_apktool, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.exit:
				checkExit();
				break;
			case R.id.settings:
				Intent i = new Intent(this, SettingsActivity.class);
				i.putExtra(Cs.ARG_PREF_TAB,
						EditorPagerAdapter.INSTANCE.getItems().size() > 0
								? Cs.TAB_EDITOR : Cs.TAB_DECOMPILER);
				startActivity(i);
				break;
			default:
				return false;
		}
		return true;
	}

	private void checkExit() {
		if (editor.hasUnSavedEditor())
			showSaveDialog();
		else
			finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Settings.isFontSizeChanged) {
			Settings.isFontSizeChanged = false;
			EditorPagerAdapter.INSTANCE.setFontSize();
		}
		setWordWrap();
	}

	@Override
	public void finish() {
		EditorPagerAdapter.INSTANCE.exit();
		super.finish();
	}

	private void showSaveDialog() {
		DialogInterface.OnClickListener l = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface p1, int p2) {
				if (p2 == DialogInterface.BUTTON_NEUTRAL)
					return;
				if (p2 == DialogInterface.BUTTON_POSITIVE)
					editor.save(true, false);
				finish();
			}
		};
		new AlertDialog.Builder(this).
			setTitle(R.string.save_file).
			setMessage(R.string.save_file_msg).
			setPositiveButton(R.string.yes, l).
			setNegativeButton(R.string.no, l).
			setNeutralButton(R.string.cancel, l).
			show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		editor.save(true, false);
	}

	private void setWordWrap(){
        if (EditorPagerAdapter.INSTANCE.getItems().size() > 0) {
            for (int i = 0; i < EditorPagerAdapter.INSTANCE.getItems().size(); i++) {
                EditorPagerAdapter.INSTANCE.getItems()
                        .get(i).getEditor()
                        .setWordWrap(Preferences.isWordWrap());
            }
        }
    }

    private int getNavBarHeight(){
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
