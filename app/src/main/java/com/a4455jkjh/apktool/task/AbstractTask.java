package com.a4455jkjh.apktool.task;


import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import brut.util.*;
import com.a4455jkjh.apktool.fragment.files.*;
import com.a4455jkjh.apktool.view.*;
import java.io.*;
import java.util.logging.*;

import brut.util.Logger;

public abstract class AbstractTask extends AsyncTask<File, CharSequence, Boolean> implements Logger {
	private Context ctx;
	private TextView message;
	private AlertDialog dialog;
	private final Refreshable refresh;

	public AbstractTask(Context ctx, Refreshable refresh) {
		this.ctx = ctx;
		this.refresh = refresh;
	}
	@Override
	protected Boolean doInBackground(File[] p1) {
		boolean success = true;
		for (File file:p1)
			if (!process(file))
				success = false;
		return Boolean.valueOf(success);
	}
	@Override
	protected void onPreExecute() {
		Context ctx = this.ctx;
		message = new TextView(ctx);
		dialog = new AlertDialog.Builder(ctx).
			setView(message).
			setTitle(getTitle()).
			setCancelable(false).
			show();
	}

	protected abstract int getTitle();
	protected abstract boolean process(File f);

	@Override
	protected void onPostExecute(Boolean result) {
		final CharSequence text =message.getText();
		dialog.dismiss();
		if (shouldShowFinishDialog()) {
			final Context ctx = this.ctx;
			final TextView m = new TextView(ctx);
			m.append(text);

			
			new AlertDialog.Builder(ctx).

				setTitle("Готово").
				setView(m).
				setPositiveButton("Copy", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
						ClipData clipData = ClipData.newPlainText("pather_log", text.toString());
						clipboardManager.setPrimaryClip(clipData);
						// TODO: Implement this method
					}

					private ClipboardManager getSystemService(String cLIPBOARD_SERVICE)
					{
						// TODO: Implement this method
						return null;
					}
				}).
				setNegativeButton("Ок", null).
				show();
		}
		if (refresh != null)
			refresh.refresh();
	}

	protected boolean shouldShowFinishDialog() {
		return true;
	}

	@Override
	protected void onProgressUpdate(CharSequence[] values) {
		for (CharSequence s:values)
			message.append(s);
	}
	private String getText(int id, Object[] args) {
		return ctx.getResources().getString(id, args);
	}

	@Override
	public void info(int id, Object... args) {
		publishProgress(String.format("I:%s\n", getText(id, args)));
	}

	@Override
	public void warning(int id, Object... args) {
		publishProgress(String.format("W:%s\n", getText(id, args)));
	}

	@Override
	public void fine(int id, Object... args) {
		//publishProgress(String.format("F:%s\n",p0));
	}

	@Override
	public void error(int id, Object... args) {
		publishProgress(String.format("E:%s\n", getText(id, args)));
	}

	@Override
	public void log(Level level, String format, Throwable ex) {
		char ch = level.getName().charAt(0);
		String fmt = "%c:%s\n";
		publishProgress(String.format(fmt, ch, format));
		log(fmt, ch, ex);
	}

	private void log(String fmt, char ch, Throwable ex) {
		if (ex == null)return;
		publishProgress(String.format(fmt, ch, ex.getMessage()));
		for (StackTraceElement ste:ex.getStackTrace())
			publishProgress(String.format(fmt, ch, ste));
		log(fmt, ch, ex.getCause());
	}


}
