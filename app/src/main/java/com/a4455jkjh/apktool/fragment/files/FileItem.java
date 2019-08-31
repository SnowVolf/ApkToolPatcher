package com.a4455jkjh.apktool.fragment.files;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.a4455jkjh.apktool.fragment.FilesFragment;
import com.a4455jkjh.apktool.lexer.LexerUtil;
import com.a4455jkjh.apktool.util.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import apk.tool.patcher.R;
import apk.tool.patcher.entity.async.GetIcon;

public class FileItem implements Item {
	private File file;

	public FileItem(File file) {
		this.file = file;
	}

	@Override
	public void setup(ImageView icon, TextView name) {
		String n = file.getName();
		name.setText(n);
		set(icon);
	}

	@Override
	public boolean edit(FilesFragment frag) {
		File file = this.file;
		if (file.isFile()) {
			String name = file.getName();
			int p = name.lastIndexOf('.');
			if (p >= 0) {
				String type = name.substring(p + 1);
				String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type);
				if ((mime != null && mime.startsWith("text/")) ||
					LexerUtil.isText(type)) {
					frag.edit(file);
					return true;
				}
			}
		}
		return false;
	}


	public static boolean isJKS(String n) {
		return n.endsWith(".jks") || n.endsWith(".keystore");
	}
	public static boolean isKey(String n) {
		return n.toLowerCase().matches(".*\\.(jks|keystore|p12|pk8|x509|pem)");
	}

	@Override
	public boolean click(View view, Refreshable refresh) {
		if (file.isDirectory())
			return false;
		FileUtils.open(file, view, refresh);
		return true;
	}

	@Override
	public void process(ModernFilesAdapter adapter) {
		adapter.refresh(file);
	}

	@Override
	public boolean longClick(View view, Refreshable refresh) {
		FileUtils.file(file, view, refresh);
		return true;
	}

	@Override
	public int compareTo(@NotNull Item another) {
		if (! (another instanceof FileItem))
			return 1;
		File f1 = file;
		File f2 = ((FileItem) another).file;
		if (f1.isDirectory() && f2.isFile())
			return -1;
		if (f2.isDirectory() && f1.isFile())
			return 1;
		return FileComparator.getDefaultAdapter().compare(f1, f2);
	}
	public int getProperty() {
		return PROPERTY_FILE;
	}

	public void set(ImageView image){
		if (file.isDirectory()){
			image.setImageResource(R.drawable.folder);
		} else {
			GetIcon.getInstance().resolve(file.getPath(), image);
		}
	}
}
