package ru.svolf.melissa.fragment.dialog;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import apk.tool.patcher.R;

public class SweetWaitDialog extends SweetViewDialog {
    private TextView matches;

    public SweetWaitDialog(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        Chronometer chronos = view.findViewById(R.id.dialog_chronos);
        matches = view.findViewById(R.id.matches);

        chronos.setBase(SystemClock.elapsedRealtime());
        chronos.start();
        setTitle(R.string.message_wait);
        setView(view);
        setCancelable(false);
        show();
    }

    public void setMessage(CharSequence text) {
        matches.setText(text);
    }

}
