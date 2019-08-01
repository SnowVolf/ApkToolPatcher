package jp.sblo.pandora.aGrep;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.ui.widget.SpringListView;

public class TextPreview extends SpringListView {

    public TextPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextPreview(Context context) {
        super(context);
        init();
    }

    private void init() {
        setSmoothScrollbarEnabled(true);
        setScrollingCacheEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setFastScrollEnabled(true);
        setDividerHeight(0);
    }

    static class Adapter extends ArrayAdapter<CharSequence> {

        private Pattern mPattern;
        private int mFgColor;
        private int mBgColor;

        public Adapter(Context context, int resource, int textViewResourceId, ArrayList<CharSequence> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view =  inflate(getContext(), R.layout.textpreview_row, null);
            }
            CharSequence d = getItem(position);

            TextView num = view.findViewById(R.id.ListIndexNum);
            TextView text = view.findViewById(R.id.ListIndex);
            num.setText(String.format(Locale.ENGLISH, "%04d", (position + 1)));
            text.setText(Search.highlightKeyword(d, mPattern, mFgColor,
                    App.getColorFromAttr(getContext(), R.attr.colorAccent)));

            return view;
        }

        public void setFormat(Pattern pattern, int fg, int bg) {
            mPattern = pattern;
            mFgColor = fg;
            mBgColor = bg;
        }
    }


}
