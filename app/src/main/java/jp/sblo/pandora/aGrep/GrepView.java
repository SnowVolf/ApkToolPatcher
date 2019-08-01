package jp.sblo.pandora.aGrep;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.ui.widget.SpringListView;

public class GrepView extends SpringListView {
    private static final String TAG = "GrepView";
    private Callback mCallback;

    public GrepView(Context context) {
        super(context);
        init();
    }

    public GrepView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GrepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setSmoothScrollbarEnabled(true);
        setScrollingCacheEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setFastScrollEnabled(true);
        setDivider(null);
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

        public File getFile() {
            return mFile;
        }

        public int getLineNumber() {
            return mLinenumber;
        }

        public CharSequence getText() {
            return mText;
        }

        private File mFile;
        private int mLinenumber;
        private CharSequence mText;

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
                holder.shadowTop = view.findViewById(R.id.shadowTop);
                holder.shadowBottom = view.findViewById(R.id.shadowBottom);
                holder.Num = view.findViewById(R.id.ListIndexNum);
                holder.Index = view.findViewById(R.id.ListIndex);
                holder.kwic = view.findViewById(R.id.ListPhone);

                view.setTag(holder);
            }
            Data d = getItem(position);

            String fname = d.getFile().getName();
            if (position > 0) {
                String prevName = getItem(position - 1).getFile().getName();
                Log.e(TAG, "getView: prev=" + prevName + ", cur=" + fname);
                if (prevName.equals(fname)) {
                    // Если предыдущий айтем имеет такой же сорс файл, как и текущий, то
                    // скрываем заголовок файла и границы "карточки" у текущего айтема
                    holder.Index.setVisibility(GONE);
                    holder.shadowTop.setVisibility(GONE);
                    holder.shadowBottom.setVisibility(GONE);
                } else {
                    // Типа вьюха же переиспользуется.
                    // И если ты один раз задал визибилити, то оно сохранится и при
                    // скролле постепенно у всех айтемов будет скрыта та вьюха
                    // Поэтому нужно вручную обновить статус визибилити.
                    holder.Index.setVisibility(VISIBLE);
                    holder.shadowTop.setVisibility(VISIBLE);
                    holder.shadowBottom.setVisibility(VISIBLE);
                }

                holder.Num.setText(String.format(Locale.ENGLISH, "%04d", d.getLineNumber()));
            }
            holder.Index.setText(fname);
            holder.kwic.setText(Search.highlightKeyword(d.mText,
                    mPattern, mFgColor, App.getColorFromAttr(getContext(), R.attr.colorAccent)));

            return view;
        }

        public void setFormat(Pattern pattern, int fgcolor) {
            mPattern = pattern;
            mFgColor = fgcolor;

        }

        static class ViewHolder {
            View shadowTop, shadowBottom;
            TextView Num;
            TextView Index;
            TextView kwic;
        }
    }
}
