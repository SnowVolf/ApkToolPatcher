package com.a4455jkjh.apktool.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;

import apk.tool.patcher.App;
import apk.tool.patcher.R;

public class KeystorePreference extends Preference implements AdapterView.OnItemSelectedListener {
	Spinner format;
	EditText key_path;
	TextView cert;
	EditText alias;
	LinearLayout password;
	EditText storePass;
	EditText keyPass;
	public KeystorePreference(Context ctx, AttributeSet a) {
		super(ctx, a);
	}

	private void initDialog(){
		View content = LayoutInflater.from(getContext()).inflate(R.layout.keystore, null);
		onBindDialogView(content);
		onPrepareDialogBuilder();
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(getTitle());
		builder.setView(content);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				click(i);
			}
		});
		builder.setNeutralButton(R.string.close_cur, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				click(i);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	@Override
	protected void onClick() {
		super.onClick();
		initDialog();
	}

	protected void onBindDialogView(View view) {
		format = view.findViewById(R.id.format);
		key_path = view.findViewById(R.id.key_path);
		cert = view.findViewById(R.id.cert);
		alias = view.findViewById(R.id.alias);
		password = view.findViewById(R.id.password);
		storePass = view.findViewById(R.id.storePass);
		keyPass = view.findViewById(R.id.keyPass);
		format.setOnItemSelectedListener(this);
	}

	public void click(int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			SharedPreferences.Editor editor = getSharedPreferences().edit();
			editor.putInt("key_type", format.getSelectedItemPosition());
			editor.putString("key_path", key_path.getText().toString());
			editor.putString("cert_or_alias", alias.getText().toString());
			editor.putString("store_pass", storePass.getText().toString());
			editor.putString("key_pass", keyPass.getText().toString());
			editor.apply();
		} else if (which == DialogInterface.BUTTON_NEUTRAL) {
			SharedPreferences.Editor editor = getSharedPreferences().edit();
			editor.putString("store_pass", "");
			editor.putString("key_pass", "");
			editor.apply();
		}
	}

	void onPrepareDialogBuilder() {
		SharedPreferences sp = App.get().getPreferences();
		int type = sp.getInt("key_type", 0);
		format.setSelection(type);
		String keyPath = sp.getString("key_path", "");
		key_path.setText(keyPath);
		String cert_or_alias = sp.getString("cert_or_alias", "");
		alias.setText(cert_or_alias);
		String store_pass = sp.getString("store_pass", "");
		storePass.setText(store_pass);
		String key_pass = sp.getString("key_pass", "");
		keyPass.setText(key_pass);
	}

	@Override
	public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
		if (p3 == 3) {
			password.setVisibility(View.GONE);
			cert.setText(R.string.cert_path);
		} else {
			password.setVisibility(View.VISIBLE);
			cert.setText(R.string.key_alias);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> p1) {
	}

}
