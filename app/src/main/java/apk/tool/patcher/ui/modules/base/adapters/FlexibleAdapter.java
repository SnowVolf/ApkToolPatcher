package apk.tool.patcher.ui.modules.base.adapters;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apk.tool.patcher.R;
import ru.svolf.melissa.model.FlexibleItem;

public class FlexibleAdapter extends RecyclerView.Adapter<FlexibleAdapter.ViewHolder> {
    private List<FlexibleItem> items;
    private FlexibleAdapter.OnItemClickListener itemClickListener;
    private @LayoutRes
    int layout;

    public FlexibleAdapter(List<FlexibleItem> items, @LayoutRes int layout) {
        this.items = items;
        this.layout = layout;
    }

    public void setItemClickListener(FlexibleAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FlexibleItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public FlexibleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
        return new FlexibleAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final FlexibleAdapter.ViewHolder holder, int position) {
        final FlexibleItem item = getItem(position);

        assert item != null;
        holder.caption.setText(item.getTitle());
        holder.code.setText(item.getContent());
    }

    public @LayoutRes
    int getLayout() {
        return layout;
    }

    public interface OnItemClickListener {
        void onItemClick(FlexibleItem menuItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView caption, code;

        public ViewHolder(View v) {
            super(v);
            caption = v.findViewById(R.id.caption);
            code = v.findViewById(R.id.content);
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
