package ru.atomofiron.apknator.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import apk.tool.patcher.R;

public class SimpleAdapter extends android.widget.SimpleAdapter {
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;
    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private LayoutInflater mInflater;

    public SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mData = data;
        mResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);

            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++)
                holder[i] = v.findViewById(to[i]);

            v.setTag(holder);
        } else
            v = convertView;

        bindView(position, v);
        return v;
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null)
            return;

        final ViewBinder binder = mViewBinder;
        final View[] holder = (View[]) view.getTag();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = holder[i];
            if (v == null)
                continue;

            final Object data = dataSet.get(from[i]);
            String text = data == null ? "" : data.toString();
            boolean bound = false;

            if (binder != null)
                bound = binder.setViewValue(v, data, text);

            if (!bound) {
                switch (v.getId()) {
                    case R.id.file_modify:
                        v.setVisibility(((boolean) data) ? View.VISIBLE : View.INVISIBLE);
                        break;
                    case R.id.icon:
                        ((ImageView) v).setImageDrawable((Drawable) data);
                        break;
                    case R.id.file_name:
                        setViewText((TextView) v, text);
                        break;
                }
                /*
                if (v instanceof TextView)
					setViewText((TextView) v, text);
				else if (v instanceof ImageView)
					if (data.getClass().equals(Boolean.class))
						v.setVisibility(((boolean)data) ? View.VISIBLE : View.INVISIBLE);
					else
						((ImageView)v).setImageDrawable((Drawable) data);*/
            }
        }
    }
}
