package apk.tool.patcher.ui.base.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apk.tool.patcher.R;
import ru.svolf.melissa.model.MenuItem;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    List<MenuItem> items;
    private MenuAdapter.OnItemClickListener itemClickListener;

    public MenuAdapter(List<MenuItem> items) {
        this.items = items;
    }

    public void setItemClickListener(MenuAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patch_list_item, parent, false);
        return new MenuAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {
        MenuItem item = getItem(position);
        assert item != null;

        if (item.forApkEditor()) {
            holder.icon.setVisibility(View.VISIBLE);
        }
        holder.text.setText(item.getTitle());
    }

    public interface OnItemClickListener {
        void onItemClick(MenuItem menuItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView text;
        public ImageView icon;

        public ViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.drawer_item_title);
            icon = v.findViewById(R.id.drawer_item_icon);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getItem(getLayoutPosition()), getLayoutPosition());
            }
        }
    }
}
