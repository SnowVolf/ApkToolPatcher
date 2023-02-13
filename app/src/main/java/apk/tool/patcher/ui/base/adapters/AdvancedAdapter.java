package apk.tool.patcher.ui.base.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apk.tool.patcher.R;
import ru.svolf.melissa.model.AdvancedItem;

public class AdvancedAdapter extends RecyclerView.Adapter<AdvancedAdapter.ViewHolder> {
    List<AdvancedItem> items;
    private AdvancedAdapter.OnItemClickListener itemClickListener;

    public AdvancedAdapter(List<AdvancedItem> items) {
        this.items = items;
    }

    public void setItemClickListener(AdvancedAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AdvancedItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public AdvancedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patch_list_item, parent, false);
        return new AdvancedAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(AdvancedAdapter.ViewHolder holder, int position) {
        AdvancedItem item = getItem(position);
        assert item != null;

        //holder.icon.setImageDrawable(AppCompatResources.getDrawable(holder.itemView.getContext(), item.getIconRes()));
        holder.text.setText(item.getTitle());
    }

    public interface OnItemClickListener {
        void onItemClick(AdvancedItem menuItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView text;
        public ImageView icon;

        public ViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.drawer_item_title);
            //icon = v.findViewById(R.id.drawer_item_icon);

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
