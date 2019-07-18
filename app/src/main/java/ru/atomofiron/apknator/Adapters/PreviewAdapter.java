package ru.atomofiron.apknator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import apk.tool.patcher.R;
import ru.atomofiron.apknator.Utils.Node;

public class PreviewAdapter extends BaseAdapter {
    private final ArrayList<Node> nodes = new ArrayList<>();
    private Context co;
    private int offset = 0;

    public PreviewAdapter(Context co) {
        this.co = co;
    }

    @Override
    public int getCount() {
        return nodes.size();
    }

    @Override
    public Object getItem(int i) {
        return nodes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(co).inflate(R.layout.item_preview, null, true);

            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        if (offset == 0)
            offset = holder.icon.getHeight();

        Node node = nodes.get(i);
        holder.icon.setImageResource(!node.isPackage ? R.drawable.ic_copyright :
                node.extended ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_right);
        holder.title.setText(node.level == 0 ? node.title.substring(1) :
                !node.isPackage ? node.title.substring(0, node.title.length() - 1) : node.title);
        holder.icon.setPadding(offset * node.level, 0, node.isPackage ? 0 : offset / 2, 0);

        return view;
    }

    public void update(ArrayList<Node> list) {
        nodes.clear();
        nodes.addAll(list);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;

        ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
}
