package jp.sblo.pandora.aGrep;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

import apk.tool.patcher.R;

public class GrepView extends ListView {

    private Callback mCallback;

    public GrepView(Context context) {
        super(context);
        init(context);
    }

    public GrepView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GrepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setSmoothScrollbarEnabled(true);
        setScrollingCacheEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setFastScrollEnabled(true);
        setDividerHeight(2);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallback != null) {
                    mCallback.onGrepItemClicked(position);
                }
            }
        });
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallback != null) {
                    return mCallback.onGrepItemLongClicked(position);
                }
                return false;
            }
        });

    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        requestFocus();
        return super.onTouchEvent(ev);
    }

    interface Callback {
        void onGrepItemClicked(int position);

        boolean onGrepItemLongClicked(int position);
    }

    static class Data implements Comparator<Data> {

        public File mFile;
        public int mLinenumber;
        public CharSequence mText;

        public Data() {
            this(null, 0, null);
        }

        public Data(File file, int linenumber, CharSequence text) {
            mFile = file;
            mLinenumber = linenumber;
            mText = text;
        }

        @Override
        public int compare(Data object1, Data object2) {
            int ret = object1.mFile.getName().compareToIgnoreCase(object2.mFile.getName());
            if (ret == 0) {
                ret = object1.mLinenumber - object2.mLinenumber;
            }
            return ret;
        }

    }

    static class GrepAdapter extends ArrayAdapter<Data> {

        private Pattern mPattern;
        private int mFgColor;
        private int mBgColor;
        private int mFontSize;


        public GrepAdapter(Context context, int resource, int textViewResourceId, ArrayList<Data> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();

            } else {
                view = inflate(getContext(), R.layout.list_row, null);

                holder = new ViewHolder();
                holder.Index = (TextView) view.findViewById(R.id.ListIndex);
                holder.kwic = (TextView) view.findViewById(R.id.ListPhone);

                holder.Index.setTextSize(mFontSize);
                holder.kwic.setTextSize(mFontSize);

                view.setTag(holder);
            }
            Data d = getItem(position);

            String fname = d.mFile.getName() + "(" + d.mLinenumber + ")";
            holder.Index.setText(fname);
            holder.kwic.setText(Search.highlightKeyword(d.mText, mPattern, mFgColor, mBgColor));

            return view;
        }

        public void setFormat(Pattern pattern, int fgcolor, int bgcolor, int size) {
            mPattern = pattern;
            mFgColor = fgcolor;
            mBgColor = bgcolor;
            mFontSize = size;

        }

        static class ViewHolder {
            TextView Index;
            TextView kwic;
        }
    }
}
