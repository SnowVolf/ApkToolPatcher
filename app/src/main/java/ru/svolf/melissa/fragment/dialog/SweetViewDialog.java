package ru.svolf.melissa.fragment.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import apk.tool.patcher.R;

public class SweetViewDialog extends BottomSheetDialog {
    private Context mContext;
    private FrameLayout mContentFrame;
    private TextView mCaption;
    private Button mPositive, mNegative;

    public SweetViewDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        initContentView();
    }

    private void initContentView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_view, null);
        mCaption = view.findViewById(R.id.content_caption);
        mContentFrame = view.findViewById(R.id.content_frame);
        mPositive = view.findViewById(R.id.positive);
        mNegative = view.findViewById(R.id.negative);
        setContentView(view);
    }

    @Override
    public void setTitle(CharSequence title) {
        mCaption.setText(title);
    }


    public void setView(View view) {
        mContentFrame.addView(view);
    }

    public void setPositive(CharSequence text, View.OnClickListener listener) {
        mPositive.setText(text);
        mPositive.setOnClickListener(listener);
    }

    public void setNegative(CharSequence text, View.OnClickListener listener) {
        mNegative.setText(text);
        mNegative.setOnClickListener(listener);
    }

    public void setPositive(int resId, View.OnClickListener listener) {
        mPositive.setText(resId);
        mPositive.setOnClickListener(listener);
    }

    public void setNegative(int resId, View.OnClickListener listener) {
        mNegative.setText(resId);
        mNegative.setOnClickListener(listener);
    }

    @Override
    public void show() {
        super.show();
        mNegative.setVisibility(mNegative.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
        mPositive.setVisibility(mPositive.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
    }
}
