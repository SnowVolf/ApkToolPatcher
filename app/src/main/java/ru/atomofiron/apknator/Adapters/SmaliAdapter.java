package ru.atomofiron.apknator.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import apk.tool.patcher.R;
import apk.tool.patcher.util.SysUtils;

public class SmaliAdapter extends ArrayAdapter<String> {

    Context co;
    ListView listView;

    ArrayList<Integer> positions;
    int searchPos = -1;
    int green = Color.parseColor("#3000ff00");
    int yellow = Color.parseColor("#30ffff00");
    int empty = Color.parseColor("#00000000");

    public SmaliAdapter(Context context, int resource, List<String> objects, ListView listView, ArrayList<Integer> positions) {
        super(context, resource, objects);
        co = context;
        this.listView = listView;
        this.positions = positions;
    }

    public void updatePositions() {
        searchPos = -1;
        scrollFor(true);
        notifyDataSetChanged();
    }

    public boolean scrollFor(boolean next) {
        if (positions == null || positions.size() == 0)
            return false;

        int lastSearchPos = searchPos;
        if (next) {
            if (positions.size() == searchPos + 1) {
                SysUtils.Toast(co, co.getString(R.string.search_top));
                searchPos = 0;
            } else searchPos++;
        } else {
            if (searchPos == 0) {
                SysUtils.Toast(co, co.getString(R.string.search_bottom));
                searchPos = positions.size() - 1;
            } else searchPos--;
        }

        SysUtils.Log("searchPos = " + searchPos);
        notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(positions.get(searchPos), 0, 10);
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        updateView(position, convertView);
        return super.getView(position, convertView, parent);
    }

    private void updateView(int position, View view) {
        if (view == null)
            return;

        if (searchPos >= 0 && position == positions.get(searchPos))
            view.setBackgroundColor(green);
        else if (positions.contains(position))
            view.setBackgroundColor(yellow);
        else
            view.setBackgroundColor(empty);
    }
}
