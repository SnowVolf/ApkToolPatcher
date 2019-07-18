package jp.sblo.pandora.aGrep;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import apk.tool.patcher.App;
import apk.tool.patcher.R;

public class TextPreview extends ListView {


    public TextPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public TextPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextPreview(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setSmoothScrollbarEnabled(true);
        setScrollingCacheEnabled(true);
        setFocusable(true);
        setBackgroundColor(ContextCompat.getColor(context, R.color.light_colorBackground));
        setFocusableInTouchMode(true);
        setFastScrollEnabled(true);
        setDividerHeight(0);
    }

    static class Adapter extends ArrayAdapter<CharSequence> {

        private int mFontSize;
        private Pattern mPattern;
        private int mFgColor;
        private int mBgColor;

        public Adapter(Context context, int resource, int textViewResourceId, ArrayList<CharSequence> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            final int padding = App.dpToPx(16);
            view.setPadding(padding, (padding / 2), padding, (padding / 2));
            if (view == null) {
                view = (TextView) inflate(getContext(), R.layout.textpreview_row, null);
                view.setTextSize(mFontSize);
            }
            CharSequence d = getItem(position);

            view.setText(Search.highlightKeyword(d, mPattern, mFgColor, mBgColor));

            return view;
        }

        public void setFormat(Pattern pattern, int fg, int bg, int size) {
            mPattern = pattern;
            mFgColor = fg;
            mBgColor = bg;
            mFontSize = size;
        }
    }


}
