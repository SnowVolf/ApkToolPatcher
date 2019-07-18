package ru.svolf.melissa.fragment.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import apk.tool.patcher.R;

/**
 * Created by Snow Volf on 20.08.2017, 12:54
 */

public class SweetInputDialog extends BottomSheetDialog {
    private Context mContext;
    private TextView mContentView;
    private EditText mEditText;
    private Button mPositive;

    public SweetInputDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        initContentView();
    }

    private void initContentView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_pref_input, null);
        mContentView = view.findViewById(R.id.title);
        mEditText = view.findViewById(R.id.field_edit);
        mPositive = view.findViewById(R.id.positive);
        setContentView(view);
    }

    public SweetInputDialog setPrefTitle(CharSequence title) {
        mContentView.setText(title);
        return this;
    }

    public SweetInputDialog setPositive(CharSequence text, View.OnClickListener listener) {
        mPositive.setText(text);
        mPositive.setOnClickListener(listener);
        return this;
    }

    public String getInputString() {
        return mEditText.getText().toString();
    }

    public SweetInputDialog setInputString(String inputString) {
        mEditText.setText(inputString);
        mEditText.setSelection(mEditText.getText().length());
        return this;
    }

    public EditText getInputField() {
        return mEditText;
    }
}
