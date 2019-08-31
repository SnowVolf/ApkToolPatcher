package com.a4455jkjh.apktool.fragment.files;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a4455jkjh.apktool.fragment.FilesFragment;
import com.a4455jkjh.apktool.task.BuildTask;
import com.a4455jkjh.apktool.task.SignUtil;

import java.io.File;

import apk.tool.patcher.R;

public class BuildItem implements Item {
	private final File projectDir;

	public BuildItem(File projectDir) {
		this.projectDir = projectDir;
	}

	public boolean isSubDir(File dir) {
		return dir.getAbsolutePath().startsWith(projectDir.getAbsolutePath());
	}

	@Override
	public void setup(ImageView icon, TextView name) {
		icon.setImageResource(R.drawable.ic_play);
		name.setText("Build");
	}

	@Override
	public boolean click(View view, final Refreshable refresh) {
		final Context ctx = view.getContext();
		SignUtil.loadKey(ctx, new SignUtil.LoadKeyCallback(){
				@Override
				public void call(SignUtil signTool) {
					new BuildTask(ctx, refresh, signTool).execute(projectDir);
				}
			});	
		return true;
	}

	@Override
	public boolean edit(FilesFragment frag) {
		return false;
	}


	@Override
	public boolean longClick(View view, Refreshable refresh) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void process(ModernFilesAdapter adapter) {
	}

	@Override
	public int getProperty() {
		return PROPERTY_BUILD;
	}

	@Override
	public int compareTo(Item p1) {
		return PROPERTY_BUILD - p1.getProperty();
	}

}
