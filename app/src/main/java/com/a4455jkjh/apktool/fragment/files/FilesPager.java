package com.a4455jkjh.apktool.fragment.files;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.fragment.FilesFragment;
import com.a4455jkjh.apktool.util.PopupUtils;
import com.a4455jkjh.apktool.util.Settings;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.widget.PathView;
import apk.tool.patcher.util.PathF;

public class FilesPager implements WatchDog {
	private final View view;
	private final Context ctx;
	private final CharSequence title;
	private RecyclerView files;
	private ModernFilesAdapter adapter;
	private Toolbar toolbar;
	private PathView pathView;

	public FilesPager(Context context) {
		ctx = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(
			R.layout.files, null);
		title = context.getText(R.string.files);
		files = view.findViewById(R.id.files);
		files.setLayoutManager(new LinearLayoutManager(context));
		pathView = view.findViewById(R.id.breadcrumbs_view);
		toolbar = view.findViewById(R.id.toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (adapter.goBack())
					return;
				((MainActivity)ctx).dismissFiles();
			}
		});
		toolbar.inflateMenu(R.menu.menu_files_caption);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()){
					case R.id.more:
						menu(toolbar);
						return true;
					case R.id.refresh:
						adapter.refresh();
						return true;
				}
				return false;
			}
		});
	}

	public void init(Bundle savedInstanceState, FilesFragment frag) {
		adapter = ModernFilesAdapter.init(frag, files, this);
		adapter.init(savedInstanceState);
	}

	public void save(Bundle outState) {
		adapter.save(outState);
	}

	public CharSequence getTitle() {
		return title;
	}

	public View getView() {
		return view;
	}

	private void menu(final View view) {
		PopupUtils.show(view, R.menu.dir, new PopupUtils.Callback() {
			@Override
			public void call(Context ctx, int pos) {
				switch (pos) {
					case R.id.go_back:
						adapter.goBack();
						break;
					case R.id.sort:
						sort();
						break;
					case R.id.new_dir:
					case R.id.new_file:
						adapter.createFileOrDir(pos);
						break;
					case R.id.set_as_output_directory:
						TextView path1 = (TextView) view;
						String output = path1.getText().toString();
						Settings.setOutputDirectory(output, path1.getContext());
						break;
				}
			}
		});
	}

	protected void sort() {
		new AlertDialog.Builder(ctx)
				.setTitle(R.string.sort)
				.setItems(R.array.sort, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					FileComparator.setDefaultAdapter(p2);
					adapter.refresh();
					PreferenceManager.getDefaultSharedPreferences(ctx)
							.edit()
							.putInt("defaultCompator", p2)
							.apply();
				}
			})
				.setCancelable(false)
				.show();
	}

	@Override
	public void watchForFile(CharSequence path) {
		String currentPath = PathF.pointToName(path.toString());
		toolbar.setTitle(currentPath);
		// FIXME Suka blyad
//		if (pathView.getItems().contains(currentPath)) {
//			pathView.addItem(BreadcrumbItem.createSimpleItem(currentPath));
//		}
	}

}
