package com.a4455jkjh.apktool.fragment.files;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import com.a4455jkjh.apktool.fragment.FilesFragment;

import org.jetbrains.annotations.NotNull;

public class FilesPagerAdapter extends PagerAdapter {
	private final FilesPager files;

	public FilesPagerAdapter(Context context) {
		files = new FilesPager(context);
	}

	public void save(Bundle outState) {
		files.save(outState);
	}

	public void init(Bundle savedInstanceState, FilesFragment frag) {
		files.init(savedInstanceState, frag);
	}

	public FilesPager getFiles() {
		return files;
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public boolean isViewFromObject(View p1, Object p2) {
		return p1 == p2;
	}

	@NotNull
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = null;
		if (position == 0)
			view = files.getView();
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (position == 0)
			return files.getTitle();
		return super.getPageTitle(position);
	}

}
