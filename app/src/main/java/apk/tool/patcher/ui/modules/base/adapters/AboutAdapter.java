package apk.tool.patcher.ui.modules.base.adapters;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apk.tool.patcher.R;
import ru.svolf.melissa.compat.Compat;
import ru.svolf.melissa.model.FlexibleItem;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
    private List<FlexibleItem> items;
    private AboutAdapter.OnItemClickListener itemClickListener;

    public AboutAdapter(List<FlexibleItem> items) {
        this.items = items;
    }

    public void setItemClickListener(AboutAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FlexibleItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help, parent, false);
        return new AboutAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final AboutAdapter.ViewHolder holder, int position) {
        final FlexibleItem item = getItem(position);

        assert item != null;
        holder.caption.setText(Compat.htmlCompat(item.getTitle()));
        holder.code.setText(Compat.htmlCompat(item.getContent()));
        holder.code.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public interface OnItemClickListener {
        void onItemClick(FlexibleItem menuItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView caption, code;

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
